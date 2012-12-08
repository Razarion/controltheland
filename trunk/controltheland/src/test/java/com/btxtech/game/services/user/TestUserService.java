package com.btxtech.game.services.user;

import com.btxtech.game.jsre.client.InvalidNickName;
import com.btxtech.game.services.AbstractServiceTest;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.annotation.DirtiesContext;

/**
 * User: beat
 * Date: Jul 11, 2009
 * Time: 12:00:44 PM
 */
public class TestUserService extends AbstractServiceTest {
    @Autowired
    private UserService userService;

    @Test
    @DirtiesContext
    public void createLoginLogoutTimeOut() throws Exception {
        configureMultiplePlanetsAndLevels();
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
        configureMultiplePlanetsAndLevels();
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
        configureMultiplePlanetsAndLevels();
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
        configureMultiplePlanetsAndLevels();
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
        configureMultiplePlanetsAndLevels();
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
        configureMultiplePlanetsAndLevels();
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

    @Test
    @DirtiesContext
    public void testLoginTwice() throws Exception {
        configureMultiplePlanetsAndLevels();
        // U1 no real base, first level
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("U1", "test", "test", "test");
        userService.login("U1", "test");
        try {
            userService.login("U1", "test");
            Assert.fail("AlreadyLoggedInException expected");
        } catch (AlreadyLoggedInException e) {
            // Expected
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testCreateUserTwice() throws Exception {
        configureMultiplePlanetsAndLevels();
        // U1 no real base, first level
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("U1", "test", "test", "test");
        userService.login("U1", "test");
        try {
            userService.createUser("U1", "test", "test", "test");
            Assert.fail("AlreadyLoggedInException expected");
        } catch (AlreadyLoggedInException e) {
            // Expected
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
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

    @Test
    @DirtiesContext
    public void isNickNameValid() throws Exception {
        configureMultiplePlanetsAndLevels();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("test", "xxx","xxx", null);
        userService.login("test", "xxx");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(InvalidNickName.TO_SHORT, userService.isNickNameValid(""));
        Assert.assertEquals(InvalidNickName.TO_SHORT, userService.isNickNameValid("a"));
        Assert.assertEquals(InvalidNickName.TO_SHORT, userService.isNickNameValid("aa"));
        Assert.assertEquals(InvalidNickName.ALREADY_USED, userService.isNickNameValid("test"));
        Assert.assertNull(userService.isNickNameValid("test33"));

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

}