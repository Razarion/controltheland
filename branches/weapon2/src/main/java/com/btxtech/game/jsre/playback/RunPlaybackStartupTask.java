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

import com.btxtech.game.jsre.client.control.StartupTaskEnum;
import com.btxtech.game.jsre.client.control.task.AbstractStartupTask;
import com.btxtech.game.jsre.client.control.task.DeferredStartup;
import com.btxtech.game.jsre.client.renderer.Renderer;

/**
 * User: beat
 * Date: 22.12.2010
 * Time: 13:26:18
 */
public class RunPlaybackStartupTask extends AbstractStartupTask {

    public RunPlaybackStartupTask(StartupTaskEnum taskEnum) {
        super(taskEnum);
    }

    @Override
    protected void privateStart(DeferredStartup deferredStartup) {
        // TODO Simulation.getInstance().initGameEngine();
        PlaybackVisualisation.getInstance().play();
        Renderer.getInstance().start();
    }
}
