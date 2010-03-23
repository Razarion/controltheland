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
import com.btxtech.game.services.forum.ForumThread;
import com.btxtech.game.services.forum.Post;
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
 * Time: 22:42:26
 */
public class ForumThreadView extends BasePage {
    @SpringBean
    private ForumService forumService;
    @SpringBean
    private UserService userService;

    public ForumThreadView(final ForumThread forumThread) {
        ListView<Post> listView = new ListView<Post>("posts", new IModel<List<Post>>() {
            private List<Post> posts;

            @Override
            public List<Post> getObject() {
                if (posts == null) {
                    posts = forumService.getPosts(forumThread);
                }
                return posts;
            }

            @Override
            public void setObject(List<Post> object) {
                // Ignored
            }

            @Override
            public void detach() {
                posts = null;
            }
        }) {
            @Override
            protected void populateItem(final ListItem<Post> listItem) {
                listItem.add(new PostField("post", listItem.getModelObject()));
            }
        };
        add(listView);
        Form addPostForm = new Form("addPost") {

            @Override
            protected void onSubmit() {
                setResponsePage(new AddEntryForm(forumThread, Post.class));
            }
        };
        addPostForm.setVisible(userService.isAuthorized(ArqEnum.FORUM_POST));
        add(addPostForm);

    }
}