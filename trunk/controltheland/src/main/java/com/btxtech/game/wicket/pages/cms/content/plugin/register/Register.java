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

package com.btxtech.game.wicket.pages.cms.content.plugin.register;

import com.btxtech.game.jsre.common.gameengine.services.user.PasswordNotMatchException;
import com.btxtech.game.jsre.common.gameengine.services.user.UserAlreadyExistsException;
import com.btxtech.game.services.cms.DbPage;
import com.btxtech.game.services.user.AlreadyLoggedInException;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class Register extends Panel {
    @SpringBean
    private UserService userService;
    @SpringBean
    private CmsUiService cmsUiService;
    private String name;
    private String password;
    private String confirmPassword;
    private String email;

    public Register(String id) {
        super(id);
        if (userService.isRegistered()) {
            cmsUiService.setMessageResponsePage(this, "Already logged in as: " + userService.getUser().getUsername());
            return;
        }

        Form form = new Form<Register>("newUserForm", new CompoundPropertyModel<Register>(this)) {
            @Override
            protected void onSubmit() {
                try {
                    userService.createUser(name, password, confirmPassword, email);
                    cmsUiService.getSecurityCmsUiService().signIn(name, password);
                    cmsUiService.setPredefinedResponsePage(this, DbPage.PredefinedType.USER_PAGE);
                } catch (AlreadyLoggedInException e) {
                    cmsUiService.setMessageResponsePage(this, e.getMessage());
                } catch (UserAlreadyExistsException e) {
                    cmsUiService.setMessageResponsePage(this, "The user already exists");
                } catch (PasswordNotMatchException e) {
                    cmsUiService.setMessageResponsePage(this, "Password and confirm password do not match");
                }
            }
        };

        form.add(new TextField<String>("name"));
        form.add(new TextField("email"));
        form.add(new PasswordTextField("password"));
        form.add(new PasswordTextField("confirmPassword"));
        add(form);

    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
