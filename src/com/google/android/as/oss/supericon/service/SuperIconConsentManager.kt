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

package com.google.android.`as`.oss.supericon.service

import androidx.datastore.core.DataStore
import com.google.android.`as`.oss.common.config.ConfigReader
import com.google.android.`as`.oss.supericon.config.SuperIconConfig
import com.google.android.libraries.clock.Clock
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/** Represents the user's consent state for the WTv2+ super icon feature. */
enum class ConsentState {
  UNSET,
  GRANTED,
  DENIED,
  /** The user explicitly turned off the feature via a UI toggle in Gboard settings. */
  REVOKED,
}

private const val CURRENT_CONSENT_VERSION = 1L

/** Manages the user's consent state for the WTv2+ super icon feature using DataStore. */
@Singleton
internal class SuperIconConsentManager
@Inject
internal constructor(
  private val dataStore: DataStore<SuperIconConsentData>,
  private val clock: Clock,
  private val configReader: ConfigReader<SuperIconConfig>,
) {

  /** A flow of the current consent state. */
  val consentStateFlow: Flow<ConsentState>
    get() =
      dataStore.data.map { data ->
        when (data.currentState) {
          ConsentStateProto.CONSENT_STATE_GRANTED -> ConsentState.GRANTED
          ConsentStateProto.CONSENT_STATE_DENIED -> ConsentState.DENIED
          ConsentStateProto.CONSENT_STATE_REVOKED -> ConsentState.REVOKED
          else -> ConsentState.UNSET
        }
      }

  /** A flow of the history of consent state changes (timestamp to state). */
  val stateHistoryFlow: Flow<List<Pair<Long, ConsentState>>>
    get() =
      dataStore.data.map { data ->
        data.historyList.map { entry ->
          val state =
            when (entry.state) {
              ConsentStateProto.CONSENT_STATE_GRANTED -> ConsentState.GRANTED
              ConsentStateProto.CONSENT_STATE_DENIED -> ConsentState.DENIED
              ConsentStateProto.CONSENT_STATE_REVOKED -> ConsentState.REVOKED
              else -> ConsentState.UNSET
            }
          entry.timestampMs to state
        }
      }

  /** Updates the consent state and records it in the history. */
  suspend fun recordConsentState(value: ConsentState) {
    val protoValue =
      when (value) {
        ConsentState.UNSET -> ConsentStateProto.CONSENT_STATE_UNSET
        ConsentState.GRANTED -> ConsentStateProto.CONSENT_STATE_GRANTED
        ConsentState.DENIED -> ConsentStateProto.CONSENT_STATE_DENIED
        ConsentState.REVOKED -> ConsentStateProto.CONSENT_STATE_REVOKED
      }

    dataStore.updateData { currentData ->
      currentData.copy {
        currentState = protoValue
        history.add(
          consentHistoryEntry {
            timestampMs = clock.instant().toEpochMilli()
            state = protoValue
            version = CURRENT_CONSENT_VERSION
          }
        )
      }
    }
  }

  suspend fun recordConsentFormShown() {
    dataStore.updateData { currentData -> currentData.copy { consentFormShownCount += 1 } }
  }

  /** Returns the number of times the consent form has been shown */
  suspend fun getConsentFormShownTimes(): Int {
    return dataStore.data.first().consentFormShownCount
  }

  /**
   * Evaluates if the consent form should be shown based on rate-limiting logic:
   * - Immediately returns false if the user explicitly opted out via the Gboard UI toggle (REVOKED)
   * - Max n times shown overall
   * - x days minimum interval after a denial/dismissal
   */
  suspend fun shouldShowConsentForm(): Boolean {
    val data = dataStore.data.first()
    val config = configReader.config

    if (
      data.currentState == ConsentStateProto.CONSENT_STATE_GRANTED ||
        data.currentState == ConsentStateProto.CONSENT_STATE_REVOKED
    ) {
      return false
    }

    // Filter history specifically for DENIED entries.
    // Note: The UI layer explicitly records dismissals (tapping outside) as DENIED,
    // so this single check covers both explicit denials and dismissals without counting UNSET.
    val denials = data.historyList.filter { it.state == ConsentStateProto.CONSENT_STATE_DENIED }

    // Max of n times
    if (denials.size >= config.maxConsentPrompts) {
      return false
    }

    val lastDenialTime = denials.maxOfOrNull { it.timestampMs }
    val repromptMs = config.repromptDuration.inWholeMilliseconds

    // Show again after x days
    if (lastDenialTime != null && clock.instant().toEpochMilli() - lastDenialTime < repromptMs) {
      return false
    }

    return true
  }
}
