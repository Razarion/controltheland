package com.btxtech.game.jsre.common.gameengine.services.collision;

import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.common.DecimalPosition;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.MathHelper;
import com.btxtech.game.jsre.common.gameengine.services.terrain.Terrain;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import model.MovingModel;

import java.util.PriorityQueue;

/**
 * User: beat
 * Date: 21.03.13
 * Time: 02:00
 */
public class CollisionService {
    public static final double CRUSH_ZONE = 10;
    private final MovingModel movingModel;
    private CollisionTileContainer collisionTileContainer;

    public CollisionService(MovingModel movingModel) {
        this.movingModel = movingModel;
    }

    public void init(Terrain terrain) {
        int xTiles = (int) Math.ceil(terrain.getXCount() * Constants.TERRAIN_TILE_WIDTH / Constants.COLLISION_TILE_WIDTH);
        int yTiles = (int) Math.ceil(terrain.getYCount() * Constants.TERRAIN_TILE_HEIGHT / Constants.COLLISION_TILE_HEIGHT);

        collisionTileContainer = new CollisionTileContainer(xTiles, yTiles);
    }

    public void moveItem(SyncItem syncItem, double factor) {
        DecimalPosition positionProposal = syncItem.calculateMoveToTarget(factor);
        Overlapping overlapping = isOverlapping(syncItem, positionProposal, 0);
        if (overlapping != null) {
            if (isBetterPositionAvailable(syncItem, overlapping)) {
                double movingAngel = syncItem.getAngel();
                double otherAngel = syncItem.getDecimalPosition().getAngleToNord(overlapping.getSyncItem().getDecimalPosition());
                double crashAngelAbs = MathHelper.getAngel(movingAngel, otherAngel);
                if (crashAngelAbs <= MathHelper.QUARTER_RADIANT) {
                    if (MathHelper.isCounterClock(movingAngel, otherAngel)) {
                        syncItem.setTargetAngel(MathHelper.normaliseAngel(syncItem.getAngel() - MathHelper.ONE_RADIANT / 24.0));
                    } else {
                        syncItem.setTargetAngel(MathHelper.normaliseAngel(syncItem.getAngel() + MathHelper.ONE_RADIANT / 24.0));
                    }
                } else {
                    syncItem.setSpeed(SyncItem.SPEED);
                    syncItem.executeMoveToTarget(factor);
                }
            } else {
                syncItem.stop();
            }
        } else {
            syncItem.setSpeed(SyncItem.SPEED);
            syncItem.executeMoveToTarget(factor);
            if (syncItem.getState() == SyncItem.MoveState.MOVING) {
                if (isOverlapping(syncItem, syncItem.getDecimalPosition(), CRUSH_ZONE) == null) {
                    double targetAngel = syncItem.getDecimalPosition().getAngleToNord(new DecimalPosition(syncItem.getTargetPosition()));
                    if (!syncItem.angelReached(targetAngel)) {
                        if (MathHelper.getAngel(syncItem.getAngel(), targetAngel) > MathHelper.ONE_RADIANT / 24.0) {
                            if (MathHelper.isCounterClock(targetAngel, syncItem.getAngel())) {
                                syncItem.setTargetAngel(MathHelper.normaliseAngel(syncItem.getAngel() - MathHelper.ONE_RADIANT / 24.0));
                            } else {
                                syncItem.setTargetAngel(MathHelper.normaliseAngel(syncItem.getAngel() + MathHelper.ONE_RADIANT / 24.0));
                            }
                        } else {
                            syncItem.setTargetAngel(targetAngel);
                        }
                    }
                }
            }
        }
    }

    private boolean isBetterPositionAvailable(SyncItem syncItem, Overlapping overlapping) {
        if (overlapping.getSyncItem().getState() == SyncItem.MoveState.STOPPED) {
            if (overlapping.getSyncItem().getPosition().getDistance(syncItem.getTargetPosition()) > overlapping.getSyncItem().getRadius()) {
                return movingModel.calculateDensityOfItems(syncItem.getPosition().getDistance(syncItem.getTargetPosition())) < 0.25;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public Overlapping isOverlapping(SyncItem syncItem, DecimalPosition positionProposal, double crushZone) {
        PriorityQueue<Overlapping> overlappings = new PriorityQueue<Overlapping>();
        for (SyncItem other : movingModel.getSyncItems()) {
            if (syncItem.equals(other)) {
                continue;
            }
            double distance = syncItem.getRadius() + other.getRadius() + crushZone - positionProposal.getDistance(other.getDecimalPosition());
            if (distance >= 0) {
                overlappings.add(new Overlapping(other, distance));
            }
        }
        if (overlappings.isEmpty()) {
            return null;
        } else {
            return overlappings.poll();
        }
    }

    public CollisionTileContainer getCollisionTileContainer() {
        return collisionTileContainer;
    }

    public boolean isBlockedAbsolute(int x, int y) {
        return collisionTileContainer.isBlocked(CollisionUtil.getCollisionTileIndexForAbsXPosition(x), CollisionUtil.getCollisionTileIndexForAbsYPosition(y));
    }

    public void findPath(SyncItem syncItem, Index targetPosition) {
        syncItem.setTargetPosition(targetPosition);
    }

    private class Overlapping implements Comparable<Overlapping> {
        private SyncItem syncItem;
        private double crushZone;
        private double distance;

        private Overlapping(SyncItem syncItem, double distance) {
            this.syncItem = syncItem;
            this.distance = distance;
            crushZone = distance - CRUSH_ZONE;
            if (crushZone < 0) {
                crushZone = 0;
            }
        }

        private SyncItem getSyncItem() {
            return syncItem;
        }

        @Override
        public int compareTo(Overlapping o) {
            return Double.compare(distance, o.distance);
        }

        public boolean isTight() {
            return crushZone < CRUSH_ZONE / 3.0;
        }

        public boolean isWide() {
            return crushZone > CRUSH_ZONE * 2.0 / 3.0;
        }

        public boolean isMiddle() {
            return !isTight() && !isWide();
        }
    }
}
