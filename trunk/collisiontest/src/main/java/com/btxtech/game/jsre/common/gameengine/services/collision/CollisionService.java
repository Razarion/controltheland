package com.btxtech.game.jsre.common.gameengine.services.collision;

import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.common.DecimalPosition;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.common.Vector;
import com.btxtech.game.jsre.common.MathHelper;
import com.btxtech.game.jsre.common.gameengine.services.terrain.Terrain;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import model.MovingModel;

/**
 * User: beat
 * Date: 21.03.13
 * Time: 02:00
 */
public class CollisionService {
    private final MovingModel movingModel;
    public static final double DENSITY_OF_ITEM = 0.2;

    public CollisionService(MovingModel movingModel) {
        this.movingModel = movingModel;
    }

    public void init(Terrain terrain) {
        int xTiles = (int) Math.ceil(terrain.getXCount() * Constants.TERRAIN_TILE_WIDTH / Constants.COLLISION_TILE_WIDTH);
        int yTiles = (int) Math.ceil(terrain.getYCount() * Constants.TERRAIN_TILE_HEIGHT / Constants.COLLISION_TILE_HEIGHT);
    }

    public void moveItem(SyncItem syncItem, double factor) {
        // DecimalPosition steering = syncItem.getSteering().add(doSeek(syncItem));
        DecimalPosition steering = collisionAvoidance(syncItem);
        steering = steering.add(doSeek(syncItem));
        steering = truncate(steering, SyncItem.MAX_FORCE);
        steering = steering.multiply(1.0 / SyncItem.MASS);

        DecimalPosition velocity = syncItem.getVelocity().add(steering);
        velocity = truncate(velocity, SyncItem.MAX_VELOCITY);

        double diff = MathHelper.negateAngel(syncItem.getAngel()) - MathHelper.negateAngel(velocity.getAngleToNorth());
        if (Math.abs(diff) < 0.1) {
            syncItem.setAngel(velocity.getAngleToNorth());
            double speedDiff = Math.abs(syncItem.getSpeed() - velocity.getLength());
            if (speedDiff < 0.01) {

            } else if (syncItem.getSpeed() > velocity.getLength()) {
                syncItem.setSpeed(syncItem.getSpeed() - 0.01);
            } else {
                syncItem.setSpeed(syncItem.getSpeed() + 0.01);
            }
            syncItem.executeMove();
        } else {
            if (MathHelper.isCounterClock(syncItem.getAngel(), velocity.getAngleToNorth())) {
                syncItem.setAngel(MathHelper.normaliseAngel(syncItem.getAngel() + 0.1));
            } else {
                syncItem.setAngel(MathHelper.normaliseAngel(syncItem.getAngel() - 0.1));
            }
        }
        if (!isBetterPositionAvailable(syncItem)) {
            syncItem.stop();
        }
    }

    private boolean isBetterPositionAvailable(SyncItem syncItem) {
        return !syncItem.getPosition().equals(syncItem.getTargetPosition().getPosition())
                && movingModel.calculateDensityOfItems(syncItem.getTargetPosition().getPosition(), syncItem.getPosition().getDistance(syncItem.getTargetPosition().getPosition())) < DENSITY_OF_ITEM;
    }


    private DecimalPosition collisionAvoidance_ORIG_1(SyncItem syncItem) {
        DecimalPosition tv = syncItem.getVelocity();
        tv = tv.normalize();
        tv = tv.multiply(SyncItem.MAX_AVOID_AHEAD * syncItem.getVelocity().getLength() / SyncItem.MAX_VELOCITY);
        DecimalPosition ahead = syncItem.getDecimalPosition().add(tv);

        SyncItem mostThreatening = null;
        for (SyncItem other : movingModel.getSyncItems()) {
            if (other == syncItem) {
                continue;
            }
            boolean collision = lineIntersecsCircle(syncItem, other, ahead);

            if (collision && (mostThreatening == null || syncItem.getDecimalPosition().getDistance(other.getDecimalPosition()) < syncItem.getDecimalPosition().getDistance(mostThreatening.getDecimalPosition()))) {
                mostThreatening = other;
            }
        }

        if (mostThreatening != null) {
            DecimalPosition avoidance = ahead.sub(mostThreatening.getDecimalPosition());
            avoidance = avoidance.normalize();
            avoidance = avoidance.multiply(SyncItem.AVOID_FORCE);
            double diff = MathHelper.negateAngel(avoidance.getAngleToNorth()) + MathHelper.negateAngel(syncItem.getVelocity().getAngleToNorth());
            if (Math.abs(diff) < 0.1) {
                avoidance = Vector.NULL_POSITION.rotateCounterClock(avoidance, 0.1);
            }
            return avoidance;
        } else {
            return Vector.NULL_POSITION;
        }
    }

