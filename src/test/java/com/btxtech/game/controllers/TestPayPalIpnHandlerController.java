package com.btxtech.game.controllers;

import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.finance.FinanceService;
import com.btxtech.game.services.finance.WrongPaymentStatusException;
import com.btxtech.game.services.finance.TransactionAlreadyProcessedException;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.net.URL;

/**
 * User: beat
 * Date: 21.03.2012
 * Time: 14:11:49
 */
public class TestPayPalIpnHandlerController {

    private MockHttpServletRequest createMockServletRequest() {
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest("get", "payment_type=instant&receiver_id=7A9K5VMDK42WY&address_street=Stj%3Frngatan+4E");
        mockHttpServletRequest.setParameter("item_number", "RAZ1000");
        mockHttpServletRequest.setParameter("shipping_method", "Default");
        mockHttpServletRequest.setParameter("residence_country", "XX");
        mockHttpServletRequest.setParameter("shipping_discount", "0.00");
        mockHttpServletRequest.setParameter("insurance_amount", "0.00");
        mockHttpServletRequest.setParameter("verify_sign", "xyxyxyxyxy");
        mockHttpServletRequest.setParameter("address_country", "unconfirmed");
        mockHttpServletRequest.setParameter("address_city", "Uddevalla");
        mockHttpServletRequest.setParameter("payment_status", "Completed");
        mockHttpServletRequest.setParameter("address_status", "unconfirmed");
        mockHttpServletRequest.setParameter("business", "xxxx@xxxx.com");
        mockHttpServletRequest.setParameter("transaction_subject", "00001");
        mockHttpServletRequest.setParameter("protection_eligibility", "Eligible");
        mockHttpServletRequest.setParameter("shipping", "0.00");
        mockHttpServletRequest.setParameter("first_name", "name1");
        mockHttpServletRequest.setParameter("payer_id", "IDIDIDID");
        mockHttpServletRequest.setParameter("payer_email", "hallo@cxyyyy.qqq");
        mockHttpServletRequest.setParameter("mc_fee", "0.49");
        mockHttpServletRequest.setParameter("btn_id", "00003333");
        mockHttpServletRequest.setParameter("txn_id", "11112222");
        mockHttpServletRequest.setParameter("receiver_email", "wwwww@qqqqq.sssss");
        mockHttpServletRequest.setParameter("quantity", "1");
        mockHttpServletRequest.setParameter("notify_version", "3.7");
        mockHttpServletRequest.setParameter("txn_type", "web_accept");
        mockHttpServletRequest.setParameter("mc_currency", "USD");
        mockHttpServletRequest.setParameter("mc_gross", "5.00");
        mockHttpServletRequest.setParameter("payer_status", "unverified");
        mockHttpServletRequest.setParameter("custom", "00001");
        mockHttpServletRequest.setParameter("payment_date", "06:34:42 Feb 24, 2013 PST");
        mockHttpServletRequest.setParameter("payment_fee", "0.49");
        mockHttpServletRequest.setParameter("address_country_code", "qq");
        mockHttpServletRequest.setParameter("charset", "windows-1252");
        mockHttpServletRequest.setParameter("payment_gross", "5.00");
        mockHttpServletRequest.setParameter("address_zip", "11111");
        mockHttpServletRequest.setParameter("ipn_track_id", "d05e19875977b");
        mockHttpServletRequest.setParameter("address_state", "");
        mockHttpServletRequest.setParameter("discount", "0.00");
        mockHttpServletRequest.setParameter("handling_amount", "0.00");
        mockHttpServletRequest.setParameter("tax", "0.00");
        mockHttpServletRequest.setParameter("item_name", "Razarion 1000");
        mockHttpServletRequest.setParameter("address_name", "aaa bbb");
        mockHttpServletRequest.setParameter("last_name", "yyyyyy");
        mockHttpServletRequest.setParameter("payment_type", "instant");
        mockHttpServletRequest.setParameter("receiver_id", "xxxxxxx");
        mockHttpServletRequest.setParameter("address_street", "ÄÄÄÖÖÖÜÜÜ");
        return mockHttpServletRequest;
    }

