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

package com.btxtech.game.jsre.client;

import com.btxtech.game.jsre.common.CmsUtil;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;

/**
 * User: beat
 * Date: 10.11.2010
 * Time: 17:57:12
 */
public class WebBrowserCustomButton extends ExtendedCustomButton {
    public WebBrowserCustomButton(String image, String toolTip, final CmsUtil.CmsPredefinedPage cmsPredefinedPage) {
        super(image, false, toolTip);
        addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Window.open(Connection.getInstance().getGameInfo().getPredefinedUrls().get(cmsPredefinedPage), CmsUtil.TARGET_BLANK, "");
            }
        });
    }
}