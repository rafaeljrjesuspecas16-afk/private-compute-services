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

package com.android.personalcontext.ace.visualizer.compat

import android.service.personalcontext.hint.ContextHint
import android.service.personalcontext.insight.ContextInsight
import android.service.personalcontext.insight.InsightCollection
import com.android.personalcontext.ace.common.LabeledContextInsight

interface PrototypeTransformCompat {

  /** Transforms the [ContextHint] into a special type name, if possible. */
  fun transform(hint: ContextHint): String? = null

  /** Transforms the [ContextInsight] into a special type name, if possible. */
  fun transform(insight: ContextInsight): String? = null

  /**
   * Transforms the [InsightCollection] into a prototype instance, if possible, and return its
   * [ContextInsight] children.
   */
  fun transformChildren(insight: ContextInsight): List<LabeledContextInsight>? = null
}
