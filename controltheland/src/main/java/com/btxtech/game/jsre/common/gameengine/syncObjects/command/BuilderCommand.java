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
 * Date: Aug 1, 2009
 * Time: 1:04:16 PM
 */
public class BuilderCommand extends PathToDestinationCommand {
    private int toBeBuilt;
    private Index positionToBeBuilt;
    private double destinationAngel;

    public int getToBeBuilt() {
        return toBeBuilt;
    }

    public void setToBeBuilt(int toBeBuilt) {
        this.toBeBuilt = toBeBuilt;
    }

    public Index getPositionToBeBuilt() {
        return positionToBeBuilt;
    }

    public void setPositionToBeBuilt(Index positionToBeBuilt) {
        this.positionToBeBuilt = positionToBeBuilt;
    }

    public double getDestinationAngel() {
        return destinationAngel;
    }

    public void setDestinationAngel(double destinationAngel) {
        this.destinationAngel = destinationAngel;
    }

    @Override
    public String toString() {
        return super.toString() + " toBeBuilt: " + toBeBuilt + " positionToBeBuilt: " + positionToBeBuilt;
    }

}
