package com.btxtech.game.services.collision;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.Territory;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.territory.TerritoryService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

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

    @Test
    @DirtiesContext
    public void testRandomPositionInRectangleWithBot() throws Exception {
        configureMinimalGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        setupMinimalBot(new Rectangle(1, 1, 3000, 3000), new Rectangle(1000, 1000, 1000, 1000));
        setupMinimalBot(new Rectangle(4000, 4000, 3000, 3000), new Rectangle(5000, 5000, 1000, 1000));

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        ItemType itemType = itemService.getItemType(TEST_START_BUILDER_ITEM_ID);
        collisionService.getFreeRandomPosition(itemType, new Rectangle(8000, 8000, 1000, 1000), 100, true);
        try {
            collisionService.getFreeRandomPosition(itemType, new Rectangle(4050, 4050, 1000, 1000), 100, true);
            Assert.fail("Exception expected");
        } catch (IllegalStateException e) {
            // Expceted
        }
    }

    @Test
    @DirtiesContext
    public void testRandomPositionInTerritory() throws Exception {
        configureComplexGame();

        Rectangle noobTerrain = new Rectangle(0, 0, 1599, 1799);
        Rectangle terrainImage1 = new Rectangle(0, 1300, 1000, 400);
        Rectangle terrainImage2 = new Rectangle(1000, 0, 400, 1000);

        ItemType itemType = itemService.getItemType(TEST_START_BUILDER_ITEM_ID);
        Territory territory = territoryService.getTerritory(COMPLEX_TERRITORY_ID);
        // 16 * 18 Tiles (100*100) = 288 Possible position for gold (100*100)
        // Minus Terrain Images 4*10 + 10*4 = 80
        // 208 possible positions if placed nicely
        // In the worst case only a quarter will be used -> 52
        // -> 100

        for (int i = 0; i < 100; i++) {
            Index index = collisionService.getFreeRandomPosition(itemType, territory, 100, false);
            Assert.assertEquals(COMPLEX_TERRITORY_ID, territoryService.getTerritory(index).getId());
            SyncItem syncItem = itemService.createSyncObject(itemService.getItemType(TEST_RESOURCE_ITEM_ID), index, null, null, 0);
            System.out.println("Gold Placed: " + i + " at: " + syncItem.getPosition());
            Assert.assertFalse(terrainImage1.adjoinsEclusive(syncItem.getRectangle()));
            Assert.assertFalse(terrainImage2.adjoinsEclusive(syncItem.getRectangle()));
            Assert.assertTrue(noobTerrain.adjoinsEclusive(syncItem.getRectangle()));
        }
    }
}
