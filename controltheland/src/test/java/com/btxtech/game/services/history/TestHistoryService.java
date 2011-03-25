package com.btxtech.game.services.history;

import com.btxtech.game.jsre.client.MovableService;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.info.RealityInfo;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.tutorial.TutorialConfig;
import com.btxtech.game.services.BaseTestService;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.user.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

/**
 * User: beat
 * Date: 23.03.2011
 * Time: 15:46:35
 */
public class TestHistoryService extends BaseTestService {
    @Autowired
    private HistoryService historyService;
    @Autowired
    private BaseService baseService;
    @Autowired
    private UserService userService;
    @Autowired
    private MovableService movableService;
    @Autowired
    private ItemService itemService;

    @Test
    @DirtiesContext
    public void testCreateBaseLevel() throws Exception {
        configureMinimalGame();

        System.out.println("**** testHistoryService ****");
        beginHttpSession();
        // Create account
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("U1", "test", "test", "test");
        userService.login("U1", "test");
        endHttpRequestAndOpenSessionInViewFilter();
        // Finish tutorial
        beginHttpRequestAndOpenSessionInViewFilter();
        movableService.sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "xx", "xx", 0, 0);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DisplayHistoryElement> displayHistoryElements = historyService.getNewestHistoryElements(userService.getUser("U1"), 1000);

        System.out.println("----- History -----");
        for (DisplayHistoryElement displayHistoryElement : displayHistoryElements) {
            System.out.println(displayHistoryElement);
        }
        System.out.println("----- History End -----");

        Assert.assertEquals(3, displayHistoryElements.size());

        Assert.assertEquals("Item created: " + TEST_START_BUILDER_ITEM, displayHistoryElements.get(0).getMessage());

        Assert.assertTrue(displayHistoryElements.get(0).getTimeStamp().getTime() >= displayHistoryElements.get(1).getTimeStamp().getTime());
        Assert.assertEquals("Base created: U1", displayHistoryElements.get(1).getMessage());

        Assert.assertTrue(displayHistoryElements.get(1).getTimeStamp().getTime() >= displayHistoryElements.get(2).getTimeStamp().getTime());
        Assert.assertEquals("Level reached: " + TEST_REAL_GAME_CREATE_BASE_LEVEL, displayHistoryElements.get(2).getMessage());

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testCreateItem() throws Exception {
        configureMinimalGame();

        System.out.println("**** testCreateItem ****");
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("U1", "test", "test", "test");
        userService.login("U1", "test");
        movableService.sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "xx", "xx", 0, 0);
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        // Establish Connection
        movableService.getGameInfo();

