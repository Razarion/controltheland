package com.btxtech.game.services.action;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.user.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

/**
 * User: beat
 * Date: 04.04.2011
 * Time: 22:57:38
 */
public class TestActionService extends AbstractServiceTest {
    @Autowired
    private UserService userService;

    @Test
    @DirtiesContext
    public void testLogoutDuringBuild() throws Exception {
        configureSimplePlanet();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        Id target = getFirstSynItemId(TEST_START_BUILDER_ITEM_ID);
        sendBuildCommand(target, new Index(500, 100), TEST_FACTORY_ITEM_ID);

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        waitForActionServiceDone();
        Assert.assertEquals(2, getMovableService().getAllSyncInfo().size());

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testLogoutDuringBuildRegUser() throws Exception {
        configureSimplePlanet();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("U1", "test", "test", "test");
        userService.login("U1", "test");
        Id target = getFirstSynItemId(TEST_START_BUILDER_ITEM_ID);
        sendBuildCommand(target, new Index(500, 100), TEST_FACTORY_ITEM_ID);

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        waitForActionServiceDone();
        Assert.assertEquals(2, getMovableService().getAllSyncInfo().size());

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testAttackWithDestination() throws Exception {
        configureSimplePlanet();

        // Target
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Id target = getFirstSynItemId(TEST_START_BUILDER_ITEM_ID);
        sendMoveCommand(target, new Index(2000,2000));
        waitForActionServiceDone();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Attacker
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(500, 100), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(500, 100), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(500, 100), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        sendFactoryCommand(getFirstSynItemId(TEST_FACTORY_ITEM_ID), TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone();
        sendAttackCommands(getAllSynItemId(TEST_ATTACK_ITEM_ID), target);
        waitForActionServiceDone();

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }
}
