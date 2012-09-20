package com.btxtech.game.services.gwt;

import com.btxtech.game.jsre.client.common.info.InvalidLevelStateException;
import com.btxtech.game.jsre.client.common.info.RealGameInfo;
import com.btxtech.game.jsre.client.common.info.SimulationInfo;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.utg.UserGuidanceService;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

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
}
