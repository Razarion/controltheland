package com.btxtech.game.jsre.common.gameengine.services.collision;

import com.btxtech.game.jsre.client.common.DecimalPosition;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Line;
import com.btxtech.game.jsre.common.MathHelper;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;

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
    private boolean hasViolation;
    private Line borderLine;
    private double coneLegAngel;
    private DecimalPosition projectionOnVelocityObstacle;

    public OrcaLine(SyncItem protagonist, SyncItem other) {
        relativePosition = other.getDecimalPosition().sub(protagonist.getDecimalPosition());
        relativeVelocity = protagonist.getVelocity().sub(other.getVelocity());
        truncationMiddle = relativePosition.divide(VelocityObstacleManager.FORECAST_FACTOR);
        DecimalPosition truncationCenter2RelativeVelocity = relativeVelocity.sub(truncationMiddle);
        double distSq = Math.pow(relativePosition.getLength(), 2);
        combinedRadius = protagonist.getRadius() + other.getRadius();
        truncationRadius = combinedRadius / VelocityObstacleManager.FORECAST_FACTOR;
        double combinedRadiusSq = Math.pow(combinedRadius, 2);
        // Vector from cutoff center to relative velocity.
        double wLengthSq = Math.pow(truncationCenter2RelativeVelocity.getLength(), 2);
        coneLegAngel = MathHelper.negateAngel(Math.asin(combinedRadius / relativePosition.getLength()));
        double baseAngel = MathHelper.negateAngel(relativePosition.getAngleToNorth());
        double truncationCenter2LegAngel = MathHelper.negateAngel(MathHelper.QUARTER_RADIANT - coneLegAngel);
        double truncationCenter2RelativeVelocityAngel = MathHelper.negateAngel(truncationMiddle.getAngleToNord(relativeVelocity) + MathHelper.HALF_RADIANT - baseAngel);

        double dotProduct1 = truncationCenter2RelativeVelocity.dotProduct(relativePosition);
        // Projection on truncated circle
        // onTruncation = dotProduct1 < 0.0 && Math.pow(dotProduct1, 2) > combinedRadiusSq * wLengthSq;
        onTruncation = Math.abs(truncationCenter2RelativeVelocityAngel) < Math.abs(truncationCenter2LegAngel);

        //if (dotProduct1 < 0.0 && Math.pow(dotProduct1, 2) > combinedRadiusSq * wLengthSq) {
        if (onTruncation) {
            // Project on cut-off circle
            double wLength = truncationCenter2RelativeVelocity.getMagnitude();
            //DecimalPosition unitW = truncationCenter2RelativeVelocity.normalize();
            hasViolation = wLength < truncationRadius;
            //direction = new DecimalPosition(unitW.getY(), -unitW.getX()); // Rotate -90 degree
            // u = unitW.multiply(truncationRadius - wLength);
            u = truncationCenter2RelativeVelocity.normalize(truncationRadius - wLength);
            projectionOnVelocityObstacle = truncationMiddle.getPointWithDistance(truncationRadius, relativeVelocity, true);
            direction = truncationMiddle.getPointWithDistance(truncationRadius + DIRECTION_LENGTH, relativeVelocity, true);
        } else {
            //Project on legs
            double leg = Math.sqrt(distSq - combinedRadiusSq);

            if (relativePosition.determinant(truncationCenter2RelativeVelocity) > 0.0) {
                // Project on left leg
                coneLine = new Line(new Index(0, 0), baseAngel - coneLegAngel, 200);
                //   direction = new DecimalPosition(relativePosition.getX() * leg + relativePosition.getY() * combinedRadius,
                //           relativePosition.getX() * combinedRadius + relativePosition.getY() * leg).multiply(1.0 / distSq);
                projectionOnVelocityObstacle = coneLine.projectOnInfiniteLine(relativeVelocity);
                double angel = Math.atan(DIRECTION_LENGTH / projectionOnVelocityObstacle.getLength());
                direction = DecimalPosition.NULL.getPointFromAngelToNord(baseAngel - coneLegAngel - angel, MathHelper.getPythagorasC(DIRECTION_LENGTH, projectionOnVelocityObstacle.getLength()));
            } else {
                // Project on right leg
                coneLine = new Line(new Index(0, 0), baseAngel + coneLegAngel, 200);
                projectionOnVelocityObstacle = coneLine.projectOnInfiniteLine(relativeVelocity);
                double angel = Math.atan(DIRECTION_LENGTH / projectionOnVelocityObstacle.getLength());
                direction = DecimalPosition.NULL.getPointFromAngelToNord(baseAngel + coneLegAngel + angel, MathHelper.getPythagorasC(DIRECTION_LENGTH, projectionOnVelocityObstacle.getLength()));
                //  direction = new DecimalPosition(relativePosition.getX() * leg - relativePosition.getY() * combinedRadius,
                //          -relativePosition.getX() * combinedRadius + relativePosition.getY() * leg).negate().multiply(1.0 / distSq);
            }

            // double dotProduct2 = relativeVelocity.dotProduct(direction);
            // u = direction.multiply(dotProduct2).sub(relativeVelocity);
            DecimalPosition projection = coneLine.projectOnInfiniteLine(relativeVelocity);
            u = projection.sub(relativeVelocity);
            double angelToRelativeVelocity = MathHelper.getAngel(baseAngel, relativeVelocity.getAngleToNorth());
            double coneLegAngelAbs = Math.abs(MathHelper.normaliseAngel(coneLegAngel));
            hasViolation = angelToRelativeVelocity < coneLegAngelAbs;
        }
        if (u.getMagnitude() == 0.0) {
            // throw new UnsupportedOperationException();
            /*
            // TODO test this
            point = protagonist.getVelocity().getCopy();
            if (onTruncation) {
                direction = truncationMiddle.getPointWithDistance(truncationRadius + DIRECTION_LENGTH, point, true);
            } else {
                double angel = Math.abs(Math.atan(DIRECTION_LENGTH / point.getLength())) + Math.abs(MathHelper.normaliseAngel(coneLegAngel));
                DecimalPosition conePoint = new DecimalPosition(coneLine.getPoint2());
                double directionAngel;
                if (conePoint.determinant(relativePosition) > 0) {
                    directionAngel = conePoint.getAngleToNorth() + angel;
                } else {
                    directionAngel = conePoint.getAngleToNorth() - angel;
                }
                direction = DecimalPosition.NULL.getPointFromAngelToNord(directionAngel, MathHelper.getPythagorasC(DIRECTION_LENGTH, point.getLength()));
            }*/
        } else if (!hasViolation) {
            //  throw new UnsupportedOperationException();
            //    direction = direction.rotateCounterClock(point, MathHelper.HALF_RADIANT);
        /*   point = protagonist.getVelocity().getCopy();
            if (onTruncation) {
                direction = truncationMiddle.getPointWithDistance(truncationRadius + DIRECTION_LENGTH, point, true);
            } else {
                double angel = Math.abs(Math.atan(DIRECTION_LENGTH / point.getLength())) + Math.abs(MathHelper.normaliseAngel(coneLegAngel));
                DecimalPosition conePoint = new DecimalPosition(coneLine.getPoint2());
                double directionAngel;
                if (conePoint.determinant(relativePosition) > 0) {
                    directionAngel = conePoint.getAngleToNorth() + angel;
                } else {
                    directionAngel = conePoint.getAngleToNorth() - angel;
                }
                direction = DecimalPosition.NULL.getPointFromAngelToNord(directionAngel, MathHelper.getPythagorasC(DIRECTION_LENGTH, point.getLength()));
            }*/
        } else {
            point = protagonist.getVelocity().add(u.multiply(0.5));
            //direction = point.add(u.normalize(DIRECTION_LENGTH));
            Index lp1 = direction.rotateCounterClock(point, MathHelper.QUARTER_RADIANT).getPosition();
            Index lp2 = direction.rotateCounterClock(point, MathHelper.THREE_QUARTER_RADIANT).getPosition();
            borderLine = new Line(lp1, lp2);
        }

        u = projectionOnVelocityObstacle.sub(relativeVelocity).multiply(0.5);
        point = protagonist.getVelocity().add(u);
        direction = direction.sub(projectionOnVelocityObstacle).add(point);
        //if (!hasViolation) {
        //    direction = direction.rotateCounterClock(point, MathHelper.HALF_RADIANT);
        //}
    }

    public OrcaLine() {

    }

    public DecimalPosition getPoint() {
        return point;
    }

    public DecimalPosition getDirection() {
        return direction;
    }

    public void setPoint(DecimalPosition point) {
        this.point = point;
    }

    public void setDirection(DecimalPosition direction) {
        this.direction = direction;
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

    private boolean isViolated(DecimalPosition preferredVelocity) {
        // DecimalPosition relativeDirection = direction.sub(point);
        // DecimalPosition relativePreferredVelocity = preferredVelocity.sub(point);
        double angelRelativeDirection = point.getAngleToNord(direction);
        double angelRelativePreferredVelocity = point.getAngleToNord(preferredVelocity);
        //return relativeDirection.dotProduct(relativePreferredVelocity) >= 0;
        return MathHelper.getAngel(angelRelativeDirection, angelRelativePreferredVelocity) > MathHelper.QUARTER_RADIANT;
    }

    public DecimalPosition nearestVelocity(DecimalPosition preferredVelocity) {
        if (point.sub(preferredVelocity).getLength() < 0.0001) {
            // Is on point
            return preferredVelocity;
        }

        if (!isViolated(preferredVelocity)) {
            // No VO violation
            return preferredVelocity;
        }

        DecimalPosition originOnLine = borderLine.projectOnInfiniteLine(new DecimalPosition(0, 0));
        if (preferredVelocity.getMagnitude() < originOnLine.getMagnitude()) {
            // ???
            return preferredVelocity;
        }

        DecimalPosition preferredVelocityOnLine = borderLine.projectOnInfiniteLine(preferredVelocity);
        if (originOnLine.getDistance(preferredVelocityOnLine) == 0.0) {
            // ???
            return preferredVelocity;
        } else {
            double distanceOnLine = MathHelper.getPythagorasA(preferredVelocity.getMagnitude(), originOnLine.getMagnitude());
            return originOnLine.getPointWithDistance(distanceOnLine, preferredVelocityOnLine, true);
        }
    }

    public boolean isHasViolation() {
        return hasViolation;
    }

    public DecimalPosition getProjectionOnVelocityObstacle() {
        return projectionOnVelocityObstacle;
    }
}
