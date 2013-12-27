package com.btxtech.game.services.common;

/**
 * User: beat
 * Date: 01.02.13
 * Time: 20:13
 */
public class NameErrorPair {
    private String name;
    private String error;

    public NameErrorPair(String name, String error) {
        this.name = name;
        this.error = error;
    }

    public String getName() {
        return name;
    }

    public String getError() {
        return error;
    }
}
