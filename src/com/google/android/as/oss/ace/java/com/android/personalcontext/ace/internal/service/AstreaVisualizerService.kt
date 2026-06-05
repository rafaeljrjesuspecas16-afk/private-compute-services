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

package com.android.personalcontext.ace.internal.service

import android.content.Context
import android.service.personalcontext.RenderToken
import android.service.personalcontext.embedded.InsightSurfaceClientInfo
import android.service.personalcontext.embedded.InsightSurfaceVisualizerService
import android.service.personalcontext.insight.PublishedContextInsight
import android.view.View
import com.android.personalcontext.ace.common.wrappers.wrap
import com.android.personalcontext.ace.visualizer.connector.VisualizerServiceConnector
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint(InsightSurfaceVisualizerService::class)
class PcsVisualizerService : Hilt_PcsVisualizerService() {

  @Inject lateinit var connector: Lazy<VisualizerServiceConnector>

  override fun onClientConnected(info: InsightSurfaceClientInfo) {
    connector.get().onClientConnected(info)
  }

  override fun onCreateEmbeddedView(
    context: Context,
    publishedInsight: PublishedContextInsight,
    renderToken: RenderToken?,
    info: InsightSurfaceClientInfo,
  ): View? {
    return connector
      .get()
      .onCreateEmbeddedView(context, publishedInsight.wrap(), renderToken?.wrap(), info.wrap())
  }

  override fun onClientUpdated(
    oldClientInfo: InsightSurfaceClientInfo,
    newClientInfo: InsightSurfaceClientInfo,
  ): Boolean {
    return connector.get().onClientUpdated(oldClientInfo.wrap(), newClientInfo.wrap())
  }

  override fun onClientDisconnected(info: InsightSurfaceClientInfo) {
    connector.get().onClientDisconnected(info.wrap())
  }
}
