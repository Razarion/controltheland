package com.btxtech.game.services.bot;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotEnragementStateConfig;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotItemConfig;
import com.btxtech.game.jsre.common.gameengine.services.bot.impl.BotEnragementState;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.TestServices;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.collision.CollisionService;
import com.btxtech.game.services.item.ItemService;
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
    private ItemService itemService;

    @Test
    @DirtiesContext
    public void noEnrageUp() throws Exception {
        configureRealGame();

        SimpleBase botBase = new SimpleBase(1);
        SimpleBase actorBase = new SimpleBase(2);
        SyncBaseItem botItem1 = createSyncBaseItem(TEST_SIMPLE_BUILDING_ID, new Index(200, 200), new Id(1, Id.NO_ID, 0), botBase);

        Collection<BotItemConfig> botItems = new ArrayList<>();
        botItems.add(new BotItemConfig((BaseItemType) itemService.getItemType(TEST_SIMPLE_BUILDING_ID), 1, true, new Rectangle(0, 0, 1000, 1000), false, null, false, null));

        List<BotEnragementStateConfig> botEnragementStateConfigs = new ArrayList<>();
        botEnragementStateConfigs.add(new BotEnragementStateConfig("NormalTest", botItems, null));

        TestServices testServices = new TestServices();

        BotEnragementState.Listener listener = EasyMock.createStrictMock(BotEnragementState.Listener.class);

        BaseService baseServiceMock = EasyMock.createStrictMock(BaseService.class);
        EasyMock.expect(baseServiceMock.getItems(botBase)).andReturn(null);
        testServices.setBaseService(baseServiceMock);

        ItemService mockItemService = EasyMock.createStrictMock(ItemService.class);
        EasyMock.expect(mockItemService.createSyncObject(itemService.getItemType(TEST_SIMPLE_BUILDING_ID), new Index(200, 200), null, botBase, 0)).andReturn(botItem1);
        testServices.setItemService(mockItemService);

        CollisionService mockCollisionService = EasyMock.createStrictMock(CollisionService.class);
        EasyMock.expect(mockCollisionService.getFreeRandomPosition(botItem1.getBaseItemType(), new Rectangle(0, 0, 1000, 1000), 0, false, true)).andReturn(new Index(200, 200));
        testServices.setCollisionService(mockCollisionService);

        EasyMock.replay(listener, baseServiceMock, mockItemService, mockCollisionService);

        BotEnragementState botEnragementState = new BotEnragementState(botEnragementStateConfigs, new Rectangle(0, 0, 1000, 1000), testServices, "TestBot", listener);
        botEnragementState.work(botBase);
        botEnragementState.onBotItemKilled(botItem1, actorBase);

        EasyMock.verify(listener, baseServiceMock, mockItemService, mockCollisionService);
    }

    @Test
    @DirtiesContext
    public void oneEnrageUp() throws Exception {
        configureRealGame();

        SimpleBase botBase = new SimpleBase(1);
        SimpleBase actorBase = new SimpleBase(2);
        SyncBaseItem botItem1State1 = createSyncBaseItem(TEST_SIMPLE_BUILDING_ID, new Index(200, 200), new Id(1, Id.NO_ID, 0), botBase);
        SyncBaseItem botItem2State1 = createSyncBaseItem(TEST_SIMPLE_BUILDING_ID, new Index(200, 200), new Id(2, Id.NO_ID, 0), botBase);
        SyncBaseItem botItem3State2 = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(200, 200), new Id(3, Id.NO_ID, 0), botBase);
        SyncBaseItem attacker = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(200, 200), new Id(4, Id.NO_ID, 0), actorBase);

        List<BotEnragementStateConfig> botEnragementStateConfigs = new ArrayList<>();
        Collection<BotItemConfig> botItems = new ArrayList<>();
        botItems.add(new BotItemConfig((BaseItemType) itemService.getItemType(TEST_SIMPLE_BUILDING_ID), 2, true, new Rectangle(0, 0, 1000, 1000), false, null, false, null));
        BotEnragementStateConfig normalState = new BotEnragementStateConfig("NormalTest", botItems, 2);
        botEnragementStateConfigs.add(normalState);
        botItems = new ArrayList<>();
        botItems.add(new BotItemConfig((BaseItemType) itemService.getItemType(TEST_ATTACK_ITEM_ID), 1, true, new Rectangle(0, 0, 1000, 1000), false, null, false, null));
        BotEnragementStateConfig botEnragementStateConfig = new BotEnragementStateConfig("AngryTest", botItems, null);
        botEnragementStateConfigs.add(botEnragementStateConfig);

        TestServices testServices = new TestServices();

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
        testServices.setBaseService(baseServiceMock);

        ItemService mockItemService = EasyMock.createStrictMock(ItemService.class);
        EasyMock.expect(mockItemService.createSyncObject(itemService.getItemType(TEST_SIMPLE_BUILDING_ID), new Index(200, 200), null, botBase, 0)).andReturn(botItem1State1);
        EasyMock.expect(mockItemService.createSyncObject(itemService.getItemType(TEST_SIMPLE_BUILDING_ID), new Index(200, 200), null, botBase, 0)).andReturn(botItem2State1);
        EasyMock.expect(mockItemService.createSyncObject(itemService.getItemType(TEST_SIMPLE_BUILDING_ID), new Index(200, 200), null, botBase, 0)).andReturn(botItem1State1);
        mockItemService.killSyncItem(botItem2State1, null, true, false);
        EasyMock.expect(mockItemService.createSyncObject(itemService.getItemType(TEST_ATTACK_ITEM_ID), new Index(200, 200), null, botBase, 0)).andReturn(botItem3State2);
        mockItemService.killSyncItem(botItem3State2, null, true, false);
        EasyMock.expect(mockItemService.createSyncObject(itemService.getItemType(TEST_SIMPLE_BUILDING_ID), new Index(200, 200), null, botBase, 0)).andReturn(botItem1State1);
        EasyMock.expect(mockItemService.createSyncObject(itemService.getItemType(TEST_SIMPLE_BUILDING_ID), new Index(200, 200), null, botBase, 0)).andReturn(botItem2State1);
        testServices.setItemService(mockItemService);

        CollisionService mockCollisionService = EasyMock.createStrictMock(CollisionService.class);
        EasyMock.expect(mockCollisionService.getFreeRandomPosition(botItem1State1.getBaseItemType(), new Rectangle(0, 0, 1000, 1000), 0, false, true)).andReturn(new Index(200, 200));
        EasyMock.expect(mockCollisionService.getFreeRandomPosition(botItem1State1.getBaseItemType(), new Rectangle(0, 0, 1000, 1000), 0, false, true)).andReturn(new Index(200, 200));
        EasyMock.expect(mockCollisionService.getFreeRandomPosition(botItem1State1.getBaseItemType(), new Rectangle(0, 0, 1000, 1000), 0, false, true)).andReturn(new Index(200, 200));
        EasyMock.expect(mockCollisionService.getFreeRandomPosition(botItem3State2.getBaseItemType(), new Rectangle(0, 0, 1000, 1000), 0, false, true)).andReturn(new Index(200, 200));
        EasyMock.expect(mockCollisionService.getFreeRandomPosition(botItem1State1.getBaseItemType(), new Rectangle(0, 0, 1000, 1000), 0, false, true)).andReturn(new Index(200, 200));
        EasyMock.expect(mockCollisionService.getFreeRandomPosition(botItem1State1.getBaseItemType(), new Rectangle(0, 0, 1000, 1000), 0, false, true)).andReturn(new Index(200, 200));
        testServices.setCollisionService(mockCollisionService);

        EasyMock.replay(listener, baseServiceMock, mockItemService, mockCollisionService);

        BotEnragementState botEnragementState = new BotEnragementState(botEnragementStateConfigs, new Rectangle(0, 0, 1000, 1000), testServices, "TestBot", listener);
        botEnragementState.work(botBase);
        botEnragementState.handleIntruders(Collections.singletonList(attacker), botBase);
        botEnragementState.onBotItemKilled(botItem1State1, actorBase);
        botItem1State1.setHealth(0);
        botEnragementState.handleIntruders(Collections.singletonList(attacker),botBase);
        botEnragementState.work(botBase); // Remove item
        botItem1State1.setHealth(1.0);
        botEnragementState.work(botBase); // Create new item
        botItem1State1.setHealth(0);
        botEnragementState.onBotItemKilled(botItem1State1, actorBase);
        // Next enrage state reached
        botEnragementState.handleIntruders(Collections.singletonList(attacker),botBase);
        botEnragementState.work(botBase);
        botEnragementState.handleIntruders(Collections.<SyncBaseItem>emptyList(),botBase);
        // Normal enrage reached
        botEnragementState.work(botBase);

        EasyMock.verify(listener, baseServiceMock, mockItemService, mockCollisionService);
    }

    @Test
    @DirtiesContext
    public void realmLeft() throws Exception {
        configureRealGame();

        SimpleBase botBase = new SimpleBase(1);
        SimpleBase actorBase = new SimpleBase(2);
        SyncBaseItem botItem1State1 = createSyncBaseItem(TEST_SIMPLE_BUILDING_ID, new Index(200, 200), new Id(1, Id.NO_ID, 0), botBase);
        SyncBaseItem botItem2State1 = createSyncBaseItem(TEST_SIMPLE_BUILDING_ID, new Index(200, 200), new Id(2, Id.NO_ID, 0), botBase);
        SyncBaseItem attacker1 = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(200, 200), new Id(4, Id.NO_ID, 0), actorBase);

        List<BotEnragementStateConfig> botEnragementStateConfigs = new ArrayList<>();
        Collection<BotItemConfig> botItems = new ArrayList<>();
        botItems.add(new BotItemConfig((BaseItemType) itemService.getItemType(TEST_SIMPLE_BUILDING_ID), 2, true, new Rectangle(0, 0, 1000, 1000), false, null, false, null));
        BotEnragementStateConfig normalState = new BotEnragementStateConfig("NormalTest", botItems, 2);
        botEnragementStateConfigs.add(normalState);
        botItems = new ArrayList<>();
        botItems.add(new BotItemConfig((BaseItemType) itemService.getItemType(TEST_ATTACK_ITEM_ID), 1, true, new Rectangle(0, 0, 1000, 1000), false, null, false, null));
        BotEnragementStateConfig botEnragementStateConfig = new BotEnragementStateConfig("AngryTest", botItems, null);
        botEnragementStateConfigs.add(botEnragementStateConfig);

        TestServices testServices = new TestServices();

        BotEnragementState.Listener listener = EasyMock.createStrictMock(BotEnragementState.Listener.class);
        listener.onEnrageUp("TestBot", botEnragementStateConfig, actorBase);

        BaseService baseServiceMock = EasyMock.createStrictMock(BaseService.class);
        EasyMock.expect(baseServiceMock.getItems(botBase)).andReturn(null);
        EasyMock.expect(baseServiceMock.getItems(botBase)).andReturn(null);
        EasyMock.expect(baseServiceMock.getItems(botBase)).andReturn(null);
        EasyMock.expect(baseServiceMock.getItems(botBase)).andReturn(null);
        EasyMock.expect(baseServiceMock.getItems(botBase)).andReturn(null);
        EasyMock.expect(baseServiceMock.getItems(botBase)).andReturn(null);
        testServices.setBaseService(baseServiceMock);

        ItemService mockItemService = EasyMock.createStrictMock(ItemService.class);
        EasyMock.expect(mockItemService.createSyncObject(itemService.getItemType(TEST_SIMPLE_BUILDING_ID), new Index(200, 200), null, botBase, 0)).andReturn(botItem1State1);
        EasyMock.expect(mockItemService.createSyncObject(itemService.getItemType(TEST_SIMPLE_BUILDING_ID), new Index(200, 200), null, botBase, 0)).andReturn(botItem2State1);
        EasyMock.expect(mockItemService.createSyncObject(itemService.getItemType(TEST_SIMPLE_BUILDING_ID), new Index(200, 200), null, botBase, 0)).andReturn(botItem1State1);
        EasyMock.expect(mockItemService.createSyncObject(itemService.getItemType(TEST_SIMPLE_BUILDING_ID), new Index(200, 200), null, botBase, 0)).andReturn(botItem1State1);
        mockItemService.killSyncItem(botItem2State1, null, true, false);
        testServices.setItemService(mockItemService);

        CollisionService mockCollisionService = EasyMock.createStrictMock(CollisionService.class);
        EasyMock.expect(mockCollisionService.getFreeRandomPosition(botItem1State1.getBaseItemType(), new Rectangle(0, 0, 1000, 1000), 0, false, true)).andReturn(new Index(200, 200));
        EasyMock.expect(mockCollisionService.getFreeRandomPosition(botItem1State1.getBaseItemType(), new Rectangle(0, 0, 1000, 1000), 0, false, true)).andReturn(new Index(200, 200));
        EasyMock.expect(mockCollisionService.getFreeRandomPosition(botItem1State1.getBaseItemType(), new Rectangle(0, 0, 1000, 1000), 0, false, true)).andReturn(new Index(200, 200));
        EasyMock.expect(mockCollisionService.getFreeRandomPosition(botItem1State1.getBaseItemType(), new Rectangle(0, 0, 1000, 1000), 0, false, true)).andReturn(new Index(200, 200));
        testServices.setCollisionService(mockCollisionService);

        EasyMock.replay(listener, baseServiceMock, mockItemService, mockCollisionService);

        BotEnragementState botEnragementState = new BotEnragementState(botEnragementStateConfigs, new Rectangle(0, 0, 1000, 1000), testServices, "TestBot", listener);
        botEnragementState.work(botBase);
        botEnragementState.handleIntruders(Collections.singletonList(attacker1),botBase);
        botEnragementState.onBotItemKilled(botItem1State1, actorBase);
        botItem1State1.setHealth(0);
        botEnragementState.handleIntruders(Collections.singletonList(attacker1),botBase);
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

        EasyMock.verify(listener, baseServiceMock, mockItemService, mockCollisionService);
    }

    @Test
    @DirtiesContext
    public void multipleAttackers() throws Exception {
        configureRealGame();

        SimpleBase botBase = new SimpleBase(1);
        SimpleBase actorBase1 = new SimpleBase(2);
        SimpleBase actorBase2 = new SimpleBase(3);
        SyncBaseItem botItem1State1 = createSyncBaseItem(TEST_SIMPLE_BUILDING_ID, new Index(200, 200), new Id(1, Id.NO_ID, 0), botBase);
        SyncBaseItem botItem2State1 = createSyncBaseItem(TEST_SIMPLE_BUILDING_ID, new Index(200, 200), new Id(2, Id.NO_ID, 0), botBase);
        SyncBaseItem attacker1 = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(200, 200), new Id(4, Id.NO_ID, 0), actorBase1);
        SyncBaseItem attacker2 = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(200, 200), new Id(5, Id.NO_ID, 0), actorBase2);

        List<BotEnragementStateConfig> botEnragementStateConfigs = new ArrayList<>();
        Collection<BotItemConfig> botItems = new ArrayList<>();
        botItems.add(new BotItemConfig((BaseItemType) itemService.getItemType(TEST_SIMPLE_BUILDING_ID), 2, true, new Rectangle(0, 0, 1000, 1000), false, null, false, null));
        BotEnragementStateConfig normalState = new BotEnragementStateConfig("NormalTest", botItems, 2);
        botEnragementStateConfigs.add(normalState);
        botItems = new ArrayList<>();
        botItems.add(new BotItemConfig((BaseItemType) itemService.getItemType(TEST_ATTACK_ITEM_ID), 1, true, new Rectangle(0, 0, 1000, 1000), false, null, false, null));
        BotEnragementStateConfig botEnragementStateConfig = new BotEnragementStateConfig("AngryTest", botItems, null);
        botEnragementStateConfigs.add(botEnragementStateConfig);

        TestServices testServices = new TestServices();

        BotEnragementState.Listener listener = EasyMock.createStrictMock(BotEnragementState.Listener.class);
        listener.onEnrageUp("TestBot", botEnragementStateConfig, actorBase2);

        BaseService baseServiceMock = EasyMock.createStrictMock(BaseService.class);
        EasyMock.expect(baseServiceMock.getItems(botBase)).andReturn(null);
        EasyMock.expect(baseServiceMock.getItems(botBase)).andReturn(null);
        EasyMock.expect(baseServiceMock.getItems(botBase)).andReturn(null);
        EasyMock.expect(baseServiceMock.getItems(botBase)).andReturn(null);
        EasyMock.expect(baseServiceMock.getItems(botBase)).andReturn(null);
        EasyMock.expect(baseServiceMock.getItems(botBase)).andReturn(null);
        testServices.setBaseService(baseServiceMock);

        ItemService mockItemService = EasyMock.createStrictMock(ItemService.class);
        EasyMock.expect(mockItemService.createSyncObject(itemService.getItemType(TEST_SIMPLE_BUILDING_ID), new Index(200, 200), null, botBase, 0)).andReturn(botItem1State1);
        EasyMock.expect(mockItemService.createSyncObject(itemService.getItemType(TEST_SIMPLE_BUILDING_ID), new Index(200, 200), null, botBase, 0)).andReturn(botItem2State1);
        EasyMock.expect(mockItemService.createSyncObject(itemService.getItemType(TEST_SIMPLE_BUILDING_ID), new Index(200, 200), null, botBase, 0)).andReturn(botItem1State1);
        EasyMock.expect(mockItemService.createSyncObject(itemService.getItemType(TEST_SIMPLE_BUILDING_ID), new Index(200, 200), null, botBase, 0)).andReturn(botItem1State1);
        mockItemService.killSyncItem(botItem2State1, null, true, false);
        testServices.setItemService(mockItemService);

        CollisionService mockCollisionService = EasyMock.createStrictMock(CollisionService.class);
        EasyMock.expect(mockCollisionService.getFreeRandomPosition(botItem1State1.getBaseItemType(), new Rectangle(0, 0, 1000, 1000), 0, false, true)).andReturn(new Index(200, 200));
        EasyMock.expect(mockCollisionService.getFreeRandomPosition(botItem1State1.getBaseItemType(), new Rectangle(0, 0, 1000, 1000), 0, false, true)).andReturn(new Index(200, 200));
        EasyMock.expect(mockCollisionService.getFreeRandomPosition(botItem1State1.getBaseItemType(), new Rectangle(0, 0, 1000, 1000), 0, false, true)).andReturn(new Index(200, 200));
        EasyMock.expect(mockCollisionService.getFreeRandomPosition(botItem1State1.getBaseItemType(), new Rectangle(0, 0, 1000, 1000), 0, false, true)).andReturn(new Index(200, 200));
        testServices.setCollisionService(mockCollisionService);

        EasyMock.replay(listener, baseServiceMock, mockItemService, mockCollisionService);

        BotEnragementState botEnragementState = new BotEnragementState(botEnragementStateConfigs, new Rectangle(0, 0, 1000, 1000), testServices, "TestBot", listener);
        botEnragementState.work(botBase);
        botEnragementState.handleIntruders(Collections.singletonList(attacker1),botBase);
        // Actor 1 is killing
        botEnragementState.onBotItemKilled(botItem1State1, actorBase1);
        botItem1State1.setHealth(0);
        botEnragementState.handleIntruders(Collections.singletonList(attacker1),botBase);
        botEnragementState.work(botBase); // Remove item
        botItem1State1.setHealth(1.0);
        botEnragementState.work(botBase); // Create new item
        // Actor 2 is killing. No rage up
        botEnragementState.handleIntruders(Arrays.asList(attacker1, attacker2),botBase);
        botEnragementState.onBotItemKilled(botItem1State1, actorBase2);
        botItem1State1.setHealth(0);
        botEnragementState.handleIntruders(Arrays.asList(attacker1, attacker2),botBase);
        botEnragementState.work(botBase); // Remove item
        botItem1State1.setHealth(1.0);
        botEnragementState.work(botBase); // Create new item
        // Actor 2 is killing. Rage up
        botEnragementState.handleIntruders(Arrays.asList(attacker1, attacker2),botBase);
        botItem1State1.setHealth(0);
        botEnragementState.onBotItemKilled(botItem1State1, actorBase2);

        EasyMock.verify(listener, baseServiceMock, mockItemService, mockCollisionService);
    }

}
