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

package com.btxtech.game.wicket.pages.forum;

import com.btxtech.game.services.forum.ForumService;
import com.btxtech.game.services.forum.SubForum;
import com.btxtech.game.services.user.ArqEnum;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.wicket.pages.basepage.BasePage;
import java.util.List;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 21.03.2010
 * Time: 19:34:04
 */
public class ForumView extends BasePage {
    @SpringBean
    private ForumService forumService;
    @SpringBean
    private UserService userService;

    public ForumView() {
        ListView<SubForum> listView = new ListView<SubForum>("subForums", new IModel<List<SubForum>>() {
            private List<SubForum> fromEntries;

            @Override
            public List<SubForum> getObject() {
                if (fromEntries == null) {
                    fromEntries = forumService.getSubForums();
                }
                return fromEntries;
            }

            @Override
            public void setObject(List<SubForum> object) {
                // Ignored
            }

            @Override
            public void detach() {
                fromEntries = null;
            }
        }) {
            @Override
            protected void populateItem(final ListItem<SubForum> listItem) {
                SubForum subForum = listItem.getModelObject();
                listItem.add(new SubForumView("subForum", subForum));
            }
        };
        add(listView);
        Form addSubForum = new Form("addSubForumForm") {

            @Override
            protected void onSubmit() {
                setResponsePage(new AddEntryForm(null, SubForum.class));
            }
        };
        addSubForum.setVisible(userService.isAuthorized(ArqEnum.FORUM_ADMIN));
        add(addSubForum);
    }
}
