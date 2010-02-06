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
import com.btxtech.game.jsre.common.gameengine.services.terrain.AbstractTerrainService;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImage;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImagePosition;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainSettings;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.widgetideas.graphics.client.ImageLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * User: beat
 * Date: 30.11.2009
 * Time: 22:58:26
 */
public class TerrainHandler extends AbstractTerrainService {
    private HashMap<Integer, ImageElement> terrainTileImageElements = new HashMap<Integer, ImageElement>();
    private ImageElement backgroundImage;

    public void setupTerrain(TerrainSettings terrainSettings) {
        setTerrainSettings(terrainSettings);
        loadBackgroundAndDrawMap();
    }

    public void setupTerrainImagePositions(List<TerrainImagePosition> terrainImagePositions) {
        setTerrainImagePositions(terrainImagePositions);
        loadImagesAndDrawMap();
    }

    public ImageElement getTileImageElement(int tileId) {
        ImageElement imageElement = terrainTileImageElements.get(tileId);
        if (imageElement == null) {
            loadImagesAndDrawMap();
            return terrainTileImageElements.get(tileId);
        }
        return imageElement;
    }

    public ImageElement getBackgroundImage() {
        return backgroundImage;
    }

    private void loadBackgroundAndDrawMap() {
        ImageLoader.loadImages(new String[]{ImageHandler.getTerrainBackgroundUrl()}, new ImageLoader.CallBack() {

            @Override
            public void onImagesLoaded(ImageElement[] imageElements) {
                try {
                    backgroundImage = imageElements[0];
                    fireTerrainChanged();
                } catch (Throwable throwable) {
                    GwtCommon.handleException(throwable);
                }
            }
        });
    }

    private void loadImagesAndDrawMap() {
        ArrayList<String> urls = new ArrayList<String>();
        for (TerrainImagePosition terrainImagePosition : getTerrainImagePositions()) {
            urls.add(ImageHandler.getTerrainImageUrl(terrainImagePosition.getImageId()));
        }
        ImageLoader.loadImages(urls.toArray(new String[urls.size()]), new ImageLoader.CallBack() {

            @Override
            public void onImagesLoaded(ImageElement[] imageElements) {
                terrainTileImageElements.clear();
                try {
                    for (int i = 0; i < imageElements.length; i++) {
                        terrainTileImageElements.put(getTerrainImagePositions().get(i).getImageId(), imageElements[i]);
                    }
                    fireTerrainChanged();
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

    public void moveTerrainImagePosition(int absX, int absY, TerrainImagePosition terrainImagePosition) {
        Index index = getTerrainTileIndexForAbsPosition(absX, absY);
        terrainImagePosition.setTileIndex(index);
        fireTerrainChanged();
    }

    public void removeTerrainImagePosition(TerrainImagePosition terrainImagePosition) {
        super.removeTerrainImagePosition(terrainImagePosition);
        fireTerrainChanged();
    }

}
