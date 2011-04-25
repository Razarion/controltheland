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

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainSettings;
import com.btxtech.game.services.terrain.TerrainService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * User: beat
 * Date: May 27, 2009
 * Time: 9:42:28 AM
 */
public class PassableRectangle {
    private HashMap<PassableRectangle, Neighbor> neighbors = new HashMap<PassableRectangle, Neighbor>();
    private Rectangle rectangle;
    private TerrainService terrainService;

    private class Neighbor {
        private PassableRectangle passableRectangle;
        private Rectangle crossSection;

        private Neighbor(PassableRectangle passableRectangle, Rectangle crossSection) {
            this.passableRectangle = passableRectangle;
            if (crossSection.getWidth() > 0 && crossSection.getHeight() > 0) {
                throw new IllegalArgumentException();
            }

            this.crossSection = crossSection;
        }

        public PassableRectangle getPassableRectangle() {
            return passableRectangle;
        }

        public Rectangle getCrossSection() {
            return crossSection;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Neighbor neighbor = (Neighbor) o;

            return !(passableRectangle != null ? !passableRectangle.equals(neighbor.passableRectangle) : neighbor.passableRectangle != null);
        }

        @Override
        public int hashCode() {
            return passableRectangle != null ? passableRectangle.hashCode() : 0;
        }
    }

    public PassableRectangle(Rectangle rectangle, TerrainService terrainService) {
        this.rectangle = rectangle;
        this.terrainService = terrainService;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public void addNeighbor(PassableRectangle neighborPassableRectangle) {
        Rectangle crossSectionRectangle = rectangle.getCrossSection(neighborPassableRectangle.rectangle);
        if (crossSectionRectangle.getWidth() > 0 && crossSectionRectangle.getHeight() > 0) {
            throw new IllegalArgumentException("Rectangle must be a line");
        }
        Neighbor neighbor = new Neighbor(neighborPassableRectangle, crossSectionRectangle);
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
        return getPixelRectangle(terrainSettings).contains(absoluteIndex); // TODO contains() method in rectangle changed !!!
    }

    public Path findAllPossiblePassableRectanglePaths(PassableRectangle destinationRect, Index absDestination) {
        Path path = new Path(this);

        // Check level
        for (int tries = 0; tries < 1000; tries++) {
            PathElement next = getBestSuitable(path, absDestination, 0);
            if (next == null) {
                next = backtracking(path, absDestination);
            }
            if (path.containsPassableRectangle(next)) {
                // prevent Loop
                next = backtracking(path, absDestination);
            }
            path.add(next);
            if (next.equalsTo(destinationRect)) {
                return path;
            }

        }

        throw new IllegalStateException("Path can not be found. Max tries exceeded");
    }

    private PathElement backtracking(Path path, Index absDestination) {
        int oldRank = path.backToElementWithAlternatives();
        PathElement alternativeNext = getBestSuitable(path, absDestination, oldRank + 1);
        if (alternativeNext != null) {
            return alternativeNext;
        } else {
            return backtracking(path, absDestination);
        }
    }

    private PathElement getBestSuitable(Path path, Index absDestination, int rank) {
        PassableRectangle secondLast = path.getSecondLastPassableRectangle();
        List<PathElement> allNeighbors = new ArrayList<PathElement>();
        for (PassableRectangle neighbor : path.getLast().getPassableRectangle().neighbors.keySet()) {
            if (neighbor.equals(secondLast)) {
                // Don't go back
                continue;
            }
            Rectangle absRectNeighbor = terrainService.convertToAbsolutePosition(neighbor.getRectangle());
            int d;
            if (absRectNeighbor.contains(absDestination)) {// TODO contains() method in rectangle changed !!!
                d = 0;
            } else {
                d = absRectNeighbor.getNearestPoint(absDestination).getDistance(absDestination);
            }
            allNeighbors.add(new PathElement(neighbor, d));
        }

        if (allNeighbors.size() <= rank) {
            return null;
        }

        Collections.sort(allNeighbors, PathElement.createDistanceComparator());
        PathElement result = allNeighbors.get(rank);
        result.setRank(rank);
        result.setHasAlternatives(allNeighbors.size() + 1 > rank);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PassableRectangle that = (PassableRectangle) o;

        return !(rectangle != null ? !rectangle.equals(that.rectangle) : that.rectangle != null);

    }

    @Override
    public int hashCode() {
        return rectangle != null ? rectangle.hashCode() : 0;
    }

    public Rectangle getBorder(PassableRectangle passableRectangle) {
        return neighbors.get(passableRectangle).getCrossSection();
    }

    @Override
    public String toString() {
        return getClass() + " " + rectangle.toString();
    }
}
