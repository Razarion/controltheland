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
import com.btxtech.game.jsre.client.cockpit.Cockpit;
import com.btxtech.game.jsre.client.control.StartupTaskEnum;
import com.btxtech.game.jsre.client.dialogs.RegisterDialog;
import com.btxtech.game.jsre.client.terrain.MapWindow;
import com.btxtech.game.jsre.client.utg.MissionTarget;

/**
 * User: beat
 * Date: 05.12.2010
 * Time: 17:55:03
 */
public class RunRealGameStartupTask extends AbstractStartupTask {
    
    public RunRealGameStartupTask(StartupTaskEnum taskEnum) {
        super(taskEnum);
    }

    @Override
    protected void privateStart(DeferredStartup deferredStartup) {
        Connection.getInstance().startSyncInfoPoll();
        MissionTarget.getInstance().showMissionTargetDialog();
        RegisterDialog.showDialogRepeating();
        MapWindow.getInstance().displayVisibleItems();
        Cockpit.getInstance().updateItemLimit();
    }
}
