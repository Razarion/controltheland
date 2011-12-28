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

/**
 * User: beat
 * Date: 22.11.2009
 * Time: 22:21:39
 */
public interface SyncItemListener {
    public enum Change {
        ANGEL,
        POSITION,
        ON_ATTACK,
        HEALTH,
        FACTORY_PROGRESS,
        RESOURCE,
        BUILD;
    }

    void onItemChanged(Change change, SyncItem syncItem);
}