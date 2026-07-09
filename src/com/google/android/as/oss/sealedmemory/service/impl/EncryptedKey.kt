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

import com.google.protobuf.ByteString

/**
 * Data class representing an encrypted key with its associated metadata.
 *
 * @property encryptedData The concatenated IV and ciphertext (including GCM tag).
 * @property ivLength The length of the initialization vector in bytes.
 * @property tagLengthBits The length of the GCM authentication tag in bits.
 */
data class EncryptedKey(val encryptedData: ByteString, val ivLength: Int, val tagLengthBits: Int)
