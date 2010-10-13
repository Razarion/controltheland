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
    private double x;
    private double y;

    /**
     * Used by GWT
     */
    DecimalPosition() {
    }

    public DecimalPosition(double x, double y) {
        // TODO check if < 0
        this.x = x;
        this.y = y;
    }

    public DecimalPosition(Index position) {
        this(position.getX(), position.getY());
    }

    public void setPosition(Index index) {
        x = index.getX();
        y = index.getY();
    }

    public Index getPosition() {
        return new Index((int) Math.round(x), (int) Math.round(y));
    }

    public DecimalPosition getPointWithDistance(double distance, DecimalPosition directionTo) {
        double dirDeltaX = directionTo.x - x;
        double dirDeltaY = directionTo.y - y;
        double directionDistance = getDistance(directionTo);
        if (directionDistance > 1.0) {
            return directionTo.getCopy();
        }
        double deltaX = (dirDeltaX * distance) / directionDistance;
        double deltaY = (dirDeltaY * distance) / directionDistance;
        return new DecimalPosition(x + deltaX, y + deltaY);
    }

    public DecimalPosition getPointWithDistance(double distance, Index directionTo, boolean allowOverrun) {
        double directionDistance = getDistance(directionTo);
        if (!allowOverrun && directionDistance <= distance) {
            return new DecimalPosition(directionTo);
        }
        double dirDeltaX = directionTo.getX() - x;
        double dirDeltaY = directionTo.getY() - y;
        double deltaX = (dirDeltaX * distance) / directionDistance;
        double deltaY = (dirDeltaY * distance) / directionDistance;
        return new DecimalPosition(x + deltaX, y + deltaY);
    }

    public double getDistance(DecimalPosition decimalPosition) {
        double sqrtC = Math.pow(decimalPosition.x - x, 2) + Math.pow(decimalPosition.y - y, 2);
        return Math.sqrt(sqrtC);
    }

    public double getDistance(Index index) {
        double sqrtC = Math.pow(index.getX() - x, 2) + Math.pow(index.getY() - y, 2);
        return Math.sqrt(sqrtC);
    }

    public DecimalPosition getCopy() {
        return new DecimalPosition(x, y);
    }

    @Override
    public String toString() {
        return "x: " + x + " y: " + y;
    }
}
