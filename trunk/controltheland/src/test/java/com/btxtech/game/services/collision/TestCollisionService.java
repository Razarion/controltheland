package com.btxtech.game.services.collision;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.Territory;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.collision.PlaceCanNotBeFoundException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.debug.DebugService;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.territory.TerritoryService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.awt.*;

/**
 * User: beat
 * Date: Jul 19, 2009
 * Time: 5:46:45 PM
 */
public class TestCollisionService extends AbstractServiceTest {
    @Autowired
    private CollisionService collisionService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private TerritoryService territoryService;
    @Autowired
    private DebugService debugService;


    @Test
    @DirtiesContext
    public void testRandomPositionInRectangleWithBot() throws Exception {
        configureMinimalGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        setupMinimalBot(new Rectangle(1, 1, 3000, 3000));
        setupMinimalBot(new Rectangle(4000, 4000, 3000, 3000));

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        ItemType itemType = itemService.getItemType(TEST_START_BUILDER_ITEM_ID);
        collisionService.getFreeRandomPosition(itemType, new Rectangle(8000, 8000, 1000, 1000), 100, true, false);
        try {
            collisionService.getFreeRandomPosition(itemType, new Rectangle(4050, 4050, 1000, 1000), 100, true, false);
            Assert.fail("Exception expected");
        } catch (PlaceCanNotBeFoundException e) {
            // Expceted
        }
    }

    // TODO @Test
    @DirtiesContext
    public void testRandomPositionInTerritory() throws Exception {
        configureComplexGameOneRealLevel();

        Rectangle noobTerrain = new Rectangle(0, 0, 1599, 1799);
        Rectangle terrainImage1 = new Rectangle(0, 1300, 1000, 400);
        Rectangle terrainImage2 = new Rectangle(1000, 0, 400, 1000);

        ItemType goldItemType = itemService.getItemType(TEST_START_BUILDER_ITEM_ID);
        Territory territory = territoryService.getTerritory(COMPLEX_TERRITORY_ID);
        // 16 * 18 Tiles (100*100) = 288 Possible position for gold (100*100)
        // Minus Terrain Images 4*10 + 10*4 = 80
        // 208 possible positions if placed nicely
        // In the worst case only a quarter will be used -> 52
        // -> 100

        debugService.drawRectangle(noobTerrain, Color.BLACK);
        debugService.drawRectangle(terrainImage1, Color.BLUE);
        debugService.drawRectangle(terrainImage2, Color.BLUE);

        for (int i = 0; i < 100; i++) {
            Index index = collisionService.getFreeRandomPosition(goldItemType, territory, 100, false);
            Assert.assertEquals(COMPLEX_TERRITORY_ID, territoryService.getTerritory(index).getId());
            SyncItem gold = itemService.createSyncObject(itemService.getItemType(TEST_RESOURCE_ITEM_ID), index, null, null, 0);
            //System.out.println("Gold Placed: " + i + " at: " + gold.getSyncItemArea().getPosition());
            debugService.drawSyncItemArea(gold.getSyncItemArea(), Color.RED);
            //Assert.assertFalse("terrainImage1 overlapped", gold.getSyncItemArea().contains(terrainImage1));
            //Assert.assertFalse("terrainImage2 overlapped", gold.getSyncItemArea().contains(terrainImage2));
            //Assert.assertTrue("noobTerrain overlapped", gold.getSyncItemArea().contains(noobTerrain));

            if (gold.getSyncItemArea().contains(terrainImage1)) {
                System.out.println("** Overlapping terrainImage1: " + gold.getSyncItemArea());
            }
            if (gold.getSyncItemArea().contains(terrainImage2)) {
                System.out.println("** Overlapping terrainImage2: " + gold.getSyncItemArea());
            }
            if (!gold.getSyncItemArea().contains(noobTerrain)) {
                System.out.println("** Not in terrain: " + gold.getSyncItemArea());
            }
        }
        debugService.waitForClose();
    }
}
