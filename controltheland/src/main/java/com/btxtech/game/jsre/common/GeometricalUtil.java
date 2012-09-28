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

package com.btxtech.game.jsre.common;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * User: beat
 * Date: 26.05.2010
 * Time: 11:07:15
 */
public class GeometricalUtil {
    /**
     * Splits Rectangles into smaller Rectangles with 1*1 size (indexes)
     *
     * @param tileRectangle input rectangle
     * @return Collection with Indexes
     */
    public static Set<Index> splitRectanglesToIndexes(Collection<Rectangle> tileRectangle) {
        Set<Index> tiles = new HashSet<Index>();
        for (Rectangle rectangle : tileRectangle) {
            Collection<Rectangle> tileRectangles = rectangle.split(1, 1);
            for (Rectangle tileRect : tileRectangles) {
                tiles.add(new Index(tileRect.getX(), tileRect.getY()));
            }
        }
        return tiles;
    }

    /**
     * Groups Indexes to rectangles
     * 4 adjoining indexes will form a 2*2 or 1*4 rectangle if the can be grouped to a single rectangle
     *
     * @param indexes input tiles
     * @return Collection with rectangles
     */
    public static ArrayList<Rectangle> groupIndexToRectangles(Collection<Index> indexes) {
        ArrayList<Rectangle> rectangles = new ArrayList<Rectangle>();
        if (indexes.isEmpty()) {
            return rectangles;
        }
        Collection<Index> remainingTiles = new HashSet<Index>(indexes);
        removeRectangles(remainingTiles, rectangles);
        return rectangles;
    }

    private static void removeRectangles(Collection<Index> remainingTiles, ArrayList<Rectangle> rectangles) {
        while (!remainingTiles.isEmpty()) {
            Index start = remainingTiles.iterator().next();

            Rectangle rectangle = findBiggestRectangle(start.getX(), start.getY(), remainingTiles);
            rectangles.add(rectangle);

            // remove used atoms
            for (int x = rectangle.getStart().getX(); x <= rectangle.getEnd().getX(); x++) {
                for (int y = rectangle.getStart().getY(); y <= rectangle.getEnd().getY(); y++) {
                    Index indexToRemove = new Index(x, y);
                    remainingTiles.remove(indexToRemove);
                }
            }
            // Make inclusive
            rectangle.growEast(1);
            rectangle.growSouth(1);
        }
    }

    private static Rectangle findBiggestRectangle(int startX, int startY, Collection<Index> tiles) {
        Index index = new Index(startX, startY);
        if (!tiles.contains(index)) {
            throw new IllegalArgumentException("Invalid start point");
        }

        Rectangle rectangle = new Rectangle(index, index);
        boolean canGrowNorth = true;
        boolean canGrowEast = true;
        boolean canGrowSouth = true;
        boolean canGrowWest = true;

        while (canGrowNorth || canGrowEast || canGrowSouth || canGrowWest) {
            if (rectangle.getStart().getY() == 0) {
                canGrowNorth = false;
            }

            if (rectangle.getStart().getX() == 0) {
                canGrowWest = false;
            }

            if (canGrowNorth) {
                Rectangle newRectangle = rectangle.copy();
                newRectangle.growNorth(1);
                int size = Math.abs(newRectangle.getStart().getX() - newRectangle.getEnd().getX());
                if (checkPassableAtomsHorizontal(newRectangle.getStart().getX(), newRectangle.getStart().getY(), size, tiles)) {
                    rectangle = newRectangle;
                } else {
                    canGrowNorth = false;
                }
            }
            if (canGrowEast) {
                Rectangle newRectangle = rectangle.copy();
                newRectangle.growEast(1);
                int size = Math.abs(newRectangle.getStart().getY() - newRectangle.getEnd().getY());
                if (checkPassableAtomsVertial(newRectangle.getEnd().getX(), newRectangle.getStart().getY(), size, tiles)) {
                    rectangle = newRectangle;
                } else {
                    canGrowEast = false;
                }
            }
            if (canGrowSouth) {
                Rectangle newRectangle = rectangle.copy();
                newRectangle.growSouth(1);
                int size = Math.abs(newRectangle.getStart().getX() - newRectangle.getEnd().getX());
                if (checkPassableAtomsHorizontal(newRectangle.getStart().getX(), newRectangle.getEnd().getY(), size, tiles)) {
                    rectangle = newRectangle;
                } else {
                    canGrowSouth = false;
                }
            }
            if (canGrowWest) {
                Rectangle newRectangle = rectangle.copy();
                newRectangle.growWest(1);
                int size = Math.abs(newRectangle.getStart().getY() - newRectangle.getEnd().getY());
                if (checkPassableAtomsVertial(newRectangle.getStart().getX(), newRectangle.getStart().getY(), size, tiles)) {
                    rectangle = newRectangle;
                } else {
                    canGrowWest = false;
                }
            }
        }
        return rectangle;
    }

    private static boolean checkPassableAtomsVertial(int startX, int startY, int length, Collection<Index> passableMap) {
        for (int y = startY; y <= (startY + length); y++) {
            Index index = new Index(startX, y);
            if (!passableMap.contains(index)) {
                return false;
            }
        }
        return true;
    }


    private static boolean checkPassableAtomsHorizontal(int startX, int startY, int length, Collection<Index> passableMap) {
        for (int x = startX; x <= (startX + length); x++) {
            Index index = new Index(x, startY);
            if (!passableMap.contains(index)) {
                return false;
            }
        }
        return true;
    }
}
