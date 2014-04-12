package com.btxtech.game.jsre.common.gameengine.services.collision;

import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.common.DecimalPosition;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.services.collision.impl.AStar;
import com.btxtech.game.jsre.common.gameengine.services.collision.impl.BestPositionFoundException;
import com.btxtech.game.jsre.common.gameengine.services.collision.impl.NoBetterPathFoundException;
import com.btxtech.game.jsre.common.gameengine.services.terrain.Terrain;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;

import java.util.Collection;
import java.util.Collections;

/**
 * User: beat
 * Date: 21.03.13
 * Time: 02:00
 */
public class CollisionService {
    private CollisionTileContainer collisionTileContainer;

    public void init(Terrain terrain) {
        int xTiles = (int) Math.ceil(terrain.getXCount() * Constants.TERRAIN_TILE_WIDTH / Constants.COLLISION_TILE_WIDTH);
        int yTiles = (int) Math.ceil(terrain.getYCount() * Constants.TERRAIN_TILE_HEIGHT / Constants.COLLISION_TILE_HEIGHT);

        collisionTileContainer = new CollisionTileContainer(xTiles, yTiles);
    }

    public void moveItem(SyncItem syncItem, double factor) {
        if (syncItem.getState() == SyncItem.MoveState.BLOCKED) {
            try {
                findPath(syncItem, syncItem.getTargetPosition());
            } catch (NoBetterPathFoundException e) {
                syncItem.setBlocked();
            }
        } else {
            DecimalPosition positionProposal = syncItem.calculateMoveToTarget(factor);
            boolean overlapping = isOverlapping(syncItem, positionProposal);
            if (overlapping) {
                try {
                    findPath(syncItem, syncItem.getTargetPosition());
                } catch (NoBetterPathFoundException e) {
                    syncItem.setBlocked();
                }
            } else {
                cleatBlocked(syncItem);
                try {
                    syncItem.executeMoveToTarget(factor);
                    if (!syncItem.getPosition().equals(positionProposal.getPosition())) {
                        System.out.println("Error id=" + syncItem.getId() + ":" + syncItem.getPosition() + "---" + positionProposal.getPosition());
                    }
                } finally {
                    setBlocked(syncItem);
                }
            }
        }
    }

    public boolean isOverlapping(SyncItem syncItem, DecimalPosition positionProposal) {
        Collection<Index> coveringTiles = CollisionUtil.getCoveringTilesAbsolute(positionProposal.getPosition(), syncItem.getRadius());
        Collection<Index> ownBlockingTiles = syncItem.getBlockingCollisionTiles();
        for (Index coveringTile : coveringTiles) {
            if (ownBlockingTiles != null && ownBlockingTiles.contains(coveringTile)) {
                continue;
            }

            if (collisionTileContainer.isBlocked(coveringTile.getX(), coveringTile.getY())) {
                return true;
            }
        }
        return false;
    }

    public void addSyncItem(SyncItem syncItem) {
        setBlocked(syncItem);
    }

    public void cleatBlocked(SyncItem syncItem) {
        Collection<Index> blockingTiles = syncItem.getBlockingCollisionTiles();
        if (blockingTiles == null) {
            return;
        }
        collisionTileContainer.clearBlocked(blockingTiles);
        syncItem.setBlockingCollisionTiles(null);
    }

    public void setBlocked(SyncItem syncItem) {
        Collection<Index> coveringTiles = CollisionUtil.getCoveringTilesAbsolute(syncItem.getPosition(), syncItem.getRadius());
        collisionTileContainer.setBlocked(coveringTiles);
        syncItem.setBlockingCollisionTiles(coveringTiles);
    }

    public void findPath(SyncItem syncItem, Index destination) throws NoBetterPathFoundException {
        Index start = syncItem.getPosition();
        if (destination == null) {
            throw new NullPointerException("destination == null " + syncItem);
        }
        if (start.equals(destination)) {
            return;
        }

        Index tileStart = CollisionUtil.getCollisionTileIndexForAbsPosition(start);
        Index tileDestination = CollisionUtil.getCollisionTileIndexForAbsPosition(destination);

        if (tileStart.equals(tileDestination)) {
            Path path = new Path(start, destination, Collections.<Index>emptyList());
            syncItem.setTargetPosition(path);
        } else {
            collisionTileContainer.clearBlocked(syncItem.getBlockingCollisionTiles());
            try {
                AStar aStar = AStar.findTilePath(collisionTileContainer, tileStart, tileDestination, syncItem.getRadius());
                if (aStar.isPathFound()) {
                    Path path = new Path(start, CollisionUtil.getAbsoluteIndexForCollisionTileIndex(aStar.getEndTile()), CollisionUtil.toAbsolutePath(aStar.getTilePath()));
                    syncItem.setTargetPosition(path);
                } else {
                    Path path = new Path(start, CollisionUtil.getAbsoluteIndexForCollisionTileIndex(aStar.getBestFitTile()), CollisionUtil.toAbsolutePath(aStar.getTilePath()));
                    syncItem.setTargetPosition(path);
                }
            } catch (BestPositionFoundException e) {
                syncItem.stop();
            } finally {
                collisionTileContainer.setBlocked(syncItem.getBlockingCollisionTiles());
            }
        }

    }

    public CollisionTileContainer getCollisionTileContainer() {
        return collisionTileContainer;
    }

    public boolean isBlockedAbsolute(int x, int y) {
        return collisionTileContainer.isBlocked(CollisionUtil.getCollisionTileIndexForAbsXPosition(x), CollisionUtil.getCollisionTileIndexForAbsYPosition(y));
    }
}
