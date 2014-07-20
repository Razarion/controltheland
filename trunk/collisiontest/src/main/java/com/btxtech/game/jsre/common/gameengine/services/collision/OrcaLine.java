package com.btxtech.game.jsre.common.gameengine.services.collision;

import com.btxtech.game.jsre.client.common.DecimalPosition;
import com.btxtech.game.jsre.client.common.Line;
import com.btxtech.game.jsre.common.MathHelper;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import model.MovingModel;

/**
 * Created by beat
 * on 22.06.2014.
 */
public class OrcaLine {
    private final static double DIRECTION_LENGTH = 100;
    private DecimalPosition point;
    private DecimalPosition direction;
    private DecimalPosition relativePosition;
    private DecimalPosition relativeVelocity;
    private DecimalPosition truncationMiddle;
    private double combinedRadius;
    private double truncationRadius;
    private DecimalPosition u;
    private boolean onTruncation;
    private Line coneLine;
    private Line borderLine;
    private DecimalPosition projectionOnVelocityObstacle;
    private SyncItem other;

    public OrcaLine(SyncItem protagonist, SyncItem other, double distance) {
        this.other = other;
        relativePosition = other.getDecimalPosition().sub(protagonist.getDecimalPosition());
        relativeVelocity = protagonist.getVelocity().sub(other.getVelocity());
        combinedRadius = protagonist.getRadius() + other.getRadius();
        // Vector from cutoff center to relative velocity.
        double coneLegAngel = MathHelper.negateAngel(Math.asin(combinedRadius / relativePosition.getLength()));
        double baseAngel = MathHelper.negateAngel(relativePosition.getAngleToNorth());
        double truncationCenter2LegAngel = MathHelper.negateAngel(MathHelper.QUARTER_RADIANT - coneLegAngel);


        if (distance >= 0) {
            truncationMiddle = relativePosition.divide(VelocityObstacleManager.FORECAST_FACTOR);
            DecimalPosition truncationCenter2RelativeVelocity = relativeVelocity.sub(truncationMiddle);
            truncationRadius = combinedRadius / VelocityObstacleManager.FORECAST_FACTOR;
            double truncationCenter2RelativeVelocityAngel = MathHelper.negateAngel(truncationMiddle.getAngleToNord(relativeVelocity) + MathHelper.HALF_RADIANT - baseAngel);


            onTruncation = Math.abs(truncationCenter2RelativeVelocityAngel) < Math.abs(truncationCenter2LegAngel);

            if (onTruncation) {
                // Project on cut-off circle
                double wLength = truncationCenter2RelativeVelocity.getMagnitude();
                u = truncationCenter2RelativeVelocity.normalize(truncationRadius - wLength);
                if(truncationMiddle.getDistance(relativeVelocity) < 0.01) {
                    projectionOnVelocityObstacle = DecimalPosition.NULL.getPointWithDistance(truncationMiddle.getMagnitude() - truncationRadius,truncationMiddle, true);
                    direction = DecimalPosition.NULL.getPointWithDistance(truncationMiddle.getMagnitude() - truncationRadius - DIRECTION_LENGTH,truncationMiddle, true);
                }else{
                    projectionOnVelocityObstacle = truncationMiddle.getPointWithDistance(truncationRadius, relativeVelocity, true);
                    direction = truncationMiddle.getPointWithDistance(truncationRadius + DIRECTION_LENGTH, relativeVelocity, true);
                }
            } else {
                //Project on legs
                if (relativePosition.determinant(truncationCenter2RelativeVelocity) > 0.0) {
                    // Project on left leg
                    coneLine = new Line(new DecimalPosition(0, 0), baseAngel - coneLegAngel, 200);
                    projectionOnVelocityObstacle = coneLine.projectOnInfiniteLine(relativeVelocity);
                    double angel = Math.atan(DIRECTION_LENGTH / projectionOnVelocityObstacle.getLength());
                    direction = DecimalPosition.NULL.getPointFromAngelToNord(baseAngel - coneLegAngel - angel, MathHelper.getPythagorasC(DIRECTION_LENGTH, projectionOnVelocityObstacle.getLength()));
                } else {
                    // Project on right leg
                    coneLine = new Line(new DecimalPosition(0, 0), baseAngel + coneLegAngel, 200);
                    projectionOnVelocityObstacle = coneLine.projectOnInfiniteLine(relativeVelocity);
                    double angel = Math.atan(DIRECTION_LENGTH / projectionOnVelocityObstacle.getLength());
                    direction = DecimalPosition.NULL.getPointFromAngelToNord(baseAngel + coneLegAngel + angel, MathHelper.getPythagorasC(DIRECTION_LENGTH, projectionOnVelocityObstacle.getLength()));
                }

                DecimalPosition projection = coneLine.projectOnInfiniteLine(relativeVelocity);
                u = projection.sub(relativeVelocity);
            }
            point = protagonist.getVelocity().add(u.multiply(0.5));
        } else {
            // Is colliding
            // Project on cut-off circle
            truncationMiddle = relativePosition.divide(MovingModel.TIMER_DELAY);
            DecimalPosition truncationCenter2RelativeVelocity = relativeVelocity.sub(truncationMiddle);
            truncationRadius = combinedRadius / MovingModel.TIMER_DELAY;
            u = truncationCenter2RelativeVelocity.normalize(truncationRadius - truncationCenter2RelativeVelocity.getMagnitude());
            projectionOnVelocityObstacle = truncationMiddle.getPointWithDistance(truncationRadius, relativeVelocity, true);
            direction = truncationMiddle.getPointWithDistance(truncationRadius + DIRECTION_LENGTH, relativeVelocity, true);

            point = protagonist.getVelocity().add(u.multiply(0.5));
            if (point.getMagnitude() + 0.1 > SyncItem.SPEED) {
                point = point.normalize(SyncItem.SPEED - 0.1);
            }
        }

        DecimalPosition lp1 = direction.rotateCounterClock(point, MathHelper.QUARTER_RADIANT);
        DecimalPosition lp2 = direction.rotateCounterClock(point, MathHelper.THREE_QUARTER_RADIANT);
        borderLine = new Line(lp1, lp2);
    }

