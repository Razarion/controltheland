package com.btxtech.game.services.bot;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.CommonJava;
import com.btxtech.game.jsre.common.Region;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.services.bot.impl.BotEnragementState;
import com.btxtech.game.jsre.common.gameengine.services.bot.impl.BotSyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.services.bot.impl.IntruderHandler;
import com.btxtech.game.jsre.common.gameengine.services.bot.impl.ShortestWaySorter;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.TestPlanetServices;
import com.btxtech.game.services.planet.ActionService;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.planet.ServerItemService;
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
    private PlanetSystemService planetSystemService;

    @Test
    @DirtiesContext
    public void noIntruders() throws Exception {
        SimpleBase botBase = new SimpleBase(1, 1);
        Region region = createRegion(new Rectangle(0, 0, 2000, 2000), 1);

        ServerItemService mockServerItemService = EasyMock.createStrictMock(ServerItemService.class);
        EasyMock.expect(mockServerItemService.getEnemyItems(botBase, region)).andReturn(Collections.<SyncBaseItem>emptyList());
        BotEnragementState mockEnragementState = EasyMock.createStrictMock(BotEnragementState.class);
        mockEnragementState.handleIntruders(Collections.<SyncBaseItem>emptyList(), botBase);

        TestPlanetServices testServices = new TestPlanetServices();
        testServices.setItemService(mockServerItemService);

        IntruderHandler intruderHandler = new IntruderHandler(mockEnragementState, region, testServices);

        EasyMock.replay(mockEnragementState);
        EasyMock.replay(mockServerItemService);
        intruderHandler.handleIntruders(botBase);
        EasyMock.verify(mockEnragementState);
        EasyMock.verify(mockServerItemService);
    }

    @Test
    @DirtiesContext
    public void oneIntrudersNoDefender() throws Exception {
        configureSimplePlanetNoResources();

        SimpleBase botBase = new SimpleBase(1, 1);
        Region region = createRegion(new Rectangle(0, 0, 2000, 2000), 1);
        SyncBaseItem intruder = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(1000, 1000), new Id(-1, -1));

        BotEnragementState mockEnragementState = EasyMock.createStrictMock(BotEnragementState.class);
        EasyMock.expect(mockEnragementState.getAllIdleAttackers()).andReturn(Collections.<BotSyncBaseItem>emptyList());
        mockEnragementState.handleIntruders(Collections.<SyncBaseItem>singletonList(intruder), botBase);
        EasyMock.expect(mockEnragementState.getAllIdleAttackers()).andReturn(Collections.<BotSyncBaseItem>emptyList());
        mockEnragementState.handleIntruders(Collections.<SyncBaseItem>singletonList(intruder), botBase);
        EasyMock.expect(mockEnragementState.getAllIdleAttackers()).andReturn(Collections.<BotSyncBaseItem>emptyList());
        mockEnragementState.handleIntruders(Collections.<SyncBaseItem>singletonList(intruder), botBase);

        ServerItemService mockServerItemService = EasyMock.createStrictMock(ServerItemService.class);
        EasyMock.expect(mockServerItemService.getEnemyItems(botBase, region)).andReturn(Collections.<SyncBaseItem>singletonList(intruder)).times(3);

        TestPlanetServices testServices = new TestPlanetServices();
        testServices.setItemService(mockServerItemService);

        IntruderHandler intruderHandler = new IntruderHandler(mockEnragementState, region, testServices);

        EasyMock.replay(mockEnragementState);
        EasyMock.replay(mockServerItemService);
        intruderHandler.handleIntruders(botBase);
        intruderHandler.handleIntruders(botBase);
        intruderHandler.handleIntruders(botBase);
        EasyMock.verify(mockEnragementState);
        EasyMock.verify(mockServerItemService);
    }

    @Test
    @DirtiesContext
    public void oneIntruders() throws Exception {
        configureSimplePlanetNoResources();

        SimpleBase botBase = new SimpleBase(1, 1);
        Region region = createRegion(new Rectangle(0, 0, 2000, 2000), 1);
        SyncBaseItem intruder = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(1000, 1000), new Id(-1, -1));
        SyncBaseItem defender = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(200, 200), new Id(-2, -2));

        ActionService mockActionService = EasyMock.createStrictMock(ActionService.class);
        mockActionService.attack(EasyMock.eq(defender), EasyMock.eq(intruder), EasyMock.eq(new Index(1000, 741)), EasyMock.eq(3.14159265358979, 0.1), EasyMock.eq(true));

        ServerItemService mockServerItemService = EasyMock.createStrictMock(ServerItemService.class);
        EasyMock.expect(mockServerItemService.getEnemyItems(botBase, region)).andReturn(Collections.<SyncBaseItem>singletonList(intruder));

        TestPlanetServices testServices = new TestPlanetServices();
        testServices.setItemService(mockServerItemService);
        testServices.setCollisionService(planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getCollisionService());
        testServices.setActionService(mockActionService);

        BotEnragementState mockEnragementState = EasyMock.createStrictMock(BotEnragementState.class);
        BotSyncBaseItem defenderBotItem = new BotSyncBaseItem(defender, null, testServices);
        EasyMock.expect(mockEnragementState.getAllIdleAttackers()).andReturn(Collections.singleton(defenderBotItem));
        mockEnragementState.handleIntruders(Collections.<SyncBaseItem>singletonList(intruder), botBase);

        IntruderHandler intruderHandler = new IntruderHandler(mockEnragementState, region, testServices);

        EasyMock.replay(mockEnragementState);
        EasyMock.replay(mockServerItemService);
        EasyMock.replay(mockActionService);
        intruderHandler.handleIntruders(botBase);
        EasyMock.verify(mockEnragementState);
        EasyMock.verify(mockServerItemService);
        EasyMock.verify(mockActionService);
    }

    @Test
    @DirtiesContext
    public void oneIntruderAndGone() throws Exception {
        configureSimplePlanetNoResources();

        SimpleBase botBase = new SimpleBase(1, 1);
        Region region = createRegion(new Rectangle(0, 0, 2000, 2000), 1);
        SyncBaseItem intruder = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(1000, 1000), new Id(-1, -1));
        SyncBaseItem defender = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(200, 200), new Id(-2, -2));

        TestPlanetServices testServices = new TestPlanetServices();

        ActionService mockActionService = EasyMock.createStrictMock(ActionService.class);
        testServices.setActionService(mockActionService);
        mockActionService.attack(EasyMock.eq(defender), EasyMock.eq(intruder), EasyMock.eq(new Index(1000, 741)), EasyMock.eq(3.14159265358979, 0.1), EasyMock.eq(true));

        BotEnragementState mockEnragementState = EasyMock.createStrictMock(BotEnragementState.class);
        BotSyncBaseItem defenderBotItem = new BotSyncBaseItem(defender, null, testServices);
        EasyMock.expect(mockEnragementState.getAllIdleAttackers()).andReturn(Collections.singleton(defenderBotItem));
        mockEnragementState.handleIntruders(Collections.<SyncBaseItem>singletonList(intruder), botBase);
        mockEnragementState.handleIntruders(Collections.<SyncBaseItem>emptyList(), botBase);

        ServerItemService mockServerItemService = EasyMock.createStrictMock(ServerItemService.class);
        EasyMock.expect(mockServerItemService.getEnemyItems(botBase, region)).andReturn(Collections.<SyncBaseItem>singletonList(intruder));
        EasyMock.expect(mockServerItemService.getEnemyItems(botBase, region)).andReturn(Collections.<SyncBaseItem>emptyList());

        testServices.setItemService(mockServerItemService);
        testServices.setCollisionService(planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getCollisionService());

        IntruderHandler intruderHandler = new IntruderHandler(mockEnragementState, region, testServices);

        EasyMock.replay(mockEnragementState);
        EasyMock.replay(mockServerItemService);
        EasyMock.replay(mockActionService);
        intruderHandler.handleIntruders(botBase);
        intruderHandler.handleIntruders(botBase);
        EasyMock.verify(mockEnragementState);
        EasyMock.verify(mockServerItemService);
        EasyMock.verify(mockActionService);
    }

    @Test
    @DirtiesContext
    public void oneIntruderAndAttackerDies() throws Exception {
        configureSimplePlanetNoResources();

        SimpleBase botBase = new SimpleBase(1, 1);
        Region region = createRegion(new Rectangle(0, 0, 2000, 2000), 1);
        SyncBaseItem intruder = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(1000, 1000), new Id(-1, -1));
        SyncBaseItem defender = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(200, 200), new Id(-2, -2));

        TestPlanetServices testServices = new TestPlanetServices();

        ActionService mockActionService = EasyMock.createStrictMock(ActionService.class);
        mockActionService.attack(EasyMock.eq(defender), EasyMock.eq(intruder), EasyMock.eq(new Index(1000, 741)), EasyMock.eq(3.14159265358979, 0.1), EasyMock.eq(true));
        testServices.setActionService(mockActionService);

        BotEnragementState mockEnragementState = EasyMock.createStrictMock(BotEnragementState.class);
        BotSyncBaseItem defenderBotItem = new BotSyncBaseItem(defender, null, testServices);
        EasyMock.expect(mockEnragementState.getAllIdleAttackers()).andReturn(Collections.singleton(defenderBotItem));
        mockEnragementState.handleIntruders(Collections.<SyncBaseItem>singletonList(intruder), botBase);
        EasyMock.expect(mockEnragementState.getAllIdleAttackers()).andReturn(Collections.<BotSyncBaseItem>emptyList());
        mockEnragementState.handleIntruders(Collections.<SyncBaseItem>singletonList(intruder), botBase);

        ServerItemService mockServerItemService = EasyMock.createStrictMock(ServerItemService.class);
        EasyMock.expect(mockServerItemService.getEnemyItems(botBase, region)).andReturn(Collections.<SyncBaseItem>singletonList(intruder));
        EasyMock.expect(mockServerItemService.getEnemyItems(botBase, region)).andReturn(Collections.<SyncBaseItem>singletonList(intruder));

        testServices.setItemService(mockServerItemService);
        testServices.setCollisionService(planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getCollisionService());

        IntruderHandler intruderHandler = new IntruderHandler(mockEnragementState, region, testServices);

        EasyMock.replay(mockEnragementState);
        EasyMock.replay(mockServerItemService);
        EasyMock.replay(mockActionService);
        intruderHandler.handleIntruders(botBase);
        defender.setHealth(0);
        intruderHandler.handleIntruders(botBase);
        EasyMock.verify(mockEnragementState);
        EasyMock.verify(mockServerItemService);
        EasyMock.verify(mockActionService);
    }

    @Test
    @DirtiesContext
    public void oneIntruderAndAttackerBecomesIdle() throws Exception {
        configureSimplePlanetNoResources();

        SimpleBase botBase = new SimpleBase(1, 1);
        Region region = createRegion(new Rectangle(0, 0, 2000, 2000), 1);
        SyncBaseItem intruder = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(1000, 1000), new Id(-1, -1));
        SyncBaseItem attacker = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(200, 200), new Id(-2, -2));

        TestPlanetServices testServices = new TestPlanetServices();

        ActionService mockActionService = EasyMock.createStrictMock(ActionService.class);
        mockActionService.attack(EasyMock.eq(attacker), EasyMock.eq(intruder), EasyMock.eq(new Index(1000, 741)), EasyMock.eq(3.14159265358979, 0.1), EasyMock.eq(true));
        mockActionService.attack(EasyMock.eq(attacker), EasyMock.eq(intruder), EasyMock.eq(new Index(1000, 741)), EasyMock.eq(3.14159265358979, 0.1), EasyMock.eq(true));
        testServices.setActionService(mockActionService);

        BotEnragementState mockEnragementState = EasyMock.createStrictMock(BotEnragementState.class);
        BotSyncBaseItem attackerBotItem = new BotSyncBaseItem(attacker, null, testServices);
        EasyMock.expect(mockEnragementState.getAllIdleAttackers()).andReturn(Collections.singleton(attackerBotItem));
        mockEnragementState.handleIntruders(Collections.<SyncBaseItem>singletonList(intruder), botBase);
        EasyMock.expect(mockEnragementState.getAllIdleAttackers()).andReturn(Collections.singleton(attackerBotItem));
        mockEnragementState.handleIntruders(Collections.<SyncBaseItem>singletonList(intruder), botBase);

        ServerItemService mockServerItemService = EasyMock.createStrictMock(ServerItemService.class);
        EasyMock.expect(mockServerItemService.getEnemyItems(botBase, region)).andReturn(Collections.<SyncBaseItem>singletonList(intruder));
        EasyMock.expect(mockServerItemService.getEnemyItems(botBase, region)).andReturn(Collections.<SyncBaseItem>singletonList(intruder));

        testServices.setItemService(mockServerItemService);
        testServices.setCollisionService(planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getCollisionService());

        IntruderHandler intruderHandler = new IntruderHandler(mockEnragementState, region, testServices);

        EasyMock.replay(mockEnragementState);
        EasyMock.replay(mockServerItemService);
        EasyMock.replay(mockActionService);
        intruderHandler.handleIntruders(botBase);
        setPrivateField(BotSyncBaseItem.class, attackerBotItem, "idle", true);
        intruderHandler.handleIntruders(botBase);
        EasyMock.verify(mockEnragementState);
        EasyMock.verify(mockServerItemService);
        EasyMock.verify(mockActionService);
    }

    @Test
    @DirtiesContext
    public void oneIntruderAndAttackerException() throws Exception {
        configureSimplePlanetNoResources();

        SimpleBase botBase = new SimpleBase(1, 1);
        Region region = createRegion(new Rectangle(0, 0, 2000, 2000), 1);
        SyncBaseItem intruder = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(1000, 1000), new Id(-1, -1));
        SyncBaseItem attacker = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(200, 200), new Id(-2, -2));

        TestPlanetServices testServices = new TestPlanetServices();

        ActionService mockActionService = EasyMock.createStrictMock(ActionService.class);
        mockActionService.attack(EasyMock.eq(attacker), EasyMock.eq(intruder), EasyMock.eq(new Index(1000, 741)), EasyMock.eq(3.14159265358979, 0.1), EasyMock.eq(true));
        //noinspection ThrowableInstanceNeverThrown
        EasyMock.expectLastCall().andThrow(new RuntimeException());
        mockActionService.attack(EasyMock.eq(attacker), EasyMock.eq(intruder), EasyMock.eq(new Index(1000, 741)), EasyMock.eq(3.14159265358979, 0.1), EasyMock.eq(true));
        testServices.setActionService(mockActionService);

        BotEnragementState mockEnragementState = EasyMock.createStrictMock(BotEnragementState.class);
        BotSyncBaseItem attackerBotItem = new BotSyncBaseItem(attacker, null, testServices);
        EasyMock.expect(mockEnragementState.getAllIdleAttackers()).andReturn(Collections.singleton(attackerBotItem));
        mockEnragementState.handleIntruders(Collections.<SyncBaseItem>singletonList(intruder), botBase);
        EasyMock.expect(mockEnragementState.getAllIdleAttackers()).andReturn(Collections.singleton(attackerBotItem));
        mockEnragementState.handleIntruders(Collections.<SyncBaseItem>singletonList(intruder), botBase);

        ServerItemService mockServerItemService = EasyMock.createStrictMock(ServerItemService.class);
        EasyMock.expect(mockServerItemService.getEnemyItems(botBase, region)).andReturn(Collections.<SyncBaseItem>singletonList(intruder));
        EasyMock.expect(mockServerItemService.getEnemyItems(botBase, region)).andReturn(Collections.<SyncBaseItem>singletonList(intruder));

        testServices.setItemService(mockServerItemService);
        testServices.setCollisionService(planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getCollisionService());

        IntruderHandler intruderHandler = new IntruderHandler(mockEnragementState, region, testServices);

        EasyMock.replay(mockEnragementState);
        EasyMock.replay(mockServerItemService);
        EasyMock.replay(mockActionService);
        intruderHandler.handleIntruders(botBase);
        intruderHandler.handleIntruders(botBase);
        EasyMock.verify(mockEnragementState);
        EasyMock.verify(mockServerItemService);
        EasyMock.verify(mockActionService);
    }

    @Test
    @DirtiesContext
    public void twoIntruderTakeNextEnemy() throws Exception {
        configureSimplePlanetNoResources();

        SimpleBase botBase = new SimpleBase(1, 1);
        Region region = createRegion(new Rectangle(0, 0, 2000, 2000), 1);
        SyncBaseItem intruder1 = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(1000, 1000), new Id(-1, -1));
        SyncBaseItem intruder2 = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(1000, 1500), new Id(-2, -1));
        SyncBaseItem attacker = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(200, 200), new Id(-3, -2));

        TestPlanetServices testServices = new TestPlanetServices();

        ActionService mockActionService = EasyMock.createStrictMock(ActionService.class);
        mockActionService.attack(EasyMock.eq(attacker), EasyMock.eq(intruder1), EasyMock.eq(new Index(1000, 741)), EasyMock.eq(3.14159265358979, 0.1), EasyMock.eq(true));
        //noinspection ThrowableInstanceNeverThrown
        EasyMock.expectLastCall().andThrow(new RuntimeException());
        mockActionService.attack(EasyMock.eq(attacker), EasyMock.eq(intruder1), EasyMock.eq(new Index(1000, 741)), EasyMock.eq(3.14159265358979, 0.1), EasyMock.eq(true));
        testServices.setActionService(mockActionService);

        BotEnragementState mockEnragementState = EasyMock.createStrictMock(BotEnragementState.class);
        BotSyncBaseItem attackerBotItem = new BotSyncBaseItem(attacker, null, testServices);
        EasyMock.expect(mockEnragementState.getAllIdleAttackers()).andReturn(Collections.singleton(attackerBotItem));
        mockEnragementState.handleIntruders(Arrays.asList(intruder2, intruder1), botBase);
        EasyMock.expect(mockEnragementState.getAllIdleAttackers()).andReturn(Collections.singleton(attackerBotItem));
        mockEnragementState.handleIntruders(Collections.<SyncBaseItem>singletonList(intruder1), botBase);

        ServerItemService mockServerItemService = EasyMock.createStrictMock(ServerItemService.class);
        EasyMock.expect(mockServerItemService.getEnemyItems(botBase, region)).andReturn(Arrays.asList(intruder2, intruder1));
        EasyMock.expect(mockServerItemService.getEnemyItems(botBase, region)).andReturn(Collections.<SyncBaseItem>singletonList(intruder1));

        testServices.setItemService(mockServerItemService);
        testServices.setCollisionService(planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getCollisionService());

        IntruderHandler intruderHandler = new IntruderHandler(mockEnragementState, region, testServices);

        EasyMock.replay(mockEnragementState);
        EasyMock.replay(mockServerItemService);
        EasyMock.replay(mockActionService);
        intruderHandler.handleIntruders(botBase);
        intruderHandler.handleIntruders(botBase);
        EasyMock.verify(mockEnragementState);
        EasyMock.verify(mockServerItemService);
        EasyMock.verify(mockActionService);
    }

    @Test
    @DirtiesContext
    public void shortestWaySorter() throws Exception {
        configureSimplePlanetNoResources();

        // Intruders
        SyncBaseItem intruder1 = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(1000, 1200), new Id(-1, -1));
        SyncBaseItem intruder2 = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(1000, 1400), new Id(-2, -1));
        SyncBaseItem intruder3 = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(1000, 1600), new Id(-3, -1));
        SyncBaseItem intruder4 = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(1000, 1800), new Id(-4, -1));
        SyncBaseItem intruder5 = createSyncBaseItem(TEST_ATTACK_ITEM_ID_2, new Index(1000, 2000), new Id(-5, -1));

        // Attackers
        SyncBaseItem attacker1 = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(2000, 1200), new Id(-6, -2));
        BotSyncBaseItem attackerBotItem1 = new BotSyncBaseItem(attacker1, null, null);
        SyncBaseItem attacker2 = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(2000, 1400), new Id(-7, -2));
        BotSyncBaseItem attackerBotItem2 = new BotSyncBaseItem(attacker2, null, null);
        SyncBaseItem attacker3 = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(2000, 1600), new Id(-8, -2));
        BotSyncBaseItem attackerBotItem3 = new BotSyncBaseItem(attacker3, null, null);
        SyncBaseItem attacker4 = createSyncBaseItem(TEST_ATTACK_ITEM_ID_2, new Index(2000, 1800), new Id(-9, -2));
        BotSyncBaseItem attackerBotItem4 = new BotSyncBaseItem(attacker4, null, null);
        SyncBaseItem attacker5 = createSyncBaseItem(TEST_ATTACK_ITEM_ID_2, new Index(2000, 2000), new Id(-10, -2));
        BotSyncBaseItem attackerBotItem5 = new BotSyncBaseItem(attacker5, null, null);

        Collection<SyncBaseItem> intruders;
        Collection<BotSyncBaseItem> attackers;
        Map<BotSyncBaseItem, SyncBaseItem> sorted;

        intruders = new ArrayList<>();
        intruders.add(intruder1);
        attackers = new ArrayList<>();
        attackers.add(attackerBotItem1);
        sorted = ShortestWaySorter.setupAttackerTarget(attackers, intruders);
        Assert.assertEquals(1, sorted.size());
        Assert.assertEquals(attackerBotItem1, CommonJava.getFirst(sorted.entrySet()).getKey());
        Assert.assertEquals(intruder1, CommonJava.getFirst(sorted.entrySet()).getValue());

        intruders = new ArrayList<>();
        intruders.add(intruder1);
        intruders.add(intruder2);
        attackers = new ArrayList<>();
        attackers.add(attackerBotItem1);
        sorted = ShortestWaySorter.setupAttackerTarget(attackers, intruders);
        Assert.assertEquals(1, sorted.size());
        Assert.assertEquals(attackerBotItem1, CommonJava.getFirst(sorted.entrySet()).getKey());
        Assert.assertEquals(intruder1, CommonJava.getFirst(sorted.entrySet()).getValue());

        intruders = new ArrayList<>();
        intruders.add(intruder1);
        intruders.add(intruder2);
        attackers = new ArrayList<>();
        attackers.add(attackerBotItem1);
        attackers.add(attackerBotItem2);
        sorted = ShortestWaySorter.setupAttackerTarget(attackers, intruders);
        Assert.assertEquals(2, sorted.size());
        assertShortestWaySorter(sorted, attackerBotItem1, intruder1);
        assertShortestWaySorter(sorted, attackerBotItem2, intruder2);

        intruders = new ArrayList<>();
        intruders.add(intruder1);
        intruders.add(intruder3);
        attackers = new ArrayList<>();
        attackers.add(attackerBotItem1);
        attackers.add(attackerBotItem2);
        sorted = ShortestWaySorter.setupAttackerTarget(attackers, intruders);
        Assert.assertEquals(2, sorted.size());
        assertShortestWaySorter(sorted, attackerBotItem1, intruder1);
        assertShortestWaySorter(sorted, attackerBotItem2, intruder3);

        intruders = new ArrayList<>();
        intruders.add(intruder1);
        intruders.add(intruder2);
        intruders.add(intruder3);
        attackers = new ArrayList<>();
        attackers.add(attackerBotItem1);
        attackers.add(attackerBotItem2);
        sorted = ShortestWaySorter.setupAttackerTarget(attackers, intruders);
        Assert.assertEquals(2, sorted.size());
        assertShortestWaySorter(sorted, attackerBotItem1, intruder1);
        assertShortestWaySorter(sorted, attackerBotItem2, intruder2);

        intruders = new ArrayList<>();
        intruders.add(intruder1);
        intruders.add(intruder3);
        attackers = new ArrayList<>();
        attackers.add(attackerBotItem1);
        attackers.add(attackerBotItem2);
        attackers.add(attackerBotItem3);
        sorted = ShortestWaySorter.setupAttackerTarget(attackers, intruders);
        Assert.assertEquals(2, sorted.size());
        assertShortestWaySorter(sorted, attackerBotItem1, intruder1);
        assertShortestWaySorter(sorted, attackerBotItem3, intruder3);

        intruders = new ArrayList<>();
        intruders.add(intruder1);
        intruders.add(intruder2);
        intruders.add(intruder3);
        attackers = new ArrayList<>();
        attackers.add(attackerBotItem1);
        attackers.add(attackerBotItem2);
        attackers.add(attackerBotItem3);
        sorted = ShortestWaySorter.setupAttackerTarget(attackers, intruders);
        Assert.assertEquals(3, sorted.size());
        assertShortestWaySorter(sorted, attackerBotItem1, intruder1);
        assertShortestWaySorter(sorted, attackerBotItem2, intruder2);
        assertShortestWaySorter(sorted, attackerBotItem3, intruder3);

        intruders = new ArrayList<>();
        intruders.add(intruder1);
        intruders.add(intruder2);
        intruders.add(intruder3);
        intruders.add(intruder4);
        attackers = new ArrayList<>();
        attackers.add(attackerBotItem1);
        sorted = ShortestWaySorter.setupAttackerTarget(attackers, intruders);
        Assert.assertEquals(1, sorted.size());
        assertShortestWaySorter(sorted, attackerBotItem1, intruder1);

        intruders = new ArrayList<>();
        intruders.add(intruder4);
        attackers = new ArrayList<>();
        attackers.add(attackerBotItem1);
        attackers.add(attackerBotItem2);
        attackers.add(attackerBotItem3);
        attackers.add(attackerBotItem4);
        sorted = ShortestWaySorter.setupAttackerTarget(attackers, intruders);
        Assert.assertEquals(1, sorted.size());
        assertShortestWaySorter(sorted, attackerBotItem3, intruder4);

        intruders = new ArrayList<>();
        intruders.add(intruder1);
        intruders.add(intruder5);
        attackers = new ArrayList<>();
        attackers.add(attackerBotItem1);
        attackers.add(attackerBotItem4);
        sorted = ShortestWaySorter.setupAttackerTarget(attackers, intruders);
        Assert.assertEquals(2, sorted.size());
        assertShortestWaySorter(sorted, attackerBotItem1, intruder1);
        assertShortestWaySorter(sorted, attackerBotItem4, intruder5);

        intruders = new ArrayList<>();
        intruders.add(intruder1);
        intruders.add(intruder2);
        intruders.add(intruder3);
        intruders.add(intruder4);
        intruders.add(intruder5);
        attackers = new ArrayList<>();
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

        intruders = new ArrayList<>();
        intruders.add(intruder1);
        intruders.add(intruder2);
        intruders.add(intruder3);
        intruders.add(intruder4);
        attackers = new ArrayList<>();
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

    @Test
    @DirtiesContext
    public void onIntrudersLeft() throws Exception {
        configureSimplePlanetNoResources();

        SimpleBase botBase = new SimpleBase(1, 1);
        Region region = createRegion(new Rectangle(0, 0, 2000, 2000), 1);
        SyncBaseItem intruder1 = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(1000, 1000), new Id(-1, -1));
        SyncBaseItem intruder2 = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(1000, 1500), new Id(-2, -1));

        TestPlanetServices testServices = new TestPlanetServices();

        ActionService mockActionService = EasyMock.createNiceMock(ActionService.class);
        testServices.setActionService(mockActionService);

        BotEnragementState mockEnragementState = EasyMock.createStrictMock(BotEnragementState.class);
        mockEnragementState.handleIntruders(Collections.<SyncBaseItem>emptyList(), botBase);
        EasyMock.expect(mockEnragementState.getAllIdleAttackers()).andReturn(Collections.<BotSyncBaseItem>emptyList());
        mockEnragementState.handleIntruders(Arrays.asList(intruder2, intruder1), botBase);
        EasyMock.expect(mockEnragementState.getAllIdleAttackers()).andReturn(Collections.<BotSyncBaseItem>emptyList());
        mockEnragementState.handleIntruders(Collections.<SyncBaseItem>singletonList(intruder1), botBase);
        mockEnragementState.handleIntruders(Collections.<SyncBaseItem>emptyList(), botBase);
        EasyMock.expect(mockEnragementState.getAllIdleAttackers()).andReturn(Collections.<BotSyncBaseItem>emptyList());
        mockEnragementState.handleIntruders(Collections.<SyncBaseItem>singletonList(intruder2), botBase);
        EasyMock.expect(mockEnragementState.getAllIdleAttackers()).andReturn(Collections.<BotSyncBaseItem>emptyList());
        mockEnragementState.handleIntruders(Arrays.asList(intruder2, intruder1), botBase);
        mockEnragementState.handleIntruders(Collections.<SyncBaseItem>emptyList(), botBase);

        ServerItemService mockServerItemService = EasyMock.createStrictMock(ServerItemService.class);
        EasyMock.expect(mockServerItemService.getEnemyItems(botBase, region)).andReturn(Collections.<SyncBaseItem>emptyList());
        EasyMock.expect(mockServerItemService.getEnemyItems(botBase, region)).andReturn(Arrays.asList(intruder2, intruder1));
        EasyMock.expect(mockServerItemService.getEnemyItems(botBase, region)).andReturn(Collections.<SyncBaseItem>singletonList(intruder1));
        EasyMock.expect(mockServerItemService.getEnemyItems(botBase, region)).andReturn(Collections.<SyncBaseItem>emptyList());
        EasyMock.expect(mockServerItemService.getEnemyItems(botBase, region)).andReturn(Collections.<SyncBaseItem>singletonList(intruder2));
        EasyMock.expect(mockServerItemService.getEnemyItems(botBase, region)).andReturn(Arrays.asList(intruder2, intruder1));
        EasyMock.expect(mockServerItemService.getEnemyItems(botBase, region)).andReturn(Collections.<SyncBaseItem>emptyList());

        testServices.setItemService(mockServerItemService);
        testServices.setCollisionService(planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getCollisionService());

        IntruderHandler intruderHandler = new IntruderHandler(mockEnragementState, region, testServices);

        EasyMock.replay(mockEnragementState);
        EasyMock.replay(mockServerItemService);
        EasyMock.replay(mockActionService);
        intruderHandler.handleIntruders(botBase);
        intruderHandler.handleIntruders(botBase);
        intruderHandler.handleIntruders(botBase);
        intruderHandler.handleIntruders(botBase);
        intruderHandler.handleIntruders(botBase);
        intruderHandler.handleIntruders(botBase);
        intruderHandler.handleIntruders(botBase);
        EasyMock.verify(mockEnragementState);
        EasyMock.verify(mockServerItemService);
        EasyMock.verify(mockActionService);
    }

}
