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

package com.btxtech.game.jsre.common.utg.condition;

import com.btxtech.game.jsre.client.cockpit.Cockpit;
import com.btxtech.game.jsre.client.simulation.SimulationConditionServiceImpl;
import com.btxtech.game.jsre.common.utg.config.CockpitWidgetEnum;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FocusWidget;

/**
 * User: beat
 * Date: 18.07.2010
 * Time: 21:06:41
 */
public class CockpitButtonClickedComparison extends AbstractComparison {
    private CockpitWidgetEnum cockpitButton;
    private HandlerRegistration handlerRegistration;
    private FocusWidget button;
    private boolean fulfilled = false;

    public CockpitButtonClickedComparison(CockpitWidgetEnum cockpitButton) {
        this.cockpitButton = cockpitButton;
        button = Cockpit.getInstance().getFocusWidget(cockpitButton);
    }

    public void onClick(ClickEvent event) {
        fulfilled = button.equals(event.getSource());
    }

    public CockpitWidgetEnum getCockpitButton() {
        return cockpitButton;
    }

    public void registerClickHandler() {
        handlerRegistration = button.addClickHandler(SimulationConditionServiceImpl.getInstance());
    }

    public void unregisterClickHandler() {
        handlerRegistration.removeHandler();
    }

    @Override
    public boolean isFulfilled() {
        return fulfilled;
    }
}