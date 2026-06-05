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

package com.android.personalcontext.ace.client.prototype.dialer

import android.os.Bundle
import com.android.personalcontext.ace.client.prototype.PrototypeHint
import com.android.personalcontext.ace.client.prototype.PrototypeHintId.DialerMetadataHintId
import com.google.android.libraries.pixel.psi.delegatedui.ace.hints.call.PixelDialerMetadata
import com.google.android.libraries.pixel.psi.delegatedui.ace.hints.call.PixelDialerMetadataKeys

/**
 * A hint for the Weather Event Suggestion / Location Suggestion use case. Check [redacted] for more
 * details.
 */
data class DialerMetadataHint(val metadata: PixelDialerMetadata) :
  PrototypeHint(DialerMetadataHintId, this) {

  override fun exportDataToBundle(bundle: Bundle) {
    bundle.putByteArray(PixelDialerMetadataKeys.KEY_PIXEL_DIALER_METADATA, metadata.toByteArray())
  }

  companion object : Creator {
    override fun create(bundle: Bundle): PrototypeHint =
      DialerMetadataHint(
        metadata =
          PixelDialerMetadata.parseFrom(
            requireNotNull(bundle.getByteArray(PixelDialerMetadataKeys.KEY_PIXEL_DIALER_METADATA)) {
              "[CallEmbedded] Bundle is missing KEY_PIXEL_DIALER_METADATA. Ensure you created the hint with a well formed [PixelDialerMetadata]"
            }
          )
      )
  }
}
