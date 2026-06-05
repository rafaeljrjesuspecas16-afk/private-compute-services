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

package com.android.personalcontext.ace.internal.compat

import android.service.personalcontext.PersonalContextManager
import android.service.personalcontext.insight.ContextInsight
import android.util.Log
import com.android.personalcontext.ace.common.FlatIndexUtils.flatIndexOf
import com.android.personalcontext.ace.common.MetaTags.ACE_EMBEDDED_TAG
import com.android.personalcontext.ace.common.PackedIntUtils.packValue
import com.android.personalcontext.ace.common.wrappers.IPublishedContextInsight
import com.android.personalcontext.ace.common.wrappers.IRenderToken
import com.android.personalcontext.ace.visualizer.compat.InsightEventReporter
import com.android.personalcontext.ace.visualizer.compat.InsightEventReporterFactoryCompat
import com.android.personalcontext.ace.visualizer.templates.utils.trafficshaperqueue.TrafficShaperQueue
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope

class InsightEventReporterFactoryCompatImpl @Inject constructor() :
  InsightEventReporterFactoryCompat {

  override fun create(scope: CoroutineScope?): InsightEventReporter {
    if (scope == null) return super.create(scope)

    val trafficShaperQueue = TrafficShaperQueue(scope)

    return object : InsightEventReporter {

      override fun PersonalContextManager.reportChildInsightEvent(
        publishedInsight: IPublishedContextInsight,
        childInsight: ContextInsight,
        eventType: Int,
        renderToken: IRenderToken,
      ) = trafficShaperQueue.enqueue {
        val flatIndex = publishedInsight.flatIndexOf(childInsight)
        Log.v(TAG, "flatIndex = $flatIndex")

        val packedEventType = eventType.packValue(flatIndex)
        val packedEventTypeString = packedEventType.toString(radix = 2).padStart(32, '0')
        Log.i(
          TAG,
          "Visualizer: reportChildInsightEvent($publishedInsight, $packedEventTypeString, $renderToken) [$ACE_EMBEDDED_TAG]",
        )

        reportInsightEvent(
          publishedInsight.unwrap() ?: return@enqueue,
          packedEventType,
          renderToken.unwrap() ?: return@enqueue,
        )
      }
    }
  }

  companion object {
    private const val TAG = "InsightEventReporterCompat"
  }
}
