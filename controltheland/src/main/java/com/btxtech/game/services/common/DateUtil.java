package com.btxtech.game.services.common;

import java.util.Calendar;
import java.util.Date;

/**
 * User: beat
 * Date: 18.09.2011
 * Time: 22:49:13
 */
public class DateUtil {
    public static final long MILLIS_IN_DAY = 1000 * 60 * 60 * 24;
    public static final long MILLIS_IN_WEEK = MILLIS_IN_DAY * 7;

    /**
     * Strip of: hour, minutes, seconds and milli seconds
     *
     * @param date input
     * @return stripped date
     */
    public static Date dayStart(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return new Date(cal.getTimeInMillis());
    }

    /**
     * Strip of: day, hour, minutes, seconds and milli seconds. The week start is always Monday.
     *
     * @param date input
     * @return stripped date
     */
    public static Date weekStart(Date date) {
        date = dayStart(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return new Date(cal.getTimeInMillis());
    }

    /**
     * Strip of: month, week, hour, minutes, seconds and milli seconds
     *
     * @param date input
     * @return stripped date
     */
    public static Date monthStart(Date date) {
        date = dayStart(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return new Date(cal.getTimeInMillis());
    }

    public static Date addOneDay(Date date) {
        return new Date(date.getTime() + MILLIS_IN_DAY);
    }

    public static Date removeOneDay(Date date) {
        return new Date(date.getTime() - MILLIS_IN_DAY);
    }

    public static Date addOneWeek(Date date) {
        return new Date(date.getTime() + MILLIS_IN_WEEK);
    }

    public static Date removeOneWeek(Date date) {
        return new Date(date.getTime() - MILLIS_IN_WEEK);
    }

    public static Date createDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return new Date(cal.getTimeInMillis());
    }

}
