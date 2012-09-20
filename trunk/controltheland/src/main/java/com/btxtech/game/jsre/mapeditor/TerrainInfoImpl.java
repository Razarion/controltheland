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

import com.btxtech.game.jsre.common.TerrainInfo;
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
public class TerrainInfoImpl implements TerrainInfo {
    private TerrainSettings terrainSettings;
    private Collection<TerrainImagePosition> terrainImagePositions;
    private Collection<SurfaceRect> surfaceRects;
    private Collection<SurfaceImage> surfaceImages;
    private Collection<TerrainImage> terrainImages;
    private TerrainImageBackground terrainImageBackground;

    @Override
    public TerrainSettings getTerrainSettings() {
        return terrainSettings;
    }

    @Override
    public void setTerrainSettings(TerrainSettings terrainSettings) {
        this.terrainSettings = terrainSettings;
    }

    @Override
    public Collection<TerrainImagePosition> getTerrainImagePositions() {
        return terrainImagePositions;
    }

    @Override
    public void setTerrainImagePositions(Collection<TerrainImagePosition> terrainImagePositions) {
        this.terrainImagePositions = terrainImagePositions;
    }

    @Override
    public Collection<SurfaceRect> getSurfaceRects() {
        return surfaceRects;
    }

    @Override
    public void setSurfaceRects(Collection<SurfaceRect> surfaceRects) {
        this.surfaceRects = surfaceRects;
    }

    @Override
    public Collection<SurfaceImage> getSurfaceImages() {
        return surfaceImages;
    }

    @Override
    public void setSurfaceImages(Collection<SurfaceImage> surfaceImages) {
        this.surfaceImages = surfaceImages;
    }

    @Override
    public Collection<TerrainImage> getTerrainImages() {
        return terrainImages;
    }

    @Override
    public void setTerrainImages(Collection<TerrainImage> terrainImages) {
        this.terrainImages = terrainImages;
    }

    @Override
    public TerrainImageBackground getTerrainImageBackground() {
        return terrainImageBackground;
    }

    @Override
    public void setTerrainImageBackground(TerrainImageBackground terrainImageBackground) {
        this.terrainImageBackground = terrainImageBackground;
    }
}
