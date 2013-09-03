package com.btxtech.game.wicket.pages;

import com.btxtech.game.services.common.ExceptionHandler;
import com.btxtech.game.services.socialnet.facebook.FacebookSignedRequest;
import com.btxtech.game.services.socialnet.facebook.FacebookUtil;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.UserTrackingService;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptContentHeaderItem;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.template.PackageTextTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * User: beat
 * Date: 12.07.13
 * Time: 17:55
 */
public class FacebookAppStart extends RazarionPage {
    @SpringBean
    private CmsUiService cmsUiService;
    @SpringBean
    private UserService userService;
    @SpringBean
    private UserGuidanceService userGuidanceService;
    @SpringBean
    private UserTrackingService userTrackingService;

    public FacebookAppStart(PageParameters parameters) {
        super(parameters);

        add(new FeedbackPanel("feedback"));

        IRequestParameters postParameters = getRequest().getPostParameters();

        if ("access_denied".equals(parameters.get("error").toString())) {
            try {
                userTrackingService.pageAccess(getClass().getName(), "---Access Denied--- Query Parameters: " + parameters.toString());
            } catch (Exception e) {
                ExceptionHandler.handleException(e);
            }
            setGameResponsePage();
        } else {
            String signedRequestParameter = postParameters.getParameterValue("signed_request").toString();
            FacebookSignedRequest facebookSignedRequest = FacebookUtil.createAndCheckFacebookSignedRequest(cmsUiService.getFacebookAppSecret(), signedRequestParameter);
            if (facebookSignedRequest.hasUserId()) {
                // Is authorized by facebook
                if (userService.isFacebookUserRegistered(facebookSignedRequest)) {
                    if (!userService.isFacebookLoggedIn(facebookSignedRequest)) {
                        userService.loginFacebookUser(facebookSignedRequest);
                    }
                    try {
                        userTrackingService.pageAccess(getClass().getName(), "---User Authorized by Facebook and registered by Game--- Parameters: " + parameters.toString());
                    } catch (Exception e) {
                        ExceptionHandler.handleException(e);
                    }
                    setGameResponsePage();
                } else {
                    User user = userService.getUser();
                    if (user != null) {
                        try {
                            userTrackingService.pageAccess(getClass().getName(), "---User Authorized by Facebook but logged in with different user Game--- Parameters: " + parameters.toString());
                        } catch (Exception e) {
                            ExceptionHandler.handleException(e);
                        }
                        error(new StringResourceModel("facebookAlreadyLoggedIn", null, new Object[]{user.getUsername()}).getString());
                    } else {
                        try {
                            userTrackingService.pageAccess(getClass().getName(), "---User Authorized by Facebook but NOT registered by Game--- Parameters: " + parameters.toString());
                        } catch (Exception e) {
                            ExceptionHandler.handleException(e);
                        }
                        setGameResponsePage();
                    }
                }
            } else {
                User user = userService.getUser();
                if (user != null) {
                    try {
                        userTrackingService.pageAccess(getClass().getName(), "---User NOT Authorized by Facebook but logged in with different user Game--- Parameters: " + parameters.toString());
                    } catch (Exception e) {
                        ExceptionHandler.handleException(e);
                    }
                    error(new StringResourceModel("facebookAlreadyLoggedIn", null, new Object[]{user.getUsername()}).getString());
                } else {
                    // Is NOT authorized by facebook
                    try {
                        userTrackingService.pageAccess(getClass().getName(), "---User NOT Authorized by Facebook--- Query Parameters: " + parameters.toString());
                    } catch (Exception e) {
                        ExceptionHandler.handleException(e);
                    }
                    setGameResponsePage();
                }
            }
        }
    }

    private void setGameResponsePage() {
        PageParameters gamePageParameters = new PageParameters();
        if (!userGuidanceService.isStartRealGame()) {
            gamePageParameters.add(com.btxtech.game.jsre.client.Game.LEVEL_TASK_ID, Integer.toString(userGuidanceService.getDefaultLevelTaskId()));
        }
        setResponsePage(Game.class, gamePageParameters);
    }
}
