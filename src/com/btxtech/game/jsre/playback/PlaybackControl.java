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
import com.btxtech.game.jsre.common.EventTrackingItem;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.widgetideas.graphics.client.Color;

/**
 * User: beat
 * Date: 22.12.2010
 * Time: 17:23:52
 */
public class PlaybackControl {
    private static PlaybackControl INSTANCE = new PlaybackControl();
    private Player player;
    private PlaybackControlPanel playbackControlPanel;
    private ExtendedCanvas extendedCanvas;

    public static PlaybackControl getInstance() {
        return INSTANCE;
    }

    /**
     * Singleton
     */
    private PlaybackControl() {
    }

    public void init(PlaybackInfo playbackInfo) {
        playbackControlPanel = new PlaybackControlPanel(this);
        player = new Player(playbackControlPanel, this);
        extendedCanvas = new ExtendedCanvas(playbackInfo.getEventTrackingStart().getXResolution(), playbackInfo.getEventTrackingStart().getYResolution());
        AbsolutePanel absolutePanel = new AbsolutePanel();
        absolutePanel.getElement().getStyle().setZIndex(2);
        absolutePanel.setHeight("100%");
        absolutePanel.setWidth("100%");
        RootPanel.get().add(absolutePanel, 0, 0);
        absolutePanel.add(extendedCanvas, 0, 0);
        playbackControlPanel.addToParent(absolutePanel, TopMapPanel.Direction.RIGHT_TOP, 10);
        player.init(playbackInfo);
    }

    public void play() {
        extendedCanvas.clear();
        extendedCanvas.beginPath();
        extendedCanvas.setLineWidth(1);
        extendedCanvas.setStrokeStyle(Color.WHITE);
        player.replay();
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

    public void skip() {
        player.skip();
    }
}
