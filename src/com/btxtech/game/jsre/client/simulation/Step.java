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

import com.btxtech.game.jsre.client.cockpit.Group;
import com.btxtech.game.jsre.client.simulation.condition.AbstractCondition;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.btxtech.game.jsre.common.tutorial.StepConfig;

/**
 * User: beat
 * Date: 18.07.2010
 * Time: 16:15:26
 */
public class Step {
    private StepConfig stepConfig;
    private AbstractCondition condition;

    public Step(StepConfig stepConfig, TutorialGui tutorialGui) {
        this.stepConfig = stepConfig;
        tutorialGui.setStepText(stepConfig.getDescription());
        condition = ConditionFactory.createCondition(stepConfig.getAbstractConditionConfig());
    }

    public StepConfig getStepConfig() {
        return stepConfig;
    }

    public void onOwnSelectionChanged(Group selectedGroup) {
        if (condition != null && condition.isFulfilledSelection(selectedGroup)) {
            condition = null;
        }
    }

    public void onSendCommand(SyncBaseItem syncItem, BaseCommand baseCommand) {
        if (condition != null && condition.isFulfilledSendCommand(syncItem, baseCommand)) {
            condition = null;
        }
    }

    public void onSyncItemDeactivated(SyncBaseItem deactivatedItem) {
        if (condition != null && condition.isFulfilledSyncItemDeactivated(deactivatedItem)) {
            condition = null;
        }
    }


    public void onSyncItemKilled(SyncItem killedItem, SyncBaseItem actor) {
        if (condition != null && condition.isFulfilledItemsKilled(killedItem, actor)) {
            condition = null;
        }
    }

    public void onItemBuilt(SyncBaseItem syncBaseItem) {
        if (condition != null && condition.isFulfilledItemBuilt(syncBaseItem)) {
            condition = null;
        }
    }

    public void onDeposit() {
        if (condition != null && condition.isFulfilledHarvest()) {
            condition = null;
        }
    }

    public boolean isFulFilled() {
        return condition == null;
    }
}
