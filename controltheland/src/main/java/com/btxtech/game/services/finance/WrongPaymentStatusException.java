package com.btxtech.game.services.finance;

/**
 * User: beat
 * Date: 03.03.13
 * Time: 12:07
 */
public class WrongPaymentStatusException extends Exception {
    public WrongPaymentStatusException(String message) {
        super(message);
    }
}
