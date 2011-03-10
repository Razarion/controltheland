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
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.utg.UserTrackingService;
import com.btxtech.game.wicket.WebCommon;
import com.btxtech.game.wicket.pages.Game;
import com.btxtech.game.wicket.pages.basepage.BasePage;
import com.btxtech.game.wicket.pages.info.Info;
import com.btxtech.game.wicket.pages.user.LoggedinBox;
import com.btxtech.game.wicket.pages.user.LoginBox;
import com.btxtech.game.wicket.uiservices.CmsImageResource;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.spring.injection.annot.SpringBean;

import javax.servlet.http.Cookie;

/**
 * User: beat
 * Date: May 31, 2009
 * Time: 9:53:21 PM
 */
public class Home extends WebPage implements IHeaderContributor {
    @SpringBean
    private UserService userService;
    @SpringBean
    private UserTrackingService userTrackingService;
    @SpringBean
    private CmsService cmsService;

    public Home() {
        Cookie[] cookies = ((WebRequest) getRequestCycle().getRequest()).getCookies();
        if (WebCommon.getCookieId(cookies) == null) {
            WebCommon.generateAndSetCookieId(((WebResponse) getRequestCycle().getResponse()).getHttpServletResponse());
        }
        add(new LoggedinBox("loggedinBox"));
        add(new LoginBox("loginBox"));

        add(new Label("style", new PropertyModel(cmsService.getHomeContentStyleDTO(), "style")));
        add(new Label("text", new PropertyModel(cmsService.getHomeContentStyleDTO(), "text")).setEscapeModelStrings(false));

        BookmarkablePageLink<WebPage> startLink = new BookmarkablePageLink<WebPage>("startLink", Game.class);
        add(startLink);
        startLink.add(CmsImageResource.createImage("startImage", CmsImageResource.ImageId.START));

        BookmarkablePageLink<WebPage> infoLink = new BookmarkablePageLink<WebPage>("infoLink", Info.class);
        add(infoLink);
        infoLink.add(CmsImageResource.createImage("infoImage", CmsImageResource.ImageId.INFO));
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        userTrackingService.pageAccess(getClass());
    }

    @Override
    public void renderHead(IHeaderResponse iHeaderResponse) {
        if (!userTrackingService.isJavaScriptDetected()) {
            iHeaderResponse.renderJavascript(BasePage.JAVA_SCRIPT_DETECTION, null);
        }
    }


}
