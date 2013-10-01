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

package com.btxtech.game.services.utg;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * User: beat
 * Date: 03.07.2010
 * Time: 15:17:07
 */
public class UserTrackingFilter implements Serializable {
    public static final String ENABLED = "Enabled";
    public static final String DISABLED = "Disabled";
    public static final String BOTH = "Both";
    public static final List<String> JS_ENABLED_CHOICES = Arrays.asList(ENABLED, DISABLED, BOTH);
    public static final List COOKIE_ENABLED_CHOICES = Arrays.asList(ENABLED, DISABLED, BOTH);
    private String jsEnabled;
    private int days;
    private Integer hits;
    private String sessionId;
    private String cookieId;
    private String optionalFacebookAdValue;

    public static UserTrackingFilter newDefaultFilter() {
        UserTrackingFilter userTrackingFilter = new UserTrackingFilter();
        userTrackingFilter.setJsEnabled(ENABLED);
        userTrackingFilter.setDays(1);
        return userTrackingFilter;
    }

    public String getJsEnabled() {
        return jsEnabled;
    }

    public void setJsEnabled(String jsEnabled) {
        this.jsEnabled = jsEnabled;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public Integer getHits() {
        return hits;
    }

    public void setHits(Integer hits) {
        this.hits = hits;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getCookieId() {
        return cookieId;
    }

    public void setCookieId(String cookieId) {
        this.cookieId = cookieId;
    }

    public String getOptionalFacebookAdValue() {
        return optionalFacebookAdValue;
    }

    public void setOptionalFacebookAdValue(String optionalFacebookAdValue) {
        this.optionalFacebookAdValue = optionalFacebookAdValue;
    }
}
