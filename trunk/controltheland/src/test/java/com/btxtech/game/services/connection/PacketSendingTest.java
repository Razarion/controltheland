package com.btxtech.game.services.connection;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.info.RealGameInfo;
import com.btxtech.game.jsre.common.AccountBalancePacket;
import com.btxtech.game.jsre.common.LevelPacket;
import com.btxtech.game.jsre.common.LevelTaskDonePacket;
import com.btxtech.game.jsre.common.SimpleBase;
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
        assertPackagesIgnoreSyncItemInfoAndClear(accountBalancePacket);

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
        configureGameMultipleLevel();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userGuidanceService.getDbLevel();
        userGuidanceService.onTutorialFinished(TEST_LEVEL_TASK_1_1_SIMULATED_ID);
        // Enter real game
        Assert.assertEquals(TEST_LEVEL_2_REAL_ID, (int) userGuidanceService.getDbLevel().getId());
        SimpleBase simpleBase = getMyBase(); // Connection
        assertPackagesIgnoreSyncItemInfoAndClear();
        // Complete first task
        serverConditionService.onMoneyIncrease(simpleBase, 3.0);
        AccountBalancePacket accountBalancePacket = new AccountBalancePacket();
        accountBalancePacket.setAccountBalance(1010);
        assertPackagesIgnoreSyncItemInfoAndClear(accountBalancePacket, new LevelTaskDonePacket());
        // Complete second task
        sendBuildCommand(getFirstSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID), new Index(200, 200), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        sendFactoryCommand(getFirstSynItemId(simpleBase, TEST_FACTORY_ITEM_ID), TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone();
        accountBalancePacket = new AccountBalancePacket();
        accountBalancePacket.setAccountBalance(1085);
        LevelPacket levelPacket = new LevelPacket();
        levelPacket.setLevel(userGuidanceService.getLevelScope());
        assertPackagesIgnoreSyncItemInfoAndClear(accountBalancePacket, new LevelTaskDonePacket(), levelPacket);
        Assert.assertEquals(TEST_LEVEL_3_REAL_ID, (int) userGuidanceService.getDbLevel().getId());

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }
}
