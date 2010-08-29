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

import com.btxtech.game.jsre.client.common.NotYourBaseException;
import com.btxtech.game.jsre.client.common.UserMessage;
import com.btxtech.game.jsre.client.common.info.GameInfo;
import com.btxtech.game.jsre.common.EventTrackingItem;
import com.btxtech.game.jsre.common.EventTrackingStart;
import com.btxtech.game.jsre.common.NoConnectionException;
import com.btxtech.game.jsre.common.Packet;
import com.btxtech.game.jsre.common.SelectionTrackingItem;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.services.user.PasswordNotMatchException;
import com.btxtech.game.jsre.common.gameengine.services.user.UserAlreadyExistsException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.syncInfos.SyncItemInfo;
import com.btxtech.game.jsre.common.tutorial.TutorialConfig;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("gwtrpc/movableService")
public interface MovableService extends RemoteService {
    GameInfo getGameInfo();

    void log(String message, Date date);

    void sendCommands(List<BaseCommand> baseCommands);

    Collection<Packet> getSyncInfo(SimpleBase simpleBase) throws NotYourBaseException, NoConnectionException;

    Collection<SyncItemInfo> getAllSyncInfo();

    void register(String userName, String password, String confirmPassword) throws UserAlreadyExistsException, PasswordNotMatchException;

    void sendUserMessage(UserMessage userMessage);

    void surrenderBase();

    void closeConnection();

    String getMissionTarget();

    void tutorialTerminated();

    void startUpTaskFinished(StartupTask state, Date clientTimeStamp, long duration);

    void startUpTaskFailed(StartupTask state, Date clientTimeStamp, long duration, String failureText);

    void sendTutorialProgress(TutorialConfig.TYPE type, String name, String parent, long duration, long clientTimeStamp);

    void sendEventTrackingStart(EventTrackingStart eventTrackingStart);

    void sendEventTrackerItems(Collection<EventTrackingItem> eventTrackingItems, Collection<BaseCommand> baseCommands, Collection<SelectionTrackingItem> selectionTrackingItems);

    void sendTotalStartupTime(long totalStartupTime, long clientTimeStamp);

    void sendCloseWindow(long totalRunningTime, long clientTimeStamp);

    List<String> getFreeColors(int index, int count);

    void setBaseColor(String color);
}
