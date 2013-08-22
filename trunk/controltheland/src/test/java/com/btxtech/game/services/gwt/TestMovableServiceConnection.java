package com.btxtech.game.services.gwt;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.NoConnectionException;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.packets.Message;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.common.ServerPlanetServices;
import com.btxtech.game.services.connection.impl.ServerConnectionServiceImpl;
import com.btxtech.game.services.planet.PlanetSystemService;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Locale;

/**
 * User: beat
 * Date: 03.05.12
 * Time: 11:22
 */
public class TestMovableServiceConnection extends AbstractServiceTest {
    @Autowired
    private PlanetSystemService planetSystemService;

    @Test
    @DirtiesContext
    public void noConnection() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        try {
            getMovableService().getSyncInfo(START_UID_1, false);
            Assert.fail("NoConnectionException expected");
        } catch (com.btxtech.game.jsre.common.NoConnectionException e) {
            Assert.assertEquals(NoConnectionException.Type.NON_EXISTENT, e.getType());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void validConnectionNoBase() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMovableService().getRealGameInfo(START_UID_1, null);// Opens a connection
        Assert.assertTrue(getMovableService().getSyncInfo(START_UID_1, false).isEmpty());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void validConnectionWithBase() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMovableService().getRealGameInfo(START_UID_1, null);// Opens a connection
        getMovableService().createBase(START_UID_1, new Index(1000, 1000));
        Assert.assertFalse(getMovableService().getSyncInfo(START_UID_1, false).isEmpty());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void openConnectionOnBaseKilled() throws Exception {
        configureSimplePlanetNoResources();
        ServerPlanetServices serverPlanetServices = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMovableService().getRealGameInfo(START_UID_1, null);// Opens a connection
        getMovableService().createBase(START_UID_1, new Index(1000, 1000));
        Assert.assertFalse(getMovableService().getSyncInfo(START_UID_1, false).isEmpty());
        // Kill item
        SyncBaseItem builder = (SyncBaseItem) serverPlanetServices.getItemService().getItem(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID));
        serverPlanetServices.getBaseService().onItemDeleted(builder, null);

        Assert.assertFalse(getMovableService().getSyncInfo(START_UID_1, false).isEmpty());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void syncInfoStartUid() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMovableService().getRealGameInfo(START_UID_1, null);// Opens a connection
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
    public void syncInfoCloseSession() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMovableService().getRealGameInfo(START_UID_1, null);// Opens a connection
        getMovableService().getSyncInfo(START_UID_1, false);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        try {
            getMovableService().getSyncInfo(START_UID_1, false);
            Assert.fail("NoConnectionException expected");
        } catch (NoConnectionException e) {
            Assert.assertEquals(NoConnectionException.Type.NON_EXISTENT, e.getType());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }


    @Test
    @DirtiesContext
    public void syncInfoTimedOut() throws Exception {
        setPrivateStaticField(ServerConnectionServiceImpl.class, "USER_TRACKING_PERIODE", 10);
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMovableService().getRealGameInfo(START_UID_1, null);// Opens a connection
        getMovableService().getSyncInfo(START_UID_1, false);
        Thread.sleep(500);
        try {
            getMovableService().getSyncInfo(START_UID_1, false);
            Assert.fail("NoConnectionException expected");
        } catch (NoConnectionException e) {
            Assert.assertEquals(NoConnectionException.Type.TIMED_OUT, e.getType());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void syncInfoLoggedOut() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        getMovableService().getRealGameInfo(START_UID_1, null);
        getMovableService().getSyncInfo(START_UID_1, false);
        getMovableService().logout();
        try {
            getMovableService().getSyncInfo(START_UID_1, false);
            Assert.fail("NoConnectionException expected");
        } catch (NoConnectionException e) {
            Assert.assertEquals(NoConnectionException.Type.LOGGED_OUT, e.getType());
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
        getMovableService().getRealGameInfo(START_UID_1, null);
        getMovableService().getSyncInfo(START_UID_1, false);
        getMovableService().getRealGameInfo(START_UID_2, null);
        getMovableService().getSyncInfo(START_UID_2, false);
        try {
            getMovableService().getSyncInfo(START_UID_1, false);
            Assert.fail("NoConnectionException expected");
        } catch (NoConnectionException e) {
            Assert.assertEquals(NoConnectionException.Type.ANOTHER_CONNECTION_EXISTS, e.getType());
        }
        getMovableService().getSyncInfo(START_UID_2, false);
        getMovableService().getRealGameInfo(START_UID_1, null);
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
        getMovableService().getRealGameInfo(START_UID_1, null);
        getMovableService().createBase(START_UID_1, new Index(1000, 1000));
        getMovableService().surrenderBase();
        Assert.assertNotNull(getMovableService().getSyncInfo(START_UID_1, false));
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
        SimpleBase simpleBase = getOrCreateBase();
        clearPackets();
        planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getConnectionService().sendMessage(simpleBase, "guildOfferedOnlyRegistered", new Object[]{"Hallo"}, true);
        Message message = new Message();
        message.setMessage("Hallo invited you to his guild. Only registered user can can join guilds.");
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
        SimpleBase simpleBase = getOrCreateBase();
        clearPackets();
        planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getConnectionService().sendMessage(simpleBase, "guildOfferedOnlyRegistered", new Object[]{"Hallo"}, false);
        Message message = new Message();
        message.setMessage("Hallo hat dich in seine Gilde eingeladen. Nur registrierte Benutzer können Gilden beitreten.");
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
        final SimpleBase simpleBase = getOrCreateBase();
        clearPackets();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getConnectionService().sendMessage(simpleBase, "guildOfferedOnlyRegistered", new Object[]{"Hallo"}, false);
            }
        });
        thread.start();
        thread.join();
        Message message = new Message();
        message.setMessage("Hallo hat dich in seine Gilde eingeladen. Nur registrierte Benutzer können Gilden beitreten.");
        message.setShowRegisterDialog(false);
        // TODO failed on: 05.07.2013
        assertPackagesIgnoreSyncItemInfoAndClear(true, message);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

}
