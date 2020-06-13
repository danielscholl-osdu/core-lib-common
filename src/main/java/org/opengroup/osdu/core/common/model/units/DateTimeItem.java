package org.opengroup.osdu.core.common.model.units;

import lombok.Data;
import org.opengroup.osdu.core.common.model.units.IDateTime;
import org.opengroup.osdu.core.common.model.units.impl.Date;
import org.opengroup.osdu.core.common.model.units.impl.DateTime;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Set;

@Data
public class DateTimeItem implements IDateTime {
    private String format;
    private String timeZone;
    private Date implementationDate;
    private DateTime implementationTime;
    public DateTimeItem() {
        initialize();
    }

    public DateTimeItem(Date parsedItem){
        this.initialize();
        this.implementationDate = parsedItem;
    }

    public DateTimeItem(DateTime parsedItem){
        this.initialize();
        this.implementationTime = parsedItem;
    }

    private void initialize() {
        this.implementationDate = null;
        this.implementationTime = null;
        this.format = null;
        this.timeZone = "UTC";
    }

    @Override
    public boolean isTime() {
        return this.implementationTime != null;
    }

    @Override
    public boolean isDate() {
        return this.implementationDate != null;
    }

    @Override
    public String getFormat() {
        if (this.implementationDate != null) return this.implementationDate.getFormat();
        else if (this.implementationTime != null) return this.implementationTime.getFormat();
        return null;
    }

    @Override
    public String getTimeZone() {
        if (this.implementationTime != null) return this.implementationTime.getTimeZone();
        return null;
    }

    /**
     * Check whether the current {@link IDateTime} instance is valid and usable for conversions.
     *
     * @return True if valid; False if any conversion will fail.
     */
    @Override
    public boolean isValid() {
        boolean valid = false;
        if (this.implementationTime != null) {
            valid = this.implementationTime.getFormat() != null && this.implementationTime.getTimeZone() != null;
        } else if (this.implementationDate != null) {
            valid = this.implementationDate.getFormat() != null;
        }
        return valid;
    }

    @Override
    public String createPersistableReference() {
        return null;
    }

    /**
     * Checks whether unit conversion will work given another {@link IDateTime}
     *
     * @param other the {@link IDateTime} instance
     * @return True if conversion will succeed, False if conversion will fail.
     */
    @Override
    public boolean isConvertible(IDateTime other) {
        return false;
    }

    /**
     * Checks whether a date/date-time conversion can be skipped (isEqualInBehavior) or not.
     *
     * @param other the {@link IDateTime} instance
     * @return True if date/date-time conversion can be skipped; False if date/date-time conversion is required.
     */
    @Override
    public boolean isEqualInBehavior(IDateTime other) {
        return false;
    }

    /**
     * Convert a value from current date/date-time format to the specified date/date-time.
     *
     * @param toDateTime - the target {@link IDateTime} instance
     * @param fromValue  - the value in the context of this instance of {@link IDateTime}
     * @return the converted value in the context of toDateTime or null if this instance is invalid or conversion failed.
     */
    @Override
    public String convertToDateTime(IDateTime toDateTime, String fromValue) {
        if (toDateTime.isValid()) {
            DateTimeFormatter fromDf = DateTimeFormatter.ofPattern(this.convertToJava(this.getFormat()));
            DateTimeFormatter toDf = DateTimeFormatter.ofPattern(this.convertToJava(toDateTime.getFormat()));
            try {
                if(this.isDate()) {
                    LocalDate parsedLocalDate = LocalDate.parse(fromValue, fromDf);
                    return parsedLocalDate.format(toDf);
                } else {
                    LocalDateTime parsedLocalDateTime = LocalDateTime.parse(fromValue, fromDf);
                    return parsedLocalDateTime.format(toDf);
                }
            } catch (DateTimeParseException pe) {
                return null;
            }
        }
        return null;
    }

    private static final String ISO_DATE_FORMAT = "yyyy-MM-dd";
    private static final String ISO_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final Set<String> AVAILABLE_ZONE_IDS = ZoneId.getAvailableZoneIds();

