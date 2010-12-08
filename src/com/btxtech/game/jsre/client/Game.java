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
import com.btxtech.game.jsre.client.control.StartupSeq;
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
            StartupSeq startupSeq = getStartupSeqFromHtml();
            if (!startupSeq.isCold()) {
                throw new IllegalArgumentException("Can not do a warm start on a cold system");
            }
            ClientRunner.getInstance().start(startupSeq);
        } catch (Throwable t) {
            GwtCommon.handleException(t);
        }
    }

    public static boolean isDebug() {
        return isDebug;
    }

    private StartupSeq getStartupSeqFromHtml() {
        RootPanel div = RootPanel.get(STARTUP_SEQ_ID);
        if (div == null) {
            throw new IllegalArgumentException(STARTUP_SEQ_ID + " not found in html");
        }
        String startSeqStr = div.getElement().getAttribute(STARTUP_SEQ_ID);
        if (startSeqStr == null || startSeqStr.trim().isEmpty()) {
            throw new IllegalArgumentException(STARTUP_SEQ_ID + " not found in div element as parameter");
        }
        try {
            return StartupSeq.valueOf(startSeqStr);
        } catch (Throwable t) {
            throw new IllegalArgumentException(STARTUP_SEQ_ID + " can not convert to enum: " + startSeqStr);
        }
    }
}
