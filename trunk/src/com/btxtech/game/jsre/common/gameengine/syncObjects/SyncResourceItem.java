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
import com.btxtech.game.jsre.common.gameengine.itemType.ResourceType;
import com.btxtech.game.jsre.common.gameengine.services.Services;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.syncInfos.SyncItemInfo;

/**
 * User: beat
 * Date: 04.12.2009
 * Time: 20:08:41
 */
public class SyncResourceItem extends SyncItem {
    private int amount;
    private boolean missionMoney = false;

    public SyncResourceItem(Id id, Index position, ResourceType resourceType, Services services) {
        super(id, position, resourceType, services);
        amount = resourceType.getAmount();
    }

    public int harvest(int amount) {
        if (this.amount > amount) {
            this.amount -= amount;
            fireItemChanged(SyncItemListener.Change.RESOURCE);
            return amount;
        } else {
            amount = this.amount;
            this.amount = 0;
            getServices().getItemService().killBaseSyncObject(this, null);
            return amount;
        }
    }

    @Override
    public void synchronize(SyncItemInfo syncItemInfo) throws NoSuchItemTypeException {
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

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public boolean isMissionMoney() {
        return missionMoney;
    }

    public void setMissionMoney(boolean missionMoney) {
        this.missionMoney = missionMoney;
    }
}
