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

package(default_visibility = ["//visibility:public"])

package_group(
    name = "feedback_api_visibility",
    includes = ["//visibility:public"],
)

filegroup(
    name = "manifest",
    srcs = ["AndroidManifest.xml"],
)

android_library(
    name = "feedback",
    srcs = [
        "FeedbackApi.kt",
    ],
    exports_manifest = True,
    manifest = "AndroidManifest_api.xml",
    visibility = [":feedback_api_visibility"],
    deps = [
        "//src/com/google/android/as/oss/feedback/proto:entity_feedback_dialog_data_kt_proto_lite",
        "//src/com/google/android/as/oss/feedback/proto:multi_feedback_dialog_data_kt_proto_lite",
        "//third_party/java/androidx/appcompat",
    ],
)

android_library(
    name = "prod_modules",
    exports_manifest = True,
    manifest = "AndroidManifest.xml",
    tags = [
        "keep_dep",
    ],
    exports = [
        ":feedback_activity",
        "//src/com/google/android/as/oss/feedback/config:module",
        "//src/com/google/android/as/oss/feedback/serviceclient/serviceconnection:module",
    ],
)

android_library(
    name = "feedback_activity",
    srcs = [
        "FeedbackActivity.kt",
    ],
    exports_manifest = True,
    manifest = "AndroidManifest.xml",
    resource_files = glob(["res/**"]),
    deps = [
        ":feedback",
        "//src/com/google/android/as/oss/common/config",
        "//src/com/google/android/as/oss/feedback/config",
        "//src/com/google/android/as/oss/feedback/domain:feedback_ui_state",
        "//src/com/google/android/as/oss/feedback/domain:feedback_view_model",
        "//src/com/google/android/as/oss/feedback/proto:entity_feedback_dialog_data_kt_proto_lite",
        "//src/com/google/android/as/oss/feedback/proto:multi_feedback_dialog_data_kt_proto_lite",
        "//src/com/google/android/as/oss/feedback/ui:multi_entity_feedback_dialog",
        "//src/com/google/android/as/oss/feedback/ui:single_entity_feedback_dialog",
        "//third_party/java/androidx/activity/compose",
        "//third_party/java/androidx/appcompat",
        "//third_party/java/androidx/compose/runtime",
        "@maven//:com_google_dagger_dagger",
        "@maven//:com_google_flogger_google_extensions",
        "@maven//:javax_inject_javax_inject",
    ],
)

android_library(
    name = "betterbug_activity",
    srcs = [
        "BetterBugAttachmentActivity.kt",
        "BetterBugAttachmentScreen.kt",
        "BetterBugAttachmentViewModel.kt",
    ],
    exports_manifest = True,
    manifest = "AndroidManifest_internal.xml",
    resource_files = glob(["internal_res/**"]),
    deps = [
        "//java/com/google/android/libraries/material/compose:button",
        "//java/com/google/android/libraries/material/compose:checkbox",
        "//java/com/google/android/libraries/material/compose:progress_indicator",
        "//java/com/google/android/libraries/material/compose:scaffold",
        "//java/com/google/android/libraries/material/compose:text",
        "//java/com/google/common/time:time-android",
        "//src/com/google/android/as/oss/common",
        "//src/com/google/android/as/oss/feedback/proto/dataservice:internal_feedback_data_kt_proto_lite",
        "//src/com/google/android/as/oss/feedback/serviceclient:internal_service_client",
        "//third_party/java/android_libs/material_components:dialog",
        "//third_party/java/androidx/activity/compose",
        "//third_party/java/androidx/appcompat",
        "//third_party/java/androidx/compose/foundation",
        "//third_party/java/androidx/compose/foundation/layout",
        "//third_party/java/androidx/compose/material3",
        "//third_party/java/androidx/compose/runtime",
        "//third_party/java/androidx/compose/runtime/saveable",
        "//third_party/java/androidx/compose/ui",
        "//third_party/java/androidx/compose/ui/unit",
        "//third_party/java/androidx/hilt/navigation_compose",
        "//third_party/java/dagger/hilt:view_model",
        "//third_party/kotlin/kotlinx_coroutines:kotlinx_coroutines-jvm",
        "@maven//:com_google_dagger_dagger",
        "@maven//:com_google_dagger_hilt-android",
        "@maven//:com_google_flogger_google_extensions",
        "@maven//:javax_inject_javax_inject",
    ],
)
