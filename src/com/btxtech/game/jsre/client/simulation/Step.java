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

package com.btxtech.game.jsre.client.simulation;

import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.tutorial.StepConfig;
import com.btxtech.game.jsre.common.utg.condition.CockpitButtonClickedComparison;
import com.btxtech.game.jsre.common.utg.config.CockpitButtonClickedComparisonConfig;
import com.btxtech.game.jsre.common.utg.config.CockpitWidgetEnum;
import com.btxtech.game.jsre.common.utg.config.ConditionConfig;
import com.btxtech.game.jsre.common.utg.config.ConditionTrigger;
import com.btxtech.game.jsre.common.utg.config.SyncItemIdComparisonConfig;
import com.btxtech.game.jsre.common.utg.config.SyncItemIdPositionComparisonConfig;
import java.util.ArrayList;

/**
 * User: beat
 * Date: 18.07.2010
 * Time: 16:15:26
 */
public class Step {
    private StepConfig stepConfig;

    public Step(StepConfig stepConfig) {
        this.stepConfig = stepConfig;
        // TODO SimulationConditionServiceImpl.getInstance().activateCondition(stepConfig.getConditionConfig(), null);

        _TEST_(stepConfig);
    }

    private void _TEST_(StepConfig stepConfig) {
        if (stepConfig.getName().equals("Select")) {
            _TEST_1();
        } else if (stepConfig.getName().equals("Move")) {
            _TEST_2();
        } else if (stepConfig.getName().equals("Kill")) {
            _TEST_3();
        } else if (stepConfig.getName().equals("Scroll")) {
            _TEST_4();
        } else if (stepConfig.getName().equals("Scroll Home")) {
            _TEST_5();
        }
    }

    private void _TEST_1() {
        ArrayList<Integer> arrayList = new ArrayList<Integer>();
        arrayList.add(1);
        SyncItemIdComparisonConfig syncItemIdComparisonConfig = new SyncItemIdComparisonConfig(arrayList);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_SELECT, syncItemIdComparisonConfig);
        SimulationConditionServiceImpl.getInstance().activateCondition(conditionConfig, null);
    }

    private void _TEST_2() {
        SyncItemIdPositionComparisonConfig syncItemIdPositionComparisonConfig = new SyncItemIdPositionComparisonConfig(1, new Rectangle(130, 170, 120, 120));
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_DEACTIVATE, syncItemIdPositionComparisonConfig);
        SimulationConditionServiceImpl.getInstance().activateCondition(conditionConfig, null);
    }

    private void _TEST_3() {
        ArrayList<Integer> arrayList = new ArrayList<Integer>();
        arrayList.add(2);
        SyncItemIdComparisonConfig syncItemIdComparisonConfig = new SyncItemIdComparisonConfig(arrayList);
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SYNC_ITEM_KILLED, syncItemIdComparisonConfig);
        SimulationConditionServiceImpl.getInstance().activateCondition(conditionConfig, null);
    }

    private void _TEST_4() {
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.SCROLL, null);
        SimulationConditionServiceImpl.getInstance().activateCondition(conditionConfig, null);
    }

    private void _TEST_5() {

        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.COCKPIT_BUTTON_EVENT, new CockpitButtonClickedComparisonConfig(CockpitWidgetEnum.SCROLL_HOME_BUTTON));
        SimulationConditionServiceImpl.getInstance().activateCondition(conditionConfig, null);
    }

    public StepConfig getStepConfig() {
        return stepConfig;
    }

}
