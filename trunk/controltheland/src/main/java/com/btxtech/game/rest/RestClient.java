package com.btxtech.game.rest;

import com.btxtech.game.services.finance.FacebookPaymentObject;

/**
 * User: beat
 * Date: 23.02.14
 * Time: 15:41
 */
public interface RestClient {
    FacebookPaymentObject getPaymentObject(String paymentId);

    String getAccessToken();
}
