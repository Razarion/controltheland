package com.btxtech.game.wicket.pages;

import com.btxtech.game.jsre.common.CmsUtil;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.cms.CmsService;
import com.btxtech.game.services.cms.layout.DbContentStaticHtml;
import com.btxtech.game.services.cms.page.DbPage;
import com.btxtech.game.services.cms.page.DbPageStyle;
import com.btxtech.game.services.common.CrudRootServiceHelper;
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
    private static final String OAUTH_DIALOG = "<div wicket:id=\"facebook\"><wicket:panel>\n" +
            "    <div id=\"fb-root\"></div>\n" +
            "    <script wicket:id=\"facebookJsSkd\" type=\"text/javascript\">window.fbAsyncInit = function () {\n" +
            "    FB.init({\n" +
            "        appId: '321838644575219', // App ID\n" +
            "        channelUrl: '//www.razarion.com/FacebookChannelFile.html', // Channel File\n" +
            "        status: true, // check login status\n" +
            "        cookie: true, // enable cookies to allow the server to access the session\n" +
            "        xfbml: true  // parse XFBML\n" +
            "    });\n" +
            "\n" +
            "\n" +
            "    FB.login(function (loginResponse) {\n" +
            "        if (loginResponse.authResponse) {\n" +
            "            FB.api('/me', function (apiResponse) {\n" +
            "                try {\n" +
            "                    var form = document.createElement('form');\n" +
            "                    form.setAttribute('method', 'post');\n" +
            "                    form.setAttribute('action', '/game_cms_choose_nick_name');\n" +
            "                    var signedRequestField = document.createElement('input');\n" +
            "                    signedRequestField.setAttribute('type', 'hidden');\n" +
            "                    signedRequestField.setAttribute('name', 'signed_request');\n" +
            "                    signedRequestField.setAttribute(\"value\", loginResponse.authResponse.signedRequest);\n" +
            "                    form.appendChild(signedRequestField);\n" +
            "                    var linkField = document.createElement('input');\n" +
            "                    linkField.setAttribute('type', 'hidden');\n" +
            "                    linkField.setAttribute('name', 'link');\n" +
            "                    linkField.setAttribute(\"value\", apiResponse.link);\n" +
            "                    form.appendChild(linkField);\n" +
            "                    var firstNameField = document.createElement('input');\n" +
            "                    firstNameField.setAttribute('type', 'hidden');\n" +
            "                    firstNameField.setAttribute('name', 'firstName');\n" +
            "                    firstNameField.setAttribute(\"value\", apiResponse.first_name);\n" +
            "                    form.appendChild(firstNameField);\n" +
            "                    var lastNameField = document.createElement('input');\n" +
            "                    lastNameField.setAttribute('type', 'hidden');\n" +
            "                    lastNameField.setAttribute('name', 'lastName');\n" +
            "                    lastNameField.setAttribute(\"value\", apiResponse.last_name);\n" +
            "                    form.appendChild(lastNameField);\n" +
            "                    var emailField = document.createElement('input');\n" +
            "                    emailField.setAttribute('type', 'hidden');\n" +
            "                    emailField.setAttribute('name', 'email');\n" +
            "                    emailField.setAttribute(\"value\", apiResponse.email);\n" +
            "                    form.appendChild(emailField);\n" +
            "                    document.body.appendChild(form);\n" +
            "                    form.submit();\n" +
            "                } catch (e) {\n" +
            "                    errorMessage = encodeURI('FacbookOAuthDialog.js exception:' + e);\n" +
            "                    pathname = encodeURI(window.location.pathname);\n" +
            "                    var img = document.createElement('img');\n" +
            "                    img.src = '/spring/lsc?e=' + errorMessage + '&t=' + new Date().getTime() + '&p=' + pathname;\n" +
            "                    document.body.appendChild(img);\n" +
            "                }\n" +
            "            });\n" +
            "        } else {\n" +
            "            window.location.href = '/game_cms_choose_nick_name?error=access_denied';\n" +
            "        }\n" +
            "    }, {scope: 'email'});\n" +
            "};\n" +
            "\n" +
            "// Load the SDK Asynchronously\n" +
            "(function (d) {\n" +
            "    var js, id = 'facebook-jssdk', ref = d.getElementsByTagName('script')[0];\n" +
            "    if (d.getElementById(id)) {\n" +
            "        return;\n" +
            "    }\n" +
            "    js = d.createElement('script');\n" +
            "    js.id = id;\n" +
            "    js.async = true;\n" +
            "    js.src = \"//connect.facebook.net/en_US/all.js\";\n" +
            "    ref.parentNode.insertBefore(js, ref);\n" +
            "}(document));</script>\n" +
            "</wicket:panel></div>";

    @Autowired
    private CmsUiService cmsUiService;
    @Autowired
    private CmsService cmsService;

    @Test
    @DirtiesContext
    public void newUserOAuth() throws Exception {
        // Must run with '-Dtestmode=true' due to 'wicket:xxx' namespace
        configureMultiplePlanetsAndLevels();
        // Prepare predefined facebook site
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage facebookApp = pageCrud.createDbChild();
        facebookApp.setPredefinedType(CmsUtil.CmsPredefinedPage.FACEBOOK_START);
        facebookApp.setName("facebookApp");
        pageCrud.updateDbChild(facebookApp);
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Do not rejoice too quickly Just... this is just a test secret.
        setPrivateField(CmsUiServiceImpl.class, cmsUiService, "facebookAppSecret", "029a30fb9677d35c79c44d8a505d8fe1");

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // First authorization show OAuth dialog
        getWicketTester().getRequest().getPostParameters().setParameterValue("signed_request", "3RaYyXwkwhCc4OlVfDhAU9Y-O_pyqN4mgE7JyzPwcIc.eyJhbGdvcml0aG0iOiJITUFDLVNIQTI1NiIsImlzc3VlZF9hdCI6MTM0MzE0NjYzOSwidXNlciI6eyJjb3VudHJ5IjoiY2giLCJsb2NhbGUiOiJlbl9VUyIsImFnZSI6eyJtaW4iOjIxfX19");
        getWicketTester().startPage(FacebookAppStart.class);
        getWicketTester().assertRenderedPage(FacebookAppStart.class);
        assertStringInBody(OAUTH_DIALOG);
        Assert.assertNull(getUser());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void registeredByFacebookRegisteredByGame() throws Exception {
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
        // Login
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().getRequest().getPostParameters().setParameterValue("signed_request", "v3-O8s1WrS9B2XnYXpRo61n2hKc9wboofRDHOxcF8XI.eyJhbGdvcml0aG0iOiJITUFDLVNIQTI1NiIsImV4cGlyZXMiOjEzNDMxNTI4MDAsImlzc3VlZF9hdCI6MTM0MzE0NjY4Mywib2F1dGhfdG9rZW4iOiJBQUFFa3RlWVZ1WkNNQkFDS29mOGpkWDMxcnVTWkN3RXFuRnFWd3Z2NnBBNldNMTVaQ1V6bzlRNmliUXJiWGtRVkJOeEF0UDJmc2EzVzY3ZXJITW5EWkFvNlZHRzVPajg4U2FJMWZOYkVyYjhCeDBuOURRWkIyIiwidXNlciI6eyJjb3VudHJ5IjoiY2giLCJsb2NhbGUiOiJlbl9VUyIsImFnZSI6eyJtaW4iOjIxfX0sInVzZXJfaWQiOiIxMDAwMDM2MzQwOTQxMzkifQ");
        getWicketTester().getRequest().getPostParameters().setParameterValue("email", "fakeEmail"); // Prevent FB api call
        getWicketTester().startPage(FacebookAppStart.class);
        getWicketTester().assertRenderedPage(FacebookAppNickName.class);
        Assert.assertNull(getUser());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void emptySignedRequest() throws Exception {
        // Must run with '-Dtestmode=true' due to 'wicket:xxx' namespace
        configureMultiplePlanetsAndLevels();
        // Prepare predefined facebook site
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage facebookApp = pageCrud.createDbChild();
        facebookApp.setPredefinedType(CmsUtil.CmsPredefinedPage.FACEBOOK_START);
        facebookApp.setName("facebookApp");
        pageCrud.updateDbChild(facebookApp);
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Test
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().startPage(FacebookAppStart.class);
        getWicketTester().assertRenderedPage(FacebookAppStart.class);
        assertStringInBody(OAUTH_DIALOG);
        Assert.assertNull(getUser());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void emptySignedRequestAndLoggedIn() throws Exception {
        configureMultiplePlanetsAndLevels();
        // Test
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("Test");
        getWicketTester().getSession().setLocale(Locale.ENGLISH);
        getWicketTester().startPage(FacebookAppStart.class);
        getWicketTester().assertRenderedPage(FacebookAppStart.class);
        getWicketTester().assertErrorMessages("You are already logged in as Test. Please logout before you login via Facebook");
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
        DbPage facebookApp = pageCrud.createDbChild();
        facebookApp.setPredefinedType(CmsUtil.CmsPredefinedPage.FACEBOOK_START);
        facebookApp.setName("facebookApp");
        pageCrud.updateDbChild(facebookApp);
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
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
        // Prepare predefined facebook site
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage facebookApp = pageCrud.createDbChild();
        facebookApp.setPredefinedType(CmsUtil.CmsPredefinedPage.FACEBOOK_START);
        facebookApp.setName("facebookApp");
        pageCrud.updateDbChild(facebookApp);
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
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
        Assert.assertEquals(1, dbPageAccesses.size());
        Assert.assertEquals(FacebookAppStart.class.getName(), dbPageAccesses.get(0).getPage());
        Assert.assertEquals("---User NOT Authorized by Facebook---", dbPageAccesses.get(0).getAdditional());
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
        Assert.assertEquals("---User NOT Authorized by Facebook but logged in with different user--- Parameters: ", dbPageAccesses.get(0).getAdditional());
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
        // Page
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage facebookApp = pageCrud.createDbChild();
        facebookApp.setPredefinedType(CmsUtil.CmsPredefinedPage.FACEBOOK_NICKNAME);
        pageCrud.updateDbChild(facebookApp);
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
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
        facebookApp.setPredefinedType(CmsUtil.CmsPredefinedPage.FACEBOOK_START);
        facebookApp.setName("facebookApp");
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
        getWicketTester().startPage(FacebookAppStart.class);
        getWicketTester().assertNoErrorMessage();
        getWicketTester().debugComponentTrees();
        getWicketTester().assertLabel("title", "Main Page");
        getWicketTester().assertLabel("form:content", "Static text 1");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

    }
}
