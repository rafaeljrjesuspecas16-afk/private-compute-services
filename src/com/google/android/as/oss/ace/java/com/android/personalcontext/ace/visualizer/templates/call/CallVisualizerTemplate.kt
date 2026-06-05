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

package com.android.personalcontext.ace.visualizer.templates.call

import android.service.personalcontext.hint.CallHint
import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.android.personalcontext.ace.common.FindHintUtils.findContextHint
import com.android.personalcontext.ace.common.wrappers.IPublishedContextInsight
import com.android.personalcontext.ace.visualizer.compat.InsightGridCompat
import com.android.personalcontext.ace.visualizer.compat.VisualMetadataCompat
import com.android.personalcontext.ace.visualizer.templates.VisualizerTemplate
import com.android.personalcontext.ace.visualizer.templates.call.compose.CallWidgetContainer
import com.android.personalcontext.ace.visualizer.templates.call.compose.FullScreenCallWidget
import com.android.personalcontext.ace.visualizer.templates.call.data.CallInsightConverter
import com.android.personalcontext.ace.visualizer.templates.call.data.CallVisualizerWidget
import javax.inject.Inject

/** A [VisualizerTemplate] that renders the Magic Cue Call UI. */
class CallVisualizerTemplate
@Inject
internal constructor(
  private val callInsightConverter: CallInsightConverter,
  private val insightGridCompat: InsightGridCompat,
  private val visualMetadataCompat: VisualMetadataCompat,
) : VisualizerTemplate {

  override fun handleInsight(
    publishedInsight: IPublishedContextInsight
  ): (@Composable () -> Unit)? {
    Log.i(TAG, "[CallEmbedded] handleInsight init")
    val insight = publishedInsight.insight
    if (insight.findContextHint<CallHint>() == null) {
      Log.v(TAG, "[CallEmbedded] No CallHint found")
      return null
    }
    Log.i(TAG, "[CallEmbedded] CallHint found, converting to widget")
    val widget: CallVisualizerWidget = callInsightConverter.convert(insight)

    val isFullScreen = with(visualMetadataCompat) { insight.isVariant() }

    Log.i(TAG, "[CallEmbedded] Returning CallTemplate")
    return {
      CallTemplate(
        widget = widget,
        insightGridCompat = insightGridCompat,
        isFullScreen = isFullScreen,
      )
    }
  }

  companion object {
    private const val TAG = "CallVisualizerTemplate"
  }
}

@Composable
private fun CallTemplate(
  widget: CallVisualizerWidget,
  insightGridCompat: InsightGridCompat,
  isFullScreen: Boolean,
) {
  CompositionLocalProvider(LocalInsightGridCompat provides insightGridCompat) {
    CallTheme {
      val backgrounds =
        if (isFullScreen) {
          CallWidgetBackgrounds(
            widgetBackground = MaterialTheme.colorScheme.surfaceContainer,
            cardBackground = MaterialTheme.colorScheme.surfaceContainerHighest,
          )
        } else {
          CallWidgetBackgrounds(
            widgetBackground = MaterialTheme.colorScheme.surfaceContainerHighest,
            cardBackground = Color.Unspecified,
          )
        }

      CompositionLocalProvider(LocalCallWidgetBackgrounds provides backgrounds) {
        if (isFullScreen) {
          FullScreenCallWidget(widget)
        } else {
          CallWidgetContainer(widget)
        }
      }
    }
  }
}

@Composable
private fun CallTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = true,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> darkColorScheme()
      else -> lightColorScheme()
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography()) { content() }
}
