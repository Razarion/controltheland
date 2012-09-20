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

    DbMovableType getDbMovableType();

    void setDbMovableType(DbMovableType dbMovableType);

    DbWeaponType getDbWeaponType();

    void setDbWeaponType(DbWeaponType dbWeaponType);

    DbFactoryType getDbFactoryType();

    void setDbFactoryType(DbFactoryType dbFactoryType);

    DbHarvesterType getDbHarvesterType();

    void setDbHarvesterType(DbHarvesterType dbHarvesterType);

    DbBuilderType getDbBuilderType();

    void setDbBuilderType(DbBuilderType dbBuilderType);

    DbConsumerType getDbConsumerType();

    void setDbConsumerType(DbConsumerType dbConsumerType);

    DbGeneratorType getDbGeneratorType();

    void setDbGeneratorType(DbGeneratorType dbGeneratorType);

    DbSpecialType getDbSpecialType();

    void setDbSpecialType(DbSpecialType dbSpecialType);

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

    DbBoxItemType getDbBoxItemType();

    void setDbBoxItemType(DbBoxItemType dbBoxItemType);

    double getDropBoxPossibility();

    void setDropBoxPossibility(double dropBoxPossibility);

    int getBoxPickupRange();

    void setBoxPickupRange(int boxPickupRange);
}
