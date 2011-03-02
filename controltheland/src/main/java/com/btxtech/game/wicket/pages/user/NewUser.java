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

package com.btxtech.game.wicket.pages.user;

import com.btxtech.game.jsre.common.gameengine.services.user.PasswordNotMatchException;
import com.btxtech.game.jsre.common.gameengine.services.user.UserAlreadyExistsException;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.wicket.pages.Info;
import com.btxtech.game.wicket.pages.basepage.BasePage;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class NewUser extends BasePage {
    @SpringBean
    private UserService userService;
    private String name;
    private String password;
    private String confirmPassword;
    private String email;

    public NewUser() {
        Form form = new Form<NewUser>("newUserForm", new CompoundPropertyModel<NewUser>(this)) {
            @Override
            protected void onSubmit() {
                User user = userService.getUser();
                if (user != null) {
                    throw new IllegalStateException("The user is already logged in: " + user);
                }

                try {
                    userService.createUserAndLoggin(name, password, confirmPassword, email, false);
                    setResponsePage(UserPage.class);
                } catch (UserAlreadyExistsException e) {
                    PageParameters parameters = new PageParameters();
                    parameters.add(Info.KEY_MESSAGE, "The user already exists");
                    setResponsePage(Info.class, parameters);
                } catch (PasswordNotMatchException e) {
                    PageParameters parameters = new PageParameters();
                    parameters.add(Info.KEY_MESSAGE, "Password and confirm password do not match");
                    setResponsePage(Info.class, parameters);
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
