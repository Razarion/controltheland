package com.btxtech.game.services.finance;

import com.btxtech.game.jsre.client.dialogs.crystals.PaymentSource;
import com.btxtech.game.rest.RestClient;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.finance.impl.FinanceServiceImpl;
import com.btxtech.game.services.user.UserDoesNotExitException;
import junit.framework.Assert;
import org.easymock.EasyMock;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

/**
 * User: beat
 * Date: 18.01.13
 * Time: 15:53
 */
public class TestFinanceServiceFacebook extends AbstractServiceTest {
    @Autowired
    private FinanceService financeService;

    @Test
    @DirtiesContext
    public void testPayPalIpnFailed() throws Exception {
        configureSimplePlanetNoResources();

        // setup mock
        RestClient restClientMock = EasyMock.createStrictMock(RestClient.class);
        EasyMock.expect(restClientMock.getPaymentObject("111")).andReturn(TestFinanceService.createFacebookPaymentObject("222", "completed", "facebookUserId1", "http://www.razarion.com/fbproducts/CRYST_2000.html", 3.0, "EUR"));
        EasyMock.expect(restClientMock.getPaymentObject("112")).andReturn(TestFinanceService.createFacebookPaymentObject("112", "qqaayy", "facebookUserId1", "http://www.razarion.com/fbproducts/CRYST_2000.html", 3.0, "EUR"));
        EasyMock.expect(restClientMock.getPaymentObject("113")).andReturn(TestFinanceService.createFacebookPaymentObject("113", "completed", "qqwweerr", "http://www.razarion.com/fbproducts/CRYST_2000.html", 3.0, "EUR"));
        EasyMock.expect(restClientMock.getPaymentObject("114")).andReturn(TestFinanceService.createFacebookPaymentObject("114", "completed", "facebookUserId1", "http://www.razarion.com/fbproducts/xxxx", 3.0, "EUR"));
        EasyMock.expect(restClientMock.getPaymentObject("115")).andReturn(TestFinanceService.createFacebookPaymentObject("115", "completed", "facebookUserId1", "http://www.razarion.com/fbproducts/CRYST_2000.html", 4.0, "EUR"));
        EasyMock.expect(restClientMock.getPaymentObject("116")).andReturn(TestFinanceService.createFacebookPaymentObject("116", "completed", "facebookUserId1", "http://www.razarion.com/fbproducts/CRYST_2000.html", 3.0, "USD"));
        EasyMock.expect(restClientMock.getPaymentObject("117")).andReturn(TestFinanceService.createFacebookPaymentObject("117", "completed", "facebookUserId1", "http://www.razarion.com/fbproducts/CRYST_2000.html", 3.0, "EUR"));
        EasyMock.expect(restClientMock.getPaymentObject("117")).andReturn(TestFinanceService.createFacebookPaymentObject("117", "completed", "facebookUserId1", "http://www.razarion.com/fbproducts/CRYST_2000.html", 3.0, "EUR"));
        EasyMock.replay(restClientMock);
        setPrivateField(FinanceServiceImpl.class, financeService, "restClient", restClientMock);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginFacebookUser("facebookUserId1", "nickname1");
        Assert.assertEquals(0, getUserState().getCrystals());

        // Wrong payment id
        try {
            financeService.crystalsBoughtViaFacebook(TestFinanceService.createFacebookPaymentUpdate("111"));
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // Expected
            Assert.assertEquals("PaymentIds are different: 111 222", e.getMessage());
        }

        // wrong payment status
        try {
            financeService.crystalsBoughtViaFacebook(TestFinanceService.createFacebookPaymentUpdate("112"));
            Assert.fail("WrongPaymentStatusException expected");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Payment Status is wrong: qqaayy", e.getMessage());
        }

        // User does not exists
        try {
            financeService.crystalsBoughtViaFacebook(TestFinanceService.createFacebookPaymentUpdate("113"));
            Assert.fail("UserDoesNotExitException expected");
        } catch (UserDoesNotExitException e) {
            // Expected
        }

        // Incorrect product
        try {
            financeService.crystalsBoughtViaFacebook(TestFinanceService.createFacebookPaymentUpdate("114"));
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // Expected
            Assert.assertEquals("No Facebook product for URL: http://www.razarion.com/fbproducts/xxxx", e.getMessage());
        }

        // Incorrect amount
        try {
            financeService.crystalsBoughtViaFacebook(TestFinanceService.createFacebookPaymentUpdate("115"));
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // Expected
            Assert.assertEquals("Invalid cost: 4.0", e.getMessage());
        }

        // Invalid Currency
        try {
            financeService.crystalsBoughtViaFacebook(TestFinanceService.createFacebookPaymentUpdate("116"));
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // Expected
            Assert.assertEquals("Currency is wrong: USD", e.getMessage());
        }

        Assert.assertEquals(0, getUserState().getCrystals());
        financeService.crystalsBoughtViaFacebook(TestFinanceService.createFacebookPaymentUpdate("117"));
        // Transaction id already used
        Assert.assertEquals(2000, getUserState().getCrystals());
        try {
            financeService.crystalsBoughtViaFacebook(TestFinanceService.createFacebookPaymentUpdate("117"));
            Assert.fail("IllegalArgumentException expected");
        } catch (TransactionAlreadyProcessedException e) {
            // Expected
            Assert.assertEquals("Transaction has already been used: 117", e.getMessage());
        }
        Assert.assertEquals(2000, getUserState().getCrystals());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        EasyMock.verify(restClientMock);
    }

