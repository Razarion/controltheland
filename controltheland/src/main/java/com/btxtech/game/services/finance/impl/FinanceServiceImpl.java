package com.btxtech.game.services.finance.impl;

import com.btxtech.game.jsre.common.MathHelper;
import com.btxtech.game.jsre.common.PayPalButton;
import com.btxtech.game.jsre.common.PayPalUtils;
import com.btxtech.game.services.finance.DbPayPalTransaction;
import com.btxtech.game.services.finance.FinanceService;
import com.btxtech.game.services.history.HistoryService;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserDoesNotExitException;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * User: beat
 * Date: 18.01.13
 * Time: 16:30
 */
@Component
public class FinanceServiceImpl implements FinanceService {
    private static final String PAYMENT_CURRENCY = "USD";
    private static final String RECEIVER_EMAIL = "finance@razarion.com";
    private static final String SANDBOX_RECEIVER_EMAIL = "beat.k_1358507890_biz@btxtech.com";
    private static final String PAYMENT_STATUS = "Completed";
    @Autowired
    private HistoryService historyService;
    @Autowired
    private UserService userService;
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void razarionBought(int razarionBought, UserState userState) {
        if (!userState.isRegistered()) {
            throw new IllegalStateException("Unregistered user can not buy Razarion: " + userState);
        }
        userState.addRazarion(razarionBought);
        historyService.addRazarionBought(userState, razarionBought);
    }

    @Override
    @Transactional
    public void razarionBought(String userName, String itemNumber, String paymentAmount, String paymentCurrency, String txnId, String payerEmail, String receiverEmail, String paymentStatus, String quantity) throws UserDoesNotExitException {
        if (!(PayPalUtils.IS_SANDBOX ? SANDBOX_RECEIVER_EMAIL : RECEIVER_EMAIL).equalsIgnoreCase(receiverEmail)) {
            throw new IllegalArgumentException("Receiver email is wrong: " + receiverEmail);
        }
        if (!PAYMENT_STATUS.equalsIgnoreCase(paymentStatus)) {
            throw new IllegalArgumentException("Payment Status is wrong: " + paymentStatus);
        }
        if (Integer.parseInt(quantity) != 1) {
            throw new IllegalArgumentException("Quantity is wrong: " + quantity);
        }

        User user = userService.getUser(userName);
        if (user == null) {
            throw new UserDoesNotExitException("User does not exist: " + userName);
        }
        UserState userState = userService.getUserState(user);
        if (userState == null) {
            throw new IllegalStateException("No UserState for user: " + userName);
        }

        PayPalButton payPalButton = PayPalButton.getButton4ItemNumber(itemNumber);

        double cost = Double.parseDouble(paymentAmount);

        if (!MathHelper.compareWithPrecision(cost, payPalButton.getCost())) {
            throw new IllegalArgumentException("Invalid cost: " + cost);
        }

        if (!PAYMENT_CURRENCY.equalsIgnoreCase(paymentCurrency.trim())) {
            throw new IllegalArgumentException("Currency is wrong: " + paymentCurrency);
        }

        if (transactionIdAlreadyExits(txnId)) {
            throw new IllegalArgumentException("Transaction has already been used: " + txnId);
        }

        sessionFactory.getCurrentSession().save(new DbPayPalTransaction(user, itemNumber, txnId, payerEmail));
        razarionBought(payPalButton.getRazarion(), userState);
    }

    private boolean transactionIdAlreadyExits(String txnId) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbPayPalTransaction.class);
        criteria.add(Restrictions.eq("txnId", txnId));
        criteria.setProjection(Projections.rowCount());
        return ((Number) criteria.list().get(0)).intValue() > 0;
    }
}
