package com.btxtech.game.services.bot;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.Region;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotEnragementStateConfig;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotItemConfig;
import com.btxtech.game.jsre.common.gameengine.services.bot.impl.BotEnragementState;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.TestPlanetServices;
import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.services.planet.BaseService;
import com.btxtech.game.services.planet.CollisionService;
import com.btxtech.game.services.planet.ServerItemService;
import org.easymock.EasyMock;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * User: beat
 * Date: 17.06.12
 * Time: 13:03
 */
public class TestBotEnragementState extends AbstractServiceTest {
    @Autowired
    private ServerItemTypeService serverItemTypeService;

    @Test
    @DirtiesContext
    public void noEnrageUp() throws Exception {
        configureSimplePlanetNoResources();

        SimpleBase botBase = new SimpleBase(1, 1);
        SimpleBase actorBase = new SimpleBase(2, 1);
        SyncBaseItem botItem1 = createSyncBaseItem(TEST_SIMPLE_BUILDING_ID, new Index(200, 200), new Id(1, Id.NO_ID), botBase);

        Collection<BotItemConfig> botItems = new ArrayList<>();
        botItems.add(new BotItemConfig((BaseItemType) serverItemTypeService.getItemType(TEST_SIMPLE_BUILDING_ID), 1, true, createRegion(new Rectangle(0, 0, 1000, 1000), 1), false, null, false, null));

        List<BotEnragementStateConfig> botEnragementStateConfigs = new ArrayList<>();
        botEnragementStateConfigs.add(new BotEnragementStateConfig("NormalTest", botItems, null));

        TestPlanetServices testServices = new TestPlanetServices();

        BotEnragementState.Listener listener = EasyMock.createStrictMock(BotEnragementState.Listener.class);

        BaseService baseServiceMock = EasyMock.createStrictMock(BaseService.class);
        EasyMock.expect(baseServiceMock.getItems(botBase)).andReturn(null);
        testServices.setBaseService(baseServiceMock);

        ServerItemService mockServerItemService = EasyMock.createStrictMock(ServerItemService.class);
        EasyMock.expect(mockServerItemService.createSyncObject(serverItemTypeService.getItemType(TEST_SIMPLE_BUILDING_ID), new Index(200, 200), null, botBase)).andReturn(botItem1);
        testServices.setItemService(mockServerItemService);

        CollisionService mockCollisionService = EasyMock.createStrictMock(CollisionService.class);
        Region region1 = createRegion(new Rectangle(0, 0, 1000, 1000), 1);
        EasyMock.expect(mockCollisionService.getFreeRandomPosition(botItem1.getBaseItemType(), region1, 0, false, true)).andReturn(new Index(200, 200));
        testServices.setCollisionService(mockCollisionService);

        EasyMock.replay(listener, baseServiceMock, mockServerItemService, mockCollisionService);

        BotEnragementState botEnragementState = new BotEnragementState(botEnragementStateConfigs, region1, testServices, "TestBot", listener);
        botEnragementState.work(botBase);
        botEnragementState.onBotItemKilled(botItem1, actorBase);

        EasyMock.verify(listener, baseServiceMock, mockServerItemService, mockCollisionService);
    }

