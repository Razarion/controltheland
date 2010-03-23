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

import com.btxtech.game.services.forum.Category;
import com.btxtech.game.services.forum.ForumService;
import com.btxtech.game.services.forum.SubForum;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.wicket.WebCommon;
import java.text.SimpleDateFormat;
import java.util.List;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 23.03.2010
 * Time: 09:24:19
 */
public class SubForumView extends Panel {
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(WebCommon.DATE_TIME_FORMAT_STRING);
    @SpringBean
    private ForumService forumService;
    @SpringBean
    private UserService userService;

    public SubForumView(String id, final SubForum subForum) {
        super(id);

        ListView<Category> listView = new ListView<Category>("categories", new IModel<List<Category>>() {
            private List<Category> categories;

            @Override
            public List<Category> getObject() {
                if (categories == null) {
                    categories = forumService.getCategories(subForum);
                }
                return categories;
            }

            @Override
            public void setObject(List<Category> object) {
                // Ignored
            }

            @Override
            public void detach() {
                categories = null;
            }
        }) {
            @Override
            protected void populateItem(final ListItem<Category> listItem) {
                Category category = listItem.getModelObject();
                listItem.add(new CategoryField("forumSubForum", category));
                listItem.add(new Label("lastPost", simpleDateFormat.format(category.getDate().getTime())));
                listItem.add(new Label("threads", Integer.toString(category.getThreadCount())));
            }
        };
        add(listView);
    }
}
