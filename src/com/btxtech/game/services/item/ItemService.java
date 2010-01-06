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

package com.btxtech.game.services.item;

import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.syncInfos.SyncItemInfo;
import com.btxtech.game.services.item.itemType.DbItemType;
import com.btxtech.game.services.item.itemType.DbItemTypeImage;
import com.btxtech.game.services.item.itemType.DbItemTypeData;
import java.util.Collection;
import java.util.List;

/**
 * User: beat
 * Date: Jun 6, 2009
 * Time: 10:45:17 AM
 */
public interface ItemService extends com.btxtech.game.jsre.common.gameengine.services.items.ItemService {
    Collection<SyncItemInfo> getSyncInfo();

    boolean hasItemsInRectangle(Rectangle rectangle);

    SyncBaseItem getFirstEnemyItemInRange(SyncBaseItem baseSyncItem, int range);

    List<SyncItem> getItemsCopy();

    void restoreItems(Collection<SyncItem> syncItems);

    void saveDbItemTypes(Collection<DbItemType> itemTypes);

    void saveDbItemType(DbItemType dBItemType);

    Collection<DbItemType> getDbItemTypes();

    void loadItemType();

    DbItemType getDbItemType(int itemTypeId);

    void delteItemType(DbItemType dbItemType);

    void removeItemTypeImages(DbItemType dbItemType);

    DbItemTypeImage getItemTypeImage(int itemTypeId, int index);

    DbItemTypeData getMuzzleFlashImage(int itemTypeId);

    DbItemTypeData getMuzzleFlashSound(int itemTypeId);
}
