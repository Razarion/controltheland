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

import com.btxtech.game.jsre.client.ClientSyncBaseItemView;
import com.btxtech.game.jsre.client.ClientSyncItemView;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.StartupTask;
import com.btxtech.game.jsre.client.cockpit.Group;
import com.btxtech.game.jsre.client.simulation.Step;
import com.btxtech.game.jsre.client.simulation.Task;
import com.btxtech.game.jsre.client.utg.missions.Mission;
import com.btxtech.game.jsre.common.gameengine.services.utg.MissionAction;
import com.btxtech.game.jsre.common.gameengine.services.utg.UserAction;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncResourceItem;
import com.btxtech.game.jsre.common.tutorial.TutorialConfig;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.ArrayList;
import java.util.Date;

/**
 * User: beat
 * Date: 13.01.2010
 * Time: 15:12:08
 */
public class ClientUserTracker {
    public static final int SEND_TIMEOUT = 1000 * 30;
    private static final ClientUserTracker INSTANCE = new ClientUserTracker();
    private ArrayList<UserAction> userActions = new ArrayList<UserAction>();
    private ArrayList<MissionAction> missionActions = new ArrayList<MissionAction>();
    private boolean stopCollection = true;
    private long timerStarted;
    private int collectionTime;
    private Timer timer;

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
                closeWindow();
            }
        });
        timer = new Timer() {
            @Override
            public void run() {
                if (System.currentTimeMillis() > timerStarted + collectionTime) {
                    timer.cancel();
                    sendUserAction(UserAction.STOP_COLLECTION, "");
                    sendUserActionsToServer();
                    stopCollection = true;
                } else {
                    sendUserActionsToServer();
                }
            }
        };
        //timer.scheduleRepeating(SEND_TIMEOUT);
        timerStarted = System.currentTimeMillis();
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


    public void onMouseDownTerrain(int absoluteX, int absoluteY) {
        sendUserAction(UserAction.TERRAIN_MOUSE_DOWN, absoluteX + ":" + absoluteY);
    }

    public void onMouseUpTerrain(int absoluteX, int absoluteY) {
        sendUserAction(UserAction.TERRAIN_MOUSE_UP, absoluteX + ":" + absoluteY);
    }

    public void clickOwnItem(SyncBaseItem syncBaseItem) {
        sendUserAction(UserAction.OWN_ITEM_CLICKED, syncBaseItem.getBaseItemType().getName() + ":" + syncBaseItem.getId());
    }

    public void clickEnemyItem(SyncBaseItem syncBaseItem) {
        sendUserAction(UserAction.ENEMY_ITEM_CLICKED, syncBaseItem.getBaseItemType().getName() + ":" + syncBaseItem.getId());
    }

    public void clickResourceItem(SyncResourceItem syncResourceItem) {
        sendUserAction(UserAction.RESOURCE_CLICKED, syncResourceItem.getId().toString());
    }

    public void onOwnItemSelectionChanged(Group selection) {
        StringBuffer buffer = new StringBuffer();
        for (ClientSyncBaseItemView clientSyncBaseItemView : selection.getItems()) {
            buffer.append(clientSyncBaseItemView.getSyncItem().getId().toString());
            buffer.append(";");
        }
        sendUserAction(UserAction.OWN_ITEM_SELECTION_CHANGE, buffer.toString());
    }

    public void onTargetSelectionItemChanged(ClientSyncItemView selection) {
        sendUserAction(UserAction.TRAGET_SELECTION_CHANGED, selection.getSyncItem().getId().toString());
    }

    public void clickSpeechBubble() {
        sendUserAction(UserAction.SPEECH_BUBBLE_CLICKED, null);
    }

    public void onScrollHome() {
        sendUserAction(UserAction.SCROLL_HOME_BUTTON, null);
    }

    public void scroll(int left, int top, int width, int height, int deltaLeft, int deltaTop) {
        if (userActions.isEmpty()) {
            sendUserAction(UserAction.SCROLL, "Origin " + left + ":" + top + " width:" + width + " height:" + height + " deltaLeft:" + deltaLeft + " deltaTop:" + deltaTop);
        } else {
            UserAction prevAction = userActions.get(userActions.size() - 1);
            if (prevAction.getType().equals(UserAction.SCROLL)) {
                prevAction.repeat("Origin " + left + ":" + top + " width:" + width + " height:" + height + " deltaLeft:" + deltaLeft + " deltaTop:" + deltaTop);
            } else {
                sendUserAction(UserAction.SCROLL, "Origin " + left + ":" + top + " width:" + width + " height:" + height + " deltaLeft:" + deltaLeft + " deltaTop:" + deltaTop);
            }
        }
    }

    public void closeWindow() {
        ArrayList<UserAction> userActions = new ArrayList<UserAction>();
        userActions.add(new UserAction(UserAction.CLOSE_WINDOW, null));
        Connection.getMovableServiceAsync().sendUserActions(userActions, missionActions, new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable throwable) {
                GwtCommon.handleException(throwable);
            }

            @Override
            public void onSuccess(Void aVoid) {
                // Ignore
            }
        });
        //sendUserAction(UserAction.CLOSE_WINDOW, null);
        //sendUserActionsToServer();
    }


    public void onRegisterDialogCloseReg() {
        sendUserAction(UserAction.REGISTER_DIALOG_CLOSE_REG, null);
    }

    public void onRegisterDialogCloseNoReg() {
        sendUserAction(UserAction.REGISTER_DIALOG_CLOSE_NO_REG, null);
    }

    public void onRegisterDialogOpen() {
        sendUserAction(UserAction.REGISTER_DIALOG_OPEN, null);
    }

    public void onMissionAction(String action, Mission mission) {
        if (mission != null) {
            sendMissionAction(action, mission.getName(), null);
        } else {
            sendMissionAction(action, "", null);
        }
        if (action.equals(MissionAction.MISSION_COMPLETED) || action.equals(MissionAction.MISSION_USER_STOPPED)) {
            sendUserActionsToServer();
        }
    }

    public void onMissionTask(Mission mission, String taskName) {
        sendMissionAction(MissionAction.TASK_START, mission.getName(), taskName);
    }

    public void onSkipMissionTask(Mission mission, String taskName) {
        sendMissionAction(MissionAction.TASK_SKIPPED, mission.getName(), taskName);
    }

    public void onTutorialTimedOut() {
        sendMissionAction(MissionAction.MISSION_TIMED_OUT, "", "");
    }

    public void sendUserAction(String type, String details) {
        if (stopCollection) {
            return;
        }
        UserAction userAction = new UserAction(type, details);
        userActions.add(userAction);
    }

    private void sendMissionAction(String action, String mission, String task) {
        if (stopCollection) {
            return;
        }

        MissionAction missionAction = new MissionAction(action, mission, task);
        missionActions.add(missionAction);
    }

    private void sendUserActionsToServer() {
        if (Connection.isConnected() && !stopCollection && (!userActions.isEmpty() || !missionActions.isEmpty())) {
            Connection.getMovableServiceAsync().sendUserActions(userActions, missionActions, new AsyncCallback<Void>() {
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
        userActions = new ArrayList<UserAction>();
        missionActions = new ArrayList<MissionAction>();
    }

    public void setCollectionTime(int collectionTime) {
        this.collectionTime = collectionTime * 1000;
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
}
