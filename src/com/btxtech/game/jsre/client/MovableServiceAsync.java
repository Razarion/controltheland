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
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>MovableService</code>.
 */
public interface MovableServiceAsync {

    void gameStartupState(GameStartupState state, AsyncCallback<Void> async);

    void getGameInfo(AsyncCallback async);

    void log(String message, AsyncCallback async);

    void sendCommand(BaseCommand baseCommand, AsyncCallback async);

    void getSyncInfo(SimpleBase simpleBase, AsyncCallback async);

    void getAllSyncInfo(AsyncCallback async);

    void getItemTypes(AsyncCallback async);

    void getTerrainField(AsyncCallback async);
}
