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
    public static final List JS_ENABLED_CHOICES = Arrays.asList(ENABLED, DISABLED, BOTH);
    private String jsEnabled;

    public static UserTrackingFilter newDefaultFilter() {
        UserTrackingFilter userTrackingFilter =  new UserTrackingFilter();
        userTrackingFilter.setJsEnabled(ENABLED);
        return userTrackingFilter;
    }

    public String getJsEnabled() {
        return jsEnabled;
    }

    public void setJsEnabled(String jsEnabled) {
        this.jsEnabled = jsEnabled;
    }
}
