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
import com.btxtech.game.services.bot.DbBotItemConfig;
import com.btxtech.game.services.cms.layout.DbContentRow;
import com.btxtech.game.services.common.CrudChildServiceHelper;
import com.btxtech.game.services.common.CrudListChildServiceHelper;
import com.btxtech.game.services.common.CrudParent;
import com.btxtech.game.services.common.Utils;
import com.btxtech.game.services.media.DbClip;
import org.hibernate.annotations.Cascade;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * User: beat
 * Date: 09.12.2009
 * Time: 15:09:34
 */
@Entity(name = "ITEM_WEAPON_TYPE")
public class DbWeaponType implements CrudParent {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(name = "theRange")
    private int range;
    private int damage;
    private double reloadTime;
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "ITEM_WEAPON_TYPE_DISALLOWED_ITEM_TYPE",
            joinColumns = @JoinColumn(name = "weaponItemTypeId"),
            inverseJoinColumns = @JoinColumn(name = "disallowedItemTypeId")
    )
    private Set<DbBaseItemType> disallowedItemTypes;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "weaponType")
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    private Collection<DbWeaponTypeItemTypeFactor> factors = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "weaponType", orphanRemoval = true)
    private Collection<DbWeaponTypeMuzzle> muzzles;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbClip muzzleFlashClip;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbClip projectileClip;
    private Integer projectileSpeed;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbClip projectileDetonationClip;
    @Transient
    private CrudChildServiceHelper<DbWeaponTypeItemTypeFactor> factorCrud;

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public Integer getProjectileSpeed() {
        return projectileSpeed;
    }

    public void setProjectileSpeed(Integer projectileSpeed) {
        this.projectileSpeed = projectileSpeed;
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

    public void setDisallowedItemTypes(Set<DbBaseItemType> disallowedItemTypes) {
        this.disallowedItemTypes = disallowedItemTypes;
    }

    public Set<DbBaseItemType> getDisallowedItemTypes() {
        return disallowedItemTypes;
    }

    public DbClip getMuzzleFlashClip() {
        return muzzleFlashClip;
    }

    public void setMuzzleFlashClip(DbClip muzzleFlashClip) {
        this.muzzleFlashClip = muzzleFlashClip;
    }

    public DbClip getProjectileClip() {
        return projectileClip;
    }

    public void setProjectileClip(DbClip projectileClip) {
        this.projectileClip = projectileClip;
    }

    public DbClip getProjectileDetonationClip() {
        return projectileDetonationClip;
    }

    public void setProjectileDetonationClip(DbClip projectileDetonationClip) {
        this.projectileDetonationClip = projectileDetonationClip;
    }

    public WeaponType createWeaponType(int imageCount) {
        if (muzzles == null) {
            muzzles = new ArrayList<>();
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
                if (positions.length > position.getImageNumber()) {
                    positions[position.getImageNumber()] = position.getPosition();
                }
            }
        }

        return new WeaponType(range,
                projectileSpeed,
                damage,
                reloadTime,
                muzzleFlashClip != null ? muzzleFlashClip.getId() : null,
                projectileClip != null ? projectileClip.getId() : null,
                projectileDetonationClip != null ? projectileDetonationClip.getId() : null,
                Utils.dbBaseItemTypesToInts(disallowedItemTypes),
                generateItemTypeFactors(),
                muzzleFlashPositions);
    }

    private Map<Integer, Double> generateItemTypeFactors() {
        Map<Integer, Double> integerDoubleMap = new HashMap<>();
        for (DbWeaponTypeItemTypeFactor factor : factors) {
            integerDoubleMap.put(factor.getDbBaseItemType().getId(), factor.getFactor());
        }
        return integerDoubleMap;
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

    public CrudChildServiceHelper<DbWeaponTypeItemTypeFactor> getFactorCrud() {
        if(factorCrud == null) {
            factorCrud = new CrudChildServiceHelper<>(factors, DbWeaponTypeItemTypeFactor.class, this);
        }
        return factorCrud;
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
}
