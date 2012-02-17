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

import com.btxtech.game.jsre.client.ColorConstants;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.terrain.MapWindow;
import com.btxtech.game.jsre.client.terrain.TerrainMouseMoveListener;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.Html5NotSupportedException;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImage;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImagePosition;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;

/**
 * User: beat
 * Date: Sep 3, 2009
 * Time: 6:26:18 PM
 */
public class TerrainImageModifier implements TerrainMouseMoveListener, MouseDownHandler {
    public static final int LINE_WIDTH = 2;
    private Cockpit cockpit;
    private Canvas marker;
    private Context2d context2d;

    public TerrainImageModifier(Cockpit cockpit) {
        this.cockpit = cockpit;
        marker = Canvas.createIfSupported();
        if (marker == null) {
            throw new Html5NotSupportedException("TerrainImageModifier: Canvas not supported.");
        }
        marker.setCoordinateSpaceWidth(100);
        marker.setCoordinateSpaceHeight(100);
        context2d = marker.getContext2d();
        marker.getElement().getStyle().setZIndex(Constants.Z_INDEX_GROUP_SELECTION_FRAME);
        MapWindow.getAbsolutePanel().add(marker, 0, 0);
        marker.setVisible(false);
        context2d.setStrokeStyle(ColorConstants.BLACK);
        marker.addMouseDownHandler(this);
    }

    @Override
    public void onMouseDown(MouseDownEvent mouseDownEvent) {
        int relX = mouseDownEvent.getRelativeX(MapWindow.getAbsolutePanel().getElement());
        int relY = mouseDownEvent.getRelativeY(MapWindow.getAbsolutePanel().getElement());
        if (cockpit.isInside(relX, relY) || cockpit.isInside(relX, relY)) {
            return;
        }

        marker.setVisible(false);
        int absoluteX = relX + TerrainView.getInstance().getViewOriginLeft();
        int absoluteY = relY + TerrainView.getInstance().getViewOriginTop();
        TerrainImagePosition terrainImagePosition = TerrainView.getInstance().getTerrainHandler().getTerrainImagePosition(absoluteX, absoluteY);
        GwtCommon.preventDefault(mouseDownEvent);
        if (terrainImagePosition == null) {
            return;
        }

        if (cockpit.isDeleteModus()) {
            TerrainView.getInstance().getTerrainHandler().removeTerrainImagePosition(terrainImagePosition);
        } else {
            new PlaceablePreviewTerrainImagePoition(terrainImagePosition, mouseDownEvent);
        }
    }

    @Override
    public void onMove(int absoluteLeft, int absoluteTop, int relativeLeft, int relativeTop) {
        if (cockpit.isInside(relativeLeft, relativeTop)) {
            marker.setVisible(false);
            return;
        }

        TerrainImagePosition terrainImagePosition = TerrainView.getInstance().getTerrainHandler().getTerrainImagePosition(absoluteLeft, absoluteTop);
        if (terrainImagePosition != null) {
            marker.setVisible(true);
            TerrainImage terrainImage = TerrainView.getInstance().getTerrainHandler().getTerrainImage(terrainImagePosition);
            Index absolute = TerrainView.getInstance().getTerrainHandler().getAbsolutIndexForTerrainTileIndex(terrainImagePosition.getTileIndex());
            MapWindow.getAbsolutePanel().setWidgetPosition(marker,
                    absolute.getX() - TerrainView.getInstance().getViewOriginLeft(),
                    absolute.getY() - TerrainView.getInstance().getViewOriginTop());
            Index size = TerrainView.getInstance().getTerrainHandler().getAbsolutIndexForTerrainTileIndex(terrainImage.getTileWidth(), terrainImage.getTileHeight());
            marker.setCoordinateSpaceWidth(size.getX());
            marker.setCoordinateSpaceHeight(size.getY());
            context2d.setLineWidth(LINE_WIDTH);
            context2d.clearRect(0, 0, size.getX(), size.getY());
            context2d.rect(LINE_WIDTH / 2,
                    LINE_WIDTH / 2,
                    size.getX() - LINE_WIDTH,
                    size.getY() - LINE_WIDTH);
            context2d.stroke();
        } else {
            marker.setVisible(false);
        }
    }
}