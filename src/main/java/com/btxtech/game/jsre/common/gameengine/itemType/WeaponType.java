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
    private int damage;
    private double reloadTime;
    private Collection<Integer> allowedItemTypes;
    // Pixel per second
    private Integer projectileSpeed;
    // dimension 1: muzzle nr, dimension 2: image nr
    private Index[][] muzzleFlashPositions;
    private Integer muzzleFlashClipId;
    private Integer projectileClipId;
    private Integer projectileDetonationClipId;

    /**
     * Used by GWT
     */
    WeaponType() {
    }

    public WeaponType(int range, Integer projectileSpeed, int damage, double reloadTime, Integer muzzleFlashClipId, Integer projectileClipId, Integer projectileDetonationClipId, Collection<Integer> allowedItemTypes, Index[][] muzzleFlashPositions) {
        this.range = range;
        this.projectileSpeed = projectileSpeed;
        this.damage = damage;
        this.reloadTime = reloadTime;
        this.muzzleFlashClipId = muzzleFlashClipId;
        this.projectileClipId = projectileClipId;
        this.projectileDetonationClipId = projectileDetonationClipId;
        this.allowedItemTypes = allowedItemTypes;
        this.muzzleFlashPositions = muzzleFlashPositions;
    }

    public void changeTo(WeaponType weaponType) {
        range = weaponType.range;
        projectileSpeed = weaponType.projectileSpeed;
        damage = weaponType.damage;
        reloadTime = weaponType.reloadTime;
        muzzleFlashClipId = weaponType.muzzleFlashClipId;
        projectileClipId = weaponType.projectileClipId;
        projectileDetonationClipId = weaponType.projectileDetonationClipId;
        allowedItemTypes = weaponType.allowedItemTypes;
        muzzleFlashPositions = weaponType.muzzleFlashPositions;
    }

    public int getRange() {
        return range;
    }

    public Integer getProjectileSpeed() {
        return projectileSpeed;
    }

    public double getDamage() {
        return damage;
    }

    public double getReloadTime() {
        return reloadTime;
    }

    public Integer getMuzzleFlashClipId() {
        return muzzleFlashClipId;
    }

    public Integer getProjectileClipId() {
        return projectileClipId;
    }

    public Integer getProjectileDetonationClipId() {
        return projectileDetonationClipId;
    }

    public boolean isItemTypeAllowed(int itemTypeId) {
        return allowedItemTypes.contains(itemTypeId);
    }

    public Index getMuzzleFlashPosition(int muzzleNr, int angelIndex) {
        return muzzleFlashPositions[muzzleNr][angelIndex];
    }

    public void setMuzzleFlashPosition(int muzzleNr, int angelIndex, Index position) {
        muzzleFlashPositions[muzzleNr][angelIndex] = position;
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
