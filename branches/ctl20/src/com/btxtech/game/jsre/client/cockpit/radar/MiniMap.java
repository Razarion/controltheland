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

package com.btxtech.game.jsre.client.cockpit.radar;

import com.btxtech.game.jsre.client.ExtendedCanvas;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainSettings;

/**
 * User: beat
 * Date: 22.12.2009
 * Time: 21:50:26
 */
public class MiniMap extends ExtendedCanvas {
    private int height;
    private int width;
    private double scale = 1.0;
    private TerrainSettings terrainSettings;

    public MiniMap(int width, int height) {
        super(width, height);
        this.height = height;
        this.width = width;

    }

    public double getScale() {
        return scale;
    }

    public void onTerrainSettings(TerrainSettings terrainSettings) {
        this.terrainSettings = terrainSettings;
        scale = Math.min((double) width / (double) terrainSettings.getPlayFieldXSize(),
                (double) height / (double) terrainSettings.getPlayFieldYSize());
        scale(scale, scale);
    }

    public TerrainSettings getTerrainSettings() {
        return terrainSettings;
    }
}
