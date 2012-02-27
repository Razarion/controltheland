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
import com.btxtech.game.jsre.common.gameengine.services.collision.PlaceCanNotBeFoundException;
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
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 05.10.2011
 * Time: 00:10:11
 */
public abstract class CommonCollisionServiceImpl implements CommonCollisionService {
    protected static final int STEPS_ANGEL = 30;
    protected static final int STEPS_DISTANCE = 50;
    protected static final int MAX_TRIES = 10000;
    private static final int MAX_RANGE_RALLY_POINT = 300;
    private Map<TerrainType, Collection<PassableRectangle>> passableRectangles4TerrainType = new HashMap<TerrainType, Collection<PassableRectangle>>();
    private Logger log = Logger.getLogger(CommonCollisionServiceImpl.class.getName());

    protected abstract Services getServices();

    protected void setupPassableTerrain() {
        log.info("Starting setup collision service");
        long time = System.currentTimeMillis();
        Map<TerrainType, boolean[][]> terrainTypeMap = getServices().getTerrainService().createSurfaceTypeField();
        log.info("Collision service flatten to field: " + (System.currentTimeMillis() - time));
        passableRectangles4TerrainType = GeometricalUtil.setupPassableRectangle(terrainTypeMap);
        ////
        // Check field
        //for (TerrainType terrainType : terrainTypeMap.keySet()) {
        //    GeometricalUtil.checkField(terrainTypeMap.get(terrainType), passableRectangles4TerrainType.get(terrainType), getServices().getTerrainService());
        //}
        ////
        log.info("Time needed to start up collision service: " + (System.currentTimeMillis() - time) + "ms");
    }

    @Override
    public List<Index> setupPathToDestination(SyncBaseItem syncItem, Index destination) {
        return setupPathToDestination(syncItem.getSyncItemArea().getPosition(), destination, syncItem.getTerrainType(), syncItem.getSyncItemArea().getBoundingBox());
    }

