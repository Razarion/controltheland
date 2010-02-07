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

import com.btxtech.game.wicket.WebCommon;
import com.btxtech.game.wicket.pages.basepage.BasePage;
import javax.servlet.http.Cookie;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebResponse;

/**
 * User: beat
 * Date: May 31, 2009
 * Time: 9:53:21 PM
 */
public class Home extends BasePage {
    public Home() {
        add(new HomeMainContent("homeMain"));
        add(new HomeStatistics("homeStatistics"));

        Cookie[] cookies = ((WebRequest) getRequestCycle().getRequest()).getCookies();
        if (WebCommon.getCookieId(cookies) == null) {
            WebCommon.generateAndSetCookieId(((WebResponse) getRequestCycle().getResponse()).getHttpServletResponse());
        }
    }
}
