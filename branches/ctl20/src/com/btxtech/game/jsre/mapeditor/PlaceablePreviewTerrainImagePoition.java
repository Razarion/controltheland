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
import com.btxtech.game.jsre.client.terrain.MapWindow;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.PlaceablePreviewWidget;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImage;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImagePosition;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseEvent;
import java.util.List;

/**
 * User: beat
 * Date: 10.01.2010
 * Time: 19:37:39
 */
public class PlaceablePreviewTerrainImagePoition extends PlaceablePreviewWidget {
    private TerrainImage terrainImage;
    private TerrainImagePosition terrainImagePosition;
    private TerrainImageModifier terrainImageModifier;

    public PlaceablePreviewTerrainImagePoition(TerrainImagePosition terrainImagePosition, MouseEvent mouseEvent, TerrainImageModifier terrainImageModifier) {
        super(ImageHandler.getTerrainImage(terrainImagePosition.getImageId()), mouseEvent);
        this.terrainImagePosition = terrainImagePosition;
        this.terrainImageModifier = terrainImageModifier;
    }

    public PlaceablePreviewTerrainImagePoition(TerrainImage terrainImage, MouseDownEvent mouseDownEvent, TerrainImageModifier terrainImageModifier) {
        super(ImageHandler.getTerrainImage(terrainImage.getId()), mouseDownEvent);
        this.terrainImage = terrainImage;
        this.terrainImageModifier = terrainImageModifier;
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

        terrainImageModifier.setPlaceablePreview(null);
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
        return TerrainView.getInstance().getTerrainHandler().getAbsolutYForTerrainTile(tileY) - offset;
    }

    @Override
    protected boolean allowedToPlace(int relX, int relY) {
        int tileX = TerrainView.getInstance().getTerrainHandler().getTerrainTileIndexForAbsXPosition(relX + TerrainView.getInstance().getViewOriginLeft());
        int tileY = TerrainView.getInstance().getTerrainHandler().getTerrainTileIndexForAbsYPosition(relY + TerrainView.getInstance().getViewOriginTop());
        TerrainImage tmpTerrainImage;
        if (terrainImage != null) {
            tmpTerrainImage = terrainImage;
        } else {
            tmpTerrainImage = TerrainView.getInstance().getTerrainHandler().getTerrainImage(terrainImagePosition);
        }
        if (tileX < 0 || tileY < 0) {
            return false;
        }
        Rectangle rectangle = new Rectangle(tileX, tileY, tmpTerrainImage.getTileWidth(), tmpTerrainImage.getTileHeight());
        rectangle = TerrainView.getInstance().getTerrainHandler().convertToAbsolutePosition(rectangle);
        List<TerrainImagePosition> terrainImagePositions = TerrainView.getInstance().getTerrainHandler().getTerrainImagesInRegion(rectangle);
        if (terrainImagePositions.isEmpty()) {
            return true;
        } else if (terrainImagePositions.size() == 1) {
            return terrainImagePositions.get(0).equals(this.terrainImagePosition);
        } else {
            return false;
        }
    }

}
