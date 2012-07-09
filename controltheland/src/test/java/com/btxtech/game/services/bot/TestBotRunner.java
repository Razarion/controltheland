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
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.bot.impl.ServerBotRunner;
import com.btxtech.game.services.common.ServerServices;
import com.btxtech.game.services.history.DbHistoryElement;
import com.btxtech.game.services.item.ItemService;
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
    private ItemService itemService;
    @Autowired
    private BaseService baseService;
    @Autowired
    private ServerServices serverServices;
    @Autowired
    private BotService botService;

    @Test
    @DirtiesContext
    public void botRunnerBuildupSimple() throws Exception {
        configureRealGame();

        BotEnragementState.Listener mockListener = EasyMock.createStrictMock(BotEnragementState.Listener.class);

        Collection<BotItemConfig> botItems = new ArrayList<>();
        botItems.add(new BotItemConfig((BaseItemType) itemService.getItemType(TEST_START_BUILDER_ITEM_ID), 1, true, new Rectangle(2000, 2000, 1000, 1000), false, null, false, null));
        List<BotEnragementStateConfig> botEnragementStateConfigs = new ArrayList<>();
        botEnragementStateConfigs.add(new BotEnragementStateConfig("NormalTest", botItems, null));
        BotConfig botConfig = new BotConfig(1, 10, botEnragementStateConfigs, null, "Bot", null, null, null, null);

        EasyMock.replay(mockListener);

        BotRunner botRunner = new ServerBotRunner(botConfig, serverServices, mockListener);
        botRunner.start();

        waitForBotRunner(botRunner);

        assertWholeItemCount(1);
        Assert.assertEquals("Bot", baseService.getBaseName(botRunner.getBase()));

        EasyMock.verify(mockListener);
    }

    @Test
    @DirtiesContext
    public void botRunnerBuildupComplex() throws Exception {
        configureRealGame();

        BotEnragementState.Listener mockListener = EasyMock.createStrictMock(BotEnragementState.Listener.class);

        Collection<BotItemConfig> botItems = new ArrayList<>();
        botItems.add(new BotItemConfig((BaseItemType) itemService.getItemType(TEST_START_BUILDER_ITEM_ID), 1, true, new Rectangle(2000, 2000, 1000, 1000), false, null, false, null));
        botItems.add(new BotItemConfig((BaseItemType) itemService.getItemType(TEST_FACTORY_ITEM_ID), 3, false, new Rectangle(2000, 2000, 1000, 1000), false, null, false, null));
        botItems.add(new BotItemConfig((BaseItemType) itemService.getItemType(TEST_ATTACK_ITEM_ID), 3, false, null, false, null, false, null));
        List<BotEnragementStateConfig> botEnragementStateConfigs = new ArrayList<>();
        botEnragementStateConfigs.add(new BotEnragementStateConfig("NormalTest", botItems, null));
        BotConfig botConfig = new BotConfig(1, 10, botEnragementStateConfigs, null, "TestBot", null, null, null, null);

        EasyMock.replay(mockListener);

        BotRunner botRunner = new ServerBotRunner(botConfig, serverServices, mockListener);
        botRunner.start();

        waitForBotRunner(botRunner);
        SimpleBase simpleBase1 = botRunner.getBase();
        assertWholeItemCount(7);
        Assert.assertEquals(1, getAllSynItemId(botRunner.getBase(), TEST_START_BUILDER_ITEM_ID, null).size());
        Assert.assertEquals(3, getAllSynItemId(botRunner.getBase(), TEST_FACTORY_ITEM_ID, null).size());
        Assert.assertEquals(3, getAllSynItemId(botRunner.getBase(), TEST_ATTACK_ITEM_ID, null).size());

        itemService.killSyncItemIds(getAllSynItemId(simpleBase1, TEST_FACTORY_ITEM_ID, null));
        itemService.killSyncItemIds(getAllSynItemId(simpleBase1, TEST_ATTACK_ITEM_ID, null));
        // TODO failed on 07.07.2012
        waitForBotRunner(botRunner);
        SimpleBase simpleBase2 = botRunner.getBase();
        assertWholeItemCount(7);
        Assert.assertEquals(1, getAllSynItemId(botRunner.getBase(), TEST_START_BUILDER_ITEM_ID, null).size());
        Assert.assertEquals(3, getAllSynItemId(botRunner.getBase(), TEST_FACTORY_ITEM_ID, null).size());
        Assert.assertEquals(3, getAllSynItemId(botRunner.getBase(), TEST_ATTACK_ITEM_ID, null).size());
        Assert.assertSame(simpleBase1, simpleBase2);
        EasyMock.verify(mockListener);
    }

    @Test
    @DirtiesContext
    public void testRebuildBot() throws Exception {
        configureRealGame();

        BotEnragementState.Listener mockListener = EasyMock.createStrictMock(BotEnragementState.Listener.class);

        Collection<BotItemConfig> botItems = new ArrayList<>();
        botItems.add(new BotItemConfig((BaseItemType) itemService.getItemType(TEST_START_BUILDER_ITEM_ID), 1, true, new Rectangle(2000, 2000, 1000, 1000), false, null, false, null));
        List<BotEnragementStateConfig> botEnragementStateConfigs = new ArrayList<>();
        botEnragementStateConfigs.add(new BotEnragementStateConfig("NormalTest", botItems, null));
        BotConfig botConfig = new BotConfig(1, 10, botEnragementStateConfigs, null, "Bot2", null, null, null, null);

        EasyMock.replay(mockListener);

        BotRunner botRunner = new ServerBotRunner(botConfig, serverServices, mockListener);
        botRunner.start();

        waitForBotRunner(botRunner);
        assertWholeItemCount(1);
        SimpleBase simpleBase1 = botRunner.getBase();
        Assert.assertEquals("Bot2", baseService.getBaseName(simpleBase1));
        itemService.killSyncItemIds(getAllSynItemId(simpleBase1, TEST_START_BUILDER_ITEM_ID, null));

        waitForBotRunner(botRunner);
        assertWholeItemCount(1);
        SimpleBase simpleBase2 = botRunner.getBase();
        Assert.assertNotSame(simpleBase1, simpleBase2);
        Assert.assertEquals("Bot2", baseService.getBaseName(simpleBase2));

        EasyMock.verify(mockListener);
    }

    @Test
    @DirtiesContext
    public void testKillBot() throws Exception {
        configureRealGame();

        BotEnragementState.Listener mockListener = EasyMock.createStrictMock(BotEnragementState.Listener.class);

        Collection<BotItemConfig> botItems = new ArrayList<>();
        botItems.add(new BotItemConfig((BaseItemType) itemService.getItemType(TEST_START_BUILDER_ITEM_ID), 1, true, new Rectangle(2000, 2000, 1000, 1000), false, null, false, null));
        botItems.add(new BotItemConfig((BaseItemType) itemService.getItemType(TEST_FACTORY_ITEM_ID), 3, false, new Rectangle(2000, 2000, 1000, 1000), false, null, false, null));
        botItems.add(new BotItemConfig((BaseItemType) itemService.getItemType(TEST_ATTACK_ITEM_ID), 4, false, null, false, null, false, null));
        List<BotEnragementStateConfig> botEnragementStateConfigs = new ArrayList<>();
        botEnragementStateConfigs.add(new BotEnragementStateConfig("NormalTest", botItems, null));
        BotConfig botConfig = new BotConfig(1, 10, botEnragementStateConfigs, null, "Bot2", null, null, null, null);

        EasyMock.replay(mockListener);

        BotRunner botRunner = new ServerBotRunner(botConfig, serverServices, mockListener);
        botRunner.start();

        waitForBotRunner(botRunner);
        assertWholeItemCount(8);
        SimpleBase simpleBase1 = botRunner.getBase();

        botRunner.kill();

        Thread.sleep(1000);

        assertWholeItemCount(0);
        SimpleBase simpleBase2 = botRunner.getBase();
        Assert.assertFalse(baseService.isAlive(simpleBase1));
        Assert.assertFalse(baseService.isAlive(simpleBase2));
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
        configureRealGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        SimpleBase targetBase = getMyBase();
        Id targetId = getFirstSynItemId(targetBase, TEST_START_BUILDER_ITEM_ID);
        sendMoveCommand(targetId, new Index(2000, 2000));
        waitForActionServiceDone();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        BotEnragementState.Listener mockListener = EasyMock.createStrictMock(BotEnragementState.Listener.class);

        Collection<BotItemConfig> botItems = new ArrayList<>();
        botItems.add(new BotItemConfig((BaseItemType) itemService.getItemType(TEST_ATTACK_ITEM_ID), 1, true, new Rectangle(0, 0, 1000, 1000), false, null, false, null));
        List<BotEnragementStateConfig> botEnragementStateConfigs = new ArrayList<>();
        botEnragementStateConfigs.add(new BotEnragementStateConfig("NormalTest", botItems, null));
        BotConfig botConfig = new BotConfig(1, 10, botEnragementStateConfigs, new Rectangle(0, 0, 4000, 4000), "Bot2", null, null, null, null);

        EasyMock.replay(mockListener);

        BotRunner botRunner = new ServerBotRunner(botConfig, serverServices, mockListener);
        botRunner.start();

        waitForBotRunner(botRunner);
        assertWholeItemCount(2);

        SyncItem target = itemService.getItem(targetId);
        Assert.assertTrue(target.isAlive());

        Thread.sleep(15);
        waitForActionServiceDone();

        Assert.assertFalse(target.isAlive());

        EasyMock.verify(mockListener);
    }

    @Test
    @DirtiesContext
    public void intervalBuildup() throws Exception {
        configureRealGame();

        BotEnragementState.Listener mockListener = EasyMock.createStrictMock(BotEnragementState.Listener.class);

        Collection<BotItemConfig> botItems = new ArrayList<>();
        botItems.add(new BotItemConfig((BaseItemType) itemService.getItemType(TEST_ATTACK_ITEM_ID), 3, true, new Rectangle(0, 0, 1000, 1000), false, null, false, null));
        List<BotEnragementStateConfig> botEnragementStateConfigs = new ArrayList<>();
        botEnragementStateConfigs.add(new BotEnragementStateConfig("NormalTest", botItems, null));
        BotConfig botConfig = new BotConfig(1, 10, botEnragementStateConfigs, new Rectangle(0, 0, 4000, 4000), "Bot4", 100L, 200L, 200L, 300L);

        EasyMock.replay(mockListener);


        BotRunner botRunner = new ServerBotRunner(botConfig, serverServices, mockListener);
        botRunner.start();

        assertWholeItemCount(0);
        Thread.sleep(250);
        assertWholeItemCount(3);
        Assert.assertEquals("Bot4", baseService.getBaseName(botRunner.getBase()));

        EasyMock.verify(mockListener);
        botRunner.kill(); //Avoid background timer & thread
    }

    @Test
    @DirtiesContext
    public void intervalPeriodicalBuildup() throws Exception {
        configureRealGame();

        BotEnragementState.Listener mockListener = EasyMock.createStrictMock(BotEnragementState.Listener.class);

        Collection<BotItemConfig> botItems = new ArrayList<>();
        botItems.add(new BotItemConfig((BaseItemType) itemService.getItemType(TEST_ATTACK_ITEM_ID), 5, true, new Rectangle(0, 0, 1000, 1000), false, null, false, null));
        List<BotEnragementStateConfig> botEnragementStateConfigs = new ArrayList<>();
        botEnragementStateConfigs.add(new BotEnragementStateConfig("NormalTest", botItems, null));
        BotConfig botConfig = new BotConfig(1, 10, botEnragementStateConfigs, new Rectangle(0, 0, 4000, 4000), "Bot4", 500L, 500L, 500L, 500L);

        EasyMock.replay(mockListener);


        BotRunner botRunner = new ServerBotRunner(botConfig, serverServices, mockListener);
        botRunner.start();
        Thread.sleep(250);

        for (int i = 0; i < 10; i++) {
            assertWholeItemCount(0);
            Thread.sleep(500);
            assertWholeItemCount(5);
            Thread.sleep(500);
        }

        EasyMock.verify(mockListener);
        botRunner.kill(); //Avoid background timer & thread
    }

    @Test
    @DirtiesContext
    public void intervalKillActive() throws Exception {
        configureRealGame();

        BotEnragementState.Listener mockListener = EasyMock.createStrictMock(BotEnragementState.Listener.class);

        Collection<BotItemConfig> botItems = new ArrayList<>();
        botItems.add(new BotItemConfig((BaseItemType) itemService.getItemType(TEST_ATTACK_ITEM_ID), 3, true, new Rectangle(0, 0, 1000, 1000), false, null, false, null));
        List<BotEnragementStateConfig> botEnragementStateConfigs = new ArrayList<>();
        botEnragementStateConfigs.add(new BotEnragementStateConfig("NormalTest", botItems, null));
        BotConfig botConfig = new BotConfig(1, 10, botEnragementStateConfigs, new Rectangle(0, 0, 4000, 4000), "Bot4", 40L, 70L, 100L, 120L);

        EasyMock.replay(mockListener);

        BotRunner botRunner = new ServerBotRunner(botConfig, serverServices, mockListener);
        botRunner.start();
        assertWholeItemCount(0);

        Thread.sleep(100);
        assertWholeItemCount(3);

        botRunner.kill();
        assertWholeItemCount(0);

        for (int i = 0; i < 200; i++) {
            Thread.sleep(20);
            assertWholeItemCount(0);
        }
        EasyMock.verify(mockListener);
    }

    @Test
    @DirtiesContext
    public void intervalKillInactive() throws Exception {
        configureRealGame();

        BotEnragementState.Listener mockListener = EasyMock.createStrictMock(BotEnragementState.Listener.class);

        Collection<BotItemConfig> botItems = new ArrayList<>();
        botItems.add(new BotItemConfig((BaseItemType) itemService.getItemType(TEST_ATTACK_ITEM_ID), 3, true, new Rectangle(0, 0, 1000, 1000), false, null, false, null));
        List<BotEnragementStateConfig> botEnragementStateConfigs = new ArrayList<>();
        botEnragementStateConfigs.add(new BotEnragementStateConfig("NormalTest", botItems, null));
        BotConfig botConfig = new BotConfig(1, 10, botEnragementStateConfigs, new Rectangle(0, 0, 4000, 4000), "Bot4", 80L, 100L, 50L, 60L);

        EasyMock.replay(mockListener);

        BotRunner botRunner = new ServerBotRunner(botConfig, serverServices, mockListener);
        botRunner.start();
        assertWholeItemCount(0);
        Thread.sleep(40);

        botRunner.kill();

        for (int i = 0; i < 200; i++) {
            Thread.sleep(20);
            assertWholeItemCount(0);
        }
        EasyMock.verify(mockListener);
    }

    @Test
    @DirtiesContext
    public void botBaseKilledAndEnrage() throws Exception {
        configureRealGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBotConfig dbBotConfig = botService.getDbBotConfigCrudServiceHelper().createDbChild();
        dbBotConfig.setActionDelay(10);
        dbBotConfig.setName("TestBot");
        dbBotConfig.setRealGameBot(true);
        dbBotConfig.setRealm(new Rectangle(0, 0, 1000, 1000));
        DbBotEnragementStateConfig dbBotEnragementStateConfig1 = dbBotConfig.getEnrageStateCrud().createDbChild();
        dbBotEnragementStateConfig1.setName("Normal");
        dbBotEnragementStateConfig1.setEnrageUpKills(2);
        DbBotItemConfig dbBotItemConfig = dbBotEnragementStateConfig1.getBotItemCrud().createDbChild();
        dbBotItemConfig.setBaseItemType(itemService.getDbBaseItemType(TEST_START_BUILDER_ITEM_ID));
        dbBotItemConfig.setCount(1);
        dbBotItemConfig.setCreateDirectly(true);
        dbBotItemConfig.setRegion(new Rectangle(500,500,200,200));
        dbBotConfig.getEnrageStateCrud().createDbChild();
        botService.getDbBotConfigCrudServiceHelper().updateDbChild(dbBotConfig);
        botService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        waitForBotToBuildup(dbBotConfig.createBotConfig(itemService));

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(5000, 5000), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        sendFactoryCommand(getFirstSynItemId(TEST_FACTORY_ITEM_ID), TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone();
        waitForBotToBuildup(dbBotConfig.createBotConfig(itemService));
        sendAttackCommand(getFirstSynItemId(TEST_ATTACK_ITEM_ID), getFirstSynItemId(getFirstBotBase(), TEST_START_BUILDER_ITEM_ID));
        waitForActionServiceDone();
        waitForBotToBuildup(dbBotConfig.createBotConfig(itemService));
        sendAttackCommand(getFirstSynItemId(TEST_ATTACK_ITEM_ID), getFirstSynItemId(getFirstBotBase(), TEST_START_BUILDER_ITEM_ID));
        waitForActionServiceDone();

        Assert.assertFalse(getAllHistoryEntriesOfType(DbHistoryElement.Type.BOT_ENRAGE_UP).isEmpty());

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

}
