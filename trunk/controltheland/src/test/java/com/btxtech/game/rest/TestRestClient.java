package com.btxtech.game.rest;

import com.btxtech.game.services.AbstractServiceTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

/**
 * User: beat
 * Date: 02.03.14
 * Time: 16:34
 */
public class TestRestClient extends AbstractServiceTest {
    @Autowired
    private RestClient restClient;

    @Test
    @DirtiesContext
    public void buyCrystals() throws Exception {
        String accessToken = restClient.getAccessToken();
        Assert.assertTrue(accessToken.length() > 30);
        Assert.assertTrue(accessToken.indexOf("|") > 0);
    }
}
