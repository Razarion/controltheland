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

package com.btxtech.game.jsre.common.gameengine;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.MathHelper;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItemArea;

import java.util.List;

/**
 * User: beat
 * Date: 15.05.2011
 * Time: 21:02:28
 */
public class AttackFormation {
    private static final int MAX_TRIES = 10000;
    //private static final double WIDTH_FACTOR = 1.1;
    private static final double OVERBOOKED_ANGEL = MathHelper.QUARTER_EIGHT;

    private enum Mode {
        FINDING_START,
        PLACING_AROUND_TARGET,
        OVERBOOKED
    }

    private SyncItemArea target;
    private List<AttackFormationItem> attackFormationItems;
    private int tryCount;
    private Mode mode = Mode.FINDING_START;
    private int maxWidth;
    private boolean counterClockWise;
    private double startAngel;
    private AttackFormationTrack counterClockwiseTrack;
    private AttackFormationTrack clockwiseTrack;
    private double overbookedAngel;
    private int overbookedRange;
    private int maxDiameter;
    private int range = Integer.MAX_VALUE;

    static public class AttackFormationItem {
        private SyncBaseItem syncBaseItem;
        private int range;
        private Index destinationHint;
        private double destinationAngel;
        private boolean inRange;

        public AttackFormationItem(SyncBaseItem syncBaseItem, int range, Index destinationHint, double destinationAngel, boolean inRange) {
            this.syncBaseItem = syncBaseItem;
            this.range = range;
            this.destinationHint = destinationHint;
            this.destinationAngel = destinationAngel;
            this.inRange = inRange;
        }


        public AttackFormationItem(SyncBaseItem syncBaseItem, int range, Index destinationHint, boolean inRange) {
            this.syncBaseItem = syncBaseItem;
            this.range = range;
            this.destinationHint = destinationHint;
            this.inRange = inRange;
        }

        public AttackFormationItem(SyncBaseItem syncBaseItem, int range) {
            this.syncBaseItem = syncBaseItem;
            this.range = range;
        }

        public Index getDestinationHint() {
            return destinationHint;
        }

        public double getDestinationAngel() {
            return destinationAngel;
        }

        public SyncBaseItem getSyncBaseItem() {
            return syncBaseItem;
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
            if (!(o instanceof AttackFormationItem)) return false;

            AttackFormationItem that = (AttackFormationItem) o;

            return syncBaseItem.equals(that.syncBaseItem);
        }

        @Override
        public int hashCode() {
            return syncBaseItem.hashCode();
        }

        @Override
        public String toString() {
            return "AttackFormation destinationHint: " + destinationHint + " inRange: " + inRange + " destinationAngel: " + destinationAngel;
        }
    }

    public AttackFormation(SyncItemArea target, double startAngel, List<AttackFormationItem> attackFormationItems) {
        this.target = target;
        this.attackFormationItems = attackFormationItems;
        this.startAngel = MathHelper.normaliseAngel(startAngel);

        for (AttackFormationItem attackFormationItem : attackFormationItems) {
            BaseItemType baseItemType = attackFormationItem.syncBaseItem.getBaseItemType();
            if (baseItemType.getBoundingBox().getWidth() > maxWidth) {
                maxWidth = baseItemType.getBoundingBox().getWidth();
            }
            if (baseItemType.getBoundingBox().getMaxDiameter() > maxDiameter) {
                maxDiameter = baseItemType.getBoundingBox().getMaxDiameter();
            }
            if (attackFormationItem.range < range) {
                range = attackFormationItem.range;
            }
        }
        // TODO To make sure the items will not overlap. If the calculation would be correct, this is not needed.
        maxWidth += 10;
        // TODO To make sure items are always in range. If the calculation would be correct, this is not needed.
        range -= 2;

        overbookedAngel = MathHelper.normaliseAngel(this.startAngel - OVERBOOKED_ANGEL);
        overbookedRange = target.getBoundingBox().getMaxDiameter() + range + maxDiameter;

        counterClockwiseTrack = new AttackFormationTrack(target, range, true);
        clockwiseTrack = new AttackFormationTrack(target, range, false);
    }

