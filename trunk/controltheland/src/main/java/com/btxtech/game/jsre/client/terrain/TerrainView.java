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

package com.btxtech.game.jsre.client.terrain;

import com.btxtech.game.jsre.client.ClientSyncItem;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.cockpit.radar.RadarPanel;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.common.Html5NotSupportedException;
import com.btxtech.game.jsre.common.gameengine.services.terrain.AbstractTerrainService;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceImage;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceRect;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImage;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImageBackground;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImagePosition;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainSettings;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainTile;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: May 22, 2009
 * Time: 12:51:09 PM
 */
public class TerrainView implements MouseDownHandler, MouseOutHandler, MouseUpHandler, TerrainListener {
    public static final int AUTO_SCROLL_DETECTION_WIDTH = 40;
    public static final int AUTO_SCROLL_MOVE_DISTANCE = 10;
    private static final TerrainView INSTANCE = new TerrainView();
    private int viewOriginLeft = 0;
    private int viewOriginTop = 0;
    private int viewWidth = 1;
    private int viewHeight = 1;
    private TerrainMouseButtonListener terrainMouseButtonListener;
    private ArrayList<TerrainScrollListener> terrainScrollListeners = new ArrayList<TerrainScrollListener>();
    public Canvas canvas;
    public Context2d context2d;
    private AbsolutePanel parent;
    private TerrainHandler terrainHandler = new TerrainHandler();
    public static boolean uglySuppressRadar = false;
    private Logger log = Logger.getLogger(TerrainView.class.getName());

    /**
     * Singleton
     */
    private TerrainView() {
        canvas = Canvas.createIfSupported();
        if (canvas == null) {
            throw new Html5NotSupportedException("TerrainView: Canvas not supported.");
        }
        context2d = canvas.getContext2d();
        canvas.addMouseOutHandler(this);
    }

    public void setupTerrain(TerrainSettings terrainSettings,
                             Collection<TerrainImagePosition> terrainImagePositions,
                             Collection<SurfaceRect> surfaceRects,
                             Collection<SurfaceImage> surfaceImages,
                             Collection<TerrainImage> terrainImages,
                             TerrainImageBackground terrainImageBackground) {
        if (terrainSettings == null) {
            GwtCommon.sendLogToServer("Invalid terrain settings");
            return;
        }
        terrainHandler.addTerrainListener(this);
        deltaSetupTerrain(terrainSettings, terrainImagePositions, surfaceRects, surfaceImages, terrainImages, terrainImageBackground);
    }

    public void deltaSetupTerrain(TerrainSettings terrainSettings,
                                  Collection<TerrainImagePosition> terrainImagePositions,
                                  Collection<SurfaceRect> surfaceRects,
                                  Collection<SurfaceImage> surfaceImages,
                                  Collection<TerrainImage> terrainImages,
                                  TerrainImageBackground terrainImageBackground) {
        if (terrainSettings == null) {
            GwtCommon.sendLogToServer("Invalid terrain settings for delta");
            return;
        }
        terrainHandler.setupTerrain(terrainSettings, terrainImagePositions, surfaceRects, surfaceImages, terrainImages, terrainImageBackground);
        if (!uglySuppressRadar) {
            RadarPanel.getInstance().onTerrainSettings(terrainSettings);
        }
    }

    public static TerrainView getInstance() {
        return INSTANCE;
    }

