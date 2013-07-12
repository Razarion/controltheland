/*
 * Copyright (c) 2010.
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; version 2 of the License.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 */

package com.btxtech.game.wicket.pages.cms.content.plugin.nickname;

import com.btxtech.game.jsre.client.VerificationRequestCallback;
import com.btxtech.game.jsre.common.gameengine.services.user.UserAlreadyExistsException;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.wicket.pages.Game;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.ajax.form.AjaxFormValidatingBehavior;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.time.Duration;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

public class ChooseNickname extends Panel {
    @SpringBean
    private UserService userService;
    @SpringBean
    private CmsUiService cmsUiService;
    @SpringBean
    private UserGuidanceService userGuidanceService;
    private Log log = LogFactory.getLog(ChooseNickname.class);

    /**
     * Constructor
     */
    public ChooseNickname(String id) {
        super(id);

        Bean bean = new Bean();
        final Form<Bean> form = new Form<>("form", new CompoundPropertyModel<>(bean));
        add(form);
        form.setOutputMarkupId(true);

        final FeedbackPanel feedback = new FeedbackPanel("feedback");
        feedback.setOutputMarkupId(true);
        form.add(feedback);

        FormComponent<String> fc = new RequiredTextField<>("name");
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
                Bean bean = form.getModelObject();
                VerificationRequestCallback.ErrorResult errorResult = userService.isNickNameValid(bean.getName());
                if (errorResult == null) {
                    try {
                        userService.createAndLoginFacebookUser(cmsUiService.getAndClearFacebookSignedRequest(), bean.getName());
                    } catch (UserAlreadyExistsException e) {
                        throw new RuntimeException(e);
                    }
                    PageParameters gamePageParameters = new PageParameters();
                    if (!userGuidanceService.isStartRealGame()) {
                        gamePageParameters.add(com.btxtech.game.jsre.client.Game.LEVEL_TASK_ID, Integer.toString(userGuidanceService.getDefaultLevelTaskId()));
                    }
                    setResponsePage(Game.class, gamePageParameters);
                } else {
                    error("Invalid nick name: " + getLocalizedErrorText(errorResult));
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

    /**
     * simple java bean.
     */
    public static class Bean {
        private String name;

        /**
         * Gets name.
         *
         * @return name
         */
        public String getName() {
            return name;
        }

        /**
         * Sets name.
         *
         * @param name name
         */
        public void setName(String name) {
            this.name = name;
        }
    }
}
