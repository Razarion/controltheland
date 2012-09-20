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

package com.btxtech.game.jsre.client.terrain;

import com.btxtech.game.jsre.common.gameengine.services.terrain.AbstractTerrainImageServiceImpl;
import com.google.gwt.dom.client.ImageElement;

import java.util.HashMap;

/**
 * User: beat
 * Date: 30.11.2009
 * Time: 22:58:26
 */
public class TerrainImageHandler extends AbstractTerrainImageServiceImpl {
    private HashMap<Integer, ImageElement> terrainImageElements = new HashMap<Integer, ImageElement>();
    private HashMap<Integer, ImageElement> surfaceImageElements = new HashMap<Integer, ImageElement>();

    public ImageElement getTerrainImageElement(int imageId) {
        return terrainImageElements.get(imageId);
    }

    public ImageElement getSurfaceImageElement(int tileId) {
        return surfaceImageElements.get(tileId);
    }

    public HashMap<Integer, ImageElement> getTerrainImageElements() {
        return terrainImageElements;
    }

    public HashMap<Integer, ImageElement> getSurfaceImageElements() {
        return surfaceImageElements;
    }
}
