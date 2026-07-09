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

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appsearch.app.AppSearchSession
import androidx.appsearch.platformstorage.PlatformStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.guava.await

/** Implementation of [AppSearchSessionProvider] using [PlatformStorage] */
@RequiresApi(Build.VERSION_CODES.S)
class PlatformAppSearchSessionProvider
@Inject
constructor(@ApplicationContext private val context: Context) : AppSearchSessionProvider {
  override suspend fun getAppSearchSession(): AppSearchSession =
    PlatformStorage.createSearchSessionAsync(
        PlatformStorage.SearchContext.Builder(context, DATABASE_NAME).build()
      )
      .await()

  companion object {
    private const val DATABASE_NAME = "sm_key_store"
  }
}
