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

package com.google.android.`as`.oss.feedback.domain

/**
 * Represents a single selectable "ground truth" option. These options are typically presented to
 * the user as potential corrections when they are providing feedback, especially when they've
 * indicated a piece of information is incorrect.
 *
 * For instance, if the system suggested an entity, and the user marks it as wrong, a list of
 * [GroundTruthData] instances might be shown as alternative, correct suggestions the user can
 * choose from.
 *
 * @property label The primary text displayed to the user for this ground truth option. This is the
 *   actual value or description of the alternative.
 * @property sourceApp The name for the application or source from which this ground truth option
 *   was derived. If provided, this is often displayed as a tag or label alongside the [label] in
 *   the UI to give context (e.g., "[Gmail]", "[Keep]"). Defaults to an empty string if the source
 *   is not specified.
 */
data class GroundTruthData(val label: String, val sourceApp: String = "")
