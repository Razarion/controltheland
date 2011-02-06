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

import com.btxtech.game.jsre.client.common.Rectangle;
import java.util.ArrayList;
import java.util.List;


/**
 * User: beat
 * Date: May 27, 2009
 * Time: 6:29:22 PM
 */
public class Path {
    private List<PathElement> pathElements = new ArrayList<PathElement>();

    public Path(PassableRectangle startPassableRectangle) {
        add(new PathElement(startPassableRectangle, 0));
    }

    public void add(PathElement pathElement) {
        pathElements.add(pathElement);
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

    public List<Rectangle> getAllPassableBorders() {
        PathElement previous = null;
        ArrayList<Rectangle> allBorders = new ArrayList<Rectangle>();
        for (PathElement passableRectangle : pathElements) {
            if (previous != null) {
                Rectangle rectangle = previous.getPassableRectangle().getBorder(passableRectangle.getPassableRectangle());
                if (rectangle.getWidth() > 0 && rectangle.getHeight() > 0) {
                    throw new IllegalArgumentException("Border mus be a line");
                }
                allBorders.add(rectangle);
            }
            previous = passableRectangle;
        }
        return allBorders;
    }

    public List<PathElement> getPathElements() {
        return pathElements;
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
            if (removed.isHasAlternatives()) {
                return removed.getRank();
            }
        }
        throw new IllegalStateException("Path can not be backtracked");
    }
}
