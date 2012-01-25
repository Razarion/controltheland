package com.btxtech.game.services.utg.impl;

import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.utg.ConditionServiceListener;
import com.btxtech.game.jsre.common.utg.config.ConditionConfig;
import com.btxtech.game.jsre.common.utg.config.ConditionTrigger;
import com.btxtech.game.jsre.common.utg.config.CountComparisonConfig;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.base.Base;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.condition.ServerConditionService;
import com.btxtech.game.services.utg.condition.impl.ServerConditionServiceImpl;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Map;

/**
 * User: beat
 * Date: 23.01.2012
 * Time: 11:37:41
 */
public class TestServerConditionServiceImpl extends AbstractServiceTest {
    @Autowired
    private ServerConditionService serverConditionService;
    private UserState actor;
    private Integer identifier;
    private boolean passed = false;


    @Test
    @DirtiesContext
    public void multiplePlayers() throws Exception {
        configureRealGame();

        UserState userState1 = new UserState();
        User user1 = new User();
        user1.registerUser("TestUser1", "", "");
        userState1.setUser(user1);
        Base base1 = new Base(userState1, 1);

        UserState userState2 = new UserState();
        User user2 = new User();
        user2.registerUser("TestUser2", "", "");
        userState2.setUser(user2);
        Base base2 = new Base(userState2, 2);

        BaseService baseServiceMock = EasyMock.createNiceMock(BaseService.class);
        EasyMock.expect(baseServiceMock.getUserState(base1.getSimpleBase())).andReturn(userState1).anyTimes();
        EasyMock.expect(baseServiceMock.getUserState(base2.getSimpleBase())).andReturn(userState2).anyTimes();
        EasyMock.replay(baseServiceMock);

        setPrivateField(ServerConditionServiceImpl.class, serverConditionService, "baseService", baseServiceMock);

        ConditionConfig conditionTutorial = new ConditionConfig(ConditionTrigger.TUTORIAL, null);
        ConditionConfig conditionXp = new ConditionConfig(ConditionTrigger.XP_INCREASED, new CountComparisonConfig(null, 30));
        ConditionConfig conditionMoney = new ConditionConfig(ConditionTrigger.MONEY_INCREASED, new CountComparisonConfig(null, 80));

        serverConditionService.activateCondition(conditionXp, userState1, null);
        serverConditionService.activateCondition(conditionTutorial, userState1, 1);
        serverConditionService.activateCondition(conditionTutorial, userState1, 2);
        serverConditionService.activateCondition(conditionMoney, userState1, 3);

        serverConditionService.activateCondition(conditionXp, userState2, null);
        serverConditionService.activateCondition(conditionTutorial, userState2, 1);
        serverConditionService.activateCondition(conditionTutorial, userState2, 2);
        serverConditionService.activateCondition(conditionMoney, userState2, 3);

        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                TestServerConditionServiceImpl.this.actor = actor;
                TestServerConditionServiceImpl.this.identifier = identifier;
            }
        });
        actor = null;
        identifier = null;
        serverConditionService.onMoneyIncrease(base1.getSimpleBase(), 50);
        assertClearActorAndIdentifier();
        serverConditionService.onMoneyIncrease(base1.getSimpleBase(), 40);
        assertActorAndIdentifierAndClear(userState1, 3);
        serverConditionService.onMoneyIncrease(base1.getSimpleBase(), 40);
        assertClearActorAndIdentifier();
        serverConditionService.onMoneyIncrease(base2.getSimpleBase(), 60);
        assertClearActorAndIdentifier();
        serverConditionService.onIncreaseXp(userState1, 20);
        assertClearActorAndIdentifier();
        serverConditionService.onTutorialFinished(userState1, 1);
        assertActorAndIdentifierAndClear(userState1, 1);
        serverConditionService.onTutorialFinished(userState1, 1);
        assertClearActorAndIdentifier();
        serverConditionService.onIncreaseXp(userState2, 25);
        assertClearActorAndIdentifier();
        serverConditionService.onIncreaseXp(userState2, 5);
        assertActorAndIdentifierAndClear(userState2, null);
        serverConditionService.onTutorialFinished(userState2, 2);
        assertActorAndIdentifierAndClear(userState2, 2);
        serverConditionService.onTutorialFinished(userState2, 1);
        assertActorAndIdentifierAndClear(userState2, 1);
        serverConditionService.onTutorialFinished(userState2, 2);
        assertClearActorAndIdentifier();
        serverConditionService.onMoneyIncrease(base1.getSimpleBase(), 40);
        assertClearActorAndIdentifier();
        serverConditionService.onMoneyIncrease(base2.getSimpleBase(), 60);
        assertActorAndIdentifierAndClear(userState2, 3);
        serverConditionService.onTutorialFinished(userState1, 2);
        assertActorAndIdentifierAndClear(userState1, 2);
        serverConditionService.onIncreaseXp(userState1, 20);
        assertActorAndIdentifierAndClear(userState1, null);

        Map triggerMap = (Map) getPrivateField(ServerConditionServiceImpl.class, serverConditionService, "triggerMap");
        Assert.assertEquals(0, triggerMap.size());
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
    public void baseDeleted() throws Exception {
        final UserState userState = new UserState();
        Base base = new Base(userState, 1);
        SimpleBase simpleBase1 = base.getSimpleBase();
        SimpleBase simpleBase2 = new SimpleBase(2);

        // Mock BaseService
        BaseService baseServiceMock = EasyMock.createStrictMock(BaseService.class);
        EasyMock.expect(baseServiceMock.getUserState(simpleBase1)).andReturn(userState);
        EasyMock.expect(baseServiceMock.getUserState(simpleBase2)).andReturn(null);
        EasyMock.expect(baseServiceMock.getUserState(simpleBase1)).andReturn(userState);
        EasyMock.replay(baseServiceMock);

        setPrivateField(ServerConditionServiceImpl.class, serverConditionService, "baseService", baseServiceMock);

        passed = false;
        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                Assert.assertEquals(userState, actor);
                Assert.assertEquals(1, (int)identifier);
                passed = true;
            }
        });
        serverConditionService.onBaseDeleted(simpleBase1);
        Assert.assertFalse(passed);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.BASE_KILLED, new CountComparisonConfig(null, 1));
        serverConditionService.activateCondition(conditionConfig, userState, 1);
        serverConditionService.onBaseDeleted(simpleBase2);
        Assert.assertFalse(passed);
        serverConditionService.onBaseDeleted(simpleBase1);
        Assert.assertTrue(passed);
    }

    @Test
    @DirtiesContext
    public void baseDeleted2() throws Exception {
        final UserState userState = new UserState();
        Base base = new Base(userState, 1);
        SimpleBase simpleBase1 = base.getSimpleBase();

        // Mock BaseService
        BaseService baseServiceMock = EasyMock.createStrictMock(BaseService.class);
        EasyMock.expect(baseServiceMock.getUserState(simpleBase1)).andReturn(userState).times(3);
        EasyMock.replay(baseServiceMock);

        setPrivateField(ServerConditionServiceImpl.class, serverConditionService, "baseService", baseServiceMock);

        passed = false;
        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                Assert.assertEquals(userState, actor);
                Assert.assertEquals(1, (int)identifier);
                passed = true;
            }
        });
        serverConditionService.onBaseDeleted(simpleBase1);
        Assert.assertFalse(passed);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.BASE_KILLED, new CountComparisonConfig(null, 2));
        serverConditionService.activateCondition(conditionConfig, userState, 1);
        Assert.assertFalse(passed);
        serverConditionService.onBaseDeleted(simpleBase1);
        Assert.assertFalse(passed);
        serverConditionService.onBaseDeleted(simpleBase1);
        Assert.assertTrue(passed);
    }

    @Test
    @DirtiesContext
    public void baseDeleted2BackupRestore() throws Exception {
        Assert.fail("TODO backup & restore");
//        final UserState userState = new UserState();
//        DbRealGameLevel dbRealGameLevel = new DbRealGameLevel();
//        setPrivateField(DbLevel.class, dbRealGameLevel, "id", 1);
//        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.BASE_KILLED, new CountComparisonConfig(null, 2));
//        setPrivateField(DbLevel.class, dbRealGameLevel, "conditionConfig", conditionConfig);
//
//        userState.setDbLevel(dbRealGameLevel);
//        Base base = new Base(userState, 1);
//        SimpleBase simpleBase1 = base.getSimpleBase();
//
//        // Mock BaseService
//        BaseService baseServiceMock = EasyMock.createStrictMock(BaseService.class);
//        EasyMock.expect(baseServiceMock.getUserState(simpleBase1)).andReturn(userState).times(4);
//        EasyMock.replay(baseServiceMock);
//        setPrivateField(ServerConditionServiceImpl.class, conditionService, "baseService", baseServiceMock);
//
//        // Mock UserGuidanceService
//        UserGuidanceService userGuidanceServiceMock = EasyMock.createStrictMock(UserGuidanceService.class);
//        EasyMock.expect(userGuidanceServiceMock.getDbLevel(1)).andReturn(dbRealGameLevel);
//        EasyMock.replay(userGuidanceServiceMock);
//        setPrivateField(ServerConditionServiceImpl.class, conditionService, "userGuidanceService", userGuidanceServiceMock);
//
//        passed = false;
//        conditionService.setConditionServiceListener(new ConditionServiceListener<UserState>() {
//            @Override
//            public void conditionPassed(UserState userState1) {
//                passed = true;
//            }
//        });
//        conditionService.onBaseDeleted(simpleBase1);
//        Assert.assertFalse(passed);
//        conditionService.activateCondition(conditionConfig, userState);
//        Assert.assertFalse(passed);
//        conditionService.onBaseDeleted(simpleBase1);
//        Assert.assertFalse(passed);
//
//        // Backup
//        DbUserState dbUserState = new DbUserState(null, userState);
//        DbAbstractComparisonBackup dbAbstractComparisonBackup = conditionService.createBackup(dbUserState, userState);
//        Assert.assertNotNull(dbAbstractComparisonBackup);
//        Assert.assertTrue(dbAbstractComparisonBackup instanceof DbCountComparisonBackup);
//        CountComparison backupCountComparison = new CountComparison(null, 0);
//        dbAbstractComparisonBackup.restore(backupCountComparison, null);
//        Assert.assertEquals(1, backupCountComparison.getCount(), 0.1);
//
//        // Fulfill before backup
//        conditionService.onBaseDeleted(simpleBase1);
//        Assert.assertTrue(passed);
//        passed = false;
//
//        // Restore
//        Map<DbUserState, UserState> userStates = new HashMap<DbUserState, UserState>();
//        dbUserState.setDbAbstractComparisonBackup(dbAbstractComparisonBackup);
//        userStates.put(dbUserState, userState);
//        conditionService.restoreBackup(userStates, null);
//
//        // Fulfill after backup
//        conditionService.onBaseDeleted(simpleBase1);
//        Assert.assertTrue(passed);
    }

}
