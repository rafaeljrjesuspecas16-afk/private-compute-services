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

package com.android.personalcontext.ace.client.prototype.message

import android.os.Bundle
import com.android.personalcontext.ace.client.prototype.PrototypeHint
import com.android.personalcontext.ace.client.prototype.PrototypeHintId.MessageMetadataHintId

/** A hint for the Messages use case. Supplements MessagesHint with additional metadata. */
data class MessageMetadataHint(val suggestionLimit: Int = 0) :
  PrototypeHint(MessageMetadataHintId, this) {

  override fun exportDataToBundle(bundle: Bundle) {
    bundle.putInt(KEY_SUGGESTION_LIMIT, suggestionLimit)
  }

  companion object : Creator {
    private const val KEY_SUGGESTION_LIMIT = "suggestion_limit"

    override fun create(bundle: Bundle): PrototypeHint =
      MessageMetadataHint(suggestionLimit = bundle.getInt(KEY_SUGGESTION_LIMIT))
  }
}
