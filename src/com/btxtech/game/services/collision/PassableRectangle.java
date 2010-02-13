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
import com.btxtech.game.services.terrain.DbTerrainSetting;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: beat
 * Date: May 27, 2009
 * Time: 9:42:28 AM
 */
public class PassableRectangle {
    private HashMap<PassableRectangle, Neighbor> neighbors = new HashMap<PassableRectangle, Neighbor>();
    private Rectangle rectangle;

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

    public PassableRectangle(Rectangle rectangle) {
        this.rectangle = rectangle;
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

    public Rectangle getPixelRectangle(DbTerrainSetting dbTerrainSetting) {
        int x = rectangle.getX() * dbTerrainSetting.getTileWidth();
        int y = rectangle.getY() * dbTerrainSetting.getTileHeight();

        int width = rectangle.getWidth() * dbTerrainSetting.getTileWidth();
        int height = rectangle.getHeight() * dbTerrainSetting.getTileHeight();

        return new Rectangle(x, y, width, height);
    }

    public boolean containAbsoluteIndex(Index absolueIndex, DbTerrainSetting dbTerrainSetting) {
        return getPixelRectangle(dbTerrainSetting).contains(absolueIndex);
    }

    public List<Path> findAllPossiblePassableRectanglePaths(PassableRectangle destinationRect, int maxDepth) {
        ArrayList<Path> successfulPaths = new ArrayList<Path>();
        Set<Path> allPaths = new HashSet<Path>();
        for (PassableRectangle passableRectangle : neighbors.keySet()) {
            Path path = new Path();
            path.add(this);
            path.add(passableRectangle);
            allPaths.add(path);
        }

        // Check level 1
        checkIfPathIsDest(allPaths, destinationRect, successfulPaths);
        if (!successfulPaths.isEmpty()) {
            return successfulPaths;
        }

        // Check level n+1
        for (int i = 0; i < maxDepth; i++) {
            allPaths = getAllNeighbors(allPaths);
            //System.out.println("Depth: " + i + " to check: " + passableRectanglePathsToCheck.size());
            checkIfPathIsDest(allPaths, destinationRect, successfulPaths);
            if (!successfulPaths.isEmpty()) {
                return successfulPaths;
            }
        }


        if (successfulPaths.isEmpty()) {
            throw new IllegalStateException("Path can not be found");
        }
        return successfulPaths;
    }

    private Set<Path> getAllNeighbors(Set<Path> paths) {
        HashSet<Path> allPaths = new HashSet<Path>();
        for (Path path : paths) {
            for (PassableRectangle neighbor : path.getTail().neighbors.keySet()) {
                Path newPath = path.createSubPath();
                newPath.add(neighbor);
                allPaths.add(newPath);
            }
        }
        return allPaths;
    }

    private void checkIfPathIsDest(Set<Path> pathsToCheck, PassableRectangle destinationRect, List<Path> successfulPaths) {
        // if (path.contains(this)) {
        // We have already been here
        //     return;
        // }

        // path.add(this);

        for (Path path : pathsToCheck) {
            if (destinationRect.equals(path.getTail())) {
                successfulPaths.add(path);
                return;
            }
        }
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
        return rectangle.toString();
    }
}