    private void drawTerrain() {
        if (terrainHandler.getTerrainSettings() == null) {
            return;
        }
        TerrainTile[][] terrainTiles = terrainHandler.getTerrainTileField();
        if (terrainTiles == null) {
            return;
        }
        final Rectangle tileRect = terrainHandler.convertToTilePositionRoundUp(new Rectangle(viewOriginLeft, viewOriginTop, viewWidth, viewHeight));
        final int scrollXOffset = viewOriginLeft % terrainHandler.getTerrainSettings().getTileWidth();
        final int scrollYOffset = viewOriginTop % terrainHandler.getTerrainSettings().getTileHeight();
        final int tileWidth = terrainHandler.getTerrainSettings().getTileWidth();
        final int tileHeight = terrainHandler.getTerrainSettings().getTileHeight();

        terrainHandler.iteratorOverAllTerrainTiles(tileRect, new AbstractTerrainService.TerrainTileEvaluator() {
            @Override
            public void evaluate(int x, int y, TerrainTile terrainTile) {
                if (terrainTile == null) {
                    return;
                }

                int relativeX = terrainHandler.getAbsolutXForTerrainTile(x - tileRect.getX());
                int imageWidth = tileWidth;
                if (relativeX == 0) {
                    imageWidth = tileWidth - scrollXOffset;
                } else {
                    relativeX -= scrollXOffset;
                }

                int relativeY = terrainHandler.getAbsolutYForTerrainTile(y - tileRect.getY());
                int imageHeight = tileHeight;
                if (relativeY == 0) {
                    imageHeight = tileHeight - scrollYOffset;
                } else {
                    relativeY -= scrollYOffset;
                }

                ImageElement imageElement;
                if (terrainTile.isSurface()) {
                    imageElement = terrainHandler.getSurfaceImageElement(terrainTile.getImageId());
                } else {
                    imageElement = terrainHandler.getTerrainImageElement(terrainTile.getImageId());
                }
                if (imageElement == null || imageElement.getWidth() == 0 || imageElement.getHeight() == 0) {
                    return;
                }

                int sourceXOffset = terrainHandler.getAbsolutXForTerrainTile(terrainTile.getTileXOffset());
                int sourceYOffset = terrainHandler.getAbsolutYForTerrainTile(terrainTile.getTileYOffset());
                if (relativeX == 0) {
                    sourceXOffset += scrollXOffset;
                }
                if (relativeY == 0) {
                    sourceYOffset += scrollYOffset;
                }

                if (terrainTile.isSurface()) {
                    sourceXOffset = sourceXOffset % imageElement.getWidth();
                    sourceYOffset = sourceYOffset % imageElement.getHeight();
                }

                try {
                    context2d.drawImage(imageElement,
                            sourceXOffset, //the start X position in the source image
                            sourceYOffset, //the start Y position in the source image
                            imageWidth, //the width in the source image you want to sample
                            imageHeight, //the height in the source image you want to sample
                            relativeX, //the start X position in the destination image
                            relativeY, //the start Y position in the destination image
                            imageWidth, //the width of drawn image in the destination
                            imageHeight // the height of the drawn image in the destination
                    );
                } catch (Throwable t) {
                    logCanvasError(t, imageElement, sourceXOffset, sourceYOffset, imageWidth, imageHeight, relativeX, relativeY, imageWidth, imageHeight);
                }
            }
        });
    }

    private void logCanvasError(Throwable t, ImageElement imageElement, int srcXStart, int srcYStart, int srcXWidth, int srcYWidth, int posX, int posY, int imageWidth, int imageHeight) {
        StringBuilder builder = new StringBuilder();
        builder.append("TerrainView.drawTerrain() error in canvas drawImage");
        builder.append("\n");

        builder.append("imageElement: ");
        builder.append(imageElement);
        builder.append("\n");

        builder.append("srcXStart: ");
        builder.append(srcXStart);
        builder.append("\n");

        builder.append("srcYStart: ");
        builder.append(srcYStart);
        builder.append("\n");

        builder.append("srcXWidth: ");
        builder.append(srcXWidth);
        builder.append("\n");

        builder.append("srcYWidth: ");
        builder.append(srcYWidth);
        builder.append("\n");

        builder.append("posX: ");
        builder.append(posX);
        builder.append("\n");

        builder.append("posY: ");
        builder.append(posY);
        builder.append("\n");

        builder.append("imageWidth: ");
        builder.append(imageWidth);
        builder.append("\n");

        builder.append("imageHeight: ");
        builder.append(imageHeight);
        builder.append("\n");

        log.log(Level.SEVERE, builder.toString(), t);
    }

    public void moveDelta(int left, int top) {
        if (terrainHandler.getTerrainSettings() == null) {
            return;
        }

        if (viewWidth == 0 && viewHeight == 0) {
            return;
        }

        int orgViewOriginLeft = viewOriginLeft;
        int orgViewOriginTop = viewOriginTop;

        int tmpViewOriginLeft = viewOriginLeft + left;
        int tmpViewOriginTop = viewOriginTop + top;

        if (tmpViewOriginLeft < 0) {
            left = left - tmpViewOriginLeft;
        } else if (tmpViewOriginLeft > terrainHandler.getTerrainSettings().getPlayFieldXSize() - viewWidth - 1) {
            left = left - (tmpViewOriginLeft - (terrainHandler.getTerrainSettings().getPlayFieldXSize() - viewWidth)) - 1;
        }
        if (viewWidth >= terrainHandler.getTerrainSettings().getPlayFieldXSize()) {
            left = 0;
            viewOriginLeft = 0;
        } else {
            viewOriginLeft += left;
        }

        if (tmpViewOriginTop < 0) {
            top = top - tmpViewOriginTop;
        } else if (tmpViewOriginTop > terrainHandler.getTerrainSettings().getPlayFieldYSize() - viewHeight - 1) {
            top = top - (tmpViewOriginTop - (terrainHandler.getTerrainSettings().getPlayFieldYSize() - viewHeight)) - 1;
        }
        if (viewHeight >= terrainHandler.getTerrainSettings().getPlayFieldYSize()) {
            top = 0;
            viewOriginTop = 0;
        } else {
            viewOriginTop += top;
        }

        if (orgViewOriginLeft == viewOriginLeft && orgViewOriginTop == viewOriginTop) {
            // No moveDelta
            return;
        }

        onTerrainChanged();
        fireScrollEvent(left, top);
    }

    public int getViewOriginLeft() {
        return viewOriginLeft;
    }

    public int getViewOriginTop() {
        return viewOriginTop;
    }

