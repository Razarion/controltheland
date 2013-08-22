package com.btxtech.game.services.cms;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.ClientDateUtil;
import com.btxtech.game.jsre.common.CmsUtil;
import com.btxtech.game.jsre.common.gameengine.services.PlanetInfo;
import com.btxtech.game.jsre.common.gameengine.services.user.PasswordNotMatchException;
import com.btxtech.game.jsre.common.gameengine.services.user.UserAlreadyExistsException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.TestPlanetHelper;
import com.btxtech.game.services.cms.layout.DbContent;
import com.btxtech.game.services.cms.layout.DbContentBook;
import com.btxtech.game.services.cms.layout.DbContentBooleanExpressionImage;
import com.btxtech.game.services.cms.layout.DbContentContainer;
import com.btxtech.game.services.cms.layout.DbContentDetailLink;
import com.btxtech.game.services.cms.layout.DbContentGameLink;
import com.btxtech.game.services.cms.layout.DbContentInvoker;
import com.btxtech.game.services.cms.layout.DbContentInvokerButton;
import com.btxtech.game.services.cms.layout.DbContentList;
import com.btxtech.game.services.cms.layout.DbContentPlugin;
import com.btxtech.game.services.cms.layout.DbContentRow;
import com.btxtech.game.services.cms.layout.DbContentSmartPageLink;
import com.btxtech.game.services.cms.layout.DbContentStaticHtml;
import com.btxtech.game.services.cms.layout.DbExpressionProperty;
import com.btxtech.game.services.cms.page.DbAds;
import com.btxtech.game.services.cms.page.DbPage;
import com.btxtech.game.services.common.CrudListChildServiceHelper;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.messenger.InvalidFieldException;
import com.btxtech.game.services.messenger.MessengerService;
import com.btxtech.game.services.planet.Base;
import com.btxtech.game.services.planet.BaseService;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.planet.impl.ServerPlanetServicesImpl;
import com.btxtech.game.services.statistics.StatisticsService;
import com.btxtech.game.services.statistics.impl.StatisticsServiceImpl;
import com.btxtech.game.services.user.DbForgotPassword;
import com.btxtech.game.services.user.RegisterService;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.wicket.pages.Game;
import com.btxtech.game.wicket.pages.cms.CmsPage;
import com.btxtech.game.wicket.pages.cms.CmsStringGenerator;
import com.btxtech.game.wicket.pages.cms.content.plugin.PluginEnum;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.commons.lang.mutable.MutableBoolean;
import org.apache.wicket.Page;
import org.apache.wicket.core.request.handler.RenderPageRequestHandler;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.protocol.http.mock.MockHttpServletResponse;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.cycle.AbstractRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.tester.FormTester;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import javax.servlet.http.Cookie;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * User: beat
 * Date: 04.06.2011
 * Time: 12:37:56
 */
public class TestCmsService2 extends AbstractServiceTest {
    @Autowired
    private CmsService cmsService;
    @Autowired
    private CmsUiService cmsUiService;
    @Autowired
    private UserService userService;
    @Autowired
    private MessengerService messengerService;
    @Autowired
    private UserGuidanceService userGuidanceService;
    @Autowired
    private StatisticsService statisticsService;
    @Autowired
    private PlanetSystemService planetSystemService;
    @Autowired
    private RegisterService registerService;
    @Autowired
    private com.btxtech.game.services.connection.Session session;

