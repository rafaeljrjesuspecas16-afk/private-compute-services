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

package com.android.personalcontext.ace.client.prototype.theme

import android.os.Bundle
import com.android.personalcontext.ace.client.prototype.PrototypeHint
import com.android.personalcontext.ace.client.prototype.PrototypeHintId.ThemeHintId

/**
 * A hint for the theme.
 *
 * @property type The type of theme.
 * @property data The data sent by the client.
 */
data class ThemeHint(val type: ThemeType, val data: Boolean) : PrototypeHint(ThemeHintId, this) {

  override fun exportDataToBundle(bundle: Bundle) {
    bundle.putInt(TYPE_KEY, type.ordinal)
    bundle.putBoolean(DATA_KEY, data)
  }

  companion object : Creator {
    const val TYPE_KEY = "TYPE_KEY"
    const val DATA_KEY = "DATA_KEY"

    override fun create(bundle: Bundle): PrototypeHint =
      ThemeHint(
        type = ThemeType.entries[bundle.getInt(TYPE_KEY)],
        data = bundle.getBoolean(DATA_KEY),
      )
  }
}
