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
import java.util.List;

/**
 * User: beat
 * Date: May 23, 2009
 * Time: 11:38:26 AM
 */
public class Rectangle implements Serializable {
    private Index start;
    private Index endExclusive;

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
        this.endExclusive = end.getCopy();
    }

    public Rectangle(int xStart, int yStart, int width, int height) {
        this.start = new Index(xStart, yStart);
        this.endExclusive = new Index(xStart + width, yStart + height);
    }

    public void replace(Rectangle target) {
        start = target.start.getCopy();
        endExclusive = target.endExclusive.getCopy();
    }

    public Index getStart() {
        return start.getCopy();
    }

    public Index getEnd() {
        return endExclusive.getCopy();
    }

    /**
     * Returns true if the given position is in the rectangle or adjoins the rectangle
     *
     * @param position to check
     * @return true if adjoins or contains position
     */
    public boolean contains(Index position) { // TODO rename: adjoinsOrContains
        return position != null && position.getX() + 1 >= start.getX() && position.getY() + 1 >= start.getY() && position.getX() <= endExclusive.getX() && position.getY() <= endExclusive.getY();
    }

    /**
     * Returns true if the given position is in the rectangle the rectangle
     *
     * @param position to check
     * @return true if adjoins or contains position
     */
    public boolean contains2(Index position) { // TODO rename: ???
        return position != null && position.getX() >= start.getX() && position.getY() >= start.getY() && position.getX() <= endExclusive.getX() && position.getY() <= endExclusive.getY();
    }


    /**
     * Returns true if the given position is in the rectangle. If the rectangle just adjoins it returns false.
     *
     * @param position to check
     * @return true if the position is inside the rectangle
     */
    public boolean containsExclusive(Index position) { // TODO rename: contains
        if (position.getX() < start.getX() || position.getY() < start.getY()) {
            return false;
        }
        if (getWidth() > 0) {
            if (position.getX() >= endExclusive.getX()) {
                return false;
            }
        } else {
            if (position.getX() > endExclusive.getX()) {
                return false;
            }
        }

        if (getHeight() > 0) {
            if (position.getY() >= endExclusive.getY()) {
                return false;
            }
        } else {
            if (position.getY() > endExclusive.getY()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true if the given rectangle is in the rectangle or adjoins the rectangle
     *
     * @param rectangle to check
     * @return true if the position is inside the rectangle
     */
    public boolean adjoins(Rectangle rectangle) {
        int startX = Math.max(start.getX(), rectangle.start.getX());
        int startY = Math.max(start.getY(), rectangle.start.getY());

        int endX = Math.min(endExclusive.getX(), rectangle.endExclusive.getX());
        int endY = Math.min(endExclusive.getY(), rectangle.endExclusive.getY());

        return startX <= endX && startY <= endY;
    }

    /**
     * Returns true if the given rectangle overlaps this rectangle. If the rectangle just adjoins it returns false.
     *
     * @param rectangle to check
     * @return true if the position is inside the rectangle
     */
    public boolean adjoinsEclusive(Rectangle rectangle) {
        int startX = Math.max(start.getX(), rectangle.start.getX());
        int startY = Math.max(start.getY(), rectangle.start.getY());

        int endX = Math.min(endExclusive.getX(), rectangle.endExclusive.getX());
        int endY = Math.min(endExclusive.getY(), rectangle.endExclusive.getY());

        return startX < endX && startY < endY;
    }

    public Rectangle getCrossSection(Rectangle rectangle) {
        int startX = Math.max(start.getX(), rectangle.start.getX());
        int startY = Math.max(start.getY(), rectangle.start.getY());

        int endX = Math.min(endExclusive.getX(), rectangle.endExclusive.getX());
        int endY = Math.min(endExclusive.getY(), rectangle.endExclusive.getY());

        if (startX > endX || startY > endY) {
            throw new IllegalArgumentException("Rectangles do not overlap");
        }

        return new Rectangle(new Index(startX, startY), new Index(endX, endY));
    }

    /**
     * Checks if the line cuts this rectangle in any way. It returns also true if the line is inside the rectangle
     *
     * @param point1 point 1 of the line
     * @param point2 point 2 of the line
     * @return true if the line cuts this rectangle
     */
    public boolean doesLineCut(Index point1, Index point2) {
        if (containsExclusive(point1)) {
            return true;
        }
        if (containsExclusive(point2)) {
            return true;
        }

        int x1 = Math.min(point1.getX(), point2.getX());
        int x2 = Math.max(point1.getX(), point2.getX());
        int y1 = Math.min(point1.getY(), point2.getY());
        int y2 = Math.max(point1.getY(), point2.getY());

        // y = mx + c
        // x = (y-c)/m
        double m = (double) (point2.getY() - point1.getY()) / (double) (point2.getX() - point1.getX());
        double c = point1.getY() - (m * (double) point1.getX());

        double xNorth = Double.NaN;
        double xSouth = Double.NaN;
        double yWest = Double.NaN;
        double yEast = Double.NaN;
        if (Double.isInfinite(m)) {
            // Vertical line
            xNorth = x1;
            xSouth = x1;
        } else if (m == 0.0) {
            yWest = c;
            yEast = c;
        } else {
            xNorth = ((double) start.getY() - c) / m;
            xSouth = ((double) endExclusive.getY() - 1 - c) / m;
            yWest = m * (double) start.getX() + c;
            yEast = m * (double) endExclusive.getX() - 1 + c;
        }


        // Since both points are outside the rectangle, one crossed edged is enough.

        // Check north
        if (!Double.isNaN(xNorth) && start.getX() <= xNorth && xNorth < endExclusive.getX() && x1 <= xNorth && xNorth <= x2 && y2 > start.getY() && y1 < endExclusive.getY()) {
            return true;
        }
        // Check east
        if (!Double.isNaN(yWest) && start.getY() <= yWest && yWest < endExclusive.getY() && y1 <= yWest && yWest <= y2 && x2 > start.getX() && x1 < endExclusive.getX()) {
            return true;
        }
        // Check south
        if (!Double.isNaN(xSouth) && start.getX() <= xSouth && xSouth < endExclusive.getX() && x1 <= xSouth && xSouth <= x2 && y2 > start.getY() && y1 < endExclusive.getY()) {
            return true;
        }
        // Check west
        return !Double.isNaN(yEast) && start.getY() <= yEast && yEast < endExclusive.getY() && y2 <= yEast && yEast <= y2 && x2 > start.getX() && x1 < endExclusive.getX();
    }

    public List<Index> getCrossPointsExclusive(Line line) {
        List<Index> crossPoints = new ArrayList<Index>();
        Index crossPoint = getLine12().getCross(line);
        if (crossPoint != null) {
            crossPoints.add(crossPoint);
        }
        crossPoint = getLine23Exclusive().getCross(line);
        if (crossPoint != null) {
            crossPoints.add(crossPoint);
        }
        crossPoint = getLine34Exclusive().getCross(line);
        if (crossPoint != null) {
            crossPoints.add(crossPoint);
        }
        crossPoint = getLine41().getCross(line);
        if (crossPoint != null) {
            crossPoints.add(crossPoint);
        }
        if (crossPoints.size() > 2) {
            throw new IllegalStateException("A rectangle can not be crossed more then twice by a line");
        }
        return crossPoints;
    }


    /**
     * Returns the shortest distance to the line, end is inclusive
     *
     * @param point1 line point 1
     * @param point2 line point 2
     * @return the shortest distance
     */
    public double getShortestDistanceToLine(Index point1, Index point2) {
        if (doesLineCut(point1, point2)) {
            return 0;
        }

        Line line = new Line(point1, point2);
        double d1 = line.getShortestDistance(start);
        double d2 = line.getShortestDistance(new Index(getX(), getEndY()));
        double d3 = line.getShortestDistance(endExclusive);
        double d4 = line.getShortestDistance(new Index(getEndX(), getY()));

        double d5 = getNearestPointInclusive(point1).getDistanceDouble(point1);
        double d6 = getNearestPointInclusive(point2).getDistanceDouble(point2);

        return Math.min(Math.min(Math.min(d1, d2), Math.min(d3, d4)), Math.min(d5, d6));
    }


    public Rectangle copy() {
        return new Rectangle(start.getCopy(), endExclusive.getCopy());
    }

    public void growNorth(int size) {
        start.setY(start.getY() - size);
    }

    public void growEast(int size) {
        endExclusive.setX(endExclusive.getX() + size);
    }

    public void growSouth(int size) {
        endExclusive.setY(endExclusive.getY() + size);
    }

    public void growWest(int size) {
        start.setX(start.getX() - size);
    }

    public void shift(int deltaX, int deltaY) {
        shift(new Index(deltaX, deltaY));
    }

    public void shift(Index delta) {
        start = start.add(delta);
        endExclusive = endExclusive.add(delta);
    }

    public Rectangle moveTo(int absX, int absY) {
        return new Rectangle(absX, absY, getWidth(), getHeight());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Rectangle rectangle = (Rectangle) o;

        return !(endExclusive != null ? !endExclusive.equals(rectangle.endExclusive) : rectangle.endExclusive != null) && !(start != null ? !start.equals(rectangle.start) : rectangle.start != null);

    }

    @Override
    public int hashCode() {
        int result = start != null ? start.hashCode() : 0;
        result = 31 * result + (endExclusive != null ? endExclusive.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Start " + start + " End " + endExclusive + " Width: " + getWidth() + " Height: " + getHeight();
    }

    public int getWidth() {
        return endExclusive.getX() - start.getX();
    }

    public int getHeight() {
        return endExclusive.getY() - start.getY();
    }

    public void setWidth(int width) {
        endExclusive.setX(start.getX() + width);
    }

    public void setHeight(int height) {
        endExclusive.setY(start.getY() + height);
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
        return endExclusive.getX();
    }

    public int getEndY() {
        return endExclusive.getY();
    }

    public void setEndX(int x) {
        endExclusive.setX(x);
    }

    public void setEndY(int y) {
        endExclusive.setY(y);
    }

    public Index getCenter() {
        int centerX = (endExclusive.getX() - start.getX()) / 2;
        int centerY = (endExclusive.getY() - start.getY()) / 2;
        return new Index(start.getX() + centerX, start.getY() + centerY);
    }

    /**
     * Returns the nearest point on the rectangle. Endpoints are exclusive
     *
     * @param point input
     * @return result (exclusive)
     */
    public Index getNearestPoint(Index point) {
        // Fist check end point
        int endXCorrection = getWidth() > 0 ? 1 : 0;
        int endYCorrection = getHeight() > 0 ? 1 : 0;

        if (point.getX() <= start.getX() && point.getY() <= start.getY()) {
            return start.getCopy();
        } else if (point.getX() >= endExclusive.getX() && point.getY() >= endExclusive.getY()) {
            return endExclusive.sub(endXCorrection, endYCorrection);
        } else if (point.getX() <= start.getX() && point.getY() >= endExclusive.getY()) {
            return new Index(start.getX(), endExclusive.getY() - endYCorrection);
        } else if (point.getX() >= endExclusive.getX() && point.getY() <= start.getY()) {
            return new Index(endExclusive.getX() - endXCorrection, start.getY());
        }

        // Do projection
        if (point.getX() <= start.getX()) {
            return new Index(start.getX(), point.getY());
        } else if (point.getX() >= endExclusive.getX()) {
            return new Index(endExclusive.getX() - endXCorrection, point.getY());
        } else if (point.getY() <= start.getY()) {
            return new Index(point.getX(), start.getY());
        } else if (point.getY() >= endExclusive.getY()) {
            return new Index(point.getX(), endExclusive.getY() - endYCorrection);
        }

        throw new IllegalArgumentException("The point is inside the rectangle. Point: " + point + " rectangel: " + this);
    }

    /**
     * Returns the nearest point on the rectangle. Endpoints are inclusive
     *
     * @param point input
     * @return result (exclusive)
     */
    public Index getNearestPointInclusive(Index point) {
        if (point.getX() <= start.getX() && point.getY() <= start.getY()) {
            return start.getCopy();
        } else if (point.getX() >= endExclusive.getX() && point.getY() >= endExclusive.getY()) {
            return endExclusive.getCopy();
        } else if (point.getX() <= start.getX() && point.getY() >= endExclusive.getY()) {
            return new Index(start.getX(), endExclusive.getY());
        } else if (point.getX() >= endExclusive.getX() && point.getY() <= start.getY()) {
            return new Index(endExclusive.getX(), start.getY());
        }

        // Do projection
        if (point.getX() <= start.getX()) {
            return new Index(start.getX(), point.getY());
        } else if (point.getX() >= endExclusive.getX()) {
            return new Index(endExclusive.getX(), point.getY());
        } else if (point.getY() <= start.getY()) {
            return new Index(point.getX(), start.getY());
        } else if (point.getY() >= endExclusive.getY()) {
            return new Index(point.getX(), endExclusive.getY());
        }

        throw new IllegalArgumentException("The point is inside the rectangle");
    }

    public boolean hasMinSize(int minSize) {
        return getHeight() >= minSize || getWidth() >= minSize;
    }

    public boolean isEmpty() {
        return getHeight() == 0 && getWidth() == 0;
    }

    /**
     * Splits the rectangle into smaller rectangles.
     * The result rectangles will always have the given width and height. If the area of this rectangle is not width * height
     * the returning result will be rounded up (e.g. thr returned rectangles have a bigger area then this rectangle).
     *
     * @param width  of the split tiles
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

    public double getDiagonally() {
        return Math.sqrt(getWidth() * getWidth() + getHeight() * getHeight());
    }

    public double getHalfDiagonally() {
        return Math.sqrt(getWidth() * getWidth() + getHeight() * getHeight()) / 2.0;
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
        Index p2 = new Index(endExclusive.getX(), start.getY());
        Index p3 = endExclusive;
        Index p4 = new Index(start.getX(), endExclusive.getY());
        Index newP1 = p1.rotateCounterClock(center, sinus, cosinus);
        Index newP2 = p2.rotateCounterClock(center, sinus, cosinus);
        Index newP3 = p3.rotateCounterClock(center, sinus, cosinus);
        Index newP4 = p4.rotateCounterClock(center, sinus, cosinus);
        return generateRectangleFromAnyPoints(newP1, newP2, newP3, newP4);
    }

    public Index getCorner1() {
        return start;
    }

    public Index getCorner2() {
        return start.add(0, getHeight());
    }

    public Index getCorner3() {
        return endExclusive;
    }

    public Index getCorner4() {
        return start.add(getWidth(), 0);
    }

    public Line getLine12() {
        return new Line(getCorner1(), getCorner2());
    }

    public Line getLine23() {
        return new Line(getCorner2(), getCorner3());
    }

    public Line getLine23Exclusive() {
        return new Line(getCorner2().sub(0, 1), getCorner3().sub(1, 1));
    }

    public Line getLine34() {
        return new Line(getCorner3(), getCorner4());
    }

    public Line getLine34Exclusive() {
        return new Line(getCorner3().sub(1, 1), getCorner4().sub(1, 0));
    }

    public Line getLine41() {
        return new Line(getCorner4(), getCorner1());
    }

    public int getArea() {
        return getWidth() * getHeight();
    }

    public Collection<Line> getLines() {
        Collection<Line> lines = new ArrayList<Line>();
        lines.add(new Line(getCorner1(), getCorner2()));
        lines.add(new Line(getCorner2(), getCorner3()));
        lines.add(new Line(getCorner3(), getCorner4()));
        lines.add(new Line(getCorner4(), getCorner1()));
        return lines;
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

    public static Rectangle generateRectangleFromMiddlePoint(Index middlePoint, int width, int height) {
        Index start = middlePoint.sub(width / 2, height / 2);
        return new Rectangle(start.getX(), start.getY(), width, height);
    }

    public static boolean contains(int x, int y, int width, int height, Index position) {
        return position != null && position.getX() >= x && position.getY() >= y && position.getX() <= x + width && position.getY() <= y + height;
    }

    /**
     * Returns true if one rectangle call to adjoinsExclusive() returns true for one other rectangles in the given list.
     *
     * @param rectangles rectangle to check
     * @return if one rectangle adjoinsExclusively another rectangle
     */
    public static boolean adjoinsExclusive(Collection<Rectangle> rectangles) {
        List<Rectangle> rectanglesList = new ArrayList<Rectangle>(rectangles);
        while (!rectanglesList.isEmpty()) {
            Rectangle rectangle = rectanglesList.remove(0);
            for (Rectangle others : rectanglesList) {
                if (rectangle.adjoinsEclusive(others)) {
                    return true;
                }
            }
        }
        return false;
    }

    public String testString() {
        return "new Rectangle(" + getX() + ", " + getY() + ", " + getWidth() + ", " + getHeight() + ")";
    }
}
