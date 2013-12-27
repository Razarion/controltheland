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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * User: beat
 * Date: 03.07.2010
 * Time: 15:17:07
 */
public class NewUserDailyTrackingFilter implements Serializable {
    private Date fromDate;
    private Date toDate;
    private TimeZone timeZone;
    private String facebookAdId;

    public static NewUserDailyTrackingFilter newDefaultFilter() {
        NewUserDailyTrackingFilter newUserTrackingFilter = new NewUserDailyTrackingFilter();
        newUserTrackingFilter.setFromDate(new Date());
        newUserTrackingFilter.setTimeZone(TimeZone.getDefault());
        return newUserTrackingFilter;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public boolean hasFromDate() {
        return fromDate != null;
    }

    public Date getToDate() {
        return toDate;
    }

    public Date getCorrectedFromDate() {
        if (fromDate == null) {
            return null;
        }
        return correctTimeZoneOffsetAdd(DateUtils.truncate(fromDate, Calendar.DAY_OF_MONTH));

    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public boolean hasToDate() {
        return toDate != null;
    }

    public Date getCorrectedExclusiveToDate() {
        if (toDate == null) {
            return null;
        }
        return correctTimeZoneOffsetAdd(DateUtils.addDays(DateUtils.truncate(toDate, Calendar.DAY_OF_MONTH), 1));
    }

    public Date correctTimeZoneOffsetAdd(Date date) {
        if (timeZone != null) {
            int offset = Calendar.getInstance().getTimeZone().getOffset(date.getTime()) - timeZone.getOffset(date.getTime());
            return new Date(date.getTime() + offset);
        } else {
            return date;
        }
    }

    public Date correctTimeZoneOffsetSub(Date date) {
        if (timeZone != null) {
            int offset = timeZone.getOffset(date.getTime()) - Calendar.getInstance().getTimeZone().getOffset(date.getTime());
            return new Date(date.getTime() + offset);
        } else {
            return date;
        }
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    public String getFacebookAdId() {
        return facebookAdId;
    }

    public void setFacebookAdId(String facebookAdId) {
        this.facebookAdId = facebookAdId;
    }

    public boolean hasFacebookAdId() {
        return !StringUtils.isEmpty(facebookAdId);
    }
}
