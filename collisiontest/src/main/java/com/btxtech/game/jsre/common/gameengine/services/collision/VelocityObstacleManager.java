package com.btxtech.game.jsre.common.gameengine.services.collision;

import com.btxtech.game.jsre.client.common.DecimalPosition;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by beat
 * on 09.06.2014.
 */
public class VelocityObstacleManager {
    public static final double FORECAST_FACTOR = 2;
    private Collection<VelocityObstacle> velocityObstacles = new ArrayList<>();
    private Collection<VelocityObstacle> insideVelocityObstacles = new ArrayList<>();
    private SyncItem protagonist;

    public VelocityObstacleManager(SyncItem protagonist) {
        this.protagonist = protagonist;
    }

    public void inspect(SyncItem other) {
        if (other == protagonist) {
            return;
        }
        double distance = other.getDecimalPosition().getDistance(protagonist.getDecimalPosition()) - other.getRadius() - protagonist.getRadius();
        if (distance > CollisionService.MAX_DISTANCE) {
            return;
        }
        if (distance < 0.0) {
            System.out.println("C*R*A*S*H"); // TODO
            return;
        }
        velocityObstacles.add(new VelocityObstacle(protagonist, other));
    }

    public DecimalPosition getOptimalVelocity() {
        DecimalPosition preferredVelocity = protagonist.getPreferredVelocity(FORECAST_FACTOR);
        if (velocityObstacles.isEmpty()) {
            return preferredVelocity;
        }
        for (VelocityObstacle velocityObstacle : velocityObstacles) {
            if (velocityObstacle.isInside(preferredVelocity)) {
                insideVelocityObstacles.add(velocityObstacle);
            }
        }
        return preferredVelocity;
    }

    public Collection<VelocityObstacle> getVelocityObstacles() {
        return velocityObstacles;
    }

    public boolean isInside(VelocityObstacle velocityObstacle) {
        return insideVelocityObstacles.contains(velocityObstacle);
    }

    public SyncItem getProtagonist() {
        return protagonist;
    }
}
