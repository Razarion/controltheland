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

package com.btxtech.game.wicket.pages.mgmt;

import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.wicket.uiservices.ListProvider;
import java.util.List;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 16.02.2010
 * Time: 21:35:44
 */
public class UserTable extends WebPage {
    @SpringBean
    private UserService userService;

    public UserTable() {
        Form form = new Form("userForm");
        add(form);

        final ListProvider<User> textProvider = new ListProvider<User>() {
            @Override
            protected List<User> createList() {
                return userService.getAllUsers();
            }
        };
        form.add(new DataView<User>("userTable", textProvider) {
            @Override
            protected void populateItem(final Item<User> item) {
                item.add(new Label("name"));
                item.add(new Button("edit") {

                    @Override
                    public void onSubmit() {
                        setResponsePage(new UserEditor(item.getModelObject().getUsername()));
                    }
                });
            }
        });
    }
}