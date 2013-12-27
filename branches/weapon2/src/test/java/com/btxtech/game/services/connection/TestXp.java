package com.btxtech.game.services.connection;

import com.btxtech.game.jsre.client.common.info.RealGameInfo;
import com.btxtech.game.jsre.common.packets.XpPacket;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.XpService;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

/**
 * User: beat
 * Date: 29.06.12
 * Time: 13:19
 */
public class TestXp extends AbstractServiceTest {
    @Autowired
    private UserGuidanceService userGuidanceService;
    @Autowired
    private XpService xpService;
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
        Assert.assertEquals(0, realGameInfo.getXpPacket().getXp());
        Assert.assertEquals(220, realGameInfo.getXpPacket().getXp2LevelUp());
        xpService.onReward(userService.getUserState(), 10);
        realGameInfo = getMovableService().getRealGameInfo(START_UID_1, null);
        Assert.assertEquals(10, realGameInfo.getXpPacket().getXp());
        Assert.assertEquals(220, realGameInfo.getXpPacket().getXp2LevelUp());
        userGuidanceService.promote(userService.getUserState(), TEST_LEVEL_3_REAL_ID);
        realGameInfo = getMovableService().getRealGameInfo(START_UID_1, null);
        Assert.assertEquals(0, realGameInfo.getXpPacket().getXp());
        Assert.assertEquals(400, realGameInfo.getXpPacket().getXp2LevelUp());
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
        Assert.assertTrue(getPackages(XpPacket.class).isEmpty());
        xpService.onReward(userService.getUserState(), 10);
        List<XpPacket> xpPackets = getPackages(XpPacket.class);
        Assert.assertEquals(1, xpPackets.size());
        Assert.assertEquals(10, xpPackets.get(0).getXp());
        Assert.assertEquals(220, xpPackets.get(0).getXp2LevelUp());
        userGuidanceService.promote(userService.getUserState(), TEST_LEVEL_3_REAL_ID);
        xpPackets = getPackages(XpPacket.class);
        Assert.assertEquals(1, xpPackets.size());
        Assert.assertEquals(0, xpPackets.get(0).getXp());
        Assert.assertEquals(400, xpPackets.get(0).getXp2LevelUp());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }
}
