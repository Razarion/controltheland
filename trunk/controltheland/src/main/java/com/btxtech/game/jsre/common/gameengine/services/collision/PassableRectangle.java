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

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.gameengine.services.terrain.AbstractTerrainService;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainSettings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: beat
 * Date: May 27, 2009
 * Time: 9:42:28 AM
 */
public class PassableRectangle {
    // No equals and hash du to the GeometricalUtil.setupPassableRectangle()
    private static final int MAX_TRIES = 10000;
    private Map<PassableRectangle, Neighbor> neighbors = new HashMap<PassableRectangle, Neighbor>();
    private Rectangle rectangle;
    private boolean canGrowX = true;
    private boolean canGrowY = true;

    public static class Neighbor {
        // No equals() and no hashCode() due to the GWT serialization/de-serialization
        private PassableRectangle passableRectangle;
        private Port port;

        /**
         * Used by GWT
         */
        Neighbor() {
        }

        private Neighbor(PassableRectangle passableRectangle, Port port) {
            this.passableRectangle = passableRectangle;
            this.port = port;
        }

        public PassableRectangle getPassableRectangle() {
            return passableRectangle;
        }

        public Port getPort() {
            return port;
        }
    }

    /**
     * Used by GWT
     */
    PassableRectangle() {
    }

    public PassableRectangle(Rectangle rectangle) {
        this.rectangle = rectangle;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public void addNeighbor(PassableRectangle neighborPassableRectangle, AbstractTerrainService terrainService) {
        if (neighbors.containsKey(neighborPassableRectangle)) {
            return;
        }
        Neighbor neighbor = new Neighbor(neighborPassableRectangle, new Port(terrainService.convertToAbsolutePosition(rectangle),
                terrainService.convertToAbsolutePosition(neighborPassableRectangle.rectangle)));
        neighbors.put(neighborPassableRectangle, neighbor);
    }

    public Rectangle getPixelRectangle(TerrainSettings terrainSettings) {
        int x = rectangle.getX() * terrainSettings.getTileWidth();
        int y = rectangle.getY() * terrainSettings.getTileHeight();

        int width = rectangle.getWidth() * terrainSettings.getTileWidth();
        int height = rectangle.getHeight() * terrainSettings.getTileHeight();

        return new Rectangle(x, y, width, height);
    }

    public boolean containAbsoluteIndex(Index absoluteIndex, TerrainSettings terrainSettings) {
        return getPixelRectangle(terrainSettings).containsExclusive(absoluteIndex);
    }

    public Path findPossiblePassableRectanglePaths(AbstractTerrainService terrainService, Index absStart, PassableRectangle destinationRect, Index absDestination) {
        Path pathStartToDestination = new Path(this);
        Path pathDestinationToStart = new Path(destinationRect);

        for (int tries = 0; tries < MAX_TRIES; tries++) {
            if (findPath(terrainService, absStart, absDestination, destinationRect, pathStartToDestination)) {
                return pathStartToDestination;
            }
            if (findPath(terrainService, absDestination, absStart, this, pathDestinationToStart)) {
                pathDestinationToStart.reverse();
                return pathDestinationToStart;
            }
        }

        throw new PathCanNotBeFoundException("Max tries exceeded");
    }

    private boolean findPath(AbstractTerrainService terrainService, Index absStart, Index absDestination, PassableRectangle destinationRect, Path path) {
        PathElement next = getBestSuitable(terrainService, path, absStart, absDestination, 0);
        if (next == null) {
            next = backtracking(terrainService, path, absStart, absDestination);
        }
        path.add(next);
        return next.equalsTo(destinationRect);
    }

    private PathElement backtracking(AbstractTerrainService terrainService, Path path, Index absStart, Index absDestination) {
        int oldRank = path.backToElementWithAlternatives();
        PathElement alternativeNext = getBestSuitable(terrainService, path, absStart, absDestination, oldRank + 1);
        if (alternativeNext != null) {
            return alternativeNext;
        } else {
            return backtracking(terrainService, path, absStart, absDestination);
        }
    }

    private PathElement getBestSuitable(AbstractTerrainService terrainService, Path path, Index absStart, Index absDestination, int rank) {
        List<PathElement> allNeighbors = new ArrayList<PathElement>();
        for (PassableRectangle neighbor : path.getLast().getPassableRectangle().neighbors.keySet()) {
            if (path.containsPassableRectangle(neighbor)) {
                // Don't go back
                continue;
            }
            //Index start = terrainService.getAbsolutIndexForTerrainTileIndex(path.getLast().getPassableRectangle().getRectangle().getCenter());
            //Rectangle absRectNeighbor = terrainService.convertToAbsolutePosition(neighbor.getRectangle());
            //int d = absRectNeighbor.getShortestDistanceToLine(start, absDestination);
            // Not found

            //Rectangle tileCross = neighbor.getBorder(path.getLast().getPassableRectangle());
            //Rectangle absCross = terrainService.convertToAbsolutePosition(tileCross);
            //int d = absCross.getShortestDistanceToLine(absStart, absDestination);
            // 11000

            //Rectangle tileCross = neighbor.getBorder(path.getLast().getPassableRectangle());
            //Rectangle absCross = terrainService.convertToAbsolutePosition(tileCross);
            //int d = absCross.getNearestPoint(absDestination).getDistance(absDestination);
            // 17000

            //Rectangle absNeighbor = terrainService.convertToAbsolutePosition(neighbor.getRectangle());
            //int d = absNeighbor.getShortestDistanceToLine(absStart, absDestination);

            int d;
            Rectangle absNeighbor = terrainService.convertToAbsolutePosition(neighbor.getRectangle());
            if (absNeighbor.containsExclusive(absDestination)) {
                d = 0;
            } else {
                d = absNeighbor.getNearestPoint(absDestination).getDistance(absDestination);
            }

            allNeighbors.add(new PathElement(neighbor, d));
        }

        if (allNeighbors.size() <= rank) {
            return null;
        }

        Collections.sort(allNeighbors, PathElement.createDistanceComparator());
        PathElement result = allNeighbors.get(rank);
        result.setRank(rank);
        result.setHasAlternativeSiblings(allNeighbors.size() + 1 > rank);
        return result;
    }

    public Port getBorder(PassableRectangle passableRectangle) {
        return neighbors.get(passableRectangle).getPort();
    }

    public Map<PassableRectangle, Neighbor> getNeighbors() {
        return neighbors;
    }

    public boolean isNeighbor(PassableRectangle passableRectangle) {
        return neighbors.containsKey(passableRectangle);
    }

    public boolean isCanGrowX() {
        return canGrowX;
    }

    public void clearCanGrowX() {
        canGrowX = false;
    }

    public boolean isCanGrowY() {
        return canGrowY;
    }

    public void clearCanGrowY() {
        canGrowY = false;
    }

    public void setY(int y) {
        rectangle.setY(y);
    }

    public void setEndX(int x) {
        rectangle.setEndX(x);
    }

    public void setEndY(int y) {
        rectangle.setEndY(y);
    }

    public void growSouth(int size) {
        rectangle.growSouth(size);
    }

    @Override
    public String toString() {
        return "PassableRectangle: " + rectangle;
    }
}
