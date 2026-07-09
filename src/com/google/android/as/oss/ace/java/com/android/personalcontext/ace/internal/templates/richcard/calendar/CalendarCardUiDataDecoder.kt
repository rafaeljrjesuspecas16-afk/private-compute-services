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

package com.android.personalcontext.ace.internal.templates.richcard.calendar

import android.service.personalcontext.insight.BundleInsight
import android.service.personalcontext.insight.ContextInsight
import android.service.personalcontext.insight.DisplayInsight
import android.service.personalcontext.insight.InsightCollection
import com.android.personalcontext.ace.internal.templates.richcard.decoder.CardUiDataDecoder
import javax.inject.Inject

/** Converts between [CardUiData<CalendarCardContext>] and [CardInsight]. */
@Suppress("NewApi")
class CalendarCardUiDataDecoder @Inject internal constructor() :
  CardUiDataDecoder<DeprecatedUiCalendarCardContext>() {

  override fun ContextInsight.toCardContext(): DeprecatedUiCalendarCardContext {
    return when (this) {
      is DisplayInsight -> {
        val text = requireNotNull(details.title?.toString()) { "Missing title for NoEvents card" }
        DeprecatedUiCalendarCardContext.NoEvents(text)
      }
      is InsightCollection -> {
        require(insights.isNotEmpty()) { "Calendar content InsightCollection cannot be empty" }
        val items = insights.map { itemInsight ->
          when (itemInsight) {
            is DisplayInsight -> itemInsight.toFreeSlot()
            is BundleInsight -> itemInsight.toEvent()
            else ->
              throw IllegalArgumentException(
                "Unexpected item context insight type in Calendar content collection: ${itemInsight::class.java.name}"
              )
          }
        }
        DeprecatedUiCalendarCardContext.Entries(items)
      }
      else ->
        throw IllegalArgumentException(
          "Unexpected Content ContextInsight type: ${this::class.java.name}"
        )
    }
  }

  private fun DisplayInsight.toFreeSlot(): DeprecatedUiCalendarCardContext.CalendarItem.FreeSlot {
    val title =
      requireNotNull(details?.title?.toString()) {
        "Missing title for FreeSlot in Calendar content"
      }
    require(title.isNotEmpty()) { "Title for FreeSlot cannot be empty" }
    return DeprecatedUiCalendarCardContext.CalendarItem.FreeSlot(title)
  }

  private fun BundleInsight.toEvent(): DeprecatedUiCalendarCardContext.CalendarItem.Event {
    val bundle = dataBundle
    require(bundle.containsKey(KEY_EVENT_TITLE)) { "Missing event title in Insight" }
    require(bundle.containsKey(KEY_EVENT_START_TIME)) { "Missing event start time in Insight" }
    require(bundle.containsKey(KEY_EVENT_END_TIME)) { "Missing event end time in Insight" }

    val title = requireNotNull(bundle.getString(KEY_EVENT_TITLE))
    val subtitle = bundle.getString(KEY_EVENT_SUBTITLE)
    val startTimeMs = bundle.getLong(KEY_EVENT_START_TIME)
    val endTimeMs = bundle.getLong(KEY_EVENT_END_TIME)

    return DeprecatedUiCalendarCardContext.CalendarItem.Event(
      title = title,
      subtitle = subtitle,
      startTimeMs = startTimeMs,
      endTimeMs = endTimeMs,
    )
  }

  companion object {
    private const val KEY_EVENT_TITLE = "event_title"
    private const val KEY_EVENT_SUBTITLE = "event_subtitle"
    private const val KEY_EVENT_START_TIME = "event_start_time"
    private const val KEY_EVENT_END_TIME = "event_end_time"
  }
}
