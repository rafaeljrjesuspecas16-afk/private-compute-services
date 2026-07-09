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

package com.android.personalcontext.ace.internal.templates.richcard.decoder

import com.android.personalcontext.ace.client.prototype.card.CardInsight
import com.android.personalcontext.ace.internal.templates.richcard.CardType
import com.android.personalcontext.ace.internal.templates.richcard.CardUiData
import com.android.personalcontext.ace.internal.templates.richcard.DeprecatedUiCardContext
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

/** Manages conversion between [CardUiData] and [CardInsight] using Dagger-injected converters. */
@Singleton
class CardDecoderManager
@Inject
constructor(
  private val decoders:
    Map<CardType, @JvmSuppressWildcards Provider<CardUiDataDecoder<out DeprecatedUiCardContext>>>
) {

  /**
   * Converts a [CardInsight] to a [CardUiData] by inferring type from [cardType] or
   * [CardInsight.cardType] (will be deprecated soon).
   *
   * @param cardInsight The card insight instance to convert.
   * @param cardType The optional card type to explicitly resolve to. If null, the type will be
   *   inferred.
   * @return The resulting [CardUiData].
   * @throws IllegalArgumentException if the inferred type is null, invalid, or if no converter is
   *   registered for the inferred card type.
   */
  fun fromInsight(
    cardInsight: CardInsight,
    cardType: CardType? = null,
  ): CardUiData<DeprecatedUiCardContext> {
    val resolvedCardType =
      cardType
        ?: run {
          val cardTypeString =
            cardInsight.cardType
              ?: throw IllegalArgumentException("cardInsight.cardType must not be null")
          try {
            CardType.valueOf(cardTypeString)
          } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Unknown card type: $cardTypeString", e)
          }
        }
    val decoder =
      decoders[resolvedCardType]?.get()
        ?: throw IllegalArgumentException("No converter found for card type: $resolvedCardType")
    return with(decoder) { cardInsight.toCardUiData() }
  }
}
