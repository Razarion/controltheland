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
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.PageParameters;

/**
 * User: beat
 * Date: 22.03.2010
 * Time: 09:03:40
 */
public class CategoryField extends Panel {
    public CategoryField(String id, final Category category) {
        super(id);
        Link link = new Link("link") {

            @Override
            public void onClick() {
                PageParameters pageParameters = new PageParameters();
                pageParameters.add(CategoryView.ID, Integer.toString(category.getId()));
                setResponsePage(CategoryView.class, pageParameters);
            }
        };
        link.add(new Label("text", category.getTitle()));
        add(link);
    }
}