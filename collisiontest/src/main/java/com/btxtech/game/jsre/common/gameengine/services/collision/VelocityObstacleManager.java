package com.btxtech.game.jsre.common.gameengine.services.collision;

import com.btxtech.game.jsre.common.CommonJava;
import com.btxtech.game.jsre.common.MathHelper;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by beat
 * on 09.06.2014.
 */
public class VelocityObstacleManager {
    private List<VelocityObstacle> velocityObstacles = new ArrayList<>();
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
        if(distance <= 0.0) {
            System.out.println("C*R*A*S*H"); // TODO
            return;
        }
        add(other);
    }


    private void add(SyncItem other) {
        VelocityObstacle velocityObstacle = new VelocityObstacle(protagonist, other);
        velocityObstacles.add(velocityObstacle);
    }

    public Double getBestAngel() {
        Collection<Double> angels = getFreeAngels(protagonist.getTargetAngel());
        if (angels.isEmpty()) {
            // No way out
            return null;
        } else if (angels.size() == 1) {
            // Way to target is free
            return CommonJava.getFirst(angels);
        } else {
            // Blocking obstacle before target
            double smallestDistance = Double.MAX_VALUE;
            double minAngel = protagonist.getTargetAngel();
            for (Double angel : angels) {
                double distance = MathHelper.getAngel(angel, protagonist.getTargetAngel());
                if (smallestDistance > distance) {
                    minAngel = angel;
                    smallestDistance = distance;
                }
            }
            return minAngel;
        }
    }

    private Collection<Double> getFreeAngels(double angel) {
        Collection<Double> freeAngels = new ArrayList<>();
        if (isAngelFree(angel)) {
            freeAngels.add(angel);
            return freeAngels;
        }
        for (VelocityObstacle velocityObstacle : velocityObstacles) {
            if (isAngelFree(velocityObstacle.getStartAngel())) {
                freeAngels.add(velocityObstacle.getStartAngel());
            }
            if (isAngelFree(velocityObstacle.getEndAngel())) {
                freeAngels.add(velocityObstacle.getEndAngel());
            }
        }

        return freeAngels;
    }

    private boolean isAngelFree(double angel) {
        for (VelocityObstacle velocityObstacle : velocityObstacles) {
            //if(velocityObstacle.isTarget()) {
            //    continue;
            //}
            if (velocityObstacle.isInside(angel)) {
                return false;
            }
        }
        return true;
    }
}
