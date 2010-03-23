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

import com.btxtech.game.services.forum.ForumThread;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * User: beat
 * Date: 22.03.2010
 * Time: 09:03:40
 */
public class ForumThreadField extends Panel {
    public ForumThreadField(String id, final ForumThread forumThread) {
        super(id);
        Link link = new Link("link") {

            @Override
            public void onClick() {
                setResponsePage(new ForumThreadView(forumThread));
            }
        };
        link.add(new Label("text", forumThread.getTitle()));
        add(link);
    }
}