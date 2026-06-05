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

import android.service.personalcontext.hint.ContextHint
import android.service.personalcontext.hint.HintFilter

/** Creates a [HintFilter] using an idiomatic Kotlin DSL. */
inline fun hintFilter(block: HintFilterKt.Dsl.() -> Unit): HintFilter =
  HintFilterKt.Dsl(HintFilter.Builder()).apply(block).build()

/** Creates an empty [HintFilter]. */
fun emptyHintFilter(): HintFilter = hintFilter {}

object HintFilterKt {

  @DslMarker annotation class HintFilterDslMarker

  @HintFilterDslMarker
  class Dsl(@PublishedApi internal val builder: HintFilter.Builder) {

    /** Configures filters that are *allowed* (hints matching these will be included). */
    inline fun allow(block: FilterScope.() -> Unit) {
      FilterScope(builder, HintFilter.FILTER_TYPE_ALLOWED).apply(block)
    }

    /** Configures filters that are *required* (a hint matching these MUST be present). */
    inline fun require(block: FilterScope.() -> Unit) {
      FilterScope(builder, HintFilter.FILTER_TYPE_REQUIRED).apply(block)
    }

    @PublishedApi internal fun build(): HintFilter = builder.build()
  }

  /** Scope class that implicitly applies the correct FilterType to all enclosed builder calls. */
  @HintFilterDslMarker
  class FilterScope(
    @PublishedApi internal val builder: HintFilter.Builder,
    @PublishedApi internal val filterType: Int,
  ) {

    /** Adds a filter that matches on a [ContextHint] subclass type. */
    inline fun <reified T : ContextHint> contextHint() {
      val unused = builder.addHintType(T::class.java, filterType)
    }

    /** Adds a filter that matches on a [ContextHint] subclass type. */
    fun contextHint(hintClass: Class<out ContextHint>) {
      val unused = builder.addHintType(hintClass, filterType)
    }

    /** Adds a filter that matches based on the hint type name specified on a `BundleHint`. */
    fun bundleHint(typeName: String) {
      val unused = builder.addBundleHintTypeName(typeName, filterType)
    }

    /** Adds a filter that matches based on the hint publisher's package. */
    fun packageName(packageName: String) {
      val unused = builder.addPackage(packageName, filterType)
    }
  }
}
