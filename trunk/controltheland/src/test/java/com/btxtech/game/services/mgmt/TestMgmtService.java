package com.btxtech.game.services.mgmt;

import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.user.User;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
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

}
