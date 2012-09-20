package com.btxtech.game.services.utg;

import com.btxtech.game.jsre.client.GameEngineMode;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.NoConnectionException;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.packets.ChatMessage;
import com.btxtech.game.jsre.common.packets.LevelTaskPacket;
import com.btxtech.game.jsre.common.packets.Packet;
import com.btxtech.game.jsre.common.utg.ConditionServiceListener;
import com.btxtech.game.jsre.common.utg.condition.AbstractSyncItemComparison;
import com.btxtech.game.jsre.common.utg.config.ConditionConfig;
import com.btxtech.game.jsre.common.utg.config.ConditionTrigger;
import com.btxtech.game.jsre.common.utg.config.SyncItemTypeComparisonConfig;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
    private String progressString;

    @Before
    public void before() throws Exception {
        progressString = null;
        progressBase = null;
        setPrivateStaticField(AbstractSyncItemComparison.class, "MIN_SEND_DELAY", 0);
        configureSimplePlanet();
        identifier = null;
        actor = null;

        // Mock objects
        userState1 = new UserState();
        userState1.setUser("TestUser1");
        userState1.setDbLevelId(TEST_LEVEL_2_REAL_ID);
        base1 = new Base(userState1, planetSystemService.getPlanet(TEST_PLANET_1_ID), 1);
        int itemId = 0;
        builder1B1 = createSyncBaseItem(TEST_START_BUILDER_ITEM_ID, new Index(100, 100), new Id(++itemId, 0, 0), createMockGlobalServices(), createMockPlanetServices(), base1.getSimpleBase());
        attacker1B1 = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(100, 100), new Id(++itemId, 0, 0), createMockGlobalServices(), createMockPlanetServices(), base1.getSimpleBase());

        UserState userState2 = new UserState();
        userState2.setUser("TestUser2");
        userState2.setDbLevelId(TEST_LEVEL_2_REAL_ID);
        base2 = new Base(userState2, planetSystemService.getPlanet(TEST_PLANET_1_ID), 2);
        builder1B2 = createSyncBaseItem(TEST_START_BUILDER_ITEM_ID, new Index(100, 100), new Id(++itemId, 0, 0), createMockGlobalServices(), createMockPlanetServices(), base2.getSimpleBase());

        BaseService baseServiceMock = EasyMock.createNiceMock(BaseService.class);
        EasyMock.expect(baseServiceMock.getUserState(base1.getSimpleBase())).andReturn(userState1).anyTimes();
        EasyMock.replay(baseServiceMock);

        ((ServerConditionServiceImpl) deAopProxy(serverConditionService)).setRate(50);
        ((ServerPlanetServicesImpl)planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID)).setBaseService(baseServiceMock);
        setPrivateField(ServerConditionServiceImpl.class, serverConditionService, "serverConnectionService", this);
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
        Assert.assertNull(progressString);
    }

    private void assertAndClearProgressString(String expected, SimpleBase simpleBase) {
        Assert.assertEquals(expected, progressString);
        Assert.assertEquals(simpleBase, progressBase);
        progressString = null;
    }

    private void assertProgressStringFromService(String expected) {
        Assert.assertEquals(expected, serverConditionService.getProgressHtml(userState1, 1));
    }

    @Test
    @DirtiesContext
    public void build1Item() throws Exception {
        Map<ItemType, Integer> itemTypes = new HashMap<>();
        itemTypes.put(builder1B1.getBaseItemType(), 1);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_BUILT, new SyncItemTypeComparisonConfig(itemTypes, "Item #C" + builder1B1.getBaseItemType().getId()), null);
        serverConditionService.activateCondition(conditionConfig, userState1, 1);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestSyncItemTypeComparison.this.actor = actor;
                TestSyncItemTypeComparison.this.identifier = identifier;
            }
        });
        assertProgressStringFromService("Item 0");
        assertClearProgressString();
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemBuilt(builder1B2);
        assertClearProgressString();
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemBuilt(builder1B1);
        assertAndClearProgressString("Item 1", base1.getSimpleBase());
        assertActorAndIdentifierAndClear(userState1, 1);
    }

    @Test
    @DirtiesContext
    public void build3Item() throws Exception {
        Map<ItemType, Integer> itemTypes = new HashMap<>();
        itemTypes.put(builder1B1.getBaseItemType(), 3);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_BUILT, new SyncItemTypeComparisonConfig(itemTypes, "Item #C" + builder1B1.getBaseItemType().getId()), null);
        serverConditionService.activateCondition(conditionConfig, userState1, 1);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestSyncItemTypeComparison.this.actor = actor;
                TestSyncItemTypeComparison.this.identifier = identifier;
            }
        });
        assertProgressStringFromService("Item 0");
        assertClearProgressString();
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemBuilt(builder1B1);
        assertAndClearProgressString("Item 1", base1.getSimpleBase());
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemBuilt(builder1B2);
        assertClearProgressString();
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemBuilt(builder1B1);
        assertAndClearProgressString("Item 2", base1.getSimpleBase());
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemBuilt(builder1B1);
        assertAndClearProgressString("Item 3", base1.getSimpleBase());
        assertActorAndIdentifierAndClear(userState1, 1);
    }

    @Test
    @DirtiesContext
    public void buildMultipeItems() throws Exception {
        Map<ItemType, Integer> itemTypes = new HashMap<>();
        itemTypes.put(builder1B1.getBaseItemType(), 2);
        itemTypes.put(attacker1B1.getBaseItemType(), 1);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_BUILT, new SyncItemTypeComparisonConfig(itemTypes, "Item #C" + builder1B1.getBaseItemType().getId()
                + " #C" + attacker1B1.getBaseItemType().getId()), null);
        serverConditionService.activateCondition(conditionConfig, userState1, 1);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestSyncItemTypeComparison.this.actor = actor;
                TestSyncItemTypeComparison.this.identifier = identifier;
            }
        });
        assertProgressStringFromService("Item 0 0");
        assertClearProgressString();
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemBuilt(builder1B1);
        assertAndClearProgressString("Item 1 0", base1.getSimpleBase());
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemBuilt(builder1B2);
        assertClearProgressString();
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemBuilt(attacker1B1);
        assertAndClearProgressString("Item 1 1", base1.getSimpleBase());
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemBuilt(builder1B1);
        assertAndClearProgressString("Item 2 1", base1.getSimpleBase());
        assertActorAndIdentifierAndClear(userState1, 1);
    }

    @Test
    @DirtiesContext
    public void killed1Item() throws Exception {
        Map<ItemType, Integer> itemTypes = new HashMap<>();
        itemTypes.put(builder1B1.getBaseItemType(), 1);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_KILLED, new SyncItemTypeComparisonConfig(itemTypes, "Kill #C" + builder1B2.getBaseItemType().getId()), null);
        serverConditionService.activateCondition(conditionConfig, userState1, 1);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestSyncItemTypeComparison.this.actor = actor;
                TestSyncItemTypeComparison.this.identifier = identifier;
            }
        });
        assertProgressStringFromService("Kill 0");
        assertClearProgressString();
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemKilled(base2.getSimpleBase(), builder1B1);
        assertClearProgressString();
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemKilled(base1.getSimpleBase(), builder1B2);
        assertAndClearProgressString("Kill 1", base1.getSimpleBase());
        assertActorAndIdentifierAndClear(userState1, 1);
    }

    @Test
    @DirtiesContext
    public void killed3Item() throws Exception {
        Map<ItemType, Integer> itemTypes = new HashMap<>();
        itemTypes.put(builder1B1.getBaseItemType(), 3);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_KILLED, new SyncItemTypeComparisonConfig(itemTypes, "Kill #C" + builder1B2.getBaseItemType().getId()), null);
        serverConditionService.activateCondition(conditionConfig, userState1, 1);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestSyncItemTypeComparison.this.actor = actor;
                TestSyncItemTypeComparison.this.identifier = identifier;
            }
        });
        assertProgressStringFromService("Kill 0");
        assertClearProgressString();
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemKilled(base2.getSimpleBase(), builder1B1);
        assertClearProgressString();
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemKilled(base1.getSimpleBase(), builder1B2);
        assertAndClearProgressString("Kill 1", base1.getSimpleBase());
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemKilled(base1.getSimpleBase(), builder1B2);
        assertAndClearProgressString("Kill 2", base1.getSimpleBase());
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemKilled(base1.getSimpleBase(), builder1B2);
        assertAndClearProgressString("Kill 3", base1.getSimpleBase());
        assertActorAndIdentifierAndClear(userState1, 1);
    }

    @Test
    @DirtiesContext
    public void killedMultipleItem() throws Exception {
        Map<ItemType, Integer> itemTypes = new HashMap<>();
        itemTypes.put(builder1B1.getBaseItemType(), 3);
        itemTypes.put(attacker1B1.getBaseItemType(), 1);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_KILLED, new SyncItemTypeComparisonConfig(itemTypes, "Kill #C" + builder1B2.getBaseItemType().getId()
                + " #C" + attacker1B1.getBaseItemType().getId()), null);
        serverConditionService.activateCondition(conditionConfig, userState1, 1);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestSyncItemTypeComparison.this.actor = actor;
                TestSyncItemTypeComparison.this.identifier = identifier;
            }
        });
        assertProgressStringFromService("Kill 0 0");
        assertClearProgressString();
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemKilled(base2.getSimpleBase(), builder1B1);
        assertClearProgressString();
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemKilled(base1.getSimpleBase(), builder1B2);
        assertAndClearProgressString("Kill 1 0", base1.getSimpleBase());
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemKilled(base1.getSimpleBase(), builder1B2);
        assertAndClearProgressString("Kill 2 0", base1.getSimpleBase());
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemKilled(base1.getSimpleBase(), attacker1B1);
        assertAndClearProgressString("Kill 2 1", base1.getSimpleBase());
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemKilled(base1.getSimpleBase(), builder1B2);
        assertAndClearProgressString("Kill 3 1", base1.getSimpleBase());
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
    public void clientLog(String message, Date date) {

    }

    @Override
    public boolean hasConnection() {
        return false;
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
        this.progressString = ((LevelTaskPacket) packet).getActiveQuestProgress();
        System.out.println("progressString: " + progressString);
    }

    @Override
    public void sendPacket(Packet packet) {

    }

    @Override
    public void sendSyncInfos(Collection<SyncBaseItem> syncItem) {

    }

    @Override
    public void sendChatMessage(ChatMessage chatMessage) {

    }

    @Override
    public List<ChatMessage> pollChatMessages(Integer lastMessageId) {
        return null;
    }

    @Override
    public Collection<SimpleBase> getOnlineBases() {
        return null;
    }

}
