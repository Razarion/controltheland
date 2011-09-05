package com.btxtech.game.jsre.common.gameengine.syncObjects;

import com.btxtech.game.jsre.client.common.DecimalPosition;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Line;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.syncInfos.SyncItemInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User: beat
 * Date: 16.08.2011
 * Time: 21:13:43
 */
public class SyncItemArea {
    private DecimalPosition position;
    private double angel;
    private SyncItem syncItem;
    private BoundingBox boundingBox;

    public SyncItemArea(SyncItem syncItem) {
        this.syncItem = syncItem;
        boundingBox = syncItem.getItemType().getBoundingBox();
    }

    public SyncItemArea(BoundingBox boundingBox, Index position) {
        this.boundingBox = boundingBox;
        setPosition(position);
        angel = 0;
    }

    public Index getPosition() {
        if (position != null) {
            return position.getPosition();
        } else {
            return null;
        }
    }

    public DecimalPosition getDecimalPosition() {
        return position;
    }

    private void checkPosition() {
        if (position != null) {
            if (position.getX() < 0 || position.getY() < 0) {
                throw new IllegalStateException("Position is not allowed to be negative: " + position + " SyncItem: " + syncItem);
            }
        }
    }

    public void setPosition(Index position) {
        if (position != null) {
            if (this.position != null) {
                this.position.setPosition(position);
            } else {
                this.position = new DecimalPosition(position);
            }
        } else {
            this.position = null;
        }
        checkPosition();
        if (syncItem != null) {
            syncItem.fireItemChanged(SyncItemListener.Change.POSITION);
        }

    }

    public void setDecimalPosition(DecimalPosition decimalPoint) {
        position = decimalPoint;
        checkPosition();
        if (syncItem != null) {
            syncItem.fireItemChanged(SyncItemListener.Change.POSITION);
        }
    }

    public double getAngel() {
        return angel;
    }

    public void setAngel(double angel) {
        this.angel = angel;
        if (syncItem != null) {
            syncItem.fireItemChanged(SyncItemListener.Change.ANGEL);
        }
    }

    public void correctPosition() {
        if (syncItem != null && syncItem.getServices() != null) {
            setPosition(syncItem.getServices().getTerrainService().correctPosition(syncItem, getPosition()));
        }
    }

    public void synchronize(SyncItemInfo syncItemInfo) {
        setPosition(syncItemInfo.getPosition());
        if (getBoundingBox().isTurnable()) {
            setAngel(syncItemInfo.getAngel());
        }
    }

    public void fillSyncItemInfo(SyncItemInfo syncItemInfo) {
        syncItemInfo.setPosition(getPosition());
        if (getBoundingBox().isTurnable()) {
            syncItemInfo.setAngel(getAngel());
        }
    }

    public boolean hasPosition() {
        return position != null;
    }

    public void turnTo(double angel) {
        if (angel != this.angel) {
            this.angel = angel;
            if (syncItem != null) {
                syncItem.fireItemChanged(SyncItemListener.Change.ANGEL);
            }
        }
    }

    public void turnTo(Index destination) {
        if (destination.equals(getPosition())) {
            return;
        }

        turnTo(getTurnToAngel(destination));
    }

    public void turnTo(SyncItem target) {
        if (contains(target.getSyncItemArea())) {
            turnTo(target.getSyncItemArea().getPosition());
            return;
        }

        turnTo(getTurnToAngel(target.getSyncItemArea()));
    }

    public double getTurnToAngel(Index destination) {
        if (destination.equals(getPosition())) {
            return getBoundingBox().getCosmeticAngel();
        }

        return getPosition().getAngleToNord(destination);
    }

    public double getTurnToAngel(SyncItemArea target) {
        if (contains(target)) {
            turnTo(target.getPosition());
            return getBoundingBox().getCosmeticAngel();
        }
        Index rotPosition = getPosition().rotateCounterClock(target.getPosition(), -target.getAngel());
        Rectangle targetRectangle = target.getBoundingBox().getRectangle(target.getPosition());

        Index rotPosOnRect = targetRectangle.getNearestPoint(rotPosition);
        Index posOnRect = rotPosOnRect.rotateCounterClock(target.getPosition(), target.getAngel());

        return getTurnToAngel(posOnRect);
    }

    public Collection<Line> getLines() {
        Index p1 = getCorner1();
        Index p2 = getCorner2();
        Index p3 = getCorner3();
        Index p4 = getCorner4();
        List<Line> lines = new ArrayList<Line>();
        lines.add(new Line(p1, p2));
        lines.add(new Line(p2, p3));
        lines.add(new Line(p3, p4));
        lines.add(new Line(p4, p1));
        return lines;
    }


