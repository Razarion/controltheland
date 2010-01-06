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

package com.btxtech.game.services;

import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.services.collision.CollisionService;
import com.btxtech.game.services.terrain.TerrainService;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

/**
 * User: beat
 * Date: 01.11.2009
 * Time: 13:47:48
 */
public class AwtMapFrame implements MouseListener {
    private final double zoom = 0.25;
    private Frame frame;
    private int tileWeidth;
    private int tileHeight;
    private CollisionService collisionService;
    private TerrainService terrainService;
    private Point start;
    private List<Index> path;

    public AwtMapFrame(CollisionService collisionService, TerrainService terrainService) {
        this.collisionService = collisionService;
        this.terrainService = terrainService;

        int length = (int) (terrainService.getPlayFieldXSize() * zoom);
        int height = (int) (terrainService.getPlayFieldYSize() * zoom);
        tileWeidth = (int) (Constants.TILE_WIDTH * zoom);
        tileHeight = (int) (Constants.TILE_HEIGHT * zoom);


        frame = new Frame() {
            @Override
            public void paint(Graphics graphics) {
                setupTerrain(graphics);
                super.paint(graphics);
            }
        };
        frame.setSize(length, height);
        frame.setVisible(true);
        frame.repaint();
        frame.addMouseListener(this);

    }

    private void setupTerrain(Graphics graphics) {
        for (int x = 0; x < terrainService.getTerrainField().length; x++) {
            int yTiles[] = terrainService.getTerrainField()[x];
            for (int y = 0; y < yTiles.length; y++) {
                int tileId = yTiles[y];
         //       if (!terrainService.getTile(tileId).checkAllowedItem(ItemType.LAND_ITEM)) {
         //           graphics.fillRect(x * tileWeidth, y * tileHeight, tileWeidth, tileHeight);
         //       }
            }
        }
        if (path != null) {
            Index prev = null;
            for (Index index : path) {
                if (prev != null) {
                    graphics.drawLine((int) (prev.getX() * zoom), (int) (prev.getY() * zoom), (int) (index.getX() * zoom), (int) (index.getY() * zoom));
                }
                prev = index;
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        // Unused
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        if (start == null) {
            start = mouseEvent.getPoint();
            path = null;
        } else {
            Index startIndex = new Index((int) (start.getX() / zoom), (int) (start.getY() / zoom));
            Point destination = mouseEvent.getPoint();
            Index destinationIndex = new Index((int) (destination.getX() / zoom), (int) (destination.getY() / zoom));
            long time = System.currentTimeMillis();
          //  path = collisionService.getPath(startIndex, destinationIndex);
            System.out.println("Time used: " + (System.currentTimeMillis() - time));
            path.add(0, startIndex);
            start = null;
        }
        frame.repaint();
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        // Unused
    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {
        // Unused
    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {
        // Unused
    }
}
