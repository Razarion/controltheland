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
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.services.GlobalServices;
import com.btxtech.game.jsre.common.gameengine.services.PlanetServices;
import com.btxtech.game.jsre.common.gameengine.services.base.HouseSpaceExceededException;
import com.btxtech.game.jsre.common.gameengine.services.base.ItemLimitExceededException;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.AttackCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BuilderCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BuilderFinalizeCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.FactoryCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.LoadContainerCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.MoneyCollectCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.MoveCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.PickupBoxCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.UnloadContainerCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.UpgradeCommand;
import com.btxtech.game.jsre.common.packets.SyncItemInfo;

/**
 * User: beat
 * Date: 04.12.2009
 * Time: 19:11:49
 */
public class SyncBaseItem extends SyncTickItem implements SyncBaseObject {
    private SimpleBase base;
    private double buildup;
    private double health;
    private SyncMovable syncMovable;
    private SyncWeapon syncWeapon;
    private SyncFactory syncFactory;
    private SyncBuilder syncBuilder;
    private SyncHarvester syncHarvester;
    private SyncGenerator syncGenerator;
    private SyncConsumer syncConsumer;
    private SyncSpecial syncSpecial;
    private SyncItemContainer syncItemContainer;
    private SyncHouse syncHouse;
    private double upgradeProgress;
    private boolean isUpgrading;
    private BaseItemType upgradingItemType;
    private Id containedIn;
    private boolean isMoneyEarningOrConsuming = false;
    private SimpleBase killedBy;

    public SyncBaseItem(Id id, Index position, BaseItemType baseItemType, GlobalServices globalServices, PlanetServices planetServices, SimpleBase base) throws NoSuchItemTypeException {
        super(id, position, baseItemType, globalServices, planetServices);
        this.base = base;
        health = baseItemType.getHealth();
        setup();
    }

    private void setup() throws NoSuchItemTypeException {
        BaseItemType baseItemType = getBaseItemType();

        if (baseItemType.getMovableType() != null) {
            syncMovable = new SyncMovable(baseItemType.getMovableType(), this);
        } else {
            syncMovable = null;
        }

        if (baseItemType.getWeaponType() != null) {
            syncWeapon = new SyncWeapon(baseItemType.getWeaponType(), this);
        } else {
            syncWeapon = null;
        }

        if (baseItemType.getFactoryType() != null) {
            syncFactory = new SyncFactory(baseItemType.getFactoryType(), this);
            if (getPlanetServices().getConnectionService().getGameEngineMode() == GameEngineMode.MASTER) {
                syncFactory.calculateRallyPoint();
            }
            isMoneyEarningOrConsuming = true;
        } else {
            syncFactory = null;
        }

        if (baseItemType.getBuilderType() != null) {
            syncBuilder = new SyncBuilder(baseItemType.getBuilderType(), this);
            isMoneyEarningOrConsuming = true;
        } else {
            syncBuilder = null;
        }

        if (baseItemType.getHarvesterType() != null) {
            syncHarvester = new SyncHarvester(baseItemType.getHarvesterType(), this);
            isMoneyEarningOrConsuming = true;
        } else {
            syncHarvester = null;
        }

        if (baseItemType.getGeneratorType() != null) {
            syncGenerator = new SyncGenerator(baseItemType.getGeneratorType(), this);
        } else {
            syncGenerator = null;
        }

        if (baseItemType.getConsumerType() != null) {
            syncConsumer = new SyncConsumer(baseItemType.getConsumerType(), this);
        } else {
            syncConsumer = null;
        }

        if (baseItemType.getSpecialType() != null) {
            syncSpecial = new SyncSpecial(baseItemType.getSpecialType(), this);
        } else {
            syncSpecial = null;
        }

        if (baseItemType.getItemContainerType() != null) {
            syncItemContainer = new SyncItemContainer(baseItemType.getItemContainerType(), this);
        } else {
            syncItemContainer = null;
        }

        if (baseItemType.getHouseType() != null) {
            syncHouse = new SyncHouse(baseItemType.getHouseType(), this);
        } else {
            syncHouse = null;
        }
    }

