package com.btxtech.game.services.user;

import com.btxtech.game.jsre.client.common.info.SimpleUser;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.ClientDateUtil;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.services.user.EmailAlreadyExitsException;
import com.btxtech.game.jsre.common.gameengine.services.user.PasswordNotMatchException;
import com.btxtech.game.jsre.common.packets.UserPacket;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.user.impl.RegisterServiceImpl;
import com.btxtech.game.services.utg.tracker.DbUserHistory;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
        Assert.assertFalse(simpleUser.isVerified());
        Assert.assertFalse(simpleUser.isFacebook());
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
        Assert.assertFalse(user.isRegistrationComplete());
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
        Assert.assertTrue(user.isRegistrationComplete());
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
    public void verifySendPacket() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createUser("U1", "xxx", "test.yyy@testXXX.com");
        User user = userService.getUser();
        user.setAwaitingVerification();
        String verificationId = userService.getUser().getVerificationId();
        saveOrUpdateInTransaction(user);
        getMovableService().getRealGameInfo(START_UID_1, null); // Create connection
        clearPackets();
        registerService.onVerificationPageCalled(verificationId);
        // Check package
        UserPacket userPacket = new UserPacket();
        userPacket.setSimpleUser(new SimpleUser("U1", 1, true, false));
        assertPackagesIgnoreSyncItemInfoAndClear(userPacket);
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
        getMovableService().getRealGameInfo(START_UID_1, null);
        SimpleBase simpleBase = getMovableService().createBase(START_UID_1, new Index(1000, 1000)).getBase();
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
        getMovableService().getRealGameInfo(START_UID_1, null);// Make connection
        simpleBase = getMovableService().createBase(START_UID_1, new Index(3000, 3000)).getBase();
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
    public void onForgotPasswordEn() throws Exception {
        configureSimplePlanetNoResources();

        // Prepare user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMockHttpServletRequest().addPreferredLocale(Locale.ENGLISH);
        createUser("U1", "xxx", "yyy@xxx.com");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        startFakeMailServer();

        // trigger
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        long timeBefore = System.currentTimeMillis();
        getMockHttpServletRequest().addPreferredLocale(Locale.ENGLISH);
        registerService.onForgotPassword("yyy@xxx.com");
        long timeAfter = System.currentTimeMillis();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify DB
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DbForgotPassword> dbForgotPasswords = HibernateUtil.loadAll(getSessionFactory(), DbForgotPassword.class);
        Assert.assertEquals(1, dbForgotPasswords.size());
        DbForgotPassword dbForgotPassword = dbForgotPasswords.get(0);
        String uuid = dbForgotPassword.getUuid();
        Assert.assertEquals("U1", dbForgotPassword.getUser().getUsername());
        Assert.assertTrue(timeBefore <= dbForgotPassword.getDate().getTime());
        Assert.assertTrue(timeAfter >= dbForgotPassword.getDate().getTime());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify Email
        Wiser wiser = getFakeMailServer();
        Assert.assertEquals(1, wiser.getMessages().size());
        WiserMessage wiserMessage = wiser.getMessages().get(0);
        Assert.assertEquals("yyy@xxx.com", wiserMessage.getEnvelopeReceiver());
        Assert.assertEquals("no-reply@razarion.com", wiserMessage.getEnvelopeSender());
        Assert.assertEquals("Razarion - Account password help", wiserMessage.getMimeMessage().getSubject());
        Assert.assertEquals("text/html;charset=UTF-8", wiserMessage.getMimeMessage().getContentType());
        assertStringIgnoreWhitespace("<html><body><h3>Hello U1,</h3><div>You're receiving this email because you requested a password reset for your Razarion account. If you did not request this change, you can safely ignore this email.<br><br>To choose a new password and complete your request, please follow the link below: <br><ahref=\"http://www.razarion.com/uuid/" + uuid + "\">http://www.razarion.com/uuid/" + uuid + "</a><br><br><br>With kind regards your Razarion team</div></body></html>\n", wiserMessage.getMimeMessage().getContent().toString());
        stopFakeMailServer();

        // Test user tracker
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DbUserHistory> historyElements = HibernateUtil.loadAll(getSessionFactory(), DbUserHistory.class);
        Assert.assertEquals(2, historyElements.size());
        Assert.assertEquals("U1", historyElements.get(1).getUser());
        Assert.assertTrue(historyElements.get(1).getForgotPasswordRequest().getTime() >= timeBefore);
        Assert.assertTrue(historyElements.get(1).getForgotPasswordRequest().getTime() <= timeAfter);
        Assert.assertEquals(uuid, historyElements.get(1).getForgotPasswordUuid());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void onForgotPasswordCn() throws Exception {
        configureSimplePlanetNoResources();

        // Prepare user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMockHttpServletRequest().addPreferredLocale(Locale.CHINESE);
        createUser("U1", "xxx", "yyy@xxx.com");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        startFakeMailServer();

        // trigger
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        long timeBefore = System.currentTimeMillis();
        getMockHttpServletRequest().addPreferredLocale(Locale.CHINESE);
        registerService.onForgotPassword("yyy@xxx.com");
        long timeAfter = System.currentTimeMillis();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify DB
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DbForgotPassword> dbForgotPasswords = HibernateUtil.loadAll(getSessionFactory(), DbForgotPassword.class);
        Assert.assertEquals(1, dbForgotPasswords.size());
        DbForgotPassword dbForgotPassword = dbForgotPasswords.get(0);
        String uuid = dbForgotPassword.getUuid();
        Assert.assertEquals("U1", dbForgotPassword.getUser().getUsername());
        Assert.assertTrue(timeBefore <= dbForgotPassword.getDate().getTime());
        Assert.assertTrue(timeAfter >= dbForgotPassword.getDate().getTime());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify Email
        Wiser wiser = getFakeMailServer();
        Assert.assertEquals(1, wiser.getMessages().size());
        WiserMessage wiserMessage = wiser.getMessages().get(0);
        Assert.assertEquals("yyy@xxx.com", wiserMessage.getEnvelopeReceiver());
        Assert.assertEquals("no-reply@razarion.com", wiserMessage.getEnvelopeSender());
        Assert.assertEquals("Razarion - Account password help", wiserMessage.getMimeMessage().getSubject());
        Assert.assertEquals("text/html;charset=UTF-8", wiserMessage.getMimeMessage().getContentType());
        assertStringIgnoreWhitespace("<html><body><h3>Hello U1,</h3><div>You're receiving this email because you requested a password reset for your Razarion account. If you did not request this change, you can safely ignore this email.<br><br>To choose a new password and complete your request, please follow the link below: <br><ahref=\"http://www.razarion.com/uuid/" + uuid + "\">http://www.razarion.com/uuid/" + uuid + "</a><br><br><br>With kind regards your Razarion team</div></body></html>\n", wiserMessage.getMimeMessage().getContent().toString());
        stopFakeMailServer();
    }

    @Test
    @DirtiesContext
    public void onForgotPasswordDe() throws Exception {
        configureSimplePlanetNoResources();

        // Prepare user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMockHttpServletRequest().addPreferredLocale(Locale.GERMAN);
        createUser("U1", "xxx", "yyy@xxx.com");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        startFakeMailServer();

        // trigger
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        long timeBefore = System.currentTimeMillis();
        getMockHttpServletRequest().addPreferredLocale(Locale.GERMAN);
        registerService.onForgotPassword("yyy@xxx.com");
        long timeAfter = System.currentTimeMillis();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify DB
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DbForgotPassword> dbForgotPasswords = HibernateUtil.loadAll(getSessionFactory(), DbForgotPassword.class);
        Assert.assertEquals(1, dbForgotPasswords.size());
        DbForgotPassword dbForgotPassword = dbForgotPasswords.get(0);
        String uuid = dbForgotPassword.getUuid();
        Assert.assertEquals("U1", dbForgotPassword.getUser().getUsername());
        Assert.assertTrue(timeBefore <= dbForgotPassword.getDate().getTime());
        Assert.assertTrue(timeAfter >= dbForgotPassword.getDate().getTime());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify Email
        Wiser wiser = getFakeMailServer();
        Assert.assertEquals(1, wiser.getMessages().size());
        WiserMessage wiserMessage = wiser.getMessages().get(0);
        Assert.assertEquals("yyy@xxx.com", wiserMessage.getEnvelopeReceiver());
        Assert.assertEquals("no-reply@razarion.com", wiserMessage.getEnvelopeSender());
        Assert.assertEquals("Razarion - Konto Passworthilfe", wiserMessage.getMimeMessage().getSubject());
        Assert.assertEquals("text/html;charset=UTF-8", wiserMessage.getMimeMessage().getContentType());
        assertStringIgnoreWhitespace("<html><body><h3>Hallo U1,</h3><div>Du erhältst dieses Email, weil du ein neues Passwort für dein Razarion Konto beantragt hast. Falls Du kein neues Password beantragt hast, kannst du diese Email ignorieren.<br><br>Um den Vorgang abzuschliessen, klicke bitte auf den Link:<br><ahref=\"http://www.razarion.com/uuid/" + uuid + "\">http://www.razarion.com/uuid/" + uuid + "</a><br><br><br>DeinRazarion-Team</div></body></html>\n", wiserMessage.getMimeMessage().getContent().toString());
        stopFakeMailServer();
    }

    @Test
    @DirtiesContext
    public void onForgotPasswordUserNotConfirmed() throws Exception {
        configureSimplePlanetNoResources();

        // Prepare user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUnverifiedUser("U1", "xxx", "xxx", "yyy@xxx.com");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // trigger
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        try {
            registerService.onForgotPassword("yyy@xxx.com");
            Assert.fail("UserIsNotConfirmedException expected");
        } catch (UserIsNotConfirmedException e) {
            // Expected
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void onForgotPasswordEmailDoesNotExist() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMockHttpServletRequest().addPreferredLocale(Locale.GERMAN);
        try {
            registerService.onForgotPassword("yyy@xxx.com");
            Assert.fail("EmailDoesNotExitException expected");
        } catch (EmailDoesNotExitException e) {
            Assert.assertEquals("yyy@xxx.com", e.getEmail());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void onForgotPasswordMultipleUserSameEmail() throws Exception {
        configureSimplePlanetNoResources();

        // Prepare user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMockHttpServletRequest().addPreferredLocale(Locale.GERMAN);
        createUser("U1", "xxx", "yyy@xxx.com");
        createUser("U2", "xxx", "yyy@xxx.com");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        startFakeMailServer();

        // trigger
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMockHttpServletRequest().addPreferredLocale(Locale.GERMAN);
        registerService.onForgotPassword("yyy@xxx.com");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify DB
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DbForgotPassword> dbForgotPasswords = HibernateUtil.loadAll(getSessionFactory(), DbForgotPassword.class);
        Assert.assertEquals(1, dbForgotPasswords.size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify Email
        Wiser wiser = getFakeMailServer();
        Assert.assertEquals(1, wiser.getMessages().size());
        WiserMessage wiserMessage = wiser.getMessages().get(0);
        Assert.assertEquals("yyy@xxx.com", wiserMessage.getEnvelopeReceiver());

        stopFakeMailServer();
    }

    @Test
    @DirtiesContext
    public void onForgotPasswordOverride() throws Exception {
        configureSimplePlanetNoResources();

        // Prepare user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMockHttpServletRequest().addPreferredLocale(Locale.ENGLISH);
        createUser("U1", "xxx", "yyy@xxx.com");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        startFakeMailServer();

        // trigger 1
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        long timeBefore1 = System.currentTimeMillis();
        getMockHttpServletRequest().addPreferredLocale(Locale.ENGLISH);
        registerService.onForgotPassword("yyy@xxx.com");
        long timeAfter1 = System.currentTimeMillis();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify DB
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DbForgotPassword> dbForgotPasswords = HibernateUtil.loadAll(getSessionFactory(), DbForgotPassword.class);
        Assert.assertEquals(1, dbForgotPasswords.size());
        DbForgotPassword dbForgotPassword1 = dbForgotPasswords.get(0);
        Integer id1 = dbForgotPassword1.getId();
        String uuid1 = dbForgotPassword1.getUuid();
        Assert.assertEquals("U1", dbForgotPassword1.getUser().getUsername());
        Assert.assertTrue(timeBefore1 <= dbForgotPassword1.getDate().getTime());
        Assert.assertTrue(timeAfter1 >= dbForgotPassword1.getDate().getTime());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify Email
        Wiser wiser = getFakeMailServer();
        Assert.assertEquals(1, wiser.getMessages().size());
        WiserMessage wiserMessage = wiser.getMessages().get(0);
        Assert.assertEquals("yyy@xxx.com", wiserMessage.getEnvelopeReceiver());
        Assert.assertTrue(wiserMessage.getMimeMessage().getContent().toString().contains(uuid1));
        stopFakeMailServer();
        startFakeMailServer();

        // trigger 2
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        long timeBefore2 = System.currentTimeMillis();
        getMockHttpServletRequest().addPreferredLocale(Locale.ENGLISH);
        registerService.onForgotPassword("yyy@xxx.com");
        long timeAfter2 = System.currentTimeMillis();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify DB
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbForgotPasswords = HibernateUtil.loadAll(getSessionFactory(), DbForgotPassword.class);
        Assert.assertEquals(1, dbForgotPasswords.size());
        DbForgotPassword dbForgotPassword2 = dbForgotPasswords.get(0);
        String uuid2 = dbForgotPassword2.getUuid();
        Assert.assertFalse(uuid1.equals(uuid2));
        Assert.assertEquals("U1", dbForgotPassword2.getUser().getUsername());
        Assert.assertTrue(timeBefore2 <= dbForgotPassword2.getDate().getTime());
        Assert.assertTrue(timeAfter2 >= dbForgotPassword2.getDate().getTime());
        Assert.assertNull(getSessionFactory().getCurrentSession().get(DbForgotPassword.class, id1));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify Email
        wiser = getFakeMailServer();
        Assert.assertEquals(1, wiser.getMessages().size());
        wiserMessage = wiser.getMessages().get(0);
        Assert.assertEquals("yyy@xxx.com", wiserMessage.getEnvelopeReceiver());
        Assert.assertFalse(wiserMessage.getMimeMessage().getContent().toString().contains(uuid1));
        Assert.assertTrue(wiserMessage.getMimeMessage().getContent().toString().contains(uuid2));

        stopFakeMailServer();

        // Test user tracker
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DbUserHistory> historyElements = HibernateUtil.loadAll(getSessionFactory(), DbUserHistory.class);
        Assert.assertEquals(3, historyElements.size());
        Assert.assertEquals("U1", historyElements.get(1).getUser());
        Assert.assertTrue(historyElements.get(1).getForgotPasswordRequest().getTime() >= timeBefore1);
        Assert.assertTrue(historyElements.get(1).getForgotPasswordRequest().getTime() <= timeAfter1);
        Assert.assertEquals(uuid1, historyElements.get(1).getForgotPasswordUuid());
        Assert.assertEquals("U1", historyElements.get(2).getUser());
        Assert.assertTrue(historyElements.get(2).getForgotPasswordRequest().getTime() >= timeBefore2);
        Assert.assertTrue(historyElements.get(2).getForgotPasswordRequest().getTime() <= timeAfter2);
        Assert.assertEquals(uuid2, historyElements.get(2).getForgotPasswordUuid());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void onPasswordReset() throws Exception {
        configureSimplePlanetNoResources();

        // Prepare user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createUser("U1", "xxx", "yyy@xxx.com");
        registerService.onForgotPassword("yyy@xxx.com");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // trigger & verify logged in
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DbForgotPassword> dbForgotPasswords = HibernateUtil.loadAll(getSessionFactory(), DbForgotPassword.class);
        Assert.assertEquals(1, dbForgotPasswords.size());
        DbForgotPassword dbForgotPassword = dbForgotPasswords.get(0);
        Assert.assertNull(userService.getUser());
        long timeBefore = System.currentTimeMillis();
        String uuid = dbForgotPassword.getUuid();
        registerService.onPasswordReset(uuid, "aaa", "aaa");
        long timeAfter = System.currentTimeMillis();
        Assert.assertEquals("U1", userService.getUser().getUsername());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify login with new password
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertNull(userService.getUser());
        loginUser("U1", "xxx");
        Assert.assertNull(userService.getUser());
        loginUser("U1", "aaa");
        Assert.assertEquals("U1", userService.getUser().getUsername());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify DB
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbForgotPasswords = HibernateUtil.loadAll(getSessionFactory(), DbForgotPassword.class);
        Assert.assertEquals(0, dbForgotPasswords.size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Test user tracker
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DbUserHistory> historyElements = HibernateUtil.loadAll(getSessionFactory(), DbUserHistory.class);
        Assert.assertEquals(7, historyElements.size());
        Assert.assertEquals("U1", historyElements.get(3).getUser());
        Assert.assertTrue(historyElements.get(3).getPasswordChanged().getTime() >= timeBefore);
        Assert.assertTrue(historyElements.get(3).getPasswordChanged().getTime() <= timeAfter);
        Assert.assertEquals(uuid, historyElements.get(3).getForgotPasswordUuid());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void onPasswordResetNoUuid() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        try {
            registerService.onPasswordReset("xxx", "aaa", "aaa");
            Assert.fail("NoForgotPasswordEntryException expected");
        } catch (NoForgotPasswordEntryException e) {
            Assert.assertEquals(e.getMessage(), "No DbForgotPassword for uuid exists: xxx");
        }
        Assert.assertNull(userService.getUser());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void onPasswordResetPasswordMatch() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        try {
            registerService.onPasswordReset("xxx", "aaa", "bbb");
            Assert.fail("PasswordNotMatchException expected");
        } catch (PasswordNotMatchException e) {
            // Ignore
        }
        Assert.assertNull(userService.getUser());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void onForgotPasswordDoubleRequests() throws Exception {
        configureSimplePlanetNoResources();

        // Prepare user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMockHttpServletRequest().addPreferredLocale(Locale.ENGLISH);
        createUser("U1", "xxx", "yyy@xxx.com");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // trigger 1
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        registerService.onForgotPassword("yyy@xxx.com");
        List<DbForgotPassword> dbForgotPasswords = HibernateUtil.loadAll(getSessionFactory(), DbForgotPassword.class);
        Assert.assertEquals(1, dbForgotPasswords.size());
        String uuid1 = dbForgotPasswords.get(0).getUuid();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // trigger 2
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        registerService.onForgotPassword("yyy@xxx.com");
        dbForgotPasswords = HibernateUtil.loadAll(getSessionFactory(), DbForgotPassword.class);
        Assert.assertEquals(1, dbForgotPasswords.size());
        String uuid2 = dbForgotPasswords.get(0).getUuid();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // trigger & verify logged in
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        try {
            registerService.onPasswordReset(uuid1, "aaa", "aaa");
            Assert.fail("NoForgotPasswordEntryException expected");
        } catch (NoForgotPasswordEntryException e) {
            Assert.assertEquals(e.getMessage(), "No DbForgotPassword for uuid exists: " + uuid1);
        }
        Assert.assertNull(userService.getUser());
        registerService.onPasswordReset(uuid2, "aaa", "aaa");
        Assert.assertEquals("U1", userService.getUser().getUsername());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void removeOldDbForgotPassword() throws Exception {
        configureSimplePlanetNoResources();

        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.add(GregorianCalendar.DAY_OF_YEAR, -1);
        gregorianCalendar.add(GregorianCalendar.SECOND, -10);

        // Prepare
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createUser("U1", "xxx", "yyy@xxx.com");
        registerService.onForgotPassword("yyy@xxx.com");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Modify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DbForgotPassword> dbForgotPasswords = HibernateUtil.loadAll(getSessionFactory(), DbForgotPassword.class);
        String uuid = dbForgotPasswords.get(0).getUuid();
        setPrivateField(DbForgotPassword.class, dbForgotPasswords.get(0), "date", gregorianCalendar.getTime());
        saveOrUpdateInTransaction(dbForgotPasswords.get(0));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        long timeBefore = System.currentTimeMillis();
        setPrivateStaticField(RegisterServiceImpl.class, "CLEANUP_DELAY", 100);
        ((RegisterServiceImpl) deAopProxy(registerService)).cleanup();
        ((RegisterServiceImpl) deAopProxy(registerService)).init();
        Thread.sleep(200);
        long timeAfter = System.currentTimeMillis();

        // Verify DB
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbForgotPasswords = HibernateUtil.loadAll(getSessionFactory(), DbForgotPassword.class);
        Assert.assertEquals(0, dbForgotPasswords.size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Test user tracker
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DbUserHistory> historyElements = HibernateUtil.loadAll(getSessionFactory(), DbUserHistory.class);
        Assert.assertEquals(3, historyElements.size());
        Assert.assertEquals("U1", historyElements.get(2).getUser());
        Assert.assertTrue(historyElements.get(2).getForgotPasswordRequestRemoved().getTime() >= timeBefore);
        Assert.assertTrue(historyElements.get(2).getForgotPasswordRequestRemoved().getTime() <= timeAfter);
        Assert.assertEquals(uuid, historyElements.get(2).getForgotPasswordUuid());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void removeOldDbForgotPasswordMultiple() throws Exception {
        configureSimplePlanetNoResources();
        setPrivateStaticField(RegisterServiceImpl.class, "CLEANUP_DELAY", 100);
        ((RegisterServiceImpl) deAopProxy(registerService)).cleanup();

        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.add(GregorianCalendar.DAY_OF_YEAR, -1);
        gregorianCalendar.add(GregorianCalendar.SECOND, -10);

        // Prepare 1
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createUser("U1", "xxx", "yy1@xxx.com");
        registerService.onForgotPassword("yy1@xxx.com");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Prepare 2
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createUser("U2", "xxx", "yy2@xxx.com");
        registerService.onForgotPassword("yy2@xxx.com");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Prepare 3
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createUser("U3", "xxx", "yy3@xxx.com");
        registerService.onForgotPassword("yy3@xxx.com");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Modify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DbForgotPassword> dbForgotPasswords = HibernateUtil.loadAll(getSessionFactory(), DbForgotPassword.class);
        String uuid1 = dbForgotPasswords.get(0).getUuid();
        String uuid2 = dbForgotPasswords.get(1).getUuid();
        String uuid3 = dbForgotPasswords.get(2).getUuid();
        setPrivateField(DbForgotPassword.class, dbForgotPasswords.get(0), "date", gregorianCalendar.getTime());
        saveOrUpdateInTransaction(dbForgotPasswords.get(0));
        setPrivateField(DbForgotPassword.class, dbForgotPasswords.get(1), "date", gregorianCalendar.getTime());
        saveOrUpdateInTransaction(dbForgotPasswords.get(1));
        setPrivateField(DbForgotPassword.class, dbForgotPasswords.get(2), "date", gregorianCalendar.getTime());
        saveOrUpdateInTransaction(dbForgotPasswords.get(2));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        long timeBefore = System.currentTimeMillis();
        Thread.sleep(200); // Due to time check
        ((RegisterServiceImpl) deAopProxy(registerService)).init();
        Thread.sleep(200); // Due to time check
        long timeAfter = System.currentTimeMillis();

        // Verify DB
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbForgotPasswords = HibernateUtil.loadAll(getSessionFactory(), DbForgotPassword.class);
        Assert.assertEquals(0, dbForgotPasswords.size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Test user tracker
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DbUserHistory> historyElements = HibernateUtil.loadAll(getSessionFactory(), DbUserHistory.class);
        Assert.assertEquals(9, historyElements.size());
        Assert.assertEquals("U1", historyElements.get(6).getUser());
        // TODO failed on 14.04.2013, 29.04.2013, 15.05.2013, 17.07.2013
        Assert.assertTrue(historyElements.get(6).getForgotPasswordRequestRemoved().getTime() >= timeBefore);
        Assert.assertTrue(historyElements.get(6).getForgotPasswordRequestRemoved().getTime() <= timeAfter);
        Assert.assertEquals(uuid1, historyElements.get(6).getForgotPasswordUuid());
        Assert.assertEquals("U2", historyElements.get(7).getUser());
        Assert.assertTrue(historyElements.get(7).getForgotPasswordRequestRemoved().getTime() >= timeBefore);
        Assert.assertTrue(historyElements.get(7).getForgotPasswordRequestRemoved().getTime() <= timeAfter);
        Assert.assertEquals(uuid2, historyElements.get(7).getForgotPasswordUuid());
        Assert.assertEquals("U3", historyElements.get(8).getUser());
        Assert.assertTrue(historyElements.get(8).getForgotPasswordRequestRemoved().getTime() >= timeBefore);
        Assert.assertTrue(historyElements.get(8).getForgotPasswordRequestRemoved().getTime() <= timeAfter);
        Assert.assertEquals(uuid3, historyElements.get(8).getForgotPasswordUuid());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

}