    public int getViewWidth() {
        return viewWidth;
    }

    public int getViewHeight() {
        return viewHeight;
    }

    public Rectangle getViewRect() {
        return new Rectangle(viewOriginLeft, viewOriginTop, viewWidth, viewHeight);
    }

    public Index toAbsoluteIndex(Index relative) {
        return relative.add(viewOriginLeft, viewOriginTop);
    }

    public Index toRelativeIndex(Index absolute) {
        return absolute.sub(viewOriginLeft, viewOriginTop);
    }

    public void moveToMiddle(ClientSyncItem clientSyncItem) {
        int left = clientSyncItem.getSyncItem().getSyncItemArea().getPosition().getX() - parent.getOffsetWidth() / 2 - viewOriginLeft;
        int top = clientSyncItem.getSyncItem().getSyncItemArea().getPosition().getY() - parent.getOffsetHeight() / 2 - viewOriginTop;
        moveDelta(left, top);
    }

    public void moveToMiddle(Index startPoint) {
        int left = startPoint.getX() - parent.getOffsetWidth() / 2 - viewOriginLeft;
        int top = startPoint.getY() - parent.getOffsetHeight() / 2 - viewOriginTop;
        moveDelta(left, top);
    }

    public void moveAbsolute(Index topLeftCorner) {
        int left = topLeftCorner.getX() - viewOriginLeft;
        int top = topLeftCorner.getY() - viewOriginTop;
        moveDelta(left, top);
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public void moveToHome() {
        ClientSyncItem scrollTo = null;
        for (ClientSyncItem itemView : ItemContainer.getInstance().getOwnItems()) {
            if (itemView.getSyncBaseItem().isContainedIn()) {
                continue;
            }

            if (itemView.getSyncBaseItem().hasSyncFactory()) {
                scrollTo = itemView;
                break;
            }
            if (itemView.getSyncBaseItem().hasSyncBuilder()) {
                scrollTo = itemView;
                break;
            }
            scrollTo = itemView;
        }
        if (scrollTo != null) {
            moveToMiddle(scrollTo);
        }
    }

    public void addToParent(final AbsolutePanel parent) {
        this.parent = parent;
        parent.add(canvas);
        updateSize();
        Window.addResizeHandler(new ResizeHandler() {
            @Override
            public void onResize(ResizeEvent resizeEvent) {
                updateSize();
                fireScrollEvent(0, 0);
            }
        });
    }

    public void updateSize() {
        viewWidth = parent.getOffsetWidth();
        viewHeight = parent.getOffsetHeight();
        canvas.setCoordinateSpaceWidth(viewWidth);
        canvas.setCoordinateSpaceHeight(viewHeight);
        canvas.setCoordinateSpaceWidth(viewWidth);
        canvas.setCoordinateSpaceHeight(viewHeight);
        onTerrainChanged();
    }

    private void fireScrollEvent(int deltaLeft, int deltaTop) {
        for (TerrainScrollListener terrainScrollListener : terrainScrollListeners) {
            terrainScrollListener.onScroll(viewOriginLeft, viewOriginTop, viewWidth, viewHeight, deltaLeft, deltaTop);
        }
    }

    public void setTerrainMouseButtonListener(TerrainMouseButtonListener terrainMouseButtonListener) {
        this.terrainMouseButtonListener = terrainMouseButtonListener;
    }

    public void addTerrainScrollListener(TerrainScrollListener terrainScrollListener) {
        if (terrainScrollListeners.contains(terrainScrollListener)) {
            return;
        }
        terrainScrollListeners.add(terrainScrollListener);
    }

    public void removeTerrainScrollListener(TerrainScrollListener terrainScrollListener) {
        terrainScrollListeners.remove(terrainScrollListener);
    }

    @Override
    public void onMouseDown(MouseDownEvent mouseDownEvent) {
        int x = mouseDownEvent.getRelativeX(canvas.getElement()) + viewOriginLeft;
        int y = mouseDownEvent.getRelativeY(canvas.getElement()) + viewOriginTop;
        if (terrainMouseButtonListener != null) {
            terrainMouseButtonListener.onMouseDown(x, y, mouseDownEvent);
        }
    }

    @Override
    public void onMouseOut(MouseOutEvent event) {
        // Ignore
    }

    @Override
    public void onMouseUp(MouseUpEvent event) {
        int x = event.getRelativeX(canvas.getElement()) + viewOriginLeft;
        int y = event.getRelativeY(canvas.getElement()) + viewOriginTop;
        if (terrainMouseButtonListener != null) {
            terrainMouseButtonListener.onMouseUp(x, y, event);
        }
    }

    public TerrainHandler getTerrainHandler() {
        return terrainHandler;
    }

    @Override
    public void onTerrainChanged() {
        context2d.clearRect(0, 0, viewWidth, viewHeight);
        // long time = System.currentTimeMillis();
        drawTerrain();
        // log.warning("Draw time: " + (System.currentTimeMillis() - time));
    }
}
