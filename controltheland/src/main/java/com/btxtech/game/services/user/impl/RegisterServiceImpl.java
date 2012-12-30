package com.btxtech.game.services.user.impl;

import com.btxtech.game.jsre.common.ClientDateUtil;
import com.btxtech.game.jsre.common.CmsUtil;
import com.btxtech.game.jsre.common.gameengine.services.user.EmailAlreadyExitsException;
import com.btxtech.game.jsre.common.gameengine.services.user.PasswordNotMatchException;
import com.btxtech.game.jsre.common.gameengine.services.user.UserAlreadyExistsException;
import com.btxtech.game.services.common.ExceptionHandler;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.user.EmailIsAlreadyVerifiedException;
import com.btxtech.game.services.user.RegisterService;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserDoesNotExitException;
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
import javax.mail.internet.MimeUtility;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private static final String REPLY_EMAIL = "no-reply@razarion.com";
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
    private ScheduledThreadPoolExecutor cleanupTimer;

    @PostConstruct
    public void init() {
        cleanupTimer = new ScheduledThreadPoolExecutor(1, new CustomizableThreadFactory("RegisterServiceImpl cleanup timer "));
        cleanupTimer.schedule(new Runnable() {
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
            }
        }, CLEANUP_DELAY, TimeUnit.MILLISECONDS);
    }

    @PreDestroy
    public void cleanup() {
        if (cleanupTimer != null) {
            cleanupTimer.shutdownNow();
            cleanupTimer = null;
        }
    }

    @Override
    public void register(String userName, String password, String confirmPassword, String email) throws UserAlreadyExistsException, PasswordNotMatchException, EmailAlreadyExitsException {
        User user = userService.createUnverifiedUser(userName, password, confirmPassword, email);
        sendEmailVerificationMail(user, generateLink(user));
    }

    private String generateLink(User user) {
        return "http://www.razarion.com" + cmsUiService.getUrl4CmsPage(CmsUtil.CmsPredefinedPage.EMAIL_VERIFICATION) + "/" + CmsUtil.EMAIL_VERIFICATION_KEY + "/" + user.getVerificationId();
    }

    @Override
    @Transactional
    public User onVerificationPageCalled(String verificationId) throws EmailIsAlreadyVerifiedException, UserDoesNotExitException {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(User.class);
        criteria.add(Restrictions.eq("verificationId", verificationId));
        List<User> users = criteria.list();
        if (users == null || users.isEmpty()) {
            throw new UserDoesNotExitException(verificationId);
        } else if (users.size() > 1) {
            throw new IllegalArgumentException("More than one user with verification id found: " + verificationId);
        }
        User user = users.get(0);
        if(user.isVerified()) {
            throw new EmailIsAlreadyVerifiedException();
        }
        user.setVerified();
        sessionFactory.getCurrentSession().saveOrUpdate(user);
        userTrackingService.onUserVerified(user);
        return user;
    }

    private void sendEmailVerificationMail(final User user, final String link) {
        try {
            MimeMessagePreparator preparator = new MimeMessagePreparator() {
                public void prepare(MimeMessage mimeMessage) throws Exception {
                    MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
                    message.setTo(user.getEmail());
                    message.setFrom(REPLY_EMAIL);
                    message.setSubject("Razarion - Bestätige deine E-Mail-Adresse");
                    Map<Object, Object> model = new HashMap<>();
                    model.put("user", user.getUsername());
                    model.put("link", link);
                    String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "com/btxtech/game/services/user/registration-confirmation.vm", model);
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
                    ExceptionHandler.handleException(throwable);
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
}