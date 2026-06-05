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

import android.app.PendingIntent
import android.app.RemoteAction
import android.service.personalcontext.insight.InsightActionDetails

/** Creates an [InsightActionDetails] using an idiomatic Kotlin DSL. */
inline fun insightActionDetails(
  block: InsightActionDetailsKt.Dsl.() -> Unit = {}
): InsightActionDetails =
  InsightActionDetailsKt.Dsl(InsightActionDetails.Builder()).apply(block).build()

object InsightActionDetailsKt {

  @DslMarker annotation class InsightActionDetailsDslMarker

  @InsightActionDetailsDslMarker
  class Dsl(@PublishedApi internal val builder: InsightActionDetails.Builder) {

    /** Sets the pending intent. */
    var pendingIntent: PendingIntent by DslProperty { builder.setPendingIntent(it) }

    /** Sets the remote action. */
    var remoteAction: RemoteAction by DslProperty { builder.setRemoteAction(it) }

    @PublishedApi internal fun build(): InsightActionDetails = builder.build()
  }
}
