package fhi360.it.assetverify.util;

import java.time.format.DateTimeFormatter;

public class DateTimeFormatterUtil {

    /**
     * Creates a DateTimeFormatter with the specified pattern.
     *
     * @param pattern The pattern for the DateTimeFormatter.
     * @return DateTimeFormatter instance.
     */
    public static DateTimeFormatter createDateTimeFormatter(String pattern) {
        return DateTimeFormatter.ofPattern(pattern);
    }
}

