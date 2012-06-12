package com.btxtech.game.services.bot;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.CommonJava;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.services.bot.impl.BotItemContainer;
import com.btxtech.game.jsre.common.gameengine.services.bot.impl.BotSyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.services.bot.impl.IntruderHandler;
import com.btxtech.game.jsre.common.gameengine.services.bot.impl.ShortestWaySorter;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.TestServices;
import com.btxtech.game.services.action.ActionService;
import com.btxtech.game.services.collision.CollisionService;
import com.btxtech.game.services.item.ItemService;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * User: beat
 * Date: 09.08.2011
 * Time: 20:40:47
 */
public class TestIntruderHandler extends AbstractServiceTest {
    @Autowired
    private CollisionService collisionService;

    @Test
    @DirtiesContext
    public void noIntruders() throws Exception {
        SimpleBase botBase = new SimpleBase(1);
        Rectangle region = new Rectangle(0, 0, 2000, 2000);

        BotItemContainer mockBotItemContainer = EasyMock.createStrictMock(BotItemContainer.class);
        ItemService mockItemService = EasyMock.createStrictMock(ItemService.class);
        EasyMock.expect(mockItemService.getEnemyItems(botBase, region)).andReturn(Collections.<SyncBaseItem>emptyList());

        TestServices testServices = new TestServices();
        testServices.setItemService(mockItemService);

        IntruderHandler intruderHandler = new IntruderHandler(mockBotItemContainer, region, testServices);

        EasyMock.replay(mockBotItemContainer);
        EasyMock.replay(mockItemService);
        intruderHandler.handleIntruders(botBase);
        EasyMock.verify(mockBotItemContainer);
        EasyMock.verify(mockItemService);
    }

    @Test
    @DirtiesContext
    public void oneIntrudersNoDefender() throws Exception {
        configureRealGame();

        SimpleBase botBase = new SimpleBase(1);
        Rectangle region = new Rectangle(0, 0, 2000, 2000);
        SyncBaseItem intruder = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(1000, 1000), new Id(-1, -1, 0));

        BotItemContainer mockBotItemContainer = EasyMock.createStrictMock(BotItemContainer.class);
        EasyMock.expect(mockBotItemContainer.getAllIdleAttackers()).andReturn(Collections.<BotSyncBaseItem>emptyList()).times(3);

        ItemService mockItemService = EasyMock.createStrictMock(ItemService.class);
        EasyMock.expect(mockItemService.getEnemyItems(botBase, region)).andReturn(Collections.<SyncBaseItem>singletonList(intruder)).times(3);

        TestServices testServices = new TestServices();
        testServices.setItemService(mockItemService);

        IntruderHandler intruderHandler = new IntruderHandler(mockBotItemContainer, region, testServices);

