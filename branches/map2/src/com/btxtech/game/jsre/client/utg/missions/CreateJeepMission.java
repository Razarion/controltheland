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
import com.btxtech.game.jsre.client.ClientSyncItemView;
import com.btxtech.game.jsre.client.Game;
import com.btxtech.game.jsre.client.cockpit.Group;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.utg.SpeechBubble;
import com.btxtech.game.jsre.client.utg.ClientUserTracker;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.google.gwt.user.client.ui.Widget;
import java.util.Collection;
import java.util.Map;

/**
 * User: beat
 * Date: 26.01.2010
 * Time: 22:20:04
 */
public class CreateJeepMission extends Mission {
    enum Task {
        WAITING_FOR_SELECTION,
        WAITING_FOR_CREATION,
        FINISHED
    }
    private SpeechBubble speechBubble;
    private ClientSyncBaseItemView item;
    private Task task;
    private long lastAction;

    public CreateJeepMission() {
        super("CreateJeepMission");
    }

    public void start() throws MissionAportedException {
        Collection<ClientSyncBaseItemView> items = ItemContainer.getInstance().getOwnItems();

        item = null;
        for (ClientSyncBaseItemView clientSyncBaseItemView : items) {
            if (clientSyncBaseItemView.getSyncBaseItem().hasSyncFactory()) {
                item = clientSyncBaseItemView;
            }
        }

        if (item == null) {
            throw new MissionAportedException("No Factory found");
        }

        scrollToItem(item);

        speechBubble = new SpeechBubble(item, HtmlConstants.CREATE_JEEP_HTML1, false);
        task = Task.WAITING_FOR_SELECTION;
        ClientUserTracker.getInstance().onMissionTask(this, task);
        lastAction = System.currentTimeMillis();
    }

    public void onOwnSelectionChanged(Group selectedGroup) throws MissionAportedException {
        if (task != Task.WAITING_FOR_SELECTION) {
            return;
        }
        lastAction = System.currentTimeMillis();
        speechBubble.blinkOff();
        task = Task.WAITING_FOR_CREATION;
        ClientUserTracker.getInstance().onMissionTask(this, task);
        speechBubble.close();

        Widget widget = null;
        for (Map.Entry<ItemType, Widget> itemTypeWidgetEntry : Game.cockpitPanel.getBuildupItemPanel().getItemTypesToBuild().entrySet()) {
            if(itemTypeWidgetEntry.getKey() instanceof BaseItemType && ((BaseItemType)itemTypeWidgetEntry.getKey()).getWeaponType() != null) {
               widget = itemTypeWidgetEntry.getValue();
            }
        }
        if(widget == null) {
            throw new MissionAportedException("No Jeep found");
        }
        int x = widget.getAbsoluteLeft() + widget.getOffsetWidth() / 2;
        int y = widget.getAbsoluteTop();
        speechBubble = new SpeechBubble(x, y, HtmlConstants.CREATE_JEEP_HTML2, true);
    }

    public void onSyncItemDeactivated(SyncBaseItem syncBaseItem) {
    }

    @Override
    public void onExecuteCommand(SyncBaseItem syncItem, BaseCommand baseCommand) {
        speechBubble.close();
    }

    @Override
    public void onItemCreated(ClientSyncItemView item) {
        if (item instanceof ClientSyncBaseItemView && ((ClientSyncBaseItemView) item).isMyOwnProperty()) {
            if (task != Task.WAITING_FOR_CREATION) {
                return;
            }
            task = Task.FINISHED;

            speechBubble = new SpeechBubble(item, HtmlConstants.CREATE_JEEP_HTML3, false);
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