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
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.market.impl.UserItemTypeAccess;

import java.util.Collection;
import java.util.List;

/**
 * User: beat
 * Date: 18.12.2009
 * Time: 21:07:42
 */
public interface ServerMarketService extends ItemTypeAccess {
    public Collection<Integer> getAllowedItemTypes();

    List<MarketEntry> getMarketEntries(MarketCategory marketCategory);

    UserItemTypeAccess getUserItemTypeAccess();

    void increaseXp(Base actorBase, SyncBaseItem killedItem);

    void increaseXp(Base base, int deltaXp);

    int getXp();

    void buy(MarketEntry marketEntry);

    XpSettings getXpPointSettings();

    void saveXpPointSettings(XpSettings xpSettings);

    List<MarketCategory> getUsedMarketCategories();

    List<MarketCategory> getMarketCategories();

    List<MarketFunction> getMarketFunctions();

    CrudRootServiceHelper<MarketCategory> getCrudMarketCategoryService();

    CrudRootServiceHelper<MarketFunction> getCrudMarketFunctionService();

    CrudRootServiceHelper<MarketEntry> getCrudMarketEntryService();
}
