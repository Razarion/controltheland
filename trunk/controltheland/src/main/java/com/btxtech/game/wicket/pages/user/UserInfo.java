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

import com.btxtech.game.wicket.pages.BorderPanel;
import com.btxtech.game.wicket.WebCommon;
import com.btxtech.game.services.user.User;
import java.text.SimpleDateFormat;
import org.apache.wicket.markup.html.basic.Label;

/**
 * User: beat
 * Date: Oct 17, 2009
 * Time: 2:12:09 PM
 */
public class UserInfo extends BorderPanel {
    public UserInfo(String id, User user) {
        super(id);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(WebCommon.DATE_FORMAT_STRING);
        add(new Label("titleUserName", user.getUsername()));
        add(new Label("registered", simpleDateFormat.format(user.getRegisterDate())));
        if(user.getLastLoginDate() != null) {
            add(new Label("lastLogin", simpleDateFormat.format(user.getLastLoginDate())));
        } else {
            add(new Label("lastLogin", ""));
        }
    }
}
