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

package com.android.personalcontext.ace.internal.templates.richcard.locationpreview

import android.graphics.Bitmap
import android.graphics.drawable.Icon
import com.android.personalcontext.ace.internal.templates.richcard.CardType
import com.android.personalcontext.ace.internal.templates.richcard.DeprecatedUiCardContext

/** Card context for a Location preview. */
@Deprecated("Domain specific cards are no longer supported.")
data class DeprecatedUiLocationPreviewCardContext(
  /** Required Fields for server retrieval */
  /** The query text used to find the location. */
  val locationQueryText: String,

  /** Optional Fields */
  /** A list of images to display in the photo gallery. */
  val galleryImages: List<GalleryImage>? = null,

  /** The name of the location (e.g., "Ippudo Ramen"). */
  val locationName: String? = null,

  /** The rating information. */
  val rating: Rating? = null,

  /** The category or type of the location (e.g., "Restaurant"). */
  val category: String? = null,

  /** The address of the location. */
  val locationAddress: String? = null,
) : DeprecatedUiCardContext {

  /** Semantically groups the rating text and its corresponding visual icon. */
  data class Rating(
    /** The display text for the rating score (e.g., "4.0"). */
    val score: String,

    /**
     * The native Android Icon representing the rating (e.g., a gold star). Handled by the data
     * layer to dictate color and shape.
     */
    val icon: Icon,
  )

  /** Pairs a bitmap with its content description for accessibility. */
  data class GalleryImage(val bitmap: Bitmap, val contentDescription: String)

  override val cardType: CardType = CardType.LOCATION_PREVIEW
}
