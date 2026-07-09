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

package com.google.android.`as`.oss.feedback.domain

/** Interface for feedback data to be displayed in the view feedback data dialog. */
interface ViewFeedbackData {
  val viewFeedbackHeader: String?
  val viewFeedbackBody: String
  val dataCollectionCategoriesLegacy: Map<DataCollectionCategory, DataCollectionCategoryDataLegacy>
  val dataCollectionCategories: Map<DataCollectionCategory, DataCollectionCategoryData>
  val dataCollectionCategoryExpandContentDescription: String
  val dataCollectionCategoryCollapseContentDescription: String
}

/** Categories of data collection that the user can choose to independently enable or disable. */
enum class DataCollectionCategory {
  /** Legacy V1 ui will use this in lieu of any actual categories. */
  LegacyV1,
  NotificationContent,
  TriggeringMessages,
  IntentQueries,
  ModelOutputs,
  QuartzModelOutputs,
  MemoryEntities,
  SelectedEntityContent,
  AppInfo,
  FailureReason,
}

data class DataCollectionCategoryDataLegacy(val header: String, val body: String)

/**
 * Data collection category data with multiple entries that will be displayed in the view feedback
 * data dialog.
 *
 * @param header The header of the data collection category.
 * @param items A list of [FeedbackBodyItem] to be displayed.
 */
data class DataCollectionCategoryData(
  val header: String,
  val items: List<FeedbackBodyItem> = emptyList(),
)

/** Sealed interface for different types of items in the feedback body. */
sealed interface FeedbackBodyItem {
  val id: String // Unique ID for state management

  /** Represents a simple, non-interactive block of text. */
  data class SimpleText(override val id: String, val text: String) : FeedbackBodyItem

  /**
   * Represents an item with a title, a checkbox, and potentially expandable child items. The
   * checkbox for this item controls the inclusion of its entire content, including children, in the
   * feedback report.
   */
  data class CheckableListItem(
    override val id: String,
    val title: String,
    val defaultChecked: Boolean = false,
    val children: List<FeedbackBodyItem> = emptyList(),
  ) : FeedbackBodyItem

  /**
   * Represents a block of text content within a CheckableListItem. This item does not have its own
   * checkbox; its inclusion is governed by the parent CheckableListItem.
   */
  data class ChildText(override val id: String, val text: String) : FeedbackBodyItem
}
