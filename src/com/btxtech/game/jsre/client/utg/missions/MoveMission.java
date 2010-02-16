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

import com.btxtech.game.jsre.client.ClientSyncBaseItemView;
import com.btxtech.game.jsre.client.cockpit.Group;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.client.utg.SpeechBubble;
import com.btxtech.game.jsre.client.utg.ClientUserTracker;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import java.util.Collection;

/**
 * User: beat
 * Date: 26.01.2010
 * Time: 22:20:04
 */
public class MoveMission extends Mission {
    public static final int MIN_RADIUS = 50;
    public static final int MAX_RADIUS = 150;

    enum Task {
        WAITING_FOR_SELECTION,
        WAITING_FOR_TERRAIN_CLICK,
        FINISHED
    }

    private SpeechBubble speechBubble;
    private ClientSyncBaseItemView item;
    private Task task;
    private long lastAction;

    public MoveMission() {
        super("MoveMission");
    }

    public void start() throws MissionAportedException {
        Collection<ClientSyncBaseItemView> items = ItemContainer.getInstance().getOwnItems();

        item = null;
        for (ClientSyncBaseItemView clientSyncBaseItemView : items) {
            if (clientSyncBaseItemView.getSyncBaseItem().hasSyncBuilder()) {
                item = clientSyncBaseItemView;
                break;
            }
            if (clientSyncBaseItemView.getSyncBaseItem().hasSyncMovable()) {
                item = clientSyncBaseItemView;
                break;
            }
        }

        if (item == null) {
            throw new MissionAportedException("No movable item found");
        }

        speechBubble = new SpeechBubble(item, HtmlConstants.MOVE_HTML1, false);
        task = Task.WAITING_FOR_SELECTION;
        ClientUserTracker.getInstance().onMissionTask(this, task);
        lastAction = System.currentTimeMillis();
    }

    public void onOwnSelectionChanged(Group selectedGroup) {
        if (task != Task.WAITING_FOR_SELECTION || speechBubble == null) {
            return;
        }
        lastAction = System.currentTimeMillis();
        speechBubble.blinkOff();
        task = Task.WAITING_FOR_TERRAIN_CLICK;
        ClientUserTracker.getInstance().onMissionTask(this, task);
        speechBubble.close();
        Index absPos = TerrainView.getInstance().getTerrainHandler().getAbsoluteFreeTerrainInRegion(item.getSyncItem().getPosition(), MIN_RADIUS, MAX_RADIUS, 100);
        speechBubble = new SpeechBubble(absPos.getX() - TerrainView.getInstance().getViewOriginLeft(),
                absPos.getY() - TerrainView.getInstance().getViewOriginTop(),
                HtmlConstants.MOVE_HTML2, false);
    }

    public void onSyncItemDeactivated(SyncBaseItem syncBaseItem) {
        if (syncBaseItem.equals(item.getSyncBaseItem())) {
            if (task != Task.WAITING_FOR_TERRAIN_CLICK) {
                return;
            }
            task = Task.FINISHED;

            speechBubble = new SpeechBubble(item, HtmlConstants.MOVE_HTML3, false);
            lastAction = System.currentTimeMillis();
        }
    }

    @Override
    public void onExecuteCommand(SyncBaseItem syncItem, BaseCommand baseCommand) {
        if (syncItem.equals(item.getSyncBaseItem()) && speechBubble != null) {
            speechBubble.close();
            speechBubble = null;
        }
    }

    @Override
    public void blink() {
        if (speechBubble == null) {
            return;
        }
        if (System.currentTimeMillis() > HtmlConstants.WAITING_FOR_BLINK + lastAction) {
            speechBubble.blink();
        }
    }

    @Override
    public boolean isAccomplished() {
        return task == Task.FINISHED;
    }

    @Override
    public long getAccomplishedTimeStamp() {
        return lastAction;
    }

    @Override
    public void close() {
        if (speechBubble != null) {
            speechBubble.close();
        }
    }
}
