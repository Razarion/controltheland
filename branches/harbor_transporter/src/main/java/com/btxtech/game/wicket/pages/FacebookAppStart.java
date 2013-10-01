package com.btxtech.game.wicket.pages;

import com.btxtech.game.services.cms.page.DbPage;
import com.btxtech.game.services.common.ExceptionHandler;
import com.btxtech.game.services.socialnet.facebook.FacebookSignedRequest;
import com.btxtech.game.services.socialnet.facebook.FacebookUtil;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.UserTrackingService;
import com.btxtech.game.wicket.facebook.FacebookController;
import com.btxtech.game.wicket.pages.cms.ContentContext;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

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
        if ("access_denied".equals(parameters.get("error").toString())) {
            try {
                userTrackingService.pageAccess(getClass().getName(), "---Access Denied--- Query Parameters: " + parameters.toString());
            } catch (Exception e) {
                ExceptionHandler.handleException(e);
            }
            setGameResponsePage();
        } else {
            IRequestParameters postParameters = getRequest().getPostParameters();
            String signedRequestParameter = postParameters.getParameterValue("signed_request").toString();
            if (StringUtils.isEmpty(signedRequestParameter)) {
                User user = userService.getUser();
                if (user != null) {
                    try {
                        userTrackingService.pageAccess(getClass().getName(), "---User emty signed request but logged in with user Game--- Parameters: " + parameters.toString());
                    } catch (Exception e) {
                        ExceptionHandler.handleException(e);
                    }
                    displayErrorLoggedInPage(user);
                } else {
                    displayOAuthLoginPage("No signed request", parameters);
                }
            } else {
                onSignedRequest(parameters, signedRequestParameter);
            }
        }
    }

    private void onSignedRequest(PageParameters parameters, String signedRequestParameter) {
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
                    displayErrorLoggedInPage(user);
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
            // Is NOT authorized by facebook
            User user = userService.getUser();
            if (user != null) {
                try {
                    userTrackingService.pageAccess(getClass().getName(), "---User NOT Authorized by Facebook but logged in with different user Game--- Parameters: " + parameters.toString());
                } catch (Exception e) {
                    ExceptionHandler.handleException(e);
                }
                displayErrorLoggedInPage(user);
            } else {
                displayOAuthLoginPage("---User NOT Authorized by Facebook---", parameters);
            }
        }
    }

    private void displayErrorLoggedInPage(User user) {
        error(new StringResourceModel("facebookAlreadyLoggedIn", null, new Object[]{user.getUsername()}).getString());
        add(new Form("form").setVisible(false));
        add(new FacebookController("facebook"));
        add(new Label("title", ""));
    }

    private void displayOAuthLoginPage(String historyInfo, PageParameters parameters) {
        try {
            userTrackingService.pageAccess(getClass().getName(), historyInfo + parameters.toString());
        } catch (Exception e) {
            ExceptionHandler.handleException(e);
        }
        DbPage dbPage = cmsUiService.getFacebookAppPage();
        setDefaultModel(new CompoundPropertyModel<>(new LoadableDetachableModel<DbPage>() {

            @Override
            protected DbPage load() {
                return cmsUiService.getFacebookAppPage();
            }
        }));
        add(new Label("title", dbPage.getDbI18nName().getString(getLocale())));
        add(new FacebookController("facebook", FacebookController.Type.OAUTH_DIALOG));
        Form form = new Form("form");
        add(form);
        form.add(cmsUiService.getRootComponent(dbPage, "content", new ContentContext(parameters, getLocale())));
    }

    private void setGameResponsePage() {
        PageParameters gamePageParameters = new PageParameters();
        if (!userGuidanceService.isStartRealGame()) {
            gamePageParameters.add(com.btxtech.game.jsre.client.Game.LEVEL_TASK_ID, Integer.toString(userGuidanceService.getDefaultLevelTaskId()));
        }
        setResponsePage(Game.class, gamePageParameters);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        DbPage dbPage = (DbPage) getDefaultModelObject();
        if (dbPage != null) {
            cmsUiService.renderCmsCssHead(response, dbPage.getId());
        }
    }
}
