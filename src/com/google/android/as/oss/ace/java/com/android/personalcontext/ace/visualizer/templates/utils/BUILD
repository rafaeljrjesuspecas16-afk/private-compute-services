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

exports_files(["BUILD"])

package(
    default_visibility = [
        "//javatests/com/google/android/apps/miphone/pcs/ace:__subpackages__",
        "//src/com/google/android/as/oss/ace:__subpackages__",
    ],
)

android_library(
    name = "tintable_icon",
    srcs = ["TintableIcon.kt"],
    deps = [
        "//third_party/java/androidx/compose/foundation",
        "//third_party/java/androidx/compose/material3",
        "//third_party/java/androidx/compose/runtime",
        "//third_party/java/androidx/compose/ui",
        "//third_party/java/androidx/compose/ui/graphics",
    ],
)

android_library(
    name = "remote_action_utils",
    srcs = [
        "RemoteActionUtils.kt",
    ],
    manifest = "AndroidManifest.xml",
    deps = [
        "//third_party/java/android/android_sdk_linux/platforms/stable:android_platform_feature_flags",
    ],
)

android_library(
    name = "embedded_theme",
    srcs = [
        "EmbeddedTheme.kt",
    ],
    deps = [
        "//third_party/java/androidx/compose/foundation",
        "//third_party/java/androidx/compose/runtime",
        "//third_party/java/androidx/compose/ui",
        "//third_party/java/androidx/compose/ui/graphics",
    ],
)
