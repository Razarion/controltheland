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

package com.btxtech.game.services.utg;

import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.services.item.itemType.DbBaseItemType;

/**
 * User: beat
 * Date: 28.12.2010
 * Time: 23:32:02
 */
public class DbScope {
    private int money;
    private Rectangle startRectangle;
    private int startItemFreeRange;
    private double itemSellFactor;
    private boolean createRealBase;


    public DbBaseItemType getStartItem() {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    public int getMoney() {
        return money;
    }

    public Rectangle getStartRectangle() {
        return startRectangle;
    }

    public int getStartItemFreeRange() {
        return startItemFreeRange;
    }

    public double getItemSellFactor() {
        return itemSellFactor;
    }

    public boolean isCreateRealBase() {
        return createRealBase;
    }
}
