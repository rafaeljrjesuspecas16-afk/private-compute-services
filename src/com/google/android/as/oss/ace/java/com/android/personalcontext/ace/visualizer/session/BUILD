# Copyright 2025 Google LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

load("@bazel_rules_android//android:rules.bzl", "android_library")

package(
    default_visibility = [
        "//third_party/java/android_app/personalcontext:libraries",
    ],
)

android_library(
    name = "visualizer_session",
    srcs = ["VisualizerSession.kt"],
    deps = [
        "//third_party/java/androidx/compose/ui",
        "//third_party/java/androidx/lifecycle/common",
        "//third_party/java/androidx/lifecycle/runtime",
        "//third_party/java/androidx/lifecycle/viewmodel",
        "//third_party/java/androidx/savedstate",
    ],
)

android_library(
    name = "visualizer_session_factory",
    srcs = ["VisualizerSessionFactory.kt"],
    deps = [
        ":visualizer_session",
        "//third_party/java/androidx/compose/ui",
    ],
)

android_library(
    name = "visualizer_session_factory_impl",
    srcs = ["VisualizerSessionFactoryImpl.kt"],
    deps = [
        ":visualizer_session",
        ":visualizer_session_factory",
        "//third_party/java/androidx/compose/ui",
        "@maven//:javax_inject_javax_inject",
    ],
)
