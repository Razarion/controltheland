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

import com.btxtech.game.services.common.CrudChildServiceHelper;
import com.btxtech.game.services.common.RuServiceHelper;
import com.btxtech.game.services.terrain.TerrainImageService;
import com.btxtech.game.services.tutorial.DbTaskConfig;
import com.btxtech.game.services.tutorial.DbTutorialConfig;
import com.btxtech.game.services.tutorial.TutorialService;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import com.btxtech.game.wicket.uiservices.CrudChildTableHelper;
import com.btxtech.game.wicket.uiservices.TerrainLinkHelper;
import com.btxtech.game.wicket.uiservices.RuModel;
import com.btxtech.game.wicket.uiservices.TerrainPanel;
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
public class TutorialEditor extends MgmtWebPage {
    @SpringBean
    private TutorialService tutorialService;
    @SpringBean
    private TerrainImageService terrainService;
    @SpringBean
    private RuServiceHelper<DbTutorialConfig> ruServiceHelper;

    public TutorialEditor(DbTutorialConfig dbTutorialConfig) {
        add(new FeedbackPanel("msgs"));

        final Form<DbTutorialConfig> form = new Form<>("tutorialForm", new CompoundPropertyModel<DbTutorialConfig>(new RuModel<DbTutorialConfig>(dbTutorialConfig, DbTutorialConfig.class) {
            @Override
            protected RuServiceHelper<DbTutorialConfig> getRuServiceHelper() {
                return ruServiceHelper;
            }
        }));
        add(form);
        form.add(new TextField<String>("ownBaseName"));
        form.add(new CheckBox("tracking"));
        form.add(new CheckBox("showTip"));
        form.add(new CheckBox("sellAllowed"));
        form.add(new CheckBox("disableScroll"));
        form.add(new TerrainPanel("dbTerrainSetting", new TerrainLinkHelper(dbTutorialConfig)));

        new CrudChildTableHelper<DbTutorialConfig, DbTaskConfig>("taskTable", null, "createTask", true, form, true) {
            @Override
            protected void extendedPopulateItem(Item<DbTaskConfig> item) {
                displayId(item);
                super.extendedPopulateItem(item);
            }

            @Override
            protected void onEditSubmit(DbTaskConfig dbTaskConfig) {
                setResponsePage(new TaskEditor(dbTaskConfig, new TerrainLinkHelper(form.getModelObject())));
            }

            @Override
            protected RuServiceHelper<DbTutorialConfig> getRuServiceHelper() {
                return ruServiceHelper;
            }

            @Override
            protected DbTutorialConfig getParent() {
                return (DbTutorialConfig) form.getDefaultModelObject();
            }

            @Override
            protected CrudChildServiceHelper<DbTaskConfig> getCrudChildServiceHelperImpl() {
                return ((DbTutorialConfig) form.getDefaultModelObject()).getDbTaskConfigCrudChildServiceHelper();
            }
        };

        form.add(new Button("save") {

            @Override
            public void onSubmit() {
                ruServiceHelper.updateDbEntity((DbTutorialConfig) form.getDefaultModelObject());
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