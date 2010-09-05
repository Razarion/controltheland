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
import com.btxtech.game.jsre.common.gameengine.itemType.FactoryType;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.FactoryCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.syncInfos.SyncItemInfo;
import java.util.ArrayList;
import java.util.Collection;

/**
 * User: beat
 * Date: 21.11.2009
 * Time: 21:38:19
 */
public class SyncFactory extends SyncBaseAbility {
    private FactoryType factoryType;
    private BaseItemType toBeBuiltType;
    private double buildupProgress;
    private int createdChildCount;
    private Index rallyPoint;

    public SyncFactory(FactoryType factoryType, SyncBaseItem syncBaseItem) throws NoSuchItemTypeException {
        super(syncBaseItem);
        this.factoryType = factoryType;
        calculateRallyPoint();
    }

    public boolean isActive() {
        return getSyncBaseItem().isAlive() && toBeBuiltType != null && getSyncBaseItem().isReady();
    }

    public boolean tick(double factor) throws InsufficientFundsException, NoSuchItemTypeException {
        if (!isActive()) {
            return false;
        }

        buildupProgress += ((double)factoryType.getProgress() * factor);
        if (buildupProgress >= toBeBuiltType.getHealth()) {
            SyncBaseItem item = (SyncBaseItem) getServices().getItemService().createSyncObject(toBeBuiltType, rallyPoint, getSyncBaseItem(), getSyncBaseItem().getBase(), createdChildCount);
            item.setFullHealth();
            item.setBuild(true);
            createdChildCount++;
            buildupProgress = 0;
            toBeBuiltType = null;
            getSyncBaseItem().fireItemChanged(SyncItemListener.Change.FACTORY_PROGRESS);
            if(item.hasSyncMovable() &&item.getSyncMovable().onFinished() ) {
                getServices().getActionService().syncItemActivated(item);
            }
            return false;
        }
        getSyncBaseItem().fireItemChanged(SyncItemListener.Change.FACTORY_PROGRESS);
        return true;
    }

    public double getBuildupProgress() {
        return buildupProgress;
    }

    public BaseItemType getToBeBuiltType() {
        return toBeBuiltType;
    }

    @Override
    public void synchronize(SyncItemInfo syncItemInfo) throws NoSuchItemTypeException {
        if (syncItemInfo.getToBeBuiltTypeId() != null) {
            toBeBuiltType = (BaseItemType) getServices().getItemService().getItemType(syncItemInfo.getToBeBuiltTypeId());
        } else {
            toBeBuiltType = null;
        }
        buildupProgress = syncItemInfo.getBuildupProgress();
        createdChildCount = syncItemInfo.getCreatedChildCount();
        rallyPoint = syncItemInfo.getRallyPoint();
    }

    @Override
    public void fillSyncItemInfo(SyncItemInfo syncItemInfo) {
        if (toBeBuiltType != null) {
            syncItemInfo.setToBeBuiltTypeId(toBeBuiltType.getId());
        }
        syncItemInfo.setBuildupProgress(buildupProgress);
        syncItemInfo.setCreatedChildCount(createdChildCount);
        syncItemInfo.setRallyPoint(rallyPoint);
    }

    public void stop() {
        toBeBuiltType = null;
        buildupProgress = 0;
    }

    public void executeCommand(FactoryCommand factoryCommand) throws InsufficientFundsException, NoSuchItemTypeException {
        if (!getSyncBaseItem().isReady()) {
            return;
        }
        if (!factoryType.isAbleToBuild(factoryCommand.getToBeBuilt())) {
            throw new IllegalArgumentException(this + " can not fabricate: " + factoryCommand.getToBeBuilt());
        }
        if (!getServices().getBaseService().isBot(getSyncBaseItem().getBase()) && !getServices().getItemTypeAccess().isAllowed(factoryCommand.getToBeBuilt())) {
            throw new IllegalArgumentException(this + " user is not allowed to fabricate: " + factoryCommand.getToBeBuilt());
        }
        if (!getServices().getTerritoryService().isAllowed(getSyncBaseItem().getPosition(), getSyncBaseItem())) {
            throw new IllegalArgumentException(this + " Factory not allowed to build on territory: " + getSyncBaseItem().getPosition() + "  " + getSyncBaseItem());
        }
        if (!getServices().getTerritoryService().isAllowed(getSyncBaseItem().getPosition(), factoryCommand.getToBeBuilt())) {
            throw new IllegalArgumentException(this + " Item can not be built on territory: " + getSyncBaseItem().getPosition() + "  " + factoryCommand.getToBeBuilt());
        }
        if (toBeBuiltType == null) {
            BaseItemType tmpToBeBuiltType = (BaseItemType) getServices().getItemService().getItemType(factoryCommand.getToBeBuilt());
            getServices().getBaseService().withdrawalMoney(tmpToBeBuiltType.getPrice(), getSyncBaseItem().getBase());
            toBeBuiltType = tmpToBeBuiltType;
        }
    }

    public int getCreatedChildCount() {
        return createdChildCount;
    }

    public void setToBeBuiltType(BaseItemType toBeBuiltType) {
        this.toBeBuiltType = toBeBuiltType;
    }

    public void setBuildupProgress(double buildupProgress) {
        this.buildupProgress = buildupProgress;
    }

    public void setCreatedChildCount(int createdChildCount) {
        this.createdChildCount = createdChildCount;
    }

    public Index getRallyPoint() {
        return rallyPoint;
    }

    public void setRallyPoint(Index rallyPoint) {
        this.rallyPoint = rallyPoint;
    }

    private void calculateRallyPoint() throws NoSuchItemTypeException {
        Collection<TerrainType> types = new ArrayList<TerrainType>();
        for (int id : factoryType.getAbleToBuild()) {
            ItemType itemType = getServices().getItemService().getItemType(id);
            types.add(itemType.getTerrainType());
        }
        rallyPoint = getServices().getCollisionService().getRallyPoint(getSyncBaseItem(), TerrainType.leastCommonMultiple(types));
    }
}
