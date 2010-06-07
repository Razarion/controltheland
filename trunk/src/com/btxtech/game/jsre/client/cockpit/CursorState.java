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
 * Date: 01.06.2010
 * Time: 21:01:04
 */
public class CursorState {
    private boolean canMove = false;
    private boolean canAttack = false;
    private boolean canCollect = false;
    private boolean canLoad = false;
    private boolean canUnload = false;

    public boolean isCanMove() {
        return canMove;
    }

    public void setCanMove() {
        canMove = true;
    }

    public boolean isCanAttack() {
        return canAttack;
    }

    public void setCanAttack() {
        canAttack = true;
    }

    public boolean isCanCollect() {
        return canCollect;
    }

    public void setCanCollect() {
        canCollect = true;
    }

    public boolean isCanLoad() {
        return canLoad;
    }

    public void setCanLoad() {
        canLoad = true;
    }

    public boolean isCanUnload() {
        return canUnload;
    }

    public void setCanUnload(boolean unload) {
        this.canUnload = unload;
    }

    /*  MOVE(CursorType.GO),
    ATTACK(CursorType.ATTACK),
    ATTACK_MOVE(CursorType.ATTACK, CursorType.GO),
    COLLECT_MOVE(CursorType.COLLECT, CursorType.GO),
    LOAD_MOVE(CursorType.LOAD, CursorType.GO),
    UNLOAD(CursorType.UNLOAD);

    CursorState(CursorType... cursorTypes) {
    }*/
}