    @Test
    public void normal() throws Exception {
        MockHttpServletRequest mockHttpServletRequest = createMockServletRequest();
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        PayPalIpnHandlerController payPalIpnHandlerController = new PayPalIpnHandlerController() {
            @Override
            protected String sendVerification(URL url, String params) throws IOException {
                Assert.assertEquals("https://www.paypal.com/cgi-bin/webscr", url.toString());
                Assert.assertEquals("cmd=_notify-validate&item_number=RAZ1000&shipping_method=Default&residence_country=XX&shipping_discou" +
                        "nt=0.00&insurance_amount=0.00&verify_sign=xyxyxyxyxy&address_country=unconfirmed&address_city=Uddevalla&payment_status=Completed&address_status=unconfirmed&bus" +
                        "iness=xxxx%40xxxx.com&transaction_subject=00001&protection_eligibility=Eligible&shipping=0.00&first_name=name1&payer_id=IDIDIDID&payer_email=hallo%40cxyyyy.qqq&mc_fee=0.49&btn_id=00003333" +
                        "&txn_id=11112222&receiver_email=wwwww%40qqqqq.sssss&quantity=1&notify_version=3.7&txn_type=web_accept&mc_currency=USD&mc_gross=5.00&payer_status=unverified&custom=00001&payment_date=06%" +
                        "3A34%3A42+Feb+24%2C+2013+PST&payment_fee=0.49&address_country_code=qq&charset=windows-1252&payment_gross=5.00&address_zip=11111&ipn_track_id=d05e19875977b&address_state=&discount=0.00&handling_amount=" +
                        "0.00&tax=0.00&item_name=Razarion+1000&address_name=aaa+bbb&last_name=yyyyyy&payment_type=instant&receiver_id=xxxxxxx&address_street=%C4%C4%C4%D6%D6%D6%DC%DC%DC", params);
                return "VERIFIED";
            }
        };
        FinanceService financeServiceMock = EasyMock.createStrictMock(FinanceService.class);
        financeServiceMock.razarionBought("00001", "RAZ1000", "5.00", "USD", "11112222", "hallo@cxyyyy.qqq", "wwwww@qqqqq.sssss", "Completed", "1");
        EasyMock.replay(financeServiceMock);
        AbstractServiceTest.setPrivateField(PayPalIpnHandlerController.class, payPalIpnHandlerController, "financeService", financeServiceMock);
        payPalIpnHandlerController.handleRequest(mockHttpServletRequest, mockHttpServletResponse);
        Assert.assertEquals(200, mockHttpServletResponse.getStatus());
        EasyMock.verify(financeServiceMock);
    }

    @Test
    public void unknownEncoding() throws Exception {
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest("get", "payment_type=instant&receiver_id=7A9K5VMDK42WY&address_street=Stj%3Frngatan+4E");
        mockHttpServletRequest.setParameter("charset", "xxx");
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        PayPalIpnHandlerController payPalIpnHandlerController = new PayPalIpnHandlerController() {
            @Override
            protected String sendVerification(URL url, String params) throws IOException {
                return "VERIFIED";
            }
        };
        payPalIpnHandlerController.handleRequest(mockHttpServletRequest, mockHttpServletResponse);
        Assert.assertEquals(400, mockHttpServletResponse.getStatus());
    }

    @Test
    public void payPalReturnsUnverified() throws Exception {
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest("get", "payment_type=instant&receiver_id=7A9K5VMDK42WY&address_street=Stj%3Frngatan+4E");
        mockHttpServletRequest.setParameter("charset", "windows-1252");
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        PayPalIpnHandlerController payPalIpnHandlerController = new PayPalIpnHandlerController() {
            @Override
            protected String sendVerification(URL url, String params) throws IOException {
                return "UNVERIFIED";
            }
        };
        payPalIpnHandlerController.handleRequest(mockHttpServletRequest, mockHttpServletResponse);
        Assert.assertEquals(400, mockHttpServletResponse.getStatus());
    }

    @Test
    public void payPalReturnsRefunded() throws Exception {
        MockHttpServletRequest mockHttpServletRequest = createMockServletRequest();
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        PayPalIpnHandlerController payPalIpnHandlerController = new PayPalIpnHandlerController() {
            @Override
            protected String sendVerification(URL url, String params) throws IOException {
                return "VERIFIED";
            }
        };
        FinanceService financeServiceMock = EasyMock.createStrictMock(FinanceService.class);
        financeServiceMock.razarionBought("00001", "RAZ1000", "5.00", "USD", "11112222", "hallo@cxyyyy.qqq", "wwwww@qqqqq.sssss", "Completed", "1");
        EasyMock.expectLastCall().andThrow(new WrongPaymentStatusException("Refunded"));
        EasyMock.replay(financeServiceMock);
        AbstractServiceTest.setPrivateField(PayPalIpnHandlerController.class, payPalIpnHandlerController, "financeService", financeServiceMock);
        payPalIpnHandlerController.handleRequest(mockHttpServletRequest, mockHttpServletResponse);
        EasyMock.verify(financeServiceMock);
        Assert.assertEquals(200, mockHttpServletResponse.getStatus());
    }

    @Test
    public void transactionAlreadyProcessed() throws Exception {
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest("get", "payment_type=instant&receiver_id=7A9K5VMDK42WY&address_street=Stj%3Frngatan+4E");
        mockHttpServletRequest.setParameter("charset", "windows-1252");
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        PayPalIpnHandlerController payPalIpnHandlerController = new PayPalIpnHandlerController() {
            @Override
            protected String sendVerification(URL url, String params) throws IOException {
                return "VERIFIED";
            }
        };
        FinanceService financeServiceMock = EasyMock.createStrictMock(FinanceService.class);
        financeServiceMock.razarionBought(null, null, null, null, null, null, null, null, null);
        EasyMock.expectLastCall().andThrow(new TransactionAlreadyProcessedException("1"));
        EasyMock.replay(financeServiceMock);
        AbstractServiceTest.setPrivateField(PayPalIpnHandlerController.class, payPalIpnHandlerController, "financeService", financeServiceMock);
        payPalIpnHandlerController.handleRequest(mockHttpServletRequest, mockHttpServletResponse);
        Assert.assertEquals(200, mockHttpServletResponse.getStatus());
        EasyMock.verify(financeServiceMock);
    }
}
