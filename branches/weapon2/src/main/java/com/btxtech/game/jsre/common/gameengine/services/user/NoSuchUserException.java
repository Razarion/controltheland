package com.btxtech.game.jsre.common.gameengine.services.user;

/**
 * User: beat
 * Date: 05.06.13
 * Time: 15:26
 */
public class NoSuchUserException extends Exception {
    private String userName;

    /**
     * Used by GWT
     */
    NoSuchUserException() {
    }

    public NoSuchUserException(String userName) {
        super("Nu such user: " + userName);
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }
}
