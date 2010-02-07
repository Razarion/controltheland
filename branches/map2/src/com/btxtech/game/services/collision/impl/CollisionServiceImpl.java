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

package com.btxtech.game.services.collision.impl;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.terrain.TerrainListener;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.services.collision.CollisionService;
import com.btxtech.game.services.collision.CollisionServiceChangedListener;
import com.btxtech.game.services.collision.PassableRectangle;
import com.btxtech.game.services.collision.Path;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.terrain.DbTerrainSetting;
import com.btxtech.game.services.terrain.TerrainService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import javax.annotation.PostConstruct;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * User: beat
 * Date: May 24, 2009
 * Time: 6:44:01 PM
 */
public class CollisionServiceImpl implements CollisionService, TerrainListener {
    @Autowired
    private TerrainService terrainService;
    @Autowired
    private ItemService itemService;
    private boolean[][] passableTerrain;
    private List<PassableRectangle> passableRectangles = new ArrayList<PassableRectangle>();
    private Log log = LogFactory.getLog(CollisionServiceImpl.class);
    private ArrayList<CollisionServiceChangedListener> collisionServiceChangedListeners = new ArrayList<CollisionServiceChangedListener>();

    @PostConstruct
    public void init() {
        terrainService.addTerrainListener(this);
        setupPassableTerrain();
    }

    private void setupPassableTerrain() {
        DbTerrainSetting dbTerrainSetting = terrainService.getDbTerrainSettings();
        passableTerrain = new boolean[dbTerrainSetting.getTileXCount()][dbTerrainSetting.getTileYCount()];
        for (int x = 0; x < dbTerrainSetting.getTileXCount(); x++) {
            for (int y = 0; y < dbTerrainSetting.getTileYCount(); y++) {
                Index absolute = terrainService.getAbsolutIndexForTerrainTileIndex(x, y);
                passableTerrain[x][y] = terrainService.isTerrainPassable(absolute);
            }
        }

        setupPassableRectangles();
    }

    private void setupPassableRectangles() {
        Collection<Index> passableTiles = getPassableTiles();
        if (passableTiles.isEmpty()) {
            log.error("Terrain does not have any passable terrain tiles");
            return;
        }

        ArrayList<Rectangle> mapAsRectangles = separateIntoRectangles(passableTiles);

        buildPassableRectangleList(mapAsRectangles);
        for (CollisionServiceChangedListener collisionServiceChangedListener : collisionServiceChangedListeners) {
            collisionServiceChangedListener.collisionServiceChanged();
        }
    }

    private void buildPassableRectangleList(ArrayList<Rectangle> rectangles) {
        passableRectangles.clear();
        for (Rectangle rectangle : rectangles) {
            PassableRectangle passableRectangle = new PassableRectangle(rectangle);
            passableRectangles.add(passableRectangle);
        }

        List<PassableRectangle> remaining = new ArrayList<PassableRectangle>(passableRectangles);
        while (!remaining.isEmpty()) {
            PassableRectangle passableRectangle = remaining.remove(0);
            for (PassableRectangle possibleNeighbor : remaining) {
                if (passableRectangle.getRectangle().adjoins(possibleNeighbor.getRectangle())) {
                    passableRectangle.addNeighbor(possibleNeighbor);
                    possibleNeighbor.addNeighbor(passableRectangle);
                }
            }
        }
    }

    private ArrayList<Rectangle> separateIntoRectangles(Collection<Index> passableTiles) {
        ArrayList<Rectangle> rectangles = new ArrayList<Rectangle>();
        Collection<Index> remainingTiles = new HashSet<Index>(passableTiles);
        removeRectangles(remainingTiles, rectangles, passableTiles.iterator().next());
        return rectangles;
    }

    private void removeRectangles(Collection<Index> remainingAtoms, ArrayList<Rectangle> rectangles, Index start) {
        Rectangle rectangle = getRectangles(start.getX(), start.getY(), remainingAtoms);
        rectangles.add(rectangle);

        // remove used atoms
        for (int x = rectangle.getStart().getX(); x <= rectangle.getEnd().getX(); x++) {
            for (int y = rectangle.getStart().getY(); y <= rectangle.getEnd().getY(); y++) {
                Index indexToRemove = new Index(x, y);
                remainingAtoms.remove(indexToRemove);
            }
        }
        // Make inclusive
        rectangle.growEast(1);
        rectangle.growSouth(1);

        if (remainingAtoms.isEmpty()) {
            return;
        }

        Index newStart = remainingAtoms.iterator().next();
        removeRectangles(remainingAtoms, rectangles, newStart);
    }


    private Collection<Index> getPassableTiles() {
        HashSet<Index> passableTiles = new HashSet<Index>();
        for (int x = 0; x < terrainService.getDbTerrainSettings().getTileXCount(); x++) {
            for (int y = 0; y < terrainService.getDbTerrainSettings().getTileYCount(); y++) {
                Index abs = terrainService.getTerrainTileIndexForAbsPosition(x, y);
                if (terrainService.getTerrainImagePosition(abs.getX(), abs.getY()) == null) {
                    passableTiles.add(new Index(x, y));
                }
            }
        }
        return passableTiles;
    }

