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

package com.btxtech.game.wicket.pages.mgmt.tutorial;

import com.btxtech.game.services.common.CrudServiceHelper;
import com.btxtech.game.services.tutorial.DbTaskConfig;
import com.btxtech.game.services.tutorial.DbTutorialConfig;
import com.btxtech.game.services.tutorial.TutorialService;
import com.btxtech.game.wicket.uiservices.CrudTableHelper;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 23.07.2010
 * Time: 23:29:54
 */
public class TutorialEditor extends WebPage {
    @SpringBean
    private TutorialService tutorialService;

    public TutorialEditor(final DbTutorialConfig dbTutorialConfig) {
        add(new FeedbackPanel("msgs"));

        Form<DbTutorialConfig> form = new Form<DbTutorialConfig>("tutorialForm", new CompoundPropertyModel<DbTutorialConfig>(dbTutorialConfig));
        add(form);

        form.add(new TextField<Integer>("ownBaseId"));
        form.add(new TextField<String>("ownBaseName"));
        form.add(new TextField<String>("ownBaseColor"));
        form.add(new TextField<Integer>("enemyBaseId"));
        form.add(new TextField<String>("enemyBaseName"));
        form.add(new TextField<String>("enemyBaseColor"));
        form.add(new TextField<String>("width"));
        form.add(new TextField<String>("height"));
        form.add(new CheckBox("failOnOwnItemsLost"));
        form.add(new TextField<Integer>("failOnMoneyBelowAndNoAttackUnits"));

        new CrudTableHelper<DbTaskConfig>("taskTable", null, "createTask", true, form) {

            @Override
            protected CrudServiceHelper<DbTaskConfig> getCrudServiceHelper() {
                return dbTutorialConfig.getCrudServiceHelper();
            }

            @Override
            protected void extendedPopulateItem(final Item<DbTaskConfig> dbTaskConfigItem) {
                super.extendedPopulateItem(dbTaskConfigItem);
                dbTaskConfigItem.add(new Button("up") {
                    @Override
                    public void onSubmit() {
                        dbTutorialConfig.moveTaskUp(dbTaskConfigItem.getModelObject());
                    }
                });
                dbTaskConfigItem.add(new Button("down") {
                    @Override
                    public void onSubmit() {
                        dbTutorialConfig.moveTaskDown(dbTaskConfigItem.getModelObject());
                    }
                });
            }

            @Override
            protected void onEditSubmit(DbTaskConfig dbTaskConfig) {
                setResponsePage(new TaskEditor(dbTutorialConfig, dbTaskConfig));
            }
        };

        form.add(new Button("save") {

            @Override
            public void onSubmit() {
                tutorialService.getDbTutorialCrudServiceHelper().updateDbChild(dbTutorialConfig);
            }
        });
        form.add(new Button("back") {

            @Override
            public void onSubmit() {
                setResponsePage(TutorialTable.class);
            }
        });

    }

}