package com.btxtech.game.services.utg;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.utg.ConditionServiceListener;
import com.btxtech.game.jsre.common.utg.config.ConditionConfig;
import com.btxtech.game.jsre.common.utg.config.ConditionTrigger;
import com.btxtech.game.jsre.common.utg.config.ItemTypePositionComparisonConfig;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.base.Base;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.condition.ServerConditionService;
import com.btxtech.game.services.utg.condition.impl.ServerConditionServiceImpl;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.HashMap;
import java.util.Map;

/**
 * User: beat
 * Date: 31.01.2012
 * Time: 16:51:19
 */
public class TestItemTypePositionCondition extends AbstractServiceTest {
    @Autowired
    private ServerConditionService serverConditionService;
    @Autowired
    private ItemService itemService;
    private Integer identifier;
    private UserState actor;

    private UserState userState1;
    private User user1;
    private Base base1;
    private SyncBaseItem builder1B1;
    private SyncBaseItem builder2B1;
    private SyncBaseItem builder3B1;
    private SyncBaseItem attacker1B1;
    private SyncBaseItem attacker2B1;
    private SyncBaseItem building1B1;
    private UserState userState2;
    private User user2;
    private Base base2;
    private SyncBaseItem builder1B2;

    @Before
    public void before() throws Exception {
        configureRealGame();
        identifier = null;
        actor = null;

        // Mock objects
        userState1 = new UserState();
        user1 = new User();
        user1.registerUser("TestUser1", "", "");
        userState1.setUser(user1);
        base1 = new Base(userState1, 1);
        int itemId = 0;
        builder1B1 = createSyncBaseItem(TEST_START_BUILDER_ITEM_ID, new Index(100, 100), new Id(++itemId, 0, 0), createMockServices(), base1.getSimpleBase());
        builder2B1 = createSyncBaseItem(TEST_START_BUILDER_ITEM_ID, new Index(100, 100), new Id(++itemId, 0, 0), createMockServices(), base1.getSimpleBase());
        builder3B1 = createSyncBaseItem(TEST_START_BUILDER_ITEM_ID, new Index(100, 100), new Id(++itemId, 0, 0), createMockServices(), base1.getSimpleBase());
        attacker1B1 = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(100, 100), new Id(++itemId, 0, 0), createMockServices(), base1.getSimpleBase());
        attacker2B1 = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(100, 100), new Id(++itemId, 0, 0), createMockServices(), base1.getSimpleBase());
        building1B1 = createSyncBaseItem(TEST_SIMPLE_BUILDING_ID, new Index(100, 100), new Id(++itemId, 0, 0), createMockServices(), base1.getSimpleBase());

        userState2 = new UserState();
        user2 = new User();
        user2.registerUser("TestUser2", "", "");
        userState2.setUser(user2);
        base2 = new Base(userState2, 2);
        builder1B2 = createSyncBaseItem(TEST_START_BUILDER_ITEM_ID, new Index(100, 100), new Id(++itemId, 0, 0), createMockServices(), base2.getSimpleBase());


        BaseService baseServiceMock = EasyMock.createNiceMock(BaseService.class);
        EasyMock.expect(baseServiceMock.getUserState(base1.getSimpleBase())).andReturn(userState1).anyTimes();
        EasyMock.replay(baseServiceMock);

        ((ServerConditionServiceImpl)deAopProxy(serverConditionService)).setRate(50);
        setPrivateField(ServerConditionServiceImpl.class, serverConditionService, "baseService", baseServiceMock);
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
    public void noneItemType() throws Exception {
        // Does not make any sense
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_POSITION, new ItemTypePositionComparisonConfig(null, null, null, null));
        serverConditionService.activateCondition(conditionConfig, userState1, 1);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestItemTypePositionCondition.this.actor = actor;
                TestItemTypePositionCondition.this.identifier = identifier;
            }
        });

        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemDeactivated(builder1B2);
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemDeactivated(builder1B1);
        assertActorAndIdentifierAndClear(userState1, 1);
    }

    @Test
    @DirtiesContext
    public void singleItemType() throws Exception {
        Map<ItemType, Integer> itemTypes = new HashMap<ItemType, Integer>();
        itemTypes.put(builder1B1.getBaseItemType(), 1);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_POSITION, new ItemTypePositionComparisonConfig(null, itemTypes, null, null));
        serverConditionService.activateCondition(conditionConfig, userState1, 1);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestItemTypePositionCondition.this.actor = actor;
                TestItemTypePositionCondition.this.identifier = identifier;
            }
        });

        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemDeactivated(builder1B2);
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemDeactivated(builder1B1);
        assertActorAndIdentifierAndClear(userState1, 1);
    }

    @Test
    @DirtiesContext
    public void multipleItemTypes1() throws Exception {
        Map<ItemType, Integer> itemTypes = new HashMap<ItemType, Integer>();
        itemTypes.put(builder1B1.getBaseItemType(), 3);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_POSITION, new ItemTypePositionComparisonConfig(null, itemTypes, null, null));
        serverConditionService.activateCondition(conditionConfig, userState1, 1);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestItemTypePositionCondition.this.actor = actor;
                TestItemTypePositionCondition.this.identifier = identifier;
            }
        });

        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemDeactivated(builder1B2);
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemDeactivated(builder1B1);
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemDeactivated(builder1B1);
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemDeactivated(builder1B1);
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemDeactivated(builder2B1);
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemDeactivated(builder2B1);
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemDeactivated(builder3B1);
        assertActorAndIdentifierAndClear(userState1, 1);
    }

    @Test
    @DirtiesContext
    public void multipleItemTypes2() throws Exception {
        Map<ItemType, Integer> itemTypes = new HashMap<ItemType, Integer>();
        itemTypes.put(builder1B1.getBaseItemType(), 3);
        itemTypes.put(attacker1B1.getBaseItemType(), 2);
        itemTypes.put(building1B1.getBaseItemType(), 1);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_POSITION, new ItemTypePositionComparisonConfig(null, itemTypes, null, null));
        serverConditionService.activateCondition(conditionConfig, userState1, 1);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestItemTypePositionCondition.this.actor = actor;
                TestItemTypePositionCondition.this.identifier = identifier;
            }
        });

        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemDeactivated(building1B1);
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemDeactivated(builder1B2);
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemDeactivated(builder1B1);
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemDeactivated(builder1B1);
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemDeactivated(builder1B1);
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemDeactivated(attacker1B1);
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemDeactivated(builder2B1);
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemDeactivated(builder2B1);
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemDeactivated(attacker1B1);
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemDeactivated(attacker2B1);
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemDeactivated(builder3B1);
        assertActorAndIdentifierAndClear(userState1, 1);
    }

    @Test
    @DirtiesContext
    public void singleItemTypeRegion() throws Exception {
        Map<ItemType, Integer> itemTypes = new HashMap<ItemType, Integer>();
        itemTypes.put(builder1B1.getBaseItemType(), 1);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_POSITION, new ItemTypePositionComparisonConfig(null, itemTypes, new Rectangle(500, 500, 1000, 1000), null));
        serverConditionService.activateCondition(conditionConfig, userState1, 1);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestItemTypePositionCondition.this.actor = actor;
                TestItemTypePositionCondition.this.identifier = identifier;
            }
        });

        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemDeactivated(builder1B1);
        assertClearActorAndIdentifier();
        builder1B1.getSyncItemArea().setPosition(new Index(600, 600));
        serverConditionService.onSyncItemDeactivated(builder1B1);
        assertActorAndIdentifierAndClear(userState1, 1);
    }

    @Test
    @DirtiesContext
    public void multipleItemTypeRegion1() throws Exception {
        Map<ItemType, Integer> itemTypes = new HashMap<ItemType, Integer>();
        itemTypes.put(builder1B1.getBaseItemType(), 2);
        itemTypes.put(attacker1B1.getBaseItemType(), 2);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_POSITION, new ItemTypePositionComparisonConfig(null, itemTypes, new Rectangle(500, 500, 1000, 1000), null));
        serverConditionService.activateCondition(conditionConfig, userState1, 1);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestItemTypePositionCondition.this.actor = actor;
                TestItemTypePositionCondition.this.identifier = identifier;
            }
        });

        assertClearActorAndIdentifier();
        builder1B1.getSyncItemArea().setPosition(new Index(600, 600));
        serverConditionService.onSyncItemDeactivated(builder1B1);
        assertClearActorAndIdentifier();
        builder2B1.getSyncItemArea().setPosition(new Index(700, 700));
        serverConditionService.onSyncItemDeactivated(builder2B1);
        assertClearActorAndIdentifier();
        attacker1B1.getSyncItemArea().setPosition(new Index(800, 800));
        serverConditionService.onSyncItemDeactivated(attacker1B1);
        assertClearActorAndIdentifier();
        serverConditionService.onSyncItemDeactivated(attacker2B1);
        assertClearActorAndIdentifier();
        attacker1B1.getSyncItemArea().setPosition(new Index(10, 100));
        serverConditionService.onSyncItemDeactivated(attacker1B1);
        assertClearActorAndIdentifier();
        attacker2B1.getSyncItemArea().setPosition(new Index(900, 900));
        serverConditionService.onSyncItemDeactivated(attacker2B1);
        assertClearActorAndIdentifier();
        attacker1B1.getSyncItemArea().setPosition(new Index(800, 800));
        serverConditionService.onSyncItemDeactivated(attacker1B1);
        assertActorAndIdentifierAndClear(userState1, 1);
    }

    @Test
    @DirtiesContext
    public void multipleItemTypeRegionItemKilled() throws Exception {
        Map<ItemType, Integer> itemTypes = new HashMap<ItemType, Integer>();
        itemTypes.put(builder1B1.getBaseItemType(), 2);
        itemTypes.put(attacker1B1.getBaseItemType(), 2);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_POSITION, new ItemTypePositionComparisonConfig(null, itemTypes, new Rectangle(500, 500, 1000, 1000), null));
        serverConditionService.activateCondition(conditionConfig, userState1, 1);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestItemTypePositionCondition.this.actor = actor;
                TestItemTypePositionCondition.this.identifier = identifier;
            }
        });

        assertClearActorAndIdentifier();
        builder1B1.getSyncItemArea().setPosition(new Index(600, 600));
        serverConditionService.onSyncItemDeactivated(builder1B1);
        assertClearActorAndIdentifier();
        builder2B1.getSyncItemArea().setPosition(new Index(700, 700));
        serverConditionService.onSyncItemDeactivated(builder2B1);
        assertClearActorAndIdentifier();
        attacker1B1.getSyncItemArea().setPosition(new Index(800, 800));
        serverConditionService.onSyncItemDeactivated(attacker1B1);
        assertClearActorAndIdentifier();
        builder1B1.setHealth(0); // Killed
        attacker2B1.getSyncItemArea().setPosition(new Index(900, 900));
        serverConditionService.onSyncItemDeactivated(attacker2B1);
        assertClearActorAndIdentifier();
        builder3B1.getSyncItemArea().setPosition(new Index(600, 600));
        serverConditionService.onSyncItemDeactivated(builder3B1);
        assertActorAndIdentifierAndClear(userState1, 1);
    }

    @Test
    @DirtiesContext
    public void singleItemTypeRegionTime() throws Exception {
        Map<ItemType, Integer> itemTypes = new HashMap<ItemType, Integer>();
        itemTypes.put(builder1B1.getBaseItemType(), 1);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_POSITION, new ItemTypePositionComparisonConfig(null, itemTypes, new Rectangle(500, 500, 1000, 1000), 100));
        serverConditionService.activateCondition(conditionConfig, userState1, 1);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestItemTypePositionCondition.this.actor = actor;
                TestItemTypePositionCondition.this.identifier = identifier;
            }
        });

        assertClearActorAndIdentifier();
        builder1B1.getSyncItemArea().setPosition(new Index(600, 600));
        serverConditionService.onSyncItemDeactivated(builder1B1);
        assertClearActorAndIdentifier();
        Thread.sleep(200);
        assertActorAndIdentifierAndClear(userState1, 1);
    }

    @Test
    @DirtiesContext
    public void multipleItemTypeRegionTime1() throws Exception {
        Map<ItemType, Integer> itemTypes = new HashMap<ItemType, Integer>();
        itemTypes.put(builder1B1.getBaseItemType(), 3);
        itemTypes.put(attacker1B1.getBaseItemType(), 2);
        itemTypes.put(building1B1.getBaseItemType(), 1);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_POSITION, new ItemTypePositionComparisonConfig(null, itemTypes, new Rectangle(500, 500, 1000, 1000), 100));
        serverConditionService.activateCondition(conditionConfig, userState1, 1);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestItemTypePositionCondition.this.actor = actor;
                TestItemTypePositionCondition.this.identifier = identifier;
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
        Thread.sleep(200);
        assertActorAndIdentifierAndClear(userState1, 1);
    }

    @Test
    @DirtiesContext
    public void multipleItemTypeRegionTime2() throws Exception {
        Map<ItemType, Integer> itemTypes = new HashMap<ItemType, Integer>();
        itemTypes.put(builder1B1.getBaseItemType(), 3);
        itemTypes.put(attacker1B1.getBaseItemType(), 2);
        itemTypes.put(building1B1.getBaseItemType(), 1);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_POSITION, new ItemTypePositionComparisonConfig(null, itemTypes, new Rectangle(500, 500, 1000, 1000), 100));
        serverConditionService.activateCondition(conditionConfig, userState1, 1);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestItemTypePositionCondition.this.actor = actor;
                TestItemTypePositionCondition.this.identifier = identifier;
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
        Thread.sleep(200);
        assertActorAndIdentifierAndClear(userState1, 1);
    }

    @Test
    @DirtiesContext
    public void multipleItemTypeRegionTime3() throws Exception {
        Map<ItemType, Integer> itemTypes = new HashMap<ItemType, Integer>();
        itemTypes.put(builder1B1.getBaseItemType(), 3);
        itemTypes.put(attacker1B1.getBaseItemType(), 2);
        itemTypes.put(building1B1.getBaseItemType(), 1);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_POSITION, new ItemTypePositionComparisonConfig(null, itemTypes, new Rectangle(500, 500, 1000, 1000), 100));
        serverConditionService.activateCondition(conditionConfig, userState1, 1);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestItemTypePositionCondition.this.actor = actor;
                TestItemTypePositionCondition.this.identifier = identifier;
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

}
