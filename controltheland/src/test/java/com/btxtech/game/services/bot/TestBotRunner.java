package com.btxtech.game.services.bot;

import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.services.AbstractServiceTest;
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
    
}
