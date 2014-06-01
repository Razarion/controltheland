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

package com.btxtech.game.jsre.common.gameengine.services.collision;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * User: beat
 * Date: May 27, 2009
 * Time: 6:29:22 PM
 */
public class Path implements Serializable {
    private List<Index> nextWayPositions;

    /**
     * Used by GWT
     */
    protected Path() {
    }

    public Path(Index destination) {
        nextWayPositions = new ArrayList<Index>(1);
        nextWayPositions.add(destination.getCopy());
    }

    public Path(List<Index> path) {
        nextWayPositions = new ArrayList<Index>(path);
    }

    public Index getNextWayPosition() {
        return nextWayPositions.get(0);
    }

    public boolean isEmpty() {
        return nextWayPositions == null || nextWayPositions.isEmpty();
    }

    public void wayPointReached() {
        nextWayPositions.remove(0);
    }
}
