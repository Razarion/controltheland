package com.btxtech.game.wicket.pages;

import com.btxtech.game.jsre.client.VerificationRequestCallback;
import com.btxtech.game.jsre.common.gameengine.services.user.UserAlreadyExistsException;
import com.btxtech.game.services.common.ExceptionHandler;
import com.btxtech.game.services.socialnet.facebook.FacebookSignedRequest;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.UserTrackingService;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.ajax.form.AjaxFormValidatingBehavior;
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
public class FacebookAppNickName extends RazarionPage {
    @SpringBean
    private UserService userService;
    @SpringBean
    private CmsUiService cmsUiService;
    @SpringBean
    private UserGuidanceService userGuidanceService;
    @SpringBean
    private UserTrackingService userTrackingService;
    private Log log = LogFactory.getLog(FacebookAppNickName.class);
    private FacebookSignedRequest facebookSignedRequest;

    public FacebookAppNickName(FacebookSignedRequest facebookSignedRequest) {
        this.facebookSignedRequest = facebookSignedRequest;
        Form form = new Form("form");
        add(form);
        form.setOutputMarkupId(true);

        FeedbackPanel feedback = new FeedbackPanel("feedback");
        feedback.setOutputMarkupId(true);
        form.add(feedback);

        final Model<String> nameModel = new Model<>();

        TextField<String> fc = new TextField<>("name", nameModel);
        fc.add(new IValidator<String>() {

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
        form.add(fc);

        form.add(new Button("goButton") {
            @Override
            public void onSubmit() {
                VerificationRequestCallback.ErrorResult errorResult = userService.isNickNameValid(nameModel.getObject());
                if (errorResult == null) {
                    try {
                        userService.createAndLoginFacebookUser(FacebookAppNickName.this.facebookSignedRequest, nameModel.getObject());
                    } catch (UserAlreadyExistsException e) {
                        throw new RuntimeException(e);
                    }
                    PageParameters gamePageParameters = new PageParameters();
                    if (!userGuidanceService.isStartRealGame()) {
                        gamePageParameters.add(com.btxtech.game.jsre.client.Game.LEVEL_TASK_ID, Integer.toString(userGuidanceService.getDefaultLevelTaskId()));
                    }
                    setResponsePage(Game.class, gamePageParameters);
                } else {
                    error(getLocalizedErrorText(errorResult));
                }
            }
        });

        // attach an ajax validation behavior to all form component's onkeydown
        // event and throttle it down to once per second
        AjaxFormValidatingBehavior.addToAllFormComponents(form, "onkeyup", Duration.milliseconds(250));
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

    @Override
    protected void onBeforeRender() {
        try {
            userTrackingService.pageAccess(getClass().getName(), getRequest().getPostParameters().toString());
        } catch (Exception e) {
            ExceptionHandler.handleException(e);
        }
        super.onBeforeRender();
    }
}
