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

import com.btxtech.game.jsre.client.common.NotYourBaseException;
import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.jsre.common.gameengine.itemType.BuildupStep;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.WeaponType;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.syncInfos.SyncItemInfo;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.item.itemType.DbBuildupStep;
import com.btxtech.game.services.item.itemType.DbItemType;
import com.btxtech.game.services.item.itemType.DbItemTypeImageData;
import com.btxtech.game.services.item.itemType.DbItemTypeSoundData;
import com.btxtech.game.services.item.itemType.DbItemTypeImage;
import com.btxtech.game.services.item.itemType.DbProjectileItemType;
import com.btxtech.game.services.item.itemType.DbResourceItemType;

import java.util.Collection;
import java.util.List;

/**
 * User: beat
 * Date: Jun 6, 2009
 * Time: 10:45:17 AM
 */
public interface ItemService extends com.btxtech.game.jsre.common.gameengine.services.items.ItemService {
    Collection<SyncItemInfo> getSyncInfo();

    List<SyncItem> getItemsCopy();

    Collection<SyncItem> getItemsCopyNoBot();

    void restoreItems(Collection<SyncItem> syncItems);

    void saveDbItemTypes(Collection<DbItemType> itemTypes);

    void saveAttackMatrix(Collection<DbBaseItemType> dbBaseItemTypes);

    void saveDbItemType(DbItemType dBItemType);

    BoundingBox getBoundingBox(int itemTypeId) throws NoSuchItemTypeException;

    void saveBoundingBox(int itemTypeId, BoundingBox boundingBox) throws NoSuchItemTypeException;

    void saveWeaponType(int itemTypeId, WeaponType weaponType) throws NoSuchItemTypeException;

    void saveBuildupStepData(int itemTypeId, List<BuildupStep> buildupSteps) throws NoSuchItemTypeException;

    Collection<DbItemType> getDbItemTypes();

    Collection<DbBaseItemType> getDbBaseItemTypes();

    Collection<DbProjectileItemType> getDbProjectileItemTypes();

    Collection<DbBaseItemType> getWeaponDbBaseItemTypes();

    void activate();

    DbItemType getDbItemType(int itemTypeId);

    DbBaseItemType getDbBaseItemType(int itemBaseTypeId);

    DbResourceItemType getDbResourceItemType(int resourceItemType);

    void deleteItemType(DbItemType dbItemType);

    DbItemTypeImage getItemTypeImage(int itemTypeId, int index);

    DbItemTypeImageData getMuzzleFlashImage(int itemTypeId);

    DbItemTypeSoundData getMuzzleFlashSound(int itemTypeId);

    void sellItem(Id id) throws ItemDoesNotExistException, NotYourBaseException;

    ItemType getItemType(DbItemType dbItemType);

    void killSyncItemIds(Collection<Id> itemsToKill);

    CrudRootServiceHelper<DbItemType> getDbItemTypeCrud();

    DbBuildupStep getDbBuildupStep(int itemTypeId, int buildupStepId);
}
