package com.btxtech.game.services.connection;

import com.btxtech.game.jsre.client.MovableService;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.info.RealityInfo;
import com.btxtech.game.jsre.common.AccountBalancePacket;
import com.btxtech.game.jsre.common.LevelPacket;
import com.btxtech.game.jsre.common.Packet;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.XpBalancePacket;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.syncInfos.SyncItemInfo;
import com.btxtech.game.jsre.common.tutorial.HouseSpacePacket;
import com.btxtech.game.jsre.common.tutorial.TutorialConfig;
import com.btxtech.game.services.BaseTestService;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.utg.DbRealGameLevel;
import com.btxtech.game.services.utg.UserGuidanceService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * User: beat
 * Date: 04.04.2011
 * Time: 16:26:59
 */
public class PacketSendingTest extends BaseTestService {
    @Autowired
    private MovableService movableService;
    @Autowired
    private UserGuidanceService userGuidanceService;
    @Autowired
    private UserService userService;

    @Test
    @DirtiesContext
    public void testCreateAndSell() throws Exception {
        configureMinimalGame();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        System.out.println("----- testSimple -----");

        movableService.sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "", "", 0, 0);
        System.out.println("----- real game entered -----");
        RealityInfo realityInfo = (RealityInfo) movableService.getGameInfo(); // Connection is created here. Don't call movableService.getGameInfo() again!
        SimpleBase simpleBase = realityInfo.getBase();
        // Buy
        sendBuildCommand(getFirstSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID), new Index(1000, 100), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();

        AccountBalancePacket accountBalancePacket = new AccountBalancePacket();
        accountBalancePacket.setAccountBalance(998);
        assertPackagesIgnoreSyncItemInfoAndClear(simpleBase, accountBalancePacket);

        // Sell
        movableService.sellItem(getFirstSynItemId(simpleBase, TEST_FACTORY_ITEM_ID));
        waitForActionServiceDone();
        accountBalancePacket = new AccountBalancePacket();
        accountBalancePacket.setAccountBalance(999);
        assertPackagesIgnoreSyncItemInfoAndClear(simpleBase, accountBalancePacket);

        System.out.println("----- testSimple end -----");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testKillXp() throws Exception {
        configureMinimalGame();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // Create target
        movableService.sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "", "", 0, 0);
        Id target = getFirstSynItemId(TEST_START_BUILDER_ITEM_ID);
        sendBuildCommand(target, new Index(500, 100), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // Actor
        movableService.sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "", "", 0, 0);
        RealityInfo realityInfo = (RealityInfo) movableService.getGameInfo(); // Connection is created here. Don't call movableService.getGameInfo() again!
        SimpleBase simpleBase = realityInfo.getBase();
        // Attack
        sendBuildCommand(getFirstSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID), new Index(1000, 100), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        sendFactoryCommand(getFirstSynItemId(simpleBase, TEST_FACTORY_ITEM_ID), TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone();
        clearPackets(simpleBase);

        sendAttackCommand(getFirstSynItemId(simpleBase, TEST_ATTACK_ITEM_ID), target);
        waitForActionServiceDone();

        XpBalancePacket xpBalancePacket = new XpBalancePacket();
        xpBalancePacket.setXp(1);
        Thread.sleep(3000);
        assertPackagesIgnoreSyncItemInfoAndClear(simpleBase, xpBalancePacket);

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testLevelUp() throws Exception {
        configureMinimalGame();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // Create target
        movableService.sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "", "", 0, 0);
        RealityInfo realityInfo = (RealityInfo) movableService.getGameInfo(); // Connection is created here. Don't call movableService.getGameInfo() again!
        SimpleBase simpleBase = realityInfo.getBase();
        clearPackets(simpleBase);
        userGuidanceService.promote(userService.getUserState(), TEST_LEVEL_3_REAL_ID);

        DbRealGameLevel dbRealGameLevel = userGuidanceService.getDbLevel();
        LevelPacket levelPacket = new LevelPacket();
        levelPacket.setLevel(dbRealGameLevel.getLevel());

        AccountBalancePacket accountBalancePacket = new AccountBalancePacket();
        accountBalancePacket.setAccountBalance(1500);

        XpBalancePacket xpBalancePacket = new XpBalancePacket();
        xpBalancePacket.setXp(500);        

        assertPackagesIgnoreSyncItemInfoAndClear(simpleBase, levelPacket, accountBalancePacket, xpBalancePacket);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

    }

    private void clearPackets(SimpleBase simpleBase) throws Exception {
        movableService.getSyncInfo(simpleBase);
    }

    private void assertPackagesIgnoreSyncItemInfoAndClear(SimpleBase simpleBase, Packet... expectedPackets) throws Exception {
        List<Packet> receivedPackets = new ArrayList<Packet>(movableService.getSyncInfo(simpleBase));
        for (Iterator<Packet> iterator = receivedPackets.iterator(); iterator.hasNext();) {
            if (iterator.next() instanceof SyncItemInfo) {
                iterator.remove();
            }

        }
        Assert.assertEquals(expectedPackets.length, receivedPackets.size());

        for (Packet expectedPacket : expectedPackets) {
            int index = receivedPackets.indexOf(expectedPacket);
            if (index < 0) {
                Assert.fail("Packet was not sent: " + expectedPacket);
            }
            comparePacket(expectedPacket, receivedPackets.get(index));
        }
    }

    private void comparePacket(Packet expectedPacket, Packet receivedPacket) {
        if (expectedPacket instanceof AccountBalancePacket) {
            AccountBalancePacket expected = (AccountBalancePacket) expectedPacket;
            AccountBalancePacket received = (AccountBalancePacket) receivedPacket;
            Assert.assertEquals(expected.getAccountBalance(), received.getAccountBalance(), 0.1);
            return;
        } else if(expectedPacket instanceof XpBalancePacket) {
            XpBalancePacket expected = (XpBalancePacket) expectedPacket;
            XpBalancePacket received = (XpBalancePacket) receivedPacket;
            Assert.assertEquals(expected.getXp(), received.getXp());
            return;
        } else if(expectedPacket instanceof LevelPacket) {
            LevelPacket expected = (LevelPacket) expectedPacket;
            LevelPacket received = (LevelPacket) receivedPacket;
            Assert.assertEquals(expected.getLevel(), received.getLevel());
            return;
        } else if(expectedPacket instanceof HouseSpacePacket) {
            HouseSpacePacket expected = (HouseSpacePacket) expectedPacket;
            HouseSpacePacket received = (HouseSpacePacket) receivedPacket;
            Assert.assertEquals(expected.getHouseSpace(), received.getHouseSpace());
            return;
        }
        Assert.fail("Unhandled packet: " + expectedPacket);
    }
}
