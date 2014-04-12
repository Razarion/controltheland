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

package com.btxtech.game.jsre.common.gameengine.services.collision;

import com.btxtech.game.jsre.client.common.DecimalPosition;
import com.btxtech.game.jsre.client.common.Index;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * User: beat
 * Date: May 27, 2009
 * Time: 6:29:22 PM
 */
public class Path implements Serializable {
    private List<Index> nextWayPositions;
    private Index lastWayPosition;

    /**
     * Used by GWT
     */
    protected Path() {
    }

    public Path(Index absoluteStart, Index absoluteDestination, List<Index> path) {
        lastWayPosition = absoluteStart;
        nextWayPositions = new ArrayList<Index>(path);
        if (nextWayPositions.isEmpty() || (!nextWayPositions.get(nextWayPositions.size() - 1).equals(absoluteDestination))) {
            nextWayPositions.add(absoluteDestination);
        }
    }

    public List<Index> getNextWayPositions() {
        return nextWayPositions;
    }

    public boolean isEmpty() {
        return nextWayPositions == null || nextWayPositions.isEmpty();
    }

    public DecimalPosition calculatePosition(double factor, double speed, DecimalPosition currentPosition) {
        return calculatePosition(factor, speed, 0, currentPosition, false);
    }

    public DecimalPosition moveToCurrentPosition(double factor, double speed, DecimalPosition currentPosition) {
        return calculatePosition(factor, speed, 0, currentPosition, true);
    }

    private DecimalPosition calculatePosition(double factor, double speed, int nextWayPoint, DecimalPosition currentPosition, boolean removePoints) {
        Index destination = nextWayPositions.get(nextWayPoint);

        DecimalPosition newPosition = currentPosition.getPointWithDistance(speed * factor, destination, false);
        if (newPosition.isSame(destination)) {
            nextWayPoint++;
            if (nextWayPositions.size() <= nextWayPoint) {
                DecimalPosition decimalPosition = new DecimalPosition(nextWayPositions.get(nextWayPositions.size() - 1));
                if (removePoints) {
                    nextWayPositions.clear();
                }
                return decimalPosition;
            }
        }

        double realDistance = newPosition.getDistance(currentPosition);
        double relativeDistance = realDistance / speed;
        if (factor - relativeDistance > DecimalPosition.FACTOR) {
            if (removePoints) {
                nextWayPoint--;
                nextWayPositions.remove(0);
            }
            return calculatePosition(factor - relativeDistance, speed, nextWayPoint, newPosition, removePoints);
        } else {
            return newPosition;
        }
    }

    public Index getAbsoluteDestination() {
        return nextWayPositions.get(nextWayPositions.size() - 1);
    }

    @Override
    public String toString() {
        return "Path{" +
                "lastWayPosition=" + lastWayPosition +
                ", nextWayPositions=" + nextWayPositions +
                '}';
    }

    private Index getNextWayPosition() {
        if (isEmpty()) {
            return null;
        } else {
            return nextWayPositions.get(0);
        }
    }
}
