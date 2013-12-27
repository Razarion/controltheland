package com.btxtech.game.services.utg.impl;

import com.btxtech.game.jsre.client.cockpit.quest.QuestProgressInfo;
import com.btxtech.game.jsre.client.common.info.RealGameInfo;
import com.btxtech.game.jsre.client.dialogs.quest.QuestInfo;
import com.btxtech.game.jsre.common.utg.config.AbstractComparisonConfig;
import com.btxtech.game.jsre.common.utg.config.ConditionConfig;
import com.btxtech.game.jsre.common.utg.config.ConditionTrigger;
import com.btxtech.game.jsre.common.utg.config.CountComparisonConfig;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.history.HistoryService;
import com.btxtech.game.services.tutorial.DbTutorialConfig;
import com.btxtech.game.services.tutorial.TutorialService;
import com.btxtech.game.services.unlock.ServerUnlockService;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.DbLevel;
import com.btxtech.game.services.utg.DbLevelTask;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.condition.DbConditionConfig;
import com.btxtech.game.services.utg.condition.DbCountComparisonConfig;
import com.btxtech.game.services.utg.condition.ServerConditionService;
import junit.framework.Assert;
import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Locale;

/**
 * User: beat
 * Date: 01.07.12
 * Time: 20:10
 */
public class TestLevelTask extends AbstractServiceTest {
    @Autowired
    private UserGuidanceService userGuidanceService;
    @Autowired
    private TutorialService tutorialService;

    @Test
    @DirtiesContext
    public void levelTask() throws Exception {
        UserState userState = new UserState();

        UserService mockUserService = EasyMock.createStrictMock(UserService.class);
        EasyMock.expect(mockUserService.getUserState()).andReturn(userState).anyTimes();
        setPrivateField(UserGuidanceServiceImpl.class, userGuidanceService, "userService", mockUserService);

        ServerConditionService mockServerConditionService = EasyMock.createStrictMock(ServerConditionService.class);
        mockServerConditionService.activateCondition(eqConditionConfig(ConditionTrigger.TUTORIAL, null), EasyMock.eq(userState), EasyMock.eq(1));
        mockServerConditionService.activateCondition(eqConditionConfig(ConditionTrigger.XP_INCREASED, CountComparisonConfig.class), EasyMock.eq(userState), EasyMock.<Integer>isNull());
        mockServerConditionService.deactivateActorCondition(userState, 1);
        mockServerConditionService.activateCondition(eqConditionConfig(ConditionTrigger.BASE_KILLED, CountComparisonConfig.class), EasyMock.eq(userState), EasyMock.eq(2));
        EasyMock.expect(mockServerConditionService.getQuestProgressInfo(userState, 2)).andReturn(new QuestProgressInfo(ConditionTrigger.BASE_KILLED));
        EasyMock.expect(mockServerConditionService.getQuestProgressInfo(userState, 2)).andReturn(new QuestProgressInfo(ConditionTrigger.BASE_KILLED));
        mockServerConditionService.activateCondition(eqConditionConfig(ConditionTrigger.TUTORIAL, null), EasyMock.eq(userState), EasyMock.eq(3));
        mockServerConditionService.onTutorialFinished(userState, 3);
        mockServerConditionService.activateCondition(eqConditionConfig(ConditionTrigger.TUTORIAL, null), EasyMock.eq(userState), EasyMock.eq(1));
        mockServerConditionService.onTutorialFinished(userState, 1);
        setPrivateField(UserGuidanceServiceImpl.class, userGuidanceService, "serverConditionService", mockServerConditionService);

        HistoryService mockHistoryService = EasyMock.createStrictMock(HistoryService.class);
        mockHistoryService.addLevelTaskActivated(EasyMock.eq(userState), EasyMock.<DbLevelTask>anyObject());
        mockHistoryService.addLevelTaskDeactivated(EasyMock.eq(userState), EasyMock.<DbLevelTask>anyObject());
        mockHistoryService.addLevelTaskActivated(EasyMock.eq(userState), EasyMock.<DbLevelTask>anyObject());
        mockHistoryService.addLevelTaskCompletedEntry(EasyMock.eq(userState), EasyMock.<DbLevelTask>anyObject());
        mockHistoryService.addLevelTaskActivated(EasyMock.eq(userState), EasyMock.<DbLevelTask>anyObject());
        mockHistoryService.addLevelTaskCompletedEntry(EasyMock.eq(userState), EasyMock.<DbLevelTask>anyObject());
        mockHistoryService.addLevelTaskActivated(EasyMock.eq(userState), EasyMock.<DbLevelTask>anyObject());
        mockHistoryService.addLevelTaskCompletedEntry(EasyMock.eq(userState), EasyMock.<DbLevelTask>anyObject());
        setPrivateField(UserGuidanceServiceImpl.class, userGuidanceService, "historyService", mockHistoryService);

        ServerUnlockService serverUnlockServiceMock = EasyMock.createStrictMock(ServerUnlockService.class);
        EasyMock.expect(serverUnlockServiceMock.isQuestLocked(EasyMock.<QuestInfo>anyObject(), EasyMock.eq(userState))).andReturn(false).times(6);
        setPrivateField(UserGuidanceServiceImpl.class, userGuidanceService, "serverUnlockService", serverUnlockServiceMock);

        EasyMock.replay(mockUserService,  mockServerConditionService,mockHistoryService,serverUnlockServiceMock);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbTutorialConfig dbTutorialConfig = tutorialService.getDbTutorialCrudRootServiceHelper().createDbChild();
        DbLevel dbLevel1 = userGuidanceService.getDbLevelCrud().createDbChild();
        dbLevel1.setXp(100);
        DbLevelTask dbLevelTask0 = dbLevel1.getLevelTaskCrud().createDbChild();
        dbLevelTask0.setName("dbLevelTask0");
        dbLevelTask0.setDbTutorialConfig(dbTutorialConfig);
        DbLevelTask dbLevelTask1 = dbLevel1.getLevelTaskCrud().createDbChild();
        DbConditionConfig dbConditionConfig1 = new DbConditionConfig();
        dbConditionConfig1.setConditionTrigger(ConditionTrigger.BASE_KILLED);
        DbCountComparisonConfig dbCountComparisonConfig = new DbCountComparisonConfig();
        dbCountComparisonConfig.setCount(1);
        dbConditionConfig1.setDbAbstractComparisonConfig(dbCountComparisonConfig);
        dbLevelTask1.setDbConditionConfig(dbConditionConfig1);
        dbLevelTask1.setName("dbLevelTask1");
        DbLevelTask dbLevelTask2 = dbLevel1.getLevelTaskCrud().createDbChild();
        dbLevelTask2.setName("dbLevelTask2");
        dbLevelTask2.setDbTutorialConfig(dbTutorialConfig);
        userGuidanceService.getDbLevelCrud().updateDbChild(dbLevel1);
        userGuidanceService.activateLevels();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        UserGuidanceServiceImpl userGuidanceServiceImpl = (UserGuidanceServiceImpl) deAopProxy(userGuidanceService);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userGuidanceService.setLevelForNewUser(userState);
        Assert.assertEquals((int) dbLevelTask0.getId(), (int) getActiveQuestId());
        userGuidanceService.activateQuest(dbLevelTask1.getId(), Locale.ENGLISH);
        Assert.assertEquals((int) dbLevelTask1.getId(), (int) getActiveQuestId());
        userGuidanceServiceImpl.conditionPassed(userState, dbLevelTask1.getId());
        Assert.assertEquals((int) dbLevelTask2.getId(), (int) getActiveQuestId());
        userGuidanceService.onTutorialFinished(dbLevelTask2.getId());
        userGuidanceServiceImpl.conditionPassed(userState, dbLevelTask2.getId());
        Assert.assertEquals((int) dbLevelTask0.getId(), (int) getActiveQuestId());
        try {
            userGuidanceService.activateQuest(dbLevelTask1.getId(), Locale.ENGLISH);
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("DbLevelTask already done: DbLevelTask{id=2, name='dbLevelTask1}", e.getMessage());
        }
        Assert.assertEquals((int) dbLevelTask0.getId(), (int) getActiveQuestId());
        // Activate active level task
        userGuidanceService.activateQuest(dbLevelTask0.getId(), Locale.ENGLISH);
        Assert.assertEquals((int) dbLevelTask0.getId(), (int) getActiveQuestId());
        userGuidanceService.onTutorialFinished(dbLevelTask0.getId());
        userGuidanceServiceImpl.conditionPassed(userState, dbLevelTask0.getId());


        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        EasyMock.verify(mockUserService, mockServerConditionService, mockHistoryService,serverUnlockServiceMock);
    }

