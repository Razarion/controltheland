package com.btxtech.game.jsre.common;

public class ClientDateUtil {
    public static final long MILLIS_IN_SECOND = 1000;
    public static final long MILLIS_IN_MINUTE = MILLIS_IN_SECOND * 60;
    public static final long MILLIS_IN_HOUR = MILLIS_IN_MINUTE * 60;
    public static final long MILLIS_IN_DAY = MILLIS_IN_HOUR * 24;
    public static final long MILLIS_IN_WEEK = MILLIS_IN_DAY * 7;

    public static String dateToMinuteString(long date) {
        return Long.toString(date / MILLIS_IN_MINUTE);
    }
}
