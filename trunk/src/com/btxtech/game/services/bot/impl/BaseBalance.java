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

package com.btxtech.game.services.bot.impl;

import com.btxtech.game.jsre.common.bot.BaseExecutor;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import java.util.ArrayList;
import java.util.List;

/**
 * User: beat
 * Date: 04.04.2010
 * Time: 23:50:53
 */
public class BaseBalance {
    private ArrayList<ItemPosAndType> itemPosAndTypes = new ArrayList<ItemPosAndType>();
    private BaseExecutor baseExecutor;

    public BaseBalance(BaseExecutor baseExecutor) {
        this.baseExecutor = baseExecutor;
    }

    public void addItemPosAndType(ItemPosAndType itemPosAndType) {
        itemPosAndTypes.add(itemPosAndType);
    }

    public void addSyncBaseItems(List<SyncBaseItem> newSyncBaseItem) {
        List<ItemPosAndType> dead = getDeadItems();
        for (SyncBaseItem newItem : newSyncBaseItem) {
            for (ItemPosAndType deadType : dead) {
                if (deadType.getBaseItemType().equals(newItem.getItemType())) {
                    if (newItem.hasSyncMovable()) {
                        deadType.setSyncBaseItem(newItem);
                        dead.remove(deadType);
                        baseExecutor.doMove(newItem, deadType.getPosition());
                        break;
                    } else {
                       if(deadType.getPosition().equals(newItem.getPosition())) {
                           deadType.setSyncBaseItem(newItem);
                           dead.remove(deadType);
                           break;
                       }
                    }
                }
            }
        }
    }

    public boolean isEmpty() {
        return itemPosAndTypes.isEmpty();
    }

    public List<ItemPosAndType> getDeadItems() {
        ArrayList<ItemPosAndType> items = new ArrayList<ItemPosAndType>();
        for (ItemPosAndType itemPosAndType : itemPosAndTypes) {
            if (itemPosAndType.isDead()) {
                items.add(itemPosAndType);
            }
        }
        return items;
    }

    public List<SyncBaseItem> getAliveItems() {
        ArrayList<SyncBaseItem> items = new ArrayList<SyncBaseItem>();
        for (ItemPosAndType itemPosAndType : itemPosAndTypes) {
            if (!itemPosAndType.isDead()) {
                items.add(itemPosAndType.getSyncBaseItem());
            }
        }
        return items;
    }
}
