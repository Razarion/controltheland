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
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.InsufficientFundsException;
import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.PositionTakenException;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.BuilderType;
import com.btxtech.game.jsre.common.gameengine.services.base.HouseSpaceExceededException;
import com.btxtech.game.jsre.common.gameengine.services.base.ItemLimitExceededException;
import com.btxtech.game.jsre.common.gameengine.services.collision.Path;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BuilderCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BuilderFinalizeCommand;
import com.btxtech.game.jsre.common.packets.SyncItemInfo;

/**
 * User: beat
 * Date: 21.11.2009
 * Time: 23:53:39
 */
public class SyncBuilder extends SyncBaseAbility {
    private BuilderType builderType;
    private SyncBaseItem currentBuildup;
    private Index toBeBuildPosition;
    private BaseItemType toBeBuiltType;
    private SyncMovable.OverlappingHandler overlappingHandler = new SyncMovable.OverlappingHandler() {
        @Override
        public Path calculateNewPath() {
            return recalculateNewPath(builderType.getRange(), getTargetSyncItemArea());
        }
    };

    public SyncBuilder(BuilderType builderType, SyncBaseItem syncBaseItem) {
        super(syncBaseItem);
        this.builderType = builderType;
    }

    public boolean isActive() {
        return toBeBuildPosition != null && toBeBuiltType != null;
    }

    public synchronized boolean tick(double factor) throws NoSuchItemTypeException {
        if (toBeBuildPosition == null || toBeBuiltType == null) {
            return false;
        }

        if (getSyncBaseItem().getSyncMovable().tickMove(factor, overlappingHandler)) {
            return true;
        }

        if (!isInRange()) {
            // Destination place was may be taken. Calculate a new one.
            if (isNewPathRecalculationAllowed()) {
                SyncItemArea syncItemArea = getTargetSyncItemArea();
                recalculateAndSetNewPath(builderType.getRange(), syncItemArea);
                getPlanetServices().getConnectionService().sendSyncInfo(getSyncBaseItem());
                return true;
            } else {
                return false;
            }
        }

        if (currentBuildup == null) {
            if (getPlanetServices().getConnectionService().getGameEngineMode() != GameEngineMode.MASTER) {
                // Wait for server to create currentBuildup
                return true;
            }
            if (toBeBuiltType == null || toBeBuildPosition == null) {
                throw new IllegalArgumentException("Invalid attributes |" + toBeBuiltType + "|" + toBeBuildPosition);
            }
            getPlanetServices().getItemService().checkBuildingsInRect(toBeBuiltType, toBeBuildPosition);
            try {
                currentBuildup = (SyncBaseItem) getPlanetServices().getItemService().createSyncObject(toBeBuiltType, toBeBuildPosition, getSyncBaseItem(), getSyncBaseItem().getBase());
                getPlanetServices().getConnectionService().sendSyncInfo(getSyncBaseItem());
            } catch (ItemLimitExceededException e) {
                stop();
                return false;
            } catch (HouseSpaceExceededException e) {
                stop();
                return false;
            }
        }
        getSyncItemArea().turnTo(toBeBuildPosition);
        if (getPlanetServices().getItemService().baseObjectExists(currentBuildup)) {
            double buildFactor = setupBuildFactor(factor, builderType.getProgress(), toBeBuiltType, currentBuildup);
            try {
                getPlanetServices().getBaseService().withdrawalMoney(buildFactor * (double) toBeBuiltType.getPrice(), getSyncBaseItem().getBase());
                currentBuildup.addBuildup(buildFactor);
                if (currentBuildup.isReady()) {
                    stop();
                    return false;
                }
                getSyncBaseItem().fireItemChanged(SyncItemListener.Change.FACTORY_PROGRESS, null);
                return true;
            } catch (InsufficientFundsException e) {
                return true;
            }
        } else {
            // It has may be killed
            stop();
            return false;
        }
    }

    private SyncItemArea getTargetSyncItemArea() {
        if (currentBuildup != null) {
            return currentBuildup.getSyncItemArea();
        } else {
            return toBeBuiltType.getBoundingBox().createSyntheticSyncItemArea(toBeBuildPosition);
        }
    }

    public static double setupBuildFactor(double factor, double builderProgress, BaseItemType toBeBuilt, SyncBaseItem currentBuildup) {
        double buildFactor = factor * builderProgress / (double) toBeBuilt.getBuildup();
        if (buildFactor + currentBuildup.getBuildup() > 1.0) {
            buildFactor = 1.0 - currentBuildup.getBuildup();
        }
        return buildFactor;
    }

