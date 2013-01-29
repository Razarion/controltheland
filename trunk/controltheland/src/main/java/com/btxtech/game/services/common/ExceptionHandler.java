package com.btxtech.game.services.common;

import com.btxtech.game.services.connection.Session;
import com.btxtech.game.services.user.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

/**
 * User: beat
 * Date: 26.08.12
 * Time: 18:58
 */
public class ExceptionHandler {
    private static Log log = LogFactory.getLog(ExceptionHandler.class);
    private static ApplicationContext applicationContext;

    public static void handleException(Throwable t, String message) {
        log.error("--------------------------------------------------------------------");
        UserService userService = null;
        if (applicationContext == null) {
            log.error("ExceptionHandler.handleException() applicationContext is not set");
        } else {
            if (applicationContext.containsBean("userService")) {
                userService = (UserService) applicationContext.getBean("userService");
            }
        }
        logParameters(log, userService);
        log.error(message, t);
    }

    public static void handleException(Throwable t) {
        handleException(t, null);
    }

    public static void init(ApplicationContext applicationContext) {
        ExceptionHandler.applicationContext = applicationContext;
    }

    public static void logParameters(Log log, UserService userService) {
        try {
            log.error("Thread: " + Thread.currentThread().getName());
            Session session = null;
            if (userService != null) {
                session = userService.getSession4ExceptionHandler();
            }
            if (session != null) {
                log.error("User Agent: " + session.getUserAgent());
                log.error("Session Id: " + session.getSessionId());
                log.error("IP: " + session.getRequest().getRemoteAddr());
                log.error("Referer: " + session.getRequest().getHeader("Referer"));
                log.error("User: " + (userService.getUserName() != null ? userService.getUserName() : "unregistered"));
            }
        } catch (Exception e) {
            log.error("ExceptionHandler.logParameters()", e);
        }
    }
}
