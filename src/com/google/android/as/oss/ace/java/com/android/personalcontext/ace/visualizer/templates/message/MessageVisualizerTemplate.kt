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

@file:Suppress("FlaggedApi", "NewApi")

package com.android.personalcontext.ace.visualizer.templates.message

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Icon
import android.service.personalcontext.PersonalContextManager
import android.service.personalcontext.hint.MessagesHint
import android.service.personalcontext.insight.ContextInsight
import android.service.personalcontext.insight.interaction.InsightEvent
import android.util.Log
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.android.personalcontext.ace.common.FindHintUtils.findContextHint
import com.android.personalcontext.ace.common.gradientTint
import com.android.personalcontext.ace.common.wrappers.IPublishedContextInsight
import com.android.personalcontext.ace.internal.energyeffects.EnergyEffectsAnimationUtils
import com.android.personalcontext.ace.visualizer.compat.ClientActionInsightCompat
import com.android.personalcontext.ace.visualizer.compat.EnergyEffectsAnimationCompat
import com.android.personalcontext.ace.visualizer.compat.FlexFontCompat
import com.android.personalcontext.ace.visualizer.templates.LocalInsightEventReporter
import com.android.personalcontext.ace.visualizer.templates.LocalInsightSurfaceClientInfo
import com.android.personalcontext.ace.visualizer.templates.LocalPublishedContextInsight
import com.android.personalcontext.ace.visualizer.templates.LocalRenderToken
import com.android.personalcontext.ace.visualizer.templates.VisualizerTemplate
import com.android.personalcontext.ace.visualizer.templates.message.MessageTemplateData.Companion.toMessageTemplateData
import com.android.personalcontext.ace.visualizer.templates.utils.EmbeddedTheme
import com.android.personalcontext.ace.visualizer.templates.utils.IconOrImage
import com.android.personalcontext.ace.visualizer.templates.utils.RemoteActionUtils.execute
import com.android.personalcontext.ace.visualizer.templates.utils.TintableIcon
import com.android.personalcontext.ace.visualizer.templates.utils.asTintableIcon
import javax.inject.Inject

