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

package com.btxtech.game.services.item.itemType;

import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.BuilderType;
import com.btxtech.game.jsre.common.gameengine.itemType.ConsumerType;
import com.btxtech.game.jsre.common.gameengine.itemType.FactoryType;
import com.btxtech.game.jsre.common.gameengine.itemType.GeneratorType;
import com.btxtech.game.jsre.common.gameengine.itemType.HarvesterType;
import com.btxtech.game.jsre.common.gameengine.itemType.HouseType;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemContainerType;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.LauncherType;
import com.btxtech.game.jsre.common.gameengine.itemType.MovableType;
import com.btxtech.game.jsre.common.gameengine.itemType.SpecialType;
import com.btxtech.game.services.common.Utils;
import com.btxtech.game.services.user.UserService;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

/**
 * User: beat
 * Date: 09.12.2009
 * Time: 14:52:18
 */
@Entity
@DiscriminatorValue("BASE")
public class DbBaseItemType extends DbItemType implements DbBaseItemTypeI {
    private int health;
    private int price;
    private int xpOnKilling;
    private int buildup;
    private int consumingHouseSpace;
    private double dropBoxPossibility;
    private int boxPickupRange;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbBoxItemType dbBoxItemType;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DbMovableType dbMovableType;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DbWeaponType dbWeaponType;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DbFactoryType dbFactoryType;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DbHarvesterType dbHarvesterType;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DbBuilderType dbBuilderType;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DbConsumerType dbConsumerType;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DbGeneratorType dbGeneratorType;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DbSpecialType dbSpecialType;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DbBaseItemType upgradable;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DbItemContainerType dbItemContainerType;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DbHouseType dbHouseType;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DbLauncherType dbLauncherType;
    private Integer upgradeProgress;
    private Integer unlockCrystals;

    @Override
    public int getHealth() {
        return health;
    }

    @Override
    public void setHealth(int health) {
        this.health = health;
    }

    @Override
    public int getPrice() {
        return price;
    }

    @Override
    public void setPrice(int price) {
        this.price = price;
    }

    public int getXpOnKilling() {
        return xpOnKilling;
    }

    public void setXpOnKilling(int xpOnKilling) {
        this.xpOnKilling = xpOnKilling;
    }

    public int getConsumingHouseSpace() {
        return consumingHouseSpace;
    }

    public void setConsumingHouseSpace(int consumingHouseSpace) {
        this.consumingHouseSpace = consumingHouseSpace;
    }

    @Override
    public int getBuildup() {
        return buildup;
    }

    @Override
    public void setBuildup(int buildup) {
        this.buildup = buildup;
    }

    @Override
    public DbBoxItemType getDbBoxItemType() {
        return dbBoxItemType;
    }

    @Override
    public void setDbBoxItemType(DbBoxItemType dbBoxItemType) {
        this.dbBoxItemType = dbBoxItemType;
    }

    @Override
    public double getDropBoxPossibility() {
        return dropBoxPossibility;
    }

    @Override
    public void setDropBoxPossibility(double dropBoxPossibility) {
        this.dropBoxPossibility = dropBoxPossibility;
    }

    @Override
    public int getBoxPickupRange() {
        return boxPickupRange;
    }

    @Override
    public void setBoxPickupRange(int boxPickupRange) {
        this.boxPickupRange = boxPickupRange;
    }

    @Override
    public DbMovableType getDbMovableType() {
        return dbMovableType;
    }

    @Override
    public void setDbMovableType(DbMovableType dbMovableType) {
        this.dbMovableType = dbMovableType;
    }

    @Override
    public DbWeaponType getDbWeaponType() {
        return dbWeaponType;
    }

    @Override
    public void setDbWeaponType(DbWeaponType dbWeaponType) {
        this.dbWeaponType = dbWeaponType;
    }

    @Override
    public DbFactoryType getDbFactoryType() {
        return dbFactoryType;
    }

    @Override
    public void setDbFactoryType(DbFactoryType dbFactoryType) {
        this.dbFactoryType = dbFactoryType;
    }

    @Override
    public DbHarvesterType getDbHarvesterType() {
        return dbHarvesterType;
    }

    @Override
    public void setDbHarvesterType(DbHarvesterType dbHarvesterType) {
        this.dbHarvesterType = dbHarvesterType;
    }

    @Override
    public DbBuilderType getDbBuilderType() {
        return dbBuilderType;
    }

    @Override
    public void setDbBuilderType(DbBuilderType dbBuilderType) {
        this.dbBuilderType = dbBuilderType;
    }

    @Override
    public DbConsumerType getDbConsumerType() {
        return dbConsumerType;
    }

    @Override
    public void setDbConsumerType(DbConsumerType dbConsumerType) {
        this.dbConsumerType = dbConsumerType;
    }

