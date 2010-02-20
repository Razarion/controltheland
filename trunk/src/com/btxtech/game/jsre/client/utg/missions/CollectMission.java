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
import com.btxtech.game.jsre.client.utg.missions.tasks.CreateTargetTask;
import com.btxtech.game.jsre.client.utg.missions.tasks.ItemCommandTask;
import com.btxtech.game.jsre.client.utg.missions.tasks.SelectProtagonistAndMoneyTask;
import com.btxtech.game.jsre.client.utg.missions.tasks.SelectProtagonistTask;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.FactoryCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.MoneyCollectCommand;
import java.util.Collection;

/**
 * User: beat
 * Date: 26.01.2010
 * Time: 22:20:04
 */
public class CollectMission extends Mission {
    public CollectMission() throws NoSuchItemTypeException {
        super("CollectMission", HtmlConstants.COLLECT_HTML6);
        addTask(new SelectProtagonistAndMoneyTask(HtmlConstants.COLLECT_HTML2));
        addTask(new CreateCommandTask(HtmlConstants.COLLECT_HTML4, Constants.HARVESTER, FactoryCommand.class));
        addTask(new SelectProtagonistTask(HtmlConstants.COLLECT_HTML4));
        addTask(new CreateTargetTask(Constants.MONEY));
        addTask(new ItemCommandTask(HtmlConstants.COLLECT_HTML5, Constants.MONEY, MoneyCollectCommand.class, false));
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
            if (name.equals(Constants.HARVESTER)) {
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