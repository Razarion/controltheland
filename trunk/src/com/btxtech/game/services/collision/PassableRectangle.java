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

import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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
            if (crossSection.getWeidth() > 0 && crossSection.getHeight() > 0) {
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
        if (crossSectionRectangle.getWeidth() > 0 && crossSectionRectangle.getHeight() > 0) {
            throw new IllegalArgumentException("Rectangle must be a line");
        }
        Neighbor neighbor = new Neighbor(neighborPassableRectangle, crossSectionRectangle);
        neighbors.put(neighborPassableRectangle, neighbor);
    }

    public Rectangle getPixelRectangle() {
        int x = rectangle.getX() * Constants.TILE_WIDTH;
        int y = rectangle.getY() * Constants.TILE_HEIGHT;

        int width = rectangle.getWeidth() * Constants.TILE_WIDTH;
        int height = rectangle.getHeight() * Constants.TILE_HEIGHT;

        return new Rectangle(x, y, width, height);
    }

    public boolean containAbsoluteIndex(Index absolueIndex) {
        return getPixelRectangle().contains(absolueIndex);
    }

    public List<Path> findAllPossiblePassableRectanglePaths(PassableRectangle destinationRect, int fuzzyLimitPaths) {
        Path path = new Path();
        ArrayList<Path> successfulPaths = new ArrayList<Path>();

        askMyNeighborsForPath(destinationRect, path, successfulPaths, fuzzyLimitPaths);

        if (successfulPaths.isEmpty()) {
            throw new IllegalStateException("Path can not be found");
        }
        return successfulPaths;
    }

    private void askMyNeighborsForPath(PassableRectangle destinationRect, Path path, List<Path> successfulPaths, int fuzzyLimitPaths) {
        if (path.contains(this)) {
            // We have already been here
            return;
        }

        path.add(this);

        // First see if a neighbor is may the destination
        Collection<PassableRectangle> neighborsCopy = new ArrayList<PassableRectangle>(neighbors.keySet());
        Iterator<PassableRectangle> iterator = neighborsCopy.iterator();
        while (iterator.hasNext()) {
            PassableRectangle neighbor = iterator.next();
            if (destinationRect.equals(neighbor)) {
                Path subPath = path.createSubPath();
                subPath.add(neighbor);
                successfulPaths.add(subPath);
                iterator.remove();
            }
        }

        if (successfulPaths.size() >= fuzzyLimitPaths) {
            return;
        }

        // Ask the remainig neighbors
        for (PassableRectangle neighbor : neighborsCopy) {
            Path subPath = path.createSubPath();
            neighbor.askMyNeighborsForPath(destinationRect, subPath, successfulPaths, fuzzyLimitPaths);
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
