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

import com.btxtech.game.jsre.client.ColorConstants;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.terrain.TerrainScrollListener;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainSettings;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.MouseDownEvent;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 22.12.2009
 * Time: 21:52:27
 */
public class RadarFrameView extends MiniMap implements TerrainScrollListener, MiniMapMouseDownListener {
    private Logger log = Logger.getLogger(RadarFrameView.class.getName());
    private int left;
    private int top;
    private int width;
    private int height;
    private boolean colorToggle;

    public RadarFrameView(int width, int height) {
        super(width, height);
        TerrainView.getInstance().addTerrainScrollListener(this);
        addMouseDownListener(this);
        Scheduler.get().scheduleFixedPeriod(new Scheduler.RepeatingCommand() {
            @Override
            public boolean execute() {
                drawFrame();
                return true;
            }
        }, 1000);
    }

    @Override
    public void onScroll(int left, int top, int width, int height, int deltaLeft, int deltaTop) {
        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;
        drawFrame();
    }

    private void drawFrame() {
        if (getTerrainSettings() == null) {
            return;
        }

        clear();
        if (colorToggle) {
            getContext2d().setStrokeStyle(ColorConstants.LIGHTGREY);
        } else {
            getContext2d().setStrokeStyle(ColorConstants.RED);
        }
        colorToggle = !colorToggle;
        getContext2d().beginPath();
        double leftDouble = (double) left / (double) getTerrainSettings().getTileWidth();
        double topDouble = (double) top / (double) getTerrainSettings().getTileHeight();
        double widthDouble = (double) width / (double) getTerrainSettings().getTileWidth();
        double heightDouble = (double) height / (double) getTerrainSettings().getTileHeight();
        try {
            getContext2d().rect(leftDouble, topDouble, widthDouble, heightDouble);
        } catch (Exception e) {
            // Fails during tests
            log.log(Level.SEVERE, "", e);
        }

        getContext2d().stroke();

    }

    @Override
    public void onTerrainSettings(TerrainSettings terrainSettings) {
        super.onTerrainSettings(terrainSettings);
        getContext2d().setLineWidth(1.0 / getScale());
        onScroll(TerrainView.getInstance().getViewOriginLeft(),
                TerrainView.getInstance().getViewOriginTop(),
                TerrainView.getInstance().getViewWidth(),
                TerrainView.getInstance().getViewHeight(),
                0,
                0);
    }

    @Override
    public void onMouseDown(int absX, int absY, MouseDownEvent mouseDownEvent) {
        TerrainView.getInstance().moveToMiddle(new Index(absX * getTerrainSettings().getTileWidth(), absY * getTerrainSettings().getTileHeight()));
    }
}