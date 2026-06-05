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

package com.android.personalcontext.ace.common.builders

import kotlin.reflect.KProperty

/** A reusable helper class to support write-only properties in DSL builders. */
class DslProperty<T>(private val set: (T) -> Any) {

  operator fun getValue(thisRef: Any?, property: KProperty<*>): T =
    throw UnsupportedOperationException("Getter is not supported for property '${property.name}'")

  operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
    set(value)
  }
}

/** A reusable helper class to support `+=` syntax for adding items in DSL builders. */
class DslCollection<T>(private val add: (T) -> Any) {

  operator fun plusAssign(item: T) {
    add(item)
  }

  operator fun plusAssign(items: Collection<T>) {
    items.forEach { add(it) }
  }
}
