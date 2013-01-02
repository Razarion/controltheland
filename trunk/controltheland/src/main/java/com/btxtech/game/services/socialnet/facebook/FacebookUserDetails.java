package com.btxtech.game.services.socialnet.facebook;

/**
 * Created with IntelliJ IDEA.
 * User: beat
 * Date: 02.01.13
 * Time: 12:25
 */
public class FacebookUserDetails {
    private String email;

    public FacebookUserDetails(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "FacebookUserDetails{email='" + email + "\'}";
    }
}
