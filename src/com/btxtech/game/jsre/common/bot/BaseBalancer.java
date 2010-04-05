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

package com.btxtech.game.jsre.common.bot;

import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.services.Services;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import java.util.List;
import java.util.Map;

/**
 * User: beat
 * Date: 14.03.2010
 * Time: 17:35:29
 */
public class BaseBalancer {
    private Services services;
    private SimpleBase simpleBase;
    private BotLevel botLevel;
    private BaseExecutor baseExecutor;

    public BaseBalancer(BotLevel botLevel, Services services, SimpleBase simpleBase) {
        this.botLevel = botLevel;
        this.services = services;
        this.simpleBase = simpleBase;
        baseExecutor = new BaseExecutor(services, simpleBase);
    }

    public void setBotLevel(BotLevel botLevel) {
        this.botLevel = botLevel;
    }

    public void doBalance() throws NoSuchItemTypeException {
        Map<BaseItemType, List<SyncBaseItem>> items = services.getItemService().getItems4Base(simpleBase);
        for (ItemTypeBalance itemTypeBalance : botLevel.getItemTypeBalances()) {
            BaseItemType itemTypeToBalance = (BaseItemType) services.getItemService().getItemType(itemTypeBalance.getItemTypeName());
            List<SyncBaseItem> syncBaseItems = items.get(itemTypeToBalance);
            if (syncBaseItems == null || syncBaseItems.size() < itemTypeBalance.getCount()) {
                baseExecutor.doBalanceItemType(items, itemTypeToBalance, null);
                return;
            }
        }
    }

    public BaseExecutor getBaseExecutor() {
        return baseExecutor;
    }
}
