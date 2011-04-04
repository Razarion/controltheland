package com.btxtech.game.services.connection;

import com.btxtech.game.jsre.client.MovableService;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.info.RealityInfo;
import com.btxtech.game.jsre.common.AccountBalancePacket;
import com.btxtech.game.jsre.common.Packet;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.syncObjects.syncInfos.SyncItemInfo;
import com.btxtech.game.jsre.common.tutorial.TutorialConfig;
import com.btxtech.game.services.BaseTestService;
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
        } else if (expectedPacket instanceof SyncItemInfo) {
            // TODO
            return;
        }
    }
}
