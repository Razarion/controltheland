package com.btxtech.game.wicket.uiservices;

import com.btxtech.game.services.common.NoSuchChildException;
import org.apache.wicket.Component;

/**
 * User: beat
 * Date: 02.04.2011
 * Time: 12:56:58
 */
public class UiExceptionHandler {
    /**
     * Singleton
     */
    private UiExceptionHandler() {
    }

    static public void handleException(Throwable throwable, Component component) {
        if (throwable instanceof NoSuchChildException) {
            component.error(throwable.getMessage());
        } else {
            throw new RuntimeException(throwable);
        }

    }
}
