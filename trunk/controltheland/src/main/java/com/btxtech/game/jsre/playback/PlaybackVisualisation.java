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

import com.btxtech.game.jsre.client.ColorConstants;
import com.btxtech.game.jsre.client.TopMapPanel;
import com.btxtech.game.jsre.common.Html5NotSupportedException;
import com.btxtech.game.jsre.common.utg.tracking.BrowserWindowTracking;
import com.btxtech.game.jsre.common.utg.tracking.EventTrackingItem;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * User: beat
 * Date: 22.12.2010
 * Time: 17:23:52
 */
public class PlaybackVisualisation {
    private static PlaybackVisualisation INSTANCE = new PlaybackVisualisation();
    private Player player;
    private Context2d mouseContext2d;
    private Context2d windowsContext2d;
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
        Canvas mouseCanvas = Canvas.createIfSupported();
        if (mouseCanvas == null) {
            throw new Html5NotSupportedException("PlaybackVisualisation: Canvas not supported.");
        }
        Canvas windowsCanvas = Canvas.createIfSupported();
        mouseContext2d = mouseCanvas.getContext2d();
        windowsContext2d = windowsCanvas.getContext2d();
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
        if (mouseContext2d.getCanvas().getWidth() != Window.getClientWidth()) {
            mouseContext2d.getCanvas().setWidth(Window.getClientWidth());
        }
        if (mouseContext2d.getCanvas().getHeight() != Window.getClientHeight()) {
            mouseContext2d.getCanvas().setHeight(Window.getClientHeight());
        }
        mouseContext2d.clearRect(0, 0, mouseContext2d.getCanvas().getWidth(), mouseContext2d.getCanvas().getHeight());
        mouseContext2d.beginPath();
        mouseContext2d.setLineWidth(1);
        mouseContext2d.setStrokeStyle(ColorConstants.WHITE);

        if (windowsContext2d.getCanvas().getWidth() != Window.getClientWidth()) {
            windowsContext2d.getCanvas().setWidth(Window.getClientWidth());
        }
        if (windowsContext2d.getCanvas().getHeight() != Window.getClientHeight()) {
            windowsContext2d.getCanvas().setHeight(Window.getClientHeight());
        }
        windowsContext2d.clearRect(0, 0, windowsContext2d.getCanvas().getWidth(), windowsContext2d.getCanvas().getHeight());
        windowsContext2d.setLineWidth(5);
        windowsContext2d.setStrokeStyle(ColorConstants.YELLOW);
        windowsContext2d.strokeRect(playbackInfo.getEventTrackingStart().getScrollLeft(),
                playbackInfo.getEventTrackingStart().getScrollTop(),
                playbackInfo.getEventTrackingStart().getClientWidth(),
                playbackInfo.getEventTrackingStart().getClientHeight());

        player.replay();
    }

    public void displayMouseTracking(EventTrackingItem eventTrackingItem) {
        switch (eventTrackingItem.getEventType()) {
            case Event.ONMOUSEMOVE: {
                mouseContext2d.lineTo(eventTrackingItem.getXPos(), eventTrackingItem.getYPos());
                mouseContext2d.stroke();
                break;
            }
            case Event.ONMOUSEDOWN: {
                mouseContext2d.setFillStyle(ColorConstants.RED);
                mouseContext2d.fillRect(eventTrackingItem.getXPos(), eventTrackingItem.getYPos(), 10, 10);
                break;
            }
            case Event.ONMOUSEUP: {
                mouseContext2d.setFillStyle(ColorConstants.BLUE);
                mouseContext2d.fillRect(eventTrackingItem.getXPos(), eventTrackingItem.getYPos(), 5, 5);
                break;
            }
        }
    }

    public void displayBrowserWindow(BrowserWindowTracking browserWindowTracking) {
        if (windowsContext2d.getCanvas().getWidth() != Window.getClientWidth()) {
            windowsContext2d.getCanvas().setWidth(Window.getClientWidth());
        }
        if (windowsContext2d.getCanvas().getHeight() != Window.getClientHeight()) {
            windowsContext2d.getCanvas().setHeight(Window.getClientHeight());
        }
        windowsContext2d.clearRect(0, 0, windowsContext2d.getCanvas().getWidth(), windowsContext2d.getCanvas().getHeight());
        windowsContext2d.strokeRect(browserWindowTracking.getScrollLeft(),
                browserWindowTracking.getScrollTop(),
                browserWindowTracking.getClientWidth(),
                browserWindowTracking.getClientHeight());
    }

    public void skip() {
        player.skip();
    }
}
