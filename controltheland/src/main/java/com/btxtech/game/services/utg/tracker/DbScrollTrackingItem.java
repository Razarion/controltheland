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

import com.btxtech.game.jsre.common.utg.tracking.TerrainScrollTracking;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * User: beat
 * Date: Jul 26, 2009
 * Time: 11:09:27 AM
 */
@Entity(name = "TRACKER_SCROLLING")
public class DbScrollTrackingItem implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;
    private Date timeStamp;
    @Column(nullable = false)
    private long clientTimeStamp;
    @Column(nullable = false)
    private String sessionId;
    @Column(name = "leftPos")
    private int left;
    private int top;
    @Deprecated
    private int width;
    @Deprecated
    private int height;

    /**
     * Used by hibernate
     */
    public DbScrollTrackingItem() {
    }

    public DbScrollTrackingItem(TerrainScrollTracking terrainScrollTracking, String sessionId) {
        this.sessionId = sessionId;
        clientTimeStamp = terrainScrollTracking.getClientTimeStamp();
        left = terrainScrollTracking.getLeft();
        top = terrainScrollTracking.getTop();
        width = terrainScrollTracking.getWidth();
        height = terrainScrollTracking.getHeight();
        timeStamp = new Date();        
    }

    public TerrainScrollTracking createScrollTrackingItem() {
        return new TerrainScrollTracking(left, top, width, height, clientTimeStamp);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbScrollTrackingItem that = (DbScrollTrackingItem) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}