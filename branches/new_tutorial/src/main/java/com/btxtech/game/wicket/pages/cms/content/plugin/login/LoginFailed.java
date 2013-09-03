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
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.wicket.pages.cms.CmsPage;
import com.btxtech.game.wicket.pages.cms.ContentContext;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class LoginFailed extends Panel {
    @SpringBean
    private UserService userService;
    @SpringBean
    private CmsUiService cmsUiService;


    public LoginFailed(String id, ContentContext contentContext) {
        super(id);

        String loginName = contentContext.getPageParameters().get(CmsPage.RESPONSE_PAGE_ADDITIONAL_PARAMETER).toString();
        User user = userService.loadUserFromDb(loginName);
        if (user == null) {
            add(new Label("text", new StringResourceModel("loginFailed", this, null)));
            add(new BookmarkablePageLink<CmsPage>("forgotPassword", CmsPage.class, cmsUiService.getPredefinedDbPageParameters(CmsUtil.CmsPredefinedPage.FORGOT_PASSWORD_REQUEST)));
        } else if (!user.isRegistrationComplete()) {
            add(new Label("text", new StringResourceModel("userEmailNoConfirmed", this, null)));
            add(new BookmarkablePageLink<CmsPage>("forgotPassword", CmsPage.class, null).setVisible(false));
        } else {
            add(new Label("text", new StringResourceModel("loginFailed", this, null)));
            add(new BookmarkablePageLink<CmsPage>("forgotPassword", CmsPage.class, cmsUiService.getPredefinedDbPageParameters(CmsUtil.CmsPredefinedPage.FORGOT_PASSWORD_REQUEST)));
        }
    }
}