class MessageVisualizerTemplate
@Inject
internal constructor(
  val flexFontCompat: FlexFontCompat,
  private val clientActionInsightCompat: ClientActionInsightCompat,
  private val energyEffectsAnimationCompat: EnergyEffectsAnimationCompat,
) : VisualizerTemplate {

  override fun handleInsight(
    publishedInsight: IPublishedContextInsight
  ): (@Composable () -> Unit)? {
    Log.d(TAG, "[MessagesEmbedded] handleInsight")
    val insight = publishedInsight.insight
    val unused = insight.findContextHint<MessagesHint>() ?: return null
    val messageTemplateData = insight.toMessageTemplateData(clientActionInsightCompat)
    return { MessageTemplate(messageTemplateData) }
  }

  @Composable
  private fun MessageTemplate(messageTemplateData: MessageTemplateData) {
    MainTheme() { MergedChipsRow(messageTemplateData) }
  }

  @Composable
  private fun MergedChipsRow(messageTemplateData: MessageTemplateData) {
    Log.d(
      TAG,
      "[MessagesEmbedded] MergedChipsRow chip count: ${messageTemplateData.messageChipList.size}",
    )
    Row(
      modifier = Modifier.wrapContentWidth().heightIn(48.dp).padding(vertical = 4.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.End),
      verticalAlignment = Alignment.Bottom,
    ) {
      val suggestionEnterEasing = CubicBezierEasing(0f, 0f, 0f, 1f)
      if (messageTemplateData.messageChipList.isNotEmpty()) {
        MessageAnimatedListItemVisibility(
          values = messageTemplateData.messageChipList,
          itemEnter = { _ ->
            scaleIn(
              animationSpec =
                tween(
                  durationMillis = MessageConstants.ANIMATION_REVEAL_DURATION_MILLIS,
                  delayMillis = MessageConstants.ANIMATION_REVEAL_DELAY_MILLIS,
                  easing = suggestionEnterEasing,
                )
            )
          },
        ) { messageChip ->
          when (messageChip) {
            is SuggestionChip -> MessageSuggestionChip(messageChip)
            is RemoteActionChip -> MessageRemoteActionChip(messageChip)
            is ClientActionChip -> MessageClientActionChip(messageChip)
          }
        }
      }
    }
  }

  @Composable
  fun MessageRemoteActionChip(remoteActionChip: RemoteActionChip) {
    Log.d(TAG, "[MessagesEmbedded] MessageRemoteActionChip: ${remoteActionChip.title}")
    val context = LocalContext.current
    MessageOutlinedButton(
      chipOnClick = {
        Log.d(TAG, "[MessagesEmbedded] remote action clicked")
        remoteActionChip.remoteAction.execute(context)
      },
      insight = remoteActionChip.insight,
    ) {
      MessageRowContent(
        title = remoteActionChip.title,
        subtitle = remoteActionChip.subtitle,
        contentDescription = remoteActionChip.contentDescription,
        icon = remoteActionChip.icon?.toBitmap(context)?.asTintableIcon(tintable = false),
      )
    }
  }

  @Composable
  fun MessageClientActionChip(clientActionChip: ClientActionChip) {
    Log.d(TAG, "[MessagesEmbedded] MessageClientActionChip: ${clientActionChip.title}")
    val context = LocalContext.current
    val info = LocalInsightSurfaceClientInfo.current
    MessageOutlinedButton(
      chipOnClick = {
        Log.d(TAG, "[MessagesEmbedded] client action clicked")
        info.onReceiveInsight(clientActionChip.insight)
      },
      insight = clientActionChip.insight,
    ) {
      val trailingIconBitmap = clientActionChip.trailingIcon?.toBitmap(context)
      val gradientModifier =
        if (trailingIconBitmap != null) {
          val primaryFixedDimColor = MaterialTheme.colorScheme.primaryFixedDim
          val primaryColor = MaterialTheme.colorScheme.primary
          Modifier.gradientTint(listOf(primaryFixedDimColor, primaryColor))
        } else {
          Modifier
        }
      MessageRowContent(
        title = clientActionChip.title,
        subtitle = clientActionChip.subtitle,
        contentDescription = clientActionChip.contentDescription,
        icon =
          clientActionChip.icon
            ?.toBitmap(context)
            ?.asTintableIcon(tintable = trailingIconBitmap != null),
        trailingIcon = trailingIconBitmap?.asTintableIcon(tintable = true),
        iconModifier = gradientModifier,
      )
    }
  }

  @Composable
  internal fun MessageSuggestionChip(suggestionChip: SuggestionChip) {
    Log.d(TAG, "[MessagesEmbedded] MessageSuggestionChip: ${suggestionChip.title}")
    val context = LocalContext.current
    val info = LocalInsightSurfaceClientInfo.current
    MessageOutlinedButton(
      chipOnClick = {
        Log.d(TAG, "[MessagesEmbedded] display insight clicked")
        info.onReceiveInsight(suggestionChip.insight)
      },
      insight = suggestionChip.insight,
    ) {
      MessageRowContent(
        title = suggestionChip.title,
        subtitle = suggestionChip.subtitle,
        contentDescription = suggestionChip.contentDescription,
        icon = suggestionChip.icon?.toBitmap(context)?.asTintableIcon(tintable = true),
      )
    }
  }

  @Composable
  private fun MessageOutlinedButton(
    chipOnClick: () -> Unit,
    insight: ContextInsight,
    chipContents: @Composable () -> Unit,
  ) {
    val shape =
      EmbeddedTheme.InlineSuggestion.shapes.suggestion
        ?: RoundedCornerShape(MessageConstants.CornerRadius)
    val interactionSource = remember { MutableInteractionSource() }
    val context = LocalContext.current
    val insightEventReporter = LocalInsightEventReporter.current
    val publishedInsight = LocalPublishedContextInsight.current
    val renderToken = LocalRenderToken.current

    val personalContextManager = remember {
      context.getSystemService(PersonalContextManager::class.java)
    }
    fun reportEvent(event: Int) {
      with(insightEventReporter) {
        personalContextManager?.reportChildInsightEvent(
          publishedInsight,
          insight,
          event,
          renderToken,
        )
      }
    }
    LaunchedEffect(Unit) { reportEvent(InsightEvent.EVENT_SHOW) }
    val density = LocalDensity.current
    val cornerRadius = remember(shape, density) { shape.toCornerRadius(density) }
    val colorScheme = MaterialTheme.colorScheme
    val geminiAnimationSpec =
      EnergyEffectsAnimationUtils.createChipSpec(
        cornerRadius = cornerRadius,
        density = density.density,
        colorScheme = colorScheme,
        context = context,
      )

    val strokeColor =
      EmbeddedTheme.InlineSuggestion.colorScheme.stroke ?: MaterialTheme.colorScheme.outlineVariant
    val backgroundColor =
      EmbeddedTheme.InlineSuggestion.colorScheme.suggestionBackground ?: Color.Transparent
    with(energyEffectsAnimationCompat) {
      Box(
        modifier =
          Modifier.clip(shape)
            .widthIn(min = 30.dp, max = 264.dp)
            .heightIn(min = 40.dp)
            .combinedClickable(
              onClick = {
                chipOnClick()
                reportEvent(InsightEvent.EVENT_USER_TAP)
              },
              onLongClick = {
                Log.d(TAG, "[MessagesEmbedded] chip long clicked")
                reportEvent(InsightEvent.EVENT_USER_LONG_PRESS)
              },
              interactionSource = interactionSource,
              indication = ripple(color = MaterialTheme.colorScheme.onSurface),
            )
            .applyEnergyEffectsAnimation(
              geminiAnimationSpec = geminiAnimationSpec,
              fallback = {
                animatedActionBorder(
                  cornerRadius = cornerRadius,
                  strokeColor = strokeColor,
                  backgroundColor = backgroundColor,
                )
              },
            )
            .semantics { role = Role.Button },
        contentAlignment = Alignment.Center,
      ) {
        chipContents()
      }
    }
  }

  @Composable
  private fun MessageRowContent(
    title: String,
    contentDescription: String,
    icon: TintableIcon?,
    iconModifier: Modifier = Modifier,
    subtitle: String? = null,
    trailingIcon: TintableIcon? = null,
  ) {
    Row(
      modifier =
        Modifier.clearAndSetSemantics(contentDescription)
          .padding(
            horizontal = MessageConstants.ButtonHorizontalPadding,
            vertical = MessageConstants.ButtonVerticalPadding,
          ),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      // Icon
      val tint =
        EmbeddedTheme.InlineSuggestion.colorScheme.icon ?: MaterialTheme.colorScheme.primary
      icon?.let {
        IconOrImage(
          icon = icon,
          modifier = Modifier.size(18.dp).align(Alignment.CenterVertically).then(iconModifier),
          tint = tint,
        )
      }

      // Text
      if (subtitle.isNullOrEmpty()) {
        SuggestionText(
          title,
          maxLines = 2,
          modifier = Modifier.align(Alignment.CenterVertically).weight(1f, fill = false),
        )
      } else {
        Column(modifier = Modifier.weight(1f, fill = false)) {
          SuggestionText(title, maxLines = 1)
          Text(
            text = subtitle,
            style =
              flexFontCompat.flexFont(
                style = MaterialTheme.typography.bodyMedium,
                weight = 550,
                round = 0f,
              ),
            color =
              EmbeddedTheme.InlineSuggestion.colorScheme.text
                ?: MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
        }
      }

      if (trailingIcon != null) {
        IconOrImage(
          icon = trailingIcon,
          modifier = Modifier.size(18.dp).align(Alignment.CenterVertically),
          tint = tint,
        )
      }
    }
  }

  @Composable
  private fun SuggestionText(text: String, maxLines: Int, modifier: Modifier = Modifier) {
    Text(
      text = text,
      modifier = modifier,
      style =
        flexFontCompat.flexFont(
          style = MaterialTheme.typography.labelLarge,
          weight = 500,
          round = 0f,
        ),
      color =
        EmbeddedTheme.InlineSuggestion.colorScheme.text ?: MaterialTheme.colorScheme.onSurface,
      overflow = TextOverflow.Ellipsis,
      maxLines = maxLines,
    )
  }

  private fun Icon.toBitmap(context: Context): Bitmap? {
    return try {
      this.loadDrawable(context)?.toBitmap()
    } catch (e: Exception) {
      Log.w(TAG, "[MessagesEmbedded] Failed to load icon to bitmap", e)
      null
    }
  }

  private fun Modifier.clearAndSetSemantics(description: String?): Modifier {
    if (description != null) {
      return clearAndSetSemantics { contentDescription = description }
    } else {
      return this
    }
  }

  /** Extracts a [CornerRadius] from a [CornerBasedShape] with the same radius for all corners. */
  private fun CornerBasedShape.toCornerRadius(density: Density): CornerRadius {
    val radiusPx = this.topStart.toPx(Size(10000f, 10000f), density)
    return CornerRadius(radiusPx)
  }

  private data class MessageColorScheme(
    val outlineVariant: Color,
    val onSurface: Color,
    val primary: Color,
    val backgroundColor: Color,
  )

  @Composable
  private fun MainTheme(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val colorScheme =
      if (isSystemInDarkTheme()) dynamicDarkColorScheme(context)
      else dynamicLightColorScheme(context)

    MaterialTheme(colorScheme = colorScheme, typography = Typography(), content = content)
  }

  companion object {
    const val TAG = "MessageVisualizerTemplate"
  }
}
