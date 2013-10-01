package com.btxtech.game.services.utg;

import com.btxtech.game.jsre.client.GameEngineMode;
import com.btxtech.game.jsre.client.cockpit.chat.ChatMessageFilter;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.LevelScope;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.PlanetLiteInfo;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.packets.Packet;
import com.btxtech.game.jsre.common.utg.ConditionServiceListener;
import com.btxtech.game.jsre.common.utg.condition.AbstractUpdatingComparison;
import com.btxtech.game.jsre.common.utg.config.ConditionConfig;
import com.btxtech.game.jsre.common.utg.config.ConditionTrigger;
import com.btxtech.game.jsre.common.utg.config.CountComparisonConfig;
import com.btxtech.game.jsre.common.utg.config.ItemTypePositionComparisonConfig;
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
public class TestConditionServiceWrongPlanet extends AbstractServiceTest implements ServerConnectionService {
    @Autowired
    private ServerConditionService serverConditionService;
    @Autowired
    private PlanetSystemService planetSystemService;
    private Integer identifier;
    private UserState actor;
    private UserState userState1;
    private Base base1Planet1;
    private SyncBaseItem builder1B1Planet1;
    private SyncBaseItem attacker1B1Planet1;
    private Base base2Planet1;
    private SyncBaseItem builder1B2Planet1;

    @Before
    public void before() throws Exception {
        setPrivateStaticField(AbstractUpdatingComparison.class, "MIN_SEND_DELAY", 0);
        configureMultiplePlanetsAndLevels();
        identifier = null;
        actor = null;

        // Mock objects
        userState1 = new UserState();
        userState1.setUser(1);
        userState1.setDbLevelId(TEST_LEVEL_2_REAL_ID);
        base1Planet1 = new Base(userState1, planetSystemService.getPlanet(TEST_PLANET_1_ID), 1);
        int itemId = 0;
        builder1B1Planet1 = createSyncBaseItem(TEST_START_BUILDER_ITEM_ID, new Index(100, 100), new Id(++itemId, 0), createMockGlobalServices(), createMockPlanetServices(), base1Planet1.getSimpleBase());
        attacker1B1Planet1 = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(100, 100), new Id(++itemId, 0), createMockGlobalServices(), createMockPlanetServices(), base1Planet1.getSimpleBase());

        UserState userState2 = new UserState();
        userState2.setUser(2);
        userState2.setDbLevelId(TEST_LEVEL_2_REAL_ID);
        base2Planet1 = new Base(userState2, planetSystemService.getPlanet(TEST_PLANET_1_ID), 2);
        builder1B2Planet1 = createSyncBaseItem(TEST_START_BUILDER_ITEM_ID, new Index(100, 100), new Id(++itemId, 0), createMockGlobalServices(), createMockPlanetServices(), base2Planet1.getSimpleBase());

        BaseService baseServiceMock = EasyMock.createNiceMock(BaseService.class);
        EasyMock.expect(baseServiceMock.getUserState(base1Planet1.getSimpleBase())).andReturn(userState1).anyTimes();
        EasyMock.expect(baseServiceMock.getUserState(base2Planet1.getSimpleBase())).andReturn(userState2).anyTimes();
        EasyMock.replay(baseServiceMock);

        LevelScope levelScope2 = new LevelScope(new PlanetLiteInfo(TEST_PLANET_2_ID, "", null), 0, 0, null, 0);
        UserGuidanceService userGuidanceServiceMock = EasyMock.createNiceMock(UserGuidanceService.class);
        EasyMock.expect(userGuidanceServiceMock.getLevelScope(userState1)).andReturn(levelScope2).anyTimes();
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

