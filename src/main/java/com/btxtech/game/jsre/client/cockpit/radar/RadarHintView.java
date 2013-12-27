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

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.MathHelper;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;

/**
 * User: beat
 * Date: 22.12.2009
 * Time: 21:52:27
 */
@Deprecated
public class RadarHintView extends MiniMap {
    private static final int EDGE_LENGTH = 15;
    private boolean color = true;
    private SyncBaseItem enemyBaseItem;
    private Index position;

    public RadarHintView(int width, int height) {
        super(width, height);
    }

    public void showHint(SyncBaseItem enemyBaseItem) {
        this.enemyBaseItem = enemyBaseItem;
        position = null;
        if (enemyBaseItem == null) {
            return;
        }
        draw();
    }

    public void showHint(Index position) {
        this.position = position;
        enemyBaseItem = null;
        draw();
    }

    @Override
    protected void render() {
        int x;
        int y;
        if (enemyBaseItem != null) {
            x = absolute2RadarPositionX(enemyBaseItem.getSyncItemArea().getPosition());
            y = absolute2RadarPositionY(enemyBaseItem.getSyncItemArea().getPosition());
        } else if (position != null) {
            x = absolute2RadarPositionX(position);
            y = absolute2RadarPositionY(position);
        } else {
            // No hint to display
            return;
        }

        getContext2d().setLineWidth(2.0);
        if (color) {
            getContext2d().setStrokeStyle("#000000");
        } else {
            getContext2d().setStrokeStyle("#FFFFFF");
        }

        getContext2d().beginPath();
        getContext2d().moveTo(x - EDGE_LENGTH, y);
        getContext2d().lineTo(x + EDGE_LENGTH, y);
        getContext2d().moveTo(x, y - EDGE_LENGTH);
        getContext2d().lineTo(x, y + EDGE_LENGTH);
        getContext2d().stroke();

        getContext2d().beginPath();
        getContext2d().arc(x, y, EDGE_LENGTH * 0.6, 0, MathHelper.ONE_RADIANT);
        getContext2d().stroke();
    }

    public void hideHint() {
        clear();
    }

    public void blinkHint() {
        draw();
        color = !color;
    }

    @Override
    public void cleanup() {
        hideHint();
        enemyBaseItem = null;
        position = null;

    }
}
