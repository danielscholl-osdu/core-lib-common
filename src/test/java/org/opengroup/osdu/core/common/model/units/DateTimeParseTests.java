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

import org.junit.Test;

import java.time.DateTimeException;
import java.time.format.DateTimeParseException;
import java.time.zone.ZoneRulesException;

import static org.junit.Assert.*;

public class DateTimeParseTests {
    private static final String DATE01 = "{\"type\": \"DAT\",\"format\": \"MM/dd/yyyy\"}";
    private static final String DATE02 = "{\"type\": \"DAT\",\"format\": \"MMMM d, yyyy\"}";
    private static final String TIME01 = "{\"type\": \"DTM\",\"format\": \"MMMM d, yyyy HH:mm:ss.SSS\", \"timeZone\": \"UTC\"}";
    private static final String TIME02 = "{\"type\": \"DTM\",\"format\": \"MM/dd/yyyy HH:mm\", \"timeZone\": \"Antarctica/South_Pole\"}";
    private static final String TIME03 = "{\"type\": \"DTM\",\"format\": \"MM/dd/yyyy HH:mm\", \"timeZone\": \"UTC+5:30\"}";
    private static final String TIME04 = "{\"type\": \"DTM\",\"format\": \"MM/dd/yyyy HH:mm\", \"timeZone\": \"GMT-5:30\"}";
    private static final String TIME05 = "{\"type\": \"DTM\",\"format\": \"MM/dd/yyyy HH:mm:ss.S\", \"timeZone\": \"UTC\"}";
    private static final String TIME06 = "{\"type\": \"DTM\",\"format\": \"MM/dd/yyyy h:mm a\", \"timeZone\": \"UTC\"}";
    private static final String TIME07 = "{\"type\": \"DAT\",\"format\": \"MM/dd/yyyy HH:mm\", \"timeZone\": \"UTC\"}";
    private static final String TIME08 = "{\"type\": \"DTM\",\"format\": \"MM/dd/yyyy HH:mm\", \"timeZone\": \"America/Port of Spain\"}";
    private static final String TIME09 = "{\"type\": \"DTM\",\"format\": \"MM/dd/yyyy HH:mm\", \"timeZone\": \"Invalid TimeZone Format\"}";
    private static final String TIME10 = "{\"type\": \"DTM\",\"format\": \"MM/dd/yyyy HH:mm\", \"timeZone\": \"UnknownTimezone\"}";
    private static final String TIME11 = "{\"type\": \"DTM\",\"format\": \"MM/ddd/yyyyy HH:mm\", \"timeZone\": \"UTC\"}";

    @Test
    public void testDate(){
        IDateTime dt = ReferenceConverter.parseDateTimeReference(DATE01);
        assertNotNull(dt);
        assertTrue(dt.isValid());
        String iso = dt.convertToIsoDateTime("07/15/2019");
        assertNotNull(iso);
        assertEquals("2019-07-15", iso);
        dt = ReferenceConverter.parseDateTimeReference(DATE02);
        assertTrue(dt.isValid());
        iso = dt.convertToIsoDateTime("July 5, 2019");
        assertEquals("2019-07-05", iso);
        String iso_pr = dt.getBaseDateTime();
        assertNotNull(iso_pr);
        IDateTime iso_dt = ReferenceConverter.parseDateTimeReference(iso_pr);
        assertEquals("yyyy-MM-dd", iso_dt.getFormat());

        dt = ReferenceConverter.parseDateTimeReference(DATE01);
        iso = dt.convertToDateTime(iso_dt, "07/15/2019");
        assertEquals("2019-07-15", iso);
    }

    @Test
    public void testDateTime(){
        IDateTime dt = ReferenceConverter.parseDateTimeReference(TIME01);
        assertNotNull(dt);
        assertTrue(dt.isValid());
        String iso_time = dt.convertToIsoDateTime("July 5, 2019 22:05:15.123");
        assertNotNull(iso_time);
        assertEquals("2019-07-05T22:05:15.123Z", iso_time);
        String iso_pr = dt.getBaseDateTime();
        assertNotNull(iso_pr);
        IDateTime iso_dt = ReferenceConverter.parseDateTimeReference(iso_pr);
        assertEquals("yyyy-MM-ddTHH:mm:ss.fffZ", iso_dt.getFormat());

        dt = ReferenceConverter.parseDateTimeReference(TIME01);
        iso_time = dt.convertToDateTime(iso_dt, "July 5, 2019 22:05:15.123");
        assertEquals("2019-07-05T22:05:15.123Z", iso_time);
    }

