package com.btxtech.game.services.collision;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.formation.AttackFormationItem;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.common.ServerPlanetServices;
import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.planet.db.DbPlanet;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.List;

/**
 * User: beat
 * Date: 18.06.12
 * Time: 23:46
 */
public class TestPositionTaken extends AbstractServiceTest {
    @Autowired
    private ServerItemTypeService serverItemTypeService;
    @Autowired
    private PlanetSystemService planetSystemService;

    @Test
    @DirtiesContext
    public void testMultipleAttackNoOverlapping() throws Exception {
        configureSimplePlanetNoResources();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbPlanet dbPlanet = planetSystemService.getDbPlanetCrud().readDbChild(TEST_PLANET_1_ID);
        dbPlanet.setHouseSpace(1000);
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet);
        planetSystemService.deactivatePlanet(TEST_PLANET_1_ID);
        planetSystemService.activatePlanet(TEST_PLANET_1_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        ServerPlanetServices serverPlanetServices = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Id target = getFirstSynItemId(TEST_START_BUILDER_ITEM_ID);
        sendMoveCommand(target, new Index(5000, 5000));
        waitForActionServiceDone();
        SyncBaseItem targetItem = (SyncBaseItem) serverPlanetServices.getItemService().getItem(target);
        targetItem.setHealth(100000);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(400, 400), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();

        for (int i = 0; i < 9; i++) {
            sendFactoryCommand(getFirstSynItemId(TEST_FACTORY_ITEM_ID), TEST_ATTACK_ITEM_ID);
            waitForActionServiceDone();
        }

        List<Id> allIds = getAllSynItemId(TEST_ATTACK_ITEM_ID);
        Assert.assertEquals(9, allIds.size());

        int range = ((BaseItemType) serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID)).getWeaponType().getRange();
        List<AttackFormationItem> attackFormationItemList = new ArrayList<>();
        for (Id id : allIds) {
            attackFormationItemList.add(new AttackFormationItem((SyncBaseItem)  serverPlanetServices.getItemService().getItem(id), range));
        }
        attackFormationItemList = serverPlanetServices.getCollisionService().setupDestinationHints(targetItem, attackFormationItemList);
        for (AttackFormationItem attackFormationItem : attackFormationItemList) {
            Assert.assertTrue(attackFormationItem.isInRange());
            serverPlanetServices.getActionService().attack(attackFormationItem.getSyncBaseItem(),
                    targetItem,
                    attackFormationItem.getDestinationHint(),
                    attackFormationItem.getDestinationAngel(),
                    true);
        }
        waitForActionServiceDone();
        // Verify position
        for (AttackFormationItem attackFormationItem : attackFormationItemList) {
            Assert.assertEquals("Position do not match " + attackFormationItem.getSyncBaseItem(), attackFormationItem.getDestinationHint(), attackFormationItem.getSyncBaseItem().getSyncItemArea().getPosition());
            Assert.assertEquals("Angel do not match " + attackFormationItem.getSyncBaseItem(), attackFormationItem.getDestinationAngel(), attackFormationItem.getSyncBaseItem().getSyncItemArea().getAngel(), 0.0001);
        }

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }
}
