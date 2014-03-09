package com.btxtech.game.services.finance;

import com.btxtech.game.jsre.client.dialogs.crystals.PaymentSource;
import com.btxtech.game.jsre.common.PayPalUtils;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.user.UserDoesNotExitException;
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
public class TestFinanceServicePaypal extends AbstractServiceTest {
    @Autowired
    private FinanceService financeService;

    @Test
    @DirtiesContext
    public void testSandBox() {
        Assert.assertFalse("Paypal is in Sandbox mode", PayPalUtils.IS_SANDBOX);
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
            financeService.crystalsBoughtViaPaypal("120", "CRYST_2000", "3.00", "EUR", "1", "payer email", "finance@razarion.com", "Completed", "1");
            Assert.fail("UserDoesNotExitException expected");
        } catch (UserDoesNotExitException e) {
            // Expected
        }
        // Wrong user Id
        try {
            financeService.crystalsBoughtViaPaypal("aaaa", "CRYST_2000", "3.00", "EUR", "1", "payer email", "finance@razarion.com", "Completed", "1");
            Assert.fail("UserDoesNotExitException expected");
        } catch (NumberFormatException e) {
            // Expected
        }
        // Incorrect item name
        try {
            financeService.crystalsBoughtViaPaypal(userIdString, "RAZ_XXXXX", "3.00", "EUR", "1", "payer email", "finance@razarion.com", "Completed", "1");
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // Expected
            Assert.assertEquals("No PayPalButton for: RAZ_XXXXX", e.getMessage());
        }
        // Incorrect cost
        try {
            financeService.crystalsBoughtViaPaypal(userIdString, "CRYST_2000", "6.00", "EUR", "1", "payer email", "finance@razarion.com", "Completed", "1");
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // Expected
            Assert.assertEquals("Invalid cost: 6.0", e.getMessage());
        }
        // Email
        try {
            financeService.crystalsBoughtViaPaypal(userIdString, "CRYST_2000", "3.00", "EUR", "1", "payer email", "lulola", "Completed", "1");
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // Expected
            Assert.assertEquals("Receiver email is wrong: lulola", e.getMessage());
        }
        // Payment status unknown
        try {
            financeService.crystalsBoughtViaPaypal(userIdString, "CRYST_2000", "3.00", "EUR", "1", "payer email", "finance@razarion.com", "kikoka", "1");
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // Expected
            Assert.assertEquals("Payment Status is wrong: kikoka", e.getMessage());
        }
        // Payment status refunded
        try {
            financeService.crystalsBoughtViaPaypal(userIdString, "CRYST_2000", "3.00", "EUR", "1", "payer email", "finance@razarion.com", "Refunded", "1");
            Assert.fail("WrongPaymentStatusException expected");
        } catch (WrongPaymentStatusException e) {
            Assert.assertEquals("Refunded", e.getMessage());
        }
        // Payment status Pending
        try {
            financeService.crystalsBoughtViaPaypal(userIdString, "CRYST_2000", "3.00", "EUR", "1", "payer email", "finance@razarion.com", "Pending", "1");
            Assert.fail("WrongPaymentStatusException expected");
        } catch (WrongPaymentStatusException e) {
            Assert.assertEquals("Pending", e.getMessage());
        }
        // Payment status Denied
        try {
            financeService.crystalsBoughtViaPaypal(userIdString, "CRYST_2000", "3.00", "EUR", "1", "payer email", "finance@razarion.com", "Denied", "1");
            Assert.fail("WrongPaymentStatusException expected");
        } catch (WrongPaymentStatusException e) {
            Assert.assertEquals("Denied", e.getMessage());
        }
        // Quantity
        try {
            financeService.crystalsBoughtViaPaypal(userIdString, "CRYST_2000", "3.00", "EUR", "1", "payer email", "finance@razarion.com", "Completed", "10");
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // Expected
            Assert.assertEquals("Quantity is wrong: 10", e.getMessage());
        }
        // Currency
        try {
            financeService.crystalsBoughtViaPaypal(userIdString, "CRYST_2000", "3.00", "USD", "1", "payer email", "finance@razarion.com", "Completed", "1");
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // Expected
            Assert.assertEquals("Currency is wrong: USD", e.getMessage());
        }
        Assert.assertEquals(0, getUserState().getCrystals());
        // Transaction id already used
        financeService.crystalsBoughtViaPaypal(userIdString, "CRYST_2000", "3.00", "EUR", "1", "payer email", "finance@razarion.com", "Completed", "1");
        Assert.assertEquals(2000, getUserState().getCrystals());
        try {
            financeService.crystalsBoughtViaPaypal(userIdString, "CRYST_2000", "3.00", "EUR", "1", "payer email", "finance@razarion.com", "Completed", "1");
            Assert.fail("IllegalArgumentException expected");
        } catch (TransactionAlreadyProcessedException e) {
            // Expected
            Assert.assertEquals("Transaction has already been used: 1", e.getMessage());
        }
        Assert.assertEquals(2000, getUserState().getCrystals());
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
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Buy
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        financeService.crystalsBoughtViaPaypal(userIdString, "CRYST_2000", "3", "EUR", "1", "payer email", "finance@razarion.com", "Completed", "1");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1");
        Assert.assertEquals(2000, getUserState().getCrystals());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Buy
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        financeService.crystalsBoughtViaPaypal(userIdString, "CRYST_4000", "5", "EUR", "2", "payer email", "finance@razarion.com", "Completed", "1");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1");
        Assert.assertEquals(6000, getUserState().getCrystals());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Buy
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        financeService.crystalsBoughtViaPaypal(userIdString, "CRYST_10000", "10", "EUR", "3", "payer email", "finance@razarion.com", "Completed", "1");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1");
        Assert.assertEquals(16000, getUserState().getCrystals());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Buy
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        financeService.crystalsBoughtViaPaypal(userIdString, "CRYST_30000", "25", "EUR", "4", "payer email", "finance@razarion.com", "Completed", "1");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1");
        Assert.assertEquals(46000, getUserState().getCrystals());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Buy
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        financeService.crystalsBoughtViaPaypal(userIdString, "CRYST_70000", "50", "EUR", "5", "payer email", "finance@razarion.com", "Completed", "1");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1");
        Assert.assertEquals(116000, getUserState().getCrystals());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DbPaymentTransaction> transactions = HibernateUtil.loadAll(getSessionFactory(), DbPaymentTransaction.class);
        Assert.assertEquals(5, transactions.size());
        TestFinanceService.assertTransaction(transactions.get(0), userId, "CRYST_2000", "1", "payer email", PaymentSource.PAYPAL);
        TestFinanceService.assertTransaction(transactions.get(1), userId, "CRYST_4000", "2", "payer email", PaymentSource.PAYPAL);
        TestFinanceService.assertTransaction(transactions.get(2), userId, "CRYST_10000", "3", "payer email", PaymentSource.PAYPAL);
        TestFinanceService.assertTransaction(transactions.get(3), userId, "CRYST_30000", "4", "payer email", PaymentSource.PAYPAL);
        TestFinanceService.assertTransaction(transactions.get(4), userId, "CRYST_70000", "5", "payer email", PaymentSource.PAYPAL);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }
}
