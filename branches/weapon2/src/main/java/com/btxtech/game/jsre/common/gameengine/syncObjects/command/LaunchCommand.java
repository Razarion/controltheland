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

import com.btxtech.game.jsre.client.common.Index;

/**
 * User: beat
 * Date: Oct 5, 2010
 * Time: 1:04:35 PM
 */
public class LaunchCommand extends BaseCommand {
    private Index target;

    public Index getTarget() {
        return target;
    }

    public void setTarget(Index target) {
        this.target = target;
    }

    @Override
    public String toString() {
        return super.toString() + " target: " + target;
    }


}
