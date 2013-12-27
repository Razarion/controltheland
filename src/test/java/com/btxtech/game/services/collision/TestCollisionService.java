package com.btxtech.game.services.collision;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.gameengine.formation.AttackFormationItem;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.collision.PlaceCanNotBeFoundException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.services.planet.CollisionService;
import com.btxtech.game.services.planet.PlanetSystemService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.List;

/**
 * User: beat
 * Date: Jul 19, 2009
 * Time: 5:46:45 PM
 */
public class TestCollisionService extends AbstractServiceTest {
    @Autowired
    private PlanetSystemService planetSystemService;
    @Autowired
    private ServerItemTypeService serverItemTypeService;

    @Test
    @DirtiesContext
    public void testRandomPositionInRectangleWithBot() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        setupMinimalBot(TEST_PLANET_1_ID, new Rectangle(1, 1, 3000, 3000));
        setupMinimalBot(TEST_PLANET_1_ID, new Rectangle(4000, 4000, 3000, 3000));

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        ItemType itemType = serverItemTypeService.getItemType(TEST_START_BUILDER_ITEM_ID);
        CollisionService collisionService = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getCollisionService();
        collisionService.getFreeRandomPosition(itemType, new Rectangle(8000, 8000, 1000, 1000), 100, true, false);
        try {
            collisionService.getFreeRandomPosition(itemType, new Rectangle(4050, 4050, 1000, 1000), 100, true, false);
            Assert.fail("Exception expected");
        } catch (PlaceCanNotBeFoundException e) {
            // Expceted
        }
    }

    @Test
    @DirtiesContext
    public void testSetupDestinationHints() throws Throwable {
        configureSimplePlanet();

        SyncBaseItem target = createSyncBaseItem(TEST_SIMPLE_BUILDING_ID, new Index(1000, 200), new Id(1, -100));
        SyncBaseItem attacker = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(200, 200), new Id(2, -100));

        List<AttackFormationItem> items = new ArrayList<>();
        items.add(new AttackFormationItem(attacker, 100));

        planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getCollisionService().setupDestinationHints(target, items);
        AttackFormationItem attackFormationItem = items.get(0);
        Assert.assertTrue(attackFormationItem.isInRange());
        Assert.assertEquals(new Index(741, 200), attackFormationItem.getDestinationHint());
    }
}
