package com.blackbox.cvrt.utils;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * @author Jerrick Pua
 */
public class DateUtils {
    public static final DateTimeFormatter DEFAULT_TIME_STAMP_FORMAT = DateTimeFormat
            .forPattern( "yyyy.MM.dd'T'HH.mm.ss" );
}