    private DecimalPosition collisionAvoidance_ORIG_2(SyncItem syncItem) {
        // Find items ahead
        double angel = syncItem.getAngel();
        double minDistance = Double.MAX_VALUE;
        SyncItem mostThreatening = null;
        Rectangle rectangleAhead = new Rectangle(syncItem.getPosition().getX() - syncItem.getRadius(),
                syncItem.getPosition().getY() - syncItem.getRadius() - (int) SyncItem.MAX_AVOID_AHEAD,
                syncItem.getRadius() * 2,
                (int) SyncItem.MAX_AVOID_AHEAD);

        for (SyncItem other : movingModel.getSyncItems()) {
            if (other == syncItem) {
                continue;
            }
            double distance = syncItem.getDecimalPosition().getDistance(other.getDecimalPosition()) - syncItem.getRadius() - other.getRadius();
            if (distance > SyncItem.MAX_AVOID_AHEAD) {
                continue;
            }
            DecimalPosition rotatedOther = other.getDecimalPosition().rotateCounterClock(syncItem.getDecimalPosition(), -angel);
            if (!rectangleAhead.adjoinsCircleExclusive(rotatedOther.getPosition(), syncItem.getRadius())) {
                continue;
            }
            if (distance < minDistance) {
                minDistance = distance;
                mostThreatening = other;
            }
        }

        if (mostThreatening != null) {
            double force = (1.0 - minDistance / SyncItem.MAX_AVOID_AHEAD) * SyncItem.AVOID_FORCE;

            if (MathHelper.isCounterClock(syncItem.getAngel(), syncItem.getDecimalPosition().getAngleToNord(mostThreatening.getDecimalPosition()))) {
                return Vector.NULL_POSITION.getPointFromAngelToNord(syncItem.getAngel() - MathHelper.EIGHTH_RADIANT, force);
            } else {
                return Vector.NULL_POSITION.getPointFromAngelToNord(syncItem.getAngel() + MathHelper.EIGHTH_RADIANT, force);
            }
        } else {
            return Vector.NULL_POSITION;
        }
    }

    private DecimalPosition collisionAvoidance(SyncItem syncItem) {
        // Find items ahead
        double angel = MathHelper.negateAngel(syncItem.getAngel());
        double minDistance = Double.MAX_VALUE;
        SyncItem mostThreatening = null;
        Rectangle rectangleAhead = new Rectangle(syncItem.getPosition().getX() - syncItem.getRadius(),
                syncItem.getPosition().getY() - syncItem.getRadius() - (int) SyncItem.MAX_AVOID_AHEAD,
                syncItem.getRadius() * 2,
                (int) SyncItem.MAX_AVOID_AHEAD);

        for (SyncItem other : movingModel.getSyncItems()) {
            if (other == syncItem) {
                continue;
            }
            double distance = syncItem.getDecimalPosition().getDistance(other.getDecimalPosition()) - syncItem.getRadius() - other.getRadius();
            if (distance > SyncItem.MAX_AVOID_AHEAD) {
                continue;
            }
            double angelToOther = syncItem.getDecimalPosition().getAngleToNord(other.getDecimalPosition());
            if(Math.abs(angel - MathHelper.negateAngel(angelToOther)) > MathHelper.QUARTER_RADIANT) {
                continue;
            }
            if (distance < minDistance) {
                minDistance = distance;
                mostThreatening = other;
            }
        }

        if (mostThreatening != null) {
            double force = (1.0 - minDistance / SyncItem.MAX_AVOID_AHEAD) * SyncItem.AVOID_FORCE;

            if (MathHelper.isCounterClock(syncItem.getAngel(), syncItem.getDecimalPosition().getAngleToNord(mostThreatening.getDecimalPosition()))) {
                return Vector.NULL_POSITION.getPointFromAngelToNord(syncItem.getAngel() - MathHelper.EIGHTH_RADIANT, force);
            } else {
                return Vector.NULL_POSITION.getPointFromAngelToNord(syncItem.getAngel() + MathHelper.EIGHTH_RADIANT, force);
            }
        } else {
            return Vector.NULL_POSITION;
        }
    }

    private boolean lineIntersecsCircle(SyncItem syncItem, SyncItem other, DecimalPosition ahead) {
        DecimalPosition tv = syncItem.getVelocity();
        tv = tv.normalize();
        tv = tv.multiply(SyncItem.MAX_AVOID_AHEAD * 0.5 * syncItem.getVelocity().getLength() / SyncItem.MAX_VELOCITY);

        DecimalPosition ahead2 = syncItem.getDecimalPosition().add(tv);
        return other.getDecimalPosition().getDistance(ahead) <= syncItem.getRadius() + other.getRadius()
                || other.getDecimalPosition().getDistance(ahead2) <= syncItem.getRadius() + other.getRadius()
                || other.getDecimalPosition().getDistance(syncItem.getDecimalPosition()) <= syncItem.getRadius() + other.getRadius();
    }

    private DecimalPosition truncate(DecimalPosition decimalPosition, double max) {
        if (decimalPosition.isNull()) {
            return decimalPosition;
        }
        double distance = max / decimalPosition.getLength();
        distance = Math.min(1.0, distance);
        return decimalPosition.multiply(distance);
    }

    private DecimalPosition doSeek(SyncItem syncItem) {
        DecimalPosition desired = syncItem.getTargetPosition().sub(syncItem.getDecimalPosition());
        double distance = desired.getLength();
        desired = desired.normalize();

        if (distance <= SyncItem.SLOWING_DOWN_RADIUS) {
            desired = desired.multiply(SyncItem.MAX_VELOCITY * distance / SyncItem.SLOWING_DOWN_RADIUS);
        } else {
            desired = desired.multiply(SyncItem.MAX_VELOCITY);
        }

        return desired;
    }


    public void findPath(SyncItem syncItem, Index targetPosition) {
        syncItem.moveTo(targetPosition);
    }

}
