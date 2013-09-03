package com.btxtech.game.services.user;

/**
 * Created with IntelliJ IDEA.
 * User: beat
 * Date: 28.12.12
 * Time: 15:13
 */
public class EmailIsAlreadyVerifiedException extends Exception {
    public EmailIsAlreadyVerifiedException(String verificationId) {
        super("Email already verified: " + verificationId);
    }
}
