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

import com.btxtech.game.services.tutorial.condition.DbSelectionConditionConfig;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * User: beat
 * Date: 28.07.2010
 * Time: 14:37:04
 */
public class SelectionConditionConfigPanel extends Panel {
    private ConditionWrapperPanel conditionWrapperPanel;

    public SelectionConditionConfigPanel(String id, final ConditionWrapperPanel conditionWrapperPanel) {
        super(id);
        this.conditionWrapperPanel = conditionWrapperPanel;
        add(new TextField<String>("itemIds", new IModel<String>() {
            @Override
            public String getObject() {
                return ((DbSelectionConditionConfig) conditionWrapperPanel.getDbAbstractConditionConfig()).getIdsString();
            }

            @Override
            public void setObject(String value) {
                ((DbSelectionConditionConfig) conditionWrapperPanel.getDbAbstractConditionConfig()).setIdsString(value);
            }

            @Override
            public void detach() {
                // Ignore
            }
        }));
    }

    @Override
    public boolean isVisible() {
        return conditionWrapperPanel.getDbAbstractConditionConfig() instanceof DbSelectionConditionConfig;
    }

}
