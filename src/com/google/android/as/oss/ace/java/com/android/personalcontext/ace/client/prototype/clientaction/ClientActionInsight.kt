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

@file:Suppress("NewApi")

package com.android.personalcontext.ace.client.prototype.clientaction

import android.app.PendingIntent
import android.os.Bundle
import android.os.Parcelable
import android.service.personalcontext.hint.PublishedContextHint
import android.service.personalcontext.insight.InsightDisplayDetails
import android.util.Log
import androidx.annotation.Keep
import androidx.annotation.VisibleForTesting
import com.android.personalcontext.ace.client.prototype.PrototypeContextInsight
import com.android.personalcontext.ace.client.prototype.PrototypeInsightId.ClientActionInsightId
import com.android.personalcontext.ace.client.prototype.clientaction.params.ClientActionParams as ClientActionParamsBundle
import com.android.personalcontext.ace.client.prototype.clientaction.params.ClientActionParamsFactory
import com.android.personalcontext.ace.client.prototype.clientaction.params.UnknownParams as UnknownParamsBundle
import com.android.personalcontext.ace.client.prototype.clientaction.params.fullscreenrequest.FullScreenRequestParams as FullScreenRequestParamsBundle
import com.android.personalcontext.ace.client.prototype.clientaction.params.sharelivelocation.ShareLiveLocationParams as ShareLiveLocationParamsBundle
import com.android.personalcontext.ace.client.prototype.clientaction.params.sharephoto.SharePhotoParams as SharePhotoParamsBundle
import com.android.personalcontext.ace.client.prototype.clientaction.params.showcards.ShowCardsParams as ShowCardsParamsBundle
import com.android.personalcontext.ace.client.prototype.clientaction.params.textpaste.TextPasteParams as TextPasteParamsBundle
import com.android.personalcontext.ace.common.InsightExtendedDetails
import kotlinx.parcelize.Parcelize

/**
 * An insight for the client action
 *
 * @property clientActionParams The parameters specific to the action type
 * @property insightDisplayDetails Display details for the client action.
 * @property insightExtendedDetails Extended details for the client action.
 * @property originHints The origin hints of the insight.
 */
