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

import com.btxtech.game.services.forum.AbstractForumEntry;
import com.btxtech.game.services.forum.Category;
import com.btxtech.game.services.forum.ForumService;
import com.btxtech.game.services.forum.ForumThread;
import com.btxtech.game.services.forum.Post;
import com.btxtech.game.services.forum.SubForum;
import com.btxtech.game.wicket.pages.basepage.BasePage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 22.03.2010
 * Time: 11:46:50
 */
public class AddEntryForm extends BasePage {
    @SpringBean
    private ForumService forumService;

    public AddEntryForm(final AbstractForumEntry parent, Class<? extends AbstractForumEntry> aClass) {
        final AbstractForumEntry abstractForumEntry = forumService.createForumEntry(aClass);
        Form<AbstractForumEntry> form = new Form<AbstractForumEntry>("forum", new CompoundPropertyModel<AbstractForumEntry>(abstractForumEntry));
        form.add(new TextField<String>("title"));
        form.add(new TextField<String>("content"));
        form.add(new Button("post") {
            @Override
            public void onSubmit() {
                forumService.insertForumEntry(parent.getId(), abstractForumEntry);
                if (abstractForumEntry instanceof Category || abstractForumEntry instanceof SubForum) {
                    setResponsePage(new ForumView());
                    return;
                } else if (abstractForumEntry instanceof ForumThread) {
                    setResponsePage(new CategoryView((Category)parent));
                    return;
                } else if (abstractForumEntry instanceof Post) {
                    setResponsePage(new ForumThreadView((ForumThread) parent));
                    return;
                }
                throw new IllegalArgumentException("Unknwon: " + abstractForumEntry);
            }
        });
        add(form);
    }
}
