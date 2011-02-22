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

import com.btxtech.game.jsre.client.control.ClientRunner;
import com.btxtech.game.jsre.client.control.GameStartupSeq;
import com.btxtech.game.jsre.client.dialogs.LevelTargetDialog;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

public class Game implements EntryPoint {
    public static final String DEBUG_PARAM = "debug";
    public static final String STARTUP_SEQ_ID = "startSeq";
    private static boolean isDebug = false;

    public void onModuleLoad() {
        try {
            GwtCommon.setUncaughtExceptionHandler();
            isDebug = Boolean.parseBoolean(Window.Location.getParameter(DEBUG_PARAM));
            LevelTargetDialog.showDialog(getLevelHtml());
            ClientServices.getInstance().getClientRunner().start(getStartupSeqFromHtml());
        } catch (Throwable t) {
            GwtCommon.handleException(t);
        }
    }

    public static boolean isDebug() {
        return isDebug;
    }

    private String getLevelHtml() {
        RootPanel div = getStartupInformation();
        return div.getElement().getInnerHTML();
    }

    private GameStartupSeq getStartupSeqFromHtml() {
        RootPanel div = getStartupInformation();
        String startSeqStr = div.getElement().getAttribute(STARTUP_SEQ_ID);
        if (startSeqStr == null || startSeqStr.trim().isEmpty()) {
            throw new IllegalArgumentException(STARTUP_SEQ_ID + " not found in div element as parameter");
        }
        GameStartupSeq gameStartupSeq;
        try {
            gameStartupSeq = GameStartupSeq.valueOf(startSeqStr);
        } catch (Throwable t) {
            throw new IllegalArgumentException(STARTUP_SEQ_ID + " can not convert to enum: " + startSeqStr);
        }
        if (gameStartupSeq.isCold()) {
            return gameStartupSeq;
        } else {
            throw new IllegalArgumentException("Can not do a warm start on a cold system");
        }

    }

    private RootPanel getStartupInformation() {
        RootPanel div = RootPanel.get(STARTUP_SEQ_ID);
        if (div == null) {
            throw new IllegalArgumentException(STARTUP_SEQ_ID + " not found in html");
        }
        return div;
    }
}
