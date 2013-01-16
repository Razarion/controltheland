package com.btxtech.game.services.connection;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.NoConnectionException;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.packets.AccountBalancePacket;
import com.btxtech.game.jsre.common.packets.Message;
import com.btxtech.game.jsre.common.packets.Packet;
import com.btxtech.game.jsre.common.packets.SyncItemInfo;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.common.ServerPlanetServices;
import com.btxtech.game.services.planet.PlanetSystemService;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * User: beat
 * Date: 03.05.12
 * Time: 11:22
 */
public class TestConnection extends AbstractServiceTest {
    @Autowired
    private PlanetSystemService planetSystemService;

    @Test
    @DirtiesContext
    public void noPendingPackets() {
        Connection connection = new Connection("1234", null);
        Assert.assertTrue(connection.getAndRemovePendingPackets(false).isEmpty());
        Assert.assertTrue(connection.getAndRemovePendingPackets(false).isEmpty());
        Assert.assertTrue(connection.getAndRemovePendingPackets(false).isEmpty());
        Assert.assertTrue(connection.getAndRemovePendingPackets(false).isEmpty());
    }

    @Test
    @DirtiesContext
    public void pendingPackets() throws Exception {
        configureSimplePlanetNoResources();

        Connection connection = new Connection("1234", null);
        SyncBaseItem attackItem = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(100, 100), new Id(1, 1));
        connection.sendBaseSyncItem(attackItem);
        AccountBalancePacket accountBalancePacket = new AccountBalancePacket();
        accountBalancePacket.setAccountBalance(112);
        connection.sendPacket(accountBalancePacket);

