package com.btxtech.game.jsre.common.tutorial;

import com.btxtech.game.jsre.client.simulation.AbstractTask;
import com.btxtech.game.jsre.client.simulation.ConditionTask;
import com.btxtech.game.jsre.common.utg.config.ConditionConfig;

/**
 * User: beat
 * Date: 11.09.13
 * Time: 15:17
 */
public class ConditionTaskConfig extends AbstractTaskConfig {
    private ConditionConfig conditionConfig;

    @Override
    public AbstractTask createTask() {
        return new ConditionTask(this);
    }

    public ConditionConfig getConditionConfig() {
        return conditionConfig;
    }

    public void setConditionConfig(ConditionConfig conditionConfig) {
        this.conditionConfig = conditionConfig;
    }
}
