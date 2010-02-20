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

package com.btxtech.game.jsre.client.utg.missions;

import com.btxtech.game.jsre.client.ClientSyncBaseItemView;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.utg.missions.tasks.SelectProtagonistTask;
import com.btxtech.game.jsre.client.utg.missions.tasks.TerrainClickTask;
import java.util.Collection;

/**
 * User: beat
 * Date: 26.01.2010
 * Time: 22:20:04
 */
public class MoveMission extends Mission {
    public MoveMission() {
        super("MoveMission", HtmlConstants.MOVE_HTML3);
        addTask(new SelectProtagonistTask(HtmlConstants.MOVE_HTML1));
        addTask(new TerrainClickTask(HtmlConstants.MOVE_HTML2));
    }

    @Override
    public boolean init() {
        Collection<ClientSyncBaseItemView> items = ItemContainer.getInstance().getOwnItems();
        if (items.size() == 1) {
            ClientSyncBaseItemView builder = items.iterator().next();
            if (builder.getSyncBaseItem().hasSyncBuilder()) {
                setProtagonist(builder);
                return true;
            }
        }
        return false;
    }
}
