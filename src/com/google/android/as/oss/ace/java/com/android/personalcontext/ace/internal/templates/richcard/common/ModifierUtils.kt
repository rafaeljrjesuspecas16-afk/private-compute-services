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

import android.app.ActivityOptions
import android.app.PendingIntent
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.shaderlib.energyeffects.EffectState
import com.google.android.shaderlib.energyeffects.builder.DefaultCardConfig
import com.google.android.shaderlib.energyeffects.compose.energyEffects
import com.google.ux.material.libmonetkt.energy.BaseColorRole
import com.google.ux.material.libmonetkt.energy.EnergyColors

/** Generic extension function for Modifier.clickable that accepts a PendingIntent. */
fun Modifier.clickable(pendingIntent: PendingIntent?, onClick: () -> Unit): Modifier {
  return if (pendingIntent == null) {
    this
  } else {
    this.clickable {
      try {
        pendingIntent.send(
          ActivityOptions.makeBasic()
            .apply {
              if (com.android.window.flags.ExportedFlags.balAdditionalStartModes()) {
                setPendingIntentBackgroundActivityStartMode(
                  ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOWED
                )
              }
            }
            .toBundle()
        )
        onClick()
      } catch (e: PendingIntent.CanceledException) {
        Log.e("ModifierUtils", "Failed to send pending intent", e)
      }
    }
  }
}

/** Overload for Modifier.clickable that accepts only a PendingIntent. */
fun Modifier.clickable(pendingIntent: PendingIntent?): Modifier {
  return this.clickable(pendingIntent = pendingIntent, onClick = {})
}

@Composable
fun Modifier.energyCardBackground(
  color: Color,
  timeSupplierMs: () -> Long = { System.currentTimeMillis() },
): Modifier {
  val context = LocalContext.current
  val mainExecutor = remember { ContextCompat.getMainExecutor(context) }
  var currentState by remember { mutableStateOf(EffectState.ENTRY) }

  val primaryColor = MaterialTheme.colorScheme.primary
  val secondaryColor = MaterialTheme.colorScheme.secondary
  val tertiaryColor = MaterialTheme.colorScheme.tertiary
  val surfaceColor = MaterialTheme.colorScheme.surface

  val energyColors =
    remember(color, primaryColor, secondaryColor, tertiaryColor, surfaceColor) {
      // We always use dynamic colors, hence isBaseline is always false.
      val isBaseline = false
      val colors =
        EnergyColors.withAccents(
          baseColor = color.toArgb(),
          primaryColor = primaryColor.toArgb(),
          secondaryColor = secondaryColor.toArgb(),
          tertiaryColor = tertiaryColor.toArgb(),
          surfaceColor = surfaceColor.toArgb(),
          isBaseline = isBaseline,
          baseColorRole = BaseColorRole.SURFACE,
        )
      intArrayOf(colors.getOrElse(0) { color.toArgb() }, colors.getOrElse(1) { color.toArgb() })
    }

  val cardConfig =
    remember(color, energyColors) {
      DefaultCardConfig(surfaceColor = color.toArgb(), energyColors = energyColors)
    }

  val baseConfig = remember(cardConfig) { cardConfig.initialConfig() }
  val stateMap = remember(cardConfig) { cardConfig.createEffectsBuilder().buildKeyframeSequences() }

  return this.energyEffects(
    initialConfig = baseConfig,
    state = currentState,
    stateMap = stateMap,
    onStateAnimationFinished = { finishedState, _ ->
      if (finishedState == EffectState.ENTRY) {
        currentState = EffectState.LOOP
      }
    },
    mainExecutor = mainExecutor,
    timeSupplierMs = timeSupplierMs,
  )
}