    @Test
    @DirtiesContext
    public void testLoginNoUser() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        // Home
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage.setName("Home");
        pageCrud.updateDbChild(dbPage);
        // Login failed
        DbPage loginFailedPage = pageCrud.createDbChild();
        loginFailedPage.setPredefinedType(CmsUtil.CmsPredefinedPage.LOGIN_FAILED);
        loginFailedPage.setName("LoginFailed");
        DbContentPlugin contentPlugin = new DbContentPlugin();
        contentPlugin.setPluginEnum(PluginEnum.LOGIN_FAILED);
        contentPlugin.init(userService);
        loginFailedPage.setContentAndAccessWrites(contentPlugin);
        pageCrud.updateDbChild(loginFailedPage);
        // Forgot password
        DbPage forgotPassword = pageCrud.createDbChild();
        forgotPassword.setPredefinedType(CmsUtil.CmsPredefinedPage.FORGOT_PASSWORD_REQUEST);
        forgotPassword.setName("ForgotPassword");
        pageCrud.updateDbChild(forgotPassword);
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // getWicketTester().setupRequestAndResponse();
        getWicketTester().getSession().setLocale(Locale.ENGLISH);
        getWicketTester().startPage(CmsPage.class);
        FormTester formTester = getWicketTester().newFormTester("header:loginBox:loginForm");
        formTester.setValue("loginName", "U1");
        formTester.setValue("loginPassword", "xxx");
        formTester.submit();
        getWicketTester().debugComponentTrees();
        getWicketTester().assertLabel("form:content:text", "Login failed. Please try again.");
        getWicketTester().assertVisible("form:content:forgotPassword");
        PageParameters pageParameters = new PageParameters();
        pageParameters.set("page", 3);
        getWicketTester().assertBookmarkablePageLink("form:content:forgotPassword", CmsPage.class, pageParameters);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testLoginWrongPassword() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createUser("U1", "aaaa", "");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        // Home
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage.setName("Home");
        pageCrud.updateDbChild(dbPage);
        // Login failed
        DbPage loginFailedPage = pageCrud.createDbChild();
        loginFailedPage.setPredefinedType(CmsUtil.CmsPredefinedPage.LOGIN_FAILED);
        loginFailedPage.setName("LoginFailed");
        DbContentPlugin contentPlugin = new DbContentPlugin();
        contentPlugin.setPluginEnum(PluginEnum.LOGIN_FAILED);
        contentPlugin.init(userService);
        loginFailedPage.setContentAndAccessWrites(contentPlugin);
        pageCrud.updateDbChild(loginFailedPage);
        // Forgot password
        DbPage forgotPassword = pageCrud.createDbChild();
        forgotPassword.setPredefinedType(CmsUtil.CmsPredefinedPage.FORGOT_PASSWORD_REQUEST);
        forgotPassword.setName("ForgotPassword");
        pageCrud.updateDbChild(forgotPassword);
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // getWicketTester().setupRequestAndResponse();
        getWicketTester().getSession().setLocale(Locale.ENGLISH);
        getWicketTester().startPage(CmsPage.class);
        FormTester formTester = getWicketTester().newFormTester("header:loginBox:loginForm");
        formTester.setValue("loginName", "U1");
        formTester.setValue("loginPassword", "xxx");
        formTester.submit();
        getWicketTester().debugComponentTrees();
        getWicketTester().assertLabel("form:content:text", "Login failed. Please try again.");
        getWicketTester().assertVisible("form:content:forgotPassword");
        PageParameters pageParameters = new PageParameters();
        pageParameters.set("page", 3);
        getWicketTester().assertBookmarkablePageLink("form:content:forgotPassword", CmsPage.class, pageParameters);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testLoginNotVerified() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        registerService.register("U1", "xxx", "xxx", "fake");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage.setName("Home");
        pageCrud.updateDbChild(dbPage);
        // Login failed
        DbPage loginFailedPage = pageCrud.createDbChild();
        loginFailedPage.setPredefinedType(CmsUtil.CmsPredefinedPage.LOGIN_FAILED);
        loginFailedPage.setName("LoginFailed");
        DbContentPlugin contentPlugin = new DbContentPlugin();
        contentPlugin.setPluginEnum(PluginEnum.LOGIN_FAILED);
        contentPlugin.init(userService);
        loginFailedPage.setContentAndAccessWrites(contentPlugin);
        pageCrud.updateDbChild(loginFailedPage);
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // getWicketTester().setupRequestAndResponse();
        getWicketTester().getSession().setLocale(Locale.ENGLISH);
        getWicketTester().startPage(CmsPage.class);
        FormTester formTester = getWicketTester().newFormTester("header:loginBox:loginForm");
        formTester.setValue("loginName", "U1");
        formTester.setValue("loginPassword", "xxx");
        formTester.submit();
        getWicketTester().debugComponentTrees();
        getWicketTester().assertLabel("form:content:text", "This account has not been confirmed by email. Please check your email and click the confirmation link.");
        getWicketTester().assertInvisible("form:content:forgotPassword");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testLogin() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createUser("U1", "xxx");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage.setName("Home2");
        pageCrud.updateDbChild(dbPage);
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().startPage(CmsPage.class);
        FormTester formTester = getWicketTester().newFormTester("header:loginBox:loginForm");
        formTester.setValue("loginName", "U1");
        formTester.setValue("loginPassword", "xxx");
        formTester.submit();
        getWicketTester().assertRenderedPage(Game.class);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testInGameLogin() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createUser("U1", "xxx");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage.setName("Home2");
        pageCrud.updateDbChild(dbPage);
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Test
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertVisible("header:loginBox:loginForm:loginName");
        getWicketTester().assertVisible("header:loginBox:loginForm:loginPassword");
        getWicketTester().assertInvisible("header:loggedinBox:loginForm:nameLink");
        getMovableService().login("U1", "xxx");
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertInvisible("header:loginBox:loginForm:loginName");
        getWicketTester().assertInvisible("header:loginBox:loginForm:loginPassword");
        getWicketTester().assertVisible("header:loggedinBox:loginForm:nameLink:name");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }


    @Test
    @DirtiesContext
    public void testInGameLogout() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createUser("U1", "xxx");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage.setName("Home2");
        pageCrud.updateDbChild(dbPage);
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().startPage(CmsPage.class);
        FormTester formTester = getWicketTester().newFormTester("header:loginBox:loginForm");
        formTester.setValue("loginName", "U1");
        formTester.setValue("loginPassword", "xxx");
        formTester.submit();
        getWicketTester().assertRenderedPage(Game.class);
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertInvisible("header:loginBox:loginForm:loginName");
        getWicketTester().assertInvisible("header:loginBox:loginForm:loginPassword");
        getWicketTester().assertVisible("header:loggedinBox:loginForm:nameLink:name");
        getMovableService().logout();
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertVisible("header:loginBox:loginForm:loginName");
        getWicketTester().assertVisible("header:loginBox:loginForm:loginPassword");
        getWicketTester().assertInvisible("header:loggedinBox:loginForm:nameLink");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testInGameRegister() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage.setName("Home2");
        pageCrud.updateDbChild(dbPage);
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertVisible("header:loginBox:loginForm:loginName");
        getWicketTester().assertVisible("header:loginBox:loginForm:loginPassword");
        getWicketTester().assertInvisible("header:loggedinBox:loginForm:nameLink");
        getMovableService().register("U1", "xxx", "xxx", "");
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertVisible("header:loginBox:loginForm:loginName");
        getWicketTester().assertVisible("header:loginBox:loginForm:loginPassword");
        getWicketTester().assertInvisible("header:loggedinBox:loginForm:nameLink");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testForgotPassword() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createUser("U1", "aaaa", "xxx@yyy.com");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        // Home
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage.setName("Home");
        pageCrud.updateDbChild(dbPage);
        // Login failed
        DbPage loginFailedPage = pageCrud.createDbChild();
        loginFailedPage.setPredefinedType(CmsUtil.CmsPredefinedPage.LOGIN_FAILED);
        loginFailedPage.setName("LoginFailed");
        DbContentPlugin loginFailedPlugin = new DbContentPlugin();
        loginFailedPlugin.setPluginEnum(PluginEnum.LOGIN_FAILED);
        loginFailedPlugin.init(userService);
        loginFailedPage.setContentAndAccessWrites(loginFailedPlugin);
        pageCrud.updateDbChild(loginFailedPage);
        // Forgot password
        DbPage forgotPassword = pageCrud.createDbChild();
        forgotPassword.setPredefinedType(CmsUtil.CmsPredefinedPage.FORGOT_PASSWORD_REQUEST);
        forgotPassword.setName("ForgotPassword");
        DbContentPlugin forgotPasswordPlugin = new DbContentPlugin();
        forgotPasswordPlugin.setPluginEnum(PluginEnum.FORGOT_PASSWORD_REQUEST);
        forgotPasswordPlugin.init(userService);
        forgotPassword.setContentAndAccessWrites(forgotPasswordPlugin);
        pageCrud.updateDbChild(forgotPassword);
        // Message
        DbPage dbMessagePage = pageCrud.createDbChild();
        dbMessagePage.setPredefinedType(CmsUtil.CmsPredefinedPage.MESSAGE);
        dbMessagePage.setName("Message Page");
        pageCrud.updateDbChild(dbMessagePage);
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // getWicketTester().setupRequestAndResponse();
        getWicketTester().getSession().setLocale(Locale.ENGLISH);
        getWicketTester().startPage(CmsPage.class);
        FormTester formTester = getWicketTester().newFormTester("header:loginBox:loginForm");
        formTester.setValue("loginName", "U1");
        formTester.setValue("loginPassword", "xxx");
        formTester.submit();
        getWicketTester().clickLink("form:content:forgotPassword");
        formTester = getWicketTester().newFormTester("form:content:form");
        formTester.setValue("emailField", "xxx@yyy.com");
        formTester.submit();
        getWicketTester().assertLabel("form:content:border:borderContent:message", "An email has been sent to reset the password.");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testForgotPasswordUserNotConfirmed() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUnverifiedUser("U1", "aaaa", "aaaa", "xxx@yyy.com");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        // Home
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage.setName("Home");
        pageCrud.updateDbChild(dbPage);
        // Login failed
        DbPage loginFailedPage = pageCrud.createDbChild();
        loginFailedPage.setPredefinedType(CmsUtil.CmsPredefinedPage.LOGIN_FAILED);
        loginFailedPage.setName("LoginFailed");
        DbContentPlugin loginFailedPlugin = new DbContentPlugin();
        loginFailedPlugin.setPluginEnum(PluginEnum.LOGIN_FAILED);
        loginFailedPlugin.init(userService);
        loginFailedPage.setContentAndAccessWrites(loginFailedPlugin);
        pageCrud.updateDbChild(loginFailedPage);
        // Forgot password
        DbPage forgotPassword = pageCrud.createDbChild();
        forgotPassword.setPredefinedType(CmsUtil.CmsPredefinedPage.FORGOT_PASSWORD_REQUEST);
        forgotPassword.setName("ForgotPassword");
        DbContentPlugin forgotPasswordPlugin = new DbContentPlugin();
        forgotPasswordPlugin.setPluginEnum(PluginEnum.FORGOT_PASSWORD_REQUEST);
        forgotPasswordPlugin.init(userService);
        forgotPassword.setContentAndAccessWrites(forgotPasswordPlugin);
        pageCrud.updateDbChild(forgotPassword);
        // Message
        DbPage dbMessagePage = pageCrud.createDbChild();
        dbMessagePage.setPredefinedType(CmsUtil.CmsPredefinedPage.MESSAGE);
        dbMessagePage.setName("Message Page");
        pageCrud.updateDbChild(dbMessagePage);
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // getWicketTester().setupRequestAndResponse();
        getWicketTester().getSession().setLocale(Locale.ENGLISH);
        PageParameters pageParameters = cmsUiService.getPredefinedDbPageParameters(CmsUtil.CmsPredefinedPage.FORGOT_PASSWORD_REQUEST);
        getWicketTester().startPage(CmsPage.class, pageParameters);
        FormTester formTester = getWicketTester().newFormTester("form:content:form");
        formTester.setValue("emailField", "xxx@yyy.com");
        formTester.submit();
        getWicketTester().assertLabel("form:content:border:borderContent:message", "This account has not been confirmed by email. Please check your email and click the confirmation link.");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testForgotPasswordWrongEmail() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        // Home
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage.setName("Home");
        pageCrud.updateDbChild(dbPage);
        // Login failed
        DbPage loginFailedPage = pageCrud.createDbChild();
        loginFailedPage.setPredefinedType(CmsUtil.CmsPredefinedPage.LOGIN_FAILED);
        loginFailedPage.setName("LoginFailed");
        DbContentPlugin loginFailedPlugin = new DbContentPlugin();
        loginFailedPlugin.setPluginEnum(PluginEnum.LOGIN_FAILED);
        loginFailedPlugin.init(userService);
        loginFailedPage.setContentAndAccessWrites(loginFailedPlugin);
        pageCrud.updateDbChild(loginFailedPage);
        // Forgot password
        DbPage forgotPassword = pageCrud.createDbChild();
        forgotPassword.setPredefinedType(CmsUtil.CmsPredefinedPage.FORGOT_PASSWORD_REQUEST);
        forgotPassword.setName("ForgotPassword");
        DbContentPlugin forgotPasswordPlugin = new DbContentPlugin();
        forgotPasswordPlugin.setPluginEnum(PluginEnum.FORGOT_PASSWORD_REQUEST);
        forgotPasswordPlugin.init(userService);
        forgotPassword.setContentAndAccessWrites(forgotPasswordPlugin);
        pageCrud.updateDbChild(forgotPassword);
        // Message
        DbPage dbMessagePage = pageCrud.createDbChild();
        dbMessagePage.setPredefinedType(CmsUtil.CmsPredefinedPage.MESSAGE);
        dbMessagePage.setName("Message Page");
        pageCrud.updateDbChild(dbMessagePage);
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // getWicketTester().setupRequestAndResponse();
        getWicketTester().getSession().setLocale(Locale.ENGLISH);
        getWicketTester().startPage(CmsPage.class);
        FormTester formTester = getWicketTester().newFormTester("header:loginBox:loginForm");
        formTester.setValue("loginName", "U1");
        formTester.setValue("loginPassword", "xxx");
        formTester.submit();
        getWicketTester().clickLink("form:content:forgotPassword");
        formTester = getWicketTester().newFormTester("form:content:form");
        formTester.setValue("emailField", "xxx@yyy.com");
        formTester.submit();
        getWicketTester().assertLabel("form:content:border:borderContent:message", "Unknown email address");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testForgotPasswordChange() throws Exception {
        configureSimplePlanetNoResources();

        // Prepare
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createUser("U1", "xxx", "xxx@yyy.com");
        registerService.onForgotPassword("xxx@yyy.com");
        String uuid = HibernateUtil.loadAll(getSessionFactory(), DbForgotPassword.class).get(0).getUuid();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        // Home
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage.setName("Home");
        pageCrud.updateDbChild(dbPage);
        // User
        DbPage userPage = pageCrud.createDbChild();
        userPage.setPredefinedType(CmsUtil.CmsPredefinedPage.USER_PAGE);
        userPage.setName("userPage");
        pageCrud.updateDbChild(dbPage);
        // Login failed
        DbPage forgotPasswordChangePage = pageCrud.createDbChild();
        forgotPasswordChangePage.setPredefinedType(CmsUtil.CmsPredefinedPage.FORGOT_PASSWORD_CHANGE);
        forgotPasswordChangePage.setName("testForgotPasswordChange");
        DbContentPlugin forgotPasswordChangePlugin = new DbContentPlugin();
        forgotPasswordChangePlugin.setPluginEnum(PluginEnum.FORGOT_PASSWORD_CHANGE);
        forgotPasswordChangePlugin.init(userService);
        forgotPasswordChangePage.setContentAndAccessWrites(forgotPasswordChangePlugin);
        pageCrud.updateDbChild(forgotPasswordChangePage);
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // getWicketTester().setupRequestAndResponse();
        getWicketTester().getSession().setLocale(Locale.ENGLISH);
        PageParameters pageParameters = cmsUiService.getPredefinedDbPageParameters(CmsUtil.CmsPredefinedPage.FORGOT_PASSWORD_CHANGE);
        pageParameters.set(CmsUtil.FORGOT_PASSWORD_UUID_KEY, uuid);
        getWicketTester().startPage(CmsPage.class, pageParameters);
        FormTester formTester = getWicketTester().newFormTester("form:content:form");
        formTester.setValue("password", "aaa");
        formTester.setValue("confirmPassword", "aaa");
        formTester.submit();
        getWicketTester().assertVisible("header:loggedinBox:loginForm:nameLink:name");
        getWicketTester().assertLabel("header:loggedinBox:loginForm:nameLink:name", "U1");
        Assert.assertEquals("U1", userService.getUser().getUsername());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testForgotPasswordUnknownUuid() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        // Home
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage.setName("Home");
        pageCrud.updateDbChild(dbPage);
        // User
        DbPage userPage = pageCrud.createDbChild();
        userPage.setPredefinedType(CmsUtil.CmsPredefinedPage.USER_PAGE);
        userPage.setName("userPage");
        pageCrud.updateDbChild(dbPage);
        // Message
        DbPage dbMessagePage = pageCrud.createDbChild();
        dbMessagePage.setPredefinedType(CmsUtil.CmsPredefinedPage.MESSAGE);
        dbMessagePage.setName("Message Page");
        pageCrud.updateDbChild(dbMessagePage);
        // Login failed
        DbPage forgotPasswordChangePage = pageCrud.createDbChild();
        forgotPasswordChangePage.setPredefinedType(CmsUtil.CmsPredefinedPage.FORGOT_PASSWORD_CHANGE);
        forgotPasswordChangePage.setName("testForgotPasswordChange");
        DbContentPlugin forgotPasswordChangePlugin = new DbContentPlugin();
        forgotPasswordChangePlugin.setPluginEnum(PluginEnum.FORGOT_PASSWORD_CHANGE);
        forgotPasswordChangePlugin.init(userService);
        forgotPasswordChangePage.setContentAndAccessWrites(forgotPasswordChangePlugin);
        pageCrud.updateDbChild(forgotPasswordChangePage);
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // getWicketTester().setupRequestAndResponse();
        getWicketTester().getSession().setLocale(Locale.ENGLISH);
        PageParameters pageParameters = cmsUiService.getPredefinedDbPageParameters(CmsUtil.CmsPredefinedPage.FORGOT_PASSWORD_CHANGE);
        pageParameters.set(CmsUtil.FORGOT_PASSWORD_UUID_KEY, "xxxxxxx");
        getWicketTester().startPage(CmsPage.class, pageParameters);
        FormTester formTester = getWicketTester().newFormTester("form:content:form");
        formTester.setValue("password", "aaa");
        formTester.setValue("confirmPassword", "aaa");
        formTester.submit();
        getWicketTester().assertLabel("form:content:border:borderContent:message", "This link is invalid. There is no request to reset the password.");
        Assert.assertNull(userService.getUser());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testForgotPasswordChangeNotMatch() throws Exception {
        configureSimplePlanetNoResources();

        // Prepare
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createUser("U1", "xxx", "xxx@yyy.com");
        registerService.onForgotPassword("xxx@yyy.com");
        String uuid = HibernateUtil.loadAll(getSessionFactory(), DbForgotPassword.class).get(0).getUuid();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        // Home
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage.setName("Home");
        pageCrud.updateDbChild(dbPage);
        // User
        DbPage userPage = pageCrud.createDbChild();
        userPage.setPredefinedType(CmsUtil.CmsPredefinedPage.USER_PAGE);
        userPage.setName("userPage");
        pageCrud.updateDbChild(dbPage);
        // Login failed
        DbPage forgotPasswordChangePage = pageCrud.createDbChild();
        forgotPasswordChangePage.setPredefinedType(CmsUtil.CmsPredefinedPage.FORGOT_PASSWORD_CHANGE);
        forgotPasswordChangePage.setName("testForgotPasswordChange");
        DbContentPlugin forgotPasswordChangePlugin = new DbContentPlugin();
        forgotPasswordChangePlugin.setPluginEnum(PluginEnum.FORGOT_PASSWORD_CHANGE);
        forgotPasswordChangePlugin.init(userService);
        forgotPasswordChangePage.setContentAndAccessWrites(forgotPasswordChangePlugin);
        pageCrud.updateDbChild(forgotPasswordChangePage);
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // getWicketTester().setupRequestAndResponse();
        getWicketTester().getSession().setLocale(Locale.ENGLISH);
        PageParameters pageParameters = cmsUiService.getPredefinedDbPageParameters(CmsUtil.CmsPredefinedPage.FORGOT_PASSWORD_CHANGE);
        pageParameters.set(CmsUtil.FORGOT_PASSWORD_UUID_KEY, uuid);
        getWicketTester().startPage(CmsPage.class, pageParameters);
        FormTester formTester = getWicketTester().newFormTester("form:content:form");
        formTester.setValue("password", "aaa");
        formTester.setValue("confirmPassword", "bbb");
        formTester.submit();
        getWicketTester().debugComponentTrees();
        getWicketTester().assertLabel("form:content:msgs:feedbackul:messages:0:message", "The password and confirmation password do not match.");
        Assert.assertNull(userService.getUser());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testPageTitleI18n() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage.setName("Home2");
        dbPage.getDbI18nName().putString("English");
        dbPage.getDbI18nName().putString(Locale.GERMAN, "German");
        pageCrud.updateDbChild(dbPage);
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // getWicketTester().setupRequestAndResponse();
        getWicketTester().getSession().setLocale(Locale.ENGLISH);
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertLabel("title", "English");
        getWicketTester().getSession().setLocale(Locale.GERMAN);
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertLabel("title", "German");
        getWicketTester().getSession().setLocale(Locale.CHINESE);
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertLabel("title", "English");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testCmsStringGenerator() throws Exception {
        Assert.assertEquals("Hallo 0", CmsStringGenerator.createNumberString(0, "Hallo 0", "Hallo 1", "Hallo $"));
        Assert.assertEquals("Hallo 1", CmsStringGenerator.createNumberString(1, "Hallo 0", "Hallo 1", "Hallo $"));
        Assert.assertEquals("Hallo 2", CmsStringGenerator.createNumberString(2, "Hallo 0", "Hallo 1", "Hallo $"));
        Assert.assertEquals("Hallo 5", CmsStringGenerator.createNumberString(5, "Hallo 0", "Hallo 1", "Hallo $"));
        Assert.assertEquals("Hallo 15", CmsStringGenerator.createNumberString(15, "Hallo 0", "Hallo 1", "Hallo $"));
    }

    @Test
    @DirtiesContext
    public void testGetValue() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("test");

        Assert.assertEquals(0, cmsUiService.getValue("messengerService", "unreadMails"));
        // Add mail
        messengerService.sendMail("test", "subject", "body");
        Assert.assertEquals(1, cmsUiService.getValue("messengerService", "unreadMails"));

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testSmartPageLink() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage.setName("Home");
        DbPage dbPage2 = pageCrud.createDbChild();
        dbPage2.setName("Page 2");
        DbContentStaticHtml dbContentStaticHtml = new DbContentStaticHtml();
        dbContentStaticHtml.getDbI18nHtml().putString("This is page two");
        dbPage2.setContentAndAccessWrites(dbContentStaticHtml);

        // Smart link
        DbContentSmartPageLink smartPageLink = new DbContentSmartPageLink();
        dbPage.setContentAndAccessWrites(smartPageLink);
        smartPageLink.init(userService);
        smartPageLink.setAccessDeniedString("No access");
        smartPageLink.setButtonName("Button Name");
        smartPageLink.setDbPage(dbPage2);
        smartPageLink.setEnableAccess(DbContent.Access.REGISTERED_USER);
        smartPageLink.setSpringBeanName("messengerService");
        smartPageLink.setPropertyExpression("unreadMails");
        smartPageLink.setString0("Nothing");
        smartPageLink.setString1("Single");
        smartPageLink.setStringN("Multi $");
        pageCrud.updateDbChild(dbPage);
        pageCrud.updateDbChild(dbPage2);

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Activate
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify not logged in
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertLabel("form:content:label", "No access");
        getWicketTester().assertDisabled("form:content:button");
        Button button = (Button) getWicketTester().getComponentFromLastRenderedPage("form:content:button");
        Assert.assertEquals("Button Name", button.getValue());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify logged in 0 mail
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("test");
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertLabel("form:content:label", "Nothing");
        getWicketTester().assertEnabled("form:content:button");
        button = (Button) getWicketTester().getComponentFromLastRenderedPage("form:content:button");
        Assert.assertEquals("Button Name", button.getValue());
        // Click mail button
        getWicketTester().newFormTester("form").submit("content:button");
        getWicketTester().assertLabel("form:content", "This is page two");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify logged in 1 mail
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("test", "test");
        messengerService.sendMail("test", "subject", "body");
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertLabel("form:content:label", "Single");
        getWicketTester().assertEnabled("form:content:button");
        button = (Button) getWicketTester().getComponentFromLastRenderedPage("form:content:button");
        Assert.assertEquals("Button Name", button.getValue());
        // Click mail button
        getWicketTester().newFormTester("form").submit("content:button");
        getWicketTester().assertLabel("form:content", "This is page two");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify logged in 2 mails
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("test", "test");
        messengerService.sendMail("test", "subject", "body");
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertLabel("form:content:label", "Multi 2");
        getWicketTester().assertEnabled("form:content:button");
        button = (Button) getWicketTester().getComponentFromLastRenderedPage("form:content:button");
        Assert.assertEquals("Button Name", button.getValue());
        // Click mail button
        getWicketTester().newFormTester("form").submit("content:button");
        getWicketTester().assertLabel("form:content", "This is page two");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testStaticContentI18n() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage.setName("Home");
        DbContentStaticHtml dbContentStaticHtml1 = new DbContentStaticHtml();
        dbContentStaticHtml1.getDbI18nHtml().putString("This is page two <br>");
        dbContentStaticHtml1.getDbI18nHtml().putString(Locale.GERMAN, "Seite 2 <br>");
        dbContentStaticHtml1.setEditorType(DbExpressionProperty.EditorType.HTML_AREA);
        dbPage.setContentAndAccessWrites(dbContentStaticHtml1);
        pageCrud.updateDbChild(dbPage);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Activate
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // getWicketTester().setupRequestAndResponse();
        getWicketTester().getSession().setLocale(Locale.ENGLISH);
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertLabel("form:content", "This is page two <br>");
        getWicketTester().getSession().setLocale(Locale.GERMAN);
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertLabel("form:content", "Seite 2 <br>");
        getWicketTester().getSession().setLocale(Locale.CHINESE);
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertLabel("form:content", "This is page two <br>");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    private void createUserAndSendEmails() throws UserAlreadyExistsException, PasswordNotMatchException, InvalidFieldException {
        // Fill mails
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        userService.logout();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U2");
        userService.logout();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Send mail 1
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1", "test");
        messengerService.sendMail("U2", "subject1", "body1");
        userService.logout();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Send mail 2
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U2", "test");
        messengerService.sendMail("U1", "subject2", "body2");
        userService.logout();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Send mail 3
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1", "test");
        messengerService.sendMail("U2", "subject3", "body3");
        userService.logout();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Send mail 4
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U2", "test");
        messengerService.sendMail("U1", "subject4", "body4");
        userService.logout();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Send mail 5
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1", "test");
        messengerService.sendMail("U2", "subject5", "body5");
        userService.logout();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Send mail 6
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U2", "test");
        messengerService.sendMail("U1", "subject6", "body6");
        userService.logout();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testMail() throws Exception {
        configureSimplePlanetNoResources();
        // Add cms image
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbCmsImage> crud = cmsService.getImageCrudRootServiceHelper();
        DbCmsImage dbCmsImage1 = crud.createDbChild();
        dbCmsImage1.setData(new byte[50000]);
        dbCmsImage1.setContentType("image1");
        crud.updateDbChild(dbCmsImage1);
        DbCmsImage dbCmsImage2 = crud.createDbChild();
        dbCmsImage2.setData(new byte[10000]);
        dbCmsImage2.setContentType("image2");
        crud.updateDbChild(dbCmsImage2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage.setName("Home");

        // Mail List
        DbContentContainer dbContentContainer = new DbContentContainer();
        dbPage.setContentAndAccessWrites(dbContentContainer);
        dbContentContainer.init(userService);
        DbContentList mailList = (DbContentList) dbContentContainer.getContentCrud().createDbChild(DbContentList.class);
        mailList.setSpringBeanName("messengerService");
        mailList.setContentProviderGetter("userMailCrud");
        DbContentBooleanExpressionImage readImage = (DbContentBooleanExpressionImage) mailList.getColumnsCrud().createDbChild(DbContentBooleanExpressionImage.class);
        readImage.setExpression("read");
        readImage.setTrueImage(dbCmsImage1);
        readImage.setFalseImage(dbCmsImage2);
        DbExpressionProperty date = (DbExpressionProperty) mailList.getColumnsCrud().createDbChild(DbExpressionProperty.class);
        date.setExpression("sent");
        date.setOptionalType(DbExpressionProperty.Type.DATE_DDMMYYYY_HH_MM_SS);
        DbExpressionProperty from = (DbExpressionProperty) mailList.getColumnsCrud().createDbChild(DbExpressionProperty.class);
        from.setExpression("fromUser");
        DbExpressionProperty subject = (DbExpressionProperty) mailList.getColumnsCrud().createDbChild(DbExpressionProperty.class);
        subject.setExpression("subject");
        DbContentDetailLink details = (DbContentDetailLink) mailList.getColumnsCrud().createDbChild(DbContentDetailLink.class);
        details.setName("read");

        DbContentBook dbContentBook = mailList.getContentBookCrud().createDbChild();
        dbContentBook.setClassName("com.btxtech.game.services.messenger.DbMail");
        dbContentBook.setHiddenMethodName("setMailRead");
        DbContentRow dbContentRow = dbContentBook.getRowCrud().createDbChild();
        DbExpressionProperty mailSubject = new DbExpressionProperty();
        mailSubject.init(userService);
        mailSubject.setParent(dbContentRow);
        dbContentRow.setDbContent(mailSubject);
        mailSubject.setExpression("subject");

        pageCrud.updateDbChild(dbPage);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Activate
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        createUserAndSendEmails();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1", "test");
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertLabel("form:content:container:1:table:rows:1:cells:4:cell", "subject6");
        getWicketTester().assertLabel("form:content:container:1:table:rows:1:cells:3:cell", "U2");
        getWicketTester().assertLabel("form:content:container:1:table:rows:2:cells:4:cell", "subject4");
        getWicketTester().assertLabel("form:content:container:1:table:rows:2:cells:3:cell", "U2");
        getWicketTester().assertLabel("form:content:container:1:table:rows:3:cells:4:cell", "subject2");
        getWicketTester().assertLabel("form:content:container:1:table:rows:3:cells:3:cell", "U2");
        Assert.assertFalse(messengerService.getMails().get(0).isRead());
        // click the read more link
        getWicketTester().clickLink("form:content:container:1:table:rows:1:cells:5:cell:link");
        getWicketTester().assertLabel("form:content:table:rows:1:cells:2:cell", "subject6");
        endHttpRequestAndOpenSessionInViewFilter();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertTrue(messengerService.getMails().get(0).isRead());
        endHttpRequestAndOpenSessionInViewFilter();

        endHttpSession();
    }

    private void setupNewMailTest() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage.setName("Home");
        DbPage dbMessagePage = pageCrud.createDbChild();
        dbMessagePage.setPredefinedType(CmsUtil.CmsPredefinedPage.MESSAGE);
        dbMessagePage.setName("Message Page");

        // Mail List
        DbContentContainer dbContentContainer = new DbContentContainer();
        dbPage.setContentAndAccessWrites(dbContentContainer);
        dbContentContainer.init(userService);
        DbContentInvokerButton newMailButton = (DbContentInvokerButton) dbContentContainer.getContentCrud().createDbChild(DbContentInvokerButton.class);
        newMailButton.setName("New Mail");
        DbContentInvoker dbContentInvoker = new DbContentInvoker();
        newMailButton.setDbContentInvoker(dbContentInvoker);
        dbContentInvoker.setParent(newMailButton);
        dbContentInvoker.init(userService);
        dbContentInvoker.setSpringBeanName("messengerService");
        dbContentInvoker.setMethodName("sendMail");
        DbExpressionProperty to = dbContentInvoker.getValueCrud().createDbChild();
        to.setName("To");
        to.setExpression("toUser");
        DbExpressionProperty subject = dbContentInvoker.getValueCrud().createDbChild();
        subject.setName("subject");
        subject.setExpression("subject");
        subject.setEditorType(DbExpressionProperty.EditorType.PLAIN_TEXT_FILED);
        DbExpressionProperty body = dbContentInvoker.getValueCrud().createDbChild();
        body.setName("Message");
        body.setEditorType(DbExpressionProperty.EditorType.HTML_AREA);
        body.setExpression("body");

        pageCrud.updateDbChild(dbPage);
        pageCrud.updateDbChild(dbMessagePage);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Activate
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testNewMail() throws Exception {
        setupNewMailTest();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createUser("U2", "test");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertVisible("form:content:container:1:button");
        getWicketTester().newFormTester("form").submit("content:container:1:button");
        getWicketTester().assertVisible("form:content:listView:0:editor:field");
        getWicketTester().assertVisible("form:content:listView:1:editor:field");
        getWicketTester().assertVisible("form:content:listView:2:editor:editor");
        FormTester formTester = getWicketTester().newFormTester("form");
        formTester.setValue("content:listView:0:editor:field", "U2");
        formTester.setValue("content:listView:1:editor:field", "subject2");
        formTester.setValue("content:listView:2:editor:editor", "message message");
        formTester.submit("content:invoke");
        getWicketTester().assertVisible("form:content:container:1:button");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify U2 got mail
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U2", "test");
        Assert.assertEquals(1, messengerService.getMails().size());
        Assert.assertEquals("U1", messengerService.getMails().get(0).getFromUser());
        Assert.assertEquals("subject2", messengerService.getMails().get(0).getSubject());
        Assert.assertEquals("message message", messengerService.getMails().get(0).getBody());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testNewMailFailWrongUser() throws Exception {
        setupNewMailTest();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // getWicketTester().setupRequestAndResponse();
        getWicketTester().getSession().setLocale(Locale.ENGLISH);
        createAndLoginUser("U1");
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertVisible("form:content:container:1:button");
        getWicketTester().newFormTester("form").submit("content:container:1:button");
        getWicketTester().assertVisible("form:content:listView:0:editor:field");
        getWicketTester().assertVisible("form:content:listView:1:editor:field");
        getWicketTester().assertVisible("form:content:listView:2:editor:editor");
        FormTester formTester = getWicketTester().newFormTester("form");
        formTester.setValue("content:listView:0:editor:field", "U5");
        formTester.setValue("content:listView:1:editor:field", "subject2");
        formTester.setValue("content:listView:2:editor:editor", "message message");
        formTester.submit("content:invoke");
        getWicketTester().assertLabel("form:content:border:borderContent:message", "Error: Unknown user: U5");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testNewMailPressCancel() throws Exception {
        setupNewMailTest();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertVisible("form:content:container:1:button");
        getWicketTester().newFormTester("form").submit("content:container:1:button");
        getWicketTester().assertVisible("form:content:listView:0:editor:field");
        getWicketTester().assertVisible("form:content:listView:1:editor:field");
        getWicketTester().assertVisible("form:content:listView:2:editor:editor");
        FormTester formTester = getWicketTester().newFormTester("form");
        formTester.setValue("content:listView:0:editor:field", "U5");
        formTester.setValue("content:listView:1:editor:field", "subject2");
        formTester.setValue("content:listView:2:editor:editor", "message message");
        formTester.submit("content:cancel");
        getWicketTester().assertVisible("form:content:container:1:button");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testAds() throws Exception {
        configureSimplePlanetNoResources();

        // Setup ads
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbAds dbAds = cmsService.getAdsCrud().createDbChild();
        dbAds.setActive(true);
        dbAds.setCode("THIS IS THE CODE");
        cmsService.getAdsCrud().updateDbChild(dbAds);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();


        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setName("Home");
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage.setAdsVisible(true);
        DbContentStaticHtml dbContentStaticHtml = new DbContentStaticHtml();
        dbContentStaticHtml.getDbI18nHtml().putString("This is a page");
        dbPage.setContentAndAccessWrites(dbContentStaticHtml);

        pageCrud.updateDbChild(dbPage);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Activate
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertLabel("form:content", "This is a page");
        getWicketTester().assertLabel("contentRight:label", "THIS IS THE CODE");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testGameLinkText() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setName("Home");
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage.setAdsVisible(true);
        DbContentGameLink gameLink = new DbContentGameLink();
        gameLink.getDbI18nName().putString("GAME LINK");
        dbPage.setContentAndAccessWrites(gameLink);

        pageCrud.updateDbChild(dbPage);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Activate
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertLabel("form:content:link:label", "GAME LINK");
        getWicketTester().assertInvisible("form:content:link:image");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testGameLinkImage() throws Exception {
        configureSimplePlanetNoResources();

        // Setup image
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbCmsImage> crud = cmsService.getImageCrudRootServiceHelper();
        DbCmsImage dbCmsImage = crud.createDbChild();
        dbCmsImage.setData(new byte[50000]);
        dbCmsImage.setContentType("image");
        crud.updateDbChild(dbCmsImage);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();


        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setName("Home");
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage.setAdsVisible(true);
        DbContentGameLink gameLink = new DbContentGameLink();
        gameLink.setDbCmsImage(dbCmsImage);
        dbPage.setContentAndAccessWrites(gameLink);

        pageCrud.updateDbChild(dbPage);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Activate
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertVisible("form:content:link:image");
        getWicketTester().assertInvisible("form:content:link:label");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testExpressionProperty() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setName("Home");
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage.setAdsVisible(true);
        DbContentContainer dbContentContainer = new DbContentContainer();
        dbPage.setContentAndAccessWrites(dbContentContainer);
        dbContentContainer.init(userService);

        DbExpressionProperty level = (DbExpressionProperty) dbContentContainer.getContentCrud().createDbChild(DbExpressionProperty.class);
        level.setSpringBeanName("userGuidanceService");
        level.setExpression("dbLevelCms.name");

        DbExpressionProperty xp = (DbExpressionProperty) dbContentContainer.getContentCrud().createDbChild(DbExpressionProperty.class);
        xp.setSpringBeanName("userService");
        xp.setExpression("userState.xp");

        pageCrud.updateDbChild(dbPage);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Activate
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // Set Level and XP
        getMovableService().getRealGameInfo(START_UID_1, null); // Connection is created here. Don't call movableService.getGameInfo() again!

        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertLabel("form:content:container:1", "2");
        getWicketTester().assertLabel("form:content:container:2", "0");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testExpressionPropertyI18n() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setName("Home");
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        DbExpressionProperty expressionProperty = new DbExpressionProperty();
        dbPage.setContentAndAccessWrites(expressionProperty);
        expressionProperty.init(userService);
        expressionProperty.setSpringBeanName("testCmsBean");
        expressionProperty.setExpression("dbI18nString");
        pageCrud.updateDbChild(dbPage);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Activate
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // getWicketTester().setupRequestAndResponse();
        getWicketTester().getSession().setLocale(Locale.ENGLISH);
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().debugComponentTrees();
        getWicketTester().assertLabel("form:content", "Hello");
        getWicketTester().getSession().setLocale(Locale.GERMAN);
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().debugComponentTrees();
        getWicketTester().assertLabel("form:content", "Hallo");
        getWicketTester().getSession().setLocale(Locale.CHINESE);
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().debugComponentTrees();
        getWicketTester().assertLabel("form:content", "Hello");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }


    @Test
    @DirtiesContext
    public void testUrlGenerating() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        pageCrud.updateDbChild(dbPage);

        dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.MESSAGE);
        pageCrud.updateDbChild(dbPage);

        dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.REGISTER);
        pageCrud.updateDbChild(dbPage);

        dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.USER_PAGE);
        pageCrud.updateDbChild(dbPage);

        dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HIGH_SCORE);
        pageCrud.updateDbChild(dbPage);

        dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.INFO);
        pageCrud.updateDbChild(dbPage);

        dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.NO_HTML5_BROWSER);
        pageCrud.updateDbChild(dbPage);

        dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.NOT_FOUND);
        pageCrud.updateDbChild(dbPage);

        dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.LEVEL_TASK_DONE);
        pageCrud.updateDbChild(dbPage);

        dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.EMAIL_VERIFICATION);
        pageCrud.updateDbChild(dbPage);

        dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.FORGOT_PASSWORD_REQUEST);
        pageCrud.updateDbChild(dbPage);

        dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.FORGOT_PASSWORD_CHANGE);
        pageCrud.updateDbChild(dbPage);

        dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.LOGIN_FAILED);
        pageCrud.updateDbChild(dbPage);

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Activate
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Map<CmsUtil.CmsPredefinedPage, String> urls = cmsUiService.getPredefinedUrls();
        Assert.assertEquals(CmsUtil.CmsPredefinedPage.values().length - 2 /*Two are deprected*/, urls.size());

        for (String url : urls.values()) {
            Assert.assertNotNull(url);
            Assert.assertTrue(url.length() > 10);
        }

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void noHtml5Browser() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.NO_HTML5_BROWSER);
        pageCrud.updateDbChild(dbPage);

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Activate
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        PageParameters pageParameters = new PageParameters();
        pageParameters.set("page", "NoHtml5Browser");
        getWicketTester().startPage(CmsPage.class, pageParameters);

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void notFound() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();

        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.NOT_FOUND);
        pageCrud.updateDbChild(dbPage);

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Activate
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertNotNull(cmsUiService.getPredefinedNotFound());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testSorting() throws Exception {
        configureMultiplePlanetsAndLevels();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbCmsImage> crud = cmsService.getImageCrudRootServiceHelper();
        DbCmsImage ascImg = crud.createDbChild();
        ascImg.setData(new byte[50000]);
        ascImg.setContentType("image");
        DbCmsImage descImg = crud.createDbChild();
        descImg.setData(new byte[50000]);
        descImg.setContentType("image");
        crud.updateDbChild(ascImg);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Mock statistics service
        List<UserState> userStates = new ArrayList<>();

        UserState userState = new UserState();
        userState.setDbLevelId(TEST_LEVEL_1_SIMULATED_ID);
        userStates.add(userState);

        PlanetInfo planetInfo = new PlanetInfo();
        planetInfo.setPlanetIdAndName(1, null, null);
        ServerPlanetServicesImpl serverPlanetServices = new ServerPlanetServicesImpl();
        serverPlanetServices.setPlanetInfo(planetInfo);
        TestPlanetHelper planet = new TestPlanetHelper();
        planet.setServerPlanetServices(serverPlanetServices);

        User user1 = new User();
        user1.registerUser("aaa", null, null, null);
        userState = new UserState();
        userState.setUser(1);
        Base base1 = new Base(userState, planet, 1);
        base1.setAccountBalance(1234);
        setPrivateField(Base.class, base1, "startTime", new Date(System.currentTimeMillis() - ClientDateUtil.MILLIS_IN_HOUR));
        base1.addItem(createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(100, 100), new Id(1, 1)));
        base1.addItem(createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(100, 100), new Id(2, 1)));
        userState.setBase(base1);
        userState.setDbLevelId(TEST_LEVEL_2_REAL_ID);
        userStates.add(userState);

        User user2 = new User();
        user2.registerUser("xxx", null, null, null);
        userState = new UserState();
        userState.setUser(2);
        Base base2 = new Base(userState, planet, 2);
        base2.setAccountBalance(90);
        setPrivateField(Base.class, base2, "startTime", new Date(System.currentTimeMillis() - ClientDateUtil.MILLIS_IN_MINUTE));
        base2.addItem(createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(100, 100), new Id(1, 1)));
        base2.addItem(createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(100, 100), new Id(2, 1)));
        base2.addItem(createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(100, 100), new Id(3, 1)));
        base2.addItem(createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(100, 100), new Id(4, 1)));
        base2.addItem(createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(100, 100), new Id(5, 1)));
        userState.setBase(base2);
        userState.setDbLevelId(TEST_LEVEL_2_REAL_ID);
        userStates.add(userState);

        UserService userServiceMock = EasyMock.createNiceMock(UserService.class);
        EasyMock.expect(userServiceMock.getAllUserStates()).andReturn(userStates).anyTimes();
        EasyMock.expect(userServiceMock.getUser(1)).andReturn(user1).anyTimes();
        EasyMock.expect(userServiceMock.getUser(2)).andReturn(user2).anyTimes();
        EasyMock.replay(userServiceMock);
        setPrivateField(StatisticsServiceImpl.class, statisticsService, "userService", userServiceMock);

        BaseService baseService = EasyMock.createMock(BaseService.class);
        EasyMock.expect(baseService.getBaseName(base1.getSimpleBase())).andReturn("aaa").times(4);
        EasyMock.expect(baseService.getBaseName(base2.getSimpleBase())).andReturn("xxx").times(4);
        EasyMock.replay(baseService);
        serverPlanetServices.setBaseService(baseService);

        PlanetSystemService planetSystemServiceMock = EasyMock.createMock(PlanetSystemService.class);
        EasyMock.replay(planetSystemServiceMock);

        setPrivateField(StatisticsServiceImpl.class, statisticsService, "planetSystemService", planetSystemServiceMock);

        // Setup CMS content
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage1 = pageCrud.createDbChild();
        dbPage1.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage1.setName("Home");

        DbContentList dbContentList = new DbContentList();
        dbContentList.init(userService);
        dbPage1.setContentAndAccessWrites(dbContentList);
        dbContentList.setRowsPerPage(5);
        dbContentList.setShowHead(true);
        dbContentList.setSpringBeanName("statisticsService");
        dbContentList.setContentProviderGetter("cmsCurrentStatistics");

        DbExpressionProperty rank = (DbExpressionProperty) dbContentList.getColumnsCrud().createDbChild(DbExpressionProperty.class);
        rank.setExpression("rank");
        rank.setName("Rank");
        DbExpressionProperty level = (DbExpressionProperty) dbContentList.getColumnsCrud().createDbChild(DbExpressionProperty.class);
        level.setExpression("level.name");
        level.setName("Level");
        level.setSortable(true);
        level.setSortLinkCssClass("sortCSS");
        level.setSortLinkCssClassActive("sortCSSActive");
        DbExpressionProperty userColumn = (DbExpressionProperty) dbContentList.getColumnsCrud().createDbChild(DbExpressionProperty.class);
        userColumn.setExpression("userName");
        userColumn.setName("User");
        userColumn.setSortable(true);
        userColumn.setSortHintExpression("userName");
        userColumn.setSortLinkCssClass("sortCSS");
        userColumn.setSortLinkCssClassActive("sortCSSActive");
        userColumn.setSortAscActiveImage(ascImg);
        userColumn.setSortDescActiveImage(descImg);
        DbExpressionProperty baseUpTime = (DbExpressionProperty) dbContentList.getColumnsCrud().createDbChild(DbExpressionProperty.class);
        baseUpTime.setExpression("baseUpTime");
        baseUpTime.setOptionalType(DbExpressionProperty.Type.DURATION_HH_MM_SS);
        baseUpTime.setName("Time");
        baseUpTime.setSortable(true);
        baseUpTime.setSortLinkCssClass("sortCSS");
        baseUpTime.setSortLinkCssClassActive("sortCSSActive");
        DbExpressionProperty itemCount = (DbExpressionProperty) dbContentList.getColumnsCrud().createDbChild(DbExpressionProperty.class);
        itemCount.setExpression("itemCount");
        itemCount.setName("Items");
        itemCount.setSortable(true);
        itemCount.setDefaultSortable(true);
        itemCount.setDefaultSortableAsc(false);
        itemCount.setSortLinkCssClass("sortCSS");
        itemCount.setSortLinkCssClassActive("sortCSSActive");
        itemCount.setSortAscActiveImage(ascImg);
        itemCount.setSortDescActiveImage(descImg);
        DbExpressionProperty money = (DbExpressionProperty) dbContentList.getColumnsCrud().createDbChild(DbExpressionProperty.class);
        money.setExpression("money");
        money.setName("Money");
        money.setSortable(false);

        pageCrud.updateDbChild(dbPage1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Activate
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertLabel("form:content:table:tHead:cell:1", "Rank");
        PageParameters pageParameters = new PageParameters();
        pageParameters.set("page", 1);
        pageParameters.set("sort1", "dLevel");
        getWicketTester().assertBookmarkablePageLink("form:content:table:tHead:cell:2:link", CmsPage.class, pageParameters);
        assertCssClass(getWicketTester(), "form:content:table:tHead:cell:2:link", "sortCSS");
        getWicketTester().assertLabel("form:content:table:tHead:cell:2:link:label", "Level");
        getWicketTester().assertInvisible("form:content:table:tHead:cell:2:link:image");
        pageParameters = new PageParameters();
        pageParameters.set("page", 1);
        pageParameters.set("sort1", "dUser");
        getWicketTester().assertBookmarkablePageLink("form:content:table:tHead:cell:3:link", CmsPage.class, pageParameters);
        assertCssClass(getWicketTester(), "form:content:table:tHead:cell:3:link", "sortCSS");
        getWicketTester().assertLabel("form:content:table:tHead:cell:3:link:label", "User");
        getWicketTester().assertInvisible("form:content:table:tHead:cell:3:link:image");
        pageParameters = new PageParameters();
        pageParameters.set("page", 1);
        pageParameters.set("sort1", "dTime");
        getWicketTester().assertBookmarkablePageLink("form:content:table:tHead:cell:4:link", CmsPage.class, pageParameters);
        assertCssClass(getWicketTester(), "form:content:table:tHead:cell:4:link", "sortCSS");
        getWicketTester().assertLabel("form:content:table:tHead:cell:4:link:label", "Time");
        getWicketTester().assertInvisible("form:content:table:tHead:cell:4:link:image");
        pageParameters = new PageParameters();
        pageParameters.set("page", 1);
        pageParameters.set("sort1", "aItems");
        getWicketTester().assertBookmarkablePageLink("form:content:table:tHead:cell:5:link", CmsPage.class, pageParameters);
        assertCssClass(getWicketTester(), "form:content:table:tHead:cell:5:link", "sortCSSActive");
        getWicketTester().assertLabel("form:content:table:tHead:cell:5:link:label", "Items");
        assertCmsImage(getWicketTester(), "form:content:table:tHead:cell:5:link:image", descImg);
        getWicketTester().assertLabel("form:content:table:tHead:cell:6", "Money");

        getWicketTester().assertLabel("form:content:table:rows:1:cells:1:cell", "1");
        getWicketTester().assertLabel("form:content:table:rows:1:cells:2:cell", "2");
        getWicketTester().assertLabel("form:content:table:rows:1:cells:3:cell", "xxx");
        // Time is 1:00:01 getWicketTester().assertLabel("form:content:table:rows:1:cells:4:cell", "0:01:00");
        getWicketTester().assertLabel("form:content:table:rows:1:cells:5:cell", "5");
        getWicketTester().assertLabel("form:content:table:rows:1:cells:6:cell", "90");

        getWicketTester().assertLabel("form:content:table:rows:2:cells:1:cell", "2");
        getWicketTester().assertLabel("form:content:table:rows:2:cells:2:cell", "2");
        getWicketTester().assertLabel("form:content:table:rows:2:cells:3:cell", "aaa");
        // Time is 1:00:01 getWicketTester().assertLabel("form:content:table:rows:2:cells:4:cell", "1:00:00");
        getWicketTester().assertLabel("form:content:table:rows:2:cells:5:cell", "2");
        getWicketTester().assertLabel("form:content:table:rows:2:cells:6:cell", "1234");

        getWicketTester().assertLabel("form:content:table:rows:3:cells:1:cell", "3");
        getWicketTester().assertLabel("form:content:table:rows:3:cells:2:cell", "1");
        getWicketTester().assertLabel("form:content:table:rows:3:cells:3:cell", "");
        getWicketTester().assertLabel("form:content:table:rows:3:cells:4:cell", "");
        getWicketTester().assertLabel("form:content:table:rows:3:cells:5:cell", "");

        getWicketTester().clickLink("form:content:table:tHead:cell:4:link");

        getWicketTester().assertLabel("form:content:table:tHead:cell:1", "Rank");
        pageParameters = new PageParameters();
        pageParameters.set("page", 1);
        pageParameters.set("sort1", "dLevel");
        getWicketTester().assertBookmarkablePageLink("form:content:table:tHead:cell:2:link", CmsPage.class, pageParameters);
        assertCssClass(getWicketTester(), "form:content:table:tHead:cell:2:link", "sortCSS");
        getWicketTester().assertLabel("form:content:table:tHead:cell:2:link:label", "Level");
        getWicketTester().assertInvisible("form:content:table:tHead:cell:2:link:image");
        pageParameters = new PageParameters();
        pageParameters.set("page", 1);
        pageParameters.set("sort1", "dUser");
        getWicketTester().assertBookmarkablePageLink("form:content:table:tHead:cell:3:link", CmsPage.class, pageParameters);
        assertCssClass(getWicketTester(), "form:content:table:tHead:cell:3:link", "sortCSS");
        getWicketTester().assertLabel("form:content:table:tHead:cell:3:link:label", "User");
        getWicketTester().assertInvisible("form:content:table:tHead:cell:3:link:image");
        pageParameters = new PageParameters();
        pageParameters.set("page", 1);
        pageParameters.set("sort1", "aTime");
        getWicketTester().assertBookmarkablePageLink("form:content:table:tHead:cell:4:link", CmsPage.class, pageParameters);
        assertCssClass(getWicketTester(), "form:content:table:tHead:cell:4:link", "sortCSSActive");
        getWicketTester().assertLabel("form:content:table:tHead:cell:4:link:label", "Time");
        getWicketTester().assertInvisible("form:content:table:tHead:cell:4:link:image");
        pageParameters = new PageParameters();
        pageParameters.set("page", 1);
        pageParameters.set("sort1", "dItems");
        getWicketTester().assertBookmarkablePageLink("form:content:table:tHead:cell:5:link", CmsPage.class, pageParameters);
        assertCssClass(getWicketTester(), "form:content:table:tHead:cell:5:link", "sortCSS");
        getWicketTester().assertLabel("form:content:table:tHead:cell:5:link:label", "Items");
        getWicketTester().assertInvisible("form:content:table:tHead:cell:5:link:image");
        getWicketTester().assertLabel("form:content:table:tHead:cell:6", "Money");

        getWicketTester().assertLabel("form:content:table:rows:1:cells:1:cell", "1");
        getWicketTester().assertLabel("form:content:table:rows:1:cells:2:cell", "2");
        getWicketTester().assertLabel("form:content:table:rows:1:cells:3:cell", "aaa");
        // time is  1:00:01 getWicketTester().assertLabel("form:content:table:rows:1:cells:4:cell", "1:00:00");
        getWicketTester().assertLabel("form:content:table:rows:1:cells:5:cell", "2");
        getWicketTester().assertLabel("form:content:table:rows:1:cells:6:cell", "1234");

        getWicketTester().assertLabel("form:content:table:rows:2:cells:1:cell", "2");
        getWicketTester().assertLabel("form:content:table:rows:2:cells:2:cell", "2");
        getWicketTester().assertLabel("form:content:table:rows:2:cells:3:cell", "xxx");
        // time is 0:01:01  getWicketTester().assertLabel("form:content:table:rows:2:cells:4:cell", "0:01:00");
        getWicketTester().assertLabel("form:content:table:rows:2:cells:5:cell", "5");
        getWicketTester().assertLabel("form:content:table:rows:2:cells:6:cell", "90");

        getWicketTester().assertLabel("form:content:table:rows:3:cells:1:cell", "3");
        getWicketTester().assertLabel("form:content:table:rows:3:cells:2:cell", "1");
        getWicketTester().assertLabel("form:content:table:rows:3:cells:3:cell", "");
        getWicketTester().assertLabel("form:content:table:rows:3:cells:4:cell", "");
        getWicketTester().assertLabel("form:content:table:rows:3:cells:5:cell", "");
        getWicketTester().assertLabel("form:content:table:rows:3:cells:6:cell", "");

        getWicketTester().clickLink("form:content:table:tHead:cell:3:link");

        getWicketTester().assertLabel("form:content:table:tHead:cell:1", "Rank");
        pageParameters = new PageParameters();
        pageParameters.set("page", 1);
        pageParameters.set("sort1", "dLevel");
        getWicketTester().assertBookmarkablePageLink("form:content:table:tHead:cell:2:link", CmsPage.class, pageParameters);
        assertCssClass(getWicketTester(), "form:content:table:tHead:cell:2:link", "sortCSS");
        getWicketTester().assertLabel("form:content:table:tHead:cell:2:link:label", "Level");
        getWicketTester().assertInvisible("form:content:table:tHead:cell:2:link:image");
        pageParameters = new PageParameters();
        pageParameters.set("page", 1);
        pageParameters.set("sort1", "aUser");
        getWicketTester().assertBookmarkablePageLink("form:content:table:tHead:cell:3:link", CmsPage.class, pageParameters);
        assertCssClass(getWicketTester(), "form:content:table:tHead:cell:3:link", "sortCSSActive");
        getWicketTester().assertLabel("form:content:table:tHead:cell:3:link:label", "User");
        assertCmsImage(getWicketTester(), "form:content:table:tHead:cell:3:link:image", descImg);
        pageParameters = new PageParameters();
        pageParameters.set("page", 1);
        pageParameters.set("sort1", "dTime");
        getWicketTester().assertBookmarkablePageLink("form:content:table:tHead:cell:4:link", CmsPage.class, pageParameters);
        assertCssClass(getWicketTester(), "form:content:table:tHead:cell:4:link", "sortCSS");
        getWicketTester().assertLabel("form:content:table:tHead:cell:4:link:label", "Time");
        getWicketTester().assertInvisible("form:content:table:tHead:cell:4:link:image");
        pageParameters = new PageParameters();
        pageParameters.set("page", 1);
        pageParameters.set("sort1", "dItems");
        getWicketTester().assertBookmarkablePageLink("form:content:table:tHead:cell:5:link", CmsPage.class, pageParameters);
        assertCssClass(getWicketTester(), "form:content:table:tHead:cell:5:link", "sortCSS");
        getWicketTester().assertLabel("form:content:table:tHead:cell:5:link:label", "Items");
        getWicketTester().assertInvisible("form:content:table:tHead:cell:5:link:image");
        getWicketTester().assertLabel("form:content:table:tHead:cell:6", "Money");

        getWicketTester().assertLabel("form:content:table:rows:1:cells:1:cell", "1");
        getWicketTester().assertLabel("form:content:table:rows:1:cells:2:cell", "2");
        getWicketTester().assertLabel("form:content:table:rows:1:cells:3:cell", "xxx");
        // Time is 1:00:01 getWicketTester().assertLabel("form:content:table:rows:1:cells:3:cell", "1:00:00");
        getWicketTester().assertLabel("form:content:table:rows:1:cells:5:cell", "5");
        getWicketTester().assertLabel("form:content:table:rows:1:cells:6:cell", "90");

        getWicketTester().assertLabel("form:content:table:rows:2:cells:1:cell", "2");
        getWicketTester().assertLabel("form:content:table:rows:2:cells:2:cell", "2");
        getWicketTester().assertLabel("form:content:table:rows:2:cells:3:cell", "aaa");
        // Time is 0:01:00 getWicketTester().assertLabel("form:content:table:rows:2:cells:4:cell", "0:01:00");
        getWicketTester().assertLabel("form:content:table:rows:2:cells:5:cell", "2");
        getWicketTester().assertLabel("form:content:table:rows:2:cells:6:cell", "1234");

        getWicketTester().assertLabel("form:content:table:rows:3:cells:1:cell", "3");
        getWicketTester().assertLabel("form:content:table:rows:3:cells:2:cell", "1");
        getWicketTester().assertLabel("form:content:table:rows:3:cells:3:cell", "");
        getWicketTester().assertLabel("form:content:table:rows:3:cells:4:cell", "");
        getWicketTester().assertLabel("form:content:table:rows:3:cells:5:cell", "");
        getWicketTester().assertLabel("form:content:table:rows:3:cells:6:cell", "");

        getWicketTester().clickLink("form:content:table:tHead:cell:3:link");

        getWicketTester().assertLabel("form:content:table:tHead:cell:1", "Rank");
        pageParameters = new PageParameters();
        pageParameters.set("page", 1);
        pageParameters.set("sort1", "dLevel");
        getWicketTester().assertBookmarkablePageLink("form:content:table:tHead:cell:2:link", CmsPage.class, pageParameters);
        assertCssClass(getWicketTester(), "form:content:table:tHead:cell:2:link", "sortCSS");
        getWicketTester().assertLabel("form:content:table:tHead:cell:2:link:label", "Level");
        getWicketTester().assertInvisible("form:content:table:tHead:cell:2:link:image");
        pageParameters = new PageParameters();
        pageParameters.set("page", 1);
        pageParameters.set("sort1", "dUser");
        getWicketTester().assertBookmarkablePageLink("form:content:table:tHead:cell:3:link", CmsPage.class, pageParameters);
        assertCssClass(getWicketTester(), "form:content:table:tHead:cell:3:link", "sortCSSActive");
        getWicketTester().assertLabel("form:content:table:tHead:cell:3:link:label", "User");
        assertCmsImage(getWicketTester(), "form:content:table:tHead:cell:3:link:image", ascImg);
        pageParameters = new PageParameters();
        pageParameters.set("page", 1);
        pageParameters.set("sort1", "dTime");
        getWicketTester().assertBookmarkablePageLink("form:content:table:tHead:cell:4:link", CmsPage.class, pageParameters);
        assertCssClass(getWicketTester(), "form:content:table:tHead:cell:4:link", "sortCSS");
        getWicketTester().assertLabel("form:content:table:tHead:cell:4:link:label", "Time");
        getWicketTester().assertInvisible("form:content:table:tHead:cell:4:link:image");
        pageParameters = new PageParameters();
        pageParameters.set("page", 1);
        pageParameters.set("sort1", "dItems");
        getWicketTester().assertBookmarkablePageLink("form:content:table:tHead:cell:5:link", CmsPage.class, pageParameters);
        assertCssClass(getWicketTester(), "form:content:table:tHead:cell:5:link", "sortCSS");
        getWicketTester().assertLabel("form:content:table:tHead:cell:5:link:label", "Items");
        getWicketTester().assertInvisible("form:content:table:tHead:cell:5:link:image");
        getWicketTester().assertLabel("form:content:table:tHead:cell:6", "Money");

        getWicketTester().assertLabel("form:content:table:rows:1:cells:1:cell", "3");
        getWicketTester().assertLabel("form:content:table:rows:1:cells:2:cell", "1");
        getWicketTester().assertLabel("form:content:table:rows:1:cells:3:cell", "");
        getWicketTester().assertLabel("form:content:table:rows:1:cells:4:cell", "");
        getWicketTester().assertLabel("form:content:table:rows:1:cells:5:cell", "");
        getWicketTester().assertLabel("form:content:table:rows:1:cells:6:cell", "");

        getWicketTester().assertLabel("form:content:table:rows:2:cells:1:cell", "2");
        getWicketTester().assertLabel("form:content:table:rows:2:cells:2:cell", "2");
        getWicketTester().assertLabel("form:content:table:rows:2:cells:3:cell", "aaa");
        // Time is 0:01:01 getWicketTester().assertLabel("form:content:table:rows:2:cells:4:cell", "0:01:00");
        getWicketTester().assertLabel("form:content:table:rows:2:cells:5:cell", "2");
        getWicketTester().assertLabel("form:content:table:rows:2:cells:6:cell", "1234");

        getWicketTester().assertLabel("form:content:table:rows:3:cells:1:cell", "1");
        getWicketTester().assertLabel("form:content:table:rows:3:cells:2:cell", "2");
        getWicketTester().assertLabel("form:content:table:rows:3:cells:3:cell", "xxx");
        // Time is 1:00:01 getWicketTester().assertLabel("form:content:table:rows:3:cells:3:cell", "1:00:00");
        getWicketTester().assertLabel("form:content:table:rows:3:cells:5:cell", "5");
        getWicketTester().assertLabel("form:content:table:rows:3:cells:6:cell", "90");


        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testPaging() throws Exception {
        configureMultiplePlanetsAndLevels();
        // Mock statistics service
        List<UserState> userStates = new ArrayList<>();

        PlanetInfo planetInfo = new PlanetInfo();
        planetInfo.setPlanetIdAndName(1, null, null);
        ServerPlanetServicesImpl serverPlanetServices = new ServerPlanetServicesImpl();
        serverPlanetServices.setPlanetInfo(planetInfo);
        TestPlanetHelper planet = new TestPlanetHelper();
        planet.setServerPlanetServices(serverPlanetServices);

        User user1 = new User();
        user1.registerUser("aaa", null, null, null);
        UserState userState = new UserState();
        userState.setDbLevelId(TEST_LEVEL_1_SIMULATED_ID);
        userStates.add(userState);
        userState = new UserState();
        userState.setUser(1);
        Base base1 = new Base(userState, planet, 1);
        base1.setAccountBalance(1234);
        setPrivateField(Base.class, base1, "startTime", new Date(System.currentTimeMillis() - ClientDateUtil.MILLIS_IN_HOUR));
        base1.addItem(createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(100, 100), new Id(1, 1)));
        base1.addItem(createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(100, 100), new Id(2, 1)));
        userState.setBase(base1);
        userState.setDbLevelId(TEST_LEVEL_2_REAL_ID);
        userStates.add(userState);

        User user2 = new User();
        user2.registerUser("xxx", null, null, null);
        userState = new UserState();
        userState.setUser(2);
        Base base2 = new Base(userState, planet, 2);
        base2.setAccountBalance(90);
        setPrivateField(Base.class, base2, "startTime", new Date(System.currentTimeMillis() - ClientDateUtil.MILLIS_IN_MINUTE));
        base2.addItem(createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(100, 100), new Id(1, 1)));
        base2.addItem(createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(100, 100), new Id(2, 1)));
        base2.addItem(createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(100, 100), new Id(3, 1)));
        base2.addItem(createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(100, 100), new Id(4, 1)));
        base2.addItem(createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(100, 100), new Id(5, 1)));
        userState.setBase(base2);
        userState.setDbLevelId(TEST_LEVEL_2_REAL_ID);
        userStates.add(userState);

        UserService userServiceMock = EasyMock.createNiceMock(UserService.class);
        EasyMock.expect(userServiceMock.getAllUserStates()).andReturn(userStates).anyTimes();
        EasyMock.expect(userServiceMock.getUser(1)).andReturn(user1).anyTimes();
        EasyMock.expect(userServiceMock.getUser(2)).andReturn(user2).anyTimes();
        EasyMock.replay(userServiceMock);
        setPrivateField(StatisticsServiceImpl.class, statisticsService, "userService", userServiceMock);

        BaseService baseService = EasyMock.createMock(BaseService.class);
        EasyMock.expect(baseService.getBaseName(base1.getSimpleBase())).andReturn("Base 1").times(8);
        EasyMock.expect(baseService.getBaseName(base2.getSimpleBase())).andReturn("RegUser").times(8);
        EasyMock.replay(baseService);
        ((ServerPlanetServicesImpl) planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID)).setBaseService(baseService);

        // Setup CMS content
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage1 = pageCrud.createDbChild();
        dbPage1.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage1.setName("Home");

        DbContentContainer dbContentContainer = new DbContentContainer();
        dbContentContainer.init(userService);
        dbPage1.setContentAndAccessWrites(dbContentContainer);

        DbContentList dbContentList = (DbContentList) dbContentContainer.getContentCrud().createDbChild(DbContentList.class);
        dbContentList.setRowsPerPage(2);
        dbContentList.setSpringBeanName("statisticsService");
        dbContentList.setContentProviderGetter("cmsCurrentStatistics");
        DbExpressionProperty level = (DbExpressionProperty) dbContentList.getColumnsCrud().createDbChild(DbExpressionProperty.class);
        level.setExpression("level.name");
        level.setName("Level1");
        DbExpressionProperty userColumn = (DbExpressionProperty) dbContentList.getColumnsCrud().createDbChild(DbExpressionProperty.class);
        userColumn.setExpression("userName");
        userColumn.setName("User1");
        DbExpressionProperty baseUpTime = (DbExpressionProperty) dbContentList.getColumnsCrud().createDbChild(DbExpressionProperty.class);
        baseUpTime.setExpression("baseUpTime");
        baseUpTime.setOptionalType(DbExpressionProperty.Type.DURATION_HH_MM_SS);
        baseUpTime.setName("Time1");
        DbExpressionProperty itemCount = (DbExpressionProperty) dbContentList.getColumnsCrud().createDbChild(DbExpressionProperty.class);
        itemCount.setExpression("itemCount");
        itemCount.setName("Items1");
        DbExpressionProperty money = (DbExpressionProperty) dbContentList.getColumnsCrud().createDbChild(DbExpressionProperty.class);
        money.setExpression("money");
        money.setName("Money1");

        dbContentList = (DbContentList) dbContentContainer.getContentCrud().createDbChild(DbContentList.class);
        dbContentList.setRowsPerPage(2);
        dbContentList.setSpringBeanName("statisticsService");
        dbContentList.setContentProviderGetter("cmsCurrentStatistics");
        level = (DbExpressionProperty) dbContentList.getColumnsCrud().createDbChild(DbExpressionProperty.class);
        level.setExpression("level.name");
        level.setName("Level2");
        userColumn = (DbExpressionProperty) dbContentList.getColumnsCrud().createDbChild(DbExpressionProperty.class);
        userColumn.setExpression("userName");
        userColumn.setName("User2");
        baseUpTime = (DbExpressionProperty) dbContentList.getColumnsCrud().createDbChild(DbExpressionProperty.class);
        baseUpTime.setExpression("baseUpTime");
        baseUpTime.setOptionalType(DbExpressionProperty.Type.DURATION_HH_MM_SS);
        baseUpTime.setName("Time2");
        itemCount = (DbExpressionProperty) dbContentList.getColumnsCrud().createDbChild(DbExpressionProperty.class);
        itemCount.setExpression("itemCount");
        itemCount.setName("Items2");
        money = (DbExpressionProperty) dbContentList.getColumnsCrud().createDbChild(DbExpressionProperty.class);
        money.setExpression("money");
        money.setName("Money2");

        pageCrud.updateDbChild(dbPage1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Activate
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().startPage(CmsPage.class);
        PageParameters pageParameters = new PageParameters();
        pageParameters.set("page", 1);
        pageParameters.set("paging2", 0);
        getWicketTester().assertBookmarkablePageLink("form:content:container:1:navigator:navigation:0:pageLink", CmsPage.class, pageParameters);
        getWicketTester().assertDisabled("form:content:container:1:navigator:navigation:0:pageLink");
        getWicketTester().assertLabel("form:content:container:1:navigator:navigation:0:pageLink:pageNumber", "1");
        pageParameters = new PageParameters();
        pageParameters.set("page", 1);
        pageParameters.set("paging2", 1);
        getWicketTester().assertBookmarkablePageLink("form:content:container:1:navigator:navigation:1:pageLink", CmsPage.class, pageParameters);
        getWicketTester().assertEnabled("form:content:container:1:navigator:navigation:1:pageLink");
        getWicketTester().assertLabel("form:content:container:1:navigator:navigation:1:pageLink:pageNumber", "2");
        pageParameters = new PageParameters();
        pageParameters.set("page", 1);
        pageParameters.set("paging2", 0);
        getWicketTester().assertDisabled("form:content:container:1:navigator:first");
        getWicketTester().assertBookmarkablePageLink("form:content:container:1:navigator:first", CmsPage.class, pageParameters);
        pageParameters = new PageParameters();
        pageParameters.set("page", 1);
        pageParameters.set("paging2", 0);
        getWicketTester().assertDisabled("form:content:container:1:navigator:prev");
        getWicketTester().assertBookmarkablePageLink("form:content:container:1:navigator:prev", CmsPage.class, pageParameters);
        pageParameters = new PageParameters();
        pageParameters.set("page", 1);
        pageParameters.set("paging2", 1);
        getWicketTester().assertEnabled("form:content:container:1:navigator:next");
        getWicketTester().assertBookmarkablePageLink("form:content:container:1:navigator:next", CmsPage.class, pageParameters);
        pageParameters.set("page", 1);
        pageParameters = new PageParameters();
        pageParameters.set("page", 1);
        pageParameters.set("paging2", 1);
        getWicketTester().assertEnabled("form:content:container:1:navigator:last");
        getWicketTester().assertBookmarkablePageLink("form:content:container:1:navigator:last", CmsPage.class, pageParameters);

        getWicketTester().assertLabel("form:content:container:1:table:rows:1:cells:1:cell", "1");
        getWicketTester().assertLabel("form:content:container:1:table:rows:2:cells:1:cell", "2");

        pageParameters = new PageParameters();
        pageParameters.set("page", 1);
        pageParameters.set("paging8", 0);
        getWicketTester().assertBookmarkablePageLink("form:content:container:2:navigator:navigation:0:pageLink", CmsPage.class, pageParameters);
        getWicketTester().assertDisabled("form:content:container:2:navigator:navigation:0:pageLink");
        getWicketTester().assertLabel("form:content:container:2:navigator:navigation:0:pageLink:pageNumber", "1");

        getWicketTester().assertLabel("form:content:container:2:table:rows:1:cells:1:cell", "1");
        getWicketTester().assertLabel("form:content:container:2:table:rows:2:cells:1:cell", "2");

        getWicketTester().clickLink("form:content:container:1:navigator:next");

        pageParameters = new PageParameters();
        pageParameters.set("page", 1);
        pageParameters.set("paging2", 0);
        getWicketTester().assertBookmarkablePageLink("form:content:container:1:navigator:navigation:0:pageLink", CmsPage.class, pageParameters);
        getWicketTester().assertEnabled("form:content:container:1:navigator:navigation:0:pageLink");
        getWicketTester().assertLabel("form:content:container:1:navigator:navigation:0:pageLink:pageNumber", "1");
        pageParameters = new PageParameters();
        pageParameters.set("page", 1);
        pageParameters.set("paging2", 1);
        getWicketTester().assertBookmarkablePageLink("form:content:container:1:navigator:navigation:1:pageLink", CmsPage.class, pageParameters);
        getWicketTester().assertDisabled("form:content:container:1:navigator:navigation:1:pageLink");
        getWicketTester().assertLabel("form:content:container:1:navigator:navigation:1:pageLink:pageNumber", "2");
        pageParameters = new PageParameters();
        pageParameters.set("page", 1);
        pageParameters.set("paging2", 0);
        getWicketTester().assertEnabled("form:content:container:1:navigator:first");
        getWicketTester().assertBookmarkablePageLink("form:content:container:1:navigator:first", CmsPage.class, pageParameters);
        pageParameters = new PageParameters();
        pageParameters.set("page", 1);
        pageParameters.set("paging2", 0);
        getWicketTester().assertEnabled("form:content:container:1:navigator:prev");
        getWicketTester().assertBookmarkablePageLink("form:content:container:1:navigator:prev", CmsPage.class, pageParameters);
        pageParameters = new PageParameters();
        pageParameters.set("page", 1);
        pageParameters.set("paging2", 1);
        getWicketTester().assertDisabled("form:content:container:1:navigator:next");
        getWicketTester().assertBookmarkablePageLink("form:content:container:1:navigator:next", CmsPage.class, pageParameters);
        pageParameters = new PageParameters();
        pageParameters.set("page", 1);
        pageParameters.set("paging2", 1);
        getWicketTester().assertDisabled("form:content:container:1:navigator:last");
        getWicketTester().assertBookmarkablePageLink("form:content:container:1:navigator:last", CmsPage.class, pageParameters);

        getWicketTester().assertLabel("form:content:container:1:table:rows:1:cells:1:cell", "2");

        pageParameters = new PageParameters();
        pageParameters.set("page", 1);
        pageParameters.set("paging8", 0);
        getWicketTester().assertBookmarkablePageLink("form:content:container:2:navigator:navigation:0:pageLink", CmsPage.class, pageParameters);
        getWicketTester().assertDisabled("form:content:container:2:navigator:navigation:0:pageLink");
        getWicketTester().assertLabel("form:content:container:2:navigator:navigation:0:pageLink:pageNumber", "1");

        getWicketTester().assertLabel("form:content:container:2:table:rows:1:cells:1:cell", "1");
        getWicketTester().assertLabel("form:content:container:2:table:rows:2:cells:1:cell", "2");

        getWicketTester().clickLink("form:content:container:2:navigator:next");

        getWicketTester().assertLabel("form:content:container:1:table:rows:1:cells:1:cell", "1");

        pageParameters = new PageParameters();
        pageParameters.set("page", 1);
        pageParameters.set("paging8", 0);
        getWicketTester().assertBookmarkablePageLink("form:content:container:2:navigator:navigation:0:pageLink", CmsPage.class, pageParameters);
        getWicketTester().assertEnabled("form:content:container:2:navigator:navigation:0:pageLink");
        getWicketTester().assertLabel("form:content:container:2:navigator:navigation:0:pageLink:pageNumber", "1");

        getWicketTester().assertLabel("form:content:container:2:table:rows:1:cells:1:cell", "2");

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void borderWrapper() throws Exception {
        configureSimplePlanetNoResources();

        // Setup CMS content
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage.setName("Home");

        DbContentList dbContentList = new DbContentList();
        dbContentList.setBorderCss("borderCSS");
        dbContentList.init(userService);
        dbPage.setContentAndAccessWrites(dbContentList);
        dbContentList.setSpringBeanName("serverItemTypeService");
        dbContentList.setContentProviderGetter("dbItemTypeCrud");

        CrudListChildServiceHelper<DbContent> columnCrud = dbContentList.getColumnsCrud();
        DbExpressionProperty column = (DbExpressionProperty) columnCrud.createDbChild(DbExpressionProperty.class);
        column.setExpression("name");
        DbContentDetailLink detailLink = (DbContentDetailLink) columnCrud.createDbChild(DbContentDetailLink.class);
        detailLink.setName("Details");

        pageCrud.updateDbChild(dbPage);

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Activate
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().debugComponentTrees();
        getWicketTester().assertVisible("form:content:border");
        getWicketTester().assertVisible("form:content:border:borderContent");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testDbExpressionPropertyTypes() throws Exception {
        configureSimplePlanetNoResources();

        // Setup CMS content
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage.setName("Home");
        DbContentContainer dbContentContainer = new DbContentContainer();
        dbContentContainer.init(userService);
        dbPage.setContentAndAccessWrites(dbContentContainer);
        // Integer property
        DbExpressionProperty expressionProperty = (DbExpressionProperty) dbContentContainer.getContentCrud().createDbChild(DbExpressionProperty.class);
        expressionProperty.setSpringBeanName("testCmsBean");
        expressionProperty.setExpression("double1");
        expressionProperty.setOptionalType(DbExpressionProperty.Type.ROUNDED_DOWN_INTEGER);

        expressionProperty = (DbExpressionProperty) dbContentContainer.getContentCrud().createDbChild(DbExpressionProperty.class);
        expressionProperty.setSpringBeanName("testCmsBean");
        expressionProperty.setExpression("double2");
        expressionProperty.setOptionalType(DbExpressionProperty.Type.ROUNDED_DOWN_INTEGER);

        expressionProperty = (DbExpressionProperty) dbContentContainer.getContentCrud().createDbChild(DbExpressionProperty.class);
        expressionProperty.setSpringBeanName("testCmsBean");
        expressionProperty.setExpression("double3");
        expressionProperty.setOptionalType(DbExpressionProperty.Type.ROUNDED_DOWN_INTEGER);

        expressionProperty = (DbExpressionProperty) dbContentContainer.getContentCrud().createDbChild(DbExpressionProperty.class);
        expressionProperty.setSpringBeanName("testCmsBean");
        expressionProperty.setExpression("double4");
        expressionProperty.setOptionalType(DbExpressionProperty.Type.ROUNDED_DOWN_INTEGER);

        expressionProperty = (DbExpressionProperty) dbContentContainer.getContentCrud().createDbChild(DbExpressionProperty.class);
        expressionProperty.setSpringBeanName("testCmsBean");
        expressionProperty.setExpression("integer1");
        expressionProperty.setOptionalType(DbExpressionProperty.Type.ROUNDED_DOWN_INTEGER);

        expressionProperty = (DbExpressionProperty) dbContentContainer.getContentCrud().createDbChild(DbExpressionProperty.class);
        expressionProperty.setSpringBeanName("testCmsBean");
        expressionProperty.setExpression("integer2");
        expressionProperty.setOptionalType(DbExpressionProperty.Type.ROUNDED_DOWN_INTEGER);

        pageCrud.updateDbChild(dbPage);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Activate
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertLabel("form:content:container:1", "1");
        getWicketTester().assertLabel("form:content:container:2", "2");
        getWicketTester().assertLabel("form:content:container:3", "5");
        getWicketTester().assertLabel("form:content:container:4", "4");
        getWicketTester().assertLabel("form:content:container:5", "10");
        getWicketTester().assertLabel("form:content:container:6", "11");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testUglyBeanIdPathElement4LevelTask() throws Exception {
        configureMultiplePlanetsAndLevels();

        // Setup CMS content
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();

        // Setup the level page
        DbPage dbLevelPage = pageCrud.createDbChild();
        dbLevelPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbLevelPage.setName("Level");

        DbContentList levelContentList = new DbContentList();
        dbLevelPage.setContentAndAccessWrites(levelContentList);
        levelContentList.setRowsPerPage(5);
        levelContentList.init(userService);
        levelContentList.setSpringBeanName("userGuidanceService");
        levelContentList.setContentProviderGetter("dbLevelCrud");

        DbContentBook dbContentBook = levelContentList.getContentBookCrud().createDbChild();
        dbContentBook.setClassName("com.btxtech.game.services.utg.DbLevel");
        CrudListChildServiceHelper<DbContentRow> rowCrud = dbContentBook.getRowCrud();

        DbContentRow dbTaskRow = rowCrud.createDbChild();
        DbContentList taskContentList = new DbContentList();
        dbTaskRow.setDbContent(taskContentList);
        taskContentList.init(userService);
        taskContentList.setParent(dbTaskRow);
        taskContentList.setContentProviderGetter("levelTaskCrud");

        dbContentBook = taskContentList.getContentBookCrud().createDbChild();
        dbContentBook.setClassName("com.btxtech.game.services.utg.DbLevelTask");
        rowCrud = dbContentBook.getRowCrud();

        DbContentRow dbContentRow = rowCrud.createDbChild();
        dbContentRow.setName("Name");
        DbExpressionProperty expProperty = new DbExpressionProperty();
        expProperty.setParent(dbContentRow);
        expProperty.setExpression("name");
        expProperty.setEditorType(DbExpressionProperty.EditorType.HTML_AREA);
        dbContentRow.setDbContent(expProperty);

        pageCrud.updateDbChild(dbLevelPage);

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Activate
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userGuidanceService.getDbLevel(); // set level for new user
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertRenderedPage(CmsPage.class);
        PageParameters parameters = new PageParameters();
        parameters.add(CmsUtil.SECTION_ID, CmsUtil.LEVEL_TASK_SECTION);
        parameters.add(CmsUtil.CHILD_ID, Integer.toString(TEST_LEVEL_TASK_4_3_SIMULATED_ID));
        getWicketTester().startPage(CmsPage.class, parameters);
        getWicketTester().assertRenderedPage(CmsPage.class);
        getWicketTester().assertLabel("form:content:table:rows:1:cells:2:cell", TEST_LEVEL_TASK_4_3_SIMULATED_NAME);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testEmailVerificationPageOk() throws Exception {
        configureSimplePlanetNoResources();
        startFakeMailServer();

        // Setup CMS content
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage.setName("Home");
        pageCrud.updateDbChild(dbPage);

        DbPage dbEmail = pageCrud.createDbChild();
        dbEmail.setPredefinedType(CmsUtil.CmsPredefinedPage.EMAIL_VERIFICATION);
        dbEmail.setName("Email");
        DbContentPlugin contentPlugin = new DbContentPlugin();
        contentPlugin.setPluginEnum(PluginEnum.EMAIL_VERIFICATION);
        contentPlugin.init(userService);
        dbEmail.setContentAndAccessWrites(contentPlugin);
        pageCrud.updateDbChild(dbEmail);
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Setup user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        registerService.register("U1", "xxx", "xxx", "xxx@yyy.com");
        User user = userService.getUser();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // getWicketTester().setupRequestAndResponse();
        getWicketTester().getSession().setLocale(Locale.ENGLISH);
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertRenderedPage(CmsPage.class);

        PageParameters pageParameters = cmsUiService.getPredefinedDbPageParameters(CmsUtil.CmsPredefinedPage.EMAIL_VERIFICATION);
        pageParameters.set(CmsUtil.EMAIL_VERIFICATION_KEY, user.getVerificationId());
        getWicketTester().startPage(CmsPage.class, pageParameters);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertTrue(get(User.class, user.getId()).isVerified());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();


        stopFakeMailServer();
    }

    @Test
    @DirtiesContext
    public void testParameterMountingEmailVerificationPageOk() throws Exception {
        configureSimplePlanetNoResources();
        startFakeMailServer();

        // Setup CMS content
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage.setName("Home");
        pageCrud.updateDbChild(dbPage);

        DbPage dbEmail = pageCrud.createDbChild();
        dbEmail.setPredefinedType(CmsUtil.CmsPredefinedPage.EMAIL_VERIFICATION);
        dbEmail.setName("Email");
        DbContentPlugin contentPlugin = new DbContentPlugin();
        contentPlugin.setPluginEnum(PluginEnum.EMAIL_VERIFICATION);
        contentPlugin.init(userService);
        dbEmail.setContentAndAccessWrites(contentPlugin);
        pageCrud.updateDbChild(dbEmail);
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Setup user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        registerService.register("U1", "xxx", "xxx", "xxx@yyy.com");
        User user = userService.getUser();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().getSession().setLocale(Locale.ENGLISH);
        getWicketTester().executeUrl("game_cms/page/" + dbEmail.getId() + "/verification_code/" + user.getVerificationId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertTrue(get(User.class, user.getId()).isVerified());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        stopFakeMailServer();
    }

    @Test
    @DirtiesContext
    public void testEmailVerificationPageAlreadyVerified() throws Exception {
        configureSimplePlanetNoResources();
        startFakeMailServer();

        // Setup CMS content
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage.setName("Home");
        pageCrud.updateDbChild(dbPage);

        DbPage dbEmail = pageCrud.createDbChild();
        dbEmail.setPredefinedType(CmsUtil.CmsPredefinedPage.EMAIL_VERIFICATION);
        dbEmail.setName("Email");
        DbContentPlugin contentPlugin = new DbContentPlugin();
        contentPlugin.setPluginEnum(PluginEnum.EMAIL_VERIFICATION);
        contentPlugin.init(userService);
        dbEmail.setContentAndAccessWrites(contentPlugin);
        pageCrud.updateDbChild(dbEmail);
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Setup user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        registerService.register("U1", "xxx", "xxx", "xxx@yyy.com");
        User user = userService.getUser();
        registerService.onVerificationPageCalled(user.getVerificationId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // getWicketTester().setupRequestAndResponse();
        getWicketTester().getSession().setLocale(Locale.ENGLISH);

        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertRenderedPage(CmsPage.class);

        PageParameters pageParameters = cmsUiService.getPredefinedDbPageParameters(CmsUtil.CmsPredefinedPage.EMAIL_VERIFICATION);
        pageParameters.set(CmsUtil.EMAIL_VERIFICATION_KEY, user.getVerificationId());
        getWicketTester().startPage(CmsPage.class, pageParameters);
        getWicketTester().assertLabel("form:content:border:borderContent:message", "The email confirmation link you followed has already been verified.");
        getWicketTester().debugComponentTrees();

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        stopFakeMailServer();
    }

    @Test
    @DirtiesContext
    public void testEmailVerificationPageInvalid() throws Exception {
        configureSimplePlanetNoResources();

        // Setup CMS content
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage.setName("Home");
        pageCrud.updateDbChild(dbPage);

        DbPage dbEmail = pageCrud.createDbChild();
        dbEmail.setPredefinedType(CmsUtil.CmsPredefinedPage.EMAIL_VERIFICATION);
        dbEmail.setName("Email");
        DbContentPlugin contentPlugin = new DbContentPlugin();
        contentPlugin.setPluginEnum(PluginEnum.EMAIL_VERIFICATION);
        contentPlugin.init(userService);
        dbEmail.setContentAndAccessWrites(contentPlugin);
        pageCrud.updateDbChild(dbEmail);
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // getWicketTester().setupRequestAndResponse();
        getWicketTester().getSession().setLocale(Locale.ENGLISH);
        getWicketTester().startPage(CmsPage.class);
        getWicketTester().assertRenderedPage(CmsPage.class);

        PageParameters pageParameters = cmsUiService.getPredefinedDbPageParameters(CmsUtil.CmsPredefinedPage.EMAIL_VERIFICATION);
        pageParameters.set(CmsUtil.EMAIL_VERIFICATION_KEY, "abcedefgahijk");
        getWicketTester().startPage(CmsPage.class, pageParameters);
        getWicketTester().assertLabel("form:content:border:borderContent:message", "The email confirmation link you followed is invalid. Please re-register.");

        getWicketTester().debugComponentTrees();

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testSetMessageResponsePage() throws Exception {
        configureSimplePlanetNoResources();
        // Setup
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage.setName("Home");
        pageCrud.updateDbChild(dbPage);
        DbPage dbMessagePage = pageCrud.createDbChild();
        dbMessagePage.setPredefinedType(CmsUtil.CmsPredefinedPage.MESSAGE);
        dbMessagePage.setName("Message Page");
        pageCrud.updateDbChild(dbMessagePage);
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Page page = getWicketTester().startPage(CmsPage.class);

        final MutableBoolean mutableBoolean = new MutableBoolean(false);
        RequestCycle.get().getListeners().add(new AbstractRequestCycleListener() {

            @Override
            public void onRequestHandlerScheduled(RequestCycle cycle, IRequestHandler handler) {
                RenderPageRequestHandler requestHandler = (RenderPageRequestHandler) handler;
                Assert.assertEquals(CmsPage.class, requestHandler.getPageClass());
                Assert.assertEquals("loginAlready", requestHandler.getPageParameters().get("messageId").toString());
                Assert.assertEquals("KUH", requestHandler.getPageParameters().get("messageAdditional").toString());
                mutableBoolean.setValue(true);
            }
        });
        cmsUiService.setMessageResponsePage(page, "loginAlready", "KUH");
        Assert.assertTrue(mutableBoolean.booleanValue());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testMessage() throws Exception {
        configureSimplePlanetNoResources();
        // Setup
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage.setName("Home");
        pageCrud.updateDbChild(dbPage);
        DbPage dbMessagePage = pageCrud.createDbChild();
        dbMessagePage.setPredefinedType(CmsUtil.CmsPredefinedPage.MESSAGE);
        dbMessagePage.setName("Message Page");
        pageCrud.updateDbChild(dbMessagePage);
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify En parameter
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().getSession().setLocale(Locale.ENGLISH);
        PageParameters pageParameters = cmsUiService.getPredefinedDbPageParameters(CmsUtil.CmsPredefinedPage.MESSAGE);
        pageParameters.set(CmsPage.MESSAGE_ID, "loginAlready");
        pageParameters.set(CmsPage.MESSAGE_ADDITIONAL_PARAMETER, "KUH");
        getWicketTester().startPage(CmsPage.class, pageParameters);
        getWicketTester().assertLabel("form:content:border:borderContent:message", "Already logged in as: KUH");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify chinese parameter
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().getSession().setLocale(Locale.CHINESE);
        pageParameters = cmsUiService.getPredefinedDbPageParameters(CmsUtil.CmsPredefinedPage.MESSAGE);
        pageParameters.set(CmsPage.MESSAGE_ID, "loginAlready");
        pageParameters.set(CmsPage.MESSAGE_ADDITIONAL_PARAMETER, "KUH");
        getWicketTester().startPage(CmsPage.class, pageParameters);
        getWicketTester().assertLabel("form:content:border:borderContent:message", "Already logged in as: KUH");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify german parameter
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().getSession().setLocale(Locale.GERMAN);
        pageParameters = cmsUiService.getPredefinedDbPageParameters(CmsUtil.CmsPredefinedPage.MESSAGE);
        pageParameters.set(CmsPage.MESSAGE_ID, "loginAlready");
        pageParameters.set(CmsPage.MESSAGE_ADDITIONAL_PARAMETER, "KUH");
        getWicketTester().startPage(CmsPage.class, pageParameters);
        getWicketTester().assertLabel("form:content:border:borderContent:message", "Bereits KUH als eingeloggt");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify En
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().getSession().setLocale(Locale.ENGLISH);
        pageParameters = cmsUiService.getPredefinedDbPageParameters(CmsUtil.CmsPredefinedPage.MESSAGE);
        pageParameters.set(CmsPage.MESSAGE_ID, "registerConfirmationInvalid");
        getWicketTester().startPage(CmsPage.class, pageParameters);
        getWicketTester().assertLabel("form:content:border:borderContent:message", "The email confirmation link you followed is invalid. Please re-register.");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify En no valid key
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().getSession().setLocale(Locale.ENGLISH);
        pageParameters = cmsUiService.getPredefinedDbPageParameters(CmsUtil.CmsPredefinedPage.MESSAGE);
        pageParameters.set(CmsPage.MESSAGE_ID, "_____________thisIsAnInvalidKey___________");
        getWicketTester().startPage(CmsPage.class, pageParameters);
        getWicketTester().assertLabel("form:content:border:borderContent:message", "[Warning: Property for '_____________thisIsAnInvalidKey___________' not found]");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify umlaute
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // getWicketTester().setupRequestAndResponse();
        getWicketTester().getSession().setLocale(Locale.GERMAN);
        pageParameters = cmsUiService.getPredefinedDbPageParameters(CmsUtil.CmsPredefinedPage.MESSAGE);
        pageParameters.set(CmsPage.MESSAGE_ID, "registerUserExists");
        getWicketTester().startPage(CmsPage.class, pageParameters);
        getWicketTester().assertLabel("form:content:border:borderContent:message", "Der gewünschte Benutzername existiert bereits");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testFallbackLocale() throws Exception {
        getWicketTester().getSession().setLocale(Locale.CHINESE);
        configureSimplePlanetNoResources();
        // Setup
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage.setName("Home");
        pageCrud.updateDbChild(dbPage);
        DbPage dbMessagePage = pageCrud.createDbChild();
        dbMessagePage.setPredefinedType(CmsUtil.CmsPredefinedPage.MESSAGE);
        dbMessagePage.setName("Message Page");
        pageCrud.updateDbChild(dbMessagePage);
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify En parameter
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        PageParameters pageParameters = cmsUiService.getPredefinedDbPageParameters(CmsUtil.CmsPredefinedPage.MESSAGE);
        pageParameters.set(CmsPage.MESSAGE_ID, "loginAlready");
        pageParameters.set(CmsPage.MESSAGE_ADDITIONAL_PARAMETER, "KUH");
        getWicketTester().startPage(CmsPage.class, pageParameters);
        getWicketTester().assertLabel("form:content:border:borderContent:message", "Already logged in as: KUH");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testTrackingCookie() throws Exception {
        configureSimplePlanetNoResources();

        // Setup CMS content
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbPage> pageCrud = cmsService.getPageCrudRootServiceHelper();
        DbPage dbPage = pageCrud.createDbChild();
        dbPage.setPredefinedType(CmsUtil.CmsPredefinedPage.HOME);
        dbPage.setName("Home");
        pageCrud.updateDbChild(dbPage);
        cmsService.activateCms();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        // Setup first access
        beginHttpRequestAndOpenSessionInViewFilter();
        setWicketParameterTrackingCookie("aswedrfvc1234");
        setWicketParameterTrackingCookieNeeded(true);
        getWicketTester().startPage(CmsPage.class);
        MockHttpServletResponse response = (MockHttpServletResponse) getWicketTester().getLastResponse();
        Assert.assertEquals("aswedrfvc1234", session.getTrackingCookieId());
        assertCookie(response, "cookieId", "aswedrfvc1234", Integer.MAX_VALUE);
        endHttpRequestAndOpenSessionInViewFilter();
        // Second access
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().startPage(CmsPage.class);
        response = (MockHttpServletResponse) getWicketTester().getLastResponse();
        Assert.assertNotNull(session.getTrackingCookieId());
        assertCookieNotSet(response, "cookieId");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        // Setup first access with cookie set
        beginHttpRequestAndOpenSessionInViewFilter();
        setWicketParameterTrackingCookieNeeded(true);
        setWicketParameterTrackingCookie("qwertzui");
        getWicketTester().startPage(CmsPage.class);
        response = (MockHttpServletResponse) getWicketTester().getLastResponse();
        Assert.assertEquals("qwertzui", session.getTrackingCookieId());
        assertCookie(response, "cookieId", "qwertzui", Integer.MAX_VALUE);
        endHttpRequestAndOpenSessionInViewFilter();
        // Second access
        beginHttpRequestAndOpenSessionInViewFilter();
        getWicketTester().startPage(CmsPage.class);
        response = (MockHttpServletResponse) getWicketTester().getLastResponse();
        Assert.assertNotNull(session.getTrackingCookieId());
        assertCookieNotSet(response, "cookieId");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    private void assertCookie(MockHttpServletResponse response, String name, String expectedValue, int maxAgeInSeconds) {
        for (Cookie cookie : response.getCookies()) {
            if (cookie.getName().equals(name)) {
                Assert.assertEquals(expectedValue, cookie.getValue());
                Assert.assertEquals(maxAgeInSeconds, cookie.getMaxAge());
                return;
            }
        }
        Assert.fail("No such cookie: " + name);
    }

    private void assertCookieNotSet(MockHttpServletResponse response, String name) {
        for (Cookie cookie : response.getCookies()) {
            if (cookie.getName().equals(name)) {
                Assert.fail("Cookie not expected: " + name);
            }
        }
    }
}
