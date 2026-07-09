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

package com.android.personalcontext.ace.internal.templates.richcard.imagegallery

import android.service.personalcontext.insight.ContextInsight
import android.service.personalcontext.insight.DisplayInsight
import android.service.personalcontext.insight.InsightCollection
import com.android.personalcontext.ace.client.prototype.PrototypeInsightUtils.toPrototypeInsight
import com.android.personalcontext.ace.client.prototype.card.CardInsight
import com.android.personalcontext.ace.internal.templates.richcard.decoder.CardUiDataDecoder
import javax.inject.Inject
import javax.inject.Singleton

/** Converter between [ImageGalleryCardUiData] and [ContextInsight]. */
@Singleton
class ImageGalleryCardUiDataDecoder @Inject constructor() :
  CardUiDataDecoder<ImageGalleryCardUiData>() {

  override fun ContextInsight.toCardContext(): ImageGalleryCardUiData {
    return this.toPrototypeInsight<CardInsight>()?.let { cardInsight ->
      val headerInsights = cardInsight.header as? InsightCollection
      val displayInsights =
        headerInsights?.insights?.filterIsInstance<DisplayInsight>() ?: emptyList()

      val headerStr = displayInsights.getOrNull(0)?.details?.title?.toString() ?: ""

      val subtitleInsight = displayInsights.getOrNull(1)
      val tertiaryTextInsight = displayInsights.getOrNull(2)

      val subtitleStr = subtitleInsight?.details?.title?.toString()
      val subtitleIcon = subtitleInsight?.details?.icon
      val subtitleSuffix = subtitleInsight?.details?.subtitle?.toString()
      val tertiaryText = tertiaryTextInsight?.details?.subtitle?.toString()

      val bodyInsights = cardInsight.body as? InsightCollection
      val imageInsights = bodyInsights?.insights?.filterIsInstance<DisplayInsight>() ?: emptyList()
      val images = imageInsights.mapNotNull { it.details.icon }

      return ImageGalleryCardUiData(
        header = headerStr,
        subtitle = subtitleStr,
        subtitleIcon = subtitleIcon,
        subtitleSuffix = subtitleSuffix,
        tertiaryText = tertiaryText,
        images = images,
      )
    }
      ?: throw IllegalArgumentException(
        "Failed to convert ContextInsight to ImageGalleryCardUiData"
      )
  }
}
