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

import com.btxtech.game.jsre.common.InsufficientFundsException;
import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.itemType.LauncherType;
import com.btxtech.game.jsre.common.gameengine.itemType.ProjectileItemType;
import com.btxtech.game.jsre.common.gameengine.services.base.HouseSpaceExceededException;
import com.btxtech.game.jsre.common.gameengine.services.base.ItemLimitExceededException;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.LaunchCommand;
import com.btxtech.game.jsre.common.packets.SyncItemInfo;

/**
 * User: beat
 * Date: 04.10.2010
 * Time: 22:27:10
 */
public class SyncLauncher extends SyncBaseAbility {
    private LauncherType launcherType;
    private double buildup;
    private ProjectileItemType projectileItemType;

    public SyncLauncher(LauncherType launcherType, SyncBaseItem syncBaseItem) {
        super(syncBaseItem);
        this.launcherType = launcherType;
    }

    @Override
    public void synchronize(SyncItemInfo syncItemInfo) throws NoSuchItemTypeException, ItemDoesNotExistException {
        buildup = syncItemInfo.getProjectileBuildupProgress();
    }

    public int getRange() throws NoSuchItemTypeException {
        return getProjectileItemType().getRange();
    }

    @Override
    public void fillSyncItemInfo(SyncItemInfo syncItemInfo) {
        syncItemInfo.setProjectileBuildupProgress(buildup);
    }

    public boolean tick(double factor) throws NoSuchItemTypeException {
        if (!isActive()) {
            return false;
        }

        double buildFactor = factor * launcherType.getProgress() / (double) getProjectileItemType().getBuildup();
        if (buildFactor + buildup > 1.0) {
            buildFactor = 1.0 - buildup;
        }
        try {
            if (getProjectileItemType().getPrice() > 0) {
                getPlanetServices().getBaseService().withdrawalMoney(buildFactor * (double) getProjectileItemType().getPrice(), getSyncBaseItem().getBase());
            }
            buildup += buildFactor;
            getSyncBaseItem().fireItemChanged(SyncItemListener.Change.LAUNCHER_PROGRESS);
            return buildup < 1.0;
        } catch (InsufficientFundsException e) {
            return true;
        }
    }

    public boolean isActive() {
        return buildup < 1.0;
    }

    public double getBuildup() {
        return buildup;
    }

    public void setBuildup(double buildup) {
        this.buildup = buildup;
    }

    public void stop() {
    }

    public LauncherType getLauncherType() {
        return launcherType;
    }

    public ProjectileItemType getProjectileItemType() throws NoSuchItemTypeException {
        if(projectileItemType == null) {
            projectileItemType = (ProjectileItemType) getGlobalServices().getItemTypeService().getItemType(launcherType.getProjectileItemType());
        }
        return projectileItemType;
    }

    public void executeCommand(LaunchCommand command) throws ItemLimitExceededException, HouseSpaceExceededException, NoSuchItemTypeException {
        if (isActive()) {
            throw new IllegalStateException(this + " projectile is not built yet");
        }

        int range = getProjectileItemType().getRange();
        if (getSyncItemArea().getPosition().getDistance(command.getTarget()) > range) {
            throw new IllegalStateException(this + " range too big for projectile");
        }

        SyncProjectileItem projectile = (SyncProjectileItem) getPlanetServices().getItemService().createSyncObject(getProjectileItemType(), getSyncItemArea().getPosition(), getSyncBaseItem(), getSyncBaseItem().getBase());
        if (projectile != null) {
            getSyncItemArea().turnTo(command.getTarget());
            buildup = 0;
            getSyncBaseItem().fireItemChanged(SyncItemListener.Change.LAUNCHER_PROGRESS);
            projectile.setTarget(command.getTarget());
            getPlanetServices().getActionService().syncItemActivated(projectile);
            getPlanetServices().getConnectionService().sendSyncInfo(projectile);
        }
    }

}
