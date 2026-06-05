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

import android.os.Bundle
import android.service.personalcontext.Token
import android.service.personalcontext.hint.BundleHint

/** Creates a [BundleHint] using an idiomatic Kotlin DSL. */
inline fun bundleHint(hintTypeName: String, block: BundleHintKt.Dsl.() -> Unit = {}): BundleHint =
  BundleHintKt.Dsl(BundleHint.Builder())
    .apply { this.hintTypeName = hintTypeName }
    .apply(block)
    .build()

/** Creates an empty [BundleHint]. */
fun emptyBundleHint(hintTypeName: String): BundleHint = bundleHint(hintTypeName) {}

object BundleHintKt {

  @DslMarker annotation class BundleHintDslMarker

  @BundleHintDslMarker
  class Dsl(@PublishedApi internal val builder: BundleHint.Builder) {

    /** Sets the data bundle for the hint. */
    var dataBundle: Bundle by DslProperty { builder.setDataBundle(it) }

    /** Sets the hint type name. */
    var hintTypeName: String? by DslProperty { builder.setHintTypeName(it) }

    /** Allows adding tokens using the `+=` operator. */
    val tokens = DslCollection<Token> { builder.addToken(it) }

    @PublishedApi internal fun build(): BundleHint = builder.build()
  }
}
