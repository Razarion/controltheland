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

import com.btxtech.game.jsre.client.common.DecimalPosition;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.formation.AttackFormationItem;
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
    //private Double destinationAngel;

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
            return tickMove(factor, null);
        }
    }

    private boolean tickMove(double factor, Double destinationAngel) {
        if (pathToDestination == null || pathToDestination.isEmpty()) {
            pathToDestination = null;
            // no new destination
            return onFinished();
        }

        Index destination = pathToDestination.get(0);

        DecimalPosition decimalPoint = getSyncItemArea().getDecimalPosition().getPointWithDistance(getSpeed(factor), destination, false);
        if (decimalPoint.isSame(destination)) {
            pathToDestination.remove(0);
            if (pathToDestination.isEmpty()) {
                pathToDestination = null;
                if (destinationAngel != null) {
                    getSyncItemArea().turnTo(destinationAngel);
                } else {
                    getSyncItemArea().turnTo(destination);
                }
                getSyncItemArea().setDecimalPosition(decimalPoint);
                return onFinished();
            }
        }

        double realDistance = decimalPoint.getDistance(getSyncItemArea().getDecimalPosition());
        double relativeDistance = realDistance / (double) movableType.getSpeed();
        if (factor - relativeDistance > DecimalPosition.FACTOR) {
            getSyncItemArea().turnTo(destination);
            getSyncItemArea().setDecimalPosition(decimalPoint);
            return tickMove(factor - relativeDistance, destinationAngel);
        }

        getSyncItemArea().turnTo(destination);
        getSyncItemArea().setDecimalPosition(decimalPoint);
        return true;
    }

    public boolean onFinished() {
        // TODO throw exception and handle in other sync abilities (e.g. SyncWeapon)
        SyncBaseItem syncBaseItem = getSyncBaseItem();
        if (getServices().getItemService().isSyncItemOverlapping(syncBaseItem)) {
            pathToDestination = getServices().getTerrainService().setupPathToSyncMovableRandomPositionIfTaken(syncBaseItem);
            if (pathToDestination != null) {
                getServices().getConnectionService().sendSyncInfo(getSyncBaseItem());
                return true;
            }
        }
        return false;
    }

    public boolean tickMoveToTarget(double factor, Index destinationHint, Double destinationAngel, Index target) {
        if (pathToDestination == null || pathToDestination.isEmpty()) {
            setupPathToDestination(target, destinationHint);
            getServices().getConnectionService().sendSyncInfo(getSyncBaseItem());
        }
        return tickMove(factor, destinationAngel);
    }

    private void setupPathToDestination(Index target, Index destinationHint) {
        if (destinationHint != null) {
            pathToDestination = getServices().getTerrainService().setupPathToDestination(getSyncBaseItem().getSyncItemArea().getPosition(), destinationHint, getSyncBaseItem().getTerrainType());
        } else {
            // Will only be called on the client. Remove if path finding is moved to the client
            pathToDestination = getServices().getTerrainService().setupPathToDestination(getSyncBaseItem().getSyncItemArea().getPosition(), target, getSyncBaseItem().getTerrainType());
        }
    }

    private boolean tickMoveToContainer(double factor) {
        try {
            SyncBaseItem syncItemContainer = (SyncBaseItem) getServices().getItemService().getItem(targetContainer);
            if (getSyncItemArea().isInRange(syncItemContainer.getSyncItemContainer().getRange(), syncItemContainer)) {
                getSyncItemArea().turnTo(syncItemContainer);
                syncItemContainer.getSyncItemContainer().load(getSyncBaseItem());
                stop();
                return false;
            } else {
                if (destinationHintTargetContainer == null) {
                    AttackFormationItem formation = getServices().getCollisionService().getDestinationHint(getSyncBaseItem(),
                            syncItemContainer.getSyncItemContainer().getRange(),
                            syncItemContainer.getSyncItemArea(),
                            syncItemContainer.getTerrainType());
                    if (formation != null) {
                        destinationHintTargetContainer = formation.getDestinationHint();
                    }
                    if (destinationHintTargetContainer == null) {
                        stop();
                    }

                }
                tickMoveToTarget(factor, destinationHintTargetContainer, null, syncItemContainer.getSyncItemArea().getPosition());
                return true;
            }
        } catch (ItemDoesNotExistException ignore) {
            // Item container may be killed
            stop();
            return false;
        }
    }

    public boolean isLoadPosReachable(SyncItemContainer syncItemContainer) {
        try {
            getServices().getTerrainService().getNearestPoint(getSyncBaseItem().getTerrainType(),
                    syncItemContainer.getSyncItemArea().getPosition(),
                    syncItemContainer.getItemContainerType().getRange());
            return true;
        } catch (IllegalArgumentException ignore) {
            return false;
        }
    }

    private double getSpeed(double factor) {
        return (double) movableType.getSpeed() * factor;
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
        if (getSyncBaseItem().getSyncItemArea().positionReached(moveCommand.getDestination())) {
            return;
        }
        Index destination = getServices().getTerrainService().correctPosition(getSyncBaseItem(), moveCommand.getDestination());
        pathToDestination = getServices().getTerrainService().setupPathToDestination(getSyncBaseItem().getSyncItemArea().getPosition(), destination, getSyncBaseItem().getTerrainType());
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

    public Id getTargetContainer() {
        return targetContainer;
    }

    public void setTargetContainer(Id targetContainer) {
        this.targetContainer = targetContainer;
    }

    public MovableType getMovableType() {
        return movableType;
    }
}
