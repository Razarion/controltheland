package com.btxtech.game.services.user;

import com.btxtech.game.services.BaseTestService;
import com.btxtech.game.services.utg.UserGuidanceService;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
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
        assertNotLoggedIn();
        Assert.assertNull(userService.getUser());
        userService.createUser("U1", "test", "test", "test");
        assertNotLoggedIn();
        userService.login("U1", "test");
        Assert.assertEquals("U1", userService.getUser().getUsername());
        assertLoggedIn();
        endHttpRequestAndOpenSessionInViewFilter();
        // New request same user
        beginHttpRequestAndOpenSessionInViewFilter();
        assertLoggedIn();
        Assert.assertEquals("U1", userService.getUser().getUsername());
        endHttpRequestAndOpenSessionInViewFilter();

        endHttpSession();

        beginHttpSession();
        // New session not logged in
        beginHttpRequestAndOpenSessionInViewFilter();
        assertNotLoggedIn();
        Assert.assertNull(userService.getUser());
        endHttpRequestAndOpenSessionInViewFilter();

        endHttpSession();

        beginHttpSession();
        // Log in
        beginHttpRequestAndOpenSessionInViewFilter();
        assertNotLoggedIn();
        userService.login("U1", "test");
        assertLoggedIn();
        Assert.assertEquals("U1", userService.getUser().getUsername());
        endHttpRequestAndOpenSessionInViewFilter();

        // Logout
        beginHttpRequestAndOpenSessionInViewFilter();
        assertLoggedIn();
        Assert.assertEquals("U1", userService.getUser().getUsername());
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        assertLoggedIn();
        userService.logout();
        assertNotLoggedIn();
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        assertNotLoggedIn();
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
        userService.createUser("U2", "test", "test", "test");
        assertNotLoggedIn();
        userService.login("U2", "test");
        assertLoggedIn();
        Assert.assertEquals("U2", userService.getUser().getUsername());
        endHttpRequestAndOpenSessionInViewFilter();

        // Enter
        beginHttpRequestAndOpenSessionInViewFilter();
        UserState userState1 = userService.getUserState();
        assertLoggedIn();
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertSame(userState1, userService.getUserState());
        assertLoggedIn();
        endHttpRequestAndOpenSessionInViewFilter();

        // Leave
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertSame(userState1, userService.getUserState());
        assertLoggedIn();
        userService.logout();
        assertNotLoggedIn();
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
        assertNotLoggedIn();
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertSame(userState1, userService.getUserState());
        assertNotLoggedIn();
        endHttpRequestAndOpenSessionInViewFilter();

        // Create account
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertNull(userService.getUser());
        assertNotLoggedIn();
        userService.createUser("U1", "test", "test", "test");
        assertNotLoggedIn();
        userService.login("U1", "test");
        assertLoggedIn();
        Assert.assertEquals("U1", userService.getUser().getUsername());
        endHttpRequestAndOpenSessionInViewFilter();

        // Enter
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertSame(userState1, userService.getUserState());
        assertLoggedIn();
        endHttpRequestAndOpenSessionInViewFilter();

        // Leave
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertSame(userState1, userService.getUserState());
        assertLoggedIn();
        userService.logout();
        assertNotLoggedIn();
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
        assertNotLoggedIn();
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertSame(userState1, userService.getUserState());
        assertNotLoggedIn();
        endHttpRequestAndOpenSessionInViewFilter();

        // Timeout
        endHttpSession();

        beginHttpSession();

        // Leave
        beginHttpRequestAndOpenSessionInViewFilter();
        assertNotLoggedIn();
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
        assertNotLoggedIn();
        userService.createUser("U1", "test", "test", "test");
        assertNotLoggedIn();
        userService.login("U1", "test");
        assertLoggedIn();
        endHttpRequestAndOpenSessionInViewFilter();
        // Enter Game
        beginHttpRequestAndOpenSessionInViewFilter();
        UserState userState1 = userService.getUserState();
        assertLoggedIn();
        endHttpRequestAndOpenSessionInViewFilter();
        // Re-Enter
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertSame(userState1, userService.getUserState());
        assertLoggedIn();
        endHttpRequestAndOpenSessionInViewFilter();

        // Timeout
        endHttpSession();

        beginHttpSession();

        // Re-Enter
        beginHttpRequestAndOpenSessionInViewFilter();
        assertNotLoggedIn();
        UserState userState2 = userService.getUserState();
        Assert.assertNotSame(userState1, userState2);
        endHttpRequestAndOpenSessionInViewFilter();
        // Re-Enter
        beginHttpRequestAndOpenSessionInViewFilter();
        assertNotLoggedIn();
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
        assertNotLoggedIn();
        userService.createUser("U1", "test", "test", "test");
        assertNotLoggedIn();
        userService.login("U1", "test");
        assertLoggedIn();
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        assertLoggedIn();
        UserState userState1 = userService.getUserState();
        endHttpRequestAndOpenSessionInViewFilter();

        // Log out
        beginHttpRequestAndOpenSessionInViewFilter();
        assertLoggedIn();
        userService.logout();
        assertNotLoggedIn();
        endHttpRequestAndOpenSessionInViewFilter();

        // Log in
        beginHttpRequestAndOpenSessionInViewFilter();
        assertNotLoggedIn();
        userService.login("U1", "test");
        assertLoggedIn();
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        assertLoggedIn();
        Assert.assertSame(userState1, userService.getUserState());
        endHttpRequestAndOpenSessionInViewFilter();

        endHttpSession();
    }

    public static UserGuidanceService createUserGuidanceServiceMock() {
        return createNiceMock(UserGuidanceService.class);
    }

    private void assertLoggedIn() {
        for (GrantedAuthority grantedAuthority : userService.getAuthorities()) {
            if (grantedAuthority.getAuthority().equals(SecurityRoles.ROLE_USER)) {
                return;
            }
        }
        Assert.fail("User is not logged in");
    }

    private void assertNotLoggedIn() {
        if (!userService.getAuthorities().isEmpty()) {
            Assert.fail("User is logged in");
        }
    }
}