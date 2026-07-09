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

package com.android.personalcontext.ace.internal.templates.richcard.renderer

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.android.personalcontext.ace.internal.templates.richcard.Attribution
import com.android.personalcontext.ace.internal.templates.richcard.CardType
import com.android.personalcontext.ace.internal.templates.richcard.CardUiData
import com.android.personalcontext.ace.internal.templates.richcard.DeprecatedUiCardContext
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class CardRendererManager
@Inject
constructor(
  private val renderers:
    Map<CardType, @JvmSuppressWildcards Provider<CardRenderer<out DeprecatedUiCardContext>>>
) {
  @Suppress("UNCHECKED_CAST")
  @Composable
  fun Render(cardUiData: CardUiData<DeprecatedUiCardContext>, modifier: Modifier = Modifier) {
    val cardContext =
      cardUiData.cardContext
        ?: throw IllegalArgumentException("cardUiData.cardContext cannot be null for rendering.")
    val cardType = cardContext.cardType
    val renderer =
      renderers[cardType]?.get()
        ?: throw IllegalArgumentException("No renderer found for card type: $cardType")

    val attribution = cardUiData.attribution
    if (attribution?.isValid != true) {
      throw IllegalArgumentException("Attribution is not valid for card type: $cardType")
    }

    (renderer as CardRenderer<DeprecatedUiCardContext>).Render(cardUiData, modifier)
  }

  private val Attribution.isValid: Boolean
    get() = sourceAppIcons.isNotEmpty() && title.isNotEmpty()
}
