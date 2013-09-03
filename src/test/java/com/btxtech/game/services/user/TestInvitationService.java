package com.btxtech.game.services.user;

import com.btxtech.game.jsre.client.dialogs.incentive.FriendInvitationBonus;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.common.PropertyService;
import com.btxtech.game.services.common.PropertyServiceEnum;
import com.btxtech.game.services.history.HistoryService;
import com.btxtech.game.services.socialnet.facebook.FacebookSignedRequest;
import com.btxtech.game.services.user.impl.InvitationServiceImpl;
import com.btxtech.game.services.utg.DbLevel;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.wicket.pages.FacebookAppStart;
import com.btxtech.game.wicket.pages.InvitationStart;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import com.btxtech.game.wicket.uiservices.cms.impl.CmsUiServiceImpl;
import junit.framework.Assert;
import org.easymock.EasyMock;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.subethamail.wiser.WiserMessage;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * User: beat
 * Date: 24.07.13
 * Time: 17:47
 */
public class TestInvitationService extends AbstractServiceTest {
    private static final String MAIL_EN = "<html>\n" +
            "<body>\n" +
            "<h3>Join Razarion,</h3>\n" +
            "\n" +
            "<div>\n" +
            "    Johny has recruited you to play Razarion.\n" +
            "    <br>\n" +
            "    Johny thinks you will like Razarion and is sending you this e-mail from:\n" +
            "    <a href=\"http://www.razarion.com/game_cms_invitation/?user=1&type=mail\">http://www.razarion.com/game_cms_invitation/?user=1&type=mail</a>\n" +
            "    <br>\n" +
            "    <a href=\"http://www.razarion.com/game_cms_invitation/?user=1&type=mail\">Join</a>\n" +
            "    Razarion and experience an all-new way to play a massive multiplayer online real-time strate gygame.\n" +
            "    <br>\n" +
            "    <br>\n" +
            "    With kind regards your Razarion team\n" +
            "</div>\n" +
            "</body>\n" +
            "</html>";
    private static final String MAIL_DE = "<html>\n" +
            "<body>\n" +
            "<h3>Mach bei Razarion mit,</h3>\n" +
            "\n" +
            "<div>\n" +
            "    Johny hat dich zum Razarion spielen eingeladen.\n" +
            "    <br>\n" +
            "    Johny glaubt, dass du Razarion magst und sendet dir diese Email von:\n" +
            "    <a href=\"http://www.razarion.com/game_cms_invitation/?user=1&type=mail\">http://www.razarion.com/game_cms_invitation/?user=1&type=mail</a>\n" +
            "    <br>\n" +
            "    <a href=\"http://www.razarion.com/game_cms_invitation/?user=1&type=mail\">Spiele</a>\n" +
            "    Razarion und tauche in eine neue Dimension von massiv Multiplayer online Echtzeit-Strategiespielen ein.\n" +
            "    <br>\n" +
            "    <br>\n" +
            "    Dein Razarion-Team\n" +
            "</div>\n" +
            "</body>\n" +
            "</html>";
    @Autowired
    private InvitationService invitationService;
    @Autowired
    private UserService userService;
    @Autowired
    private CmsUiService cmsUiService;
    @Autowired
    private PropertyService propertyService;
    @Autowired
    private UserGuidanceService userGuidanceService;
    @Autowired
    private RegisterService registerService;

