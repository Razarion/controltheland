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

package com.btxtech.game.jsre.client.utg;

import com.btxtech.game.jsre.client.ClientSyncItem;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.StartupProbe;
import com.btxtech.game.jsre.client.cockpit.Group;
import com.btxtech.game.jsre.client.cockpit.SelectionHandler;
import com.btxtech.game.jsre.client.cockpit.SelectionListener;
import com.btxtech.game.jsre.client.simulation.Step;
import com.btxtech.game.jsre.client.simulation.Task;
import com.btxtech.game.jsre.client.terrain.MapWindow;
import com.btxtech.game.jsre.common.EventTrackingItem;
import com.btxtech.game.jsre.common.EventTrackingStart;
import com.btxtech.game.jsre.common.SelectionTrackingItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.btxtech.game.jsre.common.tutorial.TutorialConfig;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import java.util.ArrayList;
import java.util.List;

/**
 * User: beat
 * Date: 13.01.2010
 * Time: 15:12:08
 */
public class ClientUserTracker implements SelectionListener {
    private static final int SEND_TIMEOUT = 1000 * 30;
    private static final ClientUserTracker INSTANCE = new ClientUserTracker();
    private List<EventTrackingItem> eventTrackingItems = new ArrayList<EventTrackingItem>();
    private List<SelectionTrackingItem> selectionTrackingItems = new ArrayList<SelectionTrackingItem>();
    private List<BaseCommand> baseCommands = new ArrayList<BaseCommand>();

    public static ClientUserTracker getInstance() {
        return INSTANCE;
    }

    /**
     * Singleton
     */
    private ClientUserTracker() {
        Window.addCloseHandler(new CloseHandler<Window>() {
            @Override
            public void onClose(CloseEvent<Window> windowCloseEvent) {
                sendEventTrackerItems();
                long time = System.currentTimeMillis();
                Connection.getInstance().sendCloseWindow(time - StartupProbe.getInstance().getRunningTimeStamp(), time);
            }
        });
    }

    public void onTutorialFinished(long duration, long clientTimeStamp, Runnable runnable) {
        Connection.getInstance().sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, null, null, duration, clientTimeStamp, runnable);
    }

    public void onTutorialFailed(long duration, long clientTimeStamp, Runnable runnable) {
        Connection.getInstance().sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL_FAILED, null, null, duration, clientTimeStamp, runnable);
    }

    public void onTaskFinished(Task task, long duration, long clientTimeStamp) {
        Connection.getInstance().sendTutorialProgress(TutorialConfig.TYPE.TASK, task.getTaskConfig().getName(), null, duration, clientTimeStamp, null);
    }

    public void onStepFinished(Step step, Task task, long duration, long clientTimeStamp) {
        Connection.getInstance().sendTutorialProgress(TutorialConfig.TYPE.STEP, step.getStepConfig().getName(), task.getTaskConfig().getName(), duration, clientTimeStamp, null);
    }

    public void startEventTracking() {
        Timer timer = new Timer() {
            @Override
            public void run() {
                sendEventTrackerItems();
            }
        };
        timer.scheduleRepeating(SEND_TIMEOUT);
        SelectionHandler.getInstance().addSelectionListener(this);
        MapWindow.getInstance().setTrackingEvents();
        Connection.getInstance().sendEventTrackingStart(new EventTrackingStart(Window.getClientWidth(), Window.getClientHeight()));
    }

    public void addEventTrackingItem(int xPos, int yPos, int eventType) {
        eventTrackingItems.add(new EventTrackingItem(xPos, yPos, eventType));
    }

    public void onExecuteCommand(BaseCommand baseCommand) {
        baseCommands.add(baseCommand);
    }

    private void sendEventTrackerItems() {
        if (eventTrackingItems.isEmpty() && baseCommands.isEmpty() && selectionTrackingItems.isEmpty()) {
            return;
        }
        Connection.getInstance().sendEventTrackerItems(eventTrackingItems, baseCommands, selectionTrackingItems);
        eventTrackingItems = new ArrayList<EventTrackingItem>();
        baseCommands = new ArrayList<BaseCommand>();
        selectionTrackingItems = new ArrayList<SelectionTrackingItem>();
    }

    @Override
    public void onTargetSelectionChanged(ClientSyncItem selection) {
        selectionTrackingItems.add(new SelectionTrackingItem(selection));
    }

    @Override
    public void onSelectionCleared() {
        selectionTrackingItems.add(new SelectionTrackingItem());
    }

    @Override
    public void onOwnSelectionChanged(Group selectedGroup) {
        selectionTrackingItems.add(new SelectionTrackingItem(selectedGroup));
    }
}