    private void checkBase(SimpleBase syncBase) {
        if (base == null && syncBase == null) {
            return;
        }
        if (base == null) {
            throw new IllegalArgumentException(this + " this.base == null; sync base: " + syncBase);
        }

        if (!base.equals(syncBase)) {
            throw new IllegalArgumentException(this + " bases do not match: client: " + base + " sync: " + syncBase);
        }
    }

    public SimpleBase getBase() {
        return base;
    }

    @Override
    public void synchronize(SyncItemInfo syncItemInfo) throws NoSuchItemTypeException, ItemDoesNotExistException {
        checkBase(syncItemInfo.getBase());
        isUpgrading = syncItemInfo.isUpgrading();
        if (isUpgrading) {
            upgradingItemType = (BaseItemType) getGlobalServices().getItemTypeService().getItemType(getBaseItemType().getUpgradeable());
        } else {
            upgradingItemType = null;
        }
        if (getItemType().getId() != syncItemInfo.getItemTypeId()) {
            setItemType(getGlobalServices().getItemTypeService().getItemType(syncItemInfo.getItemTypeId()));
            fireItemChanged(SyncItemListener.Change.ITEM_TYPE_CHANGED, null);
            setup();
        }
        health = syncItemInfo.getHealth();
        setBuildup(syncItemInfo.getBuildup());
        upgradeProgress = syncItemInfo.getUpgradeProgress();
        containedIn = syncItemInfo.getContainedIn();
        killedBy = syncItemInfo.getKilledBy();

        if (syncMovable != null) {
            syncMovable.synchronize(syncItemInfo);
        }

        if (syncWeapon != null) {
            syncWeapon.synchronize(syncItemInfo);
        }

        if (syncFactory != null) {
            syncFactory.synchronize(syncItemInfo);
        }

        if (syncBuilder != null) {
            syncBuilder.synchronize(syncItemInfo);
        }

        if (syncHarvester != null) {
            syncHarvester.synchronize(syncItemInfo);
        }

        if (syncConsumer != null) {
            syncConsumer.synchronize(syncItemInfo);
        }

        if (syncItemContainer != null) {
            syncItemContainer.synchronize(syncItemInfo);
        }

        super.synchronize(syncItemInfo);
    }

    @Override
    public SyncItemInfo getSyncInfo() {
        SyncItemInfo syncItemInfo = super.getSyncInfo();
        syncItemInfo.setBase(base);
        syncItemInfo.setHealth(health);
        syncItemInfo.setBuildup(buildup);
        syncItemInfo.setUpgrading(isUpgrading);
        syncItemInfo.setUpgradeProgress(upgradeProgress);
        syncItemInfo.setContainedIn(containedIn);
        syncItemInfo.setKilledBy(killedBy);

        if (syncMovable != null) {
            syncMovable.fillSyncItemInfo(syncItemInfo);
        }

        if (syncWeapon != null) {
            syncWeapon.fillSyncItemInfo(syncItemInfo);
        }

        if (syncFactory != null) {
            syncFactory.fillSyncItemInfo(syncItemInfo);
        }

        if (syncBuilder != null) {
            syncBuilder.fillSyncItemInfo(syncItemInfo);
        }

        if (syncHarvester != null) {
            syncHarvester.fillSyncItemInfo(syncItemInfo);
        }

        if (syncConsumer != null) {
            syncConsumer.fillSyncItemInfo(syncItemInfo);
        }

        if (syncItemContainer != null) {
            syncItemContainer.fillSyncItemInfo(syncItemInfo);
        }

        return syncItemInfo;
    }

    public boolean isIdle() {
        return isReady()
                && !isUpgrading()
                && !(syncMovable != null && syncMovable.isActive())
                && !(syncWeapon != null && syncWeapon.isActive())
                && !(syncFactory != null && syncFactory.isActive())
                && !(syncBuilder != null && syncBuilder.isActive())
                && !(syncHarvester != null && syncHarvester.isActive());
    }

