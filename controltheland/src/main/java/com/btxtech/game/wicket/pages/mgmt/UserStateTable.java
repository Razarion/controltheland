/*
 * Copyright (c) 2011.
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

import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.wicket.uiservices.ListProvider;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * User: beat
 * Date: 16.02.2010
 * Time: 21:35:44
 */
public class UserStateTable extends MgmtWebPage {
    @SpringBean
    private UserService userService;

    public UserStateTable() {
        add(new FeedbackPanel("msgs"));

        final ListProvider<UserState> userStateProvider = new ListProvider<UserState>() {
            @Override
            protected List<UserState> createList() {
                return userService.getAllUserStates();
            }
        };

        Form form = new Form("form");
        add(form);
        form.add(new DataView<UserState>("userState", userStateProvider) {
            @Override
            protected void populateItem(final Item<UserState> item) {
                item.add(new Label("currentAbstractLevel.name"));
                item.add(new Label("sessionId"));
                item.add(new Label("online"));
                item.add(new Label("user.name"));
                item.add(new Button("edit") {

                    @Override
                    public void onSubmit() {
                        setResponsePage(new UserStateEditor(item.getModelObject()));
                    }
                });
            }
        });
    }
}