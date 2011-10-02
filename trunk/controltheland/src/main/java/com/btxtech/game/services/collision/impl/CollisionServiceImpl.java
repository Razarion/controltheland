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
import com.btxtech.game.jsre.common.Territory;
import com.btxtech.game.jsre.common.gameengine.formation.AttackFormation;
import com.btxtech.game.jsre.common.gameengine.formation.AttackFormationFactory;
import com.btxtech.game.jsre.common.gameengine.formation.AttackFormationItem;
import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceType;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainSettings;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItemArea;
import com.btxtech.game.jsre.mapview.common.GeometricalUtil;
import com.btxtech.game.services.bot.BotService;
import com.btxtech.game.services.collision.CollisionService;
import com.btxtech.game.services.collision.CollisionServiceChangedListener;
import com.btxtech.game.services.collision.PassableRectangle;
import com.btxtech.game.services.collision.Path;
import com.btxtech.game.services.collision.PathCanNotBeFoundException;
import com.btxtech.game.services.collision.Port;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.mgmt.MgmtService;
import com.btxtech.game.services.terrain.TerrainService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * User: beat
 * Date: May 24, 2009
 * Time: 6:44:01 PM
 */
@Component
public class CollisionServiceImpl implements CollisionService, TerrainListener {
    private static final int MAX_RANGE_RALLY_POINT = 300;
    private static final int STEPS_ANGEL = 30;
    private static final int STEPS_DISTANCE = 50;

    @Autowired
    private TerrainService terrainService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private MgmtService mgmtService;
    @Autowired
    private BotService botService;
    private HashMap<TerrainType, List<PassableRectangle>> passableRectangles4TerrainType = new HashMap<TerrainType, List<PassableRectangle>>();
    private Log log = LogFactory.getLog(CollisionServiceImpl.class);
    private ArrayList<CollisionServiceChangedListener> collisionServiceChangedListeners = new ArrayList<CollisionServiceChangedListener>();

