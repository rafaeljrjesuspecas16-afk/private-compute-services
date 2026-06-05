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

package com.android.personalcontext.ace.common.wrappers

import android.os.Parcel
import android.service.personalcontext.RenderToken
import androidx.annotation.VisibleForTesting
import java.util.UUID

/** Wrapper interface for [RenderToken]. */
sealed interface IRenderToken {
  /**
   * Returns the unwrapped [RenderToken]. May return null if originally wrapped from a unit test,
   * where constructing an instance of [RenderToken] is not possible.
   */
  fun unwrap(): RenderToken?

  /** @see RenderToken.tag */
  val tag: String?

  /** @see RenderToken.rendererComponentId */
  val rendererComponentId: UUID

  /** @see RenderToken.tokenId */
  val tokenId: UUID

  /** Parcels the token into the given [dest]. */
  fun writeToParcel(dest: Parcel, flags: Int)
}

/** Creates an [IRenderToken] from a [RenderToken]. */
fun RenderToken.wrap(): IRenderToken = RenderTokenWrapper(this)

private class RenderTokenWrapper(private val original: RenderToken) : IRenderToken {
  override fun unwrap() = original

  override val tag: String?
    get() = original.tag

  override val rendererComponentId: UUID
    get() = original.rendererComponentId

  override val tokenId: UUID
    get() = original.tokenId

  override fun writeToParcel(dest: Parcel, flags: Int) {
    original.writeToParcel(dest, flags)
  }
}

@VisibleForTesting
class RenderTokenForTesting(
  override val tag: String? = null,
  override val rendererComponentId: UUID = UUID(0, 0),
  override val tokenId: UUID = UUID(0, 0),
  private val writeToParcelAction: (Parcel, Int) -> Unit = { _, _ -> },
) : IRenderToken {
  override fun unwrap() = null

  override fun writeToParcel(dest: Parcel, flags: Int) {
    writeToParcelAction(dest, flags)
  }
}

/** Returns a collection of unwrapped [RenderToken]. */
fun Collection<IRenderToken>.unwrapAll(): List<RenderToken> = mapNotNull { it.unwrap() }
