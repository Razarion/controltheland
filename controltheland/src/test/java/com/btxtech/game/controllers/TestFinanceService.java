package com.btxtech.game.controllers;

import com.btxtech.game.jsre.common.PayPalUtils;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.common.ReadonlyListContentProvider;
import com.btxtech.game.services.finance.DbPayPalTransaction;
import com.btxtech.game.services.finance.FinanceService;
import com.btxtech.game.services.finance.WrongPaymentStatusException;
import com.btxtech.game.services.finance.TransactionAlreadyProcessedException;
import com.btxtech.game.services.history.DisplayHistoryElement;
import com.btxtech.game.services.history.HistoryService;
import com.btxtech.game.services.user.UserDoesNotExitException;
import com.btxtech.game.services.user.UserState;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

/**
 * User: beat
 * Date: 18.01.13
 * Time: 15:53
 */
public class TestFinanceService extends AbstractServiceTest {
    @Autowired
    private HistoryService historyService;
    @Autowired
    private FinanceService financeService;

    @Test
    @DirtiesContext
    public void testSandBox() {
        Assert.assertFalse("Paypal is in Sandbox mode", PayPalUtils.IS_SANDBOX);
    }

    @Test
    @DirtiesContext
    public void buyCrystals() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        UserState userState = getUserState();
        org.junit.Assert.assertEquals(0, userState.getCrystals());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        financeService.crystalsBought(100, userState);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1", "test");
        org.junit.Assert.assertEquals(100, userState.getCrystals());
        ReadonlyListContentProvider<DisplayHistoryElement> history = historyService.getNewestHistoryElements();
        org.junit.Assert.assertEquals(1, history.readDbChildren().size());
        org.junit.Assert.assertEquals("Bought 100 cristals via PayPal", history.readDbChildren().get(0).getMessage());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testPayPalIpnFailed() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        int userId = getUserState().getUser();
        String userIdString = Integer.toString(userId);
        Assert.assertEquals(0, getUserState().getCrystals());
        // User exists
        try {
            financeService.crystalsBought("120", "RAZ1000", "5", "USD", "1", "payer email", "finance@razarion.com", "Completed", "1");
            Assert.fail("UserDoesNotExitException expected");
        } catch (UserDoesNotExitException e) {
            // Expected
        }
        // Wrong user Id
        try {
            financeService.crystalsBought("aaaa", "RAZ1000", "5", "USD", "1", "payer email", "finance@razarion.com", "Completed", "1");
            Assert.fail("UserDoesNotExitException expected");
        } catch (NumberFormatException e) {
            // Expected
        }
        // Correct item name
        try {
            financeService.crystalsBought(userIdString, "RAZ_XXXXX", "5.00", "USD", "1", "payer email", "finance@razarion.com", "Completed", "1");
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // Expected
            Assert.assertEquals("No PayPalButton for: RAZ_XXXXX", e.getMessage());
        }
        // Correct cost
        try {
            financeService.crystalsBought(userIdString, "RAZ1000", "6.00", "USD", "1", "payer email", "finance@razarion.com", "Completed", "1");
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // Expected
            Assert.assertEquals("Invalid cost: 6.0", e.getMessage());
        }
        // Email
        try {
            financeService.crystalsBought(userIdString, "RAZ1000", "5.00", "USD", "1", "payer email", "lulola", "Completed", "1");
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // Expected
            Assert.assertEquals("Receiver email is wrong: lulola", e.getMessage());
        }
        // Payment status unknown
        try {
            financeService.crystalsBought(userIdString, "RAZ1000", "5.00", "USD", "1", "payer email", "finance@razarion.com", "kikoka", "1");
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // Expected
            Assert.assertEquals("Payment Status is wrong: kikoka", e.getMessage());
        }
        // Payment status refunded
        try {
            financeService.crystalsBought(userIdString, "RAZ1000", "5.00", "USD", "1", "payer email", "finance@razarion.com", "Refunded", "1");
            Assert.fail("WrongPaymentStatusException expected");
        } catch (WrongPaymentStatusException e) {
            Assert.assertEquals("Refunded", e.getMessage());
        }
        // Payment status Pending
        try {
            financeService.crystalsBought(userIdString, "RAZ1000", "5.00", "USD", "1", "payer email", "finance@razarion.com", "Pending", "1");
            Assert.fail("WrongPaymentStatusException expected");
        } catch (WrongPaymentStatusException e) {
            Assert.assertEquals("Pending", e.getMessage());
        }
        // Payment status Denied
        try {
            financeService.crystalsBought(userIdString, "RAZ1000", "5.00", "USD", "1", "payer email", "finance@razarion.com", "Denied", "1");
            Assert.fail("WrongPaymentStatusException expected");
        } catch (WrongPaymentStatusException e) {
            Assert.assertEquals("Denied", e.getMessage());
        }
        // Quantity
        try {
            financeService.crystalsBought(userIdString, "RAZ1000", "5.00", "USD", "1", "payer email", "finance@razarion.com", "Completed", "10");
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // Expected
            Assert.assertEquals("Quantity is wrong: 10", e.getMessage());
        }
        // Currency
        try {
            financeService.crystalsBought(userIdString, "RAZ1000", "5.00", "EUR", "1", "payer email", "finance@razarion.com", "Completed", "1");
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // Expected
            Assert.assertEquals("Currency is wrong: EUR", e.getMessage());
        }
        Assert.assertEquals(0, getUserState().getCrystals());
        // Transaction id already used
        financeService.crystalsBought(userIdString, "RAZ1000", "5.00", "USD", "1", "payer email", "finance@razarion.com", "Completed", "1");
        Assert.assertEquals(1000, getUserState().getCrystals());
        try {
            financeService.crystalsBought(userIdString, "RAZ1000", "5.00", "USD", "1", "payer email", "finance@razarion.com", "Completed", "1");
            Assert.fail("IllegalArgumentException expected");
        } catch (TransactionAlreadyProcessedException e) {
            // Expected
            Assert.assertEquals("Transaction has already been used: 1", e.getMessage());
        }
        Assert.assertEquals(1000, getUserState().getCrystals());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testPayPalIpn() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        int userId = getUserState().getUser();
        String userIdString = Integer.toString(userId);
        Assert.assertEquals(0, getUserState().getCrystals());
        financeService.crystalsBought(userIdString, "RAZ1000", "5", "USD", "1", "payer email", "finance@razarion.com", "Completed", "1");
        Assert.assertEquals(1000, getUserState().getCrystals());
        financeService.crystalsBought(userIdString, "RAZ2200", "10", "USD", "2", "payer email", "finance@razarion.com", "Completed", "1");
        Assert.assertEquals(3200, getUserState().getCrystals());
        financeService.crystalsBought(userIdString, "RAZ4600", "20", "USD", "3", "payer email", "finance@razarion.com", "Completed", "1");
        Assert.assertEquals(7800, getUserState().getCrystals());
        financeService.crystalsBought(userIdString, "RAZ12500", "50", "USD", "4", "payer email", "finance@razarion.com", "Completed", "1");
        Assert.assertEquals(20300, getUserState().getCrystals());

        List<DbPayPalTransaction> transactions = HibernateUtil.loadAll(getSessionFactory(), DbPayPalTransaction.class);

        Assert.assertEquals(4, transactions.size());
        assertTransaction(transactions.get(0), userId, "RAZ1000", "1", "payer email");
        assertTransaction(transactions.get(1), userId, "RAZ2200", "2", "payer email");
        assertTransaction(transactions.get(2), userId, "RAZ4600", "3", "payer email");
        assertTransaction(transactions.get(3), userId, "RAZ12500", "4", "payer email");

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    private void assertTransaction(DbPayPalTransaction payPalTransaction, int userId, String itemName, String txnId, String payerEmail) {
        Assert.assertEquals(userId, payPalTransaction.getUser());
        Assert.assertEquals(itemName, payPalTransaction.getItemNumber());
        Assert.assertEquals(txnId, payPalTransaction.getTxnId());
        Assert.assertEquals(payerEmail, payPalTransaction.getPayerEmail());
    }
}
