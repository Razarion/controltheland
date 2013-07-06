package com.btxtech.game.services.utg;

import com.btxtech.game.jsre.client.GameEngineMode;
import com.btxtech.game.jsre.client.cockpit.chat.ChatMessageFilter;
import com.btxtech.game.jsre.client.cockpit.quest.QuestProgressInfo;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.LevelScope;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.Region;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.PlanetLiteInfo;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.packets.LevelTaskPacket;
import com.btxtech.game.jsre.common.packets.Packet;
import com.btxtech.game.jsre.common.utg.ConditionServiceListener;
import com.btxtech.game.jsre.common.utg.condition.AbstractSyncItemComparison;
import com.btxtech.game.jsre.common.utg.config.ConditionConfig;
import com.btxtech.game.jsre.common.utg.config.ConditionTrigger;
import com.btxtech.game.jsre.common.utg.config.ItemTypePositionComparisonConfig;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.connection.OnlineUserDTO;
import com.btxtech.game.services.connection.ServerConnectionService;
import com.btxtech.game.services.planet.Base;
import com.btxtech.game.services.planet.BaseService;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.planet.ServerItemService;
import com.btxtech.game.services.planet.impl.PlanetSystemServiceImpl;
import com.btxtech.game.services.planet.impl.ServerPlanetServicesImpl;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.condition.ServerConditionService;
import com.btxtech.game.services.utg.condition.impl.ServerConditionServiceImpl;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * User: beat Date: 31.01.2012 Time: 16:51:19
 */
public class TestItemTypePositionComparison extends AbstractServiceTest implements ServerConnectionService {
    @Autowired
    private ServerConditionService serverConditionService;
    @Autowired
    private PlanetSystemService planetSystemService;
    private Integer identifier;
    private UserState actor;
    private UserState userState1;
    private Base base1;
    private SyncBaseItem builder1B1;
    private SyncBaseItem builder2B1;
    private SyncBaseItem builder3B1;
    private SyncBaseItem attacker1B1;
    private SyncBaseItem attacker2B1;
    private SyncBaseItem building1B1;
    private SyncBaseItem builder1B2;
    private SimpleBase progressBase;
    private QuestProgressInfo questProgressInfo;

