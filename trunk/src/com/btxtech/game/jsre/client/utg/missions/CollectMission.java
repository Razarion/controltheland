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
import com.btxtech.game.jsre.client.InfoPanel;
import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.utg.SpeechBubble;
import com.btxtech.game.jsre.client.utg.missions.tasks.CollectResourceTask;
import com.btxtech.game.jsre.client.utg.missions.tasks.CreateCommandTaskAndMoneyTask;
import com.btxtech.game.jsre.client.utg.missions.tasks.CreateTargetTask;
import com.btxtech.game.jsre.client.utg.missions.tasks.SelectProtagonistTask;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.FactoryCommand;
import com.google.gwt.user.client.ui.Widget;
import java.util.Collection;

/**
 * User: beat
 * Date: 26.01.2010
 * Time: 22:20:04
 */
public class CollectMission extends Mission {
    private SpeechBubble completedBubble;

    public CollectMission() throws NoSuchItemTypeException {
        super("CollectMission", null);
        addTask(new SelectProtagonistTask(HtmlConstants.COLLECT_HTML2));
        addTask(new CreateCommandTaskAndMoneyTask(Constants.HARVESTER, FactoryCommand.class));
        addTask(new SelectProtagonistTask(HtmlConstants.COLLECT_HTML4));
        addTask(new CreateTargetTask(Constants.MONEY));
        addTask(new CollectResourceTask(HtmlConstants.COLLECT_HTML5));
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

    @Override
    protected void showCompletedMessage() {
        super.showCompletedMessage();
        Widget w = InfoPanel.getInstance().getMoney();
        int x = w.getAbsoluteLeft() + w.getOffsetWidth() / 2;
        int y = w.getAbsoluteTop() + w.getOffsetHeight() / 2;
        completedBubble = new SpeechBubble(x, y, HtmlConstants.COLLECT_HTML6, true);
    }

    @Override
    public void close() {
        super.close();
        if (completedBubble != null) {
            completedBubble.close();
            completedBubble = null;
        }
    }
}