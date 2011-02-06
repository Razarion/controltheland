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

import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.services.base.HouseSpaceExceededException;
import com.btxtech.game.jsre.common.gameengine.services.base.ItemLimitExceededException;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;

/**
 * User: beat
 * Date: 19.09.2010
 * Time: 23:41:26
 */
public interface BotItemFactory {
    /**
     *
     * @param toBeBuilt
     * @return The item which will build the item
     * @throws ItemLimitExceededException
     * @throws HouseSpaceExceededException
     * @throws NoSuchItemTypeException
     */
    BotSyncBaseItem createItem(BaseItemType toBeBuilt) throws ItemLimitExceededException, HouseSpaceExceededException, NoSuchItemTypeException;
}
