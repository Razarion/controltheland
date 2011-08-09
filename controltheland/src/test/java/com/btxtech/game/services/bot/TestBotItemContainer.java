package com.btxtech.game.services.bot;

import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.base.Base;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.bot.impl.BotItemContainer;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeoutException;

/**
 * User: beat
 * Date: 08.08.2011
 * Time: 21:42:28
 */
public class TestBotItemContainer extends AbstractServiceTest {
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
    public void oneItem() throws Exception {
        configureMinimalGame();

        Collection<DbBotItemConfig> dbBotItemConfigs = new ArrayList<DbBotItemConfig>();
        DbBotItemConfig config1 = new DbBotItemConfig();
        config1.setCount(1);
        config1.setBaseItemType(itemService.getDbBaseItemType(TEST_START_BUILDER_ITEM_ID));
        config1.setRegion(new Rectangle(2000, 2000, 1000, 1000));
        config1.setCreateDirectly(true);
        dbBotItemConfigs.add(config1);

        BotItemContainer botItemContainer = (BotItemContainer) applicationContext.getBean("botItemContainer");
        botItemContainer.init(dbBotItemConfigs);

        UserState userState = userService.getUserState(new DbBotConfig());
        Base base = baseService.createBotBase(userState, "Test Bot");

        Assert.assertFalse(botItemContainer.isFulfilled(userState));

        botItemContainer.buildup(base.getSimpleBase(), userState);
        waitForBotItemContainer(botItemContainer, userState);

        Assert.assertEquals(1, getAllSynItemId(base.getSimpleBase(), TEST_START_BUILDER_ITEM_ID, null).size());

        botItemContainer.killAllItems();
        assertWholeItemCount(0);
    }

    private void waitForBotItemContainer(BotItemContainer botItemContainer, UserState userState) throws InterruptedException, TimeoutException {
        long maxTime = System.currentTimeMillis() + 10000;
        while (!botItemContainer.isFulfilled(userState)) {
            if (System.currentTimeMillis() > maxTime) {
                throw new TimeoutException();
            }
            Thread.sleep(100);
        }
    }

    @Test
    @DirtiesContext
    public void threeItems() throws Exception {
        configureMinimalGame();

        Collection<DbBotItemConfig> dbBotItemConfigs = new ArrayList<DbBotItemConfig>();
        DbBotItemConfig config1 = new DbBotItemConfig();
        config1.setCount(1);
        config1.setBaseItemType(itemService.getDbBaseItemType(TEST_START_BUILDER_ITEM_ID));
        config1.setRegion(new Rectangle(2000, 2000, 1000, 1000));
        config1.setCreateDirectly(true);
        dbBotItemConfigs.add(config1);
        DbBotItemConfig config2 = new DbBotItemConfig();
        config2.setCount(2);
        config2.setBaseItemType(itemService.getDbBaseItemType(TEST_FACTORY_ITEM_ID));
        config2.setRegion(new Rectangle(2000, 2000, 1000, 1000));
        dbBotItemConfigs.add(config2);

        BotItemContainer botItemContainer = (BotItemContainer) applicationContext.getBean("botItemContainer");
        botItemContainer.init(dbBotItemConfigs);

        UserState userState = userService.getUserState(new DbBotConfig());
        Base base = baseService.createBotBase(userState, "Test Bot");

        Assert.assertFalse(botItemContainer.isFulfilled(userState));

        botItemContainer.buildup(base.getSimpleBase(), userState);
        Assert.assertEquals(1, getAllSynItemId(base.getSimpleBase(), TEST_START_BUILDER_ITEM_ID, null).size());
        Assert.assertFalse(botItemContainer.isFulfilled(userState));

        botItemContainer.buildup(base.getSimpleBase(), userState);
        waitForActionServiceDone();
        Assert.assertEquals(1, getAllSynItemId(base.getSimpleBase(), TEST_START_BUILDER_ITEM_ID, null).size());
        Assert.assertEquals(1, getAllSynItemId(base.getSimpleBase(), TEST_FACTORY_ITEM_ID, null).size());
        Assert.assertFalse(botItemContainer.isFulfilled(userState));

        botItemContainer.buildup(base.getSimpleBase(), userState);
        waitForActionServiceDone();
        Assert.assertTrue(botItemContainer.isFulfilled(userState));
        Assert.assertEquals(1, getAllSynItemId(base.getSimpleBase(), TEST_START_BUILDER_ITEM_ID, null).size());
        Assert.assertEquals(2, getAllSynItemId(base.getSimpleBase(), TEST_FACTORY_ITEM_ID, null).size());

        // Does nothing
        botItemContainer.buildup(base.getSimpleBase(), userState);
        waitForActionServiceDone();
        Assert.assertTrue(botItemContainer.isFulfilled(userState));
        Assert.assertEquals(1, getAllSynItemId(base.getSimpleBase(), TEST_START_BUILDER_ITEM_ID, null).size());
        Assert.assertEquals(2, getAllSynItemId(base.getSimpleBase(), TEST_FACTORY_ITEM_ID, null).size());

        Id id = getAllSynItemId(base.getSimpleBase(), TEST_FACTORY_ITEM_ID, null).get(0);
        killSyncItem(id);
        Assert.assertFalse(botItemContainer.isFulfilled(userState));

        botItemContainer.buildup(base.getSimpleBase(), userState);
        waitForActionServiceDone();
        Assert.assertTrue(botItemContainer.isFulfilled(userState));
        Assert.assertEquals(1, getAllSynItemId(base.getSimpleBase(), TEST_START_BUILDER_ITEM_ID, null).size());
        Assert.assertEquals(2, getAllSynItemId(base.getSimpleBase(), TEST_FACTORY_ITEM_ID, null).size());

        id = getAllSynItemId(base.getSimpleBase(), TEST_START_BUILDER_ITEM_ID, null).get(0);
        killSyncItem(id);
        id = getAllSynItemId(base.getSimpleBase(), TEST_FACTORY_ITEM_ID, null).get(0);
        killSyncItem(id);
        Assert.assertFalse(botItemContainer.isFulfilled(userState));

        botItemContainer.buildup(base.getSimpleBase(), userState);
        waitForActionServiceDone();
        Assert.assertFalse(botItemContainer.isFulfilled(userState));
        Assert.assertEquals(1, getAllSynItemId(base.getSimpleBase(), TEST_START_BUILDER_ITEM_ID, null).size());
        Assert.assertEquals(1, getAllSynItemId(base.getSimpleBase(), TEST_FACTORY_ITEM_ID, null).size());

        botItemContainer.buildup(base.getSimpleBase(), userState);
        waitForActionServiceDone();
        Assert.assertTrue(botItemContainer.isFulfilled(userState));
        Assert.assertEquals(1, getAllSynItemId(base.getSimpleBase(), TEST_START_BUILDER_ITEM_ID, null).size());
        Assert.assertEquals(2, getAllSynItemId(base.getSimpleBase(), TEST_FACTORY_ITEM_ID, null).size());

        botItemContainer.killAllItems();
        assertWholeItemCount(0);
    }

