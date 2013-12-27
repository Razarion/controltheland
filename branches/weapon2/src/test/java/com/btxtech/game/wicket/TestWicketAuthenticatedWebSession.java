package com.btxtech.game.wicket;

import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.wicket.WebCommon;
import com.btxtech.game.wicket.WicketAuthenticatedWebSession;
import junit.framework.Assert;
import org.apache.wicket.request.http.WebRequest;
import org.easymock.EasyMock;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;

import javax.servlet.http.Cookie;
import java.util.Arrays;
import java.util.Locale;

/**
 * User: beat
 * Date: 25.01.13
 * Time: 17:03
 */
public class TestWicketAuthenticatedWebSession extends AbstractServiceTest {
    @Test
    @DirtiesContext
    public void testTrackingCookie() throws Exception {
        WebRequest request = EasyMock.createNiceMock(WebRequest.class);
        EasyMock.expect(request.getLocale()).andReturn(Locale.ENGLISH);
        EasyMock.replay(request);
        WicketAuthenticatedWebSession wicketSession = new WicketAuthenticatedWebSession(request);
        Assert.assertNotNull(wicketSession.getTrackingCookieId());
        Assert.assertTrue(wicketSession.isNewUserTracking());
    }


    @Test
    @DirtiesContext
    public void testNewUser() throws Exception {
        WebRequest request = EasyMock.createNiceMock(WebRequest.class);
        EasyMock.expect(request.getLocale()).andReturn(Locale.ENGLISH).anyTimes();
        EasyMock.expect(request.getCookies()).andReturn(Arrays.asList(new Cookie(WebCommon.RAZARION_COOKIE_ID, "qwertzui"))).anyTimes();
        EasyMock.replay(request);
        WicketAuthenticatedWebSession wicketSession = new WicketAuthenticatedWebSession(request);
        Assert.assertFalse(wicketSession.isNewUserTracking());
    }

}
