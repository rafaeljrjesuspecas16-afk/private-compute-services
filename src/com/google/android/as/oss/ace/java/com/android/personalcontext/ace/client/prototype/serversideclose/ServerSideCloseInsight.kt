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

package com.android.personalcontext.ace.client.prototype.serversideclose

import android.os.Bundle
import android.service.personalcontext.hint.PublishedContextHint
import android.service.personalcontext.insight.InsightDisplayDetails
import com.android.personalcontext.ace.client.prototype.PrototypeContextInsight
import com.android.personalcontext.ace.client.prototype.PrototypeInsightId

/** An insight indicating a server-side close signal of the ACE embedded session. */
@SuppressWarnings("FlaggedApi", "NewApi")
class ServerSideCloseInsight(
  val insightDisplayDetails: InsightDisplayDetails? = null,
  override val originHints: Collection<PublishedContextHint> = emptySet(),
) : PrototypeContextInsight(PrototypeInsightId.ServerSideCloseInsightId, this) {

  override fun exportDataToBundle(bundle: Bundle) {
    insightDisplayDetails?.let { bundle.putParcelable(INSIGHT_DISPLAY_DETAILS_KEY, it) }
  }

  companion object : PrototypeContextInsightCreator() {
    private const val INSIGHT_DISPLAY_DETAILS_KEY = "INSIGHT_DISPLAY_DETAILS_KEY"

    override fun create(bundle: Bundle, originHints: Set<PublishedContextHint>) =
      ServerSideCloseInsight(
        insightDisplayDetails =
          bundle.getParcelable(INSIGHT_DISPLAY_DETAILS_KEY, InsightDisplayDetails::class.java),
        originHints = originHints,
      )
  }
}
