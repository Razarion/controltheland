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
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.BuilderType;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BuilderCommand;
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

    public SyncBuilder(BuilderType builderType, SyncBaseItem syncBaseItem) {
        super(syncBaseItem);
        this.builderType = builderType;
    }

    public boolean isActive() {
        return toBeBuildPosition != null && toBeBuiltType != null;
    }

    public boolean tick(double factor) throws InsufficientFundsException, NoSuchItemTypeException {
        if (toBeBuildPosition == null || toBeBuiltType == null) {
            return false;
        }

        if (isTargetInRange(toBeBuildPosition, builderType.getRange())) {
            if (currentBuildup == null) {
                if (toBeBuiltType == null || toBeBuildPosition == null) {
                    throw new IllegalArgumentException("Invalid attributes |" + toBeBuiltType + "|" + toBeBuildPosition);
                }
                if (getSyncBaseItem().hasSyncTurnable()) {
                    getSyncBaseItem().getSyncTurnable().turnTo(toBeBuildPosition);
                }
                currentBuildup = (SyncBaseItem) getServices().getItemService().buySyncObject(toBeBuiltType, toBeBuildPosition, getSyncBaseItem(), getSyncBaseItem().getBase(), createdChildCount);
                createdChildCount++;
            }
            if (getServices().getItemService().baseObjectExists(currentBuildup)) {
                currentBuildup.increaseHealth((int) (builderType.getProgress() * factor));
                if (currentBuildup.isHealthy()) {
                    currentBuildup.setBuild(true);
                    stop();
                    return false;
                } else {
                    return true;
                }
            } else {
                // It has may be killed
                stop();
                return false;
            }
        } else {
            if (toBeBuildPosition == null) {
                throw new IllegalStateException(this + " toBeBuildPosition == null");
            }
            getSyncBaseItem().getSyncMovable().tickMoveToTarget(factor, builderType.getRange(), toBeBuildPosition);
            return true;
        }
    }

    public void stop() {
        if(currentBuildup != null) {
           getServices().getConnectionService().sendSyncInfo(currentBuildup); 
        }
        currentBuildup = null;
        toBeBuiltType = null;
        toBeBuildPosition = null;
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
    }

    @Override
    public void fillSyncItemInfo(SyncItemInfo syncItemInfo) {
        syncItemInfo.setToBeBuildPosition(toBeBuildPosition);
        if (toBeBuiltType != null) {
            syncItemInfo.setToBeBuiltTypeId(toBeBuiltType.getId());
        }
        syncItemInfo.setCreatedChildCount(createdChildCount);
    }

    public void executeCommand(BuilderCommand builderCommand) throws NoSuchItemTypeException {
        if (!builderType.isAbleToBuild(builderCommand.getToBeBuilt())) {
            throw new IllegalArgumentException(this + " can not build: " + builderCommand.getToBeBuilt());
        }

        if(!getServices().getItemTypeAccess().isAllowed(builderCommand.getToBeBuilt())) {
            throw new IllegalArgumentException(this + " user is not allowed to build: " + builderCommand.getToBeBuilt());
        }
        BaseItemType tmptoBeBuiltType = (BaseItemType) getServices().getItemService().getItemType(builderCommand.getToBeBuilt());
        if (!getServices().getTerrainService().isFree(builderCommand.getPositionToBeBuilt(), tmptoBeBuiltType)) {
            throw new IllegalArgumentException(this + " can not build: " + builderCommand.getPositionToBeBuilt() + " on: " + builderCommand.getToBeBuilt());
        }
        toBeBuiltType = tmptoBeBuiltType;
        toBeBuildPosition = builderCommand.getPositionToBeBuilt();
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

    public SyncItem getCurrentBuildup() {
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
}