    @Test
    @DirtiesContext
    public void oneEnrageUp() throws Exception {
        configureSimplePlanetNoResources();

        SimpleBase botBase = new SimpleBase(1, 1);
        SimpleBase actorBase = new SimpleBase(2, 1);
        SyncBaseItem botItem1State1 = createSyncBaseItem(TEST_SIMPLE_BUILDING_ID, new Index(200, 200), new Id(1, Id.NO_ID), botBase);
        SyncBaseItem botItem2State1 = EasyMock.createStrictMock(SyncBaseItem.class);
        botItem2State1.setBuildup(1.0);
        EasyMock.expect(botItem2State1.isAlive()).andReturn(true);
        EasyMock.expect(botItem2State1.isIdle()).andReturn(true);
        EasyMock.expect(botItem2State1.getId()).andReturn(new Id(2, Id.NO_ID));
        EasyMock.expect(botItem2State1.isAlive()).andReturn(true);
        EasyMock.expect(botItem2State1.isIdle()).andReturn(true);
        EasyMock.expect(botItem2State1.getId()).andReturn(new Id(2, Id.NO_ID));
        EasyMock.expect(botItem2State1.isAlive()).andReturn(true);
        EasyMock.expect(botItem2State1.isIdle()).andReturn(true);
        EasyMock.expect(botItem2State1.getId()).andReturn(new Id(2, Id.NO_ID));
        EasyMock.expect(botItem2State1.isAlive()).andReturn(true);
        EasyMock.expect(botItem2State1.isAlive()).andReturn(false);
        EasyMock.expect(botItem2State1.getId()).andReturn(new Id(2, Id.NO_ID));
        botItem2State1.setBuildup(1.0);



        SyncBaseItem botItem3State2 = EasyMock.createStrictMock(SyncBaseItem.class);
        botItem3State2.setBuildup(1.0);
        EasyMock.expect(botItem3State2.isAlive()).andReturn(true);
        EasyMock.expect(botItem3State2.isIdle()).andReturn(true);
        EasyMock.expect(botItem3State2.getId()).andReturn(new Id(3, Id.NO_ID));
        EasyMock.expect(botItem3State2.isAlive()).andReturn(true);
        EasyMock.expect(botItem3State2.isAlive()).andReturn(false);
        EasyMock.expect(botItem3State2.getId()).andReturn(new Id(3, Id.NO_ID));
        SyncBaseItem attacker = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(200, 200), new Id(4, Id.NO_ID), actorBase);

