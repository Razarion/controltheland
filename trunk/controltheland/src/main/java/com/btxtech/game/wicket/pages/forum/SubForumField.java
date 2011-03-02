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
import com.btxtech.game.services.user.SecurityRoles;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.wicket.WebCommon;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 22.03.2010
 * Time: 09:03:40
 */
public class SubForumField extends Panel {
    @SpringBean
    private UserService userService;
    @SpringBean
    private ForumService forumService;

    public SubForumField(String id, final SubForum subForum) {
        super(id);
        add(new Label("title", subForum.getTitle()));
        add(new Label("content", subForum.getContent()).setEscapeModelStrings(false));
        Form form = new Form("categoryForm");
        form.add(new Button("add") {
            @Override
            public void onSubmit() {
                setResponsePage(new AddEntryForm(subForum, Category.class, false));
            }
        });
        form.add(new Button("delete") {
            @Override
            public void onSubmit() {
                forumService.delete(subForum);
            }
        });

        form.setVisible(WebCommon.isAuthorized(SecurityRoles.ROLE_FORUM_ADMINISTRATOR));
        add(form);
    }
}
