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

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer

/**
 * Applies a linear gradient tint to the content of the Composable.
 *
 * Commonly used to color icons or images with a gradient tint.
 *
 * @param colors The list of colors to use for the gradient, in order from top-left to bottom-right.
 */
fun Modifier.gradientTint(colors: List<Color>): Modifier =
  this.graphicsLayer(alpha = 0.99f).drawWithCache {
    val brush =
      Brush.linearGradient(
        colors = colors,
        start = Offset.Zero,
        end = Offset(size.width, size.height),
      )
    onDrawWithContent {
      drawContent()
      drawRect(brush = brush, blendMode = BlendMode.SrcIn)
    }
  }
