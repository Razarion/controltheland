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

package com.btxtech.game.jsre.client.utg.missions;

import com.btxtech.game.jsre.client.ClientSyncItemView;
import com.btxtech.game.jsre.client.ClientSyncBaseItemView;
import com.btxtech.game.jsre.client.cockpit.Group;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;

/**
 * User: beat
 * Date: 26.01.2010
 * Time: 19:23:00
 */
public abstract class Mission {
    private String name;

    protected Mission(String name) {
        this.name = name;
    }

    abstract public void start() throws MissionAportedException;

    abstract public boolean isAccomplished();

    abstract public void blink();

    abstract public long getAccomplishedTimeStamp();

    abstract public void close();


    public void onOwnSelectionChanged(Group selectedGroup) throws MissionAportedException {
    }

    public void onSyncItemDeactivated(SyncBaseItem syncBaseItem) {
    }

    public void onExecuteCommand(SyncBaseItem syncItem, BaseCommand baseCommand) {
    }

    public void onItemCreated(ClientSyncItemView item) {
    }

    public void onItemDeleted(ClientSyncItemView item) {
    }

    public void onItemBuilt(ClientSyncBaseItemView clientSyncItemView) {
    }

    protected void scrollToItem(ClientSyncItemView item) {
        TerrainView.getInstance().moveToMiddle(item);
    }

    public String getName() {
        return name;
    }
}
