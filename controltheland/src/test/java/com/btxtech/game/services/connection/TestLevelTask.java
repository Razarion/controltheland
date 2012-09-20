package com.btxtech.game.services.connection;

import com.btxtech.game.jsre.client.dialogs.quest.QuestInfo;
import com.btxtech.game.jsre.common.packets.LevelPacket;
import com.btxtech.game.jsre.common.packets.LevelTaskPacket;
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

/**
 * User: beat
 * Date: 29.06.12
 * Time: 15:42
 */
public class TestLevelTask extends AbstractServiceTest {
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
        LevelTaskPacket levelTaskPacket = getMovableService().getRealGameInfo(START_UID_1).getLevelTaskPacket();
        Assert.assertEquals(new QuestInfo(TEST_LEVEL_TASK_1_2_REAL_NAME, "Descr2", 100, 10, TEST_LEVEL_TASK_1_2_REAL_ID, QuestInfo.Type.QUEST, null), levelTaskPacket.getQuestInfo());
        Assert.assertFalse(levelTaskPacket.isCompleted());
        Assert.assertEquals("Money: 0 of 3", levelTaskPacket.getActiveQuestProgress());
        serverConditionService.onMoneyIncrease(getMyBase(), 11);
        levelTaskPacket = getMovableService().getRealGameInfo(START_UID_1).getLevelTaskPacket();
        Assert.assertEquals(new QuestInfo(TEST_LEVEL_TASK_2_2_REAL_NAME, "Descr222", 120, 80, TEST_LEVEL_TASK_2_2_REAL_ID, QuestInfo.Type.QUEST, null), levelTaskPacket.getQuestInfo());
        Assert.assertFalse(levelTaskPacket.isCompleted());
        Assert.assertEquals("build 0", levelTaskPacket.getActiveQuestProgress());
        userGuidanceService.promote(userService.getUserState(), TEST_LEVEL_3_REAL_ID);
        levelTaskPacket = getMovableService().getRealGameInfo(START_UID_1).getLevelTaskPacket();
        Assert.assertEquals(new QuestInfo("Task3Level2", "DecrTask3Level2", 100, 10, TEST_LEVEL_TASK_1_3_REAL_ID, QuestInfo.Type.QUEST, null), levelTaskPacket.getQuestInfo());
        userGuidanceService.activateQuest(TEST_LEVEL_TASK_3_3_SIMULATED_ID);
        levelTaskPacket = getMovableService().getRealGameInfo(START_UID_1).getLevelTaskPacket();
        Assert.assertEquals(new QuestInfo(TEST_LEVEL_TASK_3_3_SIMULATED_NAME, "Task3Level2Descr", 2, 0, TEST_LEVEL_TASK_3_3_SIMULATED_ID, QuestInfo.Type.MISSION, null), levelTaskPacket.getQuestInfo());
        // No quests / missions
        userGuidanceService.promote(userService.getUserState(), TEST_LEVEL_4_REAL_ID);
        userGuidanceServiceImpl.conditionPassed(getUserState(), TEST_LEVEL_TASK_1_4_REAL_ID);
        userGuidanceServiceImpl.conditionPassed(getUserState(), TEST_LEVEL_TASK_2_4_REAL_ID);
        levelTaskPacket = getMovableService().getRealGameInfo(START_UID_1).getLevelTaskPacket();
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
        getMyBase(); // Generate Base
        Assert.assertTrue(getPackages(LevelPacket.class).isEmpty());
        serverConditionService.onMoneyIncrease(getMyBase(), 1);
        List<LevelTaskPacket> levelTaskPackets = getPackages(LevelTaskPacket.class);
        Assert.assertEquals(1, levelTaskPackets.size());
        Assert.assertFalse(levelTaskPackets.get(0).isCompleted());
        Assert.assertNull(levelTaskPackets.get(0).getQuestInfo());
        Assert.assertEquals("Money: 1 of 3", levelTaskPackets.get(0).getActiveQuestProgress());
        serverConditionService.onMoneyIncrease(getMyBase(), 3);
        levelTaskPackets = getPackages(LevelTaskPacket.class);
        Assert.assertEquals(2, levelTaskPackets.size());
        Assert.assertTrue(levelTaskPackets.get(0).isCompleted());
        Assert.assertNull(levelTaskPackets.get(0).getQuestInfo());
        Assert.assertNull(levelTaskPackets.get(0).getActiveQuestProgress());
        Assert.assertFalse(levelTaskPackets.get(1).isCompleted());
        Assert.assertEquals(new QuestInfo(TEST_LEVEL_TASK_2_2_REAL_NAME, "Descr222", 120, 80, TEST_LEVEL_TASK_2_2_REAL_ID, QuestInfo.Type.QUEST, null), levelTaskPackets.get(1).getQuestInfo());
        Assert.assertEquals("build 0", levelTaskPackets.get(1).getActiveQuestProgress());
        userGuidanceService.promote(userService.getUserState(), TEST_LEVEL_3_REAL_ID);
        levelTaskPackets = getPackages(LevelTaskPacket.class);
        Assert.assertEquals(1, levelTaskPackets.size());
        Assert.assertFalse(levelTaskPackets.get(0).isCompleted());
        Assert.assertEquals(new QuestInfo("Task3Level2", "DecrTask3Level2", 100, 10, TEST_LEVEL_TASK_1_3_REAL_ID, QuestInfo.Type.QUEST, null), levelTaskPackets.get(0).getQuestInfo());
        Assert.assertEquals("", levelTaskPackets.get(0).getActiveQuestProgress());
        userGuidanceService.activateQuest(TEST_LEVEL_TASK_3_3_SIMULATED_ID);
        levelTaskPackets = getPackages(LevelTaskPacket.class);
        Assert.assertEquals(1, levelTaskPackets.size());
        Assert.assertFalse(levelTaskPackets.get(0).isCompleted());
        Assert.assertEquals(new QuestInfo(TEST_LEVEL_TASK_3_3_SIMULATED_NAME, "Task3Level2Descr", 2, 0, TEST_LEVEL_TASK_3_3_SIMULATED_ID, QuestInfo.Type.MISSION, null), levelTaskPackets.get(0).getQuestInfo());
        Assert.assertNull(levelTaskPackets.get(0).getActiveQuestProgress());
        // No quests / missions
        userGuidanceService.promote(userService.getUserState(), TEST_LEVEL_4_REAL_ID);
        userGuidanceServiceImpl.conditionPassed(getUserState(), TEST_LEVEL_TASK_1_4_REAL_ID);
        getPackages(LevelTaskPacket.class);// Clear
        userGuidanceServiceImpl.conditionPassed(getUserState(), TEST_LEVEL_TASK_2_4_REAL_ID);
        levelTaskPackets = getPackages(LevelTaskPacket.class);
        Assert.assertEquals(1, levelTaskPackets.size());
        Assert.assertTrue(levelTaskPackets.get(0).isCompleted());
        Assert.assertNull(levelTaskPackets.get(0).getQuestInfo());
        Assert.assertNull(levelTaskPackets.get(0).getActiveQuestProgress());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

}
