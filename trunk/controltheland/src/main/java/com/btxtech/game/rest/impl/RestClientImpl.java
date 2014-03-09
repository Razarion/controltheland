package com.btxtech.game.rest.impl;

import com.btxtech.game.jsre.common.FacebookUtils;
import com.btxtech.game.rest.RestClient;
import com.btxtech.game.services.finance.FacebookPaymentObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

/**
 * User: beat
 * Date: 23.02.14
 * Time: 15:42
 */
@Component
public class RestClientImpl implements RestClient {
    // TODO this class can not be tested -> jar hell
    @Value(value = "${facebook.appsecret}")
    private String facebookAppSecret;
    @Value(value = "${facebook.appid}")
    private String facebookAppId;

    @Override
    public FacebookPaymentObject getPaymentObject(String paymentId) {
        Client client = ClientBuilder.newClient();
        return client.target(FacebookUtils.GRAPH_URL).path(paymentId).queryParam("access_token", getAccessToken()).request(MediaType.APPLICATION_JSON).get(FacebookPaymentObject.class);
    }

    @Override
    public String getAccessToken() {
        return facebookAppId + "|" + facebookAppSecret;
    }
}
