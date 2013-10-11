package com.btxtech.game.services.bot;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.CommonJava;
import com.btxtech.game.jsre.common.Region;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotConfig;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotItemConfig;
import com.btxtech.game.jsre.common.gameengine.services.bot.impl.BotItemContainer;
import com.btxtech.game.jsre.common.gameengine.services.bot.impl.BotSyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.TestPlanetServices;
import com.btxtech.game.services.common.ServerPlanetServices;
import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.services.planet.ActionService;
import com.btxtech.game.services.planet.BaseService;
import com.btxtech.game.services.planet.CollisionService;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.planet.ServerItemService;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeoutException;

/**
 * User: beat
 * Date: 08.08.2011
 * Time: 21:42:28
 */
public class TestBotItemContainer extends AbstractServiceTest {
    @Autowired
    private ServerItemTypeService serverItemTypeService;
    @Autowired
    private PlanetSystemService planetSystemService;

    @Test
    @DirtiesContext
    public void oneItem() throws Exception {
        configureSimplePlanetNoResources();
        ServerPlanetServices serverPlanetServices = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID);

        Collection<BotItemConfig> botItemConfigs = new ArrayList<>();
        botItemConfigs.add(new BotItemConfig((BaseItemType) serverItemTypeService.getItemType(TEST_START_BUILDER_ITEM_ID), 1, true, createRegion(new Rectangle(2000, 2000, 1000, 1000), 1), false, null, false, null));
        SimpleBase simpleBase = serverPlanetServices.getBaseService().createBotBase(new BotConfig(0, false, 0, null, null, "Test Bot", null, null, null, null));

        BotItemContainer botItemContainer = new BotItemContainer(botItemConfigs, null, serverPlanetServices, "Test Bot");
        Assert.assertFalse(botItemContainer.isFulfilledUseInTestOnly(simpleBase));

