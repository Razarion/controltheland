package com.btxtech.game.services.finance;

import com.btxtech.game.jsre.client.dialogs.crystals.PaymentSource;
import com.btxtech.game.jsre.common.packets.CrystalPacket;
import com.btxtech.game.rest.RestClient;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.common.ReadonlyListContentProvider;
import com.btxtech.game.services.finance.impl.FinanceServiceImpl;
import com.btxtech.game.services.history.DisplayHistoryElement;
import com.btxtech.game.services.history.HistoryService;
import com.btxtech.game.services.user.UserState;
import junit.framework.Assert;
import org.easymock.EasyMock;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

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
        org.junit.Assert.assertEquals("Bought 100 crystals", history.readDbChildren().get(0).getMessage());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }


    @Test
    @DirtiesContext
    public void testTransactionIdPaypalFacebook() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginFacebookUser("facebookUserId1", "nickname1");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // setup mock
        RestClient restClientMock = EasyMock.createStrictMock(RestClient.class);
        EasyMock.expect(restClientMock.getPaymentObject("117")).andReturn(createFacebookPaymentObject("117", "completed", "facebookUserId1", "http://www.razarion.com/fbproducts/CRYST_2000.html", 3.0, "EUR"));
        EasyMock.expect(restClientMock.getPaymentObject("117")).andReturn(createFacebookPaymentObject("117", "completed", "facebookUserId1", "http://www.razarion.com/fbproducts/CRYST_2000.html", 3.0, "EUR"));
        EasyMock.replay(restClientMock);
        setPrivateField(FinanceServiceImpl.class, financeService, "restClient", restClientMock);
        // TX id 117 facebook
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        financeService.crystalsBoughtViaFacebook(TestFinanceService.createFacebookPaymentUpdate("117"));
        // Transaction id already used
        try {
            financeService.crystalsBoughtViaFacebook(TestFinanceService.createFacebookPaymentUpdate("117"));
            Assert.fail("IllegalArgumentException expected");
        } catch (TransactionAlreadyProcessedException e) {
            // Expected
            Assert.assertEquals("Transaction has already been used: 117", e.getMessage());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // TX id 117 paypal
        // Transaction id already used
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        int userId = getUserState().getUser();
        String userIdString = Integer.toString(userId);
        financeService.crystalsBoughtViaPaypal(userIdString, "CRYST_2000", "3.00", "EUR", "117", "payer email", "finance@razarion.com", "Completed", "1");
        Assert.assertEquals(2000, getUserState().getCrystals());
        try {
            financeService.crystalsBoughtViaPaypal(userIdString, "CRYST_2000", "3.00", "EUR", "117", "payer email", "finance@razarion.com", "Completed", "1");
            Assert.fail("IllegalArgumentException expected");
        } catch (TransactionAlreadyProcessedException e) {
            // Expected
            Assert.assertEquals("Transaction has already been used: 117", e.getMessage());
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        EasyMock.verify(restClientMock);
    }

    @Test
    @DirtiesContext
    public void testMessageSent() throws Exception {
        // setup mock
        RestClient restClientMock = EasyMock.createStrictMock(RestClient.class);
        EasyMock.expect(restClientMock.getPaymentObject("117")).andReturn(createFacebookPaymentObject("117", "completed", "fb11", "http://www.razarion.com/fbproducts/CRYST_2000.html", 3.0, "EUR"));
        EasyMock.replay(restClientMock);
        setPrivateField(FinanceServiceImpl.class, financeService, "restClient", restClientMock);

        configureSimplePlanetNoResources();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        createAndLoginFacebookUser("fb11", "fbNick");
        String userIdString = Integer.toString(getUserId());

        getMovableService().getRealGameInfo(START_UID_1, null); // Connection is created here. Don't call movableService.getGameInfo() again!
        getOrCreateBase();
        Thread.sleep(1000); // Get rid of unpredictable account balance package
        clearPackets();
        waitForActionServiceDone();
        // Buy via facebook
        financeService.crystalsBoughtViaFacebook(TestFinanceService.createFacebookPaymentUpdate("117"));
        // Verify
        CrystalPacket crystalPacket = new CrystalPacket();
        crystalPacket.setDelta(2000);
        crystalPacket.setValue(2000);
        assertPackagesIgnoreSyncItemInfoAndClear(crystalPacket);
        // Buy via paypal
        financeService.crystalsBoughtViaPaypal(userIdString, "CRYST_4000", "5.00", "EUR", "1155", "payer email", "finance@razarion.com", "Completed", "1");
        crystalPacket = new CrystalPacket();
        crystalPacket.setDelta(4000);
        crystalPacket.setValue(6000);
        assertPackagesIgnoreSyncItemInfoAndClear(crystalPacket);

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }


    public static void assertTransaction(DbPaymentTransaction payPalTransaction, int userId, String itemName, String txnId, String payerEmail, PaymentSource paymentSource) {
        Assert.assertEquals(userId, payPalTransaction.getUserId());
        Assert.assertEquals(itemName, payPalTransaction.getItemNumber());
        Assert.assertEquals(txnId, payPalTransaction.getTxnId());
        Assert.assertEquals(payerEmail, payPalTransaction.getPayerEmail());
        Assert.assertEquals(paymentSource, payPalTransaction.getPaymentSource());
    }

    public static FacebookPaymentUpdate createFacebookPaymentUpdate(String paymentId) {
        FacebookPaymentUpdate facebookPaymentUpdate = new FacebookPaymentUpdate();
        FacebookPaymentUpdate.Entry entry = new FacebookPaymentUpdate.Entry();
        entry.setId(paymentId);
        facebookPaymentUpdate.setEntry(new FacebookPaymentUpdate.Entry[]{entry});
        return facebookPaymentUpdate;
    }

    public static FacebookPaymentObject createFacebookPaymentObject(String paymentId, String status, String facebookUserId, String productUrl, double cost, String currency) {
        FacebookPaymentObject facebookPaymentObject = new FacebookPaymentObject();
        facebookPaymentObject.setId(paymentId);
        FacebookPaymentObject.User user = new FacebookPaymentObject.User();
        user.setId(facebookUserId);
        facebookPaymentObject.setUser(user);
        FacebookPaymentObject.Action action = new FacebookPaymentObject.Action();
        action.setStatus(status);
        action.setAmount(cost);
        action.setCurrency(currency);
        facebookPaymentObject.setActions(new FacebookPaymentObject.Action[]{action});
        FacebookPaymentObject.Item item = new FacebookPaymentObject.Item();
        item.setProduct(productUrl);
        facebookPaymentObject.setItems(new FacebookPaymentObject.Item[]{item});
        return facebookPaymentObject;
    }

}
