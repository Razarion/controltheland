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

package com.btxtech.game.services.planet;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseObject;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.packets.SyncItemInfo;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * User: beat
 * Date: Jun 6, 2009
 * Time: 10:45:17 AM
 */
public interface ServerItemService extends com.btxtech.game.jsre.common.gameengine.services.items.ItemService {
    Collection<SyncItemInfo> getSyncInfo();

    List<SyncItem> getItemsCopy();

    Collection<SyncItem> getItems4Backup();

    void killSyncItemIds(Collection<Id> itemsToKill);

    void restore(Collection<SyncBaseObject> syncBaseObjects);

    void onGuildChanged(final Set<SimpleBase> simpleBases);

    boolean hasEnemyInRange(Set<SimpleBase> friendlyBases, Index middlePoint, int range);
}
