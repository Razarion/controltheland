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

package com.btxtech.game.wicket.pages.home;

import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.utg.UserTrackingService;
import com.btxtech.game.wicket.WebCommon;
import com.btxtech.game.wicket.pages.Game;
import com.btxtech.game.wicket.pages.info.Info;
import com.btxtech.game.wicket.pages.user.LoggedinBox;
import com.btxtech.game.wicket.pages.user.LoginBox;
import javax.servlet.http.Cookie;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: May 31, 2009
 * Time: 9:53:21 PM
 */
public class Home extends WebPage {
    @SpringBean
    private UserService userService;
    @SpringBean
    private UserTrackingService userTrackingService;
    @SpringBean
    private BaseService baseService;

    public Home() {
        Cookie[] cookies = ((WebRequest) getRequestCycle().getRequest()).getCookies();
        if (WebCommon.getCookieId(cookies) == null) {
            WebCommon.generateAndSetCookieId(((WebResponse) getRequestCycle().getResponse()).getHttpServletResponse());
        }
        // TODO this is ugly
        if (userService.isLoggedin()) {
            add(new LoggedinBox());
        } else {
            add(new LoginBox());
        }

        // add the two main links
        add(new Link("startLink") {
            @Override
            public void onClick() {
                try {
                    baseService.createNewBase();
                    setResponsePage(Game.class);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        add(new Link("infoLink") {
            @Override
            public void onClick() {
                setResponsePage(Info.class);
            }
        });

    }

    @Override
    protected void onBeforeRender() {
        userTrackingService.pageAccess(getClass());

        // TODO this is ugly
        remove("signinBox");
        if (userService.isLoggedin()) {
            add(new LoggedinBox());
        } else {
            add(new LoginBox());
        }
        super.onBeforeRender();
    }

}
