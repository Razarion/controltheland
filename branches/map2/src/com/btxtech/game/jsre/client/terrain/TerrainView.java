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

import com.btxtech.game.jsre.client.ExtendedCanvas;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainSettings;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImagePosition;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.widgetideas.graphics.client.GWTCanvas;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
    private ExtendedCanvas canvas = new ExtendedCanvas();
    private AbsolutePanel parent;
    private TerrainHandler terrainHandler = new TerrainHandler();

    /**
     * Singleton
     */
    private TerrainView() {
        canvas.addMouseDownHandler(this);
        canvas.addMouseOutHandler(this);
        canvas.addMouseUpHandler(this);
        canvas.sinkEvents(Event.ONMOUSEMOVE);
    }

    public void setupTerrain(TerrainSettings terrainSettings) {
        if (terrainSettings == null) {
            GwtCommon.sendLogToServer("Invalid terrain settings");
            return;
        }
        terrainHandler.addTerrainListener(this);
        terrainHandler.setupTerrain(terrainSettings);
    }

    public void setupTerrainImages(List<TerrainImagePosition> terrainImagePositions) {
        terrainHandler.setupTerrainImages(terrainImagePositions);
    }

    @Deprecated
    public void setupTerrain(int[][] terrainField, Collection<Integer> passableTerrainTileIds) {
    }

    public static TerrainView getInstance() {
        return INSTANCE;
    }

    private void drawBackground() {
        ImageElement imageElement = terrainHandler.getBackgroundImage();
        if (imageElement == null) {
            return;
        }
        int bgTileXStart = viewOriginLeft / imageElement.getWidth();
        int bgTileXEnd = (viewOriginLeft + viewWidth) / imageElement.getWidth();
        int bgTileYStart = viewOriginTop / imageElement.getHeight();
        int bgTileYEnd = (viewOriginTop + viewHeight) / imageElement.getHeight();

        int bgTileLeftOffset = viewOriginLeft % imageElement.getWidth();
        int bgTileRightOffset = (viewOriginLeft + viewWidth) % imageElement.getWidth();
        int bgTileTopOffset = viewOriginTop % imageElement.getHeight();
        int bgTileBottomOffset = (viewOriginTop + viewHeight) % imageElement.getHeight();

        int posX = 0;
        int posY;
        for (int x = bgTileXStart; x <= bgTileXEnd; x++) {
            posY = 0;
            int srcXStart;
            int srcXWidth;
            if (x == bgTileXStart) {
                // first column
                srcXStart = bgTileLeftOffset;
                srcXWidth = imageElement.getWidth() - bgTileLeftOffset;
            } else if (x == bgTileXEnd) {
                // last column
                srcXStart = 0;
                srcXWidth = bgTileRightOffset;
            } else {
                // middle
                srcXStart = 0;
                srcXWidth = imageElement.getWidth();
            }
            if (srcXWidth == 0) {
                // Sould never happen but happens in opera
                continue;
            }
            for (int y = bgTileYStart; y <= bgTileYEnd; y++) {
                int srcYStart;
                int srcYWidth;
                if (y == bgTileYStart) {
                    // first row
                    srcYStart = bgTileTopOffset;
                    srcYWidth = imageElement.getHeight() - bgTileTopOffset;
                } else if (y == bgTileYEnd) {
                    // last row
                    srcYStart = 0;
                    srcYWidth = bgTileBottomOffset;
                } else {
                    // middle
                    srcYStart = 0;
                    srcYWidth = imageElement.getHeight();
                }
                if (srcYWidth == 0) {
                    // Sould never happen but happens in opera
                    continue;
                }

                try {
                    canvas.drawImage(imageElement, srcXStart, srcYStart, srcXWidth, srcYWidth, posX, posY, srcXWidth, srcYWidth);
                } catch (Throwable t) {
                    GwtCommon.handleException(t);
                    sendErrorInfoToServer(imageElement, posX, posY, srcXStart, srcXWidth, srcYStart, srcYWidth);
                }
                posY += srcYWidth;
            }
            posX += srcXWidth;
        }

    }

    private void drawImages() {
        List<TerrainImagePosition> terrainImagePositions = terrainHandler.getTerrainImagesInRegion(new Rectangle(viewOriginLeft, viewOriginTop, viewWidth, viewHeight));
        if (terrainImagePositions.isEmpty()) {
            return;
        }

        for (TerrainImagePosition terrainImagePosition : terrainImagePositions) {
            Index absolutePos = terrainHandler.getAbsolutIndexForTerrainTileIndex(terrainImagePosition.getTileIndex());
            int relXStart = absolutePos.getX() - viewOriginLeft;
            int relYStart = absolutePos.getY() - viewOriginTop;
            ImageElement imageElement = terrainHandler.getTileImageElement(terrainImagePosition.getImageId());
            try {
                canvas.drawImage(imageElement, relXStart, relYStart);
            } catch (Throwable t) {
                GwtCommon.handleException(t);
                sendErrorInfoToServer(imageElement, relXStart, relYStart, 0, 0, 0, 0);
            }
        }
    }


    public void move(int left, int top) {
        if(terrainHandler.getTerrainSettings() == null) {
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
        if (viewWidth > terrainHandler.getTerrainSettings().getPlayFieldXSize()) {
            left = -viewOriginLeft;
            viewOriginLeft = 0;
        } else {
            viewOriginLeft += left;
        }

        if (tmpViewOriginTop < 0) {
            top = top - tmpViewOriginTop;
        } else if (tmpViewOriginTop > terrainHandler.getTerrainSettings().getPlayFieldYSize() - viewHeight - 1) {
            top = top - (tmpViewOriginTop - (terrainHandler.getTerrainSettings().getPlayFieldYSize() - viewHeight)) - 1;
        }
        if (viewHeight > terrainHandler.getTerrainSettings().getPlayFieldYSize()) {
            top = 0;
            viewOriginTop = 0;
        } else {
            viewOriginTop += top;
        }

        if (orgViewOriginLeft == viewOriginLeft && orgViewOriginTop == viewOriginTop) {
            // No move
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

    public void moveToMiddle(Index startPoint) {
        int left = startPoint.getX() - parent.getOffsetWidth() / 2 - viewOriginLeft;
        int top = startPoint.getY() - parent.getOffsetHeight() / 2 - viewOriginTop;
        move(left, top);
    }

    public GWTCanvas getCanvas() {
        return canvas;
    }

    public void addToParent(final AbsolutePanel parent) {
        this.parent = parent;
        parent.add(canvas);
        viewWidth = parent.getOffsetWidth();
        viewHeight = parent.getOffsetHeight();
        canvas.resize(viewWidth, viewHeight);
        onTerrainChanged();
        Window.addResizeHandler(new ResizeHandler() {
            @Override
            public void onResize(ResizeEvent resizeEvent) {
                viewWidth = parent.getOffsetWidth();
                viewHeight = parent.getOffsetHeight();
                canvas.resize(viewWidth, viewHeight);
                onTerrainChanged();
                fireScrollEvent(0, 0);
            }
        });
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
        terrainScrollListeners.add(terrainScrollListener);
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
        // Ignore
    }

    public TerrainHandler getTerrainHandler() {
        return terrainHandler;
    }

    @Override
    public void onTerrainChanged() {
        canvas.clear();
        drawBackground();
        drawImages();
    }


    private void sendErrorInfoToServer(ImageElement imageElement, int posX, int posY, int srcXStart, int srcXWidth, int srcYStart, int srcYWidth) {
        StringBuilder builder = new StringBuilder();
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

        builder.append("srcXWidth: ");
        builder.append(srcXWidth);
        builder.append("\n");

        builder.append("srcYWidth: ");
        builder.append(srcYWidth);
        builder.append("\n");

        GwtCommon.sendLogToServer(builder.toString());
    }
}
