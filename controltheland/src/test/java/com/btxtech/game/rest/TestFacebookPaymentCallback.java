package com.btxtech.game.rest;

import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.finance.FacebookPaymentUpdate;
import com.btxtech.game.services.finance.FinanceService;
import com.btxtech.game.services.user.UserDoesNotExitException;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.WebApplicationException;

/**
 * User: beat
 * Date: 02.03.14
 * Time: 16:45
 */
public class TestFacebookPaymentCallback {

    @Test
    public void subscription() {
        FacebookPaymentCallback facebookPaymentCallback = new FacebookPaymentCallback();
        String returnValue = facebookPaymentCallback.subscription("hub.mode", "hub.challenge", "hub.verify_token");
        Assert.assertEquals("hub.challenge", returnValue);
    }

    @Test
    public void update() throws Exception {
        // Prepare
        FinanceService financeServiceMock = EasyMock.createStrictMock(FinanceService.class);
        financeServiceMock.crystalsBoughtViaFacebook(EasyMock.anyObject(FacebookPaymentUpdate.class));
        financeServiceMock.crystalsBoughtViaFacebook(EasyMock.anyObject(FacebookPaymentUpdate.class));
        EasyMock.expectLastCall().andThrow(new UserDoesNotExitException("xxx"));
        EasyMock.replay(financeServiceMock);
        FacebookPaymentCallback facebookPaymentCallback = new FacebookPaymentCallback();
        AbstractServiceTest.setPrivateField(FacebookPaymentCallback.class, facebookPaymentCallback, "financeService", financeServiceMock);
        // Normal
        facebookPaymentCallback.update(new FacebookPaymentUpdate());
        // Exception
        try {
            facebookPaymentCallback.update(new FacebookPaymentUpdate());
            Assert.fail("WebApplicationException expected");
        } catch (WebApplicationException webApplicationException) {
            // Expected
        }
        EasyMock.verify(financeServiceMock);
    }

}
