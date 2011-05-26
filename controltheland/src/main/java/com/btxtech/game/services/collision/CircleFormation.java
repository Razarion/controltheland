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
import com.btxtech.game.jsre.common.MathHelper;
import com.btxtech.game.jsre.common.gameengine.PositionCanNotBeFoundException;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;

import java.util.List;

/**
 * User: beat
 * Date: 15.05.2011
 * Time: 21:02:28
 */
public class CircleFormation {
    private static final int MAX_TRIES = 10000;
    private static final double OVERBOOKED_ANGEL = MathHelper.QUARTER_EIGHT;

    private enum Mode {
        FINDING_START_ANGEL,
        PLACING_AROUND_TARGET,
        OVERBOOKED
    }

    private Rectangle rectangle;
    private List<CircleFormationItem> circleFormationItems;
    private int tryCount;
    private double range;
    private Mode mode = Mode.FINDING_START_ANGEL;
    private int maxWidth;
    private int maxHeight;
    private boolean counterClockWise;
    private double startAngel;
    private double angelClockWise;
    private double angelCounterClockWise;
    private double nextAngel;
    private double overbookedAngel;
    private double overbookedRange;

    static public class CircleFormationItem {
        private SyncBaseItem syncBaseItem;
        private int range;
        private Index destinationHint;
        private boolean inRange;

        public CircleFormationItem(SyncBaseItem syncBaseItem, int range, Index destinationHint, boolean inRange) {
            this.syncBaseItem = syncBaseItem;
            this.range = range;
            this.destinationHint = destinationHint;
            this.inRange = inRange;
        }

        public CircleFormationItem(SyncBaseItem syncBaseItem, int range) {
            this.syncBaseItem = syncBaseItem;
            this.range = range;
        }

        public Index getDestinationHint() {
            return destinationHint;
        }

        public void setDestinationHint(Index destinationHint) {
            this.destinationHint = destinationHint;
        }

        public SyncBaseItem getSyncBaseItem() {
            return syncBaseItem;
        }

        public Rectangle getRectangle() {
            return syncBaseItem.getBaseItemType().getRectangle(destinationHint);
        }

        public boolean isInRange() {
            return inRange;
        }

        public void setInRange(boolean inRange) {
            this.inRange = inRange;
        }

        public int getRange() {
            return range;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CircleFormationItem)) return false;

            CircleFormationItem that = (CircleFormationItem) o;

