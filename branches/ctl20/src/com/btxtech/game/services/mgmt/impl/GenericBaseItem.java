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
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
    @ManyToOne
    private DbBaseItemType toBeBuilt;
    private Integer health;
    private Integer createdChildCount;
    private Integer buildupProgress;
    private Boolean followTarget;
    private Double reloadProgress;
    @Type(type = "path")
    @Column(name = "pathToAbsoluteDestination")
    private List<Index> pathToAbsoluteDestination;
    @OneToOne
    private GenericBaseItem baseTarget;
    @OneToOne
    private GenericResourceItem resourceTarget;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "baseId")
    private Base base;
    private boolean build;
    private double angel;
    @Type(type = "index")
    @Columns(columns = {@Column(name = "xPosRallyPoint"), @Column(name = "yPosRallyPoint")})
    private Index rallyPoint;


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

    public void setBuild(boolean build) {
        this.build = build;
    }

    public boolean isBuild() {
        return build;
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
}