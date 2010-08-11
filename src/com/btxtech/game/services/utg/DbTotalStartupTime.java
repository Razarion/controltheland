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

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * User: beat
 * Date: 04.08.2010
 * Time: 20:08:25
 */
@Entity(name = "TRACKER_TOTAL_STARTUP")
public class DbTotalStartupTime {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(nullable = false)
    private Date timeStamp;
    @Column(nullable = false)
    private String sessionId;
    private long startupDuration;
    private long clientTimeStamp;

    /**
     * Used by Hibernate
     */
    public DbTotalStartupTime() {
    }

    public DbTotalStartupTime(long startupDuration, long clientTimeStamp, String sessionId) {
        this.clientTimeStamp = clientTimeStamp;
        this.sessionId = sessionId;
        this.startupDuration = startupDuration;
        timeStamp = new Date();
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public String getSessionId() {
        return sessionId;
    }

    public long getStartupDuration() {
        return startupDuration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbTotalStartupTime)) return false;

        DbTotalStartupTime that = (DbTotalStartupTime) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
