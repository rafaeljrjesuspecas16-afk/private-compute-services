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

import android.graphics.drawable.Icon
import android.service.personalcontext.insight.InsightDisplayDetails

/** Creates an [InsightDisplayDetails] using a title. */
inline fun insightDisplayDetails(
  title: CharSequence,
  block: InsightDisplayDetailsKt.Dsl.() -> Unit = {},
): InsightDisplayDetails =
  InsightDisplayDetailsKt.Dsl(InsightDisplayDetails.Builder(title)).apply(block).build()

/** Creates an [InsightDisplayDetails] using an icon. */
inline fun insightDisplayDetails(
  icon: Icon,
  block: InsightDisplayDetailsKt.Dsl.() -> Unit = {},
): InsightDisplayDetails =
  InsightDisplayDetailsKt.Dsl(InsightDisplayDetails.Builder(icon)).apply(block).build()

/** Creates an [InsightDisplayDetails] using a title and an icon. */
inline fun insightDisplayDetails(
  title: CharSequence,
  icon: Icon,
  block: InsightDisplayDetailsKt.Dsl.() -> Unit = {},
): InsightDisplayDetails =
  InsightDisplayDetailsKt.Dsl(InsightDisplayDetails.Builder(title, icon)).apply(block).build()

object InsightDisplayDetailsKt {

  @DslMarker annotation class InsightDisplayDetailsDslMarker

  @InsightDisplayDetailsDslMarker
  class Dsl(@PublishedApi internal val builder: InsightDisplayDetails.Builder) {

    /** Sets the content description. */
    var contentDescription: CharSequence? by DslProperty { builder.setContentDescription(it) }

    /** Sets the subtitle. */
    var subtitle: CharSequence? by DslProperty { builder.setSubtitle(it) }

    @PublishedApi internal fun build(): InsightDisplayDetails = builder.build()
  }
}
