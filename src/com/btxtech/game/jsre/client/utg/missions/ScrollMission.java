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

package com.btxtech.game.jsre.client.utg.missions;

import com.btxtech.game.jsre.client.InfoPanel;
import com.btxtech.game.jsre.client.terrain.TerrainScrollListener;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.client.utg.SpeechBubble;
import com.btxtech.game.jsre.client.utg.ClientUserTracker;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;

/**
 * User: beat
 * Date: 26.01.2010
 * Time: 22:20:04
 */
public class ScrollMission extends Mission implements TerrainScrollListener, ClickHandler {
    enum Task {
        WAIT_FOR_SCROLL,
        WAIT_FOR_HOME_BUTTON,
        FINISHED
    }
    private SpeechBubble speechBubble1;
    private SpeechBubble speechBubble2;
    private SpeechBubble speechBubble3;
    private SpeechBubble speechBubble4;
    private SpeechBubble speechBubbleHome;
    private long lastAction;
    private Task task;
    private HandlerRegistration handlerRegistrationHomeButton;

    public ScrollMission() {
        super("ScrollMission");
    }

    public void start() throws MissionAportedException {
        int screenWidth = TerrainView.getInstance().getViewWidth();
        int screenHeight = TerrainView.getInstance().getViewHeight();
        speechBubble1 = new SpeechBubble(screenWidth / 2, TerrainView.AUTO_SCROLL_DETECTION_WIDTH / 2, HtmlConstants.SCROLL_HTML1, true);
        speechBubble2 = new SpeechBubble(TerrainView.AUTO_SCROLL_DETECTION_WIDTH / 2, screenHeight / 2, HtmlConstants.SCROLL_HTML1, true);
        speechBubble3 = new SpeechBubble(screenWidth / 2 - TerrainView.AUTO_SCROLL_DETECTION_WIDTH / 2, screenHeight - TerrainView.AUTO_SCROLL_DETECTION_WIDTH / 2, HtmlConstants.SCROLL_HTML1, true);
        speechBubble4 = new SpeechBubble(screenWidth - TerrainView.AUTO_SCROLL_DETECTION_WIDTH / 2, screenHeight / 2 - TerrainView.AUTO_SCROLL_DETECTION_WIDTH / 2, HtmlConstants.SCROLL_HTML1, true);
        lastAction = System.currentTimeMillis();
        TerrainView.getInstance().addTerrainScrollListener(this);
        task = Task.WAIT_FOR_SCROLL;
        ClientUserTracker.getInstance().onMissionTask(this, task);
        lastAction = System.currentTimeMillis();
    }

    @Override
    public void onExecuteCommand(SyncBaseItem syncItem, BaseCommand baseCommand) {
    }

    @Override
    public void blink() {
        if (System.currentTimeMillis() < HtmlConstants.WAITING_FOR_BLINK + lastAction) {
          return;
        }
        if (speechBubble1 != null) {
            speechBubble1.blink();
            speechBubble2.blink();
            speechBubble3.blink();
            speechBubble4.blink();
        }
        if (speechBubbleHome != null) {
            speechBubbleHome.blink();
        }
    }

    @Override
    public boolean isAccomplished() {
        return task == Task.FINISHED;
    }

    @Override
    public long getAccomplishedTimeStamp() {
        return lastAction;
    }

    @Override
    public void close() {
    }

    @Override
    public void onScroll(int left, int top, int width, int height, int deltaLeft, int deltaTop) {
        if (task == Task.WAIT_FOR_SCROLL && speechBubble1 != null) {
            speechBubble1.close();
            speechBubble2.close();
            speechBubble3.close();
            speechBubble4.close();
            speechBubble1 = null;
            speechBubble2 = null;
            speechBubble3 = null;
            speechBubble4 = null;
            Button button = InfoPanel.getInstance().getScrollHome();
            int x = button.getAbsoluteLeft() + button.getOffsetWidth() / 2;
            int y = button.getAbsoluteTop();
            speechBubbleHome = new SpeechBubble(x, y, HtmlConstants.SCROLL_HTML2, true);
            handlerRegistrationHomeButton = button.addClickHandler(this);
            task = Task.WAIT_FOR_HOME_BUTTON;
            ClientUserTracker.getInstance().onMissionTask(this, task);
            lastAction = System.currentTimeMillis();
        }

    }

    @Override
    public void onClick(ClickEvent clickEvent) {
        if (task == Task.WAIT_FOR_HOME_BUTTON) {
            task = Task.FINISHED;
            speechBubbleHome.close();
            speechBubbleHome = null;
            handlerRegistrationHomeButton.removeHandler();
            TerrainView.getInstance().removeTerrainScrollListener(this);
        }
    }


}