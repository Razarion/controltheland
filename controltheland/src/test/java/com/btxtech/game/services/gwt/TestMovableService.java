package com.btxtech.game.services.gwt;

import com.btxtech.game.jsre.client.common.info.SimpleUser;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.RadarMode;
import com.btxtech.game.jsre.client.common.info.InvalidLevelStateException;
import com.btxtech.game.jsre.client.common.info.RealGameInfo;
import com.btxtech.game.jsre.client.common.info.SimulationInfo;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.connection.DbClientDebugEntry;
import com.btxtech.game.services.user.RegisterService;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import com.btxtech.game.wicket.uiservices.cms.impl.CmsUiServiceImpl;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Date;
import java.util.List;

/**
 * User: beat
 * Date: 14.02.2012
 * Time: 12:17:07
 */
public class TestMovableService extends AbstractServiceTest {
    @Autowired
    private UserGuidanceService userGuidanceService;
    @Autowired
    private UserService userService;
    @Autowired
    private RegisterService registerService;
    @Autowired
    private CmsUiService cmsUiService;

    @Test
    @DirtiesContext
    public void getRealGameInfo() throws Exception {
        configureMultiplePlanetsAndLevels();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        SimulationInfo simulationInfo = getMovableService().getSimulationGameInfo(TEST_LEVEL_TASK_1_1_SIMULATED_ID);
        Assert.assertNotNull(simulationInfo);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void getRealGameStartPosition() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        RealGameInfo realGameInfo = getMovableService().getRealGameInfo(START_UID_1);
        assertWholeItemCount(TEST_PLANET_1_ID, 0);
        Assert.assertEquals(-3, realGameInfo.getBase().getBaseId());
        Assert.assertEquals(TEST_PLANET_1_ID, realGameInfo.getBase().getPlanetId());
        Assert.assertEquals(0.0, realGameInfo.getAccountBalance(), 0.001);
        Assert.assertEquals(0, realGameInfo.getEnergyConsuming());
        Assert.assertEquals(0, realGameInfo.getEnergyGenerating());
        Assert.assertEquals(0, realGameInfo.getHouseSpace());
        Assert.assertTrue(realGameInfo.isSellAllowed());
        Assert.assertEquals(TEST_PLANET_1, realGameInfo.getLevelScope().getPlanetLiteInfo().getName());
        Assert.assertEquals(TEST_PLANET_1_ID, realGameInfo.getLevelScope().getPlanetLiteInfo().getPlanetId());
        Assert.assertNull(realGameInfo.getLevelScope().getPlanetLiteInfo().getUnlockRazarion());
        Assert.assertEquals(Integer.MAX_VALUE, realGameInfo.getLevelScope().getXp2LevelUp());
        Assert.assertEquals(TEST_LEVEL_2_REAL, realGameInfo.getLevelScope().getNumber());
        Assert.assertNotNull(realGameInfo.getLevelScope().getItemTypeLimitation());
        // TODO Assert.assertNotNull(realGameInfo.getLevelTaskPacket().getQuestInfo());
        Assert.assertEquals(1, realGameInfo.getAllBase().size());
        Assert.assertNull(realGameInfo.getMySimpleGuild());
        Assert.assertEquals(0, realGameInfo.getXpPacket().getXp());
        Assert.assertEquals(Integer.MAX_VALUE, realGameInfo.getXpPacket().getXp2LevelUp());
        Assert.assertEquals(20, realGameInfo.getPlanetInfo().getHouseSpace());
        Assert.assertEquals(10000, realGameInfo.getPlanetInfo().getMaxMoney());
        Assert.assertEquals(TEST_PLANET_1, realGameInfo.getPlanetInfo().getName());
        Assert.assertEquals(TEST_PLANET_1_ID, realGameInfo.getPlanetInfo().getPlanetId());
        Assert.assertNotNull(realGameInfo.getPlanetInfo().getPlanetLiteInfo());
        Assert.assertEquals(RadarMode.NONE, realGameInfo.getPlanetInfo().getRadarMode());
        Assert.assertEquals(1, realGameInfo.getAllPlanets().size());
        Assert.assertNotNull(realGameInfo.getUnlockContainer().getItemTypes());
        Assert.assertNotNull(realGameInfo.getUnlockContainer().getPlanets());
        Assert.assertNotNull(realGameInfo.getUnlockContainer().getQuests());
        Assert.assertEquals(TEST_START_BUILDER_ITEM_ID, realGameInfo.getStartPointInfo().getBaseItemTypeId());
        Assert.assertEquals(300, realGameInfo.getStartPointInfo().getItemFreeRange());
        Assert.assertEquals(new Index(1001, 1001), realGameInfo.getStartPointInfo().getSuggestedPosition());
        assertWholeItemCount(TEST_PLANET_1_ID, 0);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void getRealGameWithBase() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createBase(new Index(1000, 1000));
        RealGameInfo realGameInfo = getMovableService().getRealGameInfo(START_UID_1);
        assertWholeItemCount(TEST_PLANET_1_ID, 1);
        Assert.assertEquals(1, realGameInfo.getBase().getBaseId());
        Assert.assertEquals(TEST_PLANET_1_ID, realGameInfo.getBase().getPlanetId());
        Assert.assertEquals(1000.0, realGameInfo.getAccountBalance(), 0.001);
        Assert.assertEquals(0, realGameInfo.getEnergyConsuming());
        Assert.assertEquals(0, realGameInfo.getEnergyGenerating());
        Assert.assertEquals(0, realGameInfo.getHouseSpace());
        Assert.assertTrue(realGameInfo.isSellAllowed());
        Assert.assertEquals(TEST_PLANET_1, realGameInfo.getLevelScope().getPlanetLiteInfo().getName());
        Assert.assertEquals(TEST_PLANET_1_ID, realGameInfo.getLevelScope().getPlanetLiteInfo().getPlanetId());
        Assert.assertNull(realGameInfo.getLevelScope().getPlanetLiteInfo().getUnlockRazarion());
        Assert.assertEquals(Integer.MAX_VALUE, realGameInfo.getLevelScope().getXp2LevelUp());
        Assert.assertEquals(TEST_LEVEL_2_REAL, realGameInfo.getLevelScope().getNumber());
        Assert.assertNotNull(realGameInfo.getLevelScope().getItemTypeLimitation());
        // TODO Assert.assertNotNull(realGameInfo.getLevelTaskPacket().getQuestInfo());
        Assert.assertEquals(1, realGameInfo.getAllBase().size());
        Assert.assertNull(realGameInfo.getMySimpleGuild());
        Assert.assertEquals(0, realGameInfo.getXpPacket().getXp());
        Assert.assertEquals(Integer.MAX_VALUE, realGameInfo.getXpPacket().getXp2LevelUp());
        Assert.assertEquals(20, realGameInfo.getPlanetInfo().getHouseSpace());
        Assert.assertEquals(10000, realGameInfo.getPlanetInfo().getMaxMoney());
        Assert.assertEquals(TEST_PLANET_1, realGameInfo.getPlanetInfo().getName());
        Assert.assertEquals(TEST_PLANET_1_ID, realGameInfo.getPlanetInfo().getPlanetId());
        Assert.assertNotNull(realGameInfo.getPlanetInfo().getPlanetLiteInfo());
        Assert.assertEquals(RadarMode.NONE, realGameInfo.getPlanetInfo().getRadarMode());
        Assert.assertEquals(1, realGameInfo.getAllPlanets().size());
        Assert.assertNotNull(realGameInfo.getUnlockContainer().getItemTypes());
        Assert.assertNotNull(realGameInfo.getUnlockContainer().getPlanets());
        Assert.assertNotNull(realGameInfo.getUnlockContainer().getQuests());
        Assert.assertNull(realGameInfo.getStartPointInfo());
        assertWholeItemCount(TEST_PLANET_1_ID, 1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void createBase() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        RealGameInfo realGameInfo = getMovableService().createBase(new Index(1000,1000));
        assertWholeItemCount(TEST_PLANET_1_ID, 1);
        Assert.assertEquals(1, realGameInfo.getBase().getBaseId());
        Assert.assertEquals(TEST_PLANET_1_ID, realGameInfo.getBase().getPlanetId());
        Assert.assertEquals(1000.0, realGameInfo.getAccountBalance(), 0.001);
        Assert.assertEquals(0, realGameInfo.getEnergyConsuming());
        Assert.assertEquals(0, realGameInfo.getEnergyGenerating());
        Assert.assertEquals(0, realGameInfo.getHouseSpace());
        Assert.assertTrue(realGameInfo.isSellAllowed());
        Assert.assertNull(realGameInfo.getLevelScope());
        // TODO Assert.assertNotNull(realGameInfo.getLevelTaskPacket().getQuestInfo());
        Assert.assertEquals(1, realGameInfo.getAllBase().size());
        Assert.assertNull(realGameInfo.getMySimpleGuild());
        Assert.assertNull(realGameInfo.getXpPacket());
        Assert.assertNull(realGameInfo.getPlanetInfo());
        Assert.assertNull(realGameInfo.getAllPlanets());
        Assert.assertNull(realGameInfo.getUnlockContainer());
        Assert.assertNull(realGameInfo.getStartPointInfo());
        assertWholeItemCount(TEST_PLANET_1_ID, 1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void getSimulationGame() throws Exception {
        configureMultiplePlanetsAndLevels();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userGuidanceService.promote(userService.getUserState(), TEST_LEVEL_2_REAL_ID);
        RealGameInfo realGameInfo = getMovableService().getRealGameInfo(START_UID_1);
        Assert.assertNotNull(realGameInfo);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void getRealGameInfoButLevelHasTutorial() throws Exception {
        configureMultiplePlanetsAndLevels();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        try {
            getMovableService().getRealGameInfo(START_UID_1);
            Assert.fail("InvalidLevelStateException expected");
        } catch (InvalidLevelStateException invalidLevelStateException) {
            Assert.assertEquals(TEST_LEVEL_TASK_1_1_SIMULATED_ID, (int) invalidLevelStateException.getLevelTaskId());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void getSimulationGameInfoButRealGame() throws Exception {
        configureMultiplePlanetsAndLevels();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userGuidanceService.promote(userService.getUserState(), TEST_LEVEL_2_REAL_ID);
        try {
            getMovableService().getSimulationGameInfo(TEST_LEVEL_TASK_1_1_SIMULATED_ID);
            Assert.fail("InvalidLevelStateException expected");
        } catch (InvalidLevelStateException invalidLevelStateException) {
            Assert.assertNull(invalidLevelStateException.getLevelTaskId());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void getUnverifiedUser() throws Exception {
        configureMultiplePlanetsAndLevels();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        registerService.register("U1", "xxx", "xxx", "fake");
        userGuidanceService.promote(userService.getUserState(), TEST_LEVEL_2_REAL_ID);
        RealGameInfo realGameInfo = getMovableService().getRealGameInfo(START_UID_1);
        Assert.assertEquals("U1", realGameInfo.getSimpleUser().getName());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }


    @Test
    @DirtiesContext
    public void createAndLoginFacebookUser() throws Exception {
        configureSimplePlanetNoResources();
        // Do not rejoice too quicklyJust... this is just a  test secret.
        setPrivateField(CmsUiServiceImpl.class, cmsUiService, "facebookAppSecret", "029a30fb9677d35c79c44d8a505d8fe1");

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        SimpleUser simpleUser = getMovableService().createAndLoginFacebookUser("v3-O8s1WrS9B2XnYXpRo61n2hKc9wboofRDHOxcF8XI.eyJhbGdvcml0aG0iOiJITUFDLVNIQTI1NiIsImV4cGlyZXMiOjEzNDMxNTI4MDAsImlzc3VlZF9hdCI6MTM0MzE0NjY4Mywib2F1dGhfdG9rZW4iOiJBQUFFa3RlWVZ1WkNNQkFDS29mOGpkWDMxcnVTWkN3RXFuRnFWd3Z2NnBBNldNMTVaQ1V6bzlRNmliUXJiWGtRVkJOeEF0UDJmc2EzVzY3ZXJITW5EWkFvNlZHRzVPajg4U2FJMWZOYkVyYjhCeDBuOURRWkIyIiwidXNlciI6eyJjb3VudHJ5IjoiY2giLCJsb2NhbGUiOiJlbl9VUyIsImFnZSI6eyJtaW4iOjIxfX0sInVzZXJfaWQiOiIxMDAwMDM2MzQwOTQxMzkifQ", "Nick1", "Email");
        Assert.assertEquals((int) userService.getUser("Nick1").getId(), simpleUser.getId());
        Assert.assertEquals("Nick1", simpleUser.getName());
        Assert.assertTrue(simpleUser.isVerified());
        Assert.assertTrue(simpleUser.isFacebook());
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
        List<DbClientDebugEntry> dbEntries = HibernateUtil.loadAll(getSessionFactory(), DbClientDebugEntry.class);
        Assert.assertEquals(1, dbEntries.size());
        DbClientDebugEntry debugEntry1 = dbEntries.get(0);
        Assert.assertTrue(serverBefore.getTime() <= debugEntry1.getTimeStamp().getTime());
        Assert.assertTrue(serverAfter.getTime() >= debugEntry1.getTimeStamp().getTime());
        Assert.assertEquals(clientDate, debugEntry1.getClientTimeStamp());
        Assert.assertEquals(sessionId1, debugEntry1.getSessionId());
        Assert.assertNull(debugEntry1.getUserId());
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
        getOrCreateBase(); // Opens a connection
        int userId = getUserState().getUser();
        getMovableService().sendDebug(clientDate, "CAT1", "Text Text");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        Date serverAfter = new Date();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DbClientDebugEntry> dbEntries = HibernateUtil.loadAll(getSessionFactory(), DbClientDebugEntry.class);
        Assert.assertEquals(1, dbEntries.size());
        DbClientDebugEntry debugEntry1 = dbEntries.get(0);
        Assert.assertTrue(serverBefore.getTime() <= debugEntry1.getTimeStamp().getTime());
        Assert.assertTrue(serverAfter.getTime() >= debugEntry1.getTimeStamp().getTime());
        Assert.assertEquals(clientDate, debugEntry1.getClientTimeStamp());
        Assert.assertEquals(sessionId1, debugEntry1.getSessionId());
        Assert.assertEquals(userId, (int) debugEntry1.getUserId());
        Assert.assertEquals("CAT1", debugEntry1.getCategory());
        Assert.assertEquals("Text Text", debugEntry1.getMessage());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }
}
