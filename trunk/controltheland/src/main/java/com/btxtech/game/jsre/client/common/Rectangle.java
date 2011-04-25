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

package com.btxtech.game.jsre.client.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * User: beat
 * Date: May 23, 2009
 * Time: 11:38:26 AM
 */
public class Rectangle implements Serializable {
    private Index start;
    private Index end;

    /**
     * Used by GWT
     */
    Rectangle() {
    }

    public Rectangle(Index start, Index end) {
        if (start.getX() > end.getX() || start.getY() > end.getY()) {
            throw new IllegalArgumentException("Invalid rectangle");
        }
        this.start = start.getCopy();
        this.end = end.getCopy();
    }

    public Rectangle(int xStart, int yStart, int width, int height) {
        this.start = new Index(xStart, yStart);
        this.end = new Index(xStart + width, yStart + height);
    }

    public void replace(Rectangle target) {
        start = target.start.getCopy();
        end = target.end.getCopy();
    }

    public Index getStart() {
        return start.getCopy();
    }

    public Index getEnd() {
        return end.getCopy();
    }

    /**
     * Returns true if the given position is in the rectangle or adjoins the rectangle
     *
     * @param position to check
     * @return true if adjoins or contains position
     */
    public boolean contains(Index position) { // TODO rename: adjoinsOrContains
        return position != null && position.getX() + 1 >= start.getX() && position.getY() + 1 >= start.getY() && position.getX() <= end.getX() && position.getY() <= end.getY();
    }

    /**
     * Returns true if the given position is in the rectangle. If the rectangle just adjoins it returns false.
     *
     * @param position to check
     * @return true if the position is inside the rectangle
     */
    public boolean containsExclusive(Index position) { // TODO rename: contains
        return position.getX() >= start.getX() && position.getY() >= start.getY() && position.getX() < end.getX() && position.getY() < end.getY();
    }

    public boolean adjoins(Rectangle rectangle) {
        int startX = Math.max(start.getX(), rectangle.start.getX());
        int startY = Math.max(start.getY(), rectangle.start.getY());

        int endX = Math.min(end.getX(), rectangle.end.getX());
        int endY = Math.min(end.getY(), rectangle.end.getY());

        return startX <= endX && startY <= endY;
    }

    public boolean adjoinsEclusive(Rectangle rectangle) {
        int startX = Math.max(start.getX(), rectangle.start.getX());
        int startY = Math.max(start.getY(), rectangle.start.getY());

        int endX = Math.min(end.getX(), rectangle.end.getX());
        int endY = Math.min(end.getY(), rectangle.end.getY());

        return startX < endX && startY < endY;
    }

    public Rectangle getCrossSection(Rectangle rectangle) {
        int startX = Math.max(start.getX(), rectangle.start.getX());
        int startY = Math.max(start.getY(), rectangle.start.getY());

        int endX = Math.min(end.getX(), rectangle.end.getX());
        int endY = Math.min(end.getY(), rectangle.end.getY());

        if (startX > endX || startY > endY) {
            throw new IllegalArgumentException("Rectangles do not overlap");
        }

        return new Rectangle(new Index(startX, startY), new Index(endX, endY));
    }


    public Rectangle copy() {
        return new Rectangle(start.getCopy(), end.getCopy());
    }

    public void growNorth(int size) {
        start.setY(start.getY() - size);
    }

    public void growEast(int size) {
        end.setX(end.getX() + size);
    }

    public void growSouth(int size) {
        end.setY(end.getY() + size);
    }

    public void growWest(int size) {
        start.setX(start.getX() - size);
    }

    public void shift(int deltaX, int deltaY) {
        start = start.add(deltaX, deltaY);
        end = end.add(deltaX, deltaY);
    }

    public Rectangle moveTo(int absX, int absY) {
        return new Rectangle(absX, absY, getWidth(), getHeight());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Rectangle rectangle = (Rectangle) o;

        return !(end != null ? !end.equals(rectangle.end) : rectangle.end != null) && !(start != null ? !start.equals(rectangle.start) : rectangle.start != null);

    }

