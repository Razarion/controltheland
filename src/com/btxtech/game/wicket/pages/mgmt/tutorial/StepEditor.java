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
import com.btxtech.game.services.tutorial.DbStepConfig;
import com.btxtech.game.services.tutorial.TutorialService;
import com.btxtech.game.services.tutorial.hint.DbCockpitSpeechBubbleHintConfig;
import com.btxtech.game.services.tutorial.hint.DbHintConfig;
import com.btxtech.game.services.tutorial.hint.DbItemSpeechBubbleHintConfig;
import com.btxtech.game.services.tutorial.hint.DbResourceHintConfig;
import com.btxtech.game.services.tutorial.hint.DbTerrainPositionSpeechBubbleHintConfig;
import com.btxtech.game.wicket.pages.mgmt.condition.ConditionConfigPanel;
import com.btxtech.game.wicket.pages.mgmt.tutorial.hint.CockpitSpeechBubbleHintConfigPanel;
import com.btxtech.game.wicket.pages.mgmt.tutorial.hint.ItemSpeechBubbleHintConfigPanel;
import com.btxtech.game.wicket.pages.mgmt.tutorial.hint.ResourceHintConfigPanel;
import com.btxtech.game.wicket.pages.mgmt.tutorial.hint.TerrainPositionSpeechBubbleHintConfigPanel;
import com.btxtech.game.wicket.uiservices.CrudTableHelper;
import java.util.Arrays;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 27.07.2010
 * Time: 23:29:54
 */
public class StepEditor extends WebPage {
    @SpringBean
    private TutorialService tutorialService;
    private Class createChoice = DbHintConfig.ALL_HINTS[0];
    private DbStepConfig dbStepConfig;

    public StepEditor(final int dbStepConfigId) {
        add(new FeedbackPanel("msgs"));

        Form<DbStepConfig> form = new Form<DbStepConfig>("stepForm", new CompoundPropertyModel<DbStepConfig>(new IModel<DbStepConfig>() {


            @Override
            public DbStepConfig getObject() {
                if (dbStepConfig == null) {
                    dbStepConfig = tutorialService.getDbStepConfig(dbStepConfigId);
                }
                return dbStepConfig;
            }

            @Override
            public void setObject(DbStepConfig object) {
                // Ignore
            }

            @Override
            public void detach() {
                dbStepConfig = null;
            }
        }));
        add(form);

        form.add(new ConditionConfigPanel("conditionConfig"));
        new CrudTableHelper<DbHintConfig>("hints", null, "createHint", false, form) {
            @Override
            protected CrudServiceHelper<DbHintConfig> getCrudServiceHelper() {
                return dbStepConfig.getHintConfigCrudServiceHelper();
            }

            @Override
            protected void setupCreate(WebMarkupContainer markupContainer, String createId) {
                markupContainer.add(new DropDownChoice<Class>("createHintChoice", new IModel<Class>() {
                    @Override
                    public Class getObject() {
                        return createChoice;
                    }

                    @Override
                    public void setObject(Class object) {
                        createChoice = object;
                    }

                    @Override
                    public void detach() {
                        // Ignored
                    }
                }, Arrays.asList(DbHintConfig.ALL_HINTS)));
                markupContainer.add(new Button(createId) {

                    @Override
                    public void onSubmit() {
                        getCrudServiceHelper().createDbChild(createChoice);
                    }
                });

            }

            @Override
            protected void extendedPopulateItem(Item<DbHintConfig> dbHintConfigItem) {
                super.extendedPopulateItem(dbHintConfigItem);
                if (dbHintConfigItem.getModelObject() instanceof DbItemSpeechBubbleHintConfig) {
                    dbHintConfigItem.add(new ItemSpeechBubbleHintConfigPanel("hint"));
                } else if (dbHintConfigItem.getModelObject() instanceof DbResourceHintConfig) {
                    dbHintConfigItem.add(new ResourceHintConfigPanel("hint", (DbResourceHintConfig) dbHintConfigItem.getModelObject()));
                } else if (dbHintConfigItem.getModelObject() instanceof DbTerrainPositionSpeechBubbleHintConfig) {
                    dbHintConfigItem.add(new TerrainPositionSpeechBubbleHintConfigPanel("hint"));
                } else if (dbHintConfigItem.getModelObject() instanceof DbCockpitSpeechBubbleHintConfig) {
                    dbHintConfigItem.add(new CockpitSpeechBubbleHintConfigPanel("hint"));
                } else {
                    throw new IllegalArgumentException("Unknown DbHintConfig: " + dbHintConfigItem.getModelObject());
                }
            }
        };
        form.add(new Button("save") {

            @Override
            public void onSubmit() {
                tutorialService.saveDbStepConfig(dbStepConfig);
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