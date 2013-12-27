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

package com.btxtech.game.jsre.client.cockpit.radar;

import com.btxtech.game.jsre.client.ClientExceptionHandler;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.Html5NotSupportedException;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainSettings;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainUtil;
import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;

import java.util.ArrayList;
import java.util.List;

/**
 * User: beat
 * Date: 22.12.2009
 * Time: 21:50:26
 */
public abstract class MiniMap implements MouseMoveHandler, MouseDownHandler, MouseUpHandler {
    private int height;
    private int width;
    private ScaleStep scaleStep = ScaleStep.WHOLE_MAP;
    private TerrainSettings terrainSettings;
    private List<MiniMapMouseMoveListener> miniMapMouseMoveListeners = new ArrayList<MiniMapMouseMoveListener>();
    private List<MiniMapMouseDownListener> miniMapMouseDownListeners = new ArrayList<MiniMapMouseDownListener>();
    private List<MiniMapMouseUpListener> miniMapMouseUpListeners = new ArrayList<MiniMapMouseUpListener>();
    private HandlerRegistration moveRegistration;
    private HandlerRegistration downRegistration;
    private HandlerRegistration upRegistration;
    private Canvas canvas;
    private Context2d context2d;
    private int xShiftRadarPixel = 0;
    private int yShiftRadarPixel = 0;
    private Index viewOrigin = new Index(0, 0);
    private double scaleForFullTerrain;
    private double scale = 1.0;

    public MiniMap(int width, int height) {
        canvas = Canvas.createIfSupported();
        if (canvas == null) {
            throw new Html5NotSupportedException("MiniMap: Canvas not supported.");
        }
        canvas.setCoordinateSpaceWidth(width);
        canvas.setCoordinateSpaceHeight(height);
        context2d = canvas.getContext2d();
        this.width = width;
        this.height = height;
    }

    public ScaleStep getScale() {
        return scaleStep;
    }

    public void setScale(ScaleStep scaleStep) {
        this.scaleStep = scaleStep;
        Index absoluteMiddle = getAbsoluteMiddle();
        scale = scaleForFullTerrain * Math.sqrt(scaleStep.getZoom());

        int terrainDisplayWidth = (int) (terrainSettings.getPlayFieldXSize() * scale);
        int terrainDisplayHeight = (int) (terrainSettings.getPlayFieldYSize() * scale);
        if (terrainDisplayWidth < width) {
            xShiftRadarPixel = (int) ((width - terrainDisplayWidth) / 2.0);
        } else {
            xShiftRadarPixel = 0;
        }
        if (terrainDisplayHeight < height) {
            yShiftRadarPixel = (int) ((height - terrainDisplayHeight) / 2.0);
        } else {
            yShiftRadarPixel = 0;
        }

        setAbsoluteViewRectMiddle(absoluteMiddle);
        draw();
    }

    protected Rectangle getAbsoluteViewRectangle() {
        int width = getAbsoluteVisibleWidth();
        int height = getAbsoluteVisibleHeight();
        if (width + viewOrigin.getX() >= getTerrainSettings().getPlayFieldXSize()) {
            width = getTerrainSettings().getPlayFieldXSize() - viewOrigin.getX();
        }
        if (width < 0) {
            width = 0;
        }
        if (height + viewOrigin.getY() >= getTerrainSettings().getPlayFieldYSize()) {
            height = getTerrainSettings().getPlayFieldYSize() - viewOrigin.getY();
        }
        if (height < 0) {
            height = 0;
        }
        return new Rectangle(viewOrigin, viewOrigin.add(new Index(width, height)));
    }

    public int getAbsoluteVisibleWidth() {
        return (int) ((double) getWidth() / scale);
    }

    public int getAbsoluteVisibleHeight() {
        return (int) ((double) getHeight() / scale);
    }

    public Index getAbsoluteMiddle() {
        return new Index(viewOrigin.getX() + getAbsoluteVisibleWidth() / 2, viewOrigin.getY() + getAbsoluteVisibleHeight() / 2);
    }

    protected Rectangle getTileViewRectangle() {
        return TerrainUtil.convertToTilePositionRoundUp(getAbsoluteViewRectangle());
    }

    protected int scaleAbsoluteRadarPosition(int absolute) {
        return (int) ((double) absolute * scale);
    }

    protected int absolute2RadarPositionX(int absolute) {
        return scaleAbsoluteRadarPosition(absolute - viewOrigin.getX()) + xShiftRadarPixel;
    }

    protected int absolute2RadarPositionY(int absolute) {
        return scaleAbsoluteRadarPosition(absolute - viewOrigin.getY()) + yShiftRadarPixel;
    }

    protected int absolute2RadarPositionX(Index absolute) {
        return absolute2RadarPositionX(absolute.getX());
    }

    protected int absolute2RadarPositionY(Index absolute) {
        return absolute2RadarPositionY(absolute.getY());
    }

    public void setAbsoluteViewRectMiddle(Index middle) {
        setAbsoluteViewRect(middle.sub(getAbsoluteVisibleWidth() / 2, getAbsoluteVisibleHeight() / 2));
    }

