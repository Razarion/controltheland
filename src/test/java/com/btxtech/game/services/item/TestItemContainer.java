package com.btxtech.game.services.item;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.MathHelper;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItemContainer;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.planet.PlanetSystemService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Arrays;

/**
 * User: beat
 * Date: 26.05.2011
 * Time: 17:10:10
 */
public class TestItemContainer extends AbstractServiceTest {
    @Autowired
    private PlanetSystemService planetSystemService;

    // TODO Test the land only container

    @Test
    @DirtiesContext
    public void atLeastOneAllowedToLoad() throws Exception {
        configureOneLevelOnePlanetComplexTerrain2();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        setupItems();
        // Get the items
        Id containerId = getFirstSynItemId(TEST_WATER_CONTAINER_ITEM_ID);
        SyncItemContainer syncItemContainer = getSynBaseItem(TEST_PLANET_1_ID, containerId).getSyncItemContainer();
        SyncBaseItem attackSyncItem = getSynBaseItem(TEST_PLANET_1_ID, getFirstSynItemId(TEST_ATTACK_ITEM_ID));
        SyncBaseItem harvesterSyncItem = getSynBaseItem(TEST_PLANET_1_ID, getFirstSynItemId(TEST_HARVESTER_ITEM_ID));
        SyncBaseItem builderSyncItem = getSynBaseItem(TEST_PLANET_1_ID, getFirstSynItemId(TEST_START_BUILDER_ITEM_ID));
        // Move to position where load is not allowed
        sendMoveCommand(containerId, new Index(3500, 400));
        waitForActionServiceDone();
        Assert.assertFalse(syncItemContainer.atLeastOneAllowedToLoad(Arrays.asList(attackSyncItem, harvesterSyncItem, builderSyncItem)));
        Assert.assertFalse(syncItemContainer.atLeastOneAllowedToLoad(Arrays.asList(attackSyncItem, builderSyncItem)));
        Assert.assertFalse(syncItemContainer.atLeastOneAllowedToLoad(Arrays.asList(harvesterSyncItem)));
        // Move to position where load is allowed
        sendMoveCommand(containerId, new Index(2500, 1800));
        waitForActionServiceDone();
        Assert.assertTrue(syncItemContainer.atLeastOneAllowedToLoad(Arrays.asList(attackSyncItem, harvesterSyncItem, builderSyncItem)));
        Assert.assertTrue(syncItemContainer.atLeastOneAllowedToLoad(Arrays.asList(attackSyncItem, builderSyncItem)));
        Assert.assertFalse(syncItemContainer.atLeastOneAllowedToLoad(Arrays.asList(harvesterSyncItem)));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testLoad() throws Exception {
        configureOneLevelOnePlanetComplexTerrain2();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        setupItems();
        // Get the items
        Id containerId = getFirstSynItemId(TEST_WATER_CONTAINER_ITEM_ID);
        SyncItemContainer syncItemContainer = getSynBaseItem(TEST_PLANET_1_ID, containerId).getSyncItemContainer();
        // Load container
        sendMoveCommand(containerId, new Index(2500, 1800));
        waitForActionServiceDone();
        sendContainerLoadCommand(getFirstSynItemId(TEST_ATTACK_ITEM_ID), getFirstSynItemId(TEST_WATER_CONTAINER_ITEM_ID), new Index(2330, 1800), MathHelper.EIGHTH_RADIANT);
        waitForActionServiceDone();
        Assert.assertEquals(1, syncItemContainer.getContainedItems().size());
        Assert.assertTrue(syncItemContainer.getContainedItems().containsAll(Arrays.asList(getFirstSynItemId(TEST_ATTACK_ITEM_ID))));
        // Load second unit
        sendContainerLoadCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), getFirstSynItemId(TEST_WATER_CONTAINER_ITEM_ID), new Index(2330, 1800), MathHelper.EIGHTH_RADIANT);
        waitForActionServiceDone();
        Assert.assertEquals(2, syncItemContainer.getContainedItems().size());
        Assert.assertTrue(syncItemContainer.getContainedItems().containsAll(Arrays.asList(getFirstSynItemId(TEST_ATTACK_ITEM_ID), getFirstSynItemId(TEST_START_BUILDER_ITEM_ID))));
        // Verify items
        SyncBaseItem attackSyncItem = getSynBaseItem(TEST_PLANET_1_ID, getFirstSynItemId(TEST_ATTACK_ITEM_ID));
        SyncBaseItem builderSyncItem = getSynBaseItem(TEST_PLANET_1_ID, getFirstSynItemId(TEST_START_BUILDER_ITEM_ID));
        Assert.assertNull(attackSyncItem.getSyncItemArea().getPosition());
        Assert.assertTrue(attackSyncItem.isContainedIn());
        Assert.assertNull(builderSyncItem.getSyncItemArea().getPosition());
        Assert.assertTrue(builderSyncItem.isContainedIn());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void atLeastOneAllowedToUnload() throws Exception {
        configureOneLevelOnePlanetComplexTerrain2();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        setupItems();
        // Get the items
        Id containerId = getFirstSynItemId(TEST_WATER_CONTAINER_ITEM_ID);
        SyncItemContainer syncItemContainer = getSynBaseItem(TEST_PLANET_1_ID, containerId).getSyncItemContainer();
        // Load container
        sendMoveCommand(containerId, new Index(2500, 1800));
        waitForActionServiceDone();
        sendContainerLoadCommand(getFirstSynItemId(TEST_ATTACK_ITEM_ID), getFirstSynItemId(TEST_WATER_CONTAINER_ITEM_ID), new Index(2330, 1800), MathHelper.EIGHTH_RADIANT);
        sendContainerLoadCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), getFirstSynItemId(TEST_WATER_CONTAINER_ITEM_ID), new Index(2330, 1800), MathHelper.EIGHTH_RADIANT);
        waitForActionServiceDone();
        Assert.assertEquals(2, syncItemContainer.getContainedItems().size());
        // Move to wrong position
        sendMoveCommand(getFirstSynItemId(TEST_WATER_CONTAINER_ITEM_ID), new Index(4000, 1200));
        waitForActionServiceDone();
        Assert.assertFalse(syncItemContainer.atLeastOneAllowedToUnload(new Index(3600, 1000)));
        Assert.assertFalse(syncItemContainer.atLeastOneAllowedToUnload(new Index(2550, 1600)));
        Assert.assertFalse(syncItemContainer.atLeastOneAllowedToUnload(new Index(1400, 1600)));
        // Good position
        sendMoveCommand(getFirstSynItemId(TEST_WATER_CONTAINER_ITEM_ID), new Index(2550, 1700));
        waitForActionServiceDone();
        Assert.assertTrue(syncItemContainer.atLeastOneAllowedToUnload(new Index(2450, 1700)));
        Assert.assertFalse(syncItemContainer.atLeastOneAllowedToUnload(new Index(2650, 1700)));
        Assert.assertFalse(syncItemContainer.atLeastOneAllowedToUnload(new Index(2550, 1600)));

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testUnload() throws Exception {
        configureOneLevelOnePlanetComplexTerrain2();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        setupItems();
        // Get the items
        Id containerId = getFirstSynItemId(TEST_WATER_CONTAINER_ITEM_ID);
        SyncItemContainer syncItemContainer = getSynBaseItem(TEST_PLANET_1_ID, containerId).getSyncItemContainer();
        // Load container
        sendMoveCommand(containerId, new Index(2500, 1800));
        waitForActionServiceDone();
        sendContainerLoadCommand(getFirstSynItemId(TEST_ATTACK_ITEM_ID), getFirstSynItemId(TEST_WATER_CONTAINER_ITEM_ID), new Index(2330, 1800), MathHelper.EIGHTH_RADIANT);
        sendContainerLoadCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), getFirstSynItemId(TEST_WATER_CONTAINER_ITEM_ID), new Index(2330, 1800), MathHelper.EIGHTH_RADIANT);
        waitForActionServiceDone();
        Assert.assertEquals(2, syncItemContainer.getContainedItems().size());
        SyncBaseItem attackSyncItem = getSynBaseItem(TEST_PLANET_1_ID, getFirstSynItemId(TEST_ATTACK_ITEM_ID));
        SyncBaseItem builderSyncItem = getSynBaseItem(TEST_PLANET_1_ID, getFirstSynItemId(TEST_START_BUILDER_ITEM_ID));
        // Wrong position 1
        sendMoveCommand(getFirstSynItemId(TEST_WATER_CONTAINER_ITEM_ID), new Index(2750, 1700));
        waitForActionServiceDone();
        sendUnloadContainerCommand(containerId, new Index(2370, 1700));
        waitForActionServiceDone();
        Assert.assertEquals(2, syncItemContainer.getContainedItems().size());
        // Verify items
        Assert.assertNull(attackSyncItem.getSyncItemArea().getPosition());
        Assert.assertTrue(attackSyncItem.isContainedIn());
        Assert.assertNull(builderSyncItem.getSyncItemArea().getPosition());
        Assert.assertTrue(builderSyncItem.isContainedIn());
        // Correct position but range too long
        sendMoveCommand(getFirstSynItemId(TEST_WATER_CONTAINER_ITEM_ID), new Index(2550, 1700));
        waitForActionServiceDone();
        sendUnloadContainerCommand(containerId, new Index(500, 1700));
        waitForActionServiceDone();
        Assert.assertEquals(2, syncItemContainer.getContainedItems().size());
        // Verify items
        Assert.assertNull(attackSyncItem.getSyncItemArea().getPosition());
        Assert.assertTrue(attackSyncItem.isContainedIn());
        Assert.assertNull(builderSyncItem.getSyncItemArea().getPosition());
        Assert.assertTrue(builderSyncItem.isContainedIn());
        // Good position
        sendMoveCommand(getFirstSynItemId(TEST_WATER_CONTAINER_ITEM_ID), new Index(2550, 1700));
        waitForActionServiceDone();
        sendUnloadContainerCommand(containerId, new Index(2370, 1700));
        waitForActionServiceDone();
        Assert.assertEquals(0, syncItemContainer.getContainedItems().size());
        // Verify items
        Assert.assertEquals(new Index(2370, 1700), attackSyncItem.getSyncItemArea().getPosition());
        Assert.assertFalse(attackSyncItem.isContainedIn());
        Assert.assertEquals(new Index(2370, 1700), builderSyncItem.getSyncItemArea().getPosition());
        Assert.assertFalse(builderSyncItem.isContainedIn());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    private void setupItems() throws Exception {
        createBase(new Index(1200, 900));
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(2699, 1500), TEST_HARBOR_TYPE_ID);
        waitForActionServiceDone();
        sendFactoryCommand(getFirstSynItemId(TEST_HARBOR_TYPE_ID), TEST_WATER_CONTAINER_ITEM_ID);
        waitForActionServiceDone();
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(1400, 500), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        sendFactoryCommand(getFirstSynItemId(TEST_FACTORY_ITEM_ID), TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone();
        sendFactoryCommand(getFirstSynItemId(TEST_FACTORY_ITEM_ID), TEST_HARVESTER_ITEM_ID);
        waitForActionServiceDone();
    }
}

