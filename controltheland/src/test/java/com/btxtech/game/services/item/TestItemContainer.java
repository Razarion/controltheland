package com.btxtech.game.services.item;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItemContainer;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.collision.CollisionService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

/**
 * User: beat
 * Date: 26.05.2011
 * Time: 17:10:10
 */
public class TestItemContainer extends AbstractServiceTest {
    @Autowired
    private CollisionService collisionService;
    @Autowired
    private ItemService itemService;

    @Test
    @DirtiesContext
    public void testLoadUnloadContainer() throws Exception {
        configureRealGame();

        System.out.println("**** testLoadContainer ****");
        beginHttpSession();

        beginHttpRequestAndOpenSessionInViewFilter();
        SimpleBase actorBase = getMyBase();
        Id builderId = getFirstSynItemId(actorBase, TEST_START_BUILDER_ITEM_ID);
        SyncBaseItem builder = (SyncBaseItem) itemService.getItem(builderId);

        // Create container
        sendBuildCommand(builderId, new Index(300, 300), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        sendFactoryCommand(getFirstSynItemId(actorBase, TEST_FACTORY_ITEM_ID), TEST_CONTAINER_ITEM_ID);
        waitForActionServiceDone();
        Id containerId = getFirstSynItemId(actorBase, TEST_CONTAINER_ITEM_ID);
        SyncBaseItem container = (SyncBaseItem) itemService.getItem(containerId);
        SyncItemContainer syncItemContainer = container.getSyncItemContainer();

        sendMoveCommand(containerId, new Index(5000, 5000));
        waitForActionServiceDone();
        Assert.assertEquals(0, syncItemContainer.getContainedItems().size());
        Assert.assertFalse(builder.isContainedIn());
        Assert.assertTrue(builder.getSyncItemArea().hasPosition());

        // Load
        sendContainerLoadCommand(builderId, containerId);
        waitForActionServiceDone();
        Assert.assertEquals(1, syncItemContainer.getContainedItems().size());
        Assert.assertEquals(builderId, syncItemContainer.getContainedItems().get(0));
        Assert.assertTrue(builder.isContainedIn());
        Assert.assertFalse(builder.getSyncItemArea().hasPosition());

        // Unload
        sendUnloadContainerCommand(containerId, new Index(8000, 8000));
        waitForActionServiceDone();
        Assert.assertEquals(0, syncItemContainer.getContainedItems().size());
        Assert.assertFalse(builder.isContainedIn());
        Assert.assertEquals(new Index(8000, 8000), builder.getSyncItemArea().getPosition());

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

}

