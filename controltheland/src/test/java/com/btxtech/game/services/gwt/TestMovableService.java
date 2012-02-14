package com.btxtech.game.services.gwt;

import com.btxtech.game.jsre.client.MovableService;
import com.btxtech.game.jsre.client.common.info.InvalidLevelState;
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
    private MovableService movableService;
    @Autowired
    private UserGuidanceService userGuidanceService;
    @Autowired
    private UserService userService;

    @Test
    @DirtiesContext
    public void getRealGameInfo() throws Exception {
        configureGameMultipleLevel();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        SimulationInfo simulationInfo = movableService.getSimulationGameInfo(TEST_LEVEL_TASK_1_SIMULATED_ID);
        Assert.assertNotNull(simulationInfo);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void getSimulationGame() throws Exception {
        configureGameMultipleLevel();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userGuidanceService.promote(userService.getUserState(), TEST_LEVEL_2_REAL_ID);
        RealGameInfo realGameInfo = movableService.getRealGameInfo();
        Assert.assertNotNull(realGameInfo);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void getRealGameInfoButLevelHasTutorial() throws Exception {
        configureGameMultipleLevel();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        try {
            movableService.getRealGameInfo();
            Assert.fail("InvalidLevelState expected");
        } catch (InvalidLevelState invalidLevelState) {
            Assert.assertEquals(TEST_LEVEL_TASK_1_SIMULATED_ID, (int) invalidLevelState.getLevelTaskId());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void getSimulationGameInfoButRealGame() throws Exception {
        configureGameMultipleLevel();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userGuidanceService.promote(userService.getUserState(), TEST_LEVEL_2_REAL_ID);
        try {
            movableService.getSimulationGameInfo(TEST_LEVEL_TASK_1_SIMULATED_ID);
            Assert.fail("InvalidLevelState expected");
        } catch (InvalidLevelState invalidLevelState) {
            Assert.assertNull(invalidLevelState.getLevelTaskId());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }
}
