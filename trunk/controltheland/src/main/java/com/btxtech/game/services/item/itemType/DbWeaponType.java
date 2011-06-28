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

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import org.hibernate.annotations.Cascade;

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
    private int muzzlePointX_0;
    @Column(nullable = false, columnDefinition = "INT default '0'")
    private int muzzlePointY_0;
    @Column(nullable = false, columnDefinition = "INT default '0'")
    private int muzzlePointY_90;
    @Column(nullable = false, columnDefinition = "INT default '0'")
    private int muzzlePointX_90;
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

    public int getMuzzlePointX_0() {
        return muzzlePointX_0;
    }

    public void setMuzzlePointX_0(int muzzlePointX_0) {
        this.muzzlePointX_0 = muzzlePointX_0;
    }

    public int getMuzzlePointY_0() {
        return muzzlePointY_0;
    }

    public void setMuzzlePointY_0(int muzzlePointY_0) {
        this.muzzlePointY_0 = muzzlePointY_0;
    }

    public int getMuzzlePointY_90() {
        return muzzlePointY_90;
    }

    public void setMuzzlePointY_90(int muzzlePointY_90) {
        this.muzzlePointY_90 = muzzlePointY_90;
    }

    public int getMuzzlePointX_90() {
        return muzzlePointX_90;
    }

    public void setMuzzlePointX_90(int muzzlePointX_90) {
        this.muzzlePointX_90 = muzzlePointX_90;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbWeaponType that = (DbWeaponType) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
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
