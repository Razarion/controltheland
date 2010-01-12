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
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
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
public class DbBaseItemType extends DbItemType {
    private int health;
    private int price;
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

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public DbTurnableType getTurnableType() {
        return dbTurnableType;
    }

    public void setTurnableType(DbTurnableType dbTurnableType) {
        this.dbTurnableType = dbTurnableType;
    }

    public DbMovableType getMovableType() {
        return dbMovableType;
    }

    public void setMovableType(DbMovableType dbMovableType) {
        this.dbMovableType = dbMovableType;
    }

    public DbWeaponType getWeaponType() {
        return dbWeaponType;
    }

    public void setWeaponType(DbWeaponType dbWeaponType) {
        this.dbWeaponType = dbWeaponType;
    }

    public DbFactoryType getFactoryType() {
        return dbFactoryType;
    }

    public void setFactoryType(DbFactoryType dbFactoryType) {
        this.dbFactoryType = dbFactoryType;
    }

    public DbHarvesterType getHarvesterType() {
        return dbHarvesterType;
    }

    public void setHarvesterType(DbHarvesterType dbHarvesterType) {
        this.dbHarvesterType = dbHarvesterType;
    }

    public DbBuilderType getBuilderType() {
        return dbBuilderType;
    }

    public void setBuilderType(DbBuilderType dbBuilderType) {
        this.dbBuilderType = dbBuilderType;
    }

    public DbConsumerType getConsumerType() {
        return dbConsumerType;
    }

    public void setConsumerType(DbConsumerType dbConsumerType) {
        this.dbConsumerType = dbConsumerType;
    }

    public DbGeneratorType getGeneratorType() {
        return dbGeneratorType;
    }

    public void setGeneratorType(DbGeneratorType dbGeneratorType) {
        this.dbGeneratorType = dbGeneratorType;
    }

    public DbSpecialType getSpecialType() {
        return dbSpecialType;
    }

    public void setSpecialType(DbSpecialType dbSpecialType) {
        this.dbSpecialType = dbSpecialType;
    }

    @Override
    public ItemType createItemType() {
        BaseItemType baseItemType = new BaseItemType();
        setupItemType(baseItemType);
        baseItemType.setPrice(price);
        baseItemType.setHealth(health);
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
                    dbWeaponType.isStretchMuzzleFlashToTarget()));
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

        return baseItemType;
    }
}
