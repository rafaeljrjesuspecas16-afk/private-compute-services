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

package com.android.personalcontext.ace.visualizer.templates.utils.trafficshaperqueue

import android.util.Log
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Queue that invokes actions at a fixed [interval]. Stops when [scope] is cancelled. */
class TrafficShaperQueue(scope: CoroutineScope, interval: Duration = 50.milliseconds) {

  private val queue = Channel<() -> Unit>(Channel.UNLIMITED)

  init {
    scope.launch {
      try {
        // This loop suspends passively when the queue is empty
        for (action in queue) {
          Log.v(TAG, "Running action.")

          try {
            // Execute the queued action
            action.invoke()
          } catch (e: CancellationException) {
            throw e
          } catch (e: Exception) {
            Log.e(TAG, "Action failed.", e)
          }

          // Force a delay before the loop can grab the next item.
          // This smooths out the burst into a steady, metered flow.
          delay(interval)
        }
      } finally {
        queue.close()
      }
    }
  }

  /**
   * Adds an action to the queue and returns immediately.
   *
   * Any exceptions thrown during the execution of the [action] are caught and logged, ensuring they
   * do not prevent or impact the execution of other queued actions. However, note that pending
   * actions may not be executed if the [scope] is cancelled before they are executed.
   */
  fun enqueue(action: () -> Unit) {
    val unused = queue.trySend(action)
  }

  companion object {
    const val TAG = "TrafficShaperQueue"
  }
}
