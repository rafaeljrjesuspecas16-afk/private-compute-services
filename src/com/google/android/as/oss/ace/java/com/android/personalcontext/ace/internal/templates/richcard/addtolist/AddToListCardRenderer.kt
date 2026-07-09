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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.personalcontext.ace.internal.templates.richcard.Attribution
import com.android.personalcontext.ace.internal.templates.richcard.CardUiData
import com.android.personalcontext.ace.internal.templates.richcard.common.CardAppContextBlock
import com.android.personalcontext.ace.internal.templates.richcard.common.CardTemplateLayout
import com.android.personalcontext.ace.internal.templates.richcard.renderer.CardRenderer
import javax.inject.Inject

/** [CardRenderer] for AddToList cards. */
class AddToListCardRenderer @Inject internal constructor() :
  CardRenderer<DeprecatedUiAddToListCardContext> {
  @Composable
  override fun Render(
    cardUiData: CardUiData<DeprecatedUiAddToListCardContext>,
    modifier: Modifier,
  ) {
    CardTemplateLayout(cardUiData = cardUiData, modifier = modifier) {
      val cardContext = cardUiData.cardContext
      val attribution = cardUiData.attribution
      if (attribution?.isValid == true && cardContext != null) {
        CardAppContextBlock(attribution) {
          Column(
            modifier =
              Modifier.fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceBright, RoundedCornerShape(4.dp))
          ) {
            // Header
            Row(
              modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Text(
                text = cardContext.title,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
              )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)

            // List items
            Column(
              modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
              for (item in cardContext.items) {
                Text(
                  text = item,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  fontSize = 16.sp,
                  modifier = Modifier.padding(vertical = 6.dp),
                )
              }
            }
          }
        }
      }
    }
  }
}

private val Attribution.isValid: Boolean
  get() = sourceAppIcons.isNotEmpty() && title.isNotEmpty()
