package com.btxtech.game.jsre.client.common;

import java.io.Serializable;

/**
 * User: beat
 * Date: 08.05.2011
 * Time: 16:24:24
 */
public class Line implements Serializable {
    private Index point1;
    private Index point2;
    private double m;
    private double c;

    /**
     * Used by GWT
     */
    Line() {

    }

    public Line(Index start, double angel, int length) {
        this(start, start.getPointFromAngelToNord(angel, length));
    }

    public Line(Index point1, Index point2) {
        if (point1.equals(point2)) {
            throw new IllegalArgumentException("Points are equals: " + point1);
        }
        this.point1 = point1;
        this.point2 = point2;
        m = (double) (this.point2.getY() - this.point1.getY()) / (double) (this.point2.getX() - this.point1.getX());
        c = this.point1.getY() - (m * (double) this.point1.getX());
    }

    public Index getPoint1() {
        return point1;
    }

    public Index getPoint2() {
        return point2;
    }

    public Line translate(double angel, int distance) {
        return new Line(point1.getPointFromAngelToNord(angel, distance), point2.getPointFromAngelToNord(angel, distance));
    }

    public double getM() {
        return m;
    }

    public double getC() {
        return c;
    }

    public double getShortestDistance(Index point) {
        Index projection = projectOnInfiniteLine(point);

        int xMin = Math.min(point1.getX(), point2.getX());
        int xMax = Math.max(point1.getX(), point2.getX());
        int yMin = Math.min(point1.getY(), point2.getY());
        int yMax = Math.max(point1.getY(), point2.getY());

        if (projection.getX() < xMin || projection.getY() < yMin || projection.getX() > xMax || projection.getY() > yMax) {
            return Math.min(point.getDistanceDouble(point1), point.getDistanceDouble(point2));
        } else {
            return point.getDistanceDouble(projection);
        }

    }

    public double getShortestDistance(DecimalPosition point) {
        DecimalPosition projection = projectOnInfiniteLine(point);

        double xMin = Math.min((double) point1.getX(), (double) point2.getX());
        double xMax = Math.max((double) point1.getX(), (double) point2.getX());
        double yMin = Math.min((double) point1.getY(), (double) point2.getY());
        double yMax = Math.max((double) point1.getY(), (double) point2.getY());

        if (projection.getX() < xMin || projection.getY() < yMin || projection.getX() > xMax || projection.getY() > yMax) {
            return Math.min(point.getDistance(point1), point.getDistance(point2));
        } else {
            return point.getDistance(projection);
        }

    }

    public double getShortestDistanceOnInfiniteLine(Index point) {
        Index projection = projectOnInfiniteLine(point);
        return point.getDistanceDouble(projection);
    }

    /**
     * Project the given point on this line with infinite length
     * If the projection x or y will be negative, it returns null
     *
     * @param point point to project
     * @return projection
     */
    public Index projectOnInfiniteLine(Index point) {
        if (m == 0) {
            return new Index(point.getX(), point1.getY());
        } else if (Double.isInfinite(m)) {
            return new Index(point1.getX(), point.getY());
        }

        // m2 & c2 are the projection line which crosses this line orthogonally
        double m2 = 1.0 / -m;
        double c2 = (double) point.getY() - m2 * (double) point.getX();
        double x = (c2 - c) / (m - m2);
        double y = m2 * x + c2;
        return new Index((int) Math.round(x), (int) Math.round(y));
    }

    /**
     * Project the given point on this line with infinite length
     * If the projection x or y will be negative, it returns null
     *
     * @param point point to project
     * @return projection
     */
    public DecimalPosition projectOnInfiniteLine(DecimalPosition point) {
        if (m == 0) {
            return new DecimalPosition(point.getX(), point1.getY());
        } else if (Double.isInfinite(m)) {
            return new DecimalPosition(point1.getX(), point.getY());
        }

        // m2 & c2 are the projection line which crosses this line orthogonally
        double m2 = 1.0 / -m;
        double c2 = point.getY() - m2 * point.getX();
        double x = (c2 - c) / (m - m2);
        double y = m2 * x + c2;
        return new DecimalPosition(x, y);
    }


    public Index getNearestPointOnLine(Index point) {
        Index projection = projectOnInfiniteLine(point);
        if (isPointInLine(projection)) {
            return projection;
        }
        if (projection.getDistance(point1) < projection.getDistance(point2)) {
            return point1;
        } else {
            return point2;
        }
    }


    public boolean isPointInLine(Index point) {
        return Rectangle.generateRectangleFromAnyPoints(point1, point2).contains2(point);
    }

    public double calculateX(double y) {
        if (Double.isInfinite(m)) {
            return point1.getX();
        } else if (Double.compare(0.0, m) == 0 || Double.compare(-0.0, m) == 0) {
            throw new IllegalStateException("Can not calculate X if m is zero");
        } else {
            return (y - c) / m;
        }
    }

    public double calculateY(double x) {
        if (Double.isInfinite(m)) {
            throw new IllegalStateException("Can not calculate Y if m is infinite");
        } else if (Double.compare(0.0, m) == 0 || Double.compare(-0.0, m) == 0) {
            return point1.getY();
        } else {
            return m * x + c;
        }
    }

    public Index getCrossInfinite(Line line) {
        if (Double.compare(m, line.m) == 0
                || (Double.compare(Math.abs(m), 0.0) == 0 && Double.compare(Math.abs(line.m), 0.0) == 0)
                || (Double.isInfinite(m) && Double.isInfinite(line.m))) {
            return null;
        }

        double x;
        double y;
        if (Double.compare(Math.abs(m), 0.0) == 0) {
            y = point1.getY();
            x = line.calculateX(y);
        } else if (Double.compare(Math.abs(line.m), 0.0) == 0) {
            y = line.point1.getY();
            x = calculateX(y);
        } else if (Double.isInfinite(m)) {
            x = point1.getX();
            y = line.calculateY(x);
        } else if (Double.isInfinite(line.m)) {
            x = line.point1.getX();
            y = calculateY(x);
        } else {
            x = (line.c - c) / (m - line.m);
            y = calculateY(x);
        }

        return new Index((int) Math.round(x), (int) Math.round(y));
    }

    public Index getCross(Line line) {
        Index point = getCrossInfinite(line);
        if (point == null) {
            return null;
        }

        if (!isPointInLine(point)) {
            return null;
        }
        if (!line.isPointInLine(point)) {
            return null;
        }
        return point;
    }

    /**
     * Returns the end-point of this line (counter)clockwise relative to the given reference
     *
     * @param reference    reference point
     * @param counterClock direction
     * @return next point
     */
    public Index getEndPoint(Index reference, boolean counterClock) {
        double angel = -reference.getAngleToNord(point1);
        Index point1Rot = point1.rotateCounterClock(reference, angel);
        Index point2Rot = point2.rotateCounterClock(reference, angel);
        if (counterClock) {
            if (point1Rot.getX() < point2Rot.getX()) {
                return point1;
            } else {
                return point2;
            }
        } else {
            if (point1Rot.getX() > point2Rot.getX()) {
                return point1;
            } else {
                return point2;
            }
        }
    }

    public int getLength() {
        return point1.getDistance(point2);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Line)) return false;

        Line line = (Line) o;

        return ((point1.equals(line.point1) && point2.equals(line.point2)) || point1.equals(line.point2) && point2.equals(line.point1));
    }

    @Override
    public int hashCode() {
        return point1.hashCode() + point2.hashCode();
    }

    @Override
    public String toString() {
        return getClass().getName() + " start: " + point1 + " end: " + point2;
    }
}
