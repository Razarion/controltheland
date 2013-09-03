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
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.control.StartupTaskEnum;
import com.btxtech.game.jsre.client.control.task.AbstractStartupTask;
import com.btxtech.game.jsre.client.control.task.DeferredStartup;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * User: beat
 * Date: 04.12.2010
 * Time: 12:50:50
 */
public class LoadStartupTask extends AbstractStartupTask {

    public LoadStartupTask(StartupTaskEnum taskEnum) {
        super(taskEnum);
    }

    @Override
    protected void privateStart(final DeferredStartup deferredStartup) {
        deferredStartup.setDeferred();

        GwtCommon.setUncaughtExceptionHandler();

        PlaybackAsync playbackAsync = GWT.create(Playback.class);
        playbackAsync.getPlaybackInfo(getStartUuid(), new AsyncCallback<PlaybackInfo>() {
            @Override
            public void onFailure(Throwable caught) {
                ClientExceptionHandler.handleException(caught, true);
                deferredStartup.failed(caught);
            }

            @Override
            public void onSuccess(PlaybackInfo playbackInfo) {
                Connection.getInstance().setGameInfo(playbackInfo);
                deferredStartup.finished();
            }
        });
    }

    private String getStartUuid() {
        RootPanel div = getStartupInformation();
        String sessionId = div.getElement().getAttribute(PlaybackEntry.START_UUID);
        if (sessionId == null || sessionId.trim().isEmpty()) {
            throw new IllegalArgumentException(PlaybackEntry.START_UUID + " not found in div element as parameter");
        }
        return sessionId;
    }

    private RootPanel getStartupInformation() {
        RootPanel div = RootPanel.get(PlaybackEntry.ID);
        if (div == null) {
            throw new IllegalArgumentException(PlaybackEntry.ID + " not found in html");
        }
        return div;
    }

}
