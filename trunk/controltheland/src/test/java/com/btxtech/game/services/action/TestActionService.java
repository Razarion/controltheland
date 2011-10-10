package com.btxtech.game.services.action;

import com.btxtech.game.jsre.client.MovableService;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.tutorial.TutorialConfig;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.collision.CollisionService;
import com.btxtech.game.services.item.ItemService;
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
    private MovableService movableService;
    @Autowired
    private UserService userService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private CollisionService collisionService;


    @Test
    @DirtiesContext
    public void testLogoutDuringBuild() throws Exception {
        configureMinimalGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        movableService.sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "", "", 0, 0);
        Id target = getFirstSynItemId(TEST_START_BUILDER_ITEM_ID);
        sendBuildCommand(target, new Index(500, 100), TEST_FACTORY_ITEM_ID);

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        waitForActionServiceDone();
        Assert.assertEquals(2, movableService.getAllSyncInfo().size());

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testLogoutDuringBuildRegUser() throws Exception {
        configureMinimalGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("U1", "test", "test", "test");
        userService.login("U1", "test");
        movableService.sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "", "", 0, 0);
        Id target = getFirstSynItemId(TEST_START_BUILDER_ITEM_ID);
        sendBuildCommand(target, new Index(500, 100), TEST_FACTORY_ITEM_ID);

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        waitForActionServiceDone();
        Assert.assertEquals(2, movableService.getAllSyncInfo().size());

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testAttackWithDestination() throws Exception {
        configureMinimalGame();

        // Target
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        movableService.sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "", "", 0, 0);
        Id target = getFirstSynItemId(TEST_START_BUILDER_ITEM_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Attacker
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        movableService.sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "", "", 0, 0);
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
