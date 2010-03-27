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

package com.btxtech.game.wicket.pages.user;

import com.btxtech.game.services.history.HistoryService;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.wicket.pages.basepage.BasePage;
import com.btxtech.game.wicket.uiservices.GameControlService;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 18.01.2009
 * Time: 21:48:26
 */
public class UserPage extends BasePage {
    public static final String KEY_VIEW_USER_NAME = "viewusername";
    @SpringBean
    private HistoryService historyService;
    @SpringBean
    private UserService userService;
    @SpringBean
    private GameControlService gameControlService;
    private User viewUser;
    private boolean canEditSite;

    public UserPage(PageParameters pageParameters) {
        super(pageParameters);
        updateState();

        Form form = new Form("enterForm") {
            @Override
            protected void onSubmit() {
                setResponsePage(gameControlService.getEnterGamePage(canEditSite));
            }

            @Override
            public boolean isVisible() {
                updateState();
                return canEditSite;
            }
        };
        add(form);

        add(new UserInfo("userInfo", viewUser));
        add(new UserBaseInfo("userBaseInfo", viewUser));
    }

    private void updateState() {
        canEditSite = false;
        PageParameters pageParameters = getPageParameters();
        String viewUserName = pageParameters.getString(KEY_VIEW_USER_NAME);
        if (viewUserName != null) {
            viewUser = userService.getUser(viewUserName);
        }

        User loggedinUser = userService.getLoggedinUser();
        if (loggedinUser != null) {
            if (viewUser == null) {
                // Clicks on mySite from menu
                canEditSite = true;
                viewUser = loggedinUser;
            } else if (viewUser.equals(loggedinUser)) {
                // Clicks his site on the user list
                canEditSite = true;
            }
        }
        if (viewUser == null) {
            throw new IllegalArgumentException("No such viewUser: " + viewUserName);
        }
    }

    @Override
    public String getAdditionalPageInfo() {
        if (viewUser != null) {
            return viewUser.getName();
        } else {
            return "???";
        }
    }

}