@Suppress("DEPRECATION")
data class ClientActionInsight(
  val clientActionParams: ClientActionParamsBundle,
  val insightDisplayDetails: InsightDisplayDetails,
  val insightExtendedDetails: InsightExtendedDetails? = null,
  override val originHints: Collection<PublishedContextHint>,
) : PrototypeContextInsight(ClientActionInsightId, this) {

  override fun exportDataToBundle(bundle: Bundle) {
    val legacyClientActionParams = convertToParamsLegacy(clientActionParams)
    if (legacyClientActionParams != null) {
      // This putParcelable() remains here to support legacy Parcelable representation. Receivers
      // should prefer to use [CLIENT_ACTION_PARAMS_BUNDLE_KEY] over
      // [CLIENT_ACTION_PARAMS_LEGACY_KEY]. If the params is a new type (ie. created after May
      // 2026), [CLIENT_ACTION_PARAMS_LEGACY_KEY] will be null because there is no legacy parcelable
      // representation for new params types.
      bundle.putParcelable(CLIENT_ACTION_PARAMS_LEGACY_KEY, legacyClientActionParams)
    }

    bundle.putBundle(
      CLIENT_ACTION_PARAMS_BUNDLE_KEY,
      Bundle().apply { clientActionParams.exportDataToBundle(this) },
    )
    bundle.putParcelable(INSIGHT_DISPLAY_DETAILS_KEY, insightDisplayDetails)
    if (insightExtendedDetails != null) {
      bundle.putBundle(
        INSIGHT_EXTENDED_DETAILS_KEY,
        Bundle().apply { insightExtendedDetails.writeToBundle(this) },
      )
    }
  }

  companion object : PrototypeContextInsightCreator() {
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    const val CLIENT_ACTION_PARAMS_LEGACY_KEY = "CLIENT_ACTION_PARAMS_KEY"
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    const val CLIENT_ACTION_PARAMS_BUNDLE_KEY = "CLIENT_ACTION_PARAMS_BUNDLE_KEY"
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    const val INSIGHT_DISPLAY_DETAILS_KEY = "INSIGHT_DISPLAY_DETAILS_KEY"
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    const val INSIGHT_EXTENDED_DETAILS_KEY = "INSIGHT_EXTENDED_DETAILS_KEY"
    private const val TAG = "ClientActionInsight"

    override fun create(
      bundle: Bundle,
      originHints: Set<PublishedContextHint>,
    ): PrototypeContextInsight {
      val clientActionParamsBundle =
        parseClientActionParamsBundle(bundle)
          ?: parseClientActionParamsLegacy(bundle)
          ?: UnknownParamsBundle()

      val insightDisplayDetails =
        requireNotNull(
          bundle.getParcelable(INSIGHT_DISPLAY_DETAILS_KEY, InsightDisplayDetails::class.java)
        )

      val insightExtendedDetails =
        bundle.getBundle(INSIGHT_EXTENDED_DETAILS_KEY)?.let {
          InsightExtendedDetails.createFromBundle(it)
        }

      return ClientActionInsight(
        clientActionParams = clientActionParamsBundle,
        insightDisplayDetails = insightDisplayDetails,
        insightExtendedDetails = insightExtendedDetails,
        originHints = originHints,
      )
    }

    /** Gets [ClientActionParamsBundle] from the bundle. */
    private fun parseClientActionParamsBundle(bundle: Bundle): ClientActionParamsBundle? {
      val clientActionBundle = bundle.getBundle(CLIENT_ACTION_PARAMS_BUNDLE_KEY)
      val clientActionParamsBundle = clientActionBundle?.let {
        ClientActionParamsFactory.create(it)
      }

      return clientActionParamsBundle
    }

    /** Gets [ClientActionParamsBundle] from the legacy Parcelable representation. */
    private fun parseClientActionParamsLegacy(bundle: Bundle): ClientActionParamsBundle? {
      bundle.classLoader = ClientActionParams::class.java.classLoader
      val legacyClientActionParams: ClientActionParams? =
        runCatching<ClientActionParams> {
            val rawValue = bundle.get(CLIENT_ACTION_PARAMS_LEGACY_KEY)
            val actionParams =
              when (rawValue) {
                is Bundle -> {
                  rawValue.classLoader = ClientActionParams::class.java.classLoader
                  rawValue.getParcelable(
                    CLIENT_ACTION_PARAMS_LEGACY_KEY,
                    ClientActionParams::class.java,
                  )
                }
                is ClientActionParams -> rawValue
                else -> null
              }

            requireNotNull(actionParams) { "bundle.get(CLIENT_ACTION_PARAMS_LEGACY_KEY) is null" }
          }
          .getOrElse { e ->
            Log.e(TAG, "Failed to get Legacy ClientActionParams.", e)
            null
          }

      if (legacyClientActionParams == null) {
        Log.v(TAG, "[parseParcelableParams] legacyClientActionParams is null")
        return null
      }

      Log.i(
        TAG,
        "[parseParcelableParams] Received legacy Parcelable params, converting to Bundle params.",
      )
      return convertToParamsBundle(legacyClientActionParams)
    }

    /**
     * Converts a [ClientActionParamsBundle] to a (Legacy) ClientActionParams. Should not add any
     * new cases. All future Bundle-packed params will return null, since they will not have a
     * legacy Parcelable representation.
     *
     * This will always be used by the SENDING APK to support RECEIVING APKs that are still using
     * the legacy Parcelable representation.
     */
    private fun convertToParamsLegacy(paramsBundle: ClientActionParamsBundle): ClientActionParams? =
      when (paramsBundle) {
        is SharePhotoParamsBundle -> SharePhotoParams(query = paramsBundle.query)
        is ShowCardsParamsBundle ->
          ShowCardsParams(
            clientSessionId = paramsBundle.clientSessionId,
            selectedCardIds = paramsBundle.selectedCardIds,
            liveDataQueryBundle = paramsBundle.liveDataQueryBundle,
            liveDataQueryIntent = paramsBundle.liveDataQueryIntent,
          )
        is FullScreenRequestParamsBundle -> FullScreenRequestParams()
        is TextPasteParamsBundle -> TextPasteParams(text = paramsBundle.text)
        is ShareLiveLocationParamsBundle -> ShareLiveLocationParams()
        is UnknownParamsBundle -> UnknownParams()
        else -> null
      }

    /**
     * Converts a legacy Parcelable ClientActionParams to a [ClientActionParamsBundle].
     *
     * Should not add any new cases. All new params should be Bundle-packed from the start with no
     * Parcelable representation.
     *
     * This *may* be used by the RECEIVING APK if the SENDING APK is still sending legacy Parcelable
     * params.
     */
    private fun convertToParamsBundle(legacyParams: ClientActionParams): ClientActionParamsBundle =
      when (legacyParams) {
        is SharePhotoParams -> SharePhotoParamsBundle(query = legacyParams.query)
        is ShowCardsParams ->
          ShowCardsParamsBundle(
            clientSessionId = legacyParams.clientSessionId,
            selectedCardIds = legacyParams.selectedCardIds,
            liveDataQueryBundle = legacyParams.liveDataQueryBundle,
            liveDataQueryIntent = legacyParams.liveDataQueryIntent,
          )
        is FullScreenRequestParams -> FullScreenRequestParamsBundle()
        is TextPasteParams -> TextPasteParamsBundle(text = legacyParams.text)
        is ShareLiveLocationParams -> ShareLiveLocationParamsBundle()
        else -> UnknownParamsBundle()
      }
  }
}

