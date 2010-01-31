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
import com.btxtech.game.jsre.client.ClientSyncItemView;
import com.btxtech.game.jsre.client.ClientSyncResourceItemView;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.DepositResourceListener;
import com.btxtech.game.jsre.client.Game;
import com.btxtech.game.jsre.client.InfoPanel;
import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.cockpit.Group;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.utg.SpeechBubble;
import com.btxtech.game.jsre.client.utg.ClientUserTracker;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.FactoryCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.MoneyCollectCommand;
import com.google.gwt.user.client.ui.Widget;
import java.util.Collection;
import java.util.Map;

/**
 * User: beat
 * Date: 26.01.2010
 * Time: 22:20:04
 */
public class CollectMission extends Mission implements DepositResourceListener {

    enum Task {
        WAITING_SELECTION_FACTORY,
        WAITING_HARVESTER_CLICK,
        WAITING_CREATE_HARVESTER,
        WAITING_SELECT_HARVESTER,
        WAITING_CREATE_MONEY,
        WAITING_COLLECT_COMMAND,
        WAITING_COLLECT,
        FINISHED
    }

    private SpeechBubble speechBubble;
    private SpeechBubble speechBubble2;
    private ClientSyncBaseItemView factory;
    private ClientSyncBaseItemView harvester;
    private Task task;
    private long lastAction;

    public CollectMission() {
        super("CollectMission");
    }

    public void start() throws MissionAportedException {
        Collection<ClientSyncBaseItemView> items = ItemContainer.getInstance().getOwnItems();

        factory = null;
        for (ClientSyncBaseItemView clientSyncBaseItemView : items) {
            if (clientSyncBaseItemView.getSyncBaseItem().hasSyncFactory()) {
                factory = clientSyncBaseItemView;
            }
        }

        if (factory == null) {
            throw new MissionAportedException("No Factory Item found");
        }

        scrollToItem(factory);

        Widget w = InfoPanel.getInstance().getMoney();
        int x = w.getAbsoluteLeft() + w.getOffsetWidth() / 2;
        int y = w.getAbsoluteTop() + w.getOffsetHeight();
        speechBubble = new SpeechBubble(x, y, HtmlConstants.COLLECT_HTML1, true);
        speechBubble2 = new SpeechBubble(factory, HtmlConstants.COLLECT_HTML2, false);
        task = Task.WAITING_SELECTION_FACTORY;
        ClientUserTracker.getInstance().onMissionTask(this, task);
        lastAction = System.currentTimeMillis();
    }

    @Override
    public void onOwnSelectionChanged(Group selectedGroup) throws MissionAportedException {
        if (selectedGroup.isEmpty()) {
            return;
        }
        if (selectedGroup.contains(factory) && task == Task.WAITING_SELECTION_FACTORY) {
            speechBubble.close();
            speechBubble2.close();
            speechBubble2 = null;

            Widget widget = null;
            for (Map.Entry<ItemType, Widget> itemTypeWidgetEntry : Game.cockpitPanel.getBuildupItemPanel().getItemTypesToBuild().entrySet()) {
                if (itemTypeWidgetEntry.getKey() instanceof BaseItemType && ((BaseItemType) itemTypeWidgetEntry.getKey()).getHarvesterType() != null) {
                    widget = itemTypeWidgetEntry.getValue();
                }
            }
            if (widget == null) {
                throw new MissionAportedException("No Harvester found");
            }
            int x = widget.getAbsoluteLeft() + widget.getOffsetWidth() / 2;
            int y = widget.getAbsoluteTop();
            speechBubble = new SpeechBubble(x, y, HtmlConstants.COLLECT_HTML3, true);
            task = Task.WAITING_HARVESTER_CLICK;
            ClientUserTracker.getInstance().onMissionTask(this, task);
            lastAction = System.currentTimeMillis();
        } else if (selectedGroup.contains(harvester) && task == Task.WAITING_SELECT_HARVESTER) {
            speechBubble.close();
            Connection.getInstance().createMissionMoney(harvester.getSyncBaseItem());
            task = Task.WAITING_CREATE_MONEY;
            ClientUserTracker.getInstance().onMissionTask(this, task);
            lastAction = System.currentTimeMillis();
        }
    }

    @Override
    public void onExecuteCommand(SyncBaseItem syncItem, BaseCommand baseCommand) {
        if (baseCommand instanceof FactoryCommand && task == Task.WAITING_HARVESTER_CLICK) {
            lastAction = System.currentTimeMillis();
            task = Task.WAITING_CREATE_HARVESTER;
            ClientUserTracker.getInstance().onMissionTask(this, task);
            speechBubble.close();
            speechBubble = null;
        } else if (baseCommand instanceof MoneyCollectCommand && task == Task.WAITING_COLLECT_COMMAND) {
            task = Task.WAITING_COLLECT;
            ClientUserTracker.getInstance().onMissionTask(this, task);
            speechBubble.close();
            speechBubble = null;
            ClientBase.getInstance().setDepositResourceListener(this);
        }
    }

    @Override
    public void onItemCreated(ClientSyncItemView item) {
        if (item instanceof ClientSyncBaseItemView && task == Task.WAITING_CREATE_HARVESTER) {
            ClientSyncBaseItemView clientSyncBaseItemView = (ClientSyncBaseItemView) item;
            if (clientSyncBaseItemView.isMyOwnProperty() && clientSyncBaseItemView.getSyncBaseItem().hasSyncHarvester()) {
                lastAction = System.currentTimeMillis();
                task = Task.WAITING_SELECT_HARVESTER;
                ClientUserTracker.getInstance().onMissionTask(this, task);
                speechBubble = new SpeechBubble(clientSyncBaseItemView, HtmlConstants.COLLECT_HTML4, false);
                harvester = clientSyncBaseItemView;
            }
        } else if (item instanceof ClientSyncResourceItemView && task == Task.WAITING_CREATE_MONEY) {
            lastAction = System.currentTimeMillis();
            task = Task.WAITING_COLLECT_COMMAND;
            ClientUserTracker.getInstance().onMissionTask(this, task);
            speechBubble = new SpeechBubble(item, HtmlConstants.COLLECT_HTML5, false);
        }
    }

    @Override
    public void onDeposit() {
        if (task == Task.WAITING_COLLECT) {
            ClientBase.getInstance().setDepositResourceListener(null);
            Widget w = InfoPanel.getInstance().getMoney();
            int x = w.getAbsoluteLeft() + w.getOffsetWidth() / 2;
            int y = w.getAbsoluteTop() + w.getOffsetHeight();
            speechBubble = new SpeechBubble(x, y, HtmlConstants.COLLECT_HTML6, true);
            lastAction = System.currentTimeMillis();
            task = Task.FINISHED;
        }
    }

    @Override
    public void blink() {
        if (speechBubble == null) {
            return;
        }
        if (System.currentTimeMillis() > HtmlConstants.WAITING_FOR_BLINK + lastAction) {
            speechBubble.blink();
            if (speechBubble2 != null) {
                speechBubble2.blink();
            }
        }

    }

    @Override
    public boolean isAccomplished() {
        return task == Task.FINISHED;
    }

    @Override
    public long getAccomplishedTimeStamp() {
        return lastAction;
    }

    @Override
    public void close() {
        if (speechBubble != null) {
            speechBubble.close();
        }
    }
}