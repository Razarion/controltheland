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

import com.btxtech.game.jsre.common.ScrollTrackingItem;
import java.io.Serializable;
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
    @Column(nullable = false)
    private long clientTimeStamp;
    @Column(nullable = false)
    private String sessionId;
    @Column(name = "leftPos")
    private int left;
    private int top;
    private int width;
    private int height;

    /**
     * Used by hibernate
     */
    public DbScrollTrackingItem() {
    }

    public DbScrollTrackingItem(ScrollTrackingItem scrollTrackingItem, String sessionId) {
        this.sessionId = sessionId;
        clientTimeStamp = scrollTrackingItem.getClientTimeStamp();
        left = scrollTrackingItem.getLeft();
        top = scrollTrackingItem.getTop();
        width = scrollTrackingItem.getWidth();
        height = scrollTrackingItem.getHeight();
    }

    public ScrollTrackingItem createScrollTrackingItem() {
        return new ScrollTrackingItem(left, top, width, height, clientTimeStamp);
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