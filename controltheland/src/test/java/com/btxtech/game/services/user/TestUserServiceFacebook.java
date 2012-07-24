package com.btxtech.game.services.user;

import com.btxtech.game.jsre.common.CmsUtil;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.cms.CmsService;
import com.btxtech.game.services.cms.page.DbPage;
import com.btxtech.game.services.messenger.MessengerService;
import com.btxtech.game.services.socialnet.facebook.FacebookSignedRequest;
import com.btxtech.game.wicket.WicketApplication;
import com.btxtech.game.wicket.WicketAuthenticatedWebSession;
import com.btxtech.game.wicket.pages.cms.CmsPage;
import junit.framework.Assert;
import org.apache.wicket.Page;
import org.apache.wicket.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.HttpSessionStore;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.session.ISessionStore;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.tester.DummyHomePage;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;

import javax.servlet.http.HttpServletResponse;

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
    private WicketApplication wicketApplication;
    private WicketTester tester;

    @Before
    public void setUp() {
        tester = new WicketTester(wicketApplication);
    }

    @Test
    @DirtiesContext
    public void createLogin() throws Exception {
        configureRealGame();

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
        tester.startPage(CmsPage.class);
        FacebookSignedRequest facebookSignedRequest = new FacebookSignedRequest(null, 0, null, null, "12345");
        Assert.assertFalse(userService.isFacebookUserRegistered(facebookSignedRequest));
        Assert.assertFalse(userService.isFacebookLoggedIn(facebookSignedRequest));
        userService.createAndLoginFacebookUser(facebookSignedRequest, "nickname");
        Assert.assertTrue(userService.isFacebookLoggedIn(facebookSignedRequest));
        Assert.assertTrue(userService.isFacebookUserRegistered(facebookSignedRequest));
        Assert.assertEquals("nickname", userService.getUser().getUsername());
        userService.onSessionTimedOut(getUserState());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Login FB user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertTrue(userService.isFacebookUserRegistered(facebookSignedRequest));
        Assert.assertFalse(userService.isFacebookLoggedIn(facebookSignedRequest));
        userService.loginFacebookUser(facebookSignedRequest);
        Assert.assertTrue(userService.isFacebookLoggedIn(facebookSignedRequest));
        Assert.assertTrue(userService.isFacebookUserRegistered(facebookSignedRequest));
        Assert.assertEquals("nickname", userService.getUser().getUsername());
        userService.onSessionTimedOut(getUserState());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }
}