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
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.user.client.Timer;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 22.12.2009
 * Time: 21:52:27
 */
public class RadarFrameView extends MiniMap implements TerrainScrollListener, MiniMapMouseDownListener {
    private static final String COLOR_1 = ColorConstants.LIGHTGREY;
    private static final String COLOR_2 = ColorConstants.RED;
    private int left;
    private int top;
    private int width;
    private int height;
    private String color = COLOR_1;
    private Timer timer;
    private Logger log = Logger.getLogger(RadarFrameView.class.getName());

    public RadarFrameView(int width, int height) {
        super(width, height,  Scale.TILE);
        TerrainView.getInstance().addTerrainScrollListener(this);
        addMouseDownListener(this);
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
        getContext2d().setStrokeStyle(color);
        getContext2d().beginPath();
        double leftDouble = (double) left / (double) getTerrainSettings().getTileWidth();
        double topDouble = (double) top / (double) getTerrainSettings().getTileHeight();
        double widthDouble = (double) width / (double) getTerrainSettings().getTileWidth();
        double heightDouble = (double) height / (double) getTerrainSettings().getTileHeight();
        getContext2d().rect(leftDouble, topDouble, widthDouble, heightDouble);
        getContext2d().stroke();

    }

    @Override
    public void onTerrainSettings(TerrainSettings terrainSettings) {
        super.onTerrainSettings(terrainSettings);
        getContext2d().setLineWidth(2.0 / getScale());
        onScroll(TerrainView.getInstance().getViewOriginLeft(),
                TerrainView.getInstance().getViewOriginTop(),
                TerrainView.getInstance().getViewWidth(),
                TerrainView.getInstance().getViewHeight(),
                0,
                0);
        if (timer == null) {
            timer = new Timer() {

                @Override
                public void run() {
                    try {
                        if (color.equals(COLOR_1)) {
                            color = COLOR_2;
                        } else {
                            color = COLOR_1;
                        }
                        drawFrame();
                    } catch (Throwable t) {
                        log.log(Level.SEVERE, "Exception in RadarItemView Timer", t);
                    }
                }
            };
            timer.scheduleRepeating(1000);
        }
    }

    @Override
    public void onMouseDown(int absX, int absY, MouseDownEvent mouseDownEvent) {
        TerrainView.getInstance().moveToMiddle(new Index(absX * getTerrainSettings().getTileWidth(), absY * getTerrainSettings().getTileHeight()));
    }

    @Override
    public void cleanup() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}
