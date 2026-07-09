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

import com.google.android.`as`.oss.common.config.FlagManager.BooleanFlag
import com.google.android.`as`.oss.common.config.FlagManager.ProtoFlag

object SealedMemoryFlags {
  const val PREFIX = "SealedMemory__"

  val ENABLE_SEALED_MEMORY_SERVICE =
    BooleanFlag.create("${PREFIX}enable_sealed_memory_service", true)

  val MANAGEMENT_ALLOWED_APPS =
    ProtoFlag.create(
      "${PREFIX}management_allowed_apps",

      // PackageSecurityInfoList.getDefaultInstance(),
      /* merge= */ false,
    )

  val ACCESS_ALLOWED_APPS =
    ProtoFlag.create(
      "${PREFIX}access_allowed_apps",

      // PackageSecurityInfoList.getDefaultInstance(),
      /* merge= */ false,
    )
}
