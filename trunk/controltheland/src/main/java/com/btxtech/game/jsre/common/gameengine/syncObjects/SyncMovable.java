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

import com.btxtech.game.jsre.client.GameEngineMode;
import com.btxtech.game.jsre.client.common.DecimalPosition;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.CommonJava;
import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.itemType.MovableType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.LoadContainCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.PathToDestinationCommand;
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
    private Double destinationAngel;
    private Id targetContainer;

    public SyncMovable(MovableType movableType, SyncBaseItem syncBaseItem) {
        super(syncBaseItem);
        this.movableType = movableType;
    }

    public boolean isActive() {
        return getSyncBaseItem().isAlive() && (targetContainer != null || (pathToDestination != null && !pathToDestination.isEmpty()));
    }

    /**
     * @param factor time in s since the last ticks
     * @return true if more tick are needed to fulfil the job
     */
    public boolean tick(double factor) {
        return tickMove(factor) || targetContainer != null && putInContainer();

    }

    boolean tickMove(double factor) {
        if (pathToDestination == null) {
            return false;
        }

        if (pathToDestination.isEmpty()) {
            pathToDestination = null;
            // no new destination
            return onFinished();
        }

        Index destination = pathToDestination.get(0);

        DecimalPosition decimalPoint = getSyncItemArea().getDecimalPosition().getPointWithDistance(getDistance(factor), destination, false);
        if (decimalPoint.isSame(destination)) {
            pathToDestination.remove(0);
            if (pathToDestination.isEmpty()) {
                pathToDestination = null;
                getSyncItemArea().turnTo(destinationAngel);
                getSyncItemArea().setDecimalPosition(decimalPoint);
                return onFinished();
            }
        }

        double realDistance = decimalPoint.getDistance(getSyncItemArea().getDecimalPosition());
        double relativeDistance = realDistance / (double) movableType.getSpeed();
        if (factor - relativeDistance > DecimalPosition.FACTOR) {
            getSyncItemArea().turnTo(destination);
            getSyncItemArea().setDecimalPosition(decimalPoint);
            return tickMove(factor - relativeDistance);
        }

        getSyncItemArea().turnTo(destination);
        getSyncItemArea().setDecimalPosition(decimalPoint);
        return true;
    }

    public boolean onFinished() {
        if (getServices().getConnectionService().getGameEngineMode() != GameEngineMode.MASTER) {
            return false;
        }
        SyncBaseItem syncBaseItem = getSyncBaseItem();
        if (getServices().getItemService().isSyncItemOverlapping(syncBaseItem)) {
            pathToDestination = getServices().getCollisionService().setupPathToSyncMovableRandomPositionIfTaken(syncBaseItem);
            if (pathToDestination != null) {
                int size = pathToDestination.size();
                if (pathToDestination.size() < 2) {
                    destinationAngel = getSyncItemArea().getPosition().getAngleToNord(pathToDestination.get(size - 1));
                } else {
                    destinationAngel = pathToDestination.get(size - 2).getAngleToNord(pathToDestination.get(size - 1));
                }
                getServices().getConnectionService().sendSyncInfo(getSyncBaseItem());
                return true;
            } else {
                throw new NullPointerException("Position is null " + getSyncBaseItem());
            }
        }
        return false;
    }

    private boolean putInContainer() {
        try {
            SyncBaseItem syncItemContainer = (SyncBaseItem) getServices().getItemService().getItem(targetContainer);
            if (getSyncItemArea().isInRange(syncItemContainer.getSyncItemContainer().getRange(), syncItemContainer)) {
                getSyncItemArea().turnTo(syncItemContainer);
                syncItemContainer.getSyncItemContainer().load(getSyncBaseItem());
            } else {
                throw new IllegalStateException("Not in item container range: " + getSyncBaseItem() + " container: " + syncItemContainer);
            }
        } catch (ItemDoesNotExistException ignore) {
            // Item container may be killed
        } catch (ItemContainerFullException e) {
            // Item container full
        }
        stop();
        return false;
    }

    private double getDistance(double factor) {
        return (double) movableType.getSpeed() * factor;
    }

    @Override
    public void synchronize(SyncItemInfo syncItemInfo) {
        pathToDestination = syncItemInfo.getPathToDestination();
        targetContainer = syncItemInfo.getTargetContainer();
        destinationAngel = syncItemInfo.getDestinationAngel();
    }

    @Override
    public void fillSyncItemInfo(SyncItemInfo syncItemInfo) {
        syncItemInfo.setPathToDestination(CommonJava.saveArrayListCopy(pathToDestination));
        syncItemInfo.setDestinationAngel(destinationAngel);
        syncItemInfo.setTargetContainer(targetContainer);
    }

    public void stop() {
        pathToDestination = null;
        targetContainer = null;
        destinationAngel = null;
    }

    public void executeCommand(PathToDestinationCommand pathToDestinationCommand) {
        if (getSyncBaseItem().getSyncItemArea().positionReached(pathToDestinationCommand.getDestination())) {
            return;
        }
        pathToDestination = pathToDestinationCommand.getPathToDestination();
        destinationAngel = pathToDestinationCommand.getDestinationAngel();
    }

    public void executeCommand(LoadContainCommand loadContainCommand) {
        if (loadContainCommand.getId().equals(loadContainCommand.getItemContainer())) {
            throw new IllegalArgumentException("Can not contain oneself: " + getSyncBaseItem());
        }
        targetContainer = loadContainCommand.getItemContainer();
        pathToDestination = null;
        destinationAngel = null;
    }

    public List<Index> getPathToDestination() {
        return pathToDestination;
    }

    public Double getDestinationAngel() {
        return destinationAngel;
    }

    public void setPathToDestination(List<Index> pathToDestination, Double destinationAngel) {
        this.pathToDestination = pathToDestination;
        this.destinationAngel = destinationAngel;
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
