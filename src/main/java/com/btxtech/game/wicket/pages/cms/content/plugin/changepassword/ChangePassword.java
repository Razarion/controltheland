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

import com.btxtech.game.jsre.common.CmsUtil;
import com.btxtech.game.jsre.common.gameengine.services.user.PasswordNotMatchException;
import com.btxtech.game.services.common.ExceptionHandler;
import com.btxtech.game.services.user.NoForgotPasswordEntryException;
import com.btxtech.game.services.user.RegisterService;
import com.btxtech.game.wicket.pages.cms.ContentContext;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class ChangePassword extends Panel {
    @SpringBean
    private RegisterService registerService;
    @SpringBean
    private CmsUiService cmsUiService;

    public ChangePassword(String id, ContentContext contentContext) {
        super(id);

        final Model<String> password = new Model<>();
        final Model<String> confirmPassword = new Model<>();
        final String uuid = contentContext.getPageParameters().get(CmsUtil.FORGOT_PASSWORD_UUID_KEY).toString();
        add(new FeedbackPanel("msgs"));

        Form form = new Form("form") {
            @Override
            protected void onSubmit() {
                try {
                    registerService.onPasswordReset(uuid, password.getObject(), confirmPassword.getObject());
                    cmsUiService.setPredefinedResponsePage(ChangePassword.this, CmsUtil.CmsPredefinedPage.USER_PAGE);
                } catch (PasswordNotMatchException e) {
                    ExceptionHandler.handleException(e);
                    password.setObject("");
                    confirmPassword.setObject("");
                    error(new StringResourceModel("passwordNotMatch", this, null).getObject());
                } catch (NoForgotPasswordEntryException e) {
                    ExceptionHandler.handleException(e);
                    cmsUiService.setMessageResponsePage(ChangePassword.this, "noPasswordForgotRequest", null);
                }
            }
        };
        add(form);
        form.add(new PasswordTextField("password", password));
        form.add(new PasswordTextField("confirmPassword", confirmPassword));
    }
}
