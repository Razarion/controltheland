package com.btxtech.game.jsre.client.simulation;

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.utg.ConditionServiceListener;
import com.btxtech.game.jsre.common.utg.config.ConditionConfig;
import com.btxtech.game.jsre.common.utg.config.ConditionTrigger;
import com.btxtech.game.jsre.common.utg.config.CountComparisonConfig;
import com.btxtech.game.jsre.common.utg.config.SyncItemTypeComparisonConfig;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;

import java.util.HashMap;
import java.util.Map;

/**
 * User: beat
 * Date: 14.11.2011
 * Time: 17:02:38
 */
public class TestSimulationConditionServiceImpl {
    private boolean passed;
    private SimpleBase myBase;
    private SimpleBase enemyBase;
    private BaseItemType itemType1;
    private BaseItemType itemType2;
    private SyncBaseItem itemMock1;
    private SyncBaseItem itemMock2;
    private SyncBaseItem itemMock3;
    private SyncBaseItem itemMock4;
    private SyncBaseItem itemMock5;

    @Before
    public void before() {
        // Bases
        myBase = new SimpleBase(1, 1);
        ClientBase.getInstance().setBase(myBase);
        enemyBase = new SimpleBase(2, 1);
        // Sync Items
        itemType1 = EasyMock.createNiceMock(BaseItemType.class);
        EasyMock.expect(itemType1.getId()).andReturn(1);
        itemType2 = EasyMock.createNiceMock(BaseItemType.class);
        EasyMock.expect(itemType2.getId()).andReturn(2);
        itemMock1 = EasyMock.createNiceMock(SyncBaseItem.class);
        EasyMock.expect(itemMock1.getItemType()).andReturn(itemType1).anyTimes();
        EasyMock.expect(itemMock1.getBaseItemType()).andReturn(itemType1).anyTimes();
        itemMock2 = EasyMock.createNiceMock(SyncBaseItem.class);
        EasyMock.expect(itemMock2.getItemType()).andReturn(itemType2).anyTimes();
        EasyMock.expect(itemMock2.getBaseItemType()).andReturn(itemType2).anyTimes();
        itemMock3 = EasyMock.createNiceMock(SyncBaseItem.class);
        EasyMock.expect(itemMock3.getItemType()).andReturn(itemType1).anyTimes();
        EasyMock.expect(itemMock3.getBaseItemType()).andReturn(itemType1).anyTimes();
        EasyMock.expect(itemMock3.getBase()).andReturn(myBase).anyTimes();
        itemMock4 = EasyMock.createNiceMock(SyncBaseItem.class);
        EasyMock.expect(itemMock4.getItemType()).andReturn(itemType2).anyTimes();
        EasyMock.expect(itemMock4.getBaseItemType()).andReturn(itemType2).anyTimes();
        EasyMock.expect(itemMock4.getBase()).andReturn(enemyBase).anyTimes();
        itemMock5 = EasyMock.createNiceMock(SyncBaseItem.class);
        EasyMock.expect(itemMock5.getItemType()).andReturn(itemType2).anyTimes();
        EasyMock.expect(itemMock5.getBaseItemType()).andReturn(itemType2).anyTimes();
        EasyMock.expect(itemMock5.getBase()).andReturn(myBase).anyTimes();
        EasyMock.replay(itemType1, itemType2, itemMock1, itemMock2, itemMock3, itemMock4, itemMock5);
    }

    @Test
    public void moneyEarned() throws Exception {
        SimulationConditionServiceImpl conditionService = SimulationConditionServiceImpl.getInstance();

        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.MONEY_INCREASED, new CountComparisonConfig(100), null, null, false);
        conditionService.activateCondition(conditionConfig, myBase, null);
        passed = false;
        conditionService.setConditionServiceListener(new ConditionServiceListener<SimpleBase, Void>() {
            @Override
            public void conditionPassed(SimpleBase actor, Void identifier) {
                Assert.assertEquals(myBase, actor);
                passed = true;
            }
        });

