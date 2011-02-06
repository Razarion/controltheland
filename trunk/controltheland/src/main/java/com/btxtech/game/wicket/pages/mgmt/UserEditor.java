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

import com.btxtech.game.services.market.MarketEntry;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserService;
import java.util.ArrayList;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 09.08.2010
 * Time: 23:04:20
 */
public class UserEditor extends WebPage {
    @SpringBean
    private UserService userService;

    public UserEditor(String name) {
        add(new FeedbackPanel("msgs"));

        Form<User> form = new Form<User>("form", new CompoundPropertyModel<User>(userService.getUser(name))) {
            @Override
            protected void onSubmit() {
                userService.save(getModelObject());
            }
        };
        form.add(new Label("name"));
        form.add(new TextField<Integer>("userItemTypeAccess.xp"));
        /* TODO form.add(new ListView<MarketEntry>("allowedItemTypes", new ArrayList<MarketEntry>(userService.getUser(name).getUserItemTypeAccess().getAllowedItemTypes())) {

            @Override
            protected void populateItem(ListItem<MarketEntry> marketEntryListItem) {
                marketEntryListItem.add(new Label("itemType", marketEntryListItem.getModelObject().getItemType().getName()));
            }
        });
        add(form);*/

    }
}
