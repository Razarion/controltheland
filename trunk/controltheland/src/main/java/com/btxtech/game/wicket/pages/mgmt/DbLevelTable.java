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

import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.utg.*;
import com.btxtech.game.wicket.uiservices.CrudRootTableHelper;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 12.05.2010
 * Time: 20:26:33
 */
public class DbLevelTable extends MgmtWebPage {
    @SpringBean
    private UserGuidanceService userGuidanceService;

    public DbLevelTable() {
        add(new FeedbackPanel("msgs"));
        Form form = new Form("levelForm");
        add(form);

        new CrudRootTableHelper<DbAbstractLevel>("levels", "save", null, true, form, true) {

            @Override
            protected CrudRootServiceHelper<DbAbstractLevel> getCrudRootServiceHelperImpl() {
                return userGuidanceService.getDbLevelCrudServiceHelper();
            }

            @Override
            protected void onEditSubmit(DbAbstractLevel dbAbstractLevel) {
                setResponsePage(new DbLevelEditor(dbAbstractLevel));
            }

            @Override
            protected void setupCreate(WebMarkupContainer markupContainer, String createId) {
                markupContainer.add(new Button("createRealGame") {

                    @Override
                    public void onSubmit() {
                        createDbChild(DbRealGameLevel.class);
                        refresh();
                    }
                });
                markupContainer.add(new Button("createSimulation") {

                    @Override
                    public void onSubmit() {
                        createDbChild(DbSimulationLevel.class);
                        refresh();
                    }
                });
            }

            @Override
            protected void extendedPopulateItem(final Item<DbAbstractLevel> dbLevelItem) {
                super.extendedPopulateItem(dbLevelItem);
                dbLevelItem.add(new Label("id"));
                dbLevelItem.add(new Label("displayType"));
            }
        };
        form.add(new Button("activate") {

            @Override
            public void onSubmit() {
                try {
                    userGuidanceService.activateLevels();
                } catch (LevelActivationException e) {
                    error(e.getMessage());
                }
            }
        });

    }

}
