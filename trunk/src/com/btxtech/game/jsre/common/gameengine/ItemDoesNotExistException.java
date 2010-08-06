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

package com.btxtech.game.jsre.common.gameengine;

import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;

/**
 * User: beat
 * Date: 14.11.2009
 * Time: 19:48:26
 */
public class ItemDoesNotExistException extends Exception {
    private Id id;

    public ItemDoesNotExistException(Id id) {
        super("Item does not exist: " + id);
        this.id = id;
    }

    public Id getId() {
        return id;
    }
}
