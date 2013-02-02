package com.btxtech.game.services.user;

import com.btxtech.game.jsre.client.AdCellProvision;
import com.btxtech.game.jsre.client.InvalidNickName;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.common.NameErrorPair;
import com.btxtech.game.services.socialnet.facebook.FacebookSignedRequest;
import com.btxtech.game.wicket.WebCommon;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.annotation.DirtiesContext;

import javax.servlet.http.Cookie;
import java.util.List;

/**
 * User: beat
 * Date: Jul 11, 2009
 * Time: 12:00:44 PM
 */
public class TestUserService extends AbstractServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private RegisterService registerService;

    @Test
    @DirtiesContext
    public void createLoginLogoutTimeOut() throws Exception {
        configureMultiplePlanetsAndLevels();
        beginHttpSession();
        // Create account
        beginHttpRequestAndOpenSessionInViewFilter();
        assertNotLoggedIn();
        Assert.assertNull(userService.getUser());
        createUser("U1", "test");
        assertNotLoggedIn();
        loginUser("U1", "test");
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
        loginUser("U1", "test");
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
        createUser("U2", "test");
        assertNotLoggedIn();
        loginUser("U2", "test");
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
        createUser("U1", "test");
        assertNotLoggedIn();
        loginUser("U1", "test");
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
        createUser("U1", "test");
        assertNotLoggedIn();
        loginUser("U1", "test");
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
        createUser("U1", "test");
        assertNotLoggedIn();
        loginUser("U1", "test");
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
        loginUser("U1", "test");
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
        createAndLoginUser("U1");
        try {
            loginUser("U1", "test");
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
        createAndLoginUser("U1");
        try {
            createUser("U1", "test");
            Assert.fail("AlreadyLoggedInException expected");
        } catch (RuntimeException e) {
            if (!(e.getCause() instanceof AlreadyLoggedInException)) {
                Assert.fail("AlreadyLoggedInException expected");
            }
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
        createAndLoginUser("test");
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

    @Test
    @DirtiesContext
    public void isRegistered() throws Exception {
        configureMultiplePlanetsAndLevels();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertFalse(userService.isRegistered());
        registerService.register("test", "xxx", "xxx", "fake");
        Assert.assertFalse(userService.isRegistered());
        registerService.onVerificationPageCalled(userService.getUser().getVerificationId());
        Assert.assertTrue(userService.isRegistered());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void getSimpleUser() throws Exception {
        configureMultiplePlanetsAndLevels();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertNull(userService.getSimpleUser());
        createAndLoginUser("U1");
        Assert.assertNotNull(userService.getSimpleUser());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void handleAdCellProvisionNoPromotion() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        try {
            Assert.assertNull(userService.handleAdCellProvision().isProvisionExpected());
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            Assert.assertEquals("No user", e.getMessage());
        }
        createAndLoginUser("U1");
        AdCellProvision adCellProvision = userService.handleAdCellProvision();
        Assert.assertNotNull(adCellProvision.getSimpleUser());
        Assert.assertFalse(adCellProvision.isProvisionExpected());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void handleAdCellProvisionPromotionNormal() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        setWicketParameterAdCellBid("898uji898");
        userService.createUnverifiedUser("U1", "xxx", "xxx", null);
        registerService.onVerificationPageCalled(userService.getUser().getVerificationId());
        int userId = userService.getUser("U1").getId();
        long timeBefore = System.currentTimeMillis();
        AdCellProvision adCellProvision = userService.handleAdCellProvision();
        long timeAfter = System.currentTimeMillis();
        Assert.assertNotNull(adCellProvision.getSimpleUser());
        Assert.assertTrue(adCellProvision.isProvisionExpected());
        Assert.assertEquals("898uji898", adCellProvision.getBid());
        // History
        List<DbAdCellProvision> dbAdCellProvisions = HibernateUtil.loadAll(getSessionFactory(), DbAdCellProvision.class);
        Assert.assertEquals(1, dbAdCellProvisions.size());
        Assert.assertEquals(userId, dbAdCellProvisions.get(0).getUserId());
        Assert.assertEquals("898uji898", dbAdCellProvisions.get(0).getAdCellPid());
        Assert.assertTrue(dbAdCellProvisions.get(0).getDate().getTime() >= timeBefore);
        Assert.assertTrue(dbAdCellProvisions.get(0).getDate().getTime() <= timeAfter);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Same bid check
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMockHttpServletRequest().setParameter("bid", "898uji898");
        userService.createUnverifiedUser("U2", "xxx", "xxx", null);
        registerService.onVerificationPageCalled(userService.getUser().getVerificationId());
        adCellProvision = userService.handleAdCellProvision();
        Assert.assertNotNull(adCellProvision.getSimpleUser());
        Assert.assertFalse(adCellProvision.isProvisionExpected());
        // History
        dbAdCellProvisions = HibernateUtil.loadAll(getSessionFactory(), DbAdCellProvision.class);
        Assert.assertEquals(1, dbAdCellProvisions.size());
        Assert.assertEquals(userId, dbAdCellProvisions.get(0).getUserId());
        Assert.assertEquals("898uji898", dbAdCellProvisions.get(0).getAdCellPid());
        Assert.assertTrue(dbAdCellProvisions.get(0).getDate().getTime() >= timeBefore);
        Assert.assertTrue(dbAdCellProvisions.get(0).getDate().getTime() <= timeAfter);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void handleAdCellProvisionPromotionFacebook() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        setWicketParameterAdCellBid("898uji898xxx");
        userService.createAndLoginFacebookUser(new FacebookSignedRequest(null, 0, null, null, "12345"), "N1");
        int userId = userService.getUser("N1").getId();
        long timeBefore = System.currentTimeMillis();
        AdCellProvision adCellProvision = userService.handleAdCellProvision();
        long timeAfter = System.currentTimeMillis();
        Assert.assertNotNull(adCellProvision.getSimpleUser());
        Assert.assertTrue(adCellProvision.isProvisionExpected());
        Assert.assertEquals("898uji898xxx", adCellProvision.getBid());
        // History
        List<DbAdCellProvision> dbAdCellProvisions = HibernateUtil.loadAll(getSessionFactory(), DbAdCellProvision.class);
        Assert.assertEquals(1, dbAdCellProvisions.size());
        Assert.assertEquals(userId, dbAdCellProvisions.get(0).getUserId());
        Assert.assertEquals("898uji898xxx", dbAdCellProvisions.get(0).getAdCellPid());
        Assert.assertTrue(dbAdCellProvisions.get(0).getDate().getTime() >= timeBefore);
        Assert.assertTrue(dbAdCellProvisions.get(0).getDate().getTime() <= timeAfter);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Same bid check
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMockHttpServletRequest().setCookies(new Cookie(WebCommon.AD_CELL_COOKIE_ID, "898uji898xxx"));
        userService.createAndLoginFacebookUser(new FacebookSignedRequest(null, 0, null, null, "12345"), "N2");
        adCellProvision = userService.handleAdCellProvision();
        Assert.assertNotNull(adCellProvision.getSimpleUser());
        Assert.assertFalse(adCellProvision.isProvisionExpected());
        // History
        dbAdCellProvisions = HibernateUtil.loadAll(getSessionFactory(), DbAdCellProvision.class);
        Assert.assertEquals(1, dbAdCellProvisions.size());
        Assert.assertEquals(userId, dbAdCellProvisions.get(0).getUserId());
        Assert.assertEquals("898uji898xxx", dbAdCellProvisions.get(0).getAdCellPid());
        Assert.assertTrue(dbAdCellProvisions.get(0).getDate().getTime() >= timeBefore);
        Assert.assertTrue(dbAdCellProvisions.get(0).getDate().getTime() <= timeAfter);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void checkUserEmails() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createUser("U1", "xxx", "test1.yyy@testXXX.com");
        createUser("U2", "xxx", "test2.yyy@testXXX.com");
        createUser("U3", "xxx", null);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        List<NameErrorPair> nameErrorPairs = userService.checkUserEmails("U1\n\rU2\nU3 U4");
        Assert.assertEquals(2, nameErrorPairs.size());
        Assert.assertEquals("U3", nameErrorPairs.get(0).getName());
        Assert.assertEquals("No email address", nameErrorPairs.get(0).getError());
        Assert.assertEquals("U4", nameErrorPairs.get(1).getName());
        Assert.assertEquals("Nu such user", nameErrorPairs.get(1).getError());
    }

    @Test
    @DirtiesContext
    public void checkUserEmailsInSession() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createUser("U1", "xxx", "test1.yyy@testXXX.com");
        createUser("U2", "xxx", "test2.yyy@testXXX.com");
        createUser("U3", "xxx", null);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<NameErrorPair> nameErrorPairs = userService.checkUserEmails("U1\n\rU2\nU3 U4");
        Assert.assertEquals(2, nameErrorPairs.size());
        Assert.assertEquals("U3", nameErrorPairs.get(0).getName());
        Assert.assertEquals("No email address", nameErrorPairs.get(0).getError());
        Assert.assertEquals("U4", nameErrorPairs.get(1).getName());
        Assert.assertEquals("Nu such user", nameErrorPairs.get(1).getError());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void getUsersWithEmail() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createUser("U1", "xxx", "test1.yyy@testXXX.com");
        createUser("U2", "xxx", "test2.yyy@testXXX.com");
        createUser("U3", "xxx", null);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        List<User> users = userService.getUsersWithEmail("U1\n\rU2\nU3 U4");
        Assert.assertEquals(2, users.size());
        Assert.assertEquals("U1", users.get(0).getUsername());
        Assert.assertEquals("U2", users.get(1).getUsername());
    }

    @Test
    @DirtiesContext
    public void getUsersWithEmailInSession() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createUser("U1", "xxx", "test1.yyy@testXXX.com");
        createUser("U2", "xxx", "test2.yyy@testXXX.com");
        createUser("U3", "xxx", null);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<User> users = userService.getUsersWithEmail("U1\n\rU2\nU3 U4");
        Assert.assertEquals(2, users.size());
        Assert.assertEquals("U1", users.get(0).getUsername());
        Assert.assertEquals("U2", users.get(1).getUsername());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }
}