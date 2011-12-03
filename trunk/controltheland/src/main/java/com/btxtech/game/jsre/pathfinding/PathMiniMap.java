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
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.cockpit.radar.MiniMap;
import com.btxtech.game.jsre.client.cockpit.radar.MiniMapMouseDownListener;
import com.btxtech.game.jsre.client.cockpit.radar.MiniMapMouseMoveListener;
import com.btxtech.game.jsre.client.collision.ClientCollisionService;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainSettings;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainType;
import com.btxtech.game.jsre.itemtypeeditor.ItemTypeAccess;
import com.btxtech.game.jsre.itemtypeeditor.ItemTypeAccessAsync;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 28.06.2010
 * Time: 22:27:01
 */
public class PathMiniMap extends MiniMap implements MiniMapMouseDownListener, MiniMapMouseMoveListener {
    private Index start;
    private PathfindingCockpit pathfindingCockpit;
    private Logger log = Logger.getLogger(PathMiniMap.class.getName());
    private BoundingBox boundingBox;

    public PathMiniMap(int width, int height) {
        super(width, height);
        addMouseDownListener(this);
        addMouseMoveListener(this);
        loadBoundingBox(1);
    }

    public void loadBoundingBox(final int itemTypeId) {
        ItemTypeAccessAsync itemTypeAccess = GWT.create(ItemTypeAccess.class);
        itemTypeAccess.getItemType(itemTypeId, new AsyncCallback<ItemType>() {
            @Override
            public void onFailure(Throwable caught) {
                log.log(Level.SEVERE, "getBoundingBox call failed", caught);
            }

            @Override
            public void onSuccess(ItemType itemType) {
                boundingBox = itemType.getBoundingBox();
            }
        });

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

    @Override
    public void onMouseDown(int absX, int absY, MouseDownEvent mouseDownEvent) {
        if (start == null) {
            start = new Index(absX, absY);
        } else {
            findPath(start, new Index(absX, absY));
            start = null;
        }
    }

    public void findPath(final Index start, Index destination) {
        if (boundingBox == null) {
            return;
        }
        try {
            List<Index> indexes = ClientCollisionService.getInstance().setupPathToDestination(start, destination, TerrainType.LAND, boundingBox);
            if (indexes == null) {
                return;
            }
            displayPath(start, indexes);
        } catch (Throwable t) {
            GwtCommon.handleException(t);
        }
    }

    private void displayPath(Index start, List<Index> indexes) {
        clear();
        getContext2d().beginPath();
        getContext2d().moveTo(start.getX(), start.getY());
        for (Index index : indexes) {
            getContext2d().lineTo(index.getX(), index.getY());
        }
        getContext2d().stroke();
    }

    @Override
    protected void clear() {
        getContext2d().clearRect(0, 0, getTerrainSettings().getPlayFieldXSize(), getTerrainSettings().getPlayFieldYSize());
    }

    @Override
    public void onMouseMove(int absX, int absY) {
        pathfindingCockpit.showMousePosition(absX, absY);
    }

    public void setPathfindingCockpit(PathfindingCockpit pathfindingCockpit) {
        this.pathfindingCockpit = pathfindingCockpit;
    }
}
