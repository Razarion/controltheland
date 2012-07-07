package com.btxtech.game.services.utg;

import com.btxtech.game.jsre.client.GameEngineMode;
import com.btxtech.game.jsre.common.packets.ChatMessage;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.NoConnectionException;
import com.btxtech.game.jsre.common.packets.LevelTaskPacket;
import com.btxtech.game.jsre.common.packets.Packet;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.utg.ConditionServiceListener;
import com.btxtech.game.jsre.common.utg.condition.AbstractSyncItemComparison;
import com.btxtech.game.jsre.common.utg.config.ConditionConfig;
import com.btxtech.game.jsre.common.utg.config.ConditionTrigger;
import com.btxtech.game.jsre.common.utg.config.ItemTypePositionComparisonConfig;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.base.Base;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.common.ServerServices;
import com.btxtech.game.services.connection.Connection;
import com.btxtech.game.services.connection.ConnectionService;
import com.btxtech.game.services.item.ItemService;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: beat Date: 31.01.2012 Time: 16:51:19
 */
public class TestItemTypePositionComparison extends AbstractServiceTest implements ConnectionService {
    @Autowired
    private ServerConditionService serverConditionService;
    @Autowired
    private ServerServices serverServices;
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
    private String progressString;

    @Before
    public void before() throws Exception {
        progressString = null;
        progressBase = null;
        setPrivateStaticField(AbstractSyncItemComparison.class, "MIN_SEND_DELAY", 0);
        configureRealGame();
        identifier = null;
        actor = null;

        // Mock objects
        userState1 = new UserState();
        userState1.setUser("TestUser1");
        base1 = new Base(userState1, 1);
        int itemId = 0;
        builder1B1 = createSyncBaseItem(TEST_START_BUILDER_ITEM_ID, new Index(100, 100), new Id(++itemId, 0, 0), createMockServices(), base1.getSimpleBase());
        builder2B1 = createSyncBaseItem(TEST_START_BUILDER_ITEM_ID, new Index(100, 100), new Id(++itemId, 0, 0), createMockServices(), base1.getSimpleBase());
        builder3B1 = createSyncBaseItem(TEST_START_BUILDER_ITEM_ID, new Index(100, 100), new Id(++itemId, 0, 0), createMockServices(), base1.getSimpleBase());
        attacker1B1 = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(100, 100), new Id(++itemId, 0, 0), createMockServices(), base1.getSimpleBase());
        attacker2B1 = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(100, 100), new Id(++itemId, 0, 0), createMockServices(), base1.getSimpleBase());
        building1B1 = createSyncBaseItem(TEST_SIMPLE_BUILDING_ID, new Index(100, 100), new Id(++itemId, 0, 0), createMockServices(), base1.getSimpleBase());

        UserState userState2 = new UserState();
        userState2.setUser("TestUser2");
        Base base2 = new Base(userState2, 2);
        builder1B2 = createSyncBaseItem(TEST_START_BUILDER_ITEM_ID, new Index(100, 100), new Id(++itemId, 0, 0), createMockServices(), base2.getSimpleBase());

        BaseService baseServiceMock = EasyMock.createNiceMock(BaseService.class);
        EasyMock.expect(baseServiceMock.getUserState(base1.getSimpleBase())).andReturn(userState1).anyTimes();
        EasyMock.replay(baseServiceMock);

        ((ServerConditionServiceImpl) deAopProxy(serverConditionService)).setRate(50);
        setPrivateField(ServerConditionServiceImpl.class, serverConditionService, "baseService", baseServiceMock);
        setPrivateField(ServerConditionServiceImpl.class, serverConditionService, "connectionService", this);
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
    public void noneItemType() throws Exception {
        // Does not make any sense
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_POSITION, new ItemTypePositionComparisonConfig(null, null, null, null, false, "Move no item"), null);
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
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_POSITION, new ItemTypePositionComparisonConfig(null, itemTypes, null, null, false, "Move no #C"
                + builder1B1.getBaseItemType().getId() + " item"), null);
        serverConditionService.activateCondition(conditionConfig, userState1, 1);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestItemTypePositionComparison.this.actor = actor;
                TestItemTypePositionComparison.this.identifier = identifier;
            }
        });
        assertProgressStringFromService("Move no 0 item");
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemDeactivated(builder1B2);
        assertClearProgressString();
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemDeactivated(builder1B1);
        assertAndClearProgressString("Move no 1 item", base1.getSimpleBase());
        assertActorAndIdentifierAndClear(userState1, 1);
    }

    @Test
    @DirtiesContext
    public void multipleItemTypes1() throws Exception {
        Map<ItemType, Integer> itemTypes = new HashMap<>();
        itemTypes.put(builder1B1.getBaseItemType(), 3);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_POSITION, new ItemTypePositionComparisonConfig(null, itemTypes, null, null, false, "#C"
                + builder1B1.getBaseItemType().getId()), null);
        serverConditionService.activateCondition(conditionConfig, userState1, 1);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestItemTypePositionComparison.this.actor = actor;
                TestItemTypePositionComparison.this.identifier = identifier;
            }
        });

        assertProgressStringFromService("0");
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemDeactivated(builder1B2);
        assertClearActorAndIdentifier();
        assertClearProgressString();
        serverConditionService.onSyncItemDeactivated(builder1B1);
        assertClearActorAndIdentifier();
        assertAndClearProgressString("1", base1.getSimpleBase());
        serverConditionService.onSyncItemDeactivated(builder1B1);
        assertClearActorAndIdentifier();
        assertAndClearProgressString("1", base1.getSimpleBase());
        serverConditionService.onSyncItemDeactivated(builder1B1);
        assertClearActorAndIdentifier();
        assertAndClearProgressString("1", base1.getSimpleBase());
        serverConditionService.onSyncItemDeactivated(builder2B1);
        assertClearActorAndIdentifier();
        assertAndClearProgressString("2", base1.getSimpleBase());
        serverConditionService.onSyncItemDeactivated(builder2B1);
        assertClearActorAndIdentifier();
        assertAndClearProgressString("2", base1.getSimpleBase());
        serverConditionService.onSyncItemDeactivated(builder3B1);
        assertActorAndIdentifierAndClear(userState1, 1);
        assertAndClearProgressString("3", base1.getSimpleBase());
    }

    @Test
    @DirtiesContext
    public void multipleItemTypes2() throws Exception {
        Map<ItemType, Integer> itemTypes = new HashMap<>();
        itemTypes.put(builder1B1.getBaseItemType(), 3);
        itemTypes.put(attacker1B1.getBaseItemType(), 2);
        itemTypes.put(building1B1.getBaseItemType(), 1);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_POSITION, new ItemTypePositionComparisonConfig(null, itemTypes, null, null, false, "aaa #C"
                + builder1B1.getBaseItemType().getId() + " bbb #C" + attacker1B1.getBaseItemType().getId() + " ccc #C" + building1B1.getBaseItemType().getId()), null);
        serverConditionService.activateCondition(conditionConfig, userState1, 1);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestItemTypePositionComparison.this.actor = actor;
                TestItemTypePositionComparison.this.identifier = identifier;
            }
        });

        assertProgressStringFromService("aaa 0 bbb 0 ccc 0");
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemDeactivated(building1B1);
        assertClearActorAndIdentifier();
        assertAndClearProgressString("aaa 0 bbb 0 ccc 1", base1.getSimpleBase());
        serverConditionService.onSyncItemDeactivated(builder1B2);
        assertClearActorAndIdentifier();
        assertClearProgressString();
        serverConditionService.onSyncItemDeactivated(builder1B1);
        assertClearActorAndIdentifier();
        assertAndClearProgressString("aaa 1 bbb 0 ccc 1", base1.getSimpleBase());
        serverConditionService.onSyncItemDeactivated(builder1B1);
        assertClearActorAndIdentifier();
        assertAndClearProgressString("aaa 1 bbb 0 ccc 1", base1.getSimpleBase());
        serverConditionService.onSyncItemDeactivated(builder1B1);
        assertClearActorAndIdentifier();
        assertAndClearProgressString("aaa 1 bbb 0 ccc 1", base1.getSimpleBase());
        serverConditionService.onSyncItemDeactivated(attacker1B1);
        assertClearActorAndIdentifier();
        assertAndClearProgressString("aaa 1 bbb 1 ccc 1", base1.getSimpleBase());
        serverConditionService.onSyncItemDeactivated(builder2B1);
        assertClearActorAndIdentifier();
        assertAndClearProgressString("aaa 2 bbb 1 ccc 1", base1.getSimpleBase());
        serverConditionService.onSyncItemDeactivated(builder2B1);
        assertClearActorAndIdentifier();
        assertAndClearProgressString("aaa 2 bbb 1 ccc 1", base1.getSimpleBase());
        serverConditionService.onSyncItemDeactivated(attacker1B1);
        assertClearActorAndIdentifier();
        assertAndClearProgressString("aaa 2 bbb 1 ccc 1", base1.getSimpleBase());
        serverConditionService.onSyncItemDeactivated(attacker2B1);
        assertClearActorAndIdentifier();
        assertAndClearProgressString("aaa 2 bbb 2 ccc 1", base1.getSimpleBase());
        serverConditionService.onSyncItemDeactivated(builder3B1);
        assertActorAndIdentifierAndClear(userState1, 1);
        assertAndClearProgressString("aaa 3 bbb 2 ccc 1", base1.getSimpleBase());
    }

    @Test
    @DirtiesContext
    public void singleItemTypeRegion() throws Exception {
        Map<ItemType, Integer> itemTypes = new HashMap<>();
        itemTypes.put(builder1B1.getBaseItemType(), 1);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_POSITION, new ItemTypePositionComparisonConfig(null, itemTypes, new Rectangle(500, 500, 1000, 1000), null,
                false, "#C" + builder1B1.getBaseItemType().getId() + "  lll yyyy"), null);
        serverConditionService.activateCondition(conditionConfig, userState1, 1);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestItemTypePositionComparison.this.actor = actor;
                TestItemTypePositionComparison.this.identifier = identifier;
            }
        });

        assertProgressStringFromService("0  lll yyyy");
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemDeactivated(builder1B1);
        assertClearActorAndIdentifier();
        assertAndClearProgressString("0  lll yyyy", base1.getSimpleBase());
        builder1B1.getSyncItemArea().setPosition(new Index(600, 600));
        serverConditionService.onSyncItemDeactivated(builder1B1);
        assertActorAndIdentifierAndClear(userState1, 1);
        assertAndClearProgressString("1  lll yyyy", base1.getSimpleBase());
    }

    @Test
    @DirtiesContext
    public void multipleItemTypeRegion1() throws Exception {
        Map<ItemType, Integer> itemTypes = new HashMap<>();
        itemTypes.put(builder1B1.getBaseItemType(), 2);
        itemTypes.put(attacker1B1.getBaseItemType(), 2);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_POSITION, new ItemTypePositionComparisonConfig(null, itemTypes, new Rectangle(500, 500, 1000, 1000), null,
                false, "#C" + builder1B1.getBaseItemType().getId() + " #C" + attacker1B1.getBaseItemType().getId()), null);
        serverConditionService.activateCondition(conditionConfig, userState1, 1);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestItemTypePositionComparison.this.actor = actor;
                TestItemTypePositionComparison.this.identifier = identifier;
            }
        });

        assertProgressStringFromService("0 0");
        assertClearActorAndIdentifier();
        builder1B1.getSyncItemArea().setPosition(new Index(600, 600));
        serverConditionService.onSyncItemDeactivated(builder1B1);
        assertAndClearProgressString("1 0", base1.getSimpleBase());
        assertClearActorAndIdentifier();
        builder2B1.getSyncItemArea().setPosition(new Index(700, 700));
        serverConditionService.onSyncItemDeactivated(builder2B1);
        assertAndClearProgressString("2 0", base1.getSimpleBase());
        assertClearActorAndIdentifier();
        attacker1B1.getSyncItemArea().setPosition(new Index(800, 800));
        serverConditionService.onSyncItemDeactivated(attacker1B1);
        assertAndClearProgressString("2 1", base1.getSimpleBase());
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemDeactivated(attacker2B1);
        assertAndClearProgressString("2 1", base1.getSimpleBase());
        assertClearActorAndIdentifier();
        attacker1B1.getSyncItemArea().setPosition(new Index(10, 100));
        serverConditionService.onSyncItemDeactivated(attacker1B1);
        assertAndClearProgressString("2 0", base1.getSimpleBase());
        assertClearActorAndIdentifier();
        attacker2B1.getSyncItemArea().setPosition(new Index(900, 900));
        serverConditionService.onSyncItemDeactivated(attacker2B1);
        assertAndClearProgressString("2 1", base1.getSimpleBase());
        assertClearActorAndIdentifier();
        attacker1B1.getSyncItemArea().setPosition(new Index(800, 800));
        serverConditionService.onSyncItemDeactivated(attacker1B1);
        assertAndClearProgressString("2 2", base1.getSimpleBase());
        assertActorAndIdentifierAndClear(userState1, 1);
    }

    @Test
    @DirtiesContext
    public void multipleItemTypeRegionItemKilled() throws Exception {
        Map<ItemType, Integer> itemTypes = new HashMap<>();
        itemTypes.put(builder1B1.getBaseItemType(), 2);
        itemTypes.put(attacker1B1.getBaseItemType(), 2);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_POSITION, new ItemTypePositionComparisonConfig(null, itemTypes, new Rectangle(500, 500, 1000, 1000), null,
                false, "#C" + builder1B1.getBaseItemType().getId() + " wwww #C" + attacker1B1.getBaseItemType().getId()), null);
        serverConditionService.activateCondition(conditionConfig, userState1, 1);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestItemTypePositionComparison.this.actor = actor;
                TestItemTypePositionComparison.this.identifier = identifier;
            }
        });

        assertProgressStringFromService("0 wwww 0");
        assertClearActorAndIdentifier();
        builder1B1.getSyncItemArea().setPosition(new Index(600, 600));
        serverConditionService.onSyncItemDeactivated(builder1B1);
        assertAndClearProgressString("1 wwww 0", base1.getSimpleBase());
        assertClearActorAndIdentifier();
        builder2B1.getSyncItemArea().setPosition(new Index(700, 700));
        serverConditionService.onSyncItemDeactivated(builder2B1);
        assertAndClearProgressString("2 wwww 0", base1.getSimpleBase());
        assertClearActorAndIdentifier();
        attacker1B1.getSyncItemArea().setPosition(new Index(800, 800));
        serverConditionService.onSyncItemDeactivated(attacker1B1);
        assertAndClearProgressString("2 wwww 1", base1.getSimpleBase());
        assertClearActorAndIdentifier();
        builder1B1.setHealth(0); // Killed
        attacker2B1.getSyncItemArea().setPosition(new Index(900, 900));
        serverConditionService.onSyncItemDeactivated(attacker2B1);
        assertAndClearProgressString("1 wwww 2", base1.getSimpleBase());
        assertClearActorAndIdentifier();
        builder3B1.getSyncItemArea().setPosition(new Index(600, 600));
        serverConditionService.onSyncItemDeactivated(builder3B1);
        assertAndClearProgressString("2 wwww 2", base1.getSimpleBase());
        assertActorAndIdentifierAndClear(userState1, 1);
    }

    @Test
    @DirtiesContext
    public void singleItemTypeRegionTime() throws Exception {
        Map<ItemType, Integer> itemTypes = new HashMap<>();
        itemTypes.put(builder1B1.getBaseItemType(), 1);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_POSITION, new ItemTypePositionComparisonConfig(null, itemTypes, new Rectangle(500, 500, 1000, 1000), 100,
                false, "xxx #T yyy #C" + builder1B1.getBaseItemType().getId()), null);
        serverConditionService.activateCondition(conditionConfig, userState1, 1);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestItemTypePositionComparison.this.actor = actor;
                TestItemTypePositionComparison.this.identifier = identifier;
            }
        });

        assertProgressStringFromService("xxx - yyy 0");
        assertClearActorAndIdentifier();
        builder1B1.getSyncItemArea().setPosition(new Index(600, 600));
        serverConditionService.onSyncItemDeactivated(builder1B1);
        assertClearActorAndIdentifier();
        assertAndClearProgressString("xxx 1 yyy 1", base1.getSimpleBase());
        Thread.sleep(200);
        assertAndClearProgressString("xxx 0 yyy 1", base1.getSimpleBase());
        assertActorAndIdentifierAndClear(userState1, 1);
    }

    @Test
    @DirtiesContext
    public void multipleItemTypeRegionTime1() throws Exception {
        Map<ItemType, Integer> itemTypes = new HashMap<>();
        itemTypes.put(builder1B1.getBaseItemType(), 3);
        itemTypes.put(attacker1B1.getBaseItemType(), 2);
        itemTypes.put(building1B1.getBaseItemType(), 1);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_POSITION, new ItemTypePositionComparisonConfig(null, itemTypes, new Rectangle(500, 500, 1000, 1000), 100,
                false, "xxx #T yyy #C" + builder1B1.getBaseItemType().getId() + " #C" + attacker1B1.getBaseItemType().getId() + " #C" + building1B1.getBaseItemType().getId()), null);
        serverConditionService.activateCondition(conditionConfig, userState1, 1);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestItemTypePositionComparison.this.actor = actor;
                TestItemTypePositionComparison.this.identifier = identifier;
            }
        });

        assertProgressStringFromService("xxx - yyy 0 0 0");
        assertClearActorAndIdentifier();
        builder1B1.getSyncItemArea().setPosition(new Index(600, 600));
        serverConditionService.onSyncItemDeactivated(builder1B1);
        assertAndClearProgressString("xxx - yyy 1 0 0", base1.getSimpleBase());
        assertClearActorAndIdentifier();
        builder2B1.getSyncItemArea().setPosition(new Index(600, 600));
        serverConditionService.onSyncItemDeactivated(builder2B1);
        assertAndClearProgressString("xxx - yyy 2 0 0", base1.getSimpleBase());
        assertClearActorAndIdentifier();
        builder3B1.getSyncItemArea().setPosition(new Index(600, 600));
        serverConditionService.onSyncItemDeactivated(builder3B1);
        assertAndClearProgressString("xxx - yyy 3 0 0", base1.getSimpleBase());
        assertClearActorAndIdentifier();
        attacker1B1.getSyncItemArea().setPosition(new Index(800, 800));
        serverConditionService.onSyncItemDeactivated(attacker1B1);
        assertAndClearProgressString("xxx - yyy 3 1 0", base1.getSimpleBase());
        assertClearActorAndIdentifier();
        attacker2B1.getSyncItemArea().setPosition(new Index(1000, 800));
        serverConditionService.onSyncItemDeactivated(attacker2B1);
        assertAndClearProgressString("xxx - yyy 3 2 0", base1.getSimpleBase());
        assertClearActorAndIdentifier();
        building1B1.getSyncItemArea().setPosition(new Index(1000, 800));
        serverConditionService.onSyncItemBuilt(building1B1);
        assertAndClearProgressString("xxx 1 yyy 3 2 1", base1.getSimpleBase());
        assertClearActorAndIdentifier();
        Thread.sleep(200);
        assertAndClearProgressString("xxx 0 yyy 3 2 1", base1.getSimpleBase());
        assertActorAndIdentifierAndClear(userState1, 1);
    }

    @Test
    @DirtiesContext
    public void multipleItemTypeRegionTime2() throws Exception {
        Map<ItemType, Integer> itemTypes = new HashMap<>();
        itemTypes.put(builder1B1.getBaseItemType(), 3);
        itemTypes.put(attacker1B1.getBaseItemType(), 2);
        itemTypes.put(building1B1.getBaseItemType(), 1);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_POSITION, new ItemTypePositionComparisonConfig(null, itemTypes, new Rectangle(500, 500, 1000, 1000), 100,
                false, null), null);
        serverConditionService.activateCondition(conditionConfig, userState1, 1);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestItemTypePositionComparison.this.actor = actor;
                TestItemTypePositionComparison.this.identifier = identifier;
            }
        });

        Assert.assertEquals("", serverConditionService.getProgressHtml(userState1, 1));
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
        assertClearProgressString();
    }

    @Test
    @DirtiesContext
    public void multipleItemTypeRegionTime3() throws Exception {
        Map<ItemType, Integer> itemTypes = new HashMap<>();
        itemTypes.put(builder1B1.getBaseItemType(), 3);
        itemTypes.put(attacker1B1.getBaseItemType(), 2);
        itemTypes.put(building1B1.getBaseItemType(), 1);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_POSITION, new ItemTypePositionComparisonConfig(null, itemTypes, new Rectangle(500, 500, 1000, 1000), 100,
                false, null), null);
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
        ItemService itemServiceMock = EasyMock.createMock(ItemService.class);
        Collection<SyncBaseItem> syncBaseItems = new ArrayList<>();
        EasyMock.expect(itemServiceMock.getBaseItemsInRectangle(new Rectangle(500, 500, 1000, 1000), base1.getSimpleBase(), null)).andReturn(syncBaseItems);
        EasyMock.expect(itemServiceMock.getItemType(builder1B1.getBaseItemType().getId())).andReturn(builder1B1.getBaseItemType()).anyTimes();
        EasyMock.expect(itemServiceMock.getItemType(attacker1B1.getBaseItemType().getId())).andReturn(attacker1B1.getBaseItemType()).anyTimes();
        EasyMock.expect(itemServiceMock.getItemType(building1B1.getBaseItemType().getId())).andReturn(building1B1.getBaseItemType()).anyTimes();
        EasyMock.replay(itemServiceMock);
        setPrivateField(ServerServices.class, serverServices, "itemService", itemServiceMock);

        Map<ItemType, Integer> itemTypes = new HashMap<>();
        itemTypes.put(builder1B1.getBaseItemType(), 3);
        itemTypes.put(attacker1B1.getBaseItemType(), 2);
        itemTypes.put(building1B1.getBaseItemType(), 1);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_POSITION, new ItemTypePositionComparisonConfig(null, itemTypes, new Rectangle(500, 500, 1000, 1000), 100,
                true, "#T #C" + builder1B1.getBaseItemType().getId() + " #C" + attacker1B1.getBaseItemType().getId() + " #C" + building1B1.getBaseItemType().getId()), null);
        serverConditionService.activateCondition(conditionConfig, userState1, 1);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestItemTypePositionComparison.this.actor = actor;
                TestItemTypePositionComparison.this.identifier = identifier;
            }
        });

        assertProgressStringFromService("- 0 0 0");
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
        ItemService itemServiceMock = EasyMock.createMock(ItemService.class);
        Collection<SyncBaseItem> syncBaseItems = new ArrayList<>();
        builder1B1.getSyncItemArea().setPosition(new Index(600, 600));
        syncBaseItems.add(builder1B1);
        EasyMock.expect(itemServiceMock.getBaseItemsInRectangle(new Rectangle(500, 500, 1000, 1000), base1.getSimpleBase(), null)).andReturn(syncBaseItems);
        EasyMock.expect(itemServiceMock.getItemType(builder1B1.getBaseItemType().getId())).andReturn(builder1B1.getBaseItemType()).anyTimes();
        EasyMock.expect(itemServiceMock.getItemType(attacker1B1.getBaseItemType().getId())).andReturn(attacker1B1.getBaseItemType()).anyTimes();
        EasyMock.expect(itemServiceMock.getItemType(building1B1.getBaseItemType().getId())).andReturn(building1B1.getBaseItemType()).anyTimes();
        EasyMock.replay(itemServiceMock);
        setPrivateField(ServerServices.class, serverServices, "itemService", itemServiceMock);

        Map<ItemType, Integer> itemTypes = new HashMap<>();
        itemTypes.put(builder1B1.getBaseItemType(), 3);
        itemTypes.put(attacker1B1.getBaseItemType(), 2);
        itemTypes.put(building1B1.getBaseItemType(), 1);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_POSITION, new ItemTypePositionComparisonConfig(null, itemTypes, new Rectangle(500, 500, 1000, 1000), 100,
                true, "#T #C" + builder1B1.getBaseItemType().getId() + " #C" + attacker1B1.getBaseItemType().getId() + " #C" + building1B1.getBaseItemType().getId()), null);
        serverConditionService.activateCondition(conditionConfig, userState1, 1);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestItemTypePositionComparison.this.actor = actor;
                TestItemTypePositionComparison.this.identifier = identifier;
            }
        });

        assertProgressStringFromService("- 1 0 0");
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
        ItemService itemServiceMock = EasyMock.createMock(ItemService.class);
        Collection<SyncBaseItem> syncBaseItems = new ArrayList<>();
        builder1B1.getSyncItemArea().setPosition(new Index(600, 600));
        syncBaseItems.add(builder1B1);
        builder2B1.getSyncItemArea().setPosition(new Index(600, 600));
        syncBaseItems.add(builder2B1);
        EasyMock.expect(itemServiceMock.getBaseItemsInRectangle(new Rectangle(500, 500, 1000, 1000), base1.getSimpleBase(), null)).andReturn(syncBaseItems);
        EasyMock.expect(itemServiceMock.getItemType(builder1B1.getBaseItemType().getId())).andReturn(builder1B1.getBaseItemType()).anyTimes();
        EasyMock.expect(itemServiceMock.getItemType(attacker1B1.getBaseItemType().getId())).andReturn(attacker1B1.getBaseItemType()).anyTimes();
        EasyMock.expect(itemServiceMock.getItemType(building1B1.getBaseItemType().getId())).andReturn(building1B1.getBaseItemType()).anyTimes();
        EasyMock.replay(itemServiceMock);
        setPrivateField(ServerServices.class, serverServices, "itemService", itemServiceMock);

        Map<ItemType, Integer> itemTypes = new HashMap<>();
        itemTypes.put(builder1B1.getBaseItemType(), 3);
        itemTypes.put(attacker1B1.getBaseItemType(), 2);
        itemTypes.put(building1B1.getBaseItemType(), 1);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_POSITION, new ItemTypePositionComparisonConfig(null, itemTypes, new Rectangle(500, 500, 1000, 1000), null,
                true, "#T #C" + builder1B1.getBaseItemType().getId() + " #C" + attacker1B1.getBaseItemType().getId() + " #C" + building1B1.getBaseItemType().getId()), null);
        serverConditionService.activateCondition(conditionConfig, userState1, 1);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestItemTypePositionComparison.this.actor = actor;
                TestItemTypePositionComparison.this.identifier = identifier;
            }
        });

        assertProgressStringFromService("- 2 0 0");
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
        assertActorAndIdentifierAndClear(userState1, 1);
    }

    @Test
    @DirtiesContext
    public void multipleItemTypeRegionTimeAddExisting4() throws Exception {
        ItemService itemServiceMock = EasyMock.createMock(ItemService.class);
        Collection<SyncBaseItem> syncBaseItems = new ArrayList<>();
        syncBaseItems.add(builder1B1);
        syncBaseItems.add(builder2B1);
        EasyMock.expect(itemServiceMock.getItems4Base(base1.getSimpleBase())).andReturn(syncBaseItems);
        EasyMock.replay(itemServiceMock);
        setPrivateField(ServerServices.class, serverServices, "itemService", itemServiceMock);

        Map<ItemType, Integer> itemTypes = new HashMap<>();
        itemTypes.put(builder1B1.getBaseItemType(), 3);
        itemTypes.put(attacker1B1.getBaseItemType(), 2);
        itemTypes.put(building1B1.getBaseItemType(), 1);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_POSITION, new ItemTypePositionComparisonConfig(null, itemTypes, null, 100, true, null), null);
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
        ItemService itemServiceMock = EasyMock.createMock(ItemService.class);
        Collection<SyncBaseItem> syncBaseItems = new ArrayList<>();
        builder1B1.getSyncItemArea().setPosition(new Index(600, 600));
        syncBaseItems.add(builder1B1);
        EasyMock.expect(itemServiceMock.getBaseItemsInRectangle(new Rectangle(500, 500, 1000, 1000), base1.getSimpleBase(), null)).andReturn(syncBaseItems);
        EasyMock.replay(itemServiceMock);
        setPrivateField(ServerServices.class, serverServices, "itemService", itemServiceMock);

        Map<ItemType, Integer> itemTypes = new HashMap<>();
        itemTypes.put(builder1B1.getBaseItemType(), 1);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_POSITION, new ItemTypePositionComparisonConfig(null, itemTypes, new Rectangle(500, 500, 1000, 1000), null,
                true, null), null);

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
        ItemService itemServiceMock = EasyMock.createMock(ItemService.class);
        Collection<SyncBaseItem> syncBaseItems = new ArrayList<>();
        builder1B1.getSyncItemArea().setPosition(new Index(600, 600));
        syncBaseItems.add(builder1B1);
        builder2B1.getSyncItemArea().setPosition(new Index(600, 600));
        syncBaseItems.add(builder2B1);
        EasyMock.expect(itemServiceMock.getBaseItemsInRectangle(new Rectangle(500, 500, 1000, 1000), base1.getSimpleBase(), null)).andReturn(syncBaseItems);
        EasyMock.replay(itemServiceMock);
        setPrivateField(ServerServices.class, serverServices, "itemService", itemServiceMock);

        Map<ItemType, Integer> itemTypes = new HashMap<>();
        itemTypes.put(builder1B1.getBaseItemType(), 2);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_POSITION, new ItemTypePositionComparisonConfig(null, itemTypes, new Rectangle(500, 500, 1000, 1000), null,
                true, null), null);

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
        ItemService itemServiceMock = EasyMock.createMock(ItemService.class);
        Collection<SyncBaseItem> syncBaseItems = new ArrayList<>();
        builder1B1.getSyncItemArea().setPosition(new Index(600, 600));
        syncBaseItems.add(builder1B1);
        builder2B1.getSyncItemArea().setPosition(new Index(600, 600));
        syncBaseItems.add(builder2B1);
        EasyMock.expect(itemServiceMock.getBaseItemsInRectangle(new Rectangle(500, 500, 1000, 1000), base1.getSimpleBase(), null)).andReturn(syncBaseItems);
        EasyMock.replay(itemServiceMock);
        setPrivateField(ServerServices.class, serverServices, "itemService", itemServiceMock);

        Map<ItemType, Integer> itemTypes = new HashMap<>();
        itemTypes.put(builder1B1.getBaseItemType(), 2);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_POSITION, new ItemTypePositionComparisonConfig(null, itemTypes, new Rectangle(500, 500, 1000, 1000), 100,
                true, null), null);
        serverConditionService.activateCondition(conditionConfig, userState1, 1);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestItemTypePositionComparison.this.actor = actor;
                TestItemTypePositionComparison.this.identifier = identifier;
            }
        });

        assertClearActorAndIdentifier();
        Thread.sleep(150);
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
    public void closeConnection(SimpleBase simpleBase) {

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
