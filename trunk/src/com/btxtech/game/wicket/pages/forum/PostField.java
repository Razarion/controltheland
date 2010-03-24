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

import com.btxtech.game.services.forum.Post;
import com.btxtech.game.wicket.WebCommon;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import java.text.SimpleDateFormat;

/**
 * User: beat
 * Date: 23.03.2010
 * Time: 13:15:19
 */
public class PostField extends Panel {

    public PostField(String id, Post category) {
        super(id);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(WebCommon.DATE_TIME_FORMAT_STRING);
        add(new Label("date", simpleDateFormat.format(category.getDate())));
        add(new Label("user", category.getUser()));
        add(new Label("title", category.getTitle()));
        add(new Label("content", category.getContent()).setEscapeModelStrings(false));
    }
}
