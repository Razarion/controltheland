package com.btxtech.game.services.connection;

import com.btxtech.game.jsre.client.cockpit.quest.QuestProgressInfo;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.dialogs.quest.QuestInfo;
import com.btxtech.game.jsre.common.packets.LevelPacket;
import com.btxtech.game.jsre.common.packets.LevelTaskPacket;
import com.btxtech.game.jsre.common.utg.config.ConditionTrigger;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.condition.ServerConditionService;
import com.btxtech.game.services.utg.impl.UserGuidanceServiceImpl;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Locale;

/**
 * User: beat
 * Date: 29.06.12
 * Time: 15:42
 */
public class TestLevelTaskConnection extends AbstractServiceTest {
    @Autowired
    private UserGuidanceService userGuidanceService;
    @Autowired
    private ServerConditionService serverConditionService;
    @Autowired
    private UserService userService;


    @Test
    @DirtiesContext
    public void realGameInfo() throws Exception {
        configureMultiplePlanetsAndLevels();

        UserGuidanceServiceImpl userGuidanceServiceImpl = (UserGuidanceServiceImpl) deAopProxy(userGuidanceService);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userGuidanceService.onTutorialFinished(TEST_LEVEL_TASK_1_1_SIMULATED_ID);
        createBase(new Index(1000,1000));
        LevelTaskPacket levelTaskPacket = getMovableService().getRealGameInfo(START_UID_1, null).getLevelTaskPacket();
        Assert.assertEquals(new QuestInfo(TEST_LEVEL_TASK_1_2_REAL_NAME, "Descr2", null, null, 100, 10, TEST_LEVEL_TASK_1_2_REAL_ID, QuestInfo.Type.QUEST, null, false, null), levelTaskPacket.getQuestInfo());
        Assert.assertFalse(levelTaskPacket.isCompleted());
        assertQuestProgressInfo(levelTaskPacket.getQuestProgressInfo(), ConditionTrigger.MONEY_INCREASED, 0, 3);
        serverConditionService.onMoneyIncrease(getOrCreateBase(), 11);
        levelTaskPacket = getMovableService().getRealGameInfo(START_UID_1, null).getLevelTaskPacket();
        Assert.assertEquals(new QuestInfo(TEST_LEVEL_TASK_2_2_REAL_NAME, "Descr222", null, null, 120, 80, TEST_LEVEL_TASK_2_2_REAL_ID, QuestInfo.Type.QUEST, null, false, null), levelTaskPacket.getQuestInfo());
        Assert.assertFalse(levelTaskPacket.isCompleted());
        assertQuestProgressInfo(levelTaskPacket.getQuestProgressInfo(), ConditionTrigger.SYNC_ITEM_BUILT, 0, 2);
        userGuidanceService.promote(userService.getUserState(), TEST_LEVEL_3_REAL_ID);
        levelTaskPacket = getMovableService().getRealGameInfo(START_UID_1, null).getLevelTaskPacket();
        Assert.assertEquals(new QuestInfo("Task3Level2", "DecrTask3Level2", null, null, 100, 10, TEST_LEVEL_TASK_1_3_REAL_ID, QuestInfo.Type.QUEST, null, false, null), levelTaskPacket.getQuestInfo());
        userGuidanceService.activateQuest(TEST_LEVEL_TASK_3_3_SIMULATED_ID, Locale.ENGLISH);
        levelTaskPacket = getMovableService().getRealGameInfo(START_UID_1, null).getLevelTaskPacket();
        Assert.assertEquals(new QuestInfo(TEST_LEVEL_TASK_3_3_SIMULATED_NAME, "Task3Level2Descr", null, null, 2, 0, TEST_LEVEL_TASK_3_3_SIMULATED_ID, QuestInfo.Type.MISSION, null, false, null), levelTaskPacket.getQuestInfo());
        // No quests / missions
        userGuidanceService.promote(userService.getUserState(), TEST_LEVEL_4_REAL_ID);
        userGuidanceServiceImpl.conditionPassed(getUserState(), TEST_LEVEL_TASK_1_4_REAL_ID);
        userGuidanceServiceImpl.conditionPassed(getUserState(), TEST_LEVEL_TASK_2_4_REAL_ID);
        levelTaskPacket = getMovableService().getRealGameInfo(START_UID_1, null).getLevelTaskPacket();
        Assert.assertNull(levelTaskPacket);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void packetSending() throws Exception {
        configureMultiplePlanetsAndLevels();

        UserGuidanceServiceImpl userGuidanceServiceImpl = (UserGuidanceServiceImpl) deAopProxy(userGuidanceService);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userGuidanceService.onTutorialFinished(TEST_LEVEL_TASK_1_1_SIMULATED_ID);
        getOrCreateBase(); // Generate Base
        Assert.assertTrue(getPackages(LevelPacket.class).isEmpty());
        serverConditionService.onMoneyIncrease(getOrCreateBase(), 1);
        List<LevelTaskPacket> levelTaskPackets = getPackages(LevelTaskPacket.class);
        Assert.assertEquals(1, levelTaskPackets.size());
        Assert.assertFalse(levelTaskPackets.get(0).isCompleted());
        Assert.assertNull(levelTaskPackets.get(0).getQuestInfo());
        assertQuestProgressInfo(levelTaskPackets.get(0).getQuestProgressInfo(), ConditionTrigger.MONEY_INCREASED, 1, 3);
        serverConditionService.onMoneyIncrease(getOrCreateBase(), 3);
        levelTaskPackets = getPackages(LevelTaskPacket.class);
        Assert.assertEquals(2, levelTaskPackets.size());
        Assert.assertTrue(levelTaskPackets.get(0).isCompleted());
        Assert.assertNull(levelTaskPackets.get(0).getQuestInfo());
        Assert.assertNull(levelTaskPackets.get(0).getQuestProgressInfo());
        Assert.assertFalse(levelTaskPackets.get(1).isCompleted());
        Assert.assertEquals(new QuestInfo(TEST_LEVEL_TASK_2_2_REAL_NAME, "Descr222", null, null, 120, 80, TEST_LEVEL_TASK_2_2_REAL_ID, QuestInfo.Type.QUEST, null, false, null), levelTaskPackets.get(1).getQuestInfo());
        assertQuestProgressInfo(levelTaskPackets.get(1).getQuestProgressInfo(), ConditionTrigger.SYNC_ITEM_BUILT, 0, 2);
        userGuidanceService.promote(userService.getUserState(), TEST_LEVEL_3_REAL_ID);
        levelTaskPackets = getPackages(LevelTaskPacket.class);
        Assert.assertEquals(1, levelTaskPackets.size());
        Assert.assertFalse(levelTaskPackets.get(0).isCompleted());
        Assert.assertEquals(new QuestInfo("Task3Level2", "DecrTask3Level2", null, null, 100, 10, TEST_LEVEL_TASK_1_3_REAL_ID, QuestInfo.Type.QUEST, null, false, null), levelTaskPackets.get(0).getQuestInfo());
        assertQuestProgressInfo(levelTaskPackets.get(0).getQuestProgressInfo(), ConditionTrigger.MONEY_INCREASED, 0, 200);
        userGuidanceService.activateQuest(TEST_LEVEL_TASK_3_3_SIMULATED_ID, Locale.ENGLISH);
        levelTaskPackets = getPackages(LevelTaskPacket.class);
        Assert.assertEquals(1, levelTaskPackets.size());
        Assert.assertFalse(levelTaskPackets.get(0).isCompleted());
        Assert.assertEquals(new QuestInfo(TEST_LEVEL_TASK_3_3_SIMULATED_NAME, "Task3Level2Descr", null, null, 2, 0, TEST_LEVEL_TASK_3_3_SIMULATED_ID, QuestInfo.Type.MISSION, null, false, null), levelTaskPackets.get(0).getQuestInfo());
        Assert.assertNull(levelTaskPackets.get(0).getQuestProgressInfo());
        // No quests / missions
        userGuidanceService.promote(userService.getUserState(), TEST_LEVEL_4_REAL_ID);
        userGuidanceServiceImpl.conditionPassed(getUserState(), TEST_LEVEL_TASK_1_4_REAL_ID);
        getPackages(LevelTaskPacket.class);// Clear
        userGuidanceServiceImpl.conditionPassed(getUserState(), TEST_LEVEL_TASK_2_4_REAL_ID);
        levelTaskPackets = getPackages(LevelTaskPacket.class);
        // TODO Failed on: 18.07.2013
        Assert.assertEquals(1, levelTaskPackets.size());
        Assert.assertTrue(levelTaskPackets.get(0).isCompleted());
        Assert.assertNull(levelTaskPackets.get(0).getQuestInfo());
        Assert.assertNull(levelTaskPackets.get(0).getQuestProgressInfo());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    private void assertQuestProgressInfo(QuestProgressInfo questProgressInfo, ConditionTrigger conditionTrigger, int amount, int totalAmount) {
        Assert.assertEquals(conditionTrigger, questProgressInfo.getConditionTrigger());
        Assert.assertEquals(amount, questProgressInfo.getAmount().getAmount());
        Assert.assertEquals(totalAmount, questProgressInfo.getAmount().getTotalAmount());
    }

}
