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
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceType;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.mapview.common.GeometricalUtil;
import com.btxtech.game.services.collision.CollisionService;
import com.btxtech.game.services.collision.CollisionServiceChangedListener;
import com.btxtech.game.services.collision.PassableRectangle;
import com.btxtech.game.services.collision.Path;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.mgmt.MgmtService;
import com.btxtech.game.services.terrain.DbTerrainSetting;
import com.btxtech.game.services.terrain.TerrainService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    @Autowired
    private MgmtService mgmtService;
    private HashMap<TerrainType, List<PassableRectangle>> passableRectangles4TerrainType = new HashMap<TerrainType, List<PassableRectangle>>();
    private Log log = LogFactory.getLog(CollisionServiceImpl.class);
    private ArrayList<CollisionServiceChangedListener> collisionServiceChangedListeners = new ArrayList<CollisionServiceChangedListener>();

    @PostConstruct
    public void init() {
        terrainService.addTerrainListener(this);
        setupPassableTerrain();
    }

    private void setupPassableTerrain() {
        log.info("Starting setup collision service");
        long time = System.currentTimeMillis();
        SurfaceType[][] surfaceTypeField = getSurfaceTypeField();
        setupPassableRectangles(surfaceTypeField);
        log.info("Time needed to start up collision service: " + (System.currentTimeMillis() - time) + "ms");
    }

    private SurfaceType[][] getSurfaceTypeField() {
        DbTerrainSetting dbTerrainSetting = terrainService.getDbTerrainSettings();
        SurfaceType[][] surfaceTypeFiled = new SurfaceType[dbTerrainSetting.getTileXCount()][dbTerrainSetting.getTileYCount()];
        for (int x = 0; x < dbTerrainSetting.getTileXCount(); x++) {
            for (int y = 0; y < dbTerrainSetting.getTileYCount(); y++) {
                surfaceTypeFiled[x][y] = terrainService.getSurfaceType(new Index(x, y));
            }
        }
        return surfaceTypeFiled;
    }

    private void setupPassableRectangles(SurfaceType[][] surfaceTypeField) {
        Map<TerrainType, Collection<Index>> tiles = separateIntoTerrainTypeTiles(surfaceTypeField);
        if (tiles.isEmpty()) {
            log.error("Terrain does not have any tiles");
            return;
        }
        for (Map.Entry<TerrainType, Collection<Index>> entry : tiles.entrySet()) {
            ArrayList<Rectangle> mapAsRectangles = GeometricalUtil.separateIntoRectangles(entry.getValue(), terrainService.getTerrainSettings());
            List<PassableRectangle> passableRectangles = buildPassableRectangleList(mapAsRectangles);
            passableRectangles4TerrainType.put(entry.getKey(), passableRectangles);
        }

        for (CollisionServiceChangedListener collisionServiceChangedListener : collisionServiceChangedListeners) {
            collisionServiceChangedListener.collisionServiceChanged();
        }
    }

    private Map<TerrainType, Collection<Index>> separateIntoTerrainTypeTiles(SurfaceType[][] surfaceTypeField) {
        Map<TerrainType, Collection<Index>> map = new HashMap<TerrainType, Collection<Index>>();
        for (int x = 0; x < surfaceTypeField.length; x++) {
            for (int y = 0; y < surfaceTypeField[x].length; y++) {
                SurfaceType surfaceType = surfaceTypeField[x][y];
                Collection<TerrainType> terrainTypes = TerrainType.getAllowedTerrainType(surfaceType);
                for (TerrainType terrainType : terrainTypes) {
                    Collection<Index> tiles = map.get(terrainType);
                    if (tiles == null) {
                        tiles = new ArrayList<Index>();
                        map.put(terrainType, tiles);
                    }
                    tiles.add(new Index(x, y));
                }
            }
        }
        return map;
    }

    private List<PassableRectangle> buildPassableRectangleList(ArrayList<Rectangle> rectangles) {
        List<PassableRectangle> passableRectangles = new ArrayList<PassableRectangle>();

        for (Rectangle rectangle : rectangles) {
            PassableRectangle passableRectangle = new PassableRectangle(rectangle, terrainService);
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

        return passableRectangles;
    }

    @Override
    public Map<TerrainType, List<PassableRectangle>> getPassableRectangles() {
        return passableRectangles4TerrainType;
    }

    @Override
    public Index getFreeRandomPosition(ItemType itemType, Rectangle region, int itemFreeRange) {
        Random random = new Random();
        for (int i = 0; i < MAX_TRIES; i++) {
            int x = random.nextInt(region.getWidth()) + region.getX();
            int y = random.nextInt(region.getHeight()) + region.getY();
            Index point = new Index(x, y);
            if (!terrainService.isFree(point, itemType)) {
                continue;
            }
            Index start = point.sub(new Index(itemFreeRange / 2, itemFreeRange / 2));
            Rectangle rectangle = new Rectangle(start.getX(), start.getY(), itemFreeRange, itemFreeRange);
            if (itemService.hasItemsInRectangle(rectangle)) {
                continue;
            }
            return point;
        }
        throw new IllegalStateException("Can not find free position. itemType: " + itemType + " region: " + region + " itemFreeRange: " + itemFreeRange);
    }

    @Override
    public Index getRallyPoint(SyncBaseItem factory, Collection<SurfaceType> allowedSurfaces) {
        return getFreeRandomPosition(factory.getPosition(), 0, 0, allowedSurfaces, factory.getItemType().getHeight() / 2, factory.getItemType().getHeight());
    }

    @Override
    public Index getFreeRandomPosition(ItemType itemType, SyncItem origin, int targetMinRange, int targetMaxRange) {
        return getFreeRandomPosition(origin.getPosition(), itemType.getWidth(), itemType.getHeight(), itemType.getTerrainType().getSurfaceTypes(), targetMinRange, targetMaxRange);
    }

    @Override
    public Index getFreeRandomPosition(Index origin, int itemFreeWidth, int itemFreeHeight, Collection<SurfaceType> allowedSurfaces, int targetMinRange, int targetMaxRange) {
        Random random = new Random();
        for (int i = 0; i < MAX_TRIES; i++) {
            double angel = random.nextDouble() * 2.0 * Math.PI;
            int discance = targetMinRange + random.nextInt(targetMaxRange - targetMinRange);
            Index point = origin.getPointFromAngelToNord(angel, discance);

            if (point.getX() >= terrainService.getDbTerrainSettings().getPlayFieldXSize()) {
                continue;
            }
            if (point.getY() >= terrainService.getDbTerrainSettings().getPlayFieldYSize()) {
                continue;
            }

            if (!terrainService.isFree(point, itemFreeWidth, itemFreeHeight, allowedSurfaces)) {
                continue;
            }
            Rectangle itemRectangle = null;
            if (itemFreeWidth > 0 || itemFreeHeight > 0) {
                itemRectangle = new Rectangle(point.getX() - itemFreeWidth / 2,
                        point.getY() - itemFreeHeight / 2,
                        itemFreeWidth,
                        itemFreeHeight);

            }
            if (itemRectangle != null && itemService.hasItemsInRectangle(itemRectangle)) {
                continue;
            }
            return point;
        }
        throw new IllegalStateException("Can not find free position."
                + "Origin: " + origin
                + " itemFreeWidth: " + itemFreeWidth
                + " itemFreeHeight: " + itemFreeHeight
                + " allowedSurfaces: " + SurfaceType.toString(allowedSurfaces)
                + " targetMinRange: " + targetMinRange
                + " targetMaxRange: " + targetMaxRange);
    }


    private PassableRectangle getPassableRectangleOfAbsoluteIndex(Index absoluteIndex, TerrainType terrainType) {
        List<PassableRectangle> passableRectangles = passableRectangles4TerrainType.get(terrainType);
        if (passableRectangles == null) {
            return null;
        }
        for (PassableRectangle passableRectangle : passableRectangles) {
            if (passableRectangle.containAbsoluteIndex(absoluteIndex, terrainService.getDbTerrainSettings())) {
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
    public void onTerrainChanged() {
        setupPassableTerrain();
    }

    @Override
    public void addCollisionServiceChangedListener(CollisionServiceChangedListener collisionServiceChangedListener) {
        collisionServiceChangedListeners.add(collisionServiceChangedListener);
        if (!passableRectangles4TerrainType.isEmpty()) {
            collisionServiceChangedListener.collisionServiceChanged();
        }
    }

    @Override
    public void removeCollisionServiceChangedListener(CollisionServiceChangedListener collisionServiceChangedListener) {
        collisionServiceChangedListeners.remove(collisionServiceChangedListener);
    }

    @Override
    public List<Index> setupPathToDestination(Index start, Index destination, TerrainType terrainType) {
        PassableRectangle atomStartRect = getPassableRectangleOfAbsoluteIndex(start, terrainType);
        PassableRectangle atomDestRect = getPassableRectangleOfAbsoluteIndex(destination, terrainType);
        if (atomStartRect == null || atomDestRect == null) {
            throw new IllegalArgumentException("Illegal atomStartRect or atomDestRect. start: " + start + " destination: " + destination + " terrainType: " + terrainType);
        }

        if (atomStartRect.equals(atomDestRect)) {
            ArrayList<Index> singleIndex = new ArrayList<Index>();
            singleIndex.add(destination);
            return singleIndex;
        }

        long time = System.currentTimeMillis();
        List<Index> positions = null;
        try {
            Path path = atomStartRect.findAllPossiblePassableRectanglePaths(atomDestRect, destination);
            List<Rectangle> borders = path.getAllPassableBorders();
            positions = getShortestPath(start, borders);
            positions.add(0, start);
            positions.add(destination);
            return positions;
        } finally {
            if (System.currentTimeMillis() - time > 200 || positions == null) {
                log.fatal("Pathfinding took: " + (System.currentTimeMillis() - time) + "ms start: " + start + " destination: " + destination);
            }
        }
    }
}
