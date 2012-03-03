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
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.XpService;
import com.btxtech.game.wicket.uiservices.LevelReadonlyPanel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

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
    @SpringBean
    private XpService xpService;
    private Integer dbLevelId;
    private Integer xp;

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
        form.add(new LevelReadonlyPanel("dbLevelId"));
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
        form.add(new Label("xp"));
        form.add(new TextField<Integer>("addXp", new IModel<Integer>() {
            @Override
            public Integer getObject() {
                return null;
            }

            @Override
            public void setObject(Integer value) {
                xp = value;
            }

            @Override
            public void detach() {
                xp = null;
            }
        }, Integer.class));

        form.add(new Button("activateLevel") {

            @Override
            public void onSubmit() {
                if (dbLevelId != null) {
                    userGuidanceService.promote((UserState) form.getDefaultModelObject(), dbLevelId);
                }
            }
        });
        form.add(new Button("activateXp") {

            @Override
            public void onSubmit() {
                if (xp != null) {
                    xpService.onReward((UserState) form.getDefaultModelObject(), xp);
                }
            }
        });
        form.add(new Button("back") {

            @Override
            public void onSubmit() {
                setResponsePage(UserStateTable.class);
            }
        });


    }
}