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

package(default_visibility = [
    "//javatests/com/google/android/libraries/inputmethod/supericon:__subpackages__",
    "//visibility:public",
])

android_library(
    name = "render_options",
    srcs = ["RenderOptions.kt"],
    idl_parcelables = [
        "RenderOptions.aidl",
    ],
    plugins = [
        "//third_party/java/android_libs/safeparcel:processor",
    ],
    deps = [
        "//src/com/google/android/as/oss/supericon/utils",
        "//third_party/java/android_libs/safeparcel",
    ],
)

android_library(
    name = "conversation_data",
    srcs = ["ConversationData.kt"],
    idl_parcelables = [
        "ConversationData.aidl",
        "Message.aidl",
    ],
    plugins = [
        "//third_party/java/android_libs/safeparcel:processor",
    ],
    deps = [
        "//third_party/java/android_libs/safeparcel",
    ],
)

android_library(
    name = "conversation_content_service_aidl",
    idl_srcs = [
        "IConversationContentCallback.aidl",
        "IConversationContentService.aidl",
    ],
    deps = [
        ":conversation_data",
        "//src/com/google/android/as/oss/supericon/utils",
    ],
)

android_library(
    name = "super_icon_render_service_aidl",
    idl_srcs = [
        "ISuperIconRenderCallback.aidl",
        "ISuperIconRenderService.aidl",
        "ISuperIconUi.aidl",
        "ISuperIconSurfacePackageResultCallback.aidl",
    ],
    deps = [
        ":conversation_data",
        ":render_options",
        "//src/com/google/android/as/oss/supericon/utils",
    ],
)
