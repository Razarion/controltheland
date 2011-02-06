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

package com.btxtech.game.services.mgmt.impl;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.services.base.Base;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;

/**
 * User: beat
 * Date: 05.12.2009
 * Time: 18:45:54
 */
@Entity
@DiscriminatorValue("BASE")
public class GenericBaseItem extends GenericItem {
    @Type(type = "index")
    @Columns(columns = {@Column(name = "xPosToBeBuilt"), @Column(name = "yPosToBeBuilt")})
    private Index positionToBeBuilt;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbBaseItemType toBeBuilt;
    private Integer health;
    private Integer createdChildCount;
    private Integer buildupProgress;
    private Boolean followTarget;
    private Double reloadProgress;
    @Type(type = "path")
    private List<Index> pathToAbsoluteDestination;
    @OneToOne(fetch = FetchType.EAGER)
    private GenericBaseItem baseTarget;
    @OneToOne(fetch = FetchType.EAGER)
    private GenericResourceItem resourceTarget;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Base base;
    private double buildup;
    private double angel;
    @Type(type = "index")
    @Columns(columns = {@Column(name = "xPosRallyPoint"), @Column(name = "yPosRallyPoint")})
    private Index rallyPoint;
    private Boolean isUpgrading;
    private Double upgradeProgress;
    @OneToOne(fetch = FetchType.EAGER)
    private DbBaseItemType upgradingItemType;
    @ManyToOne(fetch = FetchType.EAGER)
    private GenericBaseItem containedIn;
    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER, mappedBy = "containedIn")
    private Set<GenericBaseItem> containedItems;
    @OneToOne(fetch = FetchType.EAGER)
    private GenericBaseItem targetContainer;
    @Type(type = "index")
    @Columns(columns = {@Column(name = "xUnloadPos"), @Column(name = "yUnloadPos")})
    private Index unloadPos;
    private Double launcherBuildup;


    /**
     * Used by hibernate
     */
    public GenericBaseItem() {
    }

    public GenericBaseItem(BackupEntry backupEntry) {
        super(backupEntry);
    }

    public void setPositionToBeBuilt(Index positionToBeBuilt) {
        this.positionToBeBuilt = positionToBeBuilt;
    }

    public void setToBeBuilt(DbBaseItemType toBeBuilt) {
        this.toBeBuilt = toBeBuilt;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setCreatedChildCount(int createdChildCount) {
        this.createdChildCount = createdChildCount;
    }

    public void setBuildupProgress(int buildupProgress) {
        this.buildupProgress = buildupProgress;
    }

    public void setFollowTarget(boolean followTarget) {
        this.followTarget = followTarget;
    }

    public void setPathToAbsoluteDestination(List<Index> pathToAbsoluteDestination) {
        this.pathToAbsoluteDestination = pathToAbsoluteDestination;
    }

    public void setBaseTarget(GenericBaseItem target) {
        this.baseTarget = target;
    }

    public void setBase(Base base) {
        base.clearId();
        this.base = base;
    }

    public Index getPositionToBeBuilt() {
        return positionToBeBuilt;
    }

    public DbBaseItemType getToBeBuilt() {
        return toBeBuilt;
    }

    public Integer getHealth() {
        return health;
    }

    public Integer getCreatedChildCount() {
        return createdChildCount;
    }

    public Integer getBuildupProgress() {
        return buildupProgress;
    }

    public Boolean isFollowTarget() {
        return followTarget;
    }

    public List<Index> getPathToAbsoluteDestination() {
        return pathToAbsoluteDestination;
    }

    public GenericBaseItem getBaseTarget() {
        return baseTarget;
    }

    public Base getBase() {
        return base;
    }

    public double getBuildup() {
        return buildup;
    }

    public void setBuildup(double buildup) {
        this.buildup = buildup;
    }

    public void setAngel(double angel) {
        this.angel = angel;
    }

    public double getAngel() {
        return angel;
    }

    public void setResourceTarget(GenericResourceItem resourceTarget) {
        this.resourceTarget = resourceTarget;
    }

    public GenericItem getResourceTarget() {
        return resourceTarget;
    }

    public Double getReloadProgress() {
        return reloadProgress;
    }

    public void setReloadProgress(double reloadProgress) {
        this.reloadProgress = reloadProgress;
    }

    public Index getRallyPoint() {
        return rallyPoint;
    }

    public void setRallyPoint(Index rallyPoint) {
        this.rallyPoint = rallyPoint;
    }

    public boolean isUpgrading() {
        return isUpgrading != null && isUpgrading;
    }

    public void setUpgrading(boolean upgrading) {
        isUpgrading = upgrading;
    }

    public Double getUpgradeProgress() {
        return upgradeProgress != null ? upgradeProgress : 0.0;
    }

    public void setUpgradeProgress(Double upgradeProgress) {
        this.upgradeProgress = upgradeProgress;
    }

    public DbBaseItemType getUpgradingItemType() {
        return upgradingItemType;
    }

    public void setUpgradingItemType(DbBaseItemType upgradingItemType) {
        this.upgradingItemType = upgradingItemType;
    }

    public GenericBaseItem getContainedIn() {
        return containedIn;
    }

    public void setContainedIn(GenericBaseItem containedIn) {
        this.containedIn = containedIn;
    }

    public Set<GenericBaseItem> getContainedItems() {
        return containedItems;
    }

    public Index getUnloadPos() {
        return unloadPos;
    }

    public void setUnloadPos(Index unloadPos) {
        this.unloadPos = unloadPos;
    }

    public GenericBaseItem getTargetContainer() {
        return targetContainer;
    }

    public void setTargetContainer(GenericBaseItem targetContainer) {
        this.targetContainer = targetContainer;
    }

    public double getLauncherBuildup() {
        return launcherBuildup;
    }

    public void setLauncherBuildup(double launcherBuildup) {
        this.launcherBuildup = launcherBuildup;
    }
}
