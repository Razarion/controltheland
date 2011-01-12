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

import com.btxtech.game.jsre.common.utg.config.CockpitWidgetEnum;
import com.btxtech.game.services.tutorial.condition.DbCockpitButtonConditionConfig;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * User: beat
 * Date: 01.12.2010
 * Time: 13:40:27
 */
public class CockpitButtonConditionConfigPanel extends Panel {
    private ConditionWrapperPanel conditionWrapperPanel;

    public CockpitButtonConditionConfigPanel(String id, final ConditionWrapperPanel conditionWrapperPanel) {
        super(id);
        this.conditionWrapperPanel = conditionWrapperPanel;
        add(new DropDownChoice<CockpitWidgetEnum>("cockpitButton", new IModel<CockpitWidgetEnum>() {

            @Override
            public CockpitWidgetEnum getObject() {
                return ((DbCockpitButtonConditionConfig) conditionWrapperPanel.getDbAbstractConditionConfig()).getCockpitButton();
            }

            @Override
            public void setObject(CockpitWidgetEnum cockpitWidgetEnum) {
                ((DbCockpitButtonConditionConfig) conditionWrapperPanel.getDbAbstractConditionConfig()).setCockpitButton(cockpitWidgetEnum);
            }

            @Override
            public void detach() {
                // Ignore
            }
        }, CockpitWidgetEnum.getButtons()));
    }

    @Override
    public boolean isVisible() {
        return conditionWrapperPanel.getDbAbstractConditionConfig() instanceof DbCockpitButtonConditionConfig;
    }
    
}
