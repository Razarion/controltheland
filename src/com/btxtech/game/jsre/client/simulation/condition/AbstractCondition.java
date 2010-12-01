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
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncTickItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;

/**
 * User: beat
 * Date: 18.07.2010
 * Time: 18:58:14
 */
public abstract class AbstractCondition {
    public boolean isFulfilledSelection(Group selectedGroup) {
        return false;
    }

    public boolean isFulfilledSendCommand(SyncBaseItem syncItem, BaseCommand baseCommand) {
        return false;
    }

    public boolean isFulfilledSyncItemDeactivated(SyncTickItem deactivatedItem) {
        return false;
    }

    public boolean isFulfilledItemsKilled(SyncItem syncItem, SimpleBase actor) {
        return false;
    }

    public boolean isFulfilledItemBuilt(SyncBaseItem newItem) {
        return false;
    }

    public boolean isFulfilledHarvest() {
        return false;
    }

    public boolean isFulfilledScroll() {
        return false;
    }
}
