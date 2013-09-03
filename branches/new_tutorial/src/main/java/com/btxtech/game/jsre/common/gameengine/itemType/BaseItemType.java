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

package com.btxtech.game.jsre.common.gameengine.itemType;

/**
 * User: beat
 * Date: 04.12.2009
 * Time: 20:12:06
 */
public class BaseItemType extends ItemType {
    private int health;
    private int price;
    private int buildup;
    private int xpOnKilling;
    private int consumingHouseSpace;
    private MovableType movableType;
    private WeaponType weaponType;
    private FactoryType factoryType;
    private HarvesterType harvesterType;
    private BuilderType builderType;
    private GeneratorType generatorType;
    private ConsumerType consumerType;
    private SpecialType specialType;
    private ItemContainerType itemContainerType;
    private HouseType houseType;
    private LauncherType launcherType;
    private Integer upgradeable;
    private int upgradeProgress;
    private double dropBoxPossibility;
    private int boxPickupRange;
    private Integer unlockRazarion;

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

    public double getDropBoxPossibility() {
        return dropBoxPossibility;
    }

    public void setDropBoxPossibility(double dropBoxPossibility) {
        this.dropBoxPossibility = dropBoxPossibility;
    }

    public int getBoxPickupRange() {
        return boxPickupRange;
    }

    public void setBoxPickupRange(int boxPickupRange) {
        this.boxPickupRange = boxPickupRange;
    }

    public MovableType getMovableType() {
        return movableType;
    }

    public void setMovableType(MovableType movableType) {
        this.movableType = movableType;
    }

    public WeaponType getWeaponType() {
        return weaponType;
    }

    public void setWeaponType(WeaponType weaponType) {
        this.weaponType = weaponType;
    }

    public FactoryType getFactoryType() {
        return factoryType;
    }

    public void setFactoryType(FactoryType factoryType) {
        this.factoryType = factoryType;
    }

    public LauncherType getLauncherType() {
        return launcherType;
    }

    public void setLauncherType(LauncherType launcherType) {
        this.launcherType = launcherType;
    }

    public HarvesterType getHarvesterType() {
        return harvesterType;
    }

    public void setHarvesterType(HarvesterType harvesterType) {
        this.harvesterType = harvesterType;
    }

    public BuilderType getBuilderType() {
        return builderType;
    }

    public void setBuilderType(BuilderType builderType) {
        this.builderType = builderType;
    }

    public GeneratorType getGeneratorType() {
        return generatorType;
    }

    public void setGeneratorType(GeneratorType generatorType) {
        this.generatorType = generatorType;
    }

    public ConsumerType getConsumerType() {
        return consumerType;
    }

    public void setConsumerType(ConsumerType consumerType) {
        this.consumerType = consumerType;
    }

    public SpecialType getSpecialType() {
        return specialType;
    }

    public void setSpecialType(SpecialType specialType) {
        this.specialType = specialType;
    }

    public ItemContainerType getItemContainerType() {
        return itemContainerType;
    }

    public void setItemContainerType(ItemContainerType itemContainerType) {
        this.itemContainerType = itemContainerType;
    }

    public HouseType getHouseType() {
        return houseType;
    }

    public void setHouseType(HouseType houseType) {
        this.houseType = houseType;
    }

    public Integer getUpgradeable() {
        return upgradeable;
    }

    public void setUpgradeable(Integer upgradeable) {
        this.upgradeable = upgradeable;
    }

    public double getUpgradeProgress() {
        return upgradeProgress;
    }

    public void setUpgradeProgress(int upgradeProgress) {
        this.upgradeProgress = upgradeProgress;
    }

    public int getBuildup() {
        return buildup;
    }

    public void setBuildup(int buildup) {
        this.buildup = buildup;
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

    public boolean isUnlockNeeded() {
        return unlockRazarion != null;
    }

    public Integer getUnlockRazarion() {
        return unlockRazarion;
    }

    public void setUnlockRazarion(Integer unlockRazarion) {
        this.unlockRazarion = unlockRazarion;
    }

    @Override
    public void changeTo(ItemType itemType) {
        super.changeTo(itemType);
        BaseItemType baseItemType = (BaseItemType) itemType;
        health = baseItemType.health;
        price = baseItemType.price;
        xpOnKilling = baseItemType.xpOnKilling;
        buildup = baseItemType.buildup;
        consumingHouseSpace = baseItemType.consumingHouseSpace;
        upgradeable = baseItemType.upgradeable;
        upgradeProgress = baseItemType.upgradeProgress;
        dropBoxPossibility = baseItemType.dropBoxPossibility;
        boxPickupRange = baseItemType.getBoxPickupRange();
        unlockRazarion = baseItemType.getUnlockRazarion();

        if (movableType != null) {
            movableType.changeTo(baseItemType.movableType);
        }
        if (weaponType != null) {
            weaponType.changeTo(baseItemType.weaponType);
        }
        if (factoryType != null) {
            factoryType.changeTo(baseItemType.factoryType);
        }
        if (harvesterType != null) {
            harvesterType.changeTo(baseItemType.harvesterType);
        }
        if (builderType != null) {
            builderType.changeTo(baseItemType.builderType);
        }
        if (generatorType != null) {
            generatorType.changeTo(baseItemType.generatorType);
        }
        if (consumerType != null) {
            consumerType.changeTo(baseItemType.consumerType);
        }
        if (specialType != null) {
            specialType.changeTo(baseItemType.specialType);
        }

        // TODO make other abilities like this
        if (baseItemType.itemContainerType == null && itemContainerType != null) {
            // remove
            itemContainerType = null;
        } else if (itemContainerType != null) {
            itemContainerType.changeTo(baseItemType.itemContainerType);
        }

        if (baseItemType.houseType == null && houseType != null) {
            // Remove
            houseType = null;
        } else if (houseType != null) {
            houseType.changeTo(baseItemType.houseType);
        }

        if (baseItemType.launcherType == null && launcherType != null) {
            // Remove
            launcherType = null;
        } else if (launcherType != null) {
            launcherType.changeTo(baseItemType.launcherType);
        }
    }
}
