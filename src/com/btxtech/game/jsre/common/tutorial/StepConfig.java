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

/**
 * User: beat
 * Date: 18.07.2010
 * Time: 15:11:11
 */
public class StepConfig implements Serializable {
    private AbstractConditionConfig abstractConditionConfig;
    private ResourceHintConfig resourceHintConfig;
    private String description;
    private String name;

    /**
     * Used by GWT
     */
    public StepConfig() {
    }

    public StepConfig(AbstractConditionConfig abstractConditionConfig, ResourceHintConfig resourceHintConfig, String description, String name) {
        this.description = description;
        this.abstractConditionConfig = abstractConditionConfig;
        this.resourceHintConfig = resourceHintConfig;
        this.name = name;
    }

    public AbstractConditionConfig getAbstractConditionConfig() {
        return abstractConditionConfig;
    }

    public ResourceHintConfig getGraphicHintConfig() {
        return resourceHintConfig;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }
}
