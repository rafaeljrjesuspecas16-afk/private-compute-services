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

package com.android.personalcontext.ace.client.prototype.clientaction.params.textpaste

import android.os.Bundle
import com.android.personalcontext.ace.client.prototype.clientaction.params.ClientActionParamId
import com.android.personalcontext.ace.client.prototype.clientaction.params.ClientActionParams

/** Parameters for TEXT_PASTE ClientAction. */
data class TextPasteParams(val text: String) : ClientActionParams() {
  override val id = ClientActionParamId.TEXT_PASTE

  override fun writeToBundle(bundle: Bundle) {
    bundle.putString(TEXT_KEY, text)
  }

  companion object : ClientActionParams.Creator {
    private const val TEXT_KEY = "TEXT_PASTE_PARAMS__TEXT"

    override fun create(bundle: Bundle): TextPasteParams =
      TextPasteParams(bundle.getString(TEXT_KEY) ?: "")
  }
}
