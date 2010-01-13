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

import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.common.gameengine.services.utg.GameStartupState;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * User: beat
 * Date: 13.01.2010
 * Time: 15:12:08
 */
public class ClientUserTracker {
    private static final ClientUserTracker INSTANCE = new ClientUserTracker();

    public static ClientUserTracker getInstance() {
        return INSTANCE;
    }

    public void sandGameStartupState(GameStartupState state) {
        if (Connection.isConnected()) {
            Connection.getMovableServiceAsync().gameStartupState(state, new AsyncCallback<Void>() {
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
}
