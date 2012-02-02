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

package com.btxtech.game.services.utg.impl;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncResourceItem;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.collision.CollisionService;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.utg.UserGuidanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * User: beat
 * Date: 29.01.2010
 * Time: 22:04:02
 */
@Component("userGuidanceService")
public class UserGuidanceServiceImpl implements UserGuidanceService {
    public static final int TARGET_MAX_RANGE = 300;
    public static final int TARGET_MIN_RANGE = 150;
    public static final int MISSON_MONEY = 5000;
    @Autowired
    private BaseService baseService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private CollisionService collisionService;


    @Override
    public void createMissionTraget(Id attacker) throws NoSuchItemTypeException, ItemDoesNotExistException {
        ItemType targetItemType = itemService.getItemType("Jeep");
        SyncItem attackerItem = itemService.getItem(attacker);
        Index targetPos = collisionService.getFreeRandomPositionInRect(targetItemType, attackerItem, TARGET_MIN_RANGE, TARGET_MAX_RANGE);
        SyncBaseItem syncBaseItem = (SyncBaseItem) itemService.createSyncObject(targetItemType, targetPos, null, baseService.getDummyBase(), 0);
        syncBaseItem.setBuild(true);
        syncBaseItem.setFullHealth();
    }

    @Override
    public void createMissionMoney(Id harvester) throws NoSuchItemTypeException, ItemDoesNotExistException {
        ItemType moneyItemType = itemService.getItemType("Money");
        SyncItem attackerItem = itemService.getItem(harvester);
        Index targetPos = collisionService.getFreeRandomPositionInRect(moneyItemType, attackerItem, TARGET_MIN_RANGE, TARGET_MAX_RANGE);
        SyncResourceItem syncBaseItem = (SyncResourceItem) itemService.createSyncObject(moneyItemType, targetPos, null, null, 0);
        syncBaseItem.setAmount(MISSON_MONEY);
        syncBaseItem.setMissionMoney(true);
    }
}