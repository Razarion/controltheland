package com.btxtech.game.services.finance;

/**
 * User: beat
 * Date: 25.02.13
 * Time: 12:00
 */
public class TransactionAlreadyProcessedException extends Exception {
    public TransactionAlreadyProcessedException(String txnId) {
        super("Transaction has already been used: " + txnId);
    }
}
