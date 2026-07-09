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

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.android.personalcontext.ace.internal.templates.richcard.CardUiData
import com.android.personalcontext.ace.internal.templates.richcard.common.CardTemplateLayout
import com.android.personalcontext.ace.internal.templates.richcard.common.LoadingBox
import com.android.personalcontext.ace.internal.templates.richcard.renderer.CardRenderer
import javax.inject.Inject

class ImageGalleryCardRenderer @Inject internal constructor() :
  CardRenderer<ImageGalleryCardUiData> {

  @Composable
  override fun Render(cardUiData: CardUiData<ImageGalleryCardUiData>, modifier: Modifier) {
    val uiContext = cardUiData.cardContext ?: return
    val attribution = cardUiData.attribution ?: return

    CardTemplateLayout(cardUiData = cardUiData, modifier = modifier) {
      Box(
        modifier =
          Modifier.fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceBright, RoundedCornerShape(12.dp))
            .padding(bottom = 12.dp)
      ) {
        CollageContent(uiContext = uiContext)
      }
    }
  }

  @Composable
  private fun CollageContent(uiContext: ImageGalleryCardUiData, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Column(modifier = modifier.fillMaxWidth()) {
      val subtitleIconBitmap =
        remember(uiContext.subtitleIcon) {
          uiContext.subtitleIcon?.loadDrawable(context)?.toBitmap()
        }
      val bitmaps =
        remember(uiContext.images) {
          uiContext.images?.mapNotNull { it.loadDrawable(context)?.toBitmap() }
        }
      InfoText(
        header = uiContext.header,
        subtitle = uiContext.subtitle,
        subtitleIcon = subtitleIconBitmap,
        subtitleSuffix = uiContext.subtitleSuffix,
        tertiaryText = uiContext.tertiaryText,
      )

      ImageCollage(images = bitmaps)
    }
  }

  @Composable
  private fun InfoText(
    header: String,
    subtitle: String?,
    subtitleIcon: Bitmap?,
    subtitleSuffix: String?,
    tertiaryText: String?,
    modifier: Modifier = Modifier,
  ) {
    Column(modifier = modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 12.dp)) {
      Text(
        text = header,
        color = MaterialTheme.colorScheme.onSurface,
        fontSize = 18.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.sp,
      )
      if (subtitle != null) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          SubduedText(subtitle)
          SubtitleIcon(subtitleIcon)
          if (subtitleSuffix != null) {
            SubduedText(subtitleSuffix)
          }
        }
      } else {
        LoadingBox(modifier = Modifier.fillMaxWidth(0.7f).height(20.dp).padding(bottom = 4.dp))
      }
      if (tertiaryText != null) {
        SubduedText(tertiaryText)
      } else {
        LoadingBox(modifier = Modifier.fillMaxWidth(0.5f).height(20.dp).padding(bottom = 4.dp))
      }
    }
  }

  @Composable
  private fun SubduedText(text: String, modifier: Modifier = Modifier) {
    Text(
      text = text,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      fontSize = 14.sp,
      fontWeight = FontWeight.W400,
      lineHeight = 20.sp,
      letterSpacing = 0.1.sp,
      modifier = modifier,
    )
  }
}

@Composable
private fun SubtitleIcon(iconBitmap: Bitmap?) {
  if (iconBitmap != null) {
    Image(
      bitmap = iconBitmap.asImageBitmap(),
      contentDescription = null,
      modifier = Modifier.size(16.dp).padding(end = 8.dp),
    )
  }
}

@Composable
private fun ImageCollage(images: List<Bitmap>?, modifier: Modifier = Modifier) {
  val displayImages =
    if (images.isNullOrEmpty()) {
      listOf(null, null, null)
    } else {
      images.take(3)
    }

  Box(
    modifier =
      modifier
        .fillMaxWidth()
        .height(200.dp)
        .padding(start = 16.dp, end = 16.dp, top = 16.dp)
        .clip(RoundedCornerShape(12.dp))
  ) {
    when (displayImages.size) {
      1 -> GalleryImage(bitmap = displayImages[0])
      2 ->
        Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          GalleryImage(bitmap = displayImages[0], modifier = Modifier.weight(1f))
          GalleryImage(bitmap = displayImages[1], modifier = Modifier.weight(1f))
        }
      3 ->
        Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          GalleryImage(bitmap = displayImages[0], modifier = Modifier.weight(1f))
          Column(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
          ) {
            GalleryImage(bitmap = displayImages[1], modifier = Modifier.weight(1f))
            GalleryImage(bitmap = displayImages[2], modifier = Modifier.weight(1f))
          }
        }
      4 ->
        Column(
          modifier = Modifier.fillMaxSize(),
          verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
          Row(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
          ) {
            GalleryImage(bitmap = displayImages[0], modifier = Modifier.weight(1f))
            GalleryImage(bitmap = displayImages[1], modifier = Modifier.weight(1f))
          }
          Row(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
          ) {
            GalleryImage(bitmap = displayImages[2], modifier = Modifier.weight(1f))
            GalleryImage(bitmap = displayImages[3], modifier = Modifier.weight(1f))
          }
        }
    }
  }
}

@Composable
private fun GalleryImage(bitmap: Bitmap?, modifier: Modifier = Modifier) {
  val imageModifier = modifier.fillMaxSize().clip(RoundedCornerShape(4.dp))
  if (bitmap != null) {
    Image(
      bitmap = bitmap.asImageBitmap(),
      contentDescription = null,
      modifier = imageModifier,
      contentScale = ContentScale.Crop,
    )
  } else {
    LoadingBox(modifier = imageModifier)
  }
}
