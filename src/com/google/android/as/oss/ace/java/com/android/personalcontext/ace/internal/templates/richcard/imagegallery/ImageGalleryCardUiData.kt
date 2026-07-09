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

import android.graphics.drawable.Icon
import com.android.personalcontext.ace.internal.templates.richcard.CardType
import com.android.personalcontext.ace.internal.templates.richcard.DeprecatedUiCardContext

/**
 * Represents the structured UI data for an Image Gallery card, containing already-loaded
 * representations appropriate for the UI.
 *
 * @property header The header text (non-null, also used for live data query).
 * @property subtitle Optional subtitle text. Passing in null will display a loading box instead. If
 *   no loading box is needed, pass in an empty string.
 * @property subtitleIcon Optional subtitle icon. Passing in null will display a loading box
 *   instead. If no loading box is needed, pass in an empty string.
 * @property tertiaryText Optional tertiary text.
 * @property images A list of image Bitmaps to display in the collage (up to 4). Can be null
 *   initially.
 * @property cardType The card type identifier.
 */
data class ImageGalleryCardUiData(
  val header: String,
  val subtitle: String? = null,
  val subtitleIcon: Icon? = null,
  val tertiaryText: String? = null,
  val images: List<Icon>? = null,
  val subtitleSuffix: String? = null,
) : DeprecatedUiCardContext {
  override val cardType: CardType = CardType.RICH_CARD_IMAGE_GALLERY
}
