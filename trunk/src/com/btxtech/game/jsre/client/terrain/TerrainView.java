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

import com.btxtech.game.jsre.client.ClientSyncBaseItemView;
import com.btxtech.game.jsre.client.ClientSyncItemView;
import com.btxtech.game.jsre.client.ExtendedCanvas;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.cockpit.radar.RadarPanel;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceImage;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceRect;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImage;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImagePosition;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainSettings;
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

    public void setupTerrain(TerrainSettings terrainSettings,
                             Collection<TerrainImagePosition> terrainImagePositions,
                             Collection<SurfaceRect> surfaceRects,
                             Collection<SurfaceImage> surfaceImages,
                             Collection<TerrainImage> terrainImages) {
        if (terrainSettings == null) {
            GwtCommon.sendLogToServer("Invalid terrain settings");
            return;
        }
        terrainHandler.addTerrainListener(this);
        terrainHandler.setupTerrain(terrainSettings, terrainImagePositions, surfaceRects, surfaceImages, terrainImages);
        RadarPanel.getInstance().onTerrainSettings(terrainSettings);
    }

    public static TerrainView getInstance() {
        return INSTANCE;
    }

    private void drawSurface() {
        List<SurfaceRect> surfaceRects = terrainHandler.getSurfaceRectsInRegion(new Rectangle(viewOriginLeft, viewOriginTop, viewWidth, viewHeight));
        if (surfaceRects.isEmpty() || terrainHandler.getSurfaceImageElements().isEmpty()) {
            return;
        }

        for (SurfaceRect surfaceRect : surfaceRects) {
            Rectangle absolutePos = terrainHandler.convertToAbsolutePosition(surfaceRect.getTileRectangle());
            ImageElement imageElement = terrainHandler.getSurfaceImageElement(surfaceRect.getSurfaceImageId());
            if (imageElement == null) {
                terrainHandler.loadImagesAndDrawMap();
                continue;
            }
            tilingSurface(imageElement, absolutePos);
        }
    }

    private void tilingSurface(ImageElement imageElement, Rectangle absolutePos) {
        int endRectX = absolutePos.getEndX() - viewOriginLeft;
        int endRectY = absolutePos.getEndY() - viewOriginTop;
        for (int x = absolutePos.getX() - viewOriginLeft; x < endRectX && x < viewWidth; x += imageElement.getWidth()) {
            int width;
            if (x + imageElement.getWidth() > endRectX) {
                width = endRectX - x;
            } else {
                width = imageElement.getWidth();
            }
            for (int y = absolutePos.getY() - viewOriginTop; y < endRectY && y < viewHeight; y += imageElement.getHeight()) {
                int height;
                if (y + imageElement.getWidth() > endRectY) {
                    height = endRectY - y;
                } else {
                    height = imageElement.getWidth();
                }
                try {
                    canvas.drawImage(imageElement,
                            0, //the start X position in the source image
                            0, //the start Y position in the source image
                            width, //the width in the source image you want to sample
                            height, //the height in the source image you want to sample
                            x, //the start X position in the destination image
                            y, //the start Y position in the destination image
                            width, //the width of drawn image in the destination
                            height // the height of the drawn image in the destination
                    );
                } catch (Throwable t) {
                    GwtCommon.handleException(t);
                    sendErrorInfoToServer("drawSurface", imageElement, x, y, 0, width, 0, height);
                }

            }
        }
    }

    private void drawImages() {
        List<TerrainImagePosition> terrainImagePositions = terrainHandler.getTerrainImagesInRegion(new Rectangle(viewOriginLeft, viewOriginTop, viewWidth, viewHeight));
        if (terrainImagePositions.isEmpty() || terrainHandler.getTerrainImageElements().isEmpty()) {
            return;
        }

        for (TerrainImagePosition terrainImagePosition : terrainImagePositions) {
            Index absolutePos = terrainHandler.getAbsolutIndexForTerrainTileIndex(terrainImagePosition.getTileIndex());
            int relXStart = absolutePos.getX() - viewOriginLeft;
            int relYStart = absolutePos.getY() - viewOriginTop;
            ImageElement imageElement = terrainHandler.getTerrainImageElement(terrainImagePosition.getImageId());
            if (imageElement == null) {
                terrainHandler.loadImagesAndDrawMap();
                continue;
            }
            try {
                canvas.drawImage(imageElement, relXStart, relYStart);
            } catch (Throwable t) {
                GwtCommon.handleException(t);
                sendErrorInfoToServer("drawImages", imageElement, relXStart, relYStart, 0, 0, 0, 0);
            }
        }
    }


    public void move(int left, int top) {
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

    public Index toAbsoluteIndex(Index relative) {
        return relative.add(viewOriginLeft, viewOriginTop);
    }

    public Index toRelativeIndex(Index absolute) {
        return absolute.sub(viewOriginLeft, viewOriginTop);
    }

    public void moveToMiddle(ClientSyncItemView clientSyncItemView) {
        int left = clientSyncItemView.getSyncItem().getPosition().getX() - parent.getOffsetWidth() / 2 - viewOriginLeft;
        int top = clientSyncItemView.getSyncItem().getPosition().getY() - parent.getOffsetHeight() / 2 - viewOriginTop;
        move(left, top);
    }

    public void moveToMiddle(Index startPoint) {
        int left = startPoint.getX() - parent.getOffsetWidth() / 2 - viewOriginLeft;
        int top = startPoint.getY() - parent.getOffsetHeight() / 2 - viewOriginTop;
        move(left, top);
    }

    public GWTCanvas getCanvas() {
        return canvas;
    }

    public void moveToHome() {
        ClientSyncBaseItemView scrollTo = null;
        for (ClientSyncBaseItemView itemView : ItemContainer.getInstance().getOwnItems()) {
            if(itemView.getSyncBaseItem().isContainedIn()) {
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
        canvas.clear();
        drawSurface();
        drawImages();
    }

    public void addNewTerrainImagePosition(int relX, int relY, TerrainImage terrainImage) {
        int absX = relX + viewOriginLeft;
        int absY = relY + viewOriginTop;

        terrainHandler.addNewTerrainImage(absX, absY, terrainImage);
    }

    public void addNewSurfaceRect(int relX, int relY, int width, int height, SurfaceImage surfaceImage) {
        int absX = relX + viewOriginLeft;
        int absY = relY + viewOriginTop;

        terrainHandler.addNewSurfaceRect(absX, absY, width, height, surfaceImage);
    }

    public void moveTerrainImagePosition(int relX, int relY, TerrainImagePosition terrainImagePosition) {
        int absX = relX + viewOriginLeft;
        int absY = relY + viewOriginTop;

        terrainHandler.moveTerrainImagePosition(absX, absY, terrainImagePosition);
    }


    public void moveSurfaceRect(int relX, int relY, SurfaceRect surfaceRect) {
        int absX = relX + viewOriginLeft;
        int absY = relY + viewOriginTop;

        terrainHandler.moveSurfaceRect(absX, absY, surfaceRect);
    }

    private void sendErrorInfoToServer(String method, ImageElement imageElement, int posX, int posY, int srcXStart, int srcXWidth, int srcYStart, int srcYWidth) {
        StringBuilder builder = new StringBuilder();
        builder.append("Method: ");
        builder.append(method);
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

        builder.append("srcXWidth: ");
        builder.append(srcXWidth);
        builder.append("\n");

        builder.append("srcYWidth: ");
        builder.append(srcYWidth);
        builder.append("\n");

        GwtCommon.sendLogToServer(builder.toString());
    }
}
