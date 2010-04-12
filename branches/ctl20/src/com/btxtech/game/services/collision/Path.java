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
    private List<PassableRectangle> rectanglePath;

    public Path(Path path) {
        rectanglePath = new ArrayList<PassableRectangle>(path.rectanglePath);
    }

    public Path() {
        rectanglePath = new ArrayList<PassableRectangle>();
    }

    public void add(PassableRectangle passableRectangle) {
        rectanglePath.add(passableRectangle);
    }

    public boolean contains(PassableRectangle passableRectangle) {
        return rectanglePath.contains(passableRectangle);
    }


    public Path createSubPath() {
        return new Path(this);
    }

    public PassableRectangle getTail() {
       return rectanglePath.get(rectanglePath.size() - 1);
    }

    public List<Rectangle> getAllPassableBorders() {
        PassableRectangle previous = null;
        ArrayList<Rectangle> allBorders = new ArrayList<Rectangle>();
        for (PassableRectangle passableRectangle : rectanglePath) {
            if (previous != null) {
                Rectangle rectangle = previous.getBorder(passableRectangle);
                if(rectangle.getWidth() > 0 && rectangle.getHeight() > 0) {
                    throw new IllegalArgumentException("Border mus be a line");
                }
                allBorders.add(rectangle);
            }
            previous = passableRectangle;
        }
        return allBorders;
    }

    public List<PassableRectangle> getRectanglePath() {
        return rectanglePath;
    }
    
}
