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

package com.google.android.`as`.oss.sealedmemory.service.impl

/**
 * Represents the result of an encryption or decryption operation.
 *
 * @param V the type of the value returned in case of success.
 */
sealed class EncryptionResult<out V> {
  /**
   * Represents a successful encryption or decryption operation.
   *
   * @property value the result of the operation.
   */
  data class Success<out V>(val value: V) : EncryptionResult<V>()

  /**
   * Represents a failure that might be resolved by retrying the operation.
   *
   * @property cause the exception that caused the failure.
   */
  data class RetryableError(val cause: Exception) : EncryptionResult<Nothing>()

  /**
   * Represents a failure that is unlikely to be resolved by retrying the operation, e.g. due to
   * corrupt data or missing key.
   *
   * @property cause the exception that caused the failure.
   */
  data class IrrecoverableError(val cause: Exception) : EncryptionResult<Nothing>()
}
