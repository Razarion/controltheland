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

package com.btxtech.game.services.utg;

import java.util.List;

/**
 * User: beat
 * Date: 17.01.2010
 * Time: 13:15:17
 */
public class GameTrackingInfo {
    private GameStartup serverGameStartup;
    private GameStartup clientStartGameStartup;
    private GameStartup clientRunningGameStartup;
    private List<DbUserAction> userActions;

    public void setServerGameStartup(GameStartup serverGameStartup) {
        this.serverGameStartup = serverGameStartup;
    }

    public void setClientStartGameStartup(GameStartup clientStartGameStartup) {
        this.clientStartGameStartup = clientStartGameStartup;
    }

    public void setClientRunningGameStartup(GameStartup clientRunningGameStartup) {
        this.clientRunningGameStartup = clientRunningGameStartup;
    }

    public GameStartup getServerGameStartup() {
        return serverGameStartup;
    }

    public GameStartup getClientStartGameStartup() {
        return clientStartGameStartup;
    }

    public GameStartup getClientRunningGameStartup() {
        return clientRunningGameStartup;
    }

    public void setUserAction(List<DbUserAction> userActions) {
        this.userActions = userActions;
    }

    public List<DbUserAction> getUserActions() {
        return userActions;
    }
}
