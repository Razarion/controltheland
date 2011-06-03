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

package com.btxtech.game.wicket.pages.cms;

import com.btxtech.game.services.cms.CmsService;
import com.btxtech.game.services.user.SecurityRoles;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.utg.UserTrackingService;
import com.btxtech.game.wicket.WebCommon;
import com.btxtech.game.wicket.pages.Game;
import com.btxtech.game.wicket.pages.basepage.BasePage;
import com.btxtech.game.wicket.pages.info.Info;
import com.btxtech.game.wicket.pages.user.LoginBox;
import com.btxtech.game.wicket.pages.user.NewUser;
import com.btxtech.game.wicket.uiservices.CmsImageResource;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: May 31, 2009
 * Time: 9:53:21 PM
 */
@Deprecated
public class Home extends WebPage implements IHeaderContributor {
    @SpringBean
    private UserService userService;
    @SpringBean
    private UserTrackingService userTrackingService;
    @SpringBean
    private CmsService cmsService;

    public Home() {
        add(new LoginBox("loginBox", false));

        add(new Label("style", cmsService.getDbCmsHomeLayout().getCssString()));
        add(new Label("text", cmsService.getDbCmsHomeText().getText()).setEscapeModelStrings(false));

        BookmarkablePageLink<WebPage> startLink = new BookmarkablePageLink<WebPage>("startLink", Game.class);
        add(startLink);
        startLink.add(CmsImageResource.createImage("startImage", CmsImageResource.ImageId.START));

        BookmarkablePageLink<WebPage> infoLink = new BookmarkablePageLink<WebPage>("infoLink", Info.class);
        add(infoLink);
        infoLink.add(CmsImageResource.createImage("infoImage", CmsImageResource.ImageId.INFO));

        BookmarkablePageLink<WebPage> registerLink = new BookmarkablePageLink<WebPage>("registerLink", NewUser.class);
        registerLink.setEnabled(!WebCommon.isAuthorized(SecurityRoles.ROLE_USER));
        add(registerLink);
        registerLink.add(CmsImageResource.createImage("registerImage", CmsImageResource.ImageId.REGISTER));
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        userTrackingService.pageAccess(getClass());
        if(userTrackingService.hasCookieToAdd()) {
            WebCommon.addCookieId(((WebResponse) getRequestCycle().getResponse()).getHttpServletResponse(), userTrackingService.getAndClearCookieToAdd());
        }
    }

    @Override
    public void renderHead(IHeaderResponse iHeaderResponse) {
        if (!userTrackingService.isJavaScriptDetected()) {
            iHeaderResponse.renderJavascript(BasePage.JAVA_SCRIPT_DETECTION, null);
        }
    }


}
