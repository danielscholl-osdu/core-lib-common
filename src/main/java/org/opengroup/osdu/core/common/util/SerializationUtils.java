package org.opengroup.osdu.core.common.util;


import java.sql.Date;

public final class SerializationUtils {

    public static final String EXPIRATION_DATE_FORMAT = "yyyy-MM-dd";
    public static final String DEFAULT_EXPIRATION_DATE_AS_STRING = "9999-12-31";
    public static final Date DEFAULT_EXPIRATION_DATE = Date.valueOf(DEFAULT_EXPIRATION_DATE_AS_STRING);

    private SerializationUtils() {}
}
