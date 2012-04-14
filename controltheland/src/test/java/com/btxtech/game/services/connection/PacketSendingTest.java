package com.btxtech.game.services.connection;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.info.RealGameInfo;
import com.btxtech.game.jsre.common.AccountBalancePacket;
import com.btxtech.game.jsre.common.LevelStatePacket;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.utg.condition.AbstractSyncItemComparison;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.condition.ServerConditionService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

/**
 * User: beat
 * Date: 04.04.2011
 * Time: 16:26:59
 */
public class PacketSendingTest extends AbstractServiceTest {
    @Autowired
    private UserGuidanceService userGuidanceService;
    @Autowired
    private UserService userService;
    @Autowired
    private ServerConditionService serverConditionService;

    @Test
    @DirtiesContext
    public void testCreateAndSell() throws Exception {
        configureRealGame();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        System.out.println("----- testSimple -----");

        RealGameInfo realGameInfo = getMovableService().getRealGameInfo(); // Connection is created here. Don't call movableService.getGameInfo() again!
        SimpleBase simpleBase = realGameInfo.getBase();
        // Buy
        sendBuildCommand(getFirstSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID), new Index(1000, 100), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();

        AccountBalancePacket accountBalancePacket = new AccountBalancePacket();
        accountBalancePacket.setAccountBalance(998);
        LevelStatePacket levelStatePacket = new LevelStatePacket();
        levelStatePacket.setXp(1);
        assertPackagesIgnoreSyncItemInfoAndClear(accountBalancePacket, levelStatePacket);

        // Sell
        getMovableService().sellItem(getFirstSynItemId(simpleBase, TEST_FACTORY_ITEM_ID));
        waitForActionServiceDone();
        accountBalancePacket = new AccountBalancePacket();
        accountBalancePacket.setAccountBalance(999);
        assertPackagesIgnoreSyncItemInfoAndClear(accountBalancePacket);

        System.out.println("----- testSimple end -----");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testLevelUp() throws Exception {
        setPrivateStaticField(AbstractSyncItemComparison.class, "MIN_SEND_DELAY", 0);
        configureGameMultipleLevel();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userGuidanceService.getDbLevel();
        userGuidanceService.onTutorialFinished(TEST_LEVEL_TASK_1_1_SIMULATED_ID);
        // Enter real game
        Assert.assertEquals(TEST_LEVEL_2_REAL_ID, (int) userGuidanceService.getDbLevel().getId());
        SimpleBase simpleBase = getMyBase(); // Connection
        //Thread.sleep(1000); // Wait for AccountBalancePacket
        //AccountBalancePacket accountBalancePacket = new AccountBalancePacket();
        //accountBalancePacket.setAccountBalance(1000);
        //assertPackagesIgnoreSyncItemInfoAndClear(accountBalancePacket);
        // First task
        userGuidanceService.activateLevelTaskCms(TEST_LEVEL_TASK_1_2_REAL_ID);
        LevelStatePacket levelStatePacket = new LevelStatePacket();
        levelStatePacket.setActiveQuestProgress("Money: 0 of 3");
        levelStatePacket.setActiveQuestLevelTaskId(TEST_LEVEL_TASK_1_2_REAL_ID);
        levelStatePacket.setActiveQuestTitle(TEST_LEVEL_TASK_1_2_REAL_NAME);
        assertPackagesIgnoreSyncItemInfoAndClear(levelStatePacket);
        serverConditionService.onMoneyIncrease(simpleBase, 1.0);
        serverConditionService.onMoneyIncrease(simpleBase, 1.0);
        levelStatePacket = new LevelStatePacket();
        levelStatePacket.setActiveQuestProgress("Money: 2 of 3");
        assertPackagesIgnoreSyncItemInfoAndClear(levelStatePacket);
        // First task done
        serverConditionService.onMoneyIncrease(simpleBase, 3.0);
        AccountBalancePacket accountBalancePacket = new AccountBalancePacket();
        accountBalancePacket.setAccountBalance(1010);
        levelStatePacket = new LevelStatePacket();
        levelStatePacket.setXp(100);
        levelStatePacket.setActiveQuestProgress("Money: 5 of 3");
        levelStatePacket.setQuestsDone(1);
        levelStatePacket.setTotalQuests(2);
        levelStatePacket.setMissionsDone(0);
        levelStatePacket.setTotalMissions(0);
        levelStatePacket.setMissionQuestCompleted(true);
        assertPackagesIgnoreSyncItemInfoAndClear(accountBalancePacket, levelStatePacket);
        // Activate second task
        userGuidanceService.activateLevelTaskCms(TEST_LEVEL_TASK_2_2_REAL_ID);
        levelStatePacket = new LevelStatePacket();
        levelStatePacket.setActiveQuestProgress("build 0");
        levelStatePacket.setActiveQuestLevelTaskId(TEST_LEVEL_TASK_2_2_REAL_ID);
        levelStatePacket.setActiveQuestTitle(TEST_LEVEL_TASK_2_2_REAL_NAME);
        assertPackagesIgnoreSyncItemInfoAndClear(levelStatePacket);
        userGuidanceService.deactivateLevelTaskCms(TEST_LEVEL_TASK_2_2_REAL_ID);
        levelStatePacket = new LevelStatePacket();
        levelStatePacket.setQuestDeactivated(true);
        assertPackagesIgnoreSyncItemInfoAndClear(levelStatePacket);
        userGuidanceService.activateLevelTaskCms(TEST_LEVEL_TASK_2_2_REAL_ID);
        levelStatePacket = new LevelStatePacket();
        levelStatePacket.setActiveQuestProgress("build 0");
        levelStatePacket.setActiveQuestLevelTaskId(TEST_LEVEL_TASK_2_2_REAL_ID);
        levelStatePacket.setActiveQuestTitle(TEST_LEVEL_TASK_2_2_REAL_NAME);
        assertPackagesIgnoreSyncItemInfoAndClear(levelStatePacket);
        sendBuildCommand(getFirstSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID), new Index(200, 200), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        sendFactoryCommand(getFirstSynItemId(simpleBase, TEST_FACTORY_ITEM_ID), TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone();
        accountBalancePacket = new AccountBalancePacket();
        accountBalancePacket.setAccountBalance(1085);
        levelStatePacket = new LevelStatePacket();
        levelStatePacket.setXp(0);
        levelStatePacket.setXp2LevelUp(400);
        levelStatePacket.setActiveQuestProgress("build 2");
        levelStatePacket.setQuestsDone(0);
        levelStatePacket.setTotalQuests(2);
        levelStatePacket.setMissionsDone(0);
        levelStatePacket.setTotalMissions(2);
        levelStatePacket.setMissionQuestCompleted(true);
        levelStatePacket.setLevel(userGuidanceService.getDbLevel().createLevelScope());
        assertPackagesIgnoreSyncItemInfoAndClear(accountBalancePacket, levelStatePacket);
        Assert.assertEquals(TEST_LEVEL_3_REAL_ID, (int) userGuidanceService.getDbLevel().getId());

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }
}
