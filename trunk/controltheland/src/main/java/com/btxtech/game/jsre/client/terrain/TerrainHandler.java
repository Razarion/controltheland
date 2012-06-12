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
import java.util.TreeSet;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 30.11.2009
 * Time: 22:58:26
 */
public class TerrainHandler extends AbstractTerrainServiceImpl {
    private HashMap<Integer, ImageElement> terrainImageElements = new HashMap<Integer, ImageElement>();
    private HashMap<Integer, ImageElement> surfaceImageElements = new HashMap<Integer, ImageElement>();
    private boolean allTerrainImagesLoaded;
    private boolean allSurfaceImagesLoaded;
    //private Logger log = Logger.getLogger(TerrainHandler.class.getName());

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
        final ArrayList<Integer> surfaceImageIds = new ArrayList<Integer>();
        final ArrayList<Integer> terrainImageIds = new ArrayList<Integer>();
        final List<String> surfaceImageUrls = new ArrayList<String>();
        final List<String> terrainImagesUrls = new ArrayList<String>();
        allSurfaceImagesLoaded = false;
        allTerrainImagesLoaded = false;

        iteratorOverAllTerrainTiles(null, new TerrainTileEvaluator() {
            TreeSet<Integer> addedSurfaceImageIds = new TreeSet<Integer>();
            TreeSet<Integer> addedTerrainImageIds = new TreeSet<Integer>();

            @Override
            public void evaluate(int x, int y, TerrainTile terrainTile) {
                if (terrainTile != null) {
                    if (terrainTile.isSurface()) {
                        if (!surfaceImageElements.containsKey(terrainTile.getImageId()) && addedSurfaceImageIds.add(terrainTile.getImageId())) {
                            surfaceImageUrls.add(ImageHandler.getSurfaceImagesUrl(terrainTile.getImageId()));
                            surfaceImageIds.add(terrainTile.getImageId());
                        }
                    } else {
                        if (!terrainImageElements.containsKey(terrainTile.getImageId()) && addedTerrainImageIds.add(terrainTile.getImageId())) {
                            terrainImagesUrls.add(ImageHandler.getTerrainImageUrl(terrainTile.getImageId()));
                            terrainImageIds.add(terrainTile.getImageId());
                        }
                    }
                }
            }
        });

        if (surfaceImageUrls.isEmpty() && terrainImagesUrls.isEmpty()) {
            deferredStartup.finished();
            return;
        }
        if (!surfaceImageUrls.isEmpty()) {
            ImageLoader.addImageUrlsAndStart(surfaceImageUrls, new ImageLoader.Listener() {
                @Override
                public void onLoaded(ImageElement[] imageElements) {
                    try {
                        for (int i = 0; i < imageElements.length; i++) {
                            surfaceImageElements.put(surfaceImageIds.get(i), imageElements[i]);
                        }
                        surfaceImageIds.clear();
                        allSurfaceImagesLoaded = true;
                        if (allTerrainImagesLoaded) {
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
        surfaceImageUrls.clear();
        if (!terrainImagesUrls.isEmpty()) {
            ImageLoader.addImageUrlsAndStart(terrainImagesUrls, new ImageLoader.Listener() {
                @Override
                public void onLoaded(ImageElement[] imageElements) {
                    try {
                        for (int i = 0; i < imageElements.length; i++) {
                            terrainImageElements.put(terrainImageIds.get(i), imageElements[i]);
                        }
                        terrainImageIds.clear();
                        allTerrainImagesLoaded = true;
                        if (allSurfaceImagesLoaded) {
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
        terrainImagesUrls.clear();

    }

    public HashMap<Integer, ImageElement> getTerrainImageElements() {
        return terrainImageElements;
    }

    public HashMap<Integer, ImageElement> getSurfaceImageElements() {
        return surfaceImageElements;
    }
}
