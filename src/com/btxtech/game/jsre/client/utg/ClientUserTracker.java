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

import com.btxtech.game.jsre.client.ClientSyncItemView;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.StartupProbe;
import com.btxtech.game.jsre.client.StartupTask;
import com.btxtech.game.jsre.client.cockpit.Group;
import com.btxtech.game.jsre.client.cockpit.SelectionHandler;
import com.btxtech.game.jsre.client.cockpit.SelectionListener;
import com.btxtech.game.jsre.client.simulation.Step;
import com.btxtech.game.jsre.client.simulation.Task;
import com.btxtech.game.jsre.client.terrain.MapWindow;
import com.btxtech.game.jsre.client.utg.missions.Mission;
import com.btxtech.game.jsre.common.EventTrackingItem;
import com.btxtech.game.jsre.common.EventTrackingStart;
import com.btxtech.game.jsre.common.SelectionTrackingItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncResourceItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.btxtech.game.jsre.common.tutorial.TutorialConfig;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: beat
 * Date: 13.01.2010
 * Time: 15:12:08
 */
public class ClientUserTracker implements SelectionListener {
    public static final int SEND_TIMEOUT = 1000 * 30;
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
                Connection.getInstance().sendCloseWindow(System.currentTimeMillis() - StartupProbe.getInstance().getRunningTimeStamp());
            }
        });
    }

    public void sandStartUpTaskFinished(StartupTask state, Date timeStamp, long duration) {
        if (Connection.isConnected()) {
            Connection.getMovableServiceAsync().startUpTaskFinished(state, timeStamp, duration, new AsyncCallback<Void>() {
                @Override
                public void onFailure(Throwable throwable) {
                    GwtCommon.handleException(throwable);
                }

                @Override
                public void onSuccess(Void aVoid) {
                    // Ignore
                }
            });
        }
    }

    public void sandStartUpTaskFailed(StartupTask state, Date timeStamp, long duration, String failureText) {
        if (Connection.isConnected()) {
            Connection.getMovableServiceAsync().startUpTaskFailed(state, timeStamp, duration, failureText, new AsyncCallback<Void>() {
                @Override
                public void onFailure(Throwable throwable) {
                    GwtCommon.handleException(throwable);
                }

                @Override
                public void onSuccess(Void aVoid) {
                    // Ignore
                }
            });
        }
    }

    @Deprecated
    public void onMouseDownTerrain(int absoluteX, int absoluteY) {
    }

    @Deprecated
    public void onMouseUpTerrain(int absoluteX, int absoluteY) {
    }

    @Deprecated
    public void clickOwnItem(SyncBaseItem syncBaseItem) {
    }

    @Deprecated
    public void clickEnemyItem(SyncBaseItem syncBaseItem) {
    }

    @Deprecated
    public void clickResourceItem(SyncResourceItem syncResourceItem) {
    }

    @Deprecated
    public void onOwnItemSelectionChanged(Group selection) {
    }

    public void onTargetSelectionItemChanged(ClientSyncItemView selection) {
    }

    public void clickSpeechBubble() {
    }

    public void onScrollHome() {
    }

    public void scroll(int left, int top, int width, int height, int deltaLeft, int deltaTop) {
    }

    public void onRegisterDialogCloseReg() {
    }

    public void onRegisterDialogCloseNoReg() {
    }

    public void onRegisterDialogOpen() {
    }

    @Deprecated
    public void onMissionAction(String action, Mission mission) {
    }

    public void onMissionTask(Mission mission, String taskName) {
    }

    public void onSkipMissionTask(Mission mission, String taskName) {
    }

    public void onTutorialTimedOut() {
    }

    public void onTutorialFinished(long duration) {
        Connection.getInstance().sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, null, null, duration);
    }

    public void onTaskFinished(Task task, long duration) {
        Connection.getInstance().sendTutorialProgress(TutorialConfig.TYPE.TASK, task.getTaskConfig().getName(), null, duration);
    }

    public void onStepFinished(Step step, Task task, long duration) {
        Connection.getInstance().sendTutorialProgress(TutorialConfig.TYPE.STEP, step.getStepConfig().getName(), task.getTaskConfig().getName(), duration);
    }

    ////////////////////////////////// Event Tracking //////////////////////////////////

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
    public void onTargetSelectionChanged(ClientSyncItemView selection) {
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
