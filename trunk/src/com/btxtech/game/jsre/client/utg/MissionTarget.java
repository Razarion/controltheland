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

/**
 * User: beat
 * Date: 12.05.2010
 * Time: 10:30:55
 */
package com.btxtech.game.jsre.client.utg;

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.cockpit.Cockpit;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.common.Level;
import com.btxtech.game.jsre.client.dialogs.MissionTargetDialog;
import com.btxtech.game.jsre.client.dialogs.PromotionDialog;
import com.btxtech.game.jsre.common.LevelPacket;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;

@Deprecated
public class MissionTarget {
    public static final int BLINK_DELAY = 500;
    private static MissionTarget INSTANCE = new MissionTarget();
    private MissionTargetDialog missionTargetDialog = new MissionTargetDialog();
    private boolean loadingRequired = true;
    private String missionTargetString;
    private Level level;

    public static MissionTarget getInstance() {
        return INSTANCE;
    }

    /**
     * Singleton
     */
    private MissionTarget() {
    }

    public void showMissionTargetDialog() {
        missionTargetDialog.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
            public void setPosition(int offsetWidth, int offsetHeight) {
                int left = (Window.getClientWidth() - offsetWidth) / 2;
                int top = (Window.getClientHeight() - offsetHeight) / 2;
                missionTargetDialog.setPopupPosition(left, top);
            }
        });

        if (loadingRequired) {
            //Connection.getInstance().getMissionTarget(this);
            loadingRequired = false;
            missionTargetDialog.setMissionTarget("Loading...");
        } else if (missionTargetString != null) {
            missionTargetDialog.setMissionTarget(missionTargetString);
        }
    }

    public void setNoConnection(Throwable caught) {
        missionTargetString = null;
        loadingRequired = true;
        if (caught != null) {
            missionTargetDialog.setMissionTarget(caught.toString());
        } else {
            missionTargetDialog.setMissionTarget("No connection");
        }
    }

    public void setMissionTarget(String htmlString) {
        missionTargetString = htmlString;
        missionTargetDialog.setMissionTarget(missionTargetString);
    }
}
