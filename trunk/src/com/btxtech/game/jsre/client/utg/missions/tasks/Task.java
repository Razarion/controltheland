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

package com.btxtech.game.jsre.client.utg.missions.tasks;

import com.btxtech.game.jsre.client.utg.missions.Mission;
import com.btxtech.game.jsre.client.utg.missions.MissionAportedException;
import com.btxtech.game.jsre.client.utg.SpeechBubble;
import com.btxtech.game.jsre.client.cockpit.Group;
import com.btxtech.game.jsre.client.ClientSyncItemView;
import com.btxtech.game.jsre.client.ClientSyncBaseItemView;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;

/**
 * User: beat
 * Date: 17.02.2010
 * Time: 20:03:23
 */
abstract public class Task {
    private String html;
    private Mission mission;
    private SpeechBubble speechBubble;

    public Task(String html) {
        this.html = html;
    }

    abstract public String getName();

    abstract public void run() throws MissionAportedException;

    public void closeBubble() {
        if (speechBubble != null) {
            speechBubble.close();
            speechBubble = null;
        }
    }

    public void blink() {
        if (speechBubble != null) {
            speechBubble.blink();
        }
    }

    protected void setSpeechBubble(SpeechBubble speechBubble) {
        this.speechBubble = speechBubble;
    }

    public Mission getMission() {
        return mission;
    }

    public void setMission(Mission mission) {
        this.mission = mission;
    }

    public String getHtml() {
        return html;
    }

    protected void activateNextTask() {
        getMission().activateNextTask();
    }

    public void onOwnSelectionChanged(Group selectedGroup) {
    }

    public void onSyncItemDeactivated(SyncBaseItem syncBaseItem) {
    }

    public void onExecuteCommand(SyncBaseItem syncItem, BaseCommand baseCommand) {
    }

    public void onItemCreated(ClientSyncItemView item) {
    }

    public void onItemBuilt(ClientSyncBaseItemView clientSyncItemView) {
    }

    public boolean canSkip() {
        return false;
    }
}
