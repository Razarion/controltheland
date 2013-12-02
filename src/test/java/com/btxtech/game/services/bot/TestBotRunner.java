package com.btxtech.game.services.bot;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotConfig;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotEnragementStateConfig;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotItemConfig;
import com.btxtech.game.jsre.common.gameengine.services.bot.impl.BotEnragementState;
import com.btxtech.game.jsre.common.gameengine.services.bot.impl.BotRunner;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.bot.impl.ServerBotRunner;
import com.btxtech.game.services.common.ServerPlanetServices;
import com.btxtech.game.services.history.DbHistoryElement;
import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.planet.db.DbPlanet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * User: beat
 * Date: 09.08.2011
 * Time: 00:51:52
 */
public class TestBotRunner extends AbstractServiceTest {
    @Autowired
    private ServerItemTypeService serverItemTypeService;
    @Autowired
    private PlanetSystemService planetSystemService;
    private Log log = LogFactory.getLog(TestBotRunner.class);

    @Test
    @DirtiesContext
    public void botRunnerBuildupSimple() throws Exception {
        configureSimplePlanetNoResources();
        ServerPlanetServices serverPlanetServices = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID);

        BotEnragementState.Listener mockListener = EasyMock.createStrictMock(BotEnragementState.Listener.class);

        Collection<BotItemConfig> botItems = new ArrayList<>();
        botItems.add(new BotItemConfig((BaseItemType) serverItemTypeService.getItemType(TEST_START_BUILDER_ITEM_ID), 1, true, createRegion(new Rectangle(2000, 2000, 1000, 1000), 1), false, null, false, null));
        List<BotEnragementStateConfig> botEnragementStateConfigs = new ArrayList<>();
        botEnragementStateConfigs.add(new BotEnragementStateConfig("NormalTest", botItems, null));
        BotConfig botConfig = new BotConfig(1, false, 10, botEnragementStateConfigs, null, "Bot", null, null, null, null);

        EasyMock.replay(mockListener);

        BotRunner botRunner = new ServerBotRunner(botConfig, serverPlanetServices, mockListener);
        botRunner.start();

        waitForBotRunner(botRunner);

        assertWholeItemCount(TEST_PLANET_1_ID, 1);
        Assert.assertEquals("Bot", serverPlanetServices.getBaseService().getBaseName(botRunner.getBase()));

