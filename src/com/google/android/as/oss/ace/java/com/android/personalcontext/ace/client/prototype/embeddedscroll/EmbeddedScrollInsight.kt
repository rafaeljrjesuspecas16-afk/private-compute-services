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

package com.android.personalcontext.ace.client.prototype.embeddedscroll

import android.os.Bundle
import android.service.personalcontext.hint.PublishedContextHint
import androidx.core.view.ViewCompat.ScrollAxis
import com.android.personalcontext.ace.client.prototype.PrototypeContextInsight
import com.android.personalcontext.ace.client.prototype.PrototypeInsightId.EmbeddedScrollInsightId
import com.android.personalcontext.ace.common.EmbeddedScrollEvent
import com.android.personalcontext.ace.common.EmbeddedScrollEventType

/**
 * An insight for an [EmbeddedScrollEvent] emitted by an Embedded Visualizer.
 *
 * @property axes If [type] is [SCROLL_START], the scroll axes.
 * @property x If [type] is [SCROLL_DELTA], the scroll delta in the x direction. If [type] is
 *   [SCROLL_STOP], the fling velocity in the x direction.
 * @property y If [type] is [SCROLL_DELTA], the scroll delta in the y direction. If [type] is
 *   [SCROLL_STOP], the fling velocity in the y direction.
 */
data class EmbeddedScrollInsight(
  val type: EmbeddedScrollEventType,
  @property:ScrollAxis val axes: Int = 0,
  val x: Float = 0f,
  val y: Float = 0f,
  override val originHints: Collection<PublishedContextHint> = emptySet(),
) : PrototypeContextInsight(EmbeddedScrollInsightId, this) {

  override fun exportDataToBundle(bundle: Bundle) {
    bundle.putString("type", type.name)
    bundle.putInt("axes", axes)
    bundle.putFloat("x", x)
    bundle.putFloat("y", y)
  }

  companion object : PrototypeContextInsightCreator() {

    override fun create(bundle: Bundle, originHints: Set<PublishedContextHint>) =
      EmbeddedScrollInsight(
        type = enumValueOf(bundle.getString("type")!!),
        axes = bundle.getInt("axes"),
        x = bundle.getFloat("x"),
        y = bundle.getFloat("y"),
        originHints = originHints,
      )

    /** Convert a [EmbeddedScrollEvent] to [EmbeddedScrollInsight]. */
    fun EmbeddedScrollEvent.toEmbeddedScrollInsight() =
      EmbeddedScrollInsight(type = this.type, axes = this.axes, x = this.x, y = this.y)
  }
}