    public boolean contains(SyncItemArea syncItemArea) {
        Collection<Line> otherLines = syncItemArea.getLines();

        for (Line line : getLines()) {
            for (Line otherLine : otherLines) {
                if (line.getCross(otherLine) != null) {
                    return true;
                }
            }
        }

        // Check completely inside
        if (getBoundingBox().getArea() < syncItemArea.getBoundingBox().getArea()) {
            return syncItemArea.contains(getPosition());
        } else {
            return contains(syncItemArea.getPosition());
        }
    }

    private boolean contains(Index position) {
        Index rotPoint = position.rotateCounterClock(getPosition(), -getAngel());
        return getBoundingBox().contains(getPosition(), rotPoint);
    }

    public boolean contains(SyncItem syncItem) {
        return contains(syncItem.getSyncItemArea());
    }

    public boolean contains(SyncItem syncItem, Index positionToCheck) {
        return contains(syncItem.getSyncItemArea().getBoundingBox().createSyntheticSyncItemArea(positionToCheck));
    }

    public boolean contains(Rectangle rectangle) {
        Collection<Line> lines = getLines();
        for (Line line : rectangle.getLines()) {
            for (Line otherLine : lines) {
                if (line.getCross(otherLine) != null) {
                    return true;
                }
            }
        }
        // Check completely inside
        if (getBoundingBox().getArea() < rectangle.getArea()) {
            return rectangle.containsExclusive(getPosition());
        } else {
            return contains(rectangle.getCenter());
        }
    }

    public boolean contains(BoundingBox boundingBox, Index positionToCheck) {
        return contains(boundingBox.createSyntheticSyncItemArea(positionToCheck));
    }

    public boolean positionReached(Index destination) {
        return getPosition().equals(destination);
    }

    public int getDistance(Index position) {
        if (contains(position)) {
            return 0;
        }
        Index rotPos = position.rotateCounterClock(getPosition(), -getAngel());
        Index nearestPoint = getBoundingBox().getRectangle(getPosition()).getNearestPoint(rotPos);
        return position.getDistance(nearestPoint);
    }

    public int getDistance(SyncItem syncItem) {
        return getDistance(syncItem.getSyncItemArea());
    }

    public int getDistance(SyncItemArea syncItemArea) {
        if (contains(syncItemArea)) {
            return 0;
        }
        Index otherP1 = syncItemArea.getCorner1().rotateCounterClock(getPosition(), -getAngel());
        Index otherP2 = syncItemArea.getCorner2().rotateCounterClock(getPosition(), -getAngel());
        Index otherP3 = syncItemArea.getCorner3().rotateCounterClock(getPosition(), -getAngel());
        Index otherP4 = syncItemArea.getCorner4().rotateCounterClock(getPosition(), -getAngel());

        Rectangle rectangle = getBoundingBox().getRectangle(getPosition());

        int d1 = rectangle.getShortestDistanceToLine(otherP1, otherP2);
        int d2 = rectangle.getShortestDistanceToLine(otherP2, otherP3);
        int d3 = rectangle.getShortestDistanceToLine(otherP3, otherP4);
        int d4 = rectangle.getShortestDistanceToLine(otherP4, otherP1);

        return Math.min(Math.min(d1, d2), Math.min(d3, d4));
    }

    public boolean isInRange(int range, SyncItem target) {
        return range >= getDistance(target);
    }

    public boolean isInRange(int range, Index position, ItemType toBeBuiltType) {
        return isInRange(range, toBeBuiltType.getBoundingBox().createSyntheticSyncItemArea(position));
    }

    public boolean isInRange(int range, Index position) {
        return range >= getDistance(position);
    }

    public boolean isInRange(int range, SyncItemArea syncItemArea) {
        System.out.println("getDistance: " + getDistance(syncItemArea));
        return range >= getDistance(syncItemArea);
    }

    public void setCosmeticsAngel() {
        setAngel(getBoundingBox().getCosmeticAngel());
    }

    public Index getCorner1() {
        return getPosition().add(getBoundingBox().getCorner1()).rotateCounterClock(getPosition(), angel);
    }

    public Index getCorner2() {
        return getPosition().add(getBoundingBox().getCorner2()).rotateCounterClock(getPosition(), angel);
    }

    public Index getCorner3() {
        return getPosition().add(getBoundingBox().getCorner3()).rotateCounterClock(getPosition(), angel);
    }

    public Index getCorner4() {
        return getPosition().add(getBoundingBox().getCorner4()).rotateCounterClock(getPosition(), angel);
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    @Override
    public String toString() {
        return " SyncItemArea: " + getPosition() + " angel: " + angel + " " + boundingBox;
    }
}
