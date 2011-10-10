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
 * Date: Aug 1, 2009
 * Time: 1:04:16 PM
 */
public class AttackCommand extends PathToDestinationCommand {
    private Id target;
    private boolean followTarget;
    private double destinationAngel;

    public Id getTarget() {
        return target;
    }

    public void setTarget(Id target) {
        this.target = target;
    }

    public boolean isFollowTarget() {
        return followTarget;
    }

    public void setFollowTarget(boolean followTarget) {
        this.followTarget = followTarget;
    }

    public double getDestinationAngel() {
        return destinationAngel;
    }

    public void setDestinationAngel(double destinationAngel) {
        this.destinationAngel = destinationAngel;
    }

    @Override
    public String toString() {
        return super.toString() + " target: " + target + " followTarget: " + followTarget;
    }
}