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

package com.btxtech.game.jsre.client;

import com.btxtech.game.jsre.client.action.ActionHandler;
import com.btxtech.game.jsre.client.bot.ClientBotService;
import com.btxtech.game.jsre.client.cockpit.CockpitMode;
import com.btxtech.game.jsre.client.cockpit.SelectionHandler;
import com.btxtech.game.jsre.client.item.ItemContainer;

/**
 * User: beat
 * Date: 11.12.2010
 * Time: 12:26:00
 */
public class GameCommon {
    public static void clearGame() {
        CockpitMode.getInstance().reset();
        ClientBotService.getInstance().clear();
        ActionHandler.getInstance().clear();
        ItemContainer.getInstance().clear();
        SelectionHandler.getInstance().clearSelection();
        ClientBase.getInstance().cleanup();
    }

    
}
