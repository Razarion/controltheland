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

package com.btxtech.game.services.item.itemType;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.itemType.WeaponType;
import com.btxtech.game.services.common.ContentProvider;
import com.btxtech.game.services.common.ReadonlyCollectionContentProvider;
import com.btxtech.game.services.common.Utils;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * User: beat
 * Date: 09.12.2009
 * Time: 15:09:34
 */
@Entity(name = "ITEM_WEAPON_TYPE")
public class DbWeaponType implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(name = "theRange")
    private int range;
    private int damage;
    private double reloadTime;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DbItemTypeData dbSound;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DbItemTypeData dbMuzzleImage;
    @Column(nullable = false, columnDefinition = "INT default '0'")
    private int muzzleFlashWidth;
    @Column(nullable = false, columnDefinition = "INT default '0'")
    private int muzzleFlashLength;
    @Column(nullable = false)
    private boolean stretchMuzzleFlashToTarget;
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "ITEM_WEAPON_TYPE_ALLOWED_ITEM_TYPE",
            joinColumns = @JoinColumn(name = "weaponItemTypeId"),
            inverseJoinColumns = @JoinColumn(name = "allowedItemTypeId")
    )
    private Set<DbBaseItemType> allowedItemTypes;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "weaponType", orphanRemoval = true)
    private Collection<DbWeaponTypeMuzzle> muzzles;

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public double getReloadTime() {
        return reloadTime;
    }

    public void setReloadTime(double reloadTime) {
        this.reloadTime = reloadTime;
    }

    public DbItemTypeData getDbMuzzleImage() {
        return dbMuzzleImage;
    }

    public void setDbMuzzleImage(DbItemTypeData dbMuzzleImage) {
        this.dbMuzzleImage = dbMuzzleImage;
    }

    public int getMuzzleFlashWidth() {
        return muzzleFlashWidth;
    }

    public void setMuzzleFlashWidth(int muzzleFlashWidth) {
        this.muzzleFlashWidth = muzzleFlashWidth;
    }

    public int getMuzzleFlashLength() {
        return muzzleFlashLength;
    }

    public void setMuzzleFlashLength(int muzzleFlashLength) {
        this.muzzleFlashLength = muzzleFlashLength;
    }

    public DbItemTypeData getDbSound() {
        return dbSound;
    }

    public void setDbSound(DbItemTypeData dbSound) {
        this.dbSound = dbSound;
    }

    public boolean isStretchMuzzleFlashToTarget() {
        return stretchMuzzleFlashToTarget;
    }

    public void setStretchMuzzleFlashToTarget(boolean stretchMuzzleFlashToTarget) {
        this.stretchMuzzleFlashToTarget = stretchMuzzleFlashToTarget;
    }

    public ContentProvider<DbBaseItemType> getAllowedItemTypeCrud() {
        return new ReadonlyCollectionContentProvider<DbBaseItemType>(allowedItemTypes);
    }

    public WeaponType createWeaponType(int imageCount) {
        if (muzzles == null) {
            muzzles = new ArrayList<DbWeaponTypeMuzzle>();
        }
        int length = muzzles.size() == 0 ? 1 : muzzles.size();
        Index[][] muzzleFlashPositions = new Index[length][];
        for (int outerIndex = 0; outerIndex < length; outerIndex++) {
            muzzleFlashPositions[outerIndex] = new Index[imageCount];
            for (int innerIndex = 0; innerIndex < muzzleFlashPositions[outerIndex].length; innerIndex++) {
                muzzleFlashPositions[outerIndex][innerIndex] = new Index(0, 0);
            }
        }

        for (DbWeaponTypeMuzzle muzzle : muzzles) {
            Index[] positions = muzzleFlashPositions[muzzle.getMuzzleNumber()];
            for (DbWeaponTypeMuzzlePosition position : muzzle.getPositions()) {
                positions[position.getImageNumber()] = position.getPosition();
            }
        }

        return new WeaponType(range,
                damage,
                reloadTime,
                muzzleFlashWidth,
                muzzleFlashLength,
                stretchMuzzleFlashToTarget,
                Utils.dbBaseItemTypesToInts(allowedItemTypes),
                muzzleFlashPositions);
    }


    public void setMuzzleFlashPositions(Index[][] muzzleFlashPositions) {
        muzzles.clear();
        for (int muzzleIndex = 0, muzzleFlashPositionsLength = muzzleFlashPositions.length; muzzleIndex < muzzleFlashPositionsLength; muzzleIndex++) {
            DbWeaponTypeMuzzle dbWeaponTypeMuzzle = new DbWeaponTypeMuzzle();
            dbWeaponTypeMuzzle.setWeaponType(this);
            dbWeaponTypeMuzzle.setMuzzleNumber(muzzleIndex);
            dbWeaponTypeMuzzle.setPositions(new ArrayList<DbWeaponTypeMuzzlePosition>());
            muzzles.add(dbWeaponTypeMuzzle);
            Index[] muzzleFlashPosition = muzzleFlashPositions[muzzleIndex];
            for (int positionIndex = 0, muzzleFlashPositionLength = muzzleFlashPosition.length; positionIndex < muzzleFlashPositionLength; positionIndex++) {
                DbWeaponTypeMuzzlePosition dbWeaponTypeMuzzlePosition = new DbWeaponTypeMuzzlePosition();
                dbWeaponTypeMuzzlePosition.setImageNumber(positionIndex);
                dbWeaponTypeMuzzlePosition.setPosition(muzzleFlashPosition[positionIndex]);
                dbWeaponTypeMuzzlePosition.setWeaponTypeMuzzle(dbWeaponTypeMuzzle);
                dbWeaponTypeMuzzle.getPositions().add(dbWeaponTypeMuzzlePosition);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbWeaponType that = (DbWeaponType) o;

        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }

    public Collection<DbBaseItemType> getAllowedItemTypes() {
        if (allowedItemTypes == null) {
            allowedItemTypes = new HashSet<DbBaseItemType>();
        }
        return allowedItemTypes;
    }

    public Boolean isItemTypeAllowed(DbBaseItemType dbBaseItemType) {
        return getAllowedItemTypes().contains(dbBaseItemType);
    }

    public void setItemTypeAllowed(DbBaseItemType dbBaseItemType, boolean allowed) {
        if (allowed) {
            getAllowedItemTypes().add(dbBaseItemType);
        } else {
            getAllowedItemTypes().remove(dbBaseItemType);
        }
    }
}
