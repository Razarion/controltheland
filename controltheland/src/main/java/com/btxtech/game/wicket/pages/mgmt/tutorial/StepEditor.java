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
import com.btxtech.game.services.tutorial.DbStepConfig;
import com.btxtech.game.services.tutorial.hint.*;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import com.btxtech.game.wicket.pages.mgmt.condition.ConditionConfigPanel;
import com.btxtech.game.wicket.pages.mgmt.tutorial.hint.CockpitSpeechBubbleHintConfigPanel;
import com.btxtech.game.wicket.pages.mgmt.tutorial.hint.ItemSpeechBubbleHintConfigPanel;
import com.btxtech.game.wicket.pages.mgmt.tutorial.hint.ResourceHintConfigPanel;
import com.btxtech.game.wicket.pages.mgmt.tutorial.hint.TerrainPositionSpeechBubbleHintConfigPanel;
import com.btxtech.game.wicket.uiservices.CrudChildTableHelper;
import com.btxtech.game.wicket.uiservices.RuModel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Arrays;

/**
 * User: beat
 * Date: 27.07.2010
 * Time: 23:29:54
 */
public class StepEditor extends MgmtWebPage {
    @SpringBean
    private RuServiceHelper<DbStepConfig> ruStepServiceHelper;
    private Class createChoice = DbHintConfig.ALL_HINTS[0];

    public StepEditor(DbStepConfig dbStepConfig) {
        add(new FeedbackPanel("msgs"));

        final Form<DbStepConfig> form = new Form<DbStepConfig>("stepForm", new CompoundPropertyModel<DbStepConfig>(new RuModel<DbStepConfig>(dbStepConfig, DbStepConfig.class) {
            @Override
            protected RuServiceHelper<DbStepConfig> getRuServiceHelper() {
                return ruStepServiceHelper;
            }
        }));
        add(form);

        form.add(new ConditionConfigPanel("conditionConfig"));
        new CrudChildTableHelper<DbStepConfig, DbHintConfig>("hints", null, "createHint", false, form, false) {

            @Override
            protected RuServiceHelper<DbStepConfig> getRuServiceHelper() {
                return ruStepServiceHelper;
            }

            @Override
            protected DbStepConfig getParent() {
                return (DbStepConfig) form.getDefaultModelObject();
            }

            @Override
            protected CrudChildServiceHelper<DbHintConfig> getCrudChildServiceHelperImpl() {
                return ((DbStepConfig) form.getDefaultModelObject()).getHintConfigCrudServiceHelper();
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
                    @SuppressWarnings("unchecked")
                    public void onSubmit() {
                        createDbChild(createChoice);
                        refresh();                        
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
                ruStepServiceHelper.updateDbEntity((DbStepConfig) form.getDefaultModelObject());
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