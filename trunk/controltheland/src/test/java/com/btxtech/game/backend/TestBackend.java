package com.btxtech.game.backend;

import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.user.SecurityRoles;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.wicket.WicketAuthenticatedWebSession;
import com.btxtech.game.wicket.pages.mgmt.MgmtPage;
import junit.framework.Assert;
import org.apache.wicket.authentication.AuthenticatedWebSession;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Collections;

/**
 * User: beat
 * Date: 23.09.12
 * Time: 22:57
 */
public class TestBackend extends AbstractServiceTest {
    @Autowired
    private UserService userService;

    @Test
    @DirtiesContext
    public void testPagesNoRights() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        try {
            getWicketTester().startPage(MgmtPage.class);
            Assert.fail();
        } catch (Exception e) {
            // Expected
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testPages() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("Admin", "admin");
        User user = userService.getUser();
        user.setRoles(Collections.singleton(SecurityRoles.ROLE_ADMINISTRATOR));
        userService.save(user);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("Admin", "admin");
        try {
            // First call crashes. Wickets need to set up wicket-session first
            getWicketTester().startPage(MgmtPage.class);
        } catch (Exception e) {
            // Ignore
        }
        ((WicketAuthenticatedWebSession) AuthenticatedWebSession.get()).setSignIn(true);
        getWicketTester().startPage(MgmtPage.class);
        getWicketTester().assertRenderedPage(MgmtPage.class);

        for (MgmtPage.LinkAndName toolPage : MgmtPage.toolPages) {
            getWicketTester().startPage(toolPage.getClazz());
            getWicketTester().assertRenderedPage(toolPage.getClazz());
        }

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }
}