    public boolean tick(double factor) throws ItemDoesNotExistException, NoSuchItemTypeException {
        if (isUpgrading) {
            if (upgradeProgress >= upgradingItemType.getHealth()) {
                setItemType(upgradingItemType);
                upgradingItemType = null;
                isUpgrading = false;
                upgradeProgress = 0;
                setup();
                fireItemChanged(SyncItemListener.Change.ITEM_TYPE_CHANGED, null);
                return false;
            } else {
                upgradeProgress += getBaseItemType().getUpgradeProgress() * factor;
                fireItemChanged(SyncItemListener.Change.UPGRADE_PROGRESS_CHANGED, null);
                return true;
            }
        }

        if (hasSyncConsumer() && !getSyncConsumer().isOperating()) {
            return false;
        }

        if (syncWeapon != null && syncWeapon.isActive()) {
            return syncWeapon.tick(factor);
        }

        if (syncFactory != null && syncFactory.isActive()) {
            return syncFactory.tick(factor);
        }

        if (syncBuilder != null && syncBuilder.isActive()) {
            return syncBuilder.tick(factor);
        }

        if (syncHarvester != null && syncHarvester.isActive()) {
            return syncHarvester.tick(factor);
        }

        if (syncItemContainer != null && syncItemContainer.isActive()) {
            return syncItemContainer.tick(factor);
        }

        return syncMovable != null && syncMovable.isActive() && syncMovable.tick(factor);
    }

    public void stop() {
        if (syncWeapon != null) {
            syncWeapon.stop();
        }

        if (syncFactory != null) {
            syncFactory.stop();
        }

        if (syncBuilder != null) {
            syncBuilder.stop();
        }

        if (syncHarvester != null) {
            syncHarvester.stop();
        }

        if (syncMovable != null) {
            syncMovable.stop();
        }

        if (syncItemContainer != null) {
            syncItemContainer.stop();
        }
    }

    public void executeCommand(BaseCommand baseCommand) throws ItemDoesNotExistException, InsufficientFundsException, NoSuchItemTypeException, ItemLimitExceededException, HouseSpaceExceededException, WrongOperationSurfaceException {
        checkId(baseCommand);

        if (baseCommand instanceof AttackCommand) {
            getSyncWeapon().executeCommand((AttackCommand) baseCommand);
            return;
        }

        if (baseCommand instanceof MoveCommand) {
            getSyncMovable().executeCommand((MoveCommand) baseCommand);
            return;
        }

        if (baseCommand instanceof MoneyCollectCommand) {
            getSyncHarvester().executeCommand((MoneyCollectCommand) baseCommand);
            return;
        }

        if (baseCommand instanceof BuilderCommand) {
            getSyncBuilder().executeCommand((BuilderCommand) baseCommand);
            return;
        }

        if (baseCommand instanceof BuilderFinalizeCommand) {
            getSyncBuilder().executeCommand((BuilderFinalizeCommand) baseCommand);
            return;
        }

        if (baseCommand instanceof FactoryCommand) {
            getSyncFactory().executeCommand((FactoryCommand) baseCommand);
            return;
        }

        if (baseCommand instanceof UpgradeCommand) {
            if (getBaseItemType().getUpgradeable() == null) {
                throw new IllegalArgumentException(this + " can not be upgraded");
            }
            BaseItemType tmpUpgradingItemType = (BaseItemType) getGlobalServices().getItemTypeService().getItemType(getBaseItemType().getUpgradeable());
            getPlanetServices().getBaseService().withdrawalMoney(tmpUpgradingItemType.getPrice(), getBase());
            isUpgrading = true;
            upgradingItemType = tmpUpgradingItemType;
            return;
        }

        if (baseCommand instanceof LoadContainerCommand) {
            getSyncMovable().executeCommand((LoadContainerCommand) baseCommand);
            return;
        }

        if (baseCommand instanceof UnloadContainerCommand) {
            getSyncItemContainer().executeCommand((UnloadContainerCommand) baseCommand);
            return;
        }

        if (baseCommand instanceof PickupBoxCommand) {
            getSyncMovable().executeCommand((PickupBoxCommand) baseCommand);
            return;
        }

        throw new IllegalArgumentException("Command not supported: " + baseCommand);
    }

