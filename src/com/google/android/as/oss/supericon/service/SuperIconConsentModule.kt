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

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.ProtoSerializer
import androidx.datastore.dataStoreFile
import com.google.android.`as`.oss.common.CoroutineQualifiers
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

@Module
@InstallIn(SingletonComponent::class)
internal object SuperIconConsentModule {

  @Provides
  @Singleton
  fun provideSuperIconConsentDataStore(
    @ApplicationContext context: Context,
    @CoroutineQualifiers.IoDispatcher ioDispatcher: CoroutineDispatcher,
  ): DataStore<SuperIconConsentData> {
    return DataStoreFactory.create(
      serializer = ProtoSerializer(superIconConsentData {}),
      scope = CoroutineScope(ioDispatcher + SupervisorJob()),
      produceFile = { context.dataStoreFile("super_icon_consent.pb") },
    )
  }
}
