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

package com.btxtech.game.wicket.pages.user;

import com.btxtech.game.services.user.UserService;
import com.btxtech.game.wicket.pages.home.Home;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 18.01.2009
 * Time: 12:37:58
 */
public class LoggedinBox extends Panel {
    @SpringBean
    private UserService userService;

    public LoggedinBox() {
        super("signinBox");
        Form form = new Form<LoggedinBox>("loginForm", new Model<LoggedinBox>()) {
            @Override
            protected void onSubmit() {
                userService.logout();
                setResponsePage(Home.class);
            }
        };
        add(form);
        Link link = new Link("nameLink") {

            @Override
            public void onClick() {
                setResponsePage(UserPage.class);
            }
        };
        link.add(new Label("name", userService.getLoggedinUser().getName()));
        form.add(link);

    }

}