    public void setAbsoluteViewRect(Index position) {
        int width = getAbsoluteVisibleWidth();
        int height = getAbsoluteVisibleHeight();

        int viewRectX = position.getX();
        if (position.getX() + width >= getTerrainSettings().getPlayFieldXSize()) {
            viewRectX = getTerrainSettings().getPlayFieldXSize() - width;
        }
        if (viewRectX < 0) {
            viewRectX = 0;
        }
        int viewRectY = position.getY();
        if (position.getY() + height >= getTerrainSettings().getPlayFieldYSize()) {
            viewRectY = getTerrainSettings().getPlayFieldYSize() - height;
        }
        if (viewRectY < 0) {
            viewRectY = 0;
        }
        viewOrigin = new Index(viewRectX, viewRectY);
        draw();
    }

    public void onTerrainSettings(TerrainSettings terrainSettings) {
        this.terrainSettings = terrainSettings;
        scaleForFullTerrain = Math.min((double) width / (double) terrainSettings.getPlayFieldXSize(), (double) height / (double) terrainSettings.getPlayFieldYSize());
        setScale(scaleStep);
    }

    protected void clear() {
        context2d.clearRect(0, 0, getWidth(), getHeight());
    }

    public TerrainSettings getTerrainSettings() {
        return terrainSettings;
    }

    public void addMouseMoveListener(MiniMapMouseMoveListener miniMapMouseMoveListener) {
        miniMapMouseMoveListeners.add(miniMapMouseMoveListener);
        if (moveRegistration == null) {
            moveRegistration = canvas.addMouseMoveHandler(this);
        }
    }

    public void removeMouseMoveListener(MiniMapMouseMoveListener miniMapMouseMoveListener) {
        miniMapMouseMoveListeners.remove(miniMapMouseMoveListener);
        if (moveRegistration != null && miniMapMouseMoveListeners.isEmpty()) {
            moveRegistration.removeHandler();
            moveRegistration = null;
        }
    }

    public void addMouseDownListener(MiniMapMouseDownListener miniMapMouseDownListener) {
        miniMapMouseDownListeners.add(miniMapMouseDownListener);
        if (downRegistration == null) {
            downRegistration = canvas.addMouseDownHandler(this);
        }
    }

    public void removeMouseDownListener(MiniMapMouseDownListener miniMapMouseDownListener) {
        miniMapMouseDownListeners.remove(miniMapMouseDownListener);
        if (downRegistration != null && miniMapMouseDownListeners.isEmpty()) {
            downRegistration.removeHandler();
            downRegistration = null;
        }
    }

    public void addMouseUpListener(MiniMapMouseUpListener miniMapMouseUpListener) {
        miniMapMouseUpListeners.add(miniMapMouseUpListener);
        if (upRegistration == null) {
            upRegistration = canvas.addMouseUpHandler(this);
        }
    }

    public void removeMouseUpListener(MiniMapMouseUpListener miniMapMouseUpListener) {
        miniMapMouseUpListeners.remove(miniMapMouseUpListener);
        if (upRegistration != null && miniMapMouseUpListeners.isEmpty()) {
            upRegistration.removeHandler();
            upRegistration = null;
        }
    }

    @Override
    public void onMouseMove(MouseMoveEvent event) {
        Index position = Index.createSaveIndex(GwtCommon.createSaveIndexRelative(event, canvas.getElement()));
        position = position.sub(xShiftRadarPixel, yShiftRadarPixel);
        position = position.scaleInverse(scale);
        position = position.add(viewOrigin);
        for (MiniMapMouseMoveListener miniMapMouseMoveListener : miniMapMouseMoveListeners) {
            miniMapMouseMoveListener.onMouseMove(position.getX(), position.getY());
        }
    }

    @Override
    public void onMouseDown(MouseDownEvent mouseDownEvent) {
        Index position = Index.createSaveIndex(GwtCommon.createSaveIndexRelative(mouseDownEvent, canvas.getElement()));
        position = position.sub(xShiftRadarPixel, yShiftRadarPixel);
        position = position.scaleInverse(scale);
        position = position.add(viewOrigin);
        for (MiniMapMouseDownListener miniMapMouseDownListener : miniMapMouseDownListeners) {
            miniMapMouseDownListener.onMouseDown(position.getX(), position.getY(), mouseDownEvent);
        }
    }

    @Override
    public void onMouseUp(MouseUpEvent event) {
        Index position = Index.createSaveIndex(GwtCommon.createSaveIndexRelative(event, canvas.getElement()));
        position = position.sub(xShiftRadarPixel, yShiftRadarPixel);
        position = position.scaleInverse(scale);
        position = position.add(viewOrigin);
        for (MiniMapMouseUpListener miniMapMouseUpListener : miniMapMouseUpListeners) {
            miniMapMouseUpListener.onMouseUp(position.getX(), position.getY(), event);
        }
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    protected Context2d getContext2d() {
        return context2d;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public void cleanup() {
    }

    public Index getViewOrigin() {
        return viewOrigin;
    }

    public void draw() {
        if (canvas != null && terrainSettings != null) {
            AnimationScheduler.get().requestAnimationFrame(new AnimationScheduler.AnimationCallback() {
                @Override
                public void execute(double timestamp) {
                    try {
                        clear();
                        render();
                    } catch (Exception e) {
                        ClientExceptionHandler.handleExceptionOnlyOnce("MiniMap.draw() failed", e);
                    }
                }
            }, canvas.getElement());
        }
    }

    protected double getScaleValue() {
        return scale;
    }

    protected abstract void render();

    public int getXShiftRadarPixel() {
        return xShiftRadarPixel;
    }

    public int getYShiftRadarPixel() {
        return yShiftRadarPixel;
    }
}
