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

import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.request.http.WebResponse;

import javax.servlet.http.Cookie;
import java.util.List;
import java.util.UUID;

/**
 * User: beat
 * Date: Aug 2, 2009
 * Time: 2:03:23 PM
 */
public class WebCommon {
    public static final String RAZARION_COOKIE_ID = "cookieId";

    public static String getCookie(List<Cookie> cookies, String name) {
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                return cookie.getValue();
            }
        }
        return null;
    }

    public static void setCookie(WebResponse response, String name, String cookieValue, int maxAgeInSecond) {
        Cookie cookie = new Cookie(name, cookieValue);
        cookie.setMaxAge(maxAgeInSecond);
        response.addCookie(cookie);
    }

    public static String getTrackingCookie(List<Cookie> cookies) {
        return getCookie(cookies, RAZARION_COOKIE_ID);
    }

    public static String generateTrackingCookieId() {
        return UUID.randomUUID().toString().toUpperCase();
    }

    public static void setTrackingCookie(WebResponse response, String cookieValue) {
        setCookie(response, RAZARION_COOKIE_ID, cookieValue, Integer.MAX_VALUE);
    }

    public static boolean isAuthorized(String role) {
        AuthenticatedWebSession session = AuthenticatedWebSession.get();
        return session.getRoles().hasRole(role);
    }
}
