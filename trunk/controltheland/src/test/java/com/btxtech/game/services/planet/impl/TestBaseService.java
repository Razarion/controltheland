package com.btxtech.game.services.planet.impl;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.NoConnectionException;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.packets.AccountBalancePacket;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.common.ServerGlobalServices;
import com.btxtech.game.services.common.impl.ServerGlobalServicesImpl;
import com.btxtech.game.services.planet.BaseService;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.user.AllianceService;
import com.btxtech.game.services.user.UserService;
import junit.framework.Assert;
import org.easymock.EasyMock;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

/**
 * User: beat
 * Date: 07.04.2011
 * Time: 13:28:45
 */
public class TestBaseService extends AbstractServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private PlanetSystemService planetSystemService;
    @Autowired
    private ServerGlobalServices serverGlobalServices;

    @Test
    @DirtiesContext
    public void testSellBaseItem() throws Exception {
        configureSimplePlanetNoResources();
        BaseService baseService = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getBaseService();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertNull(userService.getUser());
        userService.createUser("U1", "test", "test", "test");
        userService.login("U1", "test");
        // $1000
        SimpleBase simpleBase = getMyBase(); // Setup connection & create two account balance package
        Id id = getFirstSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID);
        sendBuildCommand(id, new Index(400, 400), TEST_FACTORY_ITEM_ID);
        // $998
        waitForActionServiceDone();
        Assert.assertEquals(998, baseService.getBase(simpleBase).getAccountBalance(), 0.1);
        clearPackets();
        getMovableService().sellItem(id);
        // $999
        AccountBalancePacket accountBalancePacket = new AccountBalancePacket();
        accountBalancePacket.setAccountBalance(998.5);
        assertPackagesIgnoreSyncItemInfoAndClear(accountBalancePacket/*, levelStatePacket*/);

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testSellLastBaseItem() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertNull(userService.getUser());
        userService.createUser("U1", "test", "test", "test");
        userService.login("U1", "test");
        SimpleBase simpleBase = getMyBase(); // Setup connection
        Id id = getFirstSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID);
        clearPackets();
        getMovableService().sellItem(id);

        try {
            getMovableService().getSyncInfo(START_UID_1);
            Assert.fail("NoConnectionException expected");
        } catch (NoConnectionException e) {
            Assert.assertEquals(NoConnectionException.Type.BASE_LOST, e.getType());
        }

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testGetBaseItems() throws Exception {
        configureSimplePlanetNoResources();
        BaseService baseService = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getBaseService();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("U1", "test", "test", "test");
        userService.login("U1", "test");
        SimpleBase simpleBase = getMyBase(); // Setup connection
        sendBuildCommand(getFirstSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID), new Index(100, 100), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        // TODO failed on 02.07.2012
        Assert.assertEquals(2, baseService.getBase().getItemCount());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("U1", "test");
        Assert.assertEquals(2, baseService.getBase().getItemCount());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void surrenderBase() throws Exception {
        configureSimplePlanetNoResources();
        BaseService baseService = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getBaseService();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        SimpleBase simpleBase = getMyBase(); // Setup connection
        sendBuildCommand(getFirstSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID), new Index(100, 100), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        Assert.assertEquals(2, baseService.getBase().getItemCount());
        Assert.assertFalse(baseService.getBase(simpleBase).isAbandoned());
        baseService.surrenderBase(baseService.getBase(simpleBase));
        Assert.assertTrue(baseService.getBase(simpleBase).isAbandoned());
        Assert.assertNull(userService.getUserState().getBase());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void surrenderBaseAllianceUnregistered() throws Exception {
        configureSimplePlanetNoResources();
        BaseService baseService = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getBaseService();

        AllianceService allianceServiceMock = EasyMock.createStrictMock(AllianceService.class);
        setPrivateField(ServerGlobalServicesImpl.class, serverGlobalServices,"allianceService",allianceServiceMock);
        EasyMock.replay(allianceServiceMock);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        SimpleBase simpleBase = getMyBase(); // Setup connection
        waitForActionServiceDone(TEST_PLANET_1_ID);
        baseService.surrenderBase(baseService.getBase(simpleBase));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        EasyMock.verify(allianceServiceMock);
    }

    @Test
    @DirtiesContext
    public void surrenderBaseAllianceRegistered() throws Exception {
        configureSimplePlanetNoResources();
        BaseService baseService = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getBaseService();

        AllianceService allianceServiceMock = EasyMock.createStrictMock(AllianceService.class);
        setPrivateField(ServerGlobalServicesImpl.class, serverGlobalServices,"allianceService",allianceServiceMock);
        allianceServiceMock.onBaseCreatedOrDeleted("U1");
        allianceServiceMock.onBaseCreatedOrDeleted("U1");
        EasyMock.replay(allianceServiceMock);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("U1", "test", "test", "test");
        userService.login("U1", "test");
        SimpleBase simpleBase = getMyBase(); // Setup connection
        waitForActionServiceDone();
        baseService.surrenderBase(baseService.getBase(simpleBase));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        EasyMock.verify(allianceServiceMock);
    }

}
