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
import com.btxtech.game.jsre.common.gameengine.itemType.HarvesterType;
import com.btxtech.game.jsre.common.gameengine.services.collision.Path;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.MoneyCollectCommand;
import com.btxtech.game.jsre.common.packets.SyncItemInfo;

/**
 * User: beat
 * Date: 22.11.2009
 * Time: 13:30:50
 */
public class SyncHarvester extends SyncBaseAbility {
    private HarvesterType harvesterType;
    private Id target;
    private SyncMovable.OverlappingHandler overlappingHandler = new SyncMovable.OverlappingHandler() {
        @Override
        public Path calculateNewPath() {
            try {
                SyncResourceItem resource = (SyncResourceItem) getPlanetServices().getItemService().getItem(target);
                return recalculateNewPath(harvesterType.getRange(), resource.getSyncItemArea());
            } catch (ItemDoesNotExistException e) {
                stop();
                return null;
            }
        }
    };

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

        if (getSyncBaseItem().getSyncMovable().tickMove(factor, overlappingHandler)) {
            return true;
        }

        try {
            SyncResourceItem resource = (SyncResourceItem) getPlanetServices().getItemService().getItem(target);
            if (!isInRange(resource)) {
                if (isNewPathRecalculationAllowed()) {
                    // Destination place was may be taken. Calculate a new one.
                    recalculateAndSetNewPath(harvesterType.getRange(), resource.getSyncItemArea());
                    getPlanetServices().getConnectionService().sendSyncInfo(getSyncBaseItem());
                    return true;
                } else {
                    return false;
                }
            }
            getSyncItemArea().turnTo(resource);
            double money = resource.harvest(factor * harvesterType.getProgress());
            getPlanetServices().getBaseService().depositResource(money, getSyncBaseItem().getBase());
            return true;
        } catch (ItemDoesNotExistException ignore) {
            // Target may be empty
            stop();
            return false;
        } catch (TargetHasNoPositionException e) {
            // Target moved to a container
            stop();
            return false;
        }
    }

    public void stop() {
        target = null;
        getSyncBaseItem().getSyncMovable().stop();
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
        SyncResourceItem resource = (SyncResourceItem) getPlanetServices().getItemService().getItem(attackCommand.getTarget());

        this.target = resource.getId();
        setPathToDestinationIfSyncMovable(attackCommand.getPathToDestination());
    }

    public boolean isInRange(SyncResourceItem target) throws TargetHasNoPositionException {
        return getSyncItemArea().isInRange(harvesterType.getRange(), target);
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
