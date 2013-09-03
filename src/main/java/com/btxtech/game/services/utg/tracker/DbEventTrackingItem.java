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

import com.btxtech.game.jsre.common.utg.tracking.EventTrackingItem;
import org.hibernate.annotations.Index;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * User: beat
 * Date: 03.08.2010
 * Time: 15:55:52
 */
@Entity(name = "TRACKER_EVENT_ITEM")
public class DbEventTrackingItem implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(nullable = false)
    private Date timeStamp;
    @Column(nullable = false)
    @Index(name = "TRACKER_EVENT_ITEM_INDEX_START_UUID")
    private String startUuid;
    private long clientTimeStamp;
    private int xPos;
    private int yPos;
    private int eventType;
    /**
     * Used by Hibernate
     */
    public DbEventTrackingItem() {
    }

    public DbEventTrackingItem(EventTrackingItem eventTrackingItem) {
        timeStamp = new Date();
        startUuid = eventTrackingItem.getStartUuid();
        clientTimeStamp = eventTrackingItem.getClientTimeStamp();
        xPos = eventTrackingItem.getXPos();
        yPos = eventTrackingItem.getYPos();
        eventType = eventTrackingItem.getEventType();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbEventTrackingItem)) return false;

        DbEventTrackingItem that = (DbEventTrackingItem) o;

        return id != null && id.equals(that.id);
    }

    public String getStartUuid() {
        return startUuid;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }

    public EventTrackingItem createEventTrackingItem() {
        return new EventTrackingItem(startUuid, xPos, yPos, eventType, clientTimeStamp);
    }
}