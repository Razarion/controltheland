package com.btxtech.game.services.planet;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.services.AbstractServiceTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;

/**
 * User: beat
 * Date: 24.09.12
 * Time: 12:28
 */
public class TestResourceService extends AbstractServiceTest {

    @Test
    @DirtiesContext
    public void rebuildAfterDead() throws Exception {
        configureSimplePlanet();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(1000, 1000), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone(TEST_PLANET_1_ID);
        sendFactoryCommand(getFirstSynItemId(TEST_FACTORY_ITEM_ID), TEST_HARVESTER_ITEM_ID);
        waitForActionServiceDone(TEST_PLANET_1_ID);
        Id harvester = getFirstSynItemId(TEST_HARVESTER_ITEM_ID);
        Id resource1 = getFirstResourceItem(TEST_PLANET_1_ID, TEST_RESOURCE_ITEM_ID);
        Assert.assertEquals(1, getAllResourceItems(TEST_PLANET_1_ID, TEST_RESOURCE_ITEM_ID).size());
        sendCollectCommand(harvester, resource1);
        waitForActionServiceDone();
        Thread.sleep(500);
        Id resource2 = getFirstResourceItem(TEST_PLANET_1_ID, TEST_RESOURCE_ITEM_ID);
        Assert.assertEquals(1, getAllResourceItems(TEST_PLANET_1_ID, TEST_RESOURCE_ITEM_ID).size());
        Assert.assertFalse(resource1.equals(resource2));
        sendCollectCommand(harvester, resource2);
        waitForActionServiceDone();
        Thread.sleep(500);
        Id resource3 = getFirstResourceItem(TEST_PLANET_1_ID, TEST_RESOURCE_ITEM_ID);
        Assert.assertEquals(1, getAllResourceItems(TEST_PLANET_1_ID, TEST_RESOURCE_ITEM_ID).size());
        Assert.assertFalse(resource2.equals(resource3));
        Assert.assertFalse(resource1.equals(resource3));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }
}