    @Test
    @DirtiesContext
    public void complexItems() throws Exception {
        configureMinimalGame();

        Collection<DbBotItemConfig> dbBotItemConfigs = new ArrayList<DbBotItemConfig>();
        DbBotItemConfig config1 = new DbBotItemConfig();
        config1.setCount(1);
        config1.setBaseItemType(itemService.getDbBaseItemType(TEST_START_BUILDER_ITEM_ID));
        config1.setRegion(new Rectangle(2000, 2000, 1000, 1000));
        config1.setCreateDirectly(true);
        dbBotItemConfigs.add(config1);
        DbBotItemConfig config2 = new DbBotItemConfig();
        config2.setCount(1);
        config2.setBaseItemType(itemService.getDbBaseItemType(TEST_FACTORY_ITEM_ID));
        config2.setRegion(new Rectangle(2000, 2000, 1000, 1000));
        dbBotItemConfigs.add(config2);
        DbBotItemConfig config3 = new DbBotItemConfig();
        config3.setCount(3);
        config3.setBaseItemType(itemService.getDbBaseItemType(TEST_ATTACK_ITEM_ID));
        dbBotItemConfigs.add(config3);
        DbBotItemConfig config4 = new DbBotItemConfig();
        config4.setCount(2);
        config4.setBaseItemType(itemService.getDbBaseItemType(TEST_SIMPLE_BUILDING_ID));
        config4.setRegion(new Rectangle(2000, 2000, 1000, 1000));
        config4.setCreateDirectly(true);
        dbBotItemConfigs.add(config4);

        BotItemContainer botItemContainer = (BotItemContainer) applicationContext.getBean("botItemContainer");
        botItemContainer.init(dbBotItemConfigs);

        UserState userState = userService.getUserState(new DbBotConfig());
        Base base = baseService.createBotBase(userState, "Test Bot");
        Assert.assertFalse(botItemContainer.isFulfilled(userState));

        for (int i = 0; i < 5; i++) {
            Assert.assertFalse(botItemContainer.isFulfilled(userState));
            botItemContainer.buildup(base.getSimpleBase(), userState);
            waitForActionServiceDone();
        }
        Assert.assertTrue(botItemContainer.isFulfilled(userState));
        Assert.assertEquals(1, getAllSynItemId(base.getSimpleBase(), TEST_START_BUILDER_ITEM_ID, null).size());
        Assert.assertEquals(1, getAllSynItemId(base.getSimpleBase(), TEST_FACTORY_ITEM_ID, null).size());
        Assert.assertEquals(3, getAllSynItemId(base.getSimpleBase(), TEST_ATTACK_ITEM_ID, null).size());
        Assert.assertEquals(2, getAllSynItemId(base.getSimpleBase(), TEST_SIMPLE_BUILDING_ID, null).size());

        itemService.killSyncItemIds(getAllSynItemId(base.getSimpleBase(), TEST_ATTACK_ITEM_ID, null));
        itemService.killSyncItemIds(getAllSynItemId(base.getSimpleBase(), TEST_FACTORY_ITEM_ID, null));
        itemService.killSyncItemIds(getAllSynItemId(base.getSimpleBase(), TEST_START_BUILDER_ITEM_ID, null));
        for (int i = 0; i < 5; i++) {
            Assert.assertFalse(botItemContainer.isFulfilled(userState));
            botItemContainer.buildup(base.getSimpleBase(), userState);
            waitForActionServiceDone();
        }
        Assert.assertTrue(botItemContainer.isFulfilled(userState));
        Assert.assertEquals(1, getAllSynItemId(base.getSimpleBase(), TEST_START_BUILDER_ITEM_ID, null).size());
        Assert.assertEquals(1, getAllSynItemId(base.getSimpleBase(), TEST_FACTORY_ITEM_ID, null).size());
        Assert.assertEquals(3, getAllSynItemId(base.getSimpleBase(), TEST_ATTACK_ITEM_ID, null).size());
        Assert.assertEquals(2, getAllSynItemId(base.getSimpleBase(), TEST_SIMPLE_BUILDING_ID, null).size());

        botItemContainer.killAllItems();
        assertWholeItemCount(0);
    }

