package com.btxtech.game.jsre.common.gameengine.services.collision.impl;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.GeometricalUtil;
import com.btxtech.game.jsre.common.gameengine.formation.AttackFormation;
import com.btxtech.game.jsre.common.gameengine.formation.AttackFormationFactory;
import com.btxtech.game.jsre.common.gameengine.formation.AttackFormationItem;
import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.Services;
import com.btxtech.game.jsre.common.gameengine.services.collision.CommonCollisionService;
import com.btxtech.game.jsre.common.gameengine.services.collision.PassableRectangle;
import com.btxtech.game.jsre.common.gameengine.services.collision.Path;
import com.btxtech.game.jsre.common.gameengine.services.collision.PathCanNotBeFoundException;
import com.btxtech.game.jsre.common.gameengine.services.collision.Port;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceType;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItemArea;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 05.10.2011
 * Time: 00:10:11
 */
public abstract class CommonCollisionServiceImpl implements CommonCollisionService {
    protected static final int STEPS_ANGEL = 30;
    protected static final int STEPS_DISTANCE = 50;
    protected static final int MAX_TRIES = 1000000;
    private static final int MAX_RANGE_RALLY_POINT = 300;
    private Map<TerrainType, List<PassableRectangle>> passableRectangles4TerrainType = new HashMap<TerrainType, List<PassableRectangle>>();
    private static Logger log = Logger.getLogger(CommonCollisionServiceImpl.class.getName());

    protected abstract Services getServices();

    protected void setupPassableTerrain() {
        log.info("Starting setup collision service");
        long time = System.currentTimeMillis();
        SurfaceType[][] surfaceTypeField = getServices().getTerrainService().createSurfaceTypeField();
        log.info("Collision service flatten to field: " + (System.currentTimeMillis() - time));
        setupPassableRectangles(surfaceTypeField);
        log.info("Time needed to start up collision service: " + (System.currentTimeMillis() - time) + "ms");
    }

    private void setupPassableRectangles(SurfaceType[][] surfaceTypeField) {
        passableRectangles4TerrainType.clear();
        Map<TerrainType, Collection<Index>> tiles = separateIntoTerrainTypeTiles(surfaceTypeField);
        if (tiles.isEmpty()) {
            log.log(Level.SEVERE, "Terrain does not have any tiles");
            return;
        }
        for (Map.Entry<TerrainType, Collection<Index>> entry : tiles.entrySet()) {
            ArrayList<Rectangle> mapAsRectangles = GeometricalUtil.separateIntoRectangles(entry.getValue());
            List<PassableRectangle> passableRectangles = PathFinderUtilities.buildPassableRectangleList(mapAsRectangles, getServices().getTerrainService());
            passableRectangles4TerrainType.put(entry.getKey(), passableRectangles);
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
    public List<Index> setupPathToDestination(SyncBaseItem syncItem, Index destination) {
        return setupPathToDestination(syncItem.getSyncItemArea().getPosition(), destination, syncItem.getTerrainType());
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
            Path path = atomStartRect.findPossiblePassableRectanglePaths(getServices().getTerrainService(), start, atomDestRect, destination);
            path = PathFinderUtilities.optimizePath(path);
            List<Port> ports = path.getAllPassableBorders();
            GumPath gumPath = new GumPath(start, destination, ports);
            gumPath.calculateShortestPath();
            positions = gumPath.getPath();
            return positions;
        } catch (PathCanNotBeFoundException e) {
            log.severe("PathCanNotBeFoundException: " + e.getMessage());
            throw e;
        } finally {
            if (System.currentTimeMillis() - time > 200 || positions == null) {
                log.severe("Pathfinding took: " + (System.currentTimeMillis() - time) + "ms start: " + start + " destination: " + destination);
            }
        }
    }

    private List<Index> setupPathToDifferentTerrainTypeDestination(Index start, TerrainType startTerrainType, Index destination, TerrainType destinationTerrainType) {
        PassableRectangle atomStartRect = getPassableRectangleOfAbsoluteIndex(start, startTerrainType);
        PassableRectangle atomDestRect = PathFinderUtilities.getNearestPassableRectangleDifferentTerrainTypeOfAbsoluteIndex(destination, destinationTerrainType, startTerrainType, passableRectangles4TerrainType, getServices().getTerrainService());
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
            Path path = atomStartRect.findPossiblePassableRectanglePaths(getServices().getTerrainService(), start, atomDestRect, destination);
            path = PathFinderUtilities.optimizePath(path);
            List<Port> ports = path.getAllPassableBorders();
            GumPath gumPath = new GumPath(start, destination, ports);
            gumPath.calculateShortestPath();
            positions = gumPath.getPath();
            return positions;
        } finally {
            if (System.currentTimeMillis() - time > 200 || positions == null) {
                log.log(Level.SEVERE, "Pathfinding took: " + (System.currentTimeMillis() - time) + "ms start: " + start + " destination: " + destination);
            }
        }
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
            if (!getServices().getTerrainService().isFree(attackFormationItem.getDestinationHint(), syncBaseItem.getItemType())) {
                continue;
            }
            if (getServices().getItemService().isSyncItemOverlapping(syncBaseItem, attackFormationItem.getDestinationHint(), placeAbleItems)) {
                continue;
            }
            attackFormation.lastAccepted();
        }
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

