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

import com.btxtech.game.jsre.common.gameengine.itemType.MovableType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.syncInfos.SyncItemInfo;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.MoveCommand;
import com.btxtech.game.jsre.client.common.Index;
import java.util.List;

/**
 * User: beat
 * Date: 18.11.2009
 * Time: 14:39:38
 */
public class SyncMovable extends SyncBaseAbility {
    private MovableType movableType;
    private List<Index> pathToDestination;

    public SyncMovable(MovableType movableType, SyncBaseItem syncBaseItem) {
        super(syncBaseItem);
        this.movableType = movableType;
    }

    public boolean isActive() {
        return pathToDestination != null && !pathToDestination.isEmpty();
    }

    public boolean tick(double factor) {
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

    public void tickMoveToTarget(double factor, int range, Index target) {
        if (pathToDestination != null && !pathToDestination.isEmpty()) {
            Index destination = pathToDestination.get(pathToDestination.size() - 1);
            if (!destination.isInRadius(target, range)) {
                pathToDestination = getServices().getTerrainService().setupPathToDestination(getSyncBaseItem().getPosition(), target, range);
            }
        } else {
            pathToDestination = getServices().getTerrainService().setupPathToDestination(getSyncBaseItem().getPosition(), target, range);
        }
        tick(factor);
    }

    @Override
    public void synchronize(SyncItemInfo syncItemInfo) {
        pathToDestination = syncItemInfo.getPathToDestination();
    }

    @Override
    public void fillSyncItemInfo(SyncItemInfo syncItemInfo) {
        syncItemInfo.setPathToDestination(pathToDestination);
    }

    public void stop() {
        pathToDestination = null;
    }

    public void executeCommand(MoveCommand moveCommand) {
        if (getSyncBaseItem().getPosition().equals(moveCommand.getDestination())) {
            return;
        }
        pathToDestination = getServices().getTerrainService().setupPathToDestination(getSyncBaseItem().getPosition(), moveCommand.getDestination());
    }

    public List<Index> getPathToDestination() {
        return pathToDestination;
    }

    public void setPathToDestination(List<Index> pathToDestination) {
        this.pathToDestination = pathToDestination;
    }
}
