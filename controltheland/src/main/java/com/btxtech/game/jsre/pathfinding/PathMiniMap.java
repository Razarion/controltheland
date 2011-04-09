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

import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.cockpit.radar.MiniMap;
import com.btxtech.game.jsre.client.cockpit.radar.MiniMapMouseDownListener;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainType;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.widgetideas.graphics.client.Color;

import java.util.List;

/**
 * User: beat
 * Date: 28.06.2010
 * Time: 22:27:01
 */
public class PathMiniMap extends MiniMap implements MiniMapMouseDownListener {
    private Index start;
    private PathfindingAsync pathfinding;

    public PathMiniMap(int width, int height, PathfindingAsync pathfinding) {
        super(width, height);
        this.pathfinding = pathfinding;
        addMouseDownListener(this);
    }


    @Override
    public void onMouseDown(int absX, int absY, MouseDownEvent mouseDownEvent) {
        if (start == null) {
            start = TerrainView.getInstance().getTerrainHandler().getAbsolutIndexForTerrainTileIndex(absX, absY);
        } else {
            findPath(start, TerrainView.getInstance().getTerrainHandler().getAbsolutIndexForTerrainTileIndex(absX, absY));
            start = null;
        }
    }

    private void findPath(final Index start, Index destination) {
        pathfinding.findPath(start, destination, TerrainType.LAND, new AsyncCallback<List<Index>>() {
            @Override
            public void onFailure(Throwable throwable) {
                GwtCommon.handleException(throwable);
            }

            @Override
            public void onSuccess(List<Index> indexes) {
                if (indexes == null) {
                    return;
                }
                displayPath(start, indexes);
            }
        });
    }

    private void displayPath(Index start, List<Index> indexes) {
        clear();
        setStrokeStyle(Color.BLACK);
        setLineWidth(1);

        beginPath();
        start = TerrainView.getInstance().getTerrainHandler().getTerrainTileIndexForAbsPosition(start);
        moveTo(start.getX(), start.getY());
        for (Index index : indexes) {
            index = TerrainView.getInstance().getTerrainHandler().getTerrainTileIndexForAbsPosition(index);            
            lineTo(index.getX(), index.getY());
        }
        stroke();

    }
}
