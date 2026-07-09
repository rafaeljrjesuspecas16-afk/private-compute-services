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

package com.android.personalcontext.ace.internal.templates.richcard

import android.service.personalcontext.hint.PublishedContextHint
import android.service.personalcontext.insight.DisplayInsight
import com.android.personalcontext.ace.common.InsightGridItem
import com.android.personalcontext.ace.common.InsightGridItem.Span
import com.android.personalcontext.ace.common.builders.displayInsight
import com.android.personalcontext.ace.common.builders.insightDisplayDetails

/**
 * Card Context for grid representation in a rich card.
 *
 * This context is used to display a list of [entities][DeprecatedGridEntity], which will be
 * structured in a grid format.
 */
data class DeprecatedUiGridCardContext(val entities: List<DeprecatedGridEntity>) :
  DeprecatedUiCardContext {
  override val cardType: CardType = CardType.GRID
}

/**
 * Represents a single entity within a [DeprecatedUiGridCardContext].
 *
 * @param title The title of the entity.
 * @param content The content or description of the entity.
 * @param span The number of columns this item should span in the grid. This should align with
 *   [com.android.personalcontext.ace.common.InsightGridItem].
 */
data class DeprecatedGridEntity(val title: String, val content: String, val span: Span)

/** Converts [DeprecatedGridEntity] to [InsightGridItem]. */
@Suppress("NewApi")
fun DeprecatedGridEntity.toInsightGridItem(
  originHints: Set<PublishedContextHint>
): InsightGridItem {
  return InsightGridItem(
    insight =
      displayInsight(
        insightDisplayDetails(title) { subtitle = content },
        originHints = originHints,
      ),
    span = span,
  )
}

/** Converts [InsightGridItem] to [DeprecatedGridEntity]. */
@Suppress("NewApi")
fun InsightGridItem.toGridEntity(): DeprecatedGridEntity? {
  val insight = insight as? DisplayInsight
  val insightTitle = insight?.details?.title?.toString() ?: return null
  val insightContent = insight.details.subtitle?.toString() ?: return null
  return DeprecatedGridEntity(title = insightTitle, content = insightContent, span = span)
}
