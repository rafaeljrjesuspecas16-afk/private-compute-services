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

package com.android.personalcontext.ace.client.prototype.clientaction.params.sharephoto

import android.os.Bundle
import com.android.personalcontext.ace.client.prototype.clientaction.params.ClientActionParamId
import com.android.personalcontext.ace.client.prototype.clientaction.params.ClientActionParams

/** Parameters for SHARE_PHOTO ClientAction. */
data class SharePhotoParams(val query: String) : ClientActionParams() {
  override val id = ClientActionParamId.SHARE_PHOTO

  override fun writeToBundle(bundle: Bundle) {
    bundle.putString(QUERY_KEY, query)
  }

  companion object : ClientActionParams.Creator {
    private const val QUERY_KEY = "SHARE_PHOTO_PARAMS__QUERY"

    override fun create(bundle: Bundle): SharePhotoParams =
      SharePhotoParams(bundle.getString(QUERY_KEY) ?: "")
  }
}
