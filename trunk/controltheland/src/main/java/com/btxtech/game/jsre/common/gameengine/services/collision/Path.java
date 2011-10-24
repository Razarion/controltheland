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

import com.btxtech.game.jsre.common.gameengine.services.terrain.AbstractTerrainService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * User: beat
 * Date: May 27, 2009
 * Time: 6:29:22 PM
 */
public class Path {
    private List<PathElement> pathElements = new ArrayList<PathElement>();

    private Path() {
    }

    public Path(PassableRectangle startPassableRectangle) {
        add(new PathElement(startPassableRectangle, 0));
    }

    public void add(PathElement pathElement) {
        pathElements.add(pathElement);
    }

    public Path add(Path path) {
        for (PathElement element : path.getPathElements()) {
            add(element);
        }
        return this;
    }

    public PathElement getLast() {
        return pathElements.get(pathElements.size() - 1);
    }

    public PassableRectangle getSecondLastPassableRectangle() {
        if (pathElements.size() > 1) {
            return pathElements.get(pathElements.size() - 2).getPassableRectangle();
        } else {
            return null;
        }
    }

    public List<Port> getAllPassableBorders(AbstractTerrainService terrainService) {
        PathElement previous = null;
        ArrayList<Port> allPorts = new ArrayList<Port>();
        for (PathElement pathElement : pathElements) {
            if (previous != null) {
                allPorts.add(previous.getPassableRectangle().getBorder(pathElement.getPassableRectangle(), terrainService));
            }
            previous = pathElement;
        }
        return allPorts;
    }

    public List<PathElement> getPathElements() {
        return pathElements;
    }

    public int length() {
        return pathElements.size();
    }

    public boolean containsPassableRectangle(PassableRectangle passableRectangle) {
        for (PathElement pathElement : pathElements) {
            if (pathElement.getPassableRectangle().equals(passableRectangle)) {
                return true;
            }
        }
        return false;
    }


    public boolean containsPassableRectangle(PathElement bestPathElement) {
        for (PathElement pathElement : pathElements) {
            if (pathElement.equalsTo(bestPathElement.getPassableRectangle())) {
                return true;
            }
        }
        return false;
    }

    public int backToElementWithAlternatives() {
        while (!pathElements.isEmpty()) {
            PathElement removed = pathElements.remove(pathElements.size() - 1);
            if (removed.isHasAlternativeSiblings()) {
                return removed.getRank();
            }
        }
        throw new PathCanNotBeFoundException("Path can not be backtracked");
    }

    public void reverse() {
        Collections.reverse(pathElements);
    }

    public Path subPath(int start, int endInclusive) {
        List<PathElement> newPathElements = new ArrayList<PathElement>(pathElements.subList(start, endInclusive + 1));
        Path newPath = new Path();
        newPath.pathElements = newPathElements;
        return newPath;
    }
}
