package com.btxtech.game.services.bot;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.CommonJava;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotConfig;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotItemConfig;
import com.btxtech.game.jsre.common.gameengine.services.bot.impl.BotItemContainer;
import com.btxtech.game.jsre.common.gameengine.services.bot.impl.BotSyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.TestServices;
import com.btxtech.game.services.action.ActionService;
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
import java.util.List;
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

    public static List<BotItemConfig> convert(Collection<DbBotItemConfig> dbBotItemConfigs, ItemService itemService) {
        List<BotItemConfig> botItemConfigs = new ArrayList<BotItemConfig>();
        for (DbBotItemConfig dbBotItemConfig : dbBotItemConfigs) {
            botItemConfigs.add(dbBotItemConfig.createBotItemConfig(itemService));
        }
        return botItemConfigs;
    }

    @Test
    @DirtiesContext
    public void oneItem() throws Exception {
        configureRealGame();

        Collection<DbBotItemConfig> dbBotItemConfigs = new ArrayList<DbBotItemConfig>();
        DbBotItemConfig config1 = new DbBotItemConfig();
        config1.setCount(1);
        config1.setBaseItemType(getDbBaseItemTypeInSession(TEST_START_BUILDER_ITEM_ID));
        config1.setRegion(new Rectangle(2000, 2000, 1000, 1000));
        config1.setCreateDirectly(true);
        dbBotItemConfigs.add(config1);

        Collection<BotItemConfig> botItemConfigs = convert(dbBotItemConfigs, itemService);
        BotItemContainer botItemContainer = new BotItemContainer(botItemConfigs, null, serverServices, "Test Bot");
        SimpleBase simpleBase = baseService.createBotBase(new BotConfig(0, 0, botItemConfigs, null, "Test Bot", null, null, null, null));

        Assert.assertFalse(botItemContainer.isFulfilledUseInTestOnly(simpleBase));

        botItemContainer.work(simpleBase);
        waitForBotItemContainer(botItemContainer, simpleBase);

        Assert.assertEquals(1, getAllSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID, null).size());

        botItemContainer.killAllItems(simpleBase);
        assertWholeItemCount(0);
    }

    private void waitForBotItemContainer(BotItemContainer botItemContainer, SimpleBase simpleBase) throws InterruptedException, TimeoutException {
        long maxTime = System.currentTimeMillis() + 10000;
        while (!botItemContainer.isFulfilledUseInTestOnly(simpleBase)) {
            if (System.currentTimeMillis() > maxTime) {
                throw new TimeoutException();
            }
            Thread.sleep(100);
        }
    }

    @Test
    @DirtiesContext
    public void threeItems() throws Exception {
        configureRealGame();

        Collection<DbBotItemConfig> dbBotItemConfigs = new ArrayList<DbBotItemConfig>();
        DbBotItemConfig config1 = new DbBotItemConfig();
        config1.setCount(1);
        config1.setBaseItemType(getDbBaseItemTypeInSession(TEST_START_BUILDER_ITEM_ID));
        config1.setRegion(new Rectangle(2000, 2000, 2000, 2000));
        config1.setCreateDirectly(true);
        dbBotItemConfigs.add(config1);
        DbBotItemConfig config2 = new DbBotItemConfig();
        config2.setCount(2);
        config2.setBaseItemType(getDbBaseItemTypeInSession(TEST_FACTORY_ITEM_ID));
        config2.setRegion(new Rectangle(2000, 2000, 2000, 2000));
        dbBotItemConfigs.add(config2);

        Collection<BotItemConfig> botItemConfigs = convert(dbBotItemConfigs, itemService);
        BotItemContainer botItemContainer = new BotItemContainer(botItemConfigs, null, serverServices, "Test Bot");
        SimpleBase simpleBase = baseService.createBotBase(new BotConfig(0, 0, botItemConfigs, null, "Test Bot", null, null, null, null));

        Assert.assertFalse(botItemContainer.isFulfilledUseInTestOnly(simpleBase));

        botItemContainer.work(simpleBase);
        Assert.assertEquals(1, getAllSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID, null).size());
        Assert.assertFalse(botItemContainer.isFulfilledUseInTestOnly(simpleBase));

        botItemContainer.work(simpleBase);
        waitForActionServiceDone();
        Assert.assertEquals(1, getAllSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID, null).size());
        Assert.assertEquals(1, getAllSynItemId(simpleBase, TEST_FACTORY_ITEM_ID, null).size());
        Assert.assertFalse(botItemContainer.isFulfilledUseInTestOnly(simpleBase));

        botItemContainer.work(simpleBase);
        waitForActionServiceDone();
        Assert.assertTrue(botItemContainer.isFulfilledUseInTestOnly(simpleBase));
        Assert.assertEquals(1, getAllSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID, null).size());
        Assert.assertEquals(2, getAllSynItemId(simpleBase, TEST_FACTORY_ITEM_ID, null).size());

        // Does nothing
        botItemContainer.work(simpleBase);
        waitForActionServiceDone();
        Assert.assertTrue(botItemContainer.isFulfilledUseInTestOnly(simpleBase));
        Assert.assertEquals(1, getAllSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID, null).size());
        Assert.assertEquals(2, getAllSynItemId(simpleBase, TEST_FACTORY_ITEM_ID, null).size());

        Id id = getAllSynItemId(simpleBase, TEST_FACTORY_ITEM_ID, null).get(0);
        killSyncItem(id);
        Assert.assertFalse(botItemContainer.isFulfilledUseInTestOnly(simpleBase));

        botItemContainer.work(simpleBase);
        waitForActionServiceDone();
        Assert.assertTrue(botItemContainer.isFulfilledUseInTestOnly(simpleBase));
        Assert.assertEquals(1, getAllSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID, null).size());
        Assert.assertEquals(2, getAllSynItemId(simpleBase, TEST_FACTORY_ITEM_ID, null).size());

        id = getAllSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID, null).get(0);
        killSyncItem(id);
        id = getAllSynItemId(simpleBase, TEST_FACTORY_ITEM_ID, null).get(0);
        killSyncItem(id);
        Assert.assertFalse(botItemContainer.isFulfilledUseInTestOnly(simpleBase));

        botItemContainer.work(simpleBase);
        waitForActionServiceDone();
        Assert.assertFalse(botItemContainer.isFulfilledUseInTestOnly(simpleBase));
        Assert.assertEquals(1, getAllSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID, null).size());
        Assert.assertEquals(1, getAllSynItemId(simpleBase, TEST_FACTORY_ITEM_ID, null).size());

        botItemContainer.work(simpleBase);
        waitForActionServiceDone();
        Assert.assertTrue(botItemContainer.isFulfilledUseInTestOnly(simpleBase));
        Assert.assertEquals(1, getAllSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID, null).size());
        Assert.assertEquals(2, getAllSynItemId(simpleBase, TEST_FACTORY_ITEM_ID, null).size());

        botItemContainer.killAllItems(simpleBase);
        assertWholeItemCount(0);
    }

    @Test
    @DirtiesContext
    public void complexItems() throws Exception {
        configureRealGame();

        Collection<DbBotItemConfig> dbBotItemConfigs = new ArrayList<DbBotItemConfig>();
        DbBotItemConfig config1 = new DbBotItemConfig();
        config1.setCount(1);
        config1.setBaseItemType(getDbBaseItemTypeInSession(TEST_START_BUILDER_ITEM_ID));
        config1.setRegion(new Rectangle(2000, 2000, 2000, 2000));
        config1.setCreateDirectly(true);
        dbBotItemConfigs.add(config1);
        DbBotItemConfig config2 = new DbBotItemConfig();
        config2.setCount(1);
        config2.setBaseItemType(getDbBaseItemTypeInSession(TEST_FACTORY_ITEM_ID));
        config2.setRegion(new Rectangle(4000, 2000, 2000, 2000));
        dbBotItemConfigs.add(config2);
        DbBotItemConfig config3 = new DbBotItemConfig();
        config3.setCount(3);
        config3.setBaseItemType(getDbBaseItemTypeInSession(TEST_ATTACK_ITEM_ID));
        dbBotItemConfigs.add(config3);
        DbBotItemConfig config4 = new DbBotItemConfig();
        config4.setCount(2);
        config4.setBaseItemType(getDbBaseItemTypeInSession(TEST_SIMPLE_BUILDING_ID));
        config4.setRegion(new Rectangle(4000, 4000, 2000, 2000));
        config4.setCreateDirectly(true);
        dbBotItemConfigs.add(config4);

        Collection<BotItemConfig> botItemConfigs = convert(dbBotItemConfigs, itemService);
        BotItemContainer botItemContainer = new BotItemContainer(botItemConfigs, null, serverServices, "Test Bot");
        SimpleBase simpleBase = baseService.createBotBase(new BotConfig(0, 0, botItemConfigs, null, "Test Bot", null, null, null, null));

        Assert.assertFalse(botItemContainer.isFulfilledUseInTestOnly(simpleBase));

        for (int i = 0; i < 5; i++) {
            // Mock Services instead of this for loop
            botItemContainer.work(simpleBase);
            waitForActionServiceDone();
        }
        Assert.assertTrue(botItemContainer.isFulfilledUseInTestOnly(simpleBase));
        Assert.assertEquals(1, getAllSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID, null).size());
        Assert.assertEquals(1, getAllSynItemId(simpleBase, TEST_FACTORY_ITEM_ID, null).size());
        Assert.assertEquals(3, getAllSynItemId(simpleBase, TEST_ATTACK_ITEM_ID, null).size());
        Assert.assertEquals(2, getAllSynItemId(simpleBase, TEST_SIMPLE_BUILDING_ID, null).size());

        itemService.killSyncItemIds(getAllSynItemId(simpleBase, TEST_ATTACK_ITEM_ID, null));
        itemService.killSyncItemIds(getAllSynItemId(simpleBase, TEST_FACTORY_ITEM_ID, null));
        itemService.killSyncItemIds(getAllSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID, null));
        for (int i = 0; i < 5; i++) {
            // Mock Services instead of this for loop
            botItemContainer.work(simpleBase);
            waitForActionServiceDone();
        }
        Assert.assertTrue(botItemContainer.isFulfilledUseInTestOnly(simpleBase));
        Assert.assertEquals(1, getAllSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID, null).size());
        Assert.assertEquals(1, getAllSynItemId(simpleBase, TEST_FACTORY_ITEM_ID, null).size());
        Assert.assertEquals(3, getAllSynItemId(simpleBase, TEST_ATTACK_ITEM_ID, null).size());
        Assert.assertEquals(2, getAllSynItemId(simpleBase, TEST_SIMPLE_BUILDING_ID, null).size());

        botItemContainer.killAllItems(simpleBase);
        assertWholeItemCount(0);
    }

    @Test
    @DirtiesContext
    public void regionAndMovable() throws Exception {
        configureRealGame();

        Collection<DbBotItemConfig> dbBotItemConfigs = new ArrayList<DbBotItemConfig>();
        DbBotItemConfig config1 = new DbBotItemConfig();
        config1.setCount(1);
        config1.setBaseItemType(getDbBaseItemTypeInSession(TEST_START_BUILDER_ITEM_ID));
        config1.setRegion(new Rectangle(2000, 2000, 2000, 2000));
        config1.setCreateDirectly(true);
        dbBotItemConfigs.add(config1);
        DbBotItemConfig config2 = new DbBotItemConfig();
        config2.setCount(1);
        config2.setBaseItemType(getDbBaseItemTypeInSession(TEST_FACTORY_ITEM_ID));
        config2.setRegion(new Rectangle(4000, 2000, 2000, 2000));
        dbBotItemConfigs.add(config2);
        DbBotItemConfig config3 = new DbBotItemConfig();
        config3.setCount(3);
        config3.setRegion(new Rectangle(8000, 8000, 2000, 2000));
        config3.setBaseItemType(getDbBaseItemTypeInSession(TEST_ATTACK_ITEM_ID));
        dbBotItemConfigs.add(config3);

        Collection<BotItemConfig> botItemConfigs = convert(dbBotItemConfigs, itemService);
        BotItemContainer botItemContainer = new BotItemContainer(botItemConfigs, null, serverServices, "Test Bot");
        SimpleBase simpleBase = baseService.createBotBase(new BotConfig(0, 0, botItemConfigs, null, "Test Bot", null, null, null, null));

        Assert.assertFalse(botItemContainer.isFulfilledUseInTestOnly(simpleBase));

        for (int i = 0; i < 5; i++) {
            // Mock Services instead of this for loop
            botItemContainer.work(simpleBase);
            waitForActionServiceDone();
        }
        Assert.assertTrue(botItemContainer.isFulfilledUseInTestOnly(simpleBase));
        Assert.assertEquals(1, getAllSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID, null).size());
        Assert.assertEquals(1, getAllSynItemId(simpleBase, TEST_FACTORY_ITEM_ID, null).size());
        Assert.assertEquals(3, getAllSynItemId(simpleBase, TEST_ATTACK_ITEM_ID, null).size());

        botItemContainer.killAllItems(simpleBase);
        assertWholeItemCount(0);
    }

    @Test
    @DirtiesContext
    public void multipleFactoriesAndOverdrive() throws Exception {
        configureRealGame();

        Collection<DbBotItemConfig> dbBotItemConfigs = new ArrayList<DbBotItemConfig>();
        DbBotItemConfig config1 = new DbBotItemConfig();
        config1.setCount(1);
        config1.setBaseItemType(getDbBaseItemTypeInSession(TEST_START_BUILDER_ITEM_ID));
        config1.setRegion(new Rectangle(2000, 2000, 1000, 1000));
        config1.setCreateDirectly(true);
        dbBotItemConfigs.add(config1);
        DbBotItemConfig config2 = new DbBotItemConfig();
        config2.setCount(6);
        config2.setBaseItemType(getDbBaseItemTypeInSession(TEST_FACTORY_ITEM_ID));
        config2.setRegion(new Rectangle(2000, 2000, 1000, 1000));
        dbBotItemConfigs.add(config2);
        DbBotItemConfig config3 = new DbBotItemConfig();
        config3.setCount(3);
        config3.setBaseItemType(getDbBaseItemTypeInSession(TEST_ATTACK_ITEM_ID));
        dbBotItemConfigs.add(config3);

        Collection<BotItemConfig> botItemConfigs = convert(dbBotItemConfigs, itemService);
        BotItemContainer botItemContainer = new BotItemContainer(botItemConfigs, null, serverServices, "Test Bot");
        SimpleBase simpleBase = baseService.createBotBase(new BotConfig(0, 0, botItemConfigs, null, "Test Bot", null, null, null, null));

        Assert.assertFalse(botItemContainer.isFulfilledUseInTestOnly(simpleBase));

        for (int i = 0; i < 50; i++) {
            // Mock Services instead of this for loop
            botItemContainer.work(simpleBase);
            waitForActionServiceDone();
        }
        Assert.assertTrue(botItemContainer.isFulfilledUseInTestOnly(simpleBase));
        Assert.assertEquals(1, getAllSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID, null).size());
        Assert.assertEquals(6, getAllSynItemId(simpleBase, TEST_FACTORY_ITEM_ID, null).size());
        Assert.assertEquals(3, getAllSynItemId(simpleBase, TEST_ATTACK_ITEM_ID, null).size());

        itemService.killSyncItemIds(getAllSynItemId(simpleBase, TEST_ATTACK_ITEM_ID, null));
        Assert.assertFalse(botItemContainer.isFulfilledUseInTestOnly(simpleBase));
        for (int i = 0; i < 50; i++) {
             // Mock Services instead of this for loop
            botItemContainer.work(simpleBase);
            waitForActionServiceDone();
        }
        Assert.assertTrue(botItemContainer.isFulfilledUseInTestOnly(simpleBase));
        Assert.assertEquals(1, getAllSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID, null).size());
        Assert.assertEquals(6, getAllSynItemId(simpleBase, TEST_FACTORY_ITEM_ID, null).size());
        Assert.assertEquals(3, getAllSynItemId(simpleBase, TEST_ATTACK_ITEM_ID, null).size());

        botItemContainer.killAllItems(simpleBase);
        assertWholeItemCount(0);
    }

    @Test
    @DirtiesContext
    public void buildupWrongConfig() throws Exception {
        configureRealGame();

        Collection<DbBotItemConfig> dbBotItemConfigs = new ArrayList<DbBotItemConfig>();
        DbBotItemConfig config1 = new DbBotItemConfig();
        config1.setCount(1);
        config1.setBaseItemType(getDbBaseItemTypeInSession(TEST_START_BUILDER_ITEM_ID));
        config1.setRegion(new Rectangle(2000, 2000, 1000, 1000));
        config1.setCreateDirectly(true);
        dbBotItemConfigs.add(config1);
        DbBotItemConfig config2 = new DbBotItemConfig();
        config2.setCount(3);
        config2.setBaseItemType(getDbBaseItemTypeInSession(TEST_ATTACK_ITEM_ID));
        dbBotItemConfigs.add(config2);

        Collection<BotItemConfig> botItemConfigs = convert(dbBotItemConfigs, itemService);
        BotItemContainer botItemContainer = new BotItemContainer(botItemConfigs, null, serverServices, "Test Bot");
        SimpleBase simpleBase = baseService.createBotBase(new BotConfig(0, 0, botItemConfigs, null, "Test Bot", null, null, null, null));

        Assert.assertFalse(botItemContainer.isFulfilledUseInTestOnly(simpleBase));

        for (int i = 0; i < 50; i++) {
            // Mock Services instead of this for loop
            botItemContainer.work(simpleBase);
            waitForActionServiceDone();
        }

        Assert.assertFalse(botItemContainer.isFulfilledUseInTestOnly(simpleBase));
    }

    @Test
    @DirtiesContext
    public void getIdleAttackers() throws Exception {
        configureRealGame();

        SimpleBase simpleBase = baseService.createBotBase(new BotConfig(0, 0, null, null, "Test Bot", null, null, null, null));

        SyncBaseItem defender1 = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(1000, 1000), new Id(1, Id.NO_ID, 0));
        SyncBaseItem defender2 = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(1000, 1200), new Id(2, Id.NO_ID, 0));
        SyncBaseItem defender3 = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(1000, 1400), new Id(3, Id.NO_ID, 0));

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
        attackerConfig.setBaseItemType(getDbBaseItemTypeInSession(TEST_ATTACK_ITEM_ID));
        dbBotItemConfigs.add(attackerConfig);

        Collection<BotItemConfig> botItemConfigs = convert(dbBotItemConfigs, itemService);
        TestServices testServices = new TestServices();
        testServices.setItemService(mockItemService);
        testServices.setCollisionService(mockCollisionService);
        testServices.setBaseService(baseService);
        BotItemContainer botItemContainer = new BotItemContainer(botItemConfigs, null, testServices, "Test Bot");


        EasyMock.replay(baseService);
        EasyMock.replay(mockCollisionService);
        EasyMock.replay(mockItemService);

        Assert.assertFalse(botItemContainer.isFulfilledUseInTestOnly(simpleBase));
        botItemContainer.work(simpleBase);
        Assert.assertTrue(botItemContainer.isFulfilledUseInTestOnly(simpleBase));

        Collection<BotSyncBaseItem> idleAttackers = botItemContainer.getAllIdleAttackers();
        Assert.assertEquals(3, idleAttackers.size());
        BotSyncBaseItem botItem1 = CommonJava.getFirst(idleAttackers);
        setPrivateField(BotSyncBaseItem.class, botItem1, "idle", false);

        idleAttackers = botItemContainer.getAllIdleAttackers();
        Assert.assertEquals(2, idleAttackers.size());
        BotSyncBaseItem botItem2 = CommonJava.getFirst(idleAttackers);
        setPrivateField(BotSyncBaseItem.class, botItem2, "idle", false);

        idleAttackers = botItemContainer.getAllIdleAttackers();
        Assert.assertEquals(1, idleAttackers.size());
        BotSyncBaseItem botItem3 = CommonJava.getFirst(idleAttackers);
        setPrivateField(BotSyncBaseItem.class, botItem3, "idle", false);

        idleAttackers = botItemContainer.getAllIdleAttackers();
        Assert.assertTrue(idleAttackers.isEmpty());

        EasyMock.verify(baseService);
        EasyMock.verify(mockItemService);
    }

    @Test
    @DirtiesContext
    public void moveRealIfIdle() throws Exception {
        configureRealGame();

        SimpleBase simpleBase = baseService.createBotBase(new BotConfig(0, 0, null, null, "Test Bot", null, null, null, null));
        SyncBaseItem syncBaseItem = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(500, 500), new Id(1, Id.NO_ID, 0));

        TestServices testServices = new TestServices();

        BaseService baseServiceMock = EasyMock.createStrictMock(BaseService.class);
        Collection<SyncBaseItem> botItems = new ArrayList<SyncBaseItem>();
        botItems.add(syncBaseItem);
        EasyMock.expect(baseServiceMock.getItems(simpleBase)).andReturn(null);
        EasyMock.expect(baseServiceMock.getItems(simpleBase)).andReturn(botItems).times(2);
        testServices.setBaseService(baseServiceMock);

        ActionService mockActionService = EasyMock.createStrictMock(ActionService.class);
        mockActionService.move(EasyMock.eq(syncBaseItem), EasyMock.eq(new Index(3000, 3500)));
        testServices.setActionService(mockActionService);

        CollisionService mockCollisionService = EasyMock.createStrictMock(CollisionService.class);
        EasyMock.expect(mockCollisionService.getFreeRandomPosition(syncBaseItem.getBaseItemType(), new Rectangle(0, 0, 1000, 1000), 0, false, true)).andReturn(new Index(500, 500));
        EasyMock.expect(mockCollisionService.getFreeRandomPosition(syncBaseItem.getBaseItemType(), new Rectangle(2000, 3000, 1000, 2000), 0, false, false)).andReturn(new Index(3000, 3500));
        testServices.setCollisionService(mockCollisionService);

        BaseItemType baseItemType = (BaseItemType) itemService.getItemType(TEST_ATTACK_ITEM_ID);
        Collection<BotItemConfig> botItemConfigs = new ArrayList<BotItemConfig>();
        botItemConfigs.add(new BotItemConfig(baseItemType, 1, true, new Rectangle(0, 0, 1000, 1000), true, null, false, null));

        ItemService mockItemService = EasyMock.createStrictMock(ItemService.class);
        EasyMock.expect(mockItemService.createSyncObject(itemService.getItemType(TEST_ATTACK_ITEM_ID), new Index(500, 500), null, simpleBase, 0)).andReturn(syncBaseItem);
        testServices.setItemService(mockItemService);

        EasyMock.replay(baseServiceMock);
        EasyMock.replay(mockActionService);
        EasyMock.replay(mockCollisionService);
        EasyMock.replay(mockItemService);
        BotItemContainer botItemContainer = new BotItemContainer(botItemConfigs, new Rectangle(2000, 3000, 1000, 2000), testServices, "Test Bot");
        botItemContainer.work(simpleBase);
        syncBaseItem.setBuildup(0.0); // Simulate busy
        botItemContainer.work(simpleBase);
        syncBaseItem.setBuildup(1.0); // Simulate not busy
        syncBaseItem.getSyncItemArea().setPosition(new Index(3000, 3500));
        botItemContainer.work(simpleBase);
        EasyMock.verify(baseServiceMock);
        EasyMock.verify(mockActionService);
        EasyMock.verify(mockCollisionService);
        EasyMock.verify(mockItemService);
    }

    @Test
    @DirtiesContext
    public void ttlIfIdle() throws Exception {
        configureRealGame();

        SimpleBase simpleBase = baseService.createBotBase(new BotConfig(0, 0, null, null, "Test Bot", null, null, null, null));
        SyncBaseItem syncBaseItem = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(500, 500), new Id(1, Id.NO_ID, 0));

        TestServices testServices = new TestServices();

        BaseService baseServiceMock = EasyMock.createStrictMock(BaseService.class);
        Collection<SyncBaseItem> botItems = new ArrayList<SyncBaseItem>();
        botItems.add(syncBaseItem);
        EasyMock.expect(baseServiceMock.getItems(simpleBase)).andReturn(botItems).times(2);
        testServices.setBaseService(baseServiceMock);

        ItemService mockItemService = EasyMock.createStrictMock(ItemService.class);
        EasyMock.expect(mockItemService.createSyncObject(itemService.getItemType(TEST_ATTACK_ITEM_ID), new Index(300, 350), null, simpleBase, 0)).andReturn(syncBaseItem);
        mockItemService.killSyncItem(EasyMock.eq(syncBaseItem), EasyMock.<SimpleBase>isNull(), EasyMock.eq(true), EasyMock.eq(false));
        testServices.setItemService(mockItemService);

        CollisionService mockCollisionService = EasyMock.createStrictMock(CollisionService.class);
        EasyMock.expect(mockCollisionService.getFreeRandomPosition(syncBaseItem.getBaseItemType(), new Rectangle(0, 0, 1000, 1000), 0, false, true)).andReturn(new Index(300, 350));
        testServices.setCollisionService(mockCollisionService);

        BaseItemType baseItemType = (BaseItemType) itemService.getItemType(TEST_ATTACK_ITEM_ID);
        Collection<BotItemConfig> botItemConfigs = new ArrayList<BotItemConfig>();
        botItemConfigs.add(new BotItemConfig(baseItemType, 1, true, new Rectangle(0, 0, 1000, 1000), false, 50, false, null));

        EasyMock.replay(baseServiceMock, mockItemService, mockCollisionService);
        BotItemContainer botItemContainer = new BotItemContainer(botItemConfigs, new Rectangle(2000, 3000, 1000, 2000), testServices, "Test Bot");
        botItemContainer.work(simpleBase);
        Thread.sleep(60);
        botItemContainer.work(simpleBase);
        EasyMock.verify(mockItemService, baseServiceMock, mockCollisionService);
    }

    @Test
    @DirtiesContext
    public void noRebuild() throws Exception {
        configureRealGame();

        SimpleBase simpleBase = baseService.createBotBase(new BotConfig(0, 0, null, null, "Test Bot", null, null, null, null));
        SyncBaseItem syncBaseItem = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(500, 500), new Id(1, Id.NO_ID, 0));

        TestServices testServices = new TestServices();

        BaseService baseServiceMock = EasyMock.createStrictMock(BaseService.class);
        Collection<SyncBaseItem> botItems = new ArrayList<SyncBaseItem>();
        botItems.add(syncBaseItem);
        EasyMock.expect(baseServiceMock.getItems(simpleBase)).andReturn(null);
        EasyMock.expect(baseServiceMock.getItems(simpleBase)).andReturn(botItems);
        EasyMock.expect(baseServiceMock.getItems(simpleBase)).andReturn(null);
        testServices.setBaseService(baseServiceMock);

        BaseItemType baseItemType = (BaseItemType) itemService.getItemType(TEST_ATTACK_ITEM_ID);
        Collection<BotItemConfig> botItemConfigs = new ArrayList<BotItemConfig>();
        botItemConfigs.add(new BotItemConfig(baseItemType, 1, true, new Rectangle(0, 0, 1000, 1000), false, null, true, null));

        CollisionService mockCollisionService = EasyMock.createStrictMock(CollisionService.class);
        EasyMock.expect(mockCollisionService.getFreeRandomPosition(syncBaseItem.getBaseItemType(), new Rectangle(0, 0, 1000, 1000), 0, false, true)).andReturn(new Index(3000, 3500));
        testServices.setCollisionService(mockCollisionService);

        ItemService mockItemService = EasyMock.createStrictMock(ItemService.class);
        EasyMock.expect(mockItemService.createSyncObject(itemService.getItemType(TEST_ATTACK_ITEM_ID), new Index(3000, 3500), null, simpleBase, 0)).andReturn(syncBaseItem);
        testServices.setItemService(mockItemService);

        EasyMock.replay(baseServiceMock, mockCollisionService, mockItemService);
        BotItemContainer botItemContainer = new BotItemContainer(botItemConfigs, new Rectangle(2000, 3000, 1000, 2000), testServices, "Test Bot");
        botItemContainer.work(simpleBase);
        botItemContainer.work(simpleBase);
        syncBaseItem.setHealth(0);
        botItemContainer.work(simpleBase);
        EasyMock.verify(baseServiceMock, mockCollisionService, mockItemService);
    }

    @Test
    @DirtiesContext
    public void rePop() throws Exception {
        configureRealGame();

        SimpleBase simpleBase = baseService.createBotBase(new BotConfig(0, 0, null, null, "Test Bot", null, null, null, null));
        SyncBaseItem syncBaseItem = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(500, 500), new Id(1, Id.NO_ID, 0));

        TestServices testServices = new TestServices();

        BaseService baseServiceMock = EasyMock.createStrictMock(BaseService.class);
        Collection<SyncBaseItem> botItems = new ArrayList<SyncBaseItem>();
        botItems.add(syncBaseItem);
        EasyMock.expect(baseServiceMock.getItems(simpleBase)).andReturn(null);
        EasyMock.expect(baseServiceMock.getItems(simpleBase)).andReturn(botItems);
        EasyMock.expect(baseServiceMock.getItems(simpleBase)).andReturn(null);
        EasyMock.expect(baseServiceMock.getItems(simpleBase)).andReturn(botItems);
        testServices.setBaseService(baseServiceMock);

        BaseItemType baseItemType = (BaseItemType) itemService.getItemType(TEST_ATTACK_ITEM_ID);
        Collection<BotItemConfig> botItemConfigs = new ArrayList<BotItemConfig>();
        botItemConfigs.add(new BotItemConfig(baseItemType, 1, true, new Rectangle(0, 0, 1000, 1000), false, null, false, 100L));

        CollisionService mockCollisionService = EasyMock.createStrictMock(CollisionService.class);
        EasyMock.expect(mockCollisionService.getFreeRandomPosition(syncBaseItem.getBaseItemType(), new Rectangle(0, 0, 1000, 1000), 0, false, true)).andReturn(new Index(3000, 3500));
        EasyMock.expect(mockCollisionService.getFreeRandomPosition(syncBaseItem.getBaseItemType(), new Rectangle(0, 0, 1000, 1000), 0, false, true)).andReturn(new Index(3000, 3500));
        testServices.setCollisionService(mockCollisionService);

        ItemService mockItemService = EasyMock.createStrictMock(ItemService.class);
        EasyMock.expect(mockItemService.createSyncObject(itemService.getItemType(TEST_ATTACK_ITEM_ID), new Index(3000, 3500), null, simpleBase, 0)).andReturn(syncBaseItem);
        EasyMock.expect(mockItemService.createSyncObject(itemService.getItemType(TEST_ATTACK_ITEM_ID), new Index(3000, 3500), null, simpleBase, 0)).andReturn(syncBaseItem);
        testServices.setItemService(mockItemService);

        EasyMock.replay(baseServiceMock, mockCollisionService, mockItemService);
        BotItemContainer botItemContainer = new BotItemContainer(botItemConfigs, new Rectangle(2000, 3000, 1000, 2000), testServices, "Test Bot");
        botItemContainer.work(simpleBase);
        botItemContainer.work(simpleBase);
        syncBaseItem.setHealth(0);
        botItemContainer.work(simpleBase);
        Thread.sleep(110);
        syncBaseItem.setHealth(1);
        botItemContainer.work(simpleBase);
        EasyMock.verify(baseServiceMock, mockCollisionService, mockItemService);
    }

}
