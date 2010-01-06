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

import com.btxtech.game.services.user.UserService;
import com.btxtech.game.wicket.pages.Info;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 18.01.2009
 * Time: 12:37:58
 */
public class LoginBox extends Panel {
    private String loginName = "User name";
    private String loginPassowrd = "12345678";
    @SpringBean
    private UserService userService;

    public LoginBox() {
        super("signinBox");
        Form form = new Form<LoginBox>("loginForm", new CompoundPropertyModel<LoginBox>(this)) {
            @Override
            protected void onSubmit() {
                if (!userService.login(loginName, loginPassowrd)) {
                    PageParameters parameters = new PageParameters();
                    parameters.add(Info.KEY_MESSAGE, "Login failed. Please try again");
                    setResponsePage(Info.class, parameters);
                } else {
                    setResponsePage(UserPage.class);
                }
            }
        };

        form.add(new TextField<String>("loginName"));
        form.add(new PasswordTextField("loginPassowrd"));
        form.add(new Link("createAccountLink") {

            @Override
            public void onClick() {
                setResponsePage(NewUser.class);
            }
        });
        add(form);
    }

}
