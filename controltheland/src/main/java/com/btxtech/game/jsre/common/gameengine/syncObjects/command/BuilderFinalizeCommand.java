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
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;

/**
 * User: beat
 * Date: Sep 12, 2010
 * Time: 1:04:16 PM
 */
public class BuilderFinalizeCommand extends PathToDestinationCommand {
    private Id toBeBuilt;
    private Index destinationHint;

    public Id getToBeBuilt() {
        return toBeBuilt;
    }

    public void setToBeBuilt(Id toBeBuilt) {
        this.toBeBuilt = toBeBuilt;
    }

    public Index getDestinationHint() {
        return destinationHint;
    }

    public void setDestinationHint(Index destinationHint) {
        this.destinationHint = destinationHint;
    }

    @Override
    public String toString() {
        return super.toString() + " toBeBuilt: " + toBeBuilt;
    }

}
