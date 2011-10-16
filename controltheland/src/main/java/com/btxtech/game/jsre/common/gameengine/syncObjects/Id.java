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

package com.btxtech.game.jsre.common.gameengine.syncObjects;

import java.io.Serializable;

/**
 * User: beat
 * Date: Aug 12, 2009
 * Time: 8:38:29 PM
 */
public class Id implements Serializable {
    private Integer id;
    private int parentId;
    private int childIndex;
    private long userTimeStamp;
    public static final int NO_ID = 0;
    public static final int SIMULATION_ID = -1;

    /**
     * Used by GWT
     */
    private Id() {

    }

    public Id(int id, int parentId, int childIndex) {
        this.id = id;
        this.parentId = parentId;
        this.childIndex = childIndex;
    }

    public Id(int parentId, int childIndex) {
        this.parentId = parentId;
        this.childIndex = childIndex;
    }

    public void synchronize(Id otherId) {
        if (parentId != otherId.parentId || childIndex != otherId.childIndex) {
            throw new IllegalArgumentException(this + " index are not synchron parentId: " + parentId +
                    " otherId.parentId: " + otherId.parentId +
                    " childIndex: " + childIndex + " otherId.childIndex: " + otherId.childIndex);
        }

        if (otherId.id == null) {
            throw new IllegalArgumentException(this + " otherId.id is nll");
        }

        if (id != null && !id.equals(otherId.id)) {
            throw new IllegalArgumentException(this + " are not equal id: " + id + " other.id: " + otherId.id);
        }

        id = otherId.id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Id id1 = (Id) o;
        if (id != null && id1.id != null) {
            return id.equals(id1.id);
        }

        return childIndex == id1.childIndex && parentId == id1.parentId;

    }

    @Override
    public int hashCode() {
        int result = parentId;
        result = 31 * result + childIndex;
        return result;
    }

    @Override
    public String toString() {
        return "Id [id: " + id + " parentId: " + parentId + " childIndex: " + childIndex + "]";
    }

    public int getId() {
        if (id == null) {
            throw new IllegalStateException(this + " id is null in getter");
        }
        return id;
    }

    public boolean isSynchronized() {
        return id != null;
    }

    public int getParentId() {
        return parentId;
    }

    public boolean hasParent() {
        return parentId != NO_ID;
    }

    public int getChildIndex() {
        return childIndex;
    }

    public long getUserTimeStamp() {
        return userTimeStamp;
    }

    public void setUserTimeStamp(long userTimeStamp) {
        this.userTimeStamp = userTimeStamp;
    }
}
