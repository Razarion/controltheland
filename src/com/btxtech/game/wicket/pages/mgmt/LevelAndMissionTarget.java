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

import com.btxtech.game.services.utg.DbLevel;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.wicket.uiservices.ListProvider;
import java.util.List;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 12.05.2010
 * Time: 20:26:33
 */
public class LevelAndMissionTarget extends WebPage {
    @SpringBean
    private UserGuidanceService userGuidanceService;

    public LevelAndMissionTarget() {
        setupLevelTable();
    }

    private void setupLevelTable() {
        final ListProvider<DbLevel> levelProvider = new ListProvider<DbLevel>() {
            @Override
            protected List<DbLevel> createList() {
                return userGuidanceService.getDbLevels();
            }
        };
        add(new FeedbackPanel("msgs"));
        Form form = new Form("levelForm");
        add(form);

        form.add(new DataView<DbLevel>("levels", levelProvider) {

            @Override
            protected void populateItem(final Item<DbLevel> dbLevelItem) {
                dbLevelItem.add(new TextField<String>("name"));
                dbLevelItem.add(new Button("up") {

                    @Override
                    public void onSubmit() {
                        userGuidanceService.moveUpDbLevel(dbLevelItem.getModelObject());
                    }
                });
                dbLevelItem.add(new Button("down") {

                    @Override
                    public void onSubmit() {
                        userGuidanceService.moveDownDbLevel(dbLevelItem.getModelObject());
                    }
                });
                dbLevelItem.add(new Link("missionTargetLink") {

                    @Override
                    public void onClick() {
                        setResponsePage(new MissionTarget(dbLevelItem.getModelObject()));
                    }
                });
                dbLevelItem.add(new Button("delete") {

                    @Override
                    public void onSubmit() {
                        userGuidanceService.deleteDbLevel(dbLevelItem.getModelObject());
                    }
                });

            }
        });
        form.add(new Button("add") {

            @Override
            public void onSubmit() {
                userGuidanceService.addDbLevel();
            }
        });
        form.add(new Button("save") {

            @Override
            public void onSubmit() {
                userGuidanceService.saveDbLevels(levelProvider.getLastModifiedList());
            }
        });

    }

}
