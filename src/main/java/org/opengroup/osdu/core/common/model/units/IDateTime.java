// Copyright 2021 Schlumberger
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.opengroup.osdu.core.common.model.units;

import java.time.DateTimeException;

public interface IDateTime extends IItem {
    String getFormat();
    String getTimeZone();
    /**
     * Check whether the current {@link IDateTime} instance is valid and usable for conversions.
     * @return True if valid; False if any conversion will fail.
     */
    boolean isValid();
    /**
     * Checks whether unit conversion will work given another {@link IDateTime}
     * @param other the {@link IDateTime} instance
     * @return True if conversion will succeed, False if conversion will fail.
     */
    boolean isConvertible(IDateTime other);
    /**
     * Checks whether a date/date-time conversion can be skipped (isEqualInBehavior) or not.
     * @param other the {@link IDateTime} instance
     * @return True if date/date-time conversion can be skipped; False if date/date-time conversion is required.
     */
    boolean isEqualInBehavior(IDateTime other);
    /**
     * Convert a value from current date/date-time format to the specified date/date-time.
     * @param toDateTime - the target {@link IDateTime} instance
     * @param fromValue - the value in the context of this instance of {@link IDateTime}
     * @return the converted value in the context of toDateTime or null if this instance is invalid or conversion failed.
     */
    String convertToDateTime(IDateTime toDateTime, String fromValue);

    /**
     * Convert a value from current date/date-time format to the ISO8601 date/date-time representation
     * @param fromValue - the value in the current date/date-time context
     * @return the ISO8601 date/date-time representation
     */
    String convertToIsoDateTime(String fromValue) throws IllegalArgumentException, DateTimeException;
    /**
     * Return the normalized ISO8601 date or date-time persistable representation.
     * @return the ISO8601 persistableReference string; null on invalid date/date-time instances
     */
    String getBaseDateTime();

    /**
     * Checks whether the instance is declared as a date (no time details, no time zone).
     * @return true if instance is a date, else false
     */
    boolean isDate();

    /**
     * Checks whether the instance is declared as a time.
     * @return true if instance is a time, else false
     */
    boolean isTime();
}
