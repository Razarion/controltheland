package com.btxtech.game.jsre.common.gameengine.itemType;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.item.itemType.DbWeaponType;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

/**
 * User: beat
 * Date: 18.10.12
 * Time: 15:45
 */
public class TestSyncWeapon extends AbstractServiceTest {
    @Autowired
    private ServerItemTypeService serverItemTypeService;

    @Test
    @DirtiesContext
    public void projectileAttack() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBaseItemType attackerType = (DbBaseItemType) serverItemTypeService.getDbItemTypeCrud().readDbChild(TEST_ATTACK_ITEM_ID);
        DbWeaponType dbWeaponType = attackerType.getDbWeaponType();
        dbWeaponType.setProjectileSpeed(100);
        serverItemTypeService.getDbItemTypeCrud().updateDbChild(attackerType);
        serverItemTypeService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Create Target
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Id target = getFirstSynItemId(TEST_START_BUILDER_ITEM_ID);
        sendMoveCommand(target, new Index(1000, 1000));
        waitForActionServiceDone();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Create Attacker
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(2000, 2000), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        sendFactoryCommand(getFirstSynItemId(TEST_FACTORY_ITEM_ID), TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone();
        assertWholeItemCount(TEST_PLANET_1_ID, 4);
        sendAttackCommand(getFirstSynItemId(TEST_ATTACK_ITEM_ID),target);
        waitForActionServiceDone();
        assertWholeItemCount(TEST_PLANET_1_ID, 3);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }
}
