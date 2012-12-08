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

import com.btxtech.game.jsre.common.gameengine.services.user.UserAlreadyExistsException;
import com.btxtech.game.jsre.client.InvalidNickName;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.wicket.pages.Game;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.wicket.IClusterable;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.form.AjaxFormValidatingBehavior;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.time.Duration;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.ValidationError;
import org.apache.wicket.validation.validator.AbstractValidator;

public class ChooseNickname extends Panel {
    @SpringBean
    private UserService userService;
    @SpringBean
    private CmsUiService cmsUiService;
    @SpringBean
    private UserGuidanceService userGuidanceService;

    /**
     * Constructor
     */
    public ChooseNickname(String id) {
        super(id);

        Bean bean = new Bean();
        final Form<Bean> form = new Form<>("form", new CompoundPropertyModel<Bean>(bean));
        add(form);
        form.setOutputMarkupId(true);

        final FeedbackPanel feedback = new FeedbackPanel("feedback");
        feedback.setOutputMarkupId(true);
        form.add(feedback);

        FormComponent<String> fc = new RequiredTextField<>("name");
        fc.add(new AbstractValidator<String>() {

            @Override
            protected void onValidate(IValidatable<String> validatable) {
                InvalidNickName invalidNickName = userService.isNickNameValid(validatable.getValue());
                if (invalidNickName != null) {
                    ValidationError error = new ValidationError();
                    error.addMessageKey("NickNameError");
                    error.setVariable("error", invalidNickName.getErrorMsg());
                    validatable.error(error);
                }
            }
        });
        form.add(fc);

        form.add(new Button("goButton") {
            @Override
            public void onSubmit() {
                Bean bean = form.getModelObject();
                InvalidNickName invalidNickName = userService.isNickNameValid(bean.getName());
                if (invalidNickName == null) {
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
                    error("Invalid nick name: " + invalidNickName.getErrorMsg());
                }
            }
        });

        // attach an ajax validation behavior to all form component's onkeydown
        // event and throttle it down to once per second
        AjaxFormValidatingBehavior.addToAllFormComponents(form, "onkeyup", Duration.milliseconds(250));
    }

    /**
     * simple java bean.
     */
    public static class Bean implements IClusterable {
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
