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

package com.android.personalcontext.ace.internal.templates.richcard.common

import android.graphics.drawable.Icon
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.android.personalcontext.ace.internal.flexfont.FlexFontUtils.withFlexFont
import com.android.personalcontext.ace.internal.templates.richcard.CardTitle

/** A composable that renders the card title. */
@Suppress("NewApi", "FlaggedApi")
@Composable
fun CardTitle(icon: Icon?, cardTitle: CardTitle?, modifier: Modifier = Modifier) {
  if (cardTitle == null) return
  Row(
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalAlignment = Alignment.Top,
    modifier = modifier,
  ) {
    val context = LocalContext.current
    val imageBitmap = remember(icon) { icon?.loadDrawable(context)?.toBitmap()?.asImageBitmap() }
    if (imageBitmap != null) {
      Image(
        bitmap = imageBitmap,
        contentDescription = null,
        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
        modifier = Modifier.size(20.dp),
      )
    }

    when (cardTitle) {
      is CardTitle.Loading ->
        LoadingBox(
          modifier = Modifier.fillMaxWidth().height(18.dp),
          shape = RoundedCornerShape(50.dp),
        )
      is CardTitle.Present ->
        Text(
          text = cardTitle.text,
          color = MaterialTheme.colorScheme.onSurface,
          fontSize = 16.sp,
          lineHeight = 24.sp,
          letterSpacing = 0.sp,
          style = MaterialTheme.typography.titleMedium.withFlexFont(weight = 600, round = 100f),
        )
    }
  }
}
