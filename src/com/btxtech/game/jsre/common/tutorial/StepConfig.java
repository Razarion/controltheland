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

import com.btxtech.game.jsre.common.tutorial.condition.AbstractConditionConfig;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * User: beat
 * Date: 18.07.2010
 * Time: 15:11:11
 */
public class StepConfig implements Serializable {
    private Collection<AbstractConditionConfig> abstractConditionConfigs;
    private GraphicHintConfig graphicHintConfig;
    private String description;

    /**
     * Used by GWT
     */
    public StepConfig() {
    }

    public StepConfig(AbstractConditionConfig abstractConditionConfig, GraphicHintConfig graphicHintConfig, String description) {
        this.description = description;
        abstractConditionConfigs = new ArrayList<AbstractConditionConfig>();
        abstractConditionConfigs.add(abstractConditionConfig);
        this.graphicHintConfig = graphicHintConfig;
    }

    public StepConfig(Collection<AbstractConditionConfig> abstractConditionConfigs, GraphicHintConfig graphicHintConfig, String description) {
        this.abstractConditionConfigs = abstractConditionConfigs;
        this.graphicHintConfig = graphicHintConfig;
        this.description = description;
    }

    public Collection<AbstractConditionConfig> getConditions() {
        return abstractConditionConfigs;
    }

    public GraphicHintConfig getGraphicHintConfig() {
        return graphicHintConfig;
    }

    public String getDescription() {
        return description;
    }
}
