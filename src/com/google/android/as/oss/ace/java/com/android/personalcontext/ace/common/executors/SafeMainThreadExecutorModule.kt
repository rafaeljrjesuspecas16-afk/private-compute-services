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

package com.android.personalcontext.ace.common.executors

import android.os.Handler
import android.os.Looper
import android.util.Log
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.Executor
import java.util.concurrent.RejectedExecutionException

/** Dagger module that provides [SafeMainThreadExecutor] instances. */
@Module
@InstallIn(SingletonComponent::class)
object SafeMainThreadExecutorModule {

  /**
   * Provides a [SafeMainThreadExecutor] that executes tasks on the main thread.
   *
   * Uncaught exceptions thrown by tasks are intercepted and logged to prevent crashing the
   * application.
   */
  @Provides
  @SafeMainThreadExecutor
  fun provideSafeMainThreadExecutor(): Executor {
    val mainHandler = Handler(Looper.getMainLooper())
    return SafeExecutorImpl(mainHandler)
  }
}

/**
 * An [Executor] that runs tasks on the provided [Handler].
 *
 * It wraps execution in a try-catch block to intercept and log any [Throwable], preventing
 * exceptions from crashing the process.
 */
private class SafeExecutorImpl(private val handler: Handler) : Executor {

  override fun execute(command: Runnable) {
    val safeCommand = Runnable {
      try {
        command.run()
      } catch (t: Throwable) {
        Log.e(TAG, "Intercepted exception on Main Thread", t)
      }
    }

    if (!handler.post(safeCommand)) {
      throw RejectedExecutionException("Main Looper is shutting down")
    }
  }

  companion object {
    private const val TAG = "SafeMainThreadExecutor"
  }
}
