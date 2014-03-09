package com.btxtech.game.services.finance;

import com.btxtech.game.jsre.client.dialogs.crystals.PaymentSource;
import com.btxtech.game.services.user.UserDoesNotExitException;
import com.btxtech.game.services.user.UserState;

/**
 * User: beat
 * Date: 18.01.13
 * Time: 16:29
 */
public interface FinanceService {
    void crystalsBought(int crystalsBought, UserState userState);

    void crystalsBoughtViaPaypal(String userId, String itemNumber, String paymentAmount, String paymentCurrency, String txnId, String payerEmail, String receiverEmail, String paymentStatus, String quantity) throws UserDoesNotExitException, TransactionAlreadyProcessedException, WrongPaymentStatusException;

    void crystalsBoughtViaFacebook(FacebookPaymentUpdate facebookPaymentUpdate) throws WrongPaymentStatusException, UserDoesNotExitException, TransactionAlreadyProcessedException;
}
