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
import com.btxtech.game.jsre.client.Game;
import com.btxtech.game.jsre.client.cockpit.Group;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.utg.SpeechBubble;
import com.btxtech.game.jsre.client.utg.ClientUserTracker;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.google.gwt.user.client.ui.Widget;
import java.util.Collection;

/**
 * User: beat
 * Date: 26.01.2010
 * Time: 22:20:04
 */
public class BuildMission extends Mission {
    enum Task {
        WAITING_SELECTION,
        WAITING_BUILDING_FACTORY,
        FINISHED
    }

    private SpeechBubble speechBubble;
    private ClientSyncBaseItemView item;
    private Task task;
    private long lastAction;

    public BuildMission() {
        super("BuildMission");
    }

    public void start() throws MissionAportedException {
        Collection<ClientSyncBaseItemView> items = ItemContainer.getInstance().getOwnItems();

        item = null;
        for (ClientSyncBaseItemView clientSyncBaseItemView : items) {
            if (clientSyncBaseItemView.getSyncBaseItem().hasSyncBuilder()) {
                item = clientSyncBaseItemView;
            }
        }

        if (item == null) {
            throw new MissionAportedException("No builder item found");
        }
        
        scrollToItem(item);

        speechBubble = new SpeechBubble(item, HtmlConstants.BUILD_HTML1, false);
        task = Task.WAITING_SELECTION;
        ClientUserTracker.getInstance().onMissionTask(this, task);
        lastAction = System.currentTimeMillis();
    }

    public void onOwnSelectionChanged(Group selectedGroup) {
        if (task != Task.WAITING_SELECTION || speechBubble == null) {
            return;
        }
        lastAction = System.currentTimeMillis();
        speechBubble.blinkOff();
        task = Task.WAITING_BUILDING_FACTORY;
        ClientUserTracker.getInstance().onMissionTask(this, task);
        speechBubble.close();

        Widget widget = Game.cockpitPanel.getBuildupItemPanel().getItemTypesToBuild().values().iterator().next();
        int x = widget.getAbsoluteLeft() + widget.getOffsetWidth() / 2;
        int y = widget.getAbsoluteTop();
        speechBubble = new SpeechBubble(x, y, HtmlConstants.BUILD_HTML2, true);
    }

    public void onSyncItemDeactivated(SyncBaseItem syncBaseItem) {
    }

    @Override
    public void onExecuteCommand(SyncBaseItem syncItem, BaseCommand baseCommand) {
        if (syncItem.equals(item.getSyncBaseItem()) && speechBubble != null) {
            speechBubble.close();
            speechBubble = null;
        }
    }

    @Override
    public void onItemBuilt(ClientSyncBaseItemView clientSyncItemView) {
        if (clientSyncItemView.getSyncBaseItem().isReady() && !item.equals(clientSyncItemView) && item.isMyOwnProperty()) {
            if (task != Task.WAITING_BUILDING_FACTORY) {
                return;
            }
            task = Task.FINISHED;

            speechBubble = new SpeechBubble(clientSyncItemView, HtmlConstants.BUILD_HTML3, false);
            lastAction = System.currentTimeMillis();
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