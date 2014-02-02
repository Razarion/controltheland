package com.btxtech.game.services.utg;

import com.btxtech.game.jsre.common.tutorial.TutorialConfig;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.mgmt.BackupService;
import com.btxtech.game.services.mgmt.BackupSummary;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.utg.impl.UserGuidanceServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Locale;

/**
 * User: beat
 * Date: 30.04.12
 * Time: 21:24
 */
public class TestLevelAndLevelTaskBackupRestore extends AbstractServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private UserGuidanceService userGuidanceService;
    @Autowired
    private BackupService backupService;

    @Test
    @DirtiesContext
    public void simpleLevelup() throws Exception {
        configureMultiplePlanetsAndLevels();

        // U1 no real base, second level
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        Assert.assertEquals(TEST_LEVEL_1_SIMULATED, userGuidanceService.getDbLevel().getNumber());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<BackupSummary> backupSummaries = backupService.getBackupSummary();
        backupService.restore(backupSummaries.get(0).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1", "test");
        Assert.assertEquals(TEST_LEVEL_1_SIMULATED, userGuidanceService.getDbLevel().getNumber());
        getMovableService().sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "", userGuidanceService.getDefaultLevelTaskId(), 0, "", 0, 0);
        Assert.assertEquals(TEST_LEVEL_2_REAL, userGuidanceService.getDbLevel().getNumber());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupSummaries = backupService.getBackupSummary();
        backupService.restore(backupSummaries.get(0).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1", "test");
        Assert.assertEquals(TEST_LEVEL_2_REAL, userGuidanceService.getDbLevel().getNumber());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

    }

    @Test
    @DirtiesContext
    public void levelTaskMissionDone() throws Exception {
        configureMultiplePlanetsAndLevels();

        // U1 no real base, second level
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        userGuidanceService.promote(userService.getUserState(), TEST_LEVEL_3_REAL_ID);
        Assert.assertEquals(4, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().size());
        Assert.assertEquals(TEST_LEVEL_TASK_1_3_REAL_ID, getMovableService().getRealGameInfo(START_UID_1, null).getLevelTaskPacket().getQuestInfo().getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Backup
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1", "test");
        Assert.assertEquals(4, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().size());
        Assert.assertEquals(TEST_LEVEL_TASK_1_3_REAL_ID, getMovableService().getRealGameInfo(START_UID_1, null).getLevelTaskPacket().getQuestInfo().getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Restore
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<BackupSummary> backupSummaries = backupService.getBackupSummary();
        backupService.restore(backupSummaries.get(0).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1", "test");
        Assert.assertEquals(4, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().size());
        Assert.assertEquals(TEST_LEVEL_TASK_1_3_REAL_ID, getMovableService().getRealGameInfo(START_UID_1, null).getLevelTaskPacket().getQuestInfo().getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        //Change
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1", "test");
        Assert.assertEquals(4, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().size());
        userGuidanceService.activateQuest(TEST_LEVEL_TASK_3_3_SIMULATED_ID, Locale.ENGLISH);
        Assert.assertEquals(TEST_LEVEL_TASK_3_3_SIMULATED_ID, getMovableService().getRealGameInfo(START_UID_1, null).getLevelTaskPacket().getQuestInfo().getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Backup
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1", "test");
        Assert.assertEquals(4, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().size());
        Assert.assertEquals(TEST_LEVEL_TASK_3_3_SIMULATED_ID, getMovableService().getRealGameInfo(START_UID_1, null).getLevelTaskPacket().getQuestInfo().getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Restore
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupSummaries = backupService.getBackupSummary();
        backupService.restore(backupSummaries.get(0).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify & change
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1", "test");
        Assert.assertEquals(4, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().size());
        Assert.assertEquals(TEST_LEVEL_TASK_1_3_REAL_ID, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().get(0).getId());
        Assert.assertEquals(TEST_LEVEL_TASK_2_3_REAL_ID, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().get(1).getId());
        Assert.assertEquals(TEST_LEVEL_TASK_3_3_SIMULATED_ID, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().get(2).getId());
        Assert.assertEquals(TEST_LEVEL_TASK_4_3_SIMULATED_ID, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().get(3).getId());
        Assert.assertEquals(TEST_LEVEL_TASK_3_3_SIMULATED_ID, getMovableService().getRealGameInfo(START_UID_1, null).getLevelTaskPacket().getQuestInfo().getId());
        // Complete tutorial
        userGuidanceService.onTutorialFinished(TEST_LEVEL_TASK_3_3_SIMULATED_ID);
        Assert.assertEquals(3, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().size());
        Assert.assertEquals(TEST_LEVEL_TASK_1_3_REAL_ID, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().get(0).getId());
        Assert.assertEquals(TEST_LEVEL_TASK_2_3_REAL_ID, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().get(1).getId());
        Assert.assertEquals(TEST_LEVEL_TASK_4_3_SIMULATED_ID, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().get(2).getId());
        Assert.assertEquals(TEST_LEVEL_TASK_4_3_SIMULATED_ID, getMovableService().getRealGameInfo(START_UID_1, null).getLevelTaskPacket().getQuestInfo().getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Backup
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Restore
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupSummaries = backupService.getBackupSummary();
        backupService.restore(backupSummaries.get(0).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify & change
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1", "test");
        Assert.assertEquals(3, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().size());
        Assert.assertEquals(TEST_LEVEL_TASK_1_3_REAL_ID, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().get(0).getId());
        Assert.assertEquals(TEST_LEVEL_TASK_2_3_REAL_ID, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().get(1).getId());
        Assert.assertEquals(TEST_LEVEL_TASK_4_3_SIMULATED_ID, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().get(2).getId());
        Assert.assertEquals(TEST_LEVEL_TASK_4_3_SIMULATED_ID, getMovableService().getRealGameInfo(START_UID_1, null).getLevelTaskPacket().getQuestInfo().getId());
        // Complete tutorial
        userGuidanceService.onTutorialFinished(TEST_LEVEL_TASK_4_3_SIMULATED_ID);
        Assert.assertEquals(2, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().size());
        Assert.assertEquals(TEST_LEVEL_TASK_1_3_REAL_ID, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().get(0).getId());
        Assert.assertEquals(TEST_LEVEL_TASK_2_3_REAL_ID, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().get(1).getId());
        Assert.assertEquals(TEST_LEVEL_TASK_1_3_REAL_ID, getMovableService().getRealGameInfo(START_UID_1, null).getLevelTaskPacket().getQuestInfo().getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Backup
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Restore
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupSummaries = backupService.getBackupSummary();
        backupService.restore(backupSummaries.get(0).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify & change
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1", "test");
        Assert.assertEquals(2, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().size());
        Assert.assertEquals(TEST_LEVEL_TASK_1_3_REAL_ID, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().get(0).getId());
        Assert.assertEquals(TEST_LEVEL_TASK_2_3_REAL_ID, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().get(1).getId());
        Assert.assertEquals(TEST_LEVEL_TASK_1_3_REAL_ID, getMovableService().getRealGameInfo(START_UID_1, null).getLevelTaskPacket().getQuestInfo().getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // U2
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U2");
        userGuidanceService.promote(userService.getUserState(), TEST_LEVEL_3_REAL_ID);
        Assert.assertEquals(4, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().size());
        Assert.assertEquals(TEST_LEVEL_TASK_1_3_REAL_ID, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().get(0).getId());
        Assert.assertEquals(TEST_LEVEL_TASK_2_3_REAL_ID, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().get(1).getId());
        Assert.assertEquals(TEST_LEVEL_TASK_3_3_SIMULATED_ID, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().get(2).getId());
        Assert.assertEquals(TEST_LEVEL_TASK_4_3_SIMULATED_ID, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().get(3).getId());
        Assert.assertEquals(TEST_LEVEL_TASK_1_3_REAL_ID, getMovableService().getRealGameInfo(START_UID_1, null).getLevelTaskPacket().getQuestInfo().getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Backup
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Restore
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupSummaries = backupService.getBackupSummary();
        backupService.restore(backupSummaries.get(0).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify U1
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1", "test");
        Assert.assertEquals(2, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().size());
        Assert.assertEquals(TEST_LEVEL_TASK_1_3_REAL_ID, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().get(0).getId());
        Assert.assertEquals(TEST_LEVEL_TASK_2_3_REAL_ID, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().get(1).getId());
        Assert.assertEquals(TEST_LEVEL_TASK_1_3_REAL_ID, getMovableService().getRealGameInfo(START_UID_1, null).getLevelTaskPacket().getQuestInfo().getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        UserGuidanceServiceImpl userGuidanceServiceImpl = (UserGuidanceServiceImpl) deAopProxy(userGuidanceService);

        // Verify U2 & change
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U2", "test");
        Assert.assertEquals(4, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().size());
        Assert.assertEquals(TEST_LEVEL_TASK_1_3_REAL_ID, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().get(0).getId());
        Assert.assertEquals(TEST_LEVEL_TASK_2_3_REAL_ID, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().get(1).getId());
        Assert.assertEquals(TEST_LEVEL_TASK_3_3_SIMULATED_ID, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().get(2).getId());
        Assert.assertEquals(TEST_LEVEL_TASK_4_3_SIMULATED_ID, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().get(3).getId());
        Assert.assertEquals(TEST_LEVEL_TASK_1_3_REAL_ID, getMovableService().getRealGameInfo(START_UID_1, null).getLevelTaskPacket().getQuestInfo().getId());
        // Complete quest
        userGuidanceServiceImpl.conditionPassed(getUserState(), TEST_LEVEL_TASK_1_3_REAL_ID);
        Assert.assertEquals(3, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().size());
        Assert.assertEquals(TEST_LEVEL_TASK_2_3_REAL_ID, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().get(0).getId());
        Assert.assertEquals(TEST_LEVEL_TASK_3_3_SIMULATED_ID, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().get(1).getId());
        Assert.assertEquals(TEST_LEVEL_TASK_4_3_SIMULATED_ID, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().get(2).getId());
        Assert.assertEquals(TEST_LEVEL_TASK_2_3_REAL_ID, getMovableService().getRealGameInfo(START_UID_1, null).getLevelTaskPacket().getQuestInfo().getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Backup
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Restore
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupSummaries = backupService.getBackupSummary();
        backupService.restore(backupSummaries.get(0).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify U1
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1", "test");
        Assert.assertEquals(2, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().size());
        Assert.assertEquals(TEST_LEVEL_TASK_1_3_REAL_ID, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().get(0).getId());
        Assert.assertEquals(TEST_LEVEL_TASK_2_3_REAL_ID, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().get(1).getId());
        Assert.assertEquals(TEST_LEVEL_TASK_1_3_REAL_ID, getMovableService().getRealGameInfo(START_UID_1, null).getLevelTaskPacket().getQuestInfo().getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify U2 & change
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U2", "test");
        Assert.assertEquals(3, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().size());
        Assert.assertEquals(TEST_LEVEL_TASK_2_3_REAL_ID, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().get(0).getId());
        Assert.assertEquals(TEST_LEVEL_TASK_3_3_SIMULATED_ID, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().get(1).getId());
        Assert.assertEquals(TEST_LEVEL_TASK_4_3_SIMULATED_ID, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().get(2).getId());
        Assert.assertEquals(TEST_LEVEL_TASK_2_3_REAL_ID, getMovableService().getRealGameInfo(START_UID_1, null).getLevelTaskPacket().getQuestInfo().getId());
        // Change
        userGuidanceService.promote(getUserState(), TEST_LEVEL_4_REAL_ID);
        Assert.assertEquals(2, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().size());
        Assert.assertEquals(TEST_LEVEL_TASK_1_4_REAL_ID, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().get(0).getId());
        Assert.assertEquals(TEST_LEVEL_TASK_2_4_REAL_ID, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().get(1).getId());
        Assert.assertEquals(TEST_LEVEL_TASK_1_4_REAL_ID, getMovableService().getRealGameInfo(START_UID_1, null).getLevelTaskPacket().getQuestInfo().getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Backup
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Restore
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupSummaries = backupService.getBackupSummary();
        backupService.restore(backupSummaries.get(0).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify U1
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1", "test");
        Assert.assertEquals(2, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().size());
        Assert.assertEquals(TEST_LEVEL_TASK_1_3_REAL_ID, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().get(0).getId());
        Assert.assertEquals(TEST_LEVEL_TASK_2_3_REAL_ID, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().get(1).getId());
        Assert.assertEquals(TEST_LEVEL_TASK_1_3_REAL_ID, getMovableService().getRealGameInfo(START_UID_1, null).getLevelTaskPacket().getQuestInfo().getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify U2 & change
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U2", "test");
        Assert.assertEquals(2, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().size());
        Assert.assertEquals(TEST_LEVEL_TASK_1_4_REAL_ID, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().get(0).getId());
        Assert.assertEquals(TEST_LEVEL_TASK_2_4_REAL_ID, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().get(1).getId());
        Assert.assertEquals(TEST_LEVEL_TASK_1_4_REAL_ID, getMovableService().getRealGameInfo(START_UID_1, null).getLevelTaskPacket().getQuestInfo().getId());
        // Change
        userGuidanceServiceImpl.conditionPassed(getUserState(), TEST_LEVEL_TASK_1_4_REAL_ID);
        userGuidanceServiceImpl.conditionPassed(getUserState(), TEST_LEVEL_TASK_2_4_REAL_ID);
        Assert.assertEquals(0, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().size());
        Assert.assertNull(getMovableService().getRealGameInfo(START_UID_1, null).getLevelTaskPacket());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Backup
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Restore
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupSummaries = backupService.getBackupSummary();
        backupService.restore(backupSummaries.get(0).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify U1
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1", "test");
        Assert.assertEquals(2, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().size());
        Assert.assertEquals(TEST_LEVEL_TASK_1_3_REAL_ID, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().get(0).getId());
        Assert.assertEquals(TEST_LEVEL_TASK_2_3_REAL_ID, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().get(1).getId());
        Assert.assertEquals(TEST_LEVEL_TASK_1_3_REAL_ID, getMovableService().getRealGameInfo(START_UID_1, null).getLevelTaskPacket().getQuestInfo().getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify U2
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U2", "test");
        Assert.assertEquals(0, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().size());
        Assert.assertNull(getMovableService().getRealGameInfo(START_UID_1, null).getLevelTaskPacket());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Restore to old version
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupSummaries = backupService.getBackupSummary();
        backupService.restore(backupSummaries.get(7).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify U1
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1", "test");
        Assert.assertEquals(4, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().size());
        Assert.assertEquals(TEST_LEVEL_TASK_1_3_REAL_ID, getMovableService().getRealGameInfo(START_UID_1, null).getLevelTaskPacket().getQuestInfo().getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify U2
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U2", "test");
        Assert.assertEquals(1, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().size());
        Assert.assertEquals(TEST_LEVEL_TASK_1_1_SIMULATED_ID, userGuidanceService.getQuestOverview(Locale.ENGLISH).getQuestInfos().get(0).getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

    }


}
