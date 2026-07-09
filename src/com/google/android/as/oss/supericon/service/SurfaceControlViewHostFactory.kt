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

package com.google.android.`as`.oss.supericon.service

import android.annotation.SuppressLint
import android.content.Context
import android.os.IBinder
import android.view.Display
import android.view.SurfaceControlViewHost
import android.window.InputTransferToken
import javax.inject.Inject

/** Factory to create [SurfaceControlViewHost] instances. */
@SuppressLint("NewApi")
open class SurfaceControlViewHostFactory @Inject constructor() {
  open fun create(
    context: Context,
    display: Display,
    inputTransferToken: InputTransferToken,
  ): SurfaceControlViewHost {
    return SurfaceControlViewHost(context, display, inputTransferToken)
  }

  open fun create(
    context: Context,
    display: Display,
    windowToken: IBinder,
  ): SurfaceControlViewHost {
    return SurfaceControlViewHost(context, display, windowToken)
  }
}
