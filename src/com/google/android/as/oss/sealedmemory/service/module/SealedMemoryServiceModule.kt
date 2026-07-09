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

package com.google.android.`as`.oss.sealedmemory.service.module

import android.content.Context
import com.google.android.apps.miphone.pcs.grpc.Annotations.GrpcService
import com.google.android.apps.miphone.pcs.grpc.Annotations.GrpcServiceName
import com.google.android.apps.miphone.pcs.grpc.Annotations.GrpcServiceSecurityPolicy
import com.google.android.`as`.oss.common.config.ConfigReader
import com.google.android.`as`.oss.common.security.SecurityPolicyUtils
import com.google.android.`as`.oss.common.security.api.PackageSecurityInfoList
import com.google.android.`as`.oss.sealedmemory.api.SealedMemorySecretAccessServiceGrpcKt
import com.google.android.`as`.oss.sealedmemory.api.SealedMemorySecretManagementServiceGrpcKt
import com.google.android.`as`.oss.sealedmemory.config.SealedMemoryConfig
import com.google.android.`as`.oss.sealedmemory.service.impl.SealedMemorySecretAccessService
import com.google.android.`as`.oss.sealedmemory.service.impl.SealedMemorySecretManagementService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.ElementsIntoSet
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey
import io.grpc.BindableService
import io.grpc.binder.SecurityPolicies
import io.grpc.binder.SecurityPolicy
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object SealedMemoryServiceModule {

  private fun getSecurityPolicy(
    context: Context,
    allowedAppsList: PackageSecurityInfoList,
  ): SecurityPolicy {
    val securityPolicies =
      allowedAppsList.packageSecurityInfosList
        .map {
          SecurityPolicyUtils.makeSecurityPolicy(
            it,
            context,
            /* allowTestKeys= */ !SecurityPolicyUtils.isUserBuild(),
            /* usePermissionDeniedForInvalidPolicy= */ false,
          )
        }
        .filterNotNull()
        .toTypedArray()

    return if (securityPolicies.isEmpty()) {
      SecurityPolicies.permissionDenied("No valid security policies configured")
    } else {
      SecurityPolicies.anyOf(*securityPolicies)
    }
  }

  // Bindings for SealedMemorySecretManagementService

  @Provides
  @ElementsIntoSet
  @GrpcService
  @Singleton
  fun provideManagementBindableService(
    impl: SealedMemorySecretManagementService,
    configReader: ConfigReader<SealedMemoryConfig>,
  ): Set<BindableService> {
    return if (configReader.config.isSealedMemoryServiceEnabled) {
      setOf(impl)
    } else {
      emptySet()
    }
  }

  @Provides
  @ElementsIntoSet
  @GrpcServiceName
  fun provideManagementServiceName(configReader: ConfigReader<SealedMemoryConfig>): Set<String> {
    return if (configReader.config.isSealedMemoryServiceEnabled) {
      setOf(SealedMemorySecretManagementServiceGrpcKt.SERVICE_NAME)
    } else {
      emptySet()
    }
  }

  @Provides
  @IntoMap
  @GrpcServiceSecurityPolicy
  @StringKey(SealedMemorySecretManagementServiceGrpcKt.SERVICE_NAME)
  fun provideManagementSecurityPolicy(
    @ApplicationContext context: Context,
    configReader: ConfigReader<SealedMemoryConfig>,
  ): SecurityPolicy {
    return getSecurityPolicy(context, configReader.config.managementAllowedApps)
  }

  // Bindings for SealedMemorySecretAccessService

  @Provides
  @ElementsIntoSet
  @GrpcService
  @Singleton
  fun provideAccessBindableService(
    impl: SealedMemorySecretAccessService,
    configReader: ConfigReader<SealedMemoryConfig>,
  ): Set<BindableService> {
    return if (configReader.config.isSealedMemoryServiceEnabled) {
      setOf(impl)
    } else {
      emptySet()
    }
  }

  @Provides
  @ElementsIntoSet
  @GrpcServiceName
  fun provideAccessServiceName(configReader: ConfigReader<SealedMemoryConfig>): Set<String> {
    return if (configReader.config.isSealedMemoryServiceEnabled) {
      setOf(SealedMemorySecretAccessServiceGrpcKt.SERVICE_NAME)
    } else {
      emptySet()
    }
  }

  @Provides
  @IntoMap
  @GrpcServiceSecurityPolicy
  @StringKey(SealedMemorySecretAccessServiceGrpcKt.SERVICE_NAME)
  fun provideAccessSecurityPolicy(
    @ApplicationContext context: Context,
    configReader: ConfigReader<SealedMemoryConfig>,
  ): SecurityPolicy {
    return getSecurityPolicy(context, configReader.config.accessAllowedApps)
  }
}
