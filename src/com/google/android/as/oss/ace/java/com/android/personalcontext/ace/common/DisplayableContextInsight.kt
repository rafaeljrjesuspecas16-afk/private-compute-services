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

package com.android.personalcontext.ace.common

import android.service.personalcontext.insight.ActionableInsight
import android.service.personalcontext.insight.ContextInsight
import android.service.personalcontext.insight.DisplayInsight
import android.service.personalcontext.insight.InsightDisplayDetails

/**
 * A unified interface representing any framework insight that contains display details.
 *
 * This allows your codebase to handle both [DisplayInsight] and [ActionableInsight] polymorphically
 * without needing to check their specific framework types.
 */
interface DisplayableInsight {

  /** The display details associated with this insight. */
  val displayDetails: InsightDisplayDetails

  /**
   * The underlying framework insight. Useful if you need to pass the insight back to the system
   * framework later.
   */
  val originalInsight: ContextInsight
}

/**
 * A convenience extension function to convert a framework [DisplayInsight] into a
 * [DisplayableInsight].
 */
fun DisplayInsight.asDisplayableInsight(): DisplayableInsight {
  val originalInsight = this

  return object : DisplayableInsight {
    override val displayDetails: InsightDisplayDetails
      get() = originalInsight.details

    override val originalInsight: ContextInsight
      get() = originalInsight
  }
}

/**
 * A convenience extension function to convert a framework [ActionableInsight] into a
 * [DisplayableInsight].
 */
fun ActionableInsight.asDisplayableInsight(): DisplayableInsight {
  val originalInsight = this

  return object : DisplayableInsight {
    override val displayDetails: InsightDisplayDetails
      get() = originalInsight.displayDetails

    override val originalInsight: ContextInsight
      get() = originalInsight
  }
}

/**
 * A convenience extension function to convert a framework [ContextInsight] into a
 * [DisplayableInsight], if possible.
 */
fun ContextInsight.asDisplayableInsight(): DisplayableInsight? =
  when (this) {
    is DisplayInsight -> asDisplayableInsight()
    is ActionableInsight -> asDisplayableInsight()
    else -> null
  }
