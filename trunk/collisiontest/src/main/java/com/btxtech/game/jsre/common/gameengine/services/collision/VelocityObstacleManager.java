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

    private void add(SyncItem other) {
        VelocityObstacle velocityObstacle = new VelocityObstacle(protagonist, other);
        velocityObstacles.add(velocityObstacle);
    }

    public boolean isEmpty() {
        return velocityObstacles.isEmpty();
    }

    public double getBestAngel() {
        Collection<Double> angels = getFreeAngels(protagonist.getAngel());
        if (angels.isEmpty()) {
            throw new IllegalStateException("No free angels found");
        } else if (angels.size() == 1) {
            return CommonJava.getFirst(angels);
        } else {
            double smallestDistance = Double.MAX_VALUE;
            double minAngel = protagonist.getAngel();
            for (Double angel : angels) {
                double distance = MathHelper.getAngel(angel, protagonist.getAngel());
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
            if (velocityObstacle.isInside(angel)) {
                return false;
            }
        }
        return true;
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
        double crashAngel = MathHelper.getAngel(protagonist.getDecimalPosition().getAngleToNord(other.getDecimalPosition()), protagonist.getAngel());
        if (crashAngel > MathHelper.QUARTER_RADIANT) {
            return;
        }
        add(other);
    }
}