    @PostConstruct
    public void init() {
        if (mgmtService.isNoGameEngine()) {
            return;
        }
        try {
            terrainService.addTerrainListener(this);
            setupPassableTerrain();
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    private void setupPassableTerrain() {
        log.info("Starting setup collision service");
        long time = System.currentTimeMillis();
        SurfaceType[][] surfaceTypeField = getSurfaceTypeField();
        setupPassableRectangles(surfaceTypeField);
        log.info("Time needed to start up collision service: " + (System.currentTimeMillis() - time) + "ms");
    }

    private SurfaceType[][] getSurfaceTypeField() {
        TerrainSettings terrainSettings = terrainService.getTerrainSettings();
        if (terrainSettings == null) {
            log.error("No terrain settings for real game available");
            return new SurfaceType[0][0];
        }
        SurfaceType[][] surfaceTypeFiled = new SurfaceType[terrainSettings.getTileXCount()][terrainSettings.getTileYCount()];
        for (int x = 0; x < terrainSettings.getTileXCount(); x++) {
            for (int y = 0; y < terrainSettings.getTileYCount(); y++) {
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
            ArrayList<Rectangle> mapAsRectangles = GeometricalUtil.separateIntoRectangles(entry.getValue());
            List<PassableRectangle> passableRectangles = PathFinderUtilities.buildPassableRectangleList(mapAsRectangles, terrainService);
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

    @Override
    public Map<TerrainType, List<PassableRectangle>> getPassableRectangles() {
        return passableRectangles4TerrainType;
    }

    @Override
    public Index getFreeRandomPosition(ItemType itemType, Rectangle region, int itemFreeRange, boolean botFree) {
        Random random = new Random();
        for (int i = 0; i < MAX_TRIES; i++) {
            int x = random.nextInt(region.getWidth()) + region.getX();
            int y = random.nextInt(region.getHeight()) + region.getY();
            Index point = new Index(x, y);
            if (botFree && botService.isInRealm(point)) {
                continue;
            }

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
    public Index getFreeRandomPosition(ItemType itemType, Territory territory, int itemFreeRange, boolean botFree) {
        if (!territory.isItemAllowed(itemType.getId())) {
            throw new IllegalArgumentException("Item Type '" + itemType + "' not allowed on territory: " + territory);
        }

        Random random = new Random();
        List<Rectangle> territoryRectangles = new ArrayList<Rectangle>(territory.getTerritoryTileRegions());

        for (int i = 0; i < MAX_TRIES; i++) {
            int territoryRectIndex = random.nextInt(territoryRectangles.size());
            Rectangle tileRectangle = territoryRectangles.get(territoryRectIndex);
            Rectangle absoluteRectangle = terrainService.convertToAbsolutePosition(tileRectangle);
            int x = random.nextInt(absoluteRectangle.getWidth()) + absoluteRectangle.getX();
            int y = random.nextInt(absoluteRectangle.getHeight()) + absoluteRectangle.getY();
            Index point = new Index(x, y);
            if (botFree && botService.isInRealm(point)) {
                continue;
            }

            if (!terrainService.isFree(point, itemType)) {
                continue;
            }
            Index start = point.sub(new Index(itemFreeRange / 2, itemFreeRange / 2));
            Rectangle rectangle = new Rectangle(start.getX(), start.getY(), itemFreeRange, itemFreeRange);
            if (itemService.hasStandingItemsInRect(rectangle, null)) {
                continue;
            }
            return point;
        }
        throw new IllegalStateException("Can not find free position. itemType: " + itemType + " territory: " + territory + " itemFreeRange: " + itemFreeRange);
    }

    @Override
    public Index getRallyPoint(SyncBaseItem factory, Collection<SurfaceType> allowedSurfaces) {
        return getFreeRandomPosition(factory.getSyncItemArea().getPosition(),
                0,
                0,
                allowedSurfaces,
                factory.getItemType().getBoundingBox().getMaxRadius(),
                MAX_RANGE_RALLY_POINT);
    }

    private Index getFreeRandomPosition(Index origin, int itemFreeWidth, int itemFreeHeight, Collection<SurfaceType> allowedSurfaces, int targetMinRange, int targetMaxRange) {
        int delta = (targetMaxRange - targetMinRange) / STEPS_DISTANCE;
        for (int distance = 0; distance < (targetMaxRange - targetMinRange); distance += delta) {
            for (double angel = 0.0; angel < 2.0 * Math.PI; angel += (2.0 * Math.PI / STEPS_ANGEL)) {
                Index point = origin.getPointFromAngelToNord(angel, distance + targetMinRange);

                if (point.getX() >= terrainService.getTerrainSettings().getPlayFieldXSize()) {
                    continue;
                }
                if (point.getY() >= terrainService.getTerrainSettings().getPlayFieldYSize()) {
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
        }


        throw new IllegalStateException("Can not find free position."
                + "Origin: " + origin
                + " itemFreeWidth: " + itemFreeWidth
                + " itemFreeHeight: " + itemFreeHeight
                + " allowedSurfaces: " + SurfaceType.toString(allowedSurfaces)
                + " targetMinRange: " + targetMinRange
                + " targetMaxRange: " + targetMaxRange);
    }

    @Override
    public Index getFreeSyncMovableRandomPositionIfTaken(SyncItem syncItem, int targetMaxRange) {
        ItemType itemType = syncItem.getItemType();
        Index origin = syncItem.getSyncItemArea().getPosition();
        BoundingBox boundingBox = itemType.getBoundingBox();
        int targetMinRange = boundingBox.getMaxRadius();
        int delta = (targetMaxRange - targetMinRange) / STEPS_DISTANCE;
        for (int distance = 0; distance < (targetMaxRange - targetMinRange); distance += delta) {
            for (double angel = 0.0; angel < 2.0 * Math.PI; angel += (2.0 * Math.PI / STEPS_ANGEL)) {
                Index point = origin.getPointFromAngelToNord(angel, distance + targetMinRange);

                if (point.getX() >= terrainService.getTerrainSettings().getPlayFieldXSize()) {
                    continue;
                }
                if (point.getY() >= terrainService.getTerrainSettings().getPlayFieldYSize()) {
                    continue;
                }
                if (!terrainService.isFree(point, boundingBox.getMaxDiameter(), boundingBox.getMaxDiameter(), itemType.getTerrainType().getSurfaceTypes())) {
                    continue;
                }
                if (itemService.isSyncItemOverlapping(syncItem, point, null)) {
                    continue;

                }
                return point;
            }
        }

        log.error("getFreeSyncMovableRandomPositionIfTaken: Can not find free position. "
                + syncItem
                + " Origin: " + origin
                + " targetMinRange: " + targetMinRange
                + " targetMaxRange: " + targetMaxRange);
        return null;
    }


    private PassableRectangle getPassableRectangleOfAbsoluteIndex(Index absoluteIndex, TerrainType terrainType) {
        return PathFinderUtilities.getPassableRectangleOfAbsoluteIndex(absoluteIndex, terrainType, passableRectangles4TerrainType, terrainService);
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
            if (atomStartRect == null) {
                throw new IllegalArgumentException("Illegal atomStartRect. start: " + start + " destination: " + destination + " terrainType: " + terrainType);
            } else {
                throw new IllegalArgumentException("Illegal atomDestRect. start: " + start + " destination: " + destination + " terrainType: " + terrainType);
            }
        }

        if (atomStartRect.equals(atomDestRect)) {
            ArrayList<Index> singleIndex = new ArrayList<Index>();
            singleIndex.add(destination);
            return singleIndex;
        }

        long time = System.currentTimeMillis();
        List<Index> positions = null;
        try {
            Path path = atomStartRect.findPossiblePassableRectanglePaths(start, atomDestRect, destination);
            path = PathFinderUtilities.optimizePath(path);
            List<Port> ports = path.getAllPassableBorders();
            GumPath gumPath = new GumPath(start, destination, ports);
            gumPath.calculateShortestPath();
            positions = gumPath.getPath();
            return positions;
        } catch (PathCanNotBeFoundException e) {
            log.fatal("PathCanNotBeFoundException: " + e.getMessage());
            throw e;
        } finally {
            if (System.currentTimeMillis() - time > 200 || positions == null) {
                log.fatal("Pathfinding took: " + (System.currentTimeMillis() - time) + "ms start: " + start + " destination: " + destination);
            }
        }
    }

    private List<Index> setupPathToDifferentTerrainTypeDestination(Index start, TerrainType startTerrainType, Index destination, TerrainType destinationTerrainType) {
        PassableRectangle atomStartRect = getPassableRectangleOfAbsoluteIndex(start, startTerrainType);
        PassableRectangle atomDestRect = PathFinderUtilities.getNearestPassableRectangleDifferentTerrainTypeOfAbsoluteIndex(destination, destinationTerrainType, startTerrainType, passableRectangles4TerrainType, terrainService);
        if (atomStartRect == null || atomDestRect == null) {
            throw new IllegalArgumentException("Illegal atomStartRect or atomDestRect. start: " + start + " destination: " + destination + " terrainType: " + destinationTerrainType);
        }

        if (atomStartRect.equals(atomDestRect)) {
            ArrayList<Index> singleIndex = new ArrayList<Index>();
            singleIndex.add(destination);
            return singleIndex;
        }

        long time = System.currentTimeMillis();
        List<Index> positions = null;
        try {
            Path path = atomStartRect.findPossiblePassableRectanglePaths(start, atomDestRect, destination);
            path = PathFinderUtilities.optimizePath(path);
            List<Port> ports = path.getAllPassableBorders();
            GumPath gumPath = new GumPath(start, destination, ports);
            gumPath.calculateShortestPath();
            positions = gumPath.getPath();
            return positions;
        } finally {
            if (System.currentTimeMillis() - time > 200 || positions == null) {
                log.fatal("Pathfinding took: " + (System.currentTimeMillis() - time) + "ms start: " + start + " destination: " + destination);
            }
        }
    }

    @Override
    public AttackFormationItem getDestinationHint(SyncBaseItem syncBaseItem, int range, SyncItemArea target, TerrainType targetTerrainType) {
        List<AttackFormationItem> formationItems = new ArrayList<AttackFormationItem>();
        formationItems.add(new AttackFormationItem(syncBaseItem, range));
        setupDestinationHints(target, targetTerrainType, formationItems);
        if (formationItems.get(0).isInRange()) {
            return formationItems.get(0);
        } else {
            return null;
        }
    }

    @Override
    public List<AttackFormationItem> setupDestinationHints(SyncItem target, List<AttackFormationItem> items) {
        return setupDestinationHints(target.getSyncItemArea(), target.getTerrainType(), items);
    }

    private List<AttackFormationItem> setupDestinationHints(SyncItemArea target, TerrainType targetTerrainType, List<AttackFormationItem> items) {
        Map<TerrainType, List<AttackFormationItem>> terrainTypeCollectionMap = new HashMap<TerrainType, List<AttackFormationItem>>();
        for (AttackFormationItem item : items) {
            TerrainType terrainType = item.getSyncBaseItem().getTerrainType();
            List<AttackFormationItem> attackFormationItems = terrainTypeCollectionMap.get(terrainType);
            if (attackFormationItems == null) {
                attackFormationItems = new ArrayList<AttackFormationItem>();
                terrainTypeCollectionMap.put(terrainType, attackFormationItems);
            }
            attackFormationItems.add(item);
        }

        for (Map.Entry<TerrainType, List<AttackFormationItem>> entry : terrainTypeCollectionMap.entrySet()) {
            setupDestinationHintTerrain(target, entry.getValue(), entry.getKey(), targetTerrainType);
        }
        return items;
    }

    private void setupDestinationHintTerrain(SyncItemArea target, List<AttackFormationItem> items, TerrainType terrainType, TerrainType targetTerrainType) {
        SyncItem actorItem = items.get(0).getSyncBaseItem();
        List<Index> path;
        if (terrainType == targetTerrainType) {
            path = setupPathToDestination(actorItem.getSyncItemArea().getPosition(), target.getPosition(), terrainType);
        } else {
            path = setupPathToDifferentTerrainTypeDestination(actorItem.getSyncItemArea().getPosition(), terrainType, target.getPosition(), targetTerrainType);
        }

        path.remove(path.size() - 1); // Target pos
        Index lastPoint;
        if (path.isEmpty()) {
            // Start and destination are in the same passable rectangle
            lastPoint = actorItem.getSyncItemArea().getPosition();
        } else {
            lastPoint = path.get(path.size() - 1);
        }

        Collection<SyncItem> placeAbleItems = new HashSet<SyncItem>();
        for (AttackFormationItem item : items) {
            placeAbleItems.add(item.getSyncBaseItem());
        }

        double angel = target.getPosition().getAngleToNord(lastPoint);
        AttackFormation attackFormation = AttackFormationFactory.create(target, angel, items);
        while (attackFormation.hasNext()) {
            AttackFormationItem attackFormationItem = attackFormation.calculateNextEntry();
            SyncBaseItem syncBaseItem = attackFormationItem.getSyncBaseItem();
            if (!terrainService.isFree(attackFormationItem.getDestinationHint(), syncBaseItem.getItemType())) {
                continue;
            }
            if (itemService.isSyncItemOverlapping(syncBaseItem, attackFormationItem.getDestinationHint(), placeAbleItems)) {
                continue;
            }
            attackFormation.lastAccepted();
        }
    }
}
