package com.btxtech.game.wicket.pages;

import com.btxtech.game.jsre.client.VerificationRequestCallback;
import com.btxtech.game.jsre.common.FacebookUtils;
import com.btxtech.game.jsre.common.gameengine.services.user.UserAlreadyExistsException;
import com.btxtech.game.services.cms.page.DbPage;
import com.btxtech.game.services.common.ExceptionHandler;
import com.btxtech.game.services.socialnet.facebook.FacebookSignedRequest;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.utg.UserTrackingService;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.ajax.form.AjaxFormValidatingBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.time.Duration;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

/**
 * User: beat
 * Date: 12.07.13
 * Time: 18:22
 */
public class FacebookAppNickName extends AbstractFacebookRazarionPage {
    @SpringBean
    private UserService userService;
    @SpringBean
    private UserTrackingService userTrackingService;
    @SpringBean
    private CmsUiService cmsUiService;
    private Log log = LogFactory.getLog(FacebookAppNickName.class);

    public FacebookAppNickName(PageParameters parameters) {
        super(parameters);
        handleFacebookSignedRequest(parameters);
    }

    public FacebookAppNickName(PageParameters parameters, FacebookSignedRequest facebookSignedRequest) {
        super(parameters);
        try {
            userTrackingService.pageAccess(getClass().getName(), "---User Authorized by Facebook but NOT registered by Game--- Parameters: " + parameters.toString());
        } catch (Exception e) {
            ExceptionHandler.handleException(e);
        }
        displayNickname(facebookSignedRequest);
    }

    @Override
    protected void onUserAuthorizedByFacebook(FacebookSignedRequest facebookSignedRequest) {
        displayNickname(facebookSignedRequest);
    }

    protected void displayNickname(final FacebookSignedRequest facebookSignedRequest) {
        Form form = new Form("nickNameForm");
        add(form);
        form.setOutputMarkupId(true);

        FeedbackPanel feedback = new FeedbackPanel("feedback");
        feedback.setOutputMarkupId(true);
        form.add(feedback);

        String nickname = null;
        for (int i = 0; i < 1000; i++) {
            nickname = FacebookUtils.createNickNameSuggestion(facebookSignedRequest.getFirstName(), facebookSignedRequest.getLastName());
            if (userService.isNickNameValid(nickname) == null) {
                break;
            } else {
                nickname = null;
            }
        }
        if (nickname == null) {
            ExceptionHandler.handleException("Unable to generate Facebook nickname. FirstName=" + facebookSignedRequest.getFirstName() + " LastName=" + facebookSignedRequest.getLastName());
        }

        final Model<String> nameModel = new Model<>(nickname);

        TextField<String> nameField = new TextField<>("name", nameModel);
        nameField.add(new IValidator<String>() {

            @Override
            public void validate(IValidatable<String> validatable) {
                VerificationRequestCallback.ErrorResult errorResult = userService.isNickNameValid(validatable.getValue());
                if (errorResult != null) {
                    ValidationError error = new ValidationError();
                    error.addKey("NickNameError");
                    error.setVariable("error", getLocalizedErrorText(errorResult));
                    validatable.error(error);
                }
            }
        });
        form.add(nameField);

        form.add(new Button("goButton") {
            @Override
            public void onSubmit() {
                VerificationRequestCallback.ErrorResult errorResult = userService.isNickNameValid(nameModel.getObject());
                if (errorResult == null) {
                    try {
                        userService.createAndLoginFacebookUser(facebookSignedRequest, nameModel.getObject());
                    } catch (UserAlreadyExistsException e) {
                        throw new RuntimeException(e);
                    }
                    setGameResponsePage();
                } else {
                    error(getLocalizedErrorText(errorResult));
                }
            }
        });

        // attach an ajax validation behavior to all form component's onkeydown
        // event and throttle it down to once per second
        AjaxFormValidatingBehavior.addToAllFormComponents(form, "onkeyup", Duration.milliseconds(250));

        doCmsPart();
    }

    @Override
    protected void onUserNotAuthorizedByFacebook(String historyInfo, PageParameters parameters) {
        try {
            userTrackingService.pageAccess(getClass().getName(), historyInfo + parameters.toString());
        } catch (Exception e) {
            ExceptionHandler.handleException(e);
        }
        setResponsePage(FacebookAppStart.class);
    }

    @Override
    protected DbPage getDbPage() {
        return cmsUiService.getFacebookNickNamePage();
    }

    @Override
    protected void displayErrorLoggedInPage(User user) {
        error(new StringResourceModel("facebookAlreadyLoggedIn", null, new Object[]{user.getUsername()}).getString());
        add(new Form("form").setVisible(false));
        add(new Form("nickNameForm").setVisible(false));
        add(new Label("title", ""));
    }

    private String getLocalizedErrorText(VerificationRequestCallback.ErrorResult errorResult) {
        switch (errorResult) {
            case TO_SHORT:
                return new StringResourceModel("nameToShort", this, null).getString();
            case ALREADY_USED:
                return new StringResourceModel("nameAlreadyUsed", this, null).getString();
            case UNKNOWN_ERROR:
                return new StringResourceModel("unknownErrorReceived", this, null).getString();
            default:
                log.warn("ChooseNickname: unknown errorResult: " + errorResult);
                return "???";
        }
    }
}
