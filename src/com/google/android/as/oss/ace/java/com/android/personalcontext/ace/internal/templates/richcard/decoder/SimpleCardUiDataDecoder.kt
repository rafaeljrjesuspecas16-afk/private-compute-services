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

package com.android.personalcontext.ace.internal.templates.richcard.decoder

import android.service.personalcontext.insight.ContextInsight
import android.service.personalcontext.insight.DisplayInsight
import android.service.personalcontext.insight.InsightCollection
import com.android.personalcontext.ace.internal.templates.richcard.DeprecatedUiSimpleCardContext
import javax.inject.Inject
import javax.inject.Singleton

/** Converter between [DeprecatedUiSimpleCardContext] and [CardInsight]. */
@Singleton
class SimpleCardUiDataDecoder @Inject constructor() :
  CardUiDataDecoder<DeprecatedUiSimpleCardContext>() {

  /**
   * Converts [ContextInsight] back to [DeprecatedUiSimpleCardContext].
   *
   * @param contextInsight The [ContextInsight] to convert, usually from [CardInsight.body].
   * @return The converted [DeprecatedUiSimpleCardContext], or null if conversion fails or input is
   *   null.
   */
  @Suppress("NewApi")
  override fun ContextInsight.toCardContext(): DeprecatedUiSimpleCardContext {
    require(this is InsightCollection) { "Input is not InsightCollection" }

    val displayInsights = this.insights.filterIsInstance<DisplayInsight>()
    val title =
      displayInsights.firstOrNull { !it.details.title.isNullOrEmpty() }?.details?.title?.toString()
        ?: ""

    val texts = displayInsights.mapNotNull { it.details.subtitle?.toString() }

    return DeprecatedUiSimpleCardContext(title = title, texts = texts)
  }
}
