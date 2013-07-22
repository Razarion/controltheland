package com.btxtech.game.services.common;

import com.btxtech.game.services.mgmt.RequestHelper;
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

    public static void handleException(String message) {
        handleException(null, message);
    }

    public static void init(ApplicationContext applicationContext) {
        ExceptionHandler.applicationContext = applicationContext;
    }

    public static void logParameters(Log log, UserService userService) {
        try {
            log.error("Thread: " + Thread.currentThread().getName());
            RequestHelper requestHelper = null;
            if (userService != null) {
                requestHelper = userService.getRequestHelper4ExceptionHandler();
            }
            if (requestHelper != null) {
                log.error("URI: " + requestHelper.getRequest().getRequestURI());
                log.error("URL: " + requestHelper.getRequest().getRequestURL());
                log.error("User Agent: " + requestHelper.getRequest().getHeader("user-agent"));
                log.error("Session Id: " + requestHelper.getRequest().getSession().getId());
                log.error("IP: " + requestHelper.getRequest().getRemoteAddr());
                log.error("Referer: " + requestHelper.getRequest().getHeader("Referer"));
                log.error("User: " + (userService.getUserName() != null ? userService.getUserName() : "unregistered"));
            }
        } catch (Exception e) {
            log.error("ExceptionHandler.logParameters()", e);
        }
    }
}
