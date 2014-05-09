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

import com.btxtech.game.jsre.client.ClientExceptionHandler;
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
    private Path path;

    public PathMiniMap(int width, int height) {
        super(width, height);
        addMouseDownListener(this);
        addMouseMoveListener(this);
        loadBoundingBox(1);
    }

    @Override
    protected void render() {
        Context2d context2d = getContext2d();

        if (start != null) {
            context2d.setFillStyle("#ff69ff");
            context2d.beginPath();
            context2d.arc(absolute2RadarPositionX(start.getX()), absolute2RadarPositionY(start.getY()), 3, 0, 2 * Math.PI, false);
            context2d.fill();
        }

        if (destination != null) {
            context2d.setFillStyle("#ff6969");
            context2d.beginPath();
            context2d.arc(absolute2RadarPositionX(destination.getX()), absolute2RadarPositionY(destination.getY()), 3, 0, 2 * Math.PI, false);
            context2d.fill();
        }

        if (path != null && start != null) {
            context2d.setLineWidth(2.0);
            context2d.setStrokeStyle("#FFFFFF");
            pathfindingCockpit.clearPathTable();
            context2d.beginPath();
            context2d.moveTo(absolute2RadarPositionX(start.getX()), absolute2RadarPositionY(start.getY()));
            for (Index index : path.getPath()) {
                getContext2d().lineTo(absolute2RadarPositionX(index.getX()), absolute2RadarPositionY(index.getY()));
                //pathfindingCockpit.addPathTable(index);
            }
            context2d.stroke();
        }

    }

    public void loadBoundingBox(final int itemTypeId) {
        ItemTypeAccessAsync itemTypeAccess = GWT.create(ItemTypeAccess.class);
  /*      itemTypeAccess.getItemType(itemTypeId, new AsyncCallback<ItemType>() {
            @Override
            public void onFailure(Throwable caught) {
                log.log(Level.SEVERE, "createBoundingBox call failed", caught);
            }

            @Override
            public void onSuccess(ItemType itemType) {
                boundingBox = itemType.getBoundingBox();
            }
        });   */

    }

    @Override
    public void onMouseDown(int absX, int absY, MouseDownEvent mouseDownEvent) {
        clear();
        if (start == null) {
            start = new Index(absX, absY);
            destination = null;
            path = null;
            draw();
        } else {
            destination = new Index(absX, absY);
            if (boundingBox != null) {
                try {
                    path = ClientCollisionService.getInstance().setupPathToDestination(start, destination, TerrainType.LAND, boundingBox);
                } catch (Throwable t) {
                    path = null;
                    ClientExceptionHandler.handleException(t);
                }
            }
            draw();
            start = null;
            destination = null;
        }
    }

    public void findPath(Index start, Index destination) {
        if (boundingBox != null) {
            path = ClientCollisionService.getInstance().setupPathToDestination(start, destination, TerrainType.LAND, boundingBox);
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
