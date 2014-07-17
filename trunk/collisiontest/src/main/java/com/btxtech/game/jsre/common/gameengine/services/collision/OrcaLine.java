package com.btxtech.game.jsre.common.gameengine.services.collision;

import com.btxtech.game.jsre.client.common.DecimalPosition;
import com.btxtech.game.jsre.client.common.Index;
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
    private boolean hasViolation;
    private Line borderLine;
    private double coneLegAngel;
    private DecimalPosition projectionOnVelocityObstacle;

    public OrcaLine(SyncItem protagonist, SyncItem other, double distance) {
        relativePosition = other.getDecimalPosition().sub(protagonist.getDecimalPosition());
        relativeVelocity = protagonist.getVelocity().sub(other.getVelocity());
        double distSq = Math.pow(relativePosition.getLength(), 2);
        combinedRadius = protagonist.getRadius() + other.getRadius();
        double combinedRadiusSq = Math.pow(combinedRadius, 2);
        // Vector from cutoff center to relative velocity.
        coneLegAngel = MathHelper.negateAngel(Math.asin(combinedRadius / relativePosition.getLength()));
        double baseAngel = MathHelper.negateAngel(relativePosition.getAngleToNorth());
        double truncationCenter2LegAngel = MathHelper.negateAngel(MathHelper.QUARTER_RADIANT - coneLegAngel);


        if(distance >= 0) {
            truncationMiddle = relativePosition.divide(VelocityObstacleManager.FORECAST_FACTOR);
            DecimalPosition truncationCenter2RelativeVelocity = relativeVelocity.sub(truncationMiddle);
            double wLengthSq = Math.pow(truncationCenter2RelativeVelocity.getLength(), 2);
            truncationRadius = combinedRadius / VelocityObstacleManager.FORECAST_FACTOR;
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
                    coneLine = new Line(new DecimalPosition(0, 0), baseAngel - coneLegAngel, 200);
                    //   direction = new DecimalPosition(relativePosition.getX() * leg + relativePosition.getY() * combinedRadius,
                    //           relativePosition.getX() * combinedRadius + relativePosition.getY() * leg).multiply(1.0 / distSq);
                    projectionOnVelocityObstacle = coneLine.projectOnInfiniteLine(relativeVelocity);
                    double angel = Math.atan(DIRECTION_LENGTH / projectionOnVelocityObstacle.getLength());
                    direction = DecimalPosition.NULL.getPointFromAngelToNord(baseAngel - coneLegAngel - angel, MathHelper.getPythagorasC(DIRECTION_LENGTH, projectionOnVelocityObstacle.getLength()));
                } else {
                    // Project on right leg
                    coneLine = new Line(new DecimalPosition(0, 0), baseAngel + coneLegAngel, 200);
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
            point = protagonist.getVelocity().add(u.multiply(0.5));
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
                //direction = point.add(u.normalize(DIRECTION_LENGTH));
                //DecimalPosition lp1 = direction.rotateCounterClock(point, MathHelper.QUARTER_RADIANT);
                //DecimalPosition lp2 = direction.rotateCounterClock(point, MathHelper.THREE_QUARTER_RADIANT);
                //borderLine = new Line(lp1, lp2);
            }

            //direction = direction.sub(projectionOnVelocityObstacle).add(point);
            //point = protagonist.getVelocity().add(u.multiply(0.5));
            //u = projectionOnVelocityObstacle.sub(relativeVelocity).multiply(0.5);
        } else {
            // Is colliding
            // Project on cut-off circle
            truncationMiddle = relativePosition.divide(MovingModel.TIMER_DELAY);
            DecimalPosition truncationCenter2RelativeVelocity = relativeVelocity.sub(truncationMiddle);
            //DecimalPosition unitW = truncationCenter2RelativeVelocity.normalize();
            //direction = new DecimalPosition(unitW.getY(), -unitW.getX()); // Rotate -90 degree
            // u = unitW.multiply(truncationRadius - wLength);
            truncationRadius = combinedRadius / MovingModel.TIMER_DELAY;
            u = truncationCenter2RelativeVelocity.normalize(truncationRadius - truncationCenter2RelativeVelocity.getMagnitude());
            projectionOnVelocityObstacle = truncationMiddle.getPointWithDistance(truncationRadius, relativeVelocity, true);
            direction = truncationMiddle.getPointWithDistance(truncationRadius + DIRECTION_LENGTH, relativeVelocity, true);

            point = protagonist.getVelocity().add(u.multiply(0.5));
            if(point.getMagnitude() + 0.1 > SyncItem.SPEED) {
                point = point.normalize(SyncItem.SPEED - 0.1);
            }

///



//	        // Vector from cutoff center to relative velocity
//            DecimalPosition w = relativeVelocity.sub(relativePosition.divide(MovingModel.TIMER_DELAY));
//
//            DecimalPosition unitW = w.normalize();
//
//            direction = new DecimalPosition(unitW.getY(), -unitW.getX());
//            u = unitW.multiply(combinedRadius / MovingModel.TIMER_DELAY - w.getMagnitude()).multiply(0.5);
//            point = protagonist.getVelocity().add(u);
        }

        DecimalPosition lp1 = direction.rotateCounterClock(point, MathHelper.QUARTER_RADIANT);
        DecimalPosition lp2 = direction.rotateCounterClock(point, MathHelper.THREE_QUARTER_RADIANT);
        borderLine = new Line(lp1, lp2);
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

    public boolean isViolated(DecimalPosition preferredVelocity) {
        // DecimalPosition relativeDirection = direction.sub(point);
        // DecimalPosition relativePreferredVelocity = preferredVelocity.sub(point);
        double angelRelativeDirection = point.getAngleToNord(direction);
        double angelRelativePreferredVelocity = point.getAngleToNord(preferredVelocity);
        //return relativeDirection.dotProduct(relativePreferredVelocity) >= 0;
        return MathHelper.getAngel(angelRelativeDirection, angelRelativePreferredVelocity) - 0.001 > MathHelper.QUARTER_RADIANT;
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

    public DecimalPosition getPointOnLine(DecimalPosition preferredVelocity, boolean b) {
        DecimalPosition projectionOnLine = borderLine.projectOnInfiniteLine(DecimalPosition.NULL);
        if(preferredVelocity.getMagnitude() < projectionOnLine.getMagnitude()) {
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
}
