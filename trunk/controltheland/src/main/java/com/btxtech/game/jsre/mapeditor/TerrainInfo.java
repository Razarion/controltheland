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

package com.btxtech.game.jsre.mapeditor;

import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceImage;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceRect;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImage;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImageBackground;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImagePosition;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainSettings;

import java.io.Serializable;
import java.util.Collection;

/**
 * User: beat
 * Date: 07.02.2010
 * Time: 13:30:32
 */
public class TerrainInfo implements Serializable {
    private TerrainSettings terrainSettings;
    private Collection<TerrainImagePosition> terrainImagePositions;
    private Collection<SurfaceRect> surfaceRects;
    private Collection<SurfaceImage> surfaceImages;
    private Collection<TerrainImage> terrainImages;
    private TerrainImageBackground terrainImageBackground;

    public TerrainSettings getTerrainSettings() {
        return terrainSettings;
    }

    public void setTerrainSettings(TerrainSettings terrainSettings) {
        this.terrainSettings = terrainSettings;
    }

    public Collection<TerrainImagePosition> getTerrainImagePositions() {
        return terrainImagePositions;
    }

    public void setTerrainImagePositions(Collection<TerrainImagePosition> terrainImagePositions) {
        this.terrainImagePositions = terrainImagePositions;
    }

    public Collection<SurfaceRect> getSurfaceRects() {
        return surfaceRects;
    }

    public void setSurfaceRects(Collection<SurfaceRect> surfaceRects) {
        this.surfaceRects = surfaceRects;
    }

    public Collection<SurfaceImage> getSurfaceImages() {
        return surfaceImages;
    }

    public void setSurfaceImages(Collection<SurfaceImage> surfaceImages) {
        this.surfaceImages = surfaceImages;
    }

    public Collection<TerrainImage> getTerrainImages() {
        return terrainImages;
    }

    public void setTerrainImages(Collection<TerrainImage> terrainImages) {
        this.terrainImages = terrainImages;
    }

    public TerrainImageBackground getTerrainImageBackground() {
        return terrainImageBackground;
    }

    public void setTerrainImageBackground(TerrainImageBackground terrainImageBackground) {
        this.terrainImageBackground = terrainImageBackground;
    }
}
