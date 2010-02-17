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

package com.btxtech.game.services.itemTypeAccess;

import com.btxtech.game.jsre.common.gameengine.services.itemTypeAccess.ItemTypeAccess;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.services.itemTypeAccess.impl.UserItemTypeAccess;
import com.btxtech.game.services.base.Base;
import java.util.Collection;
import java.util.ArrayList;

/**
 * User: beat
 * Date: 18.12.2009
 * Time: 21:07:42
 */
public interface ServerItemTypeAccessService extends ItemTypeAccess {
    public Collection<Integer> getAllowedItemTypes();

    Collection<ItemTypeAccessEntry> getItemTypeAccessEntries();

    void createNewItemTypeAccessEntry();

    void saveItemTypeAccessEntries(ArrayList<ItemTypeAccessEntry> itemTypeAccessEntries);

    void delteItemTypeAccessEntry(ItemTypeAccessEntry itemTypeAccessEntry);

    UserItemTypeAccess getUserItemTypeAccess();

    void increaseXp(Base actorBase, SyncBaseItem syncBaseItem);

    void clearSession();

    int getXp();

    void buy(ItemTypeAccessEntry itemTypeAccessEntry);

    XpSettings getXpPointSettings();

    void saveXpPointSettings(XpSettings xpSettings);
}
