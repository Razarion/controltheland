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

import com.btxtech.game.jsre.client.ExtendedCanvas;
import com.btxtech.game.jsre.client.TopMapPanel;
import com.btxtech.game.jsre.common.utg.tracking.BrowserWindowTracking;
import com.btxtech.game.jsre.common.utg.tracking.EventTrackingItem;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.widgetideas.graphics.client.Color;

/**
 * User: beat
 * Date: 22.12.2010
 * Time: 17:23:52
 */
public class PlaybackVisualisation {
    private static PlaybackVisualisation INSTANCE = new PlaybackVisualisation();
    private Player player;
    private ExtendedCanvas mouseCanvas;
    private ExtendedCanvas windowsCanvas;
    private PlaybackInfo playbackInfo;

    public static PlaybackVisualisation getInstance() {
        return INSTANCE;
    }

    /**
     * Singleton
     */
    private PlaybackVisualisation() {
    }

    public void init(PlaybackInfo playbackInfo) {
        this.playbackInfo = playbackInfo;
        PlaybackControlPanel playbackControlPanel = new PlaybackControlPanel(this);
        player = new Player(playbackControlPanel, this);
        mouseCanvas = new ExtendedCanvas(1920, 1200);
        windowsCanvas = new ExtendedCanvas(1920, 1200);
        AbsolutePanel absolutePanel = new AbsolutePanel();
        absolutePanel.getElement().getStyle().setZIndex(2);
        absolutePanel.setHeight("100%");
        absolutePanel.setWidth("100%");
        RootPanel.get().add(absolutePanel, 0, 0);
        absolutePanel.add(mouseCanvas, 0, 0);
        absolutePanel.add(windowsCanvas, 0, 0);
        playbackControlPanel.addToParent(absolutePanel, TopMapPanel.Direction.RIGHT_TOP, 10);
        player.init(playbackInfo);
    }

    public void play() {
        mouseCanvas.clear();
        mouseCanvas.beginPath();
        mouseCanvas.setLineWidth(1);
        mouseCanvas.setStrokeStyle(Color.WHITE);
        windowsCanvas.clear();
        windowsCanvas.setLineWidth(5);
        windowsCanvas.setStrokeStyle(Color.YELLOW);
        windowsCanvas.strokeRect(playbackInfo.getEventTrackingStart().getScrollLeft(),
                playbackInfo.getEventTrackingStart().getScrollTop(),
                playbackInfo.getEventTrackingStart().getClientWidth(),
                playbackInfo.getEventTrackingStart().getClientHeight());

        player.replay();
    }

    public void displayMouseTracking(EventTrackingItem eventTrackingItem) {
        switch (eventTrackingItem.getEventType()) {
            case Event.ONMOUSEMOVE: {
                mouseCanvas.lineTo(eventTrackingItem.getXPos(), eventTrackingItem.getYPos());
                mouseCanvas.stroke();
                break;
            }
            case Event.ONMOUSEDOWN: {
                mouseCanvas.setFillStyle(Color.RED);
                mouseCanvas.fillRect(eventTrackingItem.getXPos(), eventTrackingItem.getYPos(), 10, 10);
                break;
            }
            case Event.ONMOUSEUP: {
                mouseCanvas.setFillStyle(Color.BLUE);
                mouseCanvas.fillRect(eventTrackingItem.getXPos(), eventTrackingItem.getYPos(), 5, 5);
                break;
            }
        }
    }

    public void displayBrowserWindow(BrowserWindowTracking browserWindowTracking) {
        windowsCanvas.clear();
        windowsCanvas.strokeRect(browserWindowTracking.getScrollLeft(),
                browserWindowTracking.getScrollTop(),
                browserWindowTracking.getClientWidth(),
                browserWindowTracking.getClientHeight());
    }

    public void skip() {
        player.skip();
    }
}
