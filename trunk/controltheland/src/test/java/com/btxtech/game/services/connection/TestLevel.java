package com.btxtech.game.services.connection;

import com.btxtech.game.jsre.client.common.info.RealGameInfo;
import com.btxtech.game.jsre.common.packets.LevelPacket;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.utg.UserGuidanceService;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

/**
 * User: beat
 * Date: 29.06.12
 * Time: 15:42
 */
public class TestLevel extends AbstractServiceTest {
    @Autowired
    private UserGuidanceService userGuidanceService;
    @Autowired
    private UserService userService;

    @Test
    @DirtiesContext
    public void realGameInfo() throws Exception {
        configureMultiplePlanetsAndLevels();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userGuidanceService.onTutorialFinished(TEST_LEVEL_TASK_1_1_SIMULATED_ID);
        RealGameInfo realGameInfo = getMovableService().getRealGameInfo(START_UID_1, null);
        Assert.assertEquals(userGuidanceService.getDbLevel(TEST_LEVEL_2_REAL_ID).createLevelScope(), realGameInfo.getLevelScope());
        userGuidanceService.promote(userService.getUserState(), TEST_LEVEL_3_REAL_ID);
        realGameInfo = getMovableService().getRealGameInfo(START_UID_1, null);
        Assert.assertEquals(userGuidanceService.getDbLevel(TEST_LEVEL_3_REAL_ID).createLevelScope(), realGameInfo.getLevelScope());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void packetSending() throws Exception {
        configureMultiplePlanetsAndLevels();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userGuidanceService.onTutorialFinished(TEST_LEVEL_TASK_1_1_SIMULATED_ID);
        getOrCreateBase(); // Generate Base
        Assert.assertTrue(getPackages(LevelPacket.class).isEmpty());
        userGuidanceService.promote(userService.getUserState(), TEST_LEVEL_3_REAL_ID);
        List<LevelPacket> levelPackets = getPackages(LevelPacket.class);
        Assert.assertEquals(1, levelPackets.size());
        Assert.assertEquals(userGuidanceService.getDbLevel(TEST_LEVEL_3_REAL_ID).createLevelScope(), levelPackets.get(0).getLevel());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }
}
