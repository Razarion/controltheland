package com.btxtech.game.services.history;

import com.btxtech.game.jsre.client.MovableService;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.common.info.RealityInfo;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.tutorial.TutorialConfig;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.collision.CollisionService;
import com.btxtech.game.services.history.impl.HistoryServiceImpl;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.user.UserService;
import org.easymock.EasyMock;
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
public class TestHistoryService extends AbstractServiceTest {
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
    @Autowired
    private CollisionService collisionService;

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

        Assert.assertTrue(displayHistoryElements.get(0).getTimeStamp() >= displayHistoryElements.get(1).getTimeStamp());
        Assert.assertEquals("Base created: U1", displayHistoryElements.get(1).getMessage());

        Assert.assertTrue(displayHistoryElements.get(1).getTimeStamp() >= displayHistoryElements.get(2).getTimeStamp());
        Assert.assertEquals("Level reached: " + TEST_LEVEL_2_REAL, displayHistoryElements.get(2).getMessage());

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("U1", "test");
        Assert.assertEquals(3, historyService.getNewestHistoryElements().readDbChildren().size());
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
        Index buildPos = collisionService.getFreeRandomPosition(itemService.getItemType(TEST_FACTORY_ITEM_ID), new Rectangle(0, 0, 100000, 100000), 400, true);
        sendBuildCommand(movableService.getAllSyncInfo().iterator().next().getId(), buildPos, TEST_FACTORY_ITEM_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        waitForActionServiceDone();

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
        Assert.assertTrue(displayHistoryElements.get(0).getTimeStamp() >= displayHistoryElements.get(1).getTimeStamp());
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
        Index buildPos = collisionService.getFreeRandomPosition(itemService.getItemType(TEST_FACTORY_ITEM_ID), new Rectangle(0, 0, 100000, 100000), 400, true);
        sendBuildCommand(getFirstSynItemId(actorBase, TEST_START_BUILDER_ITEM_ID), buildPos, TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        sendFactoryCommand(getFirstSynItemId(actorBase, TEST_FACTORY_ITEM_ID), TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone();
        sendAttackCommand(getFirstSynItemId(actorBase, TEST_ATTACK_ITEM_ID), getFirstSynItemId(targetBase, TEST_START_BUILDER_ITEM_ID));
        waitForActionServiceDone();
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
        Assert.assertTrue(displayHistoryElements.get(1).getTimeStamp() >= displayHistoryElements.get(2).getTimeStamp());
        Assert.assertEquals("Destroyed a " + TEST_START_BUILDER_ITEM + " from Target", displayHistoryElements.get(1).getMessage());
        Assert.assertTrue(displayHistoryElements.get(0).getTimeStamp() >= displayHistoryElements.get(1).getTimeStamp());
        Assert.assertEquals("Base destroyed: Target", displayHistoryElements.get(0).getMessage());


        displayHistoryElements = historyService.getNewestHistoryElements(userService.getUser("Target"), 1000);
        System.out.println("----- History Target-----");
        for (DisplayHistoryElement displayHistoryElement : displayHistoryElements) {
            System.out.println(displayHistoryElement);
        }
        System.out.println("----- History End -----");
        Assert.assertEquals(5, displayHistoryElements.size());
        Assert.assertTrue(displayHistoryElements.get(1).getTimeStamp() >= displayHistoryElements.get(2).getTimeStamp());
        Assert.assertEquals("Actor destroyed your " + TEST_START_BUILDER_ITEM, displayHistoryElements.get(1).getMessage());
        Assert.assertTrue(displayHistoryElements.get(0).getTimeStamp() >= displayHistoryElements.get(1).getTimeStamp());
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
        Index buildPos = collisionService.getFreeRandomPosition(itemService.getItemType(TEST_FACTORY_ITEM_ID), new Rectangle(0, 0, 100000, 100000), 400, true);
        sendBuildCommand(getFirstSynItemId(actorBase, TEST_START_BUILDER_ITEM_ID), buildPos, TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        sendFactoryCommand(getFirstSynItemId(actorBase, TEST_FACTORY_ITEM_ID), TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone();
        sendAttackCommand(getFirstSynItemId(actorBase, TEST_ATTACK_ITEM_ID), getFirstSynItemId(targetBase, TEST_START_BUILDER_ITEM_ID));
        waitForActionServiceDone();
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
        Assert.assertTrue(displayHistoryElements.get(1).getTimeStamp() >= displayHistoryElements.get(2).getTimeStamp());
        Assert.assertEquals("Destroyed a " + TEST_START_BUILDER_ITEM + " from Base 1", displayHistoryElements.get(1).getMessage());
        Assert.assertTrue(displayHistoryElements.get(0).getTimeStamp() >= displayHistoryElements.get(1).getTimeStamp());
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
        Index buildPos = collisionService.getFreeRandomPosition(itemService.getItemType(TEST_FACTORY_ITEM_ID), new Rectangle(0, 0, 100000, 100000), 400, true);
        sendBuildCommand(getFirstSynItemId(actorBase, TEST_START_BUILDER_ITEM_ID), buildPos, TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        sendFactoryCommand(getFirstSynItemId(actorBase, TEST_FACTORY_ITEM_ID), TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone();
        sendAttackCommand(getFirstSynItemId(actorBase, TEST_ATTACK_ITEM_ID), getFirstSynItemId(targetBase, TEST_START_BUILDER_ITEM_ID));
        waitForActionServiceDone();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("Target", "test");
        getMyBase(); // Connection -> resurrection
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
        Assert.assertEquals(7, displayHistoryElements.size());


        Assert.assertTrue(displayHistoryElements.get(3).getTimeStamp() >= displayHistoryElements.get(4).getTimeStamp());
        Assert.assertEquals("Base 2 destroyed your " + TEST_START_BUILDER_ITEM, displayHistoryElements.get(3).getMessage());
        Assert.assertTrue(displayHistoryElements.get(2).getTimeStamp() >= displayHistoryElements.get(3).getTimeStamp());
        Assert.assertEquals("Your base has been destroyed by Base 2", displayHistoryElements.get(2).getMessage());
        Assert.assertTrue(displayHistoryElements.get(1).getTimeStamp() >= displayHistoryElements.get(2).getTimeStamp());
        Assert.assertEquals("Base created: Target", displayHistoryElements.get(1).getMessage());
        Assert.assertTrue(displayHistoryElements.get(0).getTimeStamp() >= displayHistoryElements.get(1).getTimeStamp());
        Assert.assertEquals("Item created: " + TEST_START_BUILDER_ITEM, displayHistoryElements.get(0).getMessage());


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
        ((RealityInfo) movableService.getGameInfo()).getBase();
        movableService.surrenderBase();

        getMyBase(); // Connection -> resurrection

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
        Assert.assertEquals(6, displayHistoryElements.size());
        Assert.assertTrue(displayHistoryElements.get(2).getTimeStamp() >= displayHistoryElements.get(3).getTimeStamp());
        Assert.assertEquals("Base surrendered", displayHistoryElements.get(2).getMessage());
        Assert.assertTrue(displayHistoryElements.get(1).getTimeStamp() >= displayHistoryElements.get(2).getTimeStamp());
        // Time different to short to assure the correct order of the two out-commented entries below
        //Assert.assertEquals("Base created: Actor", displayHistoryElements.get(1).getMessage());
        //Assert.assertTrue(displayHistoryElements.get(0).getTimeStamp().getTime() >= displayHistoryElements.get(1).getTimeStamp().getTime());
        //Assert.assertEquals("Item created: " + TEST_START_BUILDER_ITEM, displayHistoryElements.get(0).getMessage());


        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testSellItem() throws Exception {
        configureMinimalGame();

        System.out.println("**** testSellItem ****");
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("Actor", "test", "test", "test");
        userService.login("Actor", "test");
        movableService.sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "xx", "xx", 0, 0);
        SimpleBase simpleBase = getMyBase();
        movableService.sellItem(getFirstSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID));
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
        Assert.assertTrue(displayHistoryElements.get(0).getTimeStamp() >= displayHistoryElements.get(1).getTimeStamp());
        Assert.assertEquals(TEST_START_BUILDER_ITEM + " has been sold", displayHistoryElements.get(0).getMessage());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void dbHistoryElementBaseSurrenderedHuman() throws Exception {
        configureMinimalGame();

        SimpleBase humanBase1 = new SimpleBase(1);
        SimpleBase humanBase2 = new SimpleBase(2);
        SimpleBase botBase1 = new SimpleBase(3);
        SimpleBase botBase2 = new SimpleBase(4);
        SyncBaseItem humanBaseItem = createSyncBaseItem(TEST_SIMPLE_BUILDING_ID, new Index(500, 500), new Id(1, 1, 1), humanBase1);
        SyncBaseItem botBaseItem = createSyncBaseItem(TEST_SIMPLE_BUILDING_ID, new Index(500, 500), new Id(2, 1, 1), botBase1);

        BaseService baseService = EasyMock.createNiceMock(BaseService.class);
        EasyMock.expect(baseService.isBot(humanBase1)).andReturn(false).anyTimes();
        EasyMock.expect(baseService.isBot(botBase1)).andReturn(true).anyTimes();
        EasyMock.expect(baseService.isBot(humanBase2)).andReturn(false).anyTimes();
        EasyMock.expect(baseService.isBot(botBase2)).andReturn(true).anyTimes();
        EasyMock.replay(baseService);
        setPrivateField(HistoryServiceImpl.class, historyService, "baseService", baseService);

        historyService.addBaseSurrenderedEntry(humanBase1);
        checkSource(DbHistoryElement.Source.HUMAN);

        historyService.addBaseSurrenderedEntry(botBase1);
        checkSource(DbHistoryElement.Source.BOT);

        historyService.addBaseDefeatedEntry(humanBase1, humanBase2);
        checkSource(DbHistoryElement.Source.HUMAN);

        historyService.addBaseDefeatedEntry(botBase1, botBase2);
        checkSource(DbHistoryElement.Source.BOT);

        historyService.addBaseDefeatedEntry(humanBase1, botBase1);
        checkSource(DbHistoryElement.Source.HUMAN);

        historyService.addBaseDefeatedEntry(botBase1, humanBase1);
        checkSource(DbHistoryElement.Source.HUMAN);

        historyService.addBaseDefeatedEntry(null, humanBase1);
        checkSource(DbHistoryElement.Source.HUMAN);

        historyService.addBaseDefeatedEntry(null, botBase1);
        checkSource(DbHistoryElement.Source.BOT);

        historyService.addBaseStartEntry(humanBase1);
        checkSource(DbHistoryElement.Source.HUMAN);

        historyService.addBaseStartEntry(botBase1);
        checkSource(DbHistoryElement.Source.BOT);

        historyService.addItemCreatedEntry(humanBaseItem);
        checkSource(DbHistoryElement.Source.HUMAN);

        historyService.addItemCreatedEntry(botBaseItem);
        checkSource(DbHistoryElement.Source.BOT);

        historyService.addItemDestroyedEntry(humanBase2, humanBaseItem);
        checkSource(DbHistoryElement.Source.HUMAN);

        historyService.addItemDestroyedEntry(botBase1, humanBaseItem);
        checkSource(DbHistoryElement.Source.HUMAN);

        historyService.addItemDestroyedEntry(humanBase1, botBaseItem);
        checkSource(DbHistoryElement.Source.HUMAN);

        historyService.addItemDestroyedEntry(botBase2, botBaseItem);
        checkSource(DbHistoryElement.Source.BOT);

        historyService.addItemDestroyedEntry(null, humanBaseItem);
        checkSource(DbHistoryElement.Source.HUMAN);

        historyService.addItemDestroyedEntry(null, botBaseItem);
        checkSource(DbHistoryElement.Source.BOT);
    }

    private void checkSource(DbHistoryElement.Source source) {
        List<DbHistoryElement> dbHistoryElements;
        dbHistoryElements = getHibernateTemplate().loadAll(DbHistoryElement.class);
        Assert.assertEquals(1, dbHistoryElements.size());
        Assert.assertEquals(source, dbHistoryElements.get(0).getSource());
        getHibernateTemplate().deleteAll(dbHistoryElements);
    }
}