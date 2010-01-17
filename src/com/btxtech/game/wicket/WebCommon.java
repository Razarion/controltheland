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

import java.util.Date;

/**
 * User: beat
 * Date: Aug 2, 2009
 * Time: 2:03:23 PM
 */
public class WebCommon {
    public static final String DATE_TIME_FORMAT_STRING = "dd.MM.yyyy HH:mm:ss";
    public static final String DATE_FORMAT_STRING = "dd.MM.yyyy";

    static public String formatDuration(long duration) {
        duration = duration / 1000;
        return String.format("%d:%02d:%02d", duration / 3600, (duration % 3600) / 60, (duration % 60));
    }

    public static String getTimeDiff(Date start, Date end) {
        long diffMs = end.getTime() - start.getTime();
        diffMs /= 1000;
        return Long.toString(diffMs);
    }
}
