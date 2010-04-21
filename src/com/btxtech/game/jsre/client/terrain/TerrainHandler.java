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
import com.btxtech.game.jsre.client.utg.ClientUserTracker;
import com.btxtech.game.jsre.common.gameengine.services.terrain.AbstractTerrainServiceImpl;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceImage;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceRect;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImage;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImagePosition;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainSettings;
import com.btxtech.game.jsre.common.gameengine.services.utg.GameStartupState;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.widgetideas.graphics.client.ImageLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

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
                             Collection<SurfaceImage> surfaceImages,
                             Collection<TerrainImage> terrainImages) {
        setTerrainSettings(terrainSettings);
        setTerrainImagePositions(terrainImagePositions);
        setupTerrainImages(surfaceImages, terrainImages);
        loadSurfaceImagesAndDrawMap();
        loadImagesAndDrawMap();
    }

    public ImageElement getTerrainImageElement(int tileId) {
        return terrainImageElements.get(tileId);
    }

    public ImageElement getSurfaceImageElement(int tileId) {
        return surfaceImageElements.get(tileId);
    }

    protected void loadSurfaceImagesAndDrawMap() {
        ArrayList<String> urls = new ArrayList<String>();
        final ArrayList<Integer> ids = new ArrayList<Integer>();
        for (SurfaceImage surfaceImage : getSurfaceImages()) {
            urls.add(ImageHandler.getSurfaceImagesUrl(surfaceImage.getImageId()));
            ids.add(surfaceImage.getImageId());
        }

        ImageLoader.loadImages(urls.toArray(new String[urls.size()]), new ImageLoader.CallBack() {

            @Override
            public void onImagesLoaded(ImageElement[] imageElements) {
                surfaceImageElements.clear();

                try {
                    for (int i = 0; i < imageElements.length; i++) {
                        surfaceImageElements.put(ids.get(i), imageElements[i]);
                    }
                    if (!terrainImageElements.isEmpty()) {
                        fireTerrainChanged();
                    }
                    ClientUserTracker.getInstance().sandGameStartupState(GameStartupState.CLIENT_MAP_SURFACE_IMAGES_LOADED);
                } catch (Throwable throwable) {
                    GwtCommon.handleException(throwable);
                }
            }
        });
    }

    public void loadImagesAndDrawMap() {
        ArrayList<String> urls = new ArrayList<String>();
        final ArrayList<Integer> ids = new ArrayList<Integer>();
        for (TerrainImage terrainImage : getTerrainImages()) {
            urls.add(ImageHandler.getTerrainImageUrl(terrainImage.getId()));
            ids.add(terrainImage.getId());
        }
        ImageLoader.loadImages(urls.toArray(new String[urls.size()]), new ImageLoader.CallBack() {

            @Override
            public void onImagesLoaded(ImageElement[] imageElements) {
                terrainImageElements.clear();
                try {
                    for (int i = 0; i < imageElements.length; i++) {
                        terrainImageElements.put(ids.get(i), imageElements[i]);
                    }
                    if (!surfaceImageElements.isEmpty()) {
                        fireTerrainChanged();
                    }
                    ClientUserTracker.getInstance().sandGameStartupState(GameStartupState.CLIENT_MAP_IMAGES_LOADED);
                } catch (Throwable throwable) {
                    GwtCommon.handleException(throwable);
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

    public HashMap<Integer, ImageElement> getTerrainImageElements() {
        return terrainImageElements;
    }


    public HashMap<Integer, ImageElement> getSurfaceImageElements() {
        return surfaceImageElements;
    }
}
