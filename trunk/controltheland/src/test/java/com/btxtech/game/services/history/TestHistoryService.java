package com.btxtech.game.services.history;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.dialogs.history.HistoryElementInfo;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBoxItem;
import com.btxtech.game.jsre.common.tutorial.TutorialConfig;
import com.btxtech.game.jsre.common.utg.config.ConditionTrigger;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.bot.DbBotEnragementStateConfig;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.finance.FinanceService;
import com.btxtech.game.services.inventory.DbInventoryArtifact;
import com.btxtech.game.services.inventory.DbInventoryArtifactCount;
import com.btxtech.game.services.inventory.DbInventoryItem;
import com.btxtech.game.services.inventory.GlobalInventoryService;
import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.item.itemType.DbBoxItemType;
import com.btxtech.game.services.planet.BaseService;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.planet.db.DbPlanet;
import com.btxtech.game.services.planet.impl.ServerPlanetServicesImpl;
import com.btxtech.game.services.unlock.ServerUnlockService;
import com.btxtech.game.services.user.GuildService;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.utg.DbLevel;
import com.btxtech.game.services.utg.DbLevelTask;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.condition.DbConditionConfig;
import com.btxtech.game.services.utg.condition.DbCountComparisonConfig;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

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
    private UserService userService;
    @Autowired
    private ServerItemTypeService serverItemTypeService;
    @Autowired
    private PlatformTransactionManager platformTransactionManager;
    @Autowired
    private UserGuidanceService userGuidanceService;
    @Autowired
    private GuildService guildService;
    @Autowired
    private GlobalInventoryService globalInventoryService;
    @Autowired
    private PlanetSystemService planetSystemService;
    @Autowired
    private FinanceService financeService;
    @Autowired
    private ServerUnlockService unlockService;
    @Autowired
    private ServerItemTypeService itemTypeService;

    @Test
    @DirtiesContext
    public void testCreateBaseLevel() throws Exception {
        configureMultiplePlanetsAndLevels();

        System.out.println("**** testHistoryService ****");
        beginHttpSession();
        // Create account
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        endHttpRequestAndOpenSessionInViewFilter();
        // Finish tutorial
        beginHttpRequestAndOpenSessionInViewFilter();
        int levelTaskId = userGuidanceService.getDefaultLevelTaskId();
        getMovableService().sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "", levelTaskId, "xx", 0, 0);
        getOrCreateBase();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DisplayHistoryElement> displayHistoryElements = historyService.getNewestHistoryElements(userService.getUser("U1"), 0, 1000);

        System.out.println("----- History -----");
        for (DisplayHistoryElement displayHistoryElement : displayHistoryElements) {
            System.out.println(displayHistoryElement);
        }
        System.out.println("----- History End -----");

        Assert.assertEquals(5, displayHistoryElements.size());

        Assert.assertEquals("Item created: " + TEST_START_BUILDER_ITEM, displayHistoryElements.get(0).getMessage());

        Assert.assertTrue(displayHistoryElements.get(0).getTimeStamp() >= displayHistoryElements.get(1).getTimeStamp());
        Assert.assertEquals("Base created: U1", displayHistoryElements.get(1).getMessage());

        Assert.assertTrue(displayHistoryElements.get(1).getTimeStamp() >= displayHistoryElements.get(2).getTimeStamp());
        Assert.assertEquals("Level Task activated: " + TEST_LEVEL_TASK_1_2_REAL_NAME, displayHistoryElements.get(2).getMessage());

        Assert.assertTrue(displayHistoryElements.get(2).getTimeStamp() >= displayHistoryElements.get(3).getTimeStamp());
        Assert.assertEquals("Level reached: " + TEST_LEVEL_2_REAL, displayHistoryElements.get(3).getMessage());

        Assert.assertTrue(displayHistoryElements.get(3).getTimeStamp() >= displayHistoryElements.get(4).getTimeStamp());
        Assert.assertEquals("Level Task competed: " + TEST_LEVEL_TASK_1_1_SIMULATED_NAME, displayHistoryElements.get(4).getMessage());

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1", "test");
        Assert.assertEquals(5, historyService.getNewestHistoryElements().readDbChildren().size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testCreateItem() throws Exception {
        configureSimplePlanetNoResources();

        System.out.println("**** testCreateItem ****");
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        // Establish Connection
        getMovableService().getRealGameInfo(START_UID_1);

        // Build Factory
        System.out.println("---- build unit ---");
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(200, 200), TEST_FACTORY_ITEM_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        waitForActionServiceDone(TEST_PLANET_1_ID);

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DisplayHistoryElement> displayHistoryElements = historyService.getNewestHistoryElements(userService.getUser("U1"), 0, 1000);

        System.out.println("----- History -----");
        for (DisplayHistoryElement displayHistoryElement : displayHistoryElements) {
            System.out.println(displayHistoryElement);
        }
        System.out.println("----- History End -----");

        Assert.assertEquals(3, displayHistoryElements.size());
        Assert.assertTrue(displayHistoryElements.get(0).getTimeStamp() >= displayHistoryElements.get(1).getTimeStamp());
        Assert.assertEquals("Item created: " + TEST_FACTORY_ITEM, displayHistoryElements.get(0).getMessage());


        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testKillItem() throws Exception {
        configureSimplePlanetNoResources();

        System.out.println("**** testKillItem ****");
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("Target");
        SimpleBase targetBase = getOrCreateBase();
        waitForActionServiceDone();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("Actor");
        SimpleBase actorBase = createBase(new Index(2000, 2000));
        sendBuildCommand(getFirstSynItemId(actorBase, TEST_START_BUILDER_ITEM_ID), new Index(2200, 2200), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        sendFactoryCommand(getFirstSynItemId(actorBase, TEST_FACTORY_ITEM_ID), TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone();
        // TODO failed on 19.01.2013
        sendAttackCommand(getFirstSynItemId(actorBase, TEST_ATTACK_ITEM_ID), getFirstSynItemId(targetBase, TEST_START_BUILDER_ITEM_ID));
        waitForActionServiceDone();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        List<DisplayHistoryElement> displayHistoryElements = historyService.getNewestHistoryElements(userService.getUser("Actor"), 0, 1000);
        System.out.println("----- History Actor-----");
        for (DisplayHistoryElement displayHistoryElement : displayHistoryElements) {
            System.out.println(displayHistoryElement);
        }
        System.out.println("----- History End -----");
        Assert.assertEquals(6, displayHistoryElements.size());
        Assert.assertTrue(displayHistoryElements.get(1).getTimeStamp() >= displayHistoryElements.get(2).getTimeStamp());
        Assert.assertEquals("Destroyed a " + TEST_START_BUILDER_ITEM + " from Target", displayHistoryElements.get(1).getMessage());
        Assert.assertTrue(displayHistoryElements.get(0).getTimeStamp() >= displayHistoryElements.get(1).getTimeStamp());
        Assert.assertEquals("Base destroyed: Target", displayHistoryElements.get(0).getMessage());


        displayHistoryElements = historyService.getNewestHistoryElements(userService.getUser("Target"), 0, 1000);
        System.out.println("----- History Target-----");
        for (DisplayHistoryElement displayHistoryElement : displayHistoryElements) {
            System.out.println(displayHistoryElement);
        }
        System.out.println("----- History End -----");
        Assert.assertEquals(4, displayHistoryElements.size());
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
        configureSimplePlanetNoResources();

        System.out.println("**** testKillAnonymousItem ****");
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        SimpleBase targetBase = getOrCreateBase();
        sendMoveCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(1000, 1000));
        waitForActionServiceDone();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("Actor");
        SimpleBase actorBase = createBase(new Index(2000, 2000));
        sendBuildCommand(getFirstSynItemId(actorBase, TEST_START_BUILDER_ITEM_ID), new Index(200, 200), TEST_FACTORY_ITEM_ID);
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

        List<DisplayHistoryElement> displayHistoryElements = historyService.getNewestHistoryElements(userService.getUser("Actor"), 0, 1000);
        System.out.println("----- History Actor-----");
        for (DisplayHistoryElement displayHistoryElement : displayHistoryElements) {
            System.out.println(displayHistoryElement);
        }
        System.out.println("----- History End -----");
        Assert.assertEquals(6, displayHistoryElements.size());
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
        configureSimplePlanetNoResources();

        System.out.println("**** testKillByAnonymous ****");
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("Target");
        SimpleBase targetBase = getOrCreateBase();
        sendMoveCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(1000, 1000));
        waitForActionServiceDone();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        SimpleBase actorBase = createBase(new Index(2000, 2000));
        sendBuildCommand(getFirstSynItemId(actorBase, TEST_START_BUILDER_ITEM_ID), new Index(200, 200), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        // TODO failed on 03.12.2012, 03.12.2012
        sendFactoryCommand(getFirstSynItemId(actorBase, TEST_FACTORY_ITEM_ID), TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone();
        sendAttackCommand(getFirstSynItemId(actorBase, TEST_ATTACK_ITEM_ID), getFirstSynItemId(targetBase, TEST_START_BUILDER_ITEM_ID));
        waitForActionServiceDone();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("Target", "test");
        createBase(new Index(3000, 3000)); // Connection -> resurrection
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();


        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        List<DisplayHistoryElement> displayHistoryElements = historyService.getNewestHistoryElements(userService.getUser("Target"), 0, 1000);
        System.out.println("----- Target Target-----");
        for (DisplayHistoryElement displayHistoryElement : displayHistoryElements) {
            System.out.println(displayHistoryElement);
        }
        System.out.println("----- Target End -----");
        Assert.assertEquals(6, displayHistoryElements.size());


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
    public void testSellItem() throws Exception {
        configureSimplePlanetNoResources();

        System.out.println("**** testSellItem ****");
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("Actor");
        SimpleBase simpleBase = getOrCreateBase();
        getMovableService().sellItem(getFirstSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        List<DisplayHistoryElement> displayHistoryElements = historyService.getNewestHistoryElements(userService.getUser("Actor"), 0, 1000);
        System.out.println("----- Actor Target-----");
        for (DisplayHistoryElement displayHistoryElement : displayHistoryElements) {
            System.out.println(displayHistoryElement);
        }
        System.out.println("----- Actor End -----");
        Assert.assertEquals(4, displayHistoryElements.size());
        Assert.assertTrue(displayHistoryElements.get(0).getTimeStamp() >= displayHistoryElements.get(1).getTimeStamp());
        Assert.assertEquals("Your base has been destroyed", displayHistoryElements.get(0).getMessage());
        Assert.assertTrue(displayHistoryElements.get(1).getTimeStamp() >= displayHistoryElements.get(2).getTimeStamp());
        Assert.assertEquals(TEST_START_BUILDER_ITEM + " has been sold", displayHistoryElements.get(1).getMessage());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void dbHistoryElementBaseSurrenderedHuman() throws Exception {
        configureSimplePlanetNoResources();

        SimpleBase humanBase1 = new SimpleBase(1, 1);
        SimpleBase humanBase2 = new SimpleBase(2, 1);
        SimpleBase botBase1 = new SimpleBase(3, 1);
        SimpleBase botBase2 = new SimpleBase(4, 1);
        SyncBaseItem humanBaseItem = createSyncBaseItem(TEST_SIMPLE_BUILDING_ID, new Index(500, 500), new Id(1, 1), humanBase1);
        SyncBaseItem botBaseItem = createSyncBaseItem(TEST_SIMPLE_BUILDING_ID, new Index(500, 500), new Id(2, 1), botBase1);

        BaseService baseService = EasyMock.createNiceMock(BaseService.class);
        EasyMock.expect(baseService.isBot(humanBase1)).andReturn(false).anyTimes();
        EasyMock.expect(baseService.isBot(botBase1)).andReturn(true).anyTimes();
        EasyMock.expect(baseService.isBot(humanBase2)).andReturn(false).anyTimes();
        EasyMock.expect(baseService.isBot(botBase2)).andReturn(true).anyTimes();
        EasyMock.replay(baseService);
        ServerPlanetServicesImpl serverPlanetServices = (ServerPlanetServicesImpl) planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID);
        serverPlanetServices.setBaseService(baseService);

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
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        final List<DbHistoryElement> dbHistoryElements = HibernateUtil.loadAll(getSessionFactory(), DbHistoryElement.class);
        Assert.assertEquals(1, dbHistoryElements.size());
        Assert.assertEquals(source, dbHistoryElements.get(0).getSource());
        new TransactionTemplate(platformTransactionManager).execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                HibernateUtil.deleteAll(getSessionFactory(), dbHistoryElements);
            }
        });

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void alliances() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        SimpleBase simpleBase1 = getOrCreateBase();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U2");
        SimpleBase simpleBase2 = createBase(new Index(2000, 2000));
        guildService.proposeAlliance(simpleBase1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1", "test");
        guildService.acceptAllianceOffer("U2");
        guildService.breakAlliance("U2");
        guildService.proposeAlliance(simpleBase2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U2", "test");
        guildService.rejectAllianceOffer("U1");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // verify u1
        List<DisplayHistoryElement> displayHistoryElements = historyService.getNewestHistoryElements(userService.getUser("U1"), 0, 1000);
        System.out.println("----- u1 Target-----");
        for (DisplayHistoryElement displayHistoryElement : displayHistoryElements) {
            System.out.println(displayHistoryElement);
        }
        Assert.assertEquals(7, displayHistoryElements.size());
        Assert.assertTrue(displayHistoryElements.get(0).getTimeStamp() >= displayHistoryElements.get(1).getTimeStamp());
        Assert.assertEquals("Your alliance offer has been rejected by U2", displayHistoryElements.get(0).getMessage());
        Assert.assertTrue(displayHistoryElements.get(1).getTimeStamp() >= displayHistoryElements.get(2).getTimeStamp());
        Assert.assertEquals("You offered U2 an alliance", displayHistoryElements.get(1).getMessage());
        Assert.assertTrue(displayHistoryElements.get(2).getTimeStamp() >= displayHistoryElements.get(3).getTimeStamp());
        Assert.assertEquals("You broke the alliance with U2", displayHistoryElements.get(2).getMessage());
        Assert.assertTrue(displayHistoryElements.get(3).getTimeStamp() >= displayHistoryElements.get(4).getTimeStamp());
        Assert.assertEquals("You accepted an alliance with U2", displayHistoryElements.get(3).getMessage());
        Assert.assertTrue(displayHistoryElements.get(4).getTimeStamp() >= displayHistoryElements.get(5).getTimeStamp());
        Assert.assertEquals("U2 offered you an alliance", displayHistoryElements.get(4).getMessage());
        // verify u2
        displayHistoryElements = historyService.getNewestHistoryElements(userService.getUser("U2"), 0, 1000);
        System.out.println("----- u2 Target-----");
        for (DisplayHistoryElement displayHistoryElement : displayHistoryElements) {
            System.out.println(displayHistoryElement);
        }
        Assert.assertEquals(7, displayHistoryElements.size());
        Assert.assertTrue(displayHistoryElements.get(0).getTimeStamp() >= displayHistoryElements.get(1).getTimeStamp());
        Assert.assertEquals("You rejected an alliance with U1", displayHistoryElements.get(0).getMessage());
        Assert.assertTrue(displayHistoryElements.get(1).getTimeStamp() >= displayHistoryElements.get(2).getTimeStamp());
        Assert.assertEquals("U1 offered you an alliance", displayHistoryElements.get(1).getMessage());
        Assert.assertTrue(displayHistoryElements.get(2).getTimeStamp() >= displayHistoryElements.get(3).getTimeStamp());
        Assert.assertEquals("Your alliance has been broken by U1", displayHistoryElements.get(2).getMessage());
        Assert.assertTrue(displayHistoryElements.get(3).getTimeStamp() >= displayHistoryElements.get(4).getTimeStamp());
        Assert.assertEquals("Your alliance offer has been accepted by U1", displayHistoryElements.get(3).getMessage());
        Assert.assertTrue(displayHistoryElements.get(4).getTimeStamp() >= displayHistoryElements.get(5).getTimeStamp());
        Assert.assertEquals("You offered U1 an alliance", displayHistoryElements.get(4).getMessage());

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void inventory() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbInventoryArtifact dbInventoryArtifact = globalInventoryService.getArtifactCrud().createDbChild();
        dbInventoryArtifact.setName("dbInventoryArtifact");
        globalInventoryService.getArtifactCrud().updateDbChild(dbInventoryArtifact);
        DbInventoryItem dbInventoryItem = globalInventoryService.getItemCrud().createDbChild();
        dbInventoryItem.setName("dbInventoryItem");
        DbInventoryArtifactCount dbInventoryArtifactCount = dbInventoryItem.getArtifactCountCrud().createDbChild();
        dbInventoryArtifactCount.setCount(1);
        dbInventoryArtifactCount.setDbInventoryArtifact(dbInventoryArtifact);
        globalInventoryService.getItemCrud().updateDbChild(dbInventoryItem);

        DbBoxItemType dbBoxItemType = (DbBoxItemType) serverItemTypeService.getDbItemTypeCrud().createDbChild(DbBoxItemType.class);
        setupImages(dbBoxItemType, 1);
        dbBoxItemType.setName("Box Item");
        dbBoxItemType.setTerrainType(TerrainType.LAND);
        dbBoxItemType.setBounding(new BoundingBox(80, ANGELS_1));
        dbBoxItemType.setTtl(5000);
        serverItemTypeService.saveDbItemType(dbBoxItemType);
        serverItemTypeService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();


        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        SimpleBase simpleBase = getOrCreateBase();
        SyncBoxItem syncBoxItem = createSyncBoxItem(dbBoxItemType.getId(), new Index(1000, 1000), new Id(1, 1));
        SyncBaseItem syncBaseItem = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(2000, 2000), new Id(2, 2), simpleBase);

        // System entry historyService.addBoxDropped(syncBoxItem, new Index(1000, 1000), null);
        // System entry historyService.addBoxDropped(syncBoxItem, new Index(1000, 1000), syncBaseItem);
        // System entry historyService.addBoxExpired(SyncBoxItem boxItem);
        historyService.addInventoryItemBought(userService.getUserState(), "inventoryItemName", 12);
        historyService.addInventoryArtifactBought(userService.getUserState(), "inventoryArtifactName", 33);
        historyService.addBoxPicked(syncBoxItem, syncBaseItem);
        historyService.addRazarionFromBox(userService.getUserState(), 100);
        historyService.addInventoryItemFromBox(userService.getUserState(), "inventoryItemName");
        historyService.addInventoryArtifactFromBox(userService.getUserState(), "inventoryArtifactName");
        historyService.addInventoryItemUsed(userService.getUserState(), "inventoryItemName");

        List<DisplayHistoryElement> displayHistoryElements = historyService.getNewestHistoryElements(userService.getUser("U1"), 0, 1000);
        System.out.println("----- u1 Target-----");
        for (DisplayHistoryElement displayHistoryElement : displayHistoryElements) {
            System.out.println(displayHistoryElement);
        }
        Assert.assertEquals(9, displayHistoryElements.size());
        Assert.assertTrue(displayHistoryElements.get(0).getTimeStamp() >= displayHistoryElements.get(1).getTimeStamp());
        Assert.assertEquals("Inventory used inventoryItemName", displayHistoryElements.get(0).getMessage());
        Assert.assertTrue(displayHistoryElements.get(1).getTimeStamp() >= displayHistoryElements.get(2).getTimeStamp());
        Assert.assertEquals("Found inventory artifact inventoryArtifactName", displayHistoryElements.get(1).getMessage());
        Assert.assertTrue(displayHistoryElements.get(2).getTimeStamp() >= displayHistoryElements.get(3).getTimeStamp());
        Assert.assertEquals("Found inventory item inventoryItemName", displayHistoryElements.get(2).getMessage());
        Assert.assertTrue(displayHistoryElements.get(3).getTimeStamp() >= displayHistoryElements.get(4).getTimeStamp());
        Assert.assertEquals("Found razarion 100", displayHistoryElements.get(3).getMessage());
        Assert.assertTrue(displayHistoryElements.get(4).getTimeStamp() >= displayHistoryElements.get(5).getTimeStamp());
        Assert.assertEquals("Box picked", displayHistoryElements.get(4).getMessage());
        Assert.assertTrue(displayHistoryElements.get(5).getTimeStamp() >= displayHistoryElements.get(6).getTimeStamp());
        Assert.assertEquals("Inventory artifact bought: inventoryArtifactName", displayHistoryElements.get(5).getMessage());
        Assert.assertTrue(displayHistoryElements.get(6).getTimeStamp() >= displayHistoryElements.get(7).getTimeStamp());
        Assert.assertEquals("Inventory item bought: inventoryItemName", displayHistoryElements.get(6).getMessage());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void botEnragement() throws Exception {
        configureSimplePlanetNoResources();

        DbBotEnragementStateConfig enragementStateConfig1 = new DbBotEnragementStateConfig();
        enragementStateConfig1.setName("Normal");
        DbBotEnragementStateConfig enragementStateConfig2 = new DbBotEnragementStateConfig();
        enragementStateConfig2.setName("Angry");

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        SimpleBase simpleBase = getOrCreateBase();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        historyService.addBotEnrageUp("Bot1", enragementStateConfig2.createBotEnragementStateConfigg(serverItemTypeService), simpleBase);
        historyService.addBotEnrageNormal("Bot1", enragementStateConfig1.createBotEnragementStateConfigg(serverItemTypeService));

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DisplayHistoryElement> displayHistoryElements = historyService.getNewestHistoryElements(userService.getUser("U1"), 0, 1000);
        System.out.println("----- u1 Target-----");
        for (DisplayHistoryElement displayHistoryElement : displayHistoryElements) {
            System.out.println(displayHistoryElement);
        }
        Assert.assertEquals(3, displayHistoryElements.size());
        Assert.assertTrue(displayHistoryElements.get(0).getTimeStamp() >= displayHistoryElements.get(1).getTimeStamp());
        Assert.assertEquals("You have angered Bot1: Angry", displayHistoryElements.get(0).getMessage());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testRazarionBought() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        int userId = getUserState().getUser();
        String userIdString = Integer.toString(userId);
        financeService.razarionBought(userIdString, "RAZ1000", "5", "USD", "1", "payer email", "finance@razarion.com", "Completed", "1");
        financeService.razarionBought(userIdString, "RAZ2200", "10", "USD", "2", "payer email", "finance@razarion.com", "Completed", "1");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DisplayHistoryElement> displayHistoryElements = historyService.getNewestHistoryElements(userService.getUser("U1"), 0, 1000);
        System.out.println("----- u1 Target-----");
        for (DisplayHistoryElement displayHistoryElement : displayHistoryElements) {
            System.out.println(displayHistoryElement);
        }
        Assert.assertEquals(2, displayHistoryElements.size());
        Assert.assertTrue(displayHistoryElements.get(0).getTimeStamp() >= displayHistoryElements.get(1).getTimeStamp());
        Assert.assertEquals("Bought Razarion 2200 via PayPal", displayHistoryElements.get(0).getMessage());
        Assert.assertEquals("Bought Razarion 1000 via PayPal", displayHistoryElements.get(1).getMessage());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testItemUnlocked() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBaseItemType attacker = itemTypeService.getDbBaseItemType(TEST_ATTACK_ITEM_ID);
        attacker.setUnlockRazarion(10);
        itemTypeService.saveDbItemType(attacker);
        DbBaseItemType factory = itemTypeService.getDbBaseItemType(TEST_FACTORY_ITEM_ID);
        factory.setUnlockRazarion(8);
        itemTypeService.saveDbItemType(factory);
        itemTypeService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        getUserState().setRazarion(100);
        getOrCreateBase(); // Create Base
        unlockService.unlockItemType(TEST_ATTACK_ITEM_ID);
        unlockService.unlockItemType(TEST_FACTORY_ITEM_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DisplayHistoryElement> displayHistoryElements = historyService.getNewestHistoryElements(userService.getUser("U1"), 0, 1000);
        System.out.println("----- u1 Target-----");
        for (DisplayHistoryElement displayHistoryElement : displayHistoryElements) {
            System.out.println(displayHistoryElement);
        }
        Assert.assertEquals(4, displayHistoryElements.size());
        Assert.assertTrue(displayHistoryElements.get(0).getTimeStamp() >= displayHistoryElements.get(1).getTimeStamp());
        Assert.assertEquals("Item unlocked TestFactoryItem", displayHistoryElements.get(0).getMessage());
        Assert.assertEquals("Item unlocked TestAttackItem", displayHistoryElements.get(1).getMessage());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    private void setupCondition(DbLevelTask dbLevelTask1) {
        DbConditionConfig dbConditionConfig = new DbConditionConfig();
        dbConditionConfig.setConditionTrigger(ConditionTrigger.MONEY_INCREASED);
        DbCountComparisonConfig dbCountComparisonConfig = new DbCountComparisonConfig();
        dbCountComparisonConfig.setCount(3);
        dbConditionConfig.setDbAbstractComparisonConfig(dbCountComparisonConfig);
        dbLevelTask1.setDbConditionConfig(dbConditionConfig);
    }

    @Test
    @DirtiesContext
    public void testQuestUnlocked() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbLevel dbLevel = userGuidanceService.getDbLevelCrud().readDbChild(TEST_LEVEL_2_REAL_ID);
        DbLevelTask dbLevelTask1 = dbLevel.getLevelTaskCrud().createDbChild();
        dbLevelTask1.setName("LT1");
        dbLevelTask1.setUnlockRazarion(10);
        setupCondition(dbLevelTask1);
        DbLevelTask dbLevelTask2 = dbLevel.getLevelTaskCrud().createDbChild();
        dbLevelTask2.setName("LT2");
        dbLevelTask2.setUnlockRazarion(20);
        setupCondition(dbLevelTask2);
        userGuidanceService.getDbLevelCrud().updateDbChild(dbLevel);
        userGuidanceService.activateLevels();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        getUserState().setRazarion(100);
        getOrCreateBase(); // Create Base
        unlockService.unlockQuest(dbLevelTask1.getId());
        unlockService.unlockQuest(dbLevelTask2.getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DisplayHistoryElement> displayHistoryElements = historyService.getNewestHistoryElements(userService.getUser("U1"), 0, 1000);
        System.out.println("----- u1 Target-----");
        for (DisplayHistoryElement displayHistoryElement : displayHistoryElements) {
            System.out.println(displayHistoryElement);
        }
        Assert.assertEquals(5, displayHistoryElements.size());
        Assert.assertTrue(displayHistoryElements.get(0).getTimeStamp() >= displayHistoryElements.get(1).getTimeStamp());
        Assert.assertTrue(displayHistoryElements.get(1).getTimeStamp() >= displayHistoryElements.get(2).getTimeStamp());
        Assert.assertEquals("Quest unlocked LT2", displayHistoryElements.get(0).getMessage());
        Assert.assertEquals("Level Task activated: LT1", displayHistoryElements.get(1).getMessage());
        Assert.assertEquals("Quest unlocked LT1", displayHistoryElements.get(2).getMessage());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testPlanetUnlocked() throws Exception {
        configureMultiplePlanetsAndLevels();
        // Prepare
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbPlanet dbPlanet2 = planetSystemService.getDbPlanetCrud().readDbChild(TEST_PLANET_2_ID);
        dbPlanet2.setUnlockRazarion(20);
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet2);
        planetSystemService.deactivatePlanet(TEST_PLANET_2_ID);
        planetSystemService.activatePlanet(TEST_PLANET_2_ID);
        DbPlanet dbPlanet3 = planetSystemService.getDbPlanetCrud().readDbChild(TEST_PLANET_3_ID);
        dbPlanet3.setUnlockRazarion(15);
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet3);
        planetSystemService.deactivatePlanet(TEST_PLANET_3_ID);
        planetSystemService.activatePlanet(TEST_PLANET_3_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        userGuidanceService.promote(getUserState(), TEST_LEVEL_4_REAL);
        getUserState().setRazarion(100);
        getOrCreateBase(); // Create Base
        unlockService.unlockPlanet(TEST_PLANET_2_ID);
        unlockService.unlockPlanet(TEST_PLANET_3_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DisplayHistoryElement> displayHistoryElements = historyService.getNewestHistoryElements(userService.getUser("U1"), 0, 1000);
        System.out.println("----- u1 Target-----");
        for (DisplayHistoryElement displayHistoryElement : displayHistoryElements) {
            System.out.println(displayHistoryElement);
        }
        Assert.assertEquals(6, displayHistoryElements.size());
        Assert.assertTrue(displayHistoryElements.get(0).getTimeStamp() >= displayHistoryElements.get(1).getTimeStamp());
        Assert.assertEquals("Planet unlocked TEST_PLANET_3", displayHistoryElements.get(0).getMessage());
        Assert.assertEquals("Planet unlocked TEST_PLANET_2", displayHistoryElements.get(1).getMessage());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void getHistoryElements() throws Exception {
        configureSimplePlanetNoResources();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U2");
        historyService.addRazarionBought(getUserState(), 1);
        historyService.addRazarionBought(getUserState(), 2);
        historyService.addRazarionBought(getUserState(), 3);
        historyService.addRazarionBought(getUserState(), 4);
        historyService.addRazarionBought(getUserState(), 5);
        historyService.addRazarionBought(getUserState(), 6);
        historyService.addRazarionBought(getUserState(), 7);
        historyService.addRazarionBought(getUserState(), 8);
        historyService.addRazarionBought(getUserState(), 9);
        historyService.addRazarionBought(getUserState(), 10);
        // Verify 1
        HistoryElementInfo historyElementInfo = historyService.getHistoryElements(0, 10);
        Assert.assertEquals(0, historyElementInfo.getStartRow());
        Assert.assertEquals(10, historyElementInfo.getTotalRowCount());
        Assert.assertEquals(10, historyElementInfo.getHistoryElements().size());
        Assert.assertEquals("Bought Razarion 10 via PayPal", historyElementInfo.getHistoryElements().get(0).getMessage());
        Assert.assertEquals("Bought Razarion 9 via PayPal", historyElementInfo.getHistoryElements().get(1).getMessage());
        Assert.assertEquals("Bought Razarion 8 via PayPal", historyElementInfo.getHistoryElements().get(2).getMessage());
        Assert.assertEquals("Bought Razarion 7 via PayPal", historyElementInfo.getHistoryElements().get(3).getMessage());
        Assert.assertEquals("Bought Razarion 6 via PayPal", historyElementInfo.getHistoryElements().get(4).getMessage());
        Assert.assertEquals("Bought Razarion 5 via PayPal", historyElementInfo.getHistoryElements().get(5).getMessage());
        Assert.assertEquals("Bought Razarion 4 via PayPal", historyElementInfo.getHistoryElements().get(6).getMessage());
        Assert.assertEquals("Bought Razarion 3 via PayPal", historyElementInfo.getHistoryElements().get(7).getMessage());
        Assert.assertEquals("Bought Razarion 2 via PayPal", historyElementInfo.getHistoryElements().get(8).getMessage());
        Assert.assertEquals("Bought Razarion 1 via PayPal", historyElementInfo.getHistoryElements().get(9).getMessage());
        // Verify 2
        historyElementInfo = historyService.getHistoryElements(2, 5);
        Assert.assertEquals(2, historyElementInfo.getStartRow());
        Assert.assertEquals(10, historyElementInfo.getTotalRowCount());
        Assert.assertEquals(5, historyElementInfo.getHistoryElements().size());
        Assert.assertEquals("Bought Razarion 8 via PayPal", historyElementInfo.getHistoryElements().get(0).getMessage());
        Assert.assertEquals("Bought Razarion 7 via PayPal", historyElementInfo.getHistoryElements().get(1).getMessage());
        Assert.assertEquals("Bought Razarion 6 via PayPal", historyElementInfo.getHistoryElements().get(2).getMessage());
        Assert.assertEquals("Bought Razarion 5 via PayPal", historyElementInfo.getHistoryElements().get(3).getMessage());
        Assert.assertEquals("Bought Razarion 4 via PayPal", historyElementInfo.getHistoryElements().get(4).getMessage());
        // Verify 3
        historyElementInfo = historyService.getHistoryElements(8, 2);
        Assert.assertEquals(8, historyElementInfo.getStartRow());
        Assert.assertEquals(10, historyElementInfo.getTotalRowCount());
        Assert.assertEquals(2, historyElementInfo.getHistoryElements().size());
        Assert.assertEquals("Bought Razarion 2 via PayPal", historyElementInfo.getHistoryElements().get(0).getMessage());
        Assert.assertEquals("Bought Razarion 1 via PayPal", historyElementInfo.getHistoryElements().get(1).getMessage());
        // Verify 4
        historyElementInfo = historyService.getHistoryElements(8, 4);
        Assert.assertEquals(8, historyElementInfo.getStartRow());
        Assert.assertEquals(10, historyElementInfo.getTotalRowCount());
        Assert.assertEquals(2, historyElementInfo.getHistoryElements().size());
        Assert.assertEquals("Bought Razarion 2 via PayPal", historyElementInfo.getHistoryElements().get(0).getMessage());
        Assert.assertEquals("Bought Razarion 1 via PayPal", historyElementInfo.getHistoryElements().get(1).getMessage());
        // Verify 5
        historyElementInfo = historyService.getHistoryElements(10, 6);
        Assert.assertEquals(10, historyElementInfo.getTotalRowCount());
        Assert.assertEquals(0, historyElementInfo.getHistoryElements().size());
        // Verify 6
        historyElementInfo = historyService.getHistoryElements(100, 6);
        Assert.assertEquals(10, historyElementInfo.getTotalRowCount());
        Assert.assertEquals(0, historyElementInfo.getHistoryElements().size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void getHistoryElementsNoEntries() throws Exception {
        configureSimplePlanetNoResources();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U2");
        // Verify 1
        HistoryElementInfo historyElementInfo = historyService.getHistoryElements(0, 10);
        Assert.assertEquals(0, historyElementInfo.getTotalRowCount());
        Assert.assertEquals(0, historyElementInfo.getHistoryElements().size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void getHistoryElementsUnregistered() throws Exception {
        configureSimplePlanetNoResources();
        // Prepare
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        try {
            historyService.getHistoryElements(0, 10);
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            Assert.assertEquals("User is not registered", e.getMessage());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

}