    @Test
    @DirtiesContext
    public void sendMailInviteErrorCases() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        try {
            invitationService.sendMailInvite("xxx");
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            Assert.assertEquals("User is not logged in or verified", e.getMessage());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void sendMailInvite() throws Exception {
        configureSimplePlanetNoResources();

        HistoryService historyServiceMock = EasyMock.createStrictMock(HistoryService.class);
        historyServiceMock.addFriendInvitationMailSent(createUserMatcher("Johny"), EasyMock.eq("test1@email.com"));
        EasyMock.replay(historyServiceMock);
        setPrivateField(InvitationServiceImpl.class, invitationService, "historyService", historyServiceMock);

        startFakeMailServer();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMockHttpServletRequest().addPreferredLocale(Locale.ENGLISH);
        createAndLoginUser("Johny");
        invitationService.sendMailInvite("test1@email.com");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        List<WiserMessage> wiserMessages = getFakeMailServer().getMessages();
        stopFakeMailServer();
        Assert.assertEquals(1, wiserMessages.size());
        WiserMessage wiserMessage = wiserMessages.get(0);
        Assert.assertEquals("test1@email.com", wiserMessage.getEnvelopeReceiver());
        Assert.assertEquals("no-reply@razarion.com", wiserMessage.getEnvelopeSender());
        Assert.assertEquals("Johny wants you to join Razarion", wiserMessage.getMimeMessage().getSubject());
        Assert.assertEquals("text/html;charset=UTF-8", wiserMessage.getMimeMessage().getContentType());
        assertStringIgnoreWhitespace(MAIL_EN, (String) wiserMessage.getMimeMessage().getContent());
        EasyMock.verify(historyServiceMock);
    }

    @Test
    @DirtiesContext
    public void sendMailInviteDe() throws Exception {
        configureSimplePlanetNoResources();

        startFakeMailServer();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMockHttpServletRequest().addPreferredLocale(Locale.GERMAN);

        createAndLoginUser("Johny");
        invitationService.sendMailInvite("test1@email.com");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        List<WiserMessage> wiserMessages = getFakeMailServer().getMessages();
        stopFakeMailServer();
        Assert.assertEquals(1, wiserMessages.size());
        WiserMessage wiserMessage = wiserMessages.get(0);
        Assert.assertEquals("test1@email.com", wiserMessage.getEnvelopeReceiver());
        Assert.assertEquals("no-reply@razarion.com", wiserMessage.getEnvelopeSender());
        Assert.assertEquals("Johny will dass du Razarion spielst", wiserMessage.getMimeMessage().getSubject());
        Assert.assertEquals("text/html;charset=UTF-8", wiserMessage.getMimeMessage().getContentType());
        assertStringIgnoreWhitespace(MAIL_DE, (String) wiserMessage.getMimeMessage().getContent());
    }

    @Test
    @DirtiesContext
    public void onFacebookInviteErrorCases() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        try {
            invitationService.onFacebookInvite("xxx", null);
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            Assert.assertEquals("User is not logged in or verified", e.getMessage());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void onFacebookInvite() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("Johny2");
        invitationService.onFacebookInvite("fbrequid1111", Arrays.asList("llll", "aaaaa", "eeeee"));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify DB
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DbFacebookInvitation> dbFacebookInvitations = loadAll(DbFacebookInvitation.class);
        Assert.assertEquals(1, dbFacebookInvitations.size());
        DbFacebookInvitation dbFacebookInvitation = dbFacebookInvitations.get(0);
        Assert.assertEquals("Johny2", dbFacebookInvitation.getHost().getUsername());
        Assert.assertEquals("fbrequid1111", dbFacebookInvitation.getFbRequestId());
        Assert.assertEquals("llll;aaaaa;eeeee", dbFacebookInvitation.getFbInvitedUserIds());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testRegisterMailUrl() throws Exception {
        configureSimplePlanetNoResources();
        // Preparation
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("Host");
        int hostId = getUserId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Test
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter("/game_cms_invitation/?user=" + hostId + "&type=mail");
        getWicketTester().startPage(InvitationStart.class);
        endHttpRequestAndOpenSessionInViewFilter();
        beginHttpRequestAndOpenSessionInViewFilter();
        registerService.register("Invitee", "xxx", "xxx", "");
        String verificationId = getUser().getVerificationId();
        endHttpRequestAndOpenSessionInViewFilter();
        beginHttpRequestAndOpenSessionInViewFilter();
        registerService.onVerificationPageCalled(verificationId);
        loginUser("Invitee");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify DB
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DbFriendInvitationBonus> dbFriendInvitationBonuses = loadAll(DbFriendInvitationBonus.class);
        Assert.assertEquals(1, dbFriendInvitationBonuses.size());
        DbFriendInvitationBonus dbFriendInvitationBonus = dbFriendInvitationBonuses.get(0);
        Assert.assertEquals("Host", dbFriendInvitationBonus.getHost().getUsername());
        Assert.assertEquals("Invitee", dbFriendInvitationBonus.getInvitee().getUsername());
        Assert.assertEquals(0, dbFriendInvitationBonus.getBonus());
        User user = userService.getUser("Invitee");
        Assert.assertNotNull(user.getDbInvitationInfo());
        Assert.assertEquals("Host", user.getDbInvitationInfo().getHost().getUsername());
        Assert.assertEquals(DbInvitationInfo.Source.MAIL, user.getDbInvitationInfo().getSource());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testFacebookInvitation() throws Exception {
        configureSimplePlanetNoResources();
        // Do not rejoice too quickly... this is just a test secret.
        setPrivateField(CmsUiServiceImpl.class, cmsUiService, "facebookAppSecret", "029a30fb9677d35c79c44d8a505d8fe1");
        // Preparation
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("Host");
        invitationService.onFacebookInvite("fbrequid1111", Arrays.asList("llll", "aaaaa", "eeeee"));
        propertyService.createProperty(PropertyServiceEnum.FACEBOOK_OPTIONAL_AD_URL_KEY, "fbAd");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Test
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter("/game_cms_facebook_app/?request_ids=fbrequid1111");
        getWicketTester().getRequest().getPostParameters().setParameterValue("signed_request", "v3-O8s1WrS9B2XnYXpRo61n2hKc9wboofRDHOxcF8XI.eyJhbGdvcml0aG0iOiJITUFDLVNIQTI1NiIsImV4cGlyZXMiOjEzNDMxNTI4MDAsImlzc3VlZF9hdCI6MTM0MzE0NjY4Mywib2F1dGhfdG9rZW4iOiJBQUFFa3RlWVZ1WkNNQkFDS29mOGpkWDMxcnVTWkN3RXFuRnFWd3Z2NnBBNldNMTVaQ1V6bzlRNmliUXJiWGtRVkJOeEF0UDJmc2EzVzY3ZXJITW5EWkFvNlZHRzVPajg4U2FJMWZOYkVyYjhCeDBuOURRWkIyIiwidXNlciI6eyJjb3VudHJ5IjoiY2giLCJsb2NhbGUiOiJlbl9VUyIsImFnZSI6eyJtaW4iOjIxfX0sInVzZXJfaWQiOiIxMDAwMDM2MzQwOTQxMzkifQ");
        getWicketTester().getRequest().getPostParameters().setParameterValue("email", "fakeEmail");
        getWicketTester().startPage(FacebookAppStart.class);
        endHttpRequestAndOpenSessionInViewFilter();
        beginHttpRequestAndOpenSessionInViewFilter();
        FacebookSignedRequest facebookSignedRequest = new FacebookSignedRequest(null, 0, null, null, "12345");
        facebookSignedRequest.setEmail("email");
        userService.createAndLoginFacebookUser(facebookSignedRequest, "Invitee");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify DB
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DbFriendInvitationBonus> dbFriendInvitationBonuses = loadAll(DbFriendInvitationBonus.class);
        Assert.assertEquals(1, dbFriendInvitationBonuses.size());
        DbFriendInvitationBonus dbFriendInvitationBonus = dbFriendInvitationBonuses.get(0);
        Assert.assertEquals("Host", dbFriendInvitationBonus.getHost().getUsername());
        Assert.assertEquals("Invitee", dbFriendInvitationBonus.getInvitee().getUsername());
        Assert.assertEquals(0, dbFriendInvitationBonus.getBonus());
        User user = userService.getUser("Invitee");
        Assert.assertNotNull(user.getDbInvitationInfo());
        Assert.assertEquals("Host", user.getDbInvitationInfo().getHost().getUsername());
        Assert.assertEquals(DbInvitationInfo.Source.FACEBOOK, user.getDbInvitationInfo().getSource());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void onLevelUp() throws Exception {
        configureMultiplePlanetsAndLevels();
        // Do not rejoice too quickly... this is just a test secret.
        setPrivateField(CmsUiServiceImpl.class, cmsUiService, "facebookAppSecret", "029a30fb9677d35c79c44d8a505d8fe1");
        // Preparation
        HistoryService historyServiceMock = EasyMock.createStrictMock(HistoryService.class);
        historyServiceMock.addFriendInvitationFacebookSent(createUserMatcher("Host"), EasyMock.eq("fbrequid1111"));
        historyServiceMock.addFriendInvitationBonus(createUserMatcher("Host"), createUserMatcher("Invitee"), EasyMock.eq(29), EasyMock.eq(29));
        historyServiceMock.addFriendInvitationBonus(createUserMatcher("Host"), createUserMatcher("Invitee"), EasyMock.eq(25), EasyMock.eq(54));
        EasyMock.replay(historyServiceMock);
        setPrivateField(InvitationServiceImpl.class, invitationService, "historyService", historyServiceMock);
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        propertyService.createProperty(PropertyServiceEnum.FACEBOOK_OPTIONAL_AD_URL_KEY, "fbAd");
        DbLevel dbLevel = userGuidanceService.getDbLevelCrud().readDbChild(TEST_LEVEL_2_REAL_ID);
        dbLevel.setFriendInvitationBonus(29);
        userGuidanceService.getDbLevelCrud().updateDbChild(dbLevel);
        dbLevel = userGuidanceService.getDbLevelCrud().readDbChild(TEST_LEVEL_3_REAL_ID);
        dbLevel.setFriendInvitationBonus(25);
        userGuidanceService.getDbLevelCrud().updateDbChild(dbLevel);
        userGuidanceService.activateLevels();
        createAndLoginUser("Host");
        invitationService.onFacebookInvite("fbrequid1111", Arrays.asList("llll", "aaaaa", "eeeee"));
        int hostId = getUserId();
        Assert.assertEquals(0, getUserState().getRazarion());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter("/game_cms_facebook_app/?request_ids=fbrequid1111");
        getWicketTester().getRequest().getPostParameters().setParameterValue("signed_request", "v3-O8s1WrS9B2XnYXpRo61n2hKc9wboofRDHOxcF8XI.eyJhbGdvcml0aG0iOiJITUFDLVNIQTI1NiIsImV4cGlyZXMiOjEzNDMxNTI4MDAsImlzc3VlZF9hdCI6MTM0MzE0NjY4Mywib2F1dGhfdG9rZW4iOiJBQUFFa3RlWVZ1WkNNQkFDS29mOGpkWDMxcnVTWkN3RXFuRnFWd3Z2NnBBNldNMTVaQ1V6bzlRNmliUXJiWGtRVkJOeEF0UDJmc2EzVzY3ZXJITW5EWkFvNlZHRzVPajg4U2FJMWZOYkVyYjhCeDBuOURRWkIyIiwidXNlciI6eyJjb3VudHJ5IjoiY2giLCJsb2NhbGUiOiJlbl9VUyIsImFnZSI6eyJtaW4iOjIxfX0sInVzZXJfaWQiOiIxMDAwMDM2MzQwOTQxMzkifQ");
        getWicketTester().getRequest().getPostParameters().setParameterValue("email", "fakeEmail");
        getWicketTester().startPage(FacebookAppStart.class);
        endHttpRequestAndOpenSessionInViewFilter();
        beginHttpRequestAndOpenSessionInViewFilter();
        FacebookSignedRequest facebookSignedRequest = new FacebookSignedRequest(null, 0, null, null, "12345");
        facebookSignedRequest.setEmail("email");
        userService.createAndLoginFacebookUser(facebookSignedRequest, "Invitee");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Test 1
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.loginFacebookUser(facebookSignedRequest);
        userGuidanceService.promote(getUserState(), TEST_LEVEL_2_REAL_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(29, userService.getUserState(userService.getUser("Host")).getRazarion());
        List<DbFriendInvitationBonus> dbFriendInvitationBonuses = loadAll(DbFriendInvitationBonus.class);
        Assert.assertEquals(1, dbFriendInvitationBonuses.size());
        DbFriendInvitationBonus dbFriendInvitationBonus = dbFriendInvitationBonuses.get(0);
        Assert.assertEquals("Host", dbFriendInvitationBonus.getHost().getUsername());
        Assert.assertEquals("Invitee", dbFriendInvitationBonus.getInvitee().getUsername());
        Assert.assertEquals(29, dbFriendInvitationBonus.getBonus());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Test 2
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter("/game_cms_invitation/?user=" + hostId + "&type=mail");
        getWicketTester().startPage(InvitationStart.class);
        userService.loginFacebookUser(facebookSignedRequest);
        userGuidanceService.promote(getUserState(), TEST_LEVEL_3_REAL_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(54, userService.getUserState(userService.getUser("Host")).getRazarion());
        dbFriendInvitationBonuses = loadAll(DbFriendInvitationBonus.class);
        Assert.assertEquals(1, dbFriendInvitationBonuses.size());
        dbFriendInvitationBonus = dbFriendInvitationBonuses.get(0);
        Assert.assertEquals("Host", dbFriendInvitationBonus.getHost().getUsername());
        Assert.assertEquals("Invitee", dbFriendInvitationBonus.getInvitee().getUsername());
        Assert.assertEquals(54, dbFriendInvitationBonus.getBonus());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        EasyMock.verify(historyServiceMock);
    }

    @Test
    @DirtiesContext
    public void getFriendInvitationBonusesErrorCases() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        try {
            invitationService.getFriendInvitationBonus();
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            Assert.assertEquals("User is not logged in or verified", e.getMessage());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void getFriendInvitationBonuses() throws Exception {
        configureMultiplePlanetsAndLevels();
        // Preparation
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("Host1");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("Host2");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("Invitee1");
        userGuidanceService.promote(getUserState(), TEST_LEVEL_1_SIMULATED_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("Invitee2");
        userGuidanceService.promote(getUserState(), TEST_LEVEL_2_REAL_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("Invitee3");
        userGuidanceService.promote(getUserState(), TEST_LEVEL_3_REAL_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        generateDbFriendInvitationBonus("Host1", "Invitee1", 0);
        generateDbFriendInvitationBonus("Host1", "Invitee2", 100);
        generateDbFriendInvitationBonus("Host2", "Invitee3", 22);
        // Test 1
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        List<FriendInvitationBonus> bonuses = getMovableService().getFriendInvitationBonuses();
        Assert.assertTrue(bonuses.isEmpty());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify Host1
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("Host1");
        bonuses = getMovableService().getFriendInvitationBonuses();
        Assert.assertEquals(2, bonuses.size());
        Assert.assertEquals(TEST_LEVEL_2_REAL, bonuses.get(0).getLevel());
        Assert.assertEquals(100, bonuses.get(0).getRazarionBonus());
        Assert.assertEquals("Invitee2", bonuses.get(0).getUserName());
        Assert.assertEquals(TEST_LEVEL_1_SIMULATED, bonuses.get(1).getLevel());
        Assert.assertEquals(0, bonuses.get(1).getRazarionBonus());
        Assert.assertEquals("Invitee1", bonuses.get(1).getUserName());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify Host2
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("Host2");
        bonuses = getMovableService().getFriendInvitationBonuses();
        Assert.assertEquals(1, bonuses.size());
        Assert.assertEquals(TEST_LEVEL_3_REAL, bonuses.get(0).getLevel());
        Assert.assertEquals(22, bonuses.get(0).getRazarionBonus());
        Assert.assertEquals("Invitee3", bonuses.get(0).getUserName());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    private void generateDbFriendInvitationBonus(String host, String invitee, int bonus) {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbFriendInvitationBonus dbFriendInvitationBonus = new DbFriendInvitationBonus(userService.getUser(host), userService.getUser(invitee));
        dbFriendInvitationBonus.addBonus(bonus);
        getSessionFactory().getCurrentSession().save(dbFriendInvitationBonus);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void getHost4FacebookRequestErrorCases() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        try {
            invitationService.getHost4FacebookRequest(null);
            Assert.fail("IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("fbRequestIds must nt be null and must contain som characters: null", e.getMessage());
        }
        try {
            invitationService.getHost4FacebookRequest("");
            Assert.fail("IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("fbRequestIds must nt be null and must contain som characters: ", e.getMessage());
        }
        try {
            invitationService.getHost4FacebookRequest(",,,");
            Assert.fail("IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("No fbRequestIds in: ,,,", e.getMessage());
        }
        try {
            invitationService.getHost4FacebookRequest("xxx");
            Assert.fail("IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("DbFacebookInvitation for: xxx", e.getMessage());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void getHost4FacebookRequest() throws Exception {
        configureSimplePlanetNoResources();
        // Prepare
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("Host1");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("Host2");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        saveDbFacebookInvitation("Host1", "fbrequest11111", 100000000000L);
        saveDbFacebookInvitation("Host1", "fbrequest11112", 200000000000L);
        saveDbFacebookInvitation("Host1", "fbrequest11113", 300000000000L);
        saveDbFacebookInvitation("Host1", "fbrequest11114", 400000000000L);
        saveDbFacebookInvitation("Host2", "fbrequest21111", 100010000000L);
        saveDbFacebookInvitation("Host2", "fbrequest21112", 200010000000L);
        saveDbFacebookInvitation("Host2", "fbrequest21113", 300010000000L);
        saveDbFacebookInvitation("Host2", "fbrequest21114", 400010000000L);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals("Host1", invitationService.getHost4FacebookRequest("fbrequest11111").getUsername());
        Assert.assertEquals("Host2", invitationService.getHost4FacebookRequest("fbrequest21111").getUsername());
        Assert.assertEquals("Host1", invitationService.getHost4FacebookRequest("fbrequest11111,fbrequest11112").getUsername());
        Assert.assertEquals("Host1", invitationService.getHost4FacebookRequest("fbrequest11111, fbrequest11112").getUsername());
        Assert.assertEquals("Host2", invitationService.getHost4FacebookRequest("fbrequest11111,fbrequest21114").getUsername());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    private void saveDbFacebookInvitation(String userName, String fbRequestId, long time) throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbFacebookInvitation dbFacebookInvitation = new DbFacebookInvitation();
        setPrivateField(DbFacebookInvitation.class, dbFacebookInvitation, "timeStamp", new Date(time));
        dbFacebookInvitation.setFbRequestId(fbRequestId);
        dbFacebookInvitation.setHost(userService.getUser(userName));
        getSessionFactory().getCurrentSession().save(dbFacebookInvitation);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }
}
