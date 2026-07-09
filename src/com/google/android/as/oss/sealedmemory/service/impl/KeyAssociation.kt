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

import androidx.appsearch.annotation.Document

/**
 * AppSearch document representing a named pointer to a [KeyDocument].
 *
 * @property namespace the namespace of the [KeyAssociation]
 * @property id the id of the [KeyAssociation] which is also the key association name
 * @property keyName the name or id of the corresponding [KeyDocument]
 */
@Document
data class KeyAssociation(
  @Document.Namespace val namespace: String,
  @Document.Id val id: String,
  @Document.StringProperty val keyName: String,
)