    @Test
    @DirtiesContext
    public void testFacebook() throws Exception {
        configureSimplePlanetNoResources();

        // setup mock
        RestClient restClientMock = EasyMock.createStrictMock(RestClient.class);
        EasyMock.expect(restClientMock.getPaymentObject("1234")).andReturn(TestFinanceService.createFacebookPaymentObject("1234", "completed", "facebookUserId1", "http://www.razarion.com/fbproducts/CRYST_2000.html", 3.0, "EUR"));
        EasyMock.expect(restClientMock.getPaymentObject("2234")).andReturn(TestFinanceService.createFacebookPaymentObject("2234", "completed", "facebookUserId1", "http://www.razarion.com/fbproducts/CRYST_4000.html", 5.0, "EUR"));
        EasyMock.expect(restClientMock.getPaymentObject("3234")).andReturn(TestFinanceService.createFacebookPaymentObject("3234", "completed", "facebookUserId1", "http://www.razarion.com/fbproducts/CRYST_10000.html", 10.0, "EUR"));
        EasyMock.expect(restClientMock.getPaymentObject("4234")).andReturn(TestFinanceService.createFacebookPaymentObject("4234", "completed", "facebookUserId1", "http://www.razarion.com/fbproducts/CRYST_30000.html", 25.0, "EUR"));
        EasyMock.expect(restClientMock.getPaymentObject("5234")).andReturn(TestFinanceService.createFacebookPaymentObject("5234", "completed", "facebookUserId1", "http://www.razarion.com/fbproducts/CRYST_70000.html", 50.0, "EUR"));
        EasyMock.replay(restClientMock);
        setPrivateField(FinanceServiceImpl.class, financeService, "restClient", restClientMock);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginFacebookUser("facebookUserId1", "nickname1");
        int userId = getUserState().getUser();
        Assert.assertEquals(0, getUserState().getCrystals());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Buy
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        financeService.crystalsBoughtViaFacebook(TestFinanceService.createFacebookPaymentUpdate("1234"));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginFacebookUser("facebookUserId1");
        Assert.assertEquals(2000, getUserState().getCrystals());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Buy
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        financeService.crystalsBoughtViaFacebook(TestFinanceService.createFacebookPaymentUpdate("2234"));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginFacebookUser("facebookUserId1");
        Assert.assertEquals(6000, getUserState().getCrystals());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Buy
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        financeService.crystalsBoughtViaFacebook(TestFinanceService.createFacebookPaymentUpdate("3234"));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginFacebookUser("facebookUserId1");
        Assert.assertEquals(16000, getUserState().getCrystals());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Buy
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        financeService.crystalsBoughtViaFacebook(TestFinanceService.createFacebookPaymentUpdate("4234"));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginFacebookUser("facebookUserId1");
        Assert.assertEquals(46000, getUserState().getCrystals());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Buy
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        financeService.crystalsBoughtViaFacebook(TestFinanceService.createFacebookPaymentUpdate("5234"));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginFacebookUser("facebookUserId1");
        Assert.assertEquals(116000, getUserState().getCrystals());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<DbPaymentTransaction> transactions = HibernateUtil.loadAll(getSessionFactory(), DbPaymentTransaction.class);
        Assert.assertEquals(5, transactions.size());
        TestFinanceService.assertTransaction(transactions.get(0), userId, "CRYST_2000", "1234", null, PaymentSource.FACEBOOK);
        TestFinanceService.assertTransaction(transactions.get(1), userId, "CRYST_4000", "2234", null, PaymentSource.FACEBOOK);
        TestFinanceService.assertTransaction(transactions.get(2), userId, "CRYST_10000", "3234", null, PaymentSource.FACEBOOK);
        TestFinanceService.assertTransaction(transactions.get(3), userId, "CRYST_30000", "4234", null, PaymentSource.FACEBOOK);
        TestFinanceService.assertTransaction(transactions.get(4), userId, "CRYST_70000", "5234", null, PaymentSource.FACEBOOK);

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        EasyMock.verify(restClientMock);
    }
}
