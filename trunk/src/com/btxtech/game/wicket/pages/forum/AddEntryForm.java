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
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.PageParameters;
import wicket.contrib.tinymce.TinyMceBehavior;
import wicket.contrib.tinymce.settings.TinyMCESettings;

/**
 * User: beat
 * Date: 22.03.2010
 * Time: 11:46:50
 */
public class AddEntryForm extends BasePage {
    @SpringBean
    private ForumService forumService;
    private Model<String> title = new Model<String>();
    private Model<String> content = new Model<String>();

    public AddEntryForm(final AbstractForumEntry parent, Class<? extends AbstractForumEntry> aClass, boolean tinyMceEditor) {
        final AbstractForumEntry abstractForumEntry = forumService.createForumEntry(aClass);
        Form form = new Form<AbstractForumEntry>("forum");
        form.add(new TextField<String>("title", title));
        TextArea<String> contentArea = new TextArea<String>("content", content);
        if (tinyMceEditor) {
            TinyMCESettings tinyMCESettings = new TinyMCESettings();
            tinyMCESettings.add(wicket.contrib.tinymce.settings.Button.link, TinyMCESettings.Toolbar.first, TinyMCESettings.Position.after); // TODO does not work
            tinyMCESettings.add(wicket.contrib.tinymce.settings.Button.unlink, TinyMCESettings.Toolbar.first, TinyMCESettings.Position.after); // TODO does not work
            contentArea.add(new TinyMceBehavior(tinyMCESettings));
        }
        form.add(contentArea);
        form.add(new Button("post") {
            @Override
            public void onSubmit() {
                abstractForumEntry.setTitle(title.getObject());
                abstractForumEntry.setContent(content.getObject());
                int parentid = parent != null ? parent.getId() : -1;
                forumService.insertForumEntry(parentid, abstractForumEntry);
                if (abstractForumEntry instanceof Category || abstractForumEntry instanceof SubForum) {
                    setResponsePage(ForumView.class);
                    return;
                } else if (abstractForumEntry instanceof ForumThread) {
                    PageParameters pageParameters = new PageParameters();
                    pageParameters.add(CategoryView.ID, Integer.toString(abstractForumEntry.getId()));
                    setResponsePage(ForumThreadView.class, pageParameters);
                    return;
                } else if (abstractForumEntry instanceof Post) {
                    PageParameters pageParameters = new PageParameters();
                    pageParameters.add(CategoryView.ID, Integer.toString(parentid));
                    setResponsePage(ForumThreadView.class, pageParameters);
                    return;
                }
                throw new IllegalArgumentException("Unknown: " + abstractForumEntry);
            }
        });
        add(form);
    }
}
