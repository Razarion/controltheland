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

package com.btxtech.game.wicket.pages.cms.content.plugin.changepassword;

import com.btxtech.game.services.common.ExceptionHandler;
import com.btxtech.game.services.user.EmailDoesNotExitException;
import com.btxtech.game.services.user.RegisterService;
import com.btxtech.game.services.user.UserIsNotConfirmedException;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class ForgotPassword extends Panel {
    @SpringBean
    private RegisterService registerService;
    @SpringBean
    private CmsUiService cmsUiService;

    public ForgotPassword(String id) {
        super(id);

        final Model<String> emailModel = new Model<>();
        Form form = new Form("form") {
            @Override
            protected void onSubmit() {
                try {
                    registerService.onForgotPassword(emailModel.getObject());
                    cmsUiService.setMessageResponsePage(ForgotPassword.this, "passwordRequestEmailHasBeenSent", emailModel.getObject());
                } catch (EmailDoesNotExitException e) {
                    ExceptionHandler.handleException(e);
                    cmsUiService.setMessageResponsePage(ForgotPassword.this, "emailIsUnknown", emailModel.getObject());
                } catch (UserIsNotConfirmedException e) {
                    ExceptionHandler.handleException(e);
                    cmsUiService.setMessageResponsePage(ForgotPassword.this, "userEmailNoConfirmed", emailModel.getObject());
                }
            }
        };
        add(form);
        form.add(new TextField<>("emailField", emailModel));
    }
}