    private Integer getActiveQuestId() {
        RealGameInfo realGameInfo = new RealGameInfo();
        userGuidanceService.fillRealGameInfo(realGameInfo, Locale.ENGLISH);
        if (realGameInfo.getLevelTaskPacket() == null) {
            return null;
        }
        if (realGameInfo.getLevelTaskPacket().getQuestInfo() == null) {
            return null;
        }
        return realGameInfo.getLevelTaskPacket().getQuestInfo().getId();
    }

    public ConditionConfig eqConditionConfig(ConditionTrigger conditionTrigger, Class<? extends AbstractComparisonConfig> comparisonConfigClass) {
        EasyMock.reportMatcher(new ConditionConfigEquals(conditionTrigger, comparisonConfigClass));
        return null;
    }

    public class ConditionConfigEquals implements IArgumentMatcher {
        private ConditionTrigger conditionTrigger;
        private Class<? extends AbstractComparisonConfig> comparisonConfigClass;
        private String message;

        public ConditionConfigEquals(ConditionTrigger conditionTrigger, Class<? extends AbstractComparisonConfig> comparisonConfigClass) {
            this.conditionTrigger = conditionTrigger;
            this.comparisonConfigClass = comparisonConfigClass;
        }

        public boolean matches(Object actual) {
            if (!(actual instanceof ConditionConfig)) {
                message = "Passed argument is not a ConditionConfig: " + actual;
                return false;
            }
            ConditionConfig conditionConfig = (ConditionConfig) actual;
            if (conditionConfig.getConditionTrigger() != conditionTrigger) {
                message = "Passed condition trigger is wrong: " + conditionConfig.getConditionTrigger();
                return false;
            }
            if (comparisonConfigClass != null) {
                if (conditionConfig.getAbstractComparisonConfig() == null) {
                    message = "Passed comparison config is null";
                    return false;
                }
                if (!comparisonConfigClass.equals(conditionConfig.getAbstractComparisonConfig().getClass())) {
                    message = "Passed comparison config class is wrong: " + conditionConfig.getAbstractComparisonConfig().getClass();
                    return false;
                }
            } else {
                if (conditionConfig.getAbstractComparisonConfig() != null) {
                    message = "Passed comparison config class should be null: " + conditionConfig.getAbstractComparisonConfig().getClass();
                    return false;
                }
            }
            return true;
        }

        public void appendTo(StringBuffer buffer) {
            buffer.append("Wrong ConditionTrigger received: ");
            buffer.append(message);
        }
    }

}
