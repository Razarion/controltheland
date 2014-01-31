package com.btxtech.game.wicket.pages;

import com.btxtech.game.jsre.common.CmsUtil;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.cms.CmsService;
import com.btxtech.game.services.cms.layout.DbContentStaticHtml;
import com.btxtech.game.services.cms.page.DbPage;
import com.btxtech.game.services.cms.page.DbPageStyle;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.socialnet.facebook.FacebookAge;
import com.btxtech.game.services.socialnet.facebook.FacebookSignedRequest;
import com.btxtech.game.services.socialnet.facebook.FacebookUser;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.utg.tracker.DbPageAccess;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import com.btxtech.game.wicket.uiservices.cms.impl.CmsUiServiceImpl;
import org.apache.wicket.Page;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.tester.FormTester;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Locale;

/**
 * User: beat
 * Date: 13.07.13
 * Time: 09:53
 */
public class TestWicketFacebookAppNickName extends AbstractServiceTest {
    @Autowired
    private CmsUiService cmsUiService;
    @Autowired
    private CmsService cmsService;

    @Test
    @DirtiesContext
    public void noSignedRequest() throws Exception {
        configureMultiplePlanetsAndLevels();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage facebookApp = pageCrud.createDbChild();
        facebookApp.setPredefinedType(CmsUtil.CmsPredefinedPage.FACEBOOK_START);
        pageCrud.updateDbChild(facebookApp);
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Do not rejoice too quickly Just... this is just a test secret.
        setPrivateField(CmsUiServiceImpl.class, cmsUiService, "facebookAppSecret", "029a30fb9677d35c79c44d8a505d8fe1");

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // First authorization show OAuth dialog
        getWicketTester().startPage(FacebookAppNickName.class);
        getWicketTester().assertRenderedPage(FacebookAppStart.class);
        Assert.assertNull(getUser());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // verify tracker
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        assertPageAccessCount(2);
        assertPageAccess(0, FacebookAppNickName.class, "No signed request");
        assertPageAccess(1, FacebookAppStart.class, "No signed request");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void usersDeniesAccess() throws Exception {
        configureMultiplePlanetsAndLevels();
        // Prepare predefined facebook site
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage facebookNickName = pageCrud.createDbChild();
        facebookNickName.setPredefinedType(CmsUtil.CmsPredefinedPage.FACEBOOK_NICKNAME);
        facebookNickName.setName("facebookNickName");
        pageCrud.updateDbChild(facebookNickName);
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Do not rejoice too quickly Just... this is just a test secret.
        setPrivateField(CmsUiServiceImpl.class, cmsUiService, "facebookAppSecret", "029a30fb9677d35c79c44d8a505d8fe1");
        // User denies access
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        PageParameters parameters = new PageParameters();
        parameters.set("error", "access_denied");
        getWicketTester().startPage(FacebookAppNickName.class, parameters);
        getWicketTester().assertRenderedPage(Game.class);
        Assert.assertNull(getUser());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // verify tracker
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        assertPageAccessCount(2);
        assertPageAccess(0, FacebookAppNickName.class, "---Access Denied--- Query Parameters: error=[access_denied]");
        assertPageAccess(1, Game.class, "LevelTaskId=1");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void unknownToFacebook() throws Exception {
        configureMultiplePlanetsAndLevels();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage facebookApp = pageCrud.createDbChild();
        facebookApp.setPredefinedType(CmsUtil.CmsPredefinedPage.FACEBOOK_START);
        facebookApp.setName("facebook nickname");
        pageCrud.updateDbChild(facebookApp);
        DbPage facebookNickname = pageCrud.createDbChild();
        facebookNickname.setPredefinedType(CmsUtil.CmsPredefinedPage.FACEBOOK_NICKNAME);
        facebookNickname.setName("facebook nickname");
        pageCrud.updateDbChild(facebookNickname);
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Do not rejoice too quickly Just... this is just a test secret.
        setPrivateField(CmsUiServiceImpl.class, cmsUiService, "facebookAppSecret", "029a30fb9677d35c79c44d8a505d8fe1");

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // First authorization show OAuth dialog
        getWicketTester().getRequest().getPostParameters().setParameterValue("signed_request", "3RaYyXwkwhCc4OlVfDhAU9Y-O_pyqN4mgE7JyzPwcIc.eyJhbGdvcml0aG0iOiJITUFDLVNIQTI1NiIsImlzc3VlZF9hdCI6MTM0MzE0NjYzOSwidXNlciI6eyJjb3VudHJ5IjoiY2giLCJsb2NhbGUiOiJlbl9VUyIsImFnZSI6eyJtaW4iOjIxfX19");
        getWicketTester().startPage(FacebookAppNickName.class);
        getWicketTester().assertRenderedPage(FacebookAppStart.class);
        Assert.assertNull(getUser());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // verify tracker
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        assertPageAccessCount(2);
        assertPageAccess(0, FacebookAppNickName.class, "---User NOT Authorized by Facebook---");
        assertPageAccess(1, FacebookAppStart.class, "---User NOT Authorized by Facebook---");
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
        getWicketTester().startPage(FacebookAppNickName.class);
        getWicketTester().assertErrorMessages("You are already logged in as U1. Please logout before you login via Facebook");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // verify tracker
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        assertPageAccessCount(1);
        assertPageAccess(0, FacebookAppNickName.class, "---User NOT Authorized by Facebook but logged in with different user--- Parameters: ");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void registeredByFacebookRegisteredByGame() throws Exception {
        configureMultiplePlanetsAndLevels();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginFacebookUser("100003634094139", "aaa");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Do not rejoice too quickly Just... this is just a test secret.
        setPrivateField(CmsUiServiceImpl.class, cmsUiService, "facebookAppSecret", "029a30fb9677d35c79c44d8a505d8fe1");

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // First authorization show OAuth dialog
        getWicketTester().getRequest().getPostParameters().setParameterValue("signed_request", "v3-O8s1WrS9B2XnYXpRo61n2hKc9wboofRDHOxcF8XI.eyJhbGdvcml0aG0iOiJITUFDLVNIQTI1NiIsImV4cGlyZXMiOjEzNDMxNTI4MDAsImlzc3VlZF9hdCI6MTM0MzE0NjY4Mywib2F1dGhfdG9rZW4iOiJBQUFFa3RlWVZ1WkNNQkFDS29mOGpkWDMxcnVTWkN3RXFuRnFWd3Z2NnBBNldNMTVaQ1V6bzlRNmliUXJiWGtRVkJOeEF0UDJmc2EzVzY3ZXJITW5EWkFvNlZHRzVPajg4U2FJMWZOYkVyYjhCeDBuOURRWkIyIiwidXNlciI6eyJjb3VudHJ5IjoiY2giLCJsb2NhbGUiOiJlbl9VUyIsImFnZSI6eyJtaW4iOjIxfX0sInVzZXJfaWQiOiIxMDAwMDM2MzQwOTQxMzkifQ");
        getWicketTester().startPage(FacebookAppNickName.class);
        getWicketTester().assertRenderedPage(Game.class);
        User user = getUser();
        Assert.assertEquals(User.SocialNet.FACEBOOK, user.getSocialNet());
        Assert.assertEquals("aaa", user.getUsername());
        Assert.assertEquals("100003634094139", user.getSocialNetUserId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // verify tracker
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        assertPageAccessCount(2);
        assertPageAccess(0, FacebookAppNickName.class, "---User Authorized by Facebook and registered by Game--- Parameters: ");
        assertPageAccess(1, Game.class, "LevelTaskId=1");
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
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage facebookNickname = pageCrud.createDbChild();
        facebookNickname.setPredefinedType(CmsUtil.CmsPredefinedPage.FACEBOOK_NICKNAME);
        facebookNickname.setName("facebook nickname");
        pageCrud.updateDbChild(facebookNickname);
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // call nickname page
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().getRequest().getPostParameters().setParameterValue("signed_request", "v3-O8s1WrS9B2XnYXpRo61n2hKc9wboofRDHOxcF8XI.eyJhbGdvcml0aG0iOiJITUFDLVNIQTI1NiIsImV4cGlyZXMiOjEzNDMxNTI4MDAsImlzc3VlZF9hdCI6MTM0MzE0NjY4Mywib2F1dGhfdG9rZW4iOiJBQUFFa3RlWVZ1WkNNQkFDS29mOGpkWDMxcnVTWkN3RXFuRnFWd3Z2NnBBNldNMTVaQ1V6bzlRNmliUXJiWGtRVkJOeEF0UDJmc2EzVzY3ZXJITW5EWkFvNlZHRzVPajg4U2FJMWZOYkVyYjhCeDBuOURRWkIyIiwidXNlciI6eyJjb3VudHJ5IjoiY2giLCJsb2NhbGUiOiJlbl9VUyIsImFnZSI6eyJtaW4iOjIxfX0sInVzZXJfaWQiOiIxMDAwMDM2MzQwOTQxMzkifQ");
        getWicketTester().getRequest().getPostParameters().setParameterValue("email", "ssdsd@fdfdfd.com");
        getWicketTester().getRequest().getPostParameters().setParameterValue("firstName", "maxi");
        getWicketTester().getRequest().getPostParameters().setParameterValue("lastName", "gegel");
        getWicketTester().getRequest().getPostParameters().setParameterValue("link", "socialLink");
        getWicketTester().startPage(FacebookAppNickName.class);
        getWicketTester().assertNoErrorMessage();
        getWicketTester().assertRenderedPage(FacebookAppNickName.class);
        // Enter valid nickname
        Assert.assertTrue(getWicketTester().getComponentFromLastRenderedPage("nickNameForm:name").getDefaultModelObject().toString().startsWith("maxgeg"));
        getWicketTester().debugComponentTrees();
        FormTester formTester = getWicketTester().newFormTester("nickNameForm");
        formTester.setValue("name", "abced");
        formTester.submit("goButton");
        getWicketTester().assertRenderedPage(Game.class);
        Page gamePage = getWicketTester().getLastRenderedPage();
        Assert.assertEquals(TEST_LEVEL_TASK_1_1_SIMULATED_ID, gamePage.getPageParameters().get("taskId").toInt());
        User user = getUser();
        Assert.assertEquals(User.SocialNet.FACEBOOK, user.getSocialNet());
        Assert.assertEquals("abced", user.getUsername());
        Assert.assertEquals("100003634094139", user.getSocialNetUserId());
        Assert.assertEquals("ssdsd@fdfdfd.com", user.getEmail());
        Assert.assertEquals("socialLink", user.getSocialNetUserLink());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // verify tracker
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        assertPageAccessCount(2);
        assertPageAccess(0, FacebookAppNickName.class, "---User Authorized by Facebook but NOT registered by Game--- Parameters: ");
        assertPageAccess(1, Game.class, "LevelTaskId=1");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void registeredByFacebookUnregisteredByGameRedirect() throws Exception {
        configureMultiplePlanetsAndLevels();
        // Prepare
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage facebookNickname = pageCrud.createDbChild();
        facebookNickname.setPredefinedType(CmsUtil.CmsPredefinedPage.FACEBOOK_NICKNAME);
        facebookNickname.setName("facebook nickname");
        pageCrud.updateDbChild(facebookNickname);
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // call nickname page
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        FacebookSignedRequest facebookSignedRequest = new FacebookSignedRequest("", 1, new FacebookUser("CH", "de", new FacebookAge(33)), "xx", "qayxswedc");
        facebookSignedRequest.setEmail("iguhdfsgfd@sdklufh.com");
        facebookSignedRequest.setLastName("dasfsdf");
        facebookSignedRequest.setFirstName("mokinju");
        facebookSignedRequest.setLink("ufgbsdauofbgudsahzbfuodsbf");
        getWicketTester().startPage(new FacebookAppNickName(new PageParameters(), facebookSignedRequest));
        getWicketTester().assertNoErrorMessage();
        getWicketTester().assertRenderedPage(FacebookAppNickName.class);
        // Enter valid nickname
        Assert.assertTrue(getWicketTester().getComponentFromLastRenderedPage("nickNameForm:name").getDefaultModelObject().toString().startsWith("mokdas"));
        FormTester formTester = getWicketTester().newFormTester("nickNameForm");
        formTester.setValue("name", "abced");
        formTester.submit("goButton");
        getWicketTester().assertRenderedPage(Game.class);
        Page gamePage = getWicketTester().getLastRenderedPage();
        Assert.assertEquals(TEST_LEVEL_TASK_1_1_SIMULATED_ID, gamePage.getPageParameters().get("taskId").toInt());
        User user = getUser();
        Assert.assertEquals(User.SocialNet.FACEBOOK, user.getSocialNet());
        Assert.assertEquals("abced", user.getUsername());
        Assert.assertEquals("qayxswedc", user.getSocialNetUserId());
        Assert.assertEquals("iguhdfsgfd@sdklufh.com", user.getEmail());
        Assert.assertEquals("ufgbsdauofbgudsahzbfuodsbf", user.getSocialNetUserLink());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // verify tracker
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        assertPageAccessCount(2);
        assertPageAccess(0, FacebookAppNickName.class, "---User Authorized by Facebook but NOT registered by Game--- Parameters: ");
        assertPageAccess(1, Game.class, "LevelTaskId=1");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void invalidNickNameAndSendEn() throws Exception {
        configureMultiplePlanetsAndLevels();
        createUsers();
        // Do not rejoice too quickly... this is just a test secret.
        setPrivateField(CmsUiServiceImpl.class, cmsUiService, "facebookAppSecret", "029a30fb9677d35c79c44d8a505d8fe1");
        // Prepare
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage facebookNickname = pageCrud.createDbChild();
        facebookNickname.setPredefinedType(CmsUtil.CmsPredefinedPage.FACEBOOK_NICKNAME);
        facebookNickname.setName("facebook nickname");
        pageCrud.updateDbChild(facebookNickname);
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().getSession().setLocale(Locale.ENGLISH);
        getWicketTester().getRequest().getPostParameters().setParameterValue("signed_request", "v3-O8s1WrS9B2XnYXpRo61n2hKc9wboofRDHOxcF8XI.eyJhbGdvcml0aG0iOiJITUFDLVNIQTI1NiIsImV4cGlyZXMiOjEzNDMxNTI4MDAsImlzc3VlZF9hdCI6MTM0MzE0NjY4Mywib2F1dGhfdG9rZW4iOiJBQUFFa3RlWVZ1WkNNQkFDS29mOGpkWDMxcnVTWkN3RXFuRnFWd3Z2NnBBNldNMTVaQ1V6bzlRNmliUXJiWGtRVkJOeEF0UDJmc2EzVzY3ZXJITW5EWkFvNlZHRzVPajg4U2FJMWZOYkVyYjhCeDBuOURRWkIyIiwidXNlciI6eyJjb3VudHJ5IjoiY2giLCJsb2NhbGUiOiJlbl9VUyIsImFnZSI6eyJtaW4iOjIxfX0sInVzZXJfaWQiOiIxMDAwMDM2MzQwOTQxMzkifQ");
        getWicketTester().startPage(FacebookAppNickName.class);
        // Enter No nickname
        FormTester formTester = getWicketTester().newFormTester("nickNameForm");
        formTester.setValue("name", "");
        formTester.submit("goButton");
        getWicketTester().debugComponentTrees();
        getWicketTester().assertRenderedPage(FacebookAppNickName.class);
        getWicketTester().assertLabel("nickNameForm:feedback:feedbackul:messages:0:message", "Name must have at least 3 characters");
        Assert.assertNull(getUser());
        // Enter too short nickname
        formTester = getWicketTester().newFormTester("nickNameForm");
        formTester.setValue("name", "x");
        formTester.submit("goButton");
        getWicketTester().assertRenderedPage(FacebookAppNickName.class);
        getWicketTester().assertLabel("nickNameForm:feedback:feedbackul:messages:0:message", "Name must have at least 3 characters");
        Assert.assertNull(getUser());
        // Enter existing nickname
        formTester = getWicketTester().newFormTester("nickNameForm");
        formTester.setValue("name", "qayxsw");
        formTester.submit("goButton");
        getWicketTester().assertRenderedPage(FacebookAppNickName.class);
        getWicketTester().assertLabel("nickNameForm:feedback:feedbackul:messages:0:message", "Name has already been taken");
        Assert.assertNull(getUser());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void invalidNickNameAndSendDe() throws Exception {
        configureMultiplePlanetsAndLevels();
        createUsers();
        // Do not rejoice too quickly... this is just a test secret.
        setPrivateField(CmsUiServiceImpl.class, cmsUiService, "facebookAppSecret", "029a30fb9677d35c79c44d8a505d8fe1");
        // Prepare
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage facebookNickname = pageCrud.createDbChild();
        facebookNickname.setPredefinedType(CmsUtil.CmsPredefinedPage.FACEBOOK_NICKNAME);
        facebookNickname.setName("facebook nickname");
        pageCrud.updateDbChild(facebookNickname);
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().getSession().setLocale(Locale.GERMAN);
        getWicketTester().getRequest().getPostParameters().setParameterValue("signed_request", "v3-O8s1WrS9B2XnYXpRo61n2hKc9wboofRDHOxcF8XI.eyJhbGdvcml0aG0iOiJITUFDLVNIQTI1NiIsImV4cGlyZXMiOjEzNDMxNTI4MDAsImlzc3VlZF9hdCI6MTM0MzE0NjY4Mywib2F1dGhfdG9rZW4iOiJBQUFFa3RlWVZ1WkNNQkFDS29mOGpkWDMxcnVTWkN3RXFuRnFWd3Z2NnBBNldNMTVaQ1V6bzlRNmliUXJiWGtRVkJOeEF0UDJmc2EzVzY3ZXJITW5EWkFvNlZHRzVPajg4U2FJMWZOYkVyYjhCeDBuOURRWkIyIiwidXNlciI6eyJjb3VudHJ5IjoiY2giLCJsb2NhbGUiOiJlbl9VUyIsImFnZSI6eyJtaW4iOjIxfX0sInVzZXJfaWQiOiIxMDAwMDM2MzQwOTQxMzkifQ");
        getWicketTester().startPage(FacebookAppNickName.class);
        // Enter No nickname
        FormTester formTester = getWicketTester().newFormTester("nickNameForm");
        formTester.setValue("name", "");
        formTester.submit("goButton");
        getWicketTester().assertRenderedPage(FacebookAppNickName.class);
        getWicketTester().assertLabel("nickNameForm:feedback:feedbackul:messages:0:message", "Name muss mindestens 3 Zeichen lang sein");
        Assert.assertNull(getUser());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void invalidNickNameAndSendCN() throws Exception {
        configureMultiplePlanetsAndLevels();
        createUsers();
        // Do not rejoice too quickly... this is just a test secret.
        setPrivateField(CmsUiServiceImpl.class, cmsUiService, "facebookAppSecret", "029a30fb9677d35c79c44d8a505d8fe1");
        // Prepare
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage facebookNickname = pageCrud.createDbChild();
        facebookNickname.setPredefinedType(CmsUtil.CmsPredefinedPage.FACEBOOK_NICKNAME);
        facebookNickname.setName("facebook nickname");
        pageCrud.updateDbChild(facebookNickname);
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().getSession().setLocale(Locale.CHINESE);
        getWicketTester().getRequest().getPostParameters().setParameterValue("signed_request", "v3-O8s1WrS9B2XnYXpRo61n2hKc9wboofRDHOxcF8XI.eyJhbGdvcml0aG0iOiJITUFDLVNIQTI1NiIsImV4cGlyZXMiOjEzNDMxNTI4MDAsImlzc3VlZF9hdCI6MTM0MzE0NjY4Mywib2F1dGhfdG9rZW4iOiJBQUFFa3RlWVZ1WkNNQkFDS29mOGpkWDMxcnVTWkN3RXFuRnFWd3Z2NnBBNldNMTVaQ1V6bzlRNmliUXJiWGtRVkJOeEF0UDJmc2EzVzY3ZXJITW5EWkFvNlZHRzVPajg4U2FJMWZOYkVyYjhCeDBuOURRWkIyIiwidXNlciI6eyJjb3VudHJ5IjoiY2giLCJsb2NhbGUiOiJlbl9VUyIsImFnZSI6eyJtaW4iOjIxfX0sInVzZXJfaWQiOiIxMDAwMDM2MzQwOTQxMzkifQ");
        getWicketTester().startPage(FacebookAppNickName.class);
        // Enter No nickname
        FormTester formTester = getWicketTester().newFormTester("nickNameForm");
        formTester.setValue("name", "");
        formTester.submit("goButton");
        getWicketTester().assertRenderedPage(FacebookAppNickName.class);
        getWicketTester().assertLabel("nickNameForm:feedback:feedbackul:messages:0:message", "Name must have at least 3 characters");
        Assert.assertNull(getUser());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void nickNameAjaxEn() throws Exception {
        configureMultiplePlanetsAndLevels();
        createUsers();
        // Do not rejoice too quickly... this is just a test secret.
        setPrivateField(CmsUiServiceImpl.class, cmsUiService, "facebookAppSecret", "029a30fb9677d35c79c44d8a505d8fe1");
        // Prepare
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage facebookNickname = pageCrud.createDbChild();
        facebookNickname.setPredefinedType(CmsUtil.CmsPredefinedPage.FACEBOOK_NICKNAME);
        facebookNickname.setName("facebook nickname");
        pageCrud.updateDbChild(facebookNickname);
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().getSession().setLocale(Locale.ENGLISH);
        getWicketTester().getRequest().getPostParameters().setParameterValue("signed_request", "v3-O8s1WrS9B2XnYXpRo61n2hKc9wboofRDHOxcF8XI.eyJhbGdvcml0aG0iOiJITUFDLVNIQTI1NiIsImV4cGlyZXMiOjEzNDMxNTI4MDAsImlzc3VlZF9hdCI6MTM0MzE0NjY4Mywib2F1dGhfdG9rZW4iOiJBQUFFa3RlWVZ1WkNNQkFDS29mOGpkWDMxcnVTWkN3RXFuRnFWd3Z2NnBBNldNMTVaQ1V6bzlRNmliUXJiWGtRVkJOeEF0UDJmc2EzVzY3ZXJITW5EWkFvNlZHRzVPajg4U2FJMWZOYkVyYjhCeDBuOURRWkIyIiwidXNlciI6eyJjb3VudHJ5IjoiY2giLCJsb2NhbGUiOiJlbl9VUyIsImFnZSI6eyJtaW4iOjIxfX0sInVzZXJfaWQiOiIxMDAwMDM2MzQwOTQxMzkifQ");
        getWicketTester().startPage(FacebookAppNickName.class);
        // Enter too short nickname
        FormTester formTester = getWicketTester().newFormTester("nickNameForm");
        formTester.setValue("name", "x");
        getWicketTester().executeAjaxEvent("nickNameForm:name", "onkeyup");
        getWicketTester().assertLabel("nickNameForm:feedback:feedbackul:messages:0:message", "Name must have at least 3 characters");
        Assert.assertNull(getUser());
        // Enter existing nickname
        formTester = getWicketTester().newFormTester("nickNameForm");
        formTester.setValue("name", "qayxsw");
        getWicketTester().executeAjaxEvent("nickNameForm:name", "onkeyup");
        getWicketTester().assertLabel("nickNameForm:feedback:feedbackul:messages:0:message", "Name has already been taken");
        Assert.assertNull(getUser());
        // Valid nick name
        formTester = getWicketTester().newFormTester("nickNameForm");
        formTester.setValue("name", "hallo");
        getWicketTester().executeAjaxEvent("nickNameForm:name", "onkeyup");
        getWicketTester().assertNoErrorMessage();
        Assert.assertNull(getUser());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    private void createUsers() {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createUser("U1", "xxx");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createUser("qayxsw", "xxx");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    private void dumpPageAccess() {
        System.out.println("-------------------------------------------------------------------");
        for (DbPageAccess dbPageAccess : loadAll(DbPageAccess.class)) {
            System.out.println(dbPageAccess);
        }
        System.out.println("-------------------------------------------------------------------");
    }

    private void assertPageAccessCount(int count) {
        Assert.assertEquals(count, loadAll(DbPageAccess.class).size());
    }

    private void assertPageAccess(int index, Class expectedClazz, String expectedAdditional) {
        DbPageAccess dbPageAccess = loadAll(DbPageAccess.class).get(index);
        Assert.assertEquals(expectedClazz.getName(), dbPageAccess.getPage());
        Assert.assertEquals(expectedAdditional, dbPageAccess.getAdditional());
    }

    @Test
    @DirtiesContext
    public void testCmsPageSetup() throws Exception {
        configureMultiplePlanetsAndLevels();
        // Prepare predefined facebook site
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // CSS
        CrudRootServiceHelper<DbPageStyle> styleCrud = cmsService.getPageStyleCrudRootServiceHelper();
        DbPageStyle dbPageStyle = styleCrud.createDbChild();
        dbPageStyle.setCss("CSS STRING");
        dbPageStyle.setName("Main Style");
        styleCrud.updateDbChild(dbPageStyle);
        // Page
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage facebookApp = pageCrud.createDbChild();
        facebookApp.getDbI18nName().putString("Main Page");
        facebookApp.setPredefinedType(CmsUtil.CmsPredefinedPage.FACEBOOK_NICKNAME);
        facebookApp.setName("facebookNickname");
        facebookApp.setStyle(dbPageStyle);
        pageCrud.updateDbChild(facebookApp);
        // Content
        DbContentStaticHtml dbContentStaticHtml = new DbContentStaticHtml();
        dbContentStaticHtml.getDbI18nHtml().putString("Static text 1");
        facebookApp.setContentAndAccessWrites(dbContentStaticHtml);
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Test
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().startPage(new FacebookAppNickName(new PageParameters(), new FacebookSignedRequest("", 1, new FacebookUser("", "", new FacebookAge(20)), "", "")));
        getWicketTester().assertNoErrorMessage();
        getWicketTester().debugComponentTrees();
        getWicketTester().assertLabel("title", "Main Page");
        getWicketTester().assertLabel("form:content", "Static text 1");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }
}
