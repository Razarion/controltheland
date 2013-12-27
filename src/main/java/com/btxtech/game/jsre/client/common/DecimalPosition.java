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

    public void setPosition(Index index) {
        x = index.getX();
        y = index.getY();
    }

    public Index getPosition() {
        if (index == null || index.getX() != (int) Math.round(x) || index.getY() != (int) Math.round(y)) {
            index = new Index((int) Math.round(x), (int) Math.round(y));
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

    public double getDistance(DecimalPosition decimalPosition) {
        double sqrtC = Math.pow(decimalPosition.x - x, 2) + Math.pow(decimalPosition.y - y, 2);
        return Math.sqrt(sqrtC);
    }

    public double getDistance(Index index) {
        double sqrtC = Math.pow((double) index.getX() - x, 2) + Math.pow((double) index.getY() - y, 2);
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

    @Override
    public String toString() {
        return "x: " + x + " y: " + y;
    }
}
