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

package com.android.personalcontext.ace.common

import android.graphics.drawable.Icon
import android.os.Bundle

/**
 * Represents extended details for an insight.
 *
 * @property trailingIcon The optional [Icon] to display at the trailing edge of the insight.
 */
data class InsightExtendedDetails(val trailingIcon: Icon?) {

  /** Writes this [InsightExtendedDetails] to the given [bundle]. */
  fun writeToBundle(bundle: Bundle) {
    bundle.putParcelable(KEY_TRAILING_ICON, trailingIcon)
  }

  companion object {
    private const val KEY_TRAILING_ICON = "trailing_icon"

    /** Creates an [InsightExtendedDetails] from the given [bundle]. */
    fun createFromBundle(bundle: Bundle): InsightExtendedDetails {
      return InsightExtendedDetails(
        trailingIcon = bundle.getParcelable(KEY_TRAILING_ICON, Icon::class.java)
      )
    }
  }
}
