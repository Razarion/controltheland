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

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.cockpit.radar.RadarPanel;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.common.Html5NotSupportedException;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceImage;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceRect;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImage;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImageBackground;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImagePosition;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainSettings;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: May 22, 2009
 * Time: 12:51:09 PM
 */
public class TerrainView {
    public static boolean uglySuppressRadar = false;
    private static final TerrainView INSTANCE = new TerrainView();
    private int viewOriginLeft = 0;
    private int viewOriginTop = 0;
    private int viewWidth = 1;
    private int viewHeight = 1;
    private ArrayList<TerrainScrollListener> terrainScrollListeners = new ArrayList<TerrainScrollListener>();
    private TerrainScrollHandler terrainScrollHandler;
    private TerrainMouseHandler terrainMouseHandler;
    private TerrainKeyHandler terrainKeyHandler;
    private Canvas canvas;
    private Context2d context2d;
    private AbsolutePanel parent;
    private TerrainHandler terrainHandler = new TerrainHandler();
    private static Logger log = Logger.getLogger(TerrainView.class.getName());

    /**
     * Singleton
     */
    private TerrainView() {
        canvas = Canvas.createIfSupported();
        if (canvas == null) {
            throw new Html5NotSupportedException("TerrainView: Canvas not supported.");
        }
        canvas.setTabIndex(1); // IE9 need this to receive the focus
        context2d = canvas.getContext2d();

        terrainScrollHandler = new TerrainScrollHandler();
        terrainScrollHandler.setScrollExecutor(new TerrainScrollHandler.ScrollExecutor() {
            @Override
            public void moveDelta(int scrollX, int scrollY) {
                scrollDeltaSafe(scrollX, scrollY);
            }
        });
        terrainMouseHandler = new TerrainMouseHandler(canvas, this, terrainScrollHandler);
        terrainKeyHandler = new TerrainKeyHandler(canvas, terrainScrollHandler);
    }

    private void scrollDeltaSafe(int scrollX, int scrollY) {
        Index safeDelta = TerrainScrollHandler.calculateSafeDelta(scrollX, scrollY, terrainHandler.getTerrainSettings(), getViewRect());
        if (!safeDelta.isNull()) {
            viewOriginLeft = viewOriginLeft + safeDelta.getX();
            viewOriginTop = viewOriginTop + safeDelta.getY();
            fireScrollEvent(safeDelta.getX(), safeDelta.getY());
        }
    }

    public void setupTerrain(TerrainSettings terrainSettings,
                             Collection<TerrainImagePosition> terrainImagePositions,
                             Collection<SurfaceRect> surfaceRects,
                             Collection<SurfaceImage> surfaceImages,
                             Collection<TerrainImage> terrainImages,
                             TerrainImageBackground terrainImageBackground) {
        if (terrainSettings == null) {
            log.severe("TerrainView.setupTerrain() terrainSettings == null");
            return;
        }
        deltaSetupTerrain(terrainSettings, terrainImagePositions, surfaceRects, surfaceImages, terrainImages, terrainImageBackground);
    }

    public void deltaSetupTerrain(TerrainSettings terrainSettings,
                                  Collection<TerrainImagePosition> terrainImagePositions,
                                  Collection<SurfaceRect> surfaceRects,
                                  Collection<SurfaceImage> surfaceImages,
                                  Collection<TerrainImage> terrainImages,
                                  TerrainImageBackground terrainImageBackground) {
        if (terrainSettings == null) {
            log.severe("TerrainView.deltaSetupTerrain() terrainSettings == null");
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

    public void moveToMiddle(SyncItem syncItem) {
        int left = syncItem.getSyncItemArea().getPosition().getX() - parent.getOffsetWidth() / 2 - viewOriginLeft;
        int top = syncItem.getSyncItemArea().getPosition().getY() - parent.getOffsetHeight() / 2 - viewOriginTop;
        scrollDeltaSafe(left, top);
    }

    public void moveToMiddle(Index startPoint) {
        if (parent == null) {
            return;
        }
        int left = startPoint.getX() - parent.getOffsetWidth() / 2 - viewOriginLeft;
        int top = startPoint.getY() - parent.getOffsetHeight() / 2 - viewOriginTop;
        scrollDeltaSafe(left, top);
    }

    public void moveAbsolute(Index topLeftCorner) {
        int left = topLeftCorner.getX() - viewOriginLeft;
        int top = topLeftCorner.getY() - viewOriginTop;
        scrollDeltaSafe(left, top);
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public void moveToHome() {
        SyncBaseItem scrollTo = null;
        for (SyncBaseItem syncBaseItem : ItemContainer.getInstance().getItems4Base(ClientBase.getInstance().getSimpleBase())) {
            if (syncBaseItem.isContainedIn()) {
                continue;
            }

            if (syncBaseItem.hasSyncFactory()) {
                scrollTo = syncBaseItem;
                break;
            }
            if (syncBaseItem.hasSyncBuilder()) {
                scrollTo = syncBaseItem;
                break;
            }
            scrollTo = syncBaseItem;
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
        viewWidth = Window.getClientWidth();
        viewHeight = Window.getClientHeight();
    }

    private void fireScrollEvent(int deltaLeft, int deltaTop) {
        for (TerrainScrollListener terrainScrollListener : terrainScrollListeners) {
            terrainScrollListener.onScroll(viewOriginLeft, viewOriginTop, viewWidth, viewHeight, deltaLeft, deltaTop);
        }
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

    public TerrainHandler getTerrainHandler() {
        return terrainHandler;
    }

    public Context2d getContext2d() {
        return context2d;
    }

    public TerrainMouseHandler getTerrainMouseHandler() {
        return terrainMouseHandler;
    }

    public TerrainKeyHandler getTerrainKeyHandler() {
        return terrainKeyHandler;
    }

    public void setFocus() {
        canvas.setFocus(true);
    }

    public TerrainScrollHandler getTerrainScrollHandler() {
        return terrainScrollHandler;
    }

    public void cleanup() {
        terrainScrollHandler.cleanup();
    }
}
