package com.btxtech.game.services.connection;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.info.RealGameInfo;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.packets.AccountBalancePacket;
import com.btxtech.game.services.AbstractServiceTest;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;

/**
 * User: beat
 * Date: 04.04.2011
 * Time: 16:26:59
 */
public class PacketSendingTest extends AbstractServiceTest {

    @Test
    @DirtiesContext
    public void testCreateAndSell() throws Exception {
        configureSimplePlanetNoResources();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        System.out.println("----- testSimple -----");

        RealGameInfo realGameInfo = getMovableService().getRealGameInfo(START_UID_1, null); // Connection is created here. Don't call movableService.getGameInfo() again!
        SimpleBase simpleBase = getOrCreateBase();
        Thread.sleep(1000); // Get rid of unpredictable account balance package
        clearPackets();
        // Buy
        sendBuildCommand(getFirstSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID), new Index(1000, 100), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();

        AccountBalancePacket accountBalancePacket1 = new AccountBalancePacket();
        accountBalancePacket1.setAccountBalance(1000);
        AccountBalancePacket accountBalancePacket2 = new AccountBalancePacket();
        accountBalancePacket2.setAccountBalance(998);
        AccountBalancePacket accountBalancePacket3 = new AccountBalancePacket();
        accountBalancePacket3.setAccountBalance(998);
        assertPackagesIgnoreSyncItemInfoAndClear(accountBalancePacket1, accountBalancePacket2, accountBalancePacket3);

        // Sell
        getMovableService().sellItem(START_UID_1, getFirstSynItemId(simpleBase, TEST_FACTORY_ITEM_ID));
        waitForActionServiceDone();
        accountBalancePacket1 = new AccountBalancePacket();
        accountBalancePacket1.setAccountBalance(999);
        assertPackagesIgnoreSyncItemInfoAndClear(accountBalancePacket1);

        System.out.println("----- testSimple end -----");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

}
