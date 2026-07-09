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

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.android.personalcontext.ace.internal.templates.richcard.Attribution

/** A composable that renders the app context block for a visualizer card. */
@Composable
fun CardAppContextBlock(
  attribution: Attribution,
  modifier: Modifier = Modifier,
  content: @Composable ColumnScope.() -> Unit,
) {
  val appName = attribution.title
  val appIcon = attribution.sourceAppIcons.firstOrNull()

  Column(
    modifier = modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)),
    verticalArrangement = Arrangement.spacedBy(2.dp),
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier =
        Modifier.fillMaxWidth()
          .height(50.dp)
          .background(MaterialTheme.colorScheme.surfaceBright, RoundedCornerShape(4.dp))
          .padding(horizontal = 10.dp, vertical = 8.dp),
    ) {
      if (appIcon != null) {
        val context = LocalContext.current
        val imageBitmap =
          remember(appIcon) { appIcon.loadDrawable(context)?.toBitmap()?.asImageBitmap() }
        if (imageBitmap != null) {
          Image(bitmap = imageBitmap, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(8.dp))
        }
      }
      if (!appName.isEmpty()) {
        Text(
          text = appName,
          color = MaterialTheme.colorScheme.onSurface,
          fontSize = 14.sp,
          lineHeight = 16.sp,
          fontWeight = FontWeight.Medium,
        )
      }
    }

    // App Content
    Column(modifier = Modifier.fillMaxWidth()) { content() }
  }
}
