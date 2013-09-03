/*
 * Copyright (c) 2010.
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; version 2 of the License.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 */

package com.btxtech.game.jsre.client.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * User: beat
 * Date: May 22, 2009
 * Time: 11:56:58 AM
 */
public class Index implements Serializable {
    public enum Direction {
        N,
        NE,
        E,
        SE,
        S,
        SW,
        W,
        NW
    }

    private int x;
    private int y;

    /**
     * Used by GWT
     */
    Index() {
    }

    public Index(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Index index = (Index) o;

        return x == index.x && y == index.y;

    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }

    @Override
    public String toString() {
        return "x: " + x + " y: " + y;
    }

    public Index getCopy() {
        return new Index(x, y);
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Index getDelta(Index index) {
        return new Index(index.x - x, index.y - y);
    }

    public int getDistance(Index index) {
        double sqrtC = Math.pow(index.x - x, 2) + Math.pow(index.y - y, 2);
        return (int) Math.round(Math.sqrt(sqrtC));
    }

    public double getDistanceDouble(Index index) {
        double sqrtC = Math.pow(index.x - x, 2) + Math.pow(index.y - y, 2);
        return Math.sqrt(sqrtC);
    }

    public boolean isInRadius(Index index, int radius) {
        return getDistance(index) <= radius;
    }

    public double getAngleToNord(Index point) {
        if (equals(point)) {
            throw new IllegalArgumentException("Points are equal");
        }
        int gk = x - point.x;
        int ak = y - point.y;
        if (ak == 0) {
            if (gk > 0) {
                return Math.PI / 2;
            } else {
                return -Math.PI / 2;
            }
        }
        if (gk == 0) {
            if (ak > 0) {
                return 0;
            } else {
                return Math.PI;
            }
        }
        double angle = Math.atan((double) gk / (double) ak);
        if (ak < 0) {
            angle += Math.PI;
        }
        return angle;
    }

    public Index getPointFromAngelToNord(double angle, int radius) {
        int gk = (int) Math.round(Math.sin(angle) * (double) radius);
        int ak = (int) Math.round(Math.cos(angle) * (double) radius);
        int newX = x - gk;
        int newY = y - ak;
        return new Index(newX, newY);
    }

    public Index getPointFromAngelToNord(double angle, double radius) {
        int gk = (int) Math.round(Math.sin(angle) * radius);
        int ak = (int) Math.round(Math.cos(angle) * radius);
        int newX = x - gk;
        int newY = y - ak;
        return new Index(newX, newY);
    }

    public Index getPointFromAngelToNorthRoundUp(double angle, double radius) {
        int gk = (int) Math.ceil(Math.sin(angle) * radius);
        int ak = (int) Math.ceil(Math.cos(angle) * radius);
        int newX = x - gk;
        int newY = y - ak;
        return new Index(newX, newY);
    }

    public Index getPointWithDistance(int distance, Index directionTo, boolean allowOverrun) {
        double directionDistance = getDistance(directionTo);
        if (!allowOverrun && directionDistance <= distance) {
            return directionTo;
        }
        int dirDeltaX = directionTo.x - x;
        int dirDeltaY = directionTo.y - y;
        int deltaX = (int) Math.round((dirDeltaX * distance) / directionDistance);
        int deltaY = (int) Math.round((dirDeltaY * distance) / directionDistance);

        return new Index(x + deltaX, y + deltaY);
    }

    public Rectangle getRegion(int width, int height) {
        int startX = x - width / 2;
        int startY = y - height / 2;
        return new Rectangle(startX, startY, x + width / 2, y + height / 2);
    }

    public Index getSmallestPoint(Index point) {
        int smallestX = Math.min(x, point.x);
        int smallestY = Math.min(y, point.y);
        return new Index(smallestX, smallestY);
    }

    public Index getLargestPoint(Index point) {
        int largestX = Math.max(x, point.x);
        int largestY = Math.max(y, point.y);
        return new Index(largestX, largestY);
    }

    public Index scale(double scale) {
        return new Index((int) (x * scale), (int) (y * scale));
    }

    public Index scaleInverse(double scale) {
        return new Index((int) (x / scale), (int) (y / scale));
    }

    public Index add(Index point) {
        return new Index(x + point.x, y + point.y);
    }

    public Index add(int deltaX, int deltaY) {
        return new Index(x + deltaX, y + deltaY);
    }

    public Index sub(Index point) {
        return new Index(x - point.x, y - point.y);
    }

    public Index sub(int deltaX, int deltaY) {
        return new Index(x - deltaX, y - deltaY);
    }

    public Index getMiddlePoint(Index other) {
        return new Index((x + other.x) / 2, (y + other.y) / 2);
    }

    public boolean isBigger(Index point) {
        return x > point.x || y > point.y;
    }

    public boolean isSmaller(Index point) {
        return x < point.x || y < point.y;
    }

    public Index rotateCounterClock(Index center, double sinus, double cosines) {
        double normX = center.x - x;
        double normY = center.y - y;
        int newX = (int) Math.round(-normX * cosines - normY * sinus);
        int newY = (int) Math.round(-normY * cosines + normX * sinus);
        return new Index(center.x + newX, center.y + newY);
    }

    public Index rotateCounterClock(Index center, double angel) {
        double sinus = Math.sin(angel);
        double cosines = Math.cos(angel);
        return rotateCounterClock(center, sinus, cosines);
    }

    public Direction getDirection(Index other) {
        if (equals(other)) {
            throw new IllegalArgumentException("Can not determine direction if points are equals: " + this);
        }
        if (x == other.x) {
            if (other.y > y) {
                return Direction.S;
            } else {
                return Direction.N;
            }
        }
        if (y == other.y) {
            if (other.x > x) {
                return Direction.E;
            } else {
                return Direction.W;
            }
        }
        if (other.y > y) {
            if (other.x > x) {
                return Direction.SE;
            } else {
                return Direction.SW;
            }
        } else {
            if (other.x > x) {
                return Direction.NE;
            } else {
                return Direction.NW;
            }
        }
    }

    public boolean isNull() {
        return x == 0 && y == 0;
    }

    public String testString() {
        return "new Index(" + getX() + ", " + getY() + ")";
    }

    public static Index createSaveIndex(int x, int y) {
        if (x < 0) {
            x = 0;
        }
        if (y < 0) {
            y = 0;
        }
        return new Index(x, y);
    }

    public static Index createSaveIndex(Index index) {
        return createSaveIndex(index.getX(), index.getY());
    }

    public static Index saveCopy(Index index) {
        if (index != null) {
            return index.getCopy();
        } else {
            return null;
        }
    }

    public static Collection<Index> add(Collection<Index> positions, Index delta) {
        Collection<Index> result = new ArrayList<Index>();
        for (Index position : positions) {
            result.add(position.add(delta));
        }
        return result;
    }

    public static Index calculateMiddle(Collection<Index> positions) {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (Index position : positions) {
            minX = Math.min(position.getX(), minX);
            minY = Math.min(position.getY(), minY);
            maxX = Math.max(position.getX(), maxX);
            maxY = Math.max(position.getY(), maxY);
        }
        return new Index((minX + maxX) / 2, (minY + maxY) / 2);
    }

}
