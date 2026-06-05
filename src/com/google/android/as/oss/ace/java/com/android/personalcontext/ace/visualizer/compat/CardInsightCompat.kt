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

interface CardInsightCompat {

  /**
   * Runs the [block] if [insight] is a card insight, providing the safely validated insight as a
   * parameter to the lambda. Returns null if it is not a card insight.
   */
  fun <T> ifCardInsight(insight: ContextInsight, block: (SafeCardInsight) -> T): T? = null

  /** A safe wrapper that exposes properties for the card insight. */
  interface SafeCardInsight {

    /** Returns the title of this card insight. */
    val title: ContextInsight?

    /** Returns the header of this card insight. */
    val header: ContextInsight?

    /** Returns the body of this card insight. */
    val body: ContextInsight

    /** Returns the footer of this card insight. */
    val footer: ContextInsight?

    /** Returns the actions of this card insight. */
    val actions: ContextInsight?
  }
}