    protected PassableRectangle getPassableRectangleOfAbsoluteIndex(Index absoluteIndex, TerrainType terrainType) {
        return PathFinderUtilities.getPassableRectangleOfAbsoluteIndex(absoluteIndex, terrainType, passableRectangles4TerrainType, getServices().getTerrainService());
    }

    @Override
    public AttackFormationItem getDestinationHint(SyncBaseItem syncBaseItem, int range, SyncItemArea target, TerrainType targetTerrainType) {
        List<AttackFormationItem> formationItems = new ArrayList<AttackFormationItem>();
        formationItems.add(new AttackFormationItem(syncBaseItem, range));
        setupDestinationHints(target, targetTerrainType, formationItems);
        return formationItems.get(0);
    }

    @Override
    public List<AttackFormationItem> setupDestinationHints(SyncItem target, List<AttackFormationItem> items) {
        return setupDestinationHints(target.getSyncItemArea(), target.getTerrainType(), items);
    }

    private Index getFreeSyncMovableRandomPositionIfTaken(SyncItem syncItem, int targetMaxRange) {
        ItemType itemType = syncItem.getItemType();
        Index origin = syncItem.getSyncItemArea().getPosition();
        BoundingBox boundingBox = itemType.getBoundingBox();
        int targetMinRange = boundingBox.getMaxRadius();
        int delta = (targetMaxRange - targetMinRange) / STEPS_DISTANCE;
        for (int distance = 0; distance < (targetMaxRange - targetMinRange); distance += delta) {
            for (double angel = 0.0; angel < 2.0 * Math.PI; angel += (2.0 * Math.PI / STEPS_ANGEL)) {
                Index point = origin.getPointFromAngelToNord(angel, distance + targetMinRange);

                if (point.getX() >= getServices().getTerrainService().getTerrainSettings().getPlayFieldXSize()) {
                    continue;
                }
                if (point.getY() >= getServices().getTerrainService().getTerrainSettings().getPlayFieldYSize()) {
                    continue;
                }
                if (!getServices().getTerrainService().isFree(point, boundingBox.getMaxDiameter(), boundingBox.getMaxDiameter(), itemType.getTerrainType().getSurfaceTypes())) {
                    continue;
                }
                if (getServices().getItemService().isSyncItemOverlapping(syncItem, point, null)) {
                    continue;

                }
                return point;
            }
        }

        log.log(Level.SEVERE, "getFreeSyncMovableRandomPositionIfTaken: Can not find free position. "
                + syncItem
                + " Origin: " + origin
                + " targetMinRange: " + targetMinRange
                + " targetMaxRange: " + targetMaxRange);
        return null;
    }

    @Override
    public List<Index> setupPathToSyncMovableRandomPositionIfTaken(SyncItem syncItem) {
        Index position = getFreeSyncMovableRandomPositionIfTaken(syncItem, 500);
        if (position != null) {
            return setupPathToDestination(syncItem.getSyncItemArea().getPosition(), position, syncItem.getTerrainType());
        } else {
            return null;
        }
    }

    @Override
    public Map<TerrainType, List<PassableRectangle>> getPassableRectangles() {
        return passableRectangles4TerrainType;
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

                if (point.getX() >= getServices().getTerrainService().getTerrainSettings().getPlayFieldXSize()) {
                    continue;
                }
                if (point.getY() >= getServices().getTerrainService().getTerrainSettings().getPlayFieldYSize()) {
                    continue;
                }

                if (!getServices().getTerrainService().isFree(point, itemFreeWidth, itemFreeHeight, allowedSurfaces)) {
                    continue;
                }
                Rectangle itemRectangle = null;
                if (itemFreeWidth > 0 || itemFreeHeight > 0) {
                    itemRectangle = new Rectangle(point.getX() - itemFreeWidth / 2,
                            point.getY() - itemFreeHeight / 2,
                            itemFreeWidth,
                            itemFreeHeight);

                }
                if (itemRectangle != null && getServices().getItemService().hasItemsInRectangle(itemRectangle)) {
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
    public Index getFreeRandomPosition(ItemType itemType, Rectangle region, int itemFreeRange, boolean botFree) {
        Random random = new Random();
        for (int i = 0; i < MAX_TRIES; i++) {
            int x = random.nextInt(region.getWidth()) + region.getX();
            int y = random.nextInt(region.getHeight()) + region.getY();
            Index point = new Index(x, y);
            if (botFree && getServices().getBotService().isInRealm(point)) {
                continue;
            }

            if (!getServices().getTerrainService().isFree(point, itemType)) {
                continue;
            }
            Index start = point.sub(new Index(itemFreeRange / 2, itemFreeRange / 2));
            Rectangle rectangle = new Rectangle(start.getX(), start.getY(), itemFreeRange, itemFreeRange);
            if (getServices().getItemService().hasItemsInRectangle(rectangle)) {
                continue;
            }
            return point;
        }
        throw new IllegalStateException("Can not find free position. itemType: " + itemType + " region: " + region + " itemFreeRange: " + itemFreeRange);
    }


}