    private void checkId(BaseCommand baseCommand) {
        if (!baseCommand.getId().equals(getId())) {
            throw new IllegalArgumentException(this + "Id do not match: " + getId() + " command: " + baseCommand.getId());
        }
    }

    public SyncMovable getSyncMovable() {
        if (syncMovable == null) {
            throw new IllegalStateException(this + " has no SyncMovable");
        }
        return syncMovable;
    }

    public boolean hasSyncMovable() {
        return syncMovable != null;
    }

    public boolean hasSyncHarvester() {
        return syncHarvester != null;
    }

    public SyncHarvester getSyncHarvester() {
        if (syncHarvester == null) {
            throw new IllegalStateException(this + " has no SyncHarvester");
        }
        return syncHarvester;
    }

    public SyncFactory getSyncFactory() {
        if (syncFactory == null) {
            throw new IllegalStateException(this + " has no SyncFactory");
        }
        return syncFactory;
    }

    public boolean hasSyncFactory() {
        return syncFactory != null;
    }

    public boolean hasSyncWeapon() {
        return syncWeapon != null;
    }

    public SyncWeapon getSyncWeapon() {
        if (syncWeapon == null) {
            throw new IllegalStateException(this + " has no syncWeapon");
        }
        return syncWeapon;
    }

    public boolean hasSyncBuilder() {
        return syncBuilder != null;
    }

    public SyncBuilder getSyncBuilder() {
        if (syncBuilder == null) {
            throw new IllegalStateException(this + " has no SyncBuilder");
        }
        return syncBuilder;
    }

    public boolean hasSyncGenerator() {
        return syncGenerator != null;
    }

    public SyncGenerator getSyncGenerator() {
        if (syncGenerator == null) {
            throw new IllegalStateException(this + " has no SyncGenerator");
        }
        return syncGenerator;
    }

    public boolean hasSyncConsumer() {
        return syncConsumer != null;
    }

    public SyncConsumer getSyncConsumer() {
        if (syncConsumer == null) {
            throw new IllegalStateException(this + " has no SyncConsumer");
        }
        return syncConsumer;
    }

    public boolean hasSyncSpecial() {
        return syncSpecial != null;
    }

    public SyncSpecial getSyncSpecial() {
        if (syncConsumer == null) {
            throw new IllegalStateException(this + " has no SyncSpecial");
        }
        return syncSpecial;
    }

    public boolean hasSyncItemContainer() {
        return syncItemContainer != null;
    }

    public SyncItemContainer getSyncItemContainer() {
        if (syncItemContainer == null) {
            throw new IllegalStateException(this + " has no SyncItemContainer");
        }
        return syncItemContainer;
    }

    public boolean hasSyncHouse() {
        return syncHouse != null;
    }

    public SyncHouse getSyncHouse() {
        if (syncHouse == null) {
            throw new IllegalStateException(this + " has no SyncHouse");
        }
        return syncHouse;
    }

    public boolean isEnemy(SyncBaseItem syncBaseItem) {
        return getPlanetServices().getBaseService().isEnemy(this, syncBaseItem);
    }

    public boolean isEnemy(SimpleBase simpleBase) {
        return getPlanetServices().getBaseService().isEnemy(getBase(), simpleBase);
    }

    public void decreaseHealth(double progress, SimpleBase actor) {
        health -= progress;
        fireItemChanged(SyncItemListener.Change.HEALTH, null);
        if (health <= 0) {
            health = 0;
            getPlanetServices().getItemService().killSyncItem(this, actor, false, true);
        }
    }

    public void increaseHealth(double progress) {
        health += progress;
        if (health >= getBaseItemType().getHealth()) {
            health = getBaseItemType().getHealth();
        }
        fireItemChanged(SyncItemListener.Change.HEALTH, null);
    }

