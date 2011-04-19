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

import com.btxtech.game.services.user.AlreadyLoggedInException;
import com.btxtech.game.services.user.SecurityRoles;
import com.btxtech.game.wicket.pages.Info;
import org.apache.wicket.PageParameters;
import org.apache.wicket.authentication.AuthenticatedWebSession;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

/**
 * User: beat
 * Date: 18.01.2009
 * Time: 12:37:58
 */
@AuthorizeAction(action = Action.RENDER, deny = SecurityRoles.ROLE_USER)
public class LoginBox extends Panel {
    private String loginName = "User name";
    private String loginPassowrd = "12345678";

    public LoginBox(String id, boolean showRegisterLink) {
        super(id);
        Form form = new Form<LoginBox>("loginForm", new CompoundPropertyModel<LoginBox>(this)) {
            @Override
            protected void onSubmit() {
                try {
                    AuthenticatedWebSession session = AuthenticatedWebSession.get();
                    if (session.signIn(loginName, loginPassowrd)) {
                        setResponsePage(UserPage.class);
                    } else {
                        PageParameters parameters = new PageParameters();
                        parameters.add(Info.KEY_MESSAGE, "Login failed. Please try again");
                        setResponsePage(Info.class, parameters);
                    }
                } catch (AlreadyLoggedInException e) {
                    PageParameters parameters = new PageParameters();
                    parameters.add(Info.KEY_MESSAGE, e.getMessage());
                    setResponsePage(Info.class, parameters);
                }
            }
        };

        form.add(new TextField<String>("loginName"));
        form.add(new PasswordTextField("loginPassowrd"));
        form.add(new BookmarkablePageLink<NewUser>("createAccountLink", NewUser.class).setVisible(showRegisterLink));

        add(form);
    }

}
