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

import com.btxtech.game.jsre.client.ExtendedCanvas;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.terrain.TerrainHandler;
import com.btxtech.game.jsre.client.terrain.TerrainListener;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImagePosition;
import com.btxtech.game.jsre.mapeditor.TerrainInfo;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.Random;
import com.google.gwt.widgetideas.graphics.client.Color;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import java.util.List;

/**
 * User: beat
 * Date: Sep 2, 2009
 * Time: 6:51:16 PM
 */
public class PathfindingEntry implements EntryPoint, MouseDownHandler {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 800;
    private ExtendedCanvas extendedCanvas;
    private List<Rectangle> passableRectangle;
    private TerrainInfo terrainInfo;
    private Index start;
    private Index destination;
    private PathfindingAsync pathfinding;

    @Override
    public void onModuleLoad() {
        // Setup common
        GwtCommon.setUncaughtExceptionHandler();
        GwtCommon.disableBrowserContextMenuJSNI();

        pathfinding = GWT.create(Pathfinding.class);
        pathfinding.getPassableRectangles(new AsyncCallback<List<Rectangle>>() {
            @Override
            public void onFailure(Throwable throwable) {
                GwtCommon.handleException(throwable);
            }

            @Override
            public void onSuccess(List<Rectangle> rectangles) {
                passableRectangle = rectangles;
                handleMap();
            }
        });
        pathfinding.getTerrainInfo(new AsyncCallback<TerrainInfo>() {
            @Override
            public void onFailure(Throwable throwable) {
                GwtCommon.handleException(throwable);
            }

            @Override
            public void onSuccess(TerrainInfo terrainInfo) {
                PathfindingEntry.this.terrainInfo = terrainInfo;
                handleMap();
            }
        });
    }

    private void handleMap() {
        if (terrainInfo != null && passableRectangle != null) {
            extendedCanvas = new ExtendedCanvas(WIDTH, HEIGHT);
            extendedCanvas.resize(terrainInfo.getTerrainSettings().getPlayFieldXSize(), terrainInfo.getTerrainSettings().getPlayFieldYSize());
            extendedCanvas.scale((double) WIDTH / (double) terrainInfo.getTerrainSettings().getPlayFieldXSize(),
                    (double) HEIGHT / (double) terrainInfo.getTerrainSettings().getPlayFieldYSize());
            RootPanel.get().add(extendedCanvas);
            extendedCanvas.addMouseDownHandler(this);
            showPassableRectangles();
            showMap();
        }
    }

    private void showPassableRectangles() {
        for (Rectangle rectangle : passableRectangle) {
            extendedCanvas.setFillStyle(new Color(Random.nextInt(255), Random.nextInt(255), Random.nextInt(255), (float) 0.5));
            extendedCanvas.fillRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
        }
    }

    private void showMap() {
        final TerrainHandler terrainHandler = new TerrainHandler();
        terrainHandler.addTerrainListener(new TerrainListener() {
            @Override
            public void onTerrainChanged() {
                for (TerrainImagePosition terrainImagePosition : terrainHandler.getTerrainImagePositions()) {
                    Index absolute = terrainHandler.getAbsolutIndexForTerrainTileIndex(terrainImagePosition.getTileIndex());
                    ImageElement imageElement = terrainHandler.getTerrainImageElement(terrainImagePosition.getImageId());
                    if (imageElement != null) {
                        extendedCanvas.drawImage(imageElement, absolute.getX(), absolute.getY());
                    }
                }
            }
        });
        terrainHandler.setupTerrain(terrainInfo.getTerrainSettings(),
                terrainInfo.getTerrainImagePositions(),
                terrainInfo.getSurfaceImages(),
                terrainInfo.getTerrainImages());
    }


    @Override
    public void onMouseDown(MouseDownEvent mouseDownEvent) {
        if (start == null) {
            start = new Index(mouseDownEvent.getClientX(), mouseDownEvent.getClientY());
        } else {
            destination = new Index(mouseDownEvent.getClientX(), mouseDownEvent.getClientY());
            findPath(start, destination);
            start = null;
            destination = null;
        }
    }

    private void findPath(Index start, Index destination) {
        System.out.println("toAbsIndex(start): " + toAbsIndex(start));
        System.out.println("toAbsIndex(destination): " + toAbsIndex(destination));
        pathfinding.findPath(toAbsIndex(start), toAbsIndex(destination), new AsyncCallback<List<Index>>() {
            @Override
            public void onFailure(Throwable throwable) {
                GwtCommon.handleException(throwable);
            }

            @Override
            public void onSuccess(List<Index> indexes) {
                if (indexes == null) {
                    System.out.println("Error on server");
                    return;
                }
                extendedCanvas.setStrokeStyle(Color.BLACK);
                extendedCanvas.setLineWidth(50);

                extendedCanvas.beginPath();
                boolean first = true;
                for (Index index : indexes) {
                    System.out.println(index);
                    if (first) {
                        first = false;
                        extendedCanvas.moveTo(index.getX(), index.getY());
                    } else {
                        extendedCanvas.lineTo(index.getX(), index.getY());
                    }
                }
                extendedCanvas.stroke();
            }
        });
    }

    private Index toAbsIndex(Index mapIndex) {
        double factorX = (double) terrainInfo.getTerrainSettings().getPlayFieldXSize() / (double) WIDTH;
        double factorY = (double) terrainInfo.getTerrainSettings().getPlayFieldYSize() / (double) HEIGHT;
        return new Index((int) (mapIndex.getX() * factorX), (int) (mapIndex.getY() * factorY));
    }

}