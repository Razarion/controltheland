package com.btxtech.game.jsre.common.algorithm;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;

import java.util.Collection;

/**
 * User: beat
 * Date: 09.09.12
 * Time: 11:21
 */
public class RegionQuadTreeNode {
    private enum State {
        WITH, // Empty
        GREY, // Has child but not full
        BLACK // Full
    }

    private Rectangle boundary;
    private RegionQuadTreeNode northWest;
    private RegionQuadTreeNode northEast;
    private RegionQuadTreeNode southWest;
    private RegionQuadTreeNode southEast;
    private State state = State.WITH;

    public RegionQuadTreeNode(Rectangle boundary) {
        this.boundary = boundary;
        if(boundary.getWidth() == 1 && boundary.getHeight() == 1) {
            state = State.BLACK;
        }
    }

    public boolean insert(Index point) {
        if (!boundary.containsExclusive(point)) {
            return false;
        }
        if (state == State.BLACK) {
            return true;
        }
        if (boundary.getWidth() > 2) {
            if (northWest == null) {
                split();
            }
            addPointToChildNode(point);
        } else {
            Index offset = point.sub(boundary.getStart());
            if (offset.getX() == 0 && offset.getY() == 0) {
                northWest = new RegionQuadTreeNode(new Rectangle(point.getX(), point.getY(), 1, 1));
            } else if (offset.getX() == 1 && offset.getY() == 0) {
                northEast = new RegionQuadTreeNode(new Rectangle(point.getX(), point.getY(), 1, 1));
            } else if (offset.getX() == 1 && offset.getY() == 1) {
                southEast = new RegionQuadTreeNode(new Rectangle(point.getX(), point.getY(), 1, 1));
            } else if (offset.getX() == 0 && offset.getY() == 1) {
                southWest = new RegionQuadTreeNode(new Rectangle(point.getX(), point.getY(), 1, 1));
            } else {
                throw new IllegalArgumentException("RegionQuadTreeNode.insert() " + point + " " + this);
            }
        }
        optimaze();
        return true;
    }

    private void optimaze() {
        if (boundary.getWidth() > 2) {
            if (northWest.state == State.BLACK && northEast.state == State.BLACK && southEast.state == State.BLACK && southWest.state == State.BLACK) {
                state = State.BLACK;
            } else if (northWest.state == State.WITH && northEast.state == State.WITH && southEast.state == State.WITH && southWest.state == State.WITH) {
                state = State.WITH;
            } else {
                state = State.GREY;
            }
        } else {
            if (northWest != null && northEast != null && southEast != null && southWest != null) {
                state = State.BLACK;
            } else if (northWest == null && northEast == null && southEast == null && southWest == null) {
                state = State.WITH;
            } else {
                state = State.GREY;
            }
        }
        if (state == State.BLACK) {
            northWest = null;
            northEast = null;
            southEast = null;
            southWest = null;
        }
    }

    public boolean queryPointInside(Index point) {
        if (!boundary.containsExclusive(point)) {
            return false;
        }
        if (state == State.BLACK) {
            return true;
        }
        if (state == State.WITH) {
            return false;
        }
        if (northWest != null && northWest.queryPointInside(point)) {
            return true;
        }
        if (northEast != null && northEast.queryPointInside(point)) {
            return true;
        }
        if (southWest != null && southWest.queryPointInside(point)) {
            return true;
        }
        if (southEast != null && southEast.queryPointInside(point)) {
            return true;
        }
        return false;
    }

    protected void queryRegions(Collection<Rectangle> regions) {
        if (boundary.getWidth() > 2) {
            if (state == State.BLACK) {
                regions.add(boundary.copy());
            } else if (state == State.GREY) {
                northWest.queryRegions(regions);
                northEast.queryRegions(regions);
                southWest.queryRegions(regions);
                southEast.queryRegions(regions);
            }
        } else {
            if (northWest != null) {
                regions.add(northWest.boundary.copy());
            }
            if (northEast != null) {
                regions.add(northEast.boundary.copy());
            }
            if (southWest != null) {
                regions.add(southWest.boundary.copy());
            }
            if (southEast != null) {
                regions.add(southEast.boundary.copy());
            }
        }
    }

    @Override
    public String toString() {
        return "RegionQuadTreeNode{boundary=" + boundary + '}';
    }

    private void addPointToChildNode(Index point) {
        if (northWest.insert(point)) {
            return;
        } else if (northEast.insert(point)) {
            return;
        } else if (southWest.insert(point)) {
            return;
        } else if (southEast.insert(point)) {
            return;
        }
        throw new IllegalStateException("RegionQuadTreeNode.addPointToChildNode() All child nodes refuses to add point: " + point);
    }

    private void split() {
        if (northWest != null) {
            throw new IllegalStateException("RegionQuadTreeNode.subdivide() Region quad tree has already been divided");
        }
        int childWidth = boundary.getWidth() / 2;
        int childHeight = boundary.getWidth() / 2;
        // TODO rounding problems ???
        northWest = new RegionQuadTreeNode(new Rectangle(boundary.getStart().getX(), boundary.getStart().getY(), childWidth, childHeight));
        northEast = new RegionQuadTreeNode(new Rectangle(boundary.getStart().getX() + childWidth, boundary.getStart().getY(), childWidth, childHeight));
        southWest = new RegionQuadTreeNode(new Rectangle(boundary.getStart().getX() + childWidth, boundary.getStart().getY() + childHeight, childWidth, childHeight));
        southEast = new RegionQuadTreeNode(new Rectangle(boundary.getStart().getX(), boundary.getStart().getY() + childHeight, childWidth, childHeight));
    }
}
