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

package com.btxtech.game.wicket.pages.mgmt.usermgmt;

import com.btxtech.game.services.base.Base;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.connection.ConnectionService;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import com.btxtech.game.wicket.uiservices.DetachHashListProvider;
import com.btxtech.game.wicket.uiservices.LevelReadonlyPanel;
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
    @SpringBean
    private BaseService baseService;
    @SpringBean
    private ConnectionService connectionService;

    public UserStateTable() {
        add(new FeedbackPanel("msgs"));

        final DetachHashListProvider<UserState> userStateProvider = new DetachHashListProvider<UserState>() {
            @Override
            protected List<UserState> createList() {
                return userService.getAllUserStates();
            }

            @Override
            protected UserState getObject4Hash(int hash) {
                return userService.getUserState4Hash(hash);
            }
        };

        Form form = new Form("form");
        add(form);
        form.add(new DataView<UserState>("userState", userStateProvider) {
            @Override
            protected void populateItem(final Item<UserState> item) {
                item.add(new LevelReadonlyPanel("dbLevelId"));
                item.add(new Label("sessionId"));
                item.add(new Label("online"));
                item.add(new Label("user"));
                Base base = baseService.getBase(item.getModelObject());
                if (base != null && connectionService.hasConnection(base.getSimpleBase())) {
                    item.add(new Label("inGame", "yes"));
                } else {
                    item.add(new Label("inGame", ""));
                }
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