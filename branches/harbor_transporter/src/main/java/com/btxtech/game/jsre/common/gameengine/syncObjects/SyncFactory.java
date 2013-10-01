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

import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.GameEngineMode;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.InsufficientFundsException;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.FactoryType;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.base.HouseSpaceExceededException;
import com.btxtech.game.jsre.common.gameengine.services.base.ItemLimitExceededException;
import com.btxtech.game.jsre.common.gameengine.services.collision.Path;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.FactoryCommand;
import com.btxtech.game.jsre.common.packets.SyncItemInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 21.11.2009
 * Time: 21:38:19
 */
public class SyncFactory extends SyncBaseAbility {
    private FactoryType factoryType;
    private BaseItemType toBeBuiltType;
    private double buildup;
    private Index rallyPoint;
    private Logger log = Logger.getLogger(SyncFactory.class.getName());

    public SyncFactory(FactoryType factoryType, SyncBaseItem syncBaseItem) throws NoSuchItemTypeException {
        super(syncBaseItem);
        this.factoryType = factoryType;
    }

    public FactoryType getFactoryType() {
        return factoryType;
    }

    public boolean isActive() {
        return getSyncBaseItem().isAlive() && toBeBuiltType != null && getSyncBaseItem().isReady();
    }

    public boolean tick(double factor) throws NoSuchItemTypeException {
        if (!isActive()) {
            return false;
        }

        double buildFactor = factor * factoryType.getProgress() / (double) toBeBuiltType.getBuildup();
        if (buildFactor + buildup > 1.0) {
            buildFactor = 1.0 - buildup;
        }
        try {
            getPlanetServices().getBaseService().withdrawalMoney(buildFactor * (double) toBeBuiltType.getPrice(), getSyncBaseItem().getBase());
            buildup += buildFactor;
            getSyncBaseItem().fireItemChanged(SyncItemListener.Change.FACTORY_PROGRESS);
            if (buildup >= 1.0) {
                if (getPlanetServices().getConnectionService().getGameEngineMode() != GameEngineMode.MASTER) {
                    // Wait for server to create currentBuildup
                    return true;
                }
                if (!getPlanetServices().getBaseService().isItemLimit4ItemAddingAllowed(toBeBuiltType, getSyncBaseItem().getBase())) {
                    return true;
                }
                final SyncBaseItem item = (SyncBaseItem) getPlanetServices().getItemService().createSyncObject(toBeBuiltType, rallyPoint, getSyncBaseItem(), getSyncBaseItem().getBase());
                item.setBuildup(buildup);
                stop();
                if (item.hasSyncMovable() && item.getSyncMovable().onFinished(new SyncMovable.OverlappingHandler() {
                    @Override
                    public Path calculateNewPath() {
                        return getPlanetServices().getCollisionService().setupPathToSyncMovableRandomPositionIfTaken(item);
                    }
                })) {
                    getPlanetServices().getActionService().syncItemActivated(item);
                }
                return false;
            }
            return true;
        } catch (InsufficientFundsException e) {
            return true;
        } catch (HouseSpaceExceededException e) {
            return true;
        } catch (ItemLimitExceededException e) {
            return true;
        }
    }

    public double getBuildupProgress() {
        return buildup;
    }

    public BaseItemType getToBeBuiltType() {
        return toBeBuiltType;
    }

    @Override
    public void synchronize(SyncItemInfo syncItemInfo) throws NoSuchItemTypeException {
        if (syncItemInfo.getToBeBuiltTypeId() != null) {
            toBeBuiltType = (BaseItemType) getGlobalServices().getItemTypeService().getItemType(syncItemInfo.getToBeBuiltTypeId());
        } else {
            toBeBuiltType = null;
        }
        buildup = syncItemInfo.getFactoryBuildupProgress();
        rallyPoint = syncItemInfo.getRallyPoint();
    }

    @Override
    public void fillSyncItemInfo(SyncItemInfo syncItemInfo) {
        if (toBeBuiltType != null) {
            syncItemInfo.setToBeBuiltTypeId(toBeBuiltType.getId());
        }
        syncItemInfo.setFactoryBuildupProgress(buildup);
        syncItemInfo.setRallyPoint(Index.saveCopy(rallyPoint));
    }

    public void stop() {
        buildup = 0;
        toBeBuiltType = null;
        getSyncBaseItem().fireItemChanged(SyncItemListener.Change.FACTORY_PROGRESS);
    }

    public void executeCommand(FactoryCommand factoryCommand) throws InsufficientFundsException, NoSuchItemTypeException {
        if (!getSyncBaseItem().isReady()) {
            return;
        }
        if (!factoryType.isAbleToBuild(factoryCommand.getToBeBuilt())) {
            throw new IllegalArgumentException(this + " can not fabricate: " + factoryCommand.getToBeBuilt());
        }
        BaseItemType tmpToBeBuiltType = (BaseItemType) getGlobalServices().getItemTypeService().getItemType(factoryCommand.getToBeBuilt());

        if (getGlobalServices().getUnlockService().isItemLocked(tmpToBeBuiltType, getSyncBaseItem().getBase())) {
            throw new IllegalArgumentException(this + " item is locked: " + factoryCommand.getToBeBuilt());
        }
        if (toBeBuiltType == null) {
            toBeBuiltType = tmpToBeBuiltType;
        }
    }

    public void setToBeBuiltType(BaseItemType toBeBuiltType) {
        this.toBeBuiltType = toBeBuiltType;
    }

    public void setBuildupProgress(double buildup) {
        this.buildup = buildup;
    }

    public Index getRallyPoint() {
        return rallyPoint;
    }

    public void setRallyPoint(Index rallyPoint) {
        this.rallyPoint = rallyPoint;
    }

    void calculateRallyPoint() throws NoSuchItemTypeException {
        Collection<ItemType> types = new ArrayList<ItemType>();
        try {
            for (int id : factoryType.getAbleToBuild()) {
                types.add(getGlobalServices().getItemTypeService().getItemType(id));
            }
            rallyPoint = getPlanetServices().getCollisionService().getRallyPoint(getSyncBaseItem(), types);
        } catch (NoSuchItemTypeException e) {
            log.log(Level.SEVERE, "Unable to calculate rally point: " + e.getMessage());
            rallyPoint = getSyncItemArea().getPosition();
        }
    }
}
