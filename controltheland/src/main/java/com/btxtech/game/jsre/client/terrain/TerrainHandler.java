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
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.control.task.DeferredStartup;
import com.btxtech.game.jsre.common.gameengine.services.terrain.AbstractTerrainServiceImpl;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceImage;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceRect;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImage;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImagePosition;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainSettings;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.widgetideas.graphics.client.ImageLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
                             Collection<TerrainImage> terrainImages) {
        setTerrainSettings(terrainSettings);
        setTerrainImagePositions(terrainImagePositions);
        setSurfaceRects(surfaceRects);
        setupImages(surfaceImages, terrainImages);
    }

    public ImageElement getTerrainImageElement(int tileId) {
        return terrainImageElements.get(tileId);
    }

    public ImageElement getSurfaceImageElement(int tileId) {
        return surfaceImageElements.get(tileId);
    }

    public void loadImagesAndDrawMap(final DeferredStartup deferredStartup) {
        ArrayList<String> urls = new ArrayList<String>();
        final ArrayList<Integer> ids = new ArrayList<Integer>();
        TreeSet<Integer> addedIds = new TreeSet<Integer>();
        // Surface images
        for (SurfaceRect surfaceRect : getSurfaceRects()) {
            if(surfaceImageElements.containsKey(surfaceRect.getSurfaceImageId())) {
                continue;
            }
            if (!addedIds.contains(surfaceRect.getSurfaceImageId())) {
                addedIds.add(surfaceRect.getSurfaceImageId());
                ids.add(surfaceRect.getSurfaceImageId());
                urls.add(ImageHandler.getSurfaceImagesUrl(surfaceRect.getSurfaceImageId()));
            }
        }
        final int firstTerrainImageIndex = urls.size();
        // Terrain images
        addedIds.clear();
        for (TerrainImagePosition terrainImagePosition : getTerrainImagePositions()) {
            if(surfaceImageElements.containsKey(terrainImagePosition.getImageId())) {
                continue;
            }
            if (!addedIds.contains(terrainImagePosition.getImageId())) {
                addedIds.add(terrainImagePosition.getImageId());
                ids.add(terrainImagePosition.getImageId());
                urls.add(ImageHandler.getTerrainImageUrl(terrainImagePosition.getImageId()));
            }
        }
        if (urls.isEmpty()) {
            deferredStartup.finished();
            return;
        }
        ImageLoader.loadImages(urls.toArray(new String[urls.size()]), new ImageLoader.CallBack() {

            @Override
            public void onImagesLoaded(ImageElement[] imageElements) {
                try {
                    for (int i = 0; i < imageElements.length; i++) {
                        if (i < firstTerrainImageIndex) {
                            surfaceImageElements.put(ids.get(i), imageElements[i]);
                        } else {
                            terrainImageElements.put(ids.get(i), imageElements[i]);
                        }
                    }
                    fireTerrainChanged();
                    deferredStartup.finished();
                } catch (Throwable throwable) {
                    GwtCommon.handleException(throwable);
                    deferredStartup.failed(throwable);
                }
            }
        });

    }

    public void addNewTerrainImage(int absX, int absY, TerrainImage terrainImage) {
        Index index = getTerrainTileIndexForAbsPosition(absX, absY);
        addTerrainImagePosition(new TerrainImagePosition(index, terrainImage.getId()));
        fireTerrainChanged();
    }

    public void addNewSurfaceRect(int relX, int relY, int width, int height, SurfaceImage surfaceImage) {
        Rectangle tileRect = convertToTilePosition(new Rectangle(relX, relY, width, height));
        addSurfaceRect(new SurfaceRect(tileRect, surfaceImage.getImageId()));
        fireTerrainChanged();
    }

    public void moveTerrainImagePosition(int absX, int absY, TerrainImagePosition terrainImagePosition) {
        Index index = getTerrainTileIndexForAbsPosition(absX, absY);
        terrainImagePosition.setTileIndex(index);
        fireTerrainChanged();
    }


    public void moveSurfaceRect(int absX, int absY, SurfaceRect surfaceRect) {
        Index index = getTerrainTileIndexForAbsPosition(absX, absY);
        Rectangle rectangle = surfaceRect.getTileRectangle().moveTo(index.getX(), index.getY());
        surfaceRect.setTileRectangle(rectangle);
        fireTerrainChanged();
    }


    public void moveSurfaceRect(Rectangle rectangle, SurfaceRect surfaceRect) {
        Rectangle tileRect = convertToTilePosition(rectangle);
        surfaceRect.setTileRectangle(tileRect);
        fireTerrainChanged();
    }

    public void removeTerrainImagePosition(TerrainImagePosition terrainImagePosition) {
        super.removeTerrainImagePosition(terrainImagePosition);
        fireTerrainChanged();
    }

    public void removeSurfaceRect(SurfaceRect surfaceRect) {
        super.removeSurfaceRect(surfaceRect);
        fireTerrainChanged();
    }

    public HashMap<Integer, ImageElement> getTerrainImageElements() {
        return terrainImageElements;
    }


    public HashMap<Integer, ImageElement> getSurfaceImageElements() {
        return surfaceImageElements;
    }
}
