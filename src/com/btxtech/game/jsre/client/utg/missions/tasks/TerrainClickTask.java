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

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.client.utg.SpeechBubble;

/**
 * User: beat
 * Date: 17.02.2010
 * Time: 20:03:23
 */
public class TerrainClickTask extends Task {
    private static final int MIN_RADIUS = 50;
    private static final int MAX_RADIUS = 150;

    public TerrainClickTask(String html) {
        super(html);
    }

    @Override
    public String getName() {
        return "Terrain click task";
    }

    @Override
    public void run() {
        Index absPos = TerrainView.getInstance().getTerrainHandler().getAbsoluteFreeTerrainInRegion(getMission().getProtagonist().getSyncItem().getPosition(),
                MIN_RADIUS, MAX_RADIUS, 100);
        setSpeechBubble(new SpeechBubble(absPos.getX() - TerrainView.getInstance().getViewOriginLeft(),
                absPos.getY() - TerrainView.getInstance().getViewOriginTop(),
                getHtml(), false));
    }

}