/*
 * Copyright (c) 2011.
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

package com.btxtech.game.wicket.pages.mgmt.condition;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.utg.config.ConditionTrigger;
import com.btxtech.game.services.common.db.DbI18nString;
import com.btxtech.game.services.utg.condition.DbAbstractComparisonConfig;
import com.btxtech.game.services.utg.condition.DbConditionConfig;
import com.btxtech.game.wicket.uiservices.I18nStringWYSIWYGEditor;
import com.btxtech.game.wicket.uiservices.IndexPanel;
import com.btxtech.game.wicket.uiservices.TerrainLinkHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IFormModelUpdateListener;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import java.util.List;

/**
 * User: beat
 * Date: 12.01.2011
 * Time: 14:34:18
 */
public class ConditionConfigPanel extends Panel implements IFormModelUpdateListener {
    private boolean isDirty = false;
    private TerrainLinkHelper terrainLinkHelper;
    private Log log = LogFactory.getLog(ConditionConfigPanel.class);
    private IModel<ConditionTrigger> conditionTriggerModel = new IModel<ConditionTrigger>() {
        private ConditionTrigger conditionTrigger;

        @Override
        public ConditionTrigger getObject() {
            if (conditionTrigger == null) {
                DbConditionConfig dbConditionConfig = (DbConditionConfig) ConditionConfigPanel.this.getDefaultModelObject();
                if (dbConditionConfig != null) {
                    conditionTrigger = dbConditionConfig.getConditionTrigger();
                }
            }
            return conditionTrigger;
        }

        @Override
        public void setObject(ConditionTrigger conditionTrigger) {
            this.conditionTrigger = conditionTrigger;
            isDirty = true;
        }

        @Override
        public void detach() {
            //conditionTrigger = null;
            isDirty = false;
        }
    };
    private IModel<Class<? extends DbAbstractComparisonConfig>> comparisonModel = new IModel<Class<? extends DbAbstractComparisonConfig>>() {
        private Class<? extends DbAbstractComparisonConfig> dbAbstractComparisonConfig;

        @Override
        public Class<? extends DbAbstractComparisonConfig> getObject() {
            if (dbAbstractComparisonConfig == null) {
                DbConditionConfig dbConditionConfig = (DbConditionConfig) ConditionConfigPanel.this.getDefaultModelObject();
                if (dbConditionConfig != null && dbConditionConfig.getDbAbstractComparisonConfig() != null) {
                    dbAbstractComparisonConfig = dbConditionConfig.getDbAbstractComparisonConfig().getClass();
                }
            }
            return dbAbstractComparisonConfig;
        }

        @Override
        public void setObject(Class<? extends DbAbstractComparisonConfig> dbAbstractComparisonConfig) {
            this.dbAbstractComparisonConfig = dbAbstractComparisonConfig;
            isDirty = true;
        }

        @Override
        public void detach() {
            //dbAbstractComparisonConfig = null;
            isDirty = false;
        }
    };

    public ConditionConfigPanel(String id, TerrainLinkHelper terrainLinkHelper) {
        super(id);
        this.terrainLinkHelper = terrainLinkHelper;
        DropDownChoice<ConditionTrigger> triggers = new DropDownChoice<>("conditionTrigger", conditionTriggerModel, ComparisonFactory.getFilteredConditionTriggers());
        add(triggers);
        final DropDownChoice<Class<? extends DbAbstractComparisonConfig>> comparisons = new DropDownChoice<>("comparison", comparisonModel, new IModel<List<Class<? extends DbAbstractComparisonConfig>>>() {

            @Override
            public List<Class<? extends DbAbstractComparisonConfig>> getObject() {
                return ComparisonFactory.ComparisonClass.getClasses4ConditionTrigger(conditionTriggerModel.getObject());
            }

            @Override
            public void setObject(List<Class<? extends DbAbstractComparisonConfig>> object) {
                // Ignore
            }

            @Override
            public void detach() {
                // Ignore
            }
        });
        comparisons.setOutputMarkupId(true);
        add(comparisons);
        triggers.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(comparisons);
            }
        });
        IndexPanel radarPositionHint = new IndexPanel("radarPositionHint");
        radarPositionHint.setDefaultModel(new IModel<Index>() {
            @Override
            public Index getObject() {
                if (ConditionConfigPanel.this.getDefaultModelObject() != null) {
                    return ((DbConditionConfig) ConditionConfigPanel.this.getDefaultModelObject()).getRadarPositionHint();
                } else {
                    return null;
                }
            }

            @Override
            public void setObject(Index object) {
                if (ConditionConfigPanel.this.getDefaultModelObject() != null) {
                    ((DbConditionConfig) ConditionConfigPanel.this.getDefaultModelObject()).setRadarPositionHint(object);
                }
            }

            @Override
            public void detach() {
            }
        });
        add(radarPositionHint);

        I18nStringWYSIWYGEditor i18nStringWYSIWYGEditor = new I18nStringWYSIWYGEditor("i18nAdditionalDescription") {
            @Override
            public boolean isVisible() {
                return getDefaultModelObject() != null;
            }
        };
        i18nStringWYSIWYGEditor.setDefaultModel(new AbstractReadOnlyModel<DbI18nString>() {
            @Override
            public DbI18nString getObject() {
                if (ConditionConfigPanel.this.getDefaultModelObject() != null) {
                    return ((DbConditionConfig) ConditionConfigPanel.this.getDefaultModelObject()).getI18nAdditionalDescription();
                } else {
                    return null;
                }
            }

            @Override
            public void detach() {
            }
        });
        add(i18nStringWYSIWYGEditor);


        CheckBox hideQuestProgress = new CheckBox("hideQuestProgress");
        hideQuestProgress.setDefaultModel(new IModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return ConditionConfigPanel.this.getDefaultModelObject() != null
                        && ((DbConditionConfig) ConditionConfigPanel.this.getDefaultModelObject()).isHideQuestProgress();
            }

            @Override
            public void setObject(Boolean object) {
                if (ConditionConfigPanel.this.getDefaultModelObject() != null) {
                    ((DbConditionConfig) ConditionConfigPanel.this.getDefaultModelObject()).setHideQuestProgress(object);
                }
            }

            @Override
            public void detach() {
            }
        });
        add(hideQuestProgress);


        setupComparisonFields(terrainLinkHelper);
    }

    private void setupComparisonFields(TerrainLinkHelper terrainLinkHelper) {
        DbConditionConfig dbConditionConfig = (DbConditionConfig) getDefaultModelObject();
        addOrReplace(ComparisonFactory.createComparisonPanel("dbAbstractComparisonConfig", dbConditionConfig, terrainLinkHelper));
    }

    @Override
    protected void onBeforeRender() {
        setupComparisonFields(terrainLinkHelper);
        super.onBeforeRender();
    }

    @Override
    public void updateModel() {
        if (!isDirty) {
            return;
        }
        if (conditionTriggerModel.getObject().isComparisonNeeded() && comparisonModel.getObject() == null) {
            error("Comparison must be set");
            return;
        }

        DbConditionConfig dbConditionConfig = new DbConditionConfig();
        dbConditionConfig.setConditionTrigger(conditionTriggerModel.getObject());
        try {
            dbConditionConfig.setDbAbstractComparisonConfig(comparisonModel.getObject().getConstructor().newInstance());
        } catch (Exception e) {
            log.error("", e);
            error(e.toString());
        }
        setDefaultModelObject(dbConditionConfig);
    }
}