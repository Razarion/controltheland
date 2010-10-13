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

package com.btxtech.game.services.market;

import com.btxtech.game.jsre.common.gameengine.services.itemTypeAccess.ItemTypeAccess;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.services.base.Base;
import com.btxtech.game.services.market.impl.UserItemTypeAccess;
import com.btxtech.game.services.user.User;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User: beat
 * Date: 18.12.2009
 * Time: 21:07:42
 */
public interface ServerMarketService extends ItemTypeAccess {
    boolean isAllowed(int itemTypeId, Base base);

    public Collection<Integer> getAllowedItemTypes();

    List<MarketEntry> getItemTypeAccessEntries();

    List<MarketEntry> getMarketEntries(MarketCategory marketCategory);

    void createNewItemTypeAccessEntry();

    void saveItemTypeAccessEntries(ArrayList<MarketEntry> marketEntries);

    void deleteItemTypeAccessEntry(MarketEntry marketEntry);

    UserItemTypeAccess getUserItemTypeAccess();

    UserItemTypeAccess getUserItemTypeAccess(Base base);

    void increaseXp(Base actorBase, SyncBaseItem killedItem);

    void clearSession();

    int getXp();

    void buy(MarketEntry marketEntry);

    XpSettings getXpPointSettings();

    void saveXpPointSettings(XpSettings xpSettings);

    void addMarketCategory();

    void addMarketFunction();

    List<MarketCategory> getUsedMarketCategories();

    List<MarketCategory> getMarketCategories();

    List<MarketFunction> getMarketFunctions();

    void deleteMarketCategory(MarketCategory category);

    void saveMarketCategories(ArrayList<MarketCategory> marketCategories);

    void deleteMarketFunction(MarketFunction marketFunction);

    void saveMarketFunctions(ArrayList<MarketFunction> marketFunctions);

    void setUserItemTypeAccess(User user, UserItemTypeAccess userItemTypeAccess);
}