    public boolean hasNext() {
        return !attackFormationItems.isEmpty();
    }

    public void lastAccepted() {
        attackFormationItems.remove(0);
    }

    public AttackFormationItem calculateNextEntry() {
        checkMaxTries(attackFormationItems.get(0).syncBaseItem);
        switch (mode) {
            case FINDING_START:
                return findStart();
            case PLACING_AROUND_TARGET:
                return placeNextAttacker();
            case OVERBOOKED:
                return placeOverbooked();
            default:
                throw new IllegalArgumentException("Unknown mode: " + mode);
        }
    }

    private AttackFormationItem findStart() {
        AttackFormationItem attackFormationItem = attackFormationItems.get(0);
        Index point = counterClockwiseTrack.start(startAngel, attackFormationItem.getSyncBaseItem().getSyncItemArea().getBoundingBox().getHeight() / 2);
        clockwiseTrack.start(startAngel, attackFormationItem.getSyncBaseItem().getSyncItemArea().getBoundingBox().getHeight() / 2);
        mode = Mode.PLACING_AROUND_TARGET;
        return returnNextAttackFormationItemInRange(point);
    }

    private AttackFormationItem placeNextAttacker() {
        AttackFormationItem attackFormationItem = attackFormationItems.get(0);
        Index point;
        if (counterClockWise) {
            point = counterClockwiseTrack.getNextPoint(maxWidth, attackFormationItem.getSyncBaseItem().getSyncItemArea().getBoundingBox().getHeight() / 2, maxWidth);
        } else {
            point = clockwiseTrack.getNextPoint(maxWidth, attackFormationItem.getSyncBaseItem().getSyncItemArea().getBoundingBox().getHeight() / 2, maxWidth);
        }
        if (checkOverbooked(point)) {
            mode = Mode.OVERBOOKED;
            return placeOverbooked();
        }
        counterClockWise = !counterClockWise;
        return returnNextAttackFormationItemInRange(point);
    }

    private AttackFormationItem returnNextAttackFormationItemInRange(Index point) {
        AttackFormationItem attackFormationItem = attackFormationItems.get(0);
        attackFormationItem.inRange = true;
        attackFormationItem.destinationHint = point;
        attackFormationItem.destinationAngel = attackFormationItem.getSyncBaseItem().getBaseItemType().getBoundingBox().createSyntheticSyncItemArea(point).getTurnToAngel(target);
        return attackFormationItem;
    }

    private AttackFormationItem placeOverbooked() {
        Index position = target.getPosition().getPointFromAngelToNord(overbookedAngel, overbookedRange);
        overbookedAngel += Math.atan((double) maxDiameter / (double) overbookedRange);
        if (overbookedAngel > this.startAngel + OVERBOOKED_ANGEL) {
            overbookedAngel = MathHelper.normaliseAngel(this.startAngel - OVERBOOKED_ANGEL);
            overbookedRange += maxDiameter;
        }
        AttackFormationItem attackFormationItem = attackFormationItems.get(0);
        attackFormationItem.destinationHint = position;
        attackFormationItem.inRange = false;
        return attackFormationItem;
    }

    private boolean checkOverbooked(Index point) {
        double angel = MathHelper.normaliseAngel(target.getPosition().getAngleToNord(point));
        if (counterClockWise) {
            double deltaAngel = Math.atan((double) maxWidth / 2.0 / (double) range);
            return !MathHelper.isInSection(angel + deltaAngel, startAngel, MathHelper.HALF_RADIANT);
        } else {
            return !MathHelper.isInSection(angel, startAngel, -MathHelper.HALF_RADIANT);
        }
    }

    private void checkMaxTries(SyncBaseItem itemToPlace) {
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

    public AttackFormationTrack getCounterClockwiseTrack() {
        return counterClockwiseTrack;
    }

    public AttackFormationTrack getClockwiseTrack() {
        return clockwiseTrack;
    }
}
