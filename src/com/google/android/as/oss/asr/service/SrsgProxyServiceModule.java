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

package com.google.android.as.oss.asr.service;

import android.content.Context;
import com.google.android.as.oss.common.config.ConfigReader;
import com.google.android.as.oss.common.security.SecurityPolicyUtils;
import com.google.android.as.oss.common.security.api.PackageSecurityInfo;
import com.google.android.as.oss.common.security.config.PccSecurityConfig;
import com.google.android.apps.miphone.pcs.grpc.Annotations.GrpcService;
import com.google.android.apps.miphone.pcs.grpc.Annotations.GrpcServiceName;
import com.google.android.apps.miphone.pcs.grpc.Annotations.GrpcServiceSecurityPolicy;
import com.google.android.as.oss.asr.api.SrsgProxyServiceGrpc;
import com.google.android.libraries.concurrent.BackgroundExecutor;
import com.google.android.as.oss.asr.api.GoogleAsrServiceGrpc;
import com.google.common.collect.ImmutableList;
import com.google.common.io.BaseEncoding;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import dagger.multibindings.IntoMap;
import dagger.multibindings.IntoSet;
import dagger.multibindings.StringKey;
import io.grpc.BindableService;
import io.grpc.ManagedChannel;
import io.grpc.ServerInterceptors;
import io.grpc.binder.AndroidComponentAddress;
import io.grpc.binder.BinderChannelBuilder;
import io.grpc.binder.SecurityPolicies;
import io.grpc.binder.SecurityPolicy;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import javax.inject.Singleton;

@Module
@InstallIn(SingletonComponent.class)
abstract class SrsgProxyServiceModule {
  // Package and Service hosting GoogleAsrService in SRSG
  private static final String SRSG_APP_PACKAGE = "com.google.android.tts";

  @Provides
  @IntoSet
  @GrpcService
  static BindableService provideBindableService(SrsgProxyServiceImpl impl) {
    return () ->
        ServerInterceptors.intercept(
            impl, SrsgProxyServiceImpl.getPfdAndAttrSrcExtractorInterceptor());
  }

  @Binds
  @Singleton
  @GrpcService
  abstract SrsgProxyServiceImpl bindSrsgProxyServiceStub(SrsgProxyServiceImpl impl);

  @Provides
  @IntoMap
  @GrpcServiceSecurityPolicy
  @StringKey(SrsgProxyServiceGrpc.SERVICE_NAME)
  static SecurityPolicy provideSecurityPolicy(
      @ApplicationContext Context context, ConfigReader<PccSecurityConfig> securityConfigReader) {

    PackageSecurityInfo asiInfo = securityConfigReader.getConfig().asiPackageSecurityInfo();

    SecurityPolicy policy =
        SecurityPolicyUtils.makeSecurityPolicy(
            asiInfo,
            context,
            /* allowTestKeys= */ !SecurityPolicyUtils.isUserBuild(),
            /* usePermissionDeniedForInvalidPolicy= */ false);

    return policy == null
        ? SecurityPolicies.permissionDenied("No valid security policies")
        : policy;
  }

  @Provides
  @IntoSet
  @GrpcServiceName
  static String provideServiceName() {
    return SrsgProxyServiceGrpc.SERVICE_NAME;
  }

  private static BindServiceFlags getBindServiceFlags() {
    // [redacted] grant server foreground permission to open microphone
    // Set BIND_ALLOW_ACTIVITY_STARTS for Android 14+ to allow start activity from background.
    // This is needed to prompt language pack download confirm dialog.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
      return BindServiceFlags.DEFAULTS.toBuilder()
          .setIncludeCapabilities(true)
          .setAllowActivityStarts(true)
          .build();
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      return BindServiceFlags.DEFAULTS.toBuilder().setIncludeCapabilities(true).build();
    } else {
      return BindServiceFlags.DEFAULTS;
    }
  }

  // Provides the client stub for Pcs to call the GoogleAsrService in SRSG App
  @Provides
  @Singleton
  static GoogleAsrServiceGrpc.GoogleAsrServiceStub provideGoogleAsrServiceStub(
      @ApplicationContext Context context, ConfigReader<PcsAsrConfig> asrConfigReader) {
    SecurityPolicy securityPolicy =
        SecurityPolicyUtils.makeSecurityPolicy(
            asrConfigReader.getConfig().srsgPackageSecurityInfo(),
            context,
            /* allowTestKeys= */ !SecurityPolicyUtils.isUserBuild(),
            /* usePermissionDeniedForInvalidPolicy= */ false);

    ManagedChannel channel =
        BinderChannelBuilder.forAddress(
                AndroidComponentAddress.forRemoteComponent(
                    SRSG_APP_PACKAGE, GoogleAsrServiceGrpc.SERVICE_NAME),
                context)
            .securityPolicy(
                securityPolicy == null
                    ? SecurityPolicies.permissionDenied("No valid security policies for SRSG")
                    : securityPolicy)
            .setBindServiceFlags(getBindServiceFlags())
            .build();
    return GoogleAsrServiceGrpc.newStub(channel);
  }
}
