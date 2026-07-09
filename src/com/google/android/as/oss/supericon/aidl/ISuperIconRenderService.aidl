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

package com.google.android.as.oss.supericon.aidl;

import android.content.res.Configuration;
import android.graphics.drawable.Icon;
import com.google.android.as.oss.supericon.aidl.ISuperIconRenderCallback;
import com.google.android.as.oss.supericon.aidl.RenderOptions;

/**
 * Service interface for rendering the SuperIcon.
 */
oneway interface ISuperIconRenderService {
  /**
   * DEPRECATED: Use {@link #renderWithOptions(RenderOptions, int, Configuration, InputTransferToken, ISuperIconRenderCallback)} instead.
   * Renders the SuperIcon.
   *
   * @param icon the icon to render
   * @param iconWidth the width of the icon itself
   * @param iconHeight the height of the icon itself
   * @param background the icon to use as the background
   * @param width the desired width of the view
   * @param height the desired height of the view
   * @param minWidth the minimum width of the view
   * @param minHeight the minimum height of the view
   * @param maxWidth the maximum width of the view
   * @param maxHeight the maximum height of the view
   * @param displayId the display ID to render on
   * @param configuration the current configuration
   * @param hostInputToken the input transfer token from the host
   * @param callback the callback to notify when rendering is complete or events occur
   */
  void render(in Icon icon, int iconWidth, int iconHeight, in Icon background, int width,
              int height, int minWidth, int minHeight, int maxWidth, int maxHeight, int displayId,
              in Configuration configuration, in InputTransferToken hostInputToken,
              in ISuperIconRenderCallback callback) = 0;
  /**
   * Renders the SuperIcon.
   *
   * @param renderOptions for rendering views
   * @param displayId the display ID to render on
   * @param configuration the current configuration
   * @param hostInputToken the input transfer token from the host
   * @param callback the callback to notify when rendering is complete or events occur
   */
  void renderWithOptions(in RenderOptions renderOptions, int displayId, in Configuration configuration,
              in InputTransferToken hostInputToken, in ISuperIconRenderCallback callback) = 1;
};