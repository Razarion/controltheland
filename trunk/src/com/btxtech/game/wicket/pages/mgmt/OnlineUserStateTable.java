/*
 * Copyright (c) 2011.
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

package com.btxtech.game.wicket.pages.mgmt;

import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.wicket.uiservices.ListProvider;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 16.02.2010
 * Time: 21:35:44
 */
public class OnlineUserStateTable extends WebPage {
    @SpringBean
    private UserService userService;
    private Log log = LogFactory.getLog(OnlineUserStateTable.class);

    public OnlineUserStateTable() {
        add(new FeedbackPanel("msgs"));

        final ListProvider<UserState> userStateProvider = new ListProvider<UserState>() {
            @Override
            protected List<UserState> createList() {
                return userService.getAllUserStates();
            }
        };

        add(new DataView<UserState>("userState", userStateProvider) {
            @Override
            protected void populateItem(final Item<UserState> item) {
                item.add(new Label("userLevelStatus.currentAbstractLevel.name"));
                item.add(new Label("sessionId"));
                BookmarkablePageLink link = new BookmarkablePageLink<OnlineUserState>("userStateLink", OnlineUserState.class);
                link.setParameter(OnlineUserState.SESSION_ID_KEY, item.getModelObject().getSessionId());
                item.add(link);
            }
        });
    }
}