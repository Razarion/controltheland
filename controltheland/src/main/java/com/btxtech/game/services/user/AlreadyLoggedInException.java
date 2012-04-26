package com.btxtech.game.services.user;

/**
 * User: beat
 * Date: 11.04.2011
 * Time: 00:57:57
 */
public class AlreadyLoggedInException extends RuntimeException {
    public AlreadyLoggedInException(String userName) {
        super("Already logged in as: " + userName);
    }
}
