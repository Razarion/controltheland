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

package com.btxtech.game.jsre.common.gameengine.syncObjects;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.itemType.ResourceType;
import com.btxtech.game.jsre.common.gameengine.services.GlobalServices;
import com.btxtech.game.jsre.common.gameengine.services.PlanetServices;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.packets.SyncItemInfo;

/**
 * User: beat
 * Date: 04.12.2009
 * Time: 20:08:41
 */
public class SyncResourceItem extends SyncItem {
    private double amount;
    private boolean missionMoney = false;

    public SyncResourceItem(Id id, Index position, ResourceType resourceType, GlobalServices globalServices, PlanetServices planetServices) {
        super(id, position, resourceType, globalServices, planetServices);
        amount = resourceType.getAmount();
    }

    public double harvest(double amount) {
        if (this.amount > amount) {
            this.amount -= amount;
            fireItemChanged(SyncItemListener.Change.RESOURCE, null);
            return amount;
        } else {
            amount = this.amount;
            this.amount = 0;
            getPlanetServices().getItemService().killSyncItem(this, null, false, false);
            return amount;
        }
    }

    @Override
    public void synchronize(SyncItemInfo syncItemInfo) throws NoSuchItemTypeException, ItemDoesNotExistException {
        super.synchronize(syncItemInfo);
        amount = syncItemInfo.getAmount();
    }

    @Override
    public SyncItemInfo getSyncInfo() {
        SyncItemInfo syncItemInfo = super.getSyncInfo();
        syncItemInfo.setAmount(amount);
        return syncItemInfo;
    }

    @Override
    public boolean isAlive() {
        return amount > 0;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public boolean isMissionMoney() {
        return missionMoney;
    }

    public void setMissionMoney(boolean missionMoney) {
        this.missionMoney = missionMoney;
    }
}
