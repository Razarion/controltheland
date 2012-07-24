package com.btxtech.game.services.user;

/**
 * User: beat
 * Date: 23.07.12
 * Time: 13:42
 */
public enum InvalidNickName {
    TO_SHORT("name must have at least 3 characters"),
    ALREADY_USED("name has already been taken");
    private String errorMsg;

    InvalidNickName(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
