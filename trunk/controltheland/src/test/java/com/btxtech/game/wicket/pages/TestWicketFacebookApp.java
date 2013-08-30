package com.btxtech.game.wicket.pages;

import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.utg.tracker.DbPageAccess;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import com.btxtech.game.wicket.uiservices.cms.impl.CmsUiServiceImpl;
import org.apache.wicket.Page;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Locale;

/**
 * User: beat
 * Date: 13.07.13
 * Time: 09:53
 */
public class TestWicketFacebookApp extends AbstractServiceTest {
    @Autowired
    private CmsUiService cmsUiService;

    @Test
    @DirtiesContext
    public void newUserRegisterEnterGame() throws Exception {
        configureMultiplePlanetsAndLevels();

        // Do not rejoice too quickly Just... this is just a test secret.
        setPrivateField(CmsUiServiceImpl.class, cmsUiService, "facebookAppSecret", "029a30fb9677d35c79c44d8a505d8fe1");

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // First authorization -> OAuth dialog
        getWicketTester().getRequest().getPostParameters().setParameterValue("signed_request", "3RaYyXwkwhCc4OlVfDhAU9Y-O_pyqN4mgE7JyzPwcIc.eyJhbGdvcml0aG0iOiJITUFDLVNIQTI1NiIsImlzc3VlZF9hdCI6MTM0MzE0NjYzOSwidXNlciI6eyJjb3VudHJ5IjoiY2giLCJsb2NhbGUiOiJlbl9VUyIsImFnZSI6eyJtaW4iOjIxfX19");
        getWicketTester().startPage(FacebookAppStart.class);
        getWicketTester().assertRenderedPage(Game.class);
        Page gamePage = getWicketTester().getLastRenderedPage();
        Assert.assertEquals(TEST_LEVEL_TASK_1_1_SIMULATED_ID, gamePage.getPageParameters().get("taskId").toInt());
        Assert.assertNull(getUser());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void registeredByFacebookUnregisteredByGame() throws Exception {
        configureMultiplePlanetsAndLevels();
        // Do not rejoice too quickly... this is just a test secret.
        setPrivateField(CmsUiServiceImpl.class, cmsUiService, "facebookAppSecret", "029a30fb9677d35c79c44d8a505d8fe1");
        // Prepare
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginFacebookUser("100003634094139", "aaa");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Login
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().getRequest().getPostParameters().setParameterValue("signed_request", "v3-O8s1WrS9B2XnYXpRo61n2hKc9wboofRDHOxcF8XI.eyJhbGdvcml0aG0iOiJITUFDLVNIQTI1NiIsImV4cGlyZXMiOjEzNDMxNTI4MDAsImlzc3VlZF9hdCI6MTM0MzE0NjY4Mywib2F1dGhfdG9rZW4iOiJBQUFFa3RlWVZ1WkNNQkFDS29mOGpkWDMxcnVTWkN3RXFuRnFWd3Z2NnBBNldNMTVaQ1V6bzlRNmliUXJiWGtRVkJOeEF0UDJmc2EzVzY3ZXJITW5EWkFvNlZHRzVPajg4U2FJMWZOYkVyYjhCeDBuOURRWkIyIiwidXNlciI6eyJjb3VudHJ5IjoiY2giLCJsb2NhbGUiOiJlbl9VUyIsImFnZSI6eyJtaW4iOjIxfX0sInVzZXJfaWQiOiIxMDAwMDM2MzQwOTQxMzkifQ");
        getWicketTester().startPage(FacebookAppStart.class);
        getWicketTester().assertNoErrorMessage();
        getWicketTester().assertRenderedPage(Game.class);
        Page gamePage = getWicketTester().getLastRenderedPage();
        Assert.assertEquals(TEST_LEVEL_TASK_1_1_SIMULATED_ID, gamePage.getPageParameters().get("taskId").toInt());
        User user = getUser();
        Assert.assertEquals(User.SocialNet.FACEBOOK, user.getSocialNet());
        Assert.assertEquals("aaa", user.getUsername());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void unregisteredUserEnterGame() throws Exception {
        configureMultiplePlanetsAndLevels();
        // Do not rejoice too quickly... this is just a test secret.
        setPrivateField(CmsUiServiceImpl.class, cmsUiService, "facebookAppSecret", "029a30fb9677d35c79c44d8a505d8fe1");
        // Login
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().getRequest().getPostParameters().setParameterValue("signed_request", "v3-O8s1WrS9B2XnYXpRo61n2hKc9wboofRDHOxcF8XI.eyJhbGdvcml0aG0iOiJITUFDLVNIQTI1NiIsImV4cGlyZXMiOjEzNDMxNTI4MDAsImlzc3VlZF9hdCI6MTM0MzE0NjY4Mywib2F1dGhfdG9rZW4iOiJBQUFFa3RlWVZ1WkNNQkFDS29mOGpkWDMxcnVTWkN3RXFuRnFWd3Z2NnBBNldNMTVaQ1V6bzlRNmliUXJiWGtRVkJOeEF0UDJmc2EzVzY3ZXJITW5EWkFvNlZHRzVPajg4U2FJMWZOYkVyYjhCeDBuOURRWkIyIiwidXNlciI6eyJjb3VudHJ5IjoiY2giLCJsb2NhbGUiOiJlbl9VUyIsImFnZSI6eyJtaW4iOjIxfX0sInVzZXJfaWQiOiIxMDAwMDM2MzQwOTQxMzkifQ");
        getWicketTester().getRequest().getPostParameters().setParameterValue("email", "fakeEmail"); // Prevent FB api call
        getWicketTester().startPage(FacebookAppStart.class);
        getWicketTester().assertRenderedPage(Game.class);
        Assert.assertNull(getUser());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void usersDeniesAccess() throws Exception {
        configureMultiplePlanetsAndLevels();
        // Do not rejoice too quickly Just... this is just a test secret.
        setPrivateField(CmsUiServiceImpl.class, cmsUiService, "facebookAppSecret", "029a30fb9677d35c79c44d8a505d8fe1");
        // Login
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // First authorization -> OAuth dialog
        getWicketTester().getRequest().getPostParameters().setParameterValue("signed_request", "3RaYyXwkwhCc4OlVfDhAU9Y-O_pyqN4mgE7JyzPwcIc.eyJhbGdvcml0aG0iOiJITUFDLVNIQTI1NiIsImlzc3VlZF9hdCI6MTM0MzE0NjYzOSwidXNlciI6eyJjb3VudHJ5IjoiY2giLCJsb2NhbGUiOiJlbl9VUyIsImFnZSI6eyJtaW4iOjIxfX19");
        getWicketTester().startPage(FacebookAppStart.class);
        getWicketTester().assertNoErrorMessage();
        endHttpRequestAndOpenSessionInViewFilter();
        // User denies access
        beginHttpRequestAndOpenSessionInViewFilter();
        PageParameters parameters = new PageParameters();
        parameters.set("error", "access_denied");
        getWicketTester().startPage(FacebookAppStart.class, parameters);
        getWicketTester().assertRenderedPage(Game.class);
        Assert.assertNull(getUser());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void registeredUserNewFacebookUser() throws Exception {
        configureMultiplePlanetsAndLevels();

        // Do not rejoice too quickly Just... this is just a test secret.
        setPrivateField(CmsUiServiceImpl.class, cmsUiService, "facebookAppSecret", "029a30fb9677d35c79c44d8a505d8fe1");

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        getWicketTester().getSession().setLocale(Locale.ENGLISH);
        // First authorization but already logged in
        getWicketTester().getRequest().getPostParameters().setParameterValue("signed_request", "3RaYyXwkwhCc4OlVfDhAU9Y-O_pyqN4mgE7JyzPwcIc.eyJhbGdvcml0aG0iOiJITUFDLVNIQTI1NiIsImlzc3VlZF9hdCI6MTM0MzE0NjYzOSwidXNlciI6eyJjb3VudHJ5IjoiY2giLCJsb2NhbGUiOiJlbl9VUyIsImFnZSI6eyJtaW4iOjIxfX19");
        getWicketTester().startPage(FacebookAppStart.class);
        getWicketTester().assertErrorMessages("You are already logged in as U1. Please logout before you login via Facebook");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void registeredUserExistingFacebookUser() throws Exception {
        configureMultiplePlanetsAndLevels();

        // Do not rejoice too quickly Just... this is just a test secret.
        setPrivateField(CmsUiServiceImpl.class, cmsUiService, "facebookAppSecret", "029a30fb9677d35c79c44d8a505d8fe1");

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        getWicketTester().getSession().setLocale(Locale.ENGLISH);
        // First authorization but already logged in
        getWicketTester().getRequest().getPostParameters().setParameterValue("signed_request", "v3-O8s1WrS9B2XnYXpRo61n2hKc9wboofRDHOxcF8XI.eyJhbGdvcml0aG0iOiJITUFDLVNIQTI1NiIsImV4cGlyZXMiOjEzNDMxNTI4MDAsImlzc3VlZF9hdCI6MTM0MzE0NjY4Mywib2F1dGhfdG9rZW4iOiJBQUFFa3RlWVZ1WkNNQkFDS29mOGpkWDMxcnVTWkN3RXFuRnFWd3Z2NnBBNldNMTVaQ1V6bzlRNmliUXJiWGtRVkJOeEF0UDJmc2EzVzY3ZXJITW5EWkFvNlZHRzVPajg4U2FJMWZOYkVyYjhCeDBuOURRWkIyIiwidXNlciI6eyJjb3VudHJ5IjoiY2giLCJsb2NhbGUiOiJlbl9VUyIsImFnZSI6eyJtaW4iOjIxfX0sInVzZXJfaWQiOiIxMDAwMDM2MzQwOTQxMzkifQ");
        getWicketTester().startPage(FacebookAppStart.class);
        getWicketTester().assertErrorMessages("You are already logged in as U1. Please logout before you login via Facebook");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void registeredUserExistingDifferentFacebookUser() throws Exception {
        configureMultiplePlanetsAndLevels();

        // Do not rejoice too quickly Just... this is just a test secret.
        setPrivateField(CmsUiServiceImpl.class, cmsUiService, "facebookAppSecret", "029a30fb9677d35c79c44d8a505d8fe1");

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginFacebookUser("100003634094139", "xxx");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        getWicketTester().getSession().setLocale(Locale.ENGLISH);
        // First authorization but already logged in
        getWicketTester().getRequest().getPostParameters().setParameterValue("signed_request", "v3-O8s1WrS9B2XnYXpRo61n2hKc9wboofRDHOxcF8XI.eyJhbGdvcml0aG0iOiJITUFDLVNIQTI1NiIsImV4cGlyZXMiOjEzNDMxNTI4MDAsImlzc3VlZF9hdCI6MTM0MzE0NjY4Mywib2F1dGhfdG9rZW4iOiJBQUFFa3RlWVZ1WkNNQkFDS29mOGpkWDMxcnVTWkN3RXFuRnFWd3Z2NnBBNldNMTVaQ1V6bzlRNmliUXJiWGtRVkJOeEF0UDJmc2EzVzY3ZXJITW5EWkFvNlZHRzVPajg4U2FJMWZOYkVyYjhCeDBuOURRWkIyIiwidXNlciI6eyJjb3VudHJ5IjoiY2giLCJsb2NhbGUiOiJlbl9VUyIsImFnZSI6eyJtaW4iOjIxfX0sInVzZXJfaWQiOiIxMDAwMDM2MzQwOTQxMzkifQ");
        getWicketTester().startPage(FacebookAppStart.class);
        Assert.assertEquals("xxx", getUser().getUsername());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testTrackingNewUser() throws Exception {
        configureMultiplePlanetsAndLevels();

        // Do not rejoice too quickly Just... this is just a test secret.
        setPrivateField(CmsUiServiceImpl.class, cmsUiService, "facebookAppSecret", "029a30fb9677d35c79c44d8a505d8fe1");

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // First authorization
        getWicketTester().getRequest().getPostParameters().setParameterValue("signed_request", "3RaYyXwkwhCc4OlVfDhAU9Y-O_pyqN4mgE7JyzPwcIc.eyJhbGdvcml0aG0iOiJITUFDLVNIQTI1NiIsImlzc3VlZF9hdCI6MTM0MzE0NjYzOSwidXNlciI6eyJjb3VudHJ5IjoiY2giLCJsb2NhbGUiOiJlbl9VUyIsImFnZSI6eyJtaW4iOjIxfX19");
        getWicketTester().startPage(FacebookAppStart.class);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify DB
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DbPageAccess> dbPageAccesses = loadAll(DbPageAccess.class);
        Assert.assertEquals(2, dbPageAccesses.size());
        Assert.assertEquals(FacebookAppStart.class.getName(), dbPageAccesses.get(0).getPage());
        Assert.assertEquals("---User NOT Authorized by Facebook--- Query Parameters: ", dbPageAccesses.get(0).getAdditional());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testTrackingNewUserAlreadyLoggedIn() throws Exception {
        configureMultiplePlanetsAndLevels();

        // Do not rejoice too quickly Just... this is just a test secret.
        setPrivateField(CmsUiServiceImpl.class, cmsUiService, "facebookAppSecret", "029a30fb9677d35c79c44d8a505d8fe1");

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        // First authorization -> OAuth dialog
        getWicketTester().getRequest().getPostParameters().setParameterValue("signed_request", "3RaYyXwkwhCc4OlVfDhAU9Y-O_pyqN4mgE7JyzPwcIc.eyJhbGdvcml0aG0iOiJITUFDLVNIQTI1NiIsImlzc3VlZF9hdCI6MTM0MzE0NjYzOSwidXNlciI6eyJjb3VudHJ5IjoiY2giLCJsb2NhbGUiOiJlbl9VUyIsImFnZSI6eyJtaW4iOjIxfX19");
        getWicketTester().startPage(FacebookAppStart.class);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify DB
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DbPageAccess> dbPageAccesses = loadAll(DbPageAccess.class);
        Assert.assertEquals(1, dbPageAccesses.size());
        Assert.assertEquals(FacebookAppStart.class.getName(), dbPageAccesses.get(0).getPage());
        Assert.assertEquals("---User NOT Authorized by Facebook but logged in with different user Game--- Parameters: ", dbPageAccesses.get(0).getAdditional());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testTrackingAccessDenied() throws Exception {
        configureMultiplePlanetsAndLevels();
        // Login
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // First authorization -> OAuth dialog
        PageParameters parameters = new PageParameters();
        parameters.set("error", "access_denied");
        getWicketTester().startPage(FacebookAppStart.class, parameters);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify DB
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DbPageAccess> dbPageAccesses = loadAll(DbPageAccess.class);
        Assert.assertEquals(2, dbPageAccesses.size());
        Assert.assertEquals(FacebookAppStart.class.getName(), dbPageAccesses.get(0).getPage());
        Assert.assertEquals("---Access Denied--- Query Parameters: error=[access_denied]", dbPageAccesses.get(0).getAdditional());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testTrackingRegisteredUser() throws Exception {
        configureMultiplePlanetsAndLevels();
        // Do not rejoice too quickly... this is just a test secret.
        setPrivateField(CmsUiServiceImpl.class, cmsUiService, "facebookAppSecret", "029a30fb9677d35c79c44d8a505d8fe1");
        // Prepare
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginFacebookUser("100003634094139", "aaa");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Login
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().getRequest().getPostParameters().setParameterValue("signed_request", "v3-O8s1WrS9B2XnYXpRo61n2hKc9wboofRDHOxcF8XI.eyJhbGdvcml0aG0iOiJITUFDLVNIQTI1NiIsImV4cGlyZXMiOjEzNDMxNTI4MDAsImlzc3VlZF9hdCI6MTM0MzE0NjY4Mywib2F1dGhfdG9rZW4iOiJBQUFFa3RlWVZ1WkNNQkFDS29mOGpkWDMxcnVTWkN3RXFuRnFWd3Z2NnBBNldNMTVaQ1V6bzlRNmliUXJiWGtRVkJOeEF0UDJmc2EzVzY3ZXJITW5EWkFvNlZHRzVPajg4U2FJMWZOYkVyYjhCeDBuOURRWkIyIiwidXNlciI6eyJjb3VudHJ5IjoiY2giLCJsb2NhbGUiOiJlbl9VUyIsImFnZSI6eyJtaW4iOjIxfX0sInVzZXJfaWQiOiIxMDAwMDM2MzQwOTQxMzkifQ");
        getWicketTester().startPage(FacebookAppStart.class);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify DB
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DbPageAccess> dbPageAccesses = loadAll(DbPageAccess.class);
        Assert.assertEquals(2, dbPageAccesses.size());
        Assert.assertEquals(FacebookAppStart.class.getName(), dbPageAccesses.get(0).getPage());
        Assert.assertEquals("---User Authorized by Facebook and registered by Game--- Parameters: ", dbPageAccesses.get(0).getAdditional());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testTrackingRegisteredByFacebook() throws Exception {
        configureMultiplePlanetsAndLevels();
        // Do not rejoice too quickly... this is just a test secret.
        setPrivateField(CmsUiServiceImpl.class, cmsUiService, "facebookAppSecret", "029a30fb9677d35c79c44d8a505d8fe1");
        // Login
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().getRequest().getPostParameters().setParameterValue("signed_request", "v3-O8s1WrS9B2XnYXpRo61n2hKc9wboofRDHOxcF8XI.eyJhbGdvcml0aG0iOiJITUFDLVNIQTI1NiIsImV4cGlyZXMiOjEzNDMxNTI4MDAsImlzc3VlZF9hdCI6MTM0MzE0NjY4Mywib2F1dGhfdG9rZW4iOiJBQUFFa3RlWVZ1WkNNQkFDS29mOGpkWDMxcnVTWkN3RXFuRnFWd3Z2NnBBNldNMTVaQ1V6bzlRNmliUXJiWGtRVkJOeEF0UDJmc2EzVzY3ZXJITW5EWkFvNlZHRzVPajg4U2FJMWZOYkVyYjhCeDBuOURRWkIyIiwidXNlciI6eyJjb3VudHJ5IjoiY2giLCJsb2NhbGUiOiJlbl9VUyIsImFnZSI6eyJtaW4iOjIxfX0sInVzZXJfaWQiOiIxMDAwMDM2MzQwOTQxMzkifQ");
        getWicketTester().getRequest().getPostParameters().setParameterValue("email", "fakeEmail");
        getWicketTester().startPage(FacebookAppStart.class);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify DB
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DbPageAccess> dbPageAccesses = loadAll(DbPageAccess.class);
        Assert.assertEquals(2, dbPageAccesses.size());
        Assert.assertEquals(FacebookAppStart.class.getName(), dbPageAccesses.get(0).getPage());
        Assert.assertEquals("---User Authorized by Facebook but NOT registered by Game--- Parameters: ", dbPageAccesses.get(0).getAdditional());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testTrackingRegisteredByFacebookLoggedInOtherUser() throws Exception {
        configureMultiplePlanetsAndLevels();
        // Do not rejoice too quickly... this is just a test secret.
        setPrivateField(CmsUiServiceImpl.class, cmsUiService, "facebookAppSecret", "029a30fb9677d35c79c44d8a505d8fe1");
        // Login
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        getWicketTester().getRequest().getPostParameters().setParameterValue("signed_request", "v3-O8s1WrS9B2XnYXpRo61n2hKc9wboofRDHOxcF8XI.eyJhbGdvcml0aG0iOiJITUFDLVNIQTI1NiIsImV4cGlyZXMiOjEzNDMxNTI4MDAsImlzc3VlZF9hdCI6MTM0MzE0NjY4Mywib2F1dGhfdG9rZW4iOiJBQUFFa3RlWVZ1WkNNQkFDS29mOGpkWDMxcnVTWkN3RXFuRnFWd3Z2NnBBNldNMTVaQ1V6bzlRNmliUXJiWGtRVkJOeEF0UDJmc2EzVzY3ZXJITW5EWkFvNlZHRzVPajg4U2FJMWZOYkVyYjhCeDBuOURRWkIyIiwidXNlciI6eyJjb3VudHJ5IjoiY2giLCJsb2NhbGUiOiJlbl9VUyIsImFnZSI6eyJtaW4iOjIxfX0sInVzZXJfaWQiOiIxMDAwMDM2MzQwOTQxMzkifQ");
        getWicketTester().getRequest().getPostParameters().setParameterValue("email", "fakeEmail");
        getWicketTester().startPage(FacebookAppStart.class);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify DB
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DbPageAccess> dbPageAccesses = loadAll(DbPageAccess.class);
        Assert.assertEquals(1, dbPageAccesses.size());
        Assert.assertEquals(FacebookAppStart.class.getName(), dbPageAccesses.get(0).getPage());
        Assert.assertEquals("---User Authorized by Facebook but logged in with different user Game--- Parameters: ", dbPageAccesses.get(0).getAdditional());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

}
