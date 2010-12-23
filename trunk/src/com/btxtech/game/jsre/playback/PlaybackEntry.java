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

import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.control.ClientRunner;
import com.google.gwt.core.client.EntryPoint;

/**
 * User: beat
 * Date: 04.08.2010
 * Time: 10:53:54
 */
public class PlaybackEntry implements EntryPoint {
    public static final String ID = "playbackInfo";
    public static final String SESSION_ID = "sessionId";
    public static final String START_TIME = "start";
    public static final String STAGE_NAME = "stage";

    @Override
    public void onModuleLoad() {
        try {
            ClientRunner.getInstance().start(PlaybackStartupSeq.COLD_PLAYBACK);
        } catch (Throwable t) {
            GwtCommon.handleException(t);
        }
    }
}
