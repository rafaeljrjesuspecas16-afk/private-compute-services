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
import androidx.annotation.Keep // Used for parcelables so that they're the same module between APKs
import com.android.personalcontext.ace.client.prototype.PrototypeContextInsight
import com.android.personalcontext.ace.client.prototype.PrototypeInsightId.ClientActionInsightId
import com.android.personalcontext.ace.common.builders.insightDisplayDetails
import kotlin.jvm.java
import kotlinx.parcelize.Parcelize

/**
 * An insight for the client action.
 *
 * @property clientActionParams The parameters specific to the action type.
 * @property insightDisplayDetails Display details for the client action.
 * @property originHints The origin hints of the insight.
 */
data class ClientActionInsight(
  val clientActionParams: ClientActionParams,
  val insightDisplayDetails: InsightDisplayDetails,
  override val originHints: Set<PublishedContextHint>,
) : PrototypeContextInsight(ClientActionInsightId, this) {

  override fun exportDataToBundle(bundle: Bundle) {
    bundle.putParcelable(CLIENT_ACTION_PARAMS_KEY, clientActionParams)

    bundle.putBundle(
      CLIENT_ACTION_PARAMS_KEY,
      Bundle().apply { putParcelable(CLIENT_ACTION_PARAMS_KEY, clientActionParams) },
    )

    bundle.putParcelable(INSIGHT_DISPLAY_DETAILS_KEY, insightDisplayDetails)
  }

  companion object : PrototypeContextInsightCreator() {
    const val CLIENT_ACTION_PARAMS_KEY = "CLIENT_ACTION_PARAMS_KEY"
    const val INSIGHT_DISPLAY_DETAILS_KEY = "INSIGHT_DISPLAY_DETAILS_KEY"
    private const val TAG = "ClientActionInsight"

    override fun create(
      bundle: Bundle,
      originHints: Set<PublishedContextHint>,
    ): PrototypeContextInsight {
      bundle.classLoader = ClientActionParams::class.java.classLoader
      val clientActionParams =
        runCatching<ClientActionParams> {
            val rawValue = bundle.get(CLIENT_ACTION_PARAMS_KEY)
            val _clientActionParams =
              when (rawValue) {
                is Bundle -> {
                  rawValue.classLoader = ClientActionParams::class.java.classLoader
                  rawValue.getParcelable(CLIENT_ACTION_PARAMS_KEY, ClientActionParams::class.java)
                }
                is ClientActionParams -> rawValue
                else -> null
              }

            requireNotNull(_clientActionParams) { "bundle.get(CLIENT_ACTION_PARAMS_KEY) is null" }
          }
          .fold(
            onSuccess = { it },
            onFailure = { e ->
              Log.e(
                TAG,
                "Failed to get ClientActionParams. The most common reason is due to building SENDER and RECEIVER APKs from different source-control snapshots.",
                e,
              )
              UnknownParams()
            },
          )

      val insightDisplayDetails =
        runCatching<InsightDisplayDetails> {
            requireNotNull(
              bundle.getParcelable(INSIGHT_DISPLAY_DETAILS_KEY, InsightDisplayDetails::class.java)
            ) {
              "bundle.get(INSIGHT_DISPLAY_DETAILS_KEY) is null"
            }
          }
          .fold(
            onSuccess = { it },
            onFailure = { e ->
              Log.e(
                TAG,
                "Failed to get InsightDisplayDetails. Common reasons: 1. Check that the SENDER sent insightDisplayDetails. 2. (Unlikely) Check that SENDER and RECEIVER APKs are built from the same source-control snapshot.",
                e,
              )
              insightDisplayDetails("")
            },
          )

      return ClientActionInsight(
        clientActionParams = clientActionParams,
        insightDisplayDetails = insightDisplayDetails,
        originHints = originHints,
      )
    }
  }
}

/** Base interface for parameters specific to different types of client actions. */
sealed interface ClientActionParams : Parcelable

/** Unknown client action parameters. */
@Keep @Parcelize class UnknownParams : ClientActionParams

/**
 * Parameters for the SHARE_PHOTO client action.
 *
 * @property query The search query for the photo share action.
 */
@Keep @Parcelize data class SharePhotoParams(val query: String) : ClientActionParams

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
@Keep
@Parcelize
data class ShowCardsParams(
  val clientSessionId: String,
  val selectedCardIds: List<String>,
  val liveDataQueryBundle: Bundle?,
  val liveDataQueryIntent: PendingIntent?,
) : ClientActionParams

/** A FULL_SCREEN request client action. */
@Keep @Parcelize class FullScreenRequestParams : ClientActionParams

/**
 * Parameters for the text paste client action.
 *
 * @property text The text to be pasted.
 */
@Keep @Parcelize data class TextPasteParams(val text: String) : ClientActionParams

/** Parameters for the share live location action. */
@Keep @Parcelize class ShareLiveLocationParams : ClientActionParams
