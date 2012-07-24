package com.btxtech.game.services.socialnet.facebook;

/**
 * User: beat
 * Date: 22.07.12
 * Time: 14:46
 */
public class FacebookUser {
    private String country;
    private String locale;
    private FacebookAge age;

    public FacebookUser(String country, String locale, FacebookAge age) {
        this.country = country;
        this.locale = locale;
        this.age = age;
    }

    public String getCountry() {
        return country;
    }

    public String getLocale() {
        return locale;
    }

    public FacebookAge getAge() {
        return age;
    }
}
