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

import com.btxtech.game.services.common.CrudServiceHelper;
import com.btxtech.game.services.tutorial.TutorialService;
import com.btxtech.game.services.utg.DbAbstractLevel;
import com.btxtech.game.services.utg.DbRealGameLevel;
import com.btxtech.game.services.utg.DbSimulationLevel;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.wicket.uiservices.CrudTableHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
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
public class DbLevelTable extends WebPage {
    @SpringBean
    private UserGuidanceService userGuidanceService;

    public DbLevelTable() {
        add(new FeedbackPanel("msgs"));
        Form form = new Form("levelForm");
        add(form);

        new CrudTableHelper<DbAbstractLevel>("levels", "save", null, true, form) {

            @Override
            protected CrudServiceHelper<DbAbstractLevel> getCrudServiceHelper() {
                return userGuidanceService.getDbLevelCrudServiceHelper();
            }

            @Override
            protected void setupSave(WebMarkupContainer markupContainer, String saveId) {
                markupContainer.add(new Button(saveId) {

                    @Override
                    public void onSubmit() {
                        userGuidanceService.saveDbLevels(getLastModifiedList());
                    }
                });
            }

            @Override
            protected void deleteChild(DbAbstractLevel child) {
                userGuidanceService.deleteDbLevel(child);
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
                        getCrudServiceHelper().createDbChild(DbRealGameLevel.class);
                    }
                });
                markupContainer.add(new Button("createSimulation") {

                    @Override
                    public void onSubmit() {
                        getCrudServiceHelper().createDbChild(DbSimulationLevel.class);
                    }
                });
            }

            @Override
            protected void extendedPopulateItem(final Item<DbAbstractLevel> dbLevelItem) {
                super.extendedPopulateItem(dbLevelItem);
                dbLevelItem.add(new Label("displayType"));
                dbLevelItem.add(new Button("up") {

                    @Override
                    public void onSubmit() {
                        // TODO  userGuidanceService.moveUpDbLevel(dbLevelItem.getModelObject());
                    }
                });
                dbLevelItem.add(new Button("down") {

                    @Override
                    public void onSubmit() {
                        // TODO  userGuidanceService.moveDownDbLevel(dbLevelItem.getModelObject());
                    }
                });
            }
        };
        form.add(new Button("activate") {

            @Override
            public void onSubmit() {
                userGuidanceService.activateLevels();
            }
        });

    }

}
