package com.btxtech.game.controllers;

import com.btxtech.game.jsre.common.PayPalUtils;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.common.ReadonlyListContentProvider;
import com.btxtech.game.services.finance.DbPayPalTransaction;
import com.btxtech.game.services.finance.FinanceService;
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
    public void buyRazarion() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        UserState userState = getUserState();
        org.junit.Assert.assertEquals(0, userState.getRazarion());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        financeService.razarionBought(100, userState);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1", "test");
        org.junit.Assert.assertEquals(100, userState.getRazarion());
        ReadonlyListContentProvider<DisplayHistoryElement> history = historyService.getNewestHistoryElements();
        org.junit.Assert.assertEquals(1, history.readDbChildren().size());
        org.junit.Assert.assertEquals("Bought Razarion 100 via PayPal", history.readDbChildren().get(0).getMessage());
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
        Assert.assertEquals(0, getUserState().getRazarion());
        // User exists
        try {
            financeService.razarionBought("U2", "RAZ1000", "5", "USD", "1", "payer email", "finance@razarion.com", "Completed", "1");
            Assert.fail("UserDoesNotExitException expected");
        } catch (UserDoesNotExitException e) {
            // Expected
        }
        // Correct item name
        try {
            financeService.razarionBought("U1", "RAZ_XXXXX", "5.00", "USD", "1", "payer email", "finance@razarion.com", "Completed", "1");
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // Expected
            Assert.assertEquals("No PayPalButton for: RAZ_XXXXX", e.getMessage());
        }
        // Correct cost
        try {
            financeService.razarionBought("U1", "RAZ1000", "6.00", "USD", "1", "payer email", "finance@razarion.com", "Completed", "1");
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // Expected
            Assert.assertEquals("Invalid cost: 6.0", e.getMessage());
        }
        // Email
        try {
            financeService.razarionBought("U1", "RAZ1000", "5.00", "USD", "1", "payer email", "lulola", "Completed", "1");
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // Expected
            Assert.assertEquals("Receiver email is wrong: lulola", e.getMessage());
        }
        // Payment status
        try {
            financeService.razarionBought("U1", "RAZ1000", "5.00", "USD", "1", "payer email", "finance@razarion.com", "kikoka", "1");
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // Expected
            Assert.assertEquals("Payment Status is wrong: kikoka", e.getMessage());
        }
        // Quantity
        try {
            financeService.razarionBought("U1", "RAZ1000", "5.00", "USD", "1", "payer email", "finance@razarion.com", "Completed", "10");
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // Expected
            Assert.assertEquals("Quantity is wrong: 10", e.getMessage());
        }
        // Currency
        try {
            financeService.razarionBought("U1", "RAZ1000", "5.00", "EUR", "1", "payer email", "finance@razarion.com", "Completed", "1");
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // Expected
            Assert.assertEquals("Currency is wrong: EUR", e.getMessage());
        }
        Assert.assertEquals(0, getUserState().getRazarion());
        // Transaction id already used
        financeService.razarionBought("U1", "RAZ1000", "5.00", "USD", "1", "payer email", "finance@razarion.com", "Completed", "1");
        Assert.assertEquals(1000, getUserState().getRazarion());
        try {
            financeService.razarionBought("U1", "RAZ1000", "5.00", "USD", "1", "payer email", "finance@razarion.com", "Completed", "1");
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // Expected
            Assert.assertEquals("Transaction has already been used: 1", e.getMessage());
        }
        Assert.assertEquals(1000, getUserState().getRazarion());
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
        Assert.assertEquals(0, getUserState().getRazarion());
        financeService.razarionBought("U1", "RAZ1000", "5", "USD", "1", "payer email", "finance@razarion.com", "Completed", "1");
        Assert.assertEquals(1000, getUserState().getRazarion());
        financeService.razarionBought("U1", "RAZ2200", "10", "USD", "2", "payer email", "finance@razarion.com", "Completed", "1");
        Assert.assertEquals(3200, getUserState().getRazarion());
        financeService.razarionBought("U1", "RAZ4600", "20", "USD", "3", "payer email", "finance@razarion.com", "Completed", "1");
        Assert.assertEquals(7800, getUserState().getRazarion());
        financeService.razarionBought("U1", "RAZ12500", "50", "USD", "4", "payer email", "finance@razarion.com", "Completed", "1");
        Assert.assertEquals(20300, getUserState().getRazarion());

        List<DbPayPalTransaction> transactions = HibernateUtil.loadAll(getSessionFactory(), DbPayPalTransaction.class);

        Assert.assertEquals(4, transactions.size());
        assertTransaction(transactions.get(0), "U1", "RAZ1000", "1", "payer email");
        assertTransaction(transactions.get(1), "U1", "RAZ2200", "2", "payer email");
        assertTransaction(transactions.get(2), "U1", "RAZ4600", "3", "payer email");
        assertTransaction(transactions.get(3), "U1", "RAZ12500", "4", "payer email");

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    private void assertTransaction(DbPayPalTransaction payPalTransaction, String userName, String itemName, String txnId, String payerEmail) {
        Assert.assertEquals(userName, payPalTransaction.getUser());
        Assert.assertEquals(itemName, payPalTransaction.getItemNumber());
        Assert.assertEquals(txnId, payPalTransaction.getTxnId());
        Assert.assertEquals(payerEmail, payPalTransaction.getPayerEmail());
    }
}
