package com.btxtech.game.services.finance.impl;

import com.btxtech.game.jsre.client.dialogs.crystals.PaymentSource;
import com.btxtech.game.jsre.common.FacebookProducts;
import com.btxtech.game.jsre.common.MathHelper;
import com.btxtech.game.jsre.common.PayPalButton;
import com.btxtech.game.jsre.common.PayPalUtils;
import com.btxtech.game.jsre.common.packets.CrystalPacket;
import com.btxtech.game.rest.RestClient;
import com.btxtech.game.services.finance.DbPaymentTransaction;
import com.btxtech.game.services.finance.FacebookPaymentObject;
import com.btxtech.game.services.finance.FacebookPaymentUpdate;
import com.btxtech.game.services.finance.FinanceService;
import com.btxtech.game.services.finance.TransactionAlreadyProcessedException;
import com.btxtech.game.services.finance.WrongPaymentStatusException;
import com.btxtech.game.services.history.HistoryService;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.socialnet.facebook.FacebookUtil;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserDoesNotExitException;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.condition.ServerConditionService;
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
    private static final String PAYMENT_CURRENCY = "EUR";
    private static final String RECEIVER_EMAIL = "finance@razarion.com";
    private static final String SANDBOX_RECEIVER_EMAIL = "beat.k_1358507890_biz@btxtech.com";
    private static final String PAYMENT_STATUS_COMPLETED = "Completed";
    private static final String PAYMENT_STATUS_REFUNDED = "Refunded";
    private static final String PAYMENT_STATUS_DENIED = "Denied";
    private static final String PAYMENT_STATUS_PENDING = "Pending";
    @Autowired
    private HistoryService historyService;
    @Autowired
    private UserService userService;
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private ServerConditionService serverConditionService;
    @Autowired
    private RestClient restClient;
    @Autowired
    private PlanetSystemService planetSystemService;

    @Override
    public void crystalsBought(int crystalsBought, UserState userState) {
        if (!userState.isRegistered()) {
            throw new IllegalStateException("Unregistered user can not buy crystals: " + userState);
        }
        userState.addCrystals(crystalsBought);
        historyService.addCrystalsBought(userState, crystalsBought);
        serverConditionService.onCrystalsIncreased(userState, false, crystalsBought);

        CrystalPacket crystalPacket = new CrystalPacket();
        crystalPacket.setDelta(crystalsBought);
        crystalPacket.setValue(userState.getCrystals());
        planetSystemService.sendPacket(userState, crystalPacket);
    }

    @Override
    @Transactional
    public void crystalsBoughtViaPaypal(String userId, String itemNumber, String paymentAmount, String paymentCurrency, String txnId, String payerEmail, String receiverEmail, String paymentStatus, String quantity) throws UserDoesNotExitException, TransactionAlreadyProcessedException, WrongPaymentStatusException {
        if (!(PayPalUtils.IS_SANDBOX ? SANDBOX_RECEIVER_EMAIL : RECEIVER_EMAIL).equalsIgnoreCase(receiverEmail)) {
            throw new IllegalArgumentException("Receiver email is wrong: " + receiverEmail);
        }
        if (PAYMENT_STATUS_REFUNDED.equalsIgnoreCase(paymentStatus)) {
            throw new WrongPaymentStatusException(PAYMENT_STATUS_REFUNDED);
        } else if (PAYMENT_STATUS_DENIED.equalsIgnoreCase(paymentStatus)) {
            throw new WrongPaymentStatusException(PAYMENT_STATUS_DENIED);
        } else if (PAYMENT_STATUS_PENDING.equalsIgnoreCase(paymentStatus)) {
            throw new WrongPaymentStatusException(PAYMENT_STATUS_PENDING);
        } else if (!PAYMENT_STATUS_COMPLETED.equalsIgnoreCase(paymentStatus)) {
            throw new IllegalArgumentException("Payment Status is wrong: " + paymentStatus);
        }
        if (Integer.parseInt(quantity) != 1) {
            throw new IllegalArgumentException("Quantity is wrong: " + quantity);
        }

        User user = userService.getUser(Integer.parseInt(userId));
        if (user == null) {
            throw new UserDoesNotExitException("User Id does not exist: " + userId);
        }
        UserState userState = userService.getUserState(user);
        if (userState == null) {
            throw new IllegalStateException("No UserState for user: " + user);
        }

        PayPalButton payPalButton = PayPalButton.getButton4ItemNumber(itemNumber);

        double cost = Double.parseDouble(paymentAmount);

        if (!MathHelper.compareWithPrecision(cost, payPalButton.getCost())) {
            throw new IllegalArgumentException("Invalid cost: " + cost);
        }

        if (!PAYMENT_CURRENCY.equalsIgnoreCase(paymentCurrency.trim())) {
            throw new IllegalArgumentException("Currency is wrong: " + paymentCurrency);
        }

        if (transactionIdAlreadyExits(txnId, PaymentSource.PAYPAL)) {
            throw new TransactionAlreadyProcessedException(txnId);
        }

        sessionFactory.getCurrentSession().save(new DbPaymentTransaction(user, itemNumber, txnId, payerEmail, PaymentSource.PAYPAL));
        crystalsBought(payPalButton.getCrystals(), userState);
    }

    private boolean transactionIdAlreadyExits(String txnId, PaymentSource paymentSource) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbPaymentTransaction.class);
        criteria.add(Restrictions.eq("txnId", txnId));
        criteria.add(Restrictions.eq("paymentSource", paymentSource));
        criteria.setProjection(Projections.rowCount());
        return ((Number) criteria.list().get(0)).intValue() > 0;
    }

    @Override
    @Transactional
    public void crystalsBoughtViaFacebook(FacebookPaymentUpdate facebookPaymentUpdate) throws WrongPaymentStatusException, UserDoesNotExitException, TransactionAlreadyProcessedException {
        FacebookPaymentObject paymentObject = restClient.getPaymentObject(facebookPaymentUpdate.getEntry()[0].getId());

        if (!facebookPaymentUpdate.getEntry()[0].getId().equals(paymentObject.getId())) {
            throw new IllegalArgumentException("PaymentIds are different: " + facebookPaymentUpdate.getEntry()[0].getId() + " " + paymentObject.getId());
        }

        if(!paymentObject.getActions()[0].getStatus().equalsIgnoreCase(FacebookUtil.PAYMENT_STATE_COMPLETED)) {
            throw new IllegalArgumentException("Payment Status is wrong: " + paymentObject.getActions()[0].getStatus());
        }

        User user = userService.loadFacebookUserFromDb(paymentObject.getUser().getId());
        if (user == null) {
            throw new UserDoesNotExitException("Facebook User Id does not exist: " + paymentObject);
        }

        UserState userState = userService.getUserState(user);
        if (userState == null) {
            throw new IllegalStateException("No UserState for user Id: " + user);
        }

        FacebookProducts facebookProducts = FacebookProducts.getProduct4ProductUrl(paymentObject.getItems()[0].getProduct());

        if (!MathHelper.compareWithPrecision(paymentObject.getActions()[0].getAmount(), facebookProducts.getCost())) {
            throw new IllegalArgumentException("Invalid cost: " + paymentObject.getActions()[0].getAmount());
        }

        if (!PAYMENT_CURRENCY.equalsIgnoreCase(paymentObject.getActions()[0].getCurrency())) {
            throw new IllegalArgumentException("Currency is wrong: " + paymentObject.getActions()[0].getCurrency());
        }

        if (transactionIdAlreadyExits(paymentObject.getId(), PaymentSource.FACEBOOK)) {
            throw new TransactionAlreadyProcessedException(paymentObject.getId());
        }

        sessionFactory.getCurrentSession().save(new DbPaymentTransaction(user, facebookProducts.getShortName(), paymentObject.getId(), null, PaymentSource.FACEBOOK));
        crystalsBought(facebookProducts.getCrystals(), userState);
    }

}
