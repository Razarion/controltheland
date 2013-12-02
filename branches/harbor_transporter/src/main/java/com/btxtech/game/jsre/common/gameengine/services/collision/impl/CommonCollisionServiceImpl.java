package com.btxtech.game.jsre.common.gameengine.services.collision.impl;

import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.MathHelper;
import com.btxtech.game.jsre.common.Region;
import com.btxtech.game.jsre.common.gameengine.formation.AttackFormation;
import com.btxtech.game.jsre.common.gameengine.formation.AttackFormationFactory;
import com.btxtech.game.jsre.common.gameengine.formation.AttackFormationItem;
import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.PlanetServices;
import com.btxtech.game.jsre.common.gameengine.services.collision.CommonCollisionService;
import com.btxtech.game.jsre.common.gameengine.services.collision.Path;
import com.btxtech.game.jsre.common.gameengine.services.collision.PlaceCanNotBeFoundException;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceType;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainType;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainUtil;
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
    private Logger log = Logger.getLogger(CommonCollisionServiceImpl.class.getName());

    protected abstract PlanetServices getServices();

    @Override
    public Path setupPathToDestination(SyncBaseItem syncItem, Index destination) {
        return setupPathToDestination(syncItem.getSyncItemArea().getPosition(), destination, syncItem.getTerrainType(), syncItem.getSyncItemArea().getBoundingBox());
    }

    @Override
    public Path setupPathToDestination(Index start, Index destination, TerrainType terrainType, BoundingBox boundingBox) {
        if (start.equals(destination)) {
            Path path = new Path(start, destination, true);
            path.makeSameStartAndDestination();
            return path;
        }

        Index tileStart = TerrainUtil.getTerrainTileIndexForAbsPosition(start);
        Index tileDestination = TerrainUtil.getTerrainTileIndexForAbsPosition(destination);

        AStar aStar = AStar.findTilePath(getServices().getTerrainService().getTerrainTileField(), tileStart, tileDestination, terrainType.getSurfaceTypes());
        Path path = new Path(start, destination, aStar.isPathFound());
        FunnelAlgorithm funnelAlgorithm;
        if (aStar.isPathFound()) {
            funnelAlgorithm = new FunnelAlgorithm(start, destination);
        } else {
            Index bestFitTile = aStar.getBestFitTile();
            Index alternativeDestination = TerrainUtil.getAbsolutIndexForTerrainTileIndex(bestFitTile);
            alternativeDestination.add(Constants.TERRAIN_TILE_WIDTH / 2, Constants.TERRAIN_TILE_HEIGHT / 2);
            path.setAlternativeDestination(alternativeDestination);
            funnelAlgorithm = new FunnelAlgorithm(start, alternativeDestination);
        }

        funnelAlgorithm.setTilePath(aStar.getTilePath(), getServices().getTerrainService());
        List<Index> rawPath = funnelAlgorithm.stringPull();

        path.setPath(AngelCorrection.toItemAngel(rawPath, boundingBox));
        return path;
    }

    private void setupDestinationHintTerrain(SyncItemArea target, List<AttackFormationItem> items, TerrainType terrainType) {
        SyncItem actorItem = items.get(0).getSyncBaseItem();
        Path path = setupPathToDestination(actorItem.getSyncItemArea().getPosition(), target.getPosition(), terrainType, actorItem.getSyncItemArea().getBoundingBox());

        Collection<SyncItem> placeAbleItems = new HashSet<SyncItem>();
        for (AttackFormationItem item : items) {
            placeAbleItems.add(item.getSyncBaseItem());
        }

        AttackFormation attackFormation = AttackFormationFactory.create(target, MathHelper.HALF_RADIANT + path.getActualDestinationAngel(), items);
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
    public List<AttackFormationItem> setupDestinationHints(SyncItemArea target, List<AttackFormationItem> items) {
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
            setupDestinationHintTerrain(target, entry.getValue(), entry.getKey());
        }
        return items;
    }

    @Override
    public AttackFormationItem getDestinationHint(SyncBaseItem syncBaseItem, int range, SyncItemArea target) {
        List<AttackFormationItem> formationItems = new ArrayList<AttackFormationItem>();
        formationItems.add(new AttackFormationItem(syncBaseItem, range));
        setupDestinationHints(target, formationItems);
        return formationItems.get(0);
    }

    @Override
    public List<AttackFormationItem> setupDestinationHints(SyncItem target, List<AttackFormationItem> items) {
        return setupDestinationHints(target.getSyncItemArea(), items);
    }

    @Override
    public Path setupPathToSyncMovableRandomPositionIfTaken(SyncItem syncItem) {
        Index position = getFreeRandomPosition(syncItem.getItemType(), Rectangle.generateRectangleFromMiddlePoint(syncItem.getSyncItemArea().getPosition(), 500, 500), 0, false, false);
        return setupPathToDestination(syncItem.getSyncItemArea().getPosition(), position, syncItem.getTerrainType(), syncItem.getSyncItemArea().getBoundingBox());
    }

    @Override
    public Index getRallyPoint(SyncBaseItem factory, Collection<ItemType> ableToBuild) {
        int maxRadius = 0;
        Collection<TerrainType> types = new ArrayList<TerrainType>();

        for (ItemType itemType : ableToBuild) {
            if (itemType.getBoundingBox().getRadius() > maxRadius) {
                maxRadius = itemType.getBoundingBox().getRadius();
            }
            types.add(itemType.getTerrainType());
        }
        return getFreeRandomPosition(factory,
                maxRadius,
                TerrainType.leastCommonMultiple(types),
                factory.getItemType().getBoundingBox().getRadius(),
                MAX_RANGE_RALLY_POINT);
    }

    private Index getFreeRandomPosition(SyncBaseItem origin, int maxRadius, Collection<SurfaceType> allowedSurfaces, int targetMinRange, int targetMaxRange) {
        int delta = (targetMaxRange - targetMinRange) / STEPS_DISTANCE;
        for (int distance = 0; distance < (targetMaxRange - targetMinRange); distance += delta) {
            for (double angel = 0.0; angel < 2.0 * Math.PI; angel += (2.0 * Math.PI / STEPS_ANGEL)) {
                Index point = origin.getSyncItemArea().getPosition().getPointFromAngelToNord(angel, distance + targetMinRange);

                if (!getServices().getTerrainService().isFree(point, maxRadius, allowedSurfaces, null)) {
                    continue;
                }

                Rectangle rectangle = Rectangle.generateRectangleFromMiddlePoint(point, maxRadius, maxRadius);
                if (getServices().getItemService().hasItemsInRectangle(rectangle)) {
                    continue;
                }
                return point;
            }
        }

        throw new IllegalStateException("Can not find free position."
                + "Origin: " + origin
                + " maxRadius: " + maxRadius
                + " allowedSurfaces: " + SurfaceType.toString(allowedSurfaces)
                + " targetMinRange: " + targetMinRange
                + " targetMaxRange: " + targetMaxRange);
    }

    @Override
    @Deprecated
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
                Rectangle rectangle = Rectangle.generateRectangleFromMiddlePoint(point, itemType.getBoundingBox().getRadius(), itemType.getBoundingBox().getRadius());
                if (getServices().getItemService().hasItemsInRectangle(rectangle)) {
                    continue;
                }
            }
            return point;
        }
        throw new PlaceCanNotBeFoundException(itemType, region, itemFreeRange);
    }

    @Override
    public Index getFreeRandomPosition(ItemType itemType, Region region, int itemFreeRange, boolean botFree, boolean ignoreMovable) {
        Random random = new Random();
        for (int i = 0; i < MAX_TRIES; i++) {
            int tileIndex = random.nextInt(region.tileSize());
            Index tile = region.getIndexForRandomPosition(tileIndex);
            Index absolutePosition = TerrainUtil.getAbsolutIndexForTerrainTileIndex(tile).add(Constants.TERRAIN_TILE_WIDTH / 2,Constants.TERRAIN_TILE_HEIGHT / 2);
            if (botFree && getServices().getBotService().isInRealm(absolutePosition)) {
                continue;
            }

            if (!getServices().getTerrainService().isFree(absolutePosition, itemType)) {
                continue;
            }
            if (ignoreMovable) {
                if (getServices().getItemService().isUnmovableSyncItemOverlapping(itemType.getBoundingBox(), absolutePosition)) {
                    continue;
                }
            } else {
                Rectangle rectangle = Rectangle.generateRectangleFromMiddlePoint(absolutePosition, itemType.getBoundingBox().getRadius(), itemType.getBoundingBox().getRadius());
                if (getServices().getItemService().hasItemsInRectangle(rectangle)) {
                    continue;
                }
            }
            return absolutePosition;
        }
        throw new PlaceCanNotBeFoundException(itemType, region, itemFreeRange);
    }
}
