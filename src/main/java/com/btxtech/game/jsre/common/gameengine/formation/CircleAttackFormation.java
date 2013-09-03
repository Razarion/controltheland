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

package com.btxtech.game.jsre.common.gameengine.formation;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.MathHelper;
import com.btxtech.game.jsre.common.gameengine.PositionCanNotBeFoundException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItemArea;

import java.util.List;

/**
 * User: beat
 * Date: 15.05.2011
 * Time: 21:02:28
 */
public class CircleAttackFormation implements AttackFormation {
    private static final int MAX_TRIES = 10000;
    private static final double OVERBOOKED_ANGEL = MathHelper.EIGHTH_RADIANT;
    public static final double DISTANCE = 3.0;

    private enum Mode {
        FINDING_START,
        PLACING_AROUND_TARGET,
        OVERBOOKED
    }

    private SyncItemArea target;
    private SyncItemArea start;
    private List<AttackFormationItem> attackFormationItems;
    private int tryCount;
    private Mode mode = Mode.FINDING_START;
    private boolean counterClockWise;
    private double startAngel;
    private CircleAttackFormationTrack counterClockwiseTrackCircle;
    private CircleAttackFormationTrack clockwiseTrackCircle;
    private double overbookedAngel;
    private double overbookedDeltaAngel;
    private int overbookedRange;
    private int maxDiameter;

    CircleAttackFormation(SyncItemArea target, double startAngel, List<AttackFormationItem> attackFormationItems, int maxDiameter, int range) {
        this.target = target;
        this.attackFormationItems = attackFormationItems;
        this.maxDiameter = maxDiameter;
        this.startAngel = MathHelper.normaliseAngel(startAngel);

        overbookedAngel = MathHelper.normaliseAngel(this.startAngel - OVERBOOKED_ANGEL);
        overbookedRange = target.getBoundingBox().getDiameter() + range + maxDiameter + (int) DISTANCE;
        overbookedDeltaAngel = Math.atan((maxDiameter + DISTANCE) / 2.0 / overbookedRange) * 2.0;

        int totalRadius = range + target.getBoundingBox().getRadius();
        counterClockwiseTrackCircle = new CircleAttackFormationTrack(this.startAngel, target, totalRadius, true);
        clockwiseTrackCircle = new CircleAttackFormationTrack(this.startAngel, target, totalRadius, false);
    }

    @Override
    public boolean hasNext() {
        return !attackFormationItems.isEmpty();
    }

    @Override
    public void lastAccepted() {
        if (mode == Mode.FINDING_START) {
            counterClockwiseTrackCircle.setLast(start);
            clockwiseTrackCircle.setLast(start);
            mode = Mode.PLACING_AROUND_TARGET;
        }
        attackFormationItems.remove(0);
    }

    @Override
    public AttackFormationItem calculateNextEntry() {
        checkMaxTries(attackFormationItems.get(0).getSyncBaseItem());
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
        if (counterClockWise) {
            start = counterClockwiseTrackCircle.getStartPoint(attackFormationItem);
        } else {
            start = clockwiseTrackCircle.getStartPoint(attackFormationItem);
        }
        counterClockWise = !counterClockWise;
        return returnNextAttackFormationItemInRange(start);
    }

    private AttackFormationItem placeNextAttacker() {
        AttackFormationItem attackFormationItem = attackFormationItems.get(0);
        SyncItemArea syncItemArea;
        if (counterClockWise) {
            syncItemArea = counterClockwiseTrackCircle.getNextPoint(attackFormationItem);
        } else {
            syncItemArea = clockwiseTrackCircle.getNextPoint(attackFormationItem);
        }
        if (checkOverbooked()) {
            mode = Mode.OVERBOOKED;
            return placeOverbooked();
        }
        counterClockWise = !counterClockWise;
        return returnNextAttackFormationItemInRange(syncItemArea);
    }

    private AttackFormationItem returnNextAttackFormationItemInRange(SyncItemArea syncItemArea) {
        AttackFormationItem attackFormationItem = attackFormationItems.get(0);
        attackFormationItem.setInRange(true);
        attackFormationItem.setDestinationHint(syncItemArea.getPosition());
        attackFormationItem.setDestinationAngel(syncItemArea.getAngel());
        return attackFormationItem;
    }

    private AttackFormationItem placeOverbooked() {
        Index center = target.getPosition().getPointFromAngelToNord(overbookedAngel, overbookedRange);
        overbookedAngel += overbookedDeltaAngel;
        if (!MathHelper.isInSection(overbookedAngel, startAngel - MathHelper.EIGHTH_RADIANT, MathHelper.EIGHTH_RADIANT * 2.0)) {
            overbookedAngel = startAngel - MathHelper.EIGHTH_RADIANT;
            overbookedRange += maxDiameter + DISTANCE;
            overbookedDeltaAngel = Math.atan((maxDiameter + DISTANCE) / 2.0 / overbookedRange) * 2.0;
        }
        AttackFormationItem attackFormationItem = attackFormationItems.get(0);
        attackFormationItem.setDestinationHint(center);
        attackFormationItem.setInRange(false);
        return attackFormationItem;
    }

    private boolean checkOverbooked() {
        return counterClockwiseTrackCircle.getLast().contains(clockwiseTrackCircle.getLast());
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

    public CircleAttackFormationTrack getCounterClockwiseTrack() {
        return counterClockwiseTrackCircle;
    }

    public CircleAttackFormationTrack getClockwiseTrack() {
        return clockwiseTrackCircle;
    }
}
