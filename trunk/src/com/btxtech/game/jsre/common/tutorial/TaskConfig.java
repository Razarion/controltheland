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
import java.util.Collection;
import java.util.List;

/**
 * User: beat
 * Date: 17.07.2010
 * Time: 17:25:27
 */
public class TaskConfig implements Serializable {
    private Preparation preparation;
    private List<StepConfig> stepConfigs;
    private AbstractConditionConfig completionConditionConfig;
    private GraphicHintConfig graphicHintConfig;
    private Collection<Integer> allowedItemTypes;
    private int accountBalance;
    private AbstractConditionConfig restartConditionConfig;
    private String description;

    /**
     * Used by GWT
     */
    public TaskConfig() {
    }

    public TaskConfig(Preparation preparation, List<StepConfig> stepConfigs, AbstractConditionConfig completionConditionConfig, GraphicHintConfig graphicHintConfig, Collection<Integer> allowedItemTypes, int accountBalance,AbstractConditionConfig restartConditionConfig, String description) {
        this.preparation = preparation;
        this.stepConfigs = stepConfigs;
        this.completionConditionConfig = completionConditionConfig;
        this.graphicHintConfig = graphicHintConfig;
        this.allowedItemTypes = allowedItemTypes;
        this.accountBalance = accountBalance;
        this.restartConditionConfig = restartConditionConfig;
        this.description = description;
    }

    public Preparation getPreparation() {
        return preparation;
    }

    public List<StepConfig> getStepConfigs() {
        return stepConfigs;
    }

    public GraphicHintConfig getGraphicHintConfig() {
        return graphicHintConfig;
    }

    public AbstractConditionConfig getCompletionConditionConfig() {
        return completionConditionConfig;
    }

    public Collection<Integer> getAllowedItemTypes() {
        return allowedItemTypes;
    }

    public int getAccountBalance() {
        return accountBalance;
    }

    public AbstractConditionConfig getRestartConditionConfig() {
        return restartConditionConfig;
    }

    public String getDescription() {
        return description;
    }
}
