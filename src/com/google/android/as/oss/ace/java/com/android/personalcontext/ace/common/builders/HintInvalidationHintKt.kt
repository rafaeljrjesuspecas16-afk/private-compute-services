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

package com.android.personalcontext.ace.common.builders

import android.service.personalcontext.Token
import android.service.personalcontext.hint.ContextHint
import android.service.personalcontext.hint.HintInvalidationHint
import java.util.UUID

/** Creates a [HintInvalidationHint] using an idiomatic Kotlin DSL. */
inline fun hintInvalidationHint(
  invalidatedHint: ContextHint,
  block: HintInvalidationHintKt.Dsl.() -> Unit = {},
): HintInvalidationHint =
  HintInvalidationHintKt.Dsl(HintInvalidationHint.Builder(invalidatedHint)).apply(block).build()

/** Creates a [HintInvalidationHint] using an idiomatic Kotlin DSL. */
inline fun hintInvalidationHint(
  invalidatedHintId: UUID,
  block: HintInvalidationHintKt.Dsl.() -> Unit = {},
): HintInvalidationHint =
  HintInvalidationHintKt.Dsl(HintInvalidationHint.Builder(invalidatedHintId)).apply(block).build()

object HintInvalidationHintKt {

  @DslMarker annotation class HintInvalidationHintDslMarker

  @HintInvalidationHintDslMarker
  class Dsl(@PublishedApi internal val builder: HintInvalidationHint.Builder) {

    /** Allows adding tokens using the `+=` operator. */
    val tokens = DslCollection<Token> { builder.addToken(it) }

    @PublishedApi internal fun build(): HintInvalidationHint = builder.build()
  }
}