        List<BotEnragementStateConfig> botEnragementStateConfigs = new ArrayList<>();
        Collection<BotItemConfig> botItems = new ArrayList<>();
        botItems.add(new BotItemConfig((BaseItemType) serverItemTypeService.getItemType(TEST_SIMPLE_BUILDING_ID), 2, true, createRegion(new Rectangle(0, 0, 1000, 1000), 1), false, null, false, null));
        BotEnragementStateConfig normalState = new BotEnragementStateConfig("NormalTest", botItems, 2);
        botEnragementStateConfigs.add(normalState);
        botItems = new ArrayList<>();
        botItems.add(new BotItemConfig((BaseItemType) serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID), 1, true, createRegion(new Rectangle(0, 0, 1000, 1000), 1), false, null, false, null));
        BotEnragementStateConfig botEnragementStateConfig = new BotEnragementStateConfig("AngryTest", botItems, null);
        botEnragementStateConfigs.add(botEnragementStateConfig);

        TestPlanetServices testServices = new TestPlanetServices();

        BotEnragementState.Listener listener = EasyMock.createStrictMock(BotEnragementState.Listener.class);
        listener.onEnrageUp("TestBot", botEnragementStateConfig, actorBase);
        listener.onEnrageNormal("TestBot", normalState);

        BaseService baseServiceMock = EasyMock.createStrictMock(BaseService.class);
        EasyMock.expect(baseServiceMock.getItems(botBase)).andReturn(null);
        EasyMock.expect(baseServiceMock.getItems(botBase)).andReturn(null);
        EasyMock.expect(baseServiceMock.getItems(botBase)).andReturn(null);
        EasyMock.expect(baseServiceMock.getItems(botBase)).andReturn(null);
        EasyMock.expect(baseServiceMock.getItems(botBase)).andReturn(null);
        EasyMock.expect(baseServiceMock.getItems(botBase)).andReturn(null);
        EasyMock.expect(baseServiceMock.getItems(botBase)).andReturn(null);
        EasyMock.expect(baseServiceMock.getItems(botBase)).andReturn(null);
        EasyMock.expect(baseServiceMock.getItems(botBase)).andReturn(null);
        testServices.setBaseService(baseServiceMock);

        ServerItemService mockServerItemService = EasyMock.createStrictMock(ServerItemService.class);
        EasyMock.expect(mockServerItemService.createSyncObject(serverItemTypeService.getItemType(TEST_SIMPLE_BUILDING_ID), new Index(200, 200), null, botBase)).andReturn(botItem1State1);
        EasyMock.expect(mockServerItemService.createSyncObject(serverItemTypeService.getItemType(TEST_SIMPLE_BUILDING_ID), new Index(200, 200), null, botBase)).andReturn(botItem2State1);
        EasyMock.expect(mockServerItemService.createSyncObject(serverItemTypeService.getItemType(TEST_SIMPLE_BUILDING_ID), new Index(200, 200), null, botBase)).andReturn(botItem1State1);
        mockServerItemService.killSyncItem(botItem2State1, null, true, false);
        EasyMock.expect(mockServerItemService.createSyncObject(serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID), new Index(200, 200), null, botBase)).andReturn(botItem3State2);
        mockServerItemService.killSyncItem(botItem3State2, null, true, false);
        EasyMock.expect(mockServerItemService.createSyncObject(serverItemTypeService.getItemType(TEST_SIMPLE_BUILDING_ID), new Index(200, 200), null, botBase)).andReturn(botItem1State1);
        EasyMock.expect(mockServerItemService.createSyncObject(serverItemTypeService.getItemType(TEST_SIMPLE_BUILDING_ID), new Index(200, 200), null, botBase)).andReturn(botItem2State1);
        testServices.setItemService(mockServerItemService);

        CollisionService mockCollisionService = EasyMock.createStrictMock(CollisionService.class);
        Region region1 = createRegion(new Rectangle(0, 0, 1000, 1000), 1);
        EasyMock.expect(mockCollisionService.getFreeRandomPosition(botItem1State1.getBaseItemType(), region1, 0, false, true)).andReturn(new Index(200, 200));
        EasyMock.expect(mockCollisionService.getFreeRandomPosition(botItem1State1.getBaseItemType(), region1, 0, false, true)).andReturn(new Index(200, 200));
        EasyMock.expect(mockCollisionService.getFreeRandomPosition(botItem1State1.getBaseItemType(), region1, 0, false, true)).andReturn(new Index(200, 200));
        EasyMock.expect(mockCollisionService.getFreeRandomPosition(serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID), region1, 0, false, true)).andReturn(new Index(200, 200));
        EasyMock.expect(mockCollisionService.getFreeRandomPosition(botItem1State1.getBaseItemType(), region1, 0, false, true)).andReturn(new Index(200, 200));
        EasyMock.expect(mockCollisionService.getFreeRandomPosition(botItem1State1.getBaseItemType(), region1, 0, false, true)).andReturn(new Index(200, 200));
        testServices.setCollisionService(mockCollisionService);

        EasyMock.replay(listener, baseServiceMock, mockServerItemService, mockCollisionService, botItem2State1, botItem3State2);

        BotEnragementState botEnragementState = new BotEnragementState(botEnragementStateConfigs, region1, testServices, "TestBot", listener);
        botEnragementState.work(botBase);
        botEnragementState.handleIntruders(Collections.singletonList(attacker), botBase);
        botEnragementState.onBotItemKilled(botItem1State1, actorBase);
        botItem1State1.setHealth(0);
        botEnragementState.handleIntruders(Collections.singletonList(attacker), botBase);
        botEnragementState.work(botBase); // Remove item
        botItem1State1.setHealth(1.0);
        botEnragementState.work(botBase); // Create new item
        botItem1State1.setHealth(0);
        botEnragementState.onBotItemKilled(botItem1State1, actorBase);
        // Next enrage state reached
        botEnragementState.handleIntruders(Collections.singletonList(attacker), botBase);
        botEnragementState.work(botBase);
        botEnragementState.handleIntruders(Collections.<SyncBaseItem>emptyList(), botBase);
        // Normal enrage reached
        botEnragementState.work(botBase);

        EasyMock.verify(listener, baseServiceMock, mockServerItemService, mockCollisionService, botItem2State1, botItem3State2);
    }

    @Test
    @DirtiesContext
    public void realmLeft() throws Exception {
        configureSimplePlanetNoResources();

        SimpleBase botBase = new SimpleBase(1, 1);
        SimpleBase actorBase = new SimpleBase(2, 1);
        SyncBaseItem botItem1State1 = createSyncBaseItem(TEST_SIMPLE_BUILDING_ID, new Index(200, 200), new Id(1, Id.NO_ID), botBase);
        SyncBaseItem botItem2State1 = EasyMock.createStrictMock(SyncBaseItem.class);
        botItem2State1.setBuildup(1.0);
        EasyMock.expect(botItem2State1.isAlive()).andReturn(true);
        EasyMock.expect(botItem2State1.isIdle()).andReturn(true);
        EasyMock.expect(botItem2State1.getId()).andReturn(new Id(2, Id.NO_ID));
        EasyMock.expect(botItem2State1.isAlive()).andReturn(true);
        EasyMock.expect(botItem2State1.isIdle()).andReturn(true);
        EasyMock.expect(botItem2State1.getId()).andReturn(new Id(2, Id.NO_ID));
        EasyMock.expect(botItem2State1.isAlive()).andReturn(true);
        EasyMock.expect(botItem2State1.isIdle()).andReturn(true);
        EasyMock.expect(botItem2State1.getId()).andReturn(new Id(2, Id.NO_ID));
        EasyMock.expect(botItem2State1.isAlive()).andReturn(true);
        EasyMock.expect(botItem2State1.isIdle()).andReturn(true);
        EasyMock.expect(botItem2State1.getId()).andReturn(new Id(2, Id.NO_ID));
        EasyMock.expect(botItem2State1.isAlive()).andReturn(true);
        EasyMock.expect(botItem2State1.isIdle()).andReturn(true);
        EasyMock.expect(botItem2State1.getId()).andReturn(new Id(2, Id.NO_ID));
        EasyMock.expect(botItem2State1.isAlive()).andReturn(true);
        EasyMock.expect(botItem2State1.isAlive()).andReturn(false);
        EasyMock.expect(botItem2State1.getId()).andReturn(new Id(2, Id.NO_ID));
        SyncBaseItem attacker1 = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(200, 200), new Id(4, Id.NO_ID), actorBase);

        List<BotEnragementStateConfig> botEnragementStateConfigs = new ArrayList<>();
        Collection<BotItemConfig> botItems = new ArrayList<>();
        botItems.add(new BotItemConfig((BaseItemType) serverItemTypeService.getItemType(TEST_SIMPLE_BUILDING_ID), 2, true, createRegion(new Rectangle(0, 0, 1000, 1000), 1), false, null, false, null));
        BotEnragementStateConfig normalState = new BotEnragementStateConfig("NormalTest", botItems, 2);
        botEnragementStateConfigs.add(normalState);
        botItems = new ArrayList<>();
        botItems.add(new BotItemConfig((BaseItemType) serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID), 1, true, createRegion(new Rectangle(0, 0, 1000, 1000), 1), false, null, false, null));
        BotEnragementStateConfig botEnragementStateConfig = new BotEnragementStateConfig("AngryTest", botItems, null);
        botEnragementStateConfigs.add(botEnragementStateConfig);

        TestPlanetServices testServices = new TestPlanetServices();

        BotEnragementState.Listener listener = EasyMock.createStrictMock(BotEnragementState.Listener.class);
        listener.onEnrageUp("TestBot", botEnragementStateConfig, actorBase);

        BaseService baseServiceMock = EasyMock.createStrictMock(BaseService.class);
        EasyMock.expect(baseServiceMock.getItems(botBase)).andReturn(null);
        EasyMock.expect(baseServiceMock.getItems(botBase)).andReturn(null);
        EasyMock.expect(baseServiceMock.getItems(botBase)).andReturn(null);
        EasyMock.expect(baseServiceMock.getItems(botBase)).andReturn(null);
        EasyMock.expect(baseServiceMock.getItems(botBase)).andReturn(null);
        EasyMock.expect(baseServiceMock.getItems(botBase)).andReturn(null);
        EasyMock.expect(baseServiceMock.getItems(botBase)).andReturn(null);
        testServices.setBaseService(baseServiceMock);

        ServerItemService mockServerItemService = EasyMock.createStrictMock(ServerItemService.class);
        EasyMock.expect(mockServerItemService.createSyncObject(serverItemTypeService.getItemType(TEST_SIMPLE_BUILDING_ID), new Index(200, 200), null, botBase)).andReturn(botItem1State1);
        EasyMock.expect(mockServerItemService.createSyncObject(serverItemTypeService.getItemType(TEST_SIMPLE_BUILDING_ID), new Index(200, 200), null, botBase)).andReturn(botItem2State1);
        EasyMock.expect(mockServerItemService.createSyncObject(serverItemTypeService.getItemType(TEST_SIMPLE_BUILDING_ID), new Index(200, 200), null, botBase)).andReturn(botItem1State1);
        EasyMock.expect(mockServerItemService.createSyncObject(serverItemTypeService.getItemType(TEST_SIMPLE_BUILDING_ID), new Index(200, 200), null, botBase)).andReturn(botItem1State1);
        mockServerItemService.killSyncItem(botItem2State1, null, true, false);
        testServices.setItemService(mockServerItemService);

        CollisionService mockCollisionService = EasyMock.createStrictMock(CollisionService.class);
        Region region1 = createRegion(new Rectangle(0, 0, 1000, 1000), 1);
        EasyMock.expect(mockCollisionService.getFreeRandomPosition(botItem1State1.getBaseItemType(), region1, 0, false, true)).andReturn(new Index(200, 200));
        EasyMock.expect(mockCollisionService.getFreeRandomPosition(botItem1State1.getBaseItemType(), region1, 0, false, true)).andReturn(new Index(200, 200));
        EasyMock.expect(mockCollisionService.getFreeRandomPosition(botItem1State1.getBaseItemType(), region1, 0, false, true)).andReturn(new Index(200, 200));
        EasyMock.expect(mockCollisionService.getFreeRandomPosition(botItem1State1.getBaseItemType(), region1, 0, false, true)).andReturn(new Index(200, 200));
        testServices.setCollisionService(mockCollisionService);

        EasyMock.replay(listener, baseServiceMock, mockServerItemService, mockCollisionService, botItem2State1);

        BotEnragementState botEnragementState = new BotEnragementState(botEnragementStateConfigs, region1, testServices, "TestBot", listener);
        botEnragementState.work(botBase);
        botEnragementState.handleIntruders(Collections.singletonList(attacker1), botBase);
        botEnragementState.onBotItemKilled(botItem1State1, actorBase);
        botItem1State1.setHealth(0);
        botEnragementState.handleIntruders(Collections.singletonList(attacker1), botBase);
        botEnragementState.work(botBase); // Remove item
        botItem1State1.setHealth(1.0);
        botEnragementState.work(botBase); // Create new item
        // Actor actor is leaving
        botEnragementState.handleIntruders(Collections.<SyncBaseItem>emptyList(), botBase);
        // Actor coming back and kill but no enrage up
        botItem1State1.setHealth(0);
        botEnragementState.onBotItemKilled(botItem1State1, actorBase);
        botEnragementState.handleIntruders(Collections.singletonList(attacker1), botBase);
        botEnragementState.work(botBase); // Remove item
        botItem1State1.setHealth(1.0);
        botEnragementState.work(botBase); // Create new item
        botItem1State1.setHealth(0);
        // Actor kills and rage up expected
        botItem1State1.setHealth(0);
        botEnragementState.onBotItemKilled(botItem1State1, actorBase);

        EasyMock.verify(listener, baseServiceMock, mockServerItemService, mockCollisionService, botItem2State1);
    }

    @Test
    @DirtiesContext
    public void multipleAttackers() throws Exception {
        configureSimplePlanetNoResources();

        SimpleBase botBase = new SimpleBase(1, 1);
        SimpleBase actorBase1 = new SimpleBase(2, 1);
        SimpleBase actorBase2 = new SimpleBase(3, 1);
        SyncBaseItem botItem1State1 = createSyncBaseItem(TEST_SIMPLE_BUILDING_ID, new Index(200, 200), new Id(1, Id.NO_ID), botBase);
        SyncBaseItem botItem2State1 = EasyMock.createStrictMock(SyncBaseItem.class);
        botItem2State1.setBuildup(1.0);
        EasyMock.expect(botItem2State1.isAlive()).andReturn(true);
        EasyMock.expect(botItem2State1.isIdle()).andReturn(true);
        EasyMock.expect(botItem2State1.getId()).andReturn(new Id(2, Id.NO_ID));
        EasyMock.expect(botItem2State1.isAlive()).andReturn(true);
        EasyMock.expect(botItem2State1.isIdle()).andReturn(true);
        EasyMock.expect(botItem2State1.getId()).andReturn(new Id(2, Id.NO_ID));
        EasyMock.expect(botItem2State1.isAlive()).andReturn(true);
        EasyMock.expect(botItem2State1.isIdle()).andReturn(true);
        EasyMock.expect(botItem2State1.getId()).andReturn(new Id(2, Id.NO_ID));
        EasyMock.expect(botItem2State1.isAlive()).andReturn(true);
        EasyMock.expect(botItem2State1.isIdle()).andReturn(true);
        EasyMock.expect(botItem2State1.getId()).andReturn(new Id(2, Id.NO_ID));
        EasyMock.expect(botItem2State1.isAlive()).andReturn(true);
        EasyMock.expect(botItem2State1.isIdle()).andReturn(true);
        EasyMock.expect(botItem2State1.getId()).andReturn(new Id(2, Id.NO_ID));
        EasyMock.expect(botItem2State1.isAlive()).andReturn(true);
        EasyMock.expect(botItem2State1.isAlive()).andReturn(false);
        EasyMock.expect(botItem2State1.getId()).andReturn(new Id(2, Id.NO_ID));

        SyncBaseItem attacker1 = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(200, 200), new Id(4, Id.NO_ID), actorBase1);
        SyncBaseItem attacker2 = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(200, 200), new Id(5, Id.NO_ID), actorBase2);

        List<BotEnragementStateConfig> botEnragementStateConfigs = new ArrayList<>();
        Collection<BotItemConfig> botItems = new ArrayList<>();
        botItems.add(new BotItemConfig((BaseItemType) serverItemTypeService.getItemType(TEST_SIMPLE_BUILDING_ID), 2, true, createRegion(new Rectangle(0, 0, 1000, 1000), 1), false, null, false, null));
        BotEnragementStateConfig normalState = new BotEnragementStateConfig("NormalTest", botItems, 2);
        botEnragementStateConfigs.add(normalState);
        botItems = new ArrayList<>();
        botItems.add(new BotItemConfig((BaseItemType) serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID), 1, true, createRegion(new Rectangle(0, 0, 1000, 1000), 1), false, null, false, null));
        BotEnragementStateConfig botEnragementStateConfig = new BotEnragementStateConfig("AngryTest", botItems, null);
        botEnragementStateConfigs.add(botEnragementStateConfig);

        TestPlanetServices testServices = new TestPlanetServices();

        BotEnragementState.Listener listener = EasyMock.createStrictMock(BotEnragementState.Listener.class);
        listener.onEnrageUp("TestBot", botEnragementStateConfig, actorBase2);

        BaseService baseServiceMock = EasyMock.createStrictMock(BaseService.class);
        EasyMock.expect(baseServiceMock.getItems(botBase)).andReturn(null);
        EasyMock.expect(baseServiceMock.getItems(botBase)).andReturn(null);
        EasyMock.expect(baseServiceMock.getItems(botBase)).andReturn(null);
        EasyMock.expect(baseServiceMock.getItems(botBase)).andReturn(null);
        EasyMock.expect(baseServiceMock.getItems(botBase)).andReturn(null);
        EasyMock.expect(baseServiceMock.getItems(botBase)).andReturn(null);
        EasyMock.expect(baseServiceMock.getItems(botBase)).andReturn(null);
        testServices.setBaseService(baseServiceMock);

        ServerItemService mockServerItemService = EasyMock.createStrictMock(ServerItemService.class);
        EasyMock.expect(mockServerItemService.createSyncObject(serverItemTypeService.getItemType(TEST_SIMPLE_BUILDING_ID), new Index(200, 200), null, botBase)).andReturn(botItem1State1);
        EasyMock.expect(mockServerItemService.createSyncObject(serverItemTypeService.getItemType(TEST_SIMPLE_BUILDING_ID), new Index(200, 200), null, botBase)).andReturn(botItem2State1);
        EasyMock.expect(mockServerItemService.createSyncObject(serverItemTypeService.getItemType(TEST_SIMPLE_BUILDING_ID), new Index(200, 200), null, botBase)).andReturn(botItem1State1);
        EasyMock.expect(mockServerItemService.createSyncObject(serverItemTypeService.getItemType(TEST_SIMPLE_BUILDING_ID), new Index(200, 200), null, botBase)).andReturn(botItem1State1);
        mockServerItemService.killSyncItem(botItem2State1, null, true, false);
        testServices.setItemService(mockServerItemService);

        CollisionService mockCollisionService = EasyMock.createStrictMock(CollisionService.class);
        Region region1 = createRegion(new Rectangle(0, 0, 1000, 1000), 1);
        EasyMock.expect(mockCollisionService.getFreeRandomPosition(botItem1State1.getBaseItemType(), region1, 0, false, true)).andReturn(new Index(200, 200));
        EasyMock.expect(mockCollisionService.getFreeRandomPosition(botItem1State1.getBaseItemType(), region1, 0, false, true)).andReturn(new Index(200, 200));
        EasyMock.expect(mockCollisionService.getFreeRandomPosition(botItem1State1.getBaseItemType(), region1, 0, false, true)).andReturn(new Index(200, 200));
        EasyMock.expect(mockCollisionService.getFreeRandomPosition(botItem1State1.getBaseItemType(), region1, 0, false, true)).andReturn(new Index(200, 200));
        testServices.setCollisionService(mockCollisionService);

        EasyMock.replay(listener, baseServiceMock, mockServerItemService, mockCollisionService, botItem2State1);

        BotEnragementState botEnragementState = new BotEnragementState(botEnragementStateConfigs, region1, testServices, "TestBot", listener);
        botEnragementState.work(botBase);
        botEnragementState.handleIntruders(Collections.singletonList(attacker1), botBase);
        // Actor 1 is killing
        botEnragementState.onBotItemKilled(botItem1State1, actorBase1);
        botItem1State1.setHealth(0);
        botEnragementState.handleIntruders(Collections.singletonList(attacker1), botBase);
        botEnragementState.work(botBase); // Remove item
        botItem1State1.setHealth(1.0);
        botEnragementState.work(botBase); // Create new item
        // Actor 2 is killing. No rage up
        botEnragementState.handleIntruders(Arrays.asList(attacker1, attacker2), botBase);
        botEnragementState.onBotItemKilled(botItem1State1, actorBase2);
        botItem1State1.setHealth(0);
        botEnragementState.handleIntruders(Arrays.asList(attacker1, attacker2), botBase);
        botEnragementState.work(botBase); // Remove item
        botItem1State1.setHealth(1.0);
        botEnragementState.work(botBase); // Create new item
        // Actor 2 is killing. Rage up
        botEnragementState.handleIntruders(Arrays.asList(attacker1, attacker2), botBase);
        botItem1State1.setHealth(0);
        botEnragementState.onBotItemKilled(botItem1State1, actorBase2);

        EasyMock.verify(listener, baseServiceMock, mockServerItemService, mockCollisionService, botItem2State1);
    }

}
