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

package com.btxtech.game.jsre.common.gameengine.services.items;

/**
 * User: beat
 * Date: 02.12.2009
 * Time: 16:10:39
 */
public class NoSuchItemTypeException extends Exception {

    /**
     * Used bw GWT
     */
    NoSuchItemTypeException() {
    }

    public NoSuchItemTypeException(String name) {
        super("No such item type: " + name);
    }

    public NoSuchItemTypeException(int itemTypeId) {
        super("No such item type id: " + itemTypeId);
    }
}