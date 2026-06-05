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
load("//devtools/deps/check:deps_check.bzl", "check_dependencies")
load("//tools/build_defs/testing:bzl_library.bzl", "bzl_library")

exports_files(["BUILD"])

android_library(
    name = "common_modules",
    tags = [
        "keep_dep",
    ],
    visibility = ["//visibility:public"],
    exports = [
        "//src/com/google/android/as/oss/ace/java/com/android/personalcontext/ace/internal:personal_context_hilt_module",
        "//src/com/google/android/as/oss/ace/java/com/android/personalcontext/ace/internal/service:pcs_visualizer_service",
        "//src/com/google/android/as/oss/ace/java/com/android/personalcontext/ace/visualizer:personal_context_visualizer_templates_module",
    ],
)

bzl_library(
    name = "build_constants_bzl",
    srcs = ["build_constants.bzl"],
    parse_tests = False,
    visibility = [
        "//javatests/com/google/android/apps/miphone/pcs/ace:__subpackages__",
        "//visibility:public",
    ],
)
