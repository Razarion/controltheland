package com.btxtech.game.services.user;

import com.btxtech.game.jsre.client.VerificationRequestCallback;
import com.btxtech.game.jsre.client.common.info.Suggestion;
import com.btxtech.game.jsre.common.gameengine.services.user.LoginFailedException;
import com.btxtech.game.jsre.common.gameengine.services.user.LoginFailedNotVerifiedException;
import com.btxtech.game.jsre.common.packets.UserAttentionPacket;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.cms.ContentService;
import com.btxtech.game.services.common.NameErrorPair;
import com.btxtech.game.services.common.PropertyService;
import com.btxtech.game.services.common.PropertyServiceEnum;
import com.btxtech.game.services.user.impl.UserServiceImpl;
import com.google.gwt.user.client.ui.SuggestOracle;
import junit.framework.Assert;
import org.easymock.EasyMock;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.annotation.DirtiesContext;

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
    @Autowired
    private GuildService guildService;
    @Autowired
    private PropertyService propertyService;

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
        Assert.assertEquals(VerificationRequestCallback.ErrorResult.TO_SHORT, userService.isNickNameValid(""));
        Assert.assertEquals(VerificationRequestCallback.ErrorResult.TO_SHORT, userService.isNickNameValid("a"));
        Assert.assertEquals(VerificationRequestCallback.ErrorResult.TO_SHORT, userService.isNickNameValid("aa"));
        Assert.assertEquals(VerificationRequestCallback.ErrorResult.ALREADY_USED, userService.isNickNameValid("test"));
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

    @Test
    @DirtiesContext
    public void getSuggestedUserNameTracking() throws Exception {
        // HSQLDB does not support ilike
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createUser("ABCED", "xxx");
        createUser("ABQU", "xxx");
        createUser("FBRD", "xxx");
        createUser("FBLXY", "xxx");
        createUser("BZ51", "xxx");
        createUser("BZ52", "xxx");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // createGuilds
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        loginUser("ABCED", "xxx");
        guildService.createGuild("xxxx1");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("FBLXY", "xxx");
        guildService.createGuild("xxxx2");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<String> names = Suggestion.createStringList(userService.getSuggestedUserName("", UserNameSuggestionFilter.USER_TRACKING_SEARCH, 10));
        Assert.assertTrue(names.isEmpty());
        names = Suggestion.createStringList(userService.getSuggestedUserName("a", UserNameSuggestionFilter.USER_TRACKING_SEARCH, 10));
        Assert.assertEquals(2, names.size());
        Assert.assertEquals("ABCED", names.get(0));
        Assert.assertEquals("ABQU", names.get(1));
        names = Suggestion.createStringList(userService.getSuggestedUserName("ab", UserNameSuggestionFilter.USER_TRACKING_SEARCH, 10));
        Assert.assertEquals(2, names.size());
        Assert.assertEquals("ABCED", names.get(0));
        Assert.assertEquals("ABQU", names.get(1));
        names = Suggestion.createStringList(userService.getSuggestedUserName("abc", UserNameSuggestionFilter.USER_TRACKING_SEARCH, 10));
        Assert.assertEquals(1, names.size());
        Assert.assertEquals("ABCED", names.get(0));
        names = Suggestion.createStringList(userService.getSuggestedUserName("abqu", UserNameSuggestionFilter.USER_TRACKING_SEARCH, 10));
        Assert.assertEquals(1, names.size());
        Assert.assertEquals("ABQU", names.get(0));
        names = Suggestion.createStringList(userService.getSuggestedUserName("%z", UserNameSuggestionFilter.USER_TRACKING_SEARCH, 10));
        Assert.assertEquals(2, names.size());
        Assert.assertEquals("BZ51", names.get(0));
        Assert.assertEquals("BZ52", names.get(1));
        names = Suggestion.createStringList(userService.getSuggestedUserName("%z5", UserNameSuggestionFilter.USER_TRACKING_SEARCH, 10));
        Assert.assertEquals(2, names.size());
        Assert.assertEquals("BZ51", names.get(0));
        Assert.assertEquals("BZ52", names.get(1));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void getSuggestedUserNameSize() throws Exception {
        // HSQLDB does not support ilike
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        for (int i = 0; i < 50; i++) {
            createUser("ABCD" + i, "xxx");
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<String> names = Suggestion.createStringList(userService.getSuggestedUserName("abcd", UserNameSuggestionFilter.USER_TRACKING_SEARCH, 20));
        Assert.assertEquals(20, names.size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void getSuggestedUserNameGuild() throws Exception {
        // HSQLDB does not support ilike
        configureSimplePlanetNoResources();
        // Create users
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createUser("ABCED", "xxx");
        createUser("ABQU", "xxx");
        createUser("FBRD", "xxx");
        createUser("FBLXY", "xxx");
        createUser("BZ51", "xxx");
        createUser("BZ52", "xxx");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // createGuilds
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        loginUser("ABCED", "xxx");
        guildService.createGuild("xxxx1");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("FBLXY", "xxx");
        guildService.createGuild("xxxx2");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<String> names = Suggestion.createStringList(userService.getSuggestedUserName("", UserNameSuggestionFilter.USER_GILD_SEARCH, 10));
        Assert.assertTrue(names.isEmpty());
        names = Suggestion.createStringList(userService.getSuggestedUserName("a", UserNameSuggestionFilter.USER_GILD_SEARCH, 10));
        Assert.assertEquals(1, names.size());
        Assert.assertEquals("ABQU", names.get(0));
        names = Suggestion.createStringList(userService.getSuggestedUserName("ab", UserNameSuggestionFilter.USER_GILD_SEARCH, 10));
        Assert.assertEquals(1, names.size());
        Assert.assertEquals("ABQU", names.get(0));
        names = Suggestion.createStringList(userService.getSuggestedUserName("abc", UserNameSuggestionFilter.USER_GILD_SEARCH, 10));
        Assert.assertEquals(0, names.size());
        names = Suggestion.createStringList(userService.getSuggestedUserName("abqu", UserNameSuggestionFilter.USER_GILD_SEARCH, 10));
        Assert.assertEquals(1, names.size());
        Assert.assertEquals("ABQU", names.get(0));
        names = Suggestion.createStringList(userService.getSuggestedUserName("%z", UserNameSuggestionFilter.USER_GILD_SEARCH, 10));
        Assert.assertEquals(2, names.size());
        Assert.assertEquals("BZ51", names.get(0));
        Assert.assertEquals("BZ52", names.get(1));
        names = Suggestion.createStringList(userService.getSuggestedUserName("%z5", UserNameSuggestionFilter.USER_GILD_SEARCH, 10));
        Assert.assertEquals(2, names.size());
        Assert.assertEquals("BZ51", names.get(0));
        Assert.assertEquals("BZ52", names.get(1));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void getSuggestedUserNameGuildRemaining() throws Exception {
        // HSQLDB does not support ilike
        configureSimplePlanetNoResources();
        // Create users
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createUser("ABCED", "xxx");
        createUser("ABCED1", "xxx");
        createUser("ABCED2", "xxx");
        createUser("ABCED3", "xxx");
        createUser("ABCED4", "xxx");
        createUser("ABCED5", "xxx");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // createGuilds
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        loginUser("ABCED", "xxx");
        guildService.createGuild("xxxx1");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        SuggestOracle.Response response = userService.getSuggestedUserName("A", UserNameSuggestionFilter.USER_GILD_SEARCH, 3);
        Assert.assertEquals(2, response.getMoreSuggestionsCount());
        List<String> names = Suggestion.createStringList(response);
        Assert.assertEquals(3, names.size());
        Assert.assertEquals("ABCED1", names.get(0));
        Assert.assertEquals("ABCED2", names.get(1));
        Assert.assertEquals("ABCED3", names.get(2));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void inGameLogin() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createUser("U1", "xxx");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        assertNotLoggedIn();
        userService.inGameLogin("U1", "xxx");
        assertLoggedIn();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        assertNotLoggedIn();
        try {
            userService.inGameLogin("U1", "yyy");
            Assert.fail();
        } catch (LoginFailedException e) {
            // Expected
        }
        assertNotLoggedIn();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void inGameLoginNotVerified() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        registerService.register("U1", "xxx", "xxx", "");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        assertNotLoggedIn();
        try {
            userService.inGameLogin("U1", "xxx");
            Assert.fail("LoginFailedNotVerifiedException");
        } catch (LoginFailedNotVerifiedException e) {
            // Expected
        }
        assertNotLoggedIn();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }


    @Test
    @DirtiesContext
    public void createUserAttentionPacket() throws Exception {
        configureSimplePlanetNoResources();
        // Mock content service
        ContentService contentServiceMock = EasyMock.createStrictMock(ContentService.class);
        contentServiceMock.fillUserAttentionPacket(createUserMatcher("U1"), EasyMock.<UserAttentionPacket>anyObject());
        EasyMock.replay(contentServiceMock);
        setPrivateField(UserServiceImpl.class, userService, "contentService", contentServiceMock);
        // Mock guild service
        GuildService guildServiceMock = EasyMock.createStrictMock(GuildService.class);
        guildServiceMock.fillUserAttentionPacket(createUserMatcher("U1"), EasyMock.<UserAttentionPacket>anyObject());
        EasyMock.replay(guildServiceMock);
        setPrivateField(UserServiceImpl.class, userService, "guildService", guildServiceMock);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUserAttentionPacket();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        userService.createUserAttentionPacket();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        EasyMock.verify(contentServiceMock, guildServiceMock);
    }

}