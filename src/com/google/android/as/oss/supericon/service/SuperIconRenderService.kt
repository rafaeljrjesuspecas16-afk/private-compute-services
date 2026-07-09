/*
 * Copyright 2025 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.`as`.oss.supericon.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.Shader
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.os.IBinder
import android.os.Parcel
import android.os.RemoteException
import android.util.LruCache
import android.util.Size
import android.util.TypedValue
import android.view.Display
import android.view.InflateException
import android.view.LayoutInflater
import android.view.SurfaceControlViewHost
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import android.window.InputTransferToken
import androidx.annotation.VisibleForTesting
import androidx.core.graphics.createBitmap
import androidx.core.hardware.display.DisplayManagerCompat
import com.google.android.`as`.oss.common.Executors.PIR_EXECUTOR
import com.google.android.`as`.oss.common.config.ConfigReader
import com.google.android.`as`.oss.supericon.aidl.ConversationData
import com.google.android.`as`.oss.supericon.aidl.IConversationContentCallback
import com.google.android.`as`.oss.supericon.aidl.ISuperIconRenderCallback
import com.google.android.`as`.oss.supericon.aidl.ISuperIconRenderService
import com.google.android.`as`.oss.supericon.aidl.ISuperIconSurfacePackageResultCallback
import com.google.android.`as`.oss.supericon.aidl.ISuperIconUi
import com.google.android.`as`.oss.supericon.aidl.RenderOptions
import com.google.android.`as`.oss.supericon.config.SuperIconConfig
import com.google.android.`as`.oss.supericon.utils.ConsentEventConstants
import com.google.android.`as`.oss.supericon.utils.SuperIconErrorCodes
import com.google.android.`as`.oss.supericon.utils.SuperIconUiType
import com.google.common.flogger.android.AndroidFluentLogger
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

/** A service that renders a view based on the given view spec. */
@SuppressLint("NewApi")
@AndroidEntryPoint(Service::class)
class SuperIconRenderService : Hilt_SuperIconRenderService() {
  internal var backgroundDispatcher: CoroutineDispatcher = PIR_EXECUTOR.asCoroutineDispatcher()
    @VisibleForTesting
    set(value) {
      field = value
      backgroundScope = CoroutineScope(value)
    }

  internal var backgroundScope = CoroutineScope(backgroundDispatcher)

  private lateinit var mainDispatcher: CoroutineDispatcher

  private lateinit var mainScope: CoroutineScope

  @Inject lateinit var configReader: ConfigReader<SuperIconConfig>
  @Inject internal lateinit var consentManager: SuperIconConsentManager
  @Inject internal lateinit var connectionFactory: ConversationContentConnectionFactory
  @Inject internal lateinit var surfaceControlViewHostFactory: SurfaceControlViewHostFactory

  private lateinit var renderService: SuperIconRenderServiceBinderStub

  override fun onCreate() {
    logger.atInfo().log("onCreate")
    super.onCreate()
    mainDispatcher = mainExecutor.asCoroutineDispatcher()
    mainScope = CoroutineScope(mainDispatcher)
  }

  override fun onDestroy() {
    super.onDestroy()
    backgroundScope.cancel()
    mainScope.cancel()
  }

  override fun onBind(intent: Intent): IBinder {
    logger.atInfo().log("onBind")
    renderService =
      SuperIconRenderServiceBinderStub(
        this,
        backgroundScope,
        backgroundDispatcher,
        mainScope,
        mainDispatcher,
      )
    return renderService
  }

  override fun onUnbind(intent: Intent): Boolean {
    logger.atInfo().log("onUnbind")
    renderService.cancelRender()
    logger
      .atFine()
      .log(
        "size of activeSuperIconUis: %s when onUnbind()",
        renderService.activeSuperIconUis.size(),
      )
    return super.onUnbind(intent)
  }