        EasyMock.verify(mockListener);
    }

    @Test
    @DirtiesContext
    public void botRunnerBuildupComplex() throws Exception {
        configureSimplePlanetNoResources();
        ServerPlanetServices serverPlanetServices = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID);

        BotEnragementState.Listener mockListener = EasyMock.createStrictMock(BotEnragementState.Listener.class);

        Collection<BotItemConfig> botItems = new ArrayList<>();
        botItems.add(new BotItemConfig((BaseItemType) serverItemTypeService.getItemType(TEST_START_BUILDER_ITEM_ID), 1, true, createRegion(new Rectangle(2000, 2000, 1000, 1000), 1), false, null, false, null));
        botItems.add(new BotItemConfig((BaseItemType) serverItemTypeService.getItemType(TEST_FACTORY_ITEM_ID), 3, false, createRegion(new Rectangle(2000, 2000, 1000, 1000), 1), false, null, false, null));
        botItems.add(new BotItemConfig((BaseItemType) serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID), 3, false, null, false, null, false, null));
        List<BotEnragementStateConfig> botEnragementStateConfigs = new ArrayList<>();
        botEnragementStateConfigs.add(new BotEnragementStateConfig("NormalTest", botItems, null));
        BotConfig botConfig = new BotConfig(1, false, 10, botEnragementStateConfigs, null, "TestBot", null, null, null, null);

        EasyMock.replay(mockListener);

        BotRunner botRunner = new ServerBotRunner(botConfig, serverPlanetServices, mockListener);
        botRunner.start();

        waitForBotRunner(botRunner);
        SimpleBase simpleBase1 = botRunner.getBase();
        assertWholeItemCount(TEST_PLANET_1_ID, 7);
        Assert.assertEquals(1, getAllSynItemId(botRunner.getBase(), TEST_START_BUILDER_ITEM_ID, null, TEST_PLANET_1_ID).size());
        Assert.assertEquals(3, getAllSynItemId(botRunner.getBase(), TEST_FACTORY_ITEM_ID, null, TEST_PLANET_1_ID).size());
        Assert.assertEquals(3, getAllSynItemId(botRunner.getBase(), TEST_ATTACK_ITEM_ID, null, TEST_PLANET_1_ID).size());

        serverPlanetServices.getItemService().killSyncItemIds(getAllSynItemId(simpleBase1, TEST_FACTORY_ITEM_ID, null, TEST_PLANET_1_ID));
        serverPlanetServices.getItemService().killSyncItemIds(getAllSynItemId(simpleBase1, TEST_ATTACK_ITEM_ID, null, TEST_PLANET_1_ID));
        // TODO failed on 07.07.2012
        waitForBotRunner(botRunner);
        SimpleBase simpleBase2 = botRunner.getBase();
        assertWholeItemCount(TEST_PLANET_1_ID, 7);
        Assert.assertEquals(1, getAllSynItemId(botRunner.getBase(), TEST_START_BUILDER_ITEM_ID, null, TEST_PLANET_1_ID).size());
        Assert.assertEquals(3, getAllSynItemId(botRunner.getBase(), TEST_FACTORY_ITEM_ID, null, TEST_PLANET_1_ID).size());
        Assert.assertEquals(3, getAllSynItemId(botRunner.getBase(), TEST_ATTACK_ITEM_ID, null, TEST_PLANET_1_ID).size());
        Assert.assertSame(simpleBase1, simpleBase2);
        EasyMock.verify(mockListener);
    }

    @Test
    @DirtiesContext
    public void testRebuildBot1() throws Exception {
        configureSimplePlanetNoResources();
        ServerPlanetServices serverPlanetServices = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID);

        BotEnragementState.Listener mockListener = EasyMock.createStrictMock(BotEnragementState.Listener.class);

        Collection<BotItemConfig> botItems = new ArrayList<>();
        botItems.add(new BotItemConfig((BaseItemType) serverItemTypeService.getItemType(TEST_START_BUILDER_ITEM_ID), 1, true, createRegion(new Rectangle(2000, 2000, 1000, 1000), 1), false, null, false, null));
        List<BotEnragementStateConfig> botEnragementStateConfigs = new ArrayList<>();
        botEnragementStateConfigs.add(new BotEnragementStateConfig("NormalTest", botItems, null));
        BotConfig botConfig = new BotConfig(1, false, 10, botEnragementStateConfigs, null, "Bot2", null, null, null, null);

        EasyMock.replay(mockListener);

        BotRunner botRunner = new ServerBotRunner(botConfig, serverPlanetServices, mockListener);
        botRunner.start();

        waitForBotRunner(botRunner);
        assertWholeItemCount(TEST_PLANET_1_ID, 1);
        SimpleBase simpleBase1 = botRunner.getBase();
        Assert.assertEquals("Bot2", serverPlanetServices.getBaseService().getBaseName(simpleBase1));
        serverPlanetServices.getItemService().killSyncItemIds(getAllSynItemId(simpleBase1, TEST_START_BUILDER_ITEM_ID, null, TEST_PLANET_1_ID));

        waitForBotRunner(botRunner);
        assertWholeItemCount(TEST_PLANET_1_ID, 1);
        SimpleBase simpleBase2 = botRunner.getBase();
        Assert.assertEquals("Bot2", serverPlanetServices.getBaseService().getBaseName(simpleBase2));

        EasyMock.verify(mockListener);
    }

    @Test
    @DirtiesContext
    public void testRebuildBot2() throws Exception {
        configureSimplePlanetNoResources();
        ServerPlanetServices serverPlanetServices = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID);

        BotEnragementState.Listener mockListener = EasyMock.createStrictMock(BotEnragementState.Listener.class);

        Collection<BotItemConfig> botItems = new ArrayList<>();
        botItems.add(new BotItemConfig((BaseItemType) serverItemTypeService.getItemType(TEST_START_BUILDER_ITEM_ID), 1, true, createRegion(new Rectangle(2000, 2000, 1000, 1000), 1), false, null, false, null));
        List<BotEnragementStateConfig> botEnragementStateConfigs = new ArrayList<>();
        botEnragementStateConfigs.add(new BotEnragementStateConfig("NormalTest", botItems, null));
        BotConfig botConfig = new BotConfig(1, false, 10, botEnragementStateConfigs, null, "Bot2", null, null, null, null);

        EasyMock.replay(mockListener);

        BotRunner botRunner = new ServerBotRunner(botConfig, serverPlanetServices, mockListener);
        botRunner.start();

        waitForBotRunner(botRunner);
        assertWholeItemCount(TEST_PLANET_1_ID, 1);
        SimpleBase simpleBase1 = botRunner.getBase();
        Assert.assertEquals("Bot2", serverPlanetServices.getBaseService().getBaseName(simpleBase1));

        while (serverPlanetServices.getBaseService().isAlive(simpleBase1)) {
            serverPlanetServices.getItemService().killSyncItemIds(getAllSynItemId(simpleBase1, TEST_START_BUILDER_ITEM_ID, null, TEST_PLANET_1_ID));
        }

        waitForBotRunner(botRunner);
        assertWholeItemCount(TEST_PLANET_1_ID, 1);
        SimpleBase simpleBase2 = botRunner.getBase();
        Assert.assertNotSame(simpleBase1, simpleBase2);
        Assert.assertEquals("Bot2", serverPlanetServices.getBaseService().getBaseName(simpleBase2));

        EasyMock.verify(mockListener);
    }

    @Test
    @DirtiesContext
    public void testKillBot() throws Exception {
        configureSimplePlanetNoResources();
        ServerPlanetServices serverPlanetServices = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID);

        BotEnragementState.Listener mockListener = EasyMock.createStrictMock(BotEnragementState.Listener.class);

        Collection<BotItemConfig> botItems = new ArrayList<>();
        botItems.add(new BotItemConfig((BaseItemType) serverItemTypeService.getItemType(TEST_START_BUILDER_ITEM_ID), 1, true, createRegion(new Rectangle(2000, 2000, 1000, 1000), 1), false, null, false, null));
        botItems.add(new BotItemConfig((BaseItemType) serverItemTypeService.getItemType(TEST_FACTORY_ITEM_ID), 3, false, createRegion(new Rectangle(2000, 2000, 1000, 1000), 1), false, null, false, null));
        botItems.add(new BotItemConfig((BaseItemType) serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID), 4, false, null, false, null, false, null));
        List<BotEnragementStateConfig> botEnragementStateConfigs = new ArrayList<>();
        botEnragementStateConfigs.add(new BotEnragementStateConfig("NormalTest", botItems, null));
        BotConfig botConfig = new BotConfig(1, false, 10, botEnragementStateConfigs, null, "Bot2", null, null, null, null);

        EasyMock.replay(mockListener);

        BotRunner botRunner = new ServerBotRunner(botConfig, serverPlanetServices, mockListener);
        botRunner.start();

        waitForBotRunner(botRunner);
        assertWholeItemCount(TEST_PLANET_1_ID, 8);
        SimpleBase simpleBase1 = botRunner.getBase();

        botRunner.kill();

        Thread.sleep(1000);

        assertWholeItemCount(TEST_PLANET_1_ID, 0);
        SimpleBase simpleBase2 = botRunner.getBase();
        Assert.assertFalse(serverPlanetServices.getBaseService().isAlive(simpleBase1));
        Assert.assertFalse(serverPlanetServices.getBaseService().isAlive(simpleBase2));
        Assert.assertFalse(botRunner.isBuildupUseInTestOnly());

        EasyMock.verify(mockListener);
    }

    private void waitForBotRunner(BotRunner botRunner) throws InterruptedException, TimeoutException {
        long maxTime = System.currentTimeMillis() + 10000;
        while (!botRunner.isBuildupUseInTestOnly()) {
            if (System.currentTimeMillis() > maxTime) {
                throw new TimeoutException();
            }
            Thread.sleep(100);
        }
    }

    @Test
    @DirtiesContext
    public void attack() throws Exception {
        configureSimplePlanetNoResources();
        ServerPlanetServices serverPlanetServices = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        SimpleBase targetBase = getOrCreateBase();
        Id targetId = getFirstSynItemId(targetBase, TEST_START_BUILDER_ITEM_ID);
        sendMoveCommand(targetId, new Index(2000, 2000));
        waitForActionServiceDone();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        BotEnragementState.Listener mockListener = EasyMock.createStrictMock(BotEnragementState.Listener.class);

        Collection<BotItemConfig> botItems = new ArrayList<>();
        botItems.add(new BotItemConfig((BaseItemType) serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID), 1, true, createRegion(new Rectangle(0, 0, 1000, 1000), 1), false, null, false, null));
        List<BotEnragementStateConfig> botEnragementStateConfigs = new ArrayList<>();
        botEnragementStateConfigs.add(new BotEnragementStateConfig("NormalTest", botItems, null));
        BotConfig botConfig = new BotConfig(1, false, 10, botEnragementStateConfigs, createRegion(new Rectangle(0, 0, 4000, 4000), 1), "Bot2", null, null, null, null);

        EasyMock.replay(mockListener);

        BotRunner botRunner = new ServerBotRunner(botConfig, serverPlanetServices, mockListener);
        botRunner.start();

        waitForBotRunner(botRunner);
        assertWholeItemCount(TEST_PLANET_1_ID, 2);

        SyncItem target = serverPlanetServices.getItemService().getItem(targetId);
        Assert.assertTrue(target.isAlive());

        Thread.sleep(15);
        waitForActionServiceDone(TEST_PLANET_1_ID);

        Assert.assertFalse(target.isAlive());

        EasyMock.verify(mockListener);
    }

    @Test
    @DirtiesContext
    public void intervalBuildup() throws Exception {
        configureSimplePlanetNoResources();
        ServerPlanetServices serverPlanetServices = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID);

        BotEnragementState.Listener mockListener = EasyMock.createStrictMock(BotEnragementState.Listener.class);

        Collection<BotItemConfig> botItems = new ArrayList<>();
        botItems.add(new BotItemConfig((BaseItemType) serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID), 3, true, createRegion(new Rectangle(0, 0, 1000, 1000), 1), false, null, false, null));
        List<BotEnragementStateConfig> botEnragementStateConfigs = new ArrayList<>();
        botEnragementStateConfigs.add(new BotEnragementStateConfig("NormalTest", botItems, null));
        BotConfig botConfig = new BotConfig(1, false, 10, botEnragementStateConfigs, createRegion(new Rectangle(0, 0, 4000, 4000), 1), "Bot4", 100L, 200L, 200L, 300L);

        EasyMock.replay(mockListener);


        BotRunner botRunner = new ServerBotRunner(botConfig, serverPlanetServices, mockListener);
        botRunner.start();

        assertWholeItemCount(TEST_PLANET_1_ID, 0);
        Thread.sleep(250);
        // TODO failed on 12.12.2012
        assertWholeItemCount(TEST_PLANET_1_ID, 3);
        Assert.assertEquals("Bot4", serverPlanetServices.getBaseService().getBaseName(botRunner.getBase()));

        EasyMock.verify(mockListener);
        botRunner.kill(); //Avoid background timer & thread
    }

    @Test
    @DirtiesContext
    public void intervalPeriodicalBuildup() throws Exception {
        configureSimplePlanetNoResources();
        ServerPlanetServices serverPlanetServices = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID);

        BotEnragementState.Listener mockListener = EasyMock.createStrictMock(BotEnragementState.Listener.class);

        Collection<BotItemConfig> botItems = new ArrayList<>();
        botItems.add(new BotItemConfig((BaseItemType) serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID), 5, true, createRegion(new Rectangle(0, 0, 1000, 1000), 1), false, null, false, null));
        List<BotEnragementStateConfig> botEnragementStateConfigs = new ArrayList<>();
        botEnragementStateConfigs.add(new BotEnragementStateConfig("NormalTest", botItems, null));
        BotConfig botConfig = new BotConfig(1, false, 10, botEnragementStateConfigs, createRegion(new Rectangle(0, 0, 4000, 4000), 1), "Bot4", 500L, 500L, 500L, 500L);

        EasyMock.replay(mockListener);


        BotRunner botRunner = new ServerBotRunner(botConfig, serverPlanetServices, mockListener);
        botRunner.start();
        Thread.sleep(250);

        for (int i = 0; i < 10; i++) {
            assertWholeItemCount(TEST_PLANET_1_ID, 0);
            Thread.sleep(500);
            assertWholeItemCount(TEST_PLANET_1_ID, 5);
            Thread.sleep(500);
        }

        EasyMock.verify(mockListener);
        botRunner.kill(); //Avoid background timer & thread
    }

    @Test
    @DirtiesContext
    public void intervalKillActive() throws Exception {
        configureSimplePlanetNoResources();
        ServerPlanetServices serverPlanetServices = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID);

        BotEnragementState.Listener mockListener = EasyMock.createStrictMock(BotEnragementState.Listener.class);

        Collection<BotItemConfig> botItems = new ArrayList<>();
        botItems.add(new BotItemConfig((BaseItemType) serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID), 3, true, createRegion(new Rectangle(0, 0, 1000, 1000), 1), false, null, false, null));
        List<BotEnragementStateConfig> botEnragementStateConfigs = new ArrayList<>();
        botEnragementStateConfigs.add(new BotEnragementStateConfig("NormalTest", botItems, null));
        BotConfig botConfig = new BotConfig(1, false, 10, botEnragementStateConfigs, createRegion(new Rectangle(0, 0, 4000, 4000), 1), "Bot4", 30L, 50L, 100L, 120L);

        EasyMock.replay(mockListener);

        BotRunner botRunner = new ServerBotRunner(botConfig, serverPlanetServices, mockListener);
        log.error("Start");
        botRunner.start();
        log.error("assertWholeItemCount 1");
        assertWholeItemCount(TEST_PLANET_1_ID, 0);

        Thread.sleep(100);
        log.error("assertWholeItemCount 2");
        assertWholeItemCount(TEST_PLANET_1_ID, 3);

        botRunner.kill();
        log.error("assertWholeItemCount 3");
        assertWholeItemCount(TEST_PLANET_1_ID, 0);

        for (int i = 0; i < 200; i++) {
            Thread.sleep(20);
            assertWholeItemCount(TEST_PLANET_1_ID, 0);
        }
        EasyMock.verify(mockListener);
    }

    @Test
    @DirtiesContext
    public void intervalKillInactive() throws Exception {
        configureSimplePlanetNoResources();
        ServerPlanetServices serverPlanetServices = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID);

        BotEnragementState.Listener mockListener = EasyMock.createStrictMock(BotEnragementState.Listener.class);

        Collection<BotItemConfig> botItems = new ArrayList<>();
        botItems.add(new BotItemConfig((BaseItemType) serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID), 3, true, createRegion(new Rectangle(0, 0, 1000, 1000), 1), false, null, false, null));
        List<BotEnragementStateConfig> botEnragementStateConfigs = new ArrayList<>();
        botEnragementStateConfigs.add(new BotEnragementStateConfig("NormalTest", botItems, null));
        BotConfig botConfig = new BotConfig(1, false, 10, botEnragementStateConfigs, createRegion(new Rectangle(0, 0, 4000, 4000), 1), "Bot4", 80L, 100L, 50L, 60L);

        EasyMock.replay(mockListener);

        BotRunner botRunner = new ServerBotRunner(botConfig, serverPlanetServices, mockListener);
        botRunner.start();
        assertWholeItemCount(TEST_PLANET_1_ID, 0);
        Thread.sleep(40);

        botRunner.kill();

        for (int i = 0; i < 200; i++) {
            Thread.sleep(20);
            assertWholeItemCount(TEST_PLANET_1_ID, 0);
        }
        EasyMock.verify(mockListener);
    }

    @Test
    @DirtiesContext
    public void botBaseKilledAndEnrage() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbPlanet dbPlanet = planetSystemService.getDbPlanetCrud().readDbChild(TEST_PLANET_1_ID);
        DbBotConfig dbBotConfig = dbPlanet.getBotCrud().createDbChild();
        dbBotConfig.init(null);
        dbBotConfig.setActionDelay(10);
        dbBotConfig.setName("TestBot");
        dbBotConfig.setRealm(createDbRegion(new Rectangle(0, 0, 1000, 1000)));
        DbBotEnragementStateConfig dbBotEnragementStateConfig1 = dbBotConfig.getEnrageStateCrud().createDbChild();
        dbBotEnragementStateConfig1.setName("Normal");
        dbBotEnragementStateConfig1.setEnrageUpKills(2);
        DbBotItemConfig dbBotItemConfig = dbBotEnragementStateConfig1.getBotItemCrud().createDbChild();
        dbBotItemConfig.setBaseItemType(serverItemTypeService.getDbBaseItemType(TEST_START_BUILDER_ITEM_ID));
        dbBotItemConfig.setCount(1);
        dbBotItemConfig.setCreateDirectly(true);
        dbBotItemConfig.setRegion(createDbRegion(new Rectangle(500, 500, 200, 200)));
        dbBotConfig.getEnrageStateCrud().createDbChild();
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet);
        planetSystemService.deactivatePlanet(dbPlanet.getId());
        planetSystemService.activatePlanet(dbPlanet.getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        waitForBotToBuildup(TEST_PLANET_1_ID, dbBotConfig.createBotConfig(serverItemTypeService));

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(5000, 5000), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        sendFactoryCommand(getFirstSynItemId(TEST_FACTORY_ITEM_ID), TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone();
        waitForBotToBuildup(TEST_PLANET_1_ID, dbBotConfig.createBotConfig(serverItemTypeService));
        sendAttackCommand(getFirstSynItemId(TEST_ATTACK_ITEM_ID), getFirstSynItemId(getFirstBotBase(TEST_PLANET_1_ID), TEST_START_BUILDER_ITEM_ID));
        waitForActionServiceDone();
        waitForBotToBuildup(TEST_PLANET_1_ID, dbBotConfig.createBotConfig(serverItemTypeService));
        // TODO failed on 12.07.2012
        try {
            sendAttackCommand(getFirstSynItemId(TEST_ATTACK_ITEM_ID), getFirstSynItemId(getFirstBotBase(TEST_PLANET_1_ID), TEST_START_BUILDER_ITEM_ID));
        } catch (IllegalStateException e) {
            // killed before attack command could be sent
        }
        waitForActionServiceDone();

        // TODO failed on 28.11.2012, 28.11.2012, 05.12.2012
        Assert.assertFalse(getAllHistoryEntriesOfType(DbHistoryElement.Type.BOT_ENRAGE_UP).isEmpty());

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testUseRealmAsRegion() throws Exception {
        configureSimplePlanetNoResources();
        ServerPlanetServices serverPlanetServices = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID);

        Collection<BotItemConfig> botItems = new ArrayList<>();
        botItems.add(new BotItemConfig((BaseItemType) serverItemTypeService.getItemType(TEST_START_BUILDER_ITEM_ID), 1, true, null, false, null, false, null));
        botItems.add(new BotItemConfig((BaseItemType) serverItemTypeService.getItemType(TEST_FACTORY_ITEM_ID), 1, false, null, false, null, false, null));
        List<BotEnragementStateConfig> botEnragementStateConfigs = new ArrayList<>();
        botEnragementStateConfigs.add(new BotEnragementStateConfig("NormalTest", botItems, null));
        BotConfig botConfig = new BotConfig(1, false, 10, botEnragementStateConfigs, createRegion(new Rectangle(0, 0, 1000, 1000), 1), "Bot2", null, null, null, null);

        BotRunner botRunner = new ServerBotRunner(botConfig, serverPlanetServices, null);
        botRunner.start();

        waitForBotRunner(botRunner);
        assertWholeItemCount(TEST_PLANET_1_ID, 2);
    }

}