    private Rectangle getRectangles(int stratX, int startY, Collection<Index> passableMap) {
        Index index = new Index(stratX, startY);
        if (!passableMap.contains(index)) {
            throw new IllegalArgumentException("Invalid start point");
        }

        Rectangle rectangle = new Rectangle(index, index);
        boolean canGrowNorth = true;
        boolean canGrowEast = true;
        boolean canGrowSouth = true;
        boolean canGrowWest = true;

        while (canGrowNorth | canGrowEast | canGrowSouth | canGrowWest) {
            if (rectangle.getStart().getY() == 0) {
                canGrowNorth = false;
            }

            if (rectangle.getEnd().getX() == terrainService.getDbTerrainSettings().getPlayFieldXSize() - 1) {
                canGrowEast = false;
            }

            if (rectangle.getEnd().getY() == terrainService.getDbTerrainSettings().getPlayFieldYSize() - 1) {
                canGrowSouth = false;
            }

            if (rectangle.getStart().getX() == 0) {
                canGrowWest = false;
            }

            if (canGrowNorth) {
                Rectangle newRectangle = rectangle.copy();
                newRectangle.growNorth(1);
                int size = Math.abs(newRectangle.getStart().getX() - newRectangle.getEnd().getX());
                if (checkPassableAtomsHorizontal(newRectangle.getStart().getX(), newRectangle.getStart().getY(), size, passableMap)) {
                    rectangle = newRectangle;
                } else {
                    canGrowNorth = false;
                }
            }
            if (canGrowEast) {
                Rectangle newRectangle = rectangle.copy();
                newRectangle.growEast(1);
                int size = Math.abs(newRectangle.getStart().getY() - newRectangle.getEnd().getY());
                if (checkPassableAtomsVertial(newRectangle.getEnd().getX(), newRectangle.getStart().getY(), size, passableMap)) {
                    rectangle = newRectangle;
                } else {
                    canGrowEast = false;
                }
            }
            if (canGrowSouth) {
                Rectangle newRectangle = rectangle.copy();
                newRectangle.growSouth(1);
                int size = Math.abs(newRectangle.getStart().getX() - newRectangle.getEnd().getX());
                if (checkPassableAtomsHorizontal(newRectangle.getStart().getX(), newRectangle.getEnd().getY(), size, passableMap)) {
                    rectangle = newRectangle;
                } else {
                    canGrowSouth = false;
                }
            }
            if (canGrowWest) {
                Rectangle newRectangle = rectangle.copy();
                newRectangle.growWest(1);
                int size = Math.abs(newRectangle.getStart().getY() - newRectangle.getEnd().getY());
                if (checkPassableAtomsVertial(newRectangle.getStart().getX(), newRectangle.getStart().getY(), size, passableMap)) {
                    rectangle = newRectangle;
                } else {
                    canGrowWest = false;
                }
            }
        }
        return rectangle;
    }

    private boolean checkPassableAtomsVertial(int startX, int startY, int length, Collection<Index> passableMap) {
        for (int y = startY; y <= (startY + length); y++) {
            Index index = new Index(startX, y);
            if (!passableMap.contains(index)) {
                return false;
            }
        }
        return true;
    }


