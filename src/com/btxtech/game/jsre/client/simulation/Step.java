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
import com.btxtech.game.jsre.common.tutorial.condition.AbstractConditionConfig;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * User: beat
 * Date: 18.07.2010
 * Time: 16:15:26
 */
public class Step {
    private StepConfig stepConfig;
    private TutorialGui tutorialGui;
    private Collection<AbstractCondition> conditions = new ArrayList<AbstractCondition>();

    public Step(StepConfig stepConfig, TutorialGui tutorialGui) {
        this.stepConfig = stepConfig;
        tutorialGui.setStepText(stepConfig.getDescription());
        for (AbstractConditionConfig abstractConditionConfig : stepConfig.getConditions()) {
            conditions.add(ConditionFactory.createCondition(abstractConditionConfig));
        }
    }

    public StepConfig getStepConfig() {
        return stepConfig;
    }

    public void onOwnSelectionChanged(Group selectedGroup) {
        for (Iterator<AbstractCondition> iterator = conditions.iterator(); iterator.hasNext();) {
            AbstractCondition condition = iterator.next();
            if (condition.isFulfilledSelection(selectedGroup)) {
                iterator.remove();
                return;
            }
        }

    }

    public void onSendCommand(SyncBaseItem syncItem, BaseCommand baseCommand) {
        for (Iterator<AbstractCondition> iterator = conditions.iterator(); iterator.hasNext();) {
            AbstractCondition condition = iterator.next();
            if (condition.isFulfilledSendCommand(syncItem, baseCommand)) {
                iterator.remove();
                return;
            }
        }
    }

    public void onSyncItemDeactivated(SyncBaseItem deactivatedItem) {
        for (Iterator<AbstractCondition> iterator = conditions.iterator(); iterator.hasNext();) {
            AbstractCondition condition = iterator.next();
            if (condition.isFulfilledSyncItemDeactivated(deactivatedItem)) {
                iterator.remove();
                return;
            }
        }
    }


    public void onSyncItemKilled(SyncItem killedItem, SyncBaseItem actor) {
        for (Iterator<AbstractCondition> iterator = conditions.iterator(); iterator.hasNext();) {
            AbstractCondition condition = iterator.next();
            if (condition.isFulfilledItemsKilled(killedItem, actor)) {
                iterator.remove();
                return;
            }
        }
    }

    public void onItemBuilt(SyncBaseItem syncBaseItem) {
        for (Iterator<AbstractCondition> iterator = conditions.iterator(); iterator.hasNext();) {
            AbstractCondition condition = iterator.next();
            if (condition.isFulfilledItemBuilt(syncBaseItem)) {
                iterator.remove();
                return;
            }
        }
    }

    public void onDeposit() {
        for (Iterator<AbstractCondition> iterator = conditions.iterator(); iterator.hasNext();) {
            AbstractCondition condition = iterator.next();
            if (condition.isFulfilledHarvest()) {
                iterator.remove();
                return;
            }
        }
    }

    public boolean isFulFilled() {
        return conditions.isEmpty();
    }
}
