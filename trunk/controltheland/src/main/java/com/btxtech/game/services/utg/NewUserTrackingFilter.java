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

import com.btxtech.game.jsre.common.ClientDateUtil;
import com.btxtech.game.services.common.DateUtil;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * User: beat
 * Date: 03.07.2010
 * Time: 15:17:07
 */
public class NewUserTrackingFilter implements Serializable {
    private Date fromDate;
    private Date toDate;

    public static NewUserTrackingFilter newDefaultFilter() {
        NewUserTrackingFilter newUserTrackingFilter = new NewUserTrackingFilter();
        newUserTrackingFilter.setFromDate(new Date(System.currentTimeMillis() - ClientDateUtil.MILLIS_IN_DAY));
        return newUserTrackingFilter;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }
}