    public boolean isReady() {
        return buildup >= 1.0;
    }

    public double getBuildup() {
        return buildup;
    }

    public boolean isAlive() {
        return health > 0.0;
    }

    public boolean isHealthy() {
        return health >= getBaseItemType().getHealth();
    }

    public double getNormalizedHealth() {
        return Math.min(1.0, health / (double) getBaseItemType().getHealth());
    }

    public void addBuildup(double buildup) {
        setBuildup(this.buildup + buildup);
    }

    public void setBuildup(double buildup) {
        if (buildup > 1.0) {
            buildup = 1.0;
        }
        if (this.buildup == buildup) {
            return;
        }
        this.buildup = buildup;
        if (syncConsumer != null) {
            syncConsumer.setConsuming(buildup >= 1.0);
        }
        if (syncGenerator != null) {
            syncGenerator.setGenerating(buildup >= 1.0);
        }

        fireItemChanged(SyncItemListener.Change.BUILD, null);
    }

    public BaseItemType getBaseItemType() {
        return (BaseItemType) getItemType();
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = health;
        fireItemChanged(SyncItemListener.Change.HEALTH, null);
    }

    public double getUpgradeProgress() {
        return upgradeProgress;
    }

    public void setUpgradeProgress(double upgradeProgress) {
        this.upgradeProgress = upgradeProgress;
    }

    public boolean isUpgrading() {
        return isUpgrading;
    }

    public void setUpgrading(boolean upgrading) {
        isUpgrading = upgrading;
    }

    public void setContained(Id itemContainer) {
        this.containedIn = itemContainer;
        getSyncItemArea().setPosition(null);
        fireItemChanged(SyncItemListener.Change.CONTAINED_IN_CHANGED, null);
    }

    public void clearContained(Index position) {
        containedIn = null;
        getSyncItemArea().setPosition(position);
        fireItemChanged(SyncItemListener.Change.CONTAINED_IN_CHANGED, null);
    }

    public Id getContainedIn() {
        return containedIn;
    }

    public boolean isContainedIn() {
        return containedIn != null;
    }

    public boolean isUpgradeable() {
        return upgradingItemType != null;
    }

    public int getFullUpgradeProgress() {
        if (upgradingItemType == null) {
            throw new IllegalStateException(this + " can not be upgraded");
        }
        return upgradingItemType.getHealth();
    }

    public BaseItemType getUpgradingItemType() {
        return upgradingItemType;
    }

    public void setUpgradingItemType(BaseItemType upgradingItemType) {
        this.upgradingItemType = upgradingItemType;
    }

    public SimpleBase getKilledBy() {
        return killedBy;
    }

    public void setKilledBy(SimpleBase killedBy) {
        this.killedBy = killedBy;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(super.toString());
        if (hasSyncHarvester()) {
            builder.append(" target: ");
            builder.append(getSyncHarvester().getTarget());
        }
        if (containedIn != null) {
            builder.append(" containedIn: ");
            builder.append(containedIn);
        }
        builder.append(" ");
        builder.append(base);
        return builder.toString();
    }

    public boolean isMoneyEarningOrConsuming() {
        return isMoneyEarningOrConsuming;
    }

    public double getDropBoxPossibility() {
        return getBaseItemType().getDropBoxPossibility();
    }

    public void onAttacked(SyncBaseItem syncBaseItem) throws TargetHasNoPositionException {
        fireItemChanged(SyncItemListener.Change.UNDER_ATTACK, null);
        if (getPlanetServices().getConnectionService().getGameEngineMode() != GameEngineMode.MASTER) {
            return;
        }
        if (!isAlive()) {
            return;
        }
        if (!hasSyncWeapon()) {
            return;
        }
        if (!isIdle()) {
            return;
        }
        SyncWeapon syncWeapon = getSyncWeapon();
        if (!syncWeapon.isAttackAllowed(syncBaseItem)) {
            return;
        }

        if (syncWeapon.isInRange(syncBaseItem)) {
            getPlanetServices().getActionService().defend(this, syncBaseItem);
        }
    }
}
