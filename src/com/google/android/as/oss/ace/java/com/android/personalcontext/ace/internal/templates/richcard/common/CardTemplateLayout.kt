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

package com.android.personalcontext.ace.internal.templates.richcard.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.personalcontext.ace.internal.templates.richcard.CardUiData
import com.android.personalcontext.ace.internal.templates.richcard.DeprecatedUiCardContext

/**
 * A common template for visualizer cards, providing a consistent layout with attribution, app
 * content, and actions.
 */
@Suppress("NewApi", "FlaggedApi")
@Composable
fun CardTemplateLayout(
  cardUiData: CardUiData<DeprecatedUiCardContext>,
  modifier: Modifier = Modifier,
  isEnergyEffectEnabled: Boolean = false,
  timeSupplierMs: () -> Long = { System.currentTimeMillis() },
  content: @Composable ColumnScope.() -> Unit,
) {
  Surface(
    modifier = modifier.fillMaxWidth().wrapContentHeight(),
    shape = RoundedCornerShape(32.dp),
    color = MaterialTheme.colorScheme.surfaceContainerHighest,
  ) {
    Column(
      modifier =
        Modifier.then(
            if (isEnergyEffectEnabled) {
              Modifier.energyCardBackground(
                MaterialTheme.colorScheme.surfaceContainerHighest,
                timeSupplierMs,
              )
            } else {
              Modifier
            }
          )
          .padding(horizontal = 12.dp, vertical = 16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
          if (cardUiData.cardTitle != null) {
            CardTitle(
              icon = cardUiData.icon,
              cardTitle = cardUiData.cardTitle,
              modifier = Modifier.weight(1f).padding(end = 8.dp),
            )
          } else {
            Spacer(modifier = Modifier.weight(1f))
          }
          val serverSideCloseInsight = cardUiData.dismissInsight
          if (serverSideCloseInsight != null) {
            CardDismissIcon(
              dismissInsight = serverSideCloseInsight,
              modifier = Modifier.padding(top = 4.dp),
            )
          }
        }
      }

      content()

      val actions = cardUiData.actions
      if (!actions.isNullOrEmpty()) {
        CardActionRow(cardActions = actions)
      }
    }
  }
}
