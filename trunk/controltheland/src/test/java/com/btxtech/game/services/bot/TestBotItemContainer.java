package com.btxtech.game.services.bot;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotConfig;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotItemConfig;
import com.btxtech.game.jsre.common.gameengine.services.bot.impl.BotItemContainer;
import com.btxtech.game.jsre.common.gameengine.services.bot.impl.BotSyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.TestServices;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.collision.CollisionService;
import com.btxtech.game.services.common.ServerServices;
import com.btxtech.game.services.item.ItemService;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
    private ServerServices serverServices;
    @Autowired
    private ItemService itemService;
    @Autowired
    private BaseService baseService;

    public static Collection<BotItemConfig> convert(Collection<DbBotItemConfig> dbBotItemConfigs, ItemService itemService) {
        Collection<BotItemConfig> botItemConfigs = new ArrayList<BotItemConfig>();
        for (DbBotItemConfig dbBotItemConfig : dbBotItemConfigs) {
            botItemConfigs.add(dbBotItemConfig.createBotItemConfig(itemService));
        }
        return botItemConfigs;
    }

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

        Collection<BotItemConfig> botItemConfigs = convert(dbBotItemConfigs, itemService);
        BotItemContainer botItemContainer = new BotItemContainer(botItemConfigs, serverServices);
        SimpleBase simpleBase = baseService.createBotBase(new BotConfig(0, 0, botItemConfigs, null, "Test Bot", null, null, null, null));

        Assert.assertFalse(botItemContainer.isFulfilled(simpleBase));

        botItemContainer.buildup(simpleBase);
        waitForBotItemContainer(botItemContainer, simpleBase);

        Assert.assertEquals(1, getAllSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID, null).size());

        botItemContainer.killAllItems();
        assertWholeItemCount(0);
    }

    private void waitForBotItemContainer(BotItemContainer botItemContainer, SimpleBase simpleBase) throws InterruptedException, TimeoutException {
        long maxTime = System.currentTimeMillis() + 10000;
        while (!botItemContainer.isFulfilled(simpleBase)) {
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
        config1.setRegion(new Rectangle(2000, 2000, 2000, 2000));
        config1.setCreateDirectly(true);
        dbBotItemConfigs.add(config1);
        DbBotItemConfig config2 = new DbBotItemConfig();
        config2.setCount(2);
        config2.setBaseItemType(itemService.getDbBaseItemType(TEST_FACTORY_ITEM_ID));
        config2.setRegion(new Rectangle(2000, 2000, 2000, 2000));
        dbBotItemConfigs.add(config2);

        Collection<BotItemConfig> botItemConfigs = convert(dbBotItemConfigs, itemService);
        BotItemContainer botItemContainer = new BotItemContainer(botItemConfigs, serverServices);
        SimpleBase simpleBase = baseService.createBotBase(new BotConfig(0, 0, botItemConfigs, null, "Test Bot", null, null, null, null));

        Assert.assertFalse(botItemContainer.isFulfilled(simpleBase));

        botItemContainer.buildup(simpleBase);
        Assert.assertEquals(1, getAllSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID, null).size());
        Assert.assertFalse(botItemContainer.isFulfilled(simpleBase));

        botItemContainer.buildup(simpleBase);
        waitForActionServiceDone();
        Assert.assertEquals(1, getAllSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID, null).size());
        Assert.assertEquals(1, getAllSynItemId(simpleBase, TEST_FACTORY_ITEM_ID, null).size());
        Assert.assertFalse(botItemContainer.isFulfilled(simpleBase));

        botItemContainer.buildup(simpleBase);
        waitForActionServiceDone();
        Assert.assertTrue(botItemContainer.isFulfilled(simpleBase));
        Assert.assertEquals(1, getAllSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID, null).size());
        Assert.assertEquals(2, getAllSynItemId(simpleBase, TEST_FACTORY_ITEM_ID, null).size());

        // Does nothing
        botItemContainer.buildup(simpleBase);
        waitForActionServiceDone();
        Assert.assertTrue(botItemContainer.isFulfilled(simpleBase));
        Assert.assertEquals(1, getAllSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID, null).size());
        Assert.assertEquals(2, getAllSynItemId(simpleBase, TEST_FACTORY_ITEM_ID, null).size());

        Id id = getAllSynItemId(simpleBase, TEST_FACTORY_ITEM_ID, null).get(0);
        killSyncItem(id);
        Assert.assertFalse(botItemContainer.isFulfilled(simpleBase));

        botItemContainer.buildup(simpleBase);
        waitForActionServiceDone();
        Assert.assertTrue(botItemContainer.isFulfilled(simpleBase));
        Assert.assertEquals(1, getAllSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID, null).size());
        Assert.assertEquals(2, getAllSynItemId(simpleBase, TEST_FACTORY_ITEM_ID, null).size());

        id = getAllSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID, null).get(0);
        killSyncItem(id);
        id = getAllSynItemId(simpleBase, TEST_FACTORY_ITEM_ID, null).get(0);
        killSyncItem(id);
        Assert.assertFalse(botItemContainer.isFulfilled(simpleBase));

        botItemContainer.buildup(simpleBase);
        waitForActionServiceDone();
        Assert.assertFalse(botItemContainer.isFulfilled(simpleBase));
        Assert.assertEquals(1, getAllSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID, null).size());
        Assert.assertEquals(1, getAllSynItemId(simpleBase, TEST_FACTORY_ITEM_ID, null).size());

        botItemContainer.buildup(simpleBase);
        waitForActionServiceDone();
        Assert.assertTrue(botItemContainer.isFulfilled(simpleBase));
        Assert.assertEquals(1, getAllSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID, null).size());
        Assert.assertEquals(2, getAllSynItemId(simpleBase, TEST_FACTORY_ITEM_ID, null).size());

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
        config1.setRegion(new Rectangle(2000, 2000, 2000, 2000));
        config1.setCreateDirectly(true);
        dbBotItemConfigs.add(config1);
        DbBotItemConfig config2 = new DbBotItemConfig();
        config2.setCount(1);
        config2.setBaseItemType(itemService.getDbBaseItemType(TEST_FACTORY_ITEM_ID));
        config2.setRegion(new Rectangle(4000, 2000, 2000, 2000));
        dbBotItemConfigs.add(config2);
        DbBotItemConfig config3 = new DbBotItemConfig();
        config3.setCount(3);
        config3.setBaseItemType(itemService.getDbBaseItemType(TEST_ATTACK_ITEM_ID));
        dbBotItemConfigs.add(config3);
        DbBotItemConfig config4 = new DbBotItemConfig();
        config4.setCount(2);
        config4.setBaseItemType(itemService.getDbBaseItemType(TEST_SIMPLE_BUILDING_ID));
        config4.setRegion(new Rectangle(4000, 4000, 2000, 2000));
        config4.setCreateDirectly(true);
        dbBotItemConfigs.add(config4);

        Collection<BotItemConfig> botItemConfigs = convert(dbBotItemConfigs, itemService);
        BotItemContainer botItemContainer = new BotItemContainer(botItemConfigs, serverServices);
        SimpleBase simpleBase = baseService.createBotBase(new BotConfig(0, 0, botItemConfigs, null, "Test Bot", null, null, null, null));

        Assert.assertFalse(botItemContainer.isFulfilled(simpleBase));

        for (int i = 0; i < 5; i++) {
            Assert.assertFalse(botItemContainer.isFulfilled(simpleBase));
            botItemContainer.buildup(simpleBase);
            waitForActionServiceDone();
        }
        Assert.assertTrue(botItemContainer.isFulfilled(simpleBase));
        Assert.assertEquals(1, getAllSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID, null).size());
        Assert.assertEquals(1, getAllSynItemId(simpleBase, TEST_FACTORY_ITEM_ID, null).size());
        Assert.assertEquals(3, getAllSynItemId(simpleBase, TEST_ATTACK_ITEM_ID, null).size());
        Assert.assertEquals(2, getAllSynItemId(simpleBase, TEST_SIMPLE_BUILDING_ID, null).size());

        itemService.killSyncItemIds(getAllSynItemId(simpleBase, TEST_ATTACK_ITEM_ID, null));
        itemService.killSyncItemIds(getAllSynItemId(simpleBase, TEST_FACTORY_ITEM_ID, null));
        itemService.killSyncItemIds(getAllSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID, null));
        for (int i = 0; i < 5; i++) {
            Assert.assertFalse(botItemContainer.isFulfilled(simpleBase));
            botItemContainer.buildup(simpleBase);
            waitForActionServiceDone();
        }
        Assert.assertTrue(botItemContainer.isFulfilled(simpleBase));
        Assert.assertEquals(1, getAllSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID, null).size());
        Assert.assertEquals(1, getAllSynItemId(simpleBase, TEST_FACTORY_ITEM_ID, null).size());
        Assert.assertEquals(3, getAllSynItemId(simpleBase, TEST_ATTACK_ITEM_ID, null).size());
        Assert.assertEquals(2, getAllSynItemId(simpleBase, TEST_SIMPLE_BUILDING_ID, null).size());

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

        Collection<BotItemConfig> botItemConfigs = convert(dbBotItemConfigs, itemService);
        BotItemContainer botItemContainer = new BotItemContainer(botItemConfigs, serverServices);
        SimpleBase simpleBase = baseService.createBotBase(new BotConfig(0, 0, botItemConfigs, null, "Test Bot", null, null, null, null));

        Assert.assertFalse(botItemContainer.isFulfilled(simpleBase));

        for (int i = 0; i < 50; i++) {
            botItemContainer.buildup(simpleBase);
            waitForActionServiceDone();
        }
        Assert.assertTrue(botItemContainer.isFulfilled(simpleBase));
        Assert.assertEquals(1, getAllSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID, null).size());
        Assert.assertEquals(6, getAllSynItemId(simpleBase, TEST_FACTORY_ITEM_ID, null).size());
        Assert.assertEquals(3, getAllSynItemId(simpleBase, TEST_ATTACK_ITEM_ID, null).size());

        itemService.killSyncItemIds(getAllSynItemId(simpleBase, TEST_ATTACK_ITEM_ID, null));
        Assert.assertFalse(botItemContainer.isFulfilled(simpleBase));
        for (int i = 0; i < 50; i++) {
            botItemContainer.buildup(simpleBase);
            waitForActionServiceDone();
        }
        Assert.assertTrue(botItemContainer.isFulfilled(simpleBase));
        Assert.assertEquals(1, getAllSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID, null).size());
        Assert.assertEquals(6, getAllSynItemId(simpleBase, TEST_FACTORY_ITEM_ID, null).size());
        Assert.assertEquals(3, getAllSynItemId(simpleBase, TEST_ATTACK_ITEM_ID, null).size());

        botItemContainer.killAllItems();
        assertWholeItemCount(0);
    }

    @Test
    @DirtiesContext
    public void buildupWrongConfig() throws Exception {
        configureMinimalGame();

        Collection<DbBotItemConfig> dbBotItemConfigs = new ArrayList<DbBotItemConfig>();
        DbBotItemConfig config1 = new DbBotItemConfig();
        config1.setCount(1);
        config1.setBaseItemType(itemService.getDbBaseItemType(TEST_START_BUILDER_ITEM_ID));
        config1.setRegion(new Rectangle(2000, 2000, 1000, 1000));
        config1.setCreateDirectly(true);
        dbBotItemConfigs.add(config1);
        DbBotItemConfig config2 = new DbBotItemConfig();
        config2.setCount(3);
        config2.setBaseItemType(itemService.getDbBaseItemType(TEST_ATTACK_ITEM_ID));
        dbBotItemConfigs.add(config2);

        Collection<BotItemConfig> botItemConfigs = convert(dbBotItemConfigs, itemService);
        BotItemContainer botItemContainer = new BotItemContainer(botItemConfigs, serverServices);
        SimpleBase simpleBase = baseService.createBotBase(new BotConfig(0, 0, botItemConfigs, null, "Test Bot", null, null, null, null));

        Assert.assertFalse(botItemContainer.isFulfilled(simpleBase));

        for (int i = 0; i < 50; i++) {
            botItemContainer.buildup(simpleBase);
            waitForActionServiceDone();
        }

        Assert.assertFalse(botItemContainer.isFulfilled(simpleBase));
    }

    @Test
    @DirtiesContext
    public void getFirstIdleAttacker() throws Exception {
        configureMinimalGame();

        SimpleBase simpleBase = baseService.createBotBase(new BotConfig(0, 0, null, null, "Test Bot", null, null, null, null));

        SyncBaseItem defender1 = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(1000, 1000), new Id(1, Id.NO_ID, 0));
        SyncBaseItem defender2 = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(1000, 1200), new Id(2, Id.NO_ID, 0));
        SyncBaseItem defender3 = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(1000, 1400), new Id(3, Id.NO_ID, 0));

        SyncBaseItem enemy1 = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(1500, 1000), new Id(4, Id.NO_ID, 0));

        BaseService baseService = EasyMock.createStrictMock(BaseService.class);
        EasyMock.expect(baseService.getItems(simpleBase)).andReturn(null).times(2);
        Collection<SyncBaseItem> botItems = new ArrayList<SyncBaseItem>();
        botItems.add(defender1);
        botItems.add(defender2);
        botItems.add(defender3);
        EasyMock.expect(baseService.getItems(simpleBase)).andReturn(botItems);

        CollisionService mockCollisionService = EasyMock.createNiceMock(CollisionService.class);

        ItemService mockItemService = EasyMock.createStrictMock(ItemService.class);
        EasyMock.expect(mockItemService.createSyncObject(itemService.getItemType(TEST_ATTACK_ITEM_ID), null, null, simpleBase, 0)).andReturn(defender1);
        EasyMock.expect(mockItemService.createSyncObject(itemService.getItemType(TEST_ATTACK_ITEM_ID), null, null, simpleBase, 0)).andReturn(defender2);
        EasyMock.expect(mockItemService.createSyncObject(itemService.getItemType(TEST_ATTACK_ITEM_ID), null, null, simpleBase, 0)).andReturn(defender3);

        Collection<DbBotItemConfig> dbBotItemConfigs = new ArrayList<DbBotItemConfig>();
        DbBotItemConfig attackerConfig = new DbBotItemConfig();
        attackerConfig.setCount(3);
        attackerConfig.setCreateDirectly(true);
        attackerConfig.setBaseItemType(itemService.getDbBaseItemType(TEST_ATTACK_ITEM_ID));
        dbBotItemConfigs.add(attackerConfig);

        Collection<BotItemConfig> botItemConfigs = convert(dbBotItemConfigs, itemService);
        TestServices testServices = new TestServices();
        testServices.setItemService(mockItemService);
        testServices.setCollisionService(mockCollisionService);
        testServices.setBaseService(baseService);
        BotItemContainer botItemContainer = new BotItemContainer(botItemConfigs, testServices);


        EasyMock.replay(baseService);
        EasyMock.replay(mockCollisionService);
        EasyMock.replay(mockItemService);

        Assert.assertFalse(botItemContainer.isFulfilled(simpleBase));
        botItemContainer.buildup(simpleBase);
        Assert.assertTrue(botItemContainer.isFulfilled(simpleBase));

        BotSyncBaseItem botItem1 = botItemContainer.getFirstIdleAttacker(enemy1);
        Assert.assertEquals(defender1, botItem1.getSyncBaseItem());
        botItem1 = botItemContainer.getFirstIdleAttacker(enemy1);
        Assert.assertEquals(defender1, botItem1.getSyncBaseItem());
        setPrivateField(BotSyncBaseItem.class, botItem1, "idle", false);

        BotSyncBaseItem botItem2 = botItemContainer.getFirstIdleAttacker(enemy1);
        Assert.assertEquals(defender2, botItem2.getSyncBaseItem());
        setPrivateField(BotSyncBaseItem.class, botItem2, "idle", false);

        BotSyncBaseItem botItem3 = botItemContainer.getFirstIdleAttacker(enemy1);
        Assert.assertEquals(defender3, botItem3.getSyncBaseItem());
        setPrivateField(BotSyncBaseItem.class, botItem3, "idle", false);

        Assert.assertNull(botItemContainer.getFirstIdleAttacker(enemy1));

        EasyMock.verify(baseService);
        EasyMock.verify(mockItemService);
    }

}
