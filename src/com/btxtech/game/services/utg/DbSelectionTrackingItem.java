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

import com.btxtech.game.jsre.common.SelectionTrackingItem;
import com.btxtech.game.services.common.Utils;
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
@Entity(name = "TRACKER_SELECTIONS")
public class DbSelectionTrackingItem implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(nullable = false)
    private Date niceTimeStamp;
    @Column(nullable = false)
    private long clientTimeStamp;
    @Column(nullable = false)
    private String sessionId;
    @Column(nullable = false)
    private String selectedItems;
    private Boolean own;

    /**
     * Used by hibernate
     */
    public DbSelectionTrackingItem() {
    }

    public DbSelectionTrackingItem(SelectionTrackingItem selectionTrackingItem, String sessionId) {
        this.sessionId = sessionId;
        clientTimeStamp = selectionTrackingItem.getTimeStamp();
        niceTimeStamp = new Date();
        selectedItems = Utils.integerToSting(selectionTrackingItem.getSelectedIds());
        own = selectionTrackingItem.isOwn();
    }

    public SelectionTrackingItem createSelectionTrackingItem() {
        return new SelectionTrackingItem(Utils.stringToIntegers(selectedItems), clientTimeStamp, own);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbSelectionTrackingItem that = (DbSelectionTrackingItem) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}