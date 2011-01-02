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

package com.btxtech.game.jsre.common.level.config;

import java.io.Serializable;

/**
 * User: beat
 * Date: 27.12.2010
 * Time: 22:59:16
 */
public class ConditionConfig implements Serializable {
    private ConditionTrigger conditionTrigger;
    private AbstractComparisonConfig abstractComparisonConfig;

    /**
     * Used by GWT
     */
    public ConditionConfig() {
    }

    public ConditionConfig(ConditionTrigger conditionTrigger, AbstractComparisonConfig abstractComparisonConfig) {
        this.conditionTrigger = conditionTrigger;
        this.abstractComparisonConfig = abstractComparisonConfig;
    }

    public ConditionTrigger getConditionTrigger() {
        return conditionTrigger;
    }

    public AbstractComparisonConfig getAbstractComparisonConfig() {
        return abstractComparisonConfig;
    }
}
