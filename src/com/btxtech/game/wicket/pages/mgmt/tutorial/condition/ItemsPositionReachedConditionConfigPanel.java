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

import com.btxtech.game.services.tutorial.condition.DbItemsPositionReachedConditionConfig;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * User: beat
 * Date: 28.07.2010
 * Time: 14:36:46
 */
public class ItemsPositionReachedConditionConfigPanel extends Panel {
    private ConditionWrapperPanel conditionWrapperPanel;

    public ItemsPositionReachedConditionConfigPanel(String id, final ConditionWrapperPanel conditionWrapperPanel) {
        super(id);
        this.conditionWrapperPanel = conditionWrapperPanel;
        add(new TextField<String>("itemIds", new IModel<String>() {
            @Override
            public String getObject() {
                return ((DbItemsPositionReachedConditionConfig) conditionWrapperPanel.getDbAbstractConditionConfig()).getIdsString();
            }

            @Override
            public void setObject(String value) {
                ((DbItemsPositionReachedConditionConfig) conditionWrapperPanel.getDbAbstractConditionConfig()).setIdsString(value);
            }

            @Override
            public void detach() {
                // Ignore
            }
        }));
        add(new TextField<Integer>("x", new IModel<Integer>() {
            @Override
            public Integer getObject() {
                return ((DbItemsPositionReachedConditionConfig) conditionWrapperPanel.getDbAbstractConditionConfig()).getRegion().getX();
            }

            @Override
            public void setObject(Integer value) {
                ((DbItemsPositionReachedConditionConfig) conditionWrapperPanel.getDbAbstractConditionConfig()).getRegion().setX(value);
            }

            @Override
            public void detach() {
                // Ignore
            }
        }, Integer.class));
        add(new TextField<Integer>("y", new IModel<Integer>() {
            @Override
            public Integer getObject() {
                return ((DbItemsPositionReachedConditionConfig) conditionWrapperPanel.getDbAbstractConditionConfig()).getRegion().getY();
            }

            @Override
            public void setObject(Integer value) {
                ((DbItemsPositionReachedConditionConfig) conditionWrapperPanel.getDbAbstractConditionConfig()).getRegion().setY(value);
            }

            @Override
            public void detach() {
                // Ignore
            }
        }, Integer.class));
        add(new TextField<Integer>("width", new IModel<Integer>() {
            @Override
            public Integer getObject() {
                return ((DbItemsPositionReachedConditionConfig) conditionWrapperPanel.getDbAbstractConditionConfig()).getRegion().getWidth();
            }

            @Override
            public void setObject(Integer value) {
                ((DbItemsPositionReachedConditionConfig) conditionWrapperPanel.getDbAbstractConditionConfig()).getRegion().setWidth(value);
            }

            @Override
            public void detach() {
                // Ignore
            }
        }, Integer.class));
        add(new TextField<Integer>("height", new IModel<Integer>() {
            @Override
            public Integer getObject() {
                return ((DbItemsPositionReachedConditionConfig) conditionWrapperPanel.getDbAbstractConditionConfig()).getRegion().getHeight();
            }

            @Override
            public void setObject(Integer value) {
                ((DbItemsPositionReachedConditionConfig) conditionWrapperPanel.getDbAbstractConditionConfig()).getRegion().setHeight(value);
            }

            @Override
            public void detach() {
                // Ignore
            }
        }, Integer.class));

    }

    @Override
    public boolean isVisible() {
        return conditionWrapperPanel.getDbAbstractConditionConfig() instanceof DbItemsPositionReachedConditionConfig;
    }


}
