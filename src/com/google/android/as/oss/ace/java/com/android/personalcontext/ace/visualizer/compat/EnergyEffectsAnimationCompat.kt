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

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color

interface EnergyEffectsAnimationCompat {
  /** The style of the Gemini Intelligence animation. */
  enum class AnimationStyle {
    /** An animation style for chips. */
    CHIP,
    /** An animation style for cards. */
    CARD,
  }

  /**
   * Modifier that applies the energy effects animation to the composable if enabled, otherwise
   * applies the [fallback] modifier.
   *
   * @param cornerRadius The corner radius of the composable.
   * @param strokeColor The stroke color of the composable.
   * @param style The style of the animation.
   * @param fallback A modifier to apply if the energy effects animation is not enabled.
   */
  @Composable
  fun applyEnergyEffectsAnimation(
    cornerRadius: CornerRadius,
    strokeColor: Color,
    style: AnimationStyle,
    fallback: @Composable Modifier.() -> Modifier,
  ): Modifier = Modifier

  /**
   * Modifier that applies the energy effects animation to the composable if enabled, otherwise
   * returns the original modifier.
   *
   * @param cornerRadius The corner radius of the composable.
   * @param strokeColor The stroke color of the composable.
   * @param style The style of the animation.
   */
  @Composable
  fun applyEnergyEffectsAnimation(
    cornerRadius: CornerRadius,
    strokeColor: Color,
    style: AnimationStyle = AnimationStyle.CHIP,
  ): Modifier = applyEnergyEffectsAnimation(cornerRadius, strokeColor, style, fallback = { this })
}
