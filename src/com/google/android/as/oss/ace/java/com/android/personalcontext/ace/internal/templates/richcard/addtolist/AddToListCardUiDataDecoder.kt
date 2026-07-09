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

package com.android.personalcontext.ace.internal.templates.richcard.addtolist

import android.service.personalcontext.insight.ContextInsight
import android.service.personalcontext.insight.DisplayInsight
import android.service.personalcontext.insight.InsightCollection
import com.android.personalcontext.ace.internal.templates.richcard.decoder.CardUiDataDecoder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddToListCardUiDataDecoder @Inject constructor() :
  CardUiDataDecoder<DeprecatedUiAddToListCardContext>() {

  @Suppress("NewApi")
  override fun ContextInsight.toCardContext(): DeprecatedUiAddToListCardContext {
    require(this is InsightCollection) { "AddToList card insight must be an InsightCollection" }

    var title: String? = null
    val items = mutableListOf<String>()

    for (insight in insights) {
      if (insight is DisplayInsight) {
        val insightTitle = insight.details.title?.toString()
        if (insightTitle != null) {
          if (title == null) {
            title = insightTitle
          } else {
            items.add(insightTitle)
          }
        }
      }
    }

    return DeprecatedUiAddToListCardContext(title = title ?: "", items = items)
  }
}
