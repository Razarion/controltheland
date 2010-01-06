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

package com.btxtech.game.wicket.pages.entergame;

import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.wicket.pages.basepage.BasePage;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: Oct 14, 2009
 * Time: 8:53:46 PM
 */
public class EnterBasePanel extends BasePage {
    @SpringBean
    private UserService userService;
    @SpringBean
    private BaseService baseService;

    public EnterBasePanel() {
        add(new NotLoggedIn("noLoggedIn") {
            @Override
            public boolean isVisible() {
                return !userService.isLoggedin();
            }
        });

        add(new BaseRunning("baseRunning") {
            @Override
            public boolean isVisible() {
                return baseService.getBaseForLoggedInUser() != null;
            }
        });

        add(new StartBasePanel("startBasePanel") {
            @Override
            public boolean isVisible() {
                return baseService.getBaseForLoggedInUser() == null;
            }
        });
    }
}
