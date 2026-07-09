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

package com.android.personalcontext.ace.common

import android.service.personalcontext.insight.ContextInsight

/** A [ContextInsight] paired with a [label]. */
data class LabeledContextInsight(val label: String, val insight: ContextInsight?)

/** Builder function for [LabeledContextInsight]. */
infix fun ContextInsight?.labeled(label: String) = LabeledContextInsight(label, this)

/** Convenience function for labelling an indexed collection of [ContextInsight] with indices. */
fun Iterable<ContextInsight?>.withIndexLabels(): List<LabeledContextInsight> {
  return mapIndexed { index, insight -> insight labeled "$index" }
}
