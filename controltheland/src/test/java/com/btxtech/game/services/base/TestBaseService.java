package com.btxtech.game.services.base;

import com.btxtech.game.jsre.client.MovableService;
import com.btxtech.game.jsre.common.BaseChangedPacket;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.services.base.BaseAttributes;
import com.btxtech.game.jsre.common.tutorial.TutorialConfig;
import com.btxtech.game.services.BaseTestService;
import com.btxtech.game.services.user.UserService;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

/**
 * User: beat
 * Date: 07.04.2011
 * Time: 13:28:45
 */
public class TestBaseService extends BaseTestService {
    @Autowired
    private UserService userService;
    @Autowired
    private MovableService movableService;

    @Test
    @DirtiesContext
    public void testBaseColor() throws Exception {
        configureMinimalGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertNull(userService.getUser());
        userService.createUser("U1", "test", "test", "test");
        userService.login("U1", "test");
        movableService.sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "", "", 0, 0);
        SimpleBase simpleBase = getMyBase(); // Setup connection
        String color = movableService.getFreeColors(0, 1).get(0);
        clearPackets(simpleBase);
        movableService.setBaseColor(color);

        BaseChangedPacket baseChangedPacket = new BaseChangedPacket();
        baseChangedPacket.setType(BaseChangedPacket.Type.CHANGED);
        baseChangedPacket.setBaseAttributes(new BaseAttributes(simpleBase, "U1", color, false));
        assertPackagesIgnoreSyncItemInfoAndClear(simpleBase, baseChangedPacket);

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }
}
