package com.btxtech.game.services.user;

import com.btxtech.game.jsre.client.MovableService;
import com.btxtech.game.jsre.common.tutorial.TutorialConfig;
import com.btxtech.game.services.BaseTestService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

/**
 * User: beat
 * Date: 28.03.2011
 * Time: 00:31:20
 */
public class TestUserState extends BaseTestService {
    @Autowired
    private UserService userService;
    @Autowired
    private MovableService movableService;

    @Test
    @DirtiesContext
    public void testTwoRegUsers() throws Exception {
        configureMinimalGame();
        // U1 no real base, first level
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("U1", "test", "test", "test");
        userService.login("U1", "test");
        movableService.getGameInfo();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // U2 real base, second level
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("U2", "test", "test", "test");
        userService.login("U2", "test");
        movableService.getGameInfo();
        movableService.sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "", "", 0, 0);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Unregistered base, fist level
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        movableService.getGameInfo();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        Assert.assertEquals(2, userService.getAllUserStates().size());
    }

    @Test
    @DirtiesContext
    public void testOneRegUsersOneUnregOnlineUser() throws Exception {
        configureMinimalGame();
        // U1 no real base, first level
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("U1", "test", "test", "test");
        userService.login("U1", "test");
        movableService.getGameInfo();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Unregistered base, fist level
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        movableService.getGameInfo();
        Assert.assertEquals(2, userService.getAllUserStates().size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

}