    @Test
    @DirtiesContext
    public void multipleFactoriesAndOverdrive() throws Exception {
        configureMinimalGame();

        Collection<DbBotItemConfig> dbBotItemConfigs = new ArrayList<DbBotItemConfig>();
        DbBotItemConfig config1 = new DbBotItemConfig();
        config1.setCount(1);
        config1.setBaseItemType(itemService.getDbBaseItemType(TEST_START_BUILDER_ITEM_ID));
        config1.setRegion(new Rectangle(2000, 2000, 1000, 1000));
        config1.setCreateDirectly(true);
        dbBotItemConfigs.add(config1);
        DbBotItemConfig config2 = new DbBotItemConfig();
        config2.setCount(6);
        config2.setBaseItemType(itemService.getDbBaseItemType(TEST_FACTORY_ITEM_ID));
        config2.setRegion(new Rectangle(2000, 2000, 1000, 1000));
        dbBotItemConfigs.add(config2);
        DbBotItemConfig config3 = new DbBotItemConfig();
        config3.setCount(3);
        config3.setBaseItemType(itemService.getDbBaseItemType(TEST_ATTACK_ITEM_ID));
        dbBotItemConfigs.add(config3);

        BotItemContainer botItemContainer = (BotItemContainer) applicationContext.getBean("botItemContainer");
        botItemContainer.init(dbBotItemConfigs);

        UserState userState = userService.getUserState(new DbBotConfig());
        Base base = baseService.createBotBase(userState, "Test Bot");
        Assert.assertFalse(botItemContainer.isFulfilled(userState));

        for (int i = 0; i < 50; i++) {
            botItemContainer.buildup(base.getSimpleBase(), userState);
            waitForActionServiceDone();
        }
        Assert.assertTrue(botItemContainer.isFulfilled(userState));
        Assert.assertEquals(1, getAllSynItemId(base.getSimpleBase(), TEST_START_BUILDER_ITEM_ID, null).size());
        Assert.assertEquals(6, getAllSynItemId(base.getSimpleBase(), TEST_FACTORY_ITEM_ID, null).size());
        Assert.assertEquals(3, getAllSynItemId(base.getSimpleBase(), TEST_ATTACK_ITEM_ID, null).size());

        itemService.killSyncItemIds(getAllSynItemId(base.getSimpleBase(), TEST_ATTACK_ITEM_ID, null));
        Assert.assertFalse(botItemContainer.isFulfilled(userState));
        for (int i = 0; i < 50; i++) {
            botItemContainer.buildup(base.getSimpleBase(), userState);
            waitForActionServiceDone();
        }
        Assert.assertTrue(botItemContainer.isFulfilled(userState));
        Assert.assertEquals(1, getAllSynItemId(base.getSimpleBase(), TEST_START_BUILDER_ITEM_ID, null).size());
        Assert.assertEquals(6, getAllSynItemId(base.getSimpleBase(), TEST_FACTORY_ITEM_ID, null).size());
        Assert.assertEquals(3, getAllSynItemId(base.getSimpleBase(), TEST_ATTACK_ITEM_ID, null).size());

        botItemContainer.killAllItems();
        assertWholeItemCount(0);        
    }

}
