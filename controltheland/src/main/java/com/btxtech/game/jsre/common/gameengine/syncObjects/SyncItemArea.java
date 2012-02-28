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
    private double angel = 0;
    private SyncItem syncItem;
    private BoundingBox boundingBox;

    public SyncItemArea(SyncItem syncItem) {
        this.syncItem = syncItem;
        boundingBox = syncItem.getItemType().getBoundingBox();
    }

    public SyncItemArea(BoundingBox boundingBox, Index position) {
        this.boundingBox = boundingBox;
        setPosition(position);
    }

    public SyncItemArea(SyncItemArea syncItemArea) {
        position = new DecimalPosition(syncItemArea.position);
        angel = syncItemArea.angel;
        boundingBox = syncItemArea.boundingBox;
    }

    public Index getPosition() {
        if (position != null) {
            return position.getPosition();
        } else {
            return null;
        }
    }

    public Index getTopLeftFromImagePosition() {
        return getPosition().sub(boundingBox.getMiddleFromImage());
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
        setPositionNoCheck(position);
        checkPosition();
        if (syncItem != null) {
            syncItem.fireItemChanged(SyncItemListener.Change.POSITION);
        }
    }

    public void setPositionNoCheck(Index position) {
        if (position != null) {
            if (this.position != null) {
                this.position.setPosition(position);
            } else {
                this.position = new DecimalPosition(position);
            }
        } else {
            this.position = null;
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
        if (syncItem != null && syncItem.getServices() != null && hasPosition()) {
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
        turnTo(target.getSyncItemArea());
    }

    public void turnTo(SyncItemArea target) {
        if (contains(target)) {
            turnTo(target.getPosition());
            return;
        }
        turnTo(getTurnToAngel(target));
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

        Index rotPosOnRect = targetRectangle.getNearestPointInclusive(rotPosition);
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
        if (!hasPosition()) {
            return false;
        }
        if (!syncItemArea.hasPosition()) {
            return false;
        }
        
        // Increase performance
        if(getPosition().getDistance(syncItemArea.getPosition()) > (getBoundingBox().getMaxRadiusDouble() + syncItemArea.getBoundingBox().getMaxRadiusDouble())) {
            return false;
        }

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

    /**
     * Move this SyncItemArea to the given position and
     *
     * @param syncItem        to check against
     * @param positionToCheck position to move this to
     * @param angel           angel move to. If null -> 0
     * @return true if contains
     */
    public boolean contains(SyncItem syncItem, Index positionToCheck, Double angel) {
        if (angel == null) {
            angel = 0.0;
        }
        return getBoundingBox().createSyntheticSyncItemArea(positionToCheck, angel).contains(syncItem);
    }

    /**
     * Move this SyncItemArea to the given position and
     *
     * @param syncItem        to check against
     * @param positionToCheck position to move this to
     * @return true if contains
     */
    public boolean contains(SyncItem syncItem, Index positionToCheck) {
        return getBoundingBox().createSyntheticSyncItemArea(positionToCheck).contains(syncItem);
    }

    /**
     * Check if this SyncItemArea will contains the given bounding box at the given position. The bounding box
     * is moved to the given position
     *
     * @param boundingBox     bounding box
     * @param positionToCheck position to check
     * @return true if contains
     */
    public boolean contains(BoundingBox boundingBox, Index positionToCheck) {
        return contains(boundingBox.createSyntheticSyncItemArea(positionToCheck));
    }

    public boolean contains(Rectangle rectangle) {
        if (!hasPosition()) {
            return false;
        }
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

    public boolean positionReached(Index destination) {
        return getPosition().equals(destination);
    }

    public double getDistance(Index position) {
        if (contains(position)) {
            return 0;
        }
        Index rotPos = position.rotateCounterClock(getPosition(), -getAngel());
        Index nearestPoint = getBoundingBox().getRectangle(getPosition()).getNearestPointInclusive(rotPos);
        return rotPos.getDistanceDouble(nearestPoint);
    }

    public double getDistance(SyncItem syncItem) {
        return getDistance(syncItem.getSyncItemArea());
    }

    public int getDistanceRounded(SyncItem syncItem) {
        return (int) Math.round(getDistance(syncItem.getSyncItemArea()));
    }

    public int getDistanceRounded(Index position) {
        return (int) Math.round(getDistance(position));
    }

    public int getDistanceRounded(SyncItemArea syncItemArea) {
        return (int) Math.round(getDistance(syncItemArea));
    }

    public double getDistance(SyncItemArea syncItemArea) {
        if (contains(syncItemArea)) {
            return 0;
        }
        Index otherP1 = syncItemArea.getCorner1().rotateCounterClock(getPosition(), -getAngel());
        Index otherP2 = syncItemArea.getCorner2().rotateCounterClock(getPosition(), -getAngel());
        Index otherP3 = syncItemArea.getCorner3().rotateCounterClock(getPosition(), -getAngel());
        Index otherP4 = syncItemArea.getCorner4().rotateCounterClock(getPosition(), -getAngel());

        Rectangle rectangle = getBoundingBox().getRectangle(getPosition());

        double d1 = rectangle.getShortestDistanceToLine(otherP1, otherP2);
        double d2 = rectangle.getShortestDistanceToLine(otherP2, otherP3);
        double d3 = rectangle.getShortestDistanceToLine(otherP3, otherP4);
        double d4 = rectangle.getShortestDistanceToLine(otherP4, otherP1);

        return Math.min(Math.min(d1, d2), Math.min(d3, d4));
    }

    public boolean isInRange(int range, SyncItem target) {
        return range >= getDistanceRounded(target);
    }

    public boolean isInRange(int range, Index position, ItemType toBeBuiltType) {
        return isInRange(range, toBeBuiltType.getBoundingBox().createSyntheticSyncItemArea(position));
    }

    public boolean isInRange(int range, Index position) {
        return range >= getDistanceRounded(position);
    }

    public boolean isInRange(int range, SyncItemArea syncItemArea) {
        return range >= getDistanceRounded(syncItemArea);
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
