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

package com.btxtech.game.jsre.playback;

import com.btxtech.game.jsre.client.ClientExceptionHandler;
import com.btxtech.game.jsre.client.ClientGlobalServices;
import com.btxtech.game.jsre.client.Game;
import com.btxtech.game.jsre.client.control.StartupScreen;
import com.google.gwt.core.client.EntryPoint;

/**
 * User: beat
 * Date: 04.08.2010
 * Time: 10:53:54
 */
public class PlaybackEntry implements EntryPoint {
    public static final String ID = "playbackInfo";
    public static final String START_UUID = "sessionId";

    @Override
    public void onModuleLoad() {
        try {
            Game.setDebug();
            ClientGlobalServices.getInstance().getClientRunner().addStartupProgressListener(StartupScreen.getInstance());
            ClientGlobalServices.getInstance().getClientRunner().start(PlaybackStartupSeq.COLD_PLAYBACK);
        } catch (Throwable t) {
            ClientExceptionHandler.handleException(t);
        }
    }
}
