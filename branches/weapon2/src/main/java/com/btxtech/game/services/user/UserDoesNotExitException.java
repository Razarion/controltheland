package com.btxtech.game.services.user;

/**
 * Created with IntelliJ IDEA.
 * User: beat
 * Date: 28.12.12
 * Time: 15:13
 */
public class UserDoesNotExitException extends Exception {
    public UserDoesNotExitException(String message) {
        super(message);
    }


}
