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

package com.android.personalcontext.ace.internal.templates.richcard.calendar

import com.android.personalcontext.ace.internal.templates.richcard.CardType
import com.android.personalcontext.ace.internal.templates.richcard.DeprecatedUiCardContext

/** A sealed class representing the calendar card context. */
@Deprecated(
  level = DeprecationLevel.WARNING,
  message = "Should be removed once the templates is fully refactored.",
)
sealed class DeprecatedUiCalendarCardContext : DeprecatedUiCardContext {

  /** Represents a state with a list of calendar entries. */
  data class Entries(val items: List<CalendarItem>) : DeprecatedUiCalendarCardContext()

  /** Represents an empty state when there are no events on the calendar date. */
  data class NoEvents(val text: String) : DeprecatedUiCalendarCardContext()

  /**
   * A sealed interface representing an entry in the calendar visualizer. It can be either an actual
   * event or a placeholder for a free time slot.
   */
  sealed interface CalendarItem {
    /** Data class representing a calendar event. */
    data class Event(
      /** The primary title of the event. */
      val title: String,
      /** A secondary subtitle for the event (e.g. date and time string). */
      val subtitle: String?,
      /** The internal start time of this event in milliseconds. */
      val startTimeMs: Long,
      /** The internal end time of this event in milliseconds. */
      val endTimeMs: Long,
    ) : CalendarItem

    /** Data class representing a free time slot. */
    data class FreeSlot(
      /** A display string indicating the free time. */
      val text: String
    ) : CalendarItem
  }

  override val cardType: CardType = CardType.CALENDAR
}
