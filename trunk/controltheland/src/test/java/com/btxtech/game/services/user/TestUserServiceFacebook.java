package com.btxtech.game.services.user;

import com.btxtech.game.jsre.common.CmsUtil;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.cms.CmsService;
import com.btxtech.game.services.cms.page.DbPage;
import com.btxtech.game.services.socialnet.facebook.FacebookSignedRequest;
import com.btxtech.game.wicket.WicketApplication;
import com.btxtech.game.wicket.pages.cms.CmsPage;
import junit.framework.Assert;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
    private WicketApplication wicketApplication;
    private WicketTester tester;

    @Before
    public void setUp() {
        tester = new WicketTester(wicketApplication);
    }

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
        tester.startPage(CmsPage.class);
        FacebookSignedRequest facebookSignedRequest = new FacebookSignedRequest(null, 0, null, null, "12345");
        Assert.assertFalse(userService.isFacebookUserRegistered(facebookSignedRequest));
        Assert.assertFalse(userService.isFacebookLoggedIn(facebookSignedRequest));
        Assert.assertTrue(userService.getAuthorities().isEmpty());
        userService.createAndLoginFacebookUser(facebookSignedRequest, "nickname");
        Assert.assertTrue(userService.isFacebookLoggedIn(facebookSignedRequest));
        Assert.assertTrue(userService.isFacebookUserRegistered(facebookSignedRequest));
        Assert.assertFalse(userService.getAuthorities().isEmpty());
        Assert.assertEquals("nickname", userService.getUser().getUsername());
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
        Assert.assertFalse(userService.isFacebookUserRegistered(facebookSignedRequest));
        Assert.assertFalse(userService.isFacebookLoggedIn(facebookSignedRequest));
        Assert.assertTrue(userService.getAuthorities().isEmpty());
        userService.createAndLoginFacebookUser(facebookSignedRequest, "nickname");
        Assert.assertTrue(userService.isFacebookLoggedIn(facebookSignedRequest));
        Assert.assertTrue(userService.isFacebookUserRegistered(facebookSignedRequest));
        Assert.assertFalse(userService.getAuthorities().isEmpty());
        Assert.assertEquals("nickname", userService.getUser().getUsername());
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
        Assert.assertFalse(userService.getAuthorities().isEmpty());
        endHttpRequestAndOpenSessionInViewFilter();
        // Same session
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertTrue(userService.isFacebookLoggedIn(facebookSignedRequest));
        Assert.assertTrue(userService.isFacebookUserRegistered(facebookSignedRequest));
        Assert.assertEquals("nickname", userService.getUser().getUsername());
        Assert.assertFalse(userService.getAuthorities().isEmpty());
        userService.onSessionTimedOut(getUserState());
        endHttpRequestAndOpenSessionInViewFilter();

        endHttpSession();
    }

}