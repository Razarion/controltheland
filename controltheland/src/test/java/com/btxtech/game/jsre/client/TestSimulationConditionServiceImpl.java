package com.btxtech.game.jsre.client;

import com.btxtech.game.jsre.client.simulation.SimulationConditionServiceImpl;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.utg.ConditionServiceListener;
import com.btxtech.game.jsre.common.utg.config.ConditionConfig;
import com.btxtech.game.jsre.common.utg.config.ConditionTrigger;
import com.btxtech.game.jsre.common.utg.config.CountComparisonConfig;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;

/**
 * User: beat
 * Date: 14.11.2011
 * Time: 17:02:38
 */
public class TestSimulationConditionServiceImpl {
    private boolean passed;

    @Test
    @DirtiesContext
    public void baseDeleted() throws Exception {
        SimpleBase simpleBase = new SimpleBase(1);
        SimulationConditionServiceImpl conditionService = SimulationConditionServiceImpl.getInstance();

        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.BASE_KILLED, new CountComparisonConfig(null, 1));
        conditionService.activateCondition(conditionConfig, null);
        passed = false;
        conditionService.setConditionServiceListener(new ConditionServiceListener<Object>() {
            @Override
            public void conditionPassed(Object o) {
                passed = true;
            }
        });

        Assert.assertFalse(passed);
        conditionService.onBaseDeleted(simpleBase);
        Assert.assertTrue(passed);
    }

}
