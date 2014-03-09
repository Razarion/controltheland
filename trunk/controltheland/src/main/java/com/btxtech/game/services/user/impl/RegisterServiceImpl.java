package com.btxtech.game.services.user.impl;

import com.btxtech.game.jsre.client.common.info.SimpleUser;
import com.btxtech.game.jsre.common.ClientDateUtil;
import com.btxtech.game.jsre.common.CmsUtil;
import com.btxtech.game.jsre.common.gameengine.services.user.EmailAlreadyExitsException;
import com.btxtech.game.jsre.common.gameengine.services.user.PasswordNotMatchException;
import com.btxtech.game.jsre.common.gameengine.services.user.UserAlreadyExistsException;
import com.btxtech.game.jsre.common.packets.UserPacket;
import com.btxtech.game.services.common.ExceptionHandler;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.mgmt.ServerI18nHelper;
import com.btxtech.game.services.mgmt.impl.MgmtServiceImpl;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.user.DbForgotPassword;
import com.btxtech.game.services.user.EmailDoesNotExitException;
import com.btxtech.game.services.user.EmailIsAlreadyVerifiedException;
import com.btxtech.game.services.user.InvitationService;
import com.btxtech.game.services.user.NoForgotPasswordEntryException;
import com.btxtech.game.services.user.RegisterService;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserDoesNotExitException;
import com.btxtech.game.services.user.UserIsNotConfirmedException;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.UserTrackingService;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.velocity.app.VelocityEngine;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.ui.velocity.VelocityEngineUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.mail.internet.MimeMessage;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: beat
 * Date: 26.12.12
 * Time: 10:17
 */
@Component("registerService")
public class RegisterServiceImpl implements RegisterService {
    private static long CLEANUP_DELAY = 1 * ClientDateUtil.MILLIS_IN_DAY; // Is used in test cases
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private VelocityEngine velocityEngine;
    @Autowired
    private UserService userService;
    @Autowired
    private CmsUiService cmsUiService;
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private UserTrackingService userTrackingService;
    @Autowired
    private PlatformTransactionManager transactionManager;
    @Autowired
    private ServerI18nHelper serverI18nHelper;
    @Autowired
    private PlanetSystemService planetSystemService;
    @Autowired
    private InvitationService invitationService;
    private ScheduledThreadPoolExecutor cleanupTimer;

