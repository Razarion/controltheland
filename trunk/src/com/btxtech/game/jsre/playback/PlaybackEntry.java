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

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.ExtendedCanvas;
import com.btxtech.game.jsre.client.Game;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.TopMapPanel;
import com.btxtech.game.jsre.client.simulation.Simulation;
import com.btxtech.game.jsre.client.terrain.MapWindow;
import com.btxtech.game.jsre.common.EventTrackingItem;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.widgetideas.graphics.client.Color;

/**
 * User: beat
 * Date: 04.08.2010
 * Time: 10:53:54
 */
public class PlaybackEntry implements EntryPoint {
    public static final String SESSION_ID = "sessionId";
    public static final String START_TIME = "start";
    private ExtendedCanvas extendedCanvas;
    private Player player;

    @Override
    public void onModuleLoad() {
        GwtCommon.setUncaughtExceptionHandler();

        String sessionId = Window.Location.getParameter(SESSION_ID);
        long timeStamp = Long.parseLong(Window.Location.getParameter(START_TIME));

        PlaybackControlPanel playbackControlPanel = new PlaybackControlPanel(this);
        playbackControlPanel.addToParent(RootPanel.get(), TopMapPanel.Direction.RIGHT_TOP, 10);
        player = new Player(playbackControlPanel, this);

        PlaybackAsync playbackAsync = GWT.create(Playback.class);
        playbackAsync.getPlaybackInfo(sessionId, timeStamp, new AsyncCallback<PlaybackInfo>() {
            @Override
            public void onFailure(Throwable caught) {
                GwtCommon.handleException(caught, true);
            }

            @Override
            public void onSuccess(PlaybackInfo playbackInfo) {
                Game game = new Game();
                player.init(playbackInfo);
                MapWindow.getAbsolutePanel().getElement().getStyle().setZIndex(1);
                MapWindow.getAbsolutePanel().setPixelSize(playbackInfo.getEventTrackingStart().getXResolution(), playbackInfo.getEventTrackingStart().getYResolution());
                extendedCanvas = new ExtendedCanvas(playbackInfo.getEventTrackingStart().getXResolution(), playbackInfo.getEventTrackingStart().getYResolution());
                RootPanel.get().add(extendedCanvas, 0, 0);
                extendedCanvas.getElement().getStyle().setZIndex(100);
                game.init();
                ClientBase.getInstance().setAllBaseAttributes(playbackInfo.getTutorialConfig().getBaseAttributes());                
                Connection.getInstance().setupGameStructure(playbackInfo);
                Simulation.getInstance().start();
                play();
            }
        });

    }

    public void play() {
        extendedCanvas.clear();
        extendedCanvas.beginPath();
        extendedCanvas.setLineWidth(1);
        extendedCanvas.setStrokeStyle(Color.WHITE);
        player.play();
    }

    public void displayMouseTracking(EventTrackingItem eventTrackingItem) {
        switch (eventTrackingItem.getEventType()) {
            case Event.ONMOUSEMOVE: {
                extendedCanvas.lineTo(eventTrackingItem.getXPos(), eventTrackingItem.getYPos());
                extendedCanvas.stroke();
                break;
            }
            case Event.ONMOUSEDOWN: {
                extendedCanvas.setFillStyle(Color.RED);
                extendedCanvas.fillRect(eventTrackingItem.getXPos(), eventTrackingItem.getYPos(), 10, 10);
                break;
            }
            case Event.ONMOUSEUP: {
                extendedCanvas.setFillStyle(Color.BLUE);
                extendedCanvas.fillRect(eventTrackingItem.getXPos(), eventTrackingItem.getYPos(), 5, 5);
                break;
            }
        }
    }

}
