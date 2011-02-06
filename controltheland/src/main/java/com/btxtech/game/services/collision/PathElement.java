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

package com.btxtech.game.services.collision;

import java.util.Comparator;

/**
 * User: beat
 * Date: 04.07.2010
 * Time: 11:55:22
 */
public class PathElement {
    private PassableRectangle passableRectangle;
    private int distance;
    private boolean hasAlternatives = false;
    // No negative number, as smaller as higher rank
    private int rank;

    PathElement(PassableRectangle passableRectangle, int distance) {
        this.passableRectangle = passableRectangle;
        this.distance = distance;
    }

    public PassableRectangle getPassableRectangle() {
        return passableRectangle;
    }

    public boolean isHasAlternatives() {
        return hasAlternatives;
    }

    public void setHasAlternatives(boolean hasAlternatives) {
        this.hasAlternatives = hasAlternatives;
    }

    public static Comparator<PathElement> createDistanceComparator() {
        return new Comparator<PathElement>() {
            @Override
            public int compare(PathElement o1, PathElement o2) {
                return o1.distance - o2.distance;
            }
        };
    }

    public boolean equalsTo(PassableRectangle destinationRect) {
        return passableRectangle.equals(destinationRect);
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    @Override
    public String toString() {
        return getClass() + " " + passableRectangle + " rank: " + rank + " hasAlternatives: " + hasAlternatives;
    }
}