  internal fun createForceDarkImmuneDrawable(original: Drawable): Drawable {
    val width = original.intrinsicWidth.takeIf { it > 0 } ?: 100
    val height = original.intrinsicHeight.takeIf { it > 0 } ?: 100

    // 1. Bake the tint into a raw Bitmap using the CPU
    val tintedBitmap = createBitmap(width, height)
    val canvas = Canvas(tintedBitmap)
    original.setBounds(0, 0, width, height)
    original.draw(canvas)

    // 2. Wrap it in a Shader geometry so the GPU doesn't recognize it as an image
    return object : Drawable() {
      private val paint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
          shader = BitmapShader(tintedBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        }
      private val matrix = Matrix()

      override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        // Scale the texture to match the ImageView's bounds
        val scaleX = bounds.width().toFloat() / tintedBitmap.width
        val scaleY = bounds.height().toFloat() / tintedBitmap.height
        matrix.setScale(scaleX, scaleY)
        paint.shader.setLocalMatrix(matrix)
      }

      override fun draw(canvas: Canvas) {
        canvas.drawRect(bounds, paint)
      }

      override fun getIntrinsicWidth(): Int = tintedBitmap.width

      override fun getIntrinsicHeight(): Int = tintedBitmap.height

      override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
      }

      override fun setColorFilter(colorFilter: android.graphics.ColorFilter?) {}

