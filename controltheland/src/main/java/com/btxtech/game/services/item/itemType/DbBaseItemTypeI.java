package com.btxtech.game.services.item.itemType;

/**
 * User: beat
 * Date: 16.03.2011
 * Time: 10:41:55
 */
public interface DbBaseItemTypeI extends DbItemTypeI {
    int getHealth();

    void setHealth(int health);

    int getPrice();

    void setPrice(int price);

    int getBuildup();

    void setBuildup(int buildup);

    DbTurnableType getTurnableType();

    void setTurnableType(DbTurnableType dbTurnableType);

    DbMovableType getDbMovableType();

    void setDbMovableType(DbMovableType dbMovableType);

    DbWeaponType getWeaponType();

    void setWeaponType(DbWeaponType dbWeaponType);

    DbFactoryType getFactoryType();

    void setFactoryType(DbFactoryType dbFactoryType);

    DbHarvesterType getHarvesterType();

    void setHarvesterType(DbHarvesterType dbHarvesterType);

    DbBuilderType getBuilderType();

    void setBuilderType(DbBuilderType dbBuilderType);

    DbConsumerType getConsumerType();

    void setConsumerType(DbConsumerType dbConsumerType);

    DbGeneratorType getGeneratorType();

    void setGeneratorType(DbGeneratorType dbGeneratorType);

    DbSpecialType getSpecialType();

    void setSpecialType(DbSpecialType dbSpecialType);

    DbItemContainerType getDbItemContainerType();

    void setDbItemContainerType(DbItemContainerType dbItemContainerType);

    DbHouseType getDbHouseType();

    void setDbHouseType(DbHouseType dbHouseType);

    DbBaseItemType getUpgradable();

    void setUpgradable(DbBaseItemType upgradable);

    Integer getUpgradeProgress();

    void setUpgradeProgress(Integer upgradeProgress);

    DbLauncherType getDbLauncherType();

    void setDbLauncherType(DbLauncherType dbLauncherType);
}
