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

package com.btxtech.game.jsre.common.gameengine.itemType;

import com.btxtech.game.jsre.client.common.Index;

import java.io.Serializable;
import java.util.Collection;

/**
 * User: beat
 * Date: 17.11.2009
 * Time: 23:13:22
 */
public class WeaponType implements Serializable {
    private int range;
    private int demage;
    private double reloadTime;
    private Integer soundId;
    private int muzzleFlashWidth;
    private int muzzleFlashLength;
    private boolean stretchMuzzleFlashToTarget;
    private Collection<Integer> allowedItemTypes;
    // dimension 1: muzzle nr, dimension 2: image nr
    private Index[][] muzzleFlashPositions;

    /**
     * Used by GWT
     */
    WeaponType() {
    }

    public WeaponType(int range, int demage, double reloadTime, Integer soundId, int muzzleFlashWidth, int muzzleFlashLength, boolean stretchMuzzleFlashToTarget, Collection<Integer> allowedItemTypes, Index[][] muzzleFlashPositions) {
        this.range = range;
        this.demage = demage;
        this.reloadTime = reloadTime;
        this.soundId = soundId;
        this.muzzleFlashWidth = muzzleFlashWidth;
        this.muzzleFlashLength = muzzleFlashLength;
        this.stretchMuzzleFlashToTarget = stretchMuzzleFlashToTarget;
        this.allowedItemTypes = allowedItemTypes;
        this.muzzleFlashPositions = muzzleFlashPositions;
    }

    public void changeTo(WeaponType weaponType) {
        range = weaponType.range;
        demage = weaponType.demage;
        reloadTime = weaponType.reloadTime;
        soundId = weaponType.soundId;
        muzzleFlashWidth = weaponType.muzzleFlashWidth;
        muzzleFlashLength = weaponType.muzzleFlashLength;
        stretchMuzzleFlashToTarget = weaponType.stretchMuzzleFlashToTarget;
        allowedItemTypes = weaponType.allowedItemTypes;
        muzzleFlashPositions = weaponType.muzzleFlashPositions;
    }

    public int getRange() {
        return range;
    }

    public double getDemage() {
        return demage;
    }

    public double getReloadTime() {
        return reloadTime;
    }

    public Integer getSoundId() {
        return soundId;
    }

    public int getMuzzleFlashWidth() {
        return muzzleFlashWidth;
    }

    public int getMuzzleFlashLength() {
        return muzzleFlashLength;
    }

    public boolean stretchMuzzleFlashToTarget() {
        return stretchMuzzleFlashToTarget;
    }

    public boolean isItemTypeAllowed(int itemTypeId) {
        return allowedItemTypes.contains(itemTypeId);
    }

    public Index getMuzzleFlashPosition(int muzzleNr, int imageNr) {
        return muzzleFlashPositions[muzzleNr][imageNr];
    }

    public void setMuzzleFlashPosition(int muzzleNr, int imageNr, Index position) {
        muzzleFlashPositions[muzzleNr][imageNr] = position;
    }

    public int getMuzzleFlashCount() {
        return muzzleFlashPositions.length;
    }

    public Index[][] getMuzzleFlashPositions() {
        return muzzleFlashPositions;
    }

    public void changeMuzzleFlashCount(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("Item must have at least one muzzle flash");
        }
        Index[][] saveMuzzleFlashPositions = muzzleFlashPositions;
        int imageCount = muzzleFlashPositions[0].length;
        muzzleFlashPositions = new Index[count][];
        for (int muzzleFireNr = 0; muzzleFireNr < count; muzzleFireNr++) {
            muzzleFlashPositions[muzzleFireNr] = new Index[imageCount];
            for (int imageNr = 0; imageNr < imageCount; imageNr++) {
                if (muzzleFireNr < saveMuzzleFlashPositions.length) {
                    muzzleFlashPositions[muzzleFireNr][imageNr] = saveMuzzleFlashPositions[muzzleFireNr][imageNr];
                } else {
                    muzzleFlashPositions[muzzleFireNr][imageNr] = new Index(0, 0);
                }
            }
        }
    }
}
