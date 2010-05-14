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

import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.services.utg.GameStartupState;
import com.btxtech.game.jsre.common.gameengine.services.utg.MissionAction;
import com.btxtech.game.jsre.common.gameengine.services.utg.UserAction;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.btxtech.game.jsre.client.common.UserMessage;
import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.ArrayList;
import java.util.Date;

/**
 * The async counterpart of <code>MovableService</code>.
 */
public interface MovableServiceAsync {

    void gameStartupState(GameStartupState state, Date timeStamp, AsyncCallback<Void> async);

    void getGameInfo(AsyncCallback async);

    void log(String message, Date date, AsyncCallback async);

    void sendCommand(BaseCommand baseCommand, AsyncCallback async);

    void getSyncInfo(SimpleBase simpleBase, AsyncCallback async);

    void getAllSyncInfo(AsyncCallback async);

    void getItemTypes(AsyncCallback async);

    void sendUserActions(ArrayList<UserAction> userActions, ArrayList<MissionAction> missionActions, AsyncCallback<Void> asyncCallback);

    void createMissionTraget(Id attacker, AsyncCallback<Void> asyncCallback);

    void createMissionMoney(Id harvester, AsyncCallback<Void> asyncCallback);

    void register(String userName, String password, String confirmPassword, AsyncCallback<Void> asyncCallback);

    void sendUserMessage(UserMessage userMessage, AsyncCallback<Void> asyncCallback);

    void surrenderBase(AsyncCallback<Void> asyncCallback);

    void closeConnection(AsyncCallback<Void> async);

    void getMissionTarget(AsyncCallback<String> asyncCallback);
}