        // Build Factory
        System.out.println("---- build unit ---");
        sendBuildCommand(movableService.getAllSyncInfo().iterator().next().getId(), new Index(100, 100), TEST_FACTORY_ITEM_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        waitForActionServiceDone(1000);

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DisplayHistoryElement> displayHistoryElements = historyService.getNewestHistoryElements(userService.getUser("U1"), 1000);

        System.out.println("----- History -----");
        for (DisplayHistoryElement displayHistoryElement : displayHistoryElements) {
            System.out.println(displayHistoryElement);
        }
        System.out.println("----- History End -----");

        Assert.assertEquals(4, displayHistoryElements.size());
        Assert.assertTrue(displayHistoryElements.get(0).getTimeStamp().getTime() >= displayHistoryElements.get(1).getTimeStamp().getTime());
        Assert.assertEquals("Item created: " + TEST_FACTORY_ITEM, displayHistoryElements.get(0).getMessage());


        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testKillItem() throws Exception {
        configureMinimalGame();

        System.out.println("**** testKillItem ****");
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("Target", "test", "test", "test");
        userService.login("Target", "test");
        movableService.sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "xx", "xx", 0, 0);
        SimpleBase targetBase = ((RealityInfo) movableService.getGameInfo()).getBase();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("Actor", "test", "test", "test");
        userService.login("Actor", "test");
        movableService.sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "xx", "xx", 0, 0);
        SimpleBase actorBase = ((RealityInfo) movableService.getGameInfo()).getBase();
        sendBuildCommand(getFirstSynItemId(actorBase, TEST_START_BUILDER_ITEM_ID), new Index(400, 400), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone(1000);
        sendFactoryCommand(getFirstSynItemId(actorBase, TEST_FACTORY_ITEM_ID), TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone(1000);
        sendAttackCommand(getFirstSynItemId(actorBase, TEST_ATTACK_ITEM_ID), getFirstSynItemId(targetBase, TEST_START_BUILDER_ITEM_ID));
        waitForActionServiceDone(1000000);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        List<DisplayHistoryElement> displayHistoryElements = historyService.getNewestHistoryElements(userService.getUser("Actor"), 1000);
        System.out.println("----- History Actor-----");
        for (DisplayHistoryElement displayHistoryElement : displayHistoryElements) {
            System.out.println(displayHistoryElement);
        }
        System.out.println("----- History End -----");
        Assert.assertEquals(7, displayHistoryElements.size());
        Assert.assertTrue(displayHistoryElements.get(1).getTimeStamp().getTime() >= displayHistoryElements.get(2).getTimeStamp().getTime());
        Assert.assertEquals("Destroyed a " + TEST_START_BUILDER_ITEM + " from Target", displayHistoryElements.get(1).getMessage());
        Assert.assertTrue(displayHistoryElements.get(0).getTimeStamp().getTime() >= displayHistoryElements.get(1).getTimeStamp().getTime());
        Assert.assertEquals("Base destroyed: Target", displayHistoryElements.get(0).getMessage());


        displayHistoryElements = historyService.getNewestHistoryElements(userService.getUser("Target"), 1000);
        System.out.println("----- History Target-----");
        for (DisplayHistoryElement displayHistoryElement : displayHistoryElements) {
            System.out.println(displayHistoryElement);
        }
        System.out.println("----- History End -----");
        Assert.assertEquals(5, displayHistoryElements.size());
        Assert.assertTrue(displayHistoryElements.get(1).getTimeStamp().getTime() >= displayHistoryElements.get(2).getTimeStamp().getTime());
        Assert.assertEquals("Actor destroyed your " + TEST_START_BUILDER_ITEM, displayHistoryElements.get(1).getMessage());
        Assert.assertTrue(displayHistoryElements.get(0).getTimeStamp().getTime() >= displayHistoryElements.get(1).getTimeStamp().getTime());
        Assert.assertEquals("Your base has been destroyed by Actor", displayHistoryElements.get(0).getMessage());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testKillAnonymousItem() throws Exception {
        configureMinimalGame();

        System.out.println("**** testKillAnonymousItem ****");
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        movableService.sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "xx", "xx", 0, 0);
        SimpleBase targetBase = ((RealityInfo) movableService.getGameInfo()).getBase();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("Actor", "test", "test", "test");
        userService.login("Actor", "test");
        movableService.sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "xx", "xx", 0, 0);
        SimpleBase actorBase = ((RealityInfo) movableService.getGameInfo()).getBase();
        sendBuildCommand(getFirstSynItemId(actorBase, TEST_START_BUILDER_ITEM_ID), new Index(400, 400), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone(1000);
        sendFactoryCommand(getFirstSynItemId(actorBase, TEST_FACTORY_ITEM_ID), TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone(1000);
        sendAttackCommand(getFirstSynItemId(actorBase, TEST_ATTACK_ITEM_ID), getFirstSynItemId(targetBase, TEST_START_BUILDER_ITEM_ID));
        waitForActionServiceDone(1000000);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        List<DisplayHistoryElement> displayHistoryElements = historyService.getNewestHistoryElements(userService.getUser("Actor"), 1000);
        System.out.println("----- History Actor-----");
        for (DisplayHistoryElement displayHistoryElement : displayHistoryElements) {
            System.out.println(displayHistoryElement);
        }
        System.out.println("----- History End -----");
        Assert.assertEquals(7, displayHistoryElements.size());
        Assert.assertTrue(displayHistoryElements.get(1).getTimeStamp().getTime() >= displayHistoryElements.get(2).getTimeStamp().getTime());
        Assert.assertEquals("Destroyed a " + TEST_START_BUILDER_ITEM + " from Base 1", displayHistoryElements.get(1).getMessage());
        Assert.assertTrue(displayHistoryElements.get(0).getTimeStamp().getTime() >= displayHistoryElements.get(1).getTimeStamp().getTime());
        Assert.assertEquals("Base destroyed: Base 1", displayHistoryElements.get(0).getMessage());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testKillByAnonymous() throws Exception {
        configureMinimalGame();

        System.out.println("**** testKillByAnonymous ****");
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("Target", "test", "test", "test");
        userService.login("Target", "test");
        movableService.sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "xx", "xx", 0, 0);
        SimpleBase targetBase = ((RealityInfo) movableService.getGameInfo()).getBase();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        movableService.sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "xx", "xx", 0, 0);
        SimpleBase actorBase = ((RealityInfo) movableService.getGameInfo()).getBase();
        sendBuildCommand(getFirstSynItemId(actorBase, TEST_START_BUILDER_ITEM_ID), new Index(400, 400), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone(1000);
        sendFactoryCommand(getFirstSynItemId(actorBase, TEST_FACTORY_ITEM_ID), TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone(1000);
        sendAttackCommand(getFirstSynItemId(actorBase, TEST_ATTACK_ITEM_ID), getFirstSynItemId(targetBase, TEST_START_BUILDER_ITEM_ID));
        waitForActionServiceDone(1000000);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        List<DisplayHistoryElement> displayHistoryElements = historyService.getNewestHistoryElements(userService.getUser("Target"), 1000);
        System.out.println("----- Target Target-----");
        for (DisplayHistoryElement displayHistoryElement : displayHistoryElements) {
            System.out.println(displayHistoryElement);
        }
        System.out.println("----- Target End -----");
        Assert.assertEquals(5, displayHistoryElements.size());
        Assert.assertTrue(displayHistoryElements.get(1).getTimeStamp().getTime() >= displayHistoryElements.get(2).getTimeStamp().getTime());
        Assert.assertEquals("Base 2 destroyed your " + TEST_START_BUILDER_ITEM, displayHistoryElements.get(1).getMessage());
        Assert.assertTrue(displayHistoryElements.get(0).getTimeStamp().getTime() >= displayHistoryElements.get(1).getTimeStamp().getTime());
        Assert.assertEquals("Your base has been destroyed by Base 2", displayHistoryElements.get(0).getMessage());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testSurrender() throws Exception {
        configureMinimalGame();

        System.out.println("**** testSurrender ****");
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("Actor", "test", "test", "test");
        userService.login("Actor", "test");
        movableService.sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "xx", "xx", 0, 0);
        SimpleBase targetBase = ((RealityInfo) movableService.getGameInfo()).getBase();
        movableService.surrenderBase();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        List<DisplayHistoryElement> displayHistoryElements = historyService.getNewestHistoryElements(userService.getUser("Actor"), 1000);
        System.out.println("----- Actor Target-----");
        for (DisplayHistoryElement displayHistoryElement : displayHistoryElements) {
            System.out.println(displayHistoryElement);
        }
        System.out.println("----- Actor End -----");
        Assert.assertEquals(4, displayHistoryElements.size());
        Assert.assertTrue(displayHistoryElements.get(0).getTimeStamp().getTime() >= displayHistoryElements.get(1).getTimeStamp().getTime());
        Assert.assertEquals("Base surrendered", displayHistoryElements.get(0).getMessage());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }
}
