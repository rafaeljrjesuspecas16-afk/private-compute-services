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

package com.android.personalcontext.ace.internal.templates.richcard.contact

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

/** Renders a [DeprecatedUiContactCardContext] as a Composable. */
class ContactCardRenderer @Inject internal constructor() :
  CardRenderer<DeprecatedUiContactCardContext> {
  @Composable
  override fun Render(cardUiData: CardUiData<DeprecatedUiContactCardContext>, modifier: Modifier) {
    CardTemplateLayout(cardUiData = cardUiData, modifier = modifier) {
      val attribution = cardUiData.attribution
      val cardContext = cardUiData.cardContext
      if (attribution?.isValid == true && cardContext != null) {
        CardAppContextBlock(attribution) {
          Column(
            modifier =
              Modifier.fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceBright, RoundedCornerShape(4.dp))
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
          ) {
            // Name Title
            Text(
              text = cardContext.name,
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp,
              lineHeight = 22.sp,
              color = MaterialTheme.colorScheme.onSurface,
            )
            // Phone Number Details
            cardContext.phoneNumber?.let { phone ->
              Text(
                text = phone,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 22.sp,
                color = MaterialTheme.colorScheme.onSurface,
              )
            }
            // Email Details
            cardContext.emailAddress?.let { email ->
              Text(
                text = email,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 22.sp,
                color = MaterialTheme.colorScheme.onSurface,
              )
            }
            // Address Details
            cardContext.address?.let { addr ->
              Text(
                text = addr,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 22.sp,
                color = MaterialTheme.colorScheme.onSurface,
              )
            }
            // Birthday Details
            cardContext.birthday?.let { bday ->
              Text(
                text = bday,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 22.sp,
                color = MaterialTheme.colorScheme.onSurface,
              )
            }
          }
        }
      }
    }
  }
}

private val Attribution.isValid: Boolean
  get() = sourceAppIcons.isNotEmpty() && title.isNotEmpty()
