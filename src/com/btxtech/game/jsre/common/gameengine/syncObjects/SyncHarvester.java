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

import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.syncInfos.SyncItemInfo;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.MoneyCollectCommand;
import com.btxtech.game.jsre.common.gameengine.itemType.HarvesterType;

/**
 * User: beat
 * Date: 22.11.2009
 * Time: 13:30:50
 */
public class SyncHarvester extends SyncBaseAbility{
    private HarvesterType harvesterType;
    private Id target;

    public SyncHarvester(HarvesterType harvesterType, SyncBaseItem syncBaseItem) {
        super(syncBaseItem);
        this.harvesterType = harvesterType;
    }

    public boolean isActive() {
        return target != null;
    }

    public boolean tick(double factor) throws ItemDoesNotExistException {
        try {
            SyncResourceItem resource = (SyncResourceItem) getServices().getItemService().getItem(target);
            if (isTargetInRange(resource.getPosition(), harvesterType.getRange())) {
                if(getSyncBaseItem().hasSyncTurnable()) {
                 getSyncBaseItem().getSyncTurnable().turnTo(resource.getPosition());   
                }
                int money = resource.harvest(((int) (factor * harvesterType.getProgress())));
                getServices().getBaseService().depositResource(money, getSyncBaseItem().getBase());
            } else {
                getSyncBaseItem().getSyncMovable().tickMoveToTarget(factor, harvesterType.getRange(), resource.getPosition());
            }
            return true;
        } catch (ItemDoesNotExistException ignore) {
            // Target may be empty
            stop();
            return false;
        }
    }

    public void stop() {
        target = null;
    }

    @Override
    public void synchronize(SyncItemInfo syncItemInfo) {
        target = syncItemInfo.getTarget();
    }

    @Override
    public void fillSyncItemInfo(SyncItemInfo syncItemInfo) {
        syncItemInfo.setTarget(target);
    }

    public void executeCommand(MoneyCollectCommand attackCommand) throws ItemDoesNotExistException {
        SyncResourceItem resource = (SyncResourceItem) getServices().getItemService().getItem(attackCommand.getTarget());
        this.target = resource.getId();
    }

    public Id getTarget() {
        return target;
    }

    public void setTarget(Id target) {
        this.target = target;
    }
}
