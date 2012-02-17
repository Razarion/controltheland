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

package com.btxtech.game.jsre.common.gameengine.services.base;

import com.btxtech.game.jsre.client.common.LevelScope;
import com.btxtech.game.jsre.client.common.NotYourBaseException;
import com.btxtech.game.jsre.common.InsufficientFundsException;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotConfig;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;

import java.util.Collection;

/**
 * User: beat
 * Date: 22.11.2009
 * Time: 14:02:40
 */
public interface AbstractBaseService {
    void depositResource(double price, SimpleBase simpleBase);

    void withdrawalMoney(double price, SimpleBase simpleBase) throws InsufficientFundsException;

    String getBaseName(SimpleBase simpleBase);

    boolean isBot(SimpleBase simpleBase);

    boolean isAbandoned(SimpleBase simpleBase);

    Collection<BaseAttributes> getAllBaseAttributes();

    int getHouseSpace(SimpleBase simpleBase);

    int getItemCount(SimpleBase simpleBase);

    int getItemCount(SimpleBase simpleBase, int itemTypeId) throws NoSuchItemTypeException;

    void checkItemLimit4ItemAdding(BaseItemType newItemType, SimpleBase simpleBase) throws ItemLimitExceededException, HouseSpaceExceededException, NoSuchItemTypeException;

    boolean isLevelLimitation4ItemTypeExceeded(BaseItemType newItemType, SimpleBase simpleBase) throws NoSuchItemTypeException;

    boolean isHouseSpaceExceeded(SimpleBase simpleBase) throws NoSuchItemTypeException;

    LevelScope getLevel(SimpleBase simpleBase);

    boolean isAlive(SimpleBase base);

    SimpleBase createBotBase(BotConfig botConfig);

    Collection<SyncBaseItem> getItems(SimpleBase simpleBase);

    void checkBaseAccess(SyncBaseItem syncBaseItem) throws NotYourBaseException;

    void sendAccountBaseUpdate(SimpleBase simpleBase);
}