        Assert.assertFalse(passed);
        conditionService.onMoneyIncrease(enemyBase, 110);
        Assert.assertFalse(passed);
        conditionService.onMoneyIncrease(myBase, 50);
        Assert.assertFalse(passed);
        conditionService.onMoneyIncrease(myBase, 60);
        Assert.assertTrue(passed);
    }

    @Test
    public void syncItemKilled() throws Exception {
        SimulationConditionServiceImpl conditionService = SimulationConditionServiceImpl.getInstance();

        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_KILLED, new CountComparisonConfig(2), null, null, false);
        conditionService.activateCondition(conditionConfig, myBase, null);
        passed = false;
        conditionService.setConditionServiceListener(new ConditionServiceListener<SimpleBase, Void>() {
            @Override
            public void conditionPassed(SimpleBase actor, Void identifier) {
                Assert.assertEquals(myBase, actor);
                passed = true;
            }
        });

        Assert.assertFalse(passed);
        conditionService.onMoneyIncrease(enemyBase, 3);
        Assert.assertFalse(passed);
        conditionService.onSyncItemKilled(enemyBase, itemMock1);
        Assert.assertFalse(passed);
        conditionService.onSyncItemKilled(myBase, itemMock1);
        Assert.assertFalse(passed);
        conditionService.onSyncItemKilled(myBase, itemMock1);
        Assert.assertTrue(passed);
    }

    @Test
    public void syncItemKilled2() throws Exception {
        SimulationConditionServiceImpl conditionService = SimulationConditionServiceImpl.getInstance();

        Map<ItemType, Integer> itemTypeMap = new HashMap<ItemType, Integer>();
        itemTypeMap.put(itemType2, 2);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_KILLED, new SyncItemTypeComparisonConfig(itemTypeMap), null, null, false);
        conditionService.activateCondition(conditionConfig, myBase, null);
        passed = false;
        conditionService.setConditionServiceListener(new ConditionServiceListener<SimpleBase, Void>() {
            @Override
            public void conditionPassed(SimpleBase actor, Void identifier) {
                Assert.assertEquals(myBase, actor);
                passed = true;
            }
        });

        Assert.assertFalse(passed);
        conditionService.onMoneyIncrease(enemyBase, 3);
        Assert.assertFalse(passed);
        conditionService.onSyncItemKilled(enemyBase, itemMock1);
        Assert.assertFalse(passed);
        conditionService.onSyncItemKilled(myBase, itemMock1);
        Assert.assertFalse(passed);
        conditionService.onSyncItemKilled(myBase, itemMock2);
        Assert.assertFalse(passed);
        conditionService.onSyncItemKilled(myBase, itemMock2);
        Assert.assertTrue(passed);
    }

    @Test
    @DirtiesContext
    public void syncItemBuilt() throws Exception {
        SimulationConditionServiceImpl conditionService = SimulationConditionServiceImpl.getInstance();

        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_BUILT, new CountComparisonConfig(2), null, null, false);
        conditionService.activateCondition(conditionConfig, myBase, null);
        passed = false;
        conditionService.setConditionServiceListener(new ConditionServiceListener<SimpleBase, Void>() {
            @Override
            public void conditionPassed(SimpleBase actor, Void identifier) {
                Assert.assertEquals(myBase, actor);
                passed = true;
            }
        });

        Assert.assertFalse(passed);
        conditionService.onSyncItemKilled(myBase, itemMock1);
        Assert.assertFalse(passed);
        conditionService.onSyncItemBuilt(itemMock3);
        Assert.assertFalse(passed);
        conditionService.onSyncItemBuilt(itemMock4);
        Assert.assertFalse(passed);
        conditionService.onSyncItemBuilt(itemMock5);
        Assert.assertTrue(passed);
    }

    @Test
    @DirtiesContext
    public void syncItemBuilt2() throws Exception {
        SimulationConditionServiceImpl conditionService = SimulationConditionServiceImpl.getInstance();

        Map<ItemType, Integer> itemTypeMap = new HashMap<ItemType, Integer>();
        itemTypeMap.put(itemType2, 2);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_BUILT, new SyncItemTypeComparisonConfig(itemTypeMap), null, null, false);
        conditionService.activateCondition(conditionConfig, myBase, null);
        passed = false;
        conditionService.setConditionServiceListener(new ConditionServiceListener<SimpleBase, Void>() {
            @Override
            public void conditionPassed(SimpleBase actor, Void identifier) {
                Assert.assertEquals(myBase, actor);
                passed = true;
            }
        });

        Assert.assertFalse(passed);
        conditionService.onSyncItemKilled(myBase, itemMock1);
        Assert.assertFalse(passed);
        conditionService.onSyncItemBuilt(itemMock3);
        Assert.assertFalse(passed);
        conditionService.onSyncItemBuilt(itemMock4);
        Assert.assertFalse(passed);
        conditionService.onSyncItemBuilt(itemMock5);
        Assert.assertFalse(passed);
        conditionService.onSyncItemBuilt(itemMock5);
        Assert.assertTrue(passed);
    }

    @Test
    @DirtiesContext
    public void baseDeleted() throws Exception {
        final SimpleBase myBase = new SimpleBase(1, 1);
        ClientBase.getInstance().setBase(myBase);
        SimpleBase enemyBase = new SimpleBase(2, 1);

        SimulationConditionServiceImpl conditionService = SimulationConditionServiceImpl.getInstance();

        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.BASE_KILLED, new CountComparisonConfig(1), null, null, false);
        conditionService.activateCondition(conditionConfig, myBase, null);
        passed = false;
        conditionService.setConditionServiceListener(new ConditionServiceListener<SimpleBase, Void>() {
            @Override
            public void conditionPassed(SimpleBase actor, Void identifier) {
                Assert.assertEquals(myBase, actor);
                passed = true;
            }
        });

        Assert.assertFalse(passed);
        conditionService.onBaseDeleted(enemyBase);
        Assert.assertFalse(passed);
        conditionService.onBaseDeleted(myBase);
        Assert.assertTrue(passed);
    }
}
