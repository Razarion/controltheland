package com.btxtech.game.services.market;

import com.btxtech.game.jsre.client.MovableService;
import com.btxtech.game.jsre.common.XpBalancePacket;
import com.btxtech.game.jsre.common.tutorial.TutorialConfig;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.user.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

/**
 * User: beat
 * Date: 07.04.2011
 * Time: 14:32:44
 */
public class TestServerMarketService extends AbstractServiceTest {
    @Autowired
    private ServerMarketService serverMarketService;
    @Autowired
    private MovableService movableService;
    @Autowired
    private UserService userService;

    @Test
    @DirtiesContext
    public void testXpPeriod() throws Exception {
        configureMinimalGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        movableService.sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "", "", 0, 0);
        getMyBase(); // Get connection
        Thread.sleep(100); // Wait for packet to be sent
        clearPackets();
        Assert.assertEquals(0, userService.getUserState().getUserItemTypeAccess().getXp());
        setupXpSettings(100, 1.0);
        Thread.sleep(120);
        Assert.assertEquals(1, userService.getUserState().getUserItemTypeAccess().getXp());

        setupXpSettings();

        XpBalancePacket xpBalancePacket = new XpBalancePacket();
        xpBalancePacket.setXp(1);
        assertPackagesIgnoreSyncItemInfoAndClear(xpBalancePacket);

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testAvailable() throws Exception {
        configureMinimalGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(0, serverMarketService.getAvailableCrud().readDbChildren().size());
        movableService.sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "", "", 0, 0);
        Assert.assertEquals(1, serverMarketService.getAvailableCrud().readDbChildren().size());
        AvailableMarketEntry availableMarketEntry = serverMarketService.getAvailableCrud().readDbChildren().iterator().next();
        Assert.assertEquals(TEST_SIMPLE_BUILDING_ID, (int) availableMarketEntry.getDbMarketEntry().getItemType().getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

}
