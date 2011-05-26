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
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.LoadContainCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.MoveCommand;
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
    private Index destinationHintTargetContainer;

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
            return onFinished();
        }

        Index destination = pathToDestination.get(0);
        if (destination.equals(getSyncBaseItem().getPosition())) {
            pathToDestination.remove(0);
            if (pathToDestination.isEmpty()) {
                pathToDestination = null;
                return onFinished();
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
        return !destinationReached || onFinished();
    }

    public boolean onFinished() {
        SyncBaseItem syncBaseItem = getSyncBaseItem();
        if (getServices().getItemService().hasStandingItemsInRect(syncBaseItem.getRectangle(), getSyncBaseItem())) {
            pathToDestination = getServices().getTerrainService().setupPathToSyncMovableRandomPositionIfTaken(syncBaseItem);
            if (pathToDestination != null) {
                getServices().getConnectionService().sendSyncInfo(getSyncBaseItem());
                return true;
            }
        }
        return false;
    }

    public void tickMoveToTarget(double factor, Index destinationHint, Index target) {
        if (pathToDestination == null || pathToDestination.isEmpty()) {
            setupPathToDestination(target, destinationHint);
            getServices().getConnectionService().sendSyncInfo(getSyncBaseItem());
        }
        tickMove(factor);
    }

    private void setupPathToDestination(Index target, Index destinationHint) {
        if (destinationHint != null) {
            pathToDestination = getServices().getTerrainService().setupPathToDestination(getSyncBaseItem().getPosition(), destinationHint, getSyncBaseItem().getTerrainType());
        } else {
            // Will only be called on the client. Remove if path finding is moved to the client
            pathToDestination = getServices().getTerrainService().setupPathToDestination(getSyncBaseItem().getPosition(), target, getSyncBaseItem().getTerrainType());
        }
    }

    private boolean tickMoveToContainer(double factor) {
        try {
            SyncBaseItem syncItemContainer = (SyncBaseItem) getServices().getItemService().getItem(targetContainer);
            if (isTargetInRange4Container(syncItemContainer.getPosition(), syncItemContainer.getSyncItemContainer().getRange()
                    + getSyncBaseItem().getBaseItemType().getRadius()
                    + syncItemContainer.getBaseItemType().getRadius())) {
                if (getSyncBaseItem().hasSyncTurnable()) {
                    getSyncBaseItem().getSyncTurnable().turnTo(syncItemContainer.getPosition());
                }
                syncItemContainer.getSyncItemContainer().load(getSyncBaseItem());
                stop();
                return false;
            } else {
                if (destinationHintTargetContainer == null) {
                    destinationHintTargetContainer = getServices().getCollisionService().getDestinationHint(getSyncBaseItem(), syncItemContainer.getSyncItemContainer().getRange(), syncItemContainer, syncItemContainer.getPosition());
                    if (destinationHintTargetContainer == null) {
                        stop();
                    }

                }
                tickMoveToTarget(factor, destinationHintTargetContainer, syncItemContainer.getPosition());
                return true;
            }
        } catch (ItemDoesNotExistException ignore) {
            // Item container may be killed
            stop();
            return false;
        }
    }

    public boolean isTargetInRange4Container(Index targetPos, int range) {
        return getSyncBaseItem().getPosition().isInRadius(targetPos, range) && (pathToDestination == null || pathToDestination.isEmpty());
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
        destinationHintTargetContainer = null;
    }

    public void executeCommand(MoveCommand moveCommand) {
        if (getSyncBaseItem().getPosition().equals(moveCommand.getDestination())) {
            return;
        }
        Index destination = getServices().getTerrainService().correctPosition(getSyncBaseItem(), moveCommand.getDestination());
        pathToDestination = getServices().getTerrainService().setupPathToDestination(getSyncBaseItem().getPosition(), destination, getSyncBaseItem().getTerrainType());
    }

    public void executeCommand(LoadContainCommand loadContainCommand) {
        if (loadContainCommand.getId().equals(loadContainCommand.getItemContainer())) {
            throw new IllegalArgumentException("Can not contain oneself: " + getSyncBaseItem());
        }
        targetContainer = loadContainCommand.getItemContainer();
    }

    public List<Index> getPathToDestination() {
        return pathToDestination;
    }

    public void setPathToDestination(List<Index> pathToDestination) {
        this.pathToDestination = pathToDestination;
    }

    public Index getDestination() {
        if (pathToDestination != null && !pathToDestination.isEmpty()) {
            return pathToDestination.get(pathToDestination.size() - 1);
        }
        return null;
    }

    public boolean isLoadPosReachable(SyncItemContainer syncItemContainer) {
        try {
            getServices().getTerrainService().getNearestPoint(getSyncBaseItem().getTerrainType(),
                    syncItemContainer.getSyncBaseItem().getPosition(),
                    syncItemContainer.getItemContainerType().getRange());
            return true;
        } catch (IllegalArgumentException ignore) {
            return false;
        }
    }

    public Id getTargetContainer() {
        return targetContainer;
    }

    public void setTargetContainer(Id targetContainer) {
        this.targetContainer = targetContainer;
    }
}
