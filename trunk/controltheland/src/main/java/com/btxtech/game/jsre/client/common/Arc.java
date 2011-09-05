package com.btxtech.game.jsre.client.common;

import com.btxtech.game.jsre.common.MathHelper;

import java.io.Serializable;

/**
 * User: beat
 * Date: 19.08.2011
 * Time: 11:57:46
 */
public class Arc implements Segment, Serializable {
    private Index start;
    private Index end;
    private Index middle;

    /**
     * Used by GWT
     */
    Arc() {
    }

    /**
     * Represents an arc from the start point ro the end point in counter clock order
     *
     * @param start  start point
     * @param end    end point
     * @param middle middle point
     */
    public Arc(Index start, Index end, Index middle) {
        this.start = start;
        this.end = end;
        this.middle = middle;
        if (!MathHelper.compareWithPrecision(middle.getDistanceDouble(start), middle.getDistanceDouble(end))) {
            throw new IllegalArgumentException("Distance between middle and start/end must be the same.  start: " + start + " end: " + end + " middle: " + middle);
        }
    }

    public Index getStart() {
        return start;
    }

    public Index getEnd() {
        return end;
    }

    public Index getMiddle() {
        return middle;
    }

    public Index getUpperLeftCorner() {
        return middle.sub((int) Math.round(getRadius()), (int) Math.round(getRadius()));
    }

    public double getRadius() {
        return middle.getDistanceDouble(start);
    }

    public double getAngel() {
        double angel1 = middle.getAngleToNord(start);
        double angel2 = middle.getAngleToNord(end);
        return MathHelper.getAngel(angel1, angel2, true);
    }

    /**
     * Give the intersection point between the arc and the infinite line from reference with angel.
     * The ARC start and end are inclusive
     *
     * @param angel     Line angel
     * @param reference Line start
     * @return intersection point
     */
    @Override
    public Index getCross(double angel, Index reference) {
        angel = MathHelper.normaliseAngel(angel);
        if (!isInSegment(angel, reference)) {
            return null;
        }
        Index translatedReference = reference.sub(middle);
        double p1x = translatedReference.getX();
        double p1y = translatedReference.getY();
        double p2x;
        double p2y;

        if (angel == 0.0 || angel == MathHelper.ONE_RADIANT) {
            p2x = p1x;
            p2y = p1y - 100.0;
        } else if (angel == MathHelper.QUARTER_RADIANT) {
            p2x = p1x + 100.0;
            p2y = p1y;
        } else if (angel == MathHelper.HALF_RADIANT) {
            p2x = p1x;
            p2y = p1y + 100.0;
        } else if (angel == MathHelper.THREE_QUARTER_RADIANT) {
            p2x = p1x - 100.0;
            p2y = p1y;
        } else {
            double m = -Math.tan(MathHelper.QUARTER_RADIANT + angel);
            double c = (double) translatedReference.getY() - m * (double) translatedReference.getX();
            p2x = p1x + 100.0;
            p2y = m * p2x + c;
        }

        //http://mathworld.wolfram.com/Circle-LineIntersection.html
        double dx = p2x - p1x;
        double dy = p2y - p1y;

        double dr = Math.sqrt(dx * dx + dy * dy);
        double dot = p1x * p2y - p2x * p1y;
        double sqrt = Math.sqrt(getRadius() * getRadius() * dr * dr - dot * dot);

        int x1 = (int) Math.round((dot * dy + MathHelper.signum(dy) * dx * sqrt) / (dr * dr));
        int x2 = (int) Math.round((dot * dy - MathHelper.signum(dy) * dx * sqrt) / (dr * dr));
        int y1 = (int) Math.round((-dot * dx + Math.abs(dy) * sqrt) / (dr * dr));
        int y2 = (int) Math.round((-dot * dx - Math.abs(dy) * sqrt) / (dr * dr));

        Index point = middle.add(x1, y1);
        if (isPointOnSegment(point)) {
            return point;
        }
        point = middle.add(x2, y2);
        if (isPointOnSegment(point)) {
            return point;
        }
        throw new IllegalArgumentException("Line does not cross ARC: " + reference + " " + angel + " " + this);
    }

