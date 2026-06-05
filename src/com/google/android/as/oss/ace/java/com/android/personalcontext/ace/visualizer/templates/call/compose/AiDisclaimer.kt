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

package com.android.personalcontext.ace.visualizer.templates.call.compose

import android.service.personalcontext.insight.DisplayInsight
import android.util.Log
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

private const val TAG = "AiDisclaimer"

@Composable
internal fun AiDisclaimer(aiDisclaimer: DisplayInsight) {
  val text = aiDisclaimer.details.title.toString()
  if (text.isBlank()) {
    Log.v(TAG, "[CallEmbedded] AiDisclaimer text is blank")
    return
  }

  Text(
    text = text,
    style = MaterialTheme.typography.labelSmall,
    color = MaterialTheme.colorScheme.onSurfaceVariant,
  )
}
