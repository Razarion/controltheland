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
    private int id;
    private int parentId;
    private long userTimeStamp;
    public static final int NO_ID = 0;
    public static final int SIMULATION_ID = -1;

    /**
     * Used by GWT
     */
    Id() {

    }

    public Id(int id, int parentId) {
        this.id = id;
        this.parentId = parentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Id other = (Id) o;
        return id == other.id;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + parentId;
        return result;
    }

    @Override
    public String toString() {
        return "Id [id: " + id + " parentId: " + parentId + "]";
    }

    public int getId() {
        return id;
    }

    public int getParentId() {
        return parentId;
    }

    public boolean hasParent() {
        return parentId != NO_ID;
    }

    public long getUserTimeStamp() {
        return userTimeStamp;
    }

    public void setUserTimeStamp(long userTimeStamp) {
        this.userTimeStamp = userTimeStamp;
    }
}
