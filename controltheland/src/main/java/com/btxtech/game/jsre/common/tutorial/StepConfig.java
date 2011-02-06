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

package com.btxtech.game.jsre.common.tutorial;

import com.btxtech.game.jsre.common.utg.config.ConditionConfig;
import java.io.Serializable;
import java.util.Collection;

/**
 * User: beat
 * Date: 18.07.2010
 * Time: 15:11:11
 */
public class StepConfig implements Serializable {
    private Collection<HintConfig> hintConfigs;
    private String name;
    private ConditionConfig conditionConfig;

    /**
     * Used by GWT
     */
    public StepConfig() {
    }

    public StepConfig(ConditionConfig conditionConfig, Collection<HintConfig> hintConfigs, String name) {
        this.conditionConfig = conditionConfig;
        this.hintConfigs = hintConfigs;
        this.name = name;
    }

    public Collection<HintConfig> getGraphicHintConfigs() {
        return hintConfigs;
    }

    public String getName() {
        return name;
    }

    public ConditionConfig getConditionConfig() {
        return conditionConfig;
    }
}
