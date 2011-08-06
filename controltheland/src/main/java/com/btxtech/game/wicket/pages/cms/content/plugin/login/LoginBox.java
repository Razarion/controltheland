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

package com.btxtech.game.wicket.pages.cms.content.plugin.login;

import com.btxtech.game.jsre.common.CmsUtil;
import com.btxtech.game.services.user.AlreadyLoggedInException;
import com.btxtech.game.services.user.SecurityRoles;
import com.btxtech.game.wicket.pages.user.NewUser;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.wicket.authentication.AuthenticatedWebSession;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 18.01.2009
 * Time: 12:37:58
 */
@AuthorizeAction(action = Action.RENDER, deny = SecurityRoles.ROLE_USER)
public class LoginBox extends Panel {
    private String loginName = "User name";
    private String loginPassowrd = "12345678";
    @SpringBean
    private CmsUiService cmsUiService;

    public LoginBox(String id, boolean showRegisterLink) {
        super(id);
        Form form = new Form<LoginBox>("loginForm", new CompoundPropertyModel<LoginBox>(this)) {
            @Override
            protected void onSubmit() {
                try {
                    AuthenticatedWebSession session = AuthenticatedWebSession.get();
                    if (session.signIn(loginName, loginPassowrd)) {
                        cmsUiService.setPredefinedResponsePage(this, CmsUtil.CmsPredefinedPage.USER_PAGE);
                    } else {
                        cmsUiService.setMessageResponsePage(this, "Login failed. Please try again");
                    }
                } catch (AlreadyLoggedInException e) {
                    cmsUiService.setMessageResponsePage(this, e.getMessage());
                }
            }
        };

        form.add(new TextField<String>("loginName"));
        form.add(new PasswordTextField("loginPassowrd"));
        form.add(new BookmarkablePageLink<NewUser>("createAccountLink", NewUser.class).setVisible(showRegisterLink));

        add(form);
    }

}
