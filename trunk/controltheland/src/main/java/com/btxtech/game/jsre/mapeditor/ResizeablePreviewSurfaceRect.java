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

import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceRect;

/**
 * User: beat
 * Date: 18.04.2010
 * Time: 13:27:28
 */
public class ResizeablePreviewSurfaceRect extends ResizeablePreviewWidget {
    private SurfaceRect surfaceRect;
    private TerrainData terrainData;

    public ResizeablePreviewSurfaceRect(TerrainData terrainData, SurfaceRect surfaceRect, Direction direction) {
        super(ImageHandler.getSurfaceImage(surfaceRect.getSurfaceImageId()),
                direction,
                TerrainView.getInstance().getTerrainHandler().convertToAbsolutePosition(surfaceRect.getTileRectangle()));
        this.terrainData = terrainData;
        this.surfaceRect = surfaceRect;
    }

    @Override
    protected void execute(Rectangle rectangle) {
        terrainData.moveSurfaceRect(rectangle, surfaceRect);
    }

    @Override
    protected Rectangle specialResize(Rectangle rectangle) {
        int offsetX = TerrainView.getInstance().getViewOriginLeft();
        int offsetY = TerrainView.getInstance().getViewOriginTop();
        rectangle.shift(offsetX, offsetY);
        rectangle = TerrainView.getInstance().getTerrainHandler().convertToTilePosition(rectangle);
        rectangle = TerrainView.getInstance().getTerrainHandler().convertToAbsolutePosition(rectangle);
        rectangle.shift(-offsetX, -offsetY);
        return rectangle;
    }

}
