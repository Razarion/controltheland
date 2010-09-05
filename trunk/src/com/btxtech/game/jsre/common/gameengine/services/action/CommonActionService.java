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

package com.btxtech.game.jsre.common.gameengine.services.action;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.InsufficientFundsException;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncResourceItem;

/**
 * User: beat
 * Date: 14.03.2010
 * Time: 19:48:28
 */
public interface CommonActionService {
    void move(SyncBaseItem syncItem, Index destination);

    void buildFactory(SyncBaseItem builder, Index position, BaseItemType itemTypeToBuild);

    void build(SyncBaseItem builder, BaseItemType itemTypeToBuild);

    void collect(SyncBaseItem harvester, SyncResourceItem moneyItem);

    void attack(SyncBaseItem attacker, SyncBaseItem target);

    void upgrade(SyncBaseItem item) throws InsufficientFundsException;

    void syncItemActivated(SyncBaseItem syncBaseItem);
}
