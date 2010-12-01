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

package com.btxtech.game.jsre.client.simulation.condition;

import com.btxtech.game.jsre.client.cockpit.Cockpit;
import com.btxtech.game.jsre.client.simulation.Simulation;
import com.btxtech.game.jsre.common.tutorial.condition.CockpitButtonConditionConfig;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * User: beat
 * Date: 01.12.2010
 * Time: 21:05:57
 */
public class CockpitButtonCondition extends AbstractCondition {
    private HandlerRegistration handlerRegistration;
    private FocusWidget button;

    public CockpitButtonCondition(CockpitButtonConditionConfig cockpitButtonConditionConfig) {
        button = Cockpit.getInstance().getFocusWidget(cockpitButtonConditionConfig.getCockpitButton());
        handlerRegistration = button.addClickHandler(Simulation.getInstance());
    }

    @Override
    public boolean isFulfilledCockpitButton(Widget widget) {
        if (button.equals(widget)) {
            handlerRegistration.removeHandler();
            return true;
        } else {
            return false;
        }
    }
}