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

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImage;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImagePosition;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainUtil;

import java.util.Collection;

/**
 * User: beat
 * Date: Sep 3, 2009
 * Time: 6:26:18 PM
 */
public class TerrainImageModifier {
    private int imageId;
    private Index absolutePosition;
    private Index absoluteGridPosition;
    private Index relativeGridPosition;
    private TerrainImagePosition terrainImagePosition;
    private TerrainData terrainData;
    private boolean placeAllowed;
    private int width;
    private int height;
    private TerrainImage terrainImage;
    private TerrainImagePosition.ZIndex selectedZIndex;

    public TerrainImageModifier(TerrainImagePosition terrainImagePosition, Rectangle viewRectangle, TerrainData terrainData) {
        this.terrainImagePosition = terrainImagePosition;
        absolutePosition = TerrainUtil.getAbsolutIndexForTerrainTileIndex(terrainImagePosition.getTileIndex());
        setupGridPosition();
        TerrainImage terrainImage = TerrainView.getInstance().getTerrainHandler().getTerrainImageHandler().getTerrainImage(terrainImagePosition.getImageId());
        width = TerrainUtil.getAbsolutXForTerrainTile(terrainImage.getTileWidth());
        height = TerrainUtil.getAbsolutYForTerrainTile(terrainImage.getTileHeight());
        this.terrainData = terrainData;
        imageId = terrainImagePosition.getImageId();
        setupRelativePosition(viewRectangle);
        checkPlaceAllowed(null);
    }

    public TerrainImageModifier(TerrainImage terrainImage, TerrainImagePosition.ZIndex selectedZIndex, Index absolutePosition, Rectangle viewRectangle, TerrainData terrainData) {
        this.terrainImage = terrainImage;
        this.selectedZIndex = selectedZIndex;
        this.absolutePosition = absolutePosition;
        setupGridPosition();
        width = TerrainUtil.getAbsolutXForTerrainTile(terrainImage.getTileWidth());
        height = TerrainUtil.getAbsolutYForTerrainTile(terrainImage.getTileHeight());
        this.terrainData = terrainData;
        imageId = terrainImage.getId();
        setupRelativePosition(viewRectangle);
        checkPlaceAllowed(null);
    }

    public void onMouseMove(Index delta, Rectangle viewRectangle, Collection<TerrainImagePosition> exceptThem) {
        absolutePosition = absolutePosition.add(delta);
        setupGridPosition();
        setupRelativePosition(viewRectangle);
        checkPlaceAllowed(exceptThem);
    }

    public void onScroll(Rectangle viewRectangle) {
        setupRelativePosition(viewRectangle);
    }

    public Index getRelativeGridPosition() {
        return relativeGridPosition;
    }

    public int getImageId() {
        return imageId;
    }

    public boolean isPlaceAllowed() {
        return placeAllowed;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public TerrainImagePosition getTerrainImagePosition() {
        return terrainImagePosition;
    }

    public TerrainImagePosition.ZIndex getSelectedZIndex() {
        return selectedZIndex;
    }

    public TerrainImage getTerrainImage() {
        return terrainImage;
    }

    public void checkPlaceAllowed(Collection<TerrainImagePosition> exceptThem) {
        if (terrainImagePosition != null) {
            placeAllowed = !terrainData.hasTerrainImagesInRegion(new Rectangle(absoluteGridPosition.getX(), absoluteGridPosition.getY(), width, height), terrainImagePosition, exceptThem);
        } else {
            placeAllowed = !terrainData.hasTerrainImagesInRegion(new Rectangle(absoluteGridPosition.getX(), absoluteGridPosition.getY(), width, height), selectedZIndex, exceptThem);
        }
    }

    private void setupRelativePosition(Rectangle viewRectangle) {
        relativeGridPosition = absoluteGridPosition.sub(viewRectangle.getStart());
    }

    private void setupGridPosition() {
        absoluteGridPosition = TerrainUtil.moveAbsoluteToGrid(absolutePosition);
    }

    public void updateModel() {
        if (terrainImagePosition != null) {
            terrainData.moveTerrainImagePosition(absoluteGridPosition, terrainImagePosition);
        } else {
            terrainData.addNewTerrainImagePosition(absoluteGridPosition, terrainImage, selectedZIndex);
        }
    }
}