    @Override
    public int hashCode() {
        int result = start != null ? start.hashCode() : 0;
        result = 31 * result + (end != null ? end.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Start " + start + " End " + end + " Width: " + getWidth() + " Height: " + getHeight();
    }

    public int getWidth() {
        return end.getX() - start.getX();
    }

    public int getHeight() {
        return end.getY() - start.getY();
    }

    public void setWidth(int width) {
        end.setX(start.getX() + width);
    }

    public void setHeight(int height) {
        end.setY(start.getY() + height);
    }

    public int getX() {
        return start.getX();
    }

    public int getY() {
        return start.getY();
    }

    public void setX(int x) {
        start.setX(x);
    }

    public void setY(int y) {
        start.setY(y);
    }

    public int getEndX() {
        return end.getX();
    }

    public int getEndY() {
        return end.getY();
    }

    public void setEndX(int x) {
        end.setX(x);
    }

    public void setEndY(int y) {
        end.setY(y);
    }

    public Index getCenter() {
        int centerX = (end.getX() - start.getX()) / 2;
        int centerY = (end.getY() - start.getY()) / 2;
        return new Index(start.getX() + centerX, start.getY() + centerY);
    }

    public Index getNearestPoint(Index point) {
        // Fist check end point
        if (point.getX() <= start.getX() && point.getY() <= start.getY()) {
            return start.getCopy();
        } else if (point.getX() >= end.getX() && point.getY() >= end.getY()) {
            return end.getCopy();
        } else if (point.getX() <= start.getX() && point.getY() >= end.getY()) {
            return new Index(start.getX(), end.getY());
        } else if (point.getX() >= end.getX() && point.getY() <= start.getY()) {
            return new Index(end.getX(), start.getY());
        }

        // Do projection
        if (point.getX() <= start.getX()) {
            return new Index(start.getX(), point.getY());
        } else if (point.getX() >= end.getX()) {
            return new Index(end.getX(), point.getY());
        } else if (point.getY() <= start.getY()) {
            return new Index(point.getX(), start.getY());
        } else if (point.getY() >= end.getY()) {
            return new Index(point.getX(), end.getY());
        }

        throw new IllegalArgumentException("The point is inside the rectangle");
    }

    public boolean hasMinSize(int minSize) {
        return getHeight() >= minSize || getWidth() >= minSize;
    }

    /**
     * Splits the rectangle into smaller rectangles.
     * The result rectangles will always have the given width and height. If the area of this rectangle is not width * height
     * the returning result will be rounded up (e.g. thr returned rectangles have a bigger area then this rectangle).
     *
     * @param width of the split tiles
     * @param height of the split tiles
     * @return Collection with split rectangles
     */
    public Collection<Rectangle> split(int width, int height) {
        ArrayList<Rectangle> split = new ArrayList<Rectangle>();
        int xCount = (int) Math.ceil((double) getWidth() / (double) width);
        int yCount = (int) Math.ceil((double) getHeight() / (double) height);
        for (int x = 0; x < xCount; x++) {
            for (int y = 0; y < yCount; y++) {
                split.add(new Rectangle(getStart().getX() + x * width, getStart().getY() + y * height, width, height));
            }
        }
        return split;
    }

    /**
     * @param center where this rectaggle is turned arount
     * @param angle  to turn thie rectaggle counterclockwise
     * @return the surrounding rectangle if this rectangle is truned
     */
    public Rectangle getSurroundedRectangle(Index center, double angle) {
        double sinus = Math.sin(angle);
        double cosinus = Math.cos(angle);
        Index p1 = start;
        Index p2 = new Index(end.getX(), start.getY());
        Index p3 = end;
        Index p4 = new Index(start.getX(), end.getY());
        Index newP1 = p1.rotateCounterClock(center, sinus, cosinus);
        Index newP2 = p2.rotateCounterClock(center, sinus, cosinus);
        Index newP3 = p3.rotateCounterClock(center, sinus, cosinus);
        Index newP4 = p4.rotateCounterClock(center, sinus, cosinus);
        return generateRectangleFromAnyPoints(newP1, newP2, newP3, newP4);
    }

    public static Rectangle generateRectangleFromAnyPoints(Index point1, Index point2) {
        Index start = point1.getSmallestPoint(point2);
        Index end = point1.getLargestPoint(point2);
        return new Rectangle(start, end);
    }

    public static Rectangle generateRectangleFromAnyPoints(Index point1, Index point2, Index point3, Index point4) {
        Index start = point1.getSmallestPoint(point2);
        start = start.getSmallestPoint(point3);
        start = start.getSmallestPoint(point4);
        Index end = point1.getLargestPoint(point2);
        end = end.getLargestPoint(point3);
        end = end.getLargestPoint(point4);
        return new Rectangle(start, end);
    }

    public static boolean contains(int x, int y, int width, int height, Index position) {
        return position != null && position.getX() >= x && position.getY() >= y && position.getX() <= x + width && position.getY() <= y + height;
    }
}
