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

package com.btxtech.game.jsre.common.gameengine.services.terrain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * User: beat
 * Date: 05.02.2010
 * Time: 22:28:01
 */
public class CommonTerrainImageServiceImpl implements CommonTerrainImageService {
    private Map<Integer, TerrainImage> terrainImages = new HashMap<Integer, TerrainImage>();
    private Map<Integer, SurfaceImage> surfaceImages = new HashMap<Integer, SurfaceImage>();
    private TerrainImageBackground terrainImageBackground;

    @Override
    public TerrainImageBackground getTerrainImageBackground() {
        return terrainImageBackground;
    }

    public void setTerrainImageBackground(TerrainImageBackground terrainImageBackground) {
        this.terrainImageBackground = terrainImageBackground;
    }

    public void setupImages(Collection<SurfaceImage> surfaceImages, Collection<TerrainImage> terrainImages) {
        clearSurfaceImages();
        for (SurfaceImage surfaceImage : surfaceImages) {
            putSurfaceImage(surfaceImage);
        }
        clearTerrainImages();
        for (TerrainImage terrainImage : terrainImages) {
            putTerrainImage(terrainImage);
        }
    }

    protected void clearTerrainImages() {
        terrainImages.clear();
    }

    protected void putTerrainImage(TerrainImage terrainImage) {
        terrainImages.put(terrainImage.getId(), terrainImage);
    }

    protected void clearSurfaceImages() {
        surfaceImages.clear();
    }

    protected void putSurfaceImage(SurfaceImage surfaceImage) {
        surfaceImages.put(surfaceImage.getImageId(), surfaceImage);
    }

    @Override
    public Collection<TerrainImage> getTerrainImages() {
        return new ArrayList<TerrainImage>(terrainImages.values());
    }

    @Override
    public SurfaceImage getSurfaceImage(int surfaceImageId) {
        SurfaceImage surfaceImage = surfaceImages.get(surfaceImageId);
        if (surfaceImage == null) {
            throw new IllegalArgumentException(this + " getSurfaceImage(): image id does not exit: " + surfaceImageId);
        }
        return surfaceImage;
    }

    @Override
    public Collection<SurfaceImage> getSurfaceImages() {
        return new ArrayList<SurfaceImage>(surfaceImages.values());
    }

    @Override
    public TerrainImage getTerrainImage(int terrainImageId) {
        TerrainImage terrainImage = terrainImages.get(terrainImageId);
        if (terrainImage == null) {
            throw new IllegalArgumentException(this + " getTerrainImagePosRect(): image id does not exit: " + terrainImageId);
        }
        return terrainImage;
    }
}
