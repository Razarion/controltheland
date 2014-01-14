package com.btxtech.game.jsre.common.gameengine.itemType;

import com.btxtech.game.jsre.client.common.info.RealGameInfo;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainType;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.item.itemType.DbMovableType;
import com.btxtech.game.services.item.itemType.DbWeaponType;
import com.btxtech.game.services.item.itemType.DbWeaponTypeItemTypeFactor;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Arrays;

/**
 * User: beat
 * Date: 13.01.14
 * Time: 23:15
 */
public class TestDbWeaponType extends AbstractServiceTest {
    @Autowired
    private ServerItemTypeService serverItemTypeService;

    @Test
    @DirtiesContext
    public void testCreationWeaponType() throws Exception {
        configureSimplePlanet();
        // Create attacker 1
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBaseItemType dbAttacker1 = (DbBaseItemType) serverItemTypeService.getDbItemTypeCrud().createDbChild(DbBaseItemType.class);
        dbAttacker1.setTerrainType(TerrainType.LAND);
        dbAttacker1.setBounding(new BoundingBox(80, ANGELS_24));
        dbAttacker1.setHealth(10);
        dbAttacker1.setBuildup(10);
        dbAttacker1.setPrice(3);
        dbAttacker1.setImageWidth(80);
        dbAttacker1.setImageHeight(100);
        dbAttacker1.setConsumingHouseSpace(2);
        // DbWeaponType
        DbWeaponType dbWeaponType = new DbWeaponType();
        dbWeaponType.setRange(100);
        dbWeaponType.setReloadTime(1);
        dbWeaponType.setDamage(1000);
        DbWeaponTypeItemTypeFactor typeFactor = dbWeaponType.getFactorCrud().createDbChild();
        typeFactor.setDbBaseItemType(serverItemTypeService.getDbBaseItemType(TEST_HOUSE_ID));
        typeFactor.setFactor(0.5);
        typeFactor = dbWeaponType.getFactorCrud().createDbChild();
        typeFactor.setDbBaseItemType(serverItemTypeService.getDbBaseItemType(TEST_ATTACK_ITEM_ID));
        typeFactor.setFactor(1.5);
        dbWeaponType.setDisallowedItemTypes(Arrays.asList(serverItemTypeService.getDbBaseItemType(TEST_HARVESTER_ITEM_ID), serverItemTypeService.getDbBaseItemType(TEST_START_BUILDER_ITEM_ID)));
        dbAttacker1.setDbWeaponType(dbWeaponType);
        // DbMovableType
        DbMovableType dbMovableType = new DbMovableType();
        dbMovableType.setSpeed(10000);
        dbAttacker1.setDbMovableType(dbMovableType);
        serverItemTypeService.getDbItemTypeCrud().updateDbChild(dbAttacker1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Create attacker 2
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBaseItemType dbBttacker2 = (DbBaseItemType) serverItemTypeService.getDbItemTypeCrud().createDbChild(DbBaseItemType.class);
        dbBttacker2.setTerrainType(TerrainType.LAND);
        dbBttacker2.setBounding(new BoundingBox(80, ANGELS_24));
        dbBttacker2.setHealth(10);
        dbBttacker2.setBuildup(10);
        dbBttacker2.setPrice(3);
        dbBttacker2.setImageWidth(80);
        dbBttacker2.setImageHeight(100);
        dbBttacker2.setConsumingHouseSpace(2);
        // DbWeaponType
        dbWeaponType = new DbWeaponType();
        dbWeaponType.setReloadTime(2);
        dbWeaponType.setRange(101);
        dbWeaponType.setDamage(1020);
        dbWeaponType.setProjectileSpeed(1);
        dbBttacker2.setDbWeaponType(dbWeaponType);
        // DbMovableType
        serverItemTypeService.getDbItemTypeCrud().updateDbChild(dbBttacker2);
        serverItemTypeService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        RealGameInfo realGameInfo = getMovableService().getRealGameInfo(START_UID_1, TEST_PLANET_1_ID);
        BaseItemType attacker1 = getBaseItemType(realGameInfo, dbAttacker1.getId());
        Assert.assertEquals(100, attacker1.getWeaponType().getRange());
        Assert.assertEquals(1.0, attacker1.getWeaponType().getReloadTime(), 0.001);
        Assert.assertEquals(500.0, attacker1.getWeaponType().getDamage(getBaseItemType(realGameInfo, TEST_HOUSE_ID)));
        Assert.assertEquals(1500.0, attacker1.getWeaponType().getDamage(getBaseItemType(realGameInfo, TEST_ATTACK_ITEM_ID)));
        Assert.assertEquals(1000.0, attacker1.getWeaponType().getDamage(getBaseItemType(realGameInfo, TEST_CONTAINER_ITEM_ID)));
        Assert.assertEquals(1000.0, attacker1.getWeaponType().getDamage(getBaseItemType(realGameInfo, TEST_CONSUMER_TYPE_ID)));
        Assert.assertNull(attacker1.getWeaponType().getProjectileSpeed());
        Assert.assertFalse(attacker1.getWeaponType().isItemTypeDisallowed(TEST_HOUSE_ID));
        Assert.assertFalse(attacker1.getWeaponType().isItemTypeDisallowed(TEST_ATTACK_ITEM_ID));
        Assert.assertFalse(attacker1.getWeaponType().isItemTypeDisallowed(TEST_CONTAINER_ITEM_ID));
        Assert.assertFalse(attacker1.getWeaponType().isItemTypeDisallowed(TEST_CONSUMER_TYPE_ID));
        Assert.assertTrue(attacker1.getWeaponType().isItemTypeDisallowed(TEST_HARVESTER_ITEM_ID));
        Assert.assertTrue(attacker1.getWeaponType().isItemTypeDisallowed(TEST_START_BUILDER_ITEM_ID));

        BaseItemType attacker2 = getBaseItemType(realGameInfo, dbBttacker2.getId());
        Assert.assertEquals(101, attacker2.getWeaponType().getRange());
        Assert.assertEquals(2.0, attacker2.getWeaponType().getReloadTime(), 0.001);
        Assert.assertEquals(1020.0, attacker2.getWeaponType().getDamage(getBaseItemType(realGameInfo, TEST_HOUSE_ID)));
        Assert.assertEquals(1020.0, attacker2.getWeaponType().getDamage(getBaseItemType(realGameInfo, TEST_ATTACK_ITEM_ID)));
        Assert.assertEquals(1020.0, attacker2.getWeaponType().getDamage(getBaseItemType(realGameInfo, TEST_CONTAINER_ITEM_ID)));
        Assert.assertEquals(1020.0, attacker2.getWeaponType().getDamage(getBaseItemType(realGameInfo, TEST_CONSUMER_TYPE_ID)));
        Assert.assertEquals(1, (int)attacker2.getWeaponType().getProjectileSpeed());
        Assert.assertFalse(attacker2.getWeaponType().isItemTypeDisallowed(TEST_HOUSE_ID));
        Assert.assertFalse(attacker2.getWeaponType().isItemTypeDisallowed(TEST_ATTACK_ITEM_ID));
        Assert.assertFalse(attacker2.getWeaponType().isItemTypeDisallowed(TEST_CONTAINER_ITEM_ID));
        Assert.assertFalse(attacker2.getWeaponType().isItemTypeDisallowed(TEST_CONSUMER_TYPE_ID));
        Assert.assertFalse(attacker2.getWeaponType().isItemTypeDisallowed(TEST_HARVESTER_ITEM_ID));
        Assert.assertFalse(attacker2.getWeaponType().isItemTypeDisallowed(TEST_START_BUILDER_ITEM_ID));

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    private BaseItemType getBaseItemType(RealGameInfo realGameInfo, int id) {
        for (ItemType itemType : realGameInfo.getItemTypes()) {
            if (itemType.getId() == id) {
                return (BaseItemType) itemType;
            }
        }
        throw new IllegalArgumentException("No such item type wit id: " + id);
    }

}