    @Before
    public void before() throws Exception {
        questProgressInfo = null;
        progressBase = null;
        setPrivateStaticField(AbstractSyncItemComparison.class, "MIN_SEND_DELAY", 0);
        configureSimplePlanetNoResources();
        identifier = null;
        actor = null;

        // Mock objects
        userState1 = new UserState();
        userState1.setUser(1);
        userState1.setDbLevelId(TEST_LEVEL_2_REAL_ID);
        base1 = new Base(userState1, planetSystemService.getPlanet(TEST_PLANET_1_ID), 1);
        int itemId = 0;
        builder1B1 = createSyncBaseItem(TEST_START_BUILDER_ITEM_ID, new Index(100, 100), new Id(++itemId, 0), createMockGlobalServices(), createMockPlanetServices(), base1.getSimpleBase());
        builder2B1 = createSyncBaseItem(TEST_START_BUILDER_ITEM_ID, new Index(100, 100), new Id(++itemId, 0), createMockGlobalServices(), createMockPlanetServices(), base1.getSimpleBase());
        builder3B1 = createSyncBaseItem(TEST_START_BUILDER_ITEM_ID, new Index(100, 100), new Id(++itemId, 0), createMockGlobalServices(), createMockPlanetServices(), base1.getSimpleBase());
        attacker1B1 = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(100, 100), new Id(++itemId, 0), createMockGlobalServices(), createMockPlanetServices(), base1.getSimpleBase());
        attacker2B1 = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(100, 100), new Id(++itemId, 0), createMockGlobalServices(), createMockPlanetServices(), base1.getSimpleBase());
        building1B1 = createSyncBaseItem(TEST_SIMPLE_BUILDING_ID, new Index(100, 100), new Id(++itemId, 0), createMockGlobalServices(), createMockPlanetServices(), base1.getSimpleBase());

        UserState userState2 = new UserState();
        userState2.setUser(2);
        Base base2 = new Base(userState2, planetSystemService.getPlanet(TEST_PLANET_1_ID), 2);
        builder1B2 = createSyncBaseItem(TEST_START_BUILDER_ITEM_ID, new Index(100, 100), new Id(++itemId, 0), createMockGlobalServices(), createMockPlanetServices(), base2.getSimpleBase());

        BaseService baseServiceMock = EasyMock.createNiceMock(BaseService.class);
        EasyMock.expect(baseServiceMock.getUserState(base1.getSimpleBase())).andReturn(userState1).anyTimes();
        EasyMock.expect(baseServiceMock.getUserState(base2.getSimpleBase())).andReturn(userState2).anyTimes();
        EasyMock.replay(baseServiceMock);

        LevelScope levelScope = new LevelScope(new PlanetLiteInfo(TEST_PLANET_1_ID,"",null),0,0,null,0);
        UserGuidanceService userGuidanceServiceMock = EasyMock.createNiceMock(UserGuidanceService.class);
        EasyMock.expect(userGuidanceServiceMock.getLevelScope(userState1)).andReturn(levelScope).anyTimes();
        EasyMock.expect(userGuidanceServiceMock.getLevelScope(userState2)).andReturn(levelScope).anyTimes();
        EasyMock.replay(userGuidanceServiceMock);

        ((ServerConditionServiceImpl) deAopProxy(serverConditionService)).setRate(50);
        ((ServerPlanetServicesImpl) planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID)).setBaseService(baseServiceMock);
        setPrivateField(PlanetSystemServiceImpl.class, planetSystemService, "userGuidanceService", userGuidanceServiceMock);
        overrideConnectionService(((ServerPlanetServicesImpl) planetSystemService.getPlanet(TEST_PLANET_1_ID).getPlanetServices()), this);
    }

    private void assertActorAndIdentifierAndClear(UserState expectedActor, Integer expectedIdentifier) {
        Assert.assertEquals(expectedActor, actor);
        Assert.assertEquals(expectedIdentifier, identifier);
        actor = null;
        identifier = null;
    }

    private void assertClearActorAndIdentifier() {
        Assert.assertNull(actor);
        Assert.assertNull(identifier);
    }

    private void assertClearProgressString() {
        Assert.assertNull(questProgressInfo);
    }

    private void assertAndClearQuestProgressInfo(ConditionTrigger conditionTrigger, Map<Integer, QuestProgressInfo.Amount> expectedAmountMap, SimpleBase simpleBase) {
        Assert.assertEquals(conditionTrigger, questProgressInfo.getConditionTrigger());
        assertItemTypeAmount(expectedAmountMap, questProgressInfo);
        Assert.assertEquals(simpleBase, progressBase);
        Assert.assertNull(questProgressInfo.getAmount());
        questProgressInfo = null;
    }

    private void assertAndClearQuestProgressInfo(ConditionTrigger conditionTrigger, Map<Integer, QuestProgressInfo.Amount> expectedAmountMap, SimpleBase simpleBase, int time, int timeTotal) {
        Assert.assertEquals(conditionTrigger, questProgressInfo.getConditionTrigger());
        Assert.assertEquals(time, questProgressInfo.getAmount().getAmount());
        Assert.assertEquals(timeTotal, questProgressInfo.getAmount().getTotalAmount());
        Assert.assertEquals(simpleBase, progressBase);
        assertItemTypeAmount(expectedAmountMap, questProgressInfo);
        questProgressInfo = null;
    }

    private void assertQuestProgressInfoFromService(ConditionTrigger conditionTrigger, Map<Integer, QuestProgressInfo.Amount> expectedAmountMap) {
        QuestProgressInfo questProgressInfo = serverConditionService.getQuestProgressInfo(userState1, 1);
        Assert.assertEquals(conditionTrigger, questProgressInfo.getConditionTrigger());
        Assert.assertNull(questProgressInfo.getAmount());
        assertItemTypeAmount(expectedAmountMap, questProgressInfo);
    }

    private void assertQuestProgressInfoFromService(ConditionTrigger conditionTrigger, Map<Integer, QuestProgressInfo.Amount> expectedAmountMap, int time, int timeTotal) {
        QuestProgressInfo questProgressInfo = serverConditionService.getQuestProgressInfo(userState1, 1);
        Assert.assertEquals(conditionTrigger, questProgressInfo.getConditionTrigger());
        Assert.assertEquals(time, questProgressInfo.getAmount().getAmount());
        Assert.assertEquals(timeTotal, questProgressInfo.getAmount().getTotalAmount());
        assertItemTypeAmount(expectedAmountMap, questProgressInfo);
    }

    private void assertItemTypeAmount(Map<Integer, QuestProgressInfo.Amount> expectedAmountMap, QuestProgressInfo questProgressInfo) {
        Map<Integer, QuestProgressInfo.Amount> actualAmountMap = questProgressInfo.getItemIdAmounts();
        Assert.assertEquals(expectedAmountMap.size(), actualAmountMap.size());
        for (Map.Entry<Integer, QuestProgressInfo.Amount> entry : expectedAmountMap.entrySet()) {
            QuestProgressInfo.Amount expectedAmount = actualAmountMap.get(entry.getKey());
            if (expectedAmount == null) {
                Assert.fail("No actual entry for: " + entry.getKey());
            }

            Assert.assertEquals(entry.getValue().getAmount(), expectedAmount.getAmount());
            Assert.assertEquals(entry.getValue().getTotalAmount(), expectedAmount.getTotalAmount());
        }
    }

    @Test
    @DirtiesContext
    public void noneItemType() throws Exception {
        // Does not make any sense
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_POSITION, new ItemTypePositionComparisonConfig(null, null, null, false), null, null, false);
        serverConditionService.activateCondition(conditionConfig, userState1, 1);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestItemTypePositionComparison.this.actor = actor;
                TestItemTypePositionComparison.this.identifier = identifier;
            }
        });
        assertClearProgressString();
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemDeactivated(builder1B2);
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemDeactivated(builder1B1);
        assertClearActorAndIdentifier();
        assertClearProgressString();

    }

    @Test
    @DirtiesContext
    public void singleItemType() throws Exception {
        Map<ItemType, Integer> itemTypes = new HashMap<>();
        itemTypes.put(builder1B1.getBaseItemType(), 1);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_POSITION, new ItemTypePositionComparisonConfig(itemTypes, null, null, false), null, null, false);
        serverConditionService.activateCondition(conditionConfig, userState1, 1);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestItemTypePositionComparison.this.actor = actor;
                TestItemTypePositionComparison.this.identifier = identifier;
            }
        });

        Map<Integer, QuestProgressInfo.Amount> expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(0, 1));
        assertQuestProgressInfoFromService(ConditionTrigger.SYNC_ITEM_POSITION, expectedAmountMap);
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemDeactivated(builder1B2);
        assertClearProgressString();
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemDeactivated(builder1B1);
        expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(1, 1));
        assertAndClearQuestProgressInfo(ConditionTrigger.SYNC_ITEM_POSITION, expectedAmountMap, base1.getSimpleBase());
        assertActorAndIdentifierAndClear(userState1, 1);
    }

    @Test
    @DirtiesContext
    public void singleItemTypeNotReady() throws Exception {
        Map<ItemType, Integer> itemTypes = new HashMap<>();
        itemTypes.put(builder1B1.getBaseItemType(), 1);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_POSITION, new ItemTypePositionComparisonConfig(itemTypes, null, null, false), null, null, false);
        serverConditionService.activateCondition(conditionConfig, userState1, 1);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestItemTypePositionComparison.this.actor = actor;
                TestItemTypePositionComparison.this.identifier = identifier;
            }
        });

        Map<Integer, QuestProgressInfo.Amount> expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(0, 1));
        assertQuestProgressInfoFromService(ConditionTrigger.SYNC_ITEM_POSITION, expectedAmountMap);
        assertClearActorAndIdentifier();
        builder1B1.setBuildup(0.0);
        serverConditionService.onSyncItemDeactivated(builder1B1);
        assertClearProgressString();
        assertClearActorAndIdentifier();
        builder1B1.setBuildup(1.0);
        serverConditionService.onSyncItemDeactivated(builder1B1);
        expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(1, 1));
        assertAndClearQuestProgressInfo(ConditionTrigger.SYNC_ITEM_POSITION, expectedAmountMap, base1.getSimpleBase());
        assertActorAndIdentifierAndClear(userState1, 1);
    }

    @Test
    @DirtiesContext
    public void multipleItemTypes1() throws Exception {
        Map<ItemType, Integer> itemTypes = new HashMap<>();
        itemTypes.put(builder1B1.getBaseItemType(), 3);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_POSITION, new ItemTypePositionComparisonConfig(itemTypes, null, null, false), null, null, false);
        serverConditionService.activateCondition(conditionConfig, userState1, 1);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestItemTypePositionComparison.this.actor = actor;
                TestItemTypePositionComparison.this.identifier = identifier;
            }
        });

        Map<Integer, QuestProgressInfo.Amount> expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(0, 3));
        assertQuestProgressInfoFromService(ConditionTrigger.SYNC_ITEM_POSITION, expectedAmountMap);
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemDeactivated(builder1B2);
        assertClearActorAndIdentifier();
        assertClearProgressString();
        serverConditionService.onSyncItemDeactivated(builder1B1);
        assertClearActorAndIdentifier();
        expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(1, 3));
        assertAndClearQuestProgressInfo(ConditionTrigger.SYNC_ITEM_POSITION, expectedAmountMap, base1.getSimpleBase());
        serverConditionService.onSyncItemDeactivated(builder1B1);
        assertClearActorAndIdentifier();
        expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(1, 3));
        assertAndClearQuestProgressInfo(ConditionTrigger.SYNC_ITEM_POSITION, expectedAmountMap, base1.getSimpleBase());
        serverConditionService.onSyncItemDeactivated(builder1B1);
        assertClearActorAndIdentifier();
        expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(1, 3));
        assertAndClearQuestProgressInfo(ConditionTrigger.SYNC_ITEM_POSITION, expectedAmountMap, base1.getSimpleBase());
        serverConditionService.onSyncItemDeactivated(builder2B1);
        assertClearActorAndIdentifier();
        expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(2, 3));
        assertAndClearQuestProgressInfo(ConditionTrigger.SYNC_ITEM_POSITION, expectedAmountMap, base1.getSimpleBase());
        serverConditionService.onSyncItemDeactivated(builder2B1);
        assertClearActorAndIdentifier();
        expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(2, 3));
        assertAndClearQuestProgressInfo(ConditionTrigger.SYNC_ITEM_POSITION, expectedAmountMap, base1.getSimpleBase());
        serverConditionService.onSyncItemDeactivated(builder3B1);
        assertActorAndIdentifierAndClear(userState1, 1);
        expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(3, 3));
        assertAndClearQuestProgressInfo(ConditionTrigger.SYNC_ITEM_POSITION, expectedAmountMap, base1.getSimpleBase());
    }

    @Test
    @DirtiesContext
    public void multipleItemTypes2() throws Exception {
        Map<ItemType, Integer> itemTypes = new HashMap<>();
        itemTypes.put(builder1B1.getBaseItemType(), 3);
        itemTypes.put(attacker1B1.getBaseItemType(), 2);
        itemTypes.put(building1B1.getBaseItemType(), 1);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_POSITION, new ItemTypePositionComparisonConfig(itemTypes, null, null, false), null, null, false);
        serverConditionService.activateCondition(conditionConfig, userState1, 1);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestItemTypePositionComparison.this.actor = actor;
                TestItemTypePositionComparison.this.identifier = identifier;
            }
        });

        Map<Integer, QuestProgressInfo.Amount> expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(0, 3));
        expectedAmountMap.put(TEST_ATTACK_ITEM_ID, new QuestProgressInfo.Amount(0, 2));
        expectedAmountMap.put(TEST_SIMPLE_BUILDING_ID, new QuestProgressInfo.Amount(0, 1));
        assertQuestProgressInfoFromService(ConditionTrigger.SYNC_ITEM_POSITION, expectedAmountMap);
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemDeactivated(building1B1);
        assertClearActorAndIdentifier();
        expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(0, 3));
        expectedAmountMap.put(TEST_ATTACK_ITEM_ID, new QuestProgressInfo.Amount(0, 2));
        expectedAmountMap.put(TEST_SIMPLE_BUILDING_ID, new QuestProgressInfo.Amount(1, 1));
        assertAndClearQuestProgressInfo(ConditionTrigger.SYNC_ITEM_POSITION, expectedAmountMap, base1.getSimpleBase());
        serverConditionService.onSyncItemDeactivated(builder1B2);
        assertClearActorAndIdentifier();
        assertClearProgressString();
        serverConditionService.onSyncItemDeactivated(builder1B1);
        assertClearActorAndIdentifier();
        expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(1, 3));
        expectedAmountMap.put(TEST_ATTACK_ITEM_ID, new QuestProgressInfo.Amount(0, 2));
        expectedAmountMap.put(TEST_SIMPLE_BUILDING_ID, new QuestProgressInfo.Amount(1, 1));
        assertAndClearQuestProgressInfo(ConditionTrigger.SYNC_ITEM_POSITION, expectedAmountMap, base1.getSimpleBase());
        serverConditionService.onSyncItemDeactivated(builder1B1);
        assertClearActorAndIdentifier();
        expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(1, 3));
        expectedAmountMap.put(TEST_ATTACK_ITEM_ID, new QuestProgressInfo.Amount(0, 2));
        expectedAmountMap.put(TEST_SIMPLE_BUILDING_ID, new QuestProgressInfo.Amount(1, 1));
        assertAndClearQuestProgressInfo(ConditionTrigger.SYNC_ITEM_POSITION, expectedAmountMap, base1.getSimpleBase());
        serverConditionService.onSyncItemDeactivated(builder1B1);
        assertClearActorAndIdentifier();
        expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(1, 3));
        expectedAmountMap.put(TEST_ATTACK_ITEM_ID, new QuestProgressInfo.Amount(0, 2));
        expectedAmountMap.put(TEST_SIMPLE_BUILDING_ID, new QuestProgressInfo.Amount(1, 1));
        assertAndClearQuestProgressInfo(ConditionTrigger.SYNC_ITEM_POSITION, expectedAmountMap, base1.getSimpleBase());
        serverConditionService.onSyncItemDeactivated(attacker1B1);
        assertClearActorAndIdentifier();
        expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(1, 3));
        expectedAmountMap.put(TEST_ATTACK_ITEM_ID, new QuestProgressInfo.Amount(1, 2));
        expectedAmountMap.put(TEST_SIMPLE_BUILDING_ID, new QuestProgressInfo.Amount(1, 1));
        assertAndClearQuestProgressInfo(ConditionTrigger.SYNC_ITEM_POSITION, expectedAmountMap, base1.getSimpleBase());
        serverConditionService.onSyncItemDeactivated(builder2B1);
        assertClearActorAndIdentifier();
        expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(2, 3));
        expectedAmountMap.put(TEST_ATTACK_ITEM_ID, new QuestProgressInfo.Amount(1, 2));
        expectedAmountMap.put(TEST_SIMPLE_BUILDING_ID, new QuestProgressInfo.Amount(1, 1));
        assertAndClearQuestProgressInfo(ConditionTrigger.SYNC_ITEM_POSITION, expectedAmountMap, base1.getSimpleBase());
        serverConditionService.onSyncItemDeactivated(builder2B1);
        assertClearActorAndIdentifier();
        expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(2, 3));
        expectedAmountMap.put(TEST_ATTACK_ITEM_ID, new QuestProgressInfo.Amount(1, 2));
        expectedAmountMap.put(TEST_SIMPLE_BUILDING_ID, new QuestProgressInfo.Amount(1, 1));
        assertAndClearQuestProgressInfo(ConditionTrigger.SYNC_ITEM_POSITION, expectedAmountMap, base1.getSimpleBase());
        serverConditionService.onSyncItemDeactivated(attacker1B1);
        assertClearActorAndIdentifier();
        expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(2, 3));
        expectedAmountMap.put(TEST_ATTACK_ITEM_ID, new QuestProgressInfo.Amount(1, 2));
        expectedAmountMap.put(TEST_SIMPLE_BUILDING_ID, new QuestProgressInfo.Amount(1, 1));
        assertAndClearQuestProgressInfo(ConditionTrigger.SYNC_ITEM_POSITION, expectedAmountMap, base1.getSimpleBase());
        serverConditionService.onSyncItemDeactivated(attacker2B1);
        assertClearActorAndIdentifier();
        expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(2, 3));
        expectedAmountMap.put(TEST_ATTACK_ITEM_ID, new QuestProgressInfo.Amount(2, 2));
        expectedAmountMap.put(TEST_SIMPLE_BUILDING_ID, new QuestProgressInfo.Amount(1, 1));
        assertAndClearQuestProgressInfo(ConditionTrigger.SYNC_ITEM_POSITION, expectedAmountMap, base1.getSimpleBase());
        serverConditionService.onSyncItemDeactivated(builder3B1);
        assertActorAndIdentifierAndClear(userState1, 1);
        expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(3, 3));
        expectedAmountMap.put(TEST_ATTACK_ITEM_ID, new QuestProgressInfo.Amount(2, 2));
        expectedAmountMap.put(TEST_SIMPLE_BUILDING_ID, new QuestProgressInfo.Amount(1, 1));
        assertAndClearQuestProgressInfo(ConditionTrigger.SYNC_ITEM_POSITION, expectedAmountMap, base1.getSimpleBase());
    }

    @Test
    @DirtiesContext
    public void singleItemTypeRegion() throws Exception {
        Map<ItemType, Integer> itemTypes = new HashMap<>();
        itemTypes.put(builder1B1.getBaseItemType(), 1);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_POSITION, new ItemTypePositionComparisonConfig(itemTypes, createRegion(new Rectangle(500, 500, 1000, 1000), 1), null,
                false), null, null, false);
        serverConditionService.activateCondition(conditionConfig, userState1, 1);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestItemTypePositionComparison.this.actor = actor;
                TestItemTypePositionComparison.this.identifier = identifier;
            }
        });

        Map<Integer, QuestProgressInfo.Amount> expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(0, 1));
        assertQuestProgressInfoFromService(ConditionTrigger.SYNC_ITEM_POSITION, expectedAmountMap);
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemDeactivated(builder1B1);
        assertClearActorAndIdentifier();
        expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(0, 1));
        assertAndClearQuestProgressInfo(ConditionTrigger.SYNC_ITEM_POSITION, expectedAmountMap, base1.getSimpleBase());
        builder1B1.getSyncItemArea().setPosition(new Index(600, 600));
        serverConditionService.onSyncItemDeactivated(builder1B1);
        assertActorAndIdentifierAndClear(userState1, 1);
        expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(1, 1));
        assertAndClearQuestProgressInfo(ConditionTrigger.SYNC_ITEM_POSITION, expectedAmountMap, base1.getSimpleBase());
    }

    @Test
    @DirtiesContext
    public void singleItemTypeRegionNoPosition() throws Exception {
        Map<ItemType, Integer> itemTypes = new HashMap<>();
        itemTypes.put(builder1B1.getBaseItemType(), 1);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_POSITION, new ItemTypePositionComparisonConfig(itemTypes, createRegion(new Rectangle(500, 500, 1000, 1000), 1), null,
                false), null, null, false);
        serverConditionService.activateCondition(conditionConfig, userState1, 1);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestItemTypePositionComparison.this.actor = actor;
                TestItemTypePositionComparison.this.identifier = identifier;
            }
        });

        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemDeactivated(builder1B1);
        assertClearActorAndIdentifier();
        builder1B1.getSyncItemArea().setPosition(null);
        serverConditionService.onSyncItemDeactivated(builder1B1);
        assertClearActorAndIdentifier();
        builder1B1.getSyncItemArea().setPosition(new Index(600, 600));
        serverConditionService.onSyncItemDeactivated(builder1B1);
        assertActorAndIdentifierAndClear(userState1, 1);
    }

    @Test
    @DirtiesContext
    public void multipleItemTypeRegion1() throws Exception {
        Map<ItemType, Integer> itemTypes = new HashMap<>();
        itemTypes.put(builder1B1.getBaseItemType(), 2);
        itemTypes.put(attacker1B1.getBaseItemType(), 2);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_POSITION, new ItemTypePositionComparisonConfig(itemTypes, createRegion(new Rectangle(500, 500, 1000, 1000), 1), null,
                false), null, null, false);
        serverConditionService.activateCondition(conditionConfig, userState1, 1);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestItemTypePositionComparison.this.actor = actor;
                TestItemTypePositionComparison.this.identifier = identifier;
            }
        });

        Map<Integer, QuestProgressInfo.Amount> expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(0, 2));
        expectedAmountMap.put(TEST_ATTACK_ITEM_ID, new QuestProgressInfo.Amount(0, 2));
        assertQuestProgressInfoFromService(ConditionTrigger.SYNC_ITEM_POSITION, expectedAmountMap);
        assertClearActorAndIdentifier();
        builder1B1.getSyncItemArea().setPosition(new Index(600, 600));
        serverConditionService.onSyncItemDeactivated(builder1B1);
        expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(1, 2));
        expectedAmountMap.put(TEST_ATTACK_ITEM_ID, new QuestProgressInfo.Amount(0, 2));
        assertAndClearQuestProgressInfo(ConditionTrigger.SYNC_ITEM_POSITION, expectedAmountMap, base1.getSimpleBase());
        assertClearActorAndIdentifier();
        builder2B1.getSyncItemArea().setPosition(new Index(700, 700));
        serverConditionService.onSyncItemDeactivated(builder2B1);
        expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(2, 2));
        expectedAmountMap.put(TEST_ATTACK_ITEM_ID, new QuestProgressInfo.Amount(0, 2));
        assertAndClearQuestProgressInfo(ConditionTrigger.SYNC_ITEM_POSITION, expectedAmountMap, base1.getSimpleBase());
        assertClearActorAndIdentifier();
        attacker1B1.getSyncItemArea().setPosition(new Index(800, 800));
        serverConditionService.onSyncItemDeactivated(attacker1B1);
        expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(2, 2));
        expectedAmountMap.put(TEST_ATTACK_ITEM_ID, new QuestProgressInfo.Amount(1, 2));
        assertAndClearQuestProgressInfo(ConditionTrigger.SYNC_ITEM_POSITION, expectedAmountMap, base1.getSimpleBase());
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemDeactivated(attacker2B1);
        expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(2, 2));
        expectedAmountMap.put(TEST_ATTACK_ITEM_ID, new QuestProgressInfo.Amount(1, 2));
        assertAndClearQuestProgressInfo(ConditionTrigger.SYNC_ITEM_POSITION, expectedAmountMap, base1.getSimpleBase());
        assertClearActorAndIdentifier();
        attacker1B1.getSyncItemArea().setPosition(new Index(10, 100));
        serverConditionService.onSyncItemDeactivated(attacker1B1);
        expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(2, 2));
        expectedAmountMap.put(TEST_ATTACK_ITEM_ID, new QuestProgressInfo.Amount(0, 2));
        assertAndClearQuestProgressInfo(ConditionTrigger.SYNC_ITEM_POSITION, expectedAmountMap, base1.getSimpleBase());
        assertClearActorAndIdentifier();
        attacker2B1.getSyncItemArea().setPosition(new Index(900, 900));
        serverConditionService.onSyncItemDeactivated(attacker2B1);
        expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(2, 2));
        expectedAmountMap.put(TEST_ATTACK_ITEM_ID, new QuestProgressInfo.Amount(1, 2));
        assertAndClearQuestProgressInfo(ConditionTrigger.SYNC_ITEM_POSITION, expectedAmountMap, base1.getSimpleBase());
        assertClearActorAndIdentifier();
        attacker1B1.getSyncItemArea().setPosition(new Index(800, 800));
        serverConditionService.onSyncItemDeactivated(attacker1B1);
        expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(2, 2));
        expectedAmountMap.put(TEST_ATTACK_ITEM_ID, new QuestProgressInfo.Amount(2, 2));
        assertAndClearQuestProgressInfo(ConditionTrigger.SYNC_ITEM_POSITION, expectedAmountMap, base1.getSimpleBase());
        assertActorAndIdentifierAndClear(userState1, 1);
    }

    @Test
    @DirtiesContext
    public void multipleItemTypeRegionItemKilled() throws Exception {
        Map<ItemType, Integer> itemTypes = new HashMap<>();
        itemTypes.put(builder1B1.getBaseItemType(), 2);
        itemTypes.put(attacker1B1.getBaseItemType(), 2);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_POSITION, new ItemTypePositionComparisonConfig(itemTypes, createRegion(new Rectangle(500, 500, 1000, 1000), 1), null,
                false), null, null, false);
        serverConditionService.activateCondition(conditionConfig, userState1, 1);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestItemTypePositionComparison.this.actor = actor;
                TestItemTypePositionComparison.this.identifier = identifier;
            }
        });

        Map<Integer, QuestProgressInfo.Amount> expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(0, 2));
        expectedAmountMap.put(TEST_ATTACK_ITEM_ID, new QuestProgressInfo.Amount(0, 2));
        assertQuestProgressInfoFromService(ConditionTrigger.SYNC_ITEM_POSITION, expectedAmountMap);
        assertClearActorAndIdentifier();
        builder1B1.getSyncItemArea().setPosition(new Index(600, 600));
        serverConditionService.onSyncItemDeactivated(builder1B1);
        expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(1, 2));
        expectedAmountMap.put(TEST_ATTACK_ITEM_ID, new QuestProgressInfo.Amount(0, 2));
        assertAndClearQuestProgressInfo(ConditionTrigger.SYNC_ITEM_POSITION, expectedAmountMap, base1.getSimpleBase());
        assertClearActorAndIdentifier();
        builder2B1.getSyncItemArea().setPosition(new Index(700, 700));
        serverConditionService.onSyncItemDeactivated(builder2B1);
        expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(2, 2));
        expectedAmountMap.put(TEST_ATTACK_ITEM_ID, new QuestProgressInfo.Amount(0, 2));
        assertAndClearQuestProgressInfo(ConditionTrigger.SYNC_ITEM_POSITION, expectedAmountMap, base1.getSimpleBase());
        assertClearActorAndIdentifier();
        attacker1B1.getSyncItemArea().setPosition(new Index(800, 800));
        serverConditionService.onSyncItemDeactivated(attacker1B1);
        expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(2, 2));
        expectedAmountMap.put(TEST_ATTACK_ITEM_ID, new QuestProgressInfo.Amount(1, 2));
        assertAndClearQuestProgressInfo(ConditionTrigger.SYNC_ITEM_POSITION, expectedAmountMap, base1.getSimpleBase());
        assertClearActorAndIdentifier();
        builder1B1.setHealth(0); // Killed
        attacker2B1.getSyncItemArea().setPosition(new Index(900, 900));
        serverConditionService.onSyncItemDeactivated(attacker2B1);
        expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(1, 2));
        expectedAmountMap.put(TEST_ATTACK_ITEM_ID, new QuestProgressInfo.Amount(2, 2));
        assertAndClearQuestProgressInfo(ConditionTrigger.SYNC_ITEM_POSITION, expectedAmountMap, base1.getSimpleBase());
        assertClearActorAndIdentifier();
        builder3B1.getSyncItemArea().setPosition(new Index(600, 600));
        serverConditionService.onSyncItemDeactivated(builder3B1);
        expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(2, 2));
        expectedAmountMap.put(TEST_ATTACK_ITEM_ID, new QuestProgressInfo.Amount(2, 2));
        assertAndClearQuestProgressInfo(ConditionTrigger.SYNC_ITEM_POSITION, expectedAmountMap, base1.getSimpleBase());
        assertActorAndIdentifierAndClear(userState1, 1);
    }

    @Test
    @DirtiesContext
    public void singleItemTypeRegionTime() throws Exception {
        Map<ItemType, Integer> itemTypes = new HashMap<>();
        itemTypes.put(builder1B1.getBaseItemType(), 1);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_POSITION, new ItemTypePositionComparisonConfig(itemTypes, createRegion(new Rectangle(500, 500, 1000, 1000), 1), 100,
                false), null, null, false);
        serverConditionService.activateCondition(conditionConfig, userState1, 1);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestItemTypePositionComparison.this.actor = actor;
                TestItemTypePositionComparison.this.identifier = identifier;
            }
        });

        Map<Integer, QuestProgressInfo.Amount> expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(0, 1));
        assertQuestProgressInfoFromService(ConditionTrigger.SYNC_ITEM_POSITION, expectedAmountMap, 0, 0);
        assertClearActorAndIdentifier();
        builder1B1.getSyncItemArea().setPosition(new Index(600, 600));
        serverConditionService.onSyncItemDeactivated(builder1B1);
        assertClearActorAndIdentifier();
        expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(1, 1));
        assertAndClearQuestProgressInfo(ConditionTrigger.SYNC_ITEM_POSITION, expectedAmountMap, base1.getSimpleBase(), 1, 0);
        Thread.sleep(200);
        expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(1, 1));
        assertAndClearQuestProgressInfo(ConditionTrigger.SYNC_ITEM_POSITION, expectedAmountMap, base1.getSimpleBase(), 1, 0);
        assertActorAndIdentifierAndClear(userState1, 1);
    }

    @Test
    @DirtiesContext
    public void multipleItemTypeRegionTime1() throws Exception {
        Map<ItemType, Integer> itemTypes = new HashMap<>();
        itemTypes.put(builder1B1.getBaseItemType(), 3);
        itemTypes.put(attacker1B1.getBaseItemType(), 2);
        itemTypes.put(building1B1.getBaseItemType(), 1);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_POSITION, new ItemTypePositionComparisonConfig(itemTypes, createRegion(new Rectangle(500, 500, 1000, 1000), 1), 100,
                false), null, null, false);
        serverConditionService.activateCondition(conditionConfig, userState1, 1);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestItemTypePositionComparison.this.actor = actor;
                TestItemTypePositionComparison.this.identifier = identifier;
            }
        });

        Map<Integer, QuestProgressInfo.Amount> expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(0, 3));
        expectedAmountMap.put(TEST_ATTACK_ITEM_ID, new QuestProgressInfo.Amount(0, 2));
        expectedAmountMap.put(TEST_SIMPLE_BUILDING_ID, new QuestProgressInfo.Amount(0, 1));
        assertQuestProgressInfoFromService(ConditionTrigger.SYNC_ITEM_POSITION, expectedAmountMap, 0, 0);
        assertClearActorAndIdentifier();
        builder1B1.getSyncItemArea().setPosition(new Index(600, 600));
        serverConditionService.onSyncItemDeactivated(builder1B1);
        expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(1, 3));
        expectedAmountMap.put(TEST_ATTACK_ITEM_ID, new QuestProgressInfo.Amount(0, 2));
        expectedAmountMap.put(TEST_SIMPLE_BUILDING_ID, new QuestProgressInfo.Amount(0, 1));
        assertAndClearQuestProgressInfo(ConditionTrigger.SYNC_ITEM_POSITION, expectedAmountMap, base1.getSimpleBase(), 0, 0);
        assertClearActorAndIdentifier();
        builder2B1.getSyncItemArea().setPosition(new Index(600, 600));
        serverConditionService.onSyncItemDeactivated(builder2B1);
        expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(2, 3));
        expectedAmountMap.put(TEST_ATTACK_ITEM_ID, new QuestProgressInfo.Amount(0, 2));
        expectedAmountMap.put(TEST_SIMPLE_BUILDING_ID, new QuestProgressInfo.Amount(0, 1));
        assertAndClearQuestProgressInfo(ConditionTrigger.SYNC_ITEM_POSITION, expectedAmountMap, base1.getSimpleBase(), 0, 0);
        assertClearActorAndIdentifier();
        builder3B1.getSyncItemArea().setPosition(new Index(600, 600));
        serverConditionService.onSyncItemDeactivated(builder3B1);
        expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(3, 3));
        expectedAmountMap.put(TEST_ATTACK_ITEM_ID, new QuestProgressInfo.Amount(0, 2));
        expectedAmountMap.put(TEST_SIMPLE_BUILDING_ID, new QuestProgressInfo.Amount(0, 1));
        assertAndClearQuestProgressInfo(ConditionTrigger.SYNC_ITEM_POSITION, expectedAmountMap, base1.getSimpleBase(), 0, 0);
        assertClearActorAndIdentifier();
        attacker1B1.getSyncItemArea().setPosition(new Index(800, 800));
        serverConditionService.onSyncItemDeactivated(attacker1B1);
        expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(3, 3));
        expectedAmountMap.put(TEST_ATTACK_ITEM_ID, new QuestProgressInfo.Amount(1, 2));
        expectedAmountMap.put(TEST_SIMPLE_BUILDING_ID, new QuestProgressInfo.Amount(0, 1));
        assertAndClearQuestProgressInfo(ConditionTrigger.SYNC_ITEM_POSITION, expectedAmountMap, base1.getSimpleBase(), 0, 0);
        assertClearActorAndIdentifier();
        attacker2B1.getSyncItemArea().setPosition(new Index(1000, 800));
        serverConditionService.onSyncItemDeactivated(attacker2B1);
        expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(3, 3));
        expectedAmountMap.put(TEST_ATTACK_ITEM_ID, new QuestProgressInfo.Amount(2, 2));
        expectedAmountMap.put(TEST_SIMPLE_BUILDING_ID, new QuestProgressInfo.Amount(0, 1));
        assertAndClearQuestProgressInfo(ConditionTrigger.SYNC_ITEM_POSITION, expectedAmountMap, base1.getSimpleBase(), 0, 0);
        assertClearActorAndIdentifier();
        building1B1.getSyncItemArea().setPosition(new Index(1000, 800));
        serverConditionService.onSyncItemBuilt(building1B1);
        expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(3, 3));
        expectedAmountMap.put(TEST_ATTACK_ITEM_ID, new QuestProgressInfo.Amount(2, 2));
        expectedAmountMap.put(TEST_SIMPLE_BUILDING_ID, new QuestProgressInfo.Amount(1, 1));
        assertAndClearQuestProgressInfo(ConditionTrigger.SYNC_ITEM_POSITION, expectedAmountMap, base1.getSimpleBase(), 1, 0);
        assertClearActorAndIdentifier();
        Thread.sleep(200);
        expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(3, 3));
        expectedAmountMap.put(TEST_ATTACK_ITEM_ID, new QuestProgressInfo.Amount(2, 2));
        expectedAmountMap.put(TEST_SIMPLE_BUILDING_ID, new QuestProgressInfo.Amount(1, 1));
        assertAndClearQuestProgressInfo(ConditionTrigger.SYNC_ITEM_POSITION, expectedAmountMap, base1.getSimpleBase(), 1, 0);
        assertActorAndIdentifierAndClear(userState1, 1);
    }

    @Test
    @DirtiesContext
    public void multipleItemTypeRegionTime2() throws Exception {
        Map<ItemType, Integer> itemTypes = new HashMap<>();
        itemTypes.put(builder1B1.getBaseItemType(), 3);
        itemTypes.put(attacker1B1.getBaseItemType(), 2);
        itemTypes.put(building1B1.getBaseItemType(), 1);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_POSITION, new ItemTypePositionComparisonConfig(itemTypes, createRegion(new Rectangle(500, 500, 1000, 1000), 1), 100,
                false), null, null, false);
        serverConditionService.activateCondition(conditionConfig, userState1, 1);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestItemTypePositionComparison.this.actor = actor;
                TestItemTypePositionComparison.this.identifier = identifier;
            }
        });

        Map<Integer, QuestProgressInfo.Amount> expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(0, 3));
        expectedAmountMap.put(TEST_ATTACK_ITEM_ID, new QuestProgressInfo.Amount(0, 2));
        expectedAmountMap.put(TEST_SIMPLE_BUILDING_ID, new QuestProgressInfo.Amount(0, 1));
        assertQuestProgressInfoFromService(ConditionTrigger.SYNC_ITEM_POSITION, expectedAmountMap, 0, 0);
        assertClearActorAndIdentifier();
        builder1B1.getSyncItemArea().setPosition(new Index(600, 600));
        serverConditionService.onSyncItemDeactivated(builder1B1);
        assertClearActorAndIdentifier();
        builder2B1.getSyncItemArea().setPosition(new Index(600, 600));
        serverConditionService.onSyncItemDeactivated(builder2B1);
        assertClearActorAndIdentifier();
        builder3B1.getSyncItemArea().setPosition(new Index(600, 600));
        serverConditionService.onSyncItemDeactivated(builder3B1);
        assertClearActorAndIdentifier();
        attacker1B1.getSyncItemArea().setPosition(new Index(800, 800));
        serverConditionService.onSyncItemDeactivated(attacker1B1);
        assertClearActorAndIdentifier();
        attacker2B1.getSyncItemArea().setPosition(new Index(1000, 800));
        serverConditionService.onSyncItemDeactivated(attacker2B1);
        assertClearActorAndIdentifier();
        building1B1.getSyncItemArea().setPosition(new Index(1000, 800));
        serverConditionService.onSyncItemBuilt(building1B1);
        assertClearActorAndIdentifier();
        builder1B1.getSyncItemArea().setPosition(new Index(100, 100));
        Thread.sleep(200);
        assertClearActorAndIdentifier();
        builder2B1.getSyncItemArea().setPosition(new Index(100, 100));
        builder1B1.getSyncItemArea().setPosition(new Index(600, 600));
        serverConditionService.onSyncItemDeactivated(builder1B1);
        Thread.sleep(200);
        assertClearActorAndIdentifier();
        builder2B1.getSyncItemArea().setPosition(new Index(600, 600));
        serverConditionService.onSyncItemDeactivated(builder2B1);
        Thread.sleep(200);
        assertActorAndIdentifierAndClear(userState1, 1);
    }

    @Test
    @DirtiesContext
    public void multipleItemTypeRegionTime3() throws Exception {
        Map<ItemType, Integer> itemTypes = new HashMap<>();
        itemTypes.put(builder1B1.getBaseItemType(), 3);
        itemTypes.put(attacker1B1.getBaseItemType(), 2);
        itemTypes.put(building1B1.getBaseItemType(), 1);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_POSITION, new ItemTypePositionComparisonConfig(itemTypes, createRegion(new Rectangle(500, 500, 1000, 1000), 1), 100,
                false), null, null, false);
        serverConditionService.activateCondition(conditionConfig, userState1, 1);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestItemTypePositionComparison.this.actor = actor;
                TestItemTypePositionComparison.this.identifier = identifier;
            }
        });

        assertClearActorAndIdentifier();
        builder1B1.getSyncItemArea().setPosition(new Index(600, 600));
        serverConditionService.onSyncItemDeactivated(builder1B1);
        assertClearActorAndIdentifier();
        builder2B1.getSyncItemArea().setPosition(new Index(600, 600));
        serverConditionService.onSyncItemDeactivated(builder2B1);
        assertClearActorAndIdentifier();
        builder3B1.getSyncItemArea().setPosition(new Index(600, 600));
        serverConditionService.onSyncItemDeactivated(builder3B1);
        assertClearActorAndIdentifier();
        attacker1B1.getSyncItemArea().setPosition(new Index(800, 800));
        serverConditionService.onSyncItemDeactivated(attacker1B1);
        assertClearActorAndIdentifier();
        attacker2B1.getSyncItemArea().setPosition(new Index(1000, 800));
        serverConditionService.onSyncItemDeactivated(attacker2B1);
        assertClearActorAndIdentifier();
        building1B1.getSyncItemArea().setPosition(new Index(1000, 800));
        serverConditionService.onSyncItemBuilt(building1B1);
        assertClearActorAndIdentifier();
        builder1B1.getSyncItemArea().setPosition(new Index(100, 100));
        Thread.sleep(200);
        assertClearActorAndIdentifier();
        builder2B1.getSyncItemArea().setPosition(new Index(100, 100));
        builder1B1.getSyncItemArea().setPosition(new Index(600, 600));
        serverConditionService.onSyncItemDeactivated(builder1B1);
        Thread.sleep(200);
        assertClearActorAndIdentifier();
        builder2B1.getSyncItemArea().setPosition(new Index(600, 600));
        serverConditionService.onSyncItemDeactivated(builder2B1);
        building1B1.setHealth(0);
        Thread.sleep(200);
        building1B1.setHealth(1.0);
        serverConditionService.onSyncItemBuilt(building1B1);
        Thread.sleep(200);
        assertActorAndIdentifierAndClear(userState1, 1);
    }

    @Test
    @DirtiesContext
    public void multipleItemTypeRegionTimeAddExisting1() throws Exception {
        ServerItemService serverItemServiceMock = EasyMock.createMock(ServerItemService.class);
        Collection<SyncBaseItem> syncBaseItems = new ArrayList<>();
        Region region = createRegion(new Rectangle(500, 500, 1000, 1000), 1);
        EasyMock.expect(serverItemServiceMock.getBaseItemsInRectangle(region, base1.getSimpleBase(), null)).andReturn(syncBaseItems);
        EasyMock.replay(serverItemServiceMock);
        ((ServerPlanetServicesImpl) planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID)).setServerItemService(serverItemServiceMock);

        Map<ItemType, Integer> itemTypes = new HashMap<>();
        itemTypes.put(builder1B1.getBaseItemType(), 3);
        itemTypes.put(attacker1B1.getBaseItemType(), 2);
        itemTypes.put(building1B1.getBaseItemType(), 1);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_POSITION, new ItemTypePositionComparisonConfig(itemTypes, region, 100,
                true), null, null, false);
        serverConditionService.activateCondition(conditionConfig, userState1, 1);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestItemTypePositionComparison.this.actor = actor;
                TestItemTypePositionComparison.this.identifier = identifier;
            }
        });

        Map<Integer, QuestProgressInfo.Amount> expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(0, 3));
        expectedAmountMap.put(TEST_ATTACK_ITEM_ID, new QuestProgressInfo.Amount(0, 2));
        expectedAmountMap.put(TEST_SIMPLE_BUILDING_ID, new QuestProgressInfo.Amount(0, 1));
        assertQuestProgressInfoFromService(ConditionTrigger.SYNC_ITEM_POSITION, expectedAmountMap, 0, 0);
        assertClearActorAndIdentifier();
        builder1B1.getSyncItemArea().setPosition(new Index(600, 600));
        serverConditionService.onSyncItemDeactivated(builder1B1);
        assertClearActorAndIdentifier();
        builder2B1.getSyncItemArea().setPosition(new Index(600, 600));
        serverConditionService.onSyncItemDeactivated(builder2B1);
        assertClearActorAndIdentifier();
        builder3B1.getSyncItemArea().setPosition(new Index(600, 600));
        serverConditionService.onSyncItemDeactivated(builder3B1);
        assertClearActorAndIdentifier();
        attacker1B1.getSyncItemArea().setPosition(new Index(800, 800));
        serverConditionService.onSyncItemDeactivated(attacker1B1);
        assertClearActorAndIdentifier();
        attacker2B1.getSyncItemArea().setPosition(new Index(1000, 800));
        serverConditionService.onSyncItemDeactivated(attacker2B1);
        assertClearActorAndIdentifier();
        building1B1.getSyncItemArea().setPosition(new Index(1000, 800));
        serverConditionService.onSyncItemBuilt(building1B1);
        assertClearActorAndIdentifier();
        builder1B1.getSyncItemArea().setPosition(new Index(100, 100));
        Thread.sleep(200);
        assertClearActorAndIdentifier();
        builder2B1.getSyncItemArea().setPosition(new Index(100, 100));
        builder1B1.getSyncItemArea().setPosition(new Index(600, 600));
        serverConditionService.onSyncItemDeactivated(builder1B1);
        Thread.sleep(200);
        assertClearActorAndIdentifier();
        builder2B1.getSyncItemArea().setPosition(new Index(600, 600));
        serverConditionService.onSyncItemDeactivated(builder2B1);
        building1B1.setHealth(0);
        Thread.sleep(200);
        building1B1.setHealth(1.0);
        serverConditionService.onSyncItemBuilt(building1B1);
        Thread.sleep(200);
        assertActorAndIdentifierAndClear(userState1, 1);
    }

    @Test
    @DirtiesContext
    public void multipleItemTypeRegionTimeAddExisting2() throws Exception {
        ServerItemService serverItemServiceMock = EasyMock.createMock(ServerItemService.class);
        Collection<SyncBaseItem> syncBaseItems = new ArrayList<>();
        builder1B1.getSyncItemArea().setPosition(new Index(600, 600));
        syncBaseItems.add(builder1B1);
        Region region = createRegion(new Rectangle(500, 500, 1000, 1000), 1);
        EasyMock.expect(serverItemServiceMock.getBaseItemsInRectangle(region, base1.getSimpleBase(), null)).andReturn(syncBaseItems);
        EasyMock.replay(serverItemServiceMock);
        ((ServerPlanetServicesImpl) planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID)).setServerItemService(serverItemServiceMock);

        Map<ItemType, Integer> itemTypes = new HashMap<>();
        itemTypes.put(builder1B1.getBaseItemType(), 3);
        itemTypes.put(attacker1B1.getBaseItemType(), 2);
        itemTypes.put(building1B1.getBaseItemType(), 1);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_POSITION, new ItemTypePositionComparisonConfig(itemTypes, region, 100,
                true), null, null, false);
        serverConditionService.activateCondition(conditionConfig, userState1, 1);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestItemTypePositionComparison.this.actor = actor;
                TestItemTypePositionComparison.this.identifier = identifier;
            }
        });

        Map<Integer, QuestProgressInfo.Amount> expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(1, 3));
        expectedAmountMap.put(TEST_ATTACK_ITEM_ID, new QuestProgressInfo.Amount(0, 2));
        expectedAmountMap.put(TEST_SIMPLE_BUILDING_ID, new QuestProgressInfo.Amount(0, 1));
        assertQuestProgressInfoFromService(ConditionTrigger.SYNC_ITEM_POSITION, expectedAmountMap, 0, 0);
        assertClearActorAndIdentifier();
        builder2B1.getSyncItemArea().setPosition(new Index(600, 600));
        serverConditionService.onSyncItemDeactivated(builder2B1);
        assertClearActorAndIdentifier();
        builder3B1.getSyncItemArea().setPosition(new Index(600, 600));
        serverConditionService.onSyncItemDeactivated(builder3B1);
        assertClearActorAndIdentifier();
        attacker1B1.getSyncItemArea().setPosition(new Index(800, 800));
        serverConditionService.onSyncItemDeactivated(attacker1B1);
        assertClearActorAndIdentifier();
        attacker2B1.getSyncItemArea().setPosition(new Index(1000, 800));
        serverConditionService.onSyncItemDeactivated(attacker2B1);
        assertClearActorAndIdentifier();
        building1B1.getSyncItemArea().setPosition(new Index(1000, 800));
        serverConditionService.onSyncItemBuilt(building1B1);
        assertClearActorAndIdentifier();
        builder1B1.getSyncItemArea().setPosition(new Index(100, 100));
        Thread.sleep(200);
        assertClearActorAndIdentifier();
        builder2B1.getSyncItemArea().setPosition(new Index(100, 100));
        builder1B1.getSyncItemArea().setPosition(new Index(600, 600));
        serverConditionService.onSyncItemDeactivated(builder1B1);
        Thread.sleep(200);
        assertClearActorAndIdentifier();
        builder2B1.getSyncItemArea().setPosition(new Index(600, 600));
        serverConditionService.onSyncItemDeactivated(builder2B1);
        building1B1.setHealth(0);
        Thread.sleep(200);
        building1B1.setHealth(1.0);
        serverConditionService.onSyncItemBuilt(building1B1);
        Thread.sleep(200);
        assertActorAndIdentifierAndClear(userState1, 1);
    }

    @Test
    @DirtiesContext
    public void multipleItemTypeRegionTimeAddExisting3() throws Exception {
        ServerItemService serverItemServiceMock = EasyMock.createMock(ServerItemService.class);
        Collection<SyncBaseItem> syncBaseItems = new ArrayList<>();
        builder1B1.getSyncItemArea().setPosition(new Index(600, 600));
        syncBaseItems.add(builder1B1);
        builder2B1.getSyncItemArea().setPosition(new Index(600, 600));
        syncBaseItems.add(builder2B1);
        Region region = createRegion(new Rectangle(500, 500, 1000, 1000), 1);
        EasyMock.expect(serverItemServiceMock.getBaseItemsInRectangle(region, base1.getSimpleBase(), null)).andReturn(syncBaseItems);
        EasyMock.replay(serverItemServiceMock);
        ((ServerPlanetServicesImpl) planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID)).setServerItemService(serverItemServiceMock);

        Map<ItemType, Integer> itemTypes = new HashMap<>();
        itemTypes.put(builder1B1.getBaseItemType(), 3);
        itemTypes.put(attacker1B1.getBaseItemType(), 2);
        itemTypes.put(building1B1.getBaseItemType(), 1);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_POSITION, new ItemTypePositionComparisonConfig(itemTypes, region, 100,
                true), null, null, false);
        serverConditionService.activateCondition(conditionConfig, userState1, 1);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestItemTypePositionComparison.this.actor = actor;
                TestItemTypePositionComparison.this.identifier = identifier;
            }
        });

        Map<Integer, QuestProgressInfo.Amount> expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(2, 3));
        expectedAmountMap.put(TEST_ATTACK_ITEM_ID, new QuestProgressInfo.Amount(0, 2));
        expectedAmountMap.put(TEST_SIMPLE_BUILDING_ID, new QuestProgressInfo.Amount(0, 1));
        assertQuestProgressInfoFromService(ConditionTrigger.SYNC_ITEM_POSITION, expectedAmountMap, 0, 0);
        assertClearActorAndIdentifier();
        builder3B1.getSyncItemArea().setPosition(new Index(600, 600));
        serverConditionService.onSyncItemDeactivated(builder3B1);
        assertClearActorAndIdentifier();
        attacker1B1.getSyncItemArea().setPosition(new Index(800, 800));
        serverConditionService.onSyncItemDeactivated(attacker1B1);
        assertClearActorAndIdentifier();
        attacker2B1.getSyncItemArea().setPosition(new Index(1000, 800));
        serverConditionService.onSyncItemDeactivated(attacker2B1);
        assertClearActorAndIdentifier();
        building1B1.getSyncItemArea().setPosition(new Index(1000, 800));
        serverConditionService.onSyncItemBuilt(building1B1);
        assertClearActorAndIdentifier();
        Thread.sleep(200);
        assertActorAndIdentifierAndClear(userState1, 1);
    }

    @Test
    @DirtiesContext
    public void multipleItemTypeRegionTimeAddExisting4() throws Exception {
        ServerItemService serverItemServiceMock = EasyMock.createMock(ServerItemService.class);
        Collection<SyncBaseItem> syncBaseItems = new ArrayList<>();
        syncBaseItems.add(builder1B1);
        syncBaseItems.add(builder2B1);
        EasyMock.expect(serverItemServiceMock.getItems4Base(base1.getSimpleBase())).andReturn(syncBaseItems);
        EasyMock.replay(serverItemServiceMock);

        ((ServerPlanetServicesImpl) planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID)).setServerItemService(serverItemServiceMock);

        Map<ItemType, Integer> itemTypes = new HashMap<>();
        itemTypes.put(builder1B1.getBaseItemType(), 3);
        itemTypes.put(attacker1B1.getBaseItemType(), 2);
        itemTypes.put(building1B1.getBaseItemType(), 1);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_POSITION, new ItemTypePositionComparisonConfig(itemTypes, null, 100, true), null, null, false);
        serverConditionService.activateCondition(conditionConfig, userState1, 1);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestItemTypePositionComparison.this.actor = actor;
                TestItemTypePositionComparison.this.identifier = identifier;
            }
        });

        assertClearActorAndIdentifier();
        builder3B1.setHealth(1.0);
        serverConditionService.onSyncItemBuilt(builder3B1);
        assertClearActorAndIdentifier();
        attacker1B1.setHealth(1.0);
        serverConditionService.onSyncItemBuilt(attacker1B1);
        assertClearActorAndIdentifier();
        attacker2B1.setHealth(1.0);
        serverConditionService.onSyncItemBuilt(attacker2B1);
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemBuilt(building1B1);
        assertClearActorAndIdentifier();
        Thread.sleep(200);
        assertActorAndIdentifierAndClear(userState1, 1);
    }

    @Test
    @DirtiesContext
    public void multipleItemTypeRegionAddExistingNoNewItems1() throws Exception {
        ServerItemService serverItemServiceMock = EasyMock.createMock(ServerItemService.class);
        Collection<SyncBaseItem> syncBaseItems = new ArrayList<>();
        builder1B1.getSyncItemArea().setPosition(new Index(600, 600));
        syncBaseItems.add(builder1B1);
        Region region = createRegion(new Rectangle(500, 500, 1000, 1000), 1);
        EasyMock.expect(serverItemServiceMock.getBaseItemsInRectangle(region, base1.getSimpleBase(), null)).andReturn(syncBaseItems);
        EasyMock.replay(serverItemServiceMock);
        ((ServerPlanetServicesImpl) planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID)).setServerItemService(serverItemServiceMock);

        Map<ItemType, Integer> itemTypes = new HashMap<>();
        itemTypes.put(builder1B1.getBaseItemType(), 1);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_POSITION, new ItemTypePositionComparisonConfig(itemTypes, region, null,
                true), null, null, false);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestItemTypePositionComparison.this.actor = actor;
                TestItemTypePositionComparison.this.identifier = identifier;
            }
        });

        serverConditionService.activateCondition(conditionConfig, userState1, 1);
        assertActorAndIdentifierAndClear(userState1, 1);
    }

    @Test
    @DirtiesContext
    public void multipleItemTypeRegionAddExistingNoNewItems2() throws Exception {
        ServerItemService serverItemServiceMock = EasyMock.createMock(ServerItemService.class);
        Collection<SyncBaseItem> syncBaseItems = new ArrayList<>();
        builder1B1.getSyncItemArea().setPosition(new Index(600, 600));
        syncBaseItems.add(builder1B1);
        builder2B1.getSyncItemArea().setPosition(new Index(600, 600));
        syncBaseItems.add(builder2B1);
        Region region = createRegion(new Rectangle(500, 500, 1000, 1000), 1);
        EasyMock.expect(serverItemServiceMock.getBaseItemsInRectangle(region, base1.getSimpleBase(), null)).andReturn(syncBaseItems);
        EasyMock.replay(serverItemServiceMock);
        ((ServerPlanetServicesImpl) planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID)).setServerItemService(serverItemServiceMock);

        Map<ItemType, Integer> itemTypes = new HashMap<>();
        itemTypes.put(builder1B1.getBaseItemType(), 2);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_POSITION, new ItemTypePositionComparisonConfig(itemTypes, region, null,
                true), null, null, false);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestItemTypePositionComparison.this.actor = actor;
                TestItemTypePositionComparison.this.identifier = identifier;
            }
        });

        serverConditionService.activateCondition(conditionConfig, userState1, 1);
        assertActorAndIdentifierAndClear(userState1, 1);
    }

    @Test
    @DirtiesContext
    public void multipleItemTypeRegionTimeAddExistingNoNewItems() throws Exception {
        ServerItemService serverItemServiceMock = EasyMock.createMock(ServerItemService.class);
        Collection<SyncBaseItem> syncBaseItems = new ArrayList<>();
        builder1B1.getSyncItemArea().setPosition(new Index(600, 600));
        syncBaseItems.add(builder1B1);
        builder2B1.getSyncItemArea().setPosition(new Index(600, 600));
        syncBaseItems.add(builder2B1);
        Region region = createRegion(new Rectangle(500, 500, 1000, 1000), 1);
        EasyMock.expect(serverItemServiceMock.getBaseItemsInRectangle(region, base1.getSimpleBase(), null)).andReturn(syncBaseItems);
        EasyMock.replay(serverItemServiceMock);
        ((ServerPlanetServicesImpl) planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID)).setServerItemService(serverItemServiceMock);

        Map<ItemType, Integer> itemTypes = new HashMap<>();
        itemTypes.put(builder1B1.getBaseItemType(), 2);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_POSITION, new ItemTypePositionComparisonConfig(itemTypes, region, 100,
                true), null, null, false);
        serverConditionService.activateCondition(conditionConfig, userState1, 1);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestItemTypePositionComparison.this.actor = actor;
                TestItemTypePositionComparison.this.identifier = identifier;
            }
        });

        assertClearActorAndIdentifier();
        Thread.sleep(200);
        assertActorAndIdentifierAndClear(userState1, 1);
    }

    @Override
    public void sendSyncInfo(SyncItem syncItem) {

    }

    @Override
    public GameEngineMode getGameEngineMode() {
        return null;
    }

    @Override
    public boolean hasConnection(UserState userState) {
        return false;
    }

    @Override
    public void createConnection(UserState userState, String startUuid) {

    }

    @Override
    public void sendMessage(UserState userState, String key, Object[] args, boolean showRegisterDialog) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean sendPacket(UserState userState, Packet packet) {
        return false; //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void sendPacket(SimpleBase base, Packet packet) {
        this.progressBase = base;
        this.questProgressInfo = ((LevelTaskPacket) packet).getQuestProgressInfo();
        System.out.println("progressString: " + questProgressInfo);
    }

    @Override
    public void sendPacket(Packet packet) {

    }

    @Override
    public void sendMessage(SimpleBase simpleBase, String key, Object[] args, boolean showRegisterDialog) {

    }

    @Override
    public void setChatMessageFilter(UserState userState, ChatMessageFilter chatMessageFilter) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void sendSyncInfos(Collection<SyncBaseItem> syncItem) {

    }

    @Override
    public Collection<OnlineUserDTO> getOnlineConnections() {
        return null;
    }

    @Override
    public void activate() {
    }

    @Override
    public void deactivate() {
    }

    @Override
    public void onLogout() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

}
