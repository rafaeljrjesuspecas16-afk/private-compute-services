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

package com.android.personalcontext.ace.internal

import com.android.personalcontext.ace.internal.compat.CardInsightCompatImpl
import com.android.personalcontext.ace.internal.compat.ClientActionInsightCompatImpl
import com.android.personalcontext.ace.internal.compat.ClientSignalCompatImpl
import com.android.personalcontext.ace.internal.compat.EmbeddedScrollCompatImpl
import com.android.personalcontext.ace.internal.compat.EmptyRenderCompatImpl
import com.android.personalcontext.ace.internal.compat.EnergyEffectsAnimationCompatImpl
import com.android.personalcontext.ace.internal.compat.FlexFontCompatImpl
import com.android.personalcontext.ace.internal.compat.InsightEventReporterFactoryCompatImpl
import com.android.personalcontext.ace.internal.compat.InsightGridCompatImpl
import com.android.personalcontext.ace.internal.compat.PrototypeTransformCompatImpl
import com.android.personalcontext.ace.internal.compat.ThemeCompatImpl
import com.android.personalcontext.ace.internal.compat.VisualMetadataCompatImpl
import com.android.personalcontext.ace.visualizer.compat.CardInsightCompat
import com.android.personalcontext.ace.visualizer.compat.ClientActionInsightCompat
import com.android.personalcontext.ace.visualizer.compat.ClientSignalCompat
import com.android.personalcontext.ace.visualizer.compat.EmbeddedScrollCompat
import com.android.personalcontext.ace.visualizer.compat.EmptyRenderCompat
import com.android.personalcontext.ace.visualizer.compat.EnergyEffectsAnimationCompat
import com.android.personalcontext.ace.visualizer.compat.FlexFontCompat
import com.android.personalcontext.ace.visualizer.compat.InsightEventReporterFactoryCompat
import com.android.personalcontext.ace.visualizer.compat.InsightGridCompat
import com.android.personalcontext.ace.visualizer.compat.PrototypeTransformCompat
import com.android.personalcontext.ace.visualizer.compat.ThemeCompat
import com.android.personalcontext.ace.visualizer.compat.VisualMetadataCompat
import dagger.Lazy
import dagger.Module
import dagger.Provides

/** Dagger module for Pixel-only compat dependencies. */
@Module
interface PersonalContextModuleCompat {
  companion object {

    @Provides
    fun provideEmptyRenderCompat(impl: Lazy<EmptyRenderCompatImpl>): EmptyRenderCompat {
      return impl.get()
    }

    @Provides
    fun providePrototypeTransformCompat(
      impl: Lazy<PrototypeTransformCompatImpl>
    ): PrototypeTransformCompat {
      return impl.get()
    }

    @Provides
    fun provideEmbeddedScrollCompat(impl: Lazy<EmbeddedScrollCompatImpl>): EmbeddedScrollCompat {
      return impl.get()
    }

    @Provides
    fun provideFlexFontCompat(impl: Lazy<FlexFontCompatImpl>): FlexFontCompat {
      return impl.get()
    }

    @Provides
    fun provideEnergyEffectsAnimationCompat(
      impl: Lazy<EnergyEffectsAnimationCompatImpl>
    ): EnergyEffectsAnimationCompat {
      return impl.get()
    }

    @Provides
    fun provideInsightEventReporterCompat(
      impl: Lazy<InsightEventReporterFactoryCompatImpl>
    ): InsightEventReporterFactoryCompat {
      return impl.get()
    }

    @Provides
    fun provideCardInsightCompat(impl: Lazy<CardInsightCompatImpl>): CardInsightCompat {
      return impl.get()
    }

    @Provides
    fun provideInsightGridCompat(impl: Lazy<InsightGridCompatImpl>): InsightGridCompat {
      return impl.get()
    }

    @Provides
    fun provideClientSignalCompat(impl: Lazy<ClientSignalCompatImpl>): ClientSignalCompat {
      return impl.get()
    }

    @Provides
    fun provideClientActionInsightCompat(
      impl: Lazy<ClientActionInsightCompatImpl>
    ): ClientActionInsightCompat {
      return impl.get()
    }

    @Provides
    fun provideThemeCompat(impl: Lazy<ThemeCompatImpl>): ThemeCompat {
      return impl.get()
    }

    @Provides
    fun provideVisualMetadataCompat(impl: Lazy<VisualMetadataCompatImpl>): VisualMetadataCompat {
      return impl.get()
    }
  }
}
