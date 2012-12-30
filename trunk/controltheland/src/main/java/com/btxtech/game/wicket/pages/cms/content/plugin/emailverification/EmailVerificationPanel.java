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

package com.btxtech.game.wicket.pages.cms.content.plugin.emailverification;

import com.btxtech.game.jsre.common.CmsUtil;
import com.btxtech.game.services.user.EmailIsAlreadyVerifiedException;
import com.btxtech.game.services.user.RegisterService;
import com.btxtech.game.services.user.SecurityRoles;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.wicket.pages.cms.ContentContext;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import com.btxtech.game.wicket.uiservices.facebook.FacebookController;
import org.apache.wicket.authentication.AuthenticatedWebSession;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeAction;
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
public class EmailVerificationPanel extends Panel {
    @SpringBean
    private RegisterService registerService;
    @SpringBean
    private UserService userService;

    public EmailVerificationPanel(String id, ContentContext contentContext) {
        super(id);
        try {
            String verificationId = contentContext.getPageParameters().getString(CmsUtil.EMAIL_VERIFICATION_KEY);
            User user = registerService.onVerificationPageCalled(verificationId);
            userService.loginIfNotLoggedIn(user);
            add(new Label("message", "Thanks for registering"));
        } catch (EmailIsAlreadyVerifiedException e) {
            add(new Label("message", "The email has already been verified"));
        }
    }

}