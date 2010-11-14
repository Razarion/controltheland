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
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.tutorial.DbHintConfig;
import com.btxtech.game.services.tutorial.DbItemSpeechBubbleHintConfig;
import com.btxtech.game.services.tutorial.DbResourceHintConfig;
import com.btxtech.game.services.tutorial.DbStepConfig;
import com.btxtech.game.services.tutorial.DbTaskConfig;
import com.btxtech.game.services.tutorial.DbTerrainPositionSpeechBubbleHintConfig;
import com.btxtech.game.services.tutorial.DbTutorialConfig;
import com.btxtech.game.services.tutorial.TutorialService;
import com.btxtech.game.wicket.pages.mgmt.tutorial.condition.ConditionWrapperPanel;
import com.btxtech.game.wicket.uiservices.CrudTableHelper;
import java.util.Arrays;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
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
    @SpringBean
    private ItemService itemService;
    private Class createChoice = DbHintConfig.ALL_HINTS[0];

    public StepEditor(final DbTutorialConfig dbTutorialConfig, final DbStepConfig dbStepConfig) {
        add(new FeedbackPanel("msgs"));

        Form<DbTaskConfig> form = new Form<DbTaskConfig>("stepForm", new CompoundPropertyModel<DbTaskConfig>(dbStepConfig));
        add(form);

        form.add(new ConditionWrapperPanel("abstractConditionConfig", dbStepConfig));
        new CrudTableHelper<DbHintConfig>("hints", null, "createHint", false, form) {
            @Override
            protected CrudServiceHelper<DbHintConfig> getCrudServiceHelper() {
                return dbStepConfig.getHintConfigCrudServiceHelper();
            }

            @Override
            protected void setupCreate(Form form, String createId) {
                form.add(new DropDownChoice<Class>("createHintChoice", new IModel<Class>() {
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
                form.add(new Button(createId) {

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
                } else {
                    throw new IllegalArgumentException("Unknown DbHintConfig: " + dbHintConfigItem.getModelObject());
                }
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