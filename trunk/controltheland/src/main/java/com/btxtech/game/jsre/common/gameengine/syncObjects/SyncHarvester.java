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
import com.btxtech.game.jsre.common.gameengine.itemType.HarvesterType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.MoneyCollectCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.syncInfos.SyncItemInfo;

/**
 * User: beat
 * Date: 22.11.2009
 * Time: 13:30:50
 */
public class SyncHarvester extends SyncBaseAbility {
    private HarvesterType harvesterType;
    private Id target;
    private Index destinationHint;
    private Double destinationAngel;

    public SyncHarvester(HarvesterType harvesterType, SyncBaseItem syncBaseItem) {
        super(syncBaseItem);
        this.harvesterType = harvesterType;
    }

    public boolean isActive() {
        return target != null;
    }

    public boolean tick(double factor) throws ItemDoesNotExistException {
        if (!getSyncBaseItem().isAlive()) {
            return false;
        }

        try {
            SyncResourceItem resource = (SyncResourceItem) getServices().getItemService().getItem(target);
            if ((destinationHint == null || getSyncItemArea().positionReached(destinationHint)) && getSyncItemArea().isInRange(harvesterType.getRange(), resource)) {
                getSyncItemArea().turnTo(resource);
                double money = resource.harvest(factor * harvesterType.getProgress());
                getServices().getBaseService().depositResource(money, getSyncBaseItem().getBase());
            } else {
                getSyncBaseItem().getSyncMovable().tickMoveToTarget(factor, destinationHint, destinationAngel, resource.getSyncItemArea().getPosition());
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
        destinationAngel = null;
        destinationHint = null;
        getSyncBaseItem().getSyncMovable().stop();
    }

    @Override
    public void synchronize(SyncItemInfo syncItemInfo) {
        target = syncItemInfo.getTarget();
        destinationHint = syncItemInfo.getDestinationHint();
        destinationAngel = syncItemInfo.getDestinationAngel();
    }

    @Override
    public void fillSyncItemInfo(SyncItemInfo syncItemInfo) {
        syncItemInfo.setTarget(target);
        syncItemInfo.setDestinationHint(destinationHint);
        syncItemInfo.setDestinationAngel(destinationAngel);
    }

    public void executeCommand(MoneyCollectCommand attackCommand) throws ItemDoesNotExistException {
        SyncResourceItem resource = (SyncResourceItem) getServices().getItemService().getItem(attackCommand.getTarget());

        if (!getServices().getTerritoryService().isAllowed(resource.getSyncItemArea().getPosition(), getSyncBaseItem())) {
            throw new IllegalArgumentException(this + " Collector not allowed to collect on territory: " + resource.getSyncItemArea().getPosition() + "  " + getSyncBaseItem());
        }

        this.target = resource.getId();
        destinationHint = attackCommand.getDestinationHint();
        destinationAngel = attackCommand.getDestinationAngel();
    }

    public Id getTarget() {
        return target;
    }

    public void setTarget(Id target) {
        this.target = target;
    }

    public HarvesterType getHarvesterType() {
        return harvesterType;
    }
}