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

package com.btxtech.game.services.tutorial;

import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItemListener;
import com.btxtech.game.jsre.common.tutorial.SyncItemListenerTaskConfig;
import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import java.util.Locale;

/**
 * User: beat
 * Date: 21.12.2013
 * Time: 14:11:15
 */
@Entity
@DiscriminatorValue("SYNC_ITEM_LISTENER_TASK_CONFIG")
public class DbSyncItemListenerTaskConfig extends DbAbstractTaskConfig {
    @ManyToOne(fetch = FetchType.LAZY)
    private DbBaseItemType syncItemTypeToWatch;
    @Enumerated(EnumType.STRING)
    private SyncItemListener.Change syncItemChange;

    public SyncItemListener.Change getSyncItemChange() {
        return syncItemChange;
    }

    public void setSyncItemChange(SyncItemListener.Change syncItemChange) {
        this.syncItemChange = syncItemChange;
    }

    public DbBaseItemType getSyncItemTypeToWatch() {
        return syncItemTypeToWatch;
    }

    public void setSyncItemTypeToWatch(DbBaseItemType syncItemTypeToWatch) {
        this.syncItemTypeToWatch = syncItemTypeToWatch;
    }

    @Override
    protected SyncItemListenerTaskConfig createTaskConfig(ServerItemTypeService serverItemTypeService, Locale locale) {
        SyncItemListenerTaskConfig syncItemListenerTaskConfig = new SyncItemListenerTaskConfig();
        syncItemListenerTaskConfig.setSyncItemTypeToWatch(syncItemTypeToWatch.getId());
        syncItemListenerTaskConfig.setSyncItemChange(syncItemChange);
        return syncItemListenerTaskConfig;
    }
}
