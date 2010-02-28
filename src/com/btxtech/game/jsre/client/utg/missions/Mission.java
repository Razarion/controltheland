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
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.cockpit.Group;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.client.utg.ClientUserTracker;
import com.btxtech.game.jsre.client.utg.SpeechBubble;
import com.btxtech.game.jsre.client.utg.missions.tasks.Task;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import java.util.ArrayList;

/**
 * User: beat
 * Date: 26.01.2010
 * Time: 19:23:00
 */
public abstract class Mission {
    private String name;
    private ArrayList<Task> tasks = new ArrayList<Task>();
    private Task currentTask;
    private long lastTaskChange;
    private String htmlCompleted;
    private ClientSyncBaseItemView protagonist;
    private SpeechBubble completedBubble;
    private boolean autoTaskChange = false;

    protected Mission(String name, String htmlCompleted) {
        this.name = name;
        this.htmlCompleted = htmlCompleted;
    }

    protected void addTask(Task task) {
        tasks.add(task);
        task.setMission(this);
    }

    public void setAutoTaskChange(boolean autoTaskChange) {
        this.autoTaskChange = autoTaskChange;
    }

    public void start() throws MissionAportedException {
        activateNextTask();
    }

    public ClientSyncBaseItemView getProtagonist() {
        return protagonist;
    }

    public void setProtagonist(ClientSyncBaseItemView protagonist) {
        this.protagonist = protagonist;
        scrollToItem(protagonist);
    }

    public void activateNextTask() {
        try {
            startNextTask();
        } catch (MissionAportedException e) {
            GwtCommon.handleException(e);
            aportMission();
        }
    }

    private void startNextTask() throws MissionAportedException {
        closeTask();
        lastTaskChange = System.currentTimeMillis();
        if (tasks.isEmpty()) {
            currentTask = null;
            showCompletedMessage();
        } else {
            currentTask = tasks.remove(0);
            currentTask.run();
            ClientUserTracker.getInstance().onMissionTask(this, currentTask.getName());
        }
    }

    protected void showCompletedMessage() {
        if (protagonist != null && htmlCompleted != null) {
            completedBubble = new SpeechBubble(protagonist, htmlCompleted, false);
        }
    }

    public boolean isAccomplished() {
        return currentTask == null && tasks.isEmpty();
    }

    public void tick() {
        if (currentTask == null) {
            return;
        }
        if (System.currentTimeMillis() > HtmlConstants.WAITING_FOR_BLINK + lastTaskChange) {
            currentTask.blink();
        }
        if (autoTaskChange && System.currentTimeMillis() > HtmlConstants.AUTO_TASK_CHANGE + lastTaskChange) {
            activateNextTask();
        }
    }

    public long getLastTaskChangeTime() {
        return lastTaskChange;
    }

    public void close() {
        closeTask();
        if (completedBubble != null) {
            completedBubble.close();
            completedBubble = null;
        }
    }


    private void closeTask() {
        if (currentTask != null) {
            currentTask.closeBubble();
        }
    }

    public void aportMission() {
        closeTask();
        currentTask = null;
        tasks.clear();
    }

    public void onOwnSelectionChanged(Group selectedGroup) {
        if (currentTask != null && selectedGroup.getFirst().equals(protagonist)) {
            currentTask.onOwnSelectionChanged(selectedGroup);
        }
    }

    public void onSyncItemDeactivated(SyncBaseItem syncBaseItem) {
        if (currentTask != null && protagonist != null && syncBaseItem.equals(protagonist.getSyncBaseItem())) {
            currentTask.onSyncItemDeactivated(syncBaseItem);
        }
    }

    public void onExecuteCommand(SyncBaseItem syncItem, BaseCommand baseCommand) {
        if (currentTask != null && protagonist != null && syncItem.equals(protagonist.getSyncBaseItem())) {
            currentTask.onExecuteCommand(syncItem, baseCommand);
        }
    }

    public void onItemCreated(ClientSyncItemView item) {
        if (currentTask != null) {
            currentTask.onItemCreated(item);
        }
    }

    public void onItemDeleted(ClientSyncItemView item) {
    }

    public void onItemBuilt(ClientSyncBaseItemView clientSyncItemView) {
        if (currentTask != null) {
            currentTask.onItemBuilt(clientSyncItemView);
        }
    }

    protected void scrollToItem(ClientSyncItemView item) {
        TerrainView.getInstance().moveToMiddle(item);
    }

    public String getName() {
        return name;
    }

    /**
     * Init the mission
     *
     * @return true if the mission shall be started. False if the mission can be skipped
     */
    public boolean init() {
        return true;
    }
}
