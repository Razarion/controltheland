package com.btxtech.game.services.user;

/**
 * User: beat
 * Date: 27.02.13
 * Time: 00:46
 */
public class NoForgotPasswordEntryException extends Exception {
    public NoForgotPasswordEntryException(String uuid) {
        super("No DbForgotPassword for uuid exists: " + uuid);
    }
}
