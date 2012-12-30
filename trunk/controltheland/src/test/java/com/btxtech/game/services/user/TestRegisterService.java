package com.btxtech.game.services.user;

import com.btxtech.game.jsre.common.ClientDateUtil;
import com.btxtech.game.jsre.common.gameengine.services.user.EmailAlreadyExitsException;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.user.impl.RegisterServiceImpl;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created with IntelliJ IDEA.
 * User: beat
 * Date: 26.12.12
 * Time: 11:24
 */
public class TestRegisterService extends AbstractServiceTest {
    @Autowired
    private RegisterService registerService;
    @Autowired
    private UserService userService;

    @Test
    @DirtiesContext
    public void register() throws Exception {
        startFakeMailServer();

        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Date dateBefore = new Date();
        registerService.register("U1", "xxx", "xxx", "test.yyy@testXXX.com");
        Date dateAfter = new Date();
        String verificationId = userService.getUser().getVerificationId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        User user = userService.getUser("U1");
        Assert.assertNotNull(user.getVerificationId());
        Assert.assertEquals(verificationId, user.getVerificationId());
        Assert.assertNotNull(user.getAwaitingVerificationDate());
        Assert.assertTrue(user.getAwaitingVerificationDate().getTime() >= dateBefore.getTime());
        Assert.assertTrue(user.getAwaitingVerificationDate().getTime() <= dateAfter.getTime());
        Assert.assertFalse(user.isAccountNonLocked());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify Email
        Wiser wiser = getFakeMailServer();
        Assert.assertEquals(1, wiser.getMessages().size());
        WiserMessage wiserMessage = wiser.getMessages().get(0);
        Assert.assertEquals("test.yyy@testXXX.com", wiserMessage.getEnvelopeReceiver());
        Assert.assertEquals("no-reply@razarion.com", wiserMessage.getEnvelopeSender());
        Assert.assertEquals("Razarion - Bestätige deine E-Mail-Adresse", wiserMessage.getMimeMessage().getSubject());
        Assert.assertEquals("text/html;charset=UTF-8", wiserMessage.getMimeMessage().getContentType());
        Assert.assertEquals(setupMailContent(user.getVerificationId()), ((String) wiserMessage.getMimeMessage().getContent()).trim());

        stopFakeMailServer();
    }

    @Test
    @DirtiesContext
    public void registerEmailExits() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("U1", "xxx", "xxx", "test.yyy@testXXX.com");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();


        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        try {
            registerService.register("U2", "xxx", "xxx", "test.yyy@testXXX.com");
            Assert.fail();
        } catch (EmailAlreadyExitsException emailAlreadyExitsException) {
            Assert.assertEquals("test.yyy@testXXX.com", emailAlreadyExitsException.getEmail());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    private String setupMailContent(String verifyCode) {
        return "<html>\r\n" +
                "<body>\r\n" +
                "<h3>Hallo U1,</h3>\r\n" +
                "\r\n" +
                "<div>\r\n" +
                "    Vielen Dank f&uuml;r die Registrierung bei Razarion. Bitte best&auml;tige deine E-Mail-Adresse, indem du auf den folgenden\r\n" +
                "    Link klickst:<br>\r\n" +
                "    <a href=\"http://www.razarion.com/verification_code/" +
                verifyCode +
                "\">http://www.razarion.com/verification_code/" +
                verifyCode +
                "</a>\r\n" +
                "    <br>\r\n" +
                "    <br>\r\n" +
                "    Benutzername: U1\r\n" +
                "    <br>\r\n" +
                "    <br>\r\n" +
                "    Wir freuen uns darauf, dich bei Razarion begr&uuml;&szlig;en zu d&uuml;rfen!\r\n" +
                "    <br>\r\n" +
                "    <br>\r\n" +
                "    Dein Razarion-Team\r\n" +
                "</div>\r\n" +
                "</body>\r\n" +
                "</html>".trim();
    }

    @Test
    @DirtiesContext
    public void verify() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("U1", "xxx", "xxx", "test.yyy@testXXX.com");
        User user = userService.getUser();
        user.setAwaitingVerification();
        String verificationId = userService.getUser().getVerificationId();
        saveOrUpdateInTransaction(user);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        registerService.onVerificationPageCalled(verificationId);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        user = userService.getUser("U1");
        Assert.assertNotNull(user.getVerificationId());
        Assert.assertEquals(verificationId, user.getVerificationId());
        Assert.assertNull(user.getAwaitingVerificationDate());
        Assert.assertTrue(user.isAccountNonLocked());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void verifyNoUser() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        try {
            registerService.onVerificationPageCalled("abcedefgahij");
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // Expected
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void removeUnverifiedUser() throws Exception {
        configureSimplePlanetNoResources();

        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.add(GregorianCalendar.DAY_OF_YEAR, -1);
        gregorianCalendar.add(GregorianCalendar.SECOND, -10);


        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("U1", "xxx", "xxx", "test.yyy@testXXX.com");
        User user = userService.getUser();
        user.setAwaitingVerification();
        setPrivateField(User.class, user, "awaitingVerificationDate", gregorianCalendar.getTime());
        saveOrUpdateInTransaction(user);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        setPrivateStaticField(RegisterServiceImpl.class, "CLEANUP_DELAY", 100);
        ((RegisterServiceImpl) deAopProxy(registerService)).cleanup();
        ((RegisterServiceImpl) deAopProxy(registerService)).init();
        Thread.sleep(200);

        // Verify user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertNull(userService.getUser("U1"));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        setPrivateStaticField(RegisterServiceImpl.class, "CLEANUP_DELAY", 1 * ClientDateUtil.MILLIS_IN_DAY);
    }

}
