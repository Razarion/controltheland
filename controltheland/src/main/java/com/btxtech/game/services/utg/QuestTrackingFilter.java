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

import java.io.Serializable;
import java.util.Date;

/**
 * User: beat
 * Date: 03.07.2010
 * Time: 15:17:07
 */
public class QuestTrackingFilter implements Serializable {
    private Date fromDate;
    private Date toDate;
    private Integer dbId;

    public static QuestTrackingFilter newDefaultFilter() {
        QuestTrackingFilter newUserTrackingFilter = new QuestTrackingFilter();
        newUserTrackingFilter.setFromDate(new Date());
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

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public boolean hasToDate() {
        return toDate != null;
    }

    public Integer getDbId() {
        return dbId;
    }

    public void setDbId(Integer dbId) {
        this.dbId = dbId;
    }
}
