package com.btxtech.game.services.utg;

import com.btxtech.game.jsre.client.GameEngineMode;
import com.btxtech.game.jsre.client.cockpit.quest.QuestProgressInfo;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.NoConnectionException;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.packets.LevelTaskPacket;
import com.btxtech.game.jsre.common.packets.Packet;
import com.btxtech.game.jsre.common.utg.ConditionServiceListener;
import com.btxtech.game.jsre.common.utg.condition.AbstractSyncItemComparison;
import com.btxtech.game.jsre.common.utg.config.ConditionConfig;
import com.btxtech.game.jsre.common.utg.config.ConditionTrigger;
import com.btxtech.game.jsre.common.utg.config.CountComparisonConfig;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.connection.Connection;
import com.btxtech.game.services.connection.ServerConnectionService;
import com.btxtech.game.services.planet.Base;
import com.btxtech.game.services.planet.BaseService;
import com.btxtech.game.services.planet.PlanetSystemService;
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

/**
 * User: beat Date: 31.01.2012 Time: 16:51:19
 */
public class TestCountComparison extends AbstractServiceTest implements ServerConnectionService {
    @Autowired
    private ServerConditionService serverConditionService;
    @Autowired
    private PlanetSystemService planetSystemService;
    private Integer identifier;
    private UserState actor;
    private UserState userState1;
    private Base base1;
    private SyncBaseItem building1B1;
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
        userState1.setUser("TestUser1");
        userState1.setDbLevelId(TEST_LEVEL_2_REAL_ID);

        base1 = new Base(userState1, planetSystemService.getPlanet(TEST_PLANET_1_ID), 1);
        int itemId = 0;
        building1B1 = createSyncBaseItem(TEST_SIMPLE_BUILDING_ID, new Index(100, 100), new Id(++itemId, 0, 0), createMockGlobalServices(), createMockPlanetServices(), base1.getSimpleBase());

        BaseService baseServiceMock = EasyMock.createNiceMock(BaseService.class);
        EasyMock.expect(baseServiceMock.getUserState(base1.getSimpleBase())).andReturn(userState1).anyTimes();
        EasyMock.replay(baseServiceMock);

        ((ServerConditionServiceImpl) deAopProxy(serverConditionService)).setRate(50);
        ServerPlanetServicesImpl serverPlanetServices = (ServerPlanetServicesImpl) planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID);
        serverPlanetServices.setBaseService(baseServiceMock);
        overrideConnectionService(serverPlanetServices, this);
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

    private void assertAndClearProgressString(String expected, SimpleBase simpleBase) {
        Assert.assertEquals(expected, questProgressInfo);
        Assert.assertEquals(simpleBase, progressBase);
        questProgressInfo = null;
    }

    private void assertProgressStringFromService(String expected) {
        Assert.assertEquals(expected, serverConditionService.getQuestProgressInfo(userState1, 1));
    }

    @Test
    @DirtiesContext
    public void test1Money() throws Exception {
        // Does not make any sense
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.MONEY_INCREASED, new CountComparisonConfig(1, "Money #C / 1"), null);
        serverConditionService.activateCondition(conditionConfig, userState1, 1);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestCountComparison.this.actor = actor;
                TestCountComparison.this.identifier = identifier;
            }
        });
        assertProgressStringFromService("Money 0 / 1");
        assertClearProgressString();
        assertClearActorAndIdentifier();
        serverConditionService.onMoneyIncrease(base1.getSimpleBase(), 0.5);
        assertClearActorAndIdentifier();
        assertAndClearProgressString("Money 0 / 1", base1.getSimpleBase());
        serverConditionService.onMoneyIncrease(base1.getSimpleBase(), 0.5);
        assertActorAndIdentifierAndClear(userState1, 1);
        assertAndClearProgressString("Money 1 / 1", base1.getSimpleBase());
    }

    @Test
    @DirtiesContext
    public void test100Money() throws Exception {
        // Does not make any sense
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.MONEY_INCREASED, new CountComparisonConfig(100, "#C / 100"), null);
        serverConditionService.activateCondition(conditionConfig, userState1, 1);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestCountComparison.this.actor = actor;
                TestCountComparison.this.identifier = identifier;
            }
        });
        assertProgressStringFromService("0 / 100");
        assertClearProgressString();
        assertClearActorAndIdentifier();
        serverConditionService.onMoneyIncrease(base1.getSimpleBase(), 0.5);
        assertClearActorAndIdentifier();
        assertAndClearProgressString("0 / 100", base1.getSimpleBase());
        serverConditionService.onMoneyIncrease(base1.getSimpleBase(), 2.5);
        assertClearActorAndIdentifier();
        assertAndClearProgressString("3 / 100", base1.getSimpleBase());
        serverConditionService.onMoneyIncrease(base1.getSimpleBase(), 97);
        assertActorAndIdentifierAndClear(userState1, 1);
        assertAndClearProgressString("100 / 100", base1.getSimpleBase());
    }

    @Test
    @DirtiesContext
    public void test1Built() throws Exception {
        // Does not make any sense
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_BUILT, new CountComparisonConfig(1, "Item#C"), null);
        serverConditionService.activateCondition(conditionConfig, userState1, 1);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestCountComparison.this.actor = actor;
                TestCountComparison.this.identifier = identifier;
            }
        });
        assertProgressStringFromService("Item0");
        assertClearProgressString();
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemBuilt(building1B1);
        assertActorAndIdentifierAndClear(userState1, 1);
        assertAndClearProgressString("Item1", base1.getSimpleBase());
    }

    @Test
    @DirtiesContext
    public void test3Built() throws Exception {
        // Does not make any sense
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_BUILT, new CountComparisonConfig(3, "Item#C / 3"), null);
        serverConditionService.activateCondition(conditionConfig, userState1, 1);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestCountComparison.this.actor = actor;
                TestCountComparison.this.identifier = identifier;
            }
        });
        assertProgressStringFromService("Item0 / 3");
        assertClearProgressString();
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemBuilt(building1B1);
        assertClearActorAndIdentifier();
        assertAndClearProgressString("Item1 / 3", base1.getSimpleBase());
        serverConditionService.onSyncItemBuilt(building1B1);
        assertClearActorAndIdentifier();
        assertAndClearProgressString("Item2 / 3", base1.getSimpleBase());
        serverConditionService.onSyncItemBuilt(building1B1);
        assertActorAndIdentifierAndClear(userState1, 1);
        assertAndClearProgressString("Item3 / 3", base1.getSimpleBase());
    }


    @Override
    public void sendSyncInfo(SyncItem syncItem) {

    }

    @Override
    public GameEngineMode getGameEngineMode() {
        return null;
    }

    @Override
    public boolean hasConnection(SimpleBase simpleBase) {
        return false;
    }

    @Override
    public Connection getConnection(String startUuid) throws NoConnectionException {
        return null;
    }

    @Override
    public void createConnection(Base base, String startUuid) {

    }

    @Override
    public void closeConnection(SimpleBase simpleBase, NoConnectionException.Type closedReason) {

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
    public void sendSyncInfos(Collection<SyncBaseItem> syncItem) {

    }

    @Override
    public Collection<SimpleBase> getOnlineBases() {
        return null;
    }

    @Override
    public void activate() {
    }

    @Override
    public void deactivate() {
    }
}
