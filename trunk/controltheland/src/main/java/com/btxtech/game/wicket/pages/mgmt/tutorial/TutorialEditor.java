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
import com.btxtech.game.services.terrain.DbTerrainSetting;
import com.btxtech.game.services.terrain.TerrainService;
import com.btxtech.game.services.tutorial.DbTaskConfig;
import com.btxtech.game.services.tutorial.DbTutorialConfig;
import com.btxtech.game.services.tutorial.TutorialService;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import com.btxtech.game.wicket.uiservices.CrudTableHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.hibernate.SessionFactory;

/**
 * User: beat
 * Date: 23.07.2010
 * Time: 23:29:54
 */
public class TutorialEditor extends MgmtWebPage {
    @SpringBean
    private TutorialService tutorialService;
    @SpringBean
    private TerrainService terrainService;
    @SpringBean
    private SessionFactory sessionFactory;
    private Log log = LogFactory.getLog(TutorialEditor.class);

    public TutorialEditor(DbTutorialConfig dbTutorialConfig) {
        final int dbTutorialConfigId = dbTutorialConfig.getId(); 
        add(new FeedbackPanel("msgs"));

        final Form<DbTutorialConfig> form = new Form<DbTutorialConfig>("tutorialForm", new CompoundPropertyModel<DbTutorialConfig>(new IModel<DbTutorialConfig>() {
            private DbTutorialConfig dbTutorialConfig;

            @Override
            public DbTutorialConfig getObject() {
                if (dbTutorialConfig == null) {
                    dbTutorialConfig = tutorialService.getDbTutorialConfig(dbTutorialConfigId);
                }
                return dbTutorialConfig;
            }

            @Override
            public void setObject(DbTutorialConfig object) {
                // Ignore
            }

            @Override
            public void detach() {
                dbTutorialConfig = null;
            }
        }));
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
        form.add(new CheckBox("tracking"));
        form.add(new TextField<Integer>("dbTerrainSetting", new IModel<Integer>() {
            @Override
            public Integer getObject() {
                DbTutorialConfig dbTutorialConfig = (DbTutorialConfig) form.getDefaultModelObject();
                if (dbTutorialConfig.getDbTerrainSetting() != null) {
                    return dbTutorialConfig.getDbTerrainSetting().getId();
                } else {
                    return null;
                }
            }

            @Override
            public void setObject(Integer id) {
                try {
                    DbTerrainSetting dbTerrainSetting = terrainService.getDbTerrainSettingCrudServiceHelper().readDbChild(id);
                    ((DbTutorialConfig) form.getDefaultModelObject()).setDbTerrainSetting(dbTerrainSetting);
                } catch (Throwable t) {
                    log.error("", t);
                    error(t.getMessage());
                }
            }

            @Override
            public void detach() {
                // Ignore
            }
        }, Integer.class));

        new CrudTableHelper<DbTaskConfig>("taskTable", null, "createTask", true, form, true) {

            @Override
            protected CrudServiceHelper<DbTaskConfig> getCrudServiceHelper() {
                return ((DbTutorialConfig) form.getDefaultModelObject()).getCrudServiceHelper();
            }

            @Override
            protected void onEditSubmit(DbTaskConfig dbTaskConfig) {
                setResponsePage(new TaskEditor(dbTaskConfig));
            }
        };

        form.add(new Button("save") {

            @Override
            public void onSubmit() {
                tutorialService.saveTutorial((DbTutorialConfig) form.getDefaultModelObject());
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