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

package com.android.personalcontext.ace.internal.compat

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import com.android.personalcontext.ace.internal.flexfont.FlexFontUtils.withFlexFont
import com.android.personalcontext.ace.visualizer.compat.FlexFontCompat
import javax.inject.Inject

class FlexFontCompatImpl @Inject constructor() : FlexFontCompat {
  override fun flexFont(
    typography: Typography,
    slant: Float,
    width: Float,
    grade: Int,
    round: Float,
  ): Typography {
    return typography.withFlexFont(slant = slant, width = width, grade = grade, round = round)
  }

  override fun flexFont(
    style: TextStyle,
    weight: Int,
    slant: Float,
    width: Float,
    grade: Int,
    round: Float,
  ): TextStyle {
    return style.withFlexFont(
      weight = weight,
      slant = slant,
      width = width,
      grade = grade,
      round = round,
    )
  }
}
