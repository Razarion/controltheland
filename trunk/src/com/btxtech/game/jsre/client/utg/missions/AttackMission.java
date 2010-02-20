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
import com.btxtech.game.jsre.client.utg.missions.tasks.CreateTargetTask;
import com.btxtech.game.jsre.client.utg.missions.tasks.ItemCommandTask;
import com.btxtech.game.jsre.client.utg.missions.tasks.SelectProtagonistTask;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.AttackCommand;
import java.util.Collection;

/**
 * User: beat
 * Date: 26.01.2010
 * Time: 22:20:04
 */
public class AttackMission extends Mission {
    public AttackMission() throws NoSuchItemTypeException {
        super("AttackMission", HtmlConstants.ATTACK_HTML3);
        addTask(new SelectProtagonistTask(HtmlConstants.ATTACK_HTML1));
        addTask(new CreateTargetTask(Constants.JEEP));
        addTask(new ItemCommandTask(HtmlConstants.ATTACK_HTML2, Constants.JEEP, AttackCommand.class, true));
    }

    @Override
    public boolean init() {
        Collection<ClientSyncBaseItemView> items = ItemContainer.getInstance().getOwnItems();

        ClientSyncBaseItemView jeep = null;
        for (ClientSyncBaseItemView clientSyncBaseItemView : items) {
            if (clientSyncBaseItemView.getSyncBaseItem().hasSyncWaepon() && clientSyncBaseItemView.getSyncBaseItem().hasSyncMovable()) {
                jeep = clientSyncBaseItemView;
            }
        }

        if (jeep != null) {
            setProtagonist(jeep);
            return true;
        } else {
            return false;
        }
    }
}