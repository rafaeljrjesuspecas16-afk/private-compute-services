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
import android.service.personalcontext.hint.PublishedContextHint
import android.service.personalcontext.insight.BundleInsight

/** Creates a [BundleInsight] using an idiomatic Kotlin DSL. */
inline fun bundleInsight(
  insightTypeName: String,
  originHints: Collection<PublishedContextHint>,
  block: BundleInsightKt.Dsl.() -> Unit = {},
): BundleInsight =
  BundleInsightKt.Dsl(BundleInsight.Builder())
    .apply {
      this.insightTypeName = insightTypeName
      this.originHints += originHints
    }
    .apply(block)
    .build()

/** Creates a [BundleInsight] using an idiomatic Kotlin DSL. */
inline fun bundleInsight(
  insightTypeName: String,
  vararg originHints: PublishedContextHint,
  block: BundleInsightKt.Dsl.() -> Unit = {},
): BundleInsight = bundleInsight(insightTypeName, originHints.asList(), block)

object BundleInsightKt {

  @DslMarker annotation class BundleInsightDslMarker

  @BundleInsightDslMarker
  class Dsl(@PublishedApi internal val builder: BundleInsight.Builder) {

    /** Sets the data bundle for the insight. */
    var dataBundle: Bundle by DslProperty { builder.setDataBundle(it) }

    /** Sets the insight type name. */
    var insightTypeName: String? by DslProperty { builder.setInsightTypeName(it) }

    /** Allows adding originHints using the `+=` operator. */
    val originHints = DslCollection<PublishedContextHint> { builder.addOriginHint(it) }

    /** Allows adding tokens using the `+=` operator. */
    val tokens = DslCollection<Token> { builder.addToken(it) }

    @PublishedApi internal fun build(): BundleInsight = builder.build()
  }
}