    // will have risk here, further discussion on hack around needs to add
    private String convertToJava(String fmt){
        String java = fmt;
        java = java.replace("T", "'T'");
        java = java.replace("Z", "'Z'");
        java = java.replace('f', 'S');
        return java;
    }
    /**
     * Convert a value from current date/date-time format to the ISO8601 date/date-time representation
     *
     * @param fromValue - the value in the current date/date-time context
     * @return the ISO8601 date/date-time representation
     */
    @Override
    public String convertToIsoDateTime(String fromValue) throws IllegalArgumentException, DateTimeException {
        if (this.isValid()) {
            String pattern = this.convertToJava(this.getFormat());
            DateTimeFormatter df = DateTimeFormatter.ofPattern(pattern);
            String isoF = this.isDate() ? ISO_DATE_FORMAT : ISO_TIME_FORMAT;

            if (this.isDate()) {
                LocalDate parsedLocalDate = LocalDate.parse(fromValue, df);
                DateTimeFormatter iso = DateTimeFormatter.ofPattern(isoF);
                return parsedLocalDate.format(iso);
            } else {
                LocalDateTime parsedLocalDateTime = LocalDateTime.parse(fromValue, df);
                ZoneId zoneId = this.getZoneId();
                ZonedDateTime zonedDateTime = ZonedDateTime.of(parsedLocalDateTime, zoneId);
                ZonedDateTime convertedDateTime = zonedDateTime.withZoneSameInstant(ZoneOffset.UTC);
                DateTimeFormatter iso = DateTimeFormatter.ofPattern(isoF);
                return convertedDateTime.format(iso);
            }
        }
        return null;
    }

    /**
     * Return the normalized ISO8601 date or date-time persistable representation.
     *
     * @return the ISO8601 persistableReference string; null on invalid date/date-time instances
     */
    @Override
    public String getBaseDateTime() {
        String pr = null;
        if (this.isValid()) {
            if (this.isTime()) {
                DateTime dt = new DateTime();
                dt.setFormat("yyyy-MM-ddTHH:mm:ss.fffZ");
                dt.setTimeZone("UTC");
                pr = dt.toJsonString();
            } else {
                Date dt = new Date();
                dt.setFormat("yyyy-MM-dd");
                pr = dt.toJsonString();
            }
        }
        return pr;
    }

    private String getDoubleDigitHour(String o) {
        return o.substring(0,1) + "0" + o.substring(1);
    }

    /**
     * Return the ZoneId corresponding to the timezone
     * Handles UTC and GMT offset and nonleading zero hour digit
     * String validates input of region-based timezone with spaces, replaces with - and _ and try to find in set of
     * available ZoneIds provided by class
     * @return the ZoneId based on the timezone
     */
    private ZoneId getZoneId() throws DateTimeException {
        ZoneId zoneId;
        String timeZone = this.getTimeZone();
            if ((timeZone.startsWith("UTC") || timeZone.startsWith("GMT")) && timeZone.length() > 3) { // UTC and GMT with offset
                String stringOffset = timeZone.substring(3);
                String offset = stringOffset.split(":")[0].length() == 2 ? this.getDoubleDigitHour(stringOffset) : stringOffset;
                ZoneOffset zoneOffset = ZoneOffset.of(offset);
                zoneId = ZoneId.ofOffset(timeZone.substring(0, 3), zoneOffset);
            } else if (timeZone.contains(" ")) { // finding region specific based timezones with spaces, the set only contains - and _
                String temp1 = timeZone.replace(" ", "_");
                String temp2 = timeZone.replace(" ", "-");
                zoneId = AVAILABLE_ZONE_IDS.contains(temp1) ? ZoneId.of(temp1) : AVAILABLE_ZONE_IDS.contains(temp2) ? ZoneId.of(temp2) : ZoneId.of(timeZone);
            } else { // find zoneId from the class
                zoneId = ZoneId.of(timeZone);
            }
        return zoneId;
    }
}
