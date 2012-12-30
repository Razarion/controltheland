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
import com.btxtech.game.wicket.pages.cms.CmsPage;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import com.btxtech.game.wicket.uiservices.facebook.FacebookController;
import org.apache.wicket.authentication.AuthenticatedWebSession;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.StatelessForm;
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
        add(new FacebookController("facebook", FacebookController.Type.AUTO_LOGON));
        StatelessForm<LoginBox> form = new StatelessForm<LoginBox>("loginForm", new CompoundPropertyModel<LoginBox>(this)) {
            @Override
            protected void onSubmit() {
                try {
                    AuthenticatedWebSession session = AuthenticatedWebSession.get();
                    if (session.signIn(loginName, loginPassowrd)) {
                        cmsUiService.setPredefinedResponsePage(this, CmsUtil.CmsPredefinedPage.USER_PAGE);
                    } else {
                        cmsUiService.setMessageResponsePage(this, "Login failed. Please try again.<br><br>Newly created accounts must be activated first. Check your email.");
                    }
                } catch (AlreadyLoggedInException e) {
                    cmsUiService.setMessageResponsePage(this, e.getMessage());
                }
            }
        };

        form.add(new TextField<String>("loginName"));
        form.add(new PasswordTextField("loginPassowrd"));
        // form.add(new BookmarkablePageLink<CmsPage>("createAccountLink", CmsPage.class, cmsUiService.getPredefinedDbPageParameters(CmsUtil.CmsPredefinedPage.REGISTER)).setVisible(showRegisterLink));

        add(form);
    }

}
