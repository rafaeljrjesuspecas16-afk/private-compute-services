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

import android.service.personalcontext.insight.ContextInsight
import android.service.personalcontext.insight.InsightCollection

/** Creates an [InsightCollection] using an idiomatic Kotlin DSL. */
inline fun insightCollection(block: InsightCollectionKt.Dsl.() -> Unit = {}): InsightCollection =
  InsightCollectionKt.Dsl(InsightCollection.Builder()).apply(block).build()

/** Creates an [InsightCollection] from an existing collection of insights. */
inline fun insightCollection(
  insights: Collection<ContextInsight>,
  block: InsightCollectionKt.Dsl.() -> Unit = {},
): InsightCollection =
  InsightCollectionKt.Dsl(InsightCollection.Builder(insights)).apply(block).build()

object InsightCollectionKt {

  @DslMarker annotation class InsightCollectionDslMarker

  @InsightCollectionDslMarker
  class Dsl(@PublishedApi internal val builder: InsightCollection.Builder) {

    /** Allows adding insights using the `+=` operator. */
    val insights = DslCollection<ContextInsight> { builder.addInsight(it) }

    @PublishedApi internal fun build(): InsightCollection = builder.build()
  }
}
