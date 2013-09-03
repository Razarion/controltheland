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

package com.btxtech.game.services.utg.tracker;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * User: beat
 * Date: 17.12.2011
 * Time: 13:17:24
 */
@Entity(name = "TRACKER_WINDOW_CLOSED")
public class DbWindowClosed implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(nullable = false)
    private Date timeStamp;
    private long timeStampMs;
    private String sessionId;
    private String startUuid;

    /**
     * Used by Hibernate
     */
    public DbWindowClosed() {
    }

    public DbWindowClosed(String sessionId, String startUuid) {
        this.sessionId = sessionId;
        this.startUuid = startUuid;
        timeStamp = new Date();
        timeStampMs = timeStamp.getTime();
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public long getTimeStampMs() {
        return timeStampMs;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getStartUuid() {
        return startUuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbWindowClosed that = (DbWindowClosed) o;

        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id : System.identityHashCode(this);
    }
}