    public boolean isPointOnSegment(Index point) {
        if (point.equals(start) || point.equals(end)) {
            return true;
        }
        if (point.equals(middle)) {
            return false;
        }
        double delta = MathHelper.getAngel(middle.getAngleToNord(start), middle.getAngleToNord(end), true);
        return MathHelper.isInSection(middle.getAngleToNord(point), middle.getAngleToNord(start), delta);
    }

    /**
     * Test if point is in segment. Start and end are inclusive.
     *
     * @param angel     angel to test
     * @param reference reference point (must be inside the circle)
     * @return true if in section
     */
    private boolean isInSegment(double angel, Index reference) {
        double delta = MathHelper.getAngel(reference.getAngleToNord(start), reference.getAngleToNord(end), true);
        return MathHelper.isInSection(angel, reference.getAngleToNord(start), delta);
    }

    @Override
    public boolean isNextPointOnSegment(Index crossPoint, int distance, Index reference, boolean counterClock) {
        double angel = MathHelper.normaliseAngel(middle.getAngleToNord(crossPoint));
        //double deltaAngel = Math.atan((double) distance / (double) getRadius());
        double deltaAngel = Math.atan((double) distance / 2.0 / getRadius()) * 2.0;

        if (counterClock) {
            return isInSegment(angel + deltaAngel, middle);
        } else {
            return isInSegment(angel - deltaAngel, middle);
        }
    }

    @Override
    public Index getNextPoint(Index crossPoint, int distance, Index reference, boolean counterClock) {
        double angel = middle.getAngleToNord(crossPoint);
        double deltaAngel = Math.atan((double) distance / 2.0 / getRadius()) * 2.0;

        double newAngel;
        if (counterClock) {
            newAngel = angel + deltaAngel;
        } else {
            newAngel = angel - deltaAngel;
        }

        return middle.getPointFromAngelToNord(newAngel, (int) Math.round(getRadius()));
    }

    @Override
    public int getDistanceToEnd(Index crossPoint, Index reference, boolean counterClock, int width) {
        double remainingAngel = MathHelper.normaliseAngel(middle.getAngleToNord(crossPoint));

        if (counterClock) {
            remainingAngel = MathHelper.getAngel(remainingAngel, middle.getAngleToNord(end), true);
        } else {
            remainingAngel = MathHelper.getAngel(remainingAngel, middle.getAngleToNord(start), false);
        }

        int distance = (int) (Math.tan(remainingAngel) * getRadius());
        if (distance > width / 2) {
            double hypotenuse = MathHelper.getPythagoras(getRadius(), (double) width / 2.0);
            return (int) Math.round(hypotenuse * Math.sin(remainingAngel));
//            double fullAngel = MathHelper.normaliseAngel(Math.atan((double) width / 2.0 / getRadius())) * 2.0;
//            double deltaAngel = fullAngel - remainingAngel;
//            int deltaDistance = (int) (Math.tan(deltaAngel) * getRadius());
//            return deltaDistance + width / 2;
        } else {
            return distance;
        }
    }

    @Override
    public Index getPerpendicular(Index crossPoint, int perpendicularDistance, Index otherDirection) {
        return middle.getPointWithDistance((int) Math.round(getRadius()) + perpendicularDistance, crossPoint, true);
    }

    @Override
    public Index getEndPoint(Index reference, boolean counterClock) {
        if (counterClock) {
            return end;
        } else {
            return start;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Arc)) return false;

        Arc arc = (Arc) o;

        return end.equals(arc.end) && middle.equals(arc.middle) && start.equals(arc.start);

    }

    @Override
    public int hashCode() {
        int result = start.hashCode();
        result = 31 * result + end.hashCode();
        result = 31 * result + middle.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return getClass().getName() + " start: " + start + " end: " + end + " middle: " + middle;
    }
}
