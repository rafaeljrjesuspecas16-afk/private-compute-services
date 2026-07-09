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

package com.android.personalcontext.ace.internal.templates.richcard.locationpreview

import android.graphics.Bitmap
import android.graphics.drawable.Icon
import android.os.Bundle
import android.service.personalcontext.insight.BundleInsight
import android.service.personalcontext.insight.ContextInsight
import androidx.core.os.BundleCompat
import com.android.personalcontext.ace.internal.templates.richcard.decoder.CardUiDataDecoder
import javax.inject.Inject
import javax.inject.Singleton

/** Converts between [DeprecatedUiLocationPreviewCardContext] and [ContextInsight]. */
@Singleton
@Suppress("NewApi")
class LocationPreviewCardUiDataDecoder @Inject constructor() :
  CardUiDataDecoder<DeprecatedUiLocationPreviewCardContext>() {

  override fun ContextInsight.toCardContext(): DeprecatedUiLocationPreviewCardContext {
    require(this is BundleInsight) { "Expected BundleInsight for LocationPreviewCardContext" }
    val bundle = this.dataBundle
    val locationQueryText =
      requireNotNull(bundle.getString(KEY_LOCATION_QUERY_TEXT)) { "Missing locationQueryText" }
    val galleryImageBundles =
      BundleCompat.getParcelableArrayList(bundle, KEY_GALLERY_IMAGES, Bundle::class.java)
    val galleryImages = galleryImageBundles?.map { it.toGalleryImage() }
    val locationName = bundle.getString(KEY_LOCATION_NAME)
    val ratingBundle = bundle.getBundle(KEY_RATING)
    val rating = ratingBundle?.toRating()
    val category = bundle.getString(KEY_CATEGORY)
    val locationAddress = bundle.getString(KEY_LOCATION_ADDRESS)

    return DeprecatedUiLocationPreviewCardContext(
      locationQueryText = locationQueryText,
      galleryImages = galleryImages,
      locationName = locationName,
      rating = rating,
      category = category,
      locationAddress = locationAddress,
    )
  }

  private fun Bundle.toRating(): DeprecatedUiLocationPreviewCardContext.Rating {
    val score = requireNotNull(getString(KEY_RATING_SCORE)) { "Missing rating score" }
    val icon =
      requireNotNull(getParcelable(KEY_RATING_ICON, Icon::class.java)) { "Missing rating icon" }

    return DeprecatedUiLocationPreviewCardContext.Rating(score = score, icon = icon)
  }

  private fun Bundle.toGalleryImage(): DeprecatedUiLocationPreviewCardContext.GalleryImage {
    val bitmap =
      requireNotNull(getParcelable(KEY_GALLERY_IMAGE_BITMAP, Bitmap::class.java)) {
        "Missing gallery image bitmap"
      }
    val contentDescription =
      requireNotNull(getString(KEY_GALLERY_IMAGE_CONTENT_DESCRIPTION)) {
        "Missing gallery image content description"
      }
    return DeprecatedUiLocationPreviewCardContext.GalleryImage(
      bitmap = bitmap,
      contentDescription = contentDescription,
    )
  }

  internal companion object {
    const val KEY_LOCATION_QUERY_TEXT = "location_query_text"
    const val KEY_GALLERY_IMAGES = "gallery_images"
    const val KEY_GALLERY_IMAGE_BITMAP = "bitmap"
    const val KEY_GALLERY_IMAGE_CONTENT_DESCRIPTION = "content_description"
    const val KEY_LOCATION_NAME = "location_name"
    const val KEY_RATING = "rating"
    const val KEY_CATEGORY = "category"
    const val KEY_LOCATION_ADDRESS = "location_address"

    const val KEY_RATING_SCORE = "score"
    const val KEY_RATING_ICON = "icon"
  }
}