    private boolean checkPassableAtomsHorizontal(int startX, int startY, int length, Collection<Index> passableMap) {
        for (int x = startX; x <= (startX + length); x++) {
            Index index = new Index(x, startY);
            if (!passableMap.contains(index)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public List<PassableRectangle> getPassableRectangles() {
        return passableRectangles;
    }


    // @Override
    // TODO wrong here
    public boolean isFree(Index position, ItemType toBePlaced) {
        // TODO
        /*Index atomPosition = TerrainUtil.getTerrainTileIndexForAbsPosition(position);
        int tileWidth = TerrainUtil.getTerrainTileCount4PixelWidth(toBePlaced.getWidth());
        int tileHeight = TerrainUtil.getTerrainTileCount4PixelHeight(toBePlaced.getHeight());

        for (int x = atomPosition.getX() - (tileWidth / 2); x <= atomPosition.getX() + (tileWidth / 2); x++) {
            for (int y = atomPosition.getY() - (tileHeight / 2); y <= atomPosition.getY() + (tileHeight / 2); y++) {
                if (!passableTerrain[x][y]) {
                    return false;
                }
            }
        }*/
        return true;
    }

    @Override
    public Index getFreeRandomPosition(ItemType itemType, int edgeLength) {
        Random random = new Random();
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            int x = random.nextInt(terrainService.getDbTerrainSettings().getPlayFieldXSize() - 200) + 100;
            int y = random.nextInt(terrainService.getDbTerrainSettings().getPlayFieldYSize() - 200) + 100;
            Index point = new Index(x, y);
            if (!isFree(point, itemType)) {
                continue;
            }
            Index start = point.sub(new Index(edgeLength / 2, edgeLength / 2));
            Rectangle rectangle = new Rectangle(start.getX(), start.getY(), edgeLength, edgeLength);
            if (itemService.hasItemsInRectangle(rectangle)) {
                continue;
            }
            return point;
        }
        throw new IllegalStateException("Can not find free position");
    }

     @Override
    public Index getFreeRandomPositionInRect(ItemType itemType, SyncItem attackerItem, int targetMinRange, int targetMaxRange) {
        Random random = new Random();
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            int x;
            int y;
            if (random.nextBoolean()) {
                x = attackerItem.getPosition().getX() + targetMinRange + random.nextInt(targetMaxRange - targetMinRange);
            } else {
                x = attackerItem.getPosition().getX() - targetMinRange - random.nextInt(targetMaxRange - targetMinRange);
            }
            if (random.nextBoolean()) {
                y = attackerItem.getPosition().getY() + targetMinRange + random.nextInt(targetMaxRange - targetMinRange);
            } else {
                y = attackerItem.getPosition().getY() - targetMinRange - random.nextInt(targetMaxRange - targetMinRange);
            }
            if (x < itemType.getWidth() / 2 || y < itemType.getHeight() / 2) {
                continue;
            }
            if (x + itemType.getWidth() / 2 > terrainService.getTerrainSettings().getPlayFieldXSize()
                    || y + itemType.getHeight() / 2 > terrainService.getTerrainSettings().getPlayFieldYSize()) {
                continue;
            }

            Index point = new Index(x, y);
            if (!isFree(point, itemType)) {
                continue;
            }
            Rectangle itemRectangle = new Rectangle(x - itemType.getWidth() / 2,
                    y - itemType.getHeight() / 2,
                    itemType.getWidth(),
                    itemType.getHeight());

            if (itemService.hasItemsInRectangle(itemRectangle)) {
                continue;
            }
            return point;
        }
        throw new IllegalStateException("Can not find free position");
    }

    private int getDistance(List<Index> indeces) {
        Index previous = null;
        int distance = 0;
        for (Index index : indeces) {
            if (previous != null) {
                distance += previous.getDistance(index);
            }
            previous = index;
        }
        return distance;
    }

    private PassableRectangle getPassableRectangleOfAbsoluteIndex(Index absoluteIndex) {
        for (PassableRectangle passableRectangle : passableRectangles) {
            if (passableRectangle.containAbsoluteIndex(absoluteIndex)) {
                return passableRectangle;
            }
        }
        return null;
    }

    private List<Index> getShortestPath(Index absoluteStart, List<Rectangle> borders) {
        ArrayList<Index> path = new ArrayList<Index>();
        Index origin = absoluteStart;
        for (Rectangle border : borders) {
            Rectangle absBorder = terrainService.convertToAbsolutePosition(border);
            Index point = absBorder.getNearestPoint(origin);
            path.add(point);
            origin = point;
        }
        return path;
    }

    @Override
    public boolean[][] getPassableTerrain() {
        return passableTerrain;
    }

    @Override
    public void onTerrainChanged() {
        setupPassableTerrain();
    }

    @Override
    public void addCollisionServiceChangedListener(CollisionServiceChangedListener collisionServiceChangedListener) {
        collisionServiceChangedListeners.add(collisionServiceChangedListener);
        if (passableTerrain != null) {
            collisionServiceChangedListener.collisionServiceChanged();
        }
    }

    @Override
    public void removeCollisionServiceChangedListener(CollisionServiceChangedListener collisionServiceChangedListener) {
        collisionServiceChangedListeners.remove(collisionServiceChangedListener);
    }

    @Override
    public List<Index> setupPathToDestination(Index start, Index destination) {
        PassableRectangle atomStartRect = getPassableRectangleOfAbsoluteIndex(start);
        PassableRectangle atomDestRect = getPassableRectangleOfAbsoluteIndex(destination);
        if (atomStartRect == null || atomDestRect == null) {
            throw new IllegalArgumentException("Illegal atomStartRect or absoluteDestionation");
        }

        if (atomStartRect.equals(atomDestRect)) {
            ArrayList<Index> singleIndex = new ArrayList<Index>();
            singleIndex.add(destination);
            return singleIndex;
        }

        List<Path> allPaths = atomStartRect.findAllPossiblePassableRectanglePaths(atomDestRect, 1000);
        int minDistance = Integer.MAX_VALUE;
        List<Index> bestSelection = null;
        for (Path path : allPaths) {
            List<Rectangle> borders = path.getAllPassableBorders();
            List<Index> indeces = getShortestPath(start, borders);
            ArrayList<Index> totalIndeces = new ArrayList<Index>(indeces);
            totalIndeces.add(0, start);
            totalIndeces.add(destination);
            int distance = getDistance(totalIndeces);
            if (distance < minDistance) {
                minDistance = distance;
                bestSelection = indeces;
            }
        }

        if (bestSelection == null) {
            throw new IllegalArgumentException("Unable get best way");
        }
        bestSelection.add(destination);
        return bestSelection;
    }



}