    @Override
    public List<Index> setupPathToDestination(Index start, Index destination, TerrainType terrainType, BoundingBox boundingBox) {
        PassableRectangle atomStartRect = getPassableRectangleOfAbsoluteIndex(start, terrainType);
        PassableRectangle atomDestRect = getPassableRectangleOfAbsoluteIndex(destination, terrainType);
        if (atomStartRect == null || atomDestRect == null) {
            if (atomStartRect == null) {
                throw new PathCanNotBeFoundException("Illegal atomStartRect. TerrainType: " + terrainType, start, destination);
            } else {
                throw new PathCanNotBeFoundException("Illegal atomDestRect. TerrainType: " + terrainType, start, destination);
            }
        }

        if (atomStartRect.equals(atomDestRect)) {
            if (start.equals(destination)) {
                List<Index> newPath = new ArrayList<Index>();
                newPath.add(destination);
                return newPath;
            } else {
                return GumPath.toItemAngelSameAtom(start, destination, boundingBox);
            }
        }

        long time = System.currentTimeMillis();
        List<Index> positions = null;
        try {
            Path path = atomStartRect.findPossiblePassableRectanglePaths(getServices().getTerrainService(), start, atomDestRect, destination);
            path = PathFinderUtilities.optimizePath(path, getServices().getTerrainService());
            List<Port> ports = path.getAllPassableBorders(getServices().getTerrainService());
            GumPath gumPath = new GumPath(start, destination, ports, boundingBox);
            positions = gumPath.getOptimizedPath();
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

    private Index setupNearestPointOnDifferentTerrainTypeDestination(Index start, TerrainType startTerrainType, Index destination, TerrainType destinationTerrainType, BoundingBox boundingBox) {
        PassableRectangle atomStartRect = getPassableRectangleOfAbsoluteIndex(start, startTerrainType);
        PassableRectangle atomDestRect = PathFinderUtilities.getNearestPassableRectangleDifferentTerrainTypeOfAbsoluteIndex(destination, destinationTerrainType, startTerrainType, passableRectangles4TerrainType, getServices().getTerrainService());
        if (atomStartRect == null || atomDestRect == null) {
            throw new IllegalArgumentException("Illegal atomStartRect or atomDestRect. start: " + start + " destination: " + destination + " terrainType: " + destinationTerrainType);
        }

        Rectangle absRect = getServices().getTerrainService().convertToAbsolutePosition(atomDestRect.getRectangle());
        if (absRect.containsExclusive(destination)) {
            return destination;
        } else {
            return absRect.getNearestPoint(destination);
        }
    }

    private void setupDestinationHintTerrain(SyncItemArea target, List<AttackFormationItem> items, TerrainType terrainType, TerrainType targetTerrainType) {
        SyncItem actorItem = items.get(0).getSyncBaseItem();
        Index lastPoint;
        if (terrainType == targetTerrainType) {
            List<Index> path = setupPathToDestination(actorItem.getSyncItemArea().getPosition(), target.getPosition(), terrainType, actorItem.getSyncItemArea().getBoundingBox());
            path.remove(path.size() - 1); // Target pos
            if (path.isEmpty()) {
                // Start and destination are in the same passable rectangle
                lastPoint = actorItem.getSyncItemArea().getPosition();
            } else {
                lastPoint = path.get(path.size() - 1);
            }

        } else {
            lastPoint = setupNearestPointOnDifferentTerrainTypeDestination(actorItem.getSyncItemArea().getPosition(), terrainType, target.getPosition(), targetTerrainType, target.getBoundingBox());
        }

        double angel;
        if (target.getPosition().equals(lastPoint)) {
            angel = 0;
        } else {
            angel = target.getPosition().getAngleToNord(lastPoint);
        }

        Collection<SyncItem> placeAbleItems = new HashSet<SyncItem>();
        for (AttackFormationItem item : items) {
            placeAbleItems.add(item.getSyncBaseItem());
        }

        AttackFormation attackFormation = AttackFormationFactory.create(target, angel, items);
        while (attackFormation.hasNext()) {
            AttackFormationItem attackFormationItem = attackFormation.calculateNextEntry();
            SyncBaseItem syncBaseItem = attackFormationItem.getSyncBaseItem();
            if (!getServices().getTerrainService().isFree(attackFormationItem.getDestinationHint(), syncBaseItem.getItemType())) {
                continue;
            }
            if (getServices().getItemService().isSyncItemOverlapping(syncBaseItem,
                    attackFormationItem.getDestinationHint(),
                    attackFormationItem.getDestinationAngel(),
                    placeAbleItems)) {
                continue;
            }
            attackFormation.lastAccepted();
        }
    }

    @Override
    public List<AttackFormationItem> setupDestinationHints(SyncItemArea target, TerrainType targetTerrainType, List<AttackFormationItem> items) {
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

    @Override
    public List<Index> setupPathToSyncMovableRandomPositionIfTaken(SyncItem syncItem) {
        Index position = getFreeRandomPosition(syncItem.getItemType(), Rectangle.generateRectangleFromMiddlePoint(syncItem.getSyncItemArea().getPosition(), 500, 500), 0, false, false);
        return setupPathToDestination(syncItem.getSyncItemArea().getPosition(), position, syncItem.getTerrainType(), syncItem.getSyncItemArea().getBoundingBox());
    }

    @Override
    public Map<TerrainType, Collection<PassableRectangle>> getPassableRectangles() {
        return passableRectangles4TerrainType;
    }

    @Override
    public Index getRallyPoint(SyncBaseItem factory, Collection<ItemType> ableToBuild) {
        int maxWidth = 0;
        int maxHeight = 0;
        Collection<TerrainType> types = new ArrayList<TerrainType>();

        for (ItemType itemType : ableToBuild) {
            if (itemType.getBoundingBox().getWidth() > maxWidth) {
                maxWidth = itemType.getBoundingBox().getWidth();
            }
            if (itemType.getBoundingBox().getHeight() > maxHeight) {
                maxHeight = itemType.getBoundingBox().getHeight();
            }
            types.add(itemType.getTerrainType());
        }
        return getFreeRandomPosition(factory,
                maxWidth,
                maxHeight,
                TerrainType.leastCommonMultiple(types),
                factory.getItemType().getBoundingBox().getMaxRadius(),
                MAX_RANGE_RALLY_POINT);
    }

    private Index getFreeRandomPosition(SyncBaseItem origin, int itemFreeWidth, int itemFreeHeight, Collection<SurfaceType> allowedSurfaces, int targetMinRange, int targetMaxRange) {
        int delta = (targetMaxRange - targetMinRange) / STEPS_DISTANCE;
        for (int distance = 0; distance < (targetMaxRange - targetMinRange); distance += delta) {
            for (double angel = 0.0; angel < 2.0 * Math.PI; angel += (2.0 * Math.PI / STEPS_ANGEL)) {
                Index point = origin.getSyncItemArea().getPosition().getPointFromAngelToNord(angel, distance + targetMinRange);

                if (!getServices().getTerrainService().isFree(point, itemFreeWidth, itemFreeHeight, allowedSurfaces)) {
                    continue;
                }

                Rectangle rectangle = Rectangle.generateRectangleFromMiddlePoint(point, itemFreeWidth, itemFreeHeight);
                if (getServices().getItemService().hasItemsInRectangle(rectangle)) {
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
    public Index getFreeRandomPosition(ItemType itemType, Rectangle region, int itemFreeRange, boolean botFree, boolean ignoreMovable) {
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
            if (ignoreMovable) {
                if (getServices().getItemService().isUnmovableSyncItemOverlapping(itemType.getBoundingBox(), point)) {
                    continue;
                }
            } else {
                int width = itemFreeRange + itemType.getBoundingBox().getWidth();
                int height = itemFreeRange + itemType.getBoundingBox().getHeight();
                Index start = point.sub(new Index(width / 2, height / 2));
                Rectangle rectangle = new Rectangle(start.getX(), start.getY(), width, height);
                if (getServices().getItemService().hasItemsInRectangle(rectangle)) {
                    continue;
                }
            }
            return point;
        }
        throw new PlaceCanNotBeFoundException(itemType, region, itemFreeRange);
    }


}
