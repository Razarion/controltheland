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

/**
 * User: beat
 * Date: 22.11.2010
 * Time: 19:46:54
 */
public enum CockpitWidgetEnum {
    SCROLL_HOME_BUTTON(false),
    OPTION_BUTTON(false),
    SELL_BUTTON(false),
    MISSION_BUTTON(false),
    BUILDUP_ITEM(true);
    private boolean itemTypeNeeded;

    CockpitWidgetEnum(boolean itemTypeNeeded) {
        this.itemTypeNeeded = itemTypeNeeded;
    }

    public boolean isItemTypeNeeded() {
        return itemTypeNeeded;
    }
}