    @PostConstruct
    public void init() {
        cleanupTimer = new ScheduledThreadPoolExecutor(1, new CustomizableThreadFactory("RegisterServiceImpl cleanup timer "));
        cleanupTimer.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                HibernateUtil.openSession4InternalCall(sessionFactory);
                try {
                    removeUnverifiedUsers();
                } catch (Throwable throwable) {
                    ExceptionHandler.handleException(throwable);
                } finally {
                    HibernateUtil.closeSession4InternalCall(sessionFactory);
                }
                HibernateUtil.openSession4InternalCall(sessionFactory);
                try {
                    removeOldPasswordForgetEntries();
                } catch (Throwable throwable) {
                    ExceptionHandler.handleException(throwable);
                } finally {
                    HibernateUtil.closeSession4InternalCall(sessionFactory);
                }
            }
        }, CLEANUP_DELAY, CLEANUP_DELAY, TimeUnit.MILLISECONDS);
    }

    @PreDestroy
    public void cleanup() {
        if (cleanupTimer != null) {
            cleanupTimer.shutdownNow();
            cleanupTimer = null;
        }
    }

    @Override
    public SimpleUser register(String userName, String password, String confirmPassword, String email) throws UserAlreadyExistsException, PasswordNotMatchException, EmailAlreadyExitsException {
        User user = userService.createUnverifiedUser(userName, password, confirmPassword, email);
        sendEmailVerificationMail(user, generateVerificationLink(user));
        return user.createSimpleUser();
    }

    private String generateVerificationLink(User user) {
        return CmsUtil.RAZARION_URL + cmsUiService.getUrl4CmsPage(CmsUtil.CmsPredefinedPage.EMAIL_VERIFICATION) + "/" + CmsUtil.EMAIL_VERIFICATION_KEY + "/" + user.getVerificationId();
    }

    private String generateForgotPasswordLink(String uuid) {
        return CmsUtil.RAZARION_URL + cmsUiService.getUrl4CmsPage(CmsUtil.CmsPredefinedPage.FORGOT_PASSWORD_CHANGE) + "/" + CmsUtil.FORGOT_PASSWORD_UUID_KEY + "/" + uuid;
    }

    @Override
    @Transactional
    public User onVerificationPageCalled(String verificationId) throws EmailIsAlreadyVerifiedException, UserDoesNotExitException {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(User.class);
        criteria.add(Restrictions.eq("verificationId", verificationId));
        List<User> users = criteria.list();
        if (users == null || users.isEmpty()) {
            throw new UserDoesNotExitException("No user with verification id: " + verificationId);
        } else if (users.size() > 1) {
            throw new IllegalArgumentException("More than one user with verification id found: " + verificationId);
        }
        User user = users.get(0);
        if (user.isVerified()) {
            throw new EmailIsAlreadyVerifiedException(user.getVerificationId());
        }
        user.setVerified();
        sessionFactory.getCurrentSession().saveOrUpdate(user);
        userTrackingService.onUserVerified(user);
        invitationService.onUserRegisteredAndVerified(user);
        sendRegistrationCompletedPacket(user);
        return user;
    }

    private void sendRegistrationCompletedPacket(User user) {
        UserPacket userPacket = new UserPacket();
        userPacket.setSimpleUser(user.createSimpleUser());
        planetSystemService.sendPacket(userService.getUserState(user), userPacket);
    }

    @Override
    @Transactional
    public void onForgotPassword(String email) throws EmailDoesNotExitException, UserIsNotConfirmedException {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(User.class);
        criteria.add(Restrictions.eq("email", email));
        List<User> users = criteria.list();
        if (users == null || users.isEmpty()) {
            throw new EmailDoesNotExitException(email);
        } else if (users.size() > 1) {
            ExceptionHandler.handleException("More then one user have this email: " + email);
        }
        User user = users.get(0);
        if (!user.isRegistrationComplete()) {
            throw new UserIsNotConfirmedException();
        }
        String uuid = UUID.randomUUID().toString().toUpperCase();
        saveForgotPassword(user, uuid);
        sendEmailForgotPassword(user, generateForgotPasswordLink(uuid));
        userTrackingService.onPasswordForgotRequested(user, uuid);
    }

    private void saveForgotPassword(User user, String uuid) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbForgotPassword.class);
        criteria.add(Restrictions.eq("user", user));
        List<DbForgotPassword> dbForgotPasswords = criteria.list();
        if (dbForgotPasswords != null && !dbForgotPasswords.isEmpty()) {
            sessionFactory.getCurrentSession().delete(dbForgotPasswords.get(0));
        }
        sessionFactory.getCurrentSession().save(new DbForgotPassword(user, uuid));
    }

    @Override
    @Transactional
    public void onPasswordReset(String uuid, String password, String confirmPassword) throws PasswordNotMatchException, NoForgotPasswordEntryException {
        if (!password.equals(confirmPassword)) {
            throw new PasswordNotMatchException();
        }
        User user = retrieveAndDeleteForgotPassword(uuid);
        if (user == null) {
            throw new IllegalStateException("User is null: " + uuid);
        }
        userService.setNewPassword(user, password);
        userService.loginIfNotLoggedIn(user);
        userTrackingService.onPasswordReset(user, uuid);
    }

    private User retrieveAndDeleteForgotPassword(String uuid) throws NoForgotPasswordEntryException {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbForgotPassword.class);
        criteria.add(Restrictions.eq("uuid", uuid));
        List<DbForgotPassword> dbForgotPasswords = criteria.list();
        if (dbForgotPasswords == null || dbForgotPasswords.isEmpty()) {
            throw new NoForgotPasswordEntryException(uuid);
        } else if (dbForgotPasswords.size() > 1) {
            ExceptionHandler.handleException("More then one DbForgotPassword for exists for uuid: " + uuid);
        }
        User user = null;
        for (DbForgotPassword dbForgotPassword : dbForgotPasswords) {
            user = dbForgotPassword.getUser();
            sessionFactory.getCurrentSession().delete(dbForgotPassword);
        }
        return user;
    }

    private void sendEmailVerificationMail(final User user, final String link) {
        try {
            MimeMessagePreparator preparator = new MimeMessagePreparator() {
                public void prepare(MimeMessage mimeMessage) throws Exception {
                    MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
                    message.setTo(user.getEmail());
                    message.setFrom(MgmtServiceImpl.REPLY_EMAIL);
                    message.setSubject(serverI18nHelper.getString("emailSubject"));
                    Map<String, Object> model = new HashMap<>();
                    model.put("greeting", serverI18nHelper.getString("emailVeriGreeting", new Object[]{user.getUsername()}));
                    model.put("main", serverI18nHelper.getString("emailVeriMain"));
                    model.put("link", link);
                    model.put("user", serverI18nHelper.getString("emailVeriUser", new Object[]{user.getUsername()}));
                    model.put("closing", serverI18nHelper.getString("emailVeriClosing"));
                    model.put("razarionTeam", serverI18nHelper.getString("emailVeriRazarionTeam"));
                    String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "com/btxtech/game/services/user/registration-confirmation.vm", "UTF-8", model);
                    message.setText(text, true);
                }
            };
            mailSender.send(preparator);
        } catch (Exception ex) {
            ExceptionHandler.handleException(ex);
        }
    }

    private void sendEmailForgotPassword(final User user, final String link) {
        try {
            MimeMessagePreparator preparator = new MimeMessagePreparator() {
                public void prepare(MimeMessage mimeMessage) throws Exception {
                    MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
                    message.setTo(user.getEmail());
                    message.setFrom(MgmtServiceImpl.REPLY_EMAIL);
                    message.setSubject(serverI18nHelper.getString("emailForgotPasswordSubject"));
                    Map<String, Object> model = new HashMap<>();
                    model.put("greeting", serverI18nHelper.getString("emailVeriGreeting", new Object[]{user.getUsername()}));
                    model.put("main1", serverI18nHelper.getString("emailForgotPasswordSubjectMain1"));
                    model.put("main2", serverI18nHelper.getString("emailForgotPasswordSubjectMain2"));
                    model.put("link", link);
                    model.put("razarionTeam", serverI18nHelper.getString("emailVeriRazarionTeam"));
                    String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "com/btxtech/game/services/user/forgot-password.vm", "UTF-8", model);
                    message.setText(text, true);
                }
            };
            mailSender.send(preparator);
        } catch (Exception ex) {
            ExceptionHandler.handleException(ex);
        }
    }

    private void removeUnverifiedUsers() {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.add(GregorianCalendar.DAY_OF_YEAR, -1);
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(User.class);
        criteria.add(Restrictions.lt("awaitingVerificationDate", gregorianCalendar.getTime()));
        List<User> users = criteria.list();
        if (users != null) {
            for (User user : users) {
                try {
                    removeUnverifiedUser(user);
                } catch (Throwable throwable) {
                    ExceptionHandler.handleException(throwable, "Error removeUnverifiedUsers: " + user);
                }
            }
        }
    }

    private void removeUnverifiedUser(final User user) {
        // @Transactional not working here
        // It's an limitation with Spring AOP. (dynamic objects and CGLIB)
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {
                sessionFactory.getCurrentSession().delete(user);
                userTrackingService.onUnverifiedUserRemoved(user);
                UserState userState = userService.getUserState(user);
                if (userState != null) {
                    userState.setUser(null);
                    userService.removeUserState(userState);
                }
            }
        });
    }

    private void removeOldPasswordForgetEntries() {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.add(GregorianCalendar.DAY_OF_YEAR, -1);
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbForgotPassword.class);
        criteria.add(Restrictions.lt("date", gregorianCalendar.getTime()));
        List<DbForgotPassword> dbForgotPasswords = criteria.list();
        if (dbForgotPasswords != null && !dbForgotPasswords.isEmpty()) {
            removeOldPasswordForgetEntriesInTransaction(dbForgotPasswords);
        }
    }

    private void removeOldPasswordForgetEntriesInTransaction(final List<DbForgotPassword> dbForgotPasswords) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {
                for (DbForgotPassword dbForgotPassword : dbForgotPasswords) {
                    sessionFactory.getCurrentSession().delete(dbForgotPassword);
                    userTrackingService.onPasswordForgotRequestedRemoved(dbForgotPassword);
                }
            }
        });
    }
}