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

package com.android.personalcontext.ace.client.prototype.loading

import android.os.Bundle
import android.service.personalcontext.hint.PublishedContextHint
import com.android.personalcontext.ace.client.prototype.PrototypeContextInsight
import com.android.personalcontext.ace.client.prototype.PrototypeInsightId.LoadingInsightId

/**
 * An insight representing a loading state.
 *
 * @property originHints The origin hints of the insight.
 */
data class LoadingInsight(override val originHints: Collection<PublishedContextHint> = emptySet()) :
  PrototypeContextInsight(LoadingInsightId, this) {

  override fun exportDataToBundle(bundle: Bundle) {}

  companion object : PrototypeContextInsightCreator() {

    override fun create(
      bundle: Bundle,
      originHints: Set<PublishedContextHint>,
    ): PrototypeContextInsight {
      return LoadingInsight(originHints)
    }
  }
}

/** [LoadingInsight] builder function. */
fun LoadingInsight(vararg hints: PublishedContextHint) = LoadingInsight(originHints = hints.toSet())
