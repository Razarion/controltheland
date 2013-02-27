package com.btxtech.game.services.user;

/**
 * Created with IntelliJ IDEA.
 * User: beat
 * Date: 28.12.12
 * Time: 15:13
 */
public class EmailDoesNotExitException extends Exception {
    private String email;

    public EmailDoesNotExitException(String email) {
        super("No user with email: " + email);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
