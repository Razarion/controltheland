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

package com.btxtech.game.jsre.common.gameengine.syncObjects.syncInfos;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.Packet;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import java.util.List;
import java.util.Iterator;

/**
 * User: beat
 * Date: 23.11.2009
 * Time: 21:54:50
 */
public class SyncItemInfo extends Packet {
    private Id id;
    private Index position;
    private int itemTypeId;
    private boolean isAlive = true;
    private SimpleBase base;
    private List<Index> pathToDestination;
    private Double angel;
    private Index toBeBuildPosition;
    private Integer toBeBuiltTypeId;
    private int createdChildCount;
    private int buildupProgress;
    private Id target;
    private Double health;
    private Boolean isBuild;
    private Double amount;
    private Boolean followTarget;
    private Boolean operationState;
    private double reloadProgress;

    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }

    public Index getPosition() {
        return position;
    }

    public void setPosition(Index position) {
        this.position = position;
    }

    public int getItemTypeId() {
        return itemTypeId;
    }

    public void setItemTypeId(int itemTypeId) {
        this.itemTypeId = itemTypeId;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public SimpleBase getBase() {
        return base;
    }

    public void setBase(SimpleBase base) {
        this.base = base;
    }

    public List<Index> getPathToDestination() {
        return pathToDestination;
    }

    public double getAngel() {
        return angel;
    }

    public Index getToBeBuildPosition() {
        return toBeBuildPosition;
    }

    public Integer getToBeBuiltTypeId() {
        return toBeBuiltTypeId;
    }

    public int getCreatedChildCount() {
        return createdChildCount;
    }

    public int getBuildupProgress() {
        return buildupProgress;
    }

    public Id getTarget() {
        return target;
    }

    public Double getHealth() {
        return health;
    }

    public Boolean isBuild() {
        return isBuild;
    }

    public Double getAmount() {
        return amount;
    }

    public Boolean isFollowTarget() {
        return followTarget;
    }

    public void setPathToDestination(List<Index> pathToDestination) {
        this.pathToDestination = pathToDestination;
    }

    public void setAngel(Double angel) {
        this.angel = angel;
    }

    public void setToBeBuildPosition(Index toBeBuildPosition) {
        this.toBeBuildPosition = toBeBuildPosition;
    }

    public void setToBeBuiltTypeId(Integer toBeBuiltTypeId) {
        this.toBeBuiltTypeId = toBeBuiltTypeId;
    }

    public void setCreatedChildCount(int createdChildCount) {
        this.createdChildCount = createdChildCount;
    }

    public void setBuildupProgress(int buildupProgress) {
        this.buildupProgress = buildupProgress;
    }

    public void setTarget(Id target) {
        this.target = target;
    }

    public void setHealth(Double health) {
        this.health = health;
    }

    public void setBuild(Boolean build) {
        isBuild = build;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void setFollowTarget(boolean followTarget) {
        this.followTarget = followTarget;
    }

    public Boolean isOperationState() {
        return operationState;
    }

    public void setOperationState(Boolean operationState) {
        this.operationState = operationState;
    }

    public double getReloadProgress() {
        return reloadProgress;
    }

    public void setReloadProgress(double reloadProgress) {
        this.reloadProgress = reloadProgress;
    }

    private String pathToDestinationAsString() {
        StringBuilder builder = new StringBuilder();
        if (pathToDestination != null) {
            Iterator<Index> iterator = pathToDestination.iterator();
            while (iterator.hasNext()) {
                Index index = iterator.next();
                builder.append(index.toString());
                if (iterator.hasNext()) {
                    builder.append(", ");
                }
            }
        } else {
            builder.append("-");
        }
        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SyncItemInfo that = (SyncItemInfo) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "SyncItemInfo: " + id +
                " pos:" + position +
                " itemTypeId:" + itemTypeId +
                " isAlive:" + isAlive +
                " b: " + base +
                " pathToDestination:" + pathToDestinationAsString() +
                " angel:" + angel +
                " toBeBuildPosition:" + toBeBuildPosition +
                " toBeBuiltTypeId:" + toBeBuiltTypeId +
                " createdChildCount:" + createdChildCount +
                " buildupProgress:" + buildupProgress +
                " target:" + target +
                " health:" + health +
                " isBuild:" + isBuild +
                " amount:" + amount +
                " followTarget:" + followTarget +
                " operationState:" + operationState +
                " reloadProgress:" + reloadProgress;
    }
}
