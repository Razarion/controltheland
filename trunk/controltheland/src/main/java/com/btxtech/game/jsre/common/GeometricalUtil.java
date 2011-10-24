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
import com.btxtech.game.jsre.common.gameengine.services.collision.PassableRectangle;
import com.btxtech.game.jsre.common.gameengine.services.terrain.AbstractTerrainService;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: beat
 * Date: 26.05.2010
 * Time: 11:07:15
 */
public class GeometricalUtil {
    public static Map<TerrainType, Collection<PassableRectangle>> setupPassableRectangle(Map<TerrainType, boolean[][]> terrainTypeMap) {
        Map<TerrainType, Collection<PassableRectangle>> passableRectangles4TerrainType = new HashMap<TerrainType, Collection<PassableRectangle>>();
        for (Map.Entry<TerrainType, boolean[][]> terrainTypeEntry : terrainTypeMap.entrySet()) {
            Collection<PassableRectangle> passableRectangles = setupPassableRectangle(terrainTypeEntry.getValue());
            passableRectangles4TerrainType.put(terrainTypeEntry.getKey(), passableRectangles);
        }
        return passableRectangles4TerrainType;
    }

    public static Collection<PassableRectangle> setupPassableRectangle(boolean[][] field) {
        List<PassableRectangle> rectangles = new ArrayList<PassableRectangle>();
        PassableRectangle[] lastColumn = new PassableRectangle[field[0].length];
        for (int x = 0; x < field.length; x++) {
            for (int y = 0; y < field[x].length; y++) {
                if (field[x][y]) {
                    if (lastColumn[y] == null) {
                        if (y == 0) {
                            createRectangle(rectangles, lastColumn, x, y);
                        } else if (x == 0 && lastColumn[y - 1] == null) {
                            createRectangle(rectangles, lastColumn, x, y);
                        } else if (x > 0) {
                            if (lastColumn[y - 1] == null) {
                                createRectangle(rectangles, lastColumn, x, y);
                            } else if (lastColumn[y - 1].isCanGrowY()) {
                                lastColumn[y] = lastColumn[y - 1];
                                lastColumn[y].growSouth(1);
                            } else {
                                createRectangle(rectangles, lastColumn, x, y);
                            }
                        } else {
                            if (lastColumn[y - 1] != null && lastColumn[y - 1].isCanGrowY()) {
                                lastColumn[y] = lastColumn[y - 1];
                                lastColumn[y].growSouth(1);
                            } else {
                                createRectangle(rectangles, lastColumn, x, y);
                            }
                        }
                    } else {
                        // Expand rect
                        if (lastColumn[y].isCanGrowX()) {
                            lastColumn[y].setEndX(x + 1);
                            if (y > 0 && lastColumn[y - 1] != null && lastColumn[y] != lastColumn[y - 1]) {
                                lastColumn[y - 1].addNeighbor(lastColumn[y]);
                                lastColumn[y].addNeighbor(lastColumn[y - 1]);
                            }
                        } else if (lastColumn[y - 1] != null && lastColumn[y - 1].isCanGrowY()) {
                            lastColumn[y] = lastColumn[y - 1];
                            lastColumn[y].growSouth(1);
                        } else {
                            createRectangle(rectangles, lastColumn, x, y);
                        }
                    }
                } else {
                    // Terminate old rectangle
                    PassableRectangle rectangle = substituteRect(lastColumn, x, y);
                    if (rectangle != null) {
                        rectangles.add(rectangle);
                    }
                    lastColumn[y] = null;
                }
            }
        }
        return rectangles;
    }

    private static void createRectangle(List<PassableRectangle> rectangles, PassableRectangle[] lastColumn, int x, int y) {
        PassableRectangle rectangle = new PassableRectangle(new Rectangle(x, y, 1, 1));
        if (lastColumn[y] != null) {
            lastColumn[y].addNeighbor(rectangle);
            rectangle.addNeighbor(lastColumn[y]);
        }
        if (y > 0 && lastColumn[y - 1] != null) {
            lastColumn[y - 1].addNeighbor(rectangle);
            rectangle.addNeighbor(lastColumn[y - 1]);
        }
        lastColumn[y] = rectangle;
        rectangles.add(rectangle);
    }

