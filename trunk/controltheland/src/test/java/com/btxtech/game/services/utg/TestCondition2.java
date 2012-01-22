package com.btxtech.game.services.utg;

import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.utg.ConditionServiceListener;
import com.btxtech.game.jsre.common.utg.condition.CountComparison;
import com.btxtech.game.jsre.common.utg.config.ConditionConfig;
import com.btxtech.game.jsre.common.utg.config.ConditionTrigger;
import com.btxtech.game.jsre.common.utg.config.CountComparisonConfig;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.base.Base;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.mgmt.impl.DbUserState;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.condition.backup.DbAbstractComparisonBackup;
import com.btxtech.game.services.utg.condition.backup.DbCountComparisonBackup;
import com.btxtech.game.services.utg.impl.ServerConditionServiceImpl;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.HashMap;
import java.util.Map;

/**
 * User: beat
 * Date: 14.11.2011
 * Time: 13:43:08
 */
public class TestCondition2 extends AbstractServiceTest {
    @Autowired
    private ServerConditionService conditionService;
    private boolean passed = false;

    @Test
    @DirtiesContext
    public void baseDeleted() throws Exception {
        Assert.fail();
//        final UserState userState = new UserState();
//        Base base = new Base(userState, 1);
//        SimpleBase simpleBase1 = base.getSimpleBase();
//        SimpleBase simpleBase2 = new SimpleBase(2);
//
//        // Mock BaseService
//        BaseService baseServiceMock = EasyMock.createStrictMock(BaseService.class);
//        EasyMock.expect(baseServiceMock.getUserState(simpleBase1)).andReturn(userState);
//        EasyMock.expect(baseServiceMock.getUserState(simpleBase2)).andReturn(null);
//        EasyMock.expect(baseServiceMock.getUserState(simpleBase1)).andReturn(userState);
//        EasyMock.replay(baseServiceMock);
//
//        setPrivateField(ServerConditionServiceImpl.class, conditionService, "baseService", baseServiceMock);
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
//        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.BASE_KILLED, new CountComparisonConfig(null, 1));
//        conditionService.activateCondition(conditionConfig, userState);
//        conditionService.onBaseDeleted(simpleBase2);
//        Assert.assertFalse(passed);
//        conditionService.onBaseDeleted(simpleBase1);
//        Assert.assertTrue(passed);
    }

//    @Test
//    @DirtiesContext
//    public void baseDeleted2() throws Exception {
//        final UserState userState = new UserState();
//        Base base = new Base(userState, 1);
//        SimpleBase simpleBase1 = base.getSimpleBase();
//
//        // Mock BaseService
//        BaseService baseServiceMock = EasyMock.createStrictMock(BaseService.class);
//        EasyMock.expect(baseServiceMock.getUserState(simpleBase1)).andReturn(userState).times(3);
//        EasyMock.replay(baseServiceMock);
//
//        setPrivateField(ServerConditionServiceImpl.class, conditionService, "baseService", baseServiceMock);
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
//        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.BASE_KILLED, new CountComparisonConfig(null, 2));
//        conditionService.activateCondition(conditionConfig, userState);
//        Assert.assertFalse(passed);
//        conditionService.onBaseDeleted(simpleBase1);
//        Assert.assertFalse(passed);
//        conditionService.onBaseDeleted(simpleBase1);
//        Assert.assertTrue(passed);
//    }
//
//    @Test
//    @DirtiesContext
//    public void baseDeleted2BackupRestore() throws Exception {
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
//    }
//

}
