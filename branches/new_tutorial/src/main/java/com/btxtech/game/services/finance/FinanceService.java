package com.btxtech.game.services.finance;

import com.btxtech.game.services.user.UserDoesNotExitException;
import com.btxtech.game.services.user.UserState;

/**
 * User: beat
 * Date: 18.01.13
 * Time: 16:29
 */
public interface FinanceService {
    void razarionBought(int razarionBought, UserState userState);

    void razarionBought(String userId, String itemNumber, String paymentAmount, String paymentCurrency, String txnId, String payerEmail, String receiverEmail, String paymentStatus, String quantity) throws UserDoesNotExitException, TransactionAlreadyProcessedException, WrongPaymentStatusException;
}