    private static PassableRectangle substituteRect(PassableRectangle[] lastColumn, int x, int y) {
        if (y == 0) {
            if (lastColumn[y] != null) {
                lastColumn[y].clearCanGrowX();
                lastColumn[y].setEndX(x);
            }
            return null;
        }

        if (lastColumn[y] != null && lastColumn[y - 1] != null) {
            if (lastColumn[y].equals(lastColumn[y - 1])) {
                // terminate rectangle x and replace y
                lastColumn[y].setEndX(x);
                lastColumn[y].clearCanGrowX();
                PassableRectangle toBeReplaced = lastColumn[y];
                // get start y
                int startY = y;
                for (int tmpStartY = y; tmpStartY >= 0 && (lastColumn[tmpStartY] != null && lastColumn[tmpStartY].equals(toBeReplaced)); tmpStartY--) {
                    startY = tmpStartY;
                }

                PassableRectangle substituteRect;
                boolean returnNull = false;
                if (startY > 0 && lastColumn[startY - 1] != null && lastColumn[startY - 1].getRectangle().getWidth() == 1) {
                    substituteRect = lastColumn[startY - 1];
                    returnNull = true;
                } else {
                    substituteRect = new PassableRectangle(new Rectangle(x, startY, 1, 1));
                    lastColumn[startY].addNeighbor(substituteRect);
                    substituteRect.addNeighbor(lastColumn[startY]);
                }

                // Replace y with new rect
                substituteRect.setEndY(y);
                substituteRect.clearCanGrowY();

                for (int tmpY = startY; tmpY < y; tmpY++) {
                    lastColumn[tmpY] = substituteRect;
                }

                if (startY > 0 && lastColumn[startY - 1] != null && lastColumn[startY - 1] != substituteRect) {
                    lastColumn[startY - 1].addNeighbor(substituteRect);
                    substituteRect.addNeighbor(lastColumn[startY - 1]);
                }
                if (returnNull) {
                    return null;
                } else {
                    return substituteRect;
                }
            } else {
                lastColumn[y].clearCanGrowX();
                lastColumn[y - 1].clearCanGrowY();
                return null;
            }
        } else if (lastColumn[y] == null && lastColumn[y - 1] != null) {
            lastColumn[y - 1].clearCanGrowY();
            return null;
        } else if (lastColumn[y] != null && lastColumn[y - 1] == null) {
            lastColumn[y].clearCanGrowX();
            return null;
        } else {
            // lastColumn[y] == null && lastColumn[y - 1] == null -> do nothing
            return null;
        }
    }

    public static void checkField(boolean[][] field, Collection<PassableRectangle> rectangles, AbstractTerrainService terrainService) {
        Set<Index> blockedIndexes = new HashSet<Index>();
        Set<Index> freeIndexes = new HashSet<Index>();

        for (int x = 0; x < field.length; x++) {
            for (int y = 0; y < field[x].length; y++) {
                if (field[x][y]) {
                    freeIndexes.add(new Index(x, y));
                } else {
                    blockedIndexes.add(new Index(x, y));
                }
            }
        }

        for (PassableRectangle rectangle : rectangles) {
            if (rectangle.getRectangle().getWidth() == 0) {
                throw new RuntimeException("Rectangle width is 0: " + rectangle);
            }
            if (rectangle.getRectangle().getHeight() == 0) {
                throw new RuntimeException("Rectangle height is 0: " + rectangle);
            }
            Collection<Rectangle> tileRectangles = rectangle.getRectangle().split(1, 1);
            for (Rectangle tileRect : tileRectangles) {
                Index index = new Index(tileRect.getX(), tileRect.getY());
                if (!freeIndexes.remove(index)) {
                    throw new RuntimeException("The index '" + index + "' could not be found in the filed for rectangle: " + rectangle);
                }
                if (blockedIndexes.contains(index)) {
                    throw new RuntimeException("The index '" + index + "' should not be contained in the rectangle: " + rectangle);
                }
            }
        }

        if (!freeIndexes.isEmpty()) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("This indexes are not contained in a rectangle:\n");
            for (Index freeIndex : freeIndexes) {
                stringBuffer.append(freeIndex);
                stringBuffer.append("\n");
            }
            throw new RuntimeException(stringBuffer.toString());
        }

