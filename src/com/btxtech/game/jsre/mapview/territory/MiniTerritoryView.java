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

package com.btxtech.game.jsre.mapview.territory;

import com.btxtech.game.jsre.client.cockpit.radar.MiniMap;
import com.btxtech.game.jsre.client.cockpit.radar.MiniMapMouseDownListener;
import com.btxtech.game.jsre.client.cockpit.radar.MiniMapMouseMoveListener;
import com.btxtech.game.jsre.client.cockpit.radar.MiniMapMouseUpListener;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.client.territory.ClientTerritoryService;
import com.btxtech.game.jsre.common.Territory;
import com.btxtech.game.jsre.mapview.common.GeometricalUtil;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.widgetideas.graphics.client.Color;
import java.util.HashSet;

/**
 * User: beat
 * Date: 24.05.2010
 * Time: 13:54:34
 */
public class MiniTerritoryView extends MiniMap implements MiniMapMouseMoveListener, MiniMapMouseDownListener, MiniMapMouseUpListener {
    private int brushSize = 10;
    private HashSet<Index> tiles = new HashSet<Index>();
    private boolean drawMode = false;
    private boolean eraseMode = false;
    private Territory territory;

    public MiniTerritoryView(int width, int height) {
        super(width, height);
        addMouseMoveListener(this);
        addMouseDownListener(this);
        addMouseUpListener(this);
    }

    @Override
    public void onMouseMove(int absX, int absY) {
        if (!drawMode && !eraseMode) {
            return;
        }
        int tmpSize = tiles.size();
        Index tile = TerrainView.getInstance().getTerrainHandler().getTerrainTileIndexForAbsPosition(absX, absY);
        for (int x = -brushSize / 2; x < brushSize / 2; x++) {
            for (int y = -brushSize / 2; y < brushSize / 2; y++) {
                int newX = tile.getX() + x;
                if (newX < 0) {
                    newX = 0;
                }
                int newY = tile.getY() + y;
                if (newY < 0) {
                    newY = 0;
                }
                if (drawMode) {
                    Territory territory = ClientTerritoryService.getInstance().getTerritoryTile(newX, newY);
                    if (territory == null || territory.equals(this.territory)) {
                        tiles.add(new Index(newX, newY));
                    }
                } else if (eraseMode) {
                    tiles.remove(new Index(newX, newY));
                }
            }
        }
        if (tmpSize != tiles.size()) {
            drawTiles();
        }
    }

    @Override
    public void onMouseDown(int absX, int absY, MouseDownEvent mouseDownEvent) {
        if (mouseDownEvent.getNativeButton() == NativeEvent.BUTTON_LEFT) {
            drawMode = true;
            eraseMode = false;
        } else if (mouseDownEvent.getNativeButton() == NativeEvent.BUTTON_RIGHT) {
            drawMode = false;
            eraseMode = true;
        } else {
            drawMode = false;
            eraseMode = false;
        }
    }

    @Override
    public void onMouseUp(int absX, int absY, MouseUpEvent event) {
        drawMode = false;
        eraseMode = false;
    }

    public void drawTiles() {
        if (ClientTerritoryService.getInstance().getTerritories() == null || getTerrainSettings() == null) {
            return;
        }

        clear(getTerrainSettings().getPlayFieldXSize(), getTerrainSettings().getPlayFieldYSize());
        setFillStyle(Color.ALPHA_RED);
        for (Index index : tiles) {
            Index absIndex = TerrainView.getInstance().getTerrainHandler().getAbsolutIndexForTerrainTileIndex(index);
            fillRect(absIndex.getX(), absIndex.getY(), getTerrainSettings().getTileWidth(), getTerrainSettings().getTileHeight());
        }
        setFillStyle(Color.ALPHA_GREY);
        for (Territory territory : ClientTerritoryService.getInstance().getTerritories()) {
            if (!territory.equals(this.territory)) {
                for (Rectangle rectangle : territory.getTerritoryTileRegions()) {
                    Rectangle absRect = TerrainView.getInstance().getTerrainHandler().convertToAbsolutePosition(rectangle);
                    fillRect(absRect.getX(), absRect.getY(), absRect.getWidth(), absRect.getHeight());
                }
            }
        }
    }

    public HashSet<Index> getTiles() {
        return tiles;
    }

    public void setTerritory(Territory territory) {
        this.territory = territory;
        tiles = new HashSet<Index>(GeometricalUtil.splitIntoTiles(territory.getTerritoryTileRegions()));
        drawTiles();
    }
}
