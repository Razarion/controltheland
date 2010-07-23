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

package com.btxtech.game.jsre.client.simulation.condition;

import com.btxtech.game.jsre.client.cockpit.Group;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.tutorial.condition.SelectionConditionConfig;

/**
 * User: beat
 * Date: 18.07.2010
 * Time: 19:33:14
 */
public class SelectionCondition extends AbstractCondition {
    private SelectionConditionConfig selectionConditionConfig;

    public SelectionCondition(SelectionConditionConfig selectionConditionConfig) {
        this.selectionConditionConfig = selectionConditionConfig;
    }

    @Override
    public boolean isFulfilledSelection(Group selectedGroup) {
        for (Integer id : selectionConditionConfig.getIds()) {
            if (!checkIfIdExists(selectedGroup, id)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkIfIdExists(Group selectedGroup, Integer id) {
        for (SyncBaseItem syncBaseItem : selectedGroup.getSyncBaseItems()) {
            if (syncBaseItem.getId().getId() == id) {
                return true;
            }
        }
        return false;
    }
}
