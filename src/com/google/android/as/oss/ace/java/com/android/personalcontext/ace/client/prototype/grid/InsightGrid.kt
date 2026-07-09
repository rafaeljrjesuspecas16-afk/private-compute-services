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

package com.android.personalcontext.ace.client.prototype.grid

import android.os.Bundle
import android.service.personalcontext.insight.ContextInsight
import com.android.personalcontext.ace.client.prototype.PrototypeInsightCollection
import com.android.personalcontext.ace.client.prototype.PrototypeInsightId.InsightGridId
import com.android.personalcontext.ace.common.InsightGridItem
import com.android.personalcontext.ace.common.LabeledContextInsight
import com.android.personalcontext.ace.common.labeled

/**
 * Represents a grid of insights.
 *
 * @property items The list of [InsightGridItem]s to display in the grid.
 */
data class InsightGrid(val items: List<InsightGridItem>) :
  PrototypeInsightCollection(InsightGridId, this) {

  init {
    require(items.isNotEmpty()) { "InsightGrid items cannot be empty." }
  }

  override fun exportDataToBundle(bundle: Bundle) {
    bundle.putIntArray("spans", items.map { it.span.value }.toIntArray())
  }

  override fun exportInsightsToList(): List<LabeledContextInsight> = items.map {
    it.insight labeled it.span.name
  }

  companion object : PrototypeInsightCollectionCreator() {
    /** The typical total span capacity for a phone device. */
    const val TOTAL_SPAN_CAPACITY_PHONE = 6
    /** The typical total span capacity for a watch device. */
    const val TOTAL_SPAN_CAPACITY_WATCH = 2

    override fun create(
      bundle: Bundle,
      insights: List<ContextInsight?>,
    ): PrototypeInsightCollection {
      val spans = bundle.getIntArray("spans")!!.toList()

      return InsightGrid(
        items =
          insights.zip(spans).map { (insight, span) ->
            InsightGridItem(insight!!, InsightGridItem.Span.deserializeFromInt(span)!!)
          }
      )
    }
  }
}