    public DecimalPosition getPoint() {
        return point;
    }

    public DecimalPosition getDirection() {
        return direction;
    }

    public DecimalPosition getRelativePosition() {
        return relativePosition;
    }

    public DecimalPosition getRelativeVelocity() {
        return relativeVelocity;
    }

    public double getCombinedRadius() {
        return combinedRadius;
    }

    public DecimalPosition getTruncationMiddle() {
        return truncationMiddle;
    }

    public double getTruncationRadius() {
        return truncationRadius;
    }

    public DecimalPosition getU() {
        return u;
    }

    public boolean isOnTruncation() {
        return onTruncation;
    }

    public Line getConeLine() {
        return coneLine;
    }

    public boolean isViolated(DecimalPosition preferredVelocity) {
        if(preferredVelocity.getDistance(point) < 0.01) {
            return false;
        }
        if(preferredVelocity.getDistance(direction) < 0.01) {
            return false;
        }
        double angelRelativeDirection = point.getAngleToNord(direction);
        double angelRelativePreferredVelocity = point.getAngleToNord(preferredVelocity);
        return MathHelper.getAngel(angelRelativeDirection, angelRelativePreferredVelocity) - 0.001 > MathHelper.QUARTER_RADIANT;
    }

    public DecimalPosition getProjectionOnVelocityObstacle() {
        return projectionOnVelocityObstacle;
    }

    public DecimalPosition getPointOnLine(DecimalPosition preferredVelocity, boolean b) {
        DecimalPosition projectionOnLine = borderLine.projectOnInfiniteLine(DecimalPosition.NULL);
        if (preferredVelocity.getMagnitude() < projectionOnLine.getMagnitude()) {
            return null;
        }

        double deltaAngel = Math.acos(projectionOnLine.getMagnitude() / preferredVelocity.getMagnitude());
        double angel = projectionOnLine.getAngleToNorth();
        if (b) {
            angel += deltaAngel;
        } else {
            angel -= deltaAngel;
        }
        return DecimalPosition.NULL.getPointFromAngelToNord(angel, preferredVelocity.getMagnitude());
    }

    public DecimalPosition getCrossPoint(OrcaLine orcaLine) {
        return borderLine.getCrossInfinite(orcaLine.borderLine);
    }

    public Line getBorderLine() {
        return borderLine;
    }

    public SyncItem getOther() {
        return other;
    }
}