/** Base interface for parameters specific to different types of client actions. */
@Deprecated("Use Bundle-backed ClientActionParams instead (instead of Parcelable).")
sealed interface ClientActionParams : Parcelable

/** Unknown client action parameters. */
@Suppress("DEPRECATION")
@Deprecated("Use Bundle-backed ClientActionParams instead (instead of Parcelable).")
@Keep
@Parcelize
class UnknownParams : ClientActionParams

/**
 * Parameters for the SHARE_PHOTO client action.
 *
 * @property query The search query for the photo share action.
 */
@Suppress("DEPRECATION")
@Deprecated("Use Bundle-backed ClientActionParams instead (instead of Parcelable).")
@Keep
@Parcelize
data class SharePhotoParams(val query: String) : ClientActionParams

/**
 * Parameters for the SHOW_CARDS client action. When client app receives this action, it should:
 * 1. Create card DUI session.
 * 2. Render the card embedded UI via publishing a @link{CardHint}, which contains the
 *    clientSessionId and a list of card IDs that is wrapped within the parameters of this action.
 * 3. If liveDataQueryIntent is provided, client app should trigger the intent to fetch live data.
 *
 * @property clientSessionId The DAG client session ID of the request in PSI.
 * @property selectedCardIds A list of card IDs that are selected by the user.
 * @property liveDataQueryBundle The live data query bundle for the selected cards.
 * @property liveDataQueryIntent An optional pending intent to fetch the live data. This will be
 *   deprecated in favor of liveInfoRequest.
 */
@Suppress("DEPRECATION")
@Deprecated("Use Bundle-backed ClientActionParams instead (instead of Parcelable).")
@Keep
@Parcelize
data class ShowCardsParams(
  val clientSessionId: String,
  val selectedCardIds: List<String>,
  val liveDataQueryBundle: Bundle?,
  val liveDataQueryIntent: PendingIntent?,
) : ClientActionParams

/** A FULL_SCREEN request client action. */
@Suppress("DEPRECATION")
@Deprecated("Use Bundle-backed ClientActionParams instead (instead of Parcelable).")
@Keep
@Parcelize
class FullScreenRequestParams : ClientActionParams

/**
 * Parameters for the text paste client action.
 *
 * @property text The text to be pasted.
 */
@Suppress("DEPRECATION")
@Deprecated("Use Bundle-backed ClientActionParams instead (instead of Parcelable).")
@Keep
@Parcelize
data class TextPasteParams(val text: String) : ClientActionParams

/** Parameters for the share live location action. */
@Suppress("DEPRECATION")
@Deprecated("Use Bundle-backed ClientActionParams instead (instead of Parcelable).")
@Keep
@Parcelize
class ShareLiveLocationParams : ClientActionParams
