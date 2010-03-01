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

package com.btxtech.game.jsre.common.ai;

/**
 * User: beat
 * Date: 01.03.2010
 * Time: 18:22:54
 */
public class ItemTypeBalance {
    private String itemTypeName;
    private int count;

    public ItemTypeBalance(String itemTypeName, int count) {
        this.itemTypeName = itemTypeName;
        this.count = count;
    }

    public String getItemTypeName() {
        return itemTypeName;
    }

    public int getCount() {
        return count;
    }
}
