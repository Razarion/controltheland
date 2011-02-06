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

package com.btxtech.game.jsre.client.cockpit;

/**
 * User: beat
 * Date: 06.06.2010
 * Time: 12:07:34
 */
public class CursorItemState {
    private boolean isAttackTarget = false;
    private boolean isCollectTarget = false;
    private boolean isLoadTarget = false;
    private boolean isFinalizeBuild = false;

    public boolean isAttackTarget() {
        return isAttackTarget;
    }

    public void setAttackTarget() {
        isAttackTarget = true;
    }

    public boolean isCollectTarget() {
        return isCollectTarget;
    }

    public void setCollectTarget() {
        isCollectTarget = true;
    }

    public boolean isLoadTarget() {
        return isLoadTarget;
    }

    public void setLoadTarget() {
        isLoadTarget = true;
    }

    public boolean isFinalizeBuild() {
        return isFinalizeBuild;
    }

    public void setFinalizeBuild(boolean finalizeBuild) {
        isFinalizeBuild = finalizeBuild;
    }
}