        EasyMock.replay(mockBotItemContainer);
        EasyMock.replay(mockItemService);
        intruderHandler.handleIntruders(botBase);
        intruderHandler.handleIntruders(botBase);
        intruderHandler.handleIntruders(botBase);
        EasyMock.verify(mockBotItemContainer);
        EasyMock.verify(mockItemService);
    }

    @Test
    @DirtiesContext
    public void oneIntruders() throws Exception {
        configureRealGame();

        SimpleBase botBase = new SimpleBase(1);
        Rectangle region = new Rectangle(0, 0, 2000, 2000);
        SyncBaseItem intruder = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(1000, 1000), new Id(-1, -1, 0));
        SyncBaseItem defender = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(200, 200), new Id(-2, -2, 0));

        ActionService mockActionService = EasyMock.createStrictMock(ActionService.class);
        mockActionService.attack(EasyMock.eq(defender), EasyMock.eq(intruder), EasyMock.eq(new Index(1001, 822)), EasyMock.eq(3.141592653589793, 0.1), EasyMock.eq(true));

        ItemService mockItemService = EasyMock.createStrictMock(ItemService.class);
        EasyMock.expect(mockItemService.getEnemyItems(botBase, region)).andReturn(Collections.<SyncBaseItem>singletonList(intruder));

        TestServices testServices = new TestServices();
        testServices.setItemService(mockItemService);
        testServices.setCollisionService(collisionService);
        testServices.setActionService(mockActionService);

        BotItemContainer mockBotItemContainer = EasyMock.createStrictMock(BotItemContainer.class);
        BotSyncBaseItem defenderBotItem = new BotSyncBaseItem(defender, testServices);
        EasyMock.expect(mockBotItemContainer.getAllIdleAttackers()).andReturn(Collections.singleton(defenderBotItem));

        IntruderHandler intruderHandler = new IntruderHandler(mockBotItemContainer, region, testServices);

        EasyMock.replay(mockBotItemContainer);
        EasyMock.replay(mockItemService);
        EasyMock.replay(mockActionService);
        intruderHandler.handleIntruders(botBase);
        EasyMock.verify(mockBotItemContainer);
        EasyMock.verify(mockItemService);
        EasyMock.verify(mockActionService);
    }

    @Test
    @DirtiesContext
    public void oneIntruderAndGone() throws Exception {
        configureRealGame();

        SimpleBase botBase = new SimpleBase(1);
        Rectangle region = new Rectangle(0, 0, 2000, 2000);
        SyncBaseItem intruder = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(1000, 1000), new Id(-1, -1, 0));
        SyncBaseItem defender = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(200, 200), new Id(-2, -2, 0));

        TestServices testServices = new TestServices();

        ActionService mockActionService = EasyMock.createStrictMock(ActionService.class);
        testServices.setActionService(mockActionService);
        mockActionService.attack(EasyMock.eq(defender), EasyMock.eq(intruder), EasyMock.eq(new Index(1001, 822)), EasyMock.eq(3.141592653589793, 0.1), EasyMock.eq(true));

        BotItemContainer mockBotItemContainer = EasyMock.createStrictMock(BotItemContainer.class);
        BotSyncBaseItem defenderBotItem = new BotSyncBaseItem(defender, testServices);
        EasyMock.expect(mockBotItemContainer.getAllIdleAttackers()).andReturn(Collections.singleton(defenderBotItem));

        ItemService mockItemService = EasyMock.createStrictMock(ItemService.class);
        EasyMock.expect(mockItemService.getEnemyItems(botBase, region)).andReturn(Collections.<SyncBaseItem>singletonList(intruder));
        EasyMock.expect(mockItemService.getEnemyItems(botBase, region)).andReturn(Collections.<SyncBaseItem>emptyList());

        testServices.setItemService(mockItemService);
        testServices.setCollisionService(collisionService);

        IntruderHandler intruderHandler = new IntruderHandler(mockBotItemContainer, region, testServices);

        EasyMock.replay(mockBotItemContainer);
        EasyMock.replay(mockItemService);
        EasyMock.replay(mockActionService);
        intruderHandler.handleIntruders(botBase);
        intruderHandler.handleIntruders(botBase);
        EasyMock.verify(mockBotItemContainer);
        EasyMock.verify(mockItemService);
        EasyMock.verify(mockActionService);
    }

    @Test
    @DirtiesContext
    public void oneIntruderAndAttackerDies() throws Exception {
        configureRealGame();

        SimpleBase botBase = new SimpleBase(1);
        Rectangle region = new Rectangle(0, 0, 2000, 2000);
        SyncBaseItem intruder = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(1000, 1000), new Id(-1, -1, 0));
        SyncBaseItem defender = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(200, 200), new Id(-2, -2, 0));

        TestServices testServices = new TestServices();

        ActionService mockActionService = EasyMock.createStrictMock(ActionService.class);
        mockActionService.attack(EasyMock.eq(defender), EasyMock.eq(intruder), EasyMock.eq(new Index(1001, 822)), EasyMock.eq(3.141592653589793, 0.1), EasyMock.eq(true));
        testServices.setActionService(mockActionService);

        BotItemContainer mockBotItemContainer = EasyMock.createStrictMock(BotItemContainer.class);
        BotSyncBaseItem defenderBotItem = new BotSyncBaseItem(defender, testServices);
        EasyMock.expect(mockBotItemContainer.getAllIdleAttackers()).andReturn(Collections.singleton(defenderBotItem));
        EasyMock.expect(mockBotItemContainer.getAllIdleAttackers()).andReturn(Collections.<BotSyncBaseItem>emptyList());

        ItemService mockItemService = EasyMock.createStrictMock(ItemService.class);
        EasyMock.expect(mockItemService.getEnemyItems(botBase, region)).andReturn(Collections.<SyncBaseItem>singletonList(intruder));
        EasyMock.expect(mockItemService.getEnemyItems(botBase, region)).andReturn(Collections.<SyncBaseItem>singletonList(intruder));

        testServices.setItemService(mockItemService);
        testServices.setCollisionService(collisionService);

        IntruderHandler intruderHandler = new IntruderHandler(mockBotItemContainer, region, testServices);

        EasyMock.replay(mockBotItemContainer);
        EasyMock.replay(mockItemService);
        EasyMock.replay(mockActionService);
        intruderHandler.handleIntruders(botBase);
        defender.setHealth(0);
        intruderHandler.handleIntruders(botBase);
        EasyMock.verify(mockBotItemContainer);
        EasyMock.verify(mockItemService);
        EasyMock.verify(mockActionService);
    }

    @Test
    @DirtiesContext
    public void oneIntruderAndAttackerBecomesIdle() throws Exception {
        configureRealGame();

        SimpleBase botBase = new SimpleBase(1);
        Rectangle region = new Rectangle(0, 0, 2000, 2000);
        SyncBaseItem intruder = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(1000, 1000), new Id(-1, -1, 0));
        SyncBaseItem attacker = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(200, 200), new Id(-2, -2, 0));

        TestServices testServices = new TestServices();

        ActionService mockActionService = EasyMock.createStrictMock(ActionService.class);
        mockActionService.attack(EasyMock.eq(attacker), EasyMock.eq(intruder), EasyMock.eq(new Index(1001, 822)), EasyMock.eq(3.141592653589793, 0.1), EasyMock.eq(true));
        mockActionService.attack(EasyMock.eq(attacker), EasyMock.eq(intruder), EasyMock.eq(new Index(1001, 822)), EasyMock.eq(3.141592653589793, 0.1), EasyMock.eq(true));
        testServices.setActionService(mockActionService);

        BotItemContainer mockBotItemContainer = EasyMock.createStrictMock(BotItemContainer.class);
        BotSyncBaseItem attackerBotItem = new BotSyncBaseItem(attacker, testServices);
        EasyMock.expect(mockBotItemContainer.getAllIdleAttackers()).andReturn(Collections.singleton(attackerBotItem));
        EasyMock.expect(mockBotItemContainer.getAllIdleAttackers()).andReturn(Collections.singleton(attackerBotItem));

        ItemService mockItemService = EasyMock.createStrictMock(ItemService.class);
        EasyMock.expect(mockItemService.getEnemyItems(botBase, region)).andReturn(Collections.<SyncBaseItem>singletonList(intruder));
        EasyMock.expect(mockItemService.getEnemyItems(botBase, region)).andReturn(Collections.<SyncBaseItem>singletonList(intruder));

        testServices.setItemService(mockItemService);
        testServices.setCollisionService(collisionService);

        IntruderHandler intruderHandler = new IntruderHandler(mockBotItemContainer, region, testServices);

        EasyMock.replay(mockBotItemContainer);
        EasyMock.replay(mockItemService);
        EasyMock.replay(mockActionService);
        intruderHandler.handleIntruders(botBase);
        setPrivateField(BotSyncBaseItem.class, attackerBotItem, "idle", true);
        intruderHandler.handleIntruders(botBase);
        EasyMock.verify(mockBotItemContainer);
        EasyMock.verify(mockItemService);
        EasyMock.verify(mockActionService);
    }

    @Test
    @DirtiesContext
    public void oneIntruderAndAttackerException() throws Exception {
        configureRealGame();

        SimpleBase botBase = new SimpleBase(1);
        Rectangle region = new Rectangle(0, 0, 2000, 2000);
        SyncBaseItem intruder = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(1000, 1000), new Id(-1, -1, 0));
        SyncBaseItem attacker = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(200, 200), new Id(-2, -2, 0));

        TestServices testServices = new TestServices();

        ActionService mockActionService = EasyMock.createStrictMock(ActionService.class);
        mockActionService.attack(EasyMock.eq(attacker), EasyMock.eq(intruder), EasyMock.eq(new Index(1001, 822)), EasyMock.eq(3.141592653589793, 0.1), EasyMock.eq(true));
        //noinspection ThrowableInstanceNeverThrown
        EasyMock.expectLastCall().andThrow(new RuntimeException());
        mockActionService.attack(EasyMock.eq(attacker), EasyMock.eq(intruder), EasyMock.eq(new Index(1001, 822)), EasyMock.eq(3.141592653589793, 0.1), EasyMock.eq(true));
        testServices.setActionService(mockActionService);

        BotItemContainer mockBotItemContainer = EasyMock.createStrictMock(BotItemContainer.class);
        BotSyncBaseItem attackerBotItem = new BotSyncBaseItem(attacker, testServices);
        EasyMock.expect(mockBotItemContainer.getAllIdleAttackers()).andReturn(Collections.singleton(attackerBotItem));
        EasyMock.expect(mockBotItemContainer.getAllIdleAttackers()).andReturn(Collections.singleton(attackerBotItem));

        ItemService mockItemService = EasyMock.createStrictMock(ItemService.class);
        EasyMock.expect(mockItemService.getEnemyItems(botBase, region)).andReturn(Collections.<SyncBaseItem>singletonList(intruder));
        EasyMock.expect(mockItemService.getEnemyItems(botBase, region)).andReturn(Collections.<SyncBaseItem>singletonList(intruder));

        testServices.setItemService(mockItemService);
        testServices.setCollisionService(collisionService);

        IntruderHandler intruderHandler = new IntruderHandler(mockBotItemContainer, region, testServices);

        EasyMock.replay(mockBotItemContainer);
        EasyMock.replay(mockItemService);
        EasyMock.replay(mockActionService);
        intruderHandler.handleIntruders(botBase);
        intruderHandler.handleIntruders(botBase);
        EasyMock.verify(mockBotItemContainer);
        EasyMock.verify(mockItemService);
        EasyMock.verify(mockActionService);
    }

    @Test
    @DirtiesContext
    public void twoIntruderTakeNextEnemy() throws Exception {
        configureRealGame();

        SimpleBase botBase = new SimpleBase(1);
        Rectangle region = new Rectangle(0, 0, 2000, 2000);
        SyncBaseItem intruder1 = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(1000, 1000), new Id(-1, -1, 0));
        SyncBaseItem intruder2 = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(1000, 1500), new Id(-2, -1, 0));
        SyncBaseItem attacker = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(200, 200), new Id(-3, -2, 0));

        TestServices testServices = new TestServices();

        ActionService mockActionService = EasyMock.createStrictMock(ActionService.class);
        mockActionService.attack(EasyMock.eq(attacker), EasyMock.eq(intruder1), EasyMock.eq(new Index(1001, 822)), EasyMock.eq(3.141592653589793, 0.1), EasyMock.eq(true));
        //noinspection ThrowableInstanceNeverThrown
        EasyMock.expectLastCall().andThrow(new RuntimeException());
        mockActionService.attack(EasyMock.eq(attacker), EasyMock.eq(intruder1), EasyMock.eq(new Index(1001, 822)), EasyMock.eq(3.141592653589793, 0.1), EasyMock.eq(true));
        testServices.setActionService(mockActionService);

        BotItemContainer mockBotItemContainer = EasyMock.createStrictMock(BotItemContainer.class);
        BotSyncBaseItem attackerBotItem = new BotSyncBaseItem(attacker, testServices);
        EasyMock.expect(mockBotItemContainer.getAllIdleAttackers()).andReturn(Collections.singleton(attackerBotItem));
        EasyMock.expect(mockBotItemContainer.getAllIdleAttackers()).andReturn(Collections.singleton(attackerBotItem));

        ItemService mockItemService = EasyMock.createStrictMock(ItemService.class);
        EasyMock.expect(mockItemService.getEnemyItems(botBase, region)).andReturn(Arrays.asList(intruder2, intruder1));
        EasyMock.expect(mockItemService.getEnemyItems(botBase, region)).andReturn(Collections.<SyncBaseItem>singletonList(intruder1));

        testServices.setItemService(mockItemService);
        testServices.setCollisionService(collisionService);

        IntruderHandler intruderHandler = new IntruderHandler(mockBotItemContainer, region, testServices);

        EasyMock.replay(mockBotItemContainer);
        EasyMock.replay(mockItemService);
        EasyMock.replay(mockActionService);
        intruderHandler.handleIntruders(botBase);
        intruderHandler.handleIntruders(botBase);
        EasyMock.verify(mockBotItemContainer);
        EasyMock.verify(mockItemService);
        EasyMock.verify(mockActionService);
    }

    @Test
    @DirtiesContext
    public void shortestWaySorter() throws Exception {
        configureRealGame();

        // Intruders
        SyncBaseItem intruder1 = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(1000, 1200), new Id(-1, -1, 0));
        SyncBaseItem intruder2 = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(1000, 1400), new Id(-2, -1, 0));
        SyncBaseItem intruder3 = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(1000, 1600), new Id(-3, -1, 0));
        SyncBaseItem intruder4 = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(1000, 1800), new Id(-4, -1, 0));
        SyncBaseItem intruder5 = createSyncBaseItem(TEST_ATTACK_ITEM_ID_2, new Index(1000, 2000), new Id(-5, -1, 0));

        // Attackers
        SyncBaseItem attacker1 = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(2000, 1200), new Id(-6, -2, 0));
        BotSyncBaseItem attackerBotItem1 = new BotSyncBaseItem(attacker1, null);
        SyncBaseItem attacker2 = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(2000, 1400), new Id(-7, -2, 0));
        BotSyncBaseItem attackerBotItem2 = new BotSyncBaseItem(attacker2, null);
        SyncBaseItem attacker3 = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(2000, 1600), new Id(-8, -2, 0));
        BotSyncBaseItem attackerBotItem3 = new BotSyncBaseItem(attacker3, null);
        SyncBaseItem attacker4 = createSyncBaseItem(TEST_ATTACK_ITEM_ID_2, new Index(2000, 1800), new Id(-9, -2, 0));
        BotSyncBaseItem attackerBotItem4 = new BotSyncBaseItem(attacker4, null);
        SyncBaseItem attacker5 = createSyncBaseItem(TEST_ATTACK_ITEM_ID_2, new Index(2000, 2000), new Id(-10, -2, 0));
        BotSyncBaseItem attackerBotItem5 = new BotSyncBaseItem(attacker5, null);

        Collection<SyncBaseItem> intruders;
        Collection<BotSyncBaseItem> attackers;
        Map<BotSyncBaseItem, SyncBaseItem> sorted;

        intruders = new ArrayList<SyncBaseItem>();
        intruders.add(intruder1);
        attackers = new ArrayList<BotSyncBaseItem>();
        attackers.add(attackerBotItem1);
        sorted = ShortestWaySorter.setupAttackerTarget(attackers, intruders);
        Assert.assertEquals(1, sorted.size());
        Assert.assertEquals(attackerBotItem1, CommonJava.getFirst(sorted.entrySet()).getKey());
        Assert.assertEquals(intruder1, CommonJava.getFirst(sorted.entrySet()).getValue());

        intruders = new ArrayList<SyncBaseItem>();
        intruders.add(intruder1);
        intruders.add(intruder2);
        attackers = new ArrayList<BotSyncBaseItem>();
        attackers.add(attackerBotItem1);
        sorted = ShortestWaySorter.setupAttackerTarget(attackers, intruders);
        Assert.assertEquals(1, sorted.size());
        Assert.assertEquals(attackerBotItem1, CommonJava.getFirst(sorted.entrySet()).getKey());
        Assert.assertEquals(intruder1, CommonJava.getFirst(sorted.entrySet()).getValue());

        intruders = new ArrayList<SyncBaseItem>();
        intruders.add(intruder1);
        intruders.add(intruder2);
        attackers = new ArrayList<BotSyncBaseItem>();
        attackers.add(attackerBotItem1);
        attackers.add(attackerBotItem2);
        sorted = ShortestWaySorter.setupAttackerTarget(attackers, intruders);
        Assert.assertEquals(2, sorted.size());
        assertShortestWaySorter(sorted, attackerBotItem1, intruder1);
        assertShortestWaySorter(sorted, attackerBotItem2, intruder2);

        intruders = new ArrayList<SyncBaseItem>();
        intruders.add(intruder1);
        intruders.add(intruder3);
        attackers = new ArrayList<BotSyncBaseItem>();
        attackers.add(attackerBotItem1);
        attackers.add(attackerBotItem2);
        sorted = ShortestWaySorter.setupAttackerTarget(attackers, intruders);
        Assert.assertEquals(2, sorted.size());
        assertShortestWaySorter(sorted, attackerBotItem1, intruder1);
        assertShortestWaySorter(sorted, attackerBotItem2, intruder3);

        intruders = new ArrayList<SyncBaseItem>();
        intruders.add(intruder1);
        intruders.add(intruder2);
        intruders.add(intruder3);
        attackers = new ArrayList<BotSyncBaseItem>();
        attackers.add(attackerBotItem1);
        attackers.add(attackerBotItem2);
        sorted = ShortestWaySorter.setupAttackerTarget(attackers, intruders);
        Assert.assertEquals(2, sorted.size());
        assertShortestWaySorter(sorted, attackerBotItem1, intruder1);
        assertShortestWaySorter(sorted, attackerBotItem2, intruder2);

        intruders = new ArrayList<SyncBaseItem>();
        intruders.add(intruder1);
        intruders.add(intruder3);
        attackers = new ArrayList<BotSyncBaseItem>();
        attackers.add(attackerBotItem1);
        attackers.add(attackerBotItem2);
        attackers.add(attackerBotItem3);
        sorted = ShortestWaySorter.setupAttackerTarget(attackers, intruders);
        Assert.assertEquals(2, sorted.size());
        assertShortestWaySorter(sorted, attackerBotItem1, intruder1);
        assertShortestWaySorter(sorted, attackerBotItem3, intruder3);

        intruders = new ArrayList<SyncBaseItem>();
        intruders.add(intruder1);
        intruders.add(intruder2);
        intruders.add(intruder3);
        attackers = new ArrayList<BotSyncBaseItem>();
        attackers.add(attackerBotItem1);
        attackers.add(attackerBotItem2);
        attackers.add(attackerBotItem3);
        sorted = ShortestWaySorter.setupAttackerTarget(attackers, intruders);
        Assert.assertEquals(3, sorted.size());
        assertShortestWaySorter(sorted, attackerBotItem1, intruder1);
        assertShortestWaySorter(sorted, attackerBotItem2, intruder2);
        assertShortestWaySorter(sorted, attackerBotItem3, intruder3);

        intruders = new ArrayList<SyncBaseItem>();
        intruders.add(intruder1);
        intruders.add(intruder2);
        intruders.add(intruder3);
        intruders.add(intruder4);
        attackers = new ArrayList<BotSyncBaseItem>();
        attackers.add(attackerBotItem1);
        sorted = ShortestWaySorter.setupAttackerTarget(attackers, intruders);
        Assert.assertEquals(1, sorted.size());
        assertShortestWaySorter(sorted, attackerBotItem1, intruder1);

        intruders = new ArrayList<SyncBaseItem>();
        intruders.add(intruder4);
        attackers = new ArrayList<BotSyncBaseItem>();
        attackers.add(attackerBotItem1);
        attackers.add(attackerBotItem2);
        attackers.add(attackerBotItem3);
        attackers.add(attackerBotItem4);
        sorted = ShortestWaySorter.setupAttackerTarget(attackers, intruders);
        Assert.assertEquals(1, sorted.size());
        assertShortestWaySorter(sorted, attackerBotItem3, intruder4);

        intruders = new ArrayList<SyncBaseItem>();
        intruders.add(intruder1);
        intruders.add(intruder5);
        attackers = new ArrayList<BotSyncBaseItem>();
        attackers.add(attackerBotItem1);
        attackers.add(attackerBotItem4);
        sorted = ShortestWaySorter.setupAttackerTarget(attackers, intruders);
        Assert.assertEquals(2, sorted.size());
        assertShortestWaySorter(sorted, attackerBotItem1, intruder1);
        assertShortestWaySorter(sorted, attackerBotItem4, intruder5);

        intruders = new ArrayList<SyncBaseItem>();
        intruders.add(intruder1);
        intruders.add(intruder2);
        intruders.add(intruder3);
        intruders.add(intruder4);
        intruders.add(intruder5);
        attackers = new ArrayList<BotSyncBaseItem>();
        attackers.add(attackerBotItem1);
        attackers.add(attackerBotItem2);
        attackers.add(attackerBotItem3);
        attackers.add(attackerBotItem4);
        attackers.add(attackerBotItem5);
        sorted = ShortestWaySorter.setupAttackerTarget(attackers, intruders);
        Assert.assertEquals(4, sorted.size());
        assertShortestWaySorter(sorted, attackerBotItem1, intruder1);
        assertShortestWaySorter(sorted, attackerBotItem2, intruder2);
        assertShortestWaySorter(sorted, attackerBotItem3, intruder3);
        assertShortestWaySorter(sorted, attackerBotItem5, intruder5);

        intruders = new ArrayList<SyncBaseItem>();
        intruders.add(intruder1);
        intruders.add(intruder2);
        intruders.add(intruder3);
        intruders.add(intruder4);
        attackers = new ArrayList<BotSyncBaseItem>();
        attackers.add(attackerBotItem4);
        attackers.add(attackerBotItem5);
        sorted = ShortestWaySorter.setupAttackerTarget(attackers, intruders);
        Assert.assertEquals(0, sorted.size());
    }

    private void assertShortestWaySorter(Map<BotSyncBaseItem, SyncBaseItem> sorted, BotSyncBaseItem attacker, SyncBaseItem intruder) {
        SyncBaseItem syncBaseItem = sorted.get(attacker);
        Assert.assertNotNull("Attacker is not in the map: " + attacker, syncBaseItem);
        Assert.assertEquals(intruder, syncBaseItem);
    }
}
