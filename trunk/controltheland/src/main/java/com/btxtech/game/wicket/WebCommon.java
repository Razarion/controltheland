/*
 * Copyright (c) 2010.
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; version 2 of the License.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 */

package com.btxtech.game.wicket;

import org.apache.wicket.authentication.AuthenticatedWebSession;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * User: beat
 * Date: Aug 2, 2009
 * Time: 2:03:23 PM
 */
public class WebCommon {
    public static final String DATE_TIME_FORMAT_STRING = "dd.MM.yyyy HH:mm:ss";
    public static final String TIME_FORMAT_STRING = "HH:mm:ss";
    public static final String DATE_FORMAT_STRING = "dd.MM.yyyy";
    public static final String COOKIE_ID = "cookieId";

    /**
     * @param duration time in ms
     * @return String representing time h:mm:ss
     */
    static public String formatDuration(long duration) {
        duration = duration / 1000;
        return String.format("%d:%02d:%02d", duration / 3600, (duration % 3600) / 60, (duration % 60));
    }

    /**
     * @param duration time in ms
     * @return String representing time s:ms
     */
    static public String formatDurationMilis(long duration) {
        return String.format("%.2f", duration / 1000.0);
    }

    static public String formatDateTime(Date date) {
        if (date != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_TIME_FORMAT_STRING);
            return simpleDateFormat.format(date);
        } else {
            return "-";
        }
    }

    static public String formatDate(Date date) {
        if (date != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT_STRING);
            return simpleDateFormat.format(date);
        } else {
            return "-";
        }
    }

    static public String formatTime(Date date) {
        if (date != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TIME_FORMAT_STRING);
            return simpleDateFormat.format(date);
        } else {
            return "-";
        }
    }

    static public String formatTime(Long time) {
        if (time != null) {
            return formatTime(new Date(time));
        } else {
            return "-";
        }
    }

    public static String getTimeDiff(Date start, Date end) {
        long diffMs = end.getTime() - start.getTime();
        diffMs /= 1000;
        return Long.toString(diffMs);
    }

    public static String getTimeDiff(long start, long end) {
        long diffMs = end - start;
        diffMs /= 1000;
        return Long.toString(diffMs);
    }

    public static String getCookieId(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(COOKIE_ID)) {
                return cookie.getValue();
            }
        }
        return null;
    }

    public static String generateCookieId() {
        return UUID.randomUUID().toString().toUpperCase();
    }

    public static String addCookieId(HttpServletResponse response, String cookieValue) {
        Cookie cookie = new Cookie(COOKIE_ID, cookieValue);
        cookie.setMaxAge(Integer.MAX_VALUE);
        response.addCookie(cookie);
        return cookie.getValue();
    }

    public static boolean isAuthorized(String role) {
        AuthenticatedWebSession session = AuthenticatedWebSession.get();
        return session.getRoles().hasRole(role);
    }

}
