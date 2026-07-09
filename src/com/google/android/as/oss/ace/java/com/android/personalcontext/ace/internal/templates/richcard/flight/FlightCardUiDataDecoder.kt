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

package com.android.personalcontext.ace.internal.templates.richcard.flight

import android.os.Bundle
import android.service.personalcontext.insight.BundleInsight
import android.service.personalcontext.insight.ContextInsight
import com.android.personalcontext.ace.internal.templates.richcard.decoder.CardUiDataDecoder
import javax.inject.Inject
import javax.inject.Singleton

/** Converts between [DeprecatedUiFlightCardContext] and [ContextInsight] using [BundleInsight]. */
@Singleton
@Suppress("NewApi")
class FlightCardUiDataDecoder @Inject constructor() :
  CardUiDataDecoder<DeprecatedUiFlightCardContext>() {

  override fun ContextInsight.toCardContext(): DeprecatedUiFlightCardContext {
    require(this is BundleInsight) { "Unsupported insight type" }
    val bundle = this.dataBundle

    val flightNumber =
      requireNotNull(bundle.getString(KEY_FLIGHT_NUMBER)) { "Missing flight number" }
    require(bundle.containsKey(KEY_DEPARTURE_TIME_MS)) { "Missing departure time" }
    val departureTimeMs = bundle.getLong(KEY_DEPARTURE_TIME_MS)
    val flightDateYyyyMmDd =
      requireNotNull(bundle.getString(KEY_FLIGHT_DATE_YYYY_MM_DD)) { "Missing flight date" }

    return DeprecatedUiFlightCardContext(
      flightNumber = flightNumber,
      departureTimeMs = departureTimeMs,
      flightDateYyyyMmDd = flightDateYyyyMmDd,
      flightDatetimeRange = bundle.getField(KEY_FLIGHT_DATE_TIME_RANGE),
      flightStatus = bundle.getField(KEY_FLIGHT_STATUS),
      flightDepartureDatetime = bundle.getField(KEY_FLIGHT_DEPARTURE_DATETIME),
      airline = bundle.getField(KEY_AIRLINE),
      originAirportCode = bundle.getField(KEY_ORIGIN),
      destinationAirportCode = bundle.getField(KEY_DESTINATION),
      departureGate = bundle.getField(KEY_DEPARTURE_GATE),
      arrivalGate = bundle.getField(KEY_ARRIVAL_GATE),
      departureTime = bundle.getField(KEY_DEPARTURE_TIME),
      arrivalTime = bundle.getField(KEY_ARRIVAL_TIME),
      departureTerminal = bundle.getField(KEY_DEPARTURE_TERMINAL),
      arrivalTerminal = bundle.getField(KEY_ARRIVAL_TERMINAL),
    )
  }

  private fun Bundle.getField(key: String): DeprecatedUiFlightCardContext.Field? =
    getBundle(key)?.toField()

  private fun Bundle.toField(): DeprecatedUiFlightCardContext.Field? {
    val title = getString(KEY_FIELD_TITLE)
    val text = getString(KEY_FIELD_TEXT) ?: return null
    return DeprecatedUiFlightCardContext.Field(title, text)
  }

  companion object {
    internal const val KEY_FLIGHT_NUMBER = "flight_number"
    internal const val KEY_DEPARTURE_TIME_MS = "departure_time_ms"
    internal const val KEY_FLIGHT_DATE_YYYY_MM_DD = "flight_date_yyyy_mm_dd"
    internal const val KEY_FLIGHT_DATE_TIME_RANGE = "flight_date_time_range"
    internal const val KEY_FLIGHT_STATUS = "flight_status"
    internal const val KEY_FLIGHT_DEPARTURE_DATETIME = "flight_departure_datetime"
    internal const val KEY_AIRLINE = "airline"
    internal const val KEY_ORIGIN = "origin"
    internal const val KEY_DESTINATION = "destination"
    internal const val KEY_DEPARTURE_GATE = "departure_gate"
    internal const val KEY_ARRIVAL_GATE = "arrival_gate"
    internal const val KEY_DEPARTURE_TIME = "departure_time"
    internal const val KEY_ARRIVAL_TIME = "arrival_time"
    internal const val KEY_DEPARTURE_TERMINAL = "departure_terminal"
    internal const val KEY_ARRIVAL_TERMINAL = "arrival_terminal"

    internal const val KEY_FIELD_TITLE = "title"
    internal const val KEY_FIELD_TEXT = "text"
  }
}