    private boolean isInRange() {
        return getSyncItemArea().isInRange(builderType.getRange(), toBeBuildPosition, toBeBuiltType);
    }

    public synchronized void stop() {
        if (currentBuildup != null) {
            getPlanetServices().getConnectionService().sendSyncInfo(currentBuildup);
        }
        currentBuildup = null;
        toBeBuiltType = null;
        toBeBuildPosition = null;
        getSyncBaseItem().getSyncMovable().stop();
        getSyncBaseItem().fireItemChanged(SyncItemListener.Change.FACTORY_PROGRESS, null);
    }

    @Override
    public void synchronize(SyncItemInfo syncItemInfo) throws NoSuchItemTypeException, ItemDoesNotExistException {
        toBeBuildPosition = syncItemInfo.getToBeBuildPosition();
        if (syncItemInfo.getToBeBuiltTypeId() != null) {
            toBeBuiltType = (BaseItemType) getGlobalServices().getItemTypeService().getItemType(syncItemInfo.getToBeBuiltTypeId());
        } else {
            toBeBuiltType = null;
        }
        Id currentBuildupId = syncItemInfo.getCurrentBuildup();
        if (currentBuildupId != null) {
            currentBuildup = (SyncBaseItem) getPlanetServices().getItemService().getItem(currentBuildupId);
        } else {
            currentBuildup = null;
        }
    }

    @Override
    public void fillSyncItemInfo(SyncItemInfo syncItemInfo) {
        syncItemInfo.setToBeBuildPosition(Index.saveCopy(toBeBuildPosition));
        if (toBeBuiltType != null) {
            syncItemInfo.setToBeBuiltTypeId(toBeBuiltType.getId());
        }
        if (currentBuildup != null) {
            syncItemInfo.setCurrentBuildup(currentBuildup.getId());
        }
    }

    public void executeCommand(BuilderCommand builderCommand) throws NoSuchItemTypeException {
        if (!builderType.isAbleToBuild(builderCommand.getToBeBuilt())) {
            throw new IllegalArgumentException(this + " can not build: " + builderCommand.getToBeBuilt());
        }
        BaseItemType tmpToBeBuiltType = (BaseItemType) getGlobalServices().getItemTypeService().getItemType(builderCommand.getToBeBuilt());
        if (getGlobalServices().getUnlockService().isItemLocked(tmpToBeBuiltType, getSyncBaseItem().getBase())) {
            throw new IllegalArgumentException(this + " item is locked: " + builderCommand.getToBeBuilt());
        }
        if (!getPlanetServices().getTerrainService().isFree(builderCommand.getPositionToBeBuilt(), tmpToBeBuiltType)) {
            throw new PositionTakenException(builderCommand.getPositionToBeBuilt(), builderCommand.getToBeBuilt());
        }

        toBeBuiltType = tmpToBeBuiltType;
        toBeBuildPosition = builderCommand.getPositionToBeBuilt();
        setPathToDestinationIfSyncMovable(builderCommand.getPathToDestination());
    }

    public synchronized void executeCommand(BuilderFinalizeCommand builderFinalizeCommand) throws NoSuchItemTypeException, ItemDoesNotExistException {
        SyncBaseItem syncBaseItem = (SyncBaseItem) getPlanetServices().getItemService().getItem(builderFinalizeCommand.getToBeBuilt());
        if (!builderType.isAbleToBuild(syncBaseItem.getItemType().getId())) {
            throw new IllegalArgumentException(this + " can not build: " + builderFinalizeCommand.getToBeBuilt());
        }

        currentBuildup = syncBaseItem;
        toBeBuiltType = syncBaseItem.getBaseItemType();
        toBeBuildPosition = syncBaseItem.getSyncItemArea().getPosition();
        setPathToDestinationIfSyncMovable(builderFinalizeCommand.getPathToDestination());
    }

    public Index getToBeBuildPosition() {
        return toBeBuildPosition;
    }

    public BaseItemType getToBeBuiltType() {
        return toBeBuiltType;
    }

    public SyncBaseItem getCurrentBuildup() {
        return currentBuildup;
    }

    public synchronized void setCurrentBuildup(SyncBaseItem syncBaseItem) {
        this.currentBuildup = syncBaseItem;
    }

    public void setToBeBuildPosition(Index toBeBuildPosition) {
        this.toBeBuildPosition = toBeBuildPosition;
    }

    public void setToBeBuiltType(BaseItemType toBeBuiltType) {
        this.toBeBuiltType = toBeBuiltType;
    }

    public BuilderType getBuilderType() {
        return builderType;
    }
}
