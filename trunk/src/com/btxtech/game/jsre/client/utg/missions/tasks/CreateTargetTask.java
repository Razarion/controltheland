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

import com.btxtech.game.jsre.client.ClientSyncBaseItemView;
import com.btxtech.game.jsre.client.ClientSyncItemView;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;

/**
 * User: beat
 * Date: 17.02.2010
 * Time: 20:03:23
 */
public class CreateTargetTask extends Task {
    private ItemType itemType;
    private String itemTypeName;

    public CreateTargetTask(String itemTypeName) throws NoSuchItemTypeException {
        super(null);
        this.itemTypeName = itemTypeName;
        this.itemType = ItemContainer.getInstance().getItemType(itemTypeName);
    }

    @Override
    public String getName() {
        return "Waiting for target" + itemType.getName();
    }

    @Override
    public void run() {
        if (itemTypeName.equals(Constants.JEEP)) {
            Connection.getInstance().createMissionTraget(getMission().getProtagonist().getSyncBaseItem());
        } else if (itemTypeName.equals(Constants.MONEY)) {
            Connection.getInstance().createMissionMoney(getMission().getProtagonist().getSyncBaseItem());
        } else {
            throw new IllegalArgumentException(this + " unknwo itemTypeName " + itemTypeName);
        }
    }

    public boolean isTargetAccepted(ClientSyncItemView item) {
        if (item.getSyncItem().getPosition().getDistance(getMission().getProtagonist().getSyncBaseItem().getPosition()) > Constants.TARGET_MAX_RANGE) {
            return false;
        }

        if (!item.getSyncItem().getItemType().equals(itemType)) {
            return false;
        }

        if (item instanceof ClientSyncBaseItemView) {
            if (((ClientSyncBaseItemView) item).getSyncBaseItem().getBase().getName().equals(Constants.DUMMY_BASE_NAME)) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }
}