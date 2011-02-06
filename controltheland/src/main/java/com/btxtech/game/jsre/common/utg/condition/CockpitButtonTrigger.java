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

import com.btxtech.game.jsre.common.utg.config.ConditionTrigger;
import com.google.gwt.event.dom.client.ClickEvent;

/**
 * User: beat
 * Date: 12.01.2011
 * Time: 11:25:19
 */
public class CockpitButtonTrigger<T> extends AbstractConditionTrigger<T> {

    public CockpitButtonTrigger(AbstractComparison abstractComparison, T t) {
        super(ConditionTrigger.COCKPIT_BUTTON_EVENT, abstractComparison, t);
        CockpitButtonClickedComparison clickedComparison = (CockpitButtonClickedComparison) abstractComparison;
        clickedComparison.registerClickHandler();
    }

    public void onClick(ClickEvent event) {
        if (isFulfilled()) {
            return;
        }
        CockpitButtonClickedComparison clickedComparison = (CockpitButtonClickedComparison) getAbstractComparison();
        clickedComparison.onClick(event);

        if (clickedComparison.isFulfilled()) {
            clickedComparison.unregisterClickHandler();
            setFulfilled();
        }
    }
}
