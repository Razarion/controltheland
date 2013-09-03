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

package com.btxtech.game.jsre.common.utg.tracking;

import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.cockpit.Group;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;

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
    private String startUuid;

    /**
     * Used by GWT
     */
    SelectionTrackingItem() {
    }

    public SelectionTrackingItem(String startUuid) {
        this.startUuid = startUuid;
        own = null;
    }

    public SelectionTrackingItem(String startUuid, SyncItem selection) {
        this.startUuid = startUuid;
        selectedIds = new ArrayList<Integer>();
        selectedIds.add(GwtCommon.checkInt(selection.getId().getId(), "SelectionTrackingItem (selection) selection.getSyncItem().getId().getId()"));
        own = false;
    }

    public SelectionTrackingItem(String startUuid, Group selectedGroup) {
        this.startUuid = startUuid;
        selectedIds = new ArrayList<Integer>();
        for (SyncBaseItem syncBaseItem : selectedGroup.getSyncBaseItems()) {
            selectedIds.add(GwtCommon.checkInt(syncBaseItem.getId().getId(), "SelectionTrackingItem (selectedGroup) syncBaseItem.getId().getId()"));
        }
        own = true;
    }

    public SelectionTrackingItem(String startUuid, Collection<Integer> selectedIds, long timeStamp, Boolean own) {
        this.startUuid = startUuid;
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

    public String getStartUuid() {
        return startUuid;
    }
}