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

package com.btxtech.game.jsre.itemtypeeditor;

import com.btxtech.game.jsre.client.common.info.GameInfo;
import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemClipPosition;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemTypeSpriteMap;
import com.btxtech.game.jsre.common.gameengine.itemType.WeaponType;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import java.util.Collection;

/**
 * User: beat
 * Date: Sep 2, 2009
 * Time: 8:18:09 PM
 */
@RemoteServiceRelativePath("itemTypeAccess")
public interface ItemTypeAccess extends RemoteService {
    Collection<ItemType> getItemTypes();

    void saveItemTypeProperties(int itemTypeId,
                                BoundingBox boundingBox,
                                ItemTypeSpriteMap itemTypeSpriteMap,
                                WeaponType weaponType,
                                Collection<ItemTypeImageInfo> buildupImages,
                                Collection<ItemTypeImageInfo> runtimeImages,
                                Collection<ItemTypeImageInfo> demolitionImages,
                                ItemClipPosition harvesterItemClipPosition,
                                ItemClipPosition buildupItemClipPosition) throws NoSuchItemTypeException;

    GameInfo loadGameInfoLight();
}
