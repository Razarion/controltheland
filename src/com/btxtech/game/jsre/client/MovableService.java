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

import com.btxtech.game.jsre.client.common.GameInfo;
import com.btxtech.game.jsre.client.common.NotYourBaseException;
import com.btxtech.game.jsre.client.common.UserMessage;
import com.btxtech.game.jsre.common.NoConnectionException;
import com.btxtech.game.jsre.common.Packet;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.user.PasswordNotMatchException;
import com.btxtech.game.jsre.common.gameengine.services.user.UserAlreadyExistsException;
import com.btxtech.game.jsre.common.gameengine.services.utg.GameStartupState;
import com.btxtech.game.jsre.common.gameengine.services.utg.MissionAction;
import com.btxtech.game.jsre.common.gameengine.services.utg.UserAction;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.syncInfos.SyncItemInfo;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("gwtrpc/movableService")
public interface MovableService extends RemoteService {
    void gameStartupState(GameStartupState state, Date timeStamp);

    GameInfo getGameInfo();

    void log(String message, Date date);

    void sendCommand(BaseCommand baseCommand);

    Collection<Packet> getSyncInfo(SimpleBase simpleBase) throws NotYourBaseException, NoConnectionException;

    Collection<SyncItemInfo> getAllSyncInfo();

    Collection<ItemType> getItemTypes();

    void sendUserActions(ArrayList<UserAction> userActions, ArrayList<MissionAction> missionActions);

    void createMissionTraget(Id attacker);

    void createMissionMoney(Id harvester);

    void register(String userName, String password, String confirmPassword) throws UserAlreadyExistsException, PasswordNotMatchException;

    void sendUserMessage(UserMessage userMessage);

    void surrenderBase();

    void closeConnection();
}
