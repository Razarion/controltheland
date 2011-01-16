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
import com.btxtech.game.services.tutorial.DbTutorialConfig;
import com.btxtech.game.services.tutorial.TutorialService;
import com.btxtech.game.services.utg.DbLevel;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.wicket.uiservices.CrudTableHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 12.05.2010
 * Time: 20:26:33
 */
public class DbLevelTable extends WebPage {
    @SpringBean
    private UserGuidanceService userGuidanceService;
    @SpringBean
    private TutorialService tutorialService;
    private Log log = LogFactory.getLog(DbLevelTable.class);

    public DbLevelTable() {
        add(new FeedbackPanel("msgs"));
        Form form = new Form("levelForm");
        add(form);

        new CrudTableHelper<DbLevel>("levels", "save", "add", true, form) {

            @Override
            protected CrudServiceHelper<DbLevel> getCrudServiceHelper() {
                return userGuidanceService.getDbLevelCrudServiceHelper();
            }

            @Override
            protected void setupSave(Form form, String saveId) {
                form.add(new Button(saveId) {

                    @Override
                    public void onSubmit() {
                        userGuidanceService.saveDbLevels(getLastModifiedList());
                    }
                });
            }

            @Override
            protected void deleteChild(DbLevel child) {
                userGuidanceService.deleteDbLevel(child);
            }

            @Override
            protected void onEditSubmit(DbLevel dbLevel) {
                setResponsePage(new DbLevelEditor(dbLevel));
            }

            @Override
            protected void extendedPopulateItem(final Item<DbLevel> dbLevelItem) {
                super.extendedPopulateItem(dbLevelItem);
                dbLevelItem.add(new CheckBox("realGame"));
                dbLevelItem.add(new TextField<Integer>("dbTutorialConfig", new IModel<Integer>() {
                    @Override
                    public Integer getObject() {
                        if (dbLevelItem.getModelObject().getDbTutorialConfig() != null) {
                            return dbLevelItem.getModelObject().getDbTutorialConfig().getId();
                        } else {
                            return null;
                        }
                    }

                    @Override
                    public void setObject(Integer id) {
                        try {
                            DbTutorialConfig dbTutorialConfig = tutorialService.getDbTutorialCrudServiceHelper().readDbChild(id);
                            dbLevelItem.getModelObject().setDbTutorialConfig(dbTutorialConfig);
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
