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
import com.btxtech.game.jsre.client.terrain.MapWindow;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.PlaceablePreviewWidget;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceImage;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceRect;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseEvent;

/**
 * User: beat
 * Date: 14.04.2010
 * Time: 12:02:23
 */
public class PlaceablePreviewSurfaceRect extends PlaceablePreviewWidget {
    private SurfaceImage surfaceImage;
    private SurfaceRect surfaceRect;
    private SurfaceModifier surfaceModifier;

    public PlaceablePreviewSurfaceRect(SurfaceImage surfaceImage, MouseEvent mouseEvent, SurfaceModifier surfaceModifier) {
        super(ImageHandler.getTerrainImage(surfaceImage.getImageId()), mouseEvent);
        this.surfaceImage = surfaceImage;
        this.surfaceModifier = surfaceModifier;
    }

    public PlaceablePreviewSurfaceRect(SurfaceRect surfaceRect, MouseDownEvent mouseEvent, SurfaceModifier surfaceModifier) {
        super(ImageHandler.getTerrainImage(surfaceRect.getSurfaceImageId()), mouseEvent);
        this.surfaceRect = surfaceRect;
        this.surfaceModifier = surfaceModifier;
    }

    @Override
    protected void execute(MouseEvent event) {
        int relX = event.getRelativeX(MapWindow.getAbsolutePanel().getElement());
        int relY = event.getRelativeY(MapWindow.getAbsolutePanel().getElement());
        if (relX < 0 || relY < 0) {
            return;
        }
        if (surfaceRect != null) {
            TerrainView.getInstance().moveSurfaceRect(relX, relY, surfaceRect);
        } else {
            TerrainView.getInstance().addNewSurfaceRect(relX, relY, 100, 100, surfaceImage); // TODO remove 100 100
        }

        surfaceModifier.setPlaceablePreview(null);
    }
}
