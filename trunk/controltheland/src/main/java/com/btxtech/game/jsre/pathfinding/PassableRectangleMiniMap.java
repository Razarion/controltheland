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

package com.btxtech.game.jsre.pathfinding;

import com.btxtech.game.jsre.client.ColorConstants;
import com.btxtech.game.jsre.client.cockpit.radar.MiniMap;
import com.btxtech.game.jsre.client.collision.ClientCollisionService;
import com.btxtech.game.jsre.client.common.Line;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.gameengine.services.collision.PassableRectangle;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainSettings;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainType;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.user.client.Random;

import java.util.Collection;
import java.util.Map;

/**
 * User: beat
 * Date: 28.06.2010
 * Time: 22:27:01
 */
public class PassableRectangleMiniMap extends MiniMap {
    public PassableRectangleMiniMap(int width, int height) {
        super(width, height);
    }

    @Override
    public void onTerrainSettings(TerrainSettings terrainSettings) {
        super.onTerrainSettings(terrainSettings);
        double scale = Math.min((double) getWidth() / (double) terrainSettings.getPlayFieldXSize(),
                (double) getHeight() / (double) terrainSettings.getPlayFieldYSize());
        getContext2d().restore();
        getContext2d().save();
        getContext2d().scale(scale, scale);
        getContext2d().setLineWidth(2.0 / scale);
        getContext2d().setStrokeStyle(ColorConstants.WHITE);
        setScale(scale);
    }

    public void showPassableRectangles() {
        Map<TerrainType, Collection<PassableRectangle>> terrainTypeCollectionMap = ClientCollisionService.getInstance().getPassableRectangles();
        for (PassableRectangle passableRectangle : terrainTypeCollectionMap.get(TerrainType.LAND)) {
            getContext2d().setFillStyle(CssColor.make(Random.nextInt(255), Random.nextInt(255), Random.nextInt(255)));
            Rectangle rectangle = passableRectangle.getRectangle();
            rectangle = TerrainView.getInstance().getTerrainHandler().convertToAbsolutePosition(rectangle);
            getContext2d().fillRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
            drawNeighbor(passableRectangle);
        }
    }

    private void drawNeighbor(PassableRectangle passableRectangle) {
        getContext2d().setLineWidth(2.0 / getScale());
        getContext2d().setStrokeStyle(ColorConstants.WHITE);
        for (PassableRectangle.Neighbor neighbor : passableRectangle.getNeighbors().values()) {
            Line line = neighbor.getPort().getCurrentCrossLine();
            getContext2d().beginPath();
            getContext2d().moveTo(line.getPoint1().getX(), line.getPoint1().getY());
            getContext2d().lineTo(line.getPoint2().getX(), line.getPoint2().getY());
            getContext2d().stroke();
        }
    }

}
