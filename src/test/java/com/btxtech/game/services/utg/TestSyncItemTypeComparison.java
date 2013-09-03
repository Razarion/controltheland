package com.btxtech.game.services.utg;

import com.btxtech.game.jsre.client.GameEngineMode;
import com.btxtech.game.jsre.client.cockpit.chat.ChatMessageFilter;
import com.btxtech.game.jsre.client.cockpit.quest.QuestProgressInfo;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.LevelScope;
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
import com.btxtech.game.jsre.common.utg.config.SyncItemTypeComparisonConfig;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.connection.OnlineUserDTO;
import com.btxtech.game.services.connection.ServerConnectionService;
import com.btxtech.game.services.planet.Base;
import com.btxtech.game.services.planet.BaseService;
import com.btxtech.game.services.planet.PlanetSystemService;
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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * User: beat Date: 31.01.2012 Time: 16:51:19
 */
public class TestSyncItemTypeComparison extends AbstractServiceTest implements ServerConnectionService {
    @Autowired
    private ServerConditionService serverConditionService;
    @Autowired
    private PlanetSystemService planetSystemService;
    private Integer identifier;
    private UserState actor;
    private UserState userState1;
    private Base base1;
    private SyncBaseItem builder1B1;
    private SyncBaseItem attacker1B1;
    private Base base2;
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
        attacker1B1 = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(100, 100), new Id(++itemId, 0), createMockGlobalServices(), createMockPlanetServices(), base1.getSimpleBase());

        UserState userState2 = new UserState();
        userState2.setUser(2);
        userState2.setDbLevelId(TEST_LEVEL_2_REAL_ID);
        base2 = new Base(userState2, planetSystemService.getPlanet(TEST_PLANET_1_ID), 2);
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

    private void assertQuestProgressInfoFromService(ConditionTrigger conditionTrigger, Map<Integer, QuestProgressInfo.Amount> expectedAmountMap) {
        QuestProgressInfo questProgressInfo = serverConditionService.getQuestProgressInfo(userState1, 1);
        assertQuestProgressInfo(conditionTrigger, expectedAmountMap, questProgressInfo);
    }

    private void assertAndClearQuestProgressInfo(ConditionTrigger conditionTrigger, Map<Integer, QuestProgressInfo.Amount> expectedAmountMap, SimpleBase simpleBase) {
        assertQuestProgressInfo(conditionTrigger, expectedAmountMap, questProgressInfo);
        Assert.assertEquals(simpleBase, progressBase);
        questProgressInfo = null;
    }

    private void assertQuestProgressInfo(ConditionTrigger conditionTrigger, Map<Integer, QuestProgressInfo.Amount> expectedAmountMap, QuestProgressInfo questProgressInfo) {
        Assert.assertEquals(conditionTrigger, questProgressInfo.getConditionTrigger());
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
    public void build1Item() throws Exception {
        Map<ItemType, Integer> itemTypes = new HashMap<>();
        itemTypes.put(builder1B1.getBaseItemType(), 1);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_BUILT, new SyncItemTypeComparisonConfig(itemTypes), null, null, false);
        serverConditionService.activateCondition(conditionConfig, userState1, 1);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestSyncItemTypeComparison.this.actor = actor;
                TestSyncItemTypeComparison.this.identifier = identifier;
            }
        });

        Map<Integer, QuestProgressInfo.Amount> expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(0, 1));
        assertQuestProgressInfoFromService(ConditionTrigger.SYNC_ITEM_BUILT, expectedAmountMap);
        assertClearProgressString();
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemBuilt(builder1B2);
        assertClearProgressString();
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemBuilt(builder1B1);
        expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(1, 1));
        assertAndClearQuestProgressInfo(ConditionTrigger.SYNC_ITEM_BUILT, expectedAmountMap, base1.getSimpleBase());
        assertActorAndIdentifierAndClear(userState1, 1);
    }

    @Test
    @DirtiesContext
    public void build3Item() throws Exception {
        Map<ItemType, Integer> itemTypes = new HashMap<>();
        itemTypes.put(builder1B1.getBaseItemType(), 3);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_BUILT, new SyncItemTypeComparisonConfig(itemTypes), null, null, false);
        serverConditionService.activateCondition(conditionConfig, userState1, 1);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestSyncItemTypeComparison.this.actor = actor;
                TestSyncItemTypeComparison.this.identifier = identifier;
            }
        });
        Map<Integer, QuestProgressInfo.Amount> expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(0, 3));
        assertQuestProgressInfoFromService(ConditionTrigger.SYNC_ITEM_BUILT, expectedAmountMap);
        assertClearProgressString();
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemBuilt(builder1B1);
        expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(1, 3));
        assertAndClearQuestProgressInfo(ConditionTrigger.SYNC_ITEM_BUILT, expectedAmountMap, base1.getSimpleBase());
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemBuilt(builder1B2);
        assertClearProgressString();
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemBuilt(builder1B1);
        expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(2, 3));
        assertAndClearQuestProgressInfo(ConditionTrigger.SYNC_ITEM_BUILT, expectedAmountMap, base1.getSimpleBase());
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemBuilt(builder1B1);
        expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(3, 3));
        assertAndClearQuestProgressInfo(ConditionTrigger.SYNC_ITEM_BUILT, expectedAmountMap, base1.getSimpleBase());
        assertActorAndIdentifierAndClear(userState1, 1);
    }

    @Test
    @DirtiesContext
    public void buildMultipeItems() throws Exception {
        Map<ItemType, Integer> itemTypes = new HashMap<>();
        itemTypes.put(builder1B1.getBaseItemType(), 2);
        itemTypes.put(attacker1B1.getBaseItemType(), 1);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_BUILT, new SyncItemTypeComparisonConfig(itemTypes), null, null, false);
        serverConditionService.activateCondition(conditionConfig, userState1, 1);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestSyncItemTypeComparison.this.actor = actor;
                TestSyncItemTypeComparison.this.identifier = identifier;
            }
        });
        Map<Integer, QuestProgressInfo.Amount> expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(0, 2));
        expectedAmountMap.put(TEST_ATTACK_ITEM_ID, new QuestProgressInfo.Amount(0, 1));
        assertQuestProgressInfoFromService(ConditionTrigger.SYNC_ITEM_BUILT, expectedAmountMap);
        assertClearProgressString();
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemBuilt(builder1B1);
        expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(1, 2));
        expectedAmountMap.put(TEST_ATTACK_ITEM_ID, new QuestProgressInfo.Amount(0, 1));
        assertAndClearQuestProgressInfo(ConditionTrigger.SYNC_ITEM_BUILT, expectedAmountMap, base1.getSimpleBase());
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemBuilt(builder1B2);
        assertClearProgressString();
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemBuilt(attacker1B1);
        expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(1, 2));
        expectedAmountMap.put(TEST_ATTACK_ITEM_ID, new QuestProgressInfo.Amount(1, 1));
        assertAndClearQuestProgressInfo(ConditionTrigger.SYNC_ITEM_BUILT, expectedAmountMap, base1.getSimpleBase());
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemBuilt(builder1B1);
        expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(2, 2));
        expectedAmountMap.put(TEST_ATTACK_ITEM_ID, new QuestProgressInfo.Amount(1, 1));
        assertAndClearQuestProgressInfo(ConditionTrigger.SYNC_ITEM_BUILT, expectedAmountMap, base1.getSimpleBase());
        assertActorAndIdentifierAndClear(userState1, 1);
    }

    @Test
    @DirtiesContext
    public void killed1Item() throws Exception {
        Map<ItemType, Integer> itemTypes = new HashMap<>();
        itemTypes.put(builder1B1.getBaseItemType(), 1);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_KILLED, new SyncItemTypeComparisonConfig(itemTypes), null, null, false);
        serverConditionService.activateCondition(conditionConfig, userState1, 1);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestSyncItemTypeComparison.this.actor = actor;
                TestSyncItemTypeComparison.this.identifier = identifier;
            }
        });
        Map<Integer, QuestProgressInfo.Amount> expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(0, 1));
        assertQuestProgressInfoFromService(ConditionTrigger.SYNC_ITEM_KILLED, expectedAmountMap);
        assertClearProgressString();
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemKilled(base2.getSimpleBase(), builder1B1);
        assertClearProgressString();
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemKilled(base1.getSimpleBase(), builder1B2);
        expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(1, 1));
        assertAndClearQuestProgressInfo(ConditionTrigger.SYNC_ITEM_KILLED, expectedAmountMap, base1.getSimpleBase());
        assertActorAndIdentifierAndClear(userState1, 1);
    }

    @Test
    @DirtiesContext
    public void killed3Item() throws Exception {
        Map<ItemType, Integer> itemTypes = new HashMap<>();
        itemTypes.put(builder1B1.getBaseItemType(), 3);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_KILLED, new SyncItemTypeComparisonConfig(itemTypes), null, null, false);
        serverConditionService.activateCondition(conditionConfig, userState1, 1);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestSyncItemTypeComparison.this.actor = actor;
                TestSyncItemTypeComparison.this.identifier = identifier;
            }
        });
        Map<Integer, QuestProgressInfo.Amount> expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(0, 3));
        assertQuestProgressInfoFromService(ConditionTrigger.SYNC_ITEM_KILLED, expectedAmountMap);
        assertClearProgressString();
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemKilled(base2.getSimpleBase(), builder1B1);
        assertClearProgressString();
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemKilled(base1.getSimpleBase(), builder1B2);
        expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(1, 3));
        assertAndClearQuestProgressInfo(ConditionTrigger.SYNC_ITEM_KILLED, expectedAmountMap, base1.getSimpleBase());
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemKilled(base1.getSimpleBase(), builder1B2);
        expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(2, 3));
        assertAndClearQuestProgressInfo(ConditionTrigger.SYNC_ITEM_KILLED, expectedAmountMap, base1.getSimpleBase());
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemKilled(base1.getSimpleBase(), builder1B2);
        expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(3, 3));
        assertAndClearQuestProgressInfo(ConditionTrigger.SYNC_ITEM_KILLED, expectedAmountMap, base1.getSimpleBase());
        assertActorAndIdentifierAndClear(userState1, 1);
    }

    @Test
    @DirtiesContext
    public void killedMultipleItem() throws Exception {
        Map<ItemType, Integer> itemTypes = new HashMap<>();
        itemTypes.put(builder1B1.getBaseItemType(), 3);
        itemTypes.put(attacker1B1.getBaseItemType(), 1);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_KILLED, new SyncItemTypeComparisonConfig(itemTypes), null, null, false);
        serverConditionService.activateCondition(conditionConfig, userState1, 1);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestSyncItemTypeComparison.this.actor = actor;
                TestSyncItemTypeComparison.this.identifier = identifier;
            }
        });
        Map<Integer, QuestProgressInfo.Amount> expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(0, 3));
        expectedAmountMap.put(TEST_ATTACK_ITEM_ID, new QuestProgressInfo.Amount(0, 1));
        assertQuestProgressInfoFromService(ConditionTrigger.SYNC_ITEM_KILLED, expectedAmountMap);
        assertClearProgressString();
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemKilled(base2.getSimpleBase(), builder1B1);
        assertClearProgressString();
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemKilled(base1.getSimpleBase(), builder1B2);
        expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(1, 3));
        expectedAmountMap.put(TEST_ATTACK_ITEM_ID, new QuestProgressInfo.Amount(0, 1));
        assertAndClearQuestProgressInfo(ConditionTrigger.SYNC_ITEM_KILLED, expectedAmountMap, base1.getSimpleBase());
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemKilled(base1.getSimpleBase(), builder1B2);
        expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(2, 3));
        expectedAmountMap.put(TEST_ATTACK_ITEM_ID, new QuestProgressInfo.Amount(0, 1));
        assertAndClearQuestProgressInfo(ConditionTrigger.SYNC_ITEM_KILLED, expectedAmountMap, base1.getSimpleBase());
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemKilled(base1.getSimpleBase(), attacker1B1);
        expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(2, 3));
        expectedAmountMap.put(TEST_ATTACK_ITEM_ID, new QuestProgressInfo.Amount(1, 1));
        assertAndClearQuestProgressInfo(ConditionTrigger.SYNC_ITEM_KILLED, expectedAmountMap, base1.getSimpleBase());
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemKilled(base1.getSimpleBase(), builder1B2);
        expectedAmountMap = new HashMap<>();
        expectedAmountMap.put(TEST_START_BUILDER_ITEM_ID, new QuestProgressInfo.Amount(3, 3));
        expectedAmountMap.put(TEST_ATTACK_ITEM_ID, new QuestProgressInfo.Amount(1, 1));
        assertAndClearQuestProgressInfo(ConditionTrigger.SYNC_ITEM_KILLED, expectedAmountMap, base1.getSimpleBase());
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
        return false;//To change body of implemented methods use File | Settings | File Templates.
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
