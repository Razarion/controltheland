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

package com.btxtech.game.jsre.common.tutorial;

import com.btxtech.game.jsre.client.common.Index;
import java.io.Serializable;
import java.util.Collection;

/**
 * User: beat
 * Date: 17.07.2010
 * Time: 17:33:44
 */
public class Preparation implements Serializable {
    private boolean clearGame;
    private Collection<ItemTypeAndPosition> ownItems;
    private boolean isScrollingAllowed;
    private boolean isOnlineBoxVisible;
    private boolean isInfoBoxVisible;
    private Index scroll;

    /**
     * Used by GWT
     */
    public Preparation() {
    }

    public Preparation(boolean clearGame, Collection<ItemTypeAndPosition> ownItems, boolean scrollingAllowed, boolean onlineBoxVisible, boolean infoBoxVisible, Index scroll) {
        this.clearGame = clearGame;
        this.ownItems = ownItems;
        isScrollingAllowed = scrollingAllowed;
        isOnlineBoxVisible = onlineBoxVisible;
        isInfoBoxVisible = infoBoxVisible;
        this.scroll = scroll;
    }

    public boolean isClearGame() {
        return clearGame;
    }

    public Collection<ItemTypeAndPosition> getOwnItems() {
        return ownItems;
    }

    public boolean isScrollingAllowed() {
        return isScrollingAllowed;
    }

    public boolean isOnlineBoxVisible() {
        return isOnlineBoxVisible;
    }

    public boolean isInfoBoxVisible() {
        return isInfoBoxVisible;
    }

    public Index getScroll() {
        return scroll;
    }
}
