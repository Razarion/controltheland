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
import com.btxtech.game.services.forum.Post;
import com.btxtech.game.services.user.ArqEnum;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.wicket.WebCommon;
import java.text.SimpleDateFormat;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 23.03.2010
 * Time: 13:15:19
 */
public class PostField extends Panel {
    @SpringBean
    private ForumService forumService;
    @SpringBean
    private UserService userService;

    public PostField(String id, final Post category) {
        super(id);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(WebCommon.DATE_TIME_FORMAT_STRING);
        add(new Label("date", simpleDateFormat.format(category.getDate())));
        add(new Label("user", category.getUser().getName()));
        add(new Label("title", category.getTitle()));
        add(new Label("content", category.getContent()).setEscapeModelStrings(false));
        Form form = new Form("deletePostForm") {

            @Override
            protected void onSubmit() {
                forumService.delete(category);
            }
        };
        form.setVisible(userService.isAuthorized(ArqEnum.FORUM_ADMIN));
        add(form);


    }
}