            return syncBaseItem.equals(that.syncBaseItem);
        }

        @Override
        public int hashCode() {
            return syncBaseItem.hashCode();
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + " destinationHint: " + destinationHint + " inRange: " + inRange;
        }
    }

    public CircleFormation(Rectangle rectangle, double startAngel, List<CircleFormationItem> circleFormationItems) {
        this.rectangle = rectangle;
        this.circleFormationItems = circleFormationItems;
        this.startAngel = MathHelper.normaliseAngel(startAngel);
        nextAngel = this.startAngel;
        angelClockWise = this.startAngel;
        angelCounterClockWise = this.startAngel;

        int minRange = Integer.MAX_VALUE;
        for (CircleFormationItem circleFormationItem : circleFormationItems) {
            BaseItemType baseItemType = circleFormationItem.syncBaseItem.getBaseItemType();
            if (baseItemType.getWidth() > maxWidth) {
                maxWidth = baseItemType.getWidth();
            }
            if (baseItemType.getHeight() > maxHeight) {
                maxHeight = baseItemType.getHeight();
            }
            if (circleFormationItem.range < minRange) {
                minRange = circleFormationItem.range;
            }
        }
        range = maxHeight / 2 + minRange + rectangle.getHeight() / 2;
        overbookedAngel = MathHelper.normaliseAngel(this.startAngel - OVERBOOKED_ANGEL);
        overbookedRange = range + MathHelper.getPythagoras(maxWidth, maxHeight);
    }

    public boolean hasNext() {
        return !circleFormationItems.isEmpty();
    }

    public void lastAccepted() {
        circleFormationItems.remove(0);
    }

    public CircleFormationItem calculateNextEntry() {
        checkMaxTries(rectangle, circleFormationItems.get(0).syncBaseItem);
        switch (mode) {
            case FINDING_START_ANGEL:
                return placeAroundTargetAndFindStartAngel();
            case PLACING_AROUND_TARGET:
                return placeAroundTarget();
            case OVERBOOKED:
                return placeOverbooked();
            default:
                throw new IllegalArgumentException("Unknown mode: " + mode);
        }
    }

    private CircleFormationItem placeAroundTargetAndFindStartAngel() {
        CircleFormationItem circleFormationItem = circleFormationItems.get(0);
        circleFormationItem.inRange = true;
        circleFormationItem.destinationHint = rectangle.getCenter().getPointFromAngelToNord(startAngel, (int) range);
        mode = Mode.PLACING_AROUND_TARGET;
        return circleFormationItem;
    }

    private CircleFormationItem placeAroundTarget() {
        if (counterClockWise) {
            angelCounterClockWise = calculateNextRangeAndDistance(angelCounterClockWise, range, counterClockWise);
            nextAngel = angelCounterClockWise;
        } else {
            angelClockWise = calculateNextRangeAndDistance(angelClockWise, range, counterClockWise);
            nextAngel = angelClockWise;
        }
        if (checkOverbooked()) {
            mode = Mode.OVERBOOKED;
            return placeOverbooked();
        }
        counterClockWise = !counterClockWise;
        CircleFormationItem circleFormationItem = circleFormationItems.get(0);
        circleFormationItem.inRange = true;
        circleFormationItem.destinationHint = rectangle.getCenter().getPointFromAngelToNord(nextAngel, (int) range);
        return circleFormationItem;
    }

    private CircleFormationItem placeOverbooked() {
        overbookedAngel = calculateNextRangeAndDistance(overbookedAngel, overbookedRange, true);
        CircleFormationItem circleFormationItem = circleFormationItems.get(0);
        circleFormationItem.destinationHint = rectangle.getCenter().getPointFromAngelToNord(overbookedAngel, (int) overbookedRange);
        circleFormationItem.inRange = false;
        if (!MathHelper.isInSection(overbookedAngel, startAngel - OVERBOOKED_ANGEL, startAngel + OVERBOOKED_ANGEL)) {
            overbookedAngel = MathHelper.normaliseAngel(startAngel - OVERBOOKED_ANGEL);
            overbookedRange += MathHelper.getPythagoras(maxWidth, maxHeight);
        }
        return circleFormationItem;
    }

    private boolean checkOverbooked() {
        Index counterPoint = rectangle.getCenter().getPointFromAngelToNord(angelCounterClockWise, (int) range);
        Rectangle counterRectangle = Rectangle.generateRectangleFromMiddlePoint(counterPoint, maxWidth, maxHeight);
        Index point = rectangle.getCenter().getPointFromAngelToNord(angelClockWise, (int) range);
        return counterRectangle.contains(point);
    }

    private void checkMaxTries(Rectangle target, SyncBaseItem itemToPlace) {
        tryCount++;
        if (tryCount > MAX_TRIES) {
            if (mode == Mode.OVERBOOKED) {
                throw new PositionCanNotBeFoundException(target, itemToPlace);
            } else {
                mode = Mode.OVERBOOKED;
                tryCount = 0;
            }
        }
    }

    private double calculateNextRangeAndDistance(double angel, double range, boolean counterClockWise) {
        angel = MathHelper.normaliseAngel(angel);

        double deltaToVertically = Math.sin(angel) * range;
        double deltaToHorizontally = Math.cos(angel) * range;

        double newDeltaToVertically;
        double newDeltaToHorizontally;

        double verticalAngel;
        double horizontalAngel;

        if (angel < MathHelper.NORTH_WEST || angel >= MathHelper.NORTH_EAST) {
            // North
            newDeltaToHorizontally = deltaToHorizontally - maxHeight;
            if (counterClockWise) {
                newDeltaToVertically = deltaToVertically + maxWidth;
                horizontalAngel = Math.acos(newDeltaToHorizontally / range); // Corner
            } else {
                newDeltaToVertically = deltaToVertically - maxWidth;
                horizontalAngel = -Math.acos(newDeltaToHorizontally / range); // Corner
            }
            verticalAngel = Math.asin(newDeltaToVertically / range); // Normal
        } else if (MathHelper.NORTH_WEST <= angel && angel < MathHelper.SOUTH_WEST) {
            // West
            newDeltaToVertically = deltaToVertically - maxWidth;
            if (counterClockWise) {
                newDeltaToHorizontally = deltaToHorizontally - maxHeight;
                verticalAngel = MathHelper.SOUTH - Math.asin(newDeltaToVertically / range); // Corner
            } else {
                newDeltaToHorizontally = deltaToHorizontally + maxHeight;
                verticalAngel = Math.asin(newDeltaToVertically / range); // Corner
            }
            horizontalAngel = Math.acos(newDeltaToHorizontally / range); // Normal
        } else if (MathHelper.SOUTH_WEST <= angel && angel < MathHelper.SOUTH_EAST) {
            // South
            newDeltaToHorizontally = deltaToHorizontally + maxHeight;
            if (counterClockWise) {
                newDeltaToVertically = deltaToVertically - maxWidth;
                horizontalAngel = -Math.acos(newDeltaToHorizontally / range); // Corner
            } else {
                newDeltaToVertically = deltaToVertically + maxWidth;
                horizontalAngel = Math.acos(newDeltaToHorizontally / range); // Corner
            }
            verticalAngel = MathHelper.SOUTH - Math.asin(newDeltaToVertically / range); // Normal
        } else {
            // East
            newDeltaToVertically = deltaToVertically + maxWidth;
            if (counterClockWise) {
                newDeltaToHorizontally = deltaToHorizontally + maxHeight;
                verticalAngel = Math.asin(newDeltaToVertically / range); // Corner
            } else {
                newDeltaToHorizontally = deltaToHorizontally - maxHeight;
                verticalAngel = MathHelper.SOUTH - Math.asin(newDeltaToVertically / range); // Corner
            }
            horizontalAngel = -Math.acos(newDeltaToHorizontally / range);  // Normal
        }


        double newAngel;
        if (Double.isNaN(verticalAngel) && Double.isNaN(horizontalAngel)) {
            throw new IllegalStateException("Both angels are NAN");
        } else if (Double.isNaN(verticalAngel)) {
            newAngel = horizontalAngel;
        } else if (Double.isNaN(horizontalAngel)) {
            newAngel = verticalAngel;
        } else {
            newAngel = MathHelper.closerToAngel(angel, verticalAngel, horizontalAngel);
        }

        return MathHelper.normaliseAngel(newAngel);
    }
}
