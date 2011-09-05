/*
 * Copyright (c) 2011.
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

package com.btxtech.game.jsre.common.utg.condition;

import com.btxtech.game.jsre.common.gameengine.services.Services;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;

/**
 * User: beat
 * Date: 12.01.2011
 * Time: 12:05:40
 */
public abstract class AbstractSyncItemComparison implements AbstractComparison {
    private Services services;
    private Integer excludedTerritoryId;

    protected abstract void privateOnSyncItem(SyncItem syncItem);

    protected AbstractSyncItemComparison(Integer excludedTerritoryId) {
        this.excludedTerritoryId = excludedTerritoryId;
    }

    public final void onSyncItem(SyncItem syncItem) {
        if (excludedTerritoryId != null) {
            if (getServices().getTerritoryService().isTerritory(excludedTerritoryId, syncItem.getSyncItemArea().getPosition())) {
                return;
            }
        }
        privateOnSyncItem(syncItem);
    }

    protected Services getServices() {
        return services;
    }

    public void setServices(Services services) {
        this.services = services;
    }
}
