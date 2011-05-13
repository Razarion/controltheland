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

    public Line(Index point1, Index point2) {
        this.point1 = point1;
        this.point2 = point2;
        //this.point1 = new Index(Math.min(point1.getX(), point2.getX()), Math.min(point1.getY(), point2.getY()));
        //this.point2 = new Index(Math.max(point1.getX(), point2.getX()), Math.max(point1.getY(), point2.getY()));
        m = (double) (this.point2.getY() - this.point1.getY()) / (double) (this.point2.getX() - this.point1.getX());
        c = this.point1.getY() - (m * (double) this.point1.getX());
    }

    public Index getPoint1() {
        return point1;
    }

    public Index getPoint2() {
        return point2;
    }

    /*  public int getStartX() {
        return point1.getX();
    }

    public int getStartY() {
        return point1.getY();
    }

    public int getEndX() {
        return point2.getX();
    }

    public int getEndY() {
        return point2.getY();
    }*/

    public double getM() {
        return m;
    }

    public double getC() {
        return c;
    }

    public int getShortestDistance(Index point) {
        Index projection = projectOnInfiniteLine(point);
        if (projection == null) {
            projection = point; // TODO
        }

        //this.point1 = new Index(Math.min(point1.getX(), point2.getX()), Math.min(point1.getY(), point2.getY()));
        //this.point2 = new Index(Math.max(point1.getX(), point2.getX()), Math.max(point1.getY(), point2.getY()));
        int xMin = Math.min(point1.getX(), point2.getX());
        int xMax = Math.max(point1.getX(), point2.getX());
        int yMin = Math.min(point1.getY(), point2.getY());
        int yMax = Math.max(point1.getY(), point2.getY());

        if (projection.getX() < xMin || projection.getY() < yMin || projection.getX() > xMax || projection.getY() > yMax) {
            return Math.min(point.getDistance(point1), point.getDistance(point2));
        } else {
            return point.getDistance(projection);
        }

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
        if (x < 0 || y < 0) {
            return null;
        }
        return new Index((int) Math.round(x), (int) Math.round(y));
    }

    @Override
    public String toString() {
        return getClass().getName() + " start: " + point1 + " end: " + point2;
    }
}
