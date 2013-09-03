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

package com.btxtech.game.jsre.client.control.task;

import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.control.StartupTaskEnum;

/**
 * User: beat
 * Date: 04.12.2010
 * Time: 12:59:49
 */
public class LoadRealGameInfoStartupTask extends AbstractStartupTask {

    public LoadRealGameInfoStartupTask(StartupTaskEnum taskEnum) {
        super(taskEnum);
    }

    @Override
    protected void privateStart(DeferredStartup deferredStartup) {
        deferredStartup.setDeferred();
        Connection.INSTANCE.downloadRealGameInfo(deferredStartup);
    }
}
