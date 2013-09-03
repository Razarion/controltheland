package com.btxtech.game.services.mgmt;

import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.user.User;
import junit.framework.Assert;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.pages.AccessDeniedPage;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.subethamail.wiser.WiserMessage;

import java.util.List;

/**
 * User: beat
 * Date: 01.02.13
 * Time: 14:51
 */
public class TestMgmtService extends AbstractServiceTest {
    @Autowired
    private MgmtService mgmtService;

    @Test
    @DirtiesContext
    public void sendEmail() throws Exception {
        startFakeMailServer();
        User user = new User();
        user.registerUser("U1", "xxx", "test@email.com", null);
        mgmtService.sendEmail(user, "hallo", "<html><body><h3>Hi ${USER.username},</h3><div>Hi hi hi</div></body></html>");
        List<WiserMessage> wiserMessages = getFakeMailServer().getMessages();
        stopFakeMailServer();
        Assert.assertEquals(1, wiserMessages.size());
        WiserMessage wiserMessage = wiserMessages.get(0);
        Assert.assertEquals("test@email.com", wiserMessage.getEnvelopeReceiver());
        Assert.assertEquals("no-reply@razarion.com", wiserMessage.getEnvelopeSender());
        Assert.assertEquals("hallo", wiserMessage.getMimeMessage().getSubject());
        Assert.assertEquals("text/html;charset=UTF-8", wiserMessage.getMimeMessage().getContentType());
        Assert.assertEquals("<html><body><h3>Hi U1,</h3><div>Hi hi hi</div></body></html>", ((String) wiserMessage.getMimeMessage().getContent()).trim());
    }

    @Test
    @DirtiesContext
    public void sendEmailUmlaute() throws Exception {
        startFakeMailServer();
        User user = new User();
        user.registerUser("U1", "xxx", "test@email.com", null);
        mgmtService.sendEmail(user, "halloüöä", "<html><body><h3>Hi öö ${USER.username},</h3><div>Hi ü hi öhi</div></body></html>");
        List<WiserMessage> wiserMessages = getFakeMailServer().getMessages();
        stopFakeMailServer();
        Assert.assertEquals(1, wiserMessages.size());
        WiserMessage wiserMessage = wiserMessages.get(0);
        Assert.assertEquals("test@email.com", wiserMessage.getEnvelopeReceiver());
        Assert.assertEquals("no-reply@razarion.com", wiserMessage.getEnvelopeSender());
        Assert.assertEquals("halloüöä", wiserMessage.getMimeMessage().getSubject());
        Assert.assertEquals("text/html;charset=UTF-8", wiserMessage.getMimeMessage().getContentType());
        Assert.assertEquals("<html><body><h3>Hi öö U1,</h3><div>Hi ü hi öhi</div></body></html>", ((String) wiserMessage.getMimeMessage().getContent()).trim());
    }

