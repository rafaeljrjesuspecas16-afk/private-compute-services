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

package com.android.personalcontext.ace.internal.templates.richcard.contact

import android.service.personalcontext.insight.BundleInsight
import android.service.personalcontext.insight.ContextInsight
import com.android.personalcontext.ace.internal.templates.richcard.decoder.CardUiDataDecoder
import javax.inject.Inject

/** Converts between [DeprecatedUiContactCardContext] and [ContextInsight]. */
@Suppress("NewApi")
class ContactCardUiDataDecoder @Inject internal constructor() :
  CardUiDataDecoder<DeprecatedUiContactCardContext>() {

  override fun ContextInsight.toCardContext(): DeprecatedUiContactCardContext {
    require(this is BundleInsight) {
      "ContactCardUiDataConverter: unexpected ContextInsight type: ${this::class.java.name}"
    }
    val bundle = dataBundle
    val name = requireNotNull(bundle.getString(KEY_NAME)) { "Missing Contact name" }

    return DeprecatedUiContactCardContext(
      name = name,
      phoneNumber = bundle.getString(KEY_PHONE_NUMBER),
      emailAddress = bundle.getString(KEY_EMAIL_ADDRESS),
      address = bundle.getString(KEY_ADDRESS),
      birthday = bundle.getString(KEY_BIRTHDAY),
    )
  }

  companion object {
    private const val KEY_NAME = "name"
    private const val KEY_PHONE_NUMBER = "phone_number"
    private const val KEY_EMAIL_ADDRESS = "email_address"
    private const val KEY_ADDRESS = "address"
    private const val KEY_BIRTHDAY = "birthday"
  }
}
