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

package com.btxtech.game.jsre.common.tutorial.condition;

import com.btxtech.game.jsre.common.utg.config.CockpitWidgetEnum;

/**
 * User: beat
 * Date: 01.12.2010
 * Time: 13:21:11
 */
public class CockpitButtonConditionConfig extends AbstractConditionConfig {
    private CockpitWidgetEnum cockpitButton;

    public CockpitButtonConditionConfig(CockpitWidgetEnum cockpitButton) {
        this.cockpitButton = cockpitButton;
    }

    /**
     * Used by GWT
     */
    public CockpitButtonConditionConfig() {
    }

    public CockpitWidgetEnum getCockpitButton() {
        return cockpitButton;
    }
}
