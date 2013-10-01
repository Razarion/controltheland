package com.btxtech.game.services.user;

import com.btxtech.game.jsre.common.CmsUtil;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.cms.CmsService;
import com.btxtech.game.services.cms.page.DbPage;
import com.btxtech.game.services.common.PropertyService;
import com.btxtech.game.services.common.PropertyServiceEnum;
import com.btxtech.game.services.socialnet.facebook.FacebookSignedRequest;
import com.btxtech.game.wicket.pages.cms.CmsPage;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
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
public class TestUserServiceFacebook extends AbstractServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private CmsService cmsService;
    @Autowired
    private CmsUiService cmsUiService;
    @Autowired
    private PropertyService propertyService;

    @Test
    @DirtiesContext
    public void createLoginCms() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbPage page = cmsService.getPageCrudRootServiceHelper().createDbChild();
        page.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        page.setName("Home");
        cmsService.getPageCrudRootServiceHelper().updateDbChild(page);
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();


        // Register FB user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().startPage(CmsPage.class);
        FacebookSignedRequest facebookSignedRequest = new FacebookSignedRequest(null, 0, null, null, "12345");
        facebookSignedRequest.setEmail("email");
        Assert.assertFalse(userService.isFacebookUserRegistered(facebookSignedRequest));
        Assert.assertFalse(userService.isFacebookLoggedIn(facebookSignedRequest));
        Assert.assertTrue(userService.getAuthorities().isEmpty());
        userService.createAndLoginFacebookUser(facebookSignedRequest, "nickname");
        Assert.assertTrue(userService.isFacebookLoggedIn(facebookSignedRequest));
        Assert.assertTrue(userService.isFacebookUserRegistered(facebookSignedRequest));
        Assert.assertFalse(userService.getAuthorities().isEmpty());
        Assert.assertEquals("nickname", userService.getUser().getUsername());
        Assert.assertEquals("email", userService.getUser().getEmail());
        userService.onSessionTimedOut(getUserState());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Login FB user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertTrue(userService.isFacebookUserRegistered(facebookSignedRequest));
        Assert.assertFalse(userService.isFacebookLoggedIn(facebookSignedRequest));
        Assert.assertTrue(userService.getAuthorities().isEmpty());
        userService.loginFacebookUser(facebookSignedRequest);
        Assert.assertTrue(userService.isFacebookLoggedIn(facebookSignedRequest));
        Assert.assertTrue(userService.isFacebookUserRegistered(facebookSignedRequest));
        Assert.assertFalse(userService.getAuthorities().isEmpty());
        Assert.assertEquals("nickname", userService.getUser().getUsername());
        Assert.assertEquals("email", userService.getUser().getEmail());
        userService.onSessionTimedOut(getUserState());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void createLoginMovableService() throws Exception {
        configureSimplePlanetNoResources();

        // Register FB user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        FacebookSignedRequest facebookSignedRequest = new FacebookSignedRequest(null, 0, null, null, "12345");
        facebookSignedRequest.setEmail("email");
        Assert.assertFalse(userService.isFacebookUserRegistered(facebookSignedRequest));
        Assert.assertFalse(userService.isFacebookLoggedIn(facebookSignedRequest));
        Assert.assertTrue(userService.getAuthorities().isEmpty());
        userService.createAndLoginFacebookUser(facebookSignedRequest, "nickname");
        Assert.assertTrue(userService.isFacebookLoggedIn(facebookSignedRequest));
        Assert.assertTrue(userService.isFacebookUserRegistered(facebookSignedRequest));
        Assert.assertFalse(userService.getAuthorities().isEmpty());
        Assert.assertEquals("nickname", userService.getUser().getUsername());
        Assert.assertEquals("email", userService.getUser().getEmail());
        userService.onSessionTimedOut(getUserState());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Login FB user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertTrue(userService.isFacebookUserRegistered(facebookSignedRequest));
        Assert.assertFalse(userService.isFacebookLoggedIn(facebookSignedRequest));
        Assert.assertFalse(userService.isFacebookLoggedIn(facebookSignedRequest));
        userService.loginFacebookUser(facebookSignedRequest);
        Assert.assertTrue(userService.isFacebookLoggedIn(facebookSignedRequest));
        Assert.assertTrue(userService.isFacebookUserRegistered(facebookSignedRequest));
        Assert.assertEquals("nickname", userService.getUser().getUsername());
        Assert.assertEquals("email", userService.getUser().getEmail());
        Assert.assertFalse(userService.getAuthorities().isEmpty());
        endHttpRequestAndOpenSessionInViewFilter();
        // Same session
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertTrue(userService.isFacebookLoggedIn(facebookSignedRequest));
        Assert.assertTrue(userService.isFacebookUserRegistered(facebookSignedRequest));
        Assert.assertEquals("nickname", userService.getUser().getUsername());
        Assert.assertEquals("email", userService.getUser().getEmail());
        Assert.assertFalse(userService.getAuthorities().isEmpty());
        userService.onSessionTimedOut(getUserState());
        endHttpRequestAndOpenSessionInViewFilter();

        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void logoutDuringFacebookRegisterLogin() throws Exception {
        configureSimplePlanetNoResources();

        // Create FB user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        assertLoggedIn("U1");
        FacebookSignedRequest facebookSignedRequest = new FacebookSignedRequest(null, 0, null, null, "12345");
        facebookSignedRequest.setEmail("email");
        userService.createAndLoginFacebookUser(facebookSignedRequest, "F1");
        assertLoggedIn("F1");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void logoutDuringFacebookLogin() throws Exception {
        configureSimplePlanetNoResources();

        // Create FB user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        FacebookSignedRequest facebookSignedRequest = new FacebookSignedRequest(null, 0, null, null, "12345");
        facebookSignedRequest.setEmail("email");
        userService.createAndLoginFacebookUser(facebookSignedRequest, "F1");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Create FB user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        assertLoggedIn("U1");
        userService.loginFacebookUser(new FacebookSignedRequest(null, 0, null, null, "12345"));
        assertLoggedIn("F1");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    private void assertLoggedIn(String userName) {
        Assert.assertEquals(userName, userService.getSimpleUser().getName());
        for (GrantedAuthority grantedAuthority : userService.getAuthorities()) {
            if (grantedAuthority.getAuthority().equals(SecurityRoles.ROLE_USER)) {
                return;
            }
        }
        Assert.fail("User is not logged in");
    }


    @Test
    @DirtiesContext
    public void testFacebookProperties() throws Exception {
        Assert.assertEquals("https://apps.facebook.com/razarion/", cmsUiService.getFacebookRedirectUri());
    }

    @Test
    @DirtiesContext
    public void testFacebookAppTracking() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        propertyService.createProperty(PropertyServiceEnum.FACEBOOK_OPTIONAL_AD_URL_KEY, "fbAd");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter("/game_cms_facebook_app/?fb_source=bookmark_favorites&ref=bookmarks&count=0&fb_bmpos=6_0&fbAd=ad1");
        getWicketTester().getRequest().getPostParameters().setParameterValue("signed_request", "v3-O8s1WrS9B2XnYXpRo61n2hKc9wboofRDHOxcF8XI.eyJhbGdvcml0aG0iOiJITUFDLVNIQTI1NiIsImV4cGlyZXMiOjEzNDMxNTI4MDAsImlzc3VlZF9hdCI6MTM0MzE0NjY4Mywib2F1dGhfdG9rZW4iOiJBQUFFa3RlWVZ1WkNNQkFDS29mOGpkWDMxcnVTWkN3RXFuRnFWd3Z2NnBBNldNMTVaQ1V6bzlRNmliUXJiWGtRVkJOeEF0UDJmc2EzVzY3ZXJITW5EWkFvNlZHRzVPajg4U2FJMWZOYkVyYjhCeDBuOURRWkIyIiwidXNlciI6eyJjb3VudHJ5IjoiY2giLCJsb2NhbGUiOiJlbl9VUyIsImFnZSI6eyJtaW4iOjIxfX0sInVzZXJfaWQiOiIxMDAwMDM2MzQwOTQxMzkifQ");
        getWicketTester().executeUrl("/game_cms_facebook_app/?fb_source=bookmark_favorites&ref=bookmarks&count=0&fb_bmpos=6_0&fbAd=ad1");
        FacebookSignedRequest facebookSignedRequest = new FacebookSignedRequest(null, 0, null, null, "12345");
        facebookSignedRequest.setEmail("email");
        userService.createAndLoginFacebookUser(facebookSignedRequest, "F2");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        User user = userService.getUser("F2");
        Assert.assertEquals("?fb_source=bookmark_favorites&ref=bookmarks&count=0&fb_bmpos=6_0&fbAd=ad1", user.getDbFacebookSource().getWholeString());
        Assert.assertEquals("bookmark_favorites", user.getDbFacebookSource().getFbSource());
        Assert.assertEquals("ad1", user.getDbFacebookSource().getOptionalAdValue());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

}