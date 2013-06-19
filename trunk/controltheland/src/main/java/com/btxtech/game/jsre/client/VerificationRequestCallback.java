package com.btxtech.game.jsre.client;

/**
 * User: beat
 * Date: 23.07.12
 * Time: 13:42
 */
public interface VerificationRequestCallback {
    public enum ErrorResult {
        TO_SHORT,
        ALREADY_USED,
        UNKNOWN_ERROR
    }

    void onResponse(ErrorResult errorResult);
}
