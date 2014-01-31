package com.btxtech.game.wicket.pages;

import com.btxtech.game.services.cms.page.DbPage;
import com.btxtech.game.services.common.ExceptionHandler;
import com.btxtech.game.services.socialnet.facebook.FacebookSignedRequest;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.utg.UserTrackingService;
import com.btxtech.game.wicket.facebook.FacebookController;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 12.07.13
 * Time: 17:55
 */
public class FacebookAppStart extends AbstractFacebookRazarionPage {
    @SpringBean
    private UserTrackingService userTrackingService;
    @SpringBean
    private CmsUiService cmsUiService;

    public FacebookAppStart(PageParameters parameters) {
        super(parameters);
        add(new FeedbackPanel("feedback"));
        handleFacebookSignedRequest(parameters);
    }

    @Override
    protected void onUserAuthorizedByFacebook(FacebookSignedRequest facebookSignedRequest) {
        setResponsePage(new FacebookAppNickName(getPageParameters(), facebookSignedRequest));
    }

    @Override
    protected void onUserNotAuthorizedByFacebook(String historyInfo, PageParameters parameters) {
        try {
            userTrackingService.pageAccess(getClass().getName(), historyInfo + parameters.toString());
        } catch (Exception e) {
            ExceptionHandler.handleException(e);
        }
        add(new FacebookController("facebook", FacebookController.Type.OAUTH_DIALOG));
        doCmsPart();
    }

    @Override
    protected DbPage getDbPage() {
        return cmsUiService.getFacebookAppPage();
    }

    @Override
    protected void displayErrorLoggedInPage(User user) {
        error(new StringResourceModel("facebookAlreadyLoggedIn", null, new Object[]{user.getUsername()}).getString());
        add(new Form("form").setVisible(false));
        add(new FacebookController("facebook"));
        add(new Label("title", ""));
    }
}
