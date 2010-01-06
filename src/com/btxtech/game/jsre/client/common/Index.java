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

/**
 * User: beat
 * Date: May 22, 2009
 * Time: 11:56:58 AM
 */
public class Index implements Serializable {
    private int x;
    private int y;

    /**
     * Used by GWT
     */
    Index() {
    }

    public Index(int x, int y) {
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException("Index is not allowed to be negative x=" + x + " y=" + y);
        }
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
        if (x < 0) {
            throw new IllegalArgumentException("Index is not allowed to be negative");
        }
        this.x = x;
    }

    public void setY(int y) {
        if (y < 0) {
            throw new IllegalArgumentException("Index is not allowed to be negative");
        }
        this.y = y;
    }

    public Index getDelta(Index index) {
        return new Index(index.x - x, index.y - y);
    }

    public int getDistance(Index index) {
        double sqrtC = Math.pow(index.x - x, 2) + Math.pow(index.y - y, 2);
        return (int) Math.sqrt(sqrtC);
    }

    public boolean isInRadius(Index index, int radius) {
        return getDistance(index) <= radius;
    }

    public double getAngleToNord(Index point) {
        if (equals(point)) {
            throw new IllegalArgumentException("Points do overlap");
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

    @Deprecated
    public Index getPointFromAngelToNord(double angle, int radius) {
        int gk = (int) (Math.sin(angle) * (double) radius);
        int ak = (int) (Math.cos(angle) * (double) radius);
        return new Index(x - gk, y - ak);
    }

    public Index getPointWithDistance(int distance, Index directionTo) {
        int dirDeltaX = directionTo.x - x;
        int dirDeltaY = directionTo.y - y;
        int directionDistance = getDistance(directionTo);
        if (directionDistance == 0) {
            return directionTo.getCopy();
        }
        int delteX = (dirDeltaX * distance) / directionDistance;
        int deltaY = (dirDeltaY * distance) / directionDistance;
        return new Index(x + delteX, y + deltaY);
    }

    public Rectangle getRegion(int width, int height) {
        int startX = x - width / 2;
        int startY = y - height / 2;

        if (startX < 0) {
            startX = 0;
        }

        if (startY < 0) {
            startY = 0;
        }

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

    public Index add(Index point) {
        return new Index(x + point.x, y + point.y);
    }

    public Index add(int deltaX, int deltaY) {
        return new Index(x + deltaX, y + deltaY);
    }

    public Index sub(Index point) {
        int newX = x - point.x;
        int newY = y - point.y;
        if (newX < 0) {
            newX = 0;
        }
        if (newY < 0) {
            newY = 0;
        }
        return new Index(newX, newY);
    }

    public boolean isBigger(Index point) {
        return x > point.x || y > point.y;
    }

    public boolean isSmaller(Index point) {
        return x < point.x || y < point.y;
    }

    public Index rotateCounterClock(Index center, double sinus, double cosinus) {
        int normX = center.x - x;
        int normY = center.y - y;
        int newX = (int) (-normX * cosinus - normY * sinus);
        int newY = (int) (-normY * cosinus + normX * sinus);
        return new Index(center.x + newX, center.y + newY);
    }
}
