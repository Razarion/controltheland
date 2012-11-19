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

package com.btxtech.game.jsre.common;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.services.terrain.AbstractTerrainService;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;

import java.util.Collection;

/**
 * User: beat
 * Date: 15.11.2009
 * Time: 21:02:28
 */
public class RectangleFormation {
    private Index origin;
    //Making own index to prevent negative value exception
    private int lastX;
    private int lastY;
    private int maxDiameter = 0;
    private int lineCount = 0;
    private int linePartCount = 1;
    private int currentLinePartCount = 0;
    private AbstractTerrainService terrainService;

    public RectangleFormation(Index origin, Collection<SyncBaseItem> syncBaseItems, AbstractTerrainService terrainService) {
        this.terrainService = terrainService;
        for (SyncBaseItem syncBaseItem : syncBaseItems) {
            maxDiameter = Math.max(syncBaseItem.getItemType().getBoundingBox().getDiameter(), maxDiameter);
        }
        maxDiameter++;
        this.origin = terrainService.correctPosition(maxDiameter / 2, origin);
    }

    public Index calculateNextEntry() {
        if (lineCount == 0) {
            lineCount++;
            lastX = origin.getX();
            lastY = origin.getY();
            return origin;
        }
        currentLinePartCount++;
        if (lineCount % 4 == 1) {
            // X side increase
            lastX += maxDiameter;
            if (currentLinePartCount == linePartCount) {
                lineCount++;
                currentLinePartCount = 0;
            }
            return returnIndex();
        } else if (lineCount % 4 == 2) {
            //Y side increase
            lastY += maxDiameter;
            if (currentLinePartCount == linePartCount) {
                lineCount++;
                linePartCount++;
                currentLinePartCount = 0;
            }
            return returnIndex();
        } else if (lineCount % 4 == 3) {
            //X side decrease
            lastX -= maxDiameter;
            if (currentLinePartCount == linePartCount) {
                lineCount++;
                currentLinePartCount = 0;
            }
            return returnIndex();
        } else {
            //Y side decrease
            lastY -= maxDiameter;
            if (currentLinePartCount == linePartCount) {
                lineCount++;
                linePartCount++;
                currentLinePartCount = 0;
            }
            return returnIndex();
        }
    }

    private Index returnIndex() {
        Index position = new Index(lastX, lastY);
        Index corrected = terrainService.correctPosition(maxDiameter / 2, position);
        if (corrected.equals(position)) {
            return position;
        } else {
            return null;
        }
    }
}
