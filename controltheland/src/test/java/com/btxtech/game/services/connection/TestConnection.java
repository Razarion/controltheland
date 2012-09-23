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
import com.btxtech.game.services.common.ServerPlanetServices;
import com.btxtech.game.services.planet.PlanetSystemService;
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
    private PlanetSystemService planetSystemService;

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
        configureSimplePlanet();

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
        configureSimplePlanet();

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
        configureSimplePlanet();

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
        configureSimplePlanet();
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
        configureSimplePlanet();
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
        configureSimplePlanet();
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
        configureSimplePlanet();

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
        configureSimplePlanet();

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

    @Test
    @DirtiesContext
    public void surrender() throws Exception {
        configureSimplePlanet();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMovableService().getRealGameInfo(START_UID_1);
        getMovableService().surrenderBase();
        try {
            getMovableService().getSyncInfo(START_UID_1);
            Assert.fail("NoConnectionException expected");
        } catch (NoConnectionException e) {
            Assert.assertEquals(NoConnectionException.Type.BASE_SURRENDERED, e.getType());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

}
