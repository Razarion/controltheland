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

import com.btxtech.game.jsre.common.utg.config.ConditionTrigger;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.condition.DbAbstractComparisonConfig;
import com.btxtech.game.services.utg.condition.DbConditionConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IFormModelUpdateListener;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Arrays;
import java.util.List;

/**
 * User: beat
 * Date: 12.01.2011
 * Time: 14:34:18
 */
public class ConditionConfigPanel extends Panel implements IFormModelUpdateListener {
    @SpringBean
    private UserGuidanceService userGuidanceService;

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
        }

        @Override
        public void detach() {
            // Ignore
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
        }

        @Override
        public void detach() {
            dbAbstractComparisonConfig = null;
        }
    };
    private Log log = LogFactory.getLog(ConditionConfigPanel.class);

    public ConditionConfigPanel(String id) {
        super(id);
        add(new DropDownChoice<ConditionTrigger>("conditionTrigger", conditionTriggerModel, Arrays.asList(ConditionTrigger.values())));
        add(new DropDownChoice<Class<? extends DbAbstractComparisonConfig>>("comparison", comparisonModel, new IModel<List<Class<? extends DbAbstractComparisonConfig>>>() {

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
        }) {
            @Override
            public boolean isVisible() {
                if (ComparisonFactory.ComparisonClass.getClasses4ConditionTrigger(conditionTriggerModel.getObject()) != null) {
                    return true;
                } else {
                    comparisonModel.setObject(null);
                    return false;
                }
            }
        });
        add(new Button("create") {

            @Override
            public void onSubmit() {
                if (conditionTriggerModel.getObject() == null) {
                    error("Trigger must be set");
                    setupComparisonFields();
                    return;
                }
                DbConditionConfig dbConditionConfig = (DbConditionConfig) ConditionConfigPanel.this.getDefaultModelObject();

                ConditionTrigger conditionTrigger = conditionTriggerModel.getObject();
                if (conditionTriggerModel.getObject() != dbConditionConfig.getConditionTrigger()) {
                    dbConditionConfig.setConditionTrigger(conditionTriggerModel.getObject());
                    dbConditionConfig.setDbAbstractComparisonConfig(null);
                    // TODO userGuidanceService.updateDbConditionConfig(dbConditionConfig);
                    setupComparisonFields();
                    return;
                }

                if (!conditionTrigger.isComparisonNeeded()) {
                    dbConditionConfig.setDbAbstractComparisonConfig(null);
                    // TODO userGuidanceService.updateDbConditionConfig(dbConditionConfig);
                    setupComparisonFields();
                    return;
                }

                if (comparisonModel.getObject() == null) {
                    error("Comparison must be set");
                    setupComparisonFields();
                    return;
                }

                boolean createComparisonConfig = false;
                if (dbConditionConfig.getDbAbstractComparisonConfig() != null) {
                    if (!dbConditionConfig.getDbAbstractComparisonConfig().getClass().equals(comparisonModel.getObject())) {
                        createComparisonConfig = true;
                    }
                } else {
                    createComparisonConfig = true;
                }

                if (createComparisonConfig) {
                    try {
                        dbConditionConfig.setDbAbstractComparisonConfig(comparisonModel.getObject().getConstructor().newInstance());
                        // TODO userGuidanceService.updateDbConditionConfig(dbConditionConfig);
                    } catch (Exception e) {
                        log.error("", e);
                    }
                }
                setupComparisonFields();
            }
        });
        setupComparisonFields();
    }

    private void setupComparisonFields() {
        DbConditionConfig dbConditionConfig = (DbConditionConfig) getDefaultModelObject();
        addOrReplace(ComparisonFactory.createComparisonPanel(dbConditionConfig, "dbAbstractComparisonConfig"));
    }

    @Override
    protected void onBeforeRender() {
        setupComparisonFields();
        super.onBeforeRender();
    }

    @Override
    public void updateModel() {
        if (getDefaultModelObject() == null) {
            // TODO geht nicht
            setDefaultModelObject(new DbConditionConfig());
        }
    }
}