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

package com.btxtech.game.jsre.common;

import com.btxtech.game.jsre.client.ClientSyncItem;
import com.btxtech.game.jsre.client.cockpit.Group;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * User: beat
 * Date: 09.08.2010
 * Time: 22:12:06
 */
public class SelectionTrackingItem implements Serializable {
    private Collection<Integer> selectedIds;
    private long timeStamp = System.currentTimeMillis();
    private Boolean own;

    public SelectionTrackingItem(ClientSyncItem selection) {
        selectedIds = new ArrayList<Integer>();
        selectedIds.add(selection.getSyncItem().getId().getId());
        own = false;
    }

    public SelectionTrackingItem() {
        own = null;
    }

    public SelectionTrackingItem(Group selectedGroup) {
        selectedIds = new ArrayList<Integer>();
        for (SyncBaseItem syncBaseItem : selectedGroup.getSyncBaseItems()) {
            selectedIds.add(syncBaseItem.getId().getId());
        }
        own = true;
    }

    public SelectionTrackingItem(Collection<Integer> selectedIds, long timeStamp, Boolean own) {
        this.selectedIds = selectedIds;
        this.timeStamp = timeStamp;
        this.own = own;
    }

    public Collection<Integer> getSelectedIds() {
        return selectedIds;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public Boolean isOwn() {
        return own;
    }
}