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

package com.android.personalcontext.ace.client.prototype.clientaction.params.showcards

import android.app.PendingIntent
import android.os.Bundle
import com.android.personalcontext.ace.client.prototype.clientaction.params.ClientActionParamId
import com.android.personalcontext.ace.client.prototype.clientaction.params.ClientActionParams

/** Parameters for SHOW_CARDS ClientAction. */
data class ShowCardsParams(
  val clientSessionId: String,
  val selectedCardIds: List<String>,
  val liveDataQueryBundle: Bundle?,
  val liveDataQueryIntent: PendingIntent?,
) : ClientActionParams() {
  override val id = ClientActionParamId.SHOW_CARDS

  override fun writeToBundle(bundle: Bundle) {
    bundle.putString(CLIENT_SESSION_ID_KEY, clientSessionId)
    bundle.putStringArrayList(SELECTED_CARD_IDS_KEY, ArrayList(selectedCardIds))
    bundle.putBundle(LIVE_DATA_QUERY_BUNDLE_KEY, liveDataQueryBundle)
    bundle.putParcelable(LIVE_DATA_QUERY_INTENT_KEY, liveDataQueryIntent)
  }

  companion object : ClientActionParams.Creator {
    private const val CLIENT_SESSION_ID_KEY = "SHOW_CARDS_PARAMS__CLIENT_SESSION_ID"
    private const val SELECTED_CARD_IDS_KEY = "SHOW_CARDS_PARAMS__SELECTED_CARD_IDS"
    private const val LIVE_DATA_QUERY_BUNDLE_KEY = "SHOW_CARDS_PARAMS__LIVE_DATA_QUERY_BUNDLE"
    private const val LIVE_DATA_QUERY_INTENT_KEY = "SHOW_CARDS_PARAMS__LIVE_DATA_QUERY_INTENT"

    override fun create(bundle: Bundle): ShowCardsParams =
      ShowCardsParams(
        clientSessionId = bundle.getString(CLIENT_SESSION_ID_KEY) ?: "",
        selectedCardIds = bundle.getStringArrayList(SELECTED_CARD_IDS_KEY) ?: emptyList(),
        liveDataQueryBundle = bundle.getBundle(LIVE_DATA_QUERY_BUNDLE_KEY),
        liveDataQueryIntent =
          bundle.getParcelable(LIVE_DATA_QUERY_INTENT_KEY, PendingIntent::class.java),
      )
  }
}
