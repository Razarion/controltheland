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
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.cockpit.Group;
import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.utg.ClientUserTracker;
import com.btxtech.game.jsre.client.utg.SpeechBubble;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.AttackCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import java.util.Collection;

/**
 * User: beat
 * Date: 26.01.2010
 * Time: 22:20:04
 */
public class AttackMission extends Mission {
    enum Task {
        WAITING_SELECTION,
        WAITING_TARGET,
        WAITING_ATTACK,
        WAITING_KILL,
        FINISHED
    }

    private SpeechBubble speechBubble;
    private ClientSyncBaseItemView jeep;
    private Task task;
    private long lastAction;
    private ClientSyncBaseItemView target;

    public AttackMission() {
        super("AttackMission");
    }

    public void start() throws MissionAportedException {
        Collection<ClientSyncBaseItemView> items = ItemContainer.getInstance().getOwnItems();

        jeep = null;
        for (ClientSyncBaseItemView clientSyncBaseItemView : items) {
            if (clientSyncBaseItemView.getSyncBaseItem().hasSyncWaepon() && clientSyncBaseItemView.getSyncBaseItem().hasSyncMovable()) {
                jeep = clientSyncBaseItemView;
            }
        }

        if (jeep == null) {
            throw new MissionAportedException("No Attcking Item found");
        }

        scrollToItem(jeep);

        speechBubble = new SpeechBubble(jeep, HtmlConstants.ATTACK_HTML1, false);
        task = Task.WAITING_SELECTION;
        lastAction = System.currentTimeMillis();
        ClientUserTracker.getInstance().onMissionTask(this, task);
    }

    public void onOwnSelectionChanged(Group selectedGroup) {
        if (task == Task.WAITING_SELECTION && selectedGroup.canAttack()) {
            lastAction = System.currentTimeMillis();
            task = Task.WAITING_TARGET;
            ClientUserTracker.getInstance().onMissionTask(this, task);
            speechBubble.close();
            Connection.getInstance().createMissionTraget(jeep.getSyncBaseItem());
        }
    }

    @Override
    public void onItemCreated(ClientSyncItemView item) {
        if (item instanceof ClientSyncBaseItemView) {
            ClientSyncBaseItemView baseItemView = (ClientSyncBaseItemView) item;
            if (baseItemView.getSyncBaseItem().getBase().getName().equals(Constants.DUMMY_BASE_NAME)) {
                target = baseItemView;
                task = Task.WAITING_ATTACK;
                ClientUserTracker.getInstance().onMissionTask(this, task);
                lastAction = System.currentTimeMillis();
                speechBubble = new SpeechBubble(baseItemView, HtmlConstants.ATTACK_HTML2, false);
            }
        }
    }

    @Override
    public void onExecuteCommand(SyncBaseItem syncItem, BaseCommand baseCommand) {
        if (baseCommand instanceof AttackCommand && syncItem.equals(jeep.getSyncItem()) && task == Task.WAITING_ATTACK) {
            task = Task.WAITING_KILL;
            ClientUserTracker.getInstance().onMissionTask(this, task);
            lastAction = System.currentTimeMillis();
            speechBubble.close();
        }
    }

    @Override
    public void onSyncItemDeactivated(SyncBaseItem syncBaseItem) {
        if (jeep.getSyncItem().equals(syncBaseItem) && task == Task.WAITING_KILL) {
            lastAction = System.currentTimeMillis();
            speechBubble = new SpeechBubble(jeep, HtmlConstants.ATTACK_HTML3, false);
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