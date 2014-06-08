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

import com.btxtech.game.jsre.common.MathHelper;

import java.io.Serializable;

/**
 * User: beat
 * Date: 03.10.2010
 * Time: 13:01:16
 */
public class DecimalPosition implements Serializable {
    public static final double FACTOR = 0.01;
    private double x;
    private double y;
    private Index index;

    /**
     * Used by GWT
     */
    DecimalPosition() {
    }

    public DecimalPosition(double x, double y) {
        if (Double.isInfinite(x) || Double.isNaN(x)) {
            throw new IllegalArgumentException("Can not set x value in DecimalPosition: " + x);
        }
        if (Double.isInfinite(y) || Double.isNaN(y)) {
            throw new IllegalArgumentException("Can not set y value in DecimalPosition: " + y);
        }
        this.x = x;
        this.y = y;
    }

    public DecimalPosition(Index position) {
        this(position.getX(), position.getY());
    }

    public DecimalPosition(DecimalPosition position) {
        x = position.x;
        y = position.y;
    }

    public Index getPosition() {
        if (index == null || index.getX() != (int) Math.round(x) || index.getY() != (int) Math.round(y)) {
            index = new Index((int) Math.round(x), (int) Math.round(y));
        }
        return index;
    }

    public Index getPositionFloor() {
        if (index == null || index.getX() != (int) Math.round(x) || index.getY() != (int) Math.round(y)) {
            index = new Index((int) x, (int) y);
        }
        return index;
    }

    public DecimalPosition getPointWithDistance(double distance, Index directionTo, boolean allowOverrun) {
        double directionDistance = getDistance(directionTo);
        if (!allowOverrun && directionDistance <= distance) {
            return new DecimalPosition(directionTo);
        }
        double dirDeltaX = (double) directionTo.getX() - x;
        double dirDeltaY = (double) directionTo.getY() - y;
        double deltaX = (dirDeltaX * distance) / directionDistance;
        double deltaY = (dirDeltaY * distance) / directionDistance;
        return new DecimalPosition(x + deltaX, y + deltaY);
    }

    public DecimalPosition getPointWithDistance(double distance, DecimalPosition directionTo, boolean allowOverrun) {
        double directionDistance = getDistance(directionTo);
        if (directionDistance == 0.0) {
            throw new IllegalArgumentException("Point are equals. This: " + this + " directionTo: " + directionTo);
        }
        if (!allowOverrun && directionDistance <= distance) {
            return new DecimalPosition(directionTo);
        }
        double dirDeltaX = directionTo.getX() - x;
        double dirDeltaY = directionTo.getY() - y;
        double deltaX = (dirDeltaX * distance) / directionDistance;
        double deltaY = (dirDeltaY * distance) / directionDistance;
        return new DecimalPosition(x + deltaX, y + deltaY);
    }

    public DecimalPosition getPointFromAngelToNord(double angle, double radius) {
        double gk = Math.sin(angle) * radius;
        double ak = Math.cos(angle) * radius;
        double newX = x - gk;
        double newY = y - ak;
        return new DecimalPosition(newX, newY);
    }

    public double getAngleToNord(DecimalPosition point) {
        if (equals(point)) {
            throw new IllegalArgumentException("Points are equal");
        }
        double gk = x - point.x;
        double ak = y - point.y;
        if (ak == 0.0) {
            if (gk > 0.0) {
                return Math.PI / 2;
            } else {
                return -Math.PI / 2;
            }
        }
        if (gk == 0.0) {
            if (ak > 0.0) {
                return 0.0;
            } else {
                return Math.PI;
            }
        }
        double angle = Math.atan(gk / ak);
        if (ak < 0.0) {
            angle += Math.PI;
        }
        return angle;
    }

    public double getAngleToNorth() {
        if (x == 0.0 && y == 0.0) {
            return MathHelper.NORTH;
        }
        if (y == 0.0) {
            if (x <= 0.0) {
                return MathHelper.WEST;
            } else {
                return MathHelper.EAST;
            }
        }
        if (x == 0.0) {
            if (y <= 0.0) {
                return 0.0;
            } else {
                return Math.PI;
            }
        }
        double angle = Math.atan(x / y);
        if (y >= 0.0) {
            angle += Math.PI;
        }
        return angle;
    }


    public DecimalPosition rotateCounterClock(DecimalPosition center, double sinus, double cosines) {
        double normX = center.x - x;
        double normY = center.y - y;
        double newX = -normX * cosines - normY * sinus;
        double newY = -normY * cosines + normX * sinus;
        return new DecimalPosition(center.x + newX, center.y + newY);
    }

    public DecimalPosition rotateCounterClock(DecimalPosition center, double angel) {
        double sinus = Math.sin(angel);
        double cosines = Math.cos(angel);
        return rotateCounterClock(center, sinus, cosines);
    }

    public DecimalPosition add(double weight, DecimalPosition otherDecimalPosition) {
        return new DecimalPosition(x + weight * otherDecimalPosition.x, y + weight * otherDecimalPosition.y);
    }

    public DecimalPosition add(DecimalPosition point) {
        return new DecimalPosition(x + point.x, y + point.y);
    }

    public DecimalPosition add(double deltaX, double deltaY) {
        return new DecimalPosition(x + deltaX, y + deltaY);
    }

    public DecimalPosition sub(DecimalPosition point) {
        return new DecimalPosition(x - point.x, y - point.y);
    }

    public DecimalPosition sub(double deltaX, double deltaY) {
        return new DecimalPosition(x - deltaX, y - deltaY);
    }

    public double getMagnitude() {
        return Math.sqrt(x * x + y * y);
    }

    public DecimalPosition normalize(double basis) {
        double m = getMagnitude();
        if (m == 0.0) {
            return getCopy();
        }
        return multiply(basis / m);
    }

    public DecimalPosition normalize() {
        return normalize(1);
    }

    public DecimalPosition divide(double m) {
        if (m == 0.0) {
            throw new ArithmeticException("Divided by 0");
        }
        return new DecimalPosition(x / m, y / m);
    }

    public DecimalPosition multiply(double m) {
        return new DecimalPosition(x * m, y * m);
    }

    public double getDistance(DecimalPosition decimalPosition) {
        double sqrtC = Math.pow(decimalPosition.x - x, 2) + Math.pow(decimalPosition.y - y, 2);
        return Math.sqrt(sqrtC);
    }

    public double getDistance(Index index) {
        double sqrtC = Math.pow((double) index.getX() - x, 2) + Math.pow((double) index.getY() - y, 2);
        return Math.sqrt(sqrtC);
    }

    public double getLength() {
        double sqrtC = Math.pow(x, 2) + Math.pow(y, 2);
        return Math.sqrt(sqrtC);
    }

    public DecimalPosition getCopy() {
        return new DecimalPosition(x, y);
    }

    public boolean isSame(Index position) {
        return getDistance(position) < FACTOR;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public boolean isNull() {
        return x == 0.0 && y == 0.0;
    }

    @Override
    public String toString() {
        return "x: " + x + " y: " + y;
    }
}
