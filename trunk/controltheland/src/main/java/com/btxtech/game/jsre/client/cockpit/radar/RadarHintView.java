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

import com.btxtech.game.jsre.common.MathHelper;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 22.12.2009
 * Time: 21:52:27
 */
public class RadarHintView extends MiniMap {
    private static final int EDGE_LENGTH = 200;
    private Logger log = Logger.getLogger(RadarHintView.class.getName());
    private boolean visible = true;
    private SyncBaseItem enemyBaseItem;

    public RadarHintView(int width, int height) {
        super(width, height);
    }

    public void showHint(SyncBaseItem enemyBaseItem) {
        this.enemyBaseItem = enemyBaseItem;
        if (getTerrainSettings() == null) {
            return;
        }

        if (enemyBaseItem == null) {
            return;
        }

        showCross();
    }

    private void showCross() {
        getContext2d().save();
        double scale = Math.min((double) getWidth() / (double) getTerrainSettings().getPlayFieldXSize(),
                (double) getHeight() / (double) getTerrainSettings().getPlayFieldYSize()) / getScale();
        getContext2d().clearRect(0, 0, getTerrainSettings().getPlayFieldXSize(), getTerrainSettings().getPlayFieldYSize());
        try {
            getContext2d().scale(scale, scale);
        } catch (Exception e) {
            // Fails during tests
            log.log(Level.SEVERE, "", e);
        }
        getContext2d().setLineWidth(0.4 / scale);
        getContext2d().setStrokeStyle("#FF0000");

        int x = enemyBaseItem.getSyncItemArea().getPosition().getX();
        int y = enemyBaseItem.getSyncItemArea().getPosition().getY();

        getContext2d().beginPath();
        getContext2d().moveTo(x - EDGE_LENGTH, y);
        getContext2d().lineTo(x + EDGE_LENGTH, y);
        getContext2d().moveTo(x, y - EDGE_LENGTH);
        getContext2d().lineTo(x, y + EDGE_LENGTH);
        getContext2d().stroke();

        getContext2d().beginPath();
        getContext2d().arc(x, y, EDGE_LENGTH * 0.6, 0, MathHelper.ONE_RADIANT);
        getContext2d().stroke();

        getContext2d().restore();
    }

    public void hideHint() {
        clear();
    }

    public void blinkHint() {
        if (visible) {
            clear();
        } else {
            showCross();
        }
        visible = !visible;
    }
}
