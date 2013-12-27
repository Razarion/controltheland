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

package com.btxtech.game.jsre.common.gameengine.itemType;

import java.io.Serializable;

/**
 * User: beat
 * Date: 04.10.2010
 * Time: 22:22:29
 */
public class LauncherType implements Serializable {
    private int projectileItemType;
    private double progress;

    /**
     * Used by GWT
     */
    LauncherType() {
    }

    public LauncherType(int projectileItemType, double progress) {
        this.projectileItemType = projectileItemType;
        this.progress = progress;
    }

    public int getProjectileItemType() {
        return projectileItemType;
    }

    public double getProgress() {
        return progress;
    }

    public void changeTo(LauncherType launcherType) {
        projectileItemType = launcherType.projectileItemType;
        progress = launcherType.progress;
    }
}
