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

import com.btxtech.game.jsre.common.utg.tracking.EventTrackingStart;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * User: beat
 * Date: 03.08.2010
 * Time: 15:55:52
 */
@Entity(name = "TRACKER_EVENT_START")
public class DbEventTrackingStart implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;
    private long timeStampMs;
    @Column(nullable = false)
    private String sessionId;
    private long clientTimeStamp;
    private int clientWidth;
    private int clientHeight;
    private int scrollLeft;
    private int scrollTop;
    private int scrollWidth;
    private int scrollHeight;

    /**
     * Used by Hibernate
     */
    public DbEventTrackingStart() {
    }

    public DbEventTrackingStart(EventTrackingStart eventTrackingStart, String sessionId) {
        timeStampMs = System.currentTimeMillis();
        this.sessionId = sessionId;
        clientTimeStamp = eventTrackingStart.getClientTimeStamp();
        clientWidth = eventTrackingStart.getClientWidth();
        clientHeight = eventTrackingStart.getClientHeight();
        scrollLeft = eventTrackingStart.getScrollLeft();
        scrollTop = eventTrackingStart.getScrollTop();
        scrollWidth = eventTrackingStart.getScrollWidth();
        scrollHeight = eventTrackingStart.getScrollHeight();
        clientTimeStamp = eventTrackingStart.getClientTimeStamp();
    }

    public EventTrackingStart createEventTrackingStart() {
        return new EventTrackingStart(clientWidth, clientHeight, scrollLeft, scrollTop, scrollWidth, scrollHeight, clientTimeStamp);
    }

    public String getSessionId() {
        return sessionId;
    }

    public long getClientTimeStamp() {
        return clientTimeStamp;
    }

    public int getClientWidth() {
        return clientWidth;
    }

    public int getClientHeight() {
        return clientHeight;
    }

    public long getTimeStampMs() {
        return timeStampMs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbEventTrackingStart)) return false;

        DbEventTrackingStart that = (DbEventTrackingStart) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}