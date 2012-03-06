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
import java.util.UUID;

/**
 * User: beat
 * Date: Aug 2, 2009
 * Time: 2:03:23 PM
 */
public class WebCommon {
    public static final String COOKIE_ID = "cookieId";

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
