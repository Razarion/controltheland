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

import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceRect;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainUtil;

import java.util.Collection;

/**
 * User: beat
 * Date: 14.04.2010
 * Time: 12:19:47
 */
public class SurfaceModifier {
    private SurfaceRect surfaceRect;
    private Index absoluteMouse;
    private TerrainData terrainData;
    private EditMode editMode;
    private Rectangle newRectangle;
    private Rectangle newRelativeRectangle;
    private int imageId;
    private boolean placeAllowed;

    public SurfaceModifier(SurfaceRect surfaceRect, Index absoluteMouse, Rectangle viewRectangle, TerrainData terrainData, EditMode editMode) {
        imageId = surfaceRect.getSurfaceImageId();
        this.surfaceRect = surfaceRect;
        this.absoluteMouse = TerrainUtil.moveAbsoluteToGrid(absoluteMouse);
        this.terrainData = terrainData;
        this.editMode = editMode;
        newRectangle = TerrainUtil.convertToAbsolutePosition(surfaceRect.getTileRectangle());
        newRelativeRectangle = new Rectangle(newRectangle.getStart().sub(viewRectangle.getStart()), newRectangle.getEnd().sub(viewRectangle.getStart()));
        checkPlaceAllowed(null);
    }

    public SurfaceModifier(int imageId, Index absoluteMouse, Rectangle viewRectangle, TerrainData terrainData) {
        this.imageId = imageId;
        this.absoluteMouse = TerrainUtil.moveAbsoluteToGrid(absoluteMouse);
        this.terrainData = terrainData;
        editMode = new EditMode();
        newRectangle = new Rectangle(this.absoluteMouse.getX(), this.absoluteMouse.getY(), Constants.TERRAIN_TILE_WIDTH, Constants.TERRAIN_TILE_HEIGHT);
        newRelativeRectangle = new Rectangle(newRectangle.getStart().sub(viewRectangle.getStart()), newRectangle.getEnd().sub(viewRectangle.getStart()));
        checkPlaceAllowed(null);
    }

    public void onMouseMove(Index newAbsoluteMouse, Rectangle viewRectangle, Collection<SurfaceRect> surfaceRects) {
        newAbsoluteMouse = TerrainUtil.moveAbsoluteToGrid(newAbsoluteMouse);
        Index delta = newAbsoluteMouse.sub(absoluteMouse);
        absoluteMouse = newAbsoluteMouse;
        if (editMode.isNr() && editMode.isEr()) {
            newRectangle.growNorth(-delta.getY());
            newRectangle.growEast(delta.getX());
        } else if (editMode.isEr() && editMode.isSr()) {
            newRectangle.growSouth(delta.getY());
            newRectangle.growEast(delta.getX());
        } else if (editMode.isSr() && editMode.isWr()) {
            newRectangle.growSouth(delta.getY());
            newRectangle.growWest(-delta.getX());
        } else if (editMode.isWr() && editMode.isNr()) {
            newRectangle.growNorth(-delta.getY());
            newRectangle.growWest(-delta.getX());
        } else if (editMode.isNr()) {
            newRectangle.growNorth(-delta.getY());
        } else if (editMode.isEr()) {
            newRectangle.growEast(delta.getX());
        } else if (editMode.isSr()) {
            newRectangle.growSouth(delta.getY());
        } else if (editMode.isWr()) {
            newRectangle.growWest(-delta.getX());
        } else {
            newRectangle.shift(delta);
        }
        newRelativeRectangle = new Rectangle(newRectangle.getStart().sub(viewRectangle.getStart()), newRectangle.getEnd().sub(viewRectangle.getStart()));
        checkPlaceAllowed(surfaceRects);
    }

    public Rectangle getNewRelativeRectangle() {
        return newRelativeRectangle;
    }

    public Rectangle getNewRectangle() {
        return newRectangle;
    }

    public SurfaceRect getSurfaceRect() {
        return surfaceRect;
    }

    public int getImageId() {
        return imageId;
    }

    public boolean isPlaceAllowed() {
        return placeAllowed;
    }

    private void checkPlaceAllowed(Collection<SurfaceRect> surfaceRects) {
        placeAllowed = !terrainData.hasSurfaceRectInRegion(newRectangle, surfaceRect, surfaceRects);
    }

    public void onScroll(Rectangle viewRectangle) {
        newRelativeRectangle = new Rectangle(newRectangle.getStart().sub(viewRectangle.getStart()), newRectangle.getEnd().sub(viewRectangle.getStart()));
    }

    public void updateModel() {
        if (surfaceRect != null) {
            terrainData.moveSurfaceRect(newRectangle, surfaceRect);
        } else {
            terrainData.addNewSurfaceRect(newRectangle, imageId);
        }
    }

    public void resetMouseOffset(Index absoluteMouse) {
        this.absoluteMouse = TerrainUtil.moveAbsoluteToGrid(absoluteMouse);
    }
}
