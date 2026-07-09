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

package com.google.android.`as`.oss.sealedmemory.config

import com.google.android.`as`.oss.common.config.AbstractConfigReader
import com.google.android.`as`.oss.common.config.FlagListener
import com.google.android.`as`.oss.common.config.FlagManager

/** Config reader for Sealed Memory. */
class SealedMemoryConfigReader(private val flagManager: FlagManager) :
  AbstractConfigReader<SealedMemoryConfig>() {
  init {
    refreshConfig()
    flagManager
      .listenable()
      .addListener(
        FlagListener {
          if (FlagListener.anyHasPrefix(it, SealedMemoryFlags.PREFIX)) {
            refreshConfig()
          }
        }
      )
  }

  override fun computeConfig(): SealedMemoryConfig {
    return SealedMemoryConfig(
      isSealedMemoryServiceEnabled =
        flagManager.get(SealedMemoryFlags.ENABLE_SEALED_MEMORY_SERVICE),
      managementAllowedApps = flagManager.get(SealedMemoryFlags.MANAGEMENT_ALLOWED_APPS),
      accessAllowedApps = flagManager.get(SealedMemoryFlags.ACCESS_ALLOWED_APPS),
    )
  }
}
