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

import com.btxtech.game.services.market.MarketEntry;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.UserGuidanceService;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: beat
 * Date: 26.01.2011
 * Time: 16:30:59
 */
public class UserStateEditor extends MgmtWebPage {
    @SpringBean
    private UserService userService;
    @SpringBean
    private UserGuidanceService userGuidanceService;
    private Integer dbLevelId;

    public UserStateEditor(UserState userState) {
        final int userStateHash = userState.hashCode();
        add(new FeedbackPanel("msgs"));

        final Form<UserState> form = new Form<UserState>("form", new CompoundPropertyModel<UserState>(new IModel<UserState>() {
            private UserState userState;

            @Override
            public UserState getObject() {
                if (userState == null) {
                    userState = userService.getUserState4Hash(userStateHash);
                }
                return userState;
            }

            @Override
            public void setObject(UserState object) {
                // Ignore
            }

            @Override
            public void detach() {
                userState = null;
            }
        }));
        add(form);
        form.add(new Label("currentAbstractLevel.name"));
        form.add(new Label("sessionId"));
        form.add(new TextField<Integer>("newDbLevelId", new IModel<Integer>() {
            @Override
            public Integer getObject() {
                return null;
            }

            @Override
            public void setObject(Integer integer) {
                dbLevelId = integer;
            }

            @Override
            public void detach() {
                dbLevelId = null;
            }
        }, Integer.class));
        form.add(new TextField<Integer>("userItemTypeAccess.xp"));
        List<MarketEntry> marketEntries = Collections.emptyList();
        if (form.getModelObject().getUserItemTypeAccess() != null) {
            marketEntries = new ArrayList<MarketEntry>(form.getModelObject().getUserItemTypeAccess().getAllowedItemTypes());
        }
        form.add(new ListView<MarketEntry>("allowedItemTypes", marketEntries) {

            @Override
            protected void populateItem(ListItem<MarketEntry> marketEntryListItem) {
                marketEntryListItem.add(new Label("itemType", marketEntryListItem.getModelObject().getItemType().getName()));
            }
        });


        form.add(new Button("activate") {

            @Override
            public void onSubmit() {
                if (dbLevelId != null) {
                    userGuidanceService.promote((UserState) form.getDefaultModelObject(), dbLevelId);
                }
            }
        });
        form.add(new Button("cancel") {

            @Override
            public void onSubmit() {
                setResponsePage(UserStateTable.class);
            }
        });


    }
}
