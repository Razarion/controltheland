package com.btxtech.game.services.connection;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.NoConnectionException;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.packets.AccountBalancePacket;
import com.btxtech.game.jsre.common.packets.Packet;
import com.btxtech.game.jsre.common.packets.SyncItemInfo;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.item.ItemService;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

/**
 * User: beat
 * Date: 03.05.12
 * Time: 11:22
 */
public class TestConnection extends AbstractServiceTest {
    @Autowired
    private ConnectionService connectionService;
    @Autowired
    private BaseService baseService;
    @Autowired
    private ItemService itemService;

    @Test
    @DirtiesContext
    public void noPendingPackets() {
        Connection connection = new Connection("1234", null);
        Assert.assertTrue(connection.getAndRemovePendingPackets().isEmpty());
        Assert.assertTrue(connection.getAndRemovePendingPackets().isEmpty());
        Assert.assertTrue(connection.getAndRemovePendingPackets().isEmpty());
        Assert.assertTrue(connection.getAndRemovePendingPackets().isEmpty());
    }

    @Test
    @DirtiesContext
    public void pendingPackets() throws Exception {
        configureRealGame();

        Connection connection = new Connection("1234", null);
        SyncBaseItem attackItem = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(100, 100), new Id(1, 1, 1));
        connection.sendBaseSyncItem(attackItem);
        AccountBalancePacket accountBalancePacket = new AccountBalancePacket();
        accountBalancePacket.setAccountBalance(112);
        connection.sendPacket(accountBalancePacket);

        List<Packet> packets = connection.getAndRemovePendingPackets();
        Assert.assertEquals(2, packets.size());
        Assert.assertTrue(packets.get(0) instanceof AccountBalancePacket);
        Assert.assertTrue(packets.get(1) instanceof SyncItemInfo);
    }

    @Test
    @DirtiesContext
    public void noConnection() throws Exception {
        configureRealGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        try {
            connectionService.getConnection(START_UID_1);
            Assert.fail("NoConnectionException expected");
        } catch (com.btxtech.game.jsre.common.NoConnectionException e) {
            Assert.assertEquals(NoConnectionException.Type.NON_EXISTENT, e.getType());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void validConnection() throws Exception {
        configureRealGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMyBase(); // Opens a connection
        Assert.assertNotNull(connectionService.getConnection(START_UID_1));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void closeConnectionBaseKilled1() throws Exception {
        configureRealGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMyBase(); // Opens a connection
        Assert.assertNotNull(connectionService.getConnection(START_UID_1));
        SyncBaseItem builder = (SyncBaseItem) itemService.getItem(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID));
        baseService.onItemDeleted(builder, null);

        try {
            Assert.assertNotNull(connectionService.getConnection(START_UID_1));
            Assert.fail("NoConnectionException expected");
        } catch (com.btxtech.game.jsre.common.NoConnectionException e) {
            Assert.assertEquals(NoConnectionException.Type.BASE_LOST, e.getType());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void closeConnectionBaseKilled2() throws Exception {
        configureRealGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        SimpleBase humanBase = getMyBase(); // Opens a connection
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMyBase(); // Opens a connection
        Assert.assertNotNull(connectionService.getConnection(START_UID_1));
        SyncBaseItem builder = (SyncBaseItem) itemService.getItem(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID));
        baseService.onItemDeleted(builder, humanBase);

        try {
            Assert.assertNotNull(connectionService.getConnection(START_UID_1));
            Assert.fail("NoConnectionException expected");
        } catch (com.btxtech.game.jsre.common.NoConnectionException e) {
            Assert.assertEquals(NoConnectionException.Type.BASE_LOST, e.getType());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void closeConnectionBaseKilled3() throws Exception {
        configureRealGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        SimpleBase botBase = getMyBase(); // Opens a connection
        baseService.setBot(botBase, true);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMyBase(); // Opens a connection
        Assert.assertNotNull(connectionService.getConnection(START_UID_1));
        SyncBaseItem builder = (SyncBaseItem) itemService.getItem(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID));
        baseService.onItemDeleted(builder, botBase);

        try {
            Assert.assertNotNull(connectionService.getConnection(START_UID_1));
            Assert.fail("NoConnectionException expected");
        } catch (com.btxtech.game.jsre.common.NoConnectionException e) {
            Assert.assertEquals(NoConnectionException.Type.BASE_LOST, e.getType());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void syncInfoStartUid() throws Exception {
        configureRealGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMovableService().getRealGameInfo(START_UID_1);
        getMovableService().getSyncInfo(START_UID_1);
        try {
            getMovableService().getSyncInfo(START_UID_2);
            Assert.fail("NoConnectionException expected");
        } catch (NoConnectionException e) {
            Assert.assertEquals(NoConnectionException.Type.ANOTHER_CONNECTION_EXISTS, e.getType());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void twoConnectionSameSession() throws Exception {
        configureRealGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMovableService().getRealGameInfo(START_UID_1);
        getMovableService().getSyncInfo(START_UID_1);
        getMovableService().getRealGameInfo(START_UID_2);
        getMovableService().getSyncInfo(START_UID_2);
        try {
            getMovableService().getSyncInfo(START_UID_1);
            Assert.fail("NoConnectionException expected");
        } catch (NoConnectionException e) {
            Assert.assertEquals(NoConnectionException.Type.ANOTHER_CONNECTION_EXISTS, e.getType());
        }
        getMovableService().getSyncInfo(START_UID_2);
        getMovableService().getRealGameInfo(START_UID_1);
        getMovableService().getSyncInfo(START_UID_1);
        try {
            getMovableService().getSyncInfo(START_UID_2);
            Assert.fail("NoConnectionException expected");
        } catch (NoConnectionException e) {
            Assert.assertEquals(NoConnectionException.Type.ANOTHER_CONNECTION_EXISTS, e.getType());
        }
        getMovableService().getSyncInfo(START_UID_1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }
}
