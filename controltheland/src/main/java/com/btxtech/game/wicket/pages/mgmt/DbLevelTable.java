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
import com.btxtech.game.services.utg.DbAbstractLevel;
import com.btxtech.game.services.utg.DbRealGameLevel;
import com.btxtech.game.services.utg.DbResurrection;
import com.btxtech.game.services.utg.DbSimulationLevel;
import com.btxtech.game.services.utg.LevelActivationException;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.wicket.uiservices.BaseItemTypePanel;
import com.btxtech.game.wicket.uiservices.CrudRootTableHelper;
import com.btxtech.game.wicket.uiservices.TerritoryPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 12.05.2010
 * Time: 20:26:33
 */
public class DbLevelTable extends MgmtWebPage {
    @SpringBean
    private UserGuidanceService userGuidanceService;
    private Integer copyFrom;

    public DbLevelTable() {
        add(new FeedbackPanel("msgs"));
        Form form = new Form("levelForm");
        add(form);

        final RadioGroup<Integer> copyRadioGroup = new RadioGroup<Integer>("copyRadioChoiceGroup", new IModel<Integer>() {
            @Override
            public Integer getObject() {
                return copyFrom;
            }

            @Override
            public void setObject(Integer object) {
                copyFrom = object;
            }

            @Override
            public void detach() {
                copyFrom = null;
            }
        });
        form.add(copyRadioGroup);
        final CrudRootTableHelper<DbAbstractLevel> tableHelper = new CrudRootTableHelper<DbAbstractLevel>("levels", "save", null, true, copyRadioGroup, true) {

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
                dbLevelItem.add(new TextField("internalDescription"));
                dbLevelItem.add(new Radio<Integer>("copyRadioChoice", new Model<Integer>(dbLevelItem.getModelObject().getId()), copyRadioGroup)
                        .setEnabled(dbLevelItem.getModelObject() instanceof DbRealGameLevel));
            }
        };
        copyRadioGroup.add(new Button("copy") {

            @Override
            public void onSubmit() {
                if (copyFrom != null) {
                    userGuidanceService.copyDbAbstractLevel(copyFrom);
                    tableHelper.refresh();
                } else {
                    error("Copy from not selected");
                }
                copyFrom = null;
            }
        });

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

       new CrudRootTableHelper<DbResurrection>("resurrections", "saveResurrection", "createResurrection", false, form, false) {

            @Override
            protected CrudRootServiceHelper<DbResurrection> getCrudRootServiceHelperImpl() {
                return userGuidanceService.getCrudRootDbResurrection();
            }

            @Override
            protected void extendedPopulateItem(final Item<DbResurrection> item) {
                super.extendedPopulateItem(item);
                item.add(new Label("id"));
                item.add(new BaseItemTypePanel("startItemType"));
                item.add(new TerritoryPanel("dbTerritory"));
                item.add(new TextField("startItemFreeRange"));
                item.add(new TextField("money"));
            }
        };


    }

}
