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

import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.control.StartupTaskEnum;
import com.btxtech.game.jsre.client.control.task.DeferredStartup;
import com.btxtech.game.jsre.client.control.task.GameEngineStartupTask;
import com.btxtech.game.jsre.client.terrain.MapWindow;

/**
 * User: beat
 * Date: 22.12.2010
 * Time: 13:26:18
 */
public class InitStartupTask extends GameEngineStartupTask {

    public InitStartupTask(StartupTaskEnum taskEnum) {
        super(taskEnum);
    }

    @Override
    protected void privateStart(DeferredStartup deferredStartup) {
        PlaybackInfo playbackInfo = (PlaybackInfo) Connection.getInstance().getGameInfo();
        PlaybackVisualisation.getInstance().init(playbackInfo);
        MapWindow.getAbsolutePanel().getElement().getStyle().setZIndex(1);
        MapWindow.getAbsolutePanel().setPixelSize(playbackInfo.getEventTrackingStart().getScrollWidth(), playbackInfo.getEventTrackingStart().getScrollHeight());
        setupGameStructure(playbackInfo);
    }
}
