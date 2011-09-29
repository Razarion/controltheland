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
import com.btxtech.game.jsre.common.ResizeablePreviewWidget;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceRect;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;

/**
 * User: beat
 * Date: 14.04.2010
 * Time: 12:19:47
 */
public class SurfaceModifier implements TerrainMouseMoveListener, MouseDownHandler {
    public static final int LINE_WIDTH = 2;
    public static final int RESIZE_CURSOR_SPACE = 10;
    private Canvas marker;
    private Context2d context2d;
    private Cockpit cockpit;
    private boolean nr;
    private boolean er;
    private boolean sr;
    private boolean wr;

    public SurfaceModifier(Cockpit cockpit) {
        this.cockpit = cockpit;
        marker = Canvas.createIfSupported();
        if (marker == null) {
            throw new Html5NotSupportedException("SurfaceModifier: Canvas not supported.");
        }
        marker.setCoordinateSpaceWidth(100);
        marker.setCoordinateSpaceHeight(100);
        marker.getElement().getStyle().setZIndex(Constants.Z_INDEX_GROUP_SELECTION_FRAME);
        MapWindow.getAbsolutePanel().add(marker, 0, 0);
        context2d = marker.getContext2d();
        marker.setVisible(false);
        context2d.setStrokeStyle(ColorConstants.BLACK);
        marker.addMouseDownHandler(this);
    }

    @Override
    public void onMove(int absoluteLeft, int absoluteTop, int relativeLeft, int relativeTop) {
        if (cockpit.isInside(relativeLeft, relativeTop)) {
            marker.setVisible(false);
            return;
        }

        SurfaceRect surfaceRect = TerrainView.getInstance().getTerrainHandler().getSurfaceRect(absoluteLeft, absoluteTop);
        if (surfaceRect != null) {
            marker.setVisible(true);
            Index size = TerrainView.getInstance().getTerrainHandler().getAbsolutIndexForTerrainTileIndex(surfaceRect.getTileWidth(), surfaceRect.getTileHeight());
            Index absolute = TerrainView.getInstance().getTerrainHandler().getAbsolutIndexForTerrainTileIndex(surfaceRect.getTileIndex());
            int markerX = absolute.getX() - TerrainView.getInstance().getViewOriginLeft();
            int markerY = absolute.getY() - TerrainView.getInstance().getViewOriginTop();
            MapWindow.getAbsolutePanel().setWidgetPosition(marker, markerX, markerY);
            marker.setCoordinateSpaceWidth(size.getX());
            marker.setCoordinateSpaceHeight(size.getY());
            context2d.setLineWidth(LINE_WIDTH);
            context2d.clearRect(0, 0, size.getX(), size.getY());
            context2d.rect(LINE_WIDTH / 2,
                    LINE_WIDTH / 2,
                    size.getX() - LINE_WIDTH,
                    size.getY() - LINE_WIDTH);
            context2d.stroke();
            int inMarkerX = absoluteLeft - markerX - TerrainView.getInstance().getViewOriginLeft();
            int inMarkerY = absoluteTop - markerY - TerrainView.getInstance().getViewOriginTop();
            nr = false;
            er = false;
            sr = false;
            wr = false;
            if (inMarkerY >= 0 && inMarkerY <= RESIZE_CURSOR_SPACE) {
                nr = true;
            }
            if (inMarkerX >= size.getX() - RESIZE_CURSOR_SPACE && inMarkerX <= size.getX()) {
                er = true;
            }
            if (inMarkerY >= size.getY() - RESIZE_CURSOR_SPACE && inMarkerY <= size.getY()) {
                sr = true;
            }
            if (inMarkerX >= 0 && inMarkerX <= RESIZE_CURSOR_SPACE) {
                wr = true;
            }
            if (nr && er) {
                setCursor(Style.Cursor.NE_RESIZE);
            } else if (er && sr) {
                setCursor(Style.Cursor.SE_RESIZE);
            } else if (sr && wr) {
                setCursor(Style.Cursor.SW_RESIZE);
            } else if (wr && nr) {
                setCursor(Style.Cursor.NW_RESIZE);
            } else if (nr) {
                setCursor(Style.Cursor.N_RESIZE);
            } else if (er) {
                setCursor(Style.Cursor.E_RESIZE);
            } else if (sr) {
                setCursor(Style.Cursor.S_RESIZE);
            } else if (wr) {
                setCursor(Style.Cursor.W_RESIZE);
            } else {
                setCursor(Style.Cursor.MOVE);
            }
        } else {
            marker.setVisible(false);
        }
    }

    @Override
    public void onMouseDown(MouseDownEvent mouseDownEvent) {
        int relX = mouseDownEvent.getRelativeX(MapWindow.getAbsolutePanel().getElement());
        int relY = mouseDownEvent.getRelativeY(MapWindow.getAbsolutePanel().getElement());
        if (cockpit.isInside(relX, relY)) {
            return;
        }

        int absoluteX = relX + TerrainView.getInstance().getViewOriginLeft();
        int absoluteY = relY + TerrainView.getInstance().getViewOriginTop();
        GwtCommon.preventDefault(mouseDownEvent);
        SurfaceRect surfaceRect = TerrainView.getInstance().getTerrainHandler().getSurfaceRect(absoluteX, absoluteY);
        if (surfaceRect == null) {
            return;
        }
        if (nr && er) {
            new ResizeablePreviewSurfaceRect(surfaceRect, ResizeablePreviewWidget.Direction.NORTH_EAST);
        } else if (er && sr) {
            new ResizeablePreviewSurfaceRect(surfaceRect, ResizeablePreviewWidget.Direction.SOUTH_EAST);
        } else if (sr && wr) {
            new ResizeablePreviewSurfaceRect(surfaceRect, ResizeablePreviewWidget.Direction.SOUTH_WEST);
        } else if (wr && nr) {
            new ResizeablePreviewSurfaceRect(surfaceRect, ResizeablePreviewWidget.Direction.NORTH_WEST);
        } else if (nr) {
            new ResizeablePreviewSurfaceRect(surfaceRect, ResizeablePreviewWidget.Direction.NORTH);
        } else if (er) {
            new ResizeablePreviewSurfaceRect(surfaceRect, ResizeablePreviewWidget.Direction.EAST);
        } else if (sr) {
            new ResizeablePreviewSurfaceRect(surfaceRect, ResizeablePreviewWidget.Direction.SOUTH);
        } else if (wr) {
            new ResizeablePreviewSurfaceRect(surfaceRect, ResizeablePreviewWidget.Direction.WEST);
        } else {
            move(surfaceRect, mouseDownEvent);
        }
    }

    private void move(SurfaceRect surfaceRect, MouseDownEvent mouseDownEvent) {
        marker.setVisible(false);

        if (cockpit.isDeleteModus()) {
            TerrainView.getInstance().getTerrainHandler().removeSurfaceRect(surfaceRect);
        } else {
            new PlaceablePreviewSurfaceRect(surfaceRect, mouseDownEvent);
        }
    }

    private void setCursor(Style.Cursor cursor) {
        marker.getElement().getStyle().setCursor(cursor);
    }

}
