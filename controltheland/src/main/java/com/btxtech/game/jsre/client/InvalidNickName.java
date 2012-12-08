package com.btxtech.game.jsre.client;

import java.io.Serializable;

/**
 * User: beat
 * Date: 23.07.12
 * Time: 13:42
 */
public enum InvalidNickName implements Serializable {
    TO_SHORT("name must have at least 3 characters"),
    ALREADY_USED("name has already been taken"),
    UNKNOWN_ERROR("Unable to create user");
    private String errorMsg;

    InvalidNickName(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
