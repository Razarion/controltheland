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
import com.btxtech.game.jsre.common.InsufficientFundsException;
import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.PositionTakenException;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.BuilderType;
import com.btxtech.game.jsre.common.gameengine.services.base.HouseSpaceExceededException;
import com.btxtech.game.jsre.common.gameengine.services.base.ItemLimitExceededException;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BuilderCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BuilderFinalizeCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.syncInfos.SyncItemInfo;

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
    private int createdChildCount;
    private Double destinationAngel;

    public SyncBuilder(BuilderType builderType, SyncBaseItem syncBaseItem) {
        super(syncBaseItem);
        this.builderType = builderType;
    }

    public boolean isActive() {
        return toBeBuildPosition != null && toBeBuiltType != null;
    }

    public boolean tick(double factor) throws NoSuchItemTypeException {
        if (toBeBuildPosition == null || toBeBuiltType == null) {
            return false;
        }

        if (getSyncBaseItem().getSyncMovable().tickMove(factor, destinationAngel)) {
            return true;
        }

        if (!isInRange()) {
            // Destination place was may be taken. Calculate a new one.
            if (isNewPathRecalculationAllowed()) {
                SyncItemArea syncItemArea;
                if (currentBuildup != null) {
                    syncItemArea = currentBuildup.getSyncItemArea();
                } else {
                    syncItemArea = toBeBuiltType.getBoundingBox().createSyntheticSyncItemArea(toBeBuildPosition);
                }
                destinationAngel = recalculateNewPath(builderType.getRange(), syncItemArea);
                getServices().getConnectionService().sendSyncInfo(getSyncBaseItem());
                return true;
            } else {
                return false;
            }
        }

        if (currentBuildup == null) {
            if (toBeBuiltType == null || toBeBuildPosition == null) {
                throw new IllegalArgumentException("Invalid attributes |" + toBeBuiltType + "|" + toBeBuildPosition);
            }

            getServices().getItemService().checkBuildingsInRect(toBeBuiltType, toBeBuildPosition);

            try {
                currentBuildup = (SyncBaseItem) getServices().getItemService().createSyncObject(toBeBuiltType, toBeBuildPosition, getSyncBaseItem(), getSyncBaseItem().getBase(), createdChildCount);
                createdChildCount++;
            } catch (ItemLimitExceededException e) {
                stop();
                return false;
            } catch (HouseSpaceExceededException e) {
                stop();
                return false;
            }
        }
        getSyncItemArea().turnTo(toBeBuildPosition);
        if (getServices().getItemService().baseObjectExists(currentBuildup)) {
            double buildFactor = setupBuildFactor(factor, builderType.getProgress(), toBeBuiltType, currentBuildup);
            try {
                getServices().getBaseService().withdrawalMoney(buildFactor * (double) toBeBuiltType.getPrice(), getSyncBaseItem().getBase());
                currentBuildup.addBuildup(buildFactor);
                if (currentBuildup.isReady()) {
                    stop();
                    return false;
                }
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

    public void stop() {
        if (currentBuildup != null) {
            getServices().getConnectionService().sendSyncInfo(currentBuildup);
        }
        currentBuildup = null;
        toBeBuiltType = null;
        toBeBuildPosition = null;
        destinationAngel = null;
        getSyncBaseItem().getSyncMovable().stop();
    }

    @Override
    public void synchronize(SyncItemInfo syncItemInfo) throws NoSuchItemTypeException {
        toBeBuildPosition = syncItemInfo.getToBeBuildPosition();
        if (syncItemInfo.getToBeBuiltTypeId() != null) {
            toBeBuiltType = (BaseItemType) getServices().getItemService().getItemType(syncItemInfo.getToBeBuiltTypeId());
        } else {
            toBeBuiltType = null;
        }
        createdChildCount = syncItemInfo.getCreatedChildCount();
        destinationAngel = syncItemInfo.getDestinationAngel();
    }

    @Override
    public void fillSyncItemInfo(SyncItemInfo syncItemInfo) {
        syncItemInfo.setToBeBuildPosition(Index.saveCopy(toBeBuildPosition));
        if (toBeBuiltType != null) {
            syncItemInfo.setToBeBuiltTypeId(toBeBuiltType.getId());
        }
        syncItemInfo.setCreatedChildCount(createdChildCount);
        syncItemInfo.setDestinationAngel(destinationAngel);
    }

    public void executeCommand(BuilderCommand builderCommand) throws NoSuchItemTypeException {
        if (!builderType.isAbleToBuild(builderCommand.getToBeBuilt())) {
            throw new IllegalArgumentException(this + " can not build: " + builderCommand.getToBeBuilt());
        }

        if (!getServices().getBaseService().isBot(getSyncBaseItem().getBase()) && !getServices().getItemTypeAccess().isAllowed(builderCommand.getToBeBuilt())) {
            throw new IllegalArgumentException(this + " user is not allowed (ItemTypeAccess) to build: " + builderCommand.getToBeBuilt());
        }

        BaseItemType tmpToBeBuiltType = (BaseItemType) getServices().getItemService().getItemType(builderCommand.getToBeBuilt());
        if (!getServices().getTerrainService().isFree(builderCommand.getPositionToBeBuilt(), tmpToBeBuiltType)) {
            throw new PositionTakenException(builderCommand.getPositionToBeBuilt(), builderCommand.getToBeBuilt());
        }

        if (!getServices().getTerritoryService().isAllowed(builderCommand.getPositionToBeBuilt(), getSyncBaseItem())) {
            throw new IllegalArgumentException(this + " Builder not allowed (TerritoryService) to build on territory: " + builderCommand.getPositionToBeBuilt() + "  " + getSyncBaseItem());
        }

        if (!getServices().getTerritoryService().isAllowed(builderCommand.getPositionToBeBuilt(), builderCommand.getToBeBuilt())) {
            throw new IllegalArgumentException(this + " Item can not be built (TerritoryService) on territory: " + builderCommand.getPositionToBeBuilt() + "  " + builderCommand.getToBeBuilt());
        }

        toBeBuiltType = tmpToBeBuiltType;
        toBeBuildPosition = builderCommand.getPositionToBeBuilt();
        destinationAngel = builderCommand.getDestinationAngel();
        setPathToDestinationIfSyncMovable(builderCommand.getPathToDestination());
    }

    public void executeCommand(BuilderFinalizeCommand builderFinalizeCommand) throws NoSuchItemTypeException, ItemDoesNotExistException {
        SyncBaseItem syncBaseItem = (SyncBaseItem) getServices().getItemService().getItem(builderFinalizeCommand.getToBeBuilt());
        if (!builderType.isAbleToBuild(syncBaseItem.getItemType().getId())) {
            throw new IllegalArgumentException(this + " can not build: " + builderFinalizeCommand.getToBeBuilt());
        }

        if (!getServices().getBaseService().isBot(getSyncBaseItem().getBase()) && !getServices().getItemTypeAccess().isAllowed(syncBaseItem.getItemType().getId())) {
            throw new IllegalArgumentException(this + " user is not allowed to build: " + builderFinalizeCommand.getToBeBuilt());
        }

        if (!getServices().getTerritoryService().isAllowed(syncBaseItem.getSyncItemArea().getPosition(), getSyncBaseItem())) {
            throw new IllegalArgumentException(this + " Builder not allowed to build on territory: " + syncBaseItem.getSyncItemArea().getPosition() + "  " + getSyncBaseItem());
        }

        if (!getServices().getTerritoryService().isAllowed(syncBaseItem.getSyncItemArea().getPosition(), syncBaseItem)) {
            throw new IllegalArgumentException(this + " Item can not be built on territory: " + syncBaseItem.getSyncItemArea().getPosition() + "  " + syncBaseItem);
        }

        currentBuildup = syncBaseItem;
        toBeBuiltType = syncBaseItem.getBaseItemType();
        toBeBuildPosition = syncBaseItem.getSyncItemArea().getPosition();
        destinationAngel = builderFinalizeCommand.getDestinationAngel();
        setPathToDestinationIfSyncMovable(builderFinalizeCommand.getPathToDestination());
    }

    public Index getToBeBuildPosition() {
        return toBeBuildPosition;
    }

    public BaseItemType getToBeBuiltType() {
        return toBeBuiltType;
    }

    public int getCreatedChildCount() {
        return createdChildCount;
    }

    public SyncBaseItem getCurrentBuildup() {
        return currentBuildup;
    }

    public void setCurrentBuildup(SyncBaseItem syncBaseItem) {
        this.currentBuildup = syncBaseItem;
    }

    public void setToBeBuildPosition(Index toBeBuildPosition) {
        this.toBeBuildPosition = toBeBuildPosition;
    }

    public void setToBeBuiltType(BaseItemType toBeBuiltType) {
        this.toBeBuiltType = toBeBuiltType;
    }

    public void setCreatedChildCount(int createdChildCount) {
        this.createdChildCount = createdChildCount;
    }

    public BuilderType getBuilderType() {
        return builderType;
    }
}
