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

package com.btxtech.game.jsre.common.utg.config;

import java.util.ArrayList;
import java.util.List;

/**
 * User: beat
 * Date: 22.11.2010
 * Time: 19:46:54
 */
public enum CockpitWidgetEnum {
    SCROLL_HOME_BUTTON(true, false),
    OPTION_BUTTON(true, false),
    SELL_BUTTON(true, false),
    LEVEL_BUTTON(true, false),
    MONEY_FIELD(false, false),
    BUILDUP_ITEM(false, true),
    RADAR_PANEL(false, false);

    private boolean itemTypeNeeded;
    private boolean button;

    CockpitWidgetEnum(boolean isButton, boolean itemTypeNeeded) {
        this.itemTypeNeeded = itemTypeNeeded;
        button = isButton;
    }

    public boolean isItemTypeNeeded() {
        return itemTypeNeeded;
    }

    public boolean isButton() {
        return button;
    }

    public static List<CockpitWidgetEnum> getButtons() {
        ArrayList<CockpitWidgetEnum> result = new ArrayList<CockpitWidgetEnum>();
        for (CockpitWidgetEnum cockpitWidgetEnum : values()) {
            if (cockpitWidgetEnum.isButton()) {
                result.add(cockpitWidgetEnum);
            }
        }
        return result;
    }
}
