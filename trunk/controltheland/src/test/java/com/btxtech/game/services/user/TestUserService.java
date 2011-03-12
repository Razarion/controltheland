package com.btxtech.game.services.user;

import com.btxtech.game.services.BaseTestService;
import com.btxtech.game.services.utg.UserGuidanceService;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import static org.easymock.EasyMock.createNiceMock;

/**
 * User: beat
 * Date: Jul 11, 2009
 * Time: 12:00:44 PM
 */
@ContextConfiguration(locations = {"classpath:UtgMockTestService-context.xml"})
public class TestUserService extends BaseTestService {
    @Autowired
    private UserService userService;

    @Test
    @DirtiesContext
    public void createLoginLogoutTimeOut() throws Exception {
        beginHttpSession();
        // Create account
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertNull(userService.getUser());
        userService.createUserAndLoggin("U1", "test", "test", "test");
        Assert.assertEquals("U1", userService.getUser().getUsername());
        endHttpRequestAndOpenSessionInViewFilter();
        // New request same user
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals("U1", userService.getUser().getUsername());
        endHttpRequestAndOpenSessionInViewFilter();

        endHttpSession();

        beginHttpSession();
        // New session not logged in
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertNull(userService.getUser());
        endHttpRequestAndOpenSessionInViewFilter();

        endHttpSession();

        beginHttpSession();
        // Log in
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("U1", "test");
        Assert.assertEquals("U1", userService.getUser().getUsername());
        endHttpRequestAndOpenSessionInViewFilter();

        // Logout
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals("U1", userService.getUser().getUsername());
        endHttpRequestAndOpenSessionInViewFilter();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.logout();
        endHttpRequestAndOpenSessionInViewFilter();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertNull(userService.getUser());
        endHttpRequestAndOpenSessionInViewFilter();

        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void createCreateEnterLeaveGame() throws Exception {
        beginHttpSession();
        // Create account
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertNull(userService.getUser());
        userService.createUserAndLoggin("U2", "test", "test", "test");
        Assert.assertEquals("U2", userService.getUser().getUsername());
        endHttpRequestAndOpenSessionInViewFilter();

        // Enter
        beginHttpRequestAndOpenSessionInViewFilter();
        UserState userState1 = userService.getUserState();
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertSame(userState1, userService.getUserState());
        endHttpRequestAndOpenSessionInViewFilter();

        // Leave
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertSame(userState1, userService.getUserState());
        userService.logout();
        Assert.assertNotSame(userState1, userService.getUserState());                
        endHttpRequestAndOpenSessionInViewFilter();

        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void createEnterGameRegister() throws Exception {
        beginHttpSession();
        // Enter Game
        beginHttpRequestAndOpenSessionInViewFilter();
        UserState userState1 = userService.getUserState();
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertSame(userState1, userService.getUserState());
        endHttpRequestAndOpenSessionInViewFilter();

        // Create account
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertNull(userService.getUser());
        userService.createUserAndLoggin("U1", "test", "test", "test");
        Assert.assertEquals("U1", userService.getUser().getUsername());
        endHttpRequestAndOpenSessionInViewFilter();

        // Enter
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertSame(userState1, userService.getUserState());
        endHttpRequestAndOpenSessionInViewFilter();

        // Leave
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertSame(userState1, userService.getUserState());
        userService.logout();
        Assert.assertNotSame(userState1, userService.getUserState());
        endHttpRequestAndOpenSessionInViewFilter();

        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void createEnterGameUnRegTimeout() throws Exception {
        beginHttpSession();
        // Enter Game
        beginHttpRequestAndOpenSessionInViewFilter();
        UserState userState1 = userService.getUserState();
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertSame(userState1, userService.getUserState());
        endHttpRequestAndOpenSessionInViewFilter();

        // Timeout
        endHttpSession();

        beginHttpSession();

        // Leave
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertNotSame(userState1, userService.getUserState());
        endHttpRequestAndOpenSessionInViewFilter();

        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void createEnterGameRegTimeout() throws Exception {
        beginHttpSession();
        // Create account
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUserAndLoggin("U1", "test", "test", "test");
        endHttpRequestAndOpenSessionInViewFilter();
        // Enter Game
        beginHttpRequestAndOpenSessionInViewFilter();
        UserState userState1 = userService.getUserState();
        endHttpRequestAndOpenSessionInViewFilter();
        // Re-Enter
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertSame(userState1, userService.getUserState());
        endHttpRequestAndOpenSessionInViewFilter();

        // Timeout
        endHttpSession();

        beginHttpSession();

        // Re-Enter
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertNotSame(userState1, userService.getUserState());
        UserState userState2 = userService.getUserState();
        endHttpRequestAndOpenSessionInViewFilter();
        // Re-Enter
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertSame(userState2, userService.getUserState());
        endHttpRequestAndOpenSessionInViewFilter();

        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void createLoginInGame() throws Exception {
        beginHttpSession();
        // Create account
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUserAndLoggin("U1", "test", "test", "test");
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        UserState userState1 = userService.getUserState();
        endHttpRequestAndOpenSessionInViewFilter();

        // Log out
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.logout();
        endHttpRequestAndOpenSessionInViewFilter();

        // Log in
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("U1", "test");
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertSame(userState1, userService.getUserState());
        endHttpRequestAndOpenSessionInViewFilter();

        endHttpSession();
    }

    public static UserGuidanceService createUserGuidanceServiceMock() {
        return createNiceMock(UserGuidanceService.class);
    }
}