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

package com.btxtech.game.jsre.common;

import java.io.Serializable;

/**
 * User: beat
 * Date: 08.12.2010
 * Time: 16:35:30
 */
@Deprecated
public class UserStage implements Serializable {
    private String html;
    private boolean isRealGame;

    /**
     * Used by GWT
     */
    public UserStage() {
    }

    public UserStage(String html, boolean realGame) {
        this.html = html;
        isRealGame = realGame;
    }

    public String getHtml() {
        return html;
    }

    public boolean isRealGame() {
        return isRealGame;
    }
}
