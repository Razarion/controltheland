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

package com.btxtech.game.jsre.client;

import com.btxtech.game.jsre.client.common.UserMessage;
import com.btxtech.game.jsre.common.EventTrackingItem;
import com.btxtech.game.jsre.common.EventTrackingStart;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.services.utg.MissionAction;
import com.btxtech.game.jsre.common.gameengine.services.utg.UserAction;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.btxtech.game.jsre.common.tutorial.TutorialConfig;
import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The async counterpart of <code>MovableService</code>.
 */
public interface MovableServiceAsync {
    void getGameInfo(AsyncCallback async);

    void log(String message, Date date, AsyncCallback async);

    void sendCommands(List<BaseCommand> baseCommands, AsyncCallback async);

    void getSyncInfo(SimpleBase simpleBase, AsyncCallback async);

    void getAllSyncInfo(AsyncCallback async);

    @Deprecated
    void sendUserActions(ArrayList<UserAction> userActions, ArrayList<MissionAction> missionActions, AsyncCallback<Void> asyncCallback);

    void createMissionTraget(Id attacker, AsyncCallback<Void> asyncCallback);

    void createMissionMoney(Id harvester, AsyncCallback<Void> asyncCallback);

    void register(String userName, String password, String confirmPassword, AsyncCallback<Void> asyncCallback);

    void sendUserMessage(UserMessage userMessage, AsyncCallback<Void> asyncCallback);

    void surrenderBase(AsyncCallback<Void> asyncCallback);

    void closeConnection(AsyncCallback<Void> async);

    void getMissionTarget(AsyncCallback<String> asyncCallback);

    void tutorialTerminated(AsyncCallback<Void> async);

    void startUpTaskFinished(StartupTask state, Date clientTimeStamp, long duration, AsyncCallback<Void> asyncCallback);

    void startUpTaskFailed(StartupTask state, Date clientTimeStamp, long duration, String failureText, AsyncCallback<Void> asyncCallback);

    void sendTutorialProgress(TutorialConfig.TYPE type, String name, String parent, long duration, AsyncCallback<Void> asyncCallback);

    void sendEventTrackingStart(EventTrackingStart eventTrackingStart, AsyncCallback<Void> asyncCallback);

    void sendEventTrackerItems(List<EventTrackingItem> eventTrackingItems, AsyncCallback<Void> asyncCallback);

    void sendTotalStartupTime(long totalStartupTime, AsyncCallback<Void> asyncCallback);

    void sendCloseWindow(long totalRunningTime, AsyncCallback<Void> asyncCallback);
}
