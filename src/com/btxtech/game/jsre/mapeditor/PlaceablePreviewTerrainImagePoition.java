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
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImage;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImagePosition;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseEvent;

/**
 * User: beat
 * Date: 10.01.2010
 * Time: 19:37:39
 */
public class PlaceablePreviewTerrainImagePoition extends PlaceablePreviewWidget {
    private TerrainImage terrainImage;
    private TerrainImagePosition terrainImagePosition;
    private MapModifier mapModifier;

    public PlaceablePreviewTerrainImagePoition(TerrainImagePosition terrainImagePosition, MouseEvent mouseEvent, MapModifier mapModifier) {
        super(ImageHandler.getTerrainImage(terrainImagePosition.getImageId()), mouseEvent);
        this.terrainImagePosition = terrainImagePosition;
        this.mapModifier = mapModifier;
    }

    public PlaceablePreviewTerrainImagePoition(TerrainImage terrainImage, MouseDownEvent mouseDownEvent, MapModifier mapModifier) {
        super(ImageHandler.getTerrainImage(terrainImage.getId()), mouseDownEvent);
        this.terrainImage = terrainImage;
        this.mapModifier = mapModifier;
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

        mapModifier.setPlaceablePreview(null);
    }

    @Override
    protected int specialMoveX(int x) {
        int offset = TerrainView.getInstance().getViewOriginLeft();
        int tileX = TerrainView.getInstance().getTerrainHandler().getTerrainTileIndexForAbsXPosition(x + offset);
        return TerrainView.getInstance().getTerrainHandler().getAbsolutXForTerrainTile(tileX) - offset;
    }

    @Override
    protected int specialMoveY(int y) {
        int offset = TerrainView.getInstance().getViewOriginTop();
        int tileY = TerrainView.getInstance().getTerrainHandler().getTerrainTileIndexForAbsYPosition(y + offset);
        return TerrainView.getInstance().getTerrainHandler().getAbsolutYForTerrainTile(tileY - offset);
    }

    @Override
    protected boolean allowedToPlace(int relX, int relY) {
        int absoluteX = relX + TerrainView.getInstance().getViewOriginLeft();
        int absoluteY = relY + TerrainView.getInstance().getViewOriginTop();
        TerrainImagePosition terrainImagePosition = TerrainView.getInstance().getTerrainHandler().getTerrainImagePosition(absoluteX, absoluteY);
        if (terrainImagePosition == null) {
            return true;
        } else {
            return terrainImagePosition.equals(this.terrainImagePosition);
        }
    }
}
