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

package com.android.personalcontext.ace.visualizer

import com.android.personalcontext.ace.visualizer.templates.VisualizerTemplate
import com.android.personalcontext.ace.visualizer.templates.call.CallVisualizerTemplate
import com.android.personalcontext.ace.visualizer.templates.message.MessageVisualizerTemplate
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet

/** Dagger module for personal context visualizer templates. */
@Module
interface PersonalContextVisualizerTemplatesModule {
  companion object {

    @Provides
    @IntoSet
    fun provideCallVisualizerTemplate(impl: Lazy<CallVisualizerTemplate>): VisualizerTemplate {
      return impl.get()
    }

    @Provides
    @IntoSet
    fun provideMessageVisualizerTemplate(
      impl: Lazy<MessageVisualizerTemplate>
    ): VisualizerTemplate {
      return impl.get()
    }
  }
}
