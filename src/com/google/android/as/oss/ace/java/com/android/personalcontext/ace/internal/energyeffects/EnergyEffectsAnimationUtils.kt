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

package com.android.personalcontext.ace.internal.energyeffects

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.material3.ColorScheme
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
import androidx.core.content.ContextCompat
import com.google.android.libraries.material.gm3.color.tokens.R
import com.google.android.shaderlib.energyeffects.BorderLightConfig
import com.google.android.shaderlib.energyeffects.DotsConfig
import com.google.android.shaderlib.energyeffects.EffectState
import com.google.android.shaderlib.energyeffects.EnergyShaderConfig
import com.google.android.shaderlib.energyeffects.LayoutConfig
import com.google.android.shaderlib.energyeffects.animation.KeyframeSequence
import com.google.android.shaderlib.energyeffects.animation.buildTransitions
import com.google.android.shaderlib.energyeffects.builder.DefaultChipConfig
import com.google.android.shaderlib.energyeffects.builder.copy
import com.google.android.shaderlib.energyeffects.colors.EnergyColors
import com.google.android.shaderlib.energyeffects.compose.energyEffects
import com.google.android.shaderlib.energyeffects.shader.ShaderUniforms
import com.google.android.shaderlib.energyeffects.utils.CornerRadii
import com.google.android.shaderlib.energyeffects.utils.Margins
import com.google.android.shaderlib.energyeffects.view.EnergyShaderDrawable

/** Helper utilities for rendering and controlling the energy shader effects animations. */
object EnergyEffectsAnimationUtils {

  /**
   * Applies the energy effects animation to the composable [Modifier].
   *
   * @param geminiAnimationSpec The specific spec of the animation to render.
   */
  @Composable
  fun Modifier.applyEnergyEffectsAnimation(geminiAnimationSpec: GeminiAnimationSpec): Modifier {
    var currentAppState by remember { mutableStateOf(EffectState.ENTRY) }

    return this.energyEffects(
      initialConfig = geminiAnimationSpec.config,
      state = currentAppState,
      stateMap = geminiAnimationSpec.stateMap,
      onStateAnimationFinished = { state, _ ->
        if (state == EffectState.ENTRY) {
          currentAppState = EffectState.LOOP
        }
      },
      mainExecutor = ContextCompat.getMainExecutor(LocalContext.current),
    )
  }

  /**
   * Creates and starts a themed [Drawable] that applies the effects animation.
   *
   * @param context The [Context] to resolve theme colors and resources.
   * @param geminiAnimationSpec The spec of the animation.
   */
  fun getAndStartEffectsDrawable(
    context: Context,
    geminiAnimationSpec: GeminiAnimationSpec,
  ): Drawable? {
    val drawable = getEffectsDrawable(context, geminiAnimationSpec) ?: return null
    startAnimation(drawable)
    return drawable
  }

  private fun getEffectsDrawable(
    context: Context,
    geminiAnimationSpec: GeminiAnimationSpec,
  ): Drawable? {
    var drawableRef: EnergyShaderDrawable? = null
    val drawable =
      EnergyShaderDrawable(
        context = context,
        initialConfig = geminiAnimationSpec.config,
        keyframesForStates = geminiAnimationSpec.stateMap,
        initialState = EffectState.HIDDEN,
        onStateUpdateFinishedCallback = { finishedState ->
          if (finishedState == EffectState.ENTRY) {
            drawableRef?.updateState(EffectState.LOOP)
          }
        },
      )
    drawableRef = drawable
    return drawable
  }

  private fun startAnimation(drawable: Drawable) {
    (drawable as? EnergyShaderDrawable)?.updateState(EffectState.ENTRY)
  }

  /**
   * A data class that holds the configuration and keyframe sequences for a Gemini animation style.
   *
   * @param config The [EnergyShaderConfig] that defines the visual appearance of the animation.
   * @param stateMap A map of [EffectState] to [KeyframeSequence] that defines the animation
   *   sequence.
   */
  data class GeminiAnimationSpec(
    val config: EnergyShaderConfig,
    val stateMap: Map<EffectState, KeyframeSequence>,
  )

  @Composable
  fun createChipSpec(
    cornerRadius: CornerRadius,
    density: Float,
    colorScheme: ColorScheme,
    context: Context,
  ): GeminiAnimationSpec {
    return remember(cornerRadius, density, colorScheme, context) {
      val resId = R.color.gm3_sys_color_dynamic_light_primary
      val colors = EnergyColors.from(resId, context)

      val energyColor1 = colors[0] // middle
      val energyColor2 = colors[1] // end
      val chipConfig =
        MessageInlineChipConfig(
          surfaceColor = colorScheme.surface.toArgb(),
          energyColors = intArrayOf(energyColor1, energyColor2),
          cornerRadii = CornerRadii(cornerRadius.x / density),
        )
      val builder = chipConfig.createEffectsBuilder(null)
      GeminiAnimationSpec(chipConfig.chipEntryConfig(), builder.buildKeyframeSequences())
    }
  }

  @Composable
  fun createCardSpec(
    cornerRadius: CornerRadius,
    density: Float,
    glowColor: Color,
  ): GeminiAnimationSpec {
    return remember(cornerRadius, density, glowColor) {
      val glowColorArgb = glowColor.toArgb()
      createCardAnimationSpec(cornerRadius.x, glowColorArgb)
    }
  }

  private fun createCardAnimationSpec(cornerRadiusDp: Float, glowColor: Int): GeminiAnimationSpec {
    val config =
      EnergyShaderConfig(
        layout = LayoutConfig(cornerRadii = CornerRadii(cornerRadiusDp)),
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
