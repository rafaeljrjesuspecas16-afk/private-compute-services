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

package com.android.personalcontext.ace.client.prototype.richcard

import android.os.Bundle
import com.android.personalcontext.ace.client.prototype.PrototypeHint
import com.android.personalcontext.ace.client.prototype.PrototypeHintId.RichCardErrorHintId

/**
 * A hint that is used to propagate errors from the client app to the ACE server.
 *
 * @property clientSessionId The DAG client session ID for generating the card preview chip that is
 *   clicked.
 * @property cardIds A list of card IDs that are associated with the preview chip that is clicked.
 * @property message The error message that is returned from the client app when the card cannot be
 *   rendered properly, like live data is missing.
 * @property exceptionName The name of the exception that was thrown, if any.
 */
data class RichCardErrorHint(
  val clientSessionId: String,
  val cardIds: List<String>,
  val message: String,
  val exceptionName: String? = null,
) : PrototypeHint(RichCardErrorHintId, this) {
  override fun exportDataToBundle(bundle: Bundle) {
    bundle.putString(CLIENT_SESSION_ID_KEY, clientSessionId)
    bundle.putStringArrayList(CARD_IDS_KEY, ArrayList(cardIds))
    bundle.putString(MESSAGE_KEY, message)
    bundle.putString(EXCEPTION_NAME_KEY, exceptionName)
  }

  companion object : Creator {
    private const val CLIENT_SESSION_ID_KEY = "clientSessionId"
    private const val CARD_IDS_KEY = "cardIds"
    private const val MESSAGE_KEY = "message"
    private const val EXCEPTION_NAME_KEY = "exceptionName"

    override fun create(bundle: Bundle): PrototypeHint {
      return RichCardErrorHint(
        bundle.getString(CLIENT_SESSION_ID_KEY) ?: "",
        bundle.getStringArrayList(CARD_IDS_KEY) ?: emptyList(),
        bundle.getString(MESSAGE_KEY) ?: "",
        bundle.getString(EXCEPTION_NAME_KEY),
      )
    }
  }
}
