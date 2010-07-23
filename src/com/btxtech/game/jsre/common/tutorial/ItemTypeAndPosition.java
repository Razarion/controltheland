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

package com.btxtech.game.jsre.common.tutorial;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import java.io.Serializable;

/**
 * User: beat
 * Date: 17.07.2010
 * Time: 17:47:19
 */
public class ItemTypeAndPosition implements Serializable {
    private SimpleBase base;
    private int id;
    private int itemTypeId;
    private Index position;

    /**
     * Used by GWT
     */
    public ItemTypeAndPosition() {
    }

    public ItemTypeAndPosition(SimpleBase base, int id, int itemTypeId, Index position) {
        this.base = base;
        this.id = id;
        this.itemTypeId = itemTypeId;
        this.position = position;
    }

    public int getItemTypeId() {
        return itemTypeId;
    }

    public Index getPosition() {
        return position;
    }

    public SimpleBase getBase() {
        return base;
    }

    public int getId() {
        return id;
    }
}
