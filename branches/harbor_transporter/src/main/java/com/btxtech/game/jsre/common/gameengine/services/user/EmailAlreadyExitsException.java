package com.btxtech.game.jsre.common.gameengine.services.user;

/**
 * Created with IntelliJ IDEA.
 * User: beat
 * Date: 28.12.12
 * Time: 18:19
 */
public class EmailAlreadyExitsException extends Exception {
    private String email;

    /**
     * Used by GWT
     */
    EmailAlreadyExitsException() {
    }

    public EmailAlreadyExitsException(String email) {
        super("Email already exits: " + email);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
