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

package com.btxtech.game.services.bot.impl;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.services.action.ActionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * User: beat
 * Date: 17.09.2010
 * Time: 20:05:33
 */
public class BotSyncBaseItem {
    private SyncBaseItem syncBaseItem;
    private ActionService actionService;
    private Log log = LogFactory.getLog(BotSyncBaseItem.class);
    private boolean idle = true;

    public BotSyncBaseItem(SyncBaseItem syncBaseItem, ActionService actionService) {
        this.syncBaseItem = syncBaseItem;
        this.actionService = actionService;
    }

    public SyncBaseItem getSyncBaseItem() {
        return syncBaseItem;
    }

    public boolean isIdle() {
        return idle;
    }

    public void buildBuilding(Index position, BaseItemType toBeBuilt) {
        try {
            actionService.buildFactory(syncBaseItem, position, toBeBuilt);
            idle = false;
        } catch (Exception e) {
            idle = true;
            log.error("", e);
        }
    }

    public void buildUnit(BaseItemType toBeBuilt) {
        try {
            actionService.build(syncBaseItem, toBeBuilt);
            idle = false;
        } catch (Exception e) {
            idle = true;
            log.error("", e);
        }
    }

    public void attack(SyncBaseItem target) {
        try {
            actionService.attack(syncBaseItem, target, true);
            idle = false;
        } catch (Exception e) {
            idle = true;
            log.error("", e);
        }
    }

    public void updateIdleState() {
        idle = syncBaseItem.isIdle();
    }

    public boolean isAlive() {
        return syncBaseItem.isAlive();
    }

    public void stop() {
        syncBaseItem.stop();
        idle = true;
    }
}
