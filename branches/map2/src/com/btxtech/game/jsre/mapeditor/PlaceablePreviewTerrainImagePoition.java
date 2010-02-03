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
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImagePosition;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImage;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;

/**
 * User: beat
 * Date: 10.01.2010
 * Time: 19:37:39
 */
public class PlaceablePreviewTerrainImagePoition extends PlaceablePreviewWidget {
    private TerrainImage terrainImage;
    private TerrainImagePosition terrainImagePosition;

    public PlaceablePreviewTerrainImagePoition(TerrainImagePosition terrainImagePosition, MouseEvent mouseEvent) {
        super(ImageHandler.getTerrainImage(terrainImagePosition.getImageId()), mouseEvent);
        this.terrainImagePosition = terrainImagePosition;
    }

    public PlaceablePreviewTerrainImagePoition(TerrainImage terrainImage, MouseDownEvent mouseDownEvent) {
        super(ImageHandler.getTerrainImage(terrainImage.getId()), mouseDownEvent);
        this.terrainImage = terrainImage;
    }

    @Override
    protected void execute(MouseEvent event) {
        int relX = event.getRelativeX(MapWindow.getAbsolutePanel().getElement());
        int relY = event.getRelativeY(MapWindow.getAbsolutePanel().getElement());
        if (relX < 0 || relY < 0) {
            return;
        }
        if (terrainImagePosition != null) {
            TerrainView.getInstance().moveTerrainImagePosition(relX, relY, terrainImagePosition);
        } else {
            TerrainView.getInstance().addNewTerrainImagePosition(relX, relY, terrainImage);
        }
    }

    @Override
    protected int specialMoveX(int x) {
        int tileX = TerrainView.getInstance().getTerrainHandler().getTerrainTileIndexForAbsXPosition(x);
        return TerrainView.getInstance().getTerrainHandler().getAbsolutXForTerrainTile(tileX);
    }

    @Override
    protected int specialMoveY(int y) {
        int tileY = TerrainView.getInstance().getTerrainHandler().getTerrainTileIndexForAbsYPosition(y);
        return TerrainView.getInstance().getTerrainHandler().getAbsolutYForTerrainTile(tileY);
    }
}
