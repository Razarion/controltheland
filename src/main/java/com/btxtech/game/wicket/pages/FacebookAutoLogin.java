package com.btxtech.game.wicket.pages;

import com.btxtech.game.services.common.ExceptionHandler;
import com.btxtech.game.services.socialnet.facebook.FacebookSignedRequest;
import com.btxtech.game.services.socialnet.facebook.FacebookUtil;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.UserTrackingService;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 12.07.13
 * Time: 17:55
 */
public class FacebookAutoLogin extends RazarionPage {
    @SpringBean
    private CmsUiService cmsUiService;
    @SpringBean
    private UserService userService;
    @SpringBean
    private UserGuidanceService userGuidanceService;
    @SpringBean
    private UserTrackingService userTrackingService;

    public FacebookAutoLogin(PageParameters parameters) {
        super(parameters);

        try {
            userTrackingService.pageAccess(getClass().getName(), null);
        } catch (Exception e) {
            ExceptionHandler.handleException(e);
        }

        IRequestParameters postParameters = getRequest().getPostParameters();

        String signedRequestParameter = postParameters.getParameterValue("signed_request").toString();
        FacebookSignedRequest facebookSignedRequest = FacebookUtil.createAndCheckFacebookSignedRequest(cmsUiService.getFacebookAppSecret(), signedRequestParameter);
        if (facebookSignedRequest.hasUserId()) {
            // Is authorized by facebook
            if (userService.isFacebookUserRegistered(facebookSignedRequest)) {
                if (!userService.isFacebookLoggedIn(facebookSignedRequest)) {
                    userService.loginFacebookUser(facebookSignedRequest);
                }
            }
            PageParameters gamePageParameters = new PageParameters();
            if (!userGuidanceService.isStartRealGame()) {
                gamePageParameters.add(com.btxtech.game.jsre.client.Game.LEVEL_TASK_ID, Integer.toString(userGuidanceService.getDefaultLevelTaskId()));
            }
            setResponsePage(Game.class, gamePageParameters);
        } else {
            // Is NOT authorized by facebook. This should never happen
            throw new IllegalStateException("User not Authorized by Facebook. Query parameters: " + parameters.toString() + " Post parameters: " + postParameters.toString());
        }
    }
}
