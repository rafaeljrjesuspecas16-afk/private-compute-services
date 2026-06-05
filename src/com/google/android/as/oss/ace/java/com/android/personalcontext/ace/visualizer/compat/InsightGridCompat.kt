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

package com.android.personalcontext.ace.visualizer.compat

import android.service.personalcontext.insight.ContextInsight
import com.android.personalcontext.ace.common.InsightGridItem

interface InsightGridCompat {

  /** The total span capacity for phone devices. */
  val totalSpanCapacityPhone: Int
    get() = 1

  /** The total span capacity for watch devices. */
  val totalSpanCapacityWatch: Int
    get() = 1

  /**
   * Runs the [block] if [insight] is an insight grid, providing the safely validated insight as a
   * parameter to the lambda. Returns null if it is not an insight grid.
   */
  fun <T> ifInsightGrid(insight: ContextInsight, block: (SafeInsightGrid) -> T): T? = null

  /** A safe wrapper that exposes properties for the insight grid. */
  interface SafeInsightGrid {

    /** Returns the items of this insight grid. */
    val items: List<InsightGridItem>
  }
}
