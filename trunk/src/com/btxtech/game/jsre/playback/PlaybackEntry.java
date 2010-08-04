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
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.common.EventTrackingItem;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.widgetideas.graphics.client.Color;
import java.util.List;

/**
 * User: beat
 * Date: 04.08.2010
 * Time: 10:53:54
 */
public class PlaybackEntry implements EntryPoint {
    private Timer timer;
    private EventTrackingItem eventTrackingItem;
    private ExtendedCanvas extendedCanvas;
    private int currentItem = 0;
    private List<EventTrackingItem> eventTrackingItems;

    @Override
    public void onModuleLoad() {
        GwtCommon.setUncaughtExceptionHandler();
        PlaybackAsync playbackAsync = GWT.create(Playback.class);
        playbackAsync.getPlaybackInfo(new AsyncCallback<PlaybackInfo>() {
            @Override
            public void onFailure(Throwable caught) {
                GwtCommon.handleException(caught, true);
            }

            @Override
            public void onSuccess(PlaybackInfo playbackInfo) {
                extendedCanvas = new ExtendedCanvas(playbackInfo.getEventTrackingStart().getXResolution(), playbackInfo.getEventTrackingStart().getYResolution());
                extendedCanvas.getElement().getStyle().setBackgroundColor("#000000");
                RootPanel.get().add(extendedCanvas);
                eventTrackingItems = playbackInfo.getEventTrackingItems();
                play(playbackInfo.getEventTrackingStart().getClientTimeStamp());
            }
        });

    }

    private void play(long startTime) {
        timer = new Timer() {
            @Override
            public void run() {
                displayItem();
            }
        };
        EventTrackingItem eventTrackingItem = eventTrackingItems.get(0);
        long sleepTime = eventTrackingItem.getClientTimeStamp() - startTime;
        extendedCanvas.setLineWidth(1);
        extendedCanvas.setStrokeStyle(Color.WHITE);
        extendedCanvas.moveTo(eventTrackingItem.getYPos(), eventTrackingItem.getYPos());
        prepareNextItem(sleepTime, eventTrackingItem);
    }

    private void prepareNextItem(long sleepTime, EventTrackingItem eventTrackingItem) {
        this.eventTrackingItem = eventTrackingItem;
        if (sleepTime == 0) {
            displayItem();
        } else {
            timer.schedule((int) sleepTime);
        }
    }

    private void loadNextItem() {
        EventTrackingItem oldEventTrackingItem = eventTrackingItems.get(currentItem);
        currentItem++;
        if (currentItem >= eventTrackingItems.size()) {
            // Playing finished
            return;
        }
        EventTrackingItem newEventTrackingItem = eventTrackingItems.get(currentItem);
        long sleepTime = newEventTrackingItem.getClientTimeStamp() - oldEventTrackingItem.getClientTimeStamp();
        prepareNextItem(sleepTime, newEventTrackingItem);
    }

    private void displayItem() {
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
        loadNextItem();
    }
}
