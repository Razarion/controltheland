package com.btxtech.game.jsre.common.gameengine.services.collision;

import com.btxtech.game.jsre.client.common.DecimalPosition;
import com.btxtech.game.jsre.common.MathHelper;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;

/**
 * Created by beat
 * on 09.06.2014.
 */
@Deprecated
public class VelocityObstacle {
    private double start;
    private double end;
    private double deltaAngel;
    private DecimalPosition apex;

    public VelocityObstacle(SyncItem protagonist, SyncItem other) {
        apex = protagonist.getPreferredVelocity().add(other.getVelocity()).multiply(0.5);
        double distance = protagonist.getDecimalPosition().getDistance(other.getDecimalPosition());
        double radius = protagonist.getRadius() + other.getRadius();
        double angel = Math.asin(radius / distance);
        double otherAngel = MathHelper.negateAngel(protagonist.getDecimalPosition().getAngleToNord(other.getDecimalPosition()));
        start = MathHelper.normaliseAngel(otherAngel - angel);
        end = MathHelper.normaliseAngel(otherAngel + angel);
        deltaAngel = 2.0 * angel;
    }

    public double getStartAngel() {
        return start;
    }

    public boolean isInside(DecimalPosition velocity) {
        double relativeVelocityAngel = MathHelper.normaliseAngel(apex.getAngleToNord(velocity));
        return !MathHelper.compareWithPrecision(relativeVelocityAngel, start)
                && !MathHelper.compareWithPrecision(relativeVelocityAngel, end)
                && MathHelper.isInSection(relativeVelocityAngel, start, deltaAngel);

    }

    public double getEndAngel() {
        return end;
    }

    public DecimalPosition getApex() {
        return apex;
    }
}
