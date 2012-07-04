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
import com.btxtech.game.jsre.client.cockpit.radar.MiniMapMouseMoveListener;
import com.btxtech.game.jsre.client.collision.ClientCollisionService;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.collision.Path;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainType;
import com.btxtech.game.jsre.itemtypeeditor.ItemTypeAccess;
import com.btxtech.game.jsre.itemtypeeditor.ItemTypeAccessAsync;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 28.06.2010
 * Time: 22:27:01
 */
public class PathMiniMap extends MiniMap implements MiniMapMouseDownListener, MiniMapMouseMoveListener {
    private Index start;
    private Index destination;
    private PathfindingCockpit pathfindingCockpit;
    private Logger log = Logger.getLogger(PathMiniMap.class.getName());
    private BoundingBox boundingBox;

    public PathMiniMap(int width, int height) {
        super(width, height,  Scale.ABSOLUTE);
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
    public void onMouseDown(int absX, int absY, MouseDownEvent mouseDownEvent) {
        clear();
        if (start == null) {
            start = new Index(absX, absY);
            destination = null;
            drawStartAndDestination();
        } else {
            destination = new Index(absX, absY);
            drawStartAndDestination();
            findPathAbsolutePath(start, destination);
            start = null;
        }
    }

    public void findPath(Index start, Index destination) {
        clear();
        this.start = start;
        this.destination = destination;
        drawStartAndDestination();
        findPathAbsolutePath(start, destination);
    }

    private void drawStartAndDestination() {
        Context2d context2d = getContext2d();

        if (start != null) {
            context2d.setFillStyle("#ff69ff");
            context2d.beginPath();
            context2d.arc(start.getX(), start.getY(), 20, 0, 2 * Math.PI, false);
            context2d.fill();
        }

        if (destination != null) {
            context2d.setFillStyle("#ff6969");
            context2d.beginPath();
            context2d.arc(destination.getX(), destination.getY(), 20, 0, 2 * Math.PI, false);
            context2d.fill();
        }
    }

    public void findPathAbsolutePath(final Index start, Index destination) {
        if (boundingBox == null) {
            return;
        }
        try {
            Path path = ClientCollisionService.getInstance().setupPathToDestination(start, destination, TerrainType.LAND, boundingBox);
            getContext2d().setLineWidth(2.0 / getScale());
            getContext2d().setStrokeStyle("#FFFFFF");
            pathfindingCockpit.clearPathTable();
            getContext2d().beginPath();
            getContext2d().moveTo(start.getX(), start.getY());
            for (Index index : path.getPath()) {
                getContext2d().lineTo(index.getX(), index.getY());
                //pathfindingCockpit.addPathTable(index);
            }
            getContext2d().stroke();
        } catch (Throwable t) {
            GwtCommon.handleException(t);
        }
    }

    @Override
    public void onMouseMove(int absX, int absY) {
        pathfindingCockpit.showMousePosition(absX, absY);
    }

    public void setPathfindingCockpit(PathfindingCockpit pathfindingCockpit) {
        this.pathfindingCockpit = pathfindingCockpit;
    }
}
