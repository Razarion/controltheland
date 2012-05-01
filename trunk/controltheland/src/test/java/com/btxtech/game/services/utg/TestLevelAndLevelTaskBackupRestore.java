package com.btxtech.game.services.utg;

import com.btxtech.game.jsre.common.tutorial.TutorialConfig;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.mgmt.BackupSummary;
import com.btxtech.game.services.mgmt.MgmtService;
import com.btxtech.game.services.user.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

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
    private MgmtService mgmtService;

    @Test
    @DirtiesContext
    public void simpleLevelup() throws Exception {
        configureGameMultipleLevel();

        // U1 no real base, second level
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("U1", "test", "test", "test");
        userService.login("U1", "test");
        Assert.assertEquals(TEST_LEVEL_1_SIMULATED, userGuidanceService.getDbLevel().getNumber());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        mgmtService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<BackupSummary> backupSummaries = mgmtService.getBackupSummary();
        mgmtService.restore(backupSummaries.get(0).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("U1", "test");
        Assert.assertEquals(TEST_LEVEL_1_SIMULATED, userGuidanceService.getDbLevel().getNumber());
        getMovableService().sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "", userGuidanceService.getDefaultLevelTaskId(), "", 0, 0);
        Assert.assertEquals(TEST_LEVEL_2_REAL, userGuidanceService.getDbLevel().getNumber());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void levelTaskMissionDone() throws Exception {
        configureGameMultipleLevel();

        // U1 no real base, second level
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("U1", "test", "test", "test");
        userService.login("U1", "test");
        userGuidanceService.promote(userService.getUserState(), TEST_LEVEL_3_REAL_ID);
        Assert.assertEquals(2, userGuidanceService.getMercenaryMissionCms().readDbChildren().size());
        Assert.assertEquals(2, userGuidanceService.getQuestsCms().readDbChildren().size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Backup
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        mgmtService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("U1", "test");
        Assert.assertEquals(2, userGuidanceService.getMercenaryMissionCms().readDbChildren().size());
        Assert.assertEquals(2, userGuidanceService.getQuestsCms().readDbChildren().size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Restore
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<BackupSummary> backupSummaries = mgmtService.getBackupSummary();
        mgmtService.restore(backupSummaries.get(0).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify & change
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("U1", "test");
        Assert.assertEquals(2, userGuidanceService.getMercenaryMissionCms().readDbChildren().size());
        Assert.assertEquals(2, userGuidanceService.getQuestsCms().readDbChildren().size());
        for (LevelQuest levelQuest : userGuidanceService.getQuestsCms().readDbChildren()) {
            Assert.assertFalse(levelQuest.isDone());
        }
        for (LevelQuest levelQuest : userGuidanceService.getMercenaryMissionCms().readDbChildren()) {
            Assert.assertFalse(levelQuest.isDone());
        }
        // Complete tutorial
        userGuidanceService.onTutorialFinished(TEST_LEVEL_TASK_3_3_SIMULATED_ID);
        for (LevelQuest levelQuest : userGuidanceService.getQuestsCms().readDbChildren()) {
            Assert.assertFalse(levelQuest.isDone());
        }
        for (LevelQuest levelQuest : userGuidanceService.getMercenaryMissionCms().readDbChildren()) {
            Assert.assertEquals(levelQuest.getDbLevelTask().getId().equals(TEST_LEVEL_TASK_3_3_SIMULATED_ID), levelQuest.isDone());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Backup
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        mgmtService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Restore
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupSummaries = mgmtService.getBackupSummary();
        mgmtService.restore(backupSummaries.get(0).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify & change
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("U1", "test");
        Assert.assertEquals(2, userGuidanceService.getMercenaryMissionCms().readDbChildren().size());
        Assert.assertEquals(2, userGuidanceService.getQuestsCms().readDbChildren().size());
        for (LevelQuest levelQuest : userGuidanceService.getQuestsCms().readDbChildren()) {
            Assert.assertFalse(levelQuest.isDone());
        }
        for (LevelQuest levelQuest : userGuidanceService.getMercenaryMissionCms().readDbChildren()) {
            Assert.assertEquals(levelQuest.getDbLevelTask().getId().equals(TEST_LEVEL_TASK_3_3_SIMULATED_ID), levelQuest.isDone());
        }
        // Complete tutorial
        userGuidanceService.onTutorialFinished(TEST_LEVEL_TASK_4_3_SIMULATED_ID);
        for (LevelQuest levelQuest : userGuidanceService.getQuestsCms().readDbChildren()) {
            Assert.assertFalse(levelQuest.isDone());
        }
        for (LevelQuest levelQuest : userGuidanceService.getMercenaryMissionCms().readDbChildren()) {
            Assert.assertTrue(levelQuest.isDone());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Backup
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        mgmtService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Restore
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupSummaries = mgmtService.getBackupSummary();
        mgmtService.restore(backupSummaries.get(0).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify & change
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("U1", "test");
        Assert.assertEquals(2, userGuidanceService.getMercenaryMissionCms().readDbChildren().size());
        Assert.assertEquals(2, userGuidanceService.getQuestsCms().readDbChildren().size());
        for (LevelQuest levelQuest : userGuidanceService.getQuestsCms().readDbChildren()) {
            Assert.assertFalse(levelQuest.isDone());
        }
        for (LevelQuest levelQuest : userGuidanceService.getMercenaryMissionCms().readDbChildren()) {
            Assert.assertTrue(levelQuest.isDone());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // U2 no real base, second level
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("U2", "test", "test", "test");
        userService.login("U2", "test");
        userGuidanceService.promote(userService.getUserState(), TEST_LEVEL_3_REAL_ID);
        Assert.assertEquals(2, userGuidanceService.getMercenaryMissionCms().readDbChildren().size());
        Assert.assertEquals(2, userGuidanceService.getQuestsCms().readDbChildren().size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Backup
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        mgmtService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Restore
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupSummaries = mgmtService.getBackupSummary();
        mgmtService.restore(backupSummaries.get(0).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify U1
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("U1", "test");
        Assert.assertEquals(2, userGuidanceService.getMercenaryMissionCms().readDbChildren().size());
        Assert.assertEquals(2, userGuidanceService.getQuestsCms().readDbChildren().size());
        for (LevelQuest levelQuest : userGuidanceService.getQuestsCms().readDbChildren()) {
            Assert.assertFalse(levelQuest.isDone());
        }
        for (LevelQuest levelQuest : userGuidanceService.getMercenaryMissionCms().readDbChildren()) {
            Assert.assertTrue(levelQuest.isDone());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify U2 & change
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("U2", "test");
        Assert.assertEquals(2, userGuidanceService.getMercenaryMissionCms().readDbChildren().size());
        Assert.assertEquals(2, userGuidanceService.getQuestsCms().readDbChildren().size());
        for (LevelQuest levelQuest : userGuidanceService.getQuestsCms().readDbChildren()) {
            Assert.assertFalse(levelQuest.isDone());
        }
        for (LevelQuest levelQuest : userGuidanceService.getMercenaryMissionCms().readDbChildren()) {
            Assert.assertFalse(levelQuest.isDone());
        }
        // Complete tutorial
        userGuidanceService.onTutorialFinished(TEST_LEVEL_TASK_3_3_SIMULATED_ID);
        for (LevelQuest levelQuest : userGuidanceService.getQuestsCms().readDbChildren()) {
            Assert.assertFalse(levelQuest.isDone());
        }
        for (LevelQuest levelQuest : userGuidanceService.getMercenaryMissionCms().readDbChildren()) {
            Assert.assertEquals(levelQuest.getDbLevelTask().getId().equals(TEST_LEVEL_TASK_3_3_SIMULATED_ID), levelQuest.isDone());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Backup
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        mgmtService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Restore
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupSummaries = mgmtService.getBackupSummary();
        mgmtService.restore(backupSummaries.get(0).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify U1
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("U1", "test");
        Assert.assertEquals(2, userGuidanceService.getMercenaryMissionCms().readDbChildren().size());
        Assert.assertEquals(2, userGuidanceService.getQuestsCms().readDbChildren().size());
        for (LevelQuest levelQuest : userGuidanceService.getQuestsCms().readDbChildren()) {
            Assert.assertFalse(levelQuest.isDone());
        }
        for (LevelQuest levelQuest : userGuidanceService.getMercenaryMissionCms().readDbChildren()) {
            Assert.assertTrue(levelQuest.isDone());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify U2 & change
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("U2", "test");
        Assert.assertEquals(2, userGuidanceService.getMercenaryMissionCms().readDbChildren().size());
        Assert.assertEquals(2, userGuidanceService.getQuestsCms().readDbChildren().size());
        for (LevelQuest levelQuest : userGuidanceService.getQuestsCms().readDbChildren()) {
            Assert.assertFalse(levelQuest.isDone());
        }
        for (LevelQuest levelQuest : userGuidanceService.getMercenaryMissionCms().readDbChildren()) {
            Assert.assertEquals(levelQuest.getDbLevelTask().getId().equals(TEST_LEVEL_TASK_3_3_SIMULATED_ID), levelQuest.isDone());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }


}
