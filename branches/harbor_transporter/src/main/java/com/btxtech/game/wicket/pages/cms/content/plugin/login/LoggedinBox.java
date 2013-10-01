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
import com.btxtech.game.services.user.SecurityRoles;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import com.btxtech.game.wicket.facebook.FacebookController;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 18.01.2009
 * Time: 12:37:58
 */
@AuthorizeAction(action = Action.RENDER, roles = SecurityRoles.ROLE_USER)
public class LoggedinBox extends Panel {
    @SpringBean
    private UserService userService;
    @SpringBean
    private CmsUiService cmsUiService;

    public LoggedinBox(String id) {
        super(id);
        add(new FacebookController("facebook", FacebookController.Type.LOGGED_IN));
        StatelessForm<LoggedinBox> form = new StatelessForm<LoggedinBox>("loginForm", new Model<LoggedinBox>()) {
            @Override
            protected void onSubmit() {
                AuthenticatedWebSession session = AuthenticatedWebSession.get();
                session.signOut();
                cmsUiService.setPredefinedResponsePage(this, CmsUtil.CmsPredefinedPage.HOME);
            }
        };
        add(form);
        Link link = new Link("nameLink") {

            @Override
            public void onClick() {
                cmsUiService.setPredefinedResponsePage(LoggedinBox.this, CmsUtil.CmsPredefinedPage.USER_PAGE);
            }
        };
        link.add(new Label("name", new IModel() {

            @Override
            public Object getObject() {
                if (userService.getUser() != null) {
                    return userService.getUser().getUsername();
                } else {
                    return "";
                }
            }

            @Override
            public void setObject(Object object) {
                // Ignore
            }

            @Override
            public void detach() {
                // Ignore
            }
        }));
        form.add(link);

    }

}