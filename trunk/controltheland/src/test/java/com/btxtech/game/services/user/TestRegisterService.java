package com.btxtech.game.services.user;

import com.btxtech.game.jsre.client.SimpleUser;
import com.btxtech.game.jsre.common.ClientDateUtil;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.services.user.EmailAlreadyExitsException;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.user.impl.RegisterServiceImpl;
import com.btxtech.game.services.utg.tracker.DbUserHistory;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.annotation.DirtiesContext;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

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
    @Autowired
    private PlanetSystemService planetSystemService;
    @Autowired
    private JavaMailSenderImpl javaMailSender;

    @Test
    @DirtiesContext
    public void registerEn() throws Exception {
        startFakeMailServer();

        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMockHttpServletRequest().addPreferredLocale(Locale.ENGLISH);
        Date dateBefore = new Date();
        SimpleUser simpleUser = registerService.register("U1", "xxx", "xxx", "test.yyy@testXXX.com");
        Assert.assertEquals((int) userService.getUser("U1").getId(), simpleUser.getId());
        Assert.assertEquals("U1", simpleUser.getName());
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
        Assert.assertEquals("Razarion - Please confirm your Email address", wiserMessage.getMimeMessage().getSubject());
        Assert.assertEquals("text/html;charset=UTF-8", wiserMessage.getMimeMessage().getContentType());
        Assert.assertEquals(setupMailContentEn(user.getVerificationId()), ((String) wiserMessage.getMimeMessage().getContent()).trim());

        stopFakeMailServer();

        // User tracker history
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DbUserHistory> historyElements = HibernateUtil.loadAll(getSessionFactory(), DbUserHistory.class);
        Assert.assertEquals(1, historyElements.size());
        Assert.assertEquals("U1", historyElements.get(0).getUser());
        Assert.assertNull(historyElements.get(0).getVerified());
        Assert.assertEquals(verificationId, historyElements.get(0).getVerificationId());
        Assert.assertNotNull(historyElements.get(0).getAwaitingVerificationDate());
        Assert.assertTrue(historyElements.get(0).getAwaitingVerificationDate().getTime() >= dateBefore.getTime());
        Assert.assertTrue(historyElements.get(0).getAwaitingVerificationDate().getTime() <= dateAfter.getTime());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void registerDe() throws Exception {
        startFakeMailServer();

        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMockHttpServletRequest().addPreferredLocale(Locale.GERMAN);
        registerService.register("U1", "xxx", "xxx", "test.yyy@testXXX.com");
        User user = userService.getUser("U1");
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
        Assert.assertEquals(setupMailContentDe(user.getVerificationId()), ((String) wiserMessage.getMimeMessage().getContent()).trim());

        stopFakeMailServer();
    }

    @Test
    @DirtiesContext
    public void registerChinese() throws Exception {
        startFakeMailServer();

        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMockHttpServletRequest().addPreferredLocale(Locale.CHINESE);
        registerService.register("U1", "xxx", "xxx", "test.yyy@testXXX.com");
        User user = userService.getUser("U1");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify Email
        Wiser wiser = getFakeMailServer();
        Assert.assertEquals(1, wiser.getMessages().size());
        WiserMessage wiserMessage = wiser.getMessages().get(0);
        Assert.assertEquals("test.yyy@testXXX.com", wiserMessage.getEnvelopeReceiver());
        Assert.assertEquals("no-reply@razarion.com", wiserMessage.getEnvelopeSender());
        Assert.assertEquals("Razarion - Please confirm your Email address", wiserMessage.getMimeMessage().getSubject());
        Assert.assertEquals("text/html;charset=UTF-8", wiserMessage.getMimeMessage().getContentType());
        Assert.assertEquals(setupMailContentEn(user.getVerificationId()), ((String) wiserMessage.getMimeMessage().getContent()).trim());

        stopFakeMailServer();
    }

    @Test
    @DirtiesContext
    public void registerEmailExits() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createUser("U1", "xxx", "test.yyy@testXXX.com");
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

    private String setupMailContentDe(String verifyCode) {
        return "<html>\r\n" +
                "<body>\r\n" +
                "<h3>Hallo U1,</h3>\r\n" +
                "\r\n" +
                "<div>\r\n" +
                "    Vielen Dank für die Registrierung bei Razarion. Bitte bestätige deine E-Mail-Adresse, indem du auf den folgenden Link klickst:\r\n" +
                "    <br>\r\n" +
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
                "    Wir freuen uns darauf, dich bei Razarion begrüssen zu dürfen!\r\n" +
                "    <br>\r\n" +
                "    <br>\r\n" +
                "    Dein Razarion-Team\r\n" +
                "</div>\r\n" +
                "</body>\r\n" +
                "</html>".trim();
    }

    private String setupMailContentEn(String verifyCode) {
        return "<html>\r\n" +
                "<body>\r\n" +
                "<h3>Hello U1,</h3>\r\n" +
                "\r\n" +
                "<div>\r\n" +
                "    Thank you for registering at Razarion. Please follow the link below to confirm your email address:\r\n" +
                "    <br>\r\n" +
                "    <a href=\"http://www.razarion.com/verification_code/" + verifyCode + "\">http://www.razarion.com/verification_code/" + verifyCode + "</a>\r\n" +
                "    <br>\r\n" +
                "    <br>\r\n" +
                "    User name: U1\r\n" +
                "    <br>\r\n" +
                "    <br>\r\n" +
                "    We are pleased to be able to welcome you to Razarion\r\n" +
                "    <br>\r\n" +
                "    <br>\r\n" +
                "    With kind regards your Razarion team\r\n" +
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
        createUser("U1", "xxx", "test.yyy@testXXX.com");
        User user = userService.getUser();
        user.setAwaitingVerification();
        String verificationId = userService.getUser().getVerificationId();
        saveOrUpdateInTransaction(user);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Date dateBefore = new Date();
        registerService.onVerificationPageCalled(verificationId);
        Date dateAfter = new Date();
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

        // User tracker history
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DbUserHistory> historyElements = HibernateUtil.loadAll(getSessionFactory(), DbUserHistory.class);
        Assert.assertEquals(2, historyElements.size());
        Assert.assertEquals("U1", historyElements.get(1).getUser());
        Assert.assertTrue(historyElements.get(1).getVerified().getTime() >= dateBefore.getTime());
        Assert.assertTrue(historyElements.get(1).getVerified().getTime() <= dateAfter.getTime());
        Assert.assertEquals(verificationId, historyElements.get(1).getVerificationId());
        Assert.assertNull(historyElements.get(1).getAwaitingVerificationDate());
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
        } catch (UserDoesNotExitException e) {
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
        registerService.register("U1", "xxx", "xxx", "fake");
        SimpleBase simpleBase = getMyBase();
        User user = userService.getUser();
        user.setAwaitingVerification();
        String verificationId = userService.getUser().getVerificationId();
        setPrivateField(User.class, user, "awaitingVerificationDate", gregorianCalendar.getTime());
        saveOrUpdateInTransaction(user);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        Assert.assertFalse(planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getBaseService().isAbandoned(simpleBase));

        Date dateBefore = new Date();
        setPrivateStaticField(RegisterServiceImpl.class, "CLEANUP_DELAY", 100);
        ((RegisterServiceImpl) deAopProxy(registerService)).cleanup();
        ((RegisterServiceImpl) deAopProxy(registerService)).init();
        Thread.sleep(200);
        Date dateAfter = new Date();

        // Verify user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertNull(userService.getUser("U1"));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        Assert.assertTrue(planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getBaseService().isAbandoned(simpleBase));

        // User tracker history
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DbUserHistory> historyElements = HibernateUtil.loadAll(getSessionFactory(), DbUserHistory.class);
        Assert.assertEquals(4, historyElements.size());
        Assert.assertEquals("U1", historyElements.get(3).getUser());
        Assert.assertTrue(historyElements.get(3).getDeleteUnverifiedUser().getTime() >= dateBefore.getTime());
        Assert.assertTrue(historyElements.get(3).getDeleteUnverifiedUser().getTime() <= dateAfter.getTime());
        Assert.assertEquals(verificationId, historyElements.get(3).getVerificationId());
        Assert.assertNull(historyElements.get(3).getAwaitingVerificationDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Create second user
        dateBefore = new Date();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        registerService.register("U1", "xxx", "xxx", "fake");
        simpleBase = getMyBase();
        user = userService.getUser();
        user.setAwaitingVerification();
        verificationId = userService.getUser().getVerificationId();
        setPrivateField(User.class, user, "awaitingVerificationDate", gregorianCalendar.getTime());
        saveOrUpdateInTransaction(user);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        Thread.sleep(200);
        dateAfter = new Date();

        Assert.assertTrue(planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getBaseService().isAbandoned(simpleBase));

        // Verify user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertNull(userService.getUser("U1"));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // User tracker history
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        historyElements = HibernateUtil.loadAll(getSessionFactory(), DbUserHistory.class);
        Assert.assertEquals(8, historyElements.size());
        Assert.assertEquals("U1", historyElements.get(7).getUser());
        Assert.assertTrue(historyElements.get(7).getDeleteUnverifiedUser().getTime() >= dateBefore.getTime());
        Assert.assertTrue(historyElements.get(7).getDeleteUnverifiedUser().getTime() <= dateAfter.getTime());
        Assert.assertEquals(verificationId, historyElements.get(7).getVerificationId());
        Assert.assertNull(historyElements.get(7).getAwaitingVerificationDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        setPrivateStaticField(RegisterServiceImpl.class, "CLEANUP_DELAY", 1 * ClientDateUtil.MILLIS_IN_DAY);
    }

    @Test
    @DirtiesContext
    public void testEMailServerHost() throws Exception {
        Assert.assertEquals("localhost", javaMailSender.getHost());
    }
}
