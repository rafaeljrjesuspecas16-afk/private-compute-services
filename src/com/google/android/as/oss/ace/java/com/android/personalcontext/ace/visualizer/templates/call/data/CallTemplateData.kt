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

package com.android.personalcontext.ace.visualizer.templates.call.data

import android.app.RemoteAction
import android.graphics.drawable.Icon
import android.service.personalcontext.insight.ActionableInsight
import android.service.personalcontext.insight.ContextInsight
import android.service.personalcontext.insight.DisplayInsight
import com.android.personalcontext.ace.common.InsightGridItem

/**
 * Data class for the Call Visualizer Widget.
 *
 * @property detailedCards A list of detailed cards.
 * @property generalCards A 2-D list of general cards from various sources. Each inner list
 *   represents a different source.
 * @property ctaDisplayMoreResults The CTA to display more results.
 * @property aiDisclaimer The AI disclaimer.
 */
data class CallVisualizerWidget(
  val detailedCards: List<CallVisualizerDetailedCard>,
  val generalCards: List<List<CallVisualizerGeneralCard>>,
  val ctaDisplayMoreResults: CtaDisplayMoreResults?,
  val aiDisclaimer: DisplayInsight?,
)

/**
 * Data class for the Call Visualizer General Card.
 *
 * @property title The title of the card.
 * @property date The date of the card.
 * @property detailedText The detailed text of the card.
 * @property dataSource The data source of the card.
 * @property cardExpandButtonAccessibilityContentDescription The accessibility content description
 *   of the card expand button.
 * @property listUuid A random UUID so Jetpack Compose can keep track of the cards in a list.
 *   Created by and used by the frontend only. Not to be used for any other purposes.
 */
data class CallVisualizerGeneralCard(
  val title: String,
  val date: String,
  val detailedText: String? = null,
  val detailedTextIcon: Icon? = null,
  @Deprecated("Use originalDataSource instead.") val dataSource: RemoteAction? = null,
  val originalDataSource: DataSource? = null,
  val cardExpandButtonAccessibilityContentDescription: String,
  val listUuid: String,
  val originalInsight: ContextInsight,
)

/**
 * Data class for the Call Visualizer Detailed Card.
 *
 * @property title The title of the card.
 * @property items The dynamic data in the card. Each item represents **one** piece of AI-processed
 *   data
 * @property feedback The feedback for the card.
 * @property deeplink The source of the data in the card
 * @property listUuid A random UUID so Jetpack Compose can keep track of the cards in a list.
 *   Created by and used by the frontend only. Not to be used for any other purposes.
 */
data class CallVisualizerDetailedCard(
  val title: DisplayInsight,
  val items: List<InsightGridItem>,
  val feedback: DisplayInsight? = null,
  @Deprecated("Use originalDataSource instead.") val deeplink: ActionableInsight? = null,
  val originalDataSource: DataSource? = null,
  val originalInsight: ContextInsight,
  val listUuid: String,
)

/**
 * Data class for the Call Visualizer CTA display more results.
 *
 * @property title The title of the CTA.
 * @property contentDescription The content description of the CTA.
 * @property icon The icon of the CTA.
 * @property insight The insight of the CTA.
 */
data class CtaDisplayMoreResults(
  val title: String,
  val contentDescription: String?,
  val icon: Icon?,
  val originalInsight: ContextInsight,
)

data class DataSource(
  val title: String,
  val appName: String,
  val appIcon: Icon,
  val remoteAction: RemoteAction,
  val originalInsight: ContextInsight,
)