      @Deprecated("Deprecated") override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
    }
  }

  internal inner class SuperIconRenderServiceBinderStub(
    private val context: Context,
    private val backgroundScope: CoroutineScope,
    private val backgroundDispatcher: CoroutineDispatcher,
    private val mainScope: CoroutineScope,
    private val mainDispatcher: CoroutineDispatcher,
  ) : ISuperIconRenderService.Stub() {
    private val currentRenderRequests = ConcurrentHashMap<Int, RenderRequestParams>()
    internal val activeSuperIconUis =
      object : LruCache<SuperIconUi, Boolean>(MAXIMUM_ACTIVE_UI_COUNT) {
        override fun entryRemoved(
          evicted: Boolean,
          superIconUi: SuperIconUi,
          oldValue: Boolean,
          newValue: Boolean?,
        ) {
          // Final safety net to release the SurfaceControlViewHost resources in case there is a
          // leak somehow.
          logger.atFine().log("releases surfaceControlViewHost on eviction %s", superIconUi)
          superIconUi.releaseOnEviction()
        }
      }
    private var consentDialogUi: SuperIconUi? = null

    // Currently only allow Gboard to connect to [SuperIconRenderService].
    override fun onTransact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean {
      if (!configReader.config.enableSuperIcon) {
        throw RemoteException("super icon feature is disabled.")
      }
      return super.onTransact(code, data, reply, flags)
    }

    override fun render(
      icon: Icon,
      iconWidth: Int,
      iconHeight: Int,
      background: Icon,
      width: Int,
      height: Int,
      minWidth: Int,
      minHeight: Int,
      maxWidth: Int,
      maxHeight: Int,
      displayId: Int,
      configuration: Configuration,
      hostInputToken: InputTransferToken,
      callback: ISuperIconRenderCallback,
    ) {
      renderWithOptions(
        RenderOptions(
          width = width,
          height = height,
          minWidth = minWidth,
          minHeight = minHeight,
          maxWidth = maxWidth,
          maxHeight = maxHeight,
          uiType = SuperIconUiType.SUPER_ICON,
          icon = icon,
          iconWidth = iconWidth,
          iconHeight = iconHeight,
          iconScaleX = 1.0f,
          iconScaleY = 1.0f,
          background = background,
        ),
        displayId = displayId,
        configuration = configuration,
        hostInputToken = hostInputToken,
        callback = callback,
      )
    }

    override fun renderWithOptions(
      renderOptions: RenderOptions,
      displayId: Int,
      configuration: Configuration,
      hostInputToken: InputTransferToken,
      callback: ISuperIconRenderCallback,
    ) {
      val params = Params(renderOptions, displayId, configuration, hostInputToken, callback)
      if (!params.isValidRenderOptions()) {
        callback.onError(SuperIconErrorCodes.INVALID_PARAMETER, INVALID_PARAMETER_ERROR_MESSAGE)
        logger
          .atSevere()
          .log(
            "$INVALID_PARAMETER_ERROR_MESSAGE width: %s, height: %s",
            renderOptions.width,
            renderOptions.height,
          )
        return
      }
      val uiType = renderOptions.uiType
      if (params == currentRenderRequests[uiType]?.params) {
        logger.atFine().log("ignore render request with exact same options")
        return
      }
      currentRenderRequests[uiType]?.renderJob?.cancel()
      currentRenderRequests[uiType]?.contentJob?.cancel()
      currentRenderRequests[uiType] =
        RenderRequestParams(params, renderJob = null, contentJob = null).apply {
          renderJob =
            mainScope.safeLaunch(
              onError = { e ->
                when (e) {
                  is InflateException ->
                    callback.onError(
                      SuperIconErrorCodes.RENDER_FAILED,
                      e.message ?: "Inflation failed",
                    )
                  is IllegalArgumentException ->
                    callback.onError(
                      SuperIconErrorCodes.INVALID_PARAMETER,
                      e.message ?: INVALID_PARAMETER_ERROR_MESSAGE,
                    )
                  else ->
                    callback.onError(
                      SuperIconErrorCodes.UNKNOWN,
                      e.message ?: "Unknown render error",
                    )
                }
              }
            ) {
              val (renderContext, display) = getRenderContextAndDisplay(configuration, displayId)

              val view =
                if (uiType == SuperIconUiType.CONSENT_TOGGLE) {
                  createConsentToggleView(renderContext, display, hostInputToken, callback)
                } else {
                  val chipView = createChip(renderContext, params, display)
                  chipView.focusable = View.NOT_FOCUSABLE
                  chipView
                }

              val measuredSize =
                measureSize(
                  view,
                  Size(renderOptions.width, renderOptions.height),
                  Size(renderOptions.minWidth, renderOptions.minHeight),
                  Size(renderOptions.maxWidth, renderOptions.maxHeight),
                )
              logger.atFine().log("#render, measuredSize: %s, view: %s", measuredSize, view)

              renderHost(
                view,
                params,
                measuredSize,
                renderContext,
                display,
                hostInputToken,
                renderOptions.windowToken,
                uiType,
                callback,
              )
            }
        }
    }

    @SuppressLint("InflateParams")
    private fun createConsentToggleView(
      renderContext: Context,
      display: Display,
      hostInputToken: InputTransferToken,
      callback: ISuperIconRenderCallback,
    ): View {
      val view =
        LayoutInflater.from(renderContext).inflate(R.layout.super_icon_consent_toggle, null)
      val switchView = view.findViewById<Switch>(R.id.consent_toggle_switch)

      view.addOnAttachStateChangeListener(
        object : View.OnAttachStateChangeListener {
          private var observerJob: Job? = null

          override fun onViewAttachedToWindow(v: View) {
            observerJob = mainScope.launch {
              consentManager.consentStateFlow.collect { state ->
                switchView.isChecked = (state == ConsentState.GRANTED)
              }
            }
          }

          override fun onViewDetachedFromWindow(v: View) {
            observerJob?.cancel()
            observerJob = null
          }
        }
      )

      switchView.setOnClickListener {
        val isChecked = switchView.isChecked

        if (isChecked) {
          currentRenderRequests[SuperIconUiType.CONSENT_TOGGLE]?.contentJob =
            backgroundScope.safeLaunch(errorLogMessage = "Failed granting consent") {
              consentManager.recordConsentState(ConsentState.GRANTED)
              callback.onConsentGranted(awaitCallback(context))
            }
        } else {
          currentRenderRequests[SuperIconUiType.CONSENT_TOGGLE]?.contentJob =
            backgroundScope.safeLaunch(errorLogMessage = "Failed revoking context") {
              consentManager.recordConsentState(ConsentState.REVOKED)
              callback.onConsentDenied()
            }
        }
      }
      return view
    }

    @SuppressLint("InflateParams")
    private fun createConsentView(
      renderContext: Context,
      callback: ISuperIconRenderCallback,
      totalDisplayCount: Int,
      icon: Icon?,
    ): View {
      val consentView =
        LayoutInflater.from(renderContext).inflate(R.layout.super_icon_consent_dialog, null, false)

      val imageView: ImageView? = consentView.findViewById(R.id.super_icon_image)
      if (imageView == null) {
        logger
          .atSevere()
          .log("R.id.super_icon_image not found in R.layout.super_icon_consent_dialog")
      } else {
        loadDrawableFromIcon(icon, renderContext = renderContext) { drawable ->
          imageView.setImageDrawable(drawable)
        }
      }

      var userMadeExplicitChoice = false

      fun executeConsentAction(
        remoteErrorMsg: String,
        genericErrorMsg: String,
        ioErrorMsg: String = "Failed to record consent state",
        action: suspend () -> Unit,
      ): Job {
        return backgroundScope.safeLaunch(
          errorLogMessage = genericErrorMsg,
          onError = { e ->
            if (e is IOException) {
              callback.onError(SuperIconErrorCodes.UNKNOWN, e.message ?: ioErrorMsg)
            } else {
              callback.onError(SuperIconErrorCodes.UNKNOWN, e.message ?: genericErrorMsg)
            }
          },
        ) {
          action()
        }
      }

      val grantAction = View.OnClickListener {
        userMadeExplicitChoice = true
        currentRenderRequests[SuperIconUiType.CONSENT_DIALOG]?.contentJob =
          executeConsentAction(
            remoteErrorMsg = "Failed to report consent granted",
            genericErrorMsg = "Failed executing grant action",
          ) {
            consentManager.recordConsentState(ConsentState.GRANTED)
            callback.onConsentMetricsLogged(ConsentEventConstants.GRANTED, totalDisplayCount)
            callback.onConsentGranted(awaitCallback(context))
          }
      }
      val denyAction = View.OnClickListener {
        userMadeExplicitChoice = true
        currentRenderRequests[SuperIconUiType.CONSENT_DIALOG]?.contentJob =
          executeConsentAction(
            remoteErrorMsg = "Failed to report consent denied",
            genericErrorMsg = "Failed executing deny action",
          ) {
            consentManager.recordConsentState(ConsentState.DENIED)
            callback.onConsentMetricsLogged(ConsentEventConstants.DENIED, totalDisplayCount)
            callback.onConsentDenied()
          }
      }
      consentView.findViewById<View>(R.id.btn_yes).setOnClickListener(grantAction)
      consentView.findViewById<View>(R.id.btn_no).setOnClickListener(denyAction)

      consentView.addOnAttachStateChangeListener(
        object : View.OnAttachStateChangeListener {
          override fun onViewAttachedToWindow(v: View) {
            // No action needed on attach
          }

          override fun onViewDetachedFromWindow(v: View) {
            if (!userMadeExplicitChoice) {
              currentRenderRequests[SuperIconUiType.CONSENT_DIALOG]?.contentJob =
                executeConsentAction(
                  remoteErrorMsg = "Failed to report implicit consent denied",
                  ioErrorMsg = "Failed to record implicit consent state",
                  genericErrorMsg = "Failed executing implicit deny action",
                ) {
                  // User did not explicitly grant or deny consent, so we treat it as a denial.
                  consentManager.recordConsentState(ConsentState.DENIED)
                  callback.onConsentMetricsLogged(ConsentEventConstants.DENIED, totalDisplayCount)
                  callback.onConsentDenied()
                }
            }
          }
        }
      )

      return consentView
    }

    @SuppressLint("InflateParams")
    private fun createChip(renderContext: Context, params: Params, display: Display): View {
      val chipLayoutId =
        when (params.renderOptions.uiType) {
          SuperIconUiType.SPELL_CHECKER_CHIP -> R.layout.spell_checker_chip
          else -> R.layout.super_icon_chip
        }
      val view = LayoutInflater.from(renderContext).inflate(chipLayoutId, null)
      view.contentDescription = params.renderOptions.contentDescription
      logger
        .atFine()
        .log("uiType: %s renderOptions: %s", params.renderOptions.uiType, params.renderOptions)

      if (
        params.renderOptions.uiType == SuperIconUiType.SPELL_CHECKER_CHIP &&
          !params.renderOptions.label.isNullOrEmpty()
      ) {
        val textView = view.findViewById<TextView>(R.id.text)
        textView.text = params.renderOptions.label
        if (params.renderOptions.labelColor != RenderOptions.DEFAULT_LABEL_COLOR) {
          textView.setTextColor(params.renderOptions.labelColor)
        }
        val fontFamily = params.renderOptions.fontFamily ?: RenderOptions.DEFAULT_FONT_FAMILY
        if (fontFamily != RenderOptions.DEFAULT_FONT_FAMILY) {
          textView.typeface = Typeface.create(fontFamily, Typeface.NORMAL)
        }
        if (params.renderOptions.textSizeInPixels > 0) {
          textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, params.renderOptions.textSizeInPixels)
        }
        textView.textScaleX = params.renderOptions.textScaleX
      }
      // --- Set the Drawable on the Chip ---
      val imageView: ImageView = view.findViewById(R.id.icon)
      loadDrawableFromIcon(params.renderOptions.icon, renderContext = renderContext) { drawable ->
        if (drawable != null) {
          // Apply the bypass force dark theme
          val immuneDrawable = createForceDarkImmuneDrawable(drawable)
          imageView.setImageDrawable(immuneDrawable)
        } else {
          imageView.setImageDrawable(null)
        }

        imageView.scaleX = params.renderOptions.iconScaleX
        imageView.scaleY = params.renderOptions.iconScaleY
      }
      imageView.layoutParams =
        imageView.layoutParams.apply {
          width = params.renderOptions.iconWidth
          height = params.renderOptions.iconHeight
        }

      loadDrawableFromIcon(params.renderOptions.background, renderContext = renderContext) {
        background ->
        view.findViewById<ViewGroup>(R.id.chip).background = background
      }

      view.setOnClickListener {
        currentRenderRequests[SuperIconUiType.SUPER_ICON]?.contentJob =
          backgroundScope.safeLaunch(
            errorLogMessage = "Failed executing click action",
            onError = { e ->
              params.callback.onError(
                SuperIconErrorCodes.UNKNOWN,
                e.message ?: "Unknown click error",
              )
            },
          ) {
            val currentState = consentManager.consentStateFlow.first()
            if (currentState == ConsentState.GRANTED) {
              logger.atFine().log("onClick with consent granted")
              params.callback.onClick(awaitCallback(context))
            } else {
              if (awaitCallback(context).messages.isEmpty()) {
                logger.atFine().log("onClick with empty conversation data")
                params.callback.onClick(ConversationData(emptyList(), packageName = ""))
              } else if (consentManager.shouldShowConsentForm()) {
                showConsentForm(renderContext, params, display)
              } else {
                logger.atFine().log("onClick with consent denied")
                params.callback.onClick(ConversationData(emptyList(), packageName = ""))
              }
            }
          }
      }
      return view
    }

    private suspend fun showConsentForm(renderContext: Context, params: Params, display: Display) {
      withContext(mainDispatcher) {
        consentManager.recordConsentFormShown()
        val totalDisplayCount = consentManager.getConsentFormShownTimes()
        currentRenderRequests[SuperIconUiType.CONSENT_DIALOG] =
          RenderRequestParams(params, null, null)
        val consentView =
          createConsentView(
            renderContext,
            params.callback,
            totalDisplayCount,
            params.renderOptions.icon,
          )

        consentView.focusable = View.NOT_FOCUSABLE
        val displayMetrics = renderContext.resources.displayMetrics
        val dialogWidth = (displayMetrics.widthPixels * 0.85).toInt()
        val measuredSize =
          measureSize(
            consentView,
            Size(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT),
            Size(dialogWidth, 0),
            Size(dialogWidth, displayMetrics.heightPixels),
          )
        logger.atFine().log("Displaying consent form with size: %s", measuredSize)
        params.callback.onConsentMetricsLogged(ConsentEventConstants.SHOWN, totalDisplayCount)

        renderHost(
          consentView,
          params,
          measuredSize,
          renderContext,
          display,
          params.hostInputToken,
          params.renderOptions.windowToken,
          SuperIconUiType.CONSENT_DIALOG,
          params.callback,
        )
      }
    }

    private suspend fun renderHost(
      view: View,
      params: Params,
      measuredSize: Size,
      renderContext: Context,
      display: Display,
      hostInputToken: InputTransferToken,
      windowToken: IBinder?,
      @SuperIconUiType uiType: Int,
      callback: ISuperIconRenderCallback,
    ) {
      logger.atFine().log("windowToken: %s", windowToken)
      val host = createSurfaceControlViewHost(display, windowToken, hostInputToken)
      val superIconUi = SuperIconUi(host, params, view, measuredSize, renderContext, display)
      var success = false
      try {
        (view.parent as? ViewGroup)?.removeView(view)
        val viewToSet = FrameLayout(renderContext)
        viewToSet.addView(view)
        host.setView(viewToSet, measuredSize.width, measuredSize.height)
        logger.atFine().log("create a host %s", host)
        // Trigger the initial callback to update the consent toggle status box in the client (e.g.,
        // Gboard) when the popup menu is first shown.
        // We return empty ConversationData here to avoid the expensive awaitCallback IPC call
        // during initial rendering. Actual toggle value changes (handled in
        // createConsentToggleView) will fetch and return the valid conversation data.
        // Without this initial callback, the status box will show the default XML text until the
        // user interacts with the toggle.
        if (uiType == SuperIconUiType.CONSENT_TOGGLE) {
          backgroundScope.launch {
            val state = consentManager.consentStateFlow.first()
            if (state == ConsentState.GRANTED) {
              // This is for rendering the toggle, not for turning on the toggle. Return empty
              // ConversationData immediately to optimize rendering latency. Gboard's controller
              // will ignore this initial empty data to avoid overwriting its cached context data.
              callback.onConsentGranted(ConversationData(emptyList(), packageName = ""))
            } else {
              callback.onConsentDenied()
            }
          }
        }
        if (uiType == SuperIconUiType.SUPER_ICON) {
          this@SuperIconRenderServiceBinderStub.activeSuperIconUis.put(superIconUi, true)
          logger
            .atFine()
            .log("add activeSuperIconUis.count: %s %s", activeSuperIconUis.size(), superIconUi)
        } else {
          this@SuperIconRenderServiceBinderStub.consentDialogUi = superIconUi
        }
        // We post the callback invocation to the end of the main thread handler queue, to
        // make sure the callback happens after the views are drawn. This is needed because
        // calling {@link SurfaceControlViewHost#setView()} will post a task to the main
        // thread to draw the view asynchronously.
        withContext(mainDispatcher) {
          callback.onRendered(
            SuperIconUiWrapper(WeakReference<SuperIconUi>(superIconUi)),
            host.surfacePackage,
            measuredSize.width,
            measuredSize.height,
            uiType,
          )
        }
        success = true
      } finally {
        if (!success) {
          superIconUi.releaseSurfaceContentViewHost(uiType = uiType)
          logger
            .atFine()
            .log("released host due to cancellation or failure: %s %s", host, superIconUi)
        }
      }
    }

    private suspend fun getRenderContextAndDisplay(
      configuration: Configuration,
      displayId: Int,
    ): Pair<Context, Display> =
      withContext(backgroundDispatcher) {
        val renderContext = context.createConfigurationContext(configuration)
        val display =
          with(DisplayManagerCompat.getInstance(renderContext)) {
            getDisplay(displayId) ?: displays[0]
          }
        Pair(renderContext, display)
      }

    private fun createSurfaceControlViewHost(
      display: Display,
      windowToken: IBinder?,
      hostInputToken: InputTransferToken,
    ): SurfaceControlViewHost =
      if (windowToken != null) {
        surfaceControlViewHostFactory.create(context, display, windowToken)
      } else {
        surfaceControlViewHostFactory.create(context, display, hostInputToken)
      }

    internal inner class SuperIconUiWrapper(internal val weakRef: WeakReference<SuperIconUi>) :
      ISuperIconUi.Stub() {
      override fun getSurfacePackage(callback: ISuperIconSurfacePackageResultCallback) {
        val superIconUi = weakRef.get()
        logger.atFine().log("getSurfacePackage, superIconUi: %s", superIconUi)
        superIconUi?.getSurfacePackage(callback)
      }

      override fun releaseSurfaceControlViewHost(uiType: Int) {
        val superIconUi = weakRef.get()
        logger.atFine().log("releaseSurfaceControlViewHost, superIconUi: %s", superIconUi)
        superIconUi?.releaseSurfaceContentViewHost(uiType)
      }
    }

    internal inner class SuperIconUi(
      internal var viewHost: SurfaceControlViewHost?,
      val params: Params,
      val view: View,
      val measuredSize: Size,
      val renderContext: Context,
      val display: Display,
    ) {
      fun releaseSurfaceContentViewHost(uiType: Int) {
        mainScope.launch {
          releaseResourcesInternal()
          if (uiType == SuperIconUiType.SUPER_ICON) {
            activeSuperIconUis.remove(this@SuperIconUi)
            logger
              .atFine()
              .log("remove activeSuperIconUis.count: %s %s", activeSuperIconUis.size(), this)
          } else {
            this@SuperIconRenderServiceBinderStub.consentDialogUi = null
          }
        }
      }

      fun releaseOnEviction() {
        mainScope.launch { releaseResourcesInternal() }
      }

      private fun releaseResourcesInternal() {
        logger.atFine().log("release host %s %s", viewHost, this)
        viewHost?.release()
        viewHost = null
      }

      fun getSurfacePackage(surfacePackageResultCallback: ISuperIconSurfacePackageResultCallback) {
        mainScope.launch {
          releaseResourcesInternal()
          // Recreate the SurfaceControlViewHost using the window token if available.
          // This is crucial for Accessibility (e.g. TalkBack) to bridge the focus
          // between the client process (Gboard) and the host process (Pcs).
          val host =
            createSurfaceControlViewHost(
              display,
              params.renderOptions.windowToken,
              params.hostInputToken,
            )
          (view.parent as? ViewGroup)?.removeView(view)
          val viewToSet = FrameLayout(renderContext)
          viewToSet.addView(view)
          host.setView(viewToSet, measuredSize.width, measuredSize.height)
          val surfacePackage = host.surfacePackage ?: return@launch
          viewHost = host
          backgroundScope.launch {
            try {
              surfacePackageResultCallback.onResult(surfacePackage)
            } catch (e: RemoteException) {
              logger.atSevere().withCause(e).log("RemoteException calling onSurfacePackage")
            }
          }
        }
      }
    }

    fun cancelRender() {
      for (request in currentRenderRequests.values) {
        request.renderJob?.cancel()
        request.contentJob?.cancel()
      }
    }

    suspend fun awaitCallback(context: Context): ConversationData =
      withTimeoutOrNull(CALLBACK_TIMEOUT_MS) {
        suspendCancellableCoroutine { continuation ->
          var localConnection: AutoCloseable? = null
          val callback =
            object : IConversationContentCallback.Stub() {
              override fun onResponse(conversationData: ConversationData) {
                logger.atInfo().log("IConversationContentCallback.onResponse")
                localConnection?.close()
                continuation.resume(conversationData)
              }

              override fun onError(@SuperIconErrorCodes errorCode: Int, errorMessage: String) {
                logger
                  .atSevere()
                  .log("IConversationContentCallback.onError %d %s", errorCode, errorMessage)
                localConnection?.close()
                continuation.resume(ConversationData(listOf(), packageName = ""))
              }
            }
          localConnection = connectionFactory.create(context, backgroundScope, callback)
          continuation.invokeOnCancellation { localConnection.close() }
        }
      }
        ?: run {
          logger.atSevere().log("Callback timed out after %d ms", CALLBACK_TIMEOUT_MS)
          ConversationData(listOf(), packageName = "")
        }

    private fun CoroutineScope.safeLaunch(
      errorLogMessage: String = "Unhandled exception",
      onError: ((Throwable) -> Unit)? = null,
      block: suspend CoroutineScope.() -> Unit,
    ): Job {
      return launch {
        try {
          block()
        } catch (e: CancellationException) {
          throw e
        } catch (e: Exception) {
          logger.atSevere().withCause(e).log("%s", errorLogMessage)
          try {
            onError?.invoke(e)
          } catch (re: RemoteException) {
            logger.atSevere().withCause(re).log("Failed to notify caller via AIDL")
          }
        }
      }
    }
  }

  internal data class Params(
    val renderOptions: RenderOptions,
    val displayId: Int,
    val configuration: Configuration,
    val hostInputToken: InputTransferToken,
    val callback: ISuperIconRenderCallback,
  ) {
    fun isValidRenderOptions(): Boolean {
      if (
        renderOptions.width <= 0 ||
          renderOptions.height <= 0 ||
          renderOptions.minWidth <= 0 ||
          renderOptions.minHeight <= 0 ||
          renderOptions.maxWidth <= 0 ||
          renderOptions.maxHeight <= 0 ||
          renderOptions.minWidth > renderOptions.width ||
          renderOptions.minHeight > renderOptions.height ||
          renderOptions.width > renderOptions.maxWidth ||
          renderOptions.height > renderOptions.maxHeight
      ) {
        return false
      }
      if (renderOptions.uiType == SuperIconUiType.SUPER_ICON) {
        return renderOptions.icon != null &&
          renderOptions.background != null &&
          !(renderOptions.iconWidth <= 0 || renderOptions.iconHeight <= 0)
      }
      return true
    }
  }

  private data class RenderRequestParams(
    val params: Params,
    var renderJob: Job?,
    var contentJob: Job?,
  )

  private companion object {
    val logger: AndroidFluentLogger = AndroidFluentLogger.create("PcsSuperIcon")
    const val INVALID_PARAMETER_ERROR_MESSAGE: String = "invalid parameter"

    const val MAXIMUM_ACTIVE_UI_COUNT = 10
    const val CALLBACK_TIMEOUT_MS = 1000L

    fun loadDrawableFromIcon(
      icon: Icon?,
      renderContext: Context,
      action: (drawable: Drawable?) -> Unit,
    ) {
      val nonNullIcon: Icon = icon ?: return action(null)
      val iconDrawable: Drawable? =
        try {
          nonNullIcon.loadDrawable(renderContext)
        } catch (e: Exception) {
          logger.atSevere().withCause(e).log("Failed to load Drawable from Icon")
          null
        }
      if (iconDrawable != null) {
        logger.atFine().log("icon is replaced with Drawable loaded from Icon.")
        action(iconDrawable)
      } else {
        logger.atSevere().log("IconDrawable is null. Clearing the icon.")
        action(null)
      }
    }

    fun measureSize(view: View, viewSize: Size, minSize: Size, maxSize: Size): Size {
      if (
        viewSize.width != ViewGroup.LayoutParams.WRAP_CONTENT &&
          viewSize.height != ViewGroup.LayoutParams.WRAP_CONTENT
      ) {
        return Size(viewSize.width, viewSize.height)
      }
      val heightMeasureSpec =
        if (viewSize.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
          View.MeasureSpec.makeMeasureSpec(maxSize.height, View.MeasureSpec.AT_MOST)
        } else {
          View.MeasureSpec.makeMeasureSpec(viewSize.height, View.MeasureSpec.EXACTLY)
        }
      val widthMeasureSpec =
        if (viewSize.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
          View.MeasureSpec.makeMeasureSpec(maxSize.width, View.MeasureSpec.AT_MOST)
        } else {
          View.MeasureSpec.makeMeasureSpec(viewSize.width, View.MeasureSpec.EXACTLY)
        }
      view.measure(widthMeasureSpec, heightMeasureSpec)
      return Size(
        view.measuredWidth.coerceAtLeast(minSize.width),
        view.measuredHeight.coerceAtLeast(minSize.height),
      )
    }
  }
}
