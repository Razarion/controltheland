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

import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.wicket.pages.BorderPanel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: Oct 17, 2009
 * Time: 7:02:33 PM
 */
public class UserList extends BorderPanel {
    @SpringBean
    private UserService userService;

    public UserList(String id) {
        super(id);
        ListView userList = new ListView<User>("userTable", userService.getAllUsers()) {
            @Override
            protected void populateItem(final ListItem<User> listItem) {
                BookmarkablePageLink<UserPage> link = new BookmarkablePageLink<UserPage>("userLink", UserPage.class);
                link.setParameter(UserPage.KEY_VIEW_USER_NAME, listItem.getModelObject().getName());
                listItem.add(link);
                link.add(new Label("userName", listItem.getModelObject().getName()));
            }
        };
        add(userList);

    }
}
