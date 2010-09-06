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

package com.btxtech.game.jsre.common.gameengine.services.items;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.InsufficientFundsException;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.services.Services;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * User: beat
 * Date: 18.11.2009
 * Time: 22:01:19
 */
public interface ItemService {
    SyncItem getItem(Id id) throws ItemDoesNotExistException;

    List<SyncBaseItem> getBaseItems(List<Id> baseItemsIds) throws ItemDoesNotExistException;

    List<Id> getBaseItemIds(List<SyncBaseItem> baseItems);

    void killSyncItem(SyncItem killedItem, SyncBaseItem actor, boolean force, boolean explode);

    SyncItem createSyncObject(ItemType toBeBuilt, Index position, SyncBaseItem creator, SimpleBase base, int createdChildCount) throws NoSuchItemTypeException;

    SyncItem buySyncObject(BaseItemType toBeBuiltType, Index toBeBuildPosition, SyncBaseItem creator, SimpleBase base, int createdChildCount) throws InsufficientFundsException, NoSuchItemTypeException;

    boolean baseObjectExists(SyncItem currentBuildup);

    ItemType getItemType(int itemTypeId) throws NoSuchItemTypeException;

    ItemType getItemType(String name) throws NoSuchItemTypeException;

    List<ItemType> getItemTypes();

    boolean areItemTypesLoaded();

    SyncItem newSyncItem(Id id, Index position, int itemTypeId, SimpleBase base, Services services) throws NoSuchItemTypeException;

    List<BaseItemType> ableToBuild(BaseItemType toBeBuilt);

    Map<BaseItemType, List<SyncBaseItem>> getItems4Base(SimpleBase simpleBase);

    List<? extends SyncItem> getItems(ItemType itemType, SimpleBase simpleBase);

    List<? extends SyncItem> getItems(String itemTypeName, SimpleBase simpleBase) throws NoSuchItemTypeException;

    List<SyncBaseItem> getEnemyItems(SimpleBase base);

    boolean hasBuildingsInRect(Rectangle rectangle);

    boolean hasStandingItemsInRect(Rectangle rectangle, SyncItem exceptThat);
}