        // Check neighbors
        for (PassableRectangle rectangle : rectangles) {
            Map<PassableRectangle, PassableRectangle.Neighbor> neighbors = new HashMap<PassableRectangle, PassableRectangle.Neighbor>(rectangle.getNeighbors(terrainService));
            for (PassableRectangle possibleNeighbor : rectangles) {
                if (possibleNeighbor.equals(rectangle)) {
                    continue;
                }
                if (rectangle.getRectangle().adjoins(possibleNeighbor.getRectangle()) &&
                        !rectangle.getRectangle().getCrossSection(possibleNeighbor.getRectangle()).isEmpty()) {
                    if (neighbors.remove(possibleNeighbor) == null) {
                        throw new RuntimeException("'" + rectangle + "' does not know neighbor '" + possibleNeighbor + "'");
                    }
                    // Check size of port
                    Rectangle absRectangle = terrainService.convertToAbsolutePosition(rectangle.getRectangle());
                    Rectangle absNeighborRectangle = terrainService.convertToAbsolutePosition(possibleNeighbor.getRectangle());
                    Rectangle crossSection = absRectangle.getCrossSection(absNeighborRectangle);
                    int min = Math.min(crossSection.getWidth(), crossSection.getHeight());
                    int length = Math.max(crossSection.getWidth(), crossSection.getHeight());
                    if (min != 0) {
                        throw new RuntimeException("Min width/heigh should be 0");
                    }
                    PassableRectangle.Neighbor neighbor = rectangle.getNeighbors(terrainService).get(possibleNeighbor);
                    if (length - 1 != neighbor.getPort().getCurrentCrossLine().getLength()) {
                        throw new RuntimeException("Crossline lenght is wrong");
                    }

                }
            }
            if (!neighbors.isEmpty()) {
                throw new RuntimeException("Passable rectangle does bot know all neighbors");
            }
        }

    }


    public static ArrayList<Rectangle> separateIntoRectangles(Collection<Index> tiles) {
        ArrayList<Rectangle> rectangles = new ArrayList<Rectangle>();
        if (tiles.isEmpty()) {
            return rectangles;
        }
        Collection<Index> remainingTiles = new HashSet<Index>(tiles);
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

    static private Rectangle findBiggestRectangle(int startX, int startY, Collection<Index> tiles) {
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

    static private boolean checkPassableAtomsVertial(int startX, int startY, int length, Collection<Index> passableMap) {
        for (int y = startY; y <= (startY + length); y++) {
            Index index = new Index(startX, y);
            if (!passableMap.contains(index)) {
                return false;
            }
        }
        return true;
    }


    static private boolean checkPassableAtomsHorizontal(int startX, int startY, int length, Collection<Index> passableMap) {
        for (int x = startX; x <= (startX + length); x++) {
            Index index = new Index(x, startY);
            if (!passableMap.contains(index)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Splits Rectangles into smaller Rectangles with 1*1 size (indexes)
     *
     * @param tileRectangle input rectangle
     * @return Collection with Indexes
     */
    public static Collection<Index> splitIntoTiles(Collection<Rectangle> tileRectangle) {
        ArrayList<Index> tiles = new ArrayList<Index>();
        for (Rectangle rectangle : tileRectangle) {
            Collection<Rectangle> tileRectangles = rectangle.split(1, 1);
            for (Rectangle tileRect : tileRectangles) {
                tiles.add(new Index(tileRect.getX(), tileRect.getY()));
            }
        }
        return tiles;
    }
}