        List<Packet> packets = connection.getAndRemovePendingPackets(false);
        Assert.assertEquals(2, packets.size());
        Assert.assertTrue(packets.get(0) instanceof AccountBalancePacket);
        Assert.assertTrue(packets.get(1) instanceof SyncItemInfo);
    }

    @Test
    @DirtiesContext
    public void pendingPacketsResendLast() throws Exception {
        configureSimplePlanetNoResources();

        Connection connection = new Connection("1234", null);
        SyncBaseItem attackItem = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(100, 100), new Id(1, 1));
        connection.sendBaseSyncItem(attackItem);
        AccountBalancePacket accountBalancePacket = new AccountBalancePacket();
        accountBalancePacket.setAccountBalance(112);
        connection.sendPacket(accountBalancePacket);

        List<Packet> packets = connection.getAndRemovePendingPackets(true);
        Assert.assertEquals(2, packets.size());
        Assert.assertTrue(packets.get(0) instanceof AccountBalancePacket);
        Assert.assertTrue(packets.get(1) instanceof SyncItemInfo);

        packets = connection.getAndRemovePendingPackets(true);
        Assert.assertEquals(2, packets.size());
        Assert.assertTrue(packets.get(0) instanceof AccountBalancePacket);
        Assert.assertTrue(packets.get(1) instanceof SyncItemInfo);

        packets = connection.getAndRemovePendingPackets(false);
        Assert.assertEquals(0, packets.size());
    }

    @Test
    @DirtiesContext
    public void noConnection() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        try {
            planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getConnectionService().getConnection(START_UID_1);
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
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMyBase(); // Opens a connection
        Assert.assertNotNull(planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getConnectionService().getConnection(START_UID_1));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void closeConnectionBaseKilled1() throws Exception {
        configureSimplePlanetNoResources();
        ServerPlanetServices serverPlanetServices = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMyBase(); // Opens a connection
        Assert.assertNotNull(planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getConnectionService().getConnection(START_UID_1));
        SyncBaseItem builder = (SyncBaseItem) serverPlanetServices.getItemService().getItem(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID));
        serverPlanetServices.getBaseService().onItemDeleted(builder, null);

        try {
            Assert.assertNotNull(planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getConnectionService().getConnection(START_UID_1));
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
        configureSimplePlanetNoResources();
        ServerPlanetServices serverPlanetServices = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        SimpleBase humanBase = getMyBase(); // Opens a connection
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMyBase(); // Opens a connection
        Assert.assertNotNull(planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getConnectionService().getConnection(START_UID_1));
        SyncBaseItem builder = (SyncBaseItem) serverPlanetServices.getItemService().getItem(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID));
        serverPlanetServices.getBaseService().onItemDeleted(builder, humanBase);

        try {
            Assert.assertNotNull(planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getConnectionService().getConnection(START_UID_1));
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
        configureSimplePlanetNoResources();
        ServerPlanetServices serverPlanetServices = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        SimpleBase botBase = getMyBase(); // Opens a connection
        serverPlanetServices.getBaseService().setBot(botBase, true);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMyBase(); // Opens a connection
        Assert.assertNotNull(planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getConnectionService().getConnection(START_UID_1));
        SyncBaseItem builder = (SyncBaseItem) serverPlanetServices.getItemService().getItem(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID));
        serverPlanetServices.getBaseService().onItemDeleted(builder, botBase);

        try {
            Assert.assertNotNull(planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getConnectionService().getConnection(START_UID_1));
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
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMovableService().getRealGameInfo(START_UID_1);
        getMovableService().getSyncInfo(START_UID_1, false);
        try {
            getMovableService().getSyncInfo(START_UID_2, false);
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
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMovableService().getRealGameInfo(START_UID_1);
        getMovableService().getSyncInfo(START_UID_1, false);
        getMovableService().getRealGameInfo(START_UID_2);
        getMovableService().getSyncInfo(START_UID_2, false);
        try {
            getMovableService().getSyncInfo(START_UID_1, false);
            Assert.fail("NoConnectionException expected");
        } catch (NoConnectionException e) {
            Assert.assertEquals(NoConnectionException.Type.ANOTHER_CONNECTION_EXISTS, e.getType());
        }
        getMovableService().getSyncInfo(START_UID_2, false);
        getMovableService().getRealGameInfo(START_UID_1);
        getMovableService().getSyncInfo(START_UID_1, false);
        try {
            getMovableService().getSyncInfo(START_UID_2, false);
            Assert.fail("NoConnectionException expected");
        } catch (NoConnectionException e) {
            Assert.assertEquals(NoConnectionException.Type.ANOTHER_CONNECTION_EXISTS, e.getType());
        }
        getMovableService().getSyncInfo(START_UID_1, false);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void surrender() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMovableService().getRealGameInfo(START_UID_1);
        getMovableService().surrenderBase();
        try {
            getMovableService().getSyncInfo(START_UID_1, false);
            Assert.fail("NoConnectionException expected");
        } catch (NoConnectionException e) {
            Assert.assertEquals(NoConnectionException.Type.BASE_SURRENDERED, e.getType());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void sendDebug() throws Exception {
        configureSimplePlanetNoResources();

        Date clientDate = new Date(1000000);

        Date serverBefore = new Date();
        beginHttpSession();
        String sessionId1 = getHttpSessionId();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMovableService().sendDebug(clientDate, "CAT1", "Text Text");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        Date serverAfter = new Date();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<ClientDebugEntry> dbEntries = HibernateUtil.loadAll(getSessionFactory(), ClientDebugEntry.class);
        Assert.assertEquals(1, dbEntries.size());
        ClientDebugEntry debugEntry1 = dbEntries.get(0);
        Assert.assertTrue(serverBefore.getTime() <= debugEntry1.getTimeStamp().getTime());
        Assert.assertTrue(serverAfter.getTime() >= debugEntry1.getTimeStamp().getTime());
        Assert.assertEquals(clientDate, debugEntry1.getClientTimeStamp());
        Assert.assertEquals(sessionId1, debugEntry1.getSessionId());
        Assert.assertNull(debugEntry1.getUserName());
        Assert.assertEquals("CAT1", debugEntry1.getCategory());
        Assert.assertEquals("Text Text", debugEntry1.getMessage());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void sendDebugRegistered() throws Exception {
        configureSimplePlanetNoResources();

        Date clientDate = new Date(1000000);

        Date serverBefore = new Date();
        beginHttpSession();
        String sessionId1 = getHttpSessionId();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("TestUser");
        getMyBase(); // Opens a connection
        getMovableService().sendDebug(clientDate, "CAT1", "Text Text");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        Date serverAfter = new Date();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<ClientDebugEntry> dbEntries = HibernateUtil.loadAll(getSessionFactory(), ClientDebugEntry.class);
        Assert.assertEquals(1, dbEntries.size());
        ClientDebugEntry debugEntry1 = dbEntries.get(0);
        Assert.assertTrue(serverBefore.getTime() <= debugEntry1.getTimeStamp().getTime());
        Assert.assertTrue(serverAfter.getTime() >= debugEntry1.getTimeStamp().getTime());
        Assert.assertEquals(clientDate, debugEntry1.getClientTimeStamp());
        Assert.assertEquals(sessionId1, debugEntry1.getSessionId());
        Assert.assertEquals("TestUser", debugEntry1.getUserName());
        Assert.assertEquals("CAT1", debugEntry1.getCategory());
        Assert.assertEquals("Text Text", debugEntry1.getMessage());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void sendPackageEn() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMockHttpServletRequest().addPreferredLocale(Locale.ENGLISH);
        SimpleBase simpleBase = getMyBase();
        clearPackets();
        planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getConnectionService().sendMessage(simpleBase, "alliancesOfferedNotRegistered", new Object[]{"Hallo"}, true);
        Message message = new Message();
        message.setMessage("The player Hallo is not registered. Only registered user can form alliances. Use the chat to persuade him to register!");
        message.setShowRegisterDialog(true);
        assertPackagesIgnoreSyncItemInfoAndClear(true, message);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void sendPackageDe() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMockHttpServletRequest().addPreferredLocale(Locale.GERMAN);
        SimpleBase simpleBase = getMyBase();
        clearPackets();
        planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getConnectionService().sendMessage(simpleBase, "alliancesOfferedNotRegistered", new Object[]{"Hallo"}, false);
        Message message = new Message();
        message.setMessage("Der Spieler Hallo ist nicht registriert. Nur registrierte Spieler können Allianzen eingehen. Benutze den Chat um den Spieler zum Registrieren zu überreden!");
        message.setShowRegisterDialog(false);
        assertPackagesIgnoreSyncItemInfoAndClear(true, message);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void sendPackageNoRequest() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMockHttpServletRequest().addPreferredLocale(Locale.GERMAN);
        final SimpleBase simpleBase = getMyBase();
        clearPackets();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getConnectionService().sendMessage(simpleBase, "alliancesOfferedNotRegistered", new Object[]{"Hallo"}, false);
            }
        });
        thread.start();
        thread.join();
        Message message = new Message();
        message.setMessage("Der Spieler Hallo ist nicht registriert. Nur registrierte Spieler können Allianzen eingehen. Benutze den Chat um den Spieler zum Registrieren zu überreden!");
        message.setShowRegisterDialog(false);
        assertPackagesIgnoreSyncItemInfoAndClear(true, message);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

}
