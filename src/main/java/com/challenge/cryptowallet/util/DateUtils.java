package com.challenge.cryptowallet.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static String format(LocalDateTime dateTime) {
        return dateTime.format(formatter);
    }

    public static String getCurrentDateTime() {
        LocalDateTime now = LocalDateTime.now();
        return format(now);
    }
}
