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
import com.btxtech.game.jsre.common.gameengine.itemType.TurnableType;
import com.btxtech.game.jsre.common.gameengine.itemType.WeaponType;
import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
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
    private int buildup;
    @OneToOne(cascade = CascadeType.ALL)
    private DbTurnableType dbTurnableType;
    @OneToOne(cascade = CascadeType.ALL)
    private DbMovableType dbMovableType;
    @OneToOne(cascade = CascadeType.ALL)
    private DbWeaponType dbWeaponType;
    @OneToOne(cascade = CascadeType.ALL)
    private DbFactoryType dbFactoryType;
    @OneToOne(cascade = CascadeType.ALL)
    private DbHarvesterType dbHarvesterType;
    @OneToOne(cascade = CascadeType.ALL)
    private DbBuilderType dbBuilderType;
    @OneToOne(cascade = CascadeType.ALL)
    private DbConsumerType dbConsumerType;
    @OneToOne(cascade = CascadeType.ALL)
    private DbGeneratorType dbGeneratorType;
    @OneToOne(cascade = CascadeType.ALL)
    private DbSpecialType dbSpecialType;
    @OneToOne(cascade = CascadeType.ALL)
    private DbBaseItemType upgradable;
    @OneToOne(cascade = CascadeType.ALL)
    private DbItemContainerType dbItemContainerType;
    @OneToOne(cascade = CascadeType.ALL)
    private DbHouseType dbHouseType;
    @OneToOne(cascade = CascadeType.ALL)
    private DbLauncherType dbLauncherType;
    private Integer upgradeProgress;

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

    @Override
    public int getBuildup() {
        return buildup;
    }

    @Override
    public void setBuildup(int buildup) {
        this.buildup = buildup;
    }

    @Override
    public DbTurnableType getTurnableType() {
        return dbTurnableType;
    }

    @Override
    public void setTurnableType(DbTurnableType dbTurnableType) {
        this.dbTurnableType = dbTurnableType;
    }

    @Override
    public DbMovableType getMovableType() {
        return dbMovableType;
    }

    @Override
    public void setMovableType(DbMovableType dbMovableType) {
        this.dbMovableType = dbMovableType;
    }

    @Override
    public DbWeaponType getWeaponType() {
        return dbWeaponType;
    }

    @Override
    public void setWeaponType(DbWeaponType dbWeaponType) {
        this.dbWeaponType = dbWeaponType;
    }

    @Override
    public DbFactoryType getFactoryType() {
        return dbFactoryType;
    }

    @Override
    public void setFactoryType(DbFactoryType dbFactoryType) {
        this.dbFactoryType = dbFactoryType;
    }

    @Override
    public DbHarvesterType getHarvesterType() {
        return dbHarvesterType;
    }

    @Override
    public void setHarvesterType(DbHarvesterType dbHarvesterType) {
        this.dbHarvesterType = dbHarvesterType;
    }

    @Override
    public DbBuilderType getBuilderType() {
        return dbBuilderType;
    }

    @Override
    public void setBuilderType(DbBuilderType dbBuilderType) {
        this.dbBuilderType = dbBuilderType;
    }

    @Override
    public DbConsumerType getConsumerType() {
        return dbConsumerType;
    }

    @Override
    public void setConsumerType(DbConsumerType dbConsumerType) {
        this.dbConsumerType = dbConsumerType;
    }

    @Override
    public DbGeneratorType getGeneratorType() {
        return dbGeneratorType;
    }

    @Override
    public void setGeneratorType(DbGeneratorType dbGeneratorType) {
        this.dbGeneratorType = dbGeneratorType;
    }

    @Override
    public DbSpecialType getSpecialType() {
        return dbSpecialType;
    }

    @Override
    public void setSpecialType(DbSpecialType dbSpecialType) {
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

    @Override
    public DbLauncherType getDbLauncherType() {
        return dbLauncherType;
    }

    @Override
    public void setDbLauncherType(DbLauncherType dbLauncherType) {
        this.dbLauncherType = dbLauncherType;
    }

    @Override
    public ItemType createItemType() {
        BaseItemType baseItemType = new BaseItemType();
        setupItemType(baseItemType);
        baseItemType.setPrice(price);
        baseItemType.setHealth(health);
        baseItemType.setBuildup(buildup);
        if (dbTurnableType != null) {
            baseItemType.setTurnableType(new TurnableType(dbTurnableType.getImageCount()));
        }
        if (dbMovableType != null) {
            baseItemType.setMovableType(new MovableType(dbMovableType.getSpeed(), dbMovableType.getTerrainType()));
        }
        if (dbWeaponType != null) {
            baseItemType.setWeaponType(new WeaponType(dbWeaponType.getRange(),
                    dbWeaponType.getDamage(),
                    dbWeaponType.getReloadTime(),
                    dbWeaponType.getMuzzlePointX_0(),
                    dbWeaponType.getMuzzlePointY_0(),
                    dbWeaponType.getMuzzlePointX_90(),
                    dbWeaponType.getMuzzlePointY_90(),
                    dbWeaponType.getMuzzleFlashWidth(),
                    dbWeaponType.getMuzzleFlashLength(),
                    dbWeaponType.isStretchMuzzleFlashToTarget(),
                    toInt(dbWeaponType.getAllowedItemTypes())));
        }
        if (dbFactoryType != null) {
            baseItemType.setFactoryType(new FactoryType(dbFactoryType.getProgress(), toInt(dbFactoryType.getAbleToBuild())));
        }
        if (dbHarvesterType != null) {
            baseItemType.setHarvesterType(new HarvesterType(dbHarvesterType.getRange(), dbHarvesterType.getProgress()));
        }
        if (dbBuilderType != null) {
            baseItemType.setBuilderType(new BuilderType(dbBuilderType.getRange(), dbBuilderType.getProgress(), toInt(dbBuilderType.getAbleToBuild())));
        }
        if (dbConsumerType != null) {
            baseItemType.setConsumerType(new ConsumerType(dbConsumerType.getWattage()));
        }
        if (dbGeneratorType != null) {
            baseItemType.setGeneratorType(new GeneratorType(dbGeneratorType.getWattage()));
        }
        if (dbSpecialType != null) {
            baseItemType.setSpecialType(new SpecialType(dbSpecialType.getString()));
        }
        if (dbItemContainerType != null) {
            baseItemType.setItemContainerType(new ItemContainerType(toInt(dbItemContainerType.getAbleToContain()), dbItemContainerType.getMaxCount(), dbItemContainerType.getRange()));
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
