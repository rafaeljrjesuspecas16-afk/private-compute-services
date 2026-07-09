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

package com.android.personalcontext.ace.client.prototype.card

import android.os.Bundle
import android.service.personalcontext.insight.ContextInsight
import com.android.personalcontext.ace.client.prototype.PrototypeInsightCollection
import com.android.personalcontext.ace.client.prototype.PrototypeInsightId.CardInsightId
import com.android.personalcontext.ace.common.LabeledContextInsight
import com.android.personalcontext.ace.common.labeled

data class CardInsight(
  val container: ContextInsight? = null,
  val title: ContextInsight? = null,
  val header: ContextInsight? = null,
  val body: ContextInsight,
  val footer: ContextInsight? = null,
  val actions: ContextInsight? = null,
  val dismiss: ContextInsight? = null,
  /**
   * Explicit identifier required when origin hints alone cannot determine the intended card type
   * output.
   */
  val cardType: String? = null,
) : PrototypeInsightCollection(CardInsightId, this) {

  override fun exportDataToBundle(bundle: Bundle) {
    bundle.putString(KEY_CARD_TYPE, cardType)
  }

  override fun exportInsightsToList(): List<LabeledContextInsight> =
    listOf(
      title labeled "title",
      header labeled "header",
      body labeled "body",
      footer labeled "footer",
      actions labeled "actions",
      container labeled "container",
      dismiss labeled "dismiss",
    )

  companion object : PrototypeInsightCollectionCreator() {
    private const val KEY_CARD_TYPE = "cardType"

    override fun create(bundle: Bundle, insights: List<ContextInsight?>) =
      CardInsight(
        container = insights.getOrNull(5),
        title = insights.getOrNull(0),
        header = insights.getOrNull(1),
        body = requireNotNull(insights.getOrNull(2)) { "Body is required" },
        footer = insights.getOrNull(3),
        actions = insights.getOrNull(4),
        dismiss = insights.getOrNull(6),
        cardType = bundle.getString(KEY_CARD_TYPE),
      )
  }
}
