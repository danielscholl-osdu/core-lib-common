package org.opengroup.osdu.core.common.crs.dates;

public class DatesConversionServiceErrorMessages {
    public static final String MISSING_DATA_BLOCK = "DateTime Conversion: DataBlock is missing or empty in this record, no conversion applied.";
    public static final String MISSING_META_KIND = "DateTime conversion: Required property 'kind' in meta block is missing or empty, no conversion applied.";
    public static final String MISSING_PROPERTY_NAMES = "DateTime conversion: 'propertyNames' in meta block is missing or empty, no conversion applied";
    public static final String ILLEGAL_PROPERTY_NAMES = "DateTime conversion: 'propertyNames' illegal, no conversion applied.";
    public static final String MISSING_REFERENCE = "DateTime conversion: Required property 'persistableReference' is missing or empty, no conversion applied.";
    public static final String MISSING_PROPERTY = "DateTime conversion: Property %s missing, no conversion applied.";
    public static final String INVALID_REFERENCE = "DateTime conversion: 'persistableReference' not valid, no conversion applied.";
    public static final String MISMATCH_REFERENCE = "DateTime conversion: Frame of reference does not match given data for property %s, no conversion applied.";
    public static final String INVALID_TIMEZONE = "DateTime conversion: %s. No conversion applied.";
    public static final String INVALID_FORMATTER = "DateTime conversion: Invalid DateTime format. %s. No conversion applied.";
    public static final String INVALID_DATETIME_VALUE = "DateTime conversion: \"%s\" could not be parsed for property %s. %s. No conversion applied.";
    public static final String ERROR_PARSING_VALUE = "DateTime conversion: Error parsing \"%s\". No conversion applied.";
}
