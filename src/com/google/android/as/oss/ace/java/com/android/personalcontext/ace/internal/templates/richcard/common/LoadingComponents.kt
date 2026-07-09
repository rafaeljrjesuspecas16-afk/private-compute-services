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

package com.android.personalcontext.ace.internal.templates.richcard.common

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** A reusable box with a pulsing alpha animation to indicate loading. */
@Composable
fun LoadingBox(
  modifier: Modifier = Modifier,
  shape: RoundedCornerShape = RoundedCornerShape(4.dp),
) {
  val transition = rememberInfiniteTransition(label = "loading")
  val alpha by
    transition.animateFloat(
      initialValue = 0.5f,
      targetValue = 0.9f,
      animationSpec =
        infiniteRepeatable(
          animation = tween(durationMillis = 1000, easing = LinearEasing),
          repeatMode = RepeatMode.Reverse,
        ),
      label = "alpha",
    )

  Box(
    modifier =
      modifier.background(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = alpha),
        shape = shape,
      )
  )
}
