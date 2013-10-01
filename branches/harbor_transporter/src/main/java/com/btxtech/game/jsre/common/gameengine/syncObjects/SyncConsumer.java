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

import com.btxtech.game.jsre.common.gameengine.itemType.ConsumerType;
import com.btxtech.game.jsre.common.packets.SyncItemInfo;

/**
 * User: beat
 * Date: 22.11.2009
 * Time: 13:30:50
 */
public class SyncConsumer extends SyncBaseAbility {
    private ConsumerType consumerType;
    private boolean consuming = false;
    private boolean operationState;

    public SyncConsumer(ConsumerType consumerType, SyncBaseItem syncBaseItem) {
        super(syncBaseItem);
        this.consumerType = consumerType;
    }

    @Override
    public void synchronize(SyncItemInfo syncItemInfo) {
        operationState = syncItemInfo.isOperationState();
    }

    @Override
    public void fillSyncItemInfo(SyncItemInfo syncItemInfo) {
        syncItemInfo.setOperationState(operationState);
    }

    public boolean isOperating() {
        return operationState;
    }

    public void setOperationState(boolean operationState) {
        this.operationState = operationState;
    }

    public void setConsuming(boolean consuming) {
        boolean oldState = this.consuming;
        this.consuming = consuming;
        if (oldState != consuming) {
            if (consuming) {
                getPlanetServices().getEnergyService().consumerActivated(this);
            } else {
                getPlanetServices().getEnergyService().consumerDeactivated(this);
            }
        }
    }

    public int getWattage() {
        return consumerType.getWattage();
    }

}