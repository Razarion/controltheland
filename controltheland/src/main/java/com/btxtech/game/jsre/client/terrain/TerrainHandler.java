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

import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.client.control.task.DeferredStartup;
import com.btxtech.game.jsre.common.ImageLoader;
import com.btxtech.game.jsre.common.gameengine.services.terrain.AbstractTerrainServiceImpl;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceImage;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceRect;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImage;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImageBackground;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImagePosition;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainSettings;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainTile;
import com.google.gwt.dom.client.ImageElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * User: beat
 * Date: 30.11.2009
 * Time: 22:58:26
 */
public class TerrainHandler extends AbstractTerrainServiceImpl {
    private HashMap<Integer, ImageElement> terrainImageElements = new HashMap<Integer, ImageElement>();
    private HashMap<Integer, ImageElement> surfaceImageElements = new HashMap<Integer, ImageElement>();

    public void setupTerrain(TerrainSettings terrainSettings,
                             Collection<TerrainImagePosition> terrainImagePositions,
                             Collection<SurfaceRect> surfaceRects,
                             Collection<SurfaceImage> surfaceImages,
                             Collection<TerrainImage> terrainImages,
                             TerrainImageBackground terrainImageBackground) {
        setTerrainSettings(terrainSettings);
        setTerrainImageBackground(terrainImageBackground);
        setupImages(surfaceImages, terrainImages);
        createTerrainTileField(terrainImagePositions, surfaceRects);
    }

    public ImageElement getTerrainImageElement(int imageId) {
        return terrainImageElements.get(imageId);
    }

    public ImageElement getSurfaceImageElement(int tileId) {
        return surfaceImageElements.get(tileId);
    }

    public void loadImagesAndDrawMap(final DeferredStartup deferredStartup) {
        final ImageLoader<Integer> surfaceImageLoader = new ImageLoader<Integer>();
        final ImageLoader<Integer> terrainImageLoader = new ImageLoader<Integer>();

        iteratorOverAllTerrainTiles(null, new TerrainTileEvaluator() {
            TreeSet<Integer> addedSurfaceImageIds = new TreeSet<Integer>();
            TreeSet<Integer> addedTerrainImageIds = new TreeSet<Integer>();

            @Override
            public void evaluate(int x, int y, TerrainTile terrainTile) {
                if (terrainTile != null) {
                    if (terrainTile.isSurface()) {
                        if (!surfaceImageElements.containsKey(terrainTile.getImageId()) && addedSurfaceImageIds.add(terrainTile.getImageId())) {
                            surfaceImageLoader.addImageUrl(ImageHandler.getSurfaceImagesUrl(terrainTile.getImageId()), terrainTile.getImageId());
                        }
                    } else {
                        if (!terrainImageElements.containsKey(terrainTile.getImageId()) && addedTerrainImageIds.add(terrainTile.getImageId())) {
                            terrainImageLoader.addImageUrl(ImageHandler.getTerrainImageUrl(terrainTile.getImageId()), terrainTile.getImageId());
                        }
                    }
                }
            }
        });
        if (surfaceImageLoader.getUrlSize() == 0 && terrainImageLoader.getUrlSize() == 0) {
            fireTerrainChanged();
            deferredStartup.finished();
            return;
        }

        surfaceImageLoader.startLoading(new ImageLoader.Listener<Integer>() {
            @Override
            public void onLoaded(Map<Integer, ImageElement> imageElements) {
                try {
                    surfaceImageElements.putAll(imageElements);
                    if (terrainImageLoader.isLoaded()) {
                        fireTerrainChanged();
                        deferredStartup.finished();
                    }

                } catch (Throwable throwable) {
                    GwtCommon.handleException(throwable);
                    deferredStartup.failed(throwable);
                }
            }
        });
        terrainImageLoader.startLoading(new ImageLoader.Listener<Integer>() {
            @Override
            public void onLoaded(Map<Integer, ImageElement> imageElements) {
                try {
                    terrainImageElements.putAll(imageElements);
                    if (surfaceImageLoader.isLoaded()) {
                        fireTerrainChanged();
                        deferredStartup.finished();
                    }

                } catch (Throwable throwable) {
                    GwtCommon.handleException(throwable);
                    deferredStartup.failed(throwable);
                }
            }
        });
    }

    public HashMap<Integer, ImageElement> getTerrainImageElements() {
        return terrainImageElements;
    }

    public HashMap<Integer, ImageElement> getSurfaceImageElements() {
        return surfaceImageElements;
    }
}