    @Test
    @DirtiesContext
    public void saveServerDebugNoSession() throws Exception {
        Exception e = new Exception("Hallogalli");
        long before1 = System.currentTimeMillis();
        mgmtService.saveServerDebug("CAT1", e);
        long after1 = System.currentTimeMillis();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DbServerDebugEntry> entries = HibernateUtil.loadAll(getSessionFactory(), DbServerDebugEntry.class);
        Assert.assertEquals(1, entries.size());
        DbServerDebugEntry entry = entries.get(0);
        Assert.assertTrue(entry.getTimeStamp().getTime() >= before1);
        Assert.assertTrue(entry.getTimeStamp().getTime() <= after1);
        Assert.assertEquals("CAT1", entry.getCategory());
        Assert.assertEquals("Hallogalli", entry.getThrowableMessage());
        Assert.assertEquals(Thread.currentThread().getName(), entry.getThread());
        Assert.assertEquals(ExceptionUtils.getFullStackTrace(e), entry.getStackTrace());
        Assert.assertNull(entry.getCausePage());
        Assert.assertNull(entry.getUserAgent());
        Assert.assertNull(entry.getUserName());
        Assert.assertNull(entry.getRequestUri());
        Assert.assertNull(entry.getQueryString());
        Assert.assertNull(entry.getSessionId());
        Assert.assertNull(entry.getRemoteAddress());
        Assert.assertNull(entry.getReferer());
        Assert.assertNull(entry.getMessage());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void saveServerDebugInSession() throws Exception {
        configureSimplePlanetNoResources();

        Exception e = new Exception("Hallogalli");
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        String sessionID = getHttpSessionId();
        getMockHttpServletRequest().setRequestURI("/test");
        getMockHttpServletRequest().setQueryString("xxx=yyy");
        getMockHttpServletRequest().addHeader("Referer", "/eee/yyyy");
        getMockHttpServletRequest().addHeader("user-agent", "mozilla");
        createAndLoginUser("U1");
        long before1 = System.currentTimeMillis();
        mgmtService.saveServerDebug("CAT1", e);
        long after1 = System.currentTimeMillis();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DbServerDebugEntry> entries = HibernateUtil.loadAll(getSessionFactory(), DbServerDebugEntry.class);
        Assert.assertEquals(1, entries.size());
        DbServerDebugEntry entry = entries.get(0);
        Assert.assertTrue(entry.getTimeStamp().getTime() >= before1);
        Assert.assertTrue(entry.getTimeStamp().getTime() <= after1);
        Assert.assertEquals("CAT1", entry.getCategory());
        Assert.assertEquals("Hallogalli", entry.getThrowableMessage());
        Assert.assertEquals(Thread.currentThread().getName(), entry.getThread());
        Assert.assertEquals(ExceptionUtils.getFullStackTrace(e), entry.getStackTrace());
        Assert.assertNull(entry.getCausePage());
        Assert.assertEquals("mozilla", entry.getUserAgent());
        Assert.assertEquals("U1", entry.getUserName());
        Assert.assertEquals("/test", entry.getRequestUri());
        Assert.assertEquals("xxx=yyy", entry.getQueryString());
        Assert.assertEquals(sessionID, entry.getSessionId());
        Assert.assertEquals(MockHttpServletRequest.DEFAULT_REMOTE_ADDR, entry.getRemoteAddress());
        Assert.assertEquals("/eee/yyyy", entry.getReferer());
        Assert.assertNull(entry.getMessage());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void saveServerDebugWicket() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setRequestURI("/test");
        mockHttpServletRequest.setQueryString("xxx=yyy");
        mockHttpServletRequest.addHeader("user-agent", "mozilla");
        mockHttpServletRequest.addHeader("Referer", "/eee/yyyy");
        String sessionId = mockHttpServletRequest.getSession().getId();
        createAndLoginUser("U100");
        Exception e = new Exception("Hallogalli11");
        Page causePage = new AccessDeniedPage();
        long before1 = System.currentTimeMillis();
        mgmtService.saveServerDebug("CAT33", mockHttpServletRequest, causePage, e);
        long after1 = System.currentTimeMillis();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DbServerDebugEntry> entries = HibernateUtil.loadAll(getSessionFactory(), DbServerDebugEntry.class);
        Assert.assertEquals(1, entries.size());
        DbServerDebugEntry entry = entries.get(0);
        Assert.assertTrue(entry.getTimeStamp().getTime() >= before1);
        Assert.assertTrue(entry.getTimeStamp().getTime() <= after1);
        Assert.assertEquals("CAT33", entry.getCategory());
        Assert.assertEquals("Hallogalli11", entry.getThrowableMessage());
        Assert.assertEquals(Thread.currentThread().getName(), entry.getThread());
        Assert.assertEquals(ExceptionUtils.getFullStackTrace(e), entry.getStackTrace());
        Assert.assertEquals("[Page class = org.apache.wicket.markup.html.pages.AccessDeniedPage, id = 0, render count = 0]", entry.getCausePage());
        Assert.assertEquals("mozilla", entry.getUserAgent());
        Assert.assertEquals("U100", entry.getUserName());
        Assert.assertEquals("/test", entry.getRequestUri());
        Assert.assertEquals("xxx=yyy", entry.getQueryString());
        Assert.assertEquals(sessionId, entry.getSessionId());
        Assert.assertEquals(MockHttpServletRequest.DEFAULT_REMOTE_ADDR, entry.getRemoteAddress());
        Assert.assertEquals("/eee/yyyy", entry.getReferer());
        Assert.assertNull(entry.getMessage());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

}
