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
import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.utg.missions.tasks.CreateCommandTask;
import com.btxtech.game.jsre.client.utg.missions.tasks.SelectProtagonistTask;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.FactoryCommand;
import java.util.Collection;

/**
 * User: beat
 * Date: 26.01.2010
 * Time: 22:20:04
 */
public class CreateJeepMission extends Mission {
    public CreateJeepMission() throws NoSuchItemTypeException {
        super("Create Jeep", HtmlConstants.CREATE_JEEP_HTML3);
        addTask(new SelectProtagonistTask(HtmlConstants.CREATE_JEEP_HTML1));
        addTask(new CreateCommandTask(HtmlConstants.CREATE_JEEP_HTML2, Constants.JEEP, FactoryCommand.class));
    }

    @Override
    public boolean init() {
        Collection<ClientSyncBaseItemView> items = ItemContainer.getInstance().getOwnItems();
        ClientSyncBaseItemView factory = null;
        for (ClientSyncBaseItemView itemView : items) {
            String name = itemView.getSyncBaseItem().getBaseItemType().getName();
            if (name.equals(Constants.FACTORY)) {
                factory = itemView;
            }
            if (name.equals(Constants.JEEP)) {
                return false;
            }
        }
        if (factory != null) {
            setProtagonist(factory);
            return true;
        } else {
            return false;
        }
    }

}