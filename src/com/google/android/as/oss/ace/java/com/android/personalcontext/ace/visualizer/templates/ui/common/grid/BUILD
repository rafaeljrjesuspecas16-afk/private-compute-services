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

android_library(
    name = "justified_wrap_layout",
    srcs = ["JustifiedWrapLayout.kt"],
    visibility = [
        "//src/com/google/android/as/oss/ace/java/com/android/personalcontext/ace/internal/templates:__subpackages__",
        "//src/com/google/android/as/oss/ace/java/com/android/personalcontext/ace/visualizer/templates:__subpackages__",
    ],
    deps = [
        "//third_party/java/androidx/compose/foundation/layout",
        "//third_party/java/androidx/compose/runtime",
        "//third_party/java/androidx/compose/ui",
        "//third_party/java/androidx/compose/ui/unit",
        "@maven//:androidx_annotation_annotation",
    ],
)
