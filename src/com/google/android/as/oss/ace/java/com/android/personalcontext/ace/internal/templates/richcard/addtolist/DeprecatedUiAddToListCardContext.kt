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

package com.android.personalcontext.ace.internal.templates.richcard.addtolist

import com.android.personalcontext.ace.internal.templates.richcard.CardType
import com.android.personalcontext.ace.internal.templates.richcard.DeprecatedUiCardContext

/** A data class representing the add to list card context. */
@Deprecated(
  level = DeprecationLevel.WARNING,
  message = "Should be removed once the templates is fully refactored.",
)
data class DeprecatedUiAddToListCardContext(
  /** The title of the list to add to. */
  val title: String,
  /** The list of items to add. */
  val items: List<String>,
) : DeprecatedUiCardContext {
  override val cardType: CardType = CardType.ADD_TO_LIST
}
