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

/**
 * User: beat
 * Date: 01.05.2010
 * Time: 12:42:05
 */
public class LoadContainCommand extends PathToDestinationCommand {
    private Id itemContainer;

    public Id getItemContainer() {
        return itemContainer;
    }

    public void setItemContainer(Id itemContainer) {
        this.itemContainer = itemContainer;
    }
}
