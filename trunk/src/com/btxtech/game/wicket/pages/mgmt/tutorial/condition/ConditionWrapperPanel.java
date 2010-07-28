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

package com.btxtech.game.wicket.pages.mgmt.tutorial.condition;

import com.btxtech.game.services.tutorial.condition.DbAbstractConditionConfig;
import com.btxtech.game.services.tutorial.condition.DbHarvestConditionConfig;
import com.btxtech.game.services.tutorial.condition.DbItemBuiltConditionConfig;
import com.btxtech.game.services.tutorial.condition.DbItemsKilledConditionConfig;
import com.btxtech.game.services.tutorial.condition.DbItemsPositionReachedConditionConfig;
import com.btxtech.game.services.tutorial.condition.DbSelectionConditionConfig;
import com.btxtech.game.services.tutorial.condition.DbSendCommandConditionConfig;
import com.btxtech.game.wicket.pages.mgmt.tutorial.TaskEditor;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

/**
 * User: beat
 * Date: 28.07.2010
 * Time: 10:58:35
 */
public class ConditionWrapperPanel extends Panel {
    private static Class[] ALL_CONDITIONS = {
            DbHarvestConditionConfig.class,
            DbItemBuiltConditionConfig.class,
            DbItemsKilledConditionConfig.class,
            DbItemsPositionReachedConditionConfig.class,
            DbSelectionConditionConfig.class,
            DbSendCommandConditionConfig.class};
    private Object object;
    private Log log = LogFactory.getLog(TaskEditor.class);
    private DbAbstractConditionConfig dbAbstractConditionConfig;
    private Class choice = DbHarvestConditionConfig.class;


    public ConditionWrapperPanel(String id, Object object) {
        super(id);
        this.object = object;
        setupCreation();
        add(new HarvestConditionConfigPanel("harvestConditionConfigPanel", this));
        add(new ItemBuiltConditionConfigPanel("itemBuiltConditionConfigPanel", this));
        add(new ItemsKilledConditionConfigPanel("itemsKilledConditionConfigPanel", this));
        add(new ItemsPositionReachedConditionConfigPanel("itemsPositionReachedConditionConfigPanel", this));
        add(new SelectionConditionConfigPanel("selectionConditionConfigPanel", this));
        add(new SendCommandConditionConfigPanel("sendCommandConditionConfigPanel", this));
    }

    private void setupCreation() {
        add(new Label("type", new IModel<String>() {

            @Override
            public String getObject() {
                return dbAbstractConditionConfig.getClass().toString();
            }

            @Override
            public void setObject(String object) {
                //Ignore
            }

            @Override
            public void detach() {
                //Ignore
            }
        }) {
            @Override
            public boolean isVisible() {
                return dbAbstractConditionConfig != null;
            }
        });
        add(new Button("create") {

            @Override
            public void onSubmit() {
                createCondition();
            }

            @Override
            public boolean isVisible() {
                return dbAbstractConditionConfig == null;
            }
        });
        add(new DropDownChoice<Class>("createChoice", new IModel<Class>() {
            @Override
            public Class getObject() {
                return choice;
            }

            @Override
            public void setObject(Class object) {
                choice = object;
            }

            @Override
            public void detach() {
                // Ignored
            }
        }, Arrays.asList(ALL_CONDITIONS)) {
            @Override
            public boolean isVisible() {
                return dbAbstractConditionConfig == null;
            }
        });
        add(new Button("delete") {

            @Override
            public void onSubmit() {
                deleteCondition();
            }

            @Override
            public boolean isVisible() {
                return dbAbstractConditionConfig != null;
            }
        });

    }

    private void createCondition() {
        try {
            Constructor constructor = choice.getConstructor();
            dbAbstractConditionConfig = (DbAbstractConditionConfig) constructor.newInstance();
            PropertyModel propertyModel = new PropertyModel(object, getId());
            propertyModel.getPropertySetter().invoke(object, dbAbstractConditionConfig);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    private void deleteCondition() {
        try {
            dbAbstractConditionConfig = null;
            PropertyModel propertyModel = new PropertyModel(object, getId());
            propertyModel.getPropertySetter().invoke(object, dbAbstractConditionConfig);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Override
    protected void onBeforeRender() {
        PropertyModel propertyModel = new PropertyModel(object, getId());
        try {
            dbAbstractConditionConfig = (DbAbstractConditionConfig) propertyModel.getPropertyGetter().invoke(object);
        } catch (Exception e) {
            log.error("", e);
        }
        super.onBeforeRender();
    }

    public DbAbstractConditionConfig getDbAbstractConditionConfig() {
        return dbAbstractConditionConfig;
    }
}