    @Test
    @DirtiesContext
    public void tutorial() throws Exception {
        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestConditionServiceWrongPlanet.this.actor = actor;
                TestConditionServiceWrongPlanet.this.identifier = identifier;
            }
        });

        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.TUTORIAL, null, null, null, false);
        serverConditionService.activateCondition(conditionConfig, userState1, 1);
        assertClearActorAndIdentifier();
        serverConditionService.onTutorialFinished(userState1, 1);
        assertActorAndIdentifierAndClear(userState1, 1);
    }

    @Test
    @DirtiesContext
    public void xp() throws Exception {
        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestConditionServiceWrongPlanet.this.actor = actor;
                TestConditionServiceWrongPlanet.this.identifier = identifier;
            }
        });

        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.XP_INCREASED, new CountComparisonConfig(1), null, null, false);
        serverConditionService.activateCondition(conditionConfig, userState1, 1);
        assertClearActorAndIdentifier();
        serverConditionService.onIncreaseXp(userState1, 1);
        assertActorAndIdentifierAndClear(userState1, 1);
    }

    @Test
    @DirtiesContext
    public void singleItemType() throws Exception {
        Map<ItemType, Integer> itemTypes = new HashMap<>();
        itemTypes.put(builder1B1Planet1.getBaseItemType(), 1);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_POSITION, new ItemTypePositionComparisonConfig(itemTypes, null, null, false), null, null, false);
        serverConditionService.activateCondition(conditionConfig, userState1, 1);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestConditionServiceWrongPlanet.this.actor = actor;
                TestConditionServiceWrongPlanet.this.identifier = identifier;
            }
        });

        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemDeactivated(builder1B1Planet1);
        assertClearActorAndIdentifier();
    }

    @Test
    @DirtiesContext
    public void buildItem() throws Exception {
        Map<ItemType, Integer> itemTypes = new HashMap<>();
        itemTypes.put(builder1B1Planet1.getBaseItemType(), 1);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_BUILT, new SyncItemTypeComparisonConfig(itemTypes), null, null, false);
        serverConditionService.activateCondition(conditionConfig, userState1, 1);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestConditionServiceWrongPlanet.this.actor = actor;
                TestConditionServiceWrongPlanet.this.identifier = identifier;
            }
        });

        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemBuilt(builder1B1Planet1);
        assertClearActorAndIdentifier();
    }

    @Test
    @DirtiesContext
    public void killedItem() throws Exception {
        Map<ItemType, Integer> itemTypes = new HashMap<>();
        itemTypes.put(builder1B1Planet1.getBaseItemType(), 1);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_KILLED, new SyncItemTypeComparisonConfig(itemTypes), null, null, false);
        serverConditionService.activateCondition(conditionConfig, userState1, 1);
        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestConditionServiceWrongPlanet.this.actor = actor;
                TestConditionServiceWrongPlanet.this.identifier = identifier;
            }
        });
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemKilled(base1Planet1.getSimpleBase(), builder1B2Planet1);
        assertClearActorAndIdentifier();
    }

    @Test
    @DirtiesContext
    public void testMoney() {
        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestConditionServiceWrongPlanet.this.actor = actor;
                TestConditionServiceWrongPlanet.this.identifier = identifier;
            }
        });

        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.MONEY_INCREASED, new CountComparisonConfig(1), null, null, false);
        serverConditionService.activateCondition(conditionConfig, userState1, 1);
        assertClearActorAndIdentifier();
        serverConditionService.onMoneyIncrease(base1Planet1.getSimpleBase(), 1.0);
        assertClearActorAndIdentifier();
    }

    @Test
    @DirtiesContext
    public void baseDeleted() throws Exception {
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.BASE_KILLED, new CountComparisonConfig(1), null, null, false);
        serverConditionService.activateCondition(conditionConfig, userState1, 1);
        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestConditionServiceWrongPlanet.this.actor = actor;
                TestConditionServiceWrongPlanet.this.identifier = identifier;
            }
        });
        serverConditionService.activateCondition(conditionConfig, userState1, 1);
        assertClearActorAndIdentifier();
        serverConditionService.onBaseDeleted(base1Planet1.getSimpleBase());
        assertClearActorAndIdentifier();
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