    @Test
    public void testDateTimeWithUTCAndGMTOffset(){
        IDateTime dt = ReferenceConverter.parseDateTimeReference(TIME03);
        assertNotNull(dt);
        assertTrue(dt.isValid());
        String iso_time = dt.convertToIsoDateTime("08/03/2019 14:05");
        assertNotNull(iso_time);
        assertEquals("2019-08-03T08:35:00.000Z", iso_time);

        dt = ReferenceConverter.parseDateTimeReference(TIME04);
        assertNotNull(dt);
        assertTrue(dt.isValid());
        iso_time = dt.convertToIsoDateTime("08/03/2019 14:05");
        assertNotNull(iso_time);
        assertEquals("2019-08-03T19:35:00.000Z", iso_time);

        String iso_pr = dt.getBaseDateTime();
        assertNotNull(iso_pr);
        IDateTime iso_dt = ReferenceConverter.parseDateTimeReference(iso_pr);
        assertEquals("yyyy-MM-ddTHH:mm:ss.fffZ", iso_dt.getFormat());
    }

    @Test
    public void testDateTimeFractionOfSeconds(){
        IDateTime dt = ReferenceConverter.parseDateTimeReference(TIME05);
        assertNotNull(dt);
        assertTrue(dt.isValid());
        String iso_time = dt.convertToIsoDateTime("08/03/2019 13:56:22.1");
        assertNotNull(iso_time);
        assertEquals("2019-08-03T13:56:22.100Z", iso_time);
        String iso_pr = dt.getBaseDateTime();
        assertNotNull(iso_pr);
        IDateTime iso_dt = ReferenceConverter.parseDateTimeReference(iso_pr);
        assertEquals("yyyy-MM-ddTHH:mm:ss.fffZ", iso_dt.getFormat());
    }

    @Test
    public void testDateTimeWithTimeZone(){
        IDateTime dt = ReferenceConverter.parseDateTimeReference(TIME02);
        assertNotNull(dt);
        assertTrue(dt.isValid());
        String iso_time = dt.convertToIsoDateTime("08/03/2019 14:05");
        assertNotNull(iso_time);
        assertEquals("2019-08-03T02:05:00.000Z", iso_time);

        dt = ReferenceConverter.parseDateTimeReference(TIME08);
        assertNotNull(dt);
        assertTrue(dt.isValid());
        iso_time = dt.convertToIsoDateTime("08/03/2019 14:05");
        assertNotNull(iso_time);
        assertEquals("2019-08-03T18:05:00.000Z", iso_time);

        String iso_pr = dt.getBaseDateTime();
        assertNotNull(iso_pr);
        IDateTime iso_dt = ReferenceConverter.parseDateTimeReference(iso_pr);
        assertEquals("yyyy-MM-ddTHH:mm:ss.fffZ", iso_dt.getFormat());
    }

    @Test
    public void testDateTimeAmPm(){
        IDateTime dt = ReferenceConverter.parseDateTimeReference(TIME06);
        assertNotNull(dt);
        assertTrue(dt.isValid());
        String iso_time = dt.convertToIsoDateTime("08/03/2019 8:00 PM");
        assertNotNull(iso_time);
        assertEquals("2019-08-03T20:00:00.000Z", iso_time);
        String iso_pr = dt.getBaseDateTime();
        assertNotNull(iso_pr);
        IDateTime iso_dt = ReferenceConverter.parseDateTimeReference(iso_pr);
        assertEquals("yyyy-MM-ddTHH:mm:ss.fffZ", iso_dt.getFormat());
    }

    @Test
    public void testDateTimeToDate(){
        IDateTime dt = ReferenceConverter.parseDateTimeReference(TIME07);
        assertNotNull(dt);
        assertTrue(dt.isValid());
        String iso_time = dt.convertToIsoDateTime("08/03/2019 08:00");
        assertNotNull(iso_time);
        assertEquals("2019-08-03", iso_time);
        String iso_pr = dt.getBaseDateTime();
        assertNotNull(iso_pr);
        IDateTime iso_dt = ReferenceConverter.parseDateTimeReference(iso_pr);
        assertEquals("yyyy-MM-dd", iso_dt.getFormat());
    }

    @Test(expected = DateTimeParseException.class)
    public void testDateTimeMonthValidation(){
        IDateTime dt = ReferenceConverter.parseDateTimeReference(TIME05);
        assertNotNull(dt);
        assertTrue(dt.isValid());
        dt.convertToIsoDateTime("26/03/2019 13:56:22.1");
    }

    @Test(expected = DateTimeException.class)
    public void testDateTimeInvalidTimeZoneFormat(){
        IDateTime dt = ReferenceConverter.parseDateTimeReference(TIME09);
        assertNotNull(dt);
        assertTrue(dt.isValid());
        dt.convertToIsoDateTime("08/03/2019 13:56");
    }

    @Test(expected = ZoneRulesException.class)
    public void testDateTimeUnknownTimeZone(){
        IDateTime dt = ReferenceConverter.parseDateTimeReference(TIME10);
        assertNotNull(dt);
        assertTrue(dt.isValid());
        dt.convertToIsoDateTime("08/03/2019 13:56");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDateTimeInvalidPatternFormat(){
        IDateTime dt = ReferenceConverter.parseDateTimeReference(TIME11);
        assertNotNull(dt);
        assertTrue(dt.isValid());
        dt.convertToIsoDateTime("08/03/2019 13:56");
    }
}
