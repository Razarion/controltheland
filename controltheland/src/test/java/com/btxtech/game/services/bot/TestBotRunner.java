package com.btxtech.game.services.bot;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.base.Base;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.bot.impl.BotRunner;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.user.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;

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
    private ApplicationContext applicationContext;
    @Autowired
    private UserService userService;
    @Autowired
    private BaseService baseService;

    @Test
    @DirtiesContext
    public void botRunnerBuildupSimple() throws Exception {
        configureMinimalGame();

        DbBotConfig dbBotConfig = new DbBotConfig();
        dbBotConfig.init(userService);
        dbBotConfig.setActionDelay(10);
        DbBotItemConfig config1 = dbBotConfig.getBotItemCrud().createDbChild();
        config1.setCount(1);
        config1.setBaseItemType(itemService.getDbBaseItemType(TEST_START_BUILDER_ITEM_ID));
        config1.setRegion(new Rectangle(2000, 2000, 1000, 1000));
        config1.setCreateDirectly(true);

        BotRunner botRunner = (BotRunner) applicationContext.getBean("botRunner");
        botRunner.start(dbBotConfig);

        waitForBotRunner(botRunner);

        assertWholeItemCount(1);
    }

    @Test
    @DirtiesContext
    public void botRunnerBuildupComplex() throws Exception {
        configureMinimalGame();

        DbBotConfig dbBotConfig = new DbBotConfig();
        dbBotConfig.init(userService);
        dbBotConfig.setActionDelay(10);
        DbBotItemConfig config1 = dbBotConfig.getBotItemCrud().createDbChild();
        config1.setCount(1);
        config1.setBaseItemType(itemService.getDbBaseItemType(TEST_START_BUILDER_ITEM_ID));
        config1.setRegion(new Rectangle(2000, 2000, 1000, 1000));
        config1.setCreateDirectly(true);
        DbBotItemConfig config2 = dbBotConfig.getBotItemCrud().createDbChild();
        config2.setCount(3);
        config2.setBaseItemType(itemService.getDbBaseItemType(TEST_FACTORY_ITEM_ID));
        config2.setRegion(new Rectangle(2000, 2000, 1000, 1000));
        DbBotItemConfig config3 = dbBotConfig.getBotItemCrud().createDbChild();
        config3.setCount(3);
        config3.setBaseItemType(itemService.getDbBaseItemType(TEST_ATTACK_ITEM_ID));

        BotRunner botRunner = (BotRunner) applicationContext.getBean("botRunner");
        botRunner.start(dbBotConfig);

        waitForBotRunner(botRunner);
        SimpleBase simpleBase1 = botRunner.getBase().getSimpleBase();
        assertWholeItemCount(7);
        Assert.assertEquals(1, getAllSynItemId(botRunner.getBase().getSimpleBase(), TEST_START_BUILDER_ITEM_ID, null).size());
        Assert.assertEquals(3, getAllSynItemId(botRunner.getBase().getSimpleBase(), TEST_FACTORY_ITEM_ID, null).size());
        Assert.assertEquals(3, getAllSynItemId(botRunner.getBase().getSimpleBase(), TEST_ATTACK_ITEM_ID, null).size());

        itemService.killSyncItemIds(getAllSynItemId(simpleBase1, TEST_FACTORY_ITEM_ID, null));
        itemService.killSyncItemIds(getAllSynItemId(simpleBase1, TEST_ATTACK_ITEM_ID, null));
        waitForBotRunner(botRunner);
        SimpleBase simpleBase2 = botRunner.getBase().getSimpleBase();
        assertWholeItemCount(7);
        Assert.assertEquals(1, getAllSynItemId(botRunner.getBase().getSimpleBase(), TEST_START_BUILDER_ITEM_ID, null).size());
        Assert.assertEquals(3, getAllSynItemId(botRunner.getBase().getSimpleBase(), TEST_FACTORY_ITEM_ID, null).size());
        Assert.assertEquals(3, getAllSynItemId(botRunner.getBase().getSimpleBase(), TEST_ATTACK_ITEM_ID, null).size());
        Assert.assertSame(simpleBase1, simpleBase2);
    }

    @Test
    @DirtiesContext
    public void testRebuildBot() throws Exception {
        configureMinimalGame();

        DbBotConfig dbBotConfig = new DbBotConfig();
        dbBotConfig.init(userService);
        dbBotConfig.setActionDelay(10);
        DbBotItemConfig config1 = dbBotConfig.getBotItemCrud().createDbChild();
        config1.setCount(1);
        config1.setBaseItemType(itemService.getDbBaseItemType(TEST_START_BUILDER_ITEM_ID));
        config1.setRegion(new Rectangle(2000, 2000, 1000, 1000));
        config1.setCreateDirectly(true);

        BotRunner botRunner = (BotRunner) applicationContext.getBean("botRunner");
        botRunner.start(dbBotConfig);

        waitForBotRunner(botRunner);
        assertWholeItemCount(1);
        SimpleBase simpleBase1 = botRunner.getBase().getSimpleBase();
        itemService.killSyncItemIds(getAllSynItemId(simpleBase1, TEST_START_BUILDER_ITEM_ID, null));

        waitForBotRunner(botRunner);
        assertWholeItemCount(1);
        SimpleBase simpleBase2 = botRunner.getBase().getSimpleBase();
        Assert.assertNotSame(simpleBase1, simpleBase2);
    }

    @Test
    @DirtiesContext
    public void testKillBot() throws Exception {
        configureMinimalGame();

        DbBotConfig dbBotConfig = new DbBotConfig();
        dbBotConfig.init(userService);
        dbBotConfig.setActionDelay(10);
        DbBotItemConfig config1 = dbBotConfig.getBotItemCrud().createDbChild();
        config1.setCount(1);
        config1.setBaseItemType(itemService.getDbBaseItemType(TEST_START_BUILDER_ITEM_ID));
        config1.setRegion(new Rectangle(2000, 2000, 1000, 1000));
        config1.setCreateDirectly(true);
        DbBotItemConfig config2 = dbBotConfig.getBotItemCrud().createDbChild();
        config2.setCount(3);
        config2.setBaseItemType(itemService.getDbBaseItemType(TEST_FACTORY_ITEM_ID));
        config2.setRegion(new Rectangle(2000, 2000, 1000, 1000));
        DbBotItemConfig config3 = dbBotConfig.getBotItemCrud().createDbChild();
        config3.setCount(4);
        config3.setBaseItemType(itemService.getDbBaseItemType(TEST_ATTACK_ITEM_ID));

        BotRunner botRunner = (BotRunner) applicationContext.getBean("botRunner");
        botRunner.start(dbBotConfig);

        waitForBotRunner(botRunner);
        assertWholeItemCount(8);
        SimpleBase simpleBase1 = botRunner.getBase().getSimpleBase();

        botRunner.kill();

        Thread.sleep(1000);

        assertWholeItemCount(0);
        SimpleBase simpleBase2 = botRunner.getBase().getSimpleBase();
        Assert.assertFalse(baseService.isAlive(simpleBase1));
        Assert.assertFalse(baseService.isAlive(simpleBase2));
        Assert.assertFalse(botRunner.isBuildup());
    }

    private void waitForBotRunner(BotRunner botRunner) throws InterruptedException, TimeoutException {
        long maxTime = System.currentTimeMillis() + 10000;
        while (!botRunner.isBuildup()) {
            if (System.currentTimeMillis() > maxTime) {
                throw new TimeoutException();
            }
            Thread.sleep(100);
        }
    }

    @Test
    @DirtiesContext
    public void attack() throws Exception {
        configureMinimalGame();

        Base base = createBase(TEST_START_BUILDER_ITEM_ID);

        DbBotConfig dbBotConfig = new DbBotConfig();
        dbBotConfig.init(userService);
        dbBotConfig.setActionDelay(10);
        dbBotConfig.setRealm(new Rectangle(0, 0, 4000, 4000));
        DbBotItemConfig config1 = dbBotConfig.getBotItemCrud().createDbChild();
        config1.setCount(1);
        config1.setBaseItemType(itemService.getDbBaseItemType(TEST_ATTACK_ITEM_ID));
        config1.setRegion(new Rectangle(0, 0, 1000, 1000));
        config1.setCreateDirectly(true);

        BotRunner botRunner = (BotRunner) applicationContext.getBean("botRunner");
        botRunner.start(dbBotConfig);

        waitForBotRunner(botRunner);
        assertWholeItemCount(2);

        SyncBaseItem syncBaseItem2 = createSyncBaseItemAndAddItemService(TEST_START_BUILDER_ITEM_ID, new Index(2000, 2000), base.getSimpleBase());
        Assert.assertTrue(syncBaseItem2.isAlive());

        Thread.sleep(15);
        waitForActionServiceDone();

        Assert.assertFalse(syncBaseItem2.isAlive());
    }

    @Test
    @DirtiesContext
    public void intervalConfig() throws Exception {
        DbBotConfig dbBotConfig = new DbBotConfig();
        Assert.assertFalse(dbBotConfig.isIntervalBot());
        Assert.assertFalse(dbBotConfig.isIntervalValid());

        dbBotConfig.setMaxActiveMs(10L);
        dbBotConfig.setMinActiveMs(5L);
        dbBotConfig.setMaxInactiveMs(20L);
        dbBotConfig.setMinInactiveMs(15L);
        Assert.assertTrue(dbBotConfig.isIntervalBot());
        Assert.assertTrue(dbBotConfig.isIntervalValid());

        dbBotConfig.setMaxActiveMs(null);
        Assert.assertTrue(dbBotConfig.isIntervalBot());
        Assert.assertFalse(dbBotConfig.isIntervalValid());

        dbBotConfig.setMaxActiveMs(0L);
        Assert.assertTrue(dbBotConfig.isIntervalBot());
        Assert.assertFalse(dbBotConfig.isIntervalValid());

        dbBotConfig.setMaxActiveMs(1L);
        Assert.assertTrue(dbBotConfig.isIntervalBot());
        Assert.assertFalse(dbBotConfig.isIntervalValid());

        dbBotConfig.setMaxActiveMs(5L);
        Assert.assertTrue(dbBotConfig.isIntervalBot());
        Assert.assertTrue(dbBotConfig.isIntervalValid());

        dbBotConfig.setMaxActiveMs(11L);
        Assert.assertTrue(dbBotConfig.isIntervalBot());
        Assert.assertTrue(dbBotConfig.isIntervalValid());

        dbBotConfig.setMinActiveMs(null);
        Assert.assertTrue(dbBotConfig.isIntervalBot());
        Assert.assertFalse(dbBotConfig.isIntervalValid());

        dbBotConfig.setMinActiveMs(0L);
        Assert.assertTrue(dbBotConfig.isIntervalBot());
        Assert.assertFalse(dbBotConfig.isIntervalValid());

        dbBotConfig.setMinActiveMs(12L);
        Assert.assertTrue(dbBotConfig.isIntervalBot());
        Assert.assertFalse(dbBotConfig.isIntervalValid());

        dbBotConfig.setMinActiveMs(11L);
        Assert.assertTrue(dbBotConfig.isIntervalBot());
        Assert.assertTrue(dbBotConfig.isIntervalValid());

        dbBotConfig.setMinActiveMs(5L);
        Assert.assertTrue(dbBotConfig.isIntervalBot());
        Assert.assertTrue(dbBotConfig.isIntervalValid());

        dbBotConfig.setMinInactiveMs(null);
        Assert.assertTrue(dbBotConfig.isIntervalBot());
        Assert.assertFalse(dbBotConfig.isIntervalValid());

        dbBotConfig.setMinInactiveMs(0L);
        Assert.assertTrue(dbBotConfig.isIntervalBot());
        Assert.assertFalse(dbBotConfig.isIntervalValid());

        dbBotConfig.setMinInactiveMs(21L);
        Assert.assertTrue(dbBotConfig.isIntervalBot());
        Assert.assertFalse(dbBotConfig.isIntervalValid());

        dbBotConfig.setMinInactiveMs(20L);
        Assert.assertTrue(dbBotConfig.isIntervalBot());
        Assert.assertTrue(dbBotConfig.isIntervalValid());

        dbBotConfig.setMinInactiveMs(17L);
        Assert.assertTrue(dbBotConfig.isIntervalBot());
        Assert.assertTrue(dbBotConfig.isIntervalValid());

        dbBotConfig.setMaxInactiveMs(null);
        Assert.assertTrue(dbBotConfig.isIntervalBot());
        Assert.assertFalse(dbBotConfig.isIntervalValid());

        dbBotConfig.setMaxInactiveMs(0L);
        Assert.assertTrue(dbBotConfig.isIntervalBot());
        Assert.assertFalse(dbBotConfig.isIntervalValid());

        dbBotConfig.setMaxInactiveMs(16L);
        Assert.assertTrue(dbBotConfig.isIntervalBot());
        Assert.assertFalse(dbBotConfig.isIntervalValid());

        dbBotConfig.setMaxInactiveMs(17L);
        Assert.assertTrue(dbBotConfig.isIntervalBot());
        Assert.assertTrue(dbBotConfig.isIntervalValid());

        dbBotConfig.setMaxInactiveMs(18L);
        Assert.assertTrue(dbBotConfig.isIntervalBot());
        Assert.assertTrue(dbBotConfig.isIntervalValid());
    }

    @Test
    @DirtiesContext
    public void intervalBuildup() throws Exception {
        configureMinimalGame();

        DbBotConfig dbBotConfig = new DbBotConfig();
        dbBotConfig.setMaxActiveMs(300L);
        dbBotConfig.setMinActiveMs(200L);
        dbBotConfig.setMaxInactiveMs(200L);
        dbBotConfig.setMinInactiveMs(100L);
        dbBotConfig.init(userService);
        dbBotConfig.setActionDelay(10);
        dbBotConfig.setRealm(new Rectangle(0, 0, 4000, 4000));
        DbBotItemConfig config1 = dbBotConfig.getBotItemCrud().createDbChild();
        config1.setCount(3);
        config1.setBaseItemType(itemService.getDbBaseItemType(TEST_ATTACK_ITEM_ID));
        config1.setRegion(new Rectangle(0, 0, 1000, 1000));
        config1.setCreateDirectly(true);

        BotRunner botRunner = (BotRunner) applicationContext.getBean("botRunner");
        botRunner.start(dbBotConfig);

        assertWholeItemCount(0);
        Thread.sleep(250);
        assertWholeItemCount(3);

        botRunner.kill(); //Avoid background timer & thread
    }

    @Test
    @DirtiesContext
    public void intervalPeriodicalBuildup() throws Exception {
        configureMinimalGame();

        DbBotConfig dbBotConfig = new DbBotConfig();
        dbBotConfig.setMaxActiveMs(500L);
        dbBotConfig.setMinActiveMs(500L);
        dbBotConfig.setMaxInactiveMs(500L);
        dbBotConfig.setMinInactiveMs(500L);
        dbBotConfig.init(userService);
        dbBotConfig.setActionDelay(10);
        dbBotConfig.setRealm(new Rectangle(0, 0, 4000, 4000));
        DbBotItemConfig config1 = dbBotConfig.getBotItemCrud().createDbChild();
        config1.setCount(5);
        config1.setBaseItemType(itemService.getDbBaseItemType(TEST_ATTACK_ITEM_ID));
        config1.setRegion(new Rectangle(0, 0, 1000, 1000));
        config1.setCreateDirectly(true);

        BotRunner botRunner = (BotRunner) applicationContext.getBean("botRunner");
        botRunner.start(dbBotConfig);
        Thread.sleep(200);

        for (int i = 0; i < 10; i++) {
            assertWholeItemCount(0);
            Thread.sleep(500);
            assertWholeItemCount(5);
            Thread.sleep(500);
        }

        botRunner.kill(); //Avoid background timer & thread
    }

    @Test
    @DirtiesContext
    public void intervalKillActive() throws Exception {
        configureMinimalGame();

        DbBotConfig dbBotConfig = new DbBotConfig();
        dbBotConfig.setMaxActiveMs(120L);
        dbBotConfig.setMinActiveMs(100L);
        dbBotConfig.setMaxInactiveMs(70L);
        dbBotConfig.setMinInactiveMs(40L);
        dbBotConfig.init(userService);
        dbBotConfig.setActionDelay(10);
        dbBotConfig.setRealm(new Rectangle(0, 0, 4000, 4000));
        DbBotItemConfig config1 = dbBotConfig.getBotItemCrud().createDbChild();
        config1.setCount(3);
        config1.setBaseItemType(itemService.getDbBaseItemType(TEST_ATTACK_ITEM_ID));
        config1.setRegion(new Rectangle(0, 0, 1000, 1000));
        config1.setCreateDirectly(true);

        BotRunner botRunner = (BotRunner) applicationContext.getBean("botRunner");
        botRunner.start(dbBotConfig);
        assertWholeItemCount(0);

        Thread.sleep(100);
        assertWholeItemCount(3);

        botRunner.kill();
        assertWholeItemCount(0);

        for (int i = 0; i < 200; i++) {
            Thread.sleep(20);
            assertWholeItemCount(0);
        }
    }

    @Test
    @DirtiesContext
    public void intervalKillInactive() throws Exception {
        configureMinimalGame();

        DbBotConfig dbBotConfig = new DbBotConfig();
        dbBotConfig.setMaxActiveMs(60L);
        dbBotConfig.setMinActiveMs(50L);
        dbBotConfig.setMaxInactiveMs(100L);
        dbBotConfig.setMinInactiveMs(80L);
        dbBotConfig.init(userService);
        dbBotConfig.setActionDelay(10);
        dbBotConfig.setRealm(new Rectangle(0, 0, 4000, 4000));
        DbBotItemConfig config1 = dbBotConfig.getBotItemCrud().createDbChild();
        config1.setCount(3);
        config1.setBaseItemType(itemService.getDbBaseItemType(TEST_ATTACK_ITEM_ID));
        config1.setRegion(new Rectangle(0, 0, 1000, 1000));
        config1.setCreateDirectly(true);

        BotRunner botRunner = (BotRunner) applicationContext.getBean("botRunner");
        botRunner.start(dbBotConfig);
        assertWholeItemCount(0);
        Thread.sleep(40);

        botRunner.kill();

        for (int i = 0; i < 200; i++) {
            Thread.sleep(20);
            assertWholeItemCount(0);
        }
    }
}
