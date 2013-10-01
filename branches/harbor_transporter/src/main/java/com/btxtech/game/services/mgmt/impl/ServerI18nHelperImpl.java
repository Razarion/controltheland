package com.btxtech.game.services.mgmt.impl;

import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.services.common.ExceptionHandler;
import com.btxtech.game.services.mgmt.RequestHelper;
import com.btxtech.game.services.mgmt.ServerI18nHelper;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * User: beat
 * Date: 14.01.13
 * Time: 22:54
 */
@Component
public class ServerI18nHelperImpl implements ServerI18nHelper {
    private static final Locale DEFAULT = Locale.ENGLISH;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private RequestHelper requestHelper;
    @Autowired
    private UserService userService;
    private Log log = LogFactory.getLog(ServerI18nHelperImpl.class);

    @Override
    public String getString(String key) {
        return getString(key, null);
    }

    @Override
    public String getString(String key, Object[] args) {
        return messageSource.getMessage(key, args, key, getLocaleFromRequest());
    }

    @Override
    public String getStringNoRequest(UserState userState, String key, Object[] args) {
        return messageSource.getMessage(key, args, key, getLocale(userState));
    }

    @Override
    public String getStringNoRequest(SimpleBase simpleBase, String key, Object[] args) {
        return messageSource.getMessage(key, args, key, getLocale(simpleBase));
    }

    private Locale getLocaleFromRequest() {
        try {
            return requestHelper.getLocale();
        } catch (Exception e) {
            ExceptionHandler.handleException(e);
            return DEFAULT;
        }
    }

    private Locale getLocale(SimpleBase simpleBase) {
        UserState userState = userService.getUserState(simpleBase);
        if (userState != null && userState.getLocale() != null) {
            return userState.getLocale();
        } else {
            log.warn("No locale for SimpleBase: " + simpleBase);
            return DEFAULT;
        }
    }

    private Locale getLocale(UserState userState) {
        if (userState != null && userState.getLocale() != null) {
            return userState.getLocale();
        } else {
            log.warn("No locale for SimpleBase: " + userState);
            return DEFAULT;
        }
    }
}
