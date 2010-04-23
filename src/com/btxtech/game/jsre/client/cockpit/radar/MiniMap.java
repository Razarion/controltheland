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
    private double scaleX = 1.0;
    private double scaleY = 1.0;
    private TerrainSettings terrainSettings;


    public MiniMap(int width, int height) {
        super(width, height);
        this.height = height;
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public double getScaleX() {
        return scaleX;
    }

    public double getScaleY() {
        return scaleY;
    }

    public void onTerrainSettings(TerrainSettings terrainSettings) {
        this.terrainSettings = terrainSettings;
        scaleX = (double) getWidth() / (double) terrainSettings.getPlayFieldYSize();
        scaleY = (double) getHeight() / (double) terrainSettings.getPlayFieldYSize();
        scale(scaleX, scaleY);
    }

    public TerrainSettings getTerrainSettings() {
        return terrainSettings;
    }
}
