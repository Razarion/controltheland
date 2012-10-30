package com.btxtech.game.services.common;

import com.btxtech.game.services.user.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * User: beat
 * Date: 26.08.12
 * Time: 18:58
 */
public class ExceptionHandler {
    private static Log log = LogFactory.getLog(ExceptionHandler.class);
    private static UserService userService;

    public static void handleException(Throwable t, String message) {
        log.error("--------------------------------------------------------------------");
        log.error("Thread: " + Thread.currentThread().getName());
        log.error("Message: " + message);
        if (userService != null) {
            try {
                log.error("Session Id:" + userService.getUserState().getSessionId());
                log.error("User:" + (userService.getUserState().getUser() != null ? userService.getUserState().getUser() : "unregistered"));
            } catch (Exception ignore) {
                // Ignore
            }
        } else {
            log.error("UserService not initialised");
        }
        log.error("", t);
    }

    public static void handleException(Throwable t) {
        handleException(t, null);
    }

    public static void init(UserService userService) {
        ExceptionHandler.userService = userService;
    }
}