        botItemContainer.work(simpleBase);
        waitForBotItemContainer(botItemContainer, simpleBase);
        Assert.assertEquals(1, getAllSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID, null, TEST_PLANET_1_ID).size());

        botItemContainer.killAllItems(simpleBase);
        assertWholeItemCount(TEST_PLANET_1_ID, 0);
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
        configureSimplePlanetNoResources();
        ServerPlanetServices serverPlanetServices = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID);

        Collection<BotItemConfig> botItemConfigs = new ArrayList<>();
        botItemConfigs.add(new BotItemConfig((BaseItemType) serverItemTypeService.getItemType(TEST_START_BUILDER_ITEM_ID), 1, true, createRegion(new Rectangle(2000, 2000, 2000, 2000), 1), false, null, false, null));
        botItemConfigs.add(new BotItemConfig((BaseItemType) serverItemTypeService.getItemType(TEST_FACTORY_ITEM_ID), 2, false, createRegion(new Rectangle(2000, 2000, 2000, 2000), 1), false, null, false, null));
        SimpleBase simpleBase = serverPlanetServices.getBaseService().createBotBase(new BotConfig(0, false, 0, null, null, "Test Bot", null, null, null, null));

        BotItemContainer botItemContainer = new BotItemContainer(botItemConfigs, null, serverPlanetServices, "Test Bot");
        Assert.assertFalse(botItemContainer.isFulfilledUseInTestOnly(simpleBase));

        botItemContainer.work(simpleBase);
        Assert.assertEquals(1, getAllSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID, null, TEST_PLANET_1_ID).size());
        Assert.assertFalse(botItemContainer.isFulfilledUseInTestOnly(simpleBase));

        botItemContainer.work(simpleBase);
        waitForActionServiceDone(TEST_PLANET_1_ID);
        Assert.assertEquals(1, getAllSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID, null, TEST_PLANET_1_ID).size());
        // TODO failed on 11.08.2012
        Assert.assertEquals(1, getAllSynItemId(simpleBase, TEST_FACTORY_ITEM_ID, null, TEST_PLANET_1_ID).size());
        Assert.assertFalse(botItemContainer.isFulfilledUseInTestOnly(simpleBase));

        botItemContainer.work(simpleBase);
        waitForActionServiceDone(TEST_PLANET_1_ID);
        Assert.assertTrue(botItemContainer.isFulfilledUseInTestOnly(simpleBase));
        Assert.assertEquals(1, getAllSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID, null, TEST_PLANET_1_ID).size());
        Assert.assertEquals(2, getAllSynItemId(simpleBase, TEST_FACTORY_ITEM_ID, null, TEST_PLANET_1_ID).size());

        // Does nothing
        botItemContainer.work(simpleBase);
        waitForActionServiceDone(TEST_PLANET_1_ID);
        Assert.assertTrue(botItemContainer.isFulfilledUseInTestOnly(simpleBase));
        Assert.assertEquals(1, getAllSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID, null, TEST_PLANET_1_ID).size());
        Assert.assertEquals(2, getAllSynItemId(simpleBase, TEST_FACTORY_ITEM_ID, null, TEST_PLANET_1_ID).size());

        Id id = getAllSynItemId(simpleBase, TEST_FACTORY_ITEM_ID, null, TEST_PLANET_1_ID).get(0);
        killSyncItem(TEST_PLANET_1_ID, id);
        Assert.assertFalse(botItemContainer.isFulfilledUseInTestOnly(simpleBase));

        botItemContainer.work(simpleBase);
        waitForActionServiceDone(TEST_PLANET_1_ID);
        Assert.assertTrue(botItemContainer.isFulfilledUseInTestOnly(simpleBase));
        Assert.assertEquals(1, getAllSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID, null, TEST_PLANET_1_ID).size());
        Assert.assertEquals(2, getAllSynItemId(simpleBase, TEST_FACTORY_ITEM_ID, null, TEST_PLANET_1_ID).size());

        id = getAllSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID, null, TEST_PLANET_1_ID).get(0);
        killSyncItem(TEST_PLANET_1_ID, id);
        id = getAllSynItemId(simpleBase, TEST_FACTORY_ITEM_ID, null, TEST_PLANET_1_ID).get(0);
        killSyncItem(TEST_PLANET_1_ID, id);
        Assert.assertFalse(botItemContainer.isFulfilledUseInTestOnly(simpleBase));

        botItemContainer.work(simpleBase);
        waitForActionServiceDone(TEST_PLANET_1_ID);
        // Mock Services instead of two times call work
        botItemContainer.work(simpleBase);
        waitForActionServiceDone(TEST_PLANET_1_ID);
        Assert.assertTrue(botItemContainer.isFulfilledUseInTestOnly(simpleBase));
        Assert.assertEquals(1, getAllSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID, null, TEST_PLANET_1_ID).size());
        Assert.assertEquals(2, getAllSynItemId(simpleBase, TEST_FACTORY_ITEM_ID, null, TEST_PLANET_1_ID).size());

        botItemContainer.killAllItems(simpleBase);
        assertWholeItemCount(TEST_PLANET_1_ID, 0);
    }

    @Test
    @DirtiesContext
    public void complexItems() throws Exception {
        configureSimplePlanetNoResources();
        ServerPlanetServices serverPlanetServices = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID);

        Collection<BotItemConfig> botItemConfigs = new ArrayList<>();
        botItemConfigs.add(new BotItemConfig((BaseItemType) serverItemTypeService.getItemType(TEST_START_BUILDER_ITEM_ID), 1, true, createRegion(new Rectangle(2000, 2000, 2000, 2000), 1), false, null, false, null));
        botItemConfigs.add(new BotItemConfig((BaseItemType) serverItemTypeService.getItemType(TEST_FACTORY_ITEM_ID), 1, false, createRegion(new Rectangle(4000, 2000, 2000, 2000), 1), false, null, false, null));
        botItemConfigs.add(new BotItemConfig((BaseItemType) serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID), 3, false, null, false, null, false, null));
        botItemConfigs.add(new BotItemConfig((BaseItemType) serverItemTypeService.getItemType(TEST_SIMPLE_BUILDING_ID), 2, true, createRegion(new Rectangle(4000, 4000, 2000, 2000), 1), false, null, false, null));
        SimpleBase simpleBase = serverPlanetServices.getBaseService().createBotBase(new BotConfig(0, false, 0, null, null, "Test Bot", null, null, null, null));

        BotItemContainer botItemContainer = new BotItemContainer(botItemConfigs, null, serverPlanetServices, "Test Bot");

        Assert.assertFalse(botItemContainer.isFulfilledUseInTestOnly(simpleBase));

        for (int i = 0; i < 5; i++) {
            // Mock Services instead of this for loop
            botItemContainer.work(simpleBase);
            waitForActionServiceDone(TEST_PLANET_1_ID);
        }
        Assert.assertTrue(botItemContainer.isFulfilledUseInTestOnly(simpleBase));
        Assert.assertEquals(1, getAllSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID, null, TEST_PLANET_1_ID).size());
        Assert.assertEquals(1, getAllSynItemId(simpleBase, TEST_FACTORY_ITEM_ID, null, TEST_PLANET_1_ID).size());
        Assert.assertEquals(3, getAllSynItemId(simpleBase, TEST_ATTACK_ITEM_ID, null, TEST_PLANET_1_ID).size());
        Assert.assertEquals(2, getAllSynItemId(simpleBase, TEST_SIMPLE_BUILDING_ID, null, TEST_PLANET_1_ID).size());

        serverPlanetServices.getItemService().killSyncItemIds(getAllSynItemId(simpleBase, TEST_ATTACK_ITEM_ID, null, TEST_PLANET_1_ID));
        serverPlanetServices.getItemService().killSyncItemIds(getAllSynItemId(simpleBase, TEST_FACTORY_ITEM_ID, null, TEST_PLANET_1_ID));
        serverPlanetServices.getItemService().killSyncItemIds(getAllSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID, null, TEST_PLANET_1_ID));
        for (int i = 0; i < 5; i++) {
            // Mock Services instead of this for loop
            botItemContainer.work(simpleBase);
            waitForActionServiceDone(TEST_PLANET_1_ID);
        }
        Assert.assertTrue(botItemContainer.isFulfilledUseInTestOnly(simpleBase));
        Assert.assertEquals(1, getAllSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID, null, TEST_PLANET_1_ID).size());
        Assert.assertEquals(1, getAllSynItemId(simpleBase, TEST_FACTORY_ITEM_ID, null, TEST_PLANET_1_ID).size());
        Assert.assertEquals(3, getAllSynItemId(simpleBase, TEST_ATTACK_ITEM_ID, null, TEST_PLANET_1_ID).size());
        Assert.assertEquals(2, getAllSynItemId(simpleBase, TEST_SIMPLE_BUILDING_ID, null, TEST_PLANET_1_ID).size());

        botItemContainer.killAllItems(simpleBase);
        assertWholeItemCount(TEST_PLANET_1_ID, 0);
    }

    @Test
    @DirtiesContext
    public void regionAndMovable() throws Exception {
        configureSimplePlanetNoResources();
        ServerPlanetServices serverPlanetServices = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID);

        Collection<BotItemConfig> botItemConfigs = new ArrayList<>();
        botItemConfigs.add(new BotItemConfig((BaseItemType) serverItemTypeService.getItemType(TEST_START_BUILDER_ITEM_ID), 1, true, createRegion(new Rectangle(2000, 2000, 2000, 2000), 1), false, null, false, null));
        botItemConfigs.add(new BotItemConfig((BaseItemType) serverItemTypeService.getItemType(TEST_FACTORY_ITEM_ID), 1, false, createRegion(new Rectangle(4000, 2000, 2000, 2000), 1), false, null, false, null));
        botItemConfigs.add(new BotItemConfig((BaseItemType) serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID), 3, false, createRegion(new Rectangle(8000, 8000, 2000, 2000), 1), false, null, false, null));
        SimpleBase simpleBase = serverPlanetServices.getBaseService().createBotBase(new BotConfig(0, false, 0, null, null, "Test Bot", null, null, null, null));

        BotItemContainer botItemContainer = new BotItemContainer(botItemConfigs, null, serverPlanetServices, "Test Bot");
        Assert.assertFalse(botItemContainer.isFulfilledUseInTestOnly(simpleBase));

        for (int i = 0; i < 5; i++) {
            // Mock Services instead of this for loop
            botItemContainer.work(simpleBase);
            waitForActionServiceDone(TEST_PLANET_1_ID);
        }
        Assert.assertTrue(botItemContainer.isFulfilledUseInTestOnly(simpleBase));
        Assert.assertEquals(1, getAllSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID, null, TEST_PLANET_1_ID).size());
        Assert.assertEquals(1, getAllSynItemId(simpleBase, TEST_FACTORY_ITEM_ID, null, TEST_PLANET_1_ID).size());
        Assert.assertEquals(3, getAllSynItemId(simpleBase, TEST_ATTACK_ITEM_ID, null, TEST_PLANET_1_ID).size());

        botItemContainer.killAllItems(simpleBase);
        assertWholeItemCount(TEST_PLANET_1_ID, 0);
    }

    @Test
    @DirtiesContext
    public void multipleFactoriesAndOverdrive() throws Exception {
        configureSimplePlanetNoResources();
        ServerPlanetServices serverPlanetServices = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID);

        Collection<BotItemConfig> botItemConfigs = new ArrayList<>();
        botItemConfigs.add(new BotItemConfig((BaseItemType) serverItemTypeService.getItemType(TEST_START_BUILDER_ITEM_ID), 1, true, createRegion(new Rectangle(2000, 2000, 1000, 1000), 1), false, null, false, null));
        botItemConfigs.add(new BotItemConfig((BaseItemType) serverItemTypeService.getItemType(TEST_FACTORY_ITEM_ID), 6, false, createRegion(new Rectangle(2000, 2000, 1000, 1000), 1), false, null, false, null));
        botItemConfigs.add(new BotItemConfig((BaseItemType) serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID), 3, false, null, false, null, false, null));
        SimpleBase simpleBase = serverPlanetServices.getBaseService().createBotBase(new BotConfig(0, false, 0, null, null, "Test Bot", null, null, null, null));

        BotItemContainer botItemContainer = new BotItemContainer(botItemConfigs, null, serverPlanetServices, "Test Bot");
        Assert.assertFalse(botItemContainer.isFulfilledUseInTestOnly(simpleBase));

        for (int i = 0; i < 50; i++) {
            // Mock Services instead of this for loop
            botItemContainer.work(simpleBase);
            waitForActionServiceDone(TEST_PLANET_1_ID);
        }
        Assert.assertTrue(botItemContainer.isFulfilledUseInTestOnly(simpleBase));
        Assert.assertEquals(1, getAllSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID, null, TEST_PLANET_1_ID).size());
        Assert.assertEquals(6, getAllSynItemId(simpleBase, TEST_FACTORY_ITEM_ID, null, TEST_PLANET_1_ID).size());
        Assert.assertEquals(3, getAllSynItemId(simpleBase, TEST_ATTACK_ITEM_ID, null, TEST_PLANET_1_ID).size());

        serverPlanetServices.getItemService().killSyncItemIds(getAllSynItemId(simpleBase, TEST_ATTACK_ITEM_ID, null, TEST_PLANET_1_ID));
        Assert.assertFalse(botItemContainer.isFulfilledUseInTestOnly(simpleBase));
        for (int i = 0; i < 50; i++) {
            // Mock Services instead of this for loop
            botItemContainer.work(simpleBase);
            waitForActionServiceDone(TEST_PLANET_1_ID);
        }
        Assert.assertTrue(botItemContainer.isFulfilledUseInTestOnly(simpleBase));
        Assert.assertEquals(1, getAllSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID, null, TEST_PLANET_1_ID).size());
        Assert.assertEquals(6, getAllSynItemId(simpleBase, TEST_FACTORY_ITEM_ID, null, TEST_PLANET_1_ID).size());
        Assert.assertEquals(3, getAllSynItemId(simpleBase, TEST_ATTACK_ITEM_ID, null, TEST_PLANET_1_ID).size());

        botItemContainer.killAllItems(simpleBase);
        assertWholeItemCount(TEST_PLANET_1_ID, 0);
    }

    @Test
    @DirtiesContext
    public void buildupWrongConfig() throws Exception {
        configureSimplePlanetNoResources();
        ServerPlanetServices serverPlanetServices = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID);

        Collection<BotItemConfig> botItemConfigs = new ArrayList<>();
        botItemConfigs.add(new BotItemConfig((BaseItemType) serverItemTypeService.getItemType(TEST_START_BUILDER_ITEM_ID), 1, true, createRegion(new Rectangle(2000, 2000, 1000, 1000), 1), false, null, false, null));
        botItemConfigs.add(new BotItemConfig((BaseItemType) serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID), 3, false, null, false, null, false, null));
        SimpleBase simpleBase = serverPlanetServices.getBaseService().createBotBase(new BotConfig(0, false, 0, null, null, "Test Bot", null, null, null, null));

        BotItemContainer botItemContainer = new BotItemContainer(botItemConfigs, null, serverPlanetServices, "Test Bot");
        Assert.assertFalse(botItemContainer.isFulfilledUseInTestOnly(simpleBase));

        for (int i = 0; i < 50; i++) {
            // Mock Services instead of this for loop
            botItemContainer.work(simpleBase);
            waitForActionServiceDone(TEST_PLANET_1_ID);
        }

        Assert.assertFalse(botItemContainer.isFulfilledUseInTestOnly(simpleBase));
    }

    @Test
    @DirtiesContext
    public void getIdleAttackers() throws Exception {
        configureSimplePlanetNoResources();
        ServerPlanetServices serverPlanetServices = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID);

        SimpleBase simpleBase = serverPlanetServices.getBaseService().createBotBase(new BotConfig(0, false, 0, null, null, "Test Bot", null, null, null, null));

        SyncBaseItem defender1 = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(1000, 1000), new Id(1, Id.NO_ID));
        SyncBaseItem defender2 = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(1000, 1200), new Id(2, Id.NO_ID));
        SyncBaseItem defender3 = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(1000, 1400), new Id(3, Id.NO_ID));

        BaseService baseService = EasyMock.createStrictMock(BaseService.class);
        EasyMock.expect(baseService.getItems(simpleBase)).andReturn(null).times(2);
        Collection<SyncBaseItem> botItems = new ArrayList<>();
        botItems.add(defender1);
        botItems.add(defender2);
        botItems.add(defender3);
        EasyMock.expect(baseService.getItems(simpleBase)).andReturn(botItems);

        CollisionService mockCollisionService = EasyMock.createNiceMock(CollisionService.class);

        ServerItemService mockServerItemService = EasyMock.createStrictMock(ServerItemService.class);
        // TODO region expected
        EasyMock.expect(mockServerItemService.createSyncObject(serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID), null, null, simpleBase)).andReturn(defender1);
        EasyMock.expect(mockServerItemService.createSyncObject(serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID), null, null, simpleBase)).andReturn(defender2);
        EasyMock.expect(mockServerItemService.createSyncObject(serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID), null, null, simpleBase)).andReturn(defender3);

        Collection<BotItemConfig> botItemConfigs = new ArrayList<>();
        botItemConfigs.add(new BotItemConfig((BaseItemType) serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID), 3, true, createRegion(new Rectangle(2000, 2000, 1000, 1000), 1), false, null, false, null));

        TestPlanetServices testServices = new TestPlanetServices();
        testServices.setItemService(mockServerItemService);
        testServices.setCollisionService(mockCollisionService);
        testServices.setBaseService(baseService);

        EasyMock.replay(baseService);
        EasyMock.replay(mockCollisionService);
        EasyMock.replay(mockServerItemService);

        BotItemContainer botItemContainer = new BotItemContainer(botItemConfigs, null, testServices, "Test Bot");

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
        EasyMock.verify(mockServerItemService);
    }

    @Test
    @DirtiesContext
    public void moveRealIfIdle() throws Exception {
        configureSimplePlanetNoResources();
        ServerPlanetServices serverPlanetServices = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID);

        SimpleBase simpleBase = serverPlanetServices.getBaseService().createBotBase(new BotConfig(0, false, 0, null, null, "Test Bot", null, null, null, null));
        SyncBaseItem syncBaseItem = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(500, 500), new Id(1, Id.NO_ID));

        TestPlanetServices testServices = new TestPlanetServices();

        BaseService baseServiceMock = EasyMock.createStrictMock(BaseService.class);
        Collection<SyncBaseItem> botItems = new ArrayList<>();
        botItems.add(syncBaseItem);
        EasyMock.expect(baseServiceMock.getItems(simpleBase)).andReturn(null);
        EasyMock.expect(baseServiceMock.getItems(simpleBase)).andReturn(botItems).times(2);
        testServices.setBaseService(baseServiceMock);

        ActionService mockActionService = EasyMock.createStrictMock(ActionService.class);
        mockActionService.move(EasyMock.eq(syncBaseItem), EasyMock.eq(new Index(3000, 3500)));
        testServices.setActionService(mockActionService);

        Region region1 = createRegion(new Rectangle(0, 0, 1000, 1000), 1);
        Region region2 = createRegion(new Rectangle(2000, 3000, 1000, 2000), 2);
        CollisionService mockCollisionService = EasyMock.createStrictMock(CollisionService.class);
        EasyMock.expect(mockCollisionService.getFreeRandomPosition(syncBaseItem.getBaseItemType(), region1, 0, false, true)).andReturn(new Index(500, 500));
        EasyMock.expect(mockCollisionService.getFreeRandomPosition(syncBaseItem.getBaseItemType(), region2, 0, false, false)).andReturn(new Index(3000, 3500));
        testServices.setCollisionService(mockCollisionService);

        BaseItemType baseItemType = (BaseItemType) serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID);
        Collection<BotItemConfig> botItemConfigs = new ArrayList<>();
        botItemConfigs.add(new BotItemConfig(baseItemType, 1, true, region1, true, null, false, null));

        ServerItemService mockServerItemService = EasyMock.createStrictMock(ServerItemService.class);
        EasyMock.expect(mockServerItemService.createSyncObject(serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID), new Index(500, 500), null, simpleBase)).andReturn(syncBaseItem);
        testServices.setItemService(mockServerItemService);

        EasyMock.replay(baseServiceMock);
        EasyMock.replay(mockActionService);
        EasyMock.replay(mockCollisionService);
        EasyMock.replay(mockServerItemService);
        BotItemContainer botItemContainer = new BotItemContainer(botItemConfigs, region2, testServices, "Test Bot");
        botItemContainer.work(simpleBase);
        syncBaseItem.setBuildup(0.0); // Simulate busy
        botItemContainer.work(simpleBase);
        syncBaseItem.setBuildup(1.0); // Simulate not busy
        syncBaseItem.getSyncItemArea().setPosition(new Index(2000, 3500));
        botItemContainer.work(simpleBase);
        EasyMock.verify(baseServiceMock);
        EasyMock.verify(mockActionService);
        EasyMock.verify(mockCollisionService);
        EasyMock.verify(mockServerItemService);
    }

    @Test
    @DirtiesContext
    public void ttlIfIdle() throws Exception {
        configureSimplePlanetNoResources();
        ServerPlanetServices serverPlanetServices = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID);

        SimpleBase simpleBase = serverPlanetServices.getBaseService().createBotBase(new BotConfig(0, false, 0, null, null, "Test Bot", null, null, null, null));
        SyncBaseItem syncBaseItem = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(500, 500), new Id(1, Id.NO_ID));

        TestPlanetServices testServices = new TestPlanetServices();

        BaseService baseServiceMock = EasyMock.createStrictMock(BaseService.class);
        Collection<SyncBaseItem> botItems = new ArrayList<>();
        botItems.add(syncBaseItem);
        EasyMock.expect(baseServiceMock.getItems(simpleBase)).andReturn(Collections.<SyncBaseItem>emptyList());
        EasyMock.expect(baseServiceMock.getItems(simpleBase)).andReturn(botItems);
        testServices.setBaseService(baseServiceMock);

        ServerItemService mockServerItemService = EasyMock.createStrictMock(ServerItemService.class);
        EasyMock.expect(mockServerItemService.createSyncObject(serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID), new Index(300, 350), null, simpleBase)).andReturn(syncBaseItem);
        mockServerItemService.killSyncItem(EasyMock.eq(syncBaseItem), EasyMock.<SimpleBase>isNull(), EasyMock.eq(true), EasyMock.eq(false));
        testServices.setItemService(mockServerItemService);

        CollisionService mockCollisionService = EasyMock.createStrictMock(CollisionService.class);
        Region region1 = createRegion(new Rectangle(0, 0, 1000, 1000), 1);
        EasyMock.expect(mockCollisionService.getFreeRandomPosition(syncBaseItem.getBaseItemType(), region1, 0, false, true)).andReturn(new Index(300, 350));
        testServices.setCollisionService(mockCollisionService);

        BaseItemType baseItemType = (BaseItemType) serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID);
        Collection<BotItemConfig> botItemConfigs = new ArrayList<>();
        botItemConfigs.add(new BotItemConfig(baseItemType, 1, true, region1, false, 50, false, null));

        EasyMock.replay(baseServiceMock, mockServerItemService, mockCollisionService);
        BotItemContainer botItemContainer = new BotItemContainer(botItemConfigs, createRegion(new Rectangle(2000, 3000, 1000, 2000), 1), testServices, "Test Bot");
        botItemContainer.work(simpleBase);
        Thread.sleep(60);
        botItemContainer.work(simpleBase);
        EasyMock.verify(mockServerItemService, baseServiceMock, mockCollisionService);
    }

    @Test
    @DirtiesContext
    public void noRebuild() throws Exception {
        configureSimplePlanetNoResources();
        ServerPlanetServices serverPlanetServices = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID);

        SimpleBase simpleBase = serverPlanetServices.getBaseService().createBotBase(new BotConfig(0, false, 0, null, null, "Test Bot", null, null, null, null));
        SyncBaseItem syncBaseItem = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(500, 500), new Id(1, Id.NO_ID));

        TestPlanetServices testServices = new TestPlanetServices();

        BaseService baseServiceMock = EasyMock.createStrictMock(BaseService.class);
        Collection<SyncBaseItem> botItems = new ArrayList<>();
        botItems.add(syncBaseItem);
        EasyMock.expect(baseServiceMock.getItems(simpleBase)).andReturn(null);
        EasyMock.expect(baseServiceMock.getItems(simpleBase)).andReturn(botItems);
        EasyMock.expect(baseServiceMock.getItems(simpleBase)).andReturn(null);
        testServices.setBaseService(baseServiceMock);

        BaseItemType baseItemType = (BaseItemType) serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID);
        Collection<BotItemConfig> botItemConfigs = new ArrayList<>();
        Region region1 = createRegion(new Rectangle(0, 0, 1000, 1000), 1);
        botItemConfigs.add(new BotItemConfig(baseItemType, 1, true, region1, false, null, true, null));

        CollisionService mockCollisionService = EasyMock.createStrictMock(CollisionService.class);
        EasyMock.expect(mockCollisionService.getFreeRandomPosition(syncBaseItem.getBaseItemType(), region1, 0, false, true)).andReturn(new Index(3000, 3500));
        testServices.setCollisionService(mockCollisionService);

        ServerItemService mockServerItemService = EasyMock.createStrictMock(ServerItemService.class);
        EasyMock.expect(mockServerItemService.createSyncObject(serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID), new Index(3000, 3500), null, simpleBase)).andReturn(syncBaseItem);
        testServices.setItemService(mockServerItemService);

        EasyMock.replay(baseServiceMock, mockCollisionService, mockServerItemService);
        BotItemContainer botItemContainer = new BotItemContainer(botItemConfigs, createRegion(new Rectangle(2000, 3000, 1000, 2000), 1), testServices, "Test Bot");
        botItemContainer.work(simpleBase);
        botItemContainer.work(simpleBase);
        syncBaseItem.setHealth(0);
        botItemContainer.work(simpleBase);
        EasyMock.verify(baseServiceMock, mockCollisionService, mockServerItemService);
    }

    @Test
    @DirtiesContext
    public void rePop() throws Exception {
        configureSimplePlanetNoResources();
        ServerPlanetServices serverPlanetServices = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID);

        SimpleBase simpleBase = serverPlanetServices.getBaseService().createBotBase(new BotConfig(0, false, 0, null, null, "Test Bot", null, null, null, null));
        SyncBaseItem syncBaseItem = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(500, 500), new Id(1, Id.NO_ID));

        TestPlanetServices testServices = new TestPlanetServices();

        BaseService baseServiceMock = EasyMock.createStrictMock(BaseService.class);
        Collection<SyncBaseItem> botItems = new ArrayList<>();
        botItems.add(syncBaseItem);
        EasyMock.expect(baseServiceMock.getItems(simpleBase)).andReturn(null);
        EasyMock.expect(baseServiceMock.getItems(simpleBase)).andReturn(botItems);
        EasyMock.expect(baseServiceMock.getItems(simpleBase)).andReturn(null);
        EasyMock.expect(baseServiceMock.getItems(simpleBase)).andReturn(botItems);
        testServices.setBaseService(baseServiceMock);

        BaseItemType baseItemType = (BaseItemType) serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID);
        Collection<BotItemConfig> botItemConfigs = new ArrayList<>();
        Region region1 = createRegion(new Rectangle(0, 0, 1000, 1000), 1);
        botItemConfigs.add(new BotItemConfig(baseItemType, 1, true, region1, false, null, false, 100L));

        CollisionService mockCollisionService = EasyMock.createStrictMock(CollisionService.class);
        EasyMock.expect(mockCollisionService.getFreeRandomPosition(syncBaseItem.getBaseItemType(), region1, 0, false, true)).andReturn(new Index(3000, 3500));
        EasyMock.expect(mockCollisionService.getFreeRandomPosition(syncBaseItem.getBaseItemType(), region1, 0, false, true)).andReturn(new Index(3000, 3500));
        testServices.setCollisionService(mockCollisionService);

        ServerItemService mockServerItemService = EasyMock.createStrictMock(ServerItemService.class);
        EasyMock.expect(mockServerItemService.createSyncObject(serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID), new Index(3000, 3500), null, simpleBase)).andReturn(syncBaseItem);
        EasyMock.expect(mockServerItemService.createSyncObject(serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID), new Index(3000, 3500), null, simpleBase)).andReturn(syncBaseItem);
        testServices.setItemService(mockServerItemService);

        EasyMock.replay(baseServiceMock, mockCollisionService, mockServerItemService);
        BotItemContainer botItemContainer = new BotItemContainer(botItemConfigs, createRegion(new Rectangle(2000, 3000, 1000, 2000), 1), testServices, "Test Bot");
        botItemContainer.work(simpleBase);
        botItemContainer.work(simpleBase);
        syncBaseItem.setHealth(0);
        botItemContainer.work(simpleBase);
        Thread.sleep(110);
        syncBaseItem.setHealth(1);
        botItemContainer.work(simpleBase);
        EasyMock.verify(baseServiceMock, mockCollisionService, mockServerItemService);
    }

    @Test
    @DirtiesContext
    public void itemBelongsToMy() throws Exception {
        configureSimplePlanetNoResources();
        ServerPlanetServices serverPlanetServices = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID);

        SimpleBase botBase = serverPlanetServices.getBaseService().createBotBase(new BotConfig(0, false, 0, null, null, "Test Bot", null, null, null, null));
        SimpleBase otherBotBase = serverPlanetServices.getBaseService().createBotBase(new BotConfig(0, false, 0, null, null, "OtherTest Bot", null, null, null, null));
        SyncBaseItem botBaseItem = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(1000, 500), new Id(1, Id.NO_ID));
        SyncBaseItem otherBaseItem = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(500, 1000), new Id(2, Id.NO_ID), otherBotBase);
        SyncBaseItem otherItem = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(1000, 1000), new Id(3, Id.NO_ID));

        TestPlanetServices testServices = new TestPlanetServices();

        BaseService baseServiceMock = EasyMock.createStrictMock(BaseService.class);
        EasyMock.expect(baseServiceMock.getItems(botBase)).andReturn(null);
        testServices.setBaseService(baseServiceMock);

        BaseItemType baseItemType = (BaseItemType) serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID);
        Collection<BotItemConfig> botItemConfigs = new ArrayList<>();
        Region region1 = createRegion(new Rectangle(0, 0, 1000, 1000), 1);
        botItemConfigs.add(new BotItemConfig(baseItemType, 1, true, region1, false, null, false, 100L));

        CollisionService mockCollisionService = EasyMock.createStrictMock(CollisionService.class);
        EasyMock.expect(mockCollisionService.getFreeRandomPosition(botBaseItem.getBaseItemType(), region1, 0, false, true)).andReturn(new Index(3000, 3500));
        testServices.setCollisionService(mockCollisionService);

        ServerItemService mockServerItemService = EasyMock.createStrictMock(ServerItemService.class);
        EasyMock.expect(mockServerItemService.createSyncObject(serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID), new Index(3000, 3500), null, botBase)).andReturn(botBaseItem);
        testServices.setItemService(mockServerItemService);

        EasyMock.replay(baseServiceMock, mockCollisionService, mockServerItemService);
        BotItemContainer botItemContainer = new BotItemContainer(botItemConfigs, createRegion(new Rectangle(2000, 3000, 1000, 2000), 1), testServices, "Test Bot");
        botItemContainer.work(botBase);
        Assert.assertTrue(botItemContainer.itemBelongsToMy(botBaseItem));
        Assert.assertFalse(botItemContainer.itemBelongsToMy(otherBaseItem));
        Assert.assertFalse(botItemContainer.itemBelongsToMy(otherItem));
        EasyMock.verify(baseServiceMock, mockCollisionService, mockServerItemService);
    }

    @Test
    @DirtiesContext
    public void killItemsOnOverCreated() throws Exception {
        configureSimplePlanetNoResources();
        ServerPlanetServices serverPlanetServices = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID);

        SimpleBase simpleBase = serverPlanetServices.getBaseService().createBotBase(new BotConfig(0, false, 0, null, null, "Test Bot", null, null, null, null));
        SyncBaseItem syncBaseItem1 = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(500, 500), new Id(1, Id.NO_ID));
        SyncBaseItem syncBaseItem2 = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(1000, 500), new Id(2, Id.NO_ID));
        SyncBaseItem syncBaseItem3 = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(1000, 500), new Id(3, Id.NO_ID));

        TestPlanetServices testServices = new TestPlanetServices();

        BaseService baseServiceMock = EasyMock.createStrictMock(BaseService.class);
        Collection<SyncBaseItem> botItems = new ArrayList<>();
        botItems.add(syncBaseItem1);
        botItems.add(syncBaseItem2);
        botItems.add(syncBaseItem3);
        EasyMock.expect(baseServiceMock.getItems(simpleBase)).andReturn(Collections.<SyncBaseItem>emptyList());
        EasyMock.expect(baseServiceMock.getItems(simpleBase)).andReturn(botItems);
        testServices.setBaseService(baseServiceMock);

        ServerItemService mockServerItemService = EasyMock.createStrictMock(ServerItemService.class);
        EasyMock.expect(mockServerItemService.createSyncObject(serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID), new Index(300, 350), null, simpleBase)).andReturn(syncBaseItem1);
        mockServerItemService.killSyncItem(EasyMock.eq(syncBaseItem2), EasyMock.<SimpleBase>isNull(), EasyMock.eq(true), EasyMock.eq(false));
        mockServerItemService.killSyncItem(EasyMock.eq(syncBaseItem3), EasyMock.<SimpleBase>isNull(), EasyMock.eq(true), EasyMock.eq(false));
        testServices.setItemService(mockServerItemService);

        CollisionService mockCollisionService = EasyMock.createStrictMock(CollisionService.class);
        Region region1 = createRegion(new Rectangle(0, 0, 1000, 1000), 1);
        EasyMock.expect(mockCollisionService.getFreeRandomPosition(syncBaseItem1.getBaseItemType(), region1, 0, false, true)).andReturn(new Index(300, 350));
        testServices.setCollisionService(mockCollisionService);

        BaseItemType baseItemType = (BaseItemType) serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID);
        Collection<BotItemConfig> botItemConfigs = new ArrayList<>();
        botItemConfigs.add(new BotItemConfig(baseItemType, 1, true, region1, false, null, false, null));

        EasyMock.replay(baseServiceMock, mockServerItemService, mockCollisionService);
        BotItemContainer botItemContainer = new BotItemContainer(botItemConfigs, createRegion(new Rectangle(2000, 3000, 1000, 2000), 1), testServices, "Test Bot");
        botItemContainer.work(simpleBase);
        Thread.sleep(60);
        botItemContainer.work(simpleBase);
        EasyMock.verify(mockServerItemService, baseServiceMock, mockCollisionService);
    }

}
