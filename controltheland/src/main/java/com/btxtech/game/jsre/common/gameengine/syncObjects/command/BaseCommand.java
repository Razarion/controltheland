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

package com.btxtech.game.jsre.common.gameengine.syncObjects.command;

import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;

import java.io.Serializable;

/**
 * User: beat
 * Date: Aug 1, 2009
 * Time: 12:56:55 PM
 */
public class BaseCommand implements Serializable {
    private Id id;
    private long timeStamp;
    
    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp() {
        timeStamp = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return getClass().getName() + " " + id;
    }
}
