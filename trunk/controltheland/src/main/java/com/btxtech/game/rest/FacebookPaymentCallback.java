package com.btxtech.game.rest;

import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.services.common.ExceptionHandler;
import com.btxtech.game.services.finance.FacebookPaymentUpdate;
import com.btxtech.game.services.finance.FinanceService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * User: beat
 * Date: 10.02.14
 * Time: 20:59
 */
@Path(Constants.FACEBOOK_PAYMENT_CALLBACK)
public class FacebookPaymentCallback {
    private static Log log = LogFactory.getLog(ExceptionHandler.class);
    @Autowired
    private FinanceService financeService;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String subscription(@QueryParam("hub.mode") String hubMode, @QueryParam("hub.challenge") String hubChallenge, @QueryParam("hub.verify_token") String hubVerifyToken) {
        // Can not call ExceptionHandler.handleException() here. New session creation takes too long -> timeout
        log.warn("Facebook Payment Callback subscription called.  hubMode: " + hubMode + " hubChallenge: " + hubChallenge + " hubVerifyToken: " + hubVerifyToken);
        return hubChallenge;
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public void update(FacebookPaymentUpdate facebookPaymentUpdate) {
        try {
            financeService.crystalsBoughtViaFacebook(facebookPaymentUpdate);
        } catch (Exception e) {
            ExceptionHandler.handleException(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
}
