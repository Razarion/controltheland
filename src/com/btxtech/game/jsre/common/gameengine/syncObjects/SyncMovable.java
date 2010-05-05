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
import com.btxtech.game.jsre.common.gameengine.itemType.MovableType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.MoveCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.PutContainCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.syncInfos.SyncItemInfo;
import java.util.List;

/**
 * User: beat
 * Date: 18.11.2009
 * Time: 14:39:38
 */
public class SyncMovable extends SyncBaseAbility {
    private MovableType movableType;
    private List<Index> pathToDestination;
    private Id targetContainer;

    public SyncMovable(MovableType movableType, SyncBaseItem syncBaseItem) {
        super(syncBaseItem);
        this.movableType = movableType;
    }

    public boolean isActive() {
        return targetContainer != null || (pathToDestination != null && !pathToDestination.isEmpty());
    }

    public boolean tick(double factor) {
        if (targetContainer != null) {
            return tickMoveToContainer(factor);
        } else {
            return tickMove(factor);
        }
    }

    private boolean tickMove(double factor) {
        if (pathToDestination == null || pathToDestination.isEmpty()) {
            pathToDestination = null;
            // no new destination
            return false;
        }

        Index destination = pathToDestination.get(0);
        if (destination.equals(getSyncBaseItem().getPosition())) {
            pathToDestination.remove(0);
            if (pathToDestination.isEmpty()) {
                pathToDestination = null;
                return false;
            } else {
                destination = pathToDestination.get(0);
            }
        }

        Index pos = getStepToDestination(factor, destination);
        boolean destinationReached = pos.equals(getSyncBaseItem().getPosition()) && pathToDestination.isEmpty();
        if (destinationReached) {
            pathToDestination = null;
        }
        if (getSyncBaseItem().hasSyncTurnable()) {
            getSyncBaseItem().getSyncTurnable().turnTo(pos);
        }
        getSyncBaseItem().setPosition(pos);
        return !destinationReached;
    }

    public void tickMoveToTarget(double factor, int range, Index target) {
        if (pathToDestination != null && !pathToDestination.isEmpty()) {
            Index destination = pathToDestination.get(pathToDestination.size() - 1);
            if (!destination.isInRadius(target, range)) {
                pathToDestination = getServices().getTerrainService().setupPathToDestination(getSyncBaseItem().getPosition(), target, range, getSyncBaseItem().getTerrainType());
                getServices().getConnectionService().sendSyncInfo(getSyncBaseItem());
            }
        } else {
            pathToDestination = getServices().getTerrainService().setupPathToDestination(getSyncBaseItem().getPosition(), target, range, getSyncBaseItem().getTerrainType());
            getServices().getConnectionService().sendSyncInfo(getSyncBaseItem());
        }
        tickMove(factor);
    }


    private boolean tickMoveToContainer(double factor) {
        try {
            SyncBaseItem syncItemContainer = (SyncBaseItem) getServices().getItemService().getItem(targetContainer);
            if (isTargetInRange(syncItemContainer.getPosition(), syncItemContainer.getSyncItemContainer().getRange())) {
                if (getSyncBaseItem().hasSyncTurnable()) {
                    getSyncBaseItem().getSyncTurnable().turnTo(syncItemContainer.getPosition());
                }
                syncItemContainer.getSyncItemContainer().put(getSyncBaseItem());
                stop();
                return false;
            } else {
                tickMoveToTarget(factor, syncItemContainer.getSyncItemContainer().getRange(), syncItemContainer.getPosition());
                return true;
            }
        } catch (ItemDoesNotExistException ignore) {
            // Item container may be killed
            stop();
            return false;
        }
    }

    private Index getStepToDestination(double factor, Index destination) {
        int deltaX = destination.getX() - getSyncBaseItem().getPosition().getX();
        int absDeltaX = Math.abs(deltaX);
        int newX = getSyncBaseItem().getPosition().getX();
        if (absDeltaX > getSpeed(factor)) {
            absDeltaX = getSpeed(factor);
        }
        if (deltaX > 0) {
            newX += absDeltaX;
        } else if (deltaX < 0) {
            newX -= absDeltaX;
        }

        int deltaY = destination.getY() - getSyncBaseItem().getPosition().getY();
        int absDeltaY = Math.abs(deltaY);
        int newY = getSyncBaseItem().getPosition().getY();
        if (absDeltaY > getSpeed(factor)) {
            absDeltaY = getSpeed(factor);
        }
        if (deltaY > 0) {
            newY += absDeltaY;
        } else if (deltaY < 0) {
            newY -= absDeltaY;
        }

        return new Index(newX, newY);
    }

    private int getSpeed(double factor) {
        return (int) Math.round(movableType.getSpeed() * factor);
    }

    @Override
    public void synchronize(SyncItemInfo syncItemInfo) {
        pathToDestination = syncItemInfo.getPathToDestination();
        targetContainer = syncItemInfo.getTargetContainer();
    }

    @Override
    public void fillSyncItemInfo(SyncItemInfo syncItemInfo) {
        syncItemInfo.setPathToDestination(pathToDestination);
        syncItemInfo.setTargetContainer(targetContainer);
    }

    public void stop() {
        pathToDestination = null;
        targetContainer = null;
    }

    public void executeCommand(MoveCommand moveCommand) {
        if (getSyncBaseItem().getPosition().equals(moveCommand.getDestination())) {
            return;
        }
        pathToDestination = getServices().getTerrainService().setupPathToDestination(getSyncBaseItem().getPosition(), moveCommand.getDestination(), getSyncBaseItem().getTerrainType());
    }

    public void executeCommand(PutContainCommand putContainCommand) {
        if (putContainCommand.getId().equals(putContainCommand.getItemContainer())) {
            throw new IllegalArgumentException("Can not contain oneself: " + getSyncBaseItem());
        }
        targetContainer = putContainCommand.getItemContainer();
    }

    public List<Index> getPathToDestination() {
        return pathToDestination;
    }

    public void setPathToDestination(List<Index> pathToDestination) {
        this.pathToDestination = pathToDestination;
    }
}
