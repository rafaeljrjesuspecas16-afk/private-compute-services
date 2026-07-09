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

package com.android.personalcontext.ace.internal.templates.richcard.flight

import com.android.personalcontext.ace.internal.templates.richcard.CardType
import com.android.personalcontext.ace.internal.templates.richcard.DeprecatedUiCardContext

/** Card Context for a Flight info in a rich card. */
@Deprecated(
  level = DeprecationLevel.WARNING,
  message = "Should be removed once the templates is fully refactored.",
)
data class DeprecatedUiFlightCardContext(
  /** Required Fields for server retrieval */
  /** The flight number, e.g., 'UA 123' or 'UA123'. */
  val flightNumber: String,
  /** The departure time in milliseconds since epoch indicating the departure time of the flight. */
  val departureTimeMs: Long,
  val flightDateYyyyMmDd: String,

  /** Optional Fields */
  /** The flight status, e.g., 'On time', 'Delayed', 'Cancelled'. */
  val flightStatus: Field? = null,
  /** The flight departure datetime (formatted), e.g., 'Thu, Oct 12, 12:00 PM'. */
  val flightDepartureDatetime: Field? = null,
  /** The flight date and time range, e.g., 'Thu, Oct 12, 12:00 PM – 2:00 PM'. */
  val flightDatetimeRange: Field? = null,
  /** The airline name, e.g., 'United Airlines'. */
  val airline: Field? = null,
  /** The origin airport code, e.g., 'SFO'. */
  val originAirportCode: Field? = null,
  /** The destination airport code, e.g., 'LAX'. */
  val destinationAirportCode: Field? = null,
  /** The departure gate, e.g., '12'. */
  val departureGate: Field? = null,
  /** The arrival gate, e.g., '34'. */
  val arrivalGate: Field? = null,
  /** The departure time, e.g., '12:00 PM'. */
  val departureTime: Field? = null,
  /** The arrival time, e.g., '2:00 PM'. */
  val arrivalTime: Field? = null,
  /** The departure terminal, e.g., '2'. */
  val departureTerminal: Field? = null,
  /** The arrival terminal, e.g., '3'. */
  val arrivalTerminal: Field? = null,
  /** The luggage claim location identifier, e.g., '12'. */
  val luggageClaim: Field? = null,
) : DeprecatedUiCardContext {
  override val cardType: CardType = CardType.FLIGHT
  override val needsLiveInfo: Boolean = true

  /**
   * Represents an individual data property in a flight card, containing an optional localized
   * [title] and formatted [text] value.
   */
  data class Field(val title: String?, val text: String)
}
