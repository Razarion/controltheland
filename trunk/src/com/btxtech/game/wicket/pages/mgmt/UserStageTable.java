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

import com.btxtech.game.services.bot.DbBotConfig;
import com.btxtech.game.services.cms.CmsService;
import com.btxtech.game.services.common.CrudServiceHelper;
import com.btxtech.game.services.terrain.DbTerrainSetting;
import com.btxtech.game.services.terrain.TerrainService;
import com.btxtech.game.services.tutorial.DbTutorialConfig;
import com.btxtech.game.services.tutorial.TutorialService;
import com.btxtech.game.services.utg.DbUserStage;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.wicket.uiservices.CrudTableHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 16.02.2010
 * Time: 21:35:44
 */
public class UserStageTable extends WebPage {
    @SpringBean
    private UserGuidanceService userGuidanceService;
    @SpringBean
    private TutorialService tutorialService;
    @SpringBean
    private CmsService cmsService;
    @SpringBean
    private TerrainService terrainService;
    private Log log = LogFactory.getLog(UserStageTable.class);

    public UserStageTable() {
        add(new FeedbackPanel("msgs"));
        Form<DbBotConfig> form = new Form<DbBotConfig>("from");
        add(form);

        new CrudTableHelper<DbUserStage>("userStageTable", "saveUserStage", "createUserStage", true, form, false) {

            @Override
            protected CrudServiceHelper<DbUserStage> getCrudServiceHelper() {
                // TODO return userGuidanceService.getUserStageCrudServiceHelper(); 
                return null;
            }

            @Override
            protected void extendedPopulateItem(final Item<DbUserStage> item) {
                super.extendedPopulateItem(item);
                item.add(new CheckBox("isRealGame"));
                item.add(new TextField<Integer>("dbTutorialConfig", new IModel<Integer>() {
                    @Override
                    public Integer getObject() {
                        if (item.getModelObject().getDbTutorialConfig() != null) {
                            return item.getModelObject().getDbTutorialConfig().getId();
                        } else {
                            return null;
                        }
                    }

                    @Override
                    public void setObject(Integer id) {
                        try {
                            DbTutorialConfig dbTutorialConfig = tutorialService.getDbTutorialCrudServiceHelper().readDbChild(id);
                            item.getModelObject().setDbTutorialConfig(dbTutorialConfig);
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
                item.add(new TextField<Integer>("dbTerrainSetting", new IModel<Integer>() {
                    @Override
                    public Integer getObject() {
                        if (item.getModelObject().getDbTerrainSetting() != null) {
                            return item.getModelObject().getDbTerrainSetting().getId();
                        } else {
                            return null;
                        }
                    }

                    @Override
                    public void setObject(Integer id) {
                        try {
                            DbTerrainSetting dbTerrainSetting = terrainService.getDbTerrainSettingCrudServiceHelper().readDbChild(id);
                            item.getModelObject().setDbTerrainSetting(dbTerrainSetting);
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

                item.add(new Button("up") {
                    @Override
                    public void onSubmit() {
                        swapRow(item.getIndex(), item.getIndex() - 1);
                    }

                    @Override
                    public boolean isVisible() {
                        return item.getIndex() > 0;
                    }
                });
                item.add(new Button("down") {
                    @Override
                    public void onSubmit() {
                        swapRow(item.getIndex(), item.getIndex() + 1);
                    }

                    @Override
                    public boolean isVisible() {
                        return item.getIndex() + 1 < rowCount();
                    }

                });
            }

            @Override
            protected void onEditSubmit(DbUserStage dbUserStage) {
                setResponsePage(new UserStageEditor(dbUserStage));
            }
        };
    }
}