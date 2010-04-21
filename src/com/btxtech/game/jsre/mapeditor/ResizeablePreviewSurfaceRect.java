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
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.ResizeablePreviewWidget;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceRect;

/**
 * User: beat
 * Date: 18.04.2010
 * Time: 13:27:28
 */
public class ResizeablePreviewSurfaceRect extends ResizeablePreviewWidget {
    private SurfaceRect surfaceRect;
    private SurfaceModifier surfaceModifier;

    public ResizeablePreviewSurfaceRect(SurfaceModifier surfaceModifier, SurfaceRect surfaceRect, Rectangle origin, Direction direction) {
        super(ImageHandler.getSurfaceImage(surfaceRect.getSurfaceImageId()), origin, direction);
        this.surfaceModifier = surfaceModifier;
        this.surfaceRect = surfaceRect;
    }

    @Override
    protected void execute(Rectangle rectangle) {
        TerrainView.getInstance().moveSurfaceRect(rectangle, surfaceRect);
        surfaceModifier.setPlaceablePreview(null);
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
