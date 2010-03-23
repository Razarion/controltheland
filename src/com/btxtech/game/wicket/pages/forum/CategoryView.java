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
import com.btxtech.game.services.forum.ForumThread;
import com.btxtech.game.services.user.ArqEnum;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.wicket.WebCommon;
import com.btxtech.game.wicket.pages.basepage.BasePage;
import java.text.SimpleDateFormat;
import java.util.List;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 21.03.2010
 * Time: 22:42:26
 */
public class CategoryView extends BasePage {
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(WebCommon.DATE_TIME_FORMAT_STRING);
    @SpringBean
    private ForumService forumService;
    @SpringBean
    private UserService userService;

    public CategoryView(final Category category) {
        ListView<ForumThread> listView = new ListView<ForumThread>("threads", new IModel<List<ForumThread>>() {
            private List<ForumThread> forumThreads;

            @Override
            public List<ForumThread> getObject() {
                if (forumThreads == null) {
                    forumThreads = forumService.getForumThreads(category);
                }
                return forumThreads;
            }

            @Override
            public void setObject(List<ForumThread> object) {
                // Ignored
            }

            @Override
            public void detach() {
                forumThreads = null;
            }
        }) {
            @Override
            protected void populateItem(final ListItem<ForumThread> listItem) {
                listItem.add(new ForumThreadField("thread", listItem.getModelObject()));
                listItem.add(new Label("lastPost", simpleDateFormat.format(listItem.getModelObject().getDate().getTime())));
            }
        };
        add(listView);

        Form addForumThread = new Form("addForumThread") {

            @Override
            protected void onSubmit() {
                setResponsePage(new AddEntryForm(category, ForumThread.class));
            }
        };
        addForumThread.setVisible(userService.isAuthorized(ArqEnum.FORUM_POST));
        add(addForumThread);
    }
}
