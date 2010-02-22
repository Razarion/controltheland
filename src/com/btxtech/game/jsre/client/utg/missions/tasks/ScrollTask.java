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

package com.btxtech.game.jsre.client.utg.missions.tasks;

import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.client.terrain.TerrainScrollListener;
import com.btxtech.game.jsre.client.utg.SpeechBubble;
import com.btxtech.game.jsre.client.utg.missions.HtmlConstants;

/**
 * User: beat
 * Date: 17.02.2010
 * Time: 20:03:23
 */
public class ScrollTask extends Task implements TerrainScrollListener {
    private SpeechBubble speechBubble1;
    private SpeechBubble speechBubble2;
    private SpeechBubble speechBubble3;
    private SpeechBubble speechBubble4;

    public ScrollTask() {
        super(null);
    }

    @Override
    public String getName() {
        return "Wait for scroll";
    }

    @Override
    public void run() {
        int screenWidth = TerrainView.getInstance().getViewWidth();
        int screenHeight = TerrainView.getInstance().getViewHeight();
        speechBubble1 = new SpeechBubble(screenWidth / 2, TerrainView.AUTO_SCROLL_DETECTION_WIDTH / 2, HtmlConstants.SCROLL_HTML1, true);
        speechBubble2 = new SpeechBubble(TerrainView.AUTO_SCROLL_DETECTION_WIDTH / 2, screenHeight / 2, HtmlConstants.SCROLL_HTML1, true);
        speechBubble3 = new SpeechBubble(screenWidth / 2 - TerrainView.AUTO_SCROLL_DETECTION_WIDTH / 2, screenHeight - TerrainView.AUTO_SCROLL_DETECTION_WIDTH / 2, HtmlConstants.SCROLL_HTML1, true);
        speechBubble4 = new SpeechBubble(screenWidth - TerrainView.AUTO_SCROLL_DETECTION_WIDTH / 2, screenHeight / 2 - TerrainView.AUTO_SCROLL_DETECTION_WIDTH / 2, HtmlConstants.SCROLL_HTML1, true);
        TerrainView.getInstance().addTerrainScrollListener(this);
    }

    @Override
    public void blink() {
        if (speechBubble4 != null) {
            speechBubble1.blink();
            speechBubble2.blink();
            speechBubble3.blink();
            speechBubble4.blink();
        }
    }

    @Override
    public void closeBubble() {
        TerrainView.getInstance().removeTerrainScrollListener(this);
        if (speechBubble4 != null) {
            speechBubble1.close();
            speechBubble2.close();
            speechBubble3.close();
            speechBubble4.close();
            speechBubble1 = null;
            speechBubble2 = null;
            speechBubble3 = null;
            speechBubble4 = null;
        }
    }


    @Override
    public void onScroll(int left, int top, int width, int height, int deltaLeft, int deltaTop) {
        activateNextTask();
    }
}