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

import com.btxtech.game.jsre.client.action.ActionHandler;
import com.btxtech.game.jsre.client.cockpit.SelectionHandler;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.terrain.MapWindow;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.utg.tracking.BrowserWindowTracking;
import com.btxtech.game.jsre.common.utg.tracking.EventTrackingItem;
import com.btxtech.game.jsre.common.utg.tracking.SelectionTrackingItem;
import com.btxtech.game.jsre.common.utg.tracking.TerrainScrollTracking;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.google.gwt.user.client.Timer;
import java.util.ArrayList;
import java.util.Collections;

/**
 * User: beat
 * Date: 11.08.2010
 * Time: 18:50:00
 */
public class Player {
    private ArrayList<Frame> frames;
    private PlaybackControlPanel playbackControlPanel;
    private Timer timer;
    private int nextFrameIndex = 0;
    private Frame nextFrame;
    private long startTime;
    private PlaybackVisualisation playbackVisualisation;

    public Player(PlaybackControlPanel playbackControlPanel, PlaybackVisualisation playbackVisualisation) {
        this.playbackControlPanel = playbackControlPanel;
        this.playbackVisualisation = playbackVisualisation;
    }

    public void init(PlaybackInfo playbackInfo) {
        startTime = playbackInfo.getEventTrackingStart().getClientTimeStamp();
        frames = new ArrayList<Frame>();
        for (EventTrackingItem eventTrackingItem : playbackInfo.getEventTrackingItems()) {
            frames.add(new Frame(eventTrackingItem.getClientTimeStamp(), eventTrackingItem));
        }
        for (SelectionTrackingItem selectionTrackingItem : playbackInfo.getSelectionTrackingItems()) {
            frames.add(new Frame(selectionTrackingItem.getTimeStamp(), selectionTrackingItem));
        }
        for (BaseCommand baseCommand : playbackInfo.getBaseCommands()) {
            frames.add(new Frame(baseCommand.getTimeStamp(), baseCommand));
        }
        for (TerrainScrollTracking terrainScrollTracking : playbackInfo.getScrollTrackingItems()) {
            frames.add(new Frame(terrainScrollTracking.getClientTimeStamp(), terrainScrollTracking));
        }
        for (BrowserWindowTracking browserWindowTracking : playbackInfo.getBrowserWindowTrackings()) {
            frames.add(new Frame(browserWindowTracking.getClientTimeStamp(), browserWindowTracking));
        }
        Collections.sort(frames);
    }

    public void play() {
        if (frames.isEmpty()) {
            onFinished();
            return;
        }
        timer = new Timer() {
            @Override
            public void run() {
                displayItem();
            }
        };
        this.nextFrameIndex = 0;

        Frame frame = frames.get(this.nextFrameIndex);
        long sleepTime = frame.getTimeStamp() - startTime;
        prepareNextItem(sleepTime, frame);
        playbackControlPanel.setState("Play");
    }

    private void prepareNextItem(long sleepTime, Frame frame) {
        nextFrame = frame;
        if (sleepTime == 0) {
            displayItem();
        } else {
            timer.schedule((int) sleepTime);
        }
    }

    private void displayItem() {
        Object object = nextFrame.getLoad();
        if (object instanceof EventTrackingItem) {
            displayEventTrackingItemFrame((EventTrackingItem) object);
        } else if (object instanceof SelectionTrackingItem) {
            displaySelectionTrackingItemFrame((SelectionTrackingItem) object);
        } else if (object instanceof BaseCommand) {
            displayBaseCommandFrame((BaseCommand) object);
        } else if (object instanceof TerrainScrollTracking) {
            displayScrollingFrame((TerrainScrollTracking) object);
        } else if (object instanceof BrowserWindowTracking) {
            displayBrowserWindowFrame((BrowserWindowTracking) object);
        } else {
            throw new IllegalArgumentException(this + " Unknown Frame: " + object);
        }

        playbackControlPanel.setTime(nextFrame.getTimeStamp() - startTime);
        loadNextItem();
    }

    private void displayBaseCommandFrame(BaseCommand baseCommand) {
        ActionHandler.getInstance().injectCommand(baseCommand);
    }

    private void displaySelectionTrackingItemFrame(SelectionTrackingItem selectionTrackingItem) {
        SelectionHandler.getInstance().inject(selectionTrackingItem);
    }

    private void displayEventTrackingItemFrame(EventTrackingItem eventTrackingItem) {
        playbackVisualisation.displayMouseTracking(eventTrackingItem);
    }

    private void displayScrollingFrame(TerrainScrollTracking terrainScrollTracking) {
        TerrainView.getInstance().moveAbsolute(new Index(terrainScrollTracking.getLeft(), terrainScrollTracking.getTop()));
    }

    private void displayBrowserWindowFrame(BrowserWindowTracking browserWindowTracking) {
        MapWindow.getAbsolutePanel().setPixelSize(browserWindowTracking.getScrollWidth(), browserWindowTracking.getScrollHeight());
        playbackVisualisation.displayBrowserWindow(browserWindowTracking);
    }

    private void loadNextItem() {
        Frame oldFrame = nextFrame;
        nextFrameIndex++;
        if (nextFrameIndex >= frames.size()) {
            onFinished();
            return;
        }
        Frame frame = frames.get(nextFrameIndex);
        long sleepTime = frame.getTimeStamp() - oldFrame.getTimeStamp();
        prepareNextItem(sleepTime, frame);
    }

    private void onFinished() {
        playbackControlPanel.setState("Finished");
    }

    public void replay() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        play();
    }

    public void skip() {
        timer.cancel();
        displayItem();
    }
}