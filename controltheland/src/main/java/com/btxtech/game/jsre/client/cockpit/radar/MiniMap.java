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

import com.btxtech.game.jsre.common.Html5NotSupportedException;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainSettings;
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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 22.12.2009
 * Time: 21:50:26
 */
public class MiniMap implements MouseMoveHandler, MouseDownHandler, MouseUpHandler {
    private int height;
    private int width;
    private double scale = 1.0;
    private TerrainSettings terrainSettings;
    private List<MiniMapMouseMoveListener> miniMapMouseMoveListeners = new ArrayList<MiniMapMouseMoveListener>();
    private List<MiniMapMouseDownListener> miniMapMouseDownListeners = new ArrayList<MiniMapMouseDownListener>();
    private List<MiniMapMouseUpListener> miniMapMouseUpListeners = new ArrayList<MiniMapMouseUpListener>();
    private HandlerRegistration moveRegistration;
    private HandlerRegistration downRegistration;
    private HandlerRegistration upRegistration;
    private Canvas canvas;
    private Context2d context2d;
    private Logger log = Logger.getLogger(MiniMap.class.getName());

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

    public double getScale() {
        return scale;
    }

    protected void setScale(double scale) {
        this.scale = scale;
    }

    public void onTerrainSettings(TerrainSettings terrainSettings) {
        this.terrainSettings = terrainSettings;
        scale = Math.min((double) width / (double) terrainSettings.getTileXCount(),
                (double) height / (double) terrainSettings.getTileYCount());
        // Restore & save to get old state before scale
        context2d.restore();
        context2d.save();

        try {
            context2d.scale(scale, scale);
            if (getTerrainSettings().getTileXCount() > getTerrainSettings().getTileYCount()) {
                int yShift = (getTerrainSettings().getTileXCount() - getTerrainSettings().getTileYCount()) / 2;
                getContext2d().translate(0, yShift);
            } else if (getTerrainSettings().getTileYCount() > getTerrainSettings().getTileXCount()) {
                int xShift = (getTerrainSettings().getTileYCount() - getTerrainSettings().getTileXCount()) / 2;
                getContext2d().translate(xShift, 0);
            }
        } catch (Exception e) {
            // Fails during tests
            log.log(Level.SEVERE, "", e);
        }
    }

    protected void clear() {
        context2d.save();
        context2d.clearRect(0, 0, getWidth(), getHeight());
        context2d.restore();
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
        int x = event.getRelativeX(canvas.getElement());
        if (x < 0) {
            x = 0;
        }
        int y = event.getRelativeY(canvas.getElement());
        if (y < 0) {
            y = 0;
        }
        x = (int) (x / scale);
        y = (int) (y / scale);

        for (MiniMapMouseMoveListener miniMapMouseMoveListener : miniMapMouseMoveListeners) {
            miniMapMouseMoveListener.onMouseMove(x, y);
        }
    }

    @Override
    public void onMouseDown(MouseDownEvent mouseDownEvent) {
        int x = mouseDownEvent.getRelativeX(canvas.getElement());
        if (x < 0) {
            x = 0;
        }
        int y = mouseDownEvent.getRelativeY(canvas.getElement());
        if (y < 0) {
            y = 0;
        }
        x = (int) (x / scale);
        y = (int) (y / scale);

        for (MiniMapMouseDownListener miniMapMouseDownListener : miniMapMouseDownListeners) {
            miniMapMouseDownListener.onMouseDown(x, y, mouseDownEvent);
        }
    }

    @Override
    public void onMouseUp(MouseUpEvent event) {
        int x = event.getRelativeX(canvas.getElement());
        if (x < 0) {
            x = 0;
        }
        int y = event.getRelativeY(canvas.getElement());
        if (y < 0) {
            y = 0;
        }
        x = (int) (x / scale);
        y = (int) (y / scale);

        for (MiniMapMouseUpListener miniMapMouseUpListener : miniMapMouseUpListeners) {
            miniMapMouseUpListener.onMouseUp(x, y, event);
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
}
