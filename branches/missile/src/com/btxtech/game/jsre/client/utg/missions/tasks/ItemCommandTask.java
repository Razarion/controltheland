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

package com.btxtech.game.jsre.client.utg.missions.tasks;

import com.btxtech.game.jsre.client.ClientSyncItemView;
import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.utg.SpeechBubble;
import com.btxtech.game.jsre.client.utg.missions.MissionAportedException;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;

/**
 * User: beat
 * Date: 17.02.2010
 * Time: 20:03:23
 */
public class ItemCommandTask extends Task {
    private Class<? extends BaseCommand> commandClass;
    private ItemType targetItemType;
    private boolean commandReceived = false;

    public ItemCommandTask(String html, String targetItemTypeName, Class<? extends BaseCommand> commandClass) throws NoSuchItemTypeException {
        super(html);
        this.commandClass = commandClass;
        targetItemType = ItemContainer.getInstance().getItemType(targetItemTypeName);
    }

    @Override
    public String getName() {
        return "Item command: " + commandClass.getName();
    }

    @Override
    public void run() throws MissionAportedException {
        Index pos = getMission().getProtagonist().getSyncItem().getPosition();
        ClientSyncItemView targetItem = ItemContainer.getInstance().getFirstItemInRange(targetItemType, pos, Constants.TARGET_MAX_RANGE);
        if (targetItem == null) {
            throw new MissionAportedException("ItemCommandTask: no item type to attack found");
        }
        setSpeechBubble(new SpeechBubble(targetItem, getHtml(), false));
    }

    @Override
    public void onSyncItemDeactivated(SyncBaseItem syncBaseItem) {
        if (commandReceived) {
            activateNextTask();
        }
    }

    @Override
    public void onExecuteCommand(SyncBaseItem syncItem, BaseCommand baseCommand) {
        if (baseCommand.getClass().equals(commandClass)) {
            closeBubble();
            commandReceived = true;
        }
    }
}