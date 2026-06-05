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

package com.android.personalcontext.ace.internal.compat

import androidx.compose.animation.core.LinearEasing
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import com.android.personalcontext.ace.visualizer.compat.EnergyEffectsAnimationCompat
import com.android.personalcontext.ace.visualizer.compat.ThemeCompat
import com.android.personalcontext.ace.visualizer.templates.LocalPublishedContextInsight
import com.google.android.shaderlib.energyeffects.BorderLightConfig
import com.google.android.shaderlib.energyeffects.DotsConfig
import com.google.android.shaderlib.energyeffects.EffectState
import com.google.android.shaderlib.energyeffects.EnergyShaderConfig
import com.google.android.shaderlib.energyeffects.LayoutConfig
import com.google.android.shaderlib.energyeffects.animation.KeyframeSequence
import com.google.android.shaderlib.energyeffects.animation.buildTransitions
import com.google.android.shaderlib.energyeffects.builder.DefaultChipConfig
import com.google.android.shaderlib.energyeffects.builder.copy
import com.google.android.shaderlib.energyeffects.compose.energyEffects
import com.google.android.shaderlib.energyeffects.shader.ShaderUniforms
import com.google.android.shaderlib.energyeffects.utils.CornerRadii
import com.google.android.shaderlib.energyeffects.utils.Margins
import javax.inject.Inject

/**
 * An implementation of [EnergyEffectsAnimationCompat] that provides Gemini Intelligence energy
 * effects animation.
 */
class EnergyEffectsAnimationCompatImpl @Inject constructor(private val themeCompat: ThemeCompat) :
  EnergyEffectsAnimationCompat {

  @Composable
  override fun applyEnergyEffectsAnimation(
    cornerRadius: CornerRadius,
    strokeColor: Color,
    style: EnergyEffectsAnimationCompat.AnimationStyle,
    fallback: @Composable Modifier.() -> Modifier,
  ): Modifier {
    val publishedInsight = LocalPublishedContextInsight.current
    // Check if the top-most insight has the animation v2 hint set. This follows the
    // contract that the ThemeHint must be set on the top-most insight.
    val showAnimationV2 = with(themeCompat) { publishedInsight.insight.shouldShowAnimationV2() }

    if (!showAnimationV2 && style == EnergyEffectsAnimationCompat.AnimationStyle.CHIP) {
      return Modifier.fallback()
    }

    val density = LocalDensity.current
    var currentAppState by remember { mutableStateOf(EffectState.ENTRY) }
    val colorScheme = MaterialTheme.colorScheme

    val spec =
      remember(cornerRadius, density, colorScheme, style) {
        when (style) {
          EnergyEffectsAnimationCompat.AnimationStyle.CHIP ->
            createChipSpec(cornerRadius, density.density, colorScheme)
          EnergyEffectsAnimationCompat.AnimationStyle.CARD ->
            createCardSpec(cornerRadius, density.density, colorScheme)
        }
      }

    return Modifier.energyEffects(
      initialConfig = spec.config,
      state = currentAppState,
      stateMap = spec.stateMap,
      onStateAnimationFinished = { state, _ ->
        if (state == EffectState.ENTRY) {
          currentAppState = EffectState.LOOP
        }
      },
      mainExecutor = LocalContext.current.mainExecutor,
    )
  }

  private data class GeminiAnimationSpec(
    val config: EnergyShaderConfig,
    val stateMap: Map<EffectState, KeyframeSequence>,
  )

  private fun createChipSpec(
    cornerRadius: CornerRadius,
    density: Float,
    colorScheme: ColorScheme,
  ): GeminiAnimationSpec {
    val chipConfig =
      MessageInlineChipConfig(
        surfaceColor = colorScheme.surface.toArgb(),
        cornerRadii = CornerRadii(cornerRadius.x / density),
      )
    val builder = chipConfig.createEffectsBuilder(null)
    return GeminiAnimationSpec(chipConfig.chipEntryConfig(), builder.buildKeyframeSequences())
  }

  private fun createCardSpec(
    cornerRadius: CornerRadius,
    density: Float,
    colorScheme: ColorScheme,
  ): GeminiAnimationSpec {
    val glowColor = colorScheme.onSurface.toArgb()
    val config =
      EnergyShaderConfig(
        layout = LayoutConfig(cornerRadii = CornerRadii(cornerRadius.x)),
        borderLight =
          BorderLightConfig(
            strokeWidth = 1f,
            color = glowColor,
            alpha = 0f,
            topIntensity = 0.4f,
            bottomIntensity = 0.1f,
            topBlurRadius = 20f,
            bottomBlurRadius = 20f,
            distortionSpeed = 0.45f,
            distortionFrequency = 0.0033f,
            distortionAmplitude = 15.75f,
          ),
        panel = null,
        radialGradient = null,
        rimLight = null,
        spotLight = null,
        rippleLight = null,
        dots =
          DotsConfig(
            alpha = 0f,
            maskStrokeWidth = 30f,
            waveAmplitude = 10f,
            waveFrequency = 0.2f,
            color = glowColor,
          ),
        loader = null,
      )

    val stateMap =
      mapOf(
        EffectState.ENTRY to
          KeyframeSequence(
            buildTransitions {
              transition(duration = 500, easing = LinearEasing) {
                keyframe(ShaderUniforms.borderLightAlpha, 0.5f)
              }
              transition(duration = 500, easing = LinearEasing) {
                keyframe(ShaderUniforms.borderLightAlpha, 0f)
              }
            } +
              buildTransitions {
                transition(duration = 200, easing = LinearEasing) {
                  keyframe(ShaderUniforms.dotsAlpha, 0f)
                }
                transition(duration = 700, easing = LinearEasing) {
                  keyframe(ShaderUniforms.dotsAlpha, 0.5f)
                }
                transition(duration = 700, easing = LinearEasing) {
                  keyframe(ShaderUniforms.dotsAlpha, 0f)
                }
              }
          ),
        EffectState.LOOP to
          KeyframeSequence(
            buildTransitions {
              transition(duration = 2000, easing = LinearEasing) {
                keyframe(ShaderUniforms.borderLightAlpha, 0f)
                keyframe(ShaderUniforms.dotsAlpha, 0f)
              }
            },
            isRepeatable = false,
          ),
      )

    return GeminiAnimationSpec(config, stateMap)
  }
}

private const val CHIP_TRANSITION_MS = 1200L
private const val CHIP_CRYSTALLIZED_DELAY_MS = 4000L

class MessageInlineChipConfig(
  surfaceColor: Int? = null,
  energyColors: IntArray? = null,
  private val cornerRadii: CornerRadii? = null,
) : DefaultChipConfig(surfaceColor, energyColors) {
  override fun chipBaseConfig(): EnergyShaderConfig {
    return super.chipBaseConfig().copy {
      cornerRadii?.let {
        layout = layout.copy {
          cornerRadii = it
          margins = Margins(0f)
        }
      }
    }
  }
}