    @Override
    public DbGeneratorType getDbGeneratorType() {
        return dbGeneratorType;
    }

    @Override
    public void setDbGeneratorType(DbGeneratorType dbGeneratorType) {
        this.dbGeneratorType = dbGeneratorType;
    }

    @Override
    public DbSpecialType getDbSpecialType() {
        return dbSpecialType;
    }

    @Override
    public void setDbSpecialType(DbSpecialType dbSpecialType) {
        this.dbSpecialType = dbSpecialType;
    }

    @Override
    public DbItemContainerType getDbItemContainerType() {
        return dbItemContainerType;
    }

    @Override
    public void setDbItemContainerType(DbItemContainerType dbItemContainerType) {
        this.dbItemContainerType = dbItemContainerType;
    }

    @Override
    public DbHouseType getDbHouseType() {
        return dbHouseType;
    }

    @Override
    public void setDbHouseType(DbHouseType dbHouseType) {
        this.dbHouseType = dbHouseType;
    }

    @Override
    public DbBaseItemType getUpgradable() {
        return upgradable;
    }

    @Override
    public void setUpgradable(DbBaseItemType upgradable) {
        this.upgradable = upgradable;
    }

    @Override
    public Integer getUpgradeProgress() {
        return upgradeProgress;
    }

    @Override
    public void setUpgradeProgress(Integer upgradeProgress) {
        this.upgradeProgress = upgradeProgress;
    }

    public Integer getUnlockCrystals() {
        return unlockCrystals;
    }

    public void setUnlockCrystals(Integer unlockCrystals) {
        this.unlockCrystals = unlockCrystals;
    }

    @Override
    public DbLauncherType getDbLauncherType() {
        return dbLauncherType;
    }

    @Override
    public void setDbLauncherType(DbLauncherType dbLauncherType) {
        this.dbLauncherType = dbLauncherType;
    }

    @Override
    public void init(UserService userService) {
        super.init(userService);
        dropBoxPossibility = 0.0;
        boxPickupRange = 100;
    }

    @Override
    public ItemType createItemType() {
        BaseItemType baseItemType = new BaseItemType();
        setupItemType(baseItemType);
        baseItemType.setPrice(price);
        baseItemType.setXpOnKilling(xpOnKilling);
        baseItemType.setHealth(health);
        baseItemType.setConsumingHouseSpace(consumingHouseSpace);
        baseItemType.setBuildup(buildup);
        baseItemType.setDropBoxPossibility(dropBoxPossibility);
        baseItemType.setBoxPickupRange(boxPickupRange);
        baseItemType.setUnlockCrystals(unlockCrystals);
        if (dbMovableType != null) {
            baseItemType.setMovableType(new MovableType(dbMovableType.getSpeed()));
        }
        if (dbWeaponType != null) {
            baseItemType.setWeaponType(dbWeaponType.createWeaponType(getAngels().size()));
        }
        if (dbFactoryType != null) {
            baseItemType.setFactoryType(new FactoryType(dbFactoryType.getProgress(), Utils.dbBaseItemTypesToInts(dbFactoryType.getAbleToBuild())));
        }
        if (dbHarvesterType != null) {
            baseItemType.setHarvesterType(new HarvesterType(dbHarvesterType.getRange(), dbHarvesterType.getProgress()));
        }
        if (dbBuilderType != null) {
            baseItemType.setBuilderType(new BuilderType(dbBuilderType.getRange(), dbBuilderType.getProgress(), Utils.dbBaseItemTypesToInts(dbBuilderType.getAbleToBuild())));
        }
        if (dbConsumerType != null) {
            baseItemType.setConsumerType(new ConsumerType(dbConsumerType.getWattage()));
        }
        if (dbGeneratorType != null) {
            baseItemType.setGeneratorType(new GeneratorType(dbGeneratorType.getWattage()));
        }
        if (dbSpecialType != null) {
            baseItemType.setSpecialType(new SpecialType(dbSpecialType.getRadarMode()));
        }
        if (dbItemContainerType != null) {
            baseItemType.setItemContainerType(new ItemContainerType(Utils.dbBaseItemTypesToInts(dbItemContainerType.getAbleToContain()), dbItemContainerType.getOperationSurfaceType(), dbItemContainerType.getMaxCount(), dbItemContainerType.getRange()));
        }
        if (dbHouseType != null) {
            baseItemType.setHouseType(new HouseType(dbHouseType.getSpace()));
        }
        if (dbLauncherType != null) {
            baseItemType.setLauncherType(new LauncherType(dbLauncherType.getDbProjectileItemType().getId(), dbLauncherType.getProgress()));
        }
        if (upgradable != null) {
            baseItemType.setUpgradeable(upgradable.getId());
            baseItemType.setUpgradeProgress(upgradeProgress);
        }

        return baseItemType;
    